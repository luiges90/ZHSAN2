package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.zhsan.common.Paths;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 8/3/2015.
 */
public class GameScenario {

    public static final int SAVE_VERSION = 2;

    public static final String SCENARIO_PATH = Paths.DATA + "Scenario" + File.separator;
    public static final String SAVE_PATH = Paths.DATA + "Save" + File.separator;

    private GameSurvey gameSurvey;
    private GameObjectList<TerrainDetail> terrainDetails;
    private GameMap gameMap;

    public static List<Pair<String, GameSurvey>> loadAllGameSurveys() {
        List<Pair<String, GameSurvey>> result = new ArrayList<>();

        FileHandle[] scenarios = Gdx.files.external(SCENARIO_PATH).list();
        for (FileHandle f : scenarios) {
            if (f.isDirectory()) {
                result.add(new ImmutablePair<>(f.path(), GameSurvey.fromCSV(f.path())));
            }
        }

        return result;
    }

    public GameScenario(String fileName) {
        gameSurvey = GameSurvey.fromCSV(fileName);
        terrainDetails = TerrainDetail.fromCSV(fileName, this);
        gameMap = GameMap.fromCSV(fileName, this);
    }

    public GameObjectList<TerrainDetail> getTerrainDetails() {
        return terrainDetails;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public GameSurvey getGameSurvey() {
        return gameSurvey;
    }

    public void save(FileHandle out) {
        FileHandle result = out;
        if (result == null) {
            FileHandle root = Gdx.files.external(SAVE_PATH);
            int i = 1;
            do {
                result = root.child("Save" + i);
                i++;
            } while (result.exists());
            result.mkdirs();
        }

        result.emptyDirectory();

        gameSurvey.toCSV(result);
        TerrainDetail.toCSV(result, terrainDetails);
    }
}
