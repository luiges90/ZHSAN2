package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.zhsan.common.Paths;
import com.zhsan.common.exception.FileReadException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 6/3/2015.
 */
public class GlobalStrings {

    public enum Keys {
        TITLE("title"),
        GAME_SURVEY_SAVE_HEADER("gameSurveySaveHeader"),
        TERRAIN_DETAIL_SAVE_HEADER("terrainDetailSaveHeader"),
        MAP_SAVE_HEADER("mapSaveHeader"),
        ARCHITECTURE_KIND_SAVE_HEADER("architectureKindSaveHeader"),
        ARCHITECTURE_SAVE_HEADER("architectureSaveHeader"),
        SECTION_SAVE_HEADER("sectionSaveHeader"),
        FACTION_SAVE_HEADER("factionSaveHeader"),
        GAME_DATA_SAVE_HEADER("gameDataSaveHeader"),
        PERSON_SAVE_HEADER("personSaveHeader"),
        FACILITY_KIND_SAVE_HEADER("facilityKindSaveHeader"),
        FACILITY_SAVE_HEADER("facilitySaveHeader"),
        NO_CONTENT("noContent"),
        DAY("day"),
        TICK("tick"),
        UP_ARROW("upArrow"),
        FOOD_UNIT("foodUnit"),
        FOOD_UNIT_STRING("foodUnitString"),
        AGRICULTURE("Agriculture"),
        COMMERCE("Commerce"),
        TECHNOLOGY("Technology"),
        ARCHITECTURE_MORALE("ArchitectureMorale"),
        ARCHITECTURE_ENDURANCE("ArchitectureEndurance"),
        MAYOR("Mayor"),
        MILITARY_TYPE_SAVE_HEADER("militaryTypeSaveHeader"),
        MILITARY_KIND_SAVE_HEADER("militaryKindSaveHeader"),
        MILITARY_SAVE_HEADER("militarySaveHeader"),
        MILITARY_TERRAIN_SAVE_HEADER("militaryTerrainSaveHeader"),
        NEW_MILITARY("NewMilitary"),
        RECRUIT_MILITARY("RecruitMilitary"),
        TRAIN_MILITARY("TrainMilitary"),
        MILITARY_RECRUIT("MilitaryRecruit"),
        MILITARY_TRAINING("MilitaryTraining"),
        ASSIGN_LEADER("AssignLeader"),
        ASSIGN_MILITARY_PERSON("AssignMilitaryPerson"),
        TROOP_ANIMATION_SAVE_HEADER("troopAnimationSaveHeader"),
        TROOP_SAVE_HEADER("troopSaveHeader"),
        CAMPAIGN("Campaign"),
        MOVE_TO("MoveTo"),
        ATTACK_POINT("AttackPoint"),
        ATTACK_TARGET("AttackTarget"),
        TROOP_NAME("TroopName"),
        MILITARY_QUANTITY_SHORT("MilitaryQuantityShort"),
        MILITARY_MORALE_SHORT("MilitaryMoraleShort"),
        MILITARY_COMBATIVITY_SHORT("MilitaryCombativityShort"),
        MOVE_TO_ENTER("MoveToEnter"),
        RECALL_PERSON("RecallPerson"),
        MOVE_PERSON("MovePerson"),
        SELECT_DESTINATION("SelectDestination"),
    	FAMILY_SAVE_HEADER("FamilySaveHeader");

        private final String xmlName;
        Keys(String name) {
            this.xmlName = name;
        }
    }

    private static Keys getKeyFromXml(String s) {
        for (Keys k : Keys.values()) {
            if (k.xmlName.equals(s)) {
                return k;
            }
        }
        throw new IllegalArgumentException("Key " + s + " not found.");
    }

    private GlobalStrings() {}

    private static EnumMap<Keys, String> strings = null;

    private static void load() {
        FileHandle f = Gdx.files.external(Paths.RESOURCES + "GlobalStrings.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        strings = new EnumMap<>(Keys.class);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            NodeList list = dom.getElementsByTagName("GlobalStrings");
            NamedNodeMap attributes = list.item(0).getAttributes();
            for (int i = 0; i < attributes.getLength(); ++i) {
                strings.put(getKeyFromXml(attributes.item(i).getNodeName()), attributes.item(i).getNodeValue());
            }
        } catch (Exception e) {
            throw new FileReadException(Paths.RESOURCES + "GlobalStrings.xml", e);
        }
    }

    public static String getString(Keys key) {
        if (strings == null) load();
        return strings.get(key);
    }

}
