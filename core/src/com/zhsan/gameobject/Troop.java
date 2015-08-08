package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.Pair;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Peter on 8/8/2015.
 */
public class Troop extends GameObject {

    public static final String SAVE_FILE = "Troop.csv";

    private GameScenario scenario;

    private Military military;

    private Point position;

    public static final GameObjectList<Troop> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<Troop> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Troop data = new Troop(Integer.parseInt(line[0]));
                data.military = scen.getMilitary(Integer.parseInt(line[1]));
                data.position = Point.fromCSV(line[2]);
                data.scenario = scen;

                result.add(data);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<Troop> types) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.TROOP_SAVE_HEADER).split(","));
            for (Troop detail : types) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        String.valueOf(detail.military.getId()),
                        detail.position.toCSV()
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }
    }

    public Troop(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return military.getName();
    }
}
