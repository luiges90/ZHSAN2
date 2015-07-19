package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.Pair;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 19/7/2015.
 */
public class Military extends GameObject {

    public static final String SAVE_FILE = "Military.csv";

    public static class LocationType {

        private static final int ARCHITECTURE = 1;

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

        public int getLocationId() {
            if (architecture != null) {
                return architecture.getId();
            }
            return -1;
        }

        public static LocationType fromCSV(String type, String id, GameScenario scen) {
            int typeInt = Integer.parseInt(type);
            return new LocationType(scen.getArchitectures().get(Integer.parseInt(id)));
        }

        public Pair<String, String> toCSV() {
            int type, id;
            if (architecture != null) {
                type = ARCHITECTURE;
                id = architecture.getId();
            } else {
                assert false;
                return null;
            }
            return new Pair<>(String.valueOf(type), String.valueOf(id));
        }

    }

    private String name;

    private MilitaryKind kind;

    private LocationType location;

    public static final GameObjectList<Military> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<Military> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Military type = new Military(Integer.parseInt(line[0]));
                type.name = line[1];
                type.kind = scen.getMilitaryKinds().get(Integer.parseInt(line[2]));
                type.location = LocationType.fromCSV(line[3], line[4], scen);

                result.add(type);
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
                        detail.getName(),
                        String.valueOf(detail.kind.getId()),
                        savedLocation.x,
                        savedLocation.y,
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }
    }

    public Military(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MilitaryKind getKind() {
        return kind;
    }

    public void setKind(MilitaryKind kind) {
        this.kind = kind;
    }

    public void setLocation(Architecture location) {
        this.location = new LocationType(location);
    }

    public LocationType getLocation() {
        return location;
    }

}
