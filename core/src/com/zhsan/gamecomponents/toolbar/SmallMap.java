package com.zhsan.gamecomponents.toolbar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 19/4/2015.
 */
public class SmallMap extends WidgetGroup {

    public static final String RES_PATH = ToolBar.RES_PATH + "SmallMap" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private static final String FILE_NAME = "SmallMapData.xml";

    private GameScreen screen;

    private Texture architecture;
    private StateTexture toolButton;

    private float mapOpacity;
    private int mapWidth, mapHeight, tileSize, maxTileSize;

    private BitmapFont.HAlignment hAlign;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "SmallMapData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            toolButton = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("ToolTexture").item(0));
            architecture = new Texture(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(dom.getElementsByTagName("Architecture").item(0), "FileName")));

            Node mapNode = dom.getElementsByTagName("Map").item(0);
            mapOpacity = Float.parseFloat(XmlHelper.loadAttribute(mapNode, "Transparent"));
            mapWidth = Integer.parseInt(XmlHelper.loadAttribute(mapNode, "MaxWidth"));
            mapHeight = Integer.parseInt(XmlHelper.loadAttribute(mapNode, "MaxHeight"));
            tileSize = Integer.parseInt(XmlHelper.loadAttribute(mapNode, "TileLength"));
            maxTileSize = Integer.parseInt(XmlHelper.loadAttribute(mapNode, "TileLengthMax"));
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "SmallMapData.xml", e);
        }
    }

    public SmallMap(GameScreen screen, Rectangle buttonPosition, BitmapFont.HAlignment hAlign) {
        this.screen = screen;
        this.hAlign = hAlign;

        this.setX(buttonPosition.x);
        this.setY(buttonPosition.y);
        this.setWidth(buttonPosition.width);
        this.setHeight(buttonPosition.height);

        loadXml();

        this.addListener(new Listener());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(toolButton.get(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public void dispose() {
        toolButton.dispose();
    }

    private class Listener extends InputListener {

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (toolButton.getState() == StateTexture.State.NORMAL) {
                toolButton.setState(StateTexture.State.SELECTED);
            } else {
                toolButton.setState(StateTexture.State.NORMAL);
            }
            return true;
        }
    }
}
