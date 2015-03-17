package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.zhsan.common.Point;
import com.zhsan.common.exception.EmptyFileException;
import com.zhsan.common.exception.FileReadException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameMap {

    public final int zoom;
    public final int width;
    public final int height;
    public final String fileName;
    public final int imageCount;
    public final int tileInEachImage;
    public final TerrainDetail[][] mapData;

    private GameMap(int zoom, int width, int height, String fileName, int imageCount, int tileInEachImage, TerrainDetail[][] mapData) {
        this.zoom = zoom;
        this.width = width;
        this.height = height;
        this.fileName = fileName;
        this.imageCount = imageCount;
        this.tileInEachImage = tileInEachImage;
        this.mapData = mapData;
    }

    private static TerrainDetail[][] readMapData(GameScenario scen, int width, int height, String line) {
        TerrainDetail[][] result = new TerrainDetail[height][width];
        String[] split = line.split("\\s");
        for (int i = 0; i < result.length; ++i) {
            for (int j = 0; j < result[i].length; ++j) {
                result[i][j] = scen.getTerrainDetails().get(Integer.parseInt(split[i * result.length + j]));
            }
        }
        return result;
    }

    public static GameMap fromCSV(String path, @NotNull GameScenario scen) {
        FileHandle f = Gdx.files.external(path + File.separator + "Map.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read()))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                GameMapBuilder builder = new GameMapBuilder();
                builder.setZoom(Integer.parseInt(line[2]));
                builder.setWidth(Integer.parseInt(line[3]));
                builder.setHeight(Integer.parseInt(line[4]));
                builder.setMapData(readMapData(scen, builder.width, builder.height, line[5]));
                builder.setFileName(line[6]);
                builder.setImageCount(Integer.parseInt(line[7]));
                builder.setTileInEachImage(Integer.parseInt(line[8]));

                return builder.createGameMap();
            }
        } catch (IOException e) {
            throw new FileReadException(path + File.separator + "Map.csv", e);
        }

        throw new FileReadException(path + File.separator + "Map.csv", new EmptyFileException());
    }

    private static class GameMapBuilder {
        private int zoom;
        private int width;
        private int height;
        private String fileName;
        private int imageCount;
        private int tileInEachImage;
        private TerrainDetail[][] mapData;

        public GameMapBuilder setZoom(int zoom) {
            this.zoom = zoom;
            return this;
        }

        public GameMapBuilder setWidth(int width) {
            this.width = width;
            return this;
        }

        public GameMapBuilder setHeight(int height) {
            this.height = height;
            return this;
        }

        public GameMapBuilder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public GameMapBuilder setImageCount(int imageCount) {
            this.imageCount = imageCount;
            return this;
        }

        public GameMapBuilder setTileInEachImage(int tileInEachImage) {
            this.tileInEachImage = tileInEachImage;
            return this;
        }

        public GameMapBuilder setMapData(TerrainDetail[][] mapData) {
            this.mapData = mapData;
            return this;
        }

        public GameMap createGameMap() {
            return new GameMap(zoom, width, height, fileName, imageCount, tileInEachImage, mapData);
        }
    }
}
