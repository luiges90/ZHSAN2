package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.GlobalVariables;
import com.zhsan.common.Pair;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by Peter on 8/8/2015.
 */
public class Troop extends GameObject implements HasPointLocation {

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
        public final GameScenario scenario;

        private Order(GameScenario scenario, OrderKind kind, Point targetLocation) {
            this.scenario = scenario;
            this.kind = kind;
            this.targetLocation = targetLocation;
            this.targetId = -1;
        }

        private Order(GameScenario scenario, OrderKind kind, int targetId) {
            this.scenario = scenario;
            this.kind = kind;
            this.targetLocation = null;
            this.targetId = targetId;
        }

        static Order fromCSV(GameScenario scenario, String kind, String target) {
            OrderKind orderKind = OrderKind.fromCSV(kind);
            if (orderKind != null) {
                switch (orderKind) {
                    case IDLE:
                        return new Order(scenario, orderKind, null);
                    case MOVE:
                    case ATTACK_LOCATION:
                        return new Order(scenario, orderKind, Point.fromCSV(target));
                    case ATTACK_TROOP:
                    case ATTACK_ARCH:
                        return new Order(scenario, orderKind, Integer.parseInt(target));
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

        HasPointLocation target() {
            switch (kind) {
                case ATTACK_ARCH:
                    return scenario.getArchitectures().get(targetId);
                case ATTACK_TROOP:
                    return scenario.getTroops().get(targetId);
                case ATTACK_LOCATION:
                    return scenario.getTroopAt(targetLocation);
                default:
                    return null;
            }
        }
    }

    private GameScenario scenario;

    private Section belongedSection;

    private Point location;

    private Order order = new Order(null, OrderKind.IDLE, null);

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
                data.order = Order.fromCSV(scen, line[2], line[3]);
                data.belongedSection = scen.getSections().get(Integer.parseInt(line[4]));

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
                        orderStr.y,
                        String.valueOf(detail.belongedSection.getId())
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
        return String.format(GlobalStrings.getString(GlobalStrings.Keys.TROOP_NAME), getLeaderName());
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

    public Section getBelongedSection() {
        return belongedSection;
    }

    public void setBelongedSection(Section s) {
        belongedSection = s;
    }

    public Faction getBelongedFaction() {
        return getBelongedSection().getBelongedFaction();
    }

    public MilitaryKind getKind() {
        return getMilitary().getKind();
    }

    public Person getLeader() {
        return getMilitary().getLeader();
    }

    public String getLeaderName() {
        return getLeader().getName();
    }

    public int getMorale() {
        return getMilitary().getMorale();
    }

    public int getCombativity() {
        return getMilitary().getCombativity();
    }

    public int getCommand() {
        return getLeader().getCommand();
    }

    public int getStrength() {
        return getLeader().getStrength();
    }

    public int getIntelligence() {
        return getLeader().getIntelligence();
    }

    public float getOffense() {
        return (getCommand() * 0.7f + getStrength() * 0.3f) / 100.0f *
                getMorale() / 100.0f *
                scenario.getMilitaryTerrain(getKind(), scenario.getTerrainAt(getLocation())).getMultiple() *
                (getKind().getOffense() + getKind().getOffensePerUnit() * getMilitary().getUnitCount());
    }

    public float getDefense() {
        return getCommand() / 100.0f *
                getMorale() / 100.0f *
                scenario.getMilitaryTerrain(getKind(), scenario.getTerrainAt(getLocation())).getMultiple() *
                (getKind().getDefense() + getKind().getDefensePerUnit() * getMilitary().getUnitCount());
    }

    public int getQuantity() {
        return getMilitary().getQuantity();
    }

    public float getUnitCount() {
        return getMilitary().getUnitCount();
    }

    public boolean loseQuantity(int quantity) {
        getMilitary().decreaseQuantity(quantity);
        boolean destroy = checkDestroy();
        if (destroy) destroy();
        return destroy;
    }

    private boolean checkDestroy() {
        return this.getQuantity() < 0;
    }

    private void destroy() {
        // TODO destroy troop
    }

    public boolean canMoveInto(Point p) {
        Architecture destArch = scenario.getArchitectureAt(p);
        if (destArch != null && destArch.getBelongedFaction() != this.getBelongedFaction() && destArch.getEndurance() > 0) {
            return false;
        }
        Troop t = scenario.getTroopAt(p);
        if (t != null) {
            return false;
        }
        float val = scenario.getMilitaryTerrain(this.getKind(), scenario.getGameMap().getTerrainAt(p)).getAdaptability();
        if (val == Float.MAX_VALUE) {
            return false;
        }
        return true;
    }

    public void giveMoveToOrder(Point location) {
        this.order = new Order(scenario, OrderKind.MOVE, location);
    }

    public void giveAttackOrder(Point location) {
        this.order = new Order(scenario, OrderKind.ATTACK_LOCATION, location);
    }

    public void giveAttackOrder(Troop troop) {
        this.order = new Order(scenario, OrderKind.ATTACK_TROOP, troop.getId());
    }

    public void giveAttackOrder(Architecture architecture) {
        this.order = new Order(scenario, OrderKind.ATTACK_ARCH, architecture.getId());
    }

    private Queue<Point> currentPath;
    private int currentMovability;

    private boolean attacked;

    public void initExecuteOrder() {
        Point targetLocation;
        if (this.order.targetLocation != null) {
            targetLocation = this.order.targetLocation;
        } else if (this.order.kind == OrderKind.ATTACK_ARCH) {
            targetLocation = scenario.getArchitectures().get(this.order.targetId).getLocation();
        } else if (this.order.kind == OrderKind.ATTACK_TROOP) {
            targetLocation = scenario.getTroops().get(this.order.targetId).getLocation();
        } else {
            targetLocation = null;
        }

        if (targetLocation != null) {
            currentMovability = this.getMilitary().getKind().getMovability();
            currentPath = new ArrayDeque<>(scenario.getPathFinder(this).findPath(this.location, targetLocation));
            currentPath.poll();
        } else {
            currentPath = null;
        }

        attacked = false;
    }

    public boolean stepForward() {
        if (currentPath == null) {
            return true;
        }

        Point p = currentPath.poll();
        if (p == null) return false;

        if (!canMoveInto(p)) {
            return false;
        }

        float cost = scenario.getMilitaryTerrain(this.getKind(), scenario.getTerrainAt(p)).getAdaptability();

        if (cost <= currentMovability) {
            currentMovability -= cost;
            location = p;
        } else {
            return false;
        }

        return true;
    }

    public HasPointLocation getTarget() {
        return order.target();
    }

    public List<DamagePack> attack() {
        if (attacked) return Collections.emptyList();

        HasPointLocation target = order.target();
        if (target instanceof Architecture) {
            return attackArchitecture((Architecture) target);
        } else if (target instanceof Troop) {
            return attackTroop((Troop) target);
        } else if (target == null) {
            return Collections.emptyList();
        } else {
            throw new IllegalStateException("Unknown target");
        }
    }

    private boolean isLocationInAttackRange(Point p) {
        int dist = getLocation().taxiDistanceTo(p);
        return getKind().getRangeLo() <= dist && dist <= getKind().getRangeHi();
    }

    private List<DamagePack> attackArchitecture(Architecture target) {
        Optional<Point> attackOptPoint = target.getLocations().parallelStream().filter(this::isLocationInAttackRange).findFirst();
        if (!attackOptPoint.isPresent()) return Collections.emptyList();
        Point attackPoint = attackOptPoint.get();

        List<DamagePack> damagePacks = new ArrayList<>();

        float offense = this.getOffense() * this.getKind().getArchitectureOffense();
        float defense = target.getDefense();
        float ratio = offense / defense;

        int damage = Math.round(GlobalVariables.baseDamage * ratio);
        boolean destroy = target.loseEndurance(damage);
        damagePacks.add(new DamagePack(target, attackPoint, damage, destroy));

        int reactDamage;
        reactDamage = Math.round(GlobalVariables.baseDamage * (1 / ratio) * GlobalVariables.reactDamageFactor);
        this.loseQuantity(reactDamage);
        damagePacks.add(new DamagePack(this, this.getLocation(), damage, destroy));

        attacked = true;

        return damagePacks;
    }

    private List<DamagePack> attackTroop(Troop target) {
        if (!isLocationInAttackRange(target.getLocation())) return Collections.emptyList();

        List<DamagePack> damagePacks = new ArrayList<>();

        float offense = this.getOffense();
        float defense = target.getDefense();
        float ratio = offense / defense;

        int damage = Math.round(GlobalVariables.baseDamage * ratio);
        boolean destroy = target.loseQuantity(damage);
        damagePacks.add(new DamagePack(target, target.getLocation(), damage, destroy));

        int reactDamage;
        if (target.isLocationInAttackRange(this.getLocation())) {
            reactDamage = Math.round(GlobalVariables.baseDamage * (1 / ratio) * GlobalVariables.reactDamageFactor);
            destroy = this.loseQuantity(reactDamage);
            damagePacks.add(new DamagePack(this, this.getLocation(), damage, destroy));
        }

        attacked = true;

        return damagePacks;
    }


}
