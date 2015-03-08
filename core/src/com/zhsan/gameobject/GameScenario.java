package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 8/3/2015.
 */
public class GameScenario {

    public static final String PATH = "GameData" + File.separator + "Scenarios" + File.separator;

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy/M/dd H:mm:ss");

    public static List<GameSurvey> loadAllGameSurveys() {
        List<GameSurvey> result = new ArrayList<>();

        FileHandle[] scenarios = Gdx.files.external(PATH).list();
        for (FileHandle f : scenarios) {
            if (f.isDirectory()) {
                result.add(GameSurvey.fromCSV(f.path()));
            }
        }

        return result;
    }

}
