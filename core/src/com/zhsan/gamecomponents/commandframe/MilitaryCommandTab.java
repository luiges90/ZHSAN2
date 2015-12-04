package com.zhsan.gamecomponents.commandframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.zhsan.common.Point;
import com.zhsan.gamecomponents.GlobalStrings;
import com.zhsan.gamecomponents.common.ImageWidget;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.WidgetUtility;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.gamecomponents.gameframe.TabListGameFrame;
import com.zhsan.gameobject.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Peter on 15/7/2015.
 */
public class MilitaryCommandTab implements CommandTab {

    private Texture background;
    private Point backgroundPos;

    private StateTexture reorganize, newMilitary, recruit, training, merge, disband, upgrade;
    private Rectangle reorganizePos, newMilitaryPos, recruitPos, trainingPos, mergePos, disbandPos, upgradePos;

    private List<TextWidget<ArchitectureCommandFrame.TextType>> textWidgets = new ArrayList<>();

    private ArchitectureCommandFrame parent;

    private Color selectedBorderColor;

    private Rectangle militaryListPos, militaryTablePos;
    private Point militaryTablePortraitSize, militaryTableCaptionSize, militaryTableDetailSize;
    private int listNameWidth, listRecruitWidth, listTrainWidth, listQuantityWidth, listRowHeight;

    private Table militaryBottomPane, militaryTopPane;
    private Color militaryTablePortraitColor;
    private TextWidget<Military> militaryListTextTemplate, militaryTableCaptionTemplate, militaryTableDetailTemplate;

    private List<TextWidget<?>> showingTextWidgets = new ArrayList<>();

    private Military currentMilitary;
    private Rectangle currentMilitaryPos;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public MilitaryCommandTab(ArchitectureCommandFrame parent) {
        this.parent = parent;
    }

    @Override
    public void loadXml(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node n = nodes.item(i);

            if (n.getNodeName().equals("Background")) {
                background = new Texture(Gdx.files.external(
                        ArchitectureCommandFrame.DATA_PATH + XmlHelper.loadAttribute(n, "FileName")
                ));
                backgroundPos = Point.fromXml(n);
            } else if (n.getNodeName().equals("MilitaryTable")) {
                militaryTablePos = XmlHelper.loadRectangleFromXml(n);
                NodeList child = n.getChildNodes();
                for (int j = 0; j < child.getLength(); ++j) {
                    Node n2 = child.item(j);
                    if (n2.getNodeName().equals("Portrait")) {
                        militaryTablePortraitSize = Point.fromXmlAsSize(n2);
                        militaryTablePortraitColor = XmlHelper.loadColorFromXml(Integer.parseUnsignedInt(
                                XmlHelper.loadAttribute(n2, "BorderColor")
                        ));
                    } else if (n2.getNodeName().equals("Caption")) {
                        militaryTableCaptionSize = Point.fromXmlAsSize(n2);
                        militaryTableCaptionTemplate = new TextWidget<>(TextWidget.Setting.fromXml(n2));
                    } else if (n2.getNodeName().equals("Detail")) {
                        militaryTableDetailSize = Point.fromXmlAsSize(n2);
                        militaryTableDetailTemplate = new TextWidget<>(TextWidget.Setting.fromXml(n2));
                    }
                }
            } else if (n.getNodeName().equals("Reorganize")) {
                reorganize = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                reorganizePos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("New")) {
                newMilitary = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                newMilitaryPos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("Recruit")) {
                recruit = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                recruitPos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("Training")) {
                training = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                trainingPos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("Merge")) {
                merge = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                mergePos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("Disband")) {
                disband = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                disbandPos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("Upgrade")) {
                upgrade = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                upgradePos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("MilitaryList")) {
                militaryListPos = XmlHelper.loadRectangleFromXml(n);
                listNameWidth = Integer.parseInt(XmlHelper.loadAttribute(n, "NameWidth"));
                listRecruitWidth = Integer.parseInt(XmlHelper.loadAttribute(n, "RecruitWidth"));
                listTrainWidth = Integer.parseInt(XmlHelper.loadAttribute(n, "TrainWidth"));
                listQuantityWidth = Integer.parseInt(XmlHelper.loadAttribute(n, "QuantityWidth"));
                listRowHeight = Integer.parseInt(XmlHelper.loadAttribute(n, "RowHeight"));
                militaryListTextTemplate = new TextWidget<>(TextWidget.Setting.fromXml(n));
            } else if (n.getNodeName().equals("SelectedBorderColor")) {
                selectedBorderColor = XmlHelper.loadColorFromXml(Integer.parseUnsignedInt(XmlHelper.loadAttribute(n, "BorderColor")));
            } else {
                ArchitectureCommandFrame.loadText(n, textWidgets);
            }
        }
    }

    @Override
    public void drawBackground(Batch batch, float parentAlpha) {
        batch.draw(background, parent.getX() + backgroundPos.x, parent.getY() + backgroundPos.y, parent.getWidth(), parent.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(reorganize.get(), parent.getX() + reorganizePos.x, parent.getY() + reorganizePos.y, reorganizePos.width, reorganizePos.height);
        batch.draw(newMilitary.get(), parent.getX() + newMilitaryPos.x, parent.getY() + newMilitaryPos.y, newMilitaryPos.width, newMilitaryPos.height);
        batch.draw(recruit.get(), parent.getX() + recruitPos.x, parent.getY() + recruitPos.y, recruitPos.width, recruitPos.height);
        batch.draw(training.get(), parent.getX() + trainingPos.x, parent.getY() + trainingPos.y, trainingPos.width, trainingPos.height);
        batch.draw(merge.get(), parent.getX() + mergePos.x, parent.getY() + mergePos.y, mergePos.width, mergePos.height);
        batch.draw(disband.get(), parent.getX() + disbandPos.x, parent.getY() + disbandPos.y, disbandPos.width, disbandPos.height);
        batch.draw(upgrade.get(), parent.getX() + upgradePos.x, parent.getY() + upgradePos.y, upgradePos.width, upgradePos.height);

        for (TextWidget<ArchitectureCommandFrame.TextType> textWidget : textWidgets) {
            textWidget.setPosition(textWidget.getExtra().position.x + parent.getX(), textWidget.getExtra().position.y + parent.getY());
            if (textWidget.getExtra().staticText) {
                textWidget.setText(textWidget.getExtra().name);
            } else {
                textWidget.setText(parent.getCurrentArchitecture().getFieldString(textWidget.getExtra().name));
            }
            textWidget.draw(batch, parentAlpha);
        }

        if (militaryBottomPane == null) {
            initMilitaryBottomPane();
        }
        if (militaryTopPane == null) {
            initMilitaryTopPane();
        }

        if (currentMilitary != null) {
            batch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());

            shapeRenderer.setColor(selectedBorderColor);
            shapeRenderer.rect(parent.getX() + currentMilitaryPos.getX(), parent.getY() + currentMilitaryPos.getY(),
                    currentMilitaryPos.getWidth(), currentMilitaryPos.getHeight());

            shapeRenderer.end();

            batch.begin();
        }
    }

    public void invalidate() {
        invalidateListPanes();
    }

    private void invalidateListPanes() {
        if (militaryBottomPane != null) {
            militaryBottomPane.clear();
        }
        if (militaryTopPane != null) {
            militaryTopPane.clear();
        }
        showingTextWidgets.forEach(TextWidget::dispose);
        showingTextWidgets.clear();
        militaryBottomPane = null;
        militaryTopPane = null;
        currentMilitary = null;
        currentMilitaryPos = null;
    }

    private void initMilitaryBottomPane() {
        Table contentTable = new Table();

        for (Military m : parent.getCurrentArchitecture().getMilitariesWithoutLeader()) {
            List<TextWidget<Military>> rowWidgets = new ArrayList<>();

            TextWidget<Military> name = new TextWidget<>(militaryListTextTemplate);
            name.setExtra(m);
            name.setText(m.getName());
            contentTable.add(name).width(listNameWidth).height(listRowHeight);
            rowWidgets.add(name);

            TextWidget<Military> quantity = new TextWidget<>(militaryListTextTemplate);
            quantity.setExtra(m);
            quantity.setText(String.valueOf(m.getQuantity()));
            contentTable.add(quantity).width(listQuantityWidth).height(listRowHeight);
            rowWidgets.add(quantity);

            TextWidget<Military> recruit = new TextWidget<>(militaryListTextTemplate);
            recruit.setExtra(m);
            if (m.isFullyRecruited()) {
                recruit.setText(GlobalStrings.getString(GlobalStrings.Keys.TICK));
            } else if (m.isBeingRecruited()) {
                recruit.setText(GlobalStrings.getString(GlobalStrings.Keys.UP_ARROW));
            } else {
                recruit.setText("");
            }
            contentTable.add(recruit).width(listRecruitWidth).height(listRowHeight).center();
            rowWidgets.add(recruit);

            TextWidget<Military> train = new TextWidget<>(militaryListTextTemplate);
            train.setExtra(m);
            if (m.isFullyTrained()) {
                train.setText(GlobalStrings.getString(GlobalStrings.Keys.TICK));
            } else if (m.isBeingTrained()) {
                train.setText(GlobalStrings.getString(GlobalStrings.Keys.UP_ARROW));
            } else {
                train.setText("");
            }
            contentTable.add(train).width(listTrainWidth).height(listRowHeight).center();
            rowWidgets.add(train);

            rowWidgets.forEach(x -> x.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    currentMilitary = m;
                    currentMilitaryPos = new Rectangle(name.getX() + militaryListPos.getX(), name.getY() + militaryListPos.getY(),
                            militaryListPos.getWidth(), name.getHeight());
                    return true;
                }
            }));

            showingTextWidgets.addAll(rowWidgets);

            contentTable.row().height(listRowHeight);
        }

        contentTable.top().left();

        ScrollPane data = new ScrollPane(contentTable);
        militaryBottomPane = WidgetUtility.setupScrollpane(militaryListPos.getX(), militaryListPos.getY(),
                militaryListPos.getWidth(), militaryListPos.getHeight(), data, parent.getScrollbar());

        parent.addActor(militaryBottomPane);
    }

    private void initMilitaryTopPane() {
        Table contentTable = new Table();

        int itemPerRow = (int) (militaryTablePos.width / (militaryTablePortraitSize.x + militaryTableCaptionSize.x));
        int index = 0;
        for (Military m : parent.getCurrentArchitecture().getMilitariesWithLeader()) {
            Table item = new Table();

            Person leader = m.getLeader();
            ImageWidget<Military> portrait;
            if (leader != null) {
                portrait = new ImageWidget<>(parent.getScreen().getSmallPortrait(leader.getPortraitId()), militaryTablePortraitColor);
            } else {
                portrait = new ImageWidget<>(null, militaryTablePortraitColor);
            }
            portrait.setExtra(m);
            item.add(portrait).width(militaryTablePortraitSize.x).height(militaryTablePortraitSize.y);

            Table detail = new Table();

            TextWidget<Military> caption = new TextWidget<>(militaryTableCaptionTemplate);
            caption.setExtra(m);
            caption.setText(m.getName());
            detail.add(caption).width(militaryTableCaptionSize.x).height(militaryTableCaptionSize.y).row();

            TextWidget<Military> quantity = new TextWidget<>(militaryTableDetailTemplate);
            quantity.setExtra(m);
            quantity.setText(GlobalStrings.getString(GlobalStrings.Keys.MILITARY_QUANTITY_SHORT) + m.getQuantity());
            detail.add(quantity).width(militaryTableDetailSize.x).height(militaryTableDetailSize.y).row();

            TextWidget<Military> morale = new TextWidget<>(militaryTableDetailTemplate);
            morale.setExtra(m);
            morale.setText(GlobalStrings.getString(GlobalStrings.Keys.MILITARY_MORALE_SHORT) + m.getMorale());
            detail.add(morale).width(militaryTableDetailSize.x).height(militaryTableDetailSize.y).row();

            TextWidget<Military> combativity = new TextWidget<>(militaryTableDetailTemplate);
            combativity.setExtra(m);
            combativity.setText(GlobalStrings.getString(GlobalStrings.Keys.MILITARY_COMBATIVITY_SHORT) + m.getCombativity());
            detail.add(combativity).width(militaryTableDetailSize.x).height(militaryTableDetailSize.y).row();

            detail.top();
            item.add(detail);

            item.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    currentMilitary = m;
                    currentMilitaryPos = new Rectangle(item.getX() + militaryTablePos.getX(), item.getY() + militaryTablePos.getY(),
                            item.getWidth(), item.getHeight());
                    if (m.isCampaignable()) {
                        parent.getScreen().getMapLayer()
                                .startSelectingLocation(parent.getCurrentArchitecture().getCampaignablePositions(), p -> {
                                    m.startCampaign(p);
                                    invalidateListPanes();
                                });
                    }
                    return true;
                }
            });

            contentTable.add(item);

            index++;
            if (index % itemPerRow == 0) {
                contentTable.row();
            }
        }

        contentTable.top().left();

        ScrollPane data = new ScrollPane(contentTable);
        militaryTopPane = WidgetUtility.setupScrollpane(militaryTablePos.getX(), militaryTablePos.getY(),
                militaryTablePos.getWidth(), militaryTablePos.getHeight(), data, parent.getScrollbar());

        parent.addActor(militaryTopPane);
    }

    @Override
    public void dispose() {
        textWidgets.forEach(TextWidget::dispose);
        background.dispose();
        reorganize.dispose();
        newMilitary.dispose();
        recruit.dispose();
        training.dispose();
        merge.dispose();
        disband.dispose();
        upgrade.dispose();
        militaryListTextTemplate.dispose();
        invalidateListPanes();
    }

    @Override
    public void onUnselect() {
        invalidateListPanes();
    }

    @Override
    public void onMouseMove(float x, float y) {
        if (reorganizePos.contains(x, y)) {
            reorganize.setState(StateTexture.State.SELECTED);
        } else {
            reorganize.setState(StateTexture.State.NORMAL);
        }
        if (newMilitaryPos.contains(x, y)) {
            newMilitary.setState(StateTexture.State.SELECTED);
        } else {
            newMilitary.setState(StateTexture.State.NORMAL);
        }
        if (recruitPos.contains(x, y)) {
            recruit.setState(StateTexture.State.SELECTED);
        } else {
            recruit.setState(StateTexture.State.NORMAL);
        }
        if (trainingPos.contains(x, y)) {
            training.setState(StateTexture.State.SELECTED);
        } else {
            training.setState(StateTexture.State.NORMAL);
        }
        if (mergePos.contains(x, y)) {
            merge.setState(StateTexture.State.SELECTED);
        } else {
            merge.setState(StateTexture.State.NORMAL);
        }
        if (disbandPos.contains(x, y)) {
            disband.setState(StateTexture.State.SELECTED);
        } else {
            disband.setState(StateTexture.State.NORMAL);
        }
        if (upgradePos.contains(x, y)) {
            upgrade.setState(StateTexture.State.SELECTED);
        } else {
            upgrade.setState(StateTexture.State.NORMAL);
        }
    }

    @Override
    public void onClick(float x, float y) {
        if (newMilitaryPos.contains(x, y)) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.NEW_MILITARY), TabListGameFrame.ListKindType.MILITARY_KIND,
                    parent.getCurrentArchitecture(),
                    parent.getCurrentArchitecture().getActualCreatableMilitaryKinds(), TabListGameFrame.Selection.SINGLE,
                    selectedItems -> {
                        MilitaryKind kind = (MilitaryKind) selectedItems.get(0);
                        parent.getCurrentArchitecture().createMilitary(kind);
                        invalidateListPanes();
                    });
        } else if (recruitPos.contains(x, y) && parent.getCurrentArchitecture().getRecruitableMilitaries().size() > 0) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.RECRUIT_MILITARY), TabListGameFrame.ListKindType.PERSON,
                    parent.getCurrentArchitecture(),
                    parent.getCurrentArchitecture().getPersonsExcludingMayor(), TabListGameFrame.Selection.MULTIPLE,
                    selectedItems -> {
                        for (GameObject i : selectedItems) {
                            Person p = (Person) i;
                            p.setDoingWork(Person.DoingWork.RECRUIT);
                        }
                        invalidateListPanes();
                    });
        } else if (trainingPos.contains(x, y) && parent.getCurrentArchitecture().getSelectTrainableMilitaries().size() > 0) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.TRAIN_MILITARY), TabListGameFrame.ListKindType.PERSON,
                    parent.getCurrentArchitecture().getPersonsExcludingMayor(), TabListGameFrame.Selection.MULTIPLE,
                    selectedItems -> {
                        for (GameObject i : selectedItems) {
                            Person p = (Person) i;
                            p.setDoingWork(Person.DoingWork.TRAINING);
                        }
                        invalidateListPanes();
                    });
        } else if (reorganizePos.contains(x, y) && currentMilitary != null) {
            GameObjectList<Person> leaderCandidates = new GameObjectList<>(parent.getCurrentArchitecture().getPersonsNotInMilitary());
            leaderCandidates.add(currentMilitary.getLeader());
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.ASSIGN_LEADER), TabListGameFrame.ListKindType.PERSON,
                    leaderCandidates, TabListGameFrame.Selection.SINGLE,
                    selectedItems -> {
                        currentMilitary.setLeader((Person) selectedItems.get(0));
                        parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.ASSIGN_MILITARY_PERSON), TabListGameFrame.ListKindType.PERSON,
                                parent.getCurrentArchitecture().getPersonsNotInMilitary(), TabListGameFrame.Selection.MULTIPLE,
                                selectedItems1 -> {
                                    currentMilitary.setPersons(selectedItems1.stream().map(o -> (Person) o).collect(Collectors.toCollection(GameObjectList<Person>::new)));
                                    invalidateListPanes();
                                });
                    });
        }
    }
}
