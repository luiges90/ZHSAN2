package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamelogic.FactionOrder;
import com.zhsan.resources.GlobalStrings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 24/5/2015.
 */
public class Faction extends GameObject {

    public static final String SAVE_FILE = "Faction.csv";

    private String name;

    private GameScenario scenario;
    private List<FactionOrder> orders = new ArrayList<>();

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
                        XmlHelper.saveColorToXml(d.color)
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

    public Color getColor() {
        return color;
    }

    public void addOrder(FactionOrder order) {
        orders.add(order);
    }

    public List<FactionOrder> getOrders() {
        return new ArrayList<>(orders);
    }

    public void clearOrders() {
        orders.clear();
    }

    public GameObjectList<Person> getPersons() {
        return scenario.getPersons().filter(p -> p.getBelongedFaction() == this);
    }

    public GameObjectList<Architecture> getArchitectures() {
        return scenario.getArchitectures().filter(a -> a.getBelongedFaction() == this);
    }

}
