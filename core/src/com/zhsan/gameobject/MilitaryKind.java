package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
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

    private MilitaryKind(int id, GameScenario scenario, MilitaryType type, String name, String description, boolean canOnlyCreateAtArchitecture, int cost, float transportCost, int quantity) {
        super(id);
        this.scenario = scenario;
        this.type = type;
        this.name = name;
        this.description = description;
        this.canOnlyCreateAtArchitecture = canOnlyCreateAtArchitecture;
        this.cost = cost;
        this.transportCost = transportCost;
        this.quantity = quantity;
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
                        .setName(line[1])
                        .setType(scen.getMilitaryTypes().get(Integer.parseInt(line[2])))
                        .setDescription(line[3])
                        .setCanOnlyCreateAtArchitecture(Boolean.parseBoolean(line[4]))
                        .setCost(Integer.parseInt(line[5]))
                        .setTransportCost(Float.parseFloat(line[6]))
                        .setQuantity(Integer.parseInt(line[7]))
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
                        detail.name,
                        String.valueOf(detail.type.getId()),
                        detail.description,
                        String.valueOf(detail.canOnlyCreateAtArchitecture),
                        String.valueOf(detail.cost),
                        String.valueOf(detail.transportCost),
                        String.valueOf(detail.quantity)
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public String getTypeName() {
        return type.getName();
    }

    public int getCost() {
        return cost;
    }

    public MilitaryKind setCost(int cost) {
        return new MilitaryKindBuilder().from(this).setCost(cost).createMilitaryKind();
    }

    public int getQuantity() {
        return quantity;
    }

    public int getCost(Architecture location) {
        return cost;
    }

    public boolean isCanOnlyCreateAtArchitecture() {
        return canOnlyCreateAtArchitecture;
    }

    public float getTransportCost() {
        return transportCost;
    }

    public GameObjectList<Architecture> getArchitecturesCreatable() {
        return scenario.getArchitectures().filter(a -> a.getCreatableMilitaryKinds().contains(this));
    }

    public static class MilitaryKindBuilder {
        private int id;
        private GameScenario scenario;
        private MilitaryType type;
        private String name;
        private String description;
        private boolean canOnlyCreateAtArchitecture;
        private int cost;
        private float transportCost;
        private int quantity;

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

        public MilitaryKind createMilitaryKind() {
            return new MilitaryKind(id, scenario, type, name, description, canOnlyCreateAtArchitecture, cost, transportCost, quantity);
        }
    }
}
