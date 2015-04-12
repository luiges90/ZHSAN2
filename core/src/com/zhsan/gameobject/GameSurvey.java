package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.Point;
import com.zhsan.common.exception.EmptyFileException;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.resources.GlobalStrings;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Peter on 8/3/2015.
 */
public final class GameSurvey {

    public static final DateTimeFormatter SAVE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/M/dd H:mm:ss");
    public static final String SAVE_FILE = "GameSurvey.csv";

    public static final String DEFAULT_RESOURCE_PACK = "default";

    private final String title;
    private final LocalDate startDate;
    private final LocalDateTime saveDate;
    private final String message;
    private final String description;
    private Point cameraPosition;
    private final String resourcePackName;
    private final int version;

    private GameSurvey(String title, LocalDate startDate, LocalDateTime saveDate, String message,
                       Point initialPosition, String description, String resourcePackName, int version) {
        this.title = title;
        this.startDate = startDate;
        this.saveDate = saveDate;
        this.message = message;
        this.cameraPosition = initialPosition;
        this.description = description;
        this.resourcePackName = resourcePackName == null || resourcePackName.isEmpty() ? "default" : resourcePackName;
        this.version = version;
    }

    public static final GameSurvey fromCSV(FileHandle root) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read()))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                GameSurveyBuilder b = new GameSurveyBuilder();
                b.setTitle(line[0]);
                b.setStartDate(LocalDate.of(
                        Integer.parseInt(line[1]),
                        Integer.parseInt(line[2]),
                        Integer.parseInt(line[3])));
                b.setSaveDate(LocalDateTime.parse(line[4], SAVE_DATE_FORMAT));
                b.setMessage(line[5]);
                b.setInitialPosition(Point.fromCSV(line[6]));
                b.setDescription(line[7]);
                if (line.length >= 9) {
                    b.setResourcePackName(line[8]);
                    b.setVersion(Integer.parseInt(line[9]));
                } else {
                    b.setResourcePackName("");
                    b.setVersion(1);
                }

                return b.createGameSurvey();
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        throw new FileReadException(f.path(), new EmptyFileException());
    }

    public static final void toCSV(FileHandle root, GameSurvey gameSurvey) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.GAME_SURVEY_SAVE_HEADER).split(","));
            writer.writeNext(new String[]{
                    gameSurvey.title,
                    String.valueOf(gameSurvey.startDate.getYear()),
                    String.valueOf(gameSurvey.startDate.getMonth().getValue()),
                    String.valueOf(gameSurvey.startDate.getDayOfMonth()),
                    SAVE_DATE_FORMAT.format(LocalDateTime.now()),
                    gameSurvey.message,
                    gameSurvey.cameraPosition.toCSV(),
                    gameSurvey.description,
                    gameSurvey.resourcePackName,
                    String.valueOf(GameScenario.SAVE_VERSION)
            });
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

    public String getTitle() {
        return title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDateTime getSaveDate() {
        return saveDate;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    public Point getCameraPosition() {
        return cameraPosition;
    }

    public void setCameraPosition(Point cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public int getVersion() {
        return version;
    }

    public String getResourcePackName() {
        return resourcePackName;
    }

    private static class GameSurveyBuilder {
        private String title;
        private LocalDate startDate;
        private LocalDateTime saveDate;
        private String message;
        private Point initialPosition;
        private String description;
        private String resourcePackName;
        private int version;

        public GameSurveyBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public GameSurveyBuilder setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public GameSurveyBuilder setSaveDate(LocalDateTime saveDate) {
            this.saveDate = saveDate;
            return this;
        }

        public GameSurveyBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public GameSurveyBuilder setInitialPosition(Point initialPosition) {
            this.initialPosition = initialPosition;
            return this;
        }

        public GameSurveyBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public GameSurveyBuilder setVersion(int version) {
            this.version = version;
            return this;
        }

        public GameSurveyBuilder setResourcePackName(String resourcePackName) {
            this.resourcePackName = resourcePackName;
            return this;
        }

        public GameSurvey createGameSurvey() {
            return new GameSurvey(title, startDate, saveDate, message, initialPosition, description, resourcePackName, version);
        }
    }
}
