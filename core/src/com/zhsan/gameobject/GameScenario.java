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
import java.util.Iterator;
import java.util.List;

/**
 * Created by Peter on 8/3/2015.
 */
public class GameScenario {

    public static final int SAVE_VERSION = 2;

    public static final String DEFUALT_SCENARIO_DATA_PATH = Paths.DATA + "DefaultScenarioData" + File.separator;
    public static final String SCENARIO_PATH = Paths.DATA + "Scenario" + File.separator;
    public static final String SAVE_PATH = Paths.DATA + "Save" + File.separator;

    private final GameSurvey gameSurvey;
    private final GameObjectList<TerrainDetail> terrainDetails;
    private final GameMap gameMap;

    private final GameData gameData;

    private final GameObjectList<ArchitectureKind> architectureKinds;

    private final GameObjectList<Facility> facilities;
    private final GameObjectList<FacilityKind> facilityKinds;

    private final GameObjectList<Architecture> architectures;
    private final GameObjectList<Section> sections;
    private final GameObjectList<Faction> factions;
    private final GameObjectList<Person> persons;

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

    public static GameObjectList<Faction> loadFactionsQuick(FileHandle root, int version) {
        return Faction.fromCSVQuick(root);
    }

    public GameScenario(FileHandle file, boolean newGame, int playerFactionId) {
        gameSurvey = GameSurvey.fromCSV(file);

        // load common data
        terrainDetails = TerrainDetail.fromCSV(file, this);
        gameMap = GameMap.fromCSV(file, this);
        architectureKinds = ArchitectureKind.fromCSV(file, this);

        facilityKinds = FacilityKind.fromCSV(file, this);

        // load game objects
        int version = gameSurvey.getVersion();

        factions = Faction.fromCSV(file, this);
        sections = Section.fromCSV(file, this);
        architectures = Architecture.fromCSV(file, this);
        persons = Person.fromCSV(file, this);

        facilities = Facility.fromCSV(file, this);

        gameData = GameData.fromCSV(file, this);

        if (newGame) {
            Faction playerFaction = factions.get(playerFactionId);
            if (playerFaction != null) {
                gameData.setCurrentPlayer(playerFaction);
                gameSurvey.setCameraPosition(playerFaction.getArchitectures().getFirst().getLocation().get(0));
            }
        }

        setupLeaders();
        setupMayors();
        setupFacilities();
    }

    private final void setupLeaders() {
        factions.remove(f -> f.getPersons().size() == 0);
        factions.forEach(f -> {
            if (f.getLeaderUnchecked() == null) {
                f.setLeader(f.pickLeader());
            }
        });
    }

    private final void setupMayors() {
        architectures.forEach(a -> {
            if (a.getPersons().size() > 0) {
                GameObjectList<Person> mayors = a.getMayorUnchecked();
                if (mayors.size() != 1) {
                    a.getPersons().filter(p -> p.getDoingWorkType() == Person.DoingWork.MAYOR).forEach(p -> p.setDoingWork(Person.DoingWork.NONE));
                    a.addMayor(a.pickMayor());
                }
            }
        });
    }

    private final void setupFacilities() {
        GameObjectList<FacilityKind> mustHaveFacilities = facilityKinds.filter(FacilityKind::isMustHave);
        for (Architecture a : architectures) {
            GameObjectList<FacilityKind> missingFacilities = new GameObjectList<>(mustHaveFacilities, false);

            GameObjectList<Facility> archFacs = a.getFacilities();
            for (Facility i : archFacs) {
                if (missingFacilities.contains(i.getKind())) {
                    missingFacilities.remove(i.getKind());
                }
            }

            for (FacilityKind kind : missingFacilities) {
                Iterator<Point> iterator = a.getLocation().get(0).spiralOutIterator();
                while (iterator.hasNext()) {
                    Point p = iterator.next();
                    if (a.getLocation().contains(p)) continue;
                    if (kind.getCanBuildAtTerrain().contains(gameMap.getTerrainAt(p))) {
                        if (getFacilityAt(p) == null) {
                            Facility f = new Facility.FacilityBuilder()
                                    .setId(facilities.getFreeId())
                                    .setBelongedArchitecture(a)
                                    .setKind(kind)
                                    .setLocation(p)
                                    .createFacility();
                            facilities.add(f);
                            break;
                        }
                    }
                }
            }
        }

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

    public Person getPerson(int id) {
        return persons.filter(p -> p.getId() == id).getFirst();
    }

    public GameObjectList<Person> getPersons() {
        return persons.asUnmodifiable();
    }

    public GameObjectList<Facility> getFacilities() {
        return facilities.asUnmodifiable();
    }

    public GameObjectList<FacilityKind> getFacilityKinds() {
        return facilityKinds.asUnmodifiable();
    }

    public GameObjectList<Person> getAvailablePersons() {
        return persons.filter(person -> person.getState() != Person.State.DEAD && person.getState() != Person.State.UNAVAILABLE)
                .asUnmodifiable();
    }

    public Architecture getArchitectureAt(Point p) {
        return architectures.filter(a -> a.getLocation().contains(p)).getFirst();
    }

    public Facility getFacilityAt(Point p) {
        return facilities.filter(f -> f.getLocation().equals(p)).getFirst();
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

    public Faction getCurrentPlayer() {
        return gameData.getCurrentPlayer();
    }

    public LocalDate getGameDate() {
        return gameSurvey.getStartDate().plusDays(gameData.getDayPassed());
    }

    public void advanceDay() {
        gameData.advanceDay();
        architectures.forEach(Architecture::advanceDay);
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

        FacilityKind.toCSV(result, facilityKinds.asUnmodifiable());

        GameData.toCSV(result, gameData);

        Architecture.toCSV(result, architectures.asUnmodifiable());
        Section.toCSV(result, sections.asUnmodifiable());
        Faction.toCSV(result, factions.asUnmodifiable());
        Person.toCSV(result, persons.asUnmodifiable());
    }

}
