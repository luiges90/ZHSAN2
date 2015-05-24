package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.resources.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

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

    public enum LocationType {
        ARHITECTURE;

        public static LocationType fromCSV(String s) {
            switch (Integer.parseInt(s)) {
                case 1: return ARHITECTURE;
            }
            assert false;
            return null;
        }

        public String toCSV() {
            switch (this) {
                case ARHITECTURE: return "1";
            }
            assert false;
            return null;
        }
    }

    private GameScenario scenario;

    private String surname;
    private String givenName;
    private String calledName;

    private State state;

    private LocationType locationType;
    private int locationId;

    private int movingDays = 0;

    private Person(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return surname + givenName;
    }

    public static final GameObjectList<Person> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        int version = scen.getGameSurvey().getVersion();

        GameObjectList<Person> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Person data = new Person(Integer.parseInt(line[0]));
                if (version == 1) {
                    data.surname = line[3];
                    data.givenName = line[4];
                    data.calledName = line[5];
                    if (Boolean.parseBoolean(line[1])) {
                        if (Boolean.parseBoolean(line[2])) {
                            data.state = State.NORMAL;
                        } else {
                            data.state = State.DEAD;
                        }
                    } else {
                        data.state = State.UNDEBUTTED;
                    }
                    data.movingDays = Integer.parseInt(line[62]);
                } else {
                    data.surname = line[1];
                    data.givenName = line[2];
                    data.calledName = line[3];
                    data.state = State.fromCSV(line[4]);
                    data.locationType = LocationType.fromCSV(line[5]);
                    data.locationId = Integer.parseInt(line[6]);
                    data.movingDays = Integer.parseInt(line[7]);
                }

                data.scenario = scen;
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
                writer.writeNext(new String[]{
                        String.valueOf(d.getId()),
                        d.surname,
                        d.givenName,
                        d.calledName,
                        d.state.toCSV(),
                        d.locationType.toCSV(),
                        String.valueOf(d.locationId),
                        String.valueOf(d.movingDays)
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

    public static final void setup(GameScenario scenario) {
        for (Person p : scenario.getPersons()) {
            for (Architecture a : scenario.getArchitectures()) {
                if (a.getPersons().contains(p.getId()) || a.getMovingPersons().contains(p.getId())) {
                    p.locationType = LocationType.ARHITECTURE;
                    p.locationId = a.getId();
                    p.state = State.NORMAL;
                } else if (a.getUnhiredPersons().contains(p.getId()) || a.getUnhiredMovingPersons().contains(p.getId())) {
                    p.locationType = LocationType.ARHITECTURE;
                    p.locationId = a.getId();
                    p.state = State.UNHIRED;
                }
            }
        }
    }

    public GameObject getLocation() {
        if (locationType == null) {
            return null;
        }
        switch (locationType) {
            case ARHITECTURE:
                return scenario.getArchitectures().get(locationId);
        }
        assert false;
        return null;
    }

    public State getState() {
        return state;
    }

    public int getMovingDays() {
        return movingDays;
    }
}
