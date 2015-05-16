package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.resources.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by Peter on 7/4/2015.
 */
public class ArchitectureKind extends GameObject {

    public static final String SAVE_FILE = "ArchitectureKind.csv";

    private float drawOffsetL, drawOffsetW;

    private ArchitectureKind(int id) {
        super(id);
    }

    public static final GameObjectList<ArchitectureKind> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        int version = scen.getGameSurvey().getVersion();

        GameObjectList<ArchitectureKind> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                ArchitectureKind kind = new ArchitectureKind(Integer.parseInt(line[0]));
                if (version == 1) {
                    kind.setName(line[1]);
                } else {
                    kind.setName(line[1]);
                }

                if (version == 1) {
                    kind.drawOffsetL = kind.getId() == 2 ? 4 : 0;
                    kind.drawOffsetW = kind.getId() == 2 ? 2 : 0;
                } else {
                    kind.drawOffsetL = Integer.parseInt(line[2]);
                    kind.drawOffsetW = Integer.parseInt(line[3]);
                }

                result.add(kind);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<ArchitectureKind> kinds) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_KIND_SAVE_HEADER).split(","));
            for (ArchitectureKind detail : kinds) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()), detail.getName(),
                        String.valueOf(detail.getDrawOffsetLength()), String.valueOf(detail.getDrawOffsetWidth())
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

    public float getDrawOffsetLength() {
        return drawOffsetL;
    }

    public float getDrawOffsetWidth() {
        return drawOffsetW;
    }
}
