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
 * Created by Peter on 17/3/2015.
 */
public final class TerrainDetail extends GameObject {

    public static final String SAVE_FILE = "TerrainDetail.csv";

    private final String name;

    public final boolean canBeViewedThrough;
    public final float fireDamageRate;

    private TerrainDetail(int id, String name, boolean canBeViewedThrough, float fireDamageRate) {
        super(id);
        this.name = name;
        this.canBeViewedThrough = canBeViewedThrough;
        this.fireDamageRate = fireDamageRate;
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
                if (version == 1) {
                    builder.setId(Integer.parseInt(line[0]));
                    builder.setName(line[1]);
                    builder.setCanBeViewedThrough(Boolean.parseBoolean(line[3]));
                    builder.setFireDamageRate(Float.parseFloat(line[14]));
                } else {
                    builder.setId(Integer.parseInt(line[0]));
                    builder.setName(line[1]);
                    builder.setCanBeViewedThrough(Boolean.parseBoolean(line[2]));
                    builder.setFireDamageRate(Float.parseFloat(line[3]));
                }

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
                        String.valueOf(detail.getId()), detail.getName(),
                        String.valueOf(detail.canBeViewedThrough), String.valueOf(detail.fireDamageRate)
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

    private static class TerrainDetailBuilder {
        private int id;
        private String name;
        private boolean canBeViewedThrough;
        private float fireDamageRate;

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
            return new TerrainDetail(id, name, canBeViewedThrough, fireDamageRate);
        }
    }
}
