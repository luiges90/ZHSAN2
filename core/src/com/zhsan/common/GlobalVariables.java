package com.zhsan.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Peter on 22/3/2015.
 */
public final class GlobalVariables {

    private GlobalVariables() {}

    public static float scrollSpeed = 1.0f;
    public static boolean showGrid = true;

    public static int maxRunningDays = 99;
    public static Color blankColor = Color.WHITE;

    public static float diminishingGrowthMaxFactor = 1.5f;
    public static float diminishingGrowthPower = 2.0f;

    public static float internalPersonDiminishingFactor = 0.8f;
    public static float internalGrowthFactor = 0.01f;
    public static float mayorInternalWorkEfficiency = 0.3f;
    public static float internalDrop = 0.75f;
    public static int internalCost = 20;

    public static float gainFund = 1.0f;
    public static float gainFood = 60.0f;
    public static float gainFundPerPopulation = 0.003f;
    public static float gainFoodPerPopulation = 0.003f;

    public static float recruitCostFactor = 0.1f;
    public static float recruitEfficiency = 0.6f;
    public static float recruitByLeaderEfficiency = 1.0f;
    public static float trainEfficiency = 0.2f;
    public static float trainByLeaderEfficiency = 0.3f;
    public static float moraleTrainFactor = 0.5f;

    public static int maxMorale = 100;
    public static int maxCombativity = 100;
    public static int recruitMorale = 50;
    public static int recruitCombativity = 50;

    public static int architectureMinCommand = 20;
    public static float architectureDefenseEndurancePower = 0.5f;
    public static float architectureDefenseMoralePower = 0.5f;

    public static float baseDamage = 500.0f;
    public static float baseArchitectureDamage = 5.0f;
    public static float reactDamageFactor = 0.5f;

    public static long aiTimeout = 20;

    public static float personMovingSpeed = 8.0f;

    public static int damageShowTime = 60;

    public static float troopCommandPersonFactor = 0.2f;
    public static float troopStrengthPersonFactor = 0.5f;
    public static float troopIntelligencePersonFactor = 1.0f;

    public static int maxPathLengthAsConnected = 120;
    public static int leastDistanceFromArchitecturesAsConnected = 8;

    public static void load() {
        FileHandle f = Gdx.files.external(Paths.DATA + "GlobalVariables.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node node = dom.getElementsByTagName("GlobalVariables").item(0);
            scrollSpeed = Float.parseFloat(XmlHelper.loadAttribute(node, "scrollSpeed"));
            showGrid = Boolean.parseBoolean(XmlHelper.loadAttribute(node, "showGrid"));
            maxRunningDays = Integer.parseInt(XmlHelper.loadAttribute(node, "maxRunningDays"));
            blankColor = XmlHelper.loadColorFromXml(Integer.parseUnsignedInt(
                    XmlHelper.loadAttribute(node, "blankColor")
            ));
            diminishingGrowthMaxFactor = Float.parseFloat(XmlHelper.loadAttribute(node, "diminishingGrowthMaxFactor"));
            diminishingGrowthPower = Float.parseFloat(XmlHelper.loadAttribute(node, "diminishingGrowthPower"));
            internalPersonDiminishingFactor = Float.parseFloat(XmlHelper.loadAttribute(node, "internalPersonDiminishingFactor"));
            internalGrowthFactor = Float.parseFloat(XmlHelper.loadAttribute(node, "internalGrowthFactor"));
            mayorInternalWorkEfficiency = Float.parseFloat(XmlHelper.loadAttribute(node, "mayorInternalWorkEfficiency"));
            internalDrop = Float.parseFloat(XmlHelper.loadAttribute(node, "internalDrop"));
            gainFund = Float.parseFloat(XmlHelper.loadAttribute(node, "gainFund"));
            gainFood = Float.parseFloat(XmlHelper.loadAttribute(node, "gainFood"));
            gainFundPerPopulation = Float.parseFloat(XmlHelper.loadAttribute(node, "gainFundPerPopulation"));
            gainFoodPerPopulation = Float.parseFloat(XmlHelper.loadAttribute(node, "gainFoodPerPopulation"));
            internalCost = Integer.parseInt(XmlHelper.loadAttribute(node, "internalCost"));
            aiTimeout = Long.parseLong(XmlHelper.loadAttribute(node, "aiTimeout"));
            recruitCostFactor = Float.parseFloat(XmlHelper.loadAttribute(node, "recruitCostFactor"));
            recruitEfficiency = Float.parseFloat(XmlHelper.loadAttribute(node, "recruitEfficiency"));
            recruitByLeaderEfficiency = Float.parseFloat(XmlHelper.loadAttribute(node, "recruitByLeaderEfficiency"));
            trainEfficiency = Float.parseFloat(XmlHelper.loadAttribute(node, "trainEfficiency"));
            trainByLeaderEfficiency = Float.parseFloat(XmlHelper.loadAttribute(node, "trainByLeaderEfficiency"));
            moraleTrainFactor = Float.parseFloat(XmlHelper.loadAttribute(node, "moraleTrainFactor"));
            maxMorale = Integer.parseInt(XmlHelper.loadAttribute(node, "maxMorale"));
            maxCombativity = Integer.parseInt(XmlHelper.loadAttribute(node, "maxCombativity"));
            recruitMorale = Integer.parseInt(XmlHelper.loadAttribute(node, "recruitMorale"));
            recruitCombativity = Integer.parseInt(XmlHelper.loadAttribute(node, "recruitCombativity"));
            architectureMinCommand = Integer.parseInt(XmlHelper.loadAttribute(node, "architectureMinCommand"));
            architectureDefenseEndurancePower = Float.parseFloat(XmlHelper.loadAttribute(node, "architectureDefenseEndurancePower"));
            architectureDefenseMoralePower = Float.parseFloat(XmlHelper.loadAttribute(node, "architectureDefenseMoralePower"));
            baseDamage = Float.parseFloat(XmlHelper.loadAttribute(node, "baseDamage"));
            baseArchitectureDamage = Float.parseFloat(XmlHelper.loadAttribute(node, "baseArchitectureDamage"));
            reactDamageFactor = Float.parseFloat(XmlHelper.loadAttribute(node, "reactDamageFactor"));
            personMovingSpeed = Float.parseFloat(XmlHelper.loadAttribute(node, "personMovingSpeed"));
            damageShowTime = Integer.parseInt(XmlHelper.loadAttribute(node, "damageShowTime"));
            troopCommandPersonFactor = Float.parseFloat(XmlHelper.loadAttribute(node, "troopCommandPersonFactor"));
            troopStrengthPersonFactor = Float.parseFloat(XmlHelper.loadAttribute(node, "troopStrengthPersonFactor"));
            troopIntelligencePersonFactor = Float.parseFloat(XmlHelper.loadAttribute(node, "troopIntelligencePersonFactor"));
            maxPathLengthAsConnected = Integer.parseInt(XmlHelper.loadAttribute(node, "maxPathLengthAsConnected"));
            leastDistanceFromArchitecturesAsConnected = Integer.parseInt(XmlHelper.loadAttribute(node, "leastDistanceFromArchitecturesAsConnected"));
        } catch (Exception e) {
            throw new FileReadException(Paths.DATA + "GlobalVariables.xml", e);
        }
    }


}
