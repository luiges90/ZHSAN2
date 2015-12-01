package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import com.zhsan.lua.LuaAI;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 19/7/2015.
 */
public final class MilitaryKind extends GameObject {

    public static final String SAVE_FILE = "MilitaryKind.csv";

    private final GameScenario scenario;

    private final MilitaryType type;

    private final String name;
    private final String description;
    private final boolean canOnlyCreateAtArchitecture;
    private final int cost;
    private final float transportCost;
    private final int quantity;
    private final int unitQuantity;
    private final int movability;
    private final int offense, defense;
    private final int offensePerUnit, defensePerUnit;
    private final int rangeLo, rangeHi;
    private final float architectureOffense;
    private final boolean ship;

    private MilitaryKind(int id, String aitag, GameScenario scenario, MilitaryType type, String name, String description, boolean canOnlyCreateAtArchitecture, int cost, float transportCost, int quantity, int unitQuantity, int movability, int offense, int defense, int offensePerUnit, int defensePerUnit, int rangeLo, int rangeHi, float architectureOffense, boolean ship) {
        super(id);
        this.ship = ship;
        this.setAiTags(aitag);
        this.scenario = scenario;
        this.type = type;
        this.name = name;
        this.description = description;
        this.canOnlyCreateAtArchitecture = canOnlyCreateAtArchitecture;
        this.cost = cost;
        this.transportCost = transportCost;
        this.quantity = quantity;
        this.unitQuantity = unitQuantity;
        this.movability = movability;
        this.offense = offense;
        this.defense = defense;
        this.offensePerUnit = offensePerUnit;
        this.defensePerUnit = defensePerUnit;
        this.rangeLo = rangeLo;
        this.rangeHi = rangeHi;
        this.architectureOffense = architectureOffense;
    }

    public static GameObjectList<MilitaryKind> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<MilitaryKind> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                MilitaryKind kind = new MilitaryKindBuilder().setId(Integer.parseInt(line[0]))
                        .setAitag(line[1])
                        .setName(line[2])
                        .setType(scen.getMilitaryTypes().get(Integer.parseInt(line[3])))
                        .setDescription(line[4])
                        .setCanOnlyCreateAtArchitecture(Boolean.parseBoolean(line[5]))
                        .setCost(Integer.parseInt(line[6]))
                        .setTransportCost(Float.parseFloat(line[7]))
                        .setQuantity(Integer.parseInt(line[8]))
                        .setUnitQuantity(Integer.parseInt(line[9]))
                        .setMovability(Integer.parseInt(line[10]))
                        .setOffense(Integer.parseInt(line[11]))
                        .setDefense(Integer.parseInt(line[12]))
                        .setOffensePerUnit(Integer.parseInt(line[13]))
                        .setDefensePerUnit(Integer.parseInt(line[14]))
                        .setRangeLo(Integer.parseInt(line[15]))
                        .setRangeHi(Integer.parseInt(line[16]))
                        .setArchitectureOffense(Float.parseFloat(line[17]))
                        .setShip(Boolean.parseBoolean(line[18]))
                        .setScenario(scen)
                        .createMilitaryKind();

                result.add(kind);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static void toCSV(FileHandle root, GameObjectList<MilitaryKind> kinds) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.MILITARY_KIND_SAVE_HEADER).split(","));
            for (MilitaryKind detail : kinds) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        detail.getAiTags(),
                        detail.name,
                        String.valueOf(detail.type.getId()),
                        detail.description,
                        String.valueOf(detail.canOnlyCreateAtArchitecture),
                        String.valueOf(detail.cost),
                        String.valueOf(detail.transportCost),
                        String.valueOf(detail.quantity),
                        String.valueOf(detail.unitQuantity),
                        String.valueOf(detail.movability),
                        String.valueOf(detail.offense),
                        String.valueOf(detail.defense),
                        String.valueOf(detail.offensePerUnit),
                        String.valueOf(detail.defensePerUnit),
                        String.valueOf(detail.rangeLo),
                        String.valueOf(detail.rangeHi),
                        String.valueOf(detail.architectureOffense),
                        String.valueOf(detail.ship)
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }
    }

    @Override
    @LuaAI.ExportToLua
    public String getName() {
        return name;
    }

    public String getTypeName() {
        return type.getName();
    }

    @LuaAI.ExportToLua
    public int getCost() {
        return cost;
    }

    @LuaAI.ExportToLua
    public int getQuantity() {
        return quantity;
    }

    @LuaAI.ExportToLua
    public int getUnitQuantity() {
        return unitQuantity;
    }

    @LuaAI.ExportToLua
    public int getMaxUnitCount() {
        return quantity / unitQuantity;
    }

    @LuaAI.ExportToLua
    public int getCostOfArchitecture(int architectureId) {
        return getCost(scenario.getArchitectures().get(architectureId));
    }

    public int getCost(Architecture location) {
        Architecture a = this.getArchitecturesCreatable().min(
                (x, y) -> Double.compare(location.distanceTo(x), location.distanceTo(y)), null);
        if (a != null) {
            return (int) Math.round(this.getCost() + this.getTransportCost() * location.distanceTo(a));
        }
        return Integer.MAX_VALUE;
    }

    public boolean isCanOnlyCreateAtArchitecture() {
        return canOnlyCreateAtArchitecture;
    }

    public float getTransportCost() {
        return transportCost;
    }

    @LuaAI.ExportToLua
    public int getOffense() {
        return offense;
    }

    @LuaAI.ExportToLua
    public int getDefense() {
        return defense;
    }

    @LuaAI.ExportToLua
    public int getOffensePerUnit() {
        return offensePerUnit;
    }

    @LuaAI.ExportToLua
    public int getDefensePerUnit() {
        return defensePerUnit;
    }

    @LuaAI.ExportToLua
    public int getRangeLo() {
        return rangeLo;
    }

    @LuaAI.ExportToLua
    public int getRangeHi() {
        return rangeHi;
    }

    @LuaAI.ExportToLua
    public float getArchitectureOffense() {
        return architectureOffense;
    }

    @LuaAI.ExportToLua
    public int getMovability() {
        return movability;
    }

    public GameObjectList<Architecture> getArchitecturesCreatable() {
        return scenario.getArchitectures().filter(a -> a.getCreatableMilitaryKinds().contains(this));
    }

    public static class MilitaryKindBuilder {
        private int id;
        private String aitag;
        private GameScenario scenario;
        private MilitaryType type;
        private String name;
        private String description;
        private boolean canOnlyCreateAtArchitecture;
        private int cost;
        private float transportCost;
        private int quantity;
        private int unitQuantity;
        private int movability;
        private int offense, defense;
        private int offensePerUnit, defensePerUnit;
        private int rangeLo, rangeHi;
        private float architectureOffense;
        private boolean ship;

        public MilitaryKindBuilder() {
        }

        public MilitaryKindBuilder from(MilitaryKind old) {
            this.id = old.getId();
            this.scenario = old.scenario;
            this.type = old.type;
            this.name = old.name;
            this.description = old.description;
            this.canOnlyCreateAtArchitecture = old.canOnlyCreateAtArchitecture;
            this.cost = old.cost;
            this.transportCost = old.transportCost;
            this.quantity = old.quantity;
            this.unitQuantity = old.unitQuantity;
            this.movability = old.movability;
            this.offense = old.offense;
            this.defense = old.defense;
            this.offensePerUnit = old.offensePerUnit;
            this.defensePerUnit = old.defensePerUnit;
            this.rangeLo = old.rangeLo;
            this.rangeHi = old.rangeHi;
            this.architectureOffense = old.architectureOffense;
            this.ship = old.ship;
            return this;
        }

        public MilitaryKindBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public MilitaryKindBuilder setScenario(GameScenario scenario) {
            this.scenario = scenario;
            return this;
        }

        public MilitaryKindBuilder setType(MilitaryType type) {
            this.type = type;
            return this;
        }

        public MilitaryKindBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public MilitaryKindBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public MilitaryKindBuilder setCanOnlyCreateAtArchitecture(boolean canOnlyCreateAtArchitecture) {
            this.canOnlyCreateAtArchitecture = canOnlyCreateAtArchitecture;
            return this;
        }

        public MilitaryKindBuilder setCost(int cost) {
            this.cost = cost;
            return this;
        }

        public MilitaryKindBuilder setTransportCost(float transportCost) {
            this.transportCost = transportCost;
            return this;
        }

        public MilitaryKindBuilder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public MilitaryKindBuilder setUnitQuantity(int unitQuantity) {
            this.unitQuantity = unitQuantity;
            return this;
        }

        public MilitaryKindBuilder setMovability(int movability) {
            this.movability = movability;
            return this;
        }

        public MilitaryKindBuilder setOffense(int offense) {
            this.offense = offense;
            return this;
        }

        public MilitaryKindBuilder setDefense(int defense) {
            this.defense = defense;
            return this;
        }

        public MilitaryKindBuilder setRangeLo(int rangeLo) {
            this.rangeLo = rangeLo;
            return this;
        }

        public MilitaryKindBuilder setRangeHi(int rangeHi) {
            this.rangeHi = rangeHi;
            return this;
        }

        public MilitaryKindBuilder setArchitectureOffense(float architectureOffense) {
            this.architectureOffense = architectureOffense;
            return this;
        }

        public MilitaryKindBuilder setOffensePerUnit(int offensePerUnit) {
            this.offensePerUnit = offensePerUnit;
            return this;
        }

        public MilitaryKindBuilder setDefensePerUnit(int defensePerUnit) {
            this.defensePerUnit = defensePerUnit;
            return this;
        }

        public MilitaryKind createMilitaryKind() {
            return new MilitaryKind(id, aitag, scenario, type, name, description, canOnlyCreateAtArchitecture, cost, transportCost, quantity, unitQuantity, movability, offense, defense, offensePerUnit, defensePerUnit, rangeLo, rangeHi, architectureOffense, ship);
        }

        public MilitaryKindBuilder setAitag(String aitag) {
            this.aitag = aitag;
            return this;
        }

        public MilitaryKindBuilder setShip(boolean ship) {
            this.ship = ship;
            return this;
        }
    }
}
