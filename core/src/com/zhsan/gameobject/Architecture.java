package com.zhsan.gameobject;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVWriter;
import com.zhsan.common.GlobalVariables;
import com.zhsan.common.Point;
import com.zhsan.common.Utility;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.resources.GlobalStrings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public GameObjectList<Person> getPersonsExcludingMayor() {
        return scenario.getPersons().filter(p -> p.getLocation() == this && p.getState() == Person.State.NORMAL && p.getDoingWork() != Person.DoingWork.MAYOR);
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

    Person pickMayor() {
        return this.getPersons().max((p, q) -> Integer.compare(p.getAbilitySum(), q.getAbilitySum()), null);
    }

    GameObjectList<Person> getMayorUnchecked() {
        return this.getPersons().filter(person -> person.getDoingWork() == Person.DoingWork.MAYOR);
    }

    public Person getMayor() {
        if (this.getPersons().size() == 0) return null;
        GameObjectList<Person> p = this.getPersons().filter(person -> person.getDoingWork() == Person.DoingWork.MAYOR);
        if (p.size() != 1) {
            throw new IllegalStateException("There should be one and only one mayor in every architecture");
        }
        return p.getFirst();
    }

    public void changeMayor(Person newMayor) {
        if (newMayor.getLocation() != this) {
            throw new IllegalStateException("The new mayor must be in the architecture");
        }
        newMayor.setDoingWork(Person.DoingWork.MAYOR);
    }

    public void addMayor(Person newMayor) {
        if (getMayorUnchecked().size() > 0) {
            throw new IllegalStateException("addMayor may only be used when there is no mayor at all");
        }
        if (newMayor.getLocation() != this) {
            throw new IllegalStateException("The new mayor must be in the architecture");
        }
        newMayor.setDoingWorkUnchecked(Person.DoingWork.MAYOR);
    }

    public GameObjectList<Person> getWorkingPersons(Person.DoingWork doingWork) {
        return this.getPersons().filter(person -> person.getDoingWork() == doingWork);
    }

    public void advanceDay() {
        developInternal();
    }

    private void developInternal() {
        Person mayor = this.getMayor();

        float agricultureAbility = (mayor == null ? 0 : mayor.getAgricultureAbility()) * GlobalVariables.mayorInternalWorkEfficiency +
                getWorkingPersons(Person.DoingWork.AGRICULTURE).getAll().stream()
                .map(p -> (float) p.getAgricultureAbility()).collect(Utility.diminishingSum(GlobalVariables.internalPersonDiminishingFactor));
        this.agriculture = Utility.diminishingGrowth(
                this.agriculture, agricultureAbility * GlobalVariables.internalGrowthFactor, this.getKind().getAgriculture());

        float commerceAbility = (mayor == null ? 0 : mayor.getCommerceAbility()) * GlobalVariables.mayorInternalWorkEfficiency +
                getWorkingPersons(Person.DoingWork.COMMERCE).getAll().stream()
                        .map(p -> (float) p.getCommerceAbility()).collect(Utility.diminishingSum(GlobalVariables.internalPersonDiminishingFactor));
        this.commerce = Utility.diminishingGrowth(
                this.commerce, commerceAbility * GlobalVariables.internalGrowthFactor, this.getKind().getCommerce());

        float technologyAbility = (mayor == null ? 0 : mayor.getTechnologyAbility()) * GlobalVariables.mayorInternalWorkEfficiency +
                getWorkingPersons(Person.DoingWork.TECHNOLOGY).getAll().stream()
                        .map(p -> (float) p.getTechnologyAbility()).collect(Utility.diminishingSum(GlobalVariables.internalPersonDiminishingFactor));
        this.technology = Utility.diminishingGrowth(
                this.technology, technologyAbility * GlobalVariables.internalGrowthFactor, this.getKind().getTechnology());

        float moraleAbility = (mayor == null ? 0 : mayor.getMoraleAbility()) * GlobalVariables.mayorInternalWorkEfficiency +
                getWorkingPersons(Person.DoingWork.MORALE).getAll().stream()
                        .map(p -> (float) p.getMoraleAbility()).collect(Utility.diminishingSum(GlobalVariables.internalPersonDiminishingFactor));
        this.morale = Utility.diminishingGrowth(
                this.morale, moraleAbility * GlobalVariables.internalGrowthFactor, this.getKind().getMorale());

        float enduranceAbility = (mayor == null ? 0 : mayor.getEnduranceAbility()) * GlobalVariables.mayorInternalWorkEfficiency +
                getWorkingPersons(Person.DoingWork.ENDURANCE).getAll().stream()
                        .map(p -> (float) p.getEnduranceAbility()).collect(Utility.diminishingSum(GlobalVariables.internalPersonDiminishingFactor));
        this.endurance = Utility.diminishingGrowth(
                this.endurance, enduranceAbility * GlobalVariables.internalGrowthFactor, this.getKind().getEndurance());
    }

}
