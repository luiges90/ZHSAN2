package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.zhsan.common.Utility;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.TextWidget;
import com.zhsan.gameobject.Faction;
import com.zhsan.gameobject.GameScenario;
import com.zhsan.gameobject.GameSurvey;
import com.zhsan.resources.GlobalStrings;
import org.apache.commons.lang3.tuple.Pair;
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
    public static final String DATA_PATH = RES_PATH  + "Data" + File.separator;

    private int margins;
    private int listPaddings;
    private Color listSelectedColor;

    private ScrollPane scenarioPane, scenarioDescriptionPane, factionPane;
    private TextWidget.Setting scenarioStyle, scenarioDescriptionStyle, factionStyle;

    private Texture scrollButton;

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
            scrollButton = new Texture(Gdx.files.external(DATA_PATH + dom.getElementsByTagName("Scroll")
                    .item(0).getAttributes().getNamedItem( "fileName").getNodeValue()));

            scenarioStyle = TextWidget.Setting.fromXml(dom.getElementsByTagName("ScenarioList").item(0));
            scenarioDescriptionStyle = TextWidget.Setting.fromXml(dom.getElementsByTagName("ScenarioDescription").item(0));
            factionStyle = TextWidget.Setting.fromXml(dom.getElementsByTagName("FactionList").item(0));
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
        initScenarioDescriptionPane();
        initFactionPane();
    }

    private Table setupScrollpane(float x, float y, float paneWidth, float paneHeight, ScrollPane target) {
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegion(scrollButton));

        target.setStyle(scrollPaneStyle);
        target.setFadeScrollBars(false);
        target.setOverscroll(false, false);
        target.setFlickScroll(false);

        Table scenarioPaneContainer = new Table();
        scenarioPaneContainer.setX(x);
        scenarioPaneContainer.setY(y);
        scenarioPaneContainer.setWidth(paneWidth);
        scenarioPaneContainer.setHeight(paneHeight);
        scenarioPaneContainer.add(target).fill().expand();
        return scenarioPaneContainer;
    }

    private void initScenarioListPane() {
        float paneHeight = (getTopBound() - getBottomBound() - margins * 3) / 2;
        float paneWidth = (getRightBound() - getLeftBound() - margins * 3) / 2;

        List<Pair<String, GameSurvey>> surveys = GameScenario.loadAllGameSurveys();

        VerticalGroup scenarioList = new VerticalGroup();
        for (Pair<String, GameSurvey> i : surveys) {
            TextWidget<Pair<String, GameSurvey>> widget = new TextWidget<>(scenarioStyle, i.getRight().title);
            widget.setTouchable(Touchable.enabled);
            widget.setSelectedOutlineColor(listSelectedColor);
            widget.setExtra(i);
            widget.setWidth(paneWidth);
            widget.setPadding(listPaddings);
            scenarioList.addActor(widget);

            widget.addListener(new ScenarioTextInputListener(widget));
        }

        scenarioPane = new ScrollPane(scenarioList);
        Table scenarioPaneContainer = setupScrollpane(getLeftBound() + margins, getTopBound() - margins - paneHeight,
                paneWidth, paneHeight, scenarioPane);

        addActor(scenarioPaneContainer);
    }

    private void initScenarioDescriptionPane() {
        float paneHeight = (getTopBound() - getBottomBound() - margins * 3) / 2;
        float paneWidth = (getRightBound() - getLeftBound() - margins * 3) / 2;

        TextWidget scenDescPane = new TextWidget(scenarioDescriptionStyle, TextWidget.VAlignment.TOP, "");
        scenDescPane.setWidth(paneWidth);

        scenarioDescriptionPane = new ScrollPane(scenDescPane);
        Table scenarioDescriptionPaneContainer = setupScrollpane(getLeftBound() + margins, getBottomBound() + margins,
                paneWidth, paneHeight, scenarioDescriptionPane);

        addActor(scenarioDescriptionPaneContainer);
    }

    private void initFactionPane() {
        float paneHeight = (getTopBound() - getBottomBound() - margins * 2);
        float paneWidth = (getRightBound() - getLeftBound() - margins * 3) / 2;

        VerticalGroup factionList = new VerticalGroup();

        factionPane = new ScrollPane(factionList);
        Table factionPaneContainer = setupScrollpane(getRightBound() - margins - paneWidth, getBottomBound() + margins,
                paneWidth, paneHeight, factionPane);

        addActor(factionPaneContainer);
    }

    private class ScenarioTextInputListener extends InputListener {

        private TextWidget<Pair<String, GameSurvey>> widget;

        public ScenarioTextInputListener(TextWidget<Pair<String, GameSurvey>> widget) {
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

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            ((TextWidget) scenarioDescriptionPane.getWidget()).setText(widget.getExtra().getRight().description);

            List<Faction> factions = Faction.fromCSV(widget.getExtra().getLeft(), null);

            VerticalGroup group = (VerticalGroup) factionPane.getWidget();
            for (Faction i : factions) {
                TextWidget<Faction> widget = new TextWidget<>(factionStyle, i.getName());
                widget.setTouchable(Touchable.enabled);
                widget.setSelectedOutlineColor(listSelectedColor);
                widget.setExtra(i);
                widget.setWidth(factionPane.getWidth());
                widget.setPadding(listPaddings);
                group.addActor(widget);

                // widget.addListener(new ScenarioTextInputListener(widget));
            }

            return false;
        }
    }

}
