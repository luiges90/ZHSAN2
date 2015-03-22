package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.GetScrollFocusWhenEntered;
import com.zhsan.gameobject.GameMap;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 19/3/2015.
 */
public class MapLayer extends WidgetGroup {

    private enum MoveStateX {
        IDLE, LEFT, RIGHT
    }
    private enum MoveStateY {
        IDLE, TOP, BOTTOM
    }

    public static final String DATA_PATH = Paths.RESOURCES + "Map" + File.separator;

    private int mapZoomMin, mapZoomMax, mapScrollBoundary, mapMouseScrollFactor;
    private float mapScrollFactor;

    private Map<String, Texture> mapTiles = new HashMap<>();

    private GameScreen screen;
    private Vector2 mapCameraPosition;
    private MoveStateX moveStateX = MoveStateX.IDLE;
    private MoveStateY moveStateY = MoveStateY.IDLE;

    private void loadXml() {
        FileHandle f = Gdx.files.external(DATA_PATH + "MapSetting.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            NodeList list = dom.getElementsByTagName("MapSetting");
            NamedNodeMap attributes = list.item(0).getAttributes();
            mapZoomMin = Integer.parseInt(attributes.getNamedItem("mapZoomMin").getNodeValue());
            mapZoomMax = Integer.parseInt(attributes.getNamedItem("mapZoomMax").getNodeValue());
            mapMouseScrollFactor = Integer.parseInt(attributes.getNamedItem("mapMouseScrollFactor").getNodeValue());
            mapScrollBoundary = Integer.parseInt(attributes.getNamedItem("mapScrollBoundary").getNodeValue());
            mapScrollFactor = Float.parseFloat(attributes.getNamedItem("mapScrollFactor").getNodeValue());
        } catch (Exception e) {
            throw new FileReadException(DATA_PATH + "MapSetting.xml", e);
        }
    }

    public MapLayer(GameScreen screen) {
        this.screen = screen;

        loadXml();

        GameMap map = screen.getScenario().getGameMap();
        Point mapCenter = screen.getScenario().getGameSurvey().getCameraPosition();
        this.mapCameraPosition = new Vector2(mapCenter.x * mapZoomMax, (map.getHeight() - 1 - mapCenter.y) * mapZoomMax);

        this.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.addListener(new InputEventListener());
        this.addListener(new GetScrollFocusWhenEntered(this));
    }

    public void resize(int width, int height) {
        this.setBounds(0, 0, width, height);
    }

    private Texture getMapTile(String mapName, String fileName) {
        if (mapTiles.containsKey(fileName)) {
            return mapTiles.get(fileName);
        }
        Texture t = new Texture(Gdx.files.external(DATA_PATH + mapName + File.separator + fileName + ".jpg"));
        mapTiles.put(fileName, t);
        return t;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        GameMap map = screen.getScenario().getGameMap();

        if (moveStateX == MoveStateX.LEFT) {
            mapCameraPosition.add(-mapScrollFactor / map.getZoom(), 0);
        } else if (moveStateX == MoveStateX.RIGHT) {
            mapCameraPosition.add(mapScrollFactor / map.getZoom(), 0);
        }

        if (moveStateY == MoveStateY.BOTTOM) {
            mapCameraPosition.add(0, -mapScrollFactor / map.getZoom());
        } else if (moveStateY == MoveStateY.TOP) {
            mapCameraPosition.add(0, mapScrollFactor / map.getZoom());
        }

        screen.getScenario().getGameSurvey().setCameraPosition(
                new Point((int) (mapCameraPosition.x / mapZoomMax), (int) (mapCameraPosition.y / mapZoomMax))
        );
    }

    public void draw(Batch batch, float parentAlpha) {
        super.drawChildren(batch, parentAlpha);

        GameMap map = screen.getScenario().getGameMap();
        int zoom = map.getZoom();

        int imageSize = map.getZoom() * map.getTileInEachImage();

        int noImagesX = MathUtils.ceil(this.getWidth() / imageSize);
        int noImagesY = MathUtils.ceil(this.getHeight() / imageSize);

        float scaledCameraPositionX = mapCameraPosition.x / mapZoomMax * zoom;
        float scaledCameraPositionY = mapCameraPosition.y / mapZoomMax * zoom;

        int xLo = MathUtils.floor(mapCameraPosition.x / mapZoomMax / map.getTileInEachImage() - noImagesX / 2 - 1);
        int xHi = MathUtils.ceil(mapCameraPosition.x / mapZoomMax / map.getTileInEachImage() + noImagesX / 2);
        int yLo = MathUtils.floor(mapCameraPosition.y / mapZoomMax / map.getTileInEachImage() - noImagesY / 2 - 1);
        int yHi = MathUtils.ceil(mapCameraPosition.y / mapZoomMax / map.getTileInEachImage() + noImagesY / 2);

        int startPointFromCameraX = (int)(scaledCameraPositionX - (imageSize * xLo));
        int startPointFromCameraY = (int)(scaledCameraPositionY - (imageSize * yLo));

        int offsetX = (int) (startPointFromCameraX - this.getWidth() / 2);
        int offsetY = (int) (startPointFromCameraY - this.getHeight() / 2);


        for (int y = yLo; y <= yHi; ++y) {
            for (int x = xLo; x <= xHi; ++x) {
                Texture texture = getMapTile(map.getFileName(), Integer.toString((map.getImageCount() - 1 - y) * map.getImageCount() + x));
                batch.draw(texture, (x - xLo) * imageSize - offsetX, (y - yLo) * imageSize - offsetY, imageSize, imageSize);
            }
        }
    }

    public void dispose() {
        mapTiles.values().forEach(Texture::dispose);
    }

    private class InputEventListener extends InputListener {

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            moveStateX = MoveStateX.IDLE;
            moveStateY = MoveStateY.IDLE;

            if (x < mapScrollBoundary) {
                moveStateX = MoveStateX.LEFT;
            } else if (x > getWidth() - mapScrollBoundary) {
                moveStateX = MoveStateX.RIGHT;
            }
            if (y < mapScrollBoundary) {
                moveStateY = MoveStateY.BOTTOM;
            } else if (y > getHeight() - mapScrollBoundary) {
                moveStateY = MoveStateY.TOP;
            }
            return true;
        }

        @Override
        public boolean scrolled(InputEvent event, float x, float y, int amount) {
            int newZoom = screen.getScenario().getGameMap().getZoom();
            newZoom = MathUtils.clamp(newZoom + -amount * mapMouseScrollFactor, mapZoomMin, mapZoomMax);
            screen.getScenario().getGameMap().setZoom(newZoom);
            return true;
        }
    }

}
