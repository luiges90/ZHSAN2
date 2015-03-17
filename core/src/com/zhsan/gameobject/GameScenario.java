package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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

    public static final String PATH = "GameData" + File.separator + "Scenarios" + File.separator;

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy/M/dd H:mm:ss");

    private GameSurvey gameSurvey;
    private GameObjectList<Faction> factions;
    private GameObjectList<TerrainDetail> terrainDetails;
    private GameMap gameMap;

    public static List<Pair<String, GameSurvey>> loadAllGameSurveys() {
        List<Pair<String, GameSurvey>> result = new ArrayList<>();

        FileHandle[] scenarios = Gdx.files.external(PATH).list();
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
        factions =  Faction.fromCSV(fileName, this);
    }

    public GameObjectList<TerrainDetail> getTerrainDetails() {
        return terrainDetails;
    }

}
