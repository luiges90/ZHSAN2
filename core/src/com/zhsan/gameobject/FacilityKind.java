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
 * Created by Peter on 25/5/2015.
 */
public final class FacilityKind implements GameObject {

    public static final String SAVE_FILE = "FacilityKind.csv";

    private final String name;

    private final int endurance;

    private final boolean indestructible;
    private final boolean mustHave;

    private final GameObjectList<TerrainDetail> canBuildAtTerrain;

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

    private FacilityKind(int id, String aitag, String name, int endurance, boolean indestructible, boolean mustHave, GameObjectList<TerrainDetail> canBuildAtTerrain) {
        this.id = id;
        this.setAiTags(aitag);
        this.name = name;
        this.endurance = endurance;
        this.indestructible = indestructible;
        this.mustHave = mustHave;
        this.canBuildAtTerrain = new GameObjectList<>(canBuildAtTerrain, true);
    }

    public static final GameObjectList<FacilityKind> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<FacilityKind> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                FacilityKind kind = new FacilityKindBuilder().setId(Integer.parseInt(line[0]))
                        .setAiTags(line[1])
                        .setName(line[2])
                        .setEndurance(Integer.parseInt(line[3]))
                        .setIndestructible(Boolean.parseBoolean(line[4]))
                        .setMustHave(Boolean.parseBoolean(line[5]))
                        .setCanBuildAtTerrain(scen.getTerrainDetails().getItemsFromCSV(line[6]))
                        .createFacilityKind();

                result.add(kind);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<FacilityKind> kinds) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.FACILITY_KIND_SAVE_HEADER).split(","));
            for (FacilityKind detail : kinds) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        detail.getAiTags(),
                        detail.name,
                        String.valueOf(detail.endurance),
                        String.valueOf(detail.indestructible),
                        String.valueOf(detail.mustHave),
                        detail.canBuildAtTerrain.toCSV()
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

    public boolean isMustHave() {
        return mustHave;
    }

    public GameObjectList<TerrainDetail> getCanBuildAtTerrain() {
        return canBuildAtTerrain;
    }

    public static class FacilityKindBuilder {
        private int id;
        private String aiTags;
        private String name;
        private int endurance;
        private boolean indestructible;
        private boolean mustHave;
        private GameObjectList<TerrainDetail> canBuildAtTerrain;

        public FacilityKindBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public FacilityKindBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public FacilityKindBuilder setEndurance(int endurance) {
            this.endurance = endurance;
            return this;
        }

        public FacilityKindBuilder setIndestructible(boolean indestructible) {
            this.indestructible = indestructible;
            return this;
        }

        public FacilityKindBuilder setMustHave(boolean mustHave) {
            this.mustHave = mustHave;
            return this;
        }

        public FacilityKindBuilder setCanBuildAtTerrain(GameObjectList<TerrainDetail> canBuildAtTerrain) {
            this.canBuildAtTerrain = canBuildAtTerrain;
            return this;
        }

        public FacilityKind createFacilityKind() {
            return new FacilityKind(id, aiTags, name, endurance, indestructible, mustHave, canBuildAtTerrain);
        }

        public FacilityKindBuilder setAiTags(String aiTags) {
            this.aiTags = aiTags;
            return this;
        }
    }
}
