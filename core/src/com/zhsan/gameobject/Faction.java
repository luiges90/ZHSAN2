package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.GlobalStrings;
import com.zhsan.lua.LuaAI;

import java.io.IOException;
import java.util.concurrent.RecursiveAction;

/**
 * Created by Peter on 24/5/2015.
 */
public class Faction extends GameObject {

    public static final String SAVE_FILE = "Faction.csv";

    private String name;
    private Person leader;

    private GameScenario scenario;

    private Color color;

    public Faction(LoadingFaction from, GameScenario scenario) {
        super(from.getId());
        this.scenario = scenario;

        this.name = from.getName();
        this.color = from.getColor();
    }

    public static final void toCSV(FileHandle root, GameObjectList<Faction> data) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.FACTION_SAVE_HEADER).split(","));
            for (Faction d : data) {
                writer.writeNext(new String[]{
                        String.valueOf(d.getId()),
                        d.getName(),
                        XmlHelper.saveColorToXml(d.color),
                        String.valueOf(d.leader.getId())
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

    public Color getColor() {
        return color;
    }

    public void setLeader(Person p) {
        if (!this.getPersons().contains(p) || (p.getState() != Person.State.NORMAL && p.getState() != Person.State.CAPTIVE)) {
            throw new IllegalArgumentException("The leader must be in this faction");
        }
        this.leader = p;
    }

    void setLeaderUnchecked(Person p) {
        this.leader = p;
    }

    public GameObjectList<Person> getPersons() {
        return scenario.getPersons().filter(p -> p.getBelongedFaction() == this);
    }

    public GameObjectList<Section> getSections() {
        return scenario.getSections().filter(s -> s.getBelongedFaction() == this);
    }

    public GameObjectList<Architecture> getArchitectures() {
        return scenario.getArchitectures().filter(a -> a.getBelongedFaction() == this);
    }

    Person pickLeader() {
        return this.getPersons().max((p, q) -> Integer.compare(p.getAbilitySum(), q.getAbilitySum()));
    }

    Person getLeaderUnchecked() {
        return leader;
    }

    public Person getLeader() {
        if (leader == null) {
            throw new IllegalStateException("Every faction must have a leader");
        }
        return leader;
    }

    public void ai() {
        System.out.println("Running AI of Faction " + this.getId() + " on thread " + Thread.currentThread().getName() + " on date " + scenario.getGameDate());
        LuaAI.runFactionAi(this);
    }


}
