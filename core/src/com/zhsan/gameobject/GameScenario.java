package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
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

    private final GameSurvey gameSurvey;
    private final GameObjectList<TerrainDetail> terrainDetails;
    private final GameMap gameMap;

    private final GameObjectList<ArchitectureKind> architectureKinds;
    private final GameObjectList<Architecture> architectures;

    public static List<Pair<FileHandle, GameSurvey>> loadAllGameSurveys() {
        List<Pair<FileHandle, GameSurvey>> result = new ArrayList<>();

        FileHandle[] scenarios = Gdx.files.external(SCENARIO_PATH).list();
        for (FileHandle f : scenarios) {
            if (f.isDirectory()) {
                result.add(new ImmutablePair<>(f, GameSurvey.fromCSV(f)));
            }
        }

        return result;
    }

    public GameScenario(FileHandle file) {
        gameSurvey = GameSurvey.fromCSV(file);
        terrainDetails = TerrainDetail.fromCSV(file, this);
        gameMap = GameMap.fromCSV(file, this);

        architectureKinds = ArchitectureKind.fromCSV(file, this);
        architectures = Architecture.fromCSV(file, this);
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

    public GameObjectList<Architecture> getArchitectures() {
        return architectures;
    }

    public Architecture getArchitectureAt(Point p) {
        for (Architecture a : architectures) {
            if (a.getLocation().contains(p)) {
                return a;
            }
        }
        return null;
    }

    public GameObjectList<ArchitectureKind> getArchitectureKinds() {
        return architectureKinds;
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

        GameSurvey.toCSV(result, gameSurvey);
        TerrainDetail.toCSV(result, terrainDetails);
        GameMap.toCSV(result, gameMap);
        ArchitectureKind.toCSV(result, architectureKinds);
        Architecture.toCSV(result, architectures);
    }

}
