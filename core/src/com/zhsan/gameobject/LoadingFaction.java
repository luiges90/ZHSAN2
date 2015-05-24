package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.opencsv.CSVReader;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.XmlHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by Peter on 14/3/2015.
 */
class LoadingFaction extends GameObject {

    private static final String COLOR_FILE = "Color.csv";

    private String name;

    private GameScenario scenario;

    private Set<Integer> sectionIds = new HashSet<>();
    private Color color;

    private LoadingFaction(int id) {
        super(id);
    }

    public static final GameObjectList<LoadingFaction> fromCSVQuick(FileHandle root, int version) {
        GameObjectList<LoadingFaction> result = new GameObjectList<>();

        FileHandle f = root.child(Faction.SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                LoadingFaction t = new LoadingFaction(Integer.parseInt(line[0]));
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

    public static final GameObjectList<LoadingFaction> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        int version = scen.getGameSurvey().getVersion();

        Map<Integer, Color> colors = new HashMap<>();
        if (version == 1) {
            FileHandle color = root.child(COLOR_FILE);
            try (CSVReader reader = new CSVReader(new InputStreamReader(color.read(), "UTF-8"))) {
                String[] line;
                int index = 0;
                while ((line = reader.readNext()) != null) {
                    index++;
                    if (index == 1) continue; // skip first line.
                    colors.put(Integer.parseInt(line[0]),
                            XmlHelper.loadColorFromXml(Integer.parseUnsignedInt(line[1])));
                }

            } catch (IOException e) {
                throw new FileReadException(color.path(), e);
            }
        }

        GameObjectList<LoadingFaction> result = new GameObjectList<>();

        FileHandle f = root.child(Faction.SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                LoadingFaction t = new LoadingFaction(Integer.parseInt(line[0]));
                if (version == 1) {
                    t.setName(line[3]);
                    t.color = colors.get(Integer.parseInt(line[2]));
                    t.sectionIds = new HashSet<>(XmlHelper.loadIntegerListFromXml(line[9]));
                } else {
                    t.setName(line[1]);
                    t.color = XmlHelper.loadColorFromXml(Integer.parseUnsignedInt(line[2]));
                }

                t.scenario = scen;
                result.add(t);
            }

            return result;
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }
    }

    public static final void setup(GameObjectList<LoadingFaction> factions, GameObjectList<LoadingSection> sections) {
        for (LoadingFaction f : factions) {
            for (LoadingSection s : sections) {
                if (s.getBelongedFactionId() == f.getId()) {
                    f.sectionIds.add(s.getId());
                }
            }
        }
    }

    public Set<Integer> getSectionIds() {
        return Collections.unmodifiableSet(sectionIds);
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameScenario getScenario() {
        return scenario;
    }
}
