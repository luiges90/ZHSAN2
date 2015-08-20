package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.Pair;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Peter on 8/8/2015.
 */
public class Troop extends GameObject {

    public static final String SAVE_FILE = "Troop.csv";

    private enum OrderKind {
        IDLE, MOVE, ATTACK_LOCATION, ATTACK_TROOP, ATTACK_ARCH;

        static OrderKind fromCSV(String s) {
            switch (s) {
                case "idle": return IDLE;
                case "move": return MOVE;
                case "attackLocation": return ATTACK_LOCATION;
                case "attackTroop": return ATTACK_TROOP;
                case "attackArch": return ATTACK_ARCH;
                default: assert false; return null;
            }
        }

        String toCSV() {
            switch (this) {
                case IDLE: return "idle";
                case MOVE: return "move";
                case ATTACK_LOCATION: return "attackLocation";
                case ATTACK_TROOP: return "attackTroop";
                case ATTACK_ARCH: return "attackArch";
            }
            assert false;
            return null;
        }
    }

    private static class Order {
        public final OrderKind kind;
        public final Point targetLocation;
        public final int targetId;

        private Order(OrderKind kind, Point targetLocation) {
            this.kind = kind;
            this.targetLocation = targetLocation;
            this.targetId = -1;
        }

        private Order(OrderKind kind, int targetId) {
            this.kind = kind;
            this.targetLocation = null;
            this.targetId = targetId;
        }

        static Order fromCSV(String kind, String target) {
            OrderKind orderKind = OrderKind.fromCSV(kind);
            if (orderKind != null) {
                switch (orderKind) {
                    case IDLE:
                        return new Order(orderKind, null);
                    case MOVE:
                    case ATTACK_LOCATION:
                        return new Order(orderKind, Point.fromCSV(target));
                    case ATTACK_TROOP:
                    case ATTACK_ARCH:
                        return new Order(orderKind, Integer.parseInt(target));
                }
            }
            assert false;
            return null;
        }

        Pair<String, String> toCSV() {
            String orderKind = kind.toCSV();
            switch (kind) {
                case IDLE:
                    return new Pair<>(orderKind, "");
                case MOVE:
                case ATTACK_LOCATION:
                    return new Pair<>(orderKind, targetLocation.toCSV());
                case ATTACK_TROOP:
                case ATTACK_ARCH:
                    return new Pair<>(orderKind, String.valueOf(targetId));
            }
            assert false;
            return null;
        }
    }

    private GameScenario scenario;

    private Point location;

    private Order order = new Order(OrderKind.IDLE, null);

    public static final GameObjectList<Troop> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<Troop> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Troop data = new Troop(Integer.parseInt(line[0]), scen);
                data.location = Point.fromCSV(line[1]);
                data.order = Order.fromCSV(line[2], line[3]);

                result.add(data);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<Troop> types) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.TROOP_SAVE_HEADER).split(","));
            for (Troop detail : types) {
                Pair<String, String> orderStr = detail.order.toCSV();
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        detail.location.toCSV(),
                        orderStr.x,
                        orderStr.y
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }
    }

    public Troop(int id, GameScenario scen) {
        super(id);
        this.scenario = scen;
    }

    @Override
    public String getName() {
        return getLeaderName();
    }

    public Military getMilitary() {
        return scenario.getMilitaries().filter(m -> m.getLocation() == this).getFirst();
    }

    public Point getLocation() {
        return location;
    }

    public Troop setLocation(Point location) {
        this.location = location;
        return this;
    }

    public String getOrderString() {
        switch (this.order.kind) {
            case IDLE:
                return null;
            case MOVE:
                return String.format(GlobalStrings.getString(GlobalStrings.Keys.MOVE_TO), this.order.targetLocation.x, this.order.targetLocation.y);
            case ATTACK_LOCATION:
                return String.format(GlobalStrings.getString(GlobalStrings.Keys.ATTACK_POINT), this.order.targetLocation.x, this.order.targetLocation.y);
            case ATTACK_TROOP:
                return String.format(GlobalStrings.getString(GlobalStrings.Keys.ATTACK_TARGET), scenario.getTroops().get(this.order.targetId).getName());
            case ATTACK_ARCH:
                return String.format(GlobalStrings.getString(GlobalStrings.Keys.ATTACK_TARGET), scenario.getArchitectures().get(this.order.targetId).getName());
        }
        assert false;
        return null;
    }

    public String getKindString() {
        return this.getMilitary().getKind().getName();
    }

    public Faction getBelongedFaction() {
        return this.getMilitary().getLeader().getBelongedFaction();
    }

    public MilitaryKind getKind() {
        return getMilitary().getKind();
    }

    public String getBelongedFactionName() {
        return getBelongedFaction().getName();
    }

    public String getLeaderName() {
        return getMilitary().getLeader().getName();
    }

    public void giveMoveToOrder(Point location) {
        this.order = new Order(OrderKind.MOVE, location);
    }

    public void giveAttackOrder(Point Location) {
        this.order = new Order(OrderKind.ATTACK_LOCATION, location);
    }

    public void giveAttackOrder(Troop troop) {
        this.order = new Order(OrderKind.ATTACK_TROOP, troop.getId());
    }

    public void giveAttackOrder(Architecture architecture) {
        this.order = new Order(OrderKind.ATTACK_ARCH, architecture.getId());
    }

    private Queue<Point> currentPath;
    private int currentMovability;

    public void initExecuteOrder() {
        currentMovability = this.getMilitary().getKind().getMovability();
        currentPath = new ArrayDeque<>(scenario.getPathFinder(this.getKind()).findPath(this.location, this.order.targetLocation));
        currentPath.poll();
    }

    public boolean stepForward() {
        Point p = currentPath.poll();
        if (p == null) return false;

        float cost = scenario.getMilitaryTerrain(this.getKind(), scenario.getTerrainAt(p)).getAdaptability();

        if (cost <= currentMovability) {
            currentMovability -= cost;
            location = p;
        } else {
            return false;
        }

        return true;
    }

}
