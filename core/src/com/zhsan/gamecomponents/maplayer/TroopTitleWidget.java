package com.zhsan.gamecomponents.maplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.gameobject.Troop;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 27/8/2015.
 */
public class TroopTitleWidget extends WidgetGroup {

    private static class Setting {
        public static final String RES_PATH = Paths.RESOURCES + "TroopTitle" + File.separator;
        public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

        private Texture background, faction;
        private Rectangle backgroundPos, factionPos;

        private TextWidget<Void> nameText;

        private Texture done, undone, autoDone, autoUndone;
        private Rectangle donePos;

        private Texture foodShortage;
        private Rectangle foodPos;

        private Texture noControl;
        private Rectangle noControlPos;

        private Texture stunt;
        private Rectangle stuntPos;

        private TextWidget<Void> troopText;

        private Texture morale, moraleBackground;
        private Rectangle moralePos, moraleBackgroundPos;

        public Setting() {
            loadXml();
        }

        private void loadXml() {
            FileHandle f = Gdx.files.external(RES_PATH + "TroopTitleData.xml");

            Document dom;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                dom = db.parse(f.read());

                Node bgNode = dom.getElementsByTagName("BackgroundFile").item(0);
                background = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(bgNode, "FileName")));
                backgroundPos = XmlHelper.loadRectangleFromXml(bgNode);

                Node factionNode = dom.getElementsByTagName("FactionFile").item(0);
                faction = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(factionNode, "FileName")));
                factionPos = XmlHelper.loadRectangleFromXml(factionNode);

                nameText = new TextWidget<>(TextWidget.Setting.fromXml(dom.getElementsByTagName("NameText").item(0)));

                Node actionNode = dom.getElementsByTagName("ActionIcon").item(0);
                done = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(actionNode, "Done")));
                undone = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(actionNode, "Undone")));
                autoDone = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(actionNode, "AutoDone")));
                autoUndone = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(actionNode, "AutoUndone")));
                donePos = XmlHelper.loadRectangleFromXml(actionNode);

                Node foodNode = dom.getElementsByTagName("FoodIcon").item(0);
                foodShortage = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(foodNode, "Shortage")));
                foodPos = XmlHelper.loadRectangleFromXml(foodNode);

                Node controlNode = dom.getElementsByTagName("Control").item(0);
                noControl = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(controlNode, "NoControl")));
                noControlPos = XmlHelper.loadRectangleFromXml(controlNode);

                Node stuntNode = dom.getElementsByTagName("Stunt").item(0);
                stunt = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(stuntNode, "Stunt")));
                stuntPos = XmlHelper.loadRectangleFromXml(stuntNode);

                troopText = new TextWidget<>(TextWidget.Setting.fromXml(dom.getElementsByTagName("TroopText").item(0)));

                Node moraleNode = dom.getElementsByTagName("Morale").item(0);
                morale = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(moraleNode, "FileName")));
                moralePos = XmlHelper.loadRectangleFromXml(moraleNode);

                Node moraleBgNode = dom.getElementsByTagName("MoraleBackground").item(0);
                moraleBackground = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(moraleBgNode, "FileName")));
                moraleBackgroundPos = XmlHelper.loadRectangleFromXml(moraleBgNode);

            } catch (Exception e) {
                throw new FileReadException(RES_PATH + "TroopTitleData.xml", e);
            }
        }

        public void dispose() {
            background.dispose();
            faction.dispose();
            done.dispose();
            undone.dispose();
            autoDone.dispose();
            autoUndone.dispose();
            foodShortage.dispose();
            noControl.dispose();
            stunt.dispose();
            morale.dispose();
            moraleBackground.dispose();
            nameText.dispose();
            troopText.dispose();
        }
    }

    private Troop troop;

    private static Setting setting;

    private TextWidget<Void> nameText, troopText;

    public TroopTitleWidget(Troop t) {
        if (setting == null) {
            setting = new Setting();
        }

        nameText = new TextWidget<>(setting.nameText);
        troopText = new TextWidget<>(setting.troopText);

        addActor(nameText);
        addActor(troopText);
    }

    public void draw(Batch batch, float parentAlpha) {
        batch.draw(setting.background, setting.backgroundPos.x + getX(), setting.backgroundPos.y + getY(),
                setting.backgroundPos.width, setting.backgroundPos.height);
        batch.draw(setting.faction, setting.factionPos.x + getX(), setting.factionPos.y + getY(),
                setting.factionPos.width, setting.factionPos.height);

        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        removeActor(nameText);
        removeActor(troopText);
        nameText.dispose();
        troopText.dispose();
    }

    public static void disposeAll() {
        setting.dispose();
    }

}
