package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.Point;
import com.zhsan.common.exception.EmptyFileException;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.resources.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Peter on 14/5/2015.
 */
public class GameData {

    public static final String SAVE_FILE = "GameData.csv";

    private List<Faction> playerFaction;
    private Faction currentPlayer;
    private List<Faction> factionQueue;

    private GameData(){}

    public static final GameData fromCSV(FileHandle root, @NotNull GameScenario scen) {
        int version = scen.getGameSurvey().getVersion();

        FileHandle f = root.child(SAVE_FILE);

        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read()))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                GameData data = new GameData();

                List<Integer> ints = XmlHelper.loadIntegerListFromXml(line[0]);
                data.playerFaction = new ArrayList<>();
                data.playerFaction.addAll(ints.stream().map(i -> scen.getFactions().get(i)).collect(Collectors.toList()));

                data.currentPlayer = scen.getFactions().get(Integer.parseInt(line[1]));

                ints = XmlHelper.loadIntegerListFromXml(line[2]);
                data.factionQueue = new ArrayList<>();
                data.factionQueue.addAll(ints.stream().map(i -> scen.getFactions().get(i)).collect(Collectors.toList()));
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        throw new FileReadException(f.path(), new EmptyFileException());
    }

    public static final void toCSV(FileHandle root, GameData data) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.GAME_DATA_SAVE_HEADER).split(","));
            writer.writeNext(new String[]{
                    data.playerFaction.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.joining(" ")),
                    String.valueOf(data.currentPlayer.getId()),
                    data.factionQueue.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.joining(" "))
            });
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

}
