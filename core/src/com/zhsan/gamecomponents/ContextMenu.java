package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.TextWidget;
import com.zhsan.gamecomponents.toolbar.ToolBar;
import com.zhsan.screen.GameScreen;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 29/3/2015.
 */
public class ContextMenu extends WidgetGroup {

    public enum MenuKindTypes {
        SYSTEM_MENU("SystemMenu");

        public final String xmlName;
        MenuKindTypes(String xmlName) {
            this.xmlName = xmlName;
        }
    }

    public static final String RES_PATH = ToolBar.RES_PATH + "ContextMenu" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private static class MenuItem {
        private String name;
        private String displayName;
        private String oppositeName;
        @Nullable private String enabledMethodName;
        @Nullable private String oppositeMethodName;
        private boolean showDisabled;
        private List<MenuItem> children;
    }

    private static class MenuKind {
        private String name;
        private String displayName;
        private boolean triggeredByLeftClick;
        private int width;
        private int height;
        private boolean showDisabled;
        private List<MenuItem> items;
    }

    private StateTexture menuLeft, menuRight;
    private TextWidget<Void> menuLeftText, menuRightText;
    private Texture hasChild;
    private Sound clickSound, expandSound, collapseSound;

    private MenuKind menuKind;

    private GameScreen screen;
    private MenuKindTypes menuKindType;

    private Point position;
    private boolean centerAtScreen;

    private List<MenuItem> loadMenuItem(Node node) {
        NodeList itemNodes = node.getChildNodes();
        List<MenuItem> items = new ArrayList<>();

        for (int i = 0; i < itemNodes.getLength(); ++i) {
            MenuItem item = new MenuItem();
            Node itemNode = itemNodes.item(i);
            item.name = itemNode.getAttributes().getNamedItem("Name").getNodeValue();
            item.displayName = itemNode.getAttributes().getNamedItem("DisplayName").getNodeValue();

            Node enableAttr = itemNode.getAttributes().getNamedItem("DisplayIfTrue");
            if (enableAttr != null) {
                item.enabledMethodName = enableAttr.getNodeValue();
            } else {
                item.enabledMethodName = null;
            }

            Node showDisabledNode = itemNode.getAttributes().getNamedItem("DisplayAll");
            if (showDisabledNode != null) {
                item.showDisabled = Boolean.parseBoolean(showDisabledNode.getNodeValue());
            } else {
                item.showDisabled = true;
            }

            Node oppositeNode = itemNode.getAttributes().getNamedItem("OppositeName");
            if (oppositeNode != null) {
                item.oppositeName = oppositeNode.getNodeValue();
            } else {
                item.oppositeName = item.displayName;
            }

            Node oppositeMethodNode = itemNode.getAttributes().getNamedItem("OppositeIfTrue");
            if (oppositeMethodNode != null) {
                item.oppositeMethodName = oppositeMethodNode.getNodeValue();
            } else {
                item.oppositeMethodName = null;
            }

            item.children = loadMenuItem(node);

            items.add(item);
        }

        return items;
    }

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "ContextMenuData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node rightClickNode = dom.getElementsByTagName("ContextMenuRightClick").item(0);
            menuRight = StateTexture.fromXml(DATA_PATH, rightClickNode);
            menuRightText = new TextWidget<>(TextWidget.Setting.fromXml(rightClickNode));

            Node leftClickNode = dom.getElementsByTagName("ContextMenuLeftClick").item(0);
            menuLeft = StateTexture.fromXml(DATA_PATH, leftClickNode);
            menuLeftText = new TextWidget<>(TextWidget.Setting.fromXml(leftClickNode));

            hasChild = new Texture(Gdx.files.external(DATA_PATH +
                    dom.getElementsByTagName("HasChildTexture").item(0).getAttributes().getNamedItem("FileName").getNodeValue()));

            Node soundNode = dom.getElementsByTagName("SoundFile").item(0);
            clickSound = Gdx.audio.newSound(Gdx.files.external(DATA_PATH +
                                soundNode.getAttributes().getNamedItem("Click").getNodeValue()));
            expandSound = Gdx.audio.newSound(Gdx.files.external(DATA_PATH +
                                soundNode.getAttributes().getNamedItem("Open").getNodeValue()));
            collapseSound = Gdx.audio.newSound(Gdx.files.external(DATA_PATH +
                                soundNode.getAttributes().getNamedItem("Fold").getNodeValue()));

            NodeList nodeList = dom.getElementsByTagName("MenuKindList").item(0).getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node kindNode = nodeList.item(i);
                String name = kindNode.getAttributes().getNamedItem("Name").getNodeValue();

                if (name.equalsIgnoreCase(menuKindType.xmlName)) {
                    menuKind = new MenuKind();
                    menuKind.name = name;
                    menuKind.displayName = kindNode.getAttributes().getNamedItem("DisplayName").getNodeValue();
                    menuKind.triggeredByLeftClick = Boolean.parseBoolean(kindNode.getAttributes().getNamedItem("IsLeft").getNodeValue());
                    menuKind.width = Integer.parseInt(kindNode.getAttributes().getNamedItem("Width").getNodeValue());
                    menuKind.height = Integer.parseInt(kindNode.getAttributes().getNamedItem("Height").getNodeValue());
                    menuKind.showDisabled = Boolean.parseBoolean(kindNode.getAttributes().getNamedItem("DisplayAll").getNodeValue());
                    menuKind.items = loadMenuItem(kindNode);
                }
            }
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "ContextMenuData.xml", e);
        }
    }

    /**
     *
     * @param screen
     * @param menuKindName
     * @param position anchor point. if null, the menu will center at game screen
     */
    public ContextMenu(GameScreen screen, MenuKindTypes menuKindName, @Nullable Point position) {
        this.screen = screen;
        this.menuKindType = menuKindName;

        if (position != null) {
            this.position = position;
            this.centerAtScreen = false;
        } else {
            this.centerAtScreen = true;
        }

        loadXml();
    }

    public void dispose() {
        menuLeft.dispose();
        menuLeftText.dispose();
        menuRight.dispose();
        menuRightText.dispose();
        hasChild.dispose();
        clickSound.dispose();
        expandSound.dispose();
        collapseSound.dispose();
    }

}
