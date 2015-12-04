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
 * Created by Peter on 11/8/2015.
 */
public class MilitaryTerrain implements GameObject {

    public static final String SAVE_FILE = "MilitaryTerrain.csv";

    private MilitaryKind kind;
    private TerrainDetail terrain;

    private float adaptability, multiple;

    private final int id;
    private String aiTags;

    @Override
    @LuaAI.ExportToLua
    public int getId() {
        return id;
    }

    @Override
    @LuaAI.ExportToLua
    public String getAiTags() {
        return aiTags;
    }

    @Override
    @LuaAI.ExportToLua
    public GameObject setAiTags(String aiTags) {
        this.aiTags = aiTags;
        return this;
    }

    private MilitaryTerrain(int id, MilitaryKind kind, TerrainDetail terrain, float adaptability, float multiple) {
        this.id = id;
        this.kind = kind;
        this.terrain = terrain;
        this.adaptability = adaptability;
        this.multiple = multiple;
    }

    public static int getId(int kindId, int terrainId) {
        return kindId << 16 | terrainId;
    }

    public static final GameObjectList<MilitaryTerrain> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<MilitaryTerrain> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                int kindId = Integer.parseInt(line[0]);
                int terrainId = Integer.parseInt(line[1]);
                MilitaryTerrain kind = new MilitaryTerrainBuilder()
                        .setId(getId(kindId, terrainId))
                        .setKind(scen.getMilitaryKinds().get(kindId))
                        .setTerrain(scen.getTerrainDetails().get(terrainId))
                        .setAdaptability(Float.parseFloat(line[2]))
                        .setMultiple(Float.parseFloat(line[3]))
                        .createMilitaryTerrain();

                result.add(kind);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<MilitaryTerrain> kinds) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.MILITARY_TERRAIN_SAVE_HEADER).split(","));
            for (MilitaryTerrain detail : kinds) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.kind.getId()),
                        String.valueOf(detail.terrain.getId()),
                        String.valueOf(detail.adaptability),
                        String.valueOf(detail.multiple)
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

    @LuaAI.ExportToLua
    public MilitaryKind getKind() {
        return kind;
    }

    @LuaAI.ExportToLua
    public TerrainDetail getTerrain() {
        return terrain;
    }

    @LuaAI.ExportToLua
    public float getAdaptability() {
        return adaptability;
    }

    @LuaAI.ExportToLua
    public float getMultiple() {
        return multiple;
    }

    @Override
    public String getName() {
        return kind.getName() + terrain.getName();
    }

    public static class MilitaryTerrainBuilder {
        private int id;
        private MilitaryKind kind;
        private TerrainDetail terrain;
        private float adaptability;
        private float multiple;

        public MilitaryTerrainBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public MilitaryTerrainBuilder setKind(MilitaryKind kind) {
            this.kind = kind;
            return this;
        }

        public MilitaryTerrainBuilder setTerrain(TerrainDetail terrain) {
            this.terrain = terrain;
            return this;
        }

        public MilitaryTerrainBuilder setAdaptability(float adaptability) {
            this.adaptability = adaptability;
            return this;
        }

        public MilitaryTerrainBuilder setMultiple(float multiple) {
            this.multiple = multiple;
            return this;
        }

        public MilitaryTerrain createMilitaryTerrain() {
            return new MilitaryTerrain(id, kind, terrain, adaptability, multiple);
        }
    }
}
