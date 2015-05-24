package com.zhsan.gamecomponents.gameframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.*;
import com.zhsan.gamecomponents.common.textwidget.CheckboxWidget;
import com.zhsan.gamecomponents.common.textwidget.RadioButtonWidget;
import com.zhsan.gamecomponents.common.textwidget.SelectableTextWidget;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.gameobject.Faction;
import com.zhsan.gameobject.GameObjectList;
import com.zhsan.gameobject.GameScenario;
import com.zhsan.gameobject.GameSurvey;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Peter on 8/3/2015.
 */
public class NewGameFrame extends GameFrame {

    public interface OnScenarioChosenListener {
        public void onScenarioChosen(FileHandle file, int factionId);
    }

    public static final String RES_PATH = GameFrame.RES_PATH + "New" + File.separator;
    public static final String DATA_PATH = RES_PATH  + "Data" + File.separator;

    private String title;

    private int margins;
    private int listPaddings;
    private Color listSelectedColor;

    private ScrollPane scenarioPane, scenarioDescriptionPane, factionPane;
    private TextWidget.Setting scenarioStyle, scenarioDescriptionStyle, factionStyle;

    private Texture scrollButton;
    private Texture checkbox, checkboxChecked;

    private OnScenarioChosenListener listener;
    private FileHandle chosenScenario;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "NewGameFrameData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            title = XmlHelper.loadAttribute(dom.getElementsByTagName("Title").item(0), "title");

            margins = Integer.parseInt(XmlHelper.loadAttribute(dom.getElementsByTagName("Margins").item(0), "value"));
            listPaddings = Integer.parseInt(XmlHelper.loadAttribute(dom.getElementsByTagName("Lists").item(0), "padding"));
            listSelectedColor = XmlHelper.loadColorFromXml(
                    Integer.parseUnsignedInt(XmlHelper.loadAttribute(dom.getElementsByTagName("Lists").item(0), "selectedColor"))
            );
            scrollButton = new Texture(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(dom.getElementsByTagName("Scroll").item(0), "fileName")));

            scenarioStyle = TextWidget.Setting.fromXml(dom.getElementsByTagName("ScenarioList").item(0));
            scenarioDescriptionStyle = TextWidget.Setting.fromXml(dom.getElementsByTagName("ScenarioDescription").item(0));
            factionStyle = TextWidget.Setting.fromXml(dom.getElementsByTagName("FactionList").item(0));

            Node checkboxNode = dom.getElementsByTagName("FactionListCheckBox").item(0);
            checkbox = new Texture(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(checkboxNode, "Unchecked")));
            checkboxChecked = new Texture(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(checkboxNode, "Checked")));
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "NewGameFrameData.xml", e);
        }
    }

    public NewGameFrame(OnScenarioChosenListener listener) {
        super("", null);

        loadXml();

        setTitle(title);

        initScenarioListPane();
        initScenarioDescriptionPane();
        initFactionPane();

        this.listener = listener;

        super.addOnClickListener(new ButtonListener());
        super.setOkEnabled(false);
    }

    private void initScenarioListPane() {
        float paneHeight = (getTopBound() - getBottomActiveBound() - margins * 3) / 2;
        float paneWidth = (getRightBound() - getLeftBound() - margins * 3) / 2;

        List<Pair<FileHandle, GameSurvey>> surveys = GameScenario.loadAllGameSurveys();

        VerticalGroup scenarioList = new VerticalGroup();
        for (Pair<FileHandle, GameSurvey> i : surveys) {
            SelectableTextWidget<Pair<FileHandle, GameSurvey>> widget = new SelectableTextWidget<>(scenarioStyle, i.getRight().getTitle(), listSelectedColor);
            widget.setExtra(i);
            widget.setWidth(paneWidth);
            widget.setPadding(listPaddings);
            scenarioList.addActor(widget);

            widget.addListener(new ScenarioTextInputListener(widget));
        }

        scenarioPane = new ScrollPane(scenarioList);
        Table scenarioPaneContainer = WidgetUtility.setupScrollpane(getLeftBound() + margins, getTopBound() - margins - paneHeight,
                paneWidth, paneHeight, scenarioPane, scrollButton);

        addActor(scenarioPaneContainer);
    }

    private void initScenarioDescriptionPane() {
        float paneHeight = (getTopBound() - getBottomActiveBound() - margins * 3) / 2;
        float paneWidth = (getRightBound() - getLeftBound() - margins * 3) / 2;

        TextWidget scenDescPane = new TextWidget(scenarioDescriptionStyle, TextWidget.VAlignment.TOP, "");
        scenDescPane.setWidth(paneWidth);

        scenarioDescriptionPane = new ScrollPane(scenDescPane);
        Table scenarioDescriptionPaneContainer = WidgetUtility.setupScrollpane(getLeftBound() + margins, getBottomActiveBound() + margins,
                paneWidth, paneHeight, scenarioDescriptionPane, scrollButton);

        addActor(scenarioDescriptionPaneContainer);
    }

    private void initFactionPane() {
        float paneHeight = (getTopBound() - getBottomActiveBound() - margins * 2);
        float paneWidth = (getRightBound() - getLeftBound() - margins * 3) / 2;

        VerticalGroup factionList = new VerticalGroup();

        factionPane = new ScrollPane(factionList);
        Table factionPaneContainer = WidgetUtility.setupScrollpane(getRightBound() - margins - paneWidth, getBottomActiveBound() + margins,
                paneWidth, paneHeight, factionPane, scrollButton);

        addActor(factionPaneContainer);
    }

    private class ButtonListener implements OnClick {
        @Override
        public void onOkClicked() {
            int chosenFaction = -1;
            WidgetGroup factionPaneWidgets = (WidgetGroup) factionPane.getWidget();
            for (Actor w : factionPaneWidgets.getChildren()) {
                CheckboxWidget<Faction> cb = (CheckboxWidget<Faction>) w;
                if (cb.isChecked()) {
                    chosenFaction = cb.getExtra().getId();
                    break;
                }
            }
            listener.onScenarioChosen(chosenScenario, chosenFaction);
        }

        @Override
        public void onCancelClicked() {
            // no extra work, just close it
        }
    }

    private class ScenarioTextInputListener extends InputListener {

        private SelectableTextWidget<Pair<FileHandle, GameSurvey>> widget;

        public ScenarioTextInputListener(SelectableTextWidget<Pair<FileHandle, GameSurvey>> widget) {
            this.widget = widget;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            chosenScenario = widget.getExtra().getLeft();

            ((TextWidget) scenarioDescriptionPane.getWidget()).setText(widget.getExtra().getRight().getDescription());

            GameObjectList<Faction> factions = GameScenario.loadFactionsQuick(widget.getExtra().getLeft(), widget.getExtra().getRight().getVersion());

            VerticalGroup group = (VerticalGroup) factionPane.getWidget();
            group.clear();

            Set<RadioButtonWidget<?>> allRadioButtons = new HashSet<>();
            for (Faction i : factions) {
                RadioButtonWidget<Faction> widget = new RadioButtonWidget<>(factionStyle, i.getName(), checkboxChecked, checkbox);
                widget.setTouchable(Touchable.enabled);
                widget.setExtra(i);
                widget.setWidth(factionPane.getWidth());
                widget.setPadding(listPaddings);
                group.addActor(widget);
                allRadioButtons.add(widget);
            }
            for (RadioButtonWidget<?> i : allRadioButtons) {
                i.setGroup(allRadioButtons);
            }

            NewGameFrame.this.setOkEnabled(true);

            return false;
        }
    }

}
