package com.zhsan.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.opencsv.CSVReader;
import com.zhsan.common.Utility;
import com.zhsan.common.exception.EmptyFileException;
import com.zhsan.common.exception.FileReadException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 14/3/2015.
 */
public class Faction {

    private int id;
    // private Color color;
    private String name;
    private int techniquePoint;
    private int techniquePointReservedForTechnique;
    private int techniquePointReservedForFacility;
    private int reputation;
    // private List<MilitaryKind> basicMilitaryKinds;
    // private Technique upgradingTechnique;
    // private int upgradeTechniqueRemainingTime;
    // private List<Technique> techniques;
    private List<Integer> preferredUpgradeTechniqueKinds;
    // private Technique planTechnique;
    private boolean autoRefuseReleaseCaptive;
    private int emperorMerit;
    // private EmperorRank emperorRank;
    private boolean alien;
    private boolean playerSelectable;

    public static final List<Faction> fromCSV(String path, @Nullable GameScenario scen) {
        List<Faction> result = new ArrayList<>();

        FileHandle f = Gdx.files.external(path + File.separator + "Faction.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(f.read()))) {
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                index++;
                if (index == 1) continue; // skip first line.

                Faction t = new Faction();
                t.id = Integer.parseInt(line[0]);
                // t.king = line[1];
                // t.color = line[2];
                t.name = line[3];
                // t.capital = line[4];
                t.techniquePoint = Integer.parseInt(line[5]);
                t.techniquePointReservedForTechnique = Integer.parseInt(line[6]);
                t.techniquePointReservedForFacility = Integer.parseInt(line[7]);
                t.reputation = Integer.parseInt(line[8]);
                // t.sections = line[9];
                // t.informations = line[10];
                // t.architectures = line[11];
                // t.troops = line[12];
                // t.routeways = line[13];
                // t.legions = line[14];
                // t.basicMilitaryKinds = line[15];
                // t.upgradingTechnique = line[16];
                // t.upgradeTechniqueRemainingTime = line[17];
                // t.techniques = line[18];
                t.preferredUpgradeTechniqueKinds = Utility.integerListFromXml(line[19]);
                // t.planTechnique = line[20];
                t.autoRefuseReleaseCaptive = Boolean.parseBoolean(line[21]);
                t.emperorMerit = Integer.parseInt(line[22]);
                // t.emperorRank = line[23];
                t.alien = Boolean.parseBoolean(line[24]);
                t.playerSelectable = Boolean.parseBoolean(line[25]);

                result.add(t);
            }

            return result;
        } catch (IOException e) {
            throw new FileReadException(path + File.separator + "Faction.csv", e);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
