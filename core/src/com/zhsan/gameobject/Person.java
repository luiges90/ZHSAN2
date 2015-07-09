package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import com.zhsan.lua.LuaAI;

import java.io.IOException;

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

    private class LocationType {
        private final Architecture architecture;

        public LocationType(Architecture architecture) {
            this.architecture = architecture;
        }

        public GameObject get() {
            if (architecture != null) {
                return architecture;
            }
            return null;
        }

        public LoadingPerson.LoadingLocationType getLoadingLocationType() {
            if (architecture != null) {
                return LoadingPerson.LoadingLocationType.ARCHITECTURE;
            }
            return LoadingPerson.LoadingLocationType.NONE;
        }

        public int getLoadingLocationId() {
            if (architecture != null) {
                return architecture.getId();
            }
            return -1;
        }
    }

    public enum DoingWork {
        NONE, AGRICULTURE, COMMERCE, TECHNOLOGY, MORALE, ENDURANCE, MAYOR;

        public static DoingWork fromCSV(String s) {
            switch (s) {
                case "none": return NONE;
                case "agriculture": return AGRICULTURE;
                case "commerce": return COMMERCE;
                case "technology": return TECHNOLOGY;
                case "morale": return MORALE;
                case "endurance": return ENDURANCE;
                case "mayor": return MAYOR;
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
            }
            assert false;
            return null;
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

    public Person(LoadingPerson from, GameScenario scenario) {
        super(from.getId());
        this.scenario = scenario;

        this.portraitId = from.getPortraitId();
        this.surname = from.getSurname();
        this.givenName = from.getGivenName();
        this.calledName = from.getCalledName();
        this.state = from.getState();
        this.command = from.getCommand();
        this.strength = from.getStrength();
        this.intelligence = from.getIntelligence();
        this.politics = from.getPolitics();
        this.glamour = from.getGlamour();
        this.doingWork = from.getDoingWork();

        switch (from.getLoadingLocationType()) {
            case ARCHITECTURE:
                this.location = new LocationType(scenario.getArchitectures().get(from.getLocationId()));
        }

        if (this.getBelongedFaction() != null && from.getLeaderFactionId() == this.getBelongedFaction().getId() &&
                (this.getState() == State.NORMAL || this.getState() == State.CAPTIVE)) {
            this.getBelongedFaction().setLeaderUnchecked(this);
        }
    }

    public static final void toCSV(FileHandle root, GameObjectList<Person> data) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.PERSON_SAVE_HEADER).split(","));
            for (Person d : data) {
                writer.writeNext(new String[]{
                        String.valueOf(d.getId()),
                        String.valueOf(d.portraitId),
                        d.surname,
                        d.givenName,
                        d.calledName,
                        d.state.toCSV(),
                        d.location.getLoadingLocationType().toCSV(),
                        String.valueOf(d.location.getLoadingLocationId()),
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
    @LuaAI.ExportGetterToLua
    public String getName() {
        return surname + givenName;
    }

    public State getState() {
        return state;
    }

    public Faction getBelongedFaction() {
        if (state == State.NORMAL) {
            GameObject t = location.get();
            if (t instanceof Architecture) {
                return ((Architecture) t).getBelongedFaction();
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

    @LuaAI.ExportGetterToLua
    public int getCommand() {
        return command;
    }

    @LuaAI.ExportGetterToLua
    public int getStrength() {
        return strength;
    }

    @LuaAI.ExportGetterToLua
    public int getIntelligence() {
        return intelligence;
    }

    @LuaAI.ExportGetterToLua
    public int getPolitics() {
        return politics;
    }

    @LuaAI.ExportGetterToLua
    public int getGlamour() {
        return glamour;
    }

    @LuaAI.ExportGetterToLua
    public int getAbilitySum() {
        return command + strength + intelligence + politics + glamour;
    }

    @LuaAI.ExportGetterToLua
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

    public void setDoingWork(DoingWork work) {
        if (location.get() instanceof Architecture && this.state == State.NORMAL) {
            if (work == DoingWork.MAYOR) {
                ((Architecture) this.getLocation()).getMayor().doingWork = DoingWork.NONE;
                this.doingWork = DoingWork.MAYOR;
            } else {
                if (this.doingWork != DoingWork.MAYOR) {
                    this.doingWork = work;
                } else {
                    throw new IllegalStateException("You must not unassign a mayor by setDoingWork, change mayor by setting any other person to mayor first.");
                }
            }
        } else {
            throw new IllegalStateException("Person should be in an architecture, hired, if he is doing work. Person state = " + this);
        }
    }

    public String getDoingWorkString() {
        switch (getDoingWorkType()) {
            case NONE: return GlobalStrings.getString(GlobalStrings.Keys.NO_CONTENT);
            case AGRICULTURE: return GlobalStrings.getString(GlobalStrings.Keys.AGRICULTURE);
            case COMMERCE: return GlobalStrings.getString(GlobalStrings.Keys.COMMERCE);
            case TECHNOLOGY: return GlobalStrings.getString(GlobalStrings.Keys.TECHNOLOGY);
            case MORALE: return GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_MORALE);
            case ENDURANCE: return GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_ENDURANCE);
            case MAYOR: return GlobalStrings.getString(GlobalStrings.Keys.MAYOR);
            default:
                assert false;
                return GlobalStrings.getString(GlobalStrings.Keys.NO_CONTENT);
        }
    }

    @LuaAI.ExportGetterToLua
    public int getAgricultureAbility() {
        return 2 * getPolitics() + 2 * getGlamour();
    }

    @LuaAI.ExportGetterToLua
    public int getCommerceAbility() {
        return getIntelligence() + 2 * getPolitics() + getGlamour();
    }

    @LuaAI.ExportGetterToLua
    public int getTechnologyAbility() {
        return 2 * getIntelligence() + 2 * getPolitics();
    }

    @LuaAI.ExportGetterToLua
    public int getMoraleAbility() {
        return getCommand() + getPolitics() + 2 * getGlamour();
    }

    @LuaAI.ExportGetterToLua
    public int getEnduranceAbility() {
        return getStrength() + getCommand() + getIntelligence() + getPolitics();
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
