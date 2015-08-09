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
import com.zhsan.gameobject.GameMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Peter on 9/8/2015.
 */
public class ZhPathFinder {

    private class Conn implements Connection<Node> {

        private final Node from, to;

        public Conn(Node from, Node to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public float getCost() {
            return 1;
        }

        @Override
        public Node getFromNode() {
            return from;
        }

        @Override
        public Node getToNode() {
            return to;
        }
    }

    private class Node implements IndexedNode<Node> {

        private final int x, y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int getIndex() {
            return pointToIndex(x, y);
        }

        @Override
        public Array<Connection<Node>> getConnections() {
            Array<Connection<Node>> result = new Array<>(4);
            if (x > 0) {
                result.add(new Conn(nodes.get(pointToIndex(x, y)), nodes.get(pointToIndex(x - 1, y))));
            }
            if (y > 0) {
                result.add(new Conn(nodes.get(pointToIndex(x, y)), nodes.get(pointToIndex(x, y - 1))));
            }
            if (x < map.getWidth() - 1) {
                result.add(new Conn(nodes.get(pointToIndex(x, y)), nodes.get(pointToIndex(x + 1, y))));
            }
            if (y < map.getHeight() - 1) {
                result.add(new Conn(nodes.get(pointToIndex(x, y)), nodes.get(pointToIndex(x, y + 1))));
            }
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
    private Array<Node> nodes = new Array<>();
    private DefaultIndexedGraph<Node> graph;
    private IndexedAStarPathFinder<Node> pathFinder;

    private int pointToIndex(int x, int y) {
        return y * map.getWidth() + x;
    }

    public ZhPathFinder(GameMap map) {
        this.map = map;

        for (int y = 0; y < map.getHeight(); ++y) {
            for (int x = 0; x < map.getWidth(); ++x) {
                nodes.add(new Node(x, y));
            }
        }
        graph = new DefaultIndexedGraph<>(nodes);
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

    public List<Point> getPointsWithinCost(Point from, int cost) {
        // TODO use better algorithm by inspecting the A* algorithm itself
        List<Point> result = new ArrayList<>();
        int furthestDistance = cost;
        Heuristic<Node> nodeHeuristic = new H();
        for (int x = from.x - furthestDistance; x <= from.x + furthestDistance; ++x) {
            for (int y = from.y - furthestDistance; y <= from.y + furthestDistance; ++y) {
                if (x < 0 || y < 0 || x > map.getWidth() || y > map.getHeight()) continue;

                Node fromNode = nodes.get(pointToIndex(from.x, from.y));
                Node toNode = nodes.get(pointToIndex(x, y));
                if (nodeHeuristic.estimate(fromNode, toNode) > cost) continue;

                GraphPath<Connection<Node>> out = new DefaultGraphPath<>();
                boolean found = pathFinder.searchConnectionPath(fromNode, toNode, nodeHeuristic, out);
                if (!found) continue;
                int totalCost = 0;
                for (Connection<Node> i : out) {
                    totalCost += i.getCost();
                }
                if (totalCost <= cost) {
                    result.add(new Point(x, y));
                }
            }
        }
        return result;
    }

}
