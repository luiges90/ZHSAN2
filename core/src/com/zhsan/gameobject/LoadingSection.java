package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.XmlHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Peter on 22/4/2015.
 */
class LoadingSection extends GameObject {

    private GameScenario scenario;

    private String name;

    private Set<Integer> architectureIds = new HashSet<>();
    private int belongedFactionId = -1;

    public LoadingSection(int id) {
        super(id);
    }

    public static final GameObjectList<LoadingSection> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        int version = scen.getGameSurvey().getVersion();

        GameObjectList<LoadingSection> result = new GameObjectList<>();

        FileHandle f = root.child(Section.SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                LoadingSection data = new LoadingSection(Integer.parseInt(line[0]));
                data.setName(line[1]);
                if (version == 1) {
                    data.architectureIds = new HashSet<>(XmlHelper.loadIntegerListFromXml(line[7]));
                } else {
                    data.belongedFactionId = Integer.parseInt(line[2]);
                }

                data.scenario = scen;
                result.add(data);
            }
        } catch (IOException e) {
            throw new FileReadException(f.path(), e);
        }

        return result;
    }

    public static final void setup(GameObjectList<LoadingSection> sections,
                                   GameObjectList<LoadingArchitecture> architectures,
                                   GameObjectList<LoadingFaction> factions) {
        for (LoadingSection s : sections) {
            for (LoadingArchitecture a : architectures) {
                if (a.getBelongedSectionId() == s.getId()) {
                    s.architectureIds.add(a.getId());
                }
            }

            for (LoadingFaction f : factions) {
                if (f.getSectionIds().contains(s.getId())) {
                    s.belongedFactionId = f.getId();
                }
            }
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public GameScenario getScenario() {
        return scenario;
    }

    public Set<Integer> getArchitectureIds() {
        return Collections.unmodifiableSet(architectureIds);
    }

    public int getBelongedFactionId() {
        return belongedFactionId;
    }

}
