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

    private String name;
    private float drawOffsetL, drawOffsetW;

    private ArchitectureKind(int id) {
        super(id);
    }

    public static final GameObjectList<ArchitectureKind> fromCSV(FileHandle root, @NotNull GameScenario scen, FileHandle defaultRoot, int defaultVersion) {
        int version = defaultRoot == null ? defaultVersion : scen.getGameSurvey().getVersion();

        if (version == 1) {
            return fromCSV(defaultRoot, scen, null, defaultVersion);
        }

        GameObjectList<ArchitectureKind> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                ArchitectureKind kind = new ArchitectureKind(Integer.parseInt(line[0]));

                kind.setName(line[1]);

                kind.drawOffsetL = Float.parseFloat(line[2]);
                kind.drawOffsetW = Float.parseFloat(line[3]);

                result.add(kind);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void toCSV(FileHandle root, GameObjectList<ArchitectureKind> kinds) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_KIND_SAVE_HEADER).split(","));
            for (ArchitectureKind detail : kinds) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        detail.getName(),
                        String.valueOf(detail.getDrawOffsetLength()),
                        String.valueOf(detail.getDrawOffsetWidth())
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

    public void setName(String name) {
        this.name = name;
    }

    public float getDrawOffsetLength() {
        return drawOffsetL;
    }

    public float getDrawOffsetWidth() {
        return drawOffsetW;
    }
}
