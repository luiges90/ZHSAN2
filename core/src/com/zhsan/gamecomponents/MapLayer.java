package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
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

    public static final String DATA_PATH = Paths.RESOURCES + "Map" + File.separator;

    private int mapZoomMin, mapZoomMax, mapScrollBoundary, mapMouseScrollFactor;

    private Map<String, Texture> mapTiles = new HashMap<>();

    private GameScreen screen;
    private Point cameraPosition;

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
        } catch (Exception e) {
            throw new FileReadException(DATA_PATH + "MapSetting.xml", e);
        }
    }

    public MapLayer(GameScreen screen) {
        this.screen = screen;

        loadXml();

        Point mapCenter = screen.getScenario().getGameSurvey().getCameraPosition();
        this.cameraPosition = new Point(mapCenter.x * mapZoomMax, mapCenter.y * mapZoomMax);

        this.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.addListener(new InputEventListener());
        this.addListener(new GetScrollFocusWhenEntered(this));
    }

    private Texture getMapTile(String mapName, String fileName) {
        if (mapTiles.containsKey(fileName)) {
            return mapTiles.get(fileName);
        }
        Texture t = new Texture(Gdx.files.external(DATA_PATH + mapName + File.separator + fileName + ".jpg"));
        mapTiles.put(fileName, t);
        return t;
    }

    private Map<Point, String> getTerrainPicturesShown(GameMap map) {
        float noTilesX = MathUtils.ceil(screen.getWidth() / map.getZoom());
        float noTilesY = MathUtils.ceil(screen.getHeight() / map.getZoom());
        float noImagesX = MathUtils.ceil(noTilesX / map.getTileInEachImage());
        float noImagesY = MathUtils.ceil(noTilesY / map.getTileInEachImage());

        int xLo = (int) ((float) cameraPosition.x / mapZoomMax * map.getImageCount() / map.getWidth() - noImagesX / 2 - 1);
        int xHi = (int) ((float) cameraPosition.x / mapZoomMax * map.getImageCount() / map.getWidth() + noImagesX / 2 + 1);
        int yLo = (int) ((float) cameraPosition.y / mapZoomMax * map.getImageCount() / map.getHeight() - noImagesY / 2 - 1);
        int yHi = (int) ((float) cameraPosition.y / mapZoomMax * map.getImageCount() / map.getHeight() + noImagesY / 2 + 1);

        Map<Point, String> results = new HashMap<>();

        for (int y = yLo; y <= yHi; ++y) {
            for (int x = xLo; x <= xHi; ++x) {
                results.put(new Point(x - xLo, yHi - y), Integer.toString(y * map.getImageCount() + x));
            }
        }

        return results;
    }

    public void draw(Batch batch, float parentAlpha) {
        this.drawChildren(batch, parentAlpha);

        GameMap map = screen.getScenario().getGameMap();

        int imageSize = map.getZoom() * map.getTileInEachImage();
        int offsetX = imageSize - (int) (getWidth() / 2) % imageSize;
        int offsetY = imageSize - (int) (getHeight() / 2) % imageSize;

        for (Map.Entry<Point, String> e : getTerrainPicturesShown(map).entrySet()) {
            Texture texture = getMapTile(map.getFileName(), e.getValue());
            batch.draw(texture, e.getKey().x * imageSize - offsetX, e.getKey().y * imageSize - offsetY, imageSize, imageSize);
        }
    }

    public void dispose() {
        mapTiles.values().forEach(Texture::dispose);
    }

    private class InputEventListener extends InputListener {

        @Override
        public boolean scrolled(InputEvent event, float x, float y, int amount) {
            int newZoom = screen.getScenario().getGameMap().getZoom();
            newZoom = MathUtils.clamp(newZoom + amount * mapMouseScrollFactor, mapZoomMin, mapZoomMax);
            screen.getScenario().getGameMap().setZoom(newZoom);
            return true;
        }
    }

}
