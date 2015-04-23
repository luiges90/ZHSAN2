package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.resources.GlobalStrings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Peter on 14/3/2015.
 */
public class Faction extends GameObject {

    public static final String SAVE_FILE = "Faction.csv";

    private Set<Integer> sectionIds = new HashSet<>();

    private Faction(int id) {
        super(id);
    }

    public static final GameObjectList<Faction> fromCSVQuick(FileHandle root, int version) {
        GameObjectList<Faction> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read()))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Faction t = new Faction(Integer.parseInt(line[0]));
                if (version == 1) {
                    t.setName(line[3]);
                    t.sectionIds = new HashSet<>(XmlHelper.loadIntegerListFromXml(line[9]));
                } else {
                    t.setName(line[1]);
                }
                result.add(t);
            }

            return result;
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }
    }

    public static final GameObjectList<Faction> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        int version = scen.getGameSurvey().getVersion();

        GameObjectList<Faction> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read()))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Faction t = new Faction(Integer.parseInt(line[0]));
                if (version == 1) {
                    t.setName(line[3]);
                } else {
                    t.setName(line[1]);
                }
                result.add(t);
            }

            return result;
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }
    }

    public static final void toCSV(FileHandle root, GameObjectList<Faction> data) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.FACTION_SAVE_HEADER).split(","));
            for (Faction d : data) {
                writer.writeNext(new String[]{
                        String.valueOf(d.getId()),
                        d.getName()
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

    public static final void setup(GameScenario scenario) {
        for (Faction f : scenario.getFactions()) {
            for (Section s : scenario.getSections()) {
                if (s.getBelongedFactionId() == f.getId()) {
                    f.sectionIds.add(s.getId());
                }
            }
        }
    }

    public Set<Integer> getSectionIds() {
        return Collections.unmodifiableSet(sectionIds);
    }
}
