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
 * Created by Peter on 17/3/2015.
 */
public final class TerrainDetail extends GameObject {

    public static final String SAVE_FILE = "TerrainDetail.csv";

    private final String name;

    public final boolean canBeViewedThrough;
    public final float fireDamageRate;

    public final boolean water;

    private TerrainDetail(int id, String aitag, String name, boolean canBeViewedThrough, float fireDamageRate, boolean water) {
        super(id);
        this.setAiTags(aitag);
        this.name = name;
        this.canBeViewedThrough = canBeViewedThrough;
        this.fireDamageRate = fireDamageRate;
        this.water = water;
    }

    public static final GameObjectList<TerrainDetail> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        int version = scen.getGameSurvey().getVersion();

        GameObjectList<TerrainDetail> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                TerrainDetailBuilder builder = new TerrainDetailBuilder();
                builder.setId(Integer.parseInt(line[0]));
                builder.setAiTag(line[1]);
                builder.setName(line[2]);
                builder.setCanBeViewedThrough(Boolean.parseBoolean(line[3]));
                builder.setFireDamageRate(Float.parseFloat(line[4]));
                builder.setWater(Boolean.parseBoolean(line[5]));

                result.add(builder.createTerrainDetail());
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<TerrainDetail> terrainDetails) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.TERRAIN_DETAIL_SAVE_HEADER).split(","));
            for (TerrainDetail detail : terrainDetails) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        detail.getAiTags(),
                        detail.getName(),
                        String.valueOf(detail.canBeViewedThrough),
                        String.valueOf(detail.fireDamageRate),
                        String.valueOf(detail.water)
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

    public boolean isPassableByAnyMilitaryKind(GameScenario scen) {
        return Caches.get(Caches.isTerrainPassableByAnyMilitaryKind, this,
                () -> scen.getMilitaryTerrains().getAll().stream().anyMatch(mt -> mt.getTerrain() == this));
    }

    public boolean isWater() {
        return water;
    }

    @Override
    public String getName() {
        return name;
    }

    private static class TerrainDetailBuilder {
        private int id;
        private String name;
        private boolean canBeViewedThrough;
        private float fireDamageRate;
        private String aitag;
        private boolean water;

        public TerrainDetailBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public TerrainDetailBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public TerrainDetailBuilder setCanBeViewedThrough(boolean canBeViewedThrough) {
            this.canBeViewedThrough = canBeViewedThrough;
            return this;
        }

        public TerrainDetailBuilder setFireDamageRate(float fireDamageRate) {
            this.fireDamageRate = fireDamageRate;
            return this;
        }

        public TerrainDetail createTerrainDetail() {
            return new TerrainDetail(id, aitag, name, canBeViewedThrough, fireDamageRate, water);
        }

        public TerrainDetailBuilder setAiTag(String aitag) {
            this.aitag = aitag;
            return this;
        }

        public TerrainDetailBuilder setWater(boolean water) {
            this.water = water;
            return this;
        }
    }
}
