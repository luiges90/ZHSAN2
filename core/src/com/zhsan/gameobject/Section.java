package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.lua.LuaAI;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * Created by Peter on 24/5/2015.
 */
public class Section implements GameObject {

    public static final String SAVE_FILE = "Section.csv";

    private GameScenario scenario;

    private String name;

    private Faction belongedFaction;

    private final int id;
    private String aiTags;

    @Override
    @LuaAI.ExportToLua
    public int getId() {
        return id;
    }

    @Override
    @LuaAI.ExportToLua
    public String getAiTags() {
        return aiTags;
    }

    @Override
    @LuaAI.ExportToLua
    public GameObject setAiTags(String aiTags) {
        this.aiTags = aiTags;
        return this;
    }

    private Section(int id, GameScenario scen) {
        this.id = id;
        this.scenario = scen;
    }

    public static final GameObjectList<Section> fromCSV(FileHandle root, @NotNull GameScenario scen) {
        GameObjectList<Section> result = new GameObjectList<>();

        FileHandle f = root.child(Section.SAVE_FILE);
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Section data = new Section(Integer.parseInt(line[0]), scen);
                data.setAiTags(line[1]);
                data.name = line[2];
                data.belongedFaction = scen.getFactions().get(Integer.parseInt(line[3]));

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
                        d.getAiTags(),
                        d.getName(),
                        String.valueOf(d.belongedFaction.getId())
                });
            }
        } catch (IOException e) {
            throw new FileWriteException(f.path(), e);
        }

    }

    @Override
    @LuaAI.ExportToLua
    public String getName() {
        return name;
    }

    public Faction getBelongedFaction() {
        return belongedFaction;
    }

    @LuaAI.ExportToLua
    public GameObjectList<Architecture> getArchitectures() {
        return scenario.getArchitectures().filter(a -> a.getBelongedSection() == this);
    }

    @LuaAI.ExportToLua
    public GameObjectList<Troop> getTroops() {
        return scenario.getTroops().filter(t -> t.getBelongedSection() == this);
    }

}
