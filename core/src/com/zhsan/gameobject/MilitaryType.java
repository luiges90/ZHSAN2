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
public final class MilitaryType extends GameObject {

    public static final String SAVE_FILE = "MilitaryType.csv";

    private final String name;

    private MilitaryType(int id, String name) {
        super(id);
        this.name = name;
    }

    public static final GameObjectList<MilitaryType> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<MilitaryType> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                MilitaryType type = new MilitaryTypeBuilder().setId(Integer.parseInt(line[0]))
                        .setName(line[1])
                        .createMilitaryType();

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

    @Override
    public String getName() {
        return name;
    }

    public static class MilitaryTypeBuilder {
        private int id;
        private String name;

        public MilitaryTypeBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public MilitaryTypeBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public MilitaryType createMilitaryType() {
            return new MilitaryType(id, name);
        }
    }
}
