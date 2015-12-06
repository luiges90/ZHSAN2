package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.GlobalVariables;
import com.zhsan.common.Pair;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import com.zhsan.lua.LuaAI;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 19/7/2015.
 */
public class Military implements HasPointBelongsFactionGameObject {

    public static final String SAVE_FILE = "Military.csv";

    public static class LocationType {

        private static final int ARCHITECTURE = 1;
        private static final int TROOP = 2;

        private final Architecture architecture;
        private final Troop troop;

        public LocationType(Architecture architecture) {
            this.architecture = architecture;
            this.troop = null;
        }

        public LocationType(Troop troop) {
            this.architecture = null;
            this.troop = troop;
        }

        public HasPointBelongsFactionGameObject get() {
            if (architecture != null) {
                return architecture;
            }
            if (troop != null) {
                return troop;
            }
            return null;
        }

        public int getLocationId() {
            if (architecture != null) {
                return architecture.getId();
            }
            if (troop != null) {
                return troop.getId();
            }
            return -1;
        }

        public static LocationType fromCSV(String type, String id, GameScenario scen) {
            int typeInt = Integer.parseInt(type);
            if (typeInt == ARCHITECTURE) {
                return new LocationType(scen.getArchitectures().get(Integer.parseInt(id)));
            } else if (typeInt == TROOP) {
                return new LocationType(scen.getTroops().get(Integer.parseInt(id)));
            } else {
                assert false;
                return null;
            }
        }

        public Pair<String, String> toCSV() {
            int type, id;
            if (architecture != null) {
                type = ARCHITECTURE;
                id = architecture.getId();
            } else if (troop != null) {
                type = TROOP;
                id = troop.getId();
            } else {
                assert false;
                return null;
            }
            return new Pair<>(String.valueOf(type), String.valueOf(id));
        }

    }

    private GameScenario scenario;

    private String name;

    private MilitaryKind kind;

    private LocationType location;

    private int quantity;
    private int morale, combativity;

    private int movingDays;

    private Person leader;
    private GameObjectList<Person> persons = new GameObjectList<>();

    private final int id;
    private String aiTags = "";

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

    public static final GameObjectList<Military> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<Military> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Military data = new Military(Integer.parseInt(line[0]), scen);
                data.setAiTags(line[1]);
                data.name = line[2];
                data.kind = scen.getMilitaryKinds().get(Integer.parseInt(line[3]));
                data.location = LocationType.fromCSV(line[4], line[5], scen);
                data.quantity = Integer.parseInt(line[6]);
                data.morale = Integer.parseInt(line[7]);
                data.combativity = Integer.parseInt(line[8]);
                data.leader = scen.getPerson(Integer.parseInt(line[9]));
                data.persons = scen.getPersons().getItemsFromCSV(line[10]);
                data.movingDays = Integer.parseInt(line[11]);

                result.add(data);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<Military> types) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.MILITARY_SAVE_HEADER).split(","));
            for (Military detail : types) {
                Pair<String, String> savedLocation = detail.location.toCSV();
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        detail.getAiTags(),
                        detail.getName(),
                        String.valueOf(detail.kind.getId()),
                        savedLocation.x,
                        savedLocation.y,
                        String.valueOf(detail.quantity),
                        String.valueOf(detail.morale),
                        String.valueOf(detail.combativity),
                        String.valueOf(detail.leader == null ? -1 : detail.leader.getId()),
                        detail.persons.toCSV(),
                        String.valueOf(detail.movingDays)
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }
    }

    public Military(int id, GameScenario scen) {
        this.id = id;
        this.scenario = scen;
    }

    @Override
    @LuaAI.ExportToLua
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @LuaAI.ExportToLua
    public MilitaryKind getKind() {
        return kind;
    }

    public void setKind(MilitaryKind kind) {
        this.kind = kind;
    }

    public void setLocation(Architecture location) {
        this.location = new LocationType(location);
        if (this.leader != null) {
            this.leader.moveToArchitectureInstantly(location);
        }
        this.persons.forEach(p -> p.moveToArchitectureInstantly(location));
    }

    public HasPointLocationGameObject getLocation() {
        return location.get();
    }

    @Override
    public Point getPosition() {
        return location.get().getPosition();
    }

    @LuaAI.ExportToLua
    public int getQuantity() {
        return quantity;
    }

    @LuaAI.ExportToLua
    public int getUnitCount() {
        return quantity / getKind().getUnitQuantity();
    }

    @LuaAI.ExportToLua
    public int getMorale() {
        return morale;
    }

    @LuaAI.ExportToLua
    public int getCombativity() {
        return combativity;
    }

    @LuaAI.ExportToLua
    public Person getLeader() {
        return leader;
    }

    @LuaAI.ExportToLua
    public void setLeader(int id) {
        setLeader(scenario.getPerson(id));
    }

    public void setLeader(Person leader) {
        if (leader.getLocation() != this.getLocation()) {
            throw new IllegalArgumentException("Leader of military must be in the same location.");
        }
        this.leader = leader;
    }

    public GameObjectList<Person> getPersons() {
        return new GameObjectList<>(persons);
    }

    public Military setPersons(GameObjectList<Person> persons) {
        this.persons = new GameObjectList<>(persons);
        return this;
    }

    public GameObjectList<Person> getAllPersons() {
        GameObjectList<Person> p = new GameObjectList<>(persons);
        p.add(leader);
        return p;
    }

    public String getLeaderName() {
        return leader == null ? null : leader.getName();
    }

    public boolean recruitable() {
        return !isFullyRecruited();
    }

    public boolean trainable() {
        return getQuantity() > 0 && !isFullyTrained();
    }

    public boolean isFullyRecruited() {
        return getQuantity() >= getKind().getQuantity();
    }

    public boolean isFullyTrained() {
        return getMorale() >= GlobalVariables.maxMorale && getCombativity() >= GlobalVariables.maxCombativity;
    }

    public boolean isBeingRecruited() {
        return this.leader != null ? this.leader.getLocation() == this.getLocation() && this.leader.getDoingWorkType() == Person.DoingWork.RECRUIT :
                this.getLocation() instanceof Architecture && ((Architecture) this.getLocation()).getWorkingPersons(Person.DoingWork.RECRUIT).size() > 0;
    }

    public boolean isBeingTrained() {
        return this.leader != null ? this.leader.getLocation() == this.getLocation() && this.leader.getDoingWorkType() == Person.DoingWork.TRAINING :
                this.getLocation() instanceof Architecture && ((Architecture) this.getLocation()).getWorkingPersons(Person.DoingWork.TRAINING).size() > 0;
    }

    public boolean isCampaignable() {
        return getQuantity() > 0 && getMorale() > 0 &&
                getLeader() != null && getLeader().getBelongedFaction() == getBelongedFaction();
    }

    public Section getBelongedSection() {
        return location.get().getBelongedSection();
    }

    public Faction getBelongedFaction() {
        return location.get().getBelongedFaction();
    }

    public void increaseQuantity(int x, int morale, int combativity) {
        this.morale = (int) ((float) (quantity * this.morale + x * morale) / (quantity + x));
        this.combativity = (int) ((float) (quantity * this.combativity + x * combativity) / (quantity + x));
        this.quantity = Math.min(quantity + x, getKind().getQuantity());
    }

    public void increaseMorale(int x) {
        morale = Math.min(morale + x, GlobalVariables.maxMorale);
    }

    public void increaseCombativity(int x) {
        combativity = Math.min(combativity + x, GlobalVariables.maxCombativity);
    }

    public void decreaseQuantity(int quantity) {
        this.quantity = Math.max(0, this.quantity - quantity);
    }

    @LuaAI.ExportToLua
    public Troop startCampaign(int x, int y) {
        return startCampaign(new Point(x, y));
    }

    public Troop startCampaign(Point start) {
        if (!(getLocation() instanceof Architecture)) {
            throw new IllegalStateException("The troop must be in an architecture in order to leave");
        }
        if (this.leader == null) {
            throw new IllegalStateException("Troop must have a leader before leaving");
        }
        if (this.leader.getLocation() != getLocation()) {
            throw new IllegalStateException("Leader must be in the same location as this military in order to leave");
        }
        if (this.persons.getAll().stream().anyMatch(p -> p.getLocation() != this.getLocation())) {
            throw new IllegalStateException("All persons must be in the same location as this military in order to leave");
        }
        Architecture a = (Architecture) getLocation();
        if (a.getBelongedFaction() != this.leader.getBelongedFaction()) {
            throw new IllegalStateException("Leader must be of same faction to the architecture in order to leave");
        }
        if (this.persons.getAll().stream().anyMatch(p -> a.getBelongedFaction() != p.getBelongedFaction())) {
            throw new IllegalStateException("All persons must be of same faction to the architecture in order to leave");
        }

        Troop t = new Troop(scenario.getTroops().getFreeId(), scenario)
                .setLocation(start);
        t.setBelongedSection(a.getBelongedSection());
        t.setStartArchitecture(a);
        scenario.addTroop(t);

        location = new LocationType(t);
        this.leader.joinTroop(t);
        this.persons.forEach(p -> p.joinTroop(t));

        return t;
    }

    public int getMovingDays() {
        return movingDays;
    }

    public void moveToArchitecture(Architecture a) {
        if (!(this.getLocation() instanceof Architecture)) {
            throw new IllegalStateException("The military must be in an architecture in order to be moved to another");
        }
        this.movingDays = (int) Math.max(1, Math.round(Point.distance(((Architecture) this.getLocation()).getPosition(), a.getPosition())
                / GlobalVariables.militaryMovingSpeed * this.getKind().getMovability()));
        this.location = new LocationType(a);
    }
}
