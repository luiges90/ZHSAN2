package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.gameobject.pathfinding.ZhPathFinder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Peter on 8/3/2015.
 */
public class GameScenario {

    public interface OnTroopAnimationDone {
        public void onTroopAnimationDone();
    }

    public interface OnTroopDone {
        public void onStartTroopStep(Troop t, Point oldLoc, Point newLoc, OnTroopAnimationDone onTroopAnimationDone);

        public void onStartAttackStep(Troop t, HasPointLocation target, OnTroopAnimationDone onTroopAnimationDone);

        public void onAttackDone(Troop t, HasPointLocation target, List<DamagePack> damagePacks);
    }

    public static final int SAVE_VERSION = 2;

    public static final String SCENARIO_PATH = Paths.DATA + "Scenario" + File.separator;
    public static final String SAVE_PATH = Paths.DATA + "Save" + File.separator;

    private final GameSurvey gameSurvey;
    private final GameObjectList<TerrainDetail> terrainDetails;
    private final GameMap gameMap;

    private final GameData gameData;

    private final GameObjectList<ArchitectureKind> architectureKinds;

    private final GameObjectList<Facility> facilities;
    private final GameObjectList<FacilityKind> facilityKinds;

    private final GameObjectList<MilitaryType> militaryTypes;
    private final GameObjectList<MilitaryKind> militaryKinds;
    private final GameObjectList<MilitaryTerrain> militaryTerrains;

    private final GameObjectList<TroopAnimation> troopAnimations;

    private final GameObjectList<Architecture> architectures;
    private final GameObjectList<Section> sections;
    private final GameObjectList<Faction> factions;
    private final GameObjectList<Person> persons;
    private final GameObjectList<Military> militaries;
    private final GameObjectList<Troop> troops;

    private HashMap<Troop, ZhPathFinder> pathFinders = new HashMap<>();

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

        militaryTypes = MilitaryType.fromCSV(file, this);
        militaryKinds = MilitaryKind.fromCSV(file, this);

        militaryTerrains = MilitaryTerrain.fromCSV(file, this);

        troopAnimations = TroopAnimation.fromCSV(file, this);

        // load game objects
        int version = gameSurvey.getVersion();

        factions = Faction.fromCSV(file, this);
        sections = Section.fromCSV(file, this);
        architectures = Architecture.fromCSV(file, this);
        troops = Troop.fromCSV(file, this);
        persons = Person.fromCSV(file, this);
        militaries = Military.fromCSV(file, this);

        facilities = Facility.fromCSV(file, this);

        gameData = GameData.fromCSV(file, this);

        if (newGame) {
            Faction playerFaction = factions.get(playerFactionId);
            if (playerFaction != null) {
                gameData.setCurrentPlayer(playerFaction);
                gameSurvey.setCameraPosition(playerFaction.getArchitectures().getFirst().getLocations().get(0));
            }
        }

        setupLeaders();
        setupMayors();
        setupFacilities();
    }

    private final void setupLeaders() {
        factions.forEach(f -> f.setLeader(this.getPersons().get(f.getLeaderId())));

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
                    a.addMayor();
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
                Iterator<Point> iterator = a.getLocations().get(0).spiralOutIterator();
                while (iterator.hasNext()) {
                    Point p = iterator.next();
                    if (a.getLocations().contains(p)) continue;
                    if (kind.getCanBuildAtTerrain().contains(gameMap.getTerrainAt(p))) {
                        if (getFacilityAt(p) == null) {
                            Facility f = new Facility(facilities.getFreeId(), this);
                            f.setBelongedArchitecture(a);
                            f.setKind(kind);
                            f.setLocation(p);
                            facilities.add(f);
                            break;
                        }
                    }
                }
            }
        }

    }

    public GameObjectList<MilitaryType> getMilitaryTypes() {
        return new GameObjectList<>(militaryTypes, true);
    }

    public GameObjectList<MilitaryKind> getMilitaryKinds() {
        return new GameObjectList<>(militaryKinds, true);
    }

    public GameObjectList<TerrainDetail> getTerrainDetails() {
        return new GameObjectList<>(terrainDetails, true);
    }

    public TerrainDetail getTerrainAt(Point p) {
        return gameMap.getTerrainAt(p);
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public GameSurvey getGameSurvey() {
        return gameSurvey;
    }

    public GameObjectList<Architecture> getArchitectures() {
        return new GameObjectList<>(architectures, true);
    }

    public Person getPerson(int id) {
        return persons.filter(p -> p.getId() == id).getFirst();
    }

    public GameObjectList<Person> getPersons() {
        return new GameObjectList<>(persons, true);
    }

    public GameObjectList<Facility> getFacilities() {
        return new GameObjectList<>(facilities, true);
    }

    public GameObjectList<FacilityKind> getFacilityKinds() {
        return new GameObjectList<>(facilityKinds, true);
    }

    public GameObjectList<Person> getAvailablePersons() {
        GameObjectList<Person> persons1 = persons.filter(person -> person.getState() != Person.State.DEAD && person.getState() != Person.State.UNAVAILABLE);
        return new GameObjectList<>(persons1, true);
    }

    public Architecture getArchitectureAt(Point p) {
        return architectures.filter(a -> a.getLocations().contains(p)).getFirst();
    }

    public Facility getFacilityAt(Point p) {
        return facilities.filter(f -> f.getLocation().equals(p)).getFirst();
    }

    public Troop getTroopAt(Point p) {
        return troops.filter(f -> f.getLocation().equals(p)).getFirst();
    }

    public GameObjectList<ArchitectureKind> getArchitectureKinds() {
        return new GameObjectList<>(architectureKinds, true);
    }

    public GameObjectList<Section> getSections() {
        return new GameObjectList<>(sections, true);
    }

    public GameObjectList<Faction> getFactions() {
        return new GameObjectList<>(factions, true);
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

    public GameObjectList<Military> getMilitaries() {
        return new GameObjectList<>(militaries, true);
    }

    public Military getMilitary(int id) {
        return militaries.filter(x -> x.getId() == id).getFirst();
    }

    public GameObjectList<TroopAnimation> getTroopAnimations() {
        return new GameObjectList<>(troopAnimations, true);
    }

    public GameObjectList<Troop> getTroops() {
        return new GameObjectList<>(troops, true);
    }

    public void removeTroop(Troop t, boolean removeMilitary) {
        if (removeMilitary) {
            militaries.remove(t.getMilitary());
        }
        troops.remove(t);
    }

    public MilitaryTerrain getMilitaryTerrain(MilitaryKind kind, TerrainDetail terrain) {
        MilitaryTerrain mt = militaryTerrains.get(MilitaryTerrain.getId(kind.getId(), terrain.getId()));
        if (mt == null) {
            return new MilitaryTerrain.MilitaryTerrainBuilder()
                    .setId(-1)
                    .setKind(kind)
                    .setTerrain(terrain)
                    .setAdaptability(Float.MAX_VALUE)
                    .setMultiple(0)
                    .createMilitaryTerrain();
        }
        return mt;
    }

    public ZhPathFinder getPathFinder(Troop kind) {
        if (pathFinders.get(kind) == null) {
            pathFinders.put(kind, new ZhPathFinder(this, gameMap, kind));
        }
        return pathFinders.get(kind);
    }

    public boolean createMilitary(Architecture location, MilitaryKind kind) {
        int cost = kind.getCost(location);
        if (cost > location.getFund()) return false;
        location.loseFund(cost);

        Military m = new Military(militaries.getFreeId(), this);
        m.setKind(kind);
        m.setName(kind.getName());
        m.setLocation(location);

        militaries.add(m);

        return true;
    }

    public void advanceDay(OnTroopDone onTroopDone) {
        gameData.advanceDay();
        architectures.getAll().parallelStream().forEach(Architecture::advanceDay);
        persons.getAll().parallelStream().forEach(Person::advanceDay);

        troops.getAll().parallelStream().forEach(Troop::initExecuteOrder);
        List<Troop> movingTroops = new ArrayList<>(troops.getAll());
        do {
            // TODO move all troops in parallel
            Iterator<Troop> it = movingTroops.iterator();
            while (it.hasNext()) {
                Troop t = it.next();
                Point oldLoc = t.getLocation();

                HasPointLocation target = t.canAttackTarget();
                if (!t.stepForward()) {
                    if (target != null) {
                        onTroopDone.onStartAttackStep(t, target, () -> {
                            List<DamagePack> damagePacks = t.attack();
                            onTroopDone.onAttackDone(t, target, damagePacks);
                        });
                    }
                    it.remove();
                } else {
                    Point newLoc = t.getLocation();
                    onTroopDone.onStartTroopStep(t, oldLoc, newLoc, () -> {
                        if (t.tryEnter(newLoc)) {
                            return;
                        }
                        if (target != null) {
                            List<DamagePack> damagePacks = t.attack();
                            onTroopDone.onAttackDone(t, target, damagePacks);
                        }
                    });
                }
            }
        } while (movingTroops.size() > 0);
    }

    public void addTroop(Troop t) {
        troops.add(t);
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
        ArchitectureKind.toCSV(result, new GameObjectList<>(architectureKinds, true));

        FacilityKind.toCSV(result, new GameObjectList<>(facilityKinds, true));

        MilitaryType.toCSV(result, new GameObjectList<>(militaryTypes, true));
        MilitaryKind.toCSV(result, new GameObjectList<>(militaryKinds, true));

        MilitaryTerrain.toCSV(result, new GameObjectList<>(militaryTerrains, true));

        TroopAnimation.toCSV(result, new GameObjectList<>(troopAnimations, true));

        GameData.toCSV(result, gameData);

        Architecture.toCSV(result, new GameObjectList<>(architectures, true));
        Section.toCSV(result, new GameObjectList<>(sections, true));
        Faction.toCSV(result, new GameObjectList<>(factions, true));
        Person.toCSV(result, new GameObjectList<>(persons, true));
        Military.toCSV(result, new GameObjectList<>(militaries, true));
        Troop.toCSV(result, new GameObjectList<>(troops, true));

        Facility.toCSV(result, new GameObjectList<>(facilities, true));
    }

}
