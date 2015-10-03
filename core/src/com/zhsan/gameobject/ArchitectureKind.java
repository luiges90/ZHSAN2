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
 * Created by Peter on 7/4/2015.
 */
public final class ArchitectureKind extends GameObject {

    public static final String SAVE_FILE = "ArchitectureKind.csv";

    private final String name;
    private final float drawOffsetL, drawOffsetW;

    private final int agriculture, commerce, technology, endurance, morale, population;
    private final long maxFund, maxFood;

    private ArchitectureKind(int id, String aiTag, String name, float drawOffsetL, float drawOffsetW, int agriculture, int commerce, int technology, int endurance, int morale, int population, long maxFund, long maxFood) {
        super(id);
        this.name = name;
        this.setAiTags(aiTag);
        this.drawOffsetL = drawOffsetL;
        this.drawOffsetW = drawOffsetW;
        this.agriculture = agriculture;
        this.commerce = commerce;
        this.technology = technology;
        this.endurance = endurance;
        this.morale = morale;
        this.population = population;
        this.maxFund = maxFund;
        this.maxFood = maxFood;
    }

    public static final GameObjectList<ArchitectureKind> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<ArchitectureKind> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                ArchitectureKind kind = new ArchitectureKindBuilder().setId(Integer.parseInt(line[0]))
                        .setAitag(line[1])
                        .setName(line[2])
                        .setDrawOffsetL(Float.parseFloat(line[3]))
                        .setDrawOffsetW(Float.parseFloat(line[4]))
                        .setAgriculture(Integer.parseInt(line[5]))
                        .setCommerce(Integer.parseInt(line[6]))
                        .setTechnology(Integer.parseInt(line[7]))
                        .setMorale(Integer.parseInt(line[8]))
                        .setEndurance(Integer.parseInt(line[9]))
                        .setPopulation(Integer.parseInt(line[10]))
                        .setMaxFund(Long.parseLong(line[11]))
                        .setMaxFood(Long.parseLong(line[12])).createArchitectureKind();

                result.add(kind);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<ArchitectureKind> kinds) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_KIND_SAVE_HEADER).split(","));
            for (ArchitectureKind detail : kinds) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        detail.getAiTags(),
                        detail.getName(),
                        String.valueOf(detail.getDrawOffsetLength()),
                        String.valueOf(detail.getDrawOffsetWidth()),
                        String.valueOf(detail.agriculture),
                        String.valueOf(detail.commerce),
                        String.valueOf(detail.technology),
                        String.valueOf(detail.morale),
                        String.valueOf(detail.endurance),
                        String.valueOf(detail.population),
                        String.valueOf(detail.maxFund),
                        String.valueOf(detail.maxFood)
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

    public float getDrawOffsetLength() {
        return drawOffsetL;
    }

    public float getDrawOffsetWidth() {
        return drawOffsetW;
    }

    public int getAgriculture() {
        return agriculture;
    }

    public int getCommerce() {
        return commerce;
    }

    public int getTechnology() {
        return technology;
    }

    public int getEndurance() {
        return endurance;
    }

    public int getMorale() {
        return morale;
    }

    public int getPopulation() {
        return population;
    }

    public long getMaxFund() {
        return maxFund;
    }

    public long getMaxFood() {
        return maxFood;
    }

    public static class ArchitectureKindBuilder {
        private int id;
        private String aitag;
        private String name;
        private float drawOffsetL;
        private float drawOffsetW;
        private int agriculture;
        private int commerce;
        private int technology;
        private int endurance;
        private int morale;
        private int population;
        private long maxFund;
        private long maxFood;

        public ArchitectureKindBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public ArchitectureKindBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ArchitectureKindBuilder setDrawOffsetL(float drawOffsetL) {
            this.drawOffsetL = drawOffsetL;
            return this;
        }

        public ArchitectureKindBuilder setDrawOffsetW(float drawOffsetW) {
            this.drawOffsetW = drawOffsetW;
            return this;
        }

        public ArchitectureKindBuilder setAgriculture(int agriculture) {
            this.agriculture = agriculture;
            return this;
        }

        public ArchitectureKindBuilder setCommerce(int commerce) {
            this.commerce = commerce;
            return this;
        }

        public ArchitectureKindBuilder setTechnology(int technology) {
            this.technology = technology;
            return this;
        }

        public ArchitectureKindBuilder setEndurance(int endurance) {
            this.endurance = endurance;
            return this;
        }

        public ArchitectureKindBuilder setMorale(int morale) {
            this.morale = morale;
            return this;
        }

        public ArchitectureKindBuilder setPopulation(int population) {
            this.population = population;
            return this;
        }

        public ArchitectureKindBuilder setMaxFund(long maxFund) {
            this.maxFund = maxFund;
            return this;
        }

        public ArchitectureKindBuilder setMaxFood(long maxFood) {
            this.maxFood = maxFood;
            return this;
        }

        public ArchitectureKind createArchitectureKind() {
            return new ArchitectureKind(id, aitag, name, drawOffsetL, drawOffsetW, agriculture, commerce, technology, endurance, morale, population, maxFund, maxFood);
        }

        public ArchitectureKindBuilder setAitag(String aitag) {
            this.aitag = aitag;
            return this;
        }
    }
}
