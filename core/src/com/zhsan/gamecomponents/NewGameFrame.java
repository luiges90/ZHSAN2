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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.zhsan.common.Utility;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.CheckboxWidget;
import com.zhsan.gamecomponents.common.GetScrollFocusWhenEntered;
import com.zhsan.gamecomponents.common.TextWidget;
import com.zhsan.gameobject.Faction;
import com.zhsan.gameobject.GameObjectList;
import com.zhsan.gameobject.GameScenario;
import com.zhsan.gameobject.GameSurvey;
import com.zhsan.resources.GlobalStrings;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 8/3/2015.
 */
public class NewGameFrame extends GameFrame {

    public interface OnScenarioChosenListener {
        public void onScenarioChosen(String scenPath, List<Integer> factionIds);
    }

    public static final String RES_PATH = GameFrame.RES_PATH + "New" + File.separator;
    public static final String DATA_PATH = RES_PATH  + "Data" + File.separator;

    private int margins;
    private int listPaddings;
    private Color listSelectedColor;

    private ScrollPane scenarioPane, scenarioDescriptionPane, factionPane;
    private TextWidget.Setting scenarioStyle, scenarioDescriptionStyle, factionStyle;

    private Texture scrollButton;
    private Texture checkbox, checkboxChecked;

    private OnScenarioChosenListener listener;
    private String chosenScenarioPath;

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
            listSelectedColor = Utility.loadColorFromXml(
                    Integer.parseUnsignedInt(dom.getElementsByTagName("Lists").item(0).getAttributes()
                            .getNamedItem("selectedColor").getNodeValue())
            );
            scrollButton = new Texture(Gdx.files.external(DATA_PATH + dom.getElementsByTagName("Scroll")
                    .item(0).getAttributes().getNamedItem( "fileName").getNodeValue()));

            scenarioStyle = TextWidget.Setting.fromXml(dom.getElementsByTagName("ScenarioList").item(0));
            scenarioDescriptionStyle = TextWidget.Setting.fromXml(dom.getElementsByTagName("ScenarioDescription").item(0));
            factionStyle = TextWidget.Setting.fromXml(dom.getElementsByTagName("FactionList").item(0));

            Node checkboxNode = dom.getElementsByTagName("FactionListCheckBox").item(0);
            checkbox = new Texture(Gdx.files.external(DATA_PATH +
                            checkboxNode.getAttributes().getNamedItem("Unchecked").getNodeValue()));
            checkboxChecked = new Texture(Gdx.files.external(DATA_PATH +
                    checkboxNode.getAttributes().getNamedItem("Checked").getNodeValue()));
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "NewGameFrameData.xml", e);
        }
    }

    public NewGameFrame(float width, float height, OnScenarioChosenListener listener) {
        super(width, height, GlobalStrings.getString(GlobalStrings.NEW_GAME), null);

        loadXml();

        initScenarioListPane();
        initScenarioDescriptionPane();
        initFactionPane();

        this.listener = listener;

        super.addOnClickListener(new ButtonListener());
        super.setOkEnabled(false);
    }

    private Table setupScrollpane(float x, float y, float paneWidth, float paneHeight, ScrollPane target) {
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegion(scrollButton));

        target.setStyle(scrollPaneStyle);
        target.setFadeScrollBars(false);
        target.setOverscroll(false, false);
        target.setFlickScroll(false);

        target.addListener(new GetScrollFocusWhenEntered(target));

        Table scenarioPaneContainer = new Table();
        scenarioPaneContainer.setX(x);
        scenarioPaneContainer.setY(y);
        scenarioPaneContainer.setWidth(paneWidth);
        scenarioPaneContainer.setHeight(paneHeight);
        scenarioPaneContainer.add(target).fill().expand();
        return scenarioPaneContainer;
    }

    private void initScenarioListPane() {
        float paneHeight = (getTopBound() - getBottomActiveBound() - margins * 3) / 2;
        float paneWidth = (getRightBound() - getLeftBound() - margins * 3) / 2;

        List<Pair<String, GameSurvey>> surveys = GameScenario.loadAllGameSurveys();

        VerticalGroup scenarioList = new VerticalGroup();
        for (Pair<String, GameSurvey> i : surveys) {
            TextWidget<Pair<String, GameSurvey>> widget = new TextWidget<>(scenarioStyle, i.getRight().getTitle());
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
        float paneHeight = (getTopBound() - getBottomActiveBound() - margins * 3) / 2;
        float paneWidth = (getRightBound() - getLeftBound() - margins * 3) / 2;

        TextWidget scenDescPane = new TextWidget(scenarioDescriptionStyle, TextWidget.VAlignment.TOP, "");
        scenDescPane.setWidth(paneWidth);

        scenarioDescriptionPane = new ScrollPane(scenDescPane);
        Table scenarioDescriptionPaneContainer = setupScrollpane(getLeftBound() + margins, getBottomActiveBound() + margins,
                paneWidth, paneHeight, scenarioDescriptionPane);

        addActor(scenarioDescriptionPaneContainer);
    }

    private void initFactionPane() {
        float paneHeight = (getTopBound() - getBottomActiveBound() - margins * 2);
        float paneWidth = (getRightBound() - getLeftBound() - margins * 3) / 2;

        VerticalGroup factionList = new VerticalGroup();

        factionPane = new ScrollPane(factionList);
        Table factionPaneContainer = setupScrollpane(getRightBound() - margins - paneWidth, getBottomActiveBound() + margins,
                paneWidth, paneHeight, factionPane);

        addActor(factionPaneContainer);
    }

    private class ButtonListener implements OnClick {
        @Override
        public void onOkClicked() {
            List<Integer> chosenFactions = new ArrayList<>();
            WidgetGroup factionPaneWidgets = (WidgetGroup) factionPane.getWidget();
            for (Actor w : factionPaneWidgets.getChildren()) {
                CheckboxWidget<Faction> cb = (CheckboxWidget<Faction>) w;
                if (cb.isChecked()) {
                    chosenFactions.add(cb.getExtra().getId());
                }
            }
            listener.onScenarioChosen(chosenScenarioPath, chosenFactions);
        }

        @Override
        public void onCancelClicked() {
            // no extra work, just close it
        }
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
            chosenScenarioPath = widget.getExtra().getLeft();

            ((TextWidget) scenarioDescriptionPane.getWidget()).setText(widget.getExtra().getRight().getDescription());

            GameObjectList<Faction> factions = Faction.fromCSVQuick(widget.getExtra().getLeft(), widget.getExtra().getRight().getVersion());

            VerticalGroup group = (VerticalGroup) factionPane.getWidget();
            for (Faction i : factions.getListOrderedById()) {
                CheckboxWidget<Faction> widget = new CheckboxWidget<>(factionStyle, i.getName(), checkboxChecked, checkbox);
                widget.setTouchable(Touchable.enabled);
                widget.setSelectedOutlineColor(listSelectedColor);
                widget.setExtra(i);
                widget.setWidth(factionPane.getWidth());
                widget.setPadding(listPaddings);
                group.addActor(widget);
            }

            NewGameFrame.this.setOkEnabled(true);

            return false;
        }
    }

}
