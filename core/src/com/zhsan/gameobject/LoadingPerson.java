package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.zhsan.common.exception.FileReadException;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 24/5/2015.
 */
class LoadingPerson extends GameObject {

    private String surname;
    private String givenName;
    private String calledName;

    private Person.State state = Person.State.DEAD;
    private Person.DoingWork doingWork = Person.DoingWork.NONE;

    private LoadingLocationType loadingLocationType = LoadingLocationType.NONE;
    private int locationId = -1;

    private int command, strength, intelligence, politics, glamour;

    private int movingDays = 0;

    private int leaderFactionId = -1;
    private int portraitId;

    private LoadingPerson(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return surname + givenName;
    }

    public static final GameObjectList<LoadingPerson> fromCSV(FileHandle root, int version) {
        GameObjectList<LoadingPerson> result = new GameObjectList<>();

        FileHandle f = root.child(Person.SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                LoadingPerson data = new LoadingPerson(Integer.parseInt(line[0]));
                if (version == 1) {
                    data.surname = line[3];
                    data.givenName = line[4];
                    data.calledName = line[5];
                    if (Boolean.parseBoolean(line[1])) {
                        if (Boolean.parseBoolean(line[2])) {
                            data.state = Person.State.NORMAL;
                        } else {
                            data.state = Person.State.DEAD;
                        }
                    } else {
                        data.state = Person.State.UNAVAILABLE;
                    }
                    data.strength = Integer.parseInt(line[16]);
                    data.command = Integer.parseInt(line[17]);
                    data.intelligence = Integer.parseInt(line[18]);
                    data.politics = Integer.parseInt(line[19]);
                    data.glamour = Integer.parseInt(line[20]);
                    data.movingDays = Integer.parseInt(line[62]);
                    data.portraitId = Integer.parseInt(line[7]);
                } else {
                    data.portraitId = Integer.parseInt(line[1]);
                    data.surname = line[2];
                    data.givenName = line[3];
                    data.calledName = line[4];
                    data.state = Person.State.fromCSV(line[5]);
                    data.loadingLocationType = LoadingLocationType.fromCSV(line[6]);
                    data.locationId = Integer.parseInt(line[7]);
                    data.movingDays = Integer.parseInt(line[8]);
                    data.strength = Integer.parseInt(line[9]);
                    data.command = Integer.parseInt(line[10]);
                    data.intelligence = Integer.parseInt(line[11]);
                    data.politics = Integer.parseInt(line[12]);
                    data.glamour = Integer.parseInt(line[13]);
                    data.doingWork = Person.DoingWork.fromCSV(line[14]);
                }

                result.add(data);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void setup(GameObjectList<LoadingPerson> persons, GameObjectList<LoadingArchitecture> architectures, GameObjectList<LoadingFaction> factions) {
        for (LoadingPerson p : persons) {
            for (LoadingArchitecture a : architectures) {
                if (a.getPersons().contains(p.getId()) || a.getMovingPersons().contains(p.getId())) {
                    p.loadingLocationType = LoadingLocationType.ARCHITECTURE;
                    p.locationId = a.getId();
                    p.state = Person.State.NORMAL;
                } else if (a.getUnhiredPersons().contains(p.getId()) || a.getUnhiredMovingPersons().contains(p.getId())) {
                    p.loadingLocationType = LoadingLocationType.ARCHITECTURE;
                    p.locationId = a.getId();
                    p.state = Person.State.UNEMPLOYED;
                }
            }
            if (p.loadingLocationType != LoadingLocationType.ARCHITECTURE) {
                p.doingWork = Person.DoingWork.NONE;
            }
            for (LoadingFaction f : factions) {
                if (f.getLeaderId() == p.getId()) {
                    p.leaderFactionId = f.getId();
                }
            }
        }
    }

    public Person.State getState() {
        return state;
    }

    public int getMovingDays() {
        return movingDays;
    }

    public String getSurname() {
        return surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getCalledName() {
        return calledName;
    }

    public LoadingLocationType getLoadingLocationType() {
        return loadingLocationType;
    }

    public int getLocationId() {
        return locationId;
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

    public Person.DoingWork getDoingWork() {
        return doingWork;
    }

    public int getLeaderFactionId() {
        return leaderFactionId;
    }

    public int getPortraitId() {
        return portraitId;
    }

    public enum LoadingLocationType {
        NONE, ARCHITECTURE;

        public static LoadingLocationType fromCSV(String s) {
            switch (Integer.parseInt(s)) {
                case -1: return NONE;
                case 1: return ARCHITECTURE;
            }
            assert false;
            return null;
        }

        public String toCSV() {
            switch (this) {
                case NONE: return "-1";
                case ARCHITECTURE: return "1";
            }
            assert false;
            return null;
        }
    }
}
