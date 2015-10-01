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
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.gamecomponents.gameframe.TabListGameFrame;
import com.zhsan.gameobject.*;
import com.zhsan.gamecomponents.GlobalStrings;
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

    static class TextType {
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

    public static final String RES_PATH = CommandFrame.RES_PATH + "Architecture" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    private StateTexture internal, military, officer, tactics, facility;
    private Rectangle internalPos, militaryPos, officerPos, tacticsPos, facilityPos;

    private TabType currentTab = TabType.INTERNAL;

    private Architecture currentArchitecture;

    private EnumMap<TabType, CommandTab> tabs = new EnumMap<>(TabType.class);

    public static final void loadText(Node n, List<TextWidget<ArchitectureCommandFrame.TextType>> textWidgets) {
        if (n.getNodeName().equals("StaticText")) {
            TextWidget<ArchitectureCommandFrame.TextType> widget = new TextWidget<>(TextWidget.Setting.fromXml(n));
            widget.setExtra(new ArchitectureCommandFrame.TextType(
                    XmlHelper.loadRectangleFromXmlOptSize(n),
                    true,
                    XmlHelper.loadAttribute(n, "Text"),
                    widget.getColor()
            ));
            widget.setSize(widget.getExtra().position.width, widget.getExtra().position.height);
            textWidgets.add(widget);
        } else if (n.getNodeName().equals("DetailText")) {
            TextWidget<ArchitectureCommandFrame.TextType> widget = new TextWidget<>(TextWidget.Setting.fromXml(n));
            widget.setExtra(new ArchitectureCommandFrame.TextType(
                    XmlHelper.loadRectangleFromXmlOptSize(n),
                    false,
                    XmlHelper.loadAttribute(n, "Field"),
                    widget.getColor()
            ));
            widget.setSize(widget.getExtra().position.width, widget.getExtra().position.height);
            textWidgets.add(widget);
        }
    }

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "ArchitectureCommandFrameData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

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
            CommandTab internalTab = new InternalCommandTab(this);
            internalTab.loadXml(internalNodes);
            tabs.put(TabType.INTERNAL, internalTab);

            NodeList militaryNodes = dom.getElementsByTagName("MilitaryTab").item(0).getChildNodes();
            CommandTab militaryTab = new MilitaryCommandTab(this);
            militaryTab.loadXml(militaryNodes);
            tabs.put(TabType.MILITARY, militaryTab);

            NodeList officerNodes = dom.getElementsByTagName("OfficerTab").item(0).getChildNodes();
            CommandTab officerTab = new OfficerCommandTab(this);
            officerTab.loadXml(officerNodes);
            tabs.put(TabType.OFFICER, officerTab);

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
        tabs.values().forEach(CommandTab::invalidate);
        this.setVisible(true);
    }

    public void hide() {
        this.setVisible(false);
        currentArchitecture = null;
    }

    public void invalidateData() {
        tabs.values().forEach(CommandTab::invalidate);
    }

    Architecture getCurrentArchitecture() {
        return currentArchitecture;
    }

    GameScreen getScreen() {
        return screen;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        tabs.get(currentTab).drawBackground(batch, parentAlpha);

        batch.draw(internal.get(), getX() + internalPos.x, getY() + internalPos.y, internalPos.width, internalPos.height);
        batch.draw(military.get(), getX() + militaryPos.x, getY() + militaryPos.y, militaryPos.width, militaryPos.height);
        batch.draw(officer.get(), getX() + officerPos.x, getY() + officerPos.y, officerPos.width, officerPos.height);
        batch.draw(tactics.get(), getX() + tacticsPos.x, getY() + tacticsPos.y, tacticsPos.width, tacticsPos.height);
        batch.draw(facility.get(), getX() + facilityPos.x, getY() + facilityPos.y, facilityPos.width, facilityPos.height);

        tabs.get(currentTab).draw(batch, parentAlpha);
    }

    public void dispose() {
        super.dispose();
        internal.dispose();
        military.dispose();
        officer.dispose();
        tactics.dispose();
        facility.dispose();
        tabs.values().forEach(CommandTab::dispose);
    }

    public class Listener extends InputListener {
        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            if (currentArchitecture.getBelongedFaction() == screen.getScenario().getCurrentPlayer()) {
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

                tabs.get(currentTab).onMouseMove(x, y);
            }

            return false;
        }

        private void unselectAllTabs() {
            tabs.values().forEach(CommandTab::onUnselect);
            internal.setState(StateTexture.State.NORMAL);
            military.setState(StateTexture.State.NORMAL);
            officer.setState(StateTexture.State.NORMAL);
            tactics.setState(StateTexture.State.NORMAL);
            facility.setState(StateTexture.State.NORMAL);
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (internalPos.contains(x, y)) {
                currentTab = TabType.INTERNAL;
                unselectAllTabs();
                internal.setState(StateTexture.State.SELECTED);
            } else if (militaryPos.contains(x, y)){
                currentTab = TabType.MILITARY;
                unselectAllTabs();
                military.setState(StateTexture.State.SELECTED);
            } else if (officerPos.contains(x, y)) {
                currentTab = TabType.OFFICER;
                unselectAllTabs();
                officer.setState(StateTexture.State.SELECTED);
            }

            if (currentArchitecture.getBelongedFaction() == screen.getScenario().getCurrentPlayer() && !screen.getDayRunner().isDayRunning()) {
                tabs.get(currentTab).onClick(x, y);
            }

            return false;
        }
    }

}
