package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 25/5/2015.
 */
public class Facility extends GameObject {

    public static final String SAVE_FILE = "Facility.csv";

    private GameScenario scenario;

    private FacilityKind kind;
    private Point location;
    private int endurance;

    private Architecture belongedArchitecture;

    public Facility(int id, GameScenario scen) {
        super(id);
        this.scenario = scen;
    }

    public static final GameObjectList<Facility> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        int version = scen.getGameSurvey().getVersion();

        GameObjectList<Facility> result = new GameObjectList<>();

        if (version == 1) {
            return result;
        }

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Facility data = new Facility(Integer.parseInt(line[0]), scen);

                data.kind = scen.getFacilityKinds().get(Integer.parseInt(line[1]));
                data.location = Point.fromCSV(line[2]);
                data.belongedArchitecture = scen.getArchitectures().get(Integer.parseInt(line[3]));
                data.endurance = Integer.parseInt(line[4]);

                result.add(data);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<Facility> kinds) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.FACILITY_SAVE_HEADER).split(","));
            for (Facility detail : kinds) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        String.valueOf(detail.kind.getId()),
                        detail.location.toCSV(),
                        String.valueOf(detail.belongedArchitecture.getId()),
                        String.valueOf(detail.endurance)
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

    @Override
    public String getName() {
        return kind.getName();
    }

    public Architecture getBelongedArchitecture() {
        return belongedArchitecture;
    }

    public FacilityKind getKind() {
        return kind;
    }

    public Point getLocation() {
        return location;
    }

    public Facility setKind(FacilityKind kind) {
        this.kind = kind;
        return this;
    }

    public Facility setLocation(Point location) {
        this.location = location;
        return this;
    }

    public Facility setEndurance(int endurance) {
        this.endurance = endurance;
        return this;
    }

    public Facility setBelongedArchitecture(Architecture belongedArchitecture) {
        this.belongedArchitecture = belongedArchitecture;
        return this;
    }
}
