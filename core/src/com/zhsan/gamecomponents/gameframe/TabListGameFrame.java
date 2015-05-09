package com.zhsan.gamecomponents.gameframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.StateBackgroundTextWidget;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.gameobject.Architecture;
import com.zhsan.gameobject.GameObject;
import com.zhsan.gameobject.GameObjectList;
import com.zhsan.gameobject.GameScenario;
import com.zhsan.screen.GameScreen;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

/**
 * Created by Peter on 5/5/2015.
 */
public class TabListGameFrame extends GameFrame {

    public static enum ListKindType {
        ARCHITECTURE("Architecture", Architecture.class)
        ;

        public final String xmlName;
        public final Class<?> carryingObj;
        ListKindType(String xmlName, Class<?> carryingObj) {
            this.xmlName = xmlName;
            this.carryingObj = carryingObj;
        }

        public static final ListKindType fromXmlName(String name) {
            for (ListKindType type : ListKindType.values()) {
                if (type.xmlName.equals(name)) {
                    return type;
                }
            }
            return null;
        }
    }

    private static class Column {
        String name, displayName;
        TextWidget<GameObject> contentTemplate;
        TextWidget<Column> columnText;
        int width;
    }

    private static class Tab {
        StateBackgroundTextWidget<Tab> tabButton;
        String name, displayName;
        List<Column> columns;
    }

    private static class ListKind {
        List<Tab> tabs;
        int tabMargin;
        String title;
    }

    private Texture columnHeader;
    private int columnHeaderHeight;

    private Texture columnSpliter;
    private Point columnSpliterSize;

    private Texture scrollButton;
    private int scrollWidth;

    private Texture leftArrow, rightArrow;

    private String selectName, selectDisplayName;
    private StateTexture checkbox, radioButton;

    private Sound selectSound;

    private int rowHeight;

    private EnumMap<ListKindType, ListKind> listKinds;

    private GameScreen screen;

    private ListKind showingListKind;
    private GameObjectList<?> showingData;

    public static final String RES_PATH = GameFrame.RES_PATH + "TabList" + File.separator;
    public static final String DATA_PATH = RES_PATH  + "Data" + File.separator;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "TabListData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node columnHeaderNode = dom.getElementsByTagName("ColumnHeader").item(0);
            columnHeader = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(columnHeaderNode, "FileName")));
            columnHeaderHeight = Integer.parseInt(XmlHelper.loadAttribute(columnHeaderNode, "Height"));

            Node columnSpliterNode = dom.getElementsByTagName("ColumnSpliter").item(0);
            columnSpliter = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(columnSpliterNode, "FileName")));
            columnSpliterSize = new Point(
                    Integer.parseInt(XmlHelper.loadAttribute(columnSpliterNode, "Width")),
                    Integer.parseInt(XmlHelper.loadAttribute(columnSpliterNode, "Height"))
            );

            Node scrollNode = dom.getElementsByTagName("ScrollButton").item(0);
            scrollButton = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(scrollNode, "FileName")));
            scrollWidth = Integer.parseInt(XmlHelper.loadAttribute(scrollNode, "Width"));

            Node arrowNode = dom.getElementsByTagName("Arrows").item(0);
            leftArrow = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(arrowNode, "LeftFileName")));
            rightArrow = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(arrowNode, "RightFileName")));

            Node checkboxNode = dom.getElementsByTagName("CheckBox").item(0);
            selectName = XmlHelper.loadAttribute(checkboxNode, "Name");
            selectDisplayName = XmlHelper.loadAttribute(checkboxNode, "DisplayName");
            checkbox = StateTexture.fromXml(DATA_PATH, checkboxNode);
            radioButton = StateTexture.fromXml(DATA_PATH, checkboxNode, "Round");

            StateTexture tabButton = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("TabButton").item(0));
            TextWidget<Tab> tabText = new TextWidget<Tab>(
                    TextWidget.Setting.fromXml(dom.getElementsByTagName("TabText").item(0)));

            TextWidget<Column> columnText = new TextWidget<Column>(
                    TextWidget.Setting.fromXml(dom.getElementsByTagName("ColumnText").item(0)));

            selectSound = Gdx.audio.newSound(Gdx.files.external(DATA_PATH +
                            XmlHelper.loadAttribute(dom.getElementsByTagName("SoundFile").item(0), "Select")
            ));

            Node tabListNode = dom.getElementsByTagName("TabList").item(0);
            rowHeight = Integer.parseInt(XmlHelper.loadAttribute(tabListNode, "RowHeight"));

            listKinds = new EnumMap<>(ListKindType.class);
            for (int i = 0; i < tabListNode.getChildNodes().getLength(); ++i) {
                Node listKindNode = tabListNode.getChildNodes().item(i);
                if (listKindNode instanceof Text) continue;

                ListKind listKind = new ListKind();
                listKind.title = XmlHelper.loadAttribute(listKindNode, "DisplayName");

                Map<Integer, Column> columns = new HashMap<>();
                for (int j = 0; j < listKindNode.getChildNodes().getLength(); ++j) {
                    if (listKindNode.getChildNodes().item(j).getNodeName().equals("Columns")) {
                        Node columnNodes = listKindNode.getChildNodes().item(j);
                        for (int k = 0; k < columnNodes.getChildNodes().getLength(); ++k) {
                            Node columnNode = columnNodes.getChildNodes().item(k);
                            if (columnNode instanceof Text) continue;
                            Column column = new Column();
                            column.name = XmlHelper.loadAttribute(columnNode, "Name");
                            column.displayName = XmlHelper.loadAttribute(columnNode, "DisplayName");
                            column.contentTemplate = new TextWidget<>(
                                    TextWidget.Setting.fromXml(columnNode)
                            );
                            column.columnText = new TextWidget<>(columnText);
                            column.width = Integer.parseInt(XmlHelper.loadAttribute(columnNode, "MinWidth"));

                            columns.put(Integer.parseInt(XmlHelper.loadAttribute(columnNode, "ID")), column);
                        }
                    }
                }

                List<Tab> tabs = new ArrayList<>();
                for (int j = 0; j < listKindNode.getChildNodes().getLength(); ++j) {
                    if (listKindNode.getChildNodes().item(j).getNodeName().equals("Tabs")) {
                        Node tabNodes = listKindNode.getChildNodes().item(j);
                        listKind.tabMargin = Integer.parseInt(XmlHelper.loadAttribute(tabNodes, "Margin"));
                        for (int k = 0; k < tabNodes.getChildNodes().getLength(); ++k) {
                            Node tabNode = tabNodes.getChildNodes().item(k);
                            if (tabNode instanceof Text) continue;
                            Tab tab = new Tab();
                            tab.name = XmlHelper.loadAttribute(tabNode, "Name");
                            tab.displayName = XmlHelper.loadAttribute(tabNode, "DisplayName");
                            tab.tabButton = new StateBackgroundTextWidget<>(tabText, tabButton);
                            List<Integer> columnIds = XmlHelper.loadIntegerListFromXml(
                                    XmlHelper.loadAttribute(tabNode, "Columns")
                            );
                            tab.columns = new ArrayList<>();
                            for (Integer l : columnIds) {
                                tab.columns.add(columns.get(l));
                            }

                            tabs.add(tab);
                        }
                    }
                }

                listKind.tabs = tabs;

                ListKindType type = ListKindType.fromXmlName(XmlHelper.loadAttribute(listKindNode, "Name"));
                if (type != null) {
                    listKinds.put(type, listKind);
                }
            }
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "TabListData.xml", e);
        }
    }

    public TabListGameFrame(GameScreen screen) {
        super("", null);

        this.screen = screen;

        loadXml();

        this.setVisible(false);
        this.addOnClickListener(new ButtonListener());
    }

    public void show(ListKindType type, GameObjectList<?> showingData) {
        if (showingData.size() == 0) return;
        if (!type.carryingObj.isAssignableFrom(showingData.get(0).getClass())) {
            throw new IllegalArgumentException("MenuKindType " + type + " can only accept an object of type "
                    + type.carryingObj + ". " + showingData.get(0).getClass() + " received.");
        }

        this.showingListKind = listKinds.get(type);
        this.showingData = showingData;
        this.setVisible(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.setTitle(showingListKind.title);

        super.draw(batch, parentAlpha);


    }

    private class ButtonListener implements OnClick {

        @Override
        public void onOkClicked() {

        }

        @Override
        public void onCancelClicked() {
            setVisible(false);
        }
    }
}
