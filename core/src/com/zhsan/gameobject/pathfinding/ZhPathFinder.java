package com.zhsan.gameobject.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;
import com.zhsan.common.Point;
import com.zhsan.gameobject.*;

import java.util.*;

/**
 * Created by Peter on 9/8/2015.
 */
public class ZhPathFinder {

    // allow routing into virtual unreachable positions.
    private static final float VERY_LARGE_COST = 9e9f;

    private class Conn implements Connection<Node> {

        private final Node from, to;
        private final Troop troop;

        public Conn(Node from, Node to, Troop kind) {
            this.from = from;
            this.to = to;
            this.troop = kind;
        }

        @Override
        public float getCost() {
            Point toPoint = new Point(to.x, to.y);
            if (troop == null) {
                return map.getTerrainAt(toPoint).isPassableByAnyMilitaryKind(scen) ? 1 : Float.MAX_VALUE;
            }
            if (!troop.canMoveInto(toPoint)) {
                return VERY_LARGE_COST;
            }
            return scen.getMilitaryTerrain(troop.getKind(), map.getTerrainAt(toPoint)).getAdaptability();
        }

        @Override
        public Node getFromNode() {
            return from;
        }

        @Override
        public Node getToNode() {
            return to;
        }

        @Override
        public String toString() {
            return "Conn{" +
                    "from=" + from +
                    ", to=" + to +
                    ", troop=" + troop +
                    '}';
        }
    }

    private class Node implements IndexedNode<Node> {

        private final int x, y;
        private final Troop kind;

        private float cost;

        public Node(int x, int y, Troop kind) {
            this.x = x;
            this.y = y;
            this.kind = kind;
        }

        public float getCost() {
            return cost;
        }

        public Node setCost(float cost) {
            this.cost = cost;
            return this;
        }

        @Override
        public int getIndex() {
            return pointToIndex(x, y);
        }

        @Override
        public Array<Connection<Node>> getConnections() {
            Array<Connection<Node>> result = new Array<>(4);
            if (x > 0) {
                Conn conn = new Conn(nodes.get(pointToIndex(x, y)), nodes.get(pointToIndex(x - 1, y)), kind);
                if (conn.getCost() != Float.MAX_VALUE) {
                    result.add(conn);
                }
            }
            if (y > 0) {
                Conn conn = new Conn(nodes.get(pointToIndex(x, y)), nodes.get(pointToIndex(x, y - 1)), kind);
                if (conn.getCost() != Float.MAX_VALUE) {
                    result.add(conn);
                }
            }
            if (x < map.getWidth() - 1) {
                Conn conn = new Conn(nodes.get(pointToIndex(x, y)), nodes.get(pointToIndex(x + 1, y)), kind);
                if (conn.getCost() != Float.MAX_VALUE) {
                    result.add(conn);
                }
            }
            if (y < map.getHeight() - 1) {
                Conn conn = new Conn(nodes.get(pointToIndex(x, y)), nodes.get(pointToIndex(x, y + 1)), kind);
                if (conn.getCost() != Float.MAX_VALUE) {
                    result.add(conn);
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "x=" + x +
                    ", y=" + y +
                    ", troop=" + kind +
                    ", cost=" + cost +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (x != node.x) return false;
            return y == node.y;

        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }

    private class H implements Heuristic<Node> {

        @Override
        public float estimate(Node from, Node to) {
            return Math.abs(from.y - to.y) + Math.abs(from.x - to.x);
        }
    }

    private GameMap map;
    private GameScenario scen;

    private Array<Node> nodes = new Array<>();
    private IndexedAStarPathFinder<Node> pathFinder;

    private int pointToIndex(int x, int y) {
        return y * map.getWidth() + x;
    }

    public ZhPathFinder(GameScenario scen, GameMap map, Troop kind) {
        this.scen = scen;
        this.map = map;

        for (int y = 0; y < map.getHeight(); ++y) {
            for (int x = 0; x < map.getWidth(); ++x) {
                nodes.add(new Node(x, y, kind));
            }
        }
        DefaultIndexedGraph<Node> graph = new DefaultIndexedGraph<>(nodes);
        pathFinder = new IndexedAStarPathFinder<>(graph);
    }

    public List<Point> findPath(Point from, Point to) {
        GraphPath<Node> out = new DefaultGraphPath<>();
        boolean found = pathFinder.searchNodePath(nodes.get(pointToIndex(from.x, from.y)), nodes.get(pointToIndex(to.x, to.y)), new H(), out);
        if (!found) {
            return null;
        }

        List<Point> result = new ArrayList<>();
        for (Node n : out) {
            result.add(new Point(n.x, n.y));
        }
        return result;
    }

    public List<Point> getPointsWithinCost(Point from, int maxCost) {
        List<Point> result = new ArrayList<>();

        Node start = nodes.get(pointToIndex(from.x, from.y));

        // Uniform cost search getting all nodes within cost
        PriorityQueue<Node> frontier = new PriorityQueue<>((x, y) -> Float.compare(x.cost, y.cost));
        frontier.add(start);

        Set<Node> explored = new HashSet<>();

        do {
            Node n = frontier.poll();
            explored.add(n);
            result.add(new Point(n.x, n.y));
            for (Connection<Node> c : n.getConnections()) {
                Node target = c.getToNode() == n ? c.getFromNode() : c.getToNode();
                if (!explored.contains(target)) {
                    if (!frontier.contains(target)) {
                        if (n.getCost() + c.getCost() <= maxCost) {
                            target.setCost(n.getCost() + c.getCost());
                            frontier.add(target);
                        }
                    } else if (target.getCost() > n.getCost() + c.getCost()) {
                        target.setCost(n.getCost() + c.getCost());
                    }
                }
            }
        } while (frontier.size() > 0);

        explored.forEach(n -> n.setCost(0));

        return result;
    }

}
