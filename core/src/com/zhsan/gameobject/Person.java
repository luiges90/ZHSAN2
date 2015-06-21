package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.resources.GlobalStrings;

import java.io.IOException;

/**
 * Created by Peter on 24/5/2015.
 */
public class Person extends GameObject {

    public static final String SAVE_FILE = "Person.csv";

    public enum State {
        UNDEBUTTED, NORMAL, UNHIRED, CAPTIVE, DEAD;

        public static State fromCSV(String s) {
            switch (Integer.parseInt(s)) {
                case 1: return UNDEBUTTED;
                case 2: return NORMAL;
                case 3: return UNHIRED;
                case 4: return CAPTIVE;
                case 5: return DEAD;
            }
            assert false;
            return null;
        }

        public String toCSV() {
            switch (this) {
                case UNDEBUTTED: return "1";
                case NORMAL: return "2";
                case UNHIRED: return "3";
                case CAPTIVE: return "4";
                case DEAD: return "5";
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
        NONE, AGRICULTURE, COMMERCE, TECHNOLOGY, MORALE, ENDURANCE;

        public static DoingWork fromCSV(String s) {
            switch (Integer.parseInt(s)) {
                case 0: return NONE;
                case 1: return AGRICULTURE;
                case 2: return COMMERCE;
                case 3: return TECHNOLOGY;
                case 4: return MORALE;
                case 5: return ENDURANCE;
            }
            assert false;
            return null;
        }

        public String toCSV() {
            switch (this) {
                case NONE: return "0";
                case AGRICULTURE: return "1";
                case COMMERCE: return "2";
                case TECHNOLOGY: return "3";
                case MORALE: return "4";
                case ENDURANCE: return "5";
            }
            assert false;
            return null;
        }
    }

    private GameScenario scenario;

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

        this.surname = from.getSurname();
        this.givenName = from.getGivenName();
        this.calledName = from.getCalledName();
        this.state = from.getState();
        this.command = from.getCommand();
        this.strength = from.getStrength();
        this.intelligence = from.getIntelligence();
        this.politics = from.getPolitics();
        this.glamour = from.getGlamour();

        switch (from.getLoadingLocationType()) {
            case ARCHITECTURE:
                this.location = new LocationType(scenario.getArchitectures().get(from.getLocationId()));
        }
    }


    public static final void toCSV(FileHandle root, GameObjectList<Person> data) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.PERSON_SAVE_HEADER).split(","));
            for (Person d : data) {
                writer.writeNext(new String[]{
                        String.valueOf(d.getId()),
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

    public int getCommand() {
        return command;
    }

    public int getStrength() {
        return strength;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getPolitics() {
        return politics;
    }

    public int getGlamour() {
        return glamour;
    }

    public DoingWork getDoingWork() {
        if (location.get() instanceof Architecture && this.state == State.NORMAL) {
            return doingWork;
        } else {
            return DoingWork.NONE;
        }
    }

    public void setDoingWork(DoingWork work) {
        if (location.get() instanceof Architecture && this.state == State.NORMAL) {
            this.doingWork = work;
        } else {
            throw new IllegalStateException("Person should be in an architecture, hired, if he is doing work. Person state = " + this);
        }
    }

    public String getDoingWorkString() {
        switch (getDoingWork()) {
            case NONE: return GlobalStrings.getString(GlobalStrings.Keys.NO_CONTENT);
            case AGRICULTURE: return GlobalStrings.getString(GlobalStrings.Keys.AGRICULTURE);
            case COMMERCE: return GlobalStrings.getString(GlobalStrings.Keys.COMMERCE);
            case TECHNOLOGY: return GlobalStrings.getString(GlobalStrings.Keys.TECHNOLOGY);
            case MORALE: return GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_MORALE);
            case ENDURANCE: return GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_ENDURANCE);
            default:
                assert false;
                return GlobalStrings.getString(GlobalStrings.Keys.NO_CONTENT);
        }
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
