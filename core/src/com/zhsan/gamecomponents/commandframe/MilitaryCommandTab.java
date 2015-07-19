package com.zhsan.gamecomponents.commandframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 15/7/2015.
 */
public class MilitaryCommandTab implements CommandTab {

    private Texture background;

    private Rectangle portraitPos;

    private StateTexture campaign, newMilitary, recruit, training, merge, disband, upgrade;
    private Rectangle campaignPos, newMilitaryPos, recruitPos, trainingPos, mergePos, disbandPos, upgradePos;

    private List<TextWidget<ArchitectureCommandFrame.TextType>> textWidgets = new ArrayList<>();

    private ArchitectureCommandFrame parent;

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
            } else if (n.getNodeName().equals("Portraits")) {
                portraitPos = XmlHelper.loadRectangleFromXml(n);
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

        }
    }
}
