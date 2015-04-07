package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.resources.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by Peter on 7/4/2015.
 */
public class ArchitectureKind extends GameObject {

    public static final String SAVE_FILE = "ArchitectureKind.csv";

    private static final class ImageQuantifier {
        private enum Quantifier { DEFAULT, DIAGONAL_SQUARE, HORIZONTAL, VERTICAL }

        public final Quantifier quantifier;
        public final int sizeX, sizeY;

        public ImageQuantifier(@NotNull Quantifier quantifier, int sizeX, int sizeY) {
            this.quantifier = quantifier;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ImageQuantifier that = (ImageQuantifier) o;

            if (sizeX != that.sizeX) return false;
            if (sizeY != that.sizeY) return false;
            return quantifier == that.quantifier;
        }

        @Override
        public int hashCode() {
            int result = quantifier.hashCode();
            result = 31 * result + sizeX;
            result = 31 * result + sizeY;
            return result;
        }
    }

    private Map<ImageQuantifier, Texture> images;

    private ArchitectureKind(int id) {
        super(id);
    }

    public static final GameObjectList<ArchitectureKind> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        int version = scen.getGameSurvey().getVersion();

        GameObjectList<ArchitectureKind> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read()))) {
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
                        String.valueOf(detail.id), detail.getName()
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

}
