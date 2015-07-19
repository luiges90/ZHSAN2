package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 19/7/2015.
 */
public class MilitaryKind extends GameObject {

    public static final String SAVE_FILE = "MilitaryKind.csv";

    private GameScenario scen;

    private MilitaryType type;

    private String name;
    private String description;
    private boolean canOnlyCreateAtArchitecture;
    private int cost;
    private float transportCost;

    public static GameObjectList<MilitaryKind> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<MilitaryKind> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                MilitaryKind kind = new MilitaryKind(Integer.parseInt(line[0]));
                kind.type = scen.getMilitaryTypes().get(Integer.parseInt(line[1]));
                kind.name = line[2];
                kind.description = line[3];
                kind.canOnlyCreateAtArchitecture = Boolean.parseBoolean(line[4]);
                kind.cost = Integer.parseInt(line[5]);
                kind.transportCost = Float.parseFloat(line[6]);

                kind.scen = scen;

                result.add(kind);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static void toCSV(FileHandle root, GameObjectList<MilitaryKind> kinds) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.MILITARY_KIND_SAVE_HEADER).split(","));
            for (MilitaryKind detail : kinds) {
                writer.writeNext(new String[]{
                        String.valueOf(detail.getId()),
                        String.valueOf(detail.type.getId()),
                        detail.name,
                        detail.description,
                        String.valueOf(detail.canOnlyCreateAtArchitecture),
                        String.valueOf(detail.cost),
                        String.valueOf(detail.transportCost)
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }
    }

    protected MilitaryKind(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return name;
    }

    public String getTypeName() {
        return type.getName();
    }

    public int getCost() {
        return cost;
    }

}
