package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.zhsan.common.Utility;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.TextWidget;
import com.zhsan.gameobject.GameScenario;
import com.zhsan.gameobject.GameSurvey;
import com.zhsan.resources.GlobalStrings;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

/**
 * Created by Peter on 8/3/2015.
 */
public class NewGameFrame extends GameFrame {

    public static final String RES_PATH = GameFrame.RES_PATH + "New" + File.separator;

    private int margins;
    private int listPaddings;
    private Color listSelectedColor;

    private ScrollPane scenarioPane, scenarioDescriptionPane, factionPane;
    private TextWidget.Setting scenarioElement, scenarioDescriptionStyle, factionStyle;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "NewGameFrameData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            margins = Integer.parseInt(dom.getElementsByTagName("Margins").item(0).getAttributes()
                    .getNamedItem("value").getNodeValue());
            listPaddings = Integer.parseInt(dom.getElementsByTagName("Lists").item(0).getAttributes()
                    .getNamedItem("padding").getNodeValue());
            listSelectedColor = Utility.readColorFromXml(
                    Integer.parseUnsignedInt(dom.getElementsByTagName("Lists").item(0).getAttributes()
                    .getNamedItem("selectedColor").getNodeValue())
            );
            scenarioElement = TextWidget.Setting.fromXml(dom.getElementsByTagName("ScenarioList").item(0));
            scenarioDescriptionStyle = TextWidget.Setting.fromXml(dom.getElementsByTagName("ScenarioDescription").item(0));
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "NewGameFrameData.xml", e);
        }
    }

    public NewGameFrame(float width, float height) {
        super(width, height, GlobalStrings.getString(GlobalStrings.NEW_GAME), new GameFrame.OnClick() {
            @Override
            public void onOkClicked() {

            }

            @Override
            public void onCancelClicked() {

            }
        });

        loadXml();

        initScenarioListPane();
        // initScenarioDescriptionPane();
    }

    private void initScenarioListPane() {
        float paneHeight = (getTopBound() - getBottomBound() - margins * 3) / 2;
        float paneWidth = (getRightBound() - getLeftBound() - margins * 3) / 2;

        List<GameSurvey> surveys = GameScenario.loadAllGameSurveys();

        Table scenarioList = new Table();
        for (GameSurvey i : surveys) {
            TextWidget widget = new TextWidget(scenarioElement, i.title);
            widget.setTouchable(Touchable.enabled);
            widget.setSelectedOutlineColor(listSelectedColor);
            scenarioList.add(widget).size(paneWidth, widget.computeNeededHeight(paneWidth) + listPaddings);
            scenarioList.row();

            widget.addListener(new ScenarioTextInputListener(widget));
        }
        scenarioList.top().left();
        scenarioList.setX(getLeftBound());
        scenarioList.setY(getTopBound() - paneHeight);
        scenarioList.setWidth(paneWidth);
        scenarioList.setHeight(paneHeight);

        addActor(scenarioList);

    }

    private void initScenarioDescriptionPane() {
        float paneHeight = (getTopBound() - getBottomBound() - margins * 3) / 2;
        float paneWidth = (getRightBound() - getLeftBound() - margins * 3) / 2;

        TextWidget scenDescPane = new TextWidget(scenarioDescriptionStyle);
        scenDescPane.setX(getLeftBound());
        scenDescPane.setY(getBottomBound());
        scenDescPane.setWidth(paneWidth);
        scenDescPane.setHeight(paneHeight);

        addActor(scenDescPane);
    }

    private class ScenarioTextInputListener extends InputListener {

        private TextWidget widget;

        public ScenarioTextInputListener(TextWidget widget) {
            this.widget = widget;
        }

        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            widget.setSelected(true);
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            widget.setSelected(false);
        }
    }

}
