package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 19/7/2015.
 */
public class MilitaryType extends GameObject {

    public static final String SAVE_FILE = "MilitaryType.csv";

    private String name;

    public static final GameObjectList<MilitaryType> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<MilitaryType> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                MilitaryType type = new MilitaryType(Integer.parseInt(line[0]));
                type.name = line[1];

                result.add(type);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<MilitaryType> types) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.MILITARY_TYPE_SAVE_HEADER).split(","));
            for (MilitaryType detail : types) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        detail.getName(),
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }
    }

    protected MilitaryType(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return name;
    }
}
