package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.Point;
import com.zhsan.common.exception.EmptyFileException;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.MapLayer;
import com.zhsan.resources.GlobalStrings;
import jdk.nashorn.internal.objects.Global;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameMap {

    public static final String SAVE_FILE = "Map.csv";
    public static final String MAP_DATA_FILE = "MapData.txt";

    private int zoom;
    private int width;
    private int height;
    private String fileName;
    private int imageCount;
    private int tileInEachImage;
    private TerrainDetail[][] mapData;

    public GameMap(int zoom, int width, int height, String fileName, int imageCount, int tileInEachImage, TerrainDetail[][] mapData) {
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
                result[j][i] = scen.getTerrainDetails().get(Integer.parseInt(split[i * result.length + j]));
            }
        }
        return result;
    }

    public static GameMap fromCSV(String path, @NotNull GameScenario scen) {
        FileHandle f = Gdx.files.external(path + File.separator + SAVE_FILE);
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
            throw new FileReadException(f.path(), e);
        }

        throw new FileReadException(f.path(), new EmptyFileException());
    }

    public static void toCSV(FileHandle root, GameMap map) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.MAP_SAVE_HEADER).split(","));
            writer.writeNext(new String[]{
                    String.valueOf(map.zoom),
                    String.valueOf(map.width),
                    String.valueOf(map.height),
                    map.fileName,
                    String.valueOf(map.imageCount),
                    String.valueOf(map.tileInEachImage)
            });
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

        FileHandle data = root.child(MAP_DATA_FILE);
        try (Writer writer = data.writer(false)) {
            for (int r = 0; r < map.mapData.length; ++r) {
                for (int c = 0; c < map.mapData.length; ++c) {
                    writer.write(String.format("%3s", map.mapData[c][r].id));
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            throw new FileWriteException(data.path(), e);
        }

    }

    /**
     * Zoom is defined as the size of each tile in the displayed map
     * @return
     */
    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getFileName() {
        return fileName;
    }

    public int getImageCount() {
        return imageCount;
    }

    public int getTileInEachImage() {
        return tileInEachImage;
    }

    public TerrainDetail getTerrainAt(int x, int y) {
        if (x >= 0 && x < this.getWidth() && y >= 0 && y < this.getHeight()) {
            return mapData[x][y];
        } else {
            return null;
        }
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
