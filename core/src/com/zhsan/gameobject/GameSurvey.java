package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.zhsan.common.Paths;
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
    public final Point initialPosition;

    private GameSurvey(String title, LocalDate startDate, LocalDateTime saveDate, String message, String description, Point initialPosition) {
        this.title = title;
        this.startDate = startDate;
        this.saveDate = saveDate;
        this.message = message;
        this.description = description;
        this.initialPosition = initialPosition;
    }

    public static final GameSurvey fromCSV(String path) {
        FileHandle f = Gdx.files.external(path + File.separator + "GameSurvey.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read()))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Builder b = new Builder();
                b.setTitle(line[0]);
                b.setStartDate(LocalDate.of(
                        Integer.parseInt(line[1]),
                        Integer.parseInt(line[2]),
                        Integer.parseInt(line[3])));
                b.setSaveDate(LocalDateTime.parse(line[4], GameScenario.DATE_TIME_FORMAT));
                b.setMessage(line[5]);
                b.setInitialPosition(Point.fromCSV(line[6]));
                b.setDescription(line[7]);

                return b.build();
            }
        } catch (IOException e) {
            throw new FileReadException(path + File.separator + "GameSurvey.csv", e);
        }

        throw new FileReadException(path + File.separator + "GameSurvey.csv", new EmptyFileException());
    }

    public static final class Builder {

        private String title;
        private LocalDate startDate;
        private LocalDateTime saveDate;
        private String message;
        private String description;
        private Point initialPosition;

        public String getTitle() {
            return title;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public Builder setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public LocalDateTime getSaveDate() {
            return saveDate;
        }

        public Builder setSaveDate(LocalDateTime saveDate) {
            this.saveDate = saveDate;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Point getInitialPosition() {
            return initialPosition;
        }

        public Builder setInitialPosition(Point initialPosition) {
            this.initialPosition = initialPosition;
            return this;
        }

        public GameSurvey build() {
            return new GameSurvey(title, startDate, saveDate, message, description, initialPosition);
        }

    }
}
