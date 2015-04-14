package com.zhsan.gamecomponents;

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
        ARCHITECTURE_RIGHT_MENU("ArchitectureRightClick", Architecture.class)
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
    private Sound clickSound, expandSound, collapseSound;

    private EnumMap<MenuKindType, MenuKind> menuKinds = new EnumMap<>(MenuKindType.class);

    private GameScreen screen;

    private Point position;
    private boolean centerAtScreen;

    private MenuKindType showingType;

    private Object currentObject;

    private List<MenuItem> loadMenuItem(Node node, String parentName, StateTexture background, TextWidget<MenuItem> widgetTemplate) {
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
            item.children = loadMenuItem(itemNode, item.fullName, background, widgetTemplate);

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

            hasChild = new Texture(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(dom.getElementsByTagName("HasChildTexture").item(0), "FileName")));

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
                    menuKind.items = loadMenuItem(kindNode, menuKind.name, menuLeft, menuLeftText);
                } else {
                    menuKind.items = loadMenuItem(kindNode, menuKind.name, menuRight, menuRightText);
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

        this.setWidth(Gdx.graphics.getWidth());
        this.setHeight(Gdx.graphics.getHeight());

        this.setVisible(false);
        this.addListener(new ScreenClickListener());

        loadXml();
    }

    public void resize(int width, int height) {
        float widthExpanded = width / this.getWidth();
        float heightExpanded = height / this.getHeight();

        this.setWidth(width);
        this.setHeight(height);

        if (position != null) {
            position = new Point(MathUtils.round(position.x * widthExpanded), MathUtils.round(position.y * heightExpanded));
        }
    }

    public void show(MenuKindType type, Object object, Point position) {
        if (!type.carryingObj.isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException("MenuKindType " + type + " can only accept an object of type "
                + type.carryingObj + ". " + object.getClass() + " received.");
        }
        this.currentObject = object;
        this.showingType = type;
        this.position = position;
        this.setVisible(true);
        if (showingType != null) {
            clickSound.play();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (showingType != null) {
            MenuKind kind = menuKinds.get(showingType);
            int width = kind.width;
            int height = kind.height * kind.items.size();
            Rectangle bound;
            if (position == null) {
                bound = new Rectangle(this.getWidth() / 2 - width / 2, this.getHeight() / 2 - height / 2, width, height);
            } else {
                if (position.x + width > getWidth()) {
                    if (position.y - height < 0) {
                        bound = new Rectangle(position.x - width, position.y, width, height);
                    } else {
                        bound = new Rectangle(position.x - width, position.y - height, width, height);
                    }
                } else {
                    if (position.y - height < 0) {
                        bound = new Rectangle(position.x, position.y, width, height);
                    } else {
                        bound = new Rectangle(position.x, position.y - height, width, height);
                    }
                }
            }
            for (int i = 0; i < kind.items.size(); ++i) {
                // TODO disabled method, showAll
                float x = bound.getX();
                float y = bound.getY() + bound.getHeight() - (i + 1) * kind.height;
                batch.draw(kind.items.get(i).textWidget.getBackground().get(),
                        x, y, kind.width, kind.height);

                TextWidget<MenuItem> widget = kind.items.get(i).textWidget;
                widget.setText(kind.items.get(i).displayName);
                widget.setPosition(x, y);
                widget.setSize(kind.width, kind.height);
                widget.setExtra(kind.items.get(i));
                widget.setVisible(true);

                widget.draw(batch, parentAlpha);
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
        for (MenuItem item : menuKinds.get(showingType).items) {
            item.textWidget.setVisible(false);
        }
        showingType = null;
        setVisible(false);
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
            try {
                ContextMenuMethods.class.getMethod(this.widget.getExtra().fullName, GameScreen.class, Object.class)
                        .invoke(null, screen, currentObject);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
            clickSound.play();
            dismiss();
            return true;
        }
    }

    private class ScreenClickListener extends InputListener {

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            collapseSound.play();
            dismiss();
            return true;
        }
    }

}
