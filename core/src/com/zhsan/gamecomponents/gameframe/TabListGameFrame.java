package com.zhsan.gamecomponents.gameframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.WidgetUtility;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.*;
import com.zhsan.gameobject.*;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Peter on 5/5/2015.
 */
public class TabListGameFrame extends GameFrame {

    public interface OnItemSelectedListener {
        public void onItemSelected(List<GameObject> selectedItems);
    }

    public enum Selection {
        NONE, SINGLE, MULTIPLE
    }

    public enum ListKindType {
        ARCHITECTURE("Architecture", Architecture.class),
        PERSON("Person", Person.class),
        MILITARY_KIND("MilitaryKind", MilitaryKind.class),
        MILITARY("Military", Military.class),
        TROOP("Troop", Troop.class),
        FACTION("Faction", Faction.class)
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
        BackgroundTextWidget<Column> columnText;
        int width;
        boolean round;
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

    private Texture scrollButton;
    private int scrollWidth;

    private Texture leftArrow, rightArrow;

    private BackgroundTextWidget<Column> selectTextWidget;
    private int selectWidth;
    private BitmapFont.HAlignment selectAlign;
    private Texture checkbox, checkboxSelected, radio, radioSelected;

    private Sound selectSound;

    private int rowHeight;

    private EnumMap<ListKindType, ListKind> listKinds;

    private GameScreen screen;

    private ListKind showingListKind;
    private GameObjectList<?> showingData;

    private Tab showingTab;

    private ScrollPane contentPane;
    private List<TextWidget<GameObject>> showingTextWidgets = new ArrayList<>();

    private List<CheckboxWidget<GameObject>> showingCheckboxes = new ArrayList<>();
    private List<RadioButtonWidget<GameObject>> showingRadioButtons = new ArrayList<>();

    private Selection selection;
    private OnItemSelectedListener onItemSelected;

    private Color highlightRowColor;

    private String title;

    private GameObject context;

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

            Node scrollNode = dom.getElementsByTagName("ScrollButton").item(0);
            scrollButton = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(scrollNode, "FileName")));
            scrollWidth = Integer.parseInt(XmlHelper.loadAttribute(scrollNode, "Width"));

            Node arrowNode = dom.getElementsByTagName("Arrows").item(0);
            leftArrow = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(arrowNode, "LeftFileName")));
            rightArrow = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(arrowNode, "RightFileName")));

            Node checkboxNode = dom.getElementsByTagName("CheckBox").item(0);
            String selectDisplayName = XmlHelper.loadAttribute(checkboxNode, "DisplayName");
            selectWidth = Integer.parseInt(XmlHelper.loadAttribute(checkboxNode, "Width"));
            selectAlign = XmlHelper.loadHAlignmentFromXml(checkboxNode);
            checkbox = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(checkboxNode, "FileName")));
            checkboxSelected = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(checkboxNode, "Selected")));
            radio = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(checkboxNode, "RoundFileName")));
            radioSelected = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(checkboxNode, "RoundSelected")));

            StateTexture tabButton = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("TabButton").item(0));
            TextWidget<Tab> tabText = new TextWidget<>(
                    TextWidget.Setting.fromXml(dom.getElementsByTagName("TabText").item(0)));

            BackgroundTextWidget<Column> columnText = new BackgroundTextWidget<>(
                    new TextWidget<>(TextWidget.Setting.fromXml(dom.getElementsByTagName("ColumnText").item(0))), columnHeader);

            selectTextWidget = new BackgroundTextWidget<>(columnText, columnHeader);
            selectTextWidget.setText(selectDisplayName);

            selectSound = Gdx.audio.newSound(Gdx.files.external(DATA_PATH +
                            XmlHelper.loadAttribute(dom.getElementsByTagName("SoundFile").item(0), "Select")
            ));

            Node tabListNode = dom.getElementsByTagName("TabList").item(0);
            rowHeight = Integer.parseInt(XmlHelper.loadAttribute(tabListNode, "RowHeight"));

            Node highlightNode = dom.getElementsByTagName("HighlightRow").item(0);
            highlightRowColor = XmlHelper.loadColorFromXml(Integer.parseUnsignedInt(XmlHelper.loadAttribute(highlightNode, "Color")));

            listKinds = new EnumMap<>(ListKindType.class);
            for (int i = 0; i < tabListNode.getChildNodes().getLength(); ++i) {
                Node listKindNode = tabListNode.getChildNodes().item(i);
                if (listKindNode instanceof Text) continue;

                ListKind listKind = new ListKind();
                listKind.title = XmlHelper.loadAttribute(listKindNode, "DisplayName");

                Map<Integer, Column> columns = null;
                for (int j = 0; j < listKindNode.getChildNodes().getLength(); ++j) {
                    if (listKindNode.getChildNodes().item(j).getNodeName().equals("Columns")) {
                        Node columnNodes = listKindNode.getChildNodes().item(j);
                        columns = loadColumnsFromXml(columnText, columnNodes);
                    }
                }

                List<Tab> tabs = null;
                for (int j = 0; j < listKindNode.getChildNodes().getLength(); ++j) {
                    if (listKindNode.getChildNodes().item(j).getNodeName().equals("Tabs")) {
                        Node tabNodes = listKindNode.getChildNodes().item(j);
                        listKind.tabMargin = Integer.parseInt(XmlHelper.loadAttribute(tabNodes, "Margin"));
                        tabs = loadTabsFromXml(tabButton, tabText, columns, tabNodes);
                    }
                }

                listKind.tabs = tabs;
                showingTab = tabs.get(0);

                ListKindType type = ListKindType.fromXmlName(XmlHelper.loadAttribute(listKindNode, "Name"));
                if (type != null) {
                    listKinds.put(type, listKind);
                }
            }
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "TabListData.xml", e);
        }
    }

    private List<Tab> loadTabsFromXml(StateTexture tabButton, TextWidget<Tab> tabText, Map<Integer, Column> columns, Node tabNodes) {
        List<Tab> tabs = new ArrayList<>();
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
                if (columns.get(l) == null) {
                    throw new IllegalArgumentException("Column not found: " + l);
                }
                tab.columns.add(columns.get(l));
            }
            tab.tabButton.addListener(new TabButtonListener(tab));

            tabs.add(tab);
        }
        return tabs;
    }

    private Map<Integer, Column> loadColumnsFromXml(TextWidget<Column> columnText, Node columnNodes) {
        Map<Integer, Column> columns = new HashMap<>();
        for (int k = 0; k < columnNodes.getChildNodes().getLength(); ++k) {
            Node columnNode = columnNodes.getChildNodes().item(k);
            if (columnNode instanceof Text) continue;
            Column column = new Column();
            column.name = XmlHelper.loadAttribute(columnNode, "Name");
            column.displayName = XmlHelper.loadAttribute(columnNode, "DisplayName");
            column.contentTemplate = new TextWidget<>(
                    TextWidget.Setting.fromXml(columnNode)
            );
            column.columnText = new BackgroundTextWidget<>(columnText, columnHeader);
            column.columnText.setText(column.displayName);
            column.width = Integer.parseInt(XmlHelper.loadAttribute(columnNode, "MinWidth"));
            column.round = Boolean.parseBoolean(XmlHelper.loadAttribute(columnNode, "Round", "True"));

            columns.put(Integer.parseInt(XmlHelper.loadAttribute(columnNode, "ID")), column);
        }
        return columns;
    }

    public TabListGameFrame(GameScreen screen) {
        super("", null);

        this.screen = screen;

        loadXml();

        this.setVisible(false);
        this.addOnClickListener(new ButtonListener());
    }

    public void show(ListKindType type, GameObjectList<?> showingData) {
        show(null, type, showingData, Selection.NONE, null);
    }

    public void show(String title, ListKindType type, GameObjectList<?> showingData, Selection selection, OnItemSelectedListener onItemSelected) {
        show(null, type, null, showingData, selection, onItemSelected);
    }

    public void show(String title, ListKindType type, GameObject context, GameObjectList<?> showingData, Selection selection, OnItemSelectedListener onItemSelected) {
        if (showingData.size() == 0) return;
        if (!type.carryingObj.isAssignableFrom(showingData.getFirst().getClass())) {
            throw new IllegalArgumentException("MenuKindType " + type + " can only accept an object of type "
                    + type.carryingObj + ". " + showingData.getFirst().getClass() + " received.");
        }

        this.title = title;
        this.selection = selection;
        this.context = context;
        this.showingListKind = listKinds.get(type);
        this.showingData = showingData;
        this.showingTab = this.showingListKind.tabs.get(0);
        this.onItemSelected = onItemSelected;

        showingListKind.tabs.forEach(tab -> {
            addActor(tab.tabButton);
        });

        this.setVisible(true);
    }

    private void initContentPane(int offset) {
        Table contentTable = new Table();

        // header
        if (selection != Selection.NONE) {
            int align;
            switch (selectAlign) {
                case LEFT: align = Align.left; break;
                case RIGHT: align = Align.right; break;
                case CENTER:default: align = Align.center; break;
            }
            contentTable.add(selectTextWidget).width(selectWidth).height(columnHeaderHeight).align(align);
        }
        for (Column c : showingTab.columns) {
            contentTable.add(c.columnText).width(c.width).height(columnHeaderHeight);
        }
        contentTable.row();

        // content

        for (GameObject o : showingData) {
            if (selection == Selection.SINGLE) {
                RadioButtonWidget<GameObject> widget = new RadioButtonWidget<>(TextWidget.Setting.empty(), "", radioSelected, radio);
                widget.setExtra(o);

                showingRadioButtons.add(widget);
                contentTable.add(widget).width(selectWidth).height(columnHeaderHeight);
            } else if (selection == Selection.MULTIPLE) {
                CheckboxWidget<GameObject> widget = new CheckboxWidget<>(TextWidget.Setting.empty(), "", checkboxSelected, checkbox);
                widget.setExtra(o);

                showingCheckboxes.add(widget);
                contentTable.add(widget).width(selectWidth).height(columnHeaderHeight);
            }
            for (Column c : showingTab.columns) {
                TextWidget<GameObject> widget = new TextWidget<>(c.contentTemplate);
                widget.setExtra(o);
                widget.setText(o.getFieldString(c.name, c.round, context));

                showingTextWidgets.add(widget);

                contentTable.add(widget).width(c.width).height(columnHeaderHeight);
            }
            contentTable.row().height(rowHeight);
        }

        if (selection == Selection.SINGLE) {
            for (RadioButtonWidget<GameObject> widget : showingRadioButtons) {
                widget.setGroup(showingRadioButtons);
            }
        }

        contentTable.top().left();

        contentPane = new ScrollPane(contentTable);
        Table contentPaneContainer = WidgetUtility.setupScrollpane(getLeftBound(), getBottomActiveBound(),
                getRightBound() - getLeftBound(), getTopActiveBound() - offset - getBottomActiveBound(), contentPane, scrollButton);

        addActor(contentPaneContainer);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.setTitle(this.title == null ? showingListKind.title : this.title);

        super.draw(batch, parentAlpha);

        int offset = drawTabs(batch, parentAlpha);

        if (contentPane == null) {
            initContentPane(offset);
        }
    }

    private int drawTabs(Batch batch, float parentAlpha) {
        List<Tab> tabs = showingListKind.tabs;

        float offsetX = getLeftBound();
        float offsetY = getTopActiveBound();

        int x = 0;
        int y = 0;
        for (Tab t : tabs) {
            t.tabButton.setText(t.displayName);
            t.tabButton.setPosition(offsetX + x, offsetY - y);
            t.tabButton.setSize(t.tabButton.getBackground().get().getWidth(),
                    t.tabButton.getBackground().get().getHeight());

            x += t.tabButton.getWidth() + showingListKind.tabMargin;
            if (x + t.tabButton.getWidth() > getRightBound() - getLeftBound()) {
                y += t.tabButton.getHeight() + showingListKind.tabMargin;
                x = 0;
            }
        }

        return y + showingListKind.tabMargin;
    }

    private void resetContentPane() {
        if (contentPane != null) {
            contentPane.clear();
            contentPane = null;
        }
        showingTextWidgets.forEach(TextWidget::dispose);
        showingTextWidgets.clear();
        showingCheckboxes.forEach(TextWidget::dispose);
        showingCheckboxes.clear();
        showingRadioButtons.forEach(TextWidget::dispose);
        showingRadioButtons.clear();
    }

    @Override
    protected void dismiss(boolean ok) {
        super.dismiss(ok);

        resetContentPane();

        removeActor(contentPane);
        showingListKind.tabs.forEach(tab -> {
            removeActor(tab.tabButton);
        });
    }

    public void resize(int width, int height) {
        super.resize(width, height);

        resetContentPane();
    }

    public void dispose() {
        columnHeader.dispose();
        scrollButton.dispose();
        leftArrow.dispose();
        rightArrow.dispose();
        checkbox.dispose();
        checkboxSelected.dispose();
        radio.dispose();
        radioSelected.dispose();
        selectSound.dispose();
        selectTextWidget.dispose();
        listKinds.forEach((listKindType, listKind) -> {
            listKind.tabs.forEach(tab -> {
                tab.tabButton.dispose();
                tab.columns.forEach(column -> column.columnText.dispose());
            });
        });
    }

    private class ButtonListener implements OnClick {

        List<GameObject> selected = null;

        @Override
        public void onOkClicked() {
            if (selection == Selection.SINGLE) {
                selected = showingRadioButtons.stream().filter(RadioButtonWidget::isChecked)
                        .map(RadioButtonWidget::getExtra).collect(Collectors.toList());
            } else if (selection == Selection.MULTIPLE) {
                selected = showingCheckboxes.stream().filter(CheckboxWidget::isChecked)
                        .map(CheckboxWidget::getExtra).collect(Collectors.toList());
            }
        }

        @Override
        public void onPostOkClicked() {
            if (selected != null) {
                onItemSelected.onItemSelected(selected);
            }
        }

        @Override
        public void onCancelClicked() {

        }
    }

    private class TabButtonListener extends InputListener {

        private Tab tab;

        public TabButtonListener(Tab tab) {
            this.tab = tab;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            showingTab = tab;
            resetContentPane();
            return true;
        }
    }
}
