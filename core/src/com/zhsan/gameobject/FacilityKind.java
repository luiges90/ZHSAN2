package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.resources.GlobalStrings;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 25/5/2015.
 */
public class FacilityKind extends GameObject {

    public static final String SAVE_FILE = "FacilityKind.csv";

    private String name;
    private String xmlName;

    private int endurance;
    private boolean indestructible;

    private FacilityKind(int id) {
        super(id);
    }

    public static final GameObjectList<FacilityKind> fromCSV(FileHandle root, @Nullable GameScenario scen, FileHandle defaultRoot, int defaultVersion) {
        int version = scen == null ? defaultVersion : scen.getGameSurvey().getVersion();

        if (version == 1) {
            return fromCSV(defaultRoot, null, null, defaultVersion);
        }

        GameObjectList<FacilityKind> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                FacilityKind kind = new FacilityKind(Integer.parseInt(line[0]));

                kind.name = line[1];
                kind.xmlName = line[2];
                kind.endurance = Integer.parseInt(line[3]);
                kind.indestructible = Boolean.parseBoolean(line[4]);

                result.add(kind);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<FacilityKind> kinds) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.FACILITY_KIND_SAVE_HEADER).split(","));
            for (FacilityKind detail : kinds) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        detail.name,
                        detail.xmlName,
                        String.valueOf(detail.endurance),
                        String.valueOf(detail.indestructible)
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
}
