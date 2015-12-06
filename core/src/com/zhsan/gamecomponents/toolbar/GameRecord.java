package com.zhsan.gamecomponents.toolbar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 6/12/2015.
 */
public class GameRecord extends WidgetGroup {

    public static final String RES_PATH = ToolBar.RES_PATH + "GameRecord" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private static final String FILE_NAME = "GameRecordData.xml";

    private GameScreen screen;

    private BitmapFont.HAlignment hAlignment;

    private Texture background;
    private int width, height;

    private TextWidget.Setting textSetting;

    private VerticalGroup records;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + FILE_NAME);

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node node = dom.getElementsByTagName("Record").item(0);
            background =  new Texture(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(node, "Background")));
            width = Integer.parseInt(XmlHelper.loadAttribute(node, "Width"));
            height = Integer.parseInt(XmlHelper.loadAttribute(node, "Height"));
            textSetting = TextWidget.Setting.fromXml(node);
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + FILE_NAME, e);
        }
    }

    public GameRecord(GameScreen screen, BitmapFont.HAlignment hAlignment, int toolbarSize) {
        this.screen = screen;
        this.hAlignment = hAlignment;

        loadXml();

        records = new VerticalGroup();
        records.reverse();

        this.addActor(records);

        setPositionSize(toolbarSize);
    }

    public void addRecord(String msg) {
        TextWidget<Void> widget = new TextWidget<>(textSetting);
        widget.setText(msg);

        records.addActor(widget);
    }

    private final void setPositionSize(int toolbarSize) {
        float dx = 0;
        switch (hAlignment) {
            case LEFT:
                dx = 0;
                break;
            case CENTER:
                dx = Gdx.graphics.getWidth() / 2 - width / 2;
                break;
            case RIGHT:
                dx = Gdx.graphics.getWidth() - width;
                break;
        }
        float dy = toolbarSize;

        this.setPosition(dx, dy);
        this.setSize(width, height);
    }

    public final void resize(int width, int height) {
        setPositionSize(screen.getToolBarHeight());
    }

    public void dispose() {
        background.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(background, getX(), getY(), getWidth(), getHeight());

        super.draw(batch, parentAlpha);
    }
}
