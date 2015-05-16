package com.zhsan.gamecomponents.contextmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.textwidget.StateBackgroundTextWidget;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gameobject.Architecture;
import com.zhsan.gameobject.GameScenario;
import com.zhsan.screen.GameScreen;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

/**
 * Created by Peter on 29/3/2015.
 */
public class ContextMenu extends WidgetGroup {

    public enum MenuKindType {
        SYSTEM_MENU("SystemMenu", GameScenario.class),
        ARCHITECTURE_LEFT_MENU("ArchitectureLeftClick", Architecture.class),
        ARCHITECTURE_RIGHT_MENU("ArchitectureRightClick", Architecture.class),
        MAP_RIGHT_MENU("MapRightClick", GameScenario.class)
        ;

        public final String xmlName;
        public final Class<?> carryingObj;
        MenuKindType(String xmlName, Class<?> carryingObj) {
            this.xmlName = xmlName;
            this.carryingObj = carryingObj;
        }

        public static final MenuKindType fromXmlName(String name) {
            for (MenuKindType type : MenuKindType.values()) {
                if (type.xmlName.equals(name)) {
                    return type;
                }
            }
            return null;
        }
    }

    public static final String RES_PATH = Paths.RESOURCES + "ContextMenu" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private static class MenuItem {
        private String fullName;
        private String displayName;
        private String oppositeDisplayName;
        @Nullable private String enabledMethodName;
        @Nullable private String oppositeMethodName;
        private boolean showDisabled;
        private List<MenuItem> children;
        private boolean expanded = false;
        private int depth;

        private StateBackgroundTextWidget<MenuItem> textWidget;
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

    private Texture hasChild;
    private int hasChildMargin;

    private Sound clickSound, expandSound, collapseSound;

    private EnumMap<MenuKindType, MenuKind> menuKinds = new EnumMap<>(MenuKindType.class);

    private GameScreen screen;

    private Point position;
    private boolean centerAtScreen;

    private MenuKindType showingType;

    private Object currentObject;

    private List<MenuItem> loadMenuItem(Node node, String parentName, StateTexture background, TextWidget<MenuItem> widgetTemplate, int depth) {
        NodeList itemNodes = node.getChildNodes();
        List<MenuItem> items = new ArrayList<>();

        for (int i = 0; i < itemNodes.getLength(); ++i) {
            MenuItem item = new MenuItem();
            Node itemNode = itemNodes.item(i);
            if (itemNode instanceof Text) continue;

            item.fullName = parentName + "_" + XmlHelper.loadAttribute(itemNode, "Name");
            item.displayName = XmlHelper.loadAttribute(itemNode, "DisplayName");
            item.enabledMethodName = XmlHelper.loadAttribute(itemNode, "DisplayIfTrue", null);
            item.showDisabled = Boolean.parseBoolean(XmlHelper.loadAttribute(itemNode, "DisplayAll", null));
            item.oppositeDisplayName = XmlHelper.loadAttribute(itemNode, "OppositeName", item.displayName);
            item.oppositeDisplayName = XmlHelper.loadAttribute(itemNode, "OppositeIfTrue", item.oppositeMethodName);
            item.textWidget = new StateBackgroundTextWidget<>(widgetTemplate, background);
            item.textWidget.addListener(new MenuItemListener(item.textWidget));
            item.depth = depth;
            item.children = loadMenuItem(itemNode, item.fullName, background, widgetTemplate, depth + 1);

            this.addActor(item.textWidget);
            item.textWidget.setVisible(false);

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
            StateTexture menuRight = StateTexture.fromXml(DATA_PATH, rightClickNode);
            TextWidget<MenuItem> menuRightText = new TextWidget<>(TextWidget.Setting.fromXml(rightClickNode));

            Node leftClickNode = dom.getElementsByTagName("ContextMenuLeftClick").item(0);
            StateTexture menuLeft = StateTexture.fromXml(DATA_PATH, leftClickNode);
            TextWidget<MenuItem> menuLeftText = new TextWidget<>(TextWidget.Setting.fromXml(leftClickNode));

            Node hasChildNode = dom.getElementsByTagName("HasChildTexture").item(0);
            hasChild = new Texture(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(hasChildNode, "FileName")));
            hasChildMargin = Integer.parseInt(XmlHelper.loadAttribute(hasChildNode, "Margin"));

            Node soundNode = dom.getElementsByTagName("SoundFile").item(0);
            clickSound = Gdx.audio.newSound(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(soundNode, "Click")));
            expandSound = Gdx.audio.newSound(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(soundNode, "Open")));
            collapseSound = Gdx.audio.newSound(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(soundNode, "Fold")));

            NodeList nodeList = dom.getElementsByTagName("MenuKindList").item(0).getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node kindNode = nodeList.item(i);
                if (kindNode instanceof Text) continue;

                String name = XmlHelper.loadAttribute(kindNode, "Name");

                MenuKind menuKind = new MenuKind();
                menuKind.name = name;
                menuKind.displayName = XmlHelper.loadAttribute(kindNode, "DisplayName");
                menuKind.isByLeftClick = Boolean.parseBoolean(XmlHelper.loadAttribute(kindNode, "IsLeft"));
                menuKind.width = Integer.parseInt(XmlHelper.loadAttribute(kindNode, "Width"));
                menuKind.height = Integer.parseInt(XmlHelper.loadAttribute(kindNode, "Height"));
                menuKind.showDisabled = Boolean.parseBoolean(XmlHelper.loadAttribute(kindNode, "DisplayAll", "True"));

                if (menuKind.isByLeftClick) {
                    menuKind.items = loadMenuItem(kindNode, menuKind.name, menuLeft, menuLeftText, 1);
                } else {
                    menuKind.items = loadMenuItem(kindNode, menuKind.name, menuRight, menuRightText, 1);
                }

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

        this.setVisible(false);
        this.addListener(new ScreenClickListener());

        loadXml();
    }

    public void resize(int width, int height) {
        float widthExpanded = width / this.getWidth();
        float heightExpanded = height / this.getHeight();

        if (position != null) {
            position = new Point(MathUtils.round(position.x * widthExpanded + getX()), MathUtils.round(position.y * heightExpanded + getY()));
        }
    }

    public void show(MenuKindType type, Object object, Point position) {
        if (!type.carryingObj.isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException("MenuKindType " + type + " can only accept an object of type "
                + type.carryingObj + ". " + object.getClass() + " received.");
        }
        dismiss();
        this.currentObject = object;
        this.showingType = type;
        this.position = position == null ? null : new Point((int)(position.x + getX()), (int)(position.y + getY()));
        this.setVisible(true);
        if (showingType != null) {
            clickSound.play();
        }
    }

    private void drawMenuItem_r(Batch batch, float parentAlpha, MenuKind kind, MenuItem item, int depth, int index, int lastSize) {
        if (item.expanded) {
            drawMenu(batch, parentAlpha, kind.width * depth, -kind.height * (lastSize - index - 1), kind, item.children, depth);

            int innerIndex = item.children.size() - 1;
            for (MenuItem inner : item.children) {
                drawMenuItem_r(batch, parentAlpha, kind, inner, depth + 1, innerIndex, item.children.size());
                innerIndex--;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (showingType != null) {
            MenuKind kind = menuKinds.get(showingType);
            drawMenu(batch, parentAlpha, 0, 0, kind, kind.items, 0);

            int index = kind.items.size() - 1;
            for (MenuItem item : kind.items) {
                drawMenuItem_r(batch, parentAlpha, kind, item, 1, index, kind.items.size());
                index--;
            }
        }

        super.draw(batch, parentAlpha);
    }

    private void drawMenu(Batch batch, float parentAlpha, int xOffset, int yOffset, MenuKind kind, List<MenuItem> items, int submenuDepth) {
        int width = kind.width;
        int height = kind.height * items.size();
        Rectangle bound;
        if (position == null) {
            bound = new Rectangle(this.getWidth() / 2 - width / 2 + getX(), this.getHeight() / 2 - height / 2 + getY(), width, height);
        } else {
            int px = position.x + xOffset;
            int py = position.y + yOffset;
            if (px + width > getWidth()) {
                boolean rootLeft = getX() + position.x + width > getWidth();
                if (py - height < getY()) {
                    bound = new Rectangle(px - width * (submenuDepth * 2 + (rootLeft ? 1 : 0)), py - (submenuDepth > 0 ? kind.height : 0), width, height);
                } else {
                    bound = new Rectangle(px - width * (submenuDepth * 2 + (rootLeft ? 1 : 0)), py - height, width, height);
                }
            } else {
                if (py - height < getY()) {
                    bound = new Rectangle(px, py - (submenuDepth > 0 ? kind.height : 0), width, height);
                } else {
                    bound = new Rectangle(px, py - height, width, height);
                }
            }
        }

        for (int i = 0; i < items.size(); ++i) {
            // TODO disabled method, showAll
            float x = bound.getX();
            float y = bound.getY() + bound.getHeight() - (i + 1) * kind.height;

            TextWidget<MenuItem> widget = items.get(i).textWidget;
            widget.setText(items.get(i).displayName);
            widget.setPosition(x - getX(), y - getY());
            widget.setSize(kind.width, kind.height);
            widget.setExtra(items.get(i));
            widget.setVisible(true);

            if (items.get(i).children.size() > 0 &&
                    items.get(i).textWidget.getBackground().getState() == StateTexture.State.SELECTED) {
                batch.draw(hasChild,
                        x - getX() + kind.width + hasChildMargin, y + kind.height / 2 - hasChild.getHeight() / 2);
            }
        }
    }

    private void disposeMenuItems(List<MenuItem> items) {
        for (MenuItem item : items) {
            item.textWidget.dispose();
            disposeMenuItems(item.children);
        }
    }

    public void dispose() {
        hasChild.dispose();
        clickSound.dispose();
        expandSound.dispose();
        collapseSound.dispose();

        for (MenuKind kind : menuKinds.values()) {
            disposeMenuItems(kind.items);
        }
    }

    private void dismiss() {
        collapse();
        showingType = null;
        setVisible(false);
    }

    private void collapse_r(MenuItem item) {
        item.expanded = false;
        item.textWidget.setVisible(false);
        item.children.forEach(this::collapse_r);
    }

    private void collapse() {
        if (menuKinds != null && showingType != null && menuKinds.get(showingType) != null) {
            menuKinds.get(showingType).items.forEach(this::collapse_r);
        }
    }

    private MenuItem getExpandedItem(MenuItem item, int maxDepth, int currentDepth) {
        if (maxDepth > currentDepth) {
            for (MenuItem i : item.children) {
                MenuItem r = getExpandedItem(i, maxDepth, currentDepth + 1);
                if (r != null) {
                    return r;
                }
            }
        }
        if (item.expanded) {
            return item;
        }
        return null;
    }

    private boolean containDescendent(MenuItem r, MenuItem s) {
        if (r.children.contains(s)) {
            return true;
        }
        for (MenuItem item : r.children) {
            if (containDescendent(item, s)) {
                return true;
            }
        }
        return false;
    }

    private class MenuItemListener extends InputListener {

        private TextWidget<MenuItem> widget;

        public MenuItemListener(TextWidget<MenuItem> widget) {
            this.widget = widget;
        }

        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            this.widget.getExtra().textWidget.getBackground().setState(StateTexture.State.SELECTED);
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            this.widget.getExtra().textWidget.getBackground().setState(StateTexture.State.NORMAL);
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (button == Input.Buttons.LEFT) {
                if (this.widget.getExtra().children.size() > 0) {
                    MenuKind kind = menuKinds.get(showingType);
                    for (MenuItem item : kind.items) {
                        MenuItem r = getExpandedItem(item, this.widget.getExtra().depth, 1);
                        if (r != null && !containDescendent(r, this.widget.getExtra())) {
                            collapse_r(r);
                        }
                    }

                    this.widget.getExtra().expanded = true;
                    expandSound.play();
                } else {
                    clickSound.play();
                    dismiss();
                    try {
                        String name = this.widget.getExtra().fullName;
                        
                        ContextMenuMethods.class.getMethod(this.widget.getExtra().fullName, GameScreen.class, Object.class)
                                .invoke(null, screen, currentObject);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        // throw new IllegalArgumentException(e);
                    }
                }

                return true;
            }
            return false;
        }
    }

    private class ScreenClickListener extends InputListener {

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (button == Input.Buttons.RIGHT) {
                collapseSound.play();

                MenuKind kind = menuKinds.get(showingType);
                for (MenuItem item : kind.items) {
                    MenuItem r = getExpandedItem(item, Integer.MAX_VALUE, 1);
                    if (r != null) {
                        collapse_r(r);
                        return true;
                    }
                }

                dismiss();
                return true;
            }
            return false;
        }
    }

}
