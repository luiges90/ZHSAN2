package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
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
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Peter on 29/3/2015.
 */
public class ContextMenu extends WidgetGroup {

    public enum MenuKindType {
        SYSTEM_MENU("SystemMenu");

        public final String xmlName;
        MenuKindType(String xmlName) {
            this.xmlName = xmlName;
        }

        public static final MenuKindType fromXmlName(String name) {
            switch (name) {
                case "SystemMenu": return SYSTEM_MENU;
            }
            return null;
        }
    }

    public static final String RES_PATH = Paths.RESOURCES + "ContextMenu" + File.separator;
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
        private boolean isByLeftClick;
        private int width;
        private int height;
        private boolean showDisabled;
        private List<MenuItem> items;
    }

    private StateTexture menuLeft, menuRight;
    private TextWidget<Void> menuLeftText, menuRightText;
    private Texture hasChild;
    private Sound clickSound, expandSound, collapseSound;

    private EnumMap<MenuKindType, MenuKind> menuKinds;

    private GameScreen screen;

    private Point position;
    private boolean centerAtScreen;

    private MenuKindType showingType;

    private List<MenuItem> loadMenuItem(Node node) {
        NodeList itemNodes = node.getChildNodes();
        List<MenuItem> items = new ArrayList<>();

        for (int i = 0; i < itemNodes.getLength(); ++i) {
            MenuItem item = new MenuItem();
            Node itemNode = itemNodes.item(i);
            if (itemNode instanceof Text) continue;

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

            item.children = loadMenuItem(itemNode);

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
                if (kindNode instanceof Text) continue;

                String name = kindNode.getAttributes().getNamedItem("Name").getNodeValue();

                MenuKind menuKind = new MenuKind();
                menuKind.name = name;
                menuKind.displayName = kindNode.getAttributes().getNamedItem("DisplayName").getNodeValue();
                menuKind.isByLeftClick = Boolean.parseBoolean(kindNode.getAttributes().getNamedItem("IsLeft").getNodeValue());
                menuKind.width = Integer.parseInt(kindNode.getAttributes().getNamedItem("Width").getNodeValue());
                menuKind.height = Integer.parseInt(kindNode.getAttributes().getNamedItem("Height").getNodeValue());

                Node nodeShowDisabled = kindNode.getAttributes().getNamedItem("DisplayAll");
                if (nodeShowDisabled != null) {
                    menuKind.showDisabled = Boolean.parseBoolean(nodeShowDisabled.getNodeValue());
                } else {
                    menuKind.showDisabled = true;
                }
                menuKind.items = loadMenuItem(kindNode);

                MenuKindType type = MenuKindType.fromXmlName(name);
                if (type != null) {
                    menuKinds.put(type, menuKind);
                }
            }
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "ContextMenuData.xml", e);
        }
    }

    /**
     *
     * @param screen
     */
    public ContextMenu(GameScreen screen) {
        this.screen = screen;

        this.setWidth(Gdx.graphics.getWidth());
        this.setHeight(Gdx.graphics.getHeight());

        this.setVisible(false);

        loadXml();
    }

    public void show(MenuKindType type, Point position) {
        showingType = type;
        this.position = position;
    }

    private static int kindMaxDepth_r(MenuItem kind, int r) {
        int result = r + 1;
        for (MenuItem item : kind.children) {
            result = Math.max(result, kindMaxDepth_r(item, result));
        }
        return result;
    }

    private static int kindMaxDepth(MenuKind kind) {
        int result = 1;
        for (MenuItem item : kind.items) {
            result = Math.max(result, kindMaxDepth_r(item, 1));
        }
        return result;
    }

    private static int kindMaxRow_r(MenuItem kind) {
        int result = kind.children.size();
        for (int i = 0; i < kind.children.size(); ++i) {
            result = Math.max(result, i + kindMaxRow_r(kind.children.get(i)));
        }
        return result;
    }

    private static int kindMaxRow(MenuKind kind) {
        int result = kind.items.size();
        for (int i = 0; i < kind.items.size(); ++i) {
            result = Math.max(result, i + kindMaxRow_r(kind.items.get(i)));
        }
        return result;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (showingType != null) {
            MenuKind kind = menuKinds.get(showingType);
            int width = kind.width * kindMaxDepth(kind);
            int height = kind.height * kindMaxRow(kind);
            Rectangle bound;
            if (position == null) {
                bound = new Rectangle(this.getWidth() / 2 - width / 2, this.getHeight() / 2 - height / 2, width, height);
            } else {
                if (position.x + width > getWidth()) {
                    if (position.y - height < 0) {
                        bound = new Rectangle(position.x - width, position.y, position.x, position.y + height);
                    } else {
                        bound = new Rectangle(position.x - width, position.y - height, position.x, position.y);
                    }
                } else {
                    if (position.y - height < 0) {
                        bound = new Rectangle(position.x, position.y, position.x + width, position.y + height);
                    } else {
                        bound = new Rectangle(position.x, position.y - height, position.x + width, height);
                    }
                }
            }
            for (int i = 0; i < kind.items.size(); ++i) {
                // TODO disabled method, showAll
                if (kind.isByLeftClick) {
                    batch.draw(menuLeft.get(), bound.getX(), bound.getY() + i * kind.height, kind.width, kind.height);
                    menuLeftText.setText(kind.items.get(i).displayName);
                    menuLeftText.draw(batch, parentAlpha);
                } else {
                    batch.draw(menuRight.get(), bound.getX(), bound.getY() + i * kind.height, kind.width, kind.height);
                    menuRightText.setText(kind.items.get(i).displayName);
                    menuRightText.draw(batch, parentAlpha);
                }
            }
        }
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
