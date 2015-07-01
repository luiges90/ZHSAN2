package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;

import java.io.IOException;

/**
 * Created by Peter on 24/5/2015.
 */
public class Section extends GameObject {

    public static final String SAVE_FILE = "Section.csv";

    private GameScenario scenario;

    private String name;

    private Faction belongedFaction;

    public Section(LoadingSection from, GameScenario scenario) {
        super(from.getId());
        this.scenario = scenario;

        this.name = from.getName();
        this.belongedFaction = scenario.getFactions().get(from.getBelongedFactionId());
    }

    public static final void toCSV(FileHandle root, GameObjectList<Section> data) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.SECTION_SAVE_HEADER).split(","));
            for (Section d : data) {
                writer.writeNext(new String[]{
                        String.valueOf(d.getId()),
                        d.getName(),
                        String.valueOf(d.belongedFaction.getId())
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

    @Override
    public String getName() {
        return name;
    }

    public Faction getBelongedFaction() {
        return belongedFaction;
    }
}
