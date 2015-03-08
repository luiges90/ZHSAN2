package com.zhsan.gameobject;

import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 * Created by Peter on 8/3/2015.
 */
public class GameScenario {

    public static final String PATH = "GameData" + File.separator + "Scenarios" + File.separator;

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private GameSurvey gameSurvey;

    public void loadGameSurvey(String fileName) {
        gameSurvey = GameSurvey.fromCSV(PATH + fileName);
    }

    public GameSurvey getGameSurvey() {
        return gameSurvey;
    }

}
