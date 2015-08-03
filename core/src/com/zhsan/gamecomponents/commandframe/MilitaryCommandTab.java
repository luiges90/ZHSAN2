package com.zhsan.gamecomponents.commandframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.zhsan.common.Point;
import com.zhsan.gamecomponents.GlobalStrings;
import com.zhsan.gamecomponents.common.ImageWidget;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.WidgetUtility;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.gamecomponents.gameframe.TabListGameFrame;
import com.zhsan.gameobject.GameObject;
import com.zhsan.gameobject.Military;
import com.zhsan.gameobject.MilitaryKind;
import com.zhsan.gameobject.Person;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 15/7/2015.
 */
public class MilitaryCommandTab implements CommandTab {

    private Texture background;

    private StateTexture campaign, newMilitary, recruit, training, merge, disband, upgrade;
    private Rectangle campaignPos, newMilitaryPos, recruitPos, trainingPos, mergePos, disbandPos, upgradePos;

    private List<TextWidget<ArchitectureCommandFrame.TextType>> textWidgets = new ArrayList<>();

    private ArchitectureCommandFrame parent;

    private Rectangle militaryListPos, militaryTablePos;
    private Point militaryTablePortraitSize, militaryTableCaptionSize;
    private int listNameWidth, listRecruitWidth, listTrainWidth, listQuantityWidth, listRowHeight;

    private ScrollPane militaryListPane, militaryTablePane;
    private Color militaryTablePortraitColor;
    private TextWidget<Military> militaryListTextTemplate, militaryTableCaptionTemplate;

    private List<TextWidget<?>> showingTextWidgets = new ArrayList<>();

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
                    }
                }
            } else if (n.getNodeName().equals("Campaign")) {
                campaign = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                campaignPos = XmlHelper.loadRectangleFromXml(n);
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
            } else {
                ArchitectureCommandFrame.loadText(n, textWidgets);
            }
        }
    }

    @Override
    public void drawBackground(Batch batch, float parentAlpha) {
        batch.draw(background, parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(campaign.get(), parent.getX() + campaignPos.x, parent.getY() + campaignPos.y, campaignPos.width, campaignPos.height);
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

        if (militaryListPane == null) {
            initMilitaryListPane();
        }
        if (militaryTablePane == null) {
            initMilitaryTablePane();
        }
    }

    public void invalidate() {
        invalidateListPanes();
    }

    private void invalidateListPanes() {
        if (militaryListPane != null) {
            militaryListPane.clear();
        }
        if (militaryTablePane != null) {
            militaryTablePane.clear();
        }
        showingTextWidgets.forEach(TextWidget::dispose);
        showingTextWidgets.clear();
        militaryListPane = null;
        militaryTablePane = null;
    }

    private void initMilitaryListPane() {
        Table contentTable = new Table();

        for (Military m : parent.getCurrentArchitecture().getMilitaries()) {
            TextWidget<Military> name = new TextWidget<>(militaryListTextTemplate);
            name.setExtra(m);
            name.setText(m.getName());
            contentTable.add(name).width(listNameWidth).height(listRowHeight);
            showingTextWidgets.add(name);

            TextWidget<Military> quantity = new TextWidget<>(militaryListTextTemplate);
            quantity.setExtra(m);
            quantity.setText(String.valueOf(m.getQuantity()));
            contentTable.add(quantity).width(listQuantityWidth).height(listRowHeight);
            showingTextWidgets.add(quantity);

            TextWidget<Military> recruit = new TextWidget<>(militaryListTextTemplate);
            recruit.setExtra(m);
            if (m.isFullyRecruited()) {
                recruit.setText(GlobalStrings.getString(GlobalStrings.Keys.TICK));
            } else if (m.isBeingRecruited()) {
                recruit.setText(GlobalStrings.getString(GlobalStrings.Keys.UP_ARROW));
            } else {
                recruit.setText("");
            }
            contentTable.add(recruit).width(listRecruitWidth).height(listRowHeight);
            showingTextWidgets.add(recruit);

            TextWidget<Military> train = new TextWidget<>(militaryListTextTemplate);
            train.setExtra(m);
            if (m.isFullyTrained()) {
                recruit.setText(GlobalStrings.getString(GlobalStrings.Keys.TICK));
            } else if (m.isBeingTrained()) {
                recruit.setText(GlobalStrings.getString(GlobalStrings.Keys.UP_ARROW));
            } else {
                recruit.setText("");
            }
            contentTable.add(train).width(listTrainWidth).height(listRowHeight);
            showingTextWidgets.add(train);

            contentTable.row().height(listRowHeight);
        }

        contentTable.top().left();

        militaryListPane = new ScrollPane(contentTable);
        Table contentPaneContainer = WidgetUtility.setupScrollpane(militaryListPos.getX(), militaryListPos.getY(),
                militaryListPos.getWidth(), militaryListPos.getHeight(), militaryListPane, parent.getScrollbar());

        parent.addActor(contentPaneContainer);
    }

    private void initMilitaryTablePane() {
        Table contentTable = new Table();

        int itemPerRow = (int) (militaryTablePos.width / Math.max(militaryTablePortraitSize.x, militaryTableCaptionSize.x));
        int index = 0;
        for (Military m : parent.getCurrentArchitecture().getMilitaries()) {
            Table item = new Table();

            Person leader = m.getLeader();
            ImageWidget<Military> portrait;
            if (leader != null) {
                portrait = new ImageWidget<>(parent.getScreen().getSmallPortrait(leader.getPortraitId()), militaryTablePortraitColor);
            } else {
                portrait = new ImageWidget<>(null, militaryTablePortraitColor);
            }
            portrait.setExtra(m);
            item.add(portrait).width(militaryTablePortraitSize.x).height(militaryTablePortraitSize.y).center().row();

            TextWidget<Military> caption = new TextWidget<>(militaryTableCaptionTemplate);
            caption.setExtra(m);
            caption.setText(m.getName());
            item.add(caption).width(militaryTableCaptionSize.x).height(militaryTableCaptionSize.y).center();

            item.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.ASSIGN_LEADER), TabListGameFrame.ListKindType.PERSON,
                            parent.getCurrentArchitecture().getPersonsWithoutLeadingMilitary(), TabListGameFrame.Selection.SINGLE,
                            selectedItems -> {
                                caption.getExtra().setLeader((Person) selectedItems.get(0));
                                invalidateListPanes();
                            });
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

        militaryTablePane = new ScrollPane(contentTable);
        Table contentPaneContainer = WidgetUtility.setupScrollpane(militaryTablePos.getX(), militaryTablePos.getY(),
                militaryTablePos.getWidth(), militaryTablePos.getHeight(), militaryTablePane, parent.getScrollbar());

        parent.addActor(contentPaneContainer);
    }

    @Override
    public void dispose() {
        textWidgets.forEach(TextWidget::dispose);
        background.dispose();
        campaign.dispose();
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
    public void onMouseMove(float x, float y) {
        if (campaignPos.contains(x, y)) {
            campaign.setState(StateTexture.State.SELECTED);
        } else {
            campaign.setState(StateTexture.State.NORMAL);
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
                    parent.getCurrentArchitecture().getActualCreatableMilitaryKinds(), TabListGameFrame.Selection.SINGLE,
                    selectedItems -> {
                        MilitaryKind kind = (MilitaryKind) selectedItems.get(0);
                        parent.getCurrentArchitecture().createMilitary(kind);
                        invalidateListPanes();
                    });
        } else if (recruitPos.contains(x, y) && parent.getCurrentArchitecture().getRecruitableMilitaries().size() > 0) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.RECRUIT_MILITARY), TabListGameFrame.ListKindType.PERSON,
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
        }
    }
}
