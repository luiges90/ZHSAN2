package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.Point;
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
 * Created by Peter on 22/4/2015.
 */
public class Section extends GameObject {

    public static final String SAVE_FILE = "Section.csv";

    private GameScenario scenario;

    private String name;

    private Set<Integer> architectureIds = new HashSet<>();
    private int belongedFactionId = -1;

    public Section(int id) {
        super(id);
    }

    public static final GameObjectList<Section> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        int version = scen.getGameSurvey().getVersion();

        GameObjectList<Section> result = new GameObjectList<>();

        FileHandle f = root.child(SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Section data = new Section(Integer.parseInt(line[0]));
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

    public static final void toCSV(FileHandle root, GameObjectList<Section> data) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.SECTION_SAVE_HEADER).split(","));
            for (Section d : data) {
                writer.writeNext(new String[]{
                        String.valueOf(d.getId()),
                        d.getName(),
                        String.valueOf(d.belongedFactionId)
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

    public static final void setup(GameScenario scenario) {
        for (Section s : scenario.getSections()) {
            for (Architecture a : scenario.getArchitectures()) {
                if (a.getBelongedSectionId() == s.getId()) {
                    s.architectureIds.add(a.getId());
                }
            }

            for (Faction f : scenario.getFactions()) {
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

    public Set<Integer> getArchitectureIds() {
        return Collections.unmodifiableSet(architectureIds);
    }

    public int getBelongedFactionId() {
        return belongedFactionId;
    }

    public Faction getBelongedFaction() {
        return scenario.getFactions().get(belongedFactionId);
    }
}
