package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.zhsan.common.Point;
import com.zhsan.common.exception.EmptyFileException;
import com.zhsan.common.exception.FileReadException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by Peter on 8/3/2015.
 */
public final class GameSurvey {

    public final String title;
    public final LocalDate startDate;
    public final LocalDateTime saveDate;
    public final String message;
    public final String description;
    public final Point cameraPosition;
    public final int version;

    private GameSurvey(String title, LocalDate startDate, LocalDateTime saveDate, String message, Point initialPosition, String description, int version) {
        this.title = title;
        this.startDate = startDate;
        this.saveDate = saveDate;
        this.message = message;
        this.cameraPosition = initialPosition;
        this.description = description;
        this.version = version;
    }

    public static final GameSurvey fromCSV(String path) {
        FileHandle f = Gdx.files.external(path + File.separator + "GameSurvey.csv");
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
                b.setSaveDate(LocalDateTime.parse(line[4], GameScenario.DATE_TIME_FORMAT));
                b.setMessage(line[5]);
                b.setInitialPosition(Point.fromCSV(line[6]));
                b.setDescription(line[7]);
                if (line.length >= 9) {
                    b.setVersion(Integer.parseInt(line[8]));
                } else {
                    b.setVersion(1);
                }

                return b.createGameSurvey();
            }
        } catch (IOException e) {
            throw new FileReadException(path + File.separator + "GameSurvey.csv", e);
        }

        throw new FileReadException(path + File.separator + "GameSurvey.csv", new EmptyFileException());
    }

    private static class GameSurveyBuilder {
        private String title;
        private LocalDate startDate;
        private LocalDateTime saveDate;
        private String message;
        private Point initialPosition;
        private String description;
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

        public GameSurvey createGameSurvey() {
            return new GameSurvey(title, startDate, saveDate, message, initialPosition, description, version);
        }
    }
}
