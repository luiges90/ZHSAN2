package com.zhsan.gamecomponents.gameframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.zhsan.common.Paths;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.WidgetUtility;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.SelectableTextWidget;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.gameobject.GameSurvey;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Created by Peter on 4/4/2015.
 */
public class FileGameFrame extends GameFrame {

    public interface OnFileSelected {
        public void onFileSelected(FileHandle file);
    }

    public enum Usage {
        SAVE, LOAD
    }

    public static final String RES_PATH = GameFrame.RES_PATH + "File" + File.separator;
    public static final String DATA_PATH = RES_PATH  + "Data" + File.separator;

    public static final String SAVE_FILE_PATH = Paths.DATA + "Save" + File.separator;

    private String title;
    private Usage usage;
    private float widthRatio, heightRatio;

    private int margins;
    private int listPaddings;
    private Color listSelectedColor;

    private VerticalGroup fileList = new VerticalGroup();
    private ScrollPane filePane;
    private TextWidget.Setting fileStyle;

    private Texture scrollButton;
    private OnFileSelected onFileSelected;

    private void loadXml(Usage usage) {
        FileHandle f = Gdx.files.external(RES_PATH + "FileGameFrameData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node nodeSizeRatio = dom.getElementsByTagName("SizeRatio").item(0);
            widthRatio = Float.parseFloat(XmlHelper.loadAttribute(nodeSizeRatio, "width"));
            heightRatio = Float.parseFloat(XmlHelper.loadAttribute(nodeSizeRatio, "height"));

            if (usage == Usage.SAVE) {
                title = XmlHelper.loadAttribute(dom.getElementsByTagName("Title").item(0), "save");
            } else if (usage == Usage.LOAD) {
                title = XmlHelper.loadAttribute(dom.getElementsByTagName("Title").item(0), "load");
            }

            margins = Integer.parseInt(XmlHelper.loadAttribute(dom.getElementsByTagName("Margins").item(0), "value"));
            listPaddings = Integer.parseInt(XmlHelper.loadAttribute(dom.getElementsByTagName("Lists").item(0), "padding"));
            listSelectedColor = XmlHelper.loadColorFromXml(
                    Integer.parseUnsignedInt(XmlHelper.loadAttribute(dom.getElementsByTagName("Lists").item(0), "selectedColor"))
            );
            scrollButton = new Texture(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(dom.getElementsByTagName("Scroll").item(0), "fileName")));

            fileStyle = TextWidget.Setting.fromXml(dom.getElementsByTagName("FileList").item(0));

        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "FileGameFrameData.xml", e);
        }
    }

    public FileGameFrame(Usage usage, OnFileSelected fileListener) {
        super(0, 0, "", null);

        loadXml(usage);

        setTitle(title);
        setWidth(Gdx.graphics.getWidth() * widthRatio);
        setHeight(Gdx.graphics.getHeight() * heightRatio);

        onFileSelected = fileListener;

        initFilePane();

        super.setOkEnabled(false);
        super.addOnClickListener(new ButtonListener());
    }

    public void show() {
        populateFilePane();
        setVisible(true);
    }

    private float getPaneWidth() {
        return getRightBound() - getLeftBound() - margins * 2;
    }

    private float getPaneHeight() {
        return getTopBound() - getBottomActiveBound() - margins * 2;
    }

    private void populateFilePane() {
        FileHandle[] saveFiles = Gdx.files.external(SAVE_FILE_PATH).list();

        fileList.clear();
        for (FileHandle fh : saveFiles) {
            if (!fh.isDirectory()) continue;

            GameSurvey survey = GameSurvey.fromCSV(fh.path());
            String description = survey.getTitle() + " " +
                    survey.getSaveDate().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) + " " +
                    survey.getMessage();

            SelectableTextWidget<FileHandle> widget = new SelectableTextWidget<>(fileStyle, description, listSelectedColor);
            widget.setTouchable(Touchable.enabled);
            widget.setExtra(fh);
            widget.setWidth(getPaneWidth());
            widget.setPadding(listPaddings);
            fileList.addActor(widget);

            widget.addListener(new FileSelectListener(widget));
        }
    }

    private void initFilePane() {
        float paneHeight = getPaneHeight();
        float paneWidth = getPaneWidth();

        populateFilePane();

        filePane = new ScrollPane(fileList);
        Table scenarioPaneContainer = WidgetUtility.setupScrollpane(getLeftBound() + margins, getTopBound() - margins - paneHeight,
                paneWidth, paneHeight, filePane, scrollButton);

        addActor(scenarioPaneContainer);
    }

    private class FileSelectListener extends InputListener {

        private TextWidget<FileHandle> widget;

        public FileSelectListener(TextWidget<FileHandle> widget) {
            this.widget = widget;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            onFileSelected.onFileSelected(widget.getExtra());
            return true;
        }
    }

    private class ButtonListener implements GameFrame.OnClick {

        @Override
        public void onOkClicked() {

        }

        @Override
        public void onCancelClicked() {
            setVisible(false);
        }
    }

}
