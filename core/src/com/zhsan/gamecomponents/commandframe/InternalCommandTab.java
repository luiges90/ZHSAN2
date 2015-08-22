package com.zhsan.gamecomponents.commandframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.zhsan.common.GlobalVariables;
import com.zhsan.gamecomponents.GlobalStrings;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.gamecomponents.gameframe.TabListGameFrame;
import com.zhsan.gameobject.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 13/7/2015.
 */
public class InternalCommandTab implements CommandTab {

    private static class InternalPortraitType {
        public final Rectangle position;
        public final Color borderColor;
        public final String name;
        public final String fieldFirst;

        public Person leader;
        public boolean leaderSet;

        public InternalPortraitType(Rectangle position, Color borderColor, String name, String fieldFirst) {
            this.position = position;
            this.borderColor = borderColor;
            this.name = name;
            this.fieldFirst = fieldFirst;
        }
    }


    private StateTexture assign, commerce, agriculture, technology, morale, endurance;
    private Rectangle assignPos, commercePos, agriculturePos, technologyPos, moralePos, endurancePos;

    private Rectangle factionColor;
    private Color factionBorder;

    private Texture portraitBorder;
    private Rectangle mayorPortraitPos;
    private int mayorOffsetX, mayorOffsetY;

    private List<TextWidget<ArchitectureCommandFrame.TextType>> textWidgets = new ArrayList<>();
    private List<InternalPortraitType> internalPortraits = new ArrayList<>();

    private Texture background;

    private boolean mayorSet;
    private Person mayor;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private ArchitectureCommandFrame parent;

    public InternalCommandTab(ArchitectureCommandFrame parent) {
        this.parent = parent;
    }

    public void loadXml(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node n = nodes.item(i);
            if (n.getNodeName().equals("Background")) {
                background = new Texture(Gdx.files.external(
                        ArchitectureCommandFrame.DATA_PATH + XmlHelper.loadAttribute(n, "FileName")
                ));
            } else if (n.getNodeName().equals("MayorPortrait")) {
                mayorPortraitPos = XmlHelper.loadRectangleFromXml(n);
                portraitBorder = new Texture(Gdx.files.external
                        (ArchitectureCommandFrame.DATA_PATH + XmlHelper.loadAttribute(n, "Border")));
                mayorOffsetX = Integer.parseInt(XmlHelper.loadAttribute(n, "OffsetX"));
                mayorOffsetY = Integer.parseInt(XmlHelper.loadAttribute(n, "OffsetY"));
            } else if (n.getNodeName().equals("Assign")) {
                assign = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                assignPos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("Agriculture")) {
                agriculture = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                agriculturePos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("Commerce")) {
                commerce = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                commercePos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("Technology")) {
                technology = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                technologyPos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("Morale")) {
                morale = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                moralePos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("Endurance")) {
                endurance = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                endurancePos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("FactionColor")) {
                factionColor = XmlHelper.loadRectangleFromXml(n);
                factionBorder = XmlHelper.loadColorFromXml(Integer.parseUnsignedInt(XmlHelper.loadAttribute(n, "BorderColor")));
            }  else if (n.getNodeName().equals("InternalDevelopRate")) {
                TextWidget<ArchitectureCommandFrame.TextType> widget = new TextWidget<>(TextWidget.Setting.fromXml(n));
                widget.setExtra(new ArchitectureCommandFrame.TextType(
                        XmlHelper.loadRectangleFromXmlOptSize(n),
                        false,
                        XmlHelper.loadAttribute(n, "Field"),
                        XmlHelper.loadColorFromXml(Integer.parseUnsignedInt(XmlHelper.loadAttribute(n, "FallingFontColor")))
                ));
                widget.setSize(widget.getExtra().position.width, widget.getExtra().position.height);
                textWidgets.add(widget);
            } else if (n.getNodeName().equals("InternalPortrait")) {
                internalPortraits.add(new InternalPortraitType(
                        XmlHelper.loadRectangleFromXml(n),
                        XmlHelper.loadColorFromXml(Integer.parseUnsignedInt(XmlHelper.loadAttribute(n, "BorderColor"))),
                        XmlHelper.loadAttribute(n, "Field"),
                        XmlHelper.loadAttribute(n, "FieldFirst")
                ));
            } else {
                ArchitectureCommandFrame.loadText(n, textWidgets);
            }
        }

    }

    public void invalidate() {
        mayorSet = false;
        internalPortraits.forEach(t -> t.leaderSet = false);
    }

    private void updateMayor() {
        mayor = parent.getCurrentArchitecture().getMayor();
        mayorSet = true;
    }

    private void updateInternal(InternalPortraitType type) {
        type.leader = ((GameObjectList<Person>) parent.getCurrentArchitecture().getField(type.name)).max(
                (x, y) -> Integer.compare((int) x.getField(type.fieldFirst), (int) y.getField(type.fieldFirst)
        ), null);
        type.leaderSet = true;
    }

    private void updateInternal() {
        internalPortraits.forEach(this::updateInternal);
    }

    public void drawBackground(Batch batch, float parentAlpha) {
        batch.draw(background, parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!mayorSet) {
            updateMayor();
        }

        if (mayor != null) {
            batch.draw(parent.getScreen().getSmallPortrait(mayor.getPortraitId()),
                    parent.getX() + mayorPortraitPos.x + mayorOffsetX, parent.getY() + mayorPortraitPos.y + mayorOffsetY,
                    mayorPortraitPos.width - 2 * mayorOffsetX, mayorPortraitPos.height - 2 * mayorOffsetY);
        }
        batch.draw(portraitBorder, parent.getX() + mayorPortraitPos.x, parent.getY() + mayorPortraitPos.y,
                mayorPortraitPos.width, mayorPortraitPos.height);

        batch.draw(assign.get(), parent.getX() + assignPos.x, parent.getY() + assignPos.y, assignPos.width, assignPos.height);
        batch.draw(agriculture.get(), parent.getX() + agriculturePos.x, parent.getY() + agriculturePos.y, agriculturePos.width, agriculturePos.height);
        batch.draw(commerce.get(), parent.getX() + commercePos.x, parent.getY() + commercePos.y, commercePos.width, commercePos.height);
        batch.draw(technology.get(), parent.getX() + technologyPos.x, parent.getY() + technologyPos.y, technologyPos.width, technologyPos.height);
        batch.draw(morale.get(), parent.getX() + moralePos.x, parent.getY() + moralePos.y, moralePos.width, moralePos.height);
        batch.draw(endurance.get(), parent.getX() + endurancePos.x, parent.getY() + endurancePos.y, endurancePos.width, endurancePos.height);

        batch.end();

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();

        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        Faction f = parent.getCurrentArchitecture().getBelongedFaction();
        shapeRenderer.setColor(f == null ? GlobalVariables.blankColor : f.getColor());
        shapeRenderer.rect(parent.getX() + factionColor.getX(), parent.getY() + factionColor.getY(),
                factionColor.getWidth(), factionColor.getHeight());

        shapeRenderer.set(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(factionBorder);
        shapeRenderer.rect(parent.getX() + factionColor.getX(), parent.getY() + factionColor.getY(),
                factionColor.getWidth(), factionColor.getHeight());

        shapeRenderer.end();

        batch.begin();

        for (TextWidget<ArchitectureCommandFrame.TextType> textWidget : textWidgets) {
            textWidget.setPosition(textWidget.getExtra().position.x + parent.getX(), textWidget.getExtra().position.y + parent.getY());
            if (textWidget.getExtra().staticText) {
                textWidget.setText(textWidget.getExtra().name);
            } else {
                textWidget.setText(parent.getCurrentArchitecture().getFieldString(textWidget.getExtra().name));
            }
            textWidget.draw(batch, parentAlpha);
        }

        for (InternalPortraitType internalPortraitType : internalPortraits) {
            if (!internalPortraitType.leaderSet) {
                updateInternal(internalPortraitType);
            }
            if (internalPortraitType.leader != null) {
                batch.draw(parent.getScreen().getSmallPortrait(internalPortraitType.leader.getPortraitId()),
                        parent.getX() + internalPortraitType.position.x, parent.getY() + internalPortraitType.position.y,
                        internalPortraitType.position.width, internalPortraitType.position.height);
            }

            batch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

            shapeRenderer.setColor(internalPortraitType.borderColor);
            shapeRenderer.rect(parent.getX() + internalPortraitType.position.x, parent.getY() + internalPortraitType.position.y,
                    internalPortraitType.position.width, internalPortraitType.position.height);

            shapeRenderer.end();

            batch.begin();
        }
    }

    public void dispose() {
        textWidgets.forEach(TextWidget::dispose);
        background.dispose();
        assign.dispose();
        commerce.dispose();
        agriculture.dispose();
        technology.dispose();
        morale.dispose();
        endurance.dispose();
    }

    public void onUnselect() {
        // no-op
    }

    public void onMouseMove(float x, float y) {
        if (assignPos.contains(x, y) && parent.getCurrentArchitecture().canChangeMayorToOther()) {
            assign.setState(StateTexture.State.SELECTED);
        } else {
            assign.setState(StateTexture.State.NORMAL);
        }
        if (agriculturePos.contains(x, y)) {
            agriculture.setState(StateTexture.State.SELECTED);
        } else {
            agriculture.setState(StateTexture.State.NORMAL);
        }
        if (commercePos.contains(x, y)) {
            commerce.setState(StateTexture.State.SELECTED);
        } else {
            commerce.setState(StateTexture.State.NORMAL);
        }
        if (technologyPos.contains(x, y)) {
            technology.setState(StateTexture.State.SELECTED);
        } else {
            technology.setState(StateTexture.State.NORMAL);
        }
        if (endurancePos.contains(x, y)) {
            endurance.setState(StateTexture.State.SELECTED);
        } else {
            endurance.setState(StateTexture.State.NORMAL);
        }
        if (moralePos.contains(x, y)) {
            morale.setState(StateTexture.State.SELECTED);
        } else {
            morale.setState(StateTexture.State.NORMAL);
        }
    }

    @Override
    public void onClick(float x, float y) {
        if (assignPos.contains(x, y) && parent.getCurrentArchitecture().canChangeMayorToOther()) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.MAYOR), TabListGameFrame.ListKindType.PERSON,
                    parent.getCurrentArchitecture().getPersons(), TabListGameFrame.Selection.SINGLE,
                    selectedItems -> {
                        parent.getCurrentArchitecture().changeMayor((Person) selectedItems.get(0));
                        updateMayor();
                        updateInternal();
                    });
            assign.setState(StateTexture.State.NORMAL);
        } else if (agriculturePos.contains(x, y)) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.AGRICULTURE), TabListGameFrame.ListKindType.PERSON,
                    parent.getCurrentArchitecture().getPersonsExcludingMayor(), TabListGameFrame.Selection.MULTIPLE,
                    selectedItems -> {
                        for (GameObject i : selectedItems) {
                            Person p = (Person) i;
                            p.setDoingWork(Person.DoingWork.AGRICULTURE);
                        }
                        updateInternal();
                    });
            agriculture.setState(StateTexture.State.NORMAL);
        } else if (commercePos.contains(x, y)) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.COMMERCE), TabListGameFrame.ListKindType.PERSON,
                    parent.getCurrentArchitecture().getPersonsExcludingMayor(), TabListGameFrame.Selection.MULTIPLE,
                    selectedItems -> {
                        for (GameObject i : selectedItems) {
                            Person p = (Person) i;
                            p.setDoingWork(Person.DoingWork.COMMERCE);
                        }
                        updateInternal();
                    });
            commerce.setState(StateTexture.State.NORMAL);
        } else if (technologyPos.contains(x, y)) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.TECHNOLOGY), TabListGameFrame.ListKindType.PERSON,
                    parent.getCurrentArchitecture().getPersonsExcludingMayor(), TabListGameFrame.Selection.MULTIPLE,
                    selectedItems -> {
                        for (GameObject i : selectedItems) {
                            Person p = (Person) i;
                            p.setDoingWork(Person.DoingWork.TECHNOLOGY);
                        }
                        updateInternal();
                    });
            technology.setState(StateTexture.State.NORMAL);
        } else if (moralePos.contains(x, y)) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_MORALE), TabListGameFrame.ListKindType.PERSON,
                    parent.getCurrentArchitecture().getPersonsExcludingMayor(), TabListGameFrame.Selection.MULTIPLE,
                    selectedItems -> {
                        for (GameObject i : selectedItems) {
                            Person p = (Person) i;
                            p.setDoingWork(Person.DoingWork.MORALE);
                        }
                        updateInternal();
                    });
            morale.setState(StateTexture.State.NORMAL);
        } else if (endurancePos.contains(x, y)) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.ARCHITECTURE_ENDURANCE), TabListGameFrame.ListKindType.PERSON,
                    parent.getCurrentArchitecture().getPersonsExcludingMayor(), TabListGameFrame.Selection.MULTIPLE,
                    selectedItems -> {
                        for (GameObject i : selectedItems) {
                            Person p = (Person) i;
                            p.setDoingWork(Person.DoingWork.ENDURANCE);
                        }
                        updateInternal();
                    });
            endurance.setState(StateTexture.State.NORMAL);
        }
    }
}
