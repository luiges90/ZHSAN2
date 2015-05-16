package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.time.LocalDate;
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

    private final GameData gameData;

    private final GameObjectList<ArchitectureKind> architectureKinds;
    private final GameObjectList<Architecture> architectures;
    private final GameObjectList<Section> sections;
    private final GameObjectList<Faction> factions;

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

    public GameScenario(FileHandle file, int playerFactionId) {
        gameSurvey = GameSurvey.fromCSV(file);
        terrainDetails = TerrainDetail.fromCSV(file, this);
        gameMap = GameMap.fromCSV(file, this);
        architectureKinds = ArchitectureKind.fromCSV(file, this);

        architectures = Architecture.fromCSV(file, this);
        sections = Section.fromCSV(file, this);
        factions = Faction.fromCSV(file, this);

        gameData = GameData.fromCSV(file, this);

        for (int i = 0; i < 2; ++i) {
            Architecture.setup(this);
            Section.setup(this);
            Faction.setup(this);
        }

        Faction playerFaction = factions.get(playerFactionId);
        gameData.setCurrentPlayer(playerFaction);
    }

    public GameObjectList<TerrainDetail> getTerrainDetails() {
        return terrainDetails.asUnmodifiable();
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public GameSurvey getGameSurvey() {
        return gameSurvey;
    }

    public GameObjectList<Architecture> getArchitectures() {
        return architectures.asUnmodifiable();
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
        return architectureKinds.asUnmodifiable();
    }

    public GameObjectList<Section> getSections() {
        return sections.asUnmodifiable();
    }

    public GameObjectList<Faction> getFactions() {
        return factions.asUnmodifiable();
    }

    public GameData getGameData() {
        return gameData;
    }

    public LocalDate getGameDate() {
        return gameSurvey.getStartDate().plusDays(gameData.getDayPassed());
    }

    public void advanceDay() {
        gameData.advanceDay();
    }

    public enum Season {
        SPRING, SUMMER, AUTUMN, WINTER
    }
    public Season getSeason() {
        LocalDate date = getGameDate();
        switch (date.getMonth().getValue()) {
            case 3:
            case 4:
            case 5:
                return Season.SPRING;
            case 6:
            case 7:
            case 8:
                return Season.SUMMER;
            case 9:
            case 10:
            case 11:
                return Season.AUTUMN;
            case 12:
            case 1:
            case 2:
                return Season.WINTER;
        }
        throw new IllegalStateException("Unexpected month: " + date.getMonth().getValue());
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
        ArchitectureKind.toCSV(result, architectureKinds.asUnmodifiable());
        Architecture.toCSV(result, architectures.asUnmodifiable());
        Section.toCSV(result, sections.asUnmodifiable());
        Faction.toCSV(result, factions.asUnmodifiable());
        GameData.toCSV(result, gameData);
    }

}
