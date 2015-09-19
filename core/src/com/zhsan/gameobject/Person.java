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
 * Created by Peter on 24/5/2015.
 */
public class Person extends GameObject {

    public static final String SAVE_FILE = "Person.csv";

    public enum State {
        UNAVAILABLE, NORMAL, UNEMPLOYED, CAPTIVE, DEAD;

        public static State fromCSV(String s) {
            switch (s) {
                case "unavailable": return UNAVAILABLE;
                case "normal": return NORMAL;
                case "unemployed": return UNEMPLOYED;
                case "captive": return CAPTIVE;
                case "dead": return DEAD;
                default: return UNAVAILABLE;
            }
        }

        public String toCSV() {
            switch (this) {
                case UNAVAILABLE: return "unavailable";
                case NORMAL: return "normal";
                case UNEMPLOYED: return "unemployed";
                case CAPTIVE: return "captive";
                case DEAD: return "dead";
            }
            assert false;
            return null;
        }
    }

    public static class LocationType {

        private static final int NONE = -1;
        private static final int ARCHITECTURE = 1;
        private static final int TROOP = 2;

        private final Architecture architecture;
        private final Troop troop;

        public LocationType() {
            this.architecture = null;
            this.troop = null;
        }

        public LocationType(Architecture architecture) {
            this.architecture = architecture;
            this.troop = null;
        }

        public LocationType(Troop troop) {
            this.architecture = null;
            this.troop = troop;
        }

        public GameObject get() {
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
            switch (typeInt) {
                case NONE:
                    return new LocationType();
                case ARCHITECTURE:
                    return new LocationType(scen.getArchitectures().get(Integer.parseInt(id)));
                case TROOP:
                    return new LocationType(scen.getTroops().get(Integer.parseInt(id)));
                default:
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
                type = NONE;
                id = -1;
            }
            return new Pair<>(String.valueOf(type), String.valueOf(id));
        }

        public static Pair<String, String> nullToCSV() {
            return new Pair<>(String.valueOf(NONE), String.valueOf(-1));
        }
    }

    public enum DoingWork {
        NONE, AGRICULTURE, COMMERCE, TECHNOLOGY, MORALE, ENDURANCE, MAYOR, RECRUIT, TRAINING;

        public static DoingWork fromCSV(String s) {
            switch (s) {
                case "none": return NONE;
                case "agriculture": return AGRICULTURE;
                case "commerce": return COMMERCE;
                case "technology": return TECHNOLOGY;
                case "morale": return MORALE;
                case "endurance": return ENDURANCE;
                case "mayor": return MAYOR;
                case "recruit": return RECRUIT;
                case "training": return TRAINING;
                default: return NONE;
            }
        }

        public String toCSV() {
            switch (this) {
                case NONE: return "none";
                case AGRICULTURE: return "agriculture";
                case COMMERCE: return "commerce";
                case TECHNOLOGY: return "technology";
                case MORALE: return "morale";
                case ENDURANCE: return "endurance";
                case MAYOR: return "mayor";
                case RECRUIT: return "recruit";
                case TRAINING: return "training";
            }
            assert false;
            return null;
        }

        public String toDisplay() {
            switch (this) {
                case NONE: return GlobalStrings.getString(GlobalStrings.Keys.NO_CONTENT);
                case AGRICULTURE: return GlobalStrings.getString(GlobalStrings.Keys.AGRICULTURE);
                case COMMERCE: return GlobalStrings.getString(GlobalStrings.Keys.COMMERCE);
                case TECHNOLOGY: return GlobalStrings.getString(GlobalStrings.Keys.TECHNOLOGY);
                case MORALE: return GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_MORALE);
                case ENDURANCE: return GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_ENDURANCE);
                case RECRUIT: return GlobalStrings.getString(GlobalStrings.Keys.MILITARY_RECRUIT);
                case TRAINING: return GlobalStrings.getString(GlobalStrings.Keys.MILITARY_TRAINING);
                case MAYOR: return GlobalStrings.getString(GlobalStrings.Keys.MAYOR);
                default:
                    assert false;
                    return GlobalStrings.getString(GlobalStrings.Keys.NO_CONTENT);
            }
        }
    }

    private GameScenario scenario;

    private int portraitId;

    private String surname;
    private String givenName;
    private String calledName;

    private Person.State state;
    private DoingWork doingWork;

    private LocationType location;

    private int command, strength, intelligence, politics, glamour;

    private int movingDays = 0;

    private Person(int id, GameScenario scen) {
        super(id);
        this.scenario = scen;
    }

    public static final GameObjectList<Person> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<Person> result = new GameObjectList<>();

        FileHandle f = root.child(Person.SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Person data = new Person(Integer.parseInt(line[0]), scen);

                data.portraitId = Integer.parseInt(line[1]);
                data.surname = line[2];
                data.givenName = line[3];
                data.calledName = line[4];
                data.state = Person.State.fromCSV(line[5]);
                data.location = LocationType.fromCSV(line[6], line[7], scen);
                data.movingDays = Integer.parseInt(line[8]);
                data.strength = Integer.parseInt(line[9]);
                data.command = Integer.parseInt(line[10]);
                data.intelligence = Integer.parseInt(line[11]);
                data.politics = Integer.parseInt(line[12]);
                data.glamour = Integer.parseInt(line[13]);
                data.doingWork = Person.DoingWork.fromCSV(line[14]);

                result.add(data);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<Person> data) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.PERSON_SAVE_HEADER).split(","));
            for (Person d : data) {
                Pair<String, String> savedLocation = d.location == null ? LocationType.nullToCSV() : d.location.toCSV();
                writer.writeNext(new String[]{
                        String.valueOf(d.getId()),
                        String.valueOf(d.portraitId),
                        d.surname,
                        d.givenName,
                        d.calledName,
                        d.state.toCSV(),
                        savedLocation.x,
                        savedLocation.y,
                        String.valueOf(d.movingDays),
                        String.valueOf(d.command),
                        String.valueOf(d.strength),
                        String.valueOf(d.intelligence),
                        String.valueOf(d.politics),
                        String.valueOf(d.glamour),
                        d.doingWork.toCSV()
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }


    @Override
    @LuaAI.ExportToLua
    public String getName() {
        return surname + givenName;
    }

    public State getState() {
        return state;
    }

    public Section getBelongedSection() {
        if (state == State.NORMAL) {
            GameObject t = location.get();
            if (t instanceof Architecture) {
                return ((Architecture) t).getBelongedSection();
            } else if (t instanceof Troop) {
                return ((Troop) t).getBelongedSection();
            }
        }
        return null;
    }

    public Faction getBelongedFaction() {
        if (state == State.NORMAL) {
            GameObject t = location.get();
            if (t instanceof Architecture) {
                return ((Architecture) t).getBelongedFaction();
            } else if (t instanceof Troop) {
                return ((Troop) t).getBelongedFaction();
            }
        }
        return null;
    }

    public GameObject getLocation() {
        return location == null ? null : location.get();
    }

    public int getMovingDays() {
        return movingDays;
    }

    public String getMovingDaysString() {
        return movingDays == 0 ? "" : movingDays + GlobalStrings.getString(GlobalStrings.Keys.DAY);
    }

    @LuaAI.ExportToLua
    public int getCommand() {
        return command;
    }

    @LuaAI.ExportToLua
    public int getStrength() {
        return strength;
    }

    @LuaAI.ExportToLua
    public int getIntelligence() {
        return intelligence;
    }

    @LuaAI.ExportToLua
    public int getPolitics() {
        return politics;
    }

    @LuaAI.ExportToLua
    public int getGlamour() {
        return glamour;
    }

    @LuaAI.ExportToLua
    public int getAbilitySum() {
        return command + strength + intelligence + politics + glamour;
    }

    @LuaAI.ExportToLua
    public String getDoingWork() {
        return getDoingWorkType().toCSV();
    }

    public DoingWork getDoingWorkType() {
        if (location.get() instanceof Architecture && this.state == State.NORMAL) {
            return doingWork;
        } else {
            return DoingWork.NONE;
        }
    }

    void setDoingWorkUnchecked(DoingWork work) {
        this.doingWork = work;
    }

    public int getPortraitId() {
        return portraitId;
    }

    @LuaAI.ExportToLua
    public void setDoingWork(String work) {
        setDoingWork(DoingWork.fromCSV(work));
    }

    private void handoverMayor(Person newMayor, LocationType newLocation) {
        if (!(this.getLocation() instanceof Architecture)) {
            throw new IllegalStateException("Person must be in architecture in order to unset mayor. state " + this);
        }
        if (this.doingWork != DoingWork.MAYOR) {
            throw new IllegalStateException("Person must be mayor in order to unset mayor. state " + this);
        }
        Architecture location = (Architecture) this.getLocation();

        if (newMayor != null) {
            location.changeMayor(newMayor, true);
        } else {
            this.setDoingWork(DoingWork.NONE);
        }
        this.location = newLocation;
    }

    public void setDoingWork(DoingWork work) {
        if (location.get() instanceof Architecture && this.state == State.NORMAL) {
            if (work == DoingWork.MAYOR) {
                ((Architecture) this.getLocation()).getMayor().doingWork = DoingWork.NONE;
                this.doingWork = DoingWork.MAYOR;
            } else {
                if (this.doingWork != DoingWork.MAYOR) {
                    this.doingWork = work;
                } else if (((Architecture) this.location.get()).getPersons().size() <= 1) {
                    this.doingWork = work;
                } else {
                    throw new IllegalStateException("You must not unassign a mayor by setDoingWork, change mayor by setting any other person to mayor first, " +
                            "unless there is no one in it");
                }
            }
        } else {
            throw new IllegalStateException("Person should be in an architecture, hired, if he is doing work. Person state = " + this);
        }
    }

    public void joinTroop(Troop t) {
        if (!(this.location.get() instanceof Architecture)) {
            throw new IllegalStateException("To join troop, the person must be in architecture. state = " + this);
        }

        if (this.getDoingWorkType() == DoingWork.MAYOR) {
            this.handoverMayor(((Architecture) this.location.get()).pickMayor(this), new LocationType(t));
        } else {
            this.setDoingWork(DoingWork.NONE);
            this.location = new LocationType(t);
        }
    }

    public void moveToArchitecture(Point from, Architecture a) {
        this.movingDays = (int) Math.max(1, Math.round(Point.distance(from, a.getLocation()) / GlobalVariables.personMovingSpeed));
        this.location = new LocationType(a);
    }

    public void moveToArchitectureInstantly(Architecture a) {
        this.location = new LocationType(a);
    }

    public String getDoingWorkString() {
        return getDoingWorkType().toDisplay();
    }

    public void advanceDay() {
        if (this.movingDays > 0) {
            this.movingDays--;
            if (this.movingDays == 0) {
                // TODO on arrival
            }
        }
    }

    @LuaAI.ExportToLua
    public int getAgricultureAbility() {
        return getStrength() + 2 * getPolitics() + getGlamour();
    }

    @LuaAI.ExportToLua
    public int getCommerceAbility() {
        return 2 * getPolitics() + 2 * getGlamour();
    }

    @LuaAI.ExportToLua
    public int getTechnologyAbility() {
        return getIntelligence() + 3 * getPolitics();
    }

    @LuaAI.ExportToLua
    public int getMoraleAbility() {
        return getPolitics() + 3 * getGlamour();
    }

    @LuaAI.ExportToLua
    public int getEnduranceAbility() {
        return 2 * getStrength() + 2 * getPolitics();
    }

    @LuaAI.ExportToLua
    public int getRecruitAbility() {
        return 2 * getCommand() + 2 * getGlamour();
    }

    @LuaAI.ExportToLua
    public int getTrainingAbility() {
        return 3 * getCommand() + getStrength();
    }

    @Override
    public String toString() {
        return "Person{" +
                "scenario=" + scenario +
                ", surname='" + surname + '\'' +
                ", givenName='" + givenName + '\'' +
                ", calledName='" + calledName + '\'' +
                ", state=" + state +
                ", doingWork=" + doingWork +
                ", location=" + location +
                ", command=" + command +
                ", strength=" + strength +
                ", intelligence=" + intelligence +
                ", politics=" + politics +
                ", glamour=" + glamour +
                ", movingDays=" + movingDays +
                "} " + super.toString();
    }
}
