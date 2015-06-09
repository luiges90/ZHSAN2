package com.zhsan.gamecomponents.commandframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.zhsan.common.GlobalVariables;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.gameobject.Architecture;
import com.zhsan.gameobject.Faction;
import com.zhsan.screen.GameScreen;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Peter on 2/6/2015.
 */
public class ArchitectureCommandFrame extends CommandFrame {

    private enum TabType {
        INTERNAL, MILITARY, OFFICER, TACTICS, FACILITY
    }

    private class TextType {
        public final Rectangle position;
        public final boolean staticText;
        public final String name;
        public final Color fallingColor;

        public TextType(Rectangle position, boolean staticText, String name, Color fallingColor) {
            this.position = position;
            this.staticText = staticText;
            this.name = name;
            this.fallingColor = fallingColor;
        }
    }

    private class InternalPortraitType {
        public final Rectangle position;
        public final Color borderColor;
        public final String name;

        public InternalPortraitType(Rectangle position, Color borderColor, String name) {
            this.position = position;
            this.borderColor = borderColor;
            this.name = name;
        }
    }

    public static final String RES_PATH = CommandFrame.RES_PATH + "Architecture" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    private StateTexture internal, military, officer, tactics, facility;
    private Rectangle internalPos, militaryPos, officerPos, tacticsPos, facilityPos;

    private Texture portraitBorder;

    private Rectangle mayorPortraitPos;

    private StateTexture assign, commerce, agriculture, technology, morale, endurance;
    private Rectangle assignPos, commercePos, agriculturePos, technologyPos, moralePos, endurancePos;

    private Rectangle factionColor;
    private Color factionBorder;

    private List<TextWidget<TextType>> textWidgets = new ArrayList<>();
    private List<InternalPortraitType> internalPortraits = new ArrayList<>();

    private EnumMap<TabType, Texture> backgrounds = new EnumMap<>(TabType.class);
    private TabType currentTab = TabType.INTERNAL;

    private Architecture currentArchitecture;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "ArchitectureCommandFrameData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            portraitBorder = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(
                dom.getElementsByTagName("PortraitBorder").item(0), "FileName"
            )));

            NodeList tabNodes = dom.getElementsByTagName("Tabs").item(0).getChildNodes();
            for (int i = 0; i < tabNodes.getLength(); ++i) {
                Node n = tabNodes.item(i);
                if (n.getNodeName().equals("Internal")) {
                    internal = StateTexture.fromXml(DATA_PATH, n);
                    internalPos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Military")) {
                    military = StateTexture.fromXml(DATA_PATH, n);
                    militaryPos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Officer")) {
                    officer = StateTexture.fromXml(DATA_PATH, n);
                    officerPos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Tactics")) {
                    tactics = StateTexture.fromXml(DATA_PATH, n);
                    tacticsPos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Facility")) {
                    facility = StateTexture.fromXml(DATA_PATH, n);
                    facilityPos = XmlHelper.loadRectangleFromXml(n);
                }
            }

            NodeList internalNodes = dom.getElementsByTagName("InternalTab").item(0).getChildNodes();
            for (int i = 0; i < internalNodes.getLength(); ++i) {
                Node n = internalNodes.item(i);
                if (n.getNodeName().equals("Background")) {
                    backgrounds.put(TabType.INTERNAL, new Texture(Gdx.files.external(
                            DATA_PATH + XmlHelper.loadAttribute(n, "FileName")
                    )));
                } else if (n.getNodeName().equals("MayorPortrait")) {
                    mayorPortraitPos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Assign")) {
                    assign = StateTexture.fromXml(DATA_PATH, n);
                    assignPos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Agriculture")) {
                    agriculture = StateTexture.fromXml(DATA_PATH, n);
                    agriculturePos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Commerce")) {
                    commerce = StateTexture.fromXml(DATA_PATH, n);
                    commercePos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Technology")) {
                    technology = StateTexture.fromXml(DATA_PATH, n);
                    technologyPos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Morale")) {
                    morale = StateTexture.fromXml(DATA_PATH, n);
                    moralePos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Endurance")) {
                    endurance = StateTexture.fromXml(DATA_PATH, n);
                    endurancePos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("FactionColor")) {
                    factionColor = XmlHelper.loadRectangleFromXml(n);
                    factionBorder = XmlHelper.loadColorFromXml(Integer.parseUnsignedInt(XmlHelper.loadAttribute(n, "BorderColor")));
                } else if (n.getNodeName().equals("StaticText")) {
                    TextWidget<TextType> widget = new TextWidget<>(TextWidget.Setting.fromXml(n));
                    widget.setExtra(new TextType(
                            XmlHelper.loadRectangleFromXmlOptSize(n),
                            true,
                            XmlHelper.loadAttribute(n, "Text"),
                            widget.getColor()
                    ));
                    widget.setSize(widget.getExtra().position.width, widget.getExtra().position.height);
                    textWidgets.add(widget);
                } else if (n.getNodeName().equals("DetailText")) {
                    TextWidget<TextType> widget = new TextWidget<>(TextWidget.Setting.fromXml(n));
                    widget.setExtra(new TextType(
                            XmlHelper.loadRectangleFromXmlOptSize(n),
                            false,
                            XmlHelper.loadAttribute(n, "Field"),
                            widget.getColor()
                    ));
                    widget.setSize(widget.getExtra().position.width, widget.getExtra().position.height);
                    textWidgets.add(widget);
                } else if (n.getNodeName().equals("InternalDevelopRate")) {
                    TextWidget<TextType> widget = new TextWidget<>(TextWidget.Setting.fromXml(n));
                    widget.setExtra(new TextType(
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
                            XmlHelper.loadAttribute(n, "Field")
                    ));
                }
            }
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "ArchitectureCommandFrameData.xml", e);
        }
    }

    public ArchitectureCommandFrame(GameScreen screen) {
        super();

        loadXml();

        this.screen = screen;

        this.addListener(new Listener());
        internal.setState(StateTexture.State.SELECTED);
    }

    public void show(@NotNull Architecture architecture) {
        currentArchitecture = architecture;
        this.setVisible(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(backgrounds.get(currentTab), getX(), getY(), getWidth(), getHeight());

        batch.draw(internal.get(), getX() + internalPos.x, getY() + internalPos.y, internalPos.width, internalPos.height);
        batch.draw(military.get(), getX() + militaryPos.x, getY() + militaryPos.y, militaryPos.width, militaryPos.height);
        batch.draw(officer.get(), getX() + officerPos.x, getY() + officerPos.y, officerPos.width, officerPos.height);
        batch.draw(tactics.get(), getX() + tacticsPos.x, getY() + tacticsPos.y, tacticsPos.width, tacticsPos.height);
        batch.draw(facility.get(), getX() + facilityPos.x, getY() + facilityPos.y, facilityPos.width, facilityPos.height);

        batch.draw(portraitBorder, getX() + mayorPortraitPos.x, getY() + mayorPortraitPos.y, mayorPortraitPos.width, mayorPortraitPos.height);

        batch.draw(assign.get(), getX() + assignPos.x, getY() + assignPos.y, assignPos.width, assignPos.height);
        batch.draw(agriculture.get(), getX() + agriculturePos.x, getY() + agriculturePos.y, agriculturePos.width, agriculturePos.height);
        batch.draw(commerce.get(), getX() + commercePos.x, getY() + commercePos.y, commercePos.width, commercePos.height);
        batch.draw(technology.get(), getX() + technologyPos.x, getY() + technologyPos.y, technologyPos.width, technologyPos.height);
        batch.draw(morale.get(), getX() + moralePos.x, getY() + moralePos.y, moralePos.width, moralePos.height);
        batch.draw(endurance.get(), getX() + endurancePos.x, getY() + endurancePos.y, endurancePos.width, endurancePos.height);

//        batch.end();
//
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//
//        Faction f = currentArchitecture.getBelongedFaction();
//        shapeRenderer.setColor(f == null ? GlobalVariables.blankColor : f.getColor());
//        shapeRenderer.rect(getX() + factionColor.getX(), getY() + factionColor.getY(),
//                factionColor.getWidth(), factionColor.getHeight());
//
//        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
//
//        shapeRenderer.setColor(factionBorder);
//        shapeRenderer.rect(getX() + factionColor.getX(), getY() + factionColor.getY(),
//                factionColor.getWidth(), factionColor.getHeight());
//
//        shapeRenderer.end();
//
//        batch.begin();

        for (TextWidget<TextType> textWidget : textWidgets) {
            textWidget.setPosition(textWidget.getExtra().position.x + getX(), textWidget.getExtra().position.y + getY());
            if (textWidget.getExtra().staticText) {
                textWidget.setText(textWidget.getExtra().name);
            } else {
                textWidget.setText(String.valueOf(currentArchitecture.getField(textWidget.getExtra().name)));
            }
            textWidget.draw(batch, parentAlpha);
        }

        for (InternalPortraitType internalPortraitType : internalPortraits) {
            batch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.setColor(internalPortraitType.borderColor);
            shapeRenderer.rect(getX() + internalPortraitType.position.x, getY() + internalPortraitType.position.y,
                    internalPortraitType.position.width, internalPortraitType.position.height);

            shapeRenderer.end();

            batch.begin();
        }
    }

    public void dispose() {
        super.dispose();
    }

    public class Listener extends InputListener {
        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            if (internalPos.contains(x, y)) {
                internal.setState(StateTexture.State.SELECTED);
            } else {
                internal.setState(currentTab == TabType.INTERNAL ? StateTexture.State.SELECTED : StateTexture.State.NORMAL);
            }
            if (militaryPos.contains(x, y)) {
                military.setState(StateTexture.State.SELECTED);
            } else {
                military.setState(currentTab == TabType.MILITARY ? StateTexture.State.SELECTED : StateTexture.State.NORMAL);
            }
            if (officerPos.contains(x, y)) {
                officer.setState(StateTexture.State.SELECTED);
            } else {
                officer.setState(currentTab == TabType.OFFICER ? StateTexture.State.SELECTED : StateTexture.State.NORMAL);
            }
            if (tacticsPos.contains(x, y)) {
                tactics.setState(StateTexture.State.SELECTED);
            } else {
                tactics.setState(currentTab == TabType.TACTICS ? StateTexture.State.SELECTED : StateTexture.State.NORMAL);
            }
            if (facilityPos.contains(x, y)) {
                facility.setState(StateTexture.State.SELECTED);
            } else {
                facility.setState(currentTab == TabType.FACILITY ? StateTexture.State.SELECTED : StateTexture.State.NORMAL);
            }

            if (assignPos.contains(x, y)) {
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

            return false;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {


            return false;
        }
    }

}
