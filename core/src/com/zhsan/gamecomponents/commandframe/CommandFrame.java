package com.zhsan.gamecomponents.commandframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 2/6/2015.
 */
public abstract class CommandFrame extends WidgetGroup {

    public static final String RES_PATH = Paths.RESOURCES + "CommandFrame" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private Texture background;
    private Rectangle backgroundPos;

    private Texture scrollbar;
    private int scrollbarWidth;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "CommandFrameData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node bgNode = dom.getElementsByTagName("Background").item(0);
            background = new Texture(Gdx.files.external(
                DATA_PATH + XmlHelper.loadAttribute(bgNode, "FileName")
            ));
            backgroundPos = XmlHelper.loadRectangleFromXml(bgNode);

            Node scrollNode = dom.getElementsByTagName("ScrollButton").item(0);
            scrollbar = new Texture(Gdx.files.external(
                DATA_PATH + XmlHelper.loadAttribute(scrollNode, "FileName")
            ));
            scrollbarWidth = Integer.parseInt(XmlHelper.loadAttribute(scrollNode, "Width"));
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "CommandFrameData.xml", e);
        }
    }

    public CommandFrame() {
        loadXml();

        this.setPosition(backgroundPos.x, backgroundPos.y);
        this.setSize(backgroundPos.width, backgroundPos.height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(background, getX() + backgroundPos.x, getY() - backgroundPos.y, backgroundPos.width, backgroundPos.height);

        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        background.dispose();
        scrollbar.dispose();
    }

    protected Texture getScrollbar() {
        return scrollbar;
    }
}
