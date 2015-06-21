package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVWriter;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.resources.GlobalStrings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 24/5/2015.
 */
public class Architecture extends GameObject {

    public static final String SAVE_FILE = "Architecture.csv";

    private GameScenario scenario;

    private String name;
    private String nameImageName;

    private List<Point> location;

    private ArchitectureKind architectureKind;
    private Section belongedSection;

    private int population;
    private int fund, food;
    private float agriculture, commerce, technology, endurance, morale;

    public Architecture(LoadingArchitecture from, GameScenario scenario) {
        super(from.getId());
        this.scenario = scenario;

        this.name = from.getName();
        this.nameImageName = from.getNameImageName();
        this.location = from.getLocation();
        this.architectureKind = scenario.getArchitectureKinds().get(from.getArchitectureKindId());
        this.belongedSection = scenario.getSections().get(from.getBelongedSectionId());

        this.population = from.getPopulation();
        this.fund = from.getFund();
        this.food = from.getFood();
        this.agriculture = from.getAgriculture();
        this.commerce = from.getCommerce();
        this.technology = from.getTechnology();
        this.endurance = from.getEndurance();
        this.morale = from.getMorale();
    }

    public static final void toCSV(FileHandle root, GameObjectList<Architecture> data) {
        FileHandle f = root.child(SAVE_FILE);
        try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
            writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_SAVE_HEADER).split(","));
            for (Architecture d : data) {
                writer.writeNext(new String[]{
                        String.valueOf(d.getId()),
                        d.nameImageName,
                        d.getName(),
                        String.valueOf(d.architectureKind.getId()),
                        Point.toCSVList(d.location),
                        String.valueOf(d.belongedSection.getId()),
                        String.valueOf(d.population),
                        String.valueOf(d.fund),
                        String.valueOf(d.food),
                        String.valueOf(d.agriculture),
                        String.valueOf(d.commerce),
                        String.valueOf(d.technology),
                        String.valueOf(d.endurance),
                        String.valueOf(d.morale),
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

    public String getNameImageName() {
        return nameImageName;
    }

    public List<Point> getLocation() {
        return new ArrayList<>(location);
    }

    public ArchitectureKind getKind() {
        return architectureKind;
    }

    public Faction getBelongedFaction() {
        return belongedSection == null ? null : belongedSection.getBelongedFaction();
    }

    public GameObjectList<Person> getPersons() {
        return scenario.getPersons().filter(p -> p.getLocation() == this && p.getState() == Person.State.NORMAL);
    }

    public GameObjectList<Person> getUnhiredPersons() {
        return scenario.getPersons().filter(p -> p.getLocation() == this && p.getState() == Person.State.UNHIRED);
    }

    public boolean hasFaction() {
        return getBelongedFaction() != null;
    }

    public GameObjectList<Facility> getFacilities() {
        return scenario.getFacilities().filter(f -> f.getBelongedArchitecture() == this);
    }

    public String getFactionName() {
        return this.getBelongedFaction() == null ? GlobalStrings.getString(GlobalStrings.Keys.NO_CONTENT) :
                this.getBelongedFaction().getName();
    }

    public String getArchitectureKindName() {
        return this.getKind().getName();
    }

    public int getPopulation() {
        return population;
    }

    public int getFund() {
        return fund;
    }

    public int getFood() {
        return food;
    }

    public String getFoodString() {
        return food / Integer.parseInt(GlobalStrings.getString(GlobalStrings.Keys.FOOD_UNIT)) +
                GlobalStrings.getString(GlobalStrings.Keys.FOOD_UNIT_STRING);
    }

    public float getAgriculture() {
        return agriculture;
    }

    public float getCommerce() {
        return commerce;
    }

    public float getTechnology() {
        return technology;
    }

    public float getEndurance() {
        return endurance;
    }

    public float getMorale() {
        return morale;
    }

    public GameObjectList<Person> getWorkingPersons(Person.DoingWork doingWork) {
        return this.getPersons().filter(person -> person.getDoingWork() == doingWork);
    }

    public void advanceDay() {

    }

}
