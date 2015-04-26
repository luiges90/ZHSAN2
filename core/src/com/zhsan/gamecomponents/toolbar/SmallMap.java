package com.zhsan.gamecomponents.toolbar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.zhsan.gameobject.Faction;
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

    private float mapOpacity;
    private int maxMapWidth, maxMapHeight, maxTileSize;

    private int tileSize;
    private Texture map;

    private BitmapFont.HAlignment hAlignment;

    private int architectureScale;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + FILE_NAME);

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

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
            throw new FileReadException(RES_PATH + FILE_NAME, e);
        }
    }

    public SmallMap(GameScreen screen, BitmapFont.HAlignment hAlignment, int toolbarSize) {
        this.screen = screen;
        this.hAlignment = hAlignment;

        loadXml();

        GameMap gameMap = screen.getScenario().getGameMap();
        this.map = new Texture(Gdx.files.external(MapLayer.MAP_ROOT_PATH + "_" + gameMap.getFileName() + ".jpg"));

        this.tileSize = Math.min(maxTileSize, Math.min(maxMapWidth / gameMap.getWidth(), maxMapHeight / gameMap.getHeight()));
        int mapWidth = this.tileSize * gameMap.getWidth();
        int mapHeight = this.tileSize * gameMap.getHeight();

        float dx = 0;
        switch (hAlignment) {
            case LEFT:
                dx = 0;
                break;
            case CENTER:
                dx = Gdx.graphics.getWidth() / 2 - mapWidth / 2;
                break;
            case RIGHT:
                dx = Gdx.graphics.getWidth() - mapWidth;
                break;
        }
        float dy = toolbarSize;

        this.setPosition(dx, dy);
        this.setSize(mapWidth, mapHeight);

        this.addListener(new Listener());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // draw minimap
        batch.draw(map, getX(), getY(), getWidth(), getHeight());

        // draw architectures on top of it
        for (Architecture a : screen.getScenario().getArchitectures()) {
            for (Point p : a.getLocation()) {
                int size = tileSize * architectureScale;

                Faction f = a.getBelongedFaction();
                Color color = f == null ? Color.WHITE : f.getColor();

                Sprite sprite = new Sprite(architecture, size ,size);
                sprite.setCenter((int) (getX() + p.x * tileSize - size / 2), (int) (getY() + getHeight() - p.y * tileSize - size / 2));
                sprite.setColor(color);
                sprite.draw(batch);
            }
        }
    }

    public final void resize(int width, int height) {
        GameMap gameMap = screen.getScenario().getGameMap();
        this.tileSize = Math.min(maxTileSize, Math.min(maxMapWidth / gameMap.getWidth(), maxMapHeight / gameMap.getHeight()));
        int mapWidth = this.tileSize * gameMap.getWidth();
        int mapHeight = this.tileSize * gameMap.getHeight();

        float dx = 0;
        switch (hAlignment) {
            case LEFT:
                dx = 0;
                break;
            case CENTER:
                dx = width / 2 - mapWidth / 2;
                break;
            case RIGHT:
                dx = width - mapWidth;
                break;
        }
        float dy = screen.getToolBarHeight();

        this.setPosition(dx, dy);
        this.setSize(mapWidth, mapHeight);
    }

    public void dispose() {
        architecture.dispose();
        map.dispose();
    }

    private class Listener extends InputListener {

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            int px = (int) ((x) / tileSize);
            int py = (int) ((y) / tileSize);
            screen.getMapLayer().setMapCameraPosition(new Point(px, screen.getScenario().getGameMap().getHeight() - py));
            return true;
        }
    }
}
