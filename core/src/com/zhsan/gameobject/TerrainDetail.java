package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.zhsan.common.exception.FileReadException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 17/3/2015.
 */
public class TerrainDetail extends GameObject {

    public final String name;
    public final boolean canBeViewedThrough;
    public final float fireDamageRate;

    private TerrainDetail(int id, String name, boolean canBeViewedThrough, float fireDamageRate) {
        super(id);
        this.name = name;
        this.canBeViewedThrough = canBeViewedThrough;
        this.fireDamageRate = fireDamageRate;
    }

    public static final GameObjectList<TerrainDetail> fromCSV(String path, @NotNull GameScenario scen) {
        GameObjectList<TerrainDetail> result = new GameObjectList<>();

        FileHandle f = Gdx.files.external(path + File.separator + "TerrainDetail.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read()))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                TerrainDetailBuilder builder = new TerrainDetailBuilder();
                builder.setId(Integer.parseInt(line[0]));
                builder.setName(line[1]);
                builder.setCanBeViewedThrough(Boolean.parseBoolean(line[3]));
                builder.setFireDamageRate(Float.parseFloat(line[14]));

                result.add(builder.createTerrainDetail());
            }
        } catch (IOException e) {
            throw new FileReadException(path + File.separator + "TerrainDetail.csv", e);
        }

        return result;
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
