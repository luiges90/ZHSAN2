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

    public Architecture(LoadingArchitecture from, GameScenario scenario) {
        super(from.getId());
        this.scenario = scenario;

        this.name = from.getName();
        this.nameImageName = from.getNameImageName();
        this.location = from.getLocation();
        this.architectureKind = scenario.getArchitectureKinds().get(from.getArchitectureKindId());
        this.belongedSection = scenario.getSections().get(from.getBelongedSectionId());
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
                        String.valueOf(d.belongedSection.getId())
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



}
