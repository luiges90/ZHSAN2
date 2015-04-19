package com.zhsan.gamecomponents.toolbar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.MapLayer;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gameobject.Architecture;
import com.zhsan.gameobject.GameMap;
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
    private int maxMapWidth, maxMapHeight, maxTileSize;

    private Rectangle buttonPos;
    private BitmapFont.HAlignment hAlign;

    private int mapWidth, mapHeight, tileSize;
    private Texture map;

    private int architectureScale;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "SmallMapData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            toolButton = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("ToolTexture").item(0));

            Node architectureNode = dom.getElementsByTagName("Architecture").item(0);
            architecture = new Texture(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(architectureNode, "FileName")));
            architectureScale = Integer.parseInt(XmlHelper.loadAttribute(architectureNode, "Scale"));

            Node mapNode = dom.getElementsByTagName("Map").item(0);
            mapOpacity = Float.parseFloat(XmlHelper.loadAttribute(mapNode, "Transparent"));
            maxMapWidth = Integer.parseInt(XmlHelper.loadAttribute(mapNode, "MaxWidth"));
            maxMapHeight = Integer.parseInt(XmlHelper.loadAttribute(mapNode, "MaxHeight"));
            maxTileSize = Integer.parseInt(XmlHelper.loadAttribute(mapNode, "TileLengthMax"));
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "SmallMapData.xml", e);
        }
    }

    public SmallMap(GameScreen screen, Rectangle buttonPosition, BitmapFont.HAlignment hAlign) {
        this.screen = screen;
        this.hAlign = hAlign;
        this.buttonPos = buttonPosition;

        this.setX(0);
        this.setY(0);
        this.setWidth(Gdx.graphics.getWidth());
        this.setHeight(Gdx.graphics.getHeight());

        loadXml();

        GameMap gameMap = screen.getScenario().getGameMap();
        this.tileSize = Math.min(maxTileSize, Math.min(maxMapWidth / gameMap.getWidth(), maxMapHeight / gameMap.getHeight()));
        this.mapWidth = this.tileSize * gameMap.getWidth();
        this.mapHeight = this.tileSize * gameMap.getHeight();
        this.map = new Texture(Gdx.files.external(MapLayer.MAP_ROOT_PATH + "_" + gameMap.getFileName() + ".jpg"));

        this.addListener(new Listener());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(toolButton.get(), buttonPos.getX(), buttonPos.getY(), buttonPos.getWidth(), buttonPos.getHeight());

        if (toolButton.getState() == StateTexture.State.SELECTED) {
            // draw minimap
            float dx = 0;
            switch (hAlign) {
                case LEFT:
                    dx = 0;
                    break;
                case CENTER:
                    dx = this.getWidth() / 2 - mapWidth / 2;
                    break;
                case RIGHT:
                    dx = this.getWidth() - mapWidth;
                    break;
            }
            float dy = ((ToolBar) this.getParent()).getToolbarHeight();
            batch.draw(map, dx, dy, mapWidth, mapHeight);

            // draw architectures on top of it
            for (Architecture a : screen.getScenario().getArchitectures()) {
                for (Point p : a.getLocation()) {
                    int size = tileSize * architectureScale;
                    batch.draw(architecture,
                            dx + p.x * tileSize - size / 2, dy + mapHeight - p.y * tileSize - size / 2, size, size);
                }
            }
        }
    }

    public void resize(int width, int height, Rectangle pos) {
        this.setWidth(width);
        this.setHeight(height);
        buttonPos = pos;
    }

    public void dispose() {
        toolButton.dispose();
    }

    private class Listener extends InputListener {

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (buttonPos.contains(x, y)) {
                if (toolButton.getState() == StateTexture.State.NORMAL) {
                    toolButton.setState(StateTexture.State.SELECTED);
                } else {
                    toolButton.setState(StateTexture.State.NORMAL);
                }
                return true;
            }
            return false;
        }
    }
}
