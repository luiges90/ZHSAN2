package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.GlobalVariables;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.GetKeyFocusWhenEntered;
import com.zhsan.gamecomponents.common.GetScrollFocusWhenEntered;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.toolbar.ToolBar;
import com.zhsan.gameobject.GameMap;
import com.zhsan.gameobject.TerrainDetail;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
    private enum ZoomState {
        IDLE, IN, OUT
    }

    public static final String RES_PATH = Paths.RESOURCES + "Map" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private int mapZoomMin, mapZoomMax, mapScrollBoundary, mapMouseScrollFactor;
    private float mapScrollFactor;

    private Map<String, Texture> mapTiles = new HashMap<>();

    private GameScreen screen;
    private Vector2 mapCameraPosition;
    private MoveStateX moveStateX = MoveStateX.IDLE;
    private MoveStateY moveStateY = MoveStateY.IDLE;
    private ZoomState zoomState = ZoomState.IDLE;

    private TextWidget<Void> mapInfo;
    private int mapInfoMargin;
    private String mapInfoFormat;
    private Vector2 mousePosition = new Vector2();

    private Texture grid;

    private ToolBar toolBar;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "MapLayerData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node zoom = dom.getElementsByTagName("MapZoom").item(0);
            mapZoomMin = Integer.parseInt(XmlHelper.loadAttribute(zoom, "Min"));
            mapZoomMax = Integer.parseInt(XmlHelper.loadAttribute(zoom, "Max"));

            Node scroll = dom.getElementsByTagName("MapScroll").item(0);
            mapMouseScrollFactor = Integer.parseInt(XmlHelper.loadAttribute(scroll, "MouseFactor"));
            mapScrollBoundary = Integer.parseInt(XmlHelper.loadAttribute(scroll, "Boundary"));
            mapScrollFactor = Float.parseFloat(XmlHelper.loadAttribute(scroll, "Factor"));

            Node info = dom.getElementsByTagName("MapInfo").item(0);
            mapInfo = new TextWidget<>(TextWidget.Setting.fromXml(info));
            this.addActor(mapInfo);
            mapInfoMargin = Integer.parseInt(XmlHelper.loadAttribute(info, "BottomMargin"));
            mapInfoFormat = XmlHelper.loadAttribute(info, "TextFormat");

        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "MapLayerData.xml", e);
        }
    }

    public MapLayer(GameScreen screen) {
        this.screen = screen;

        // add toolbar
        toolBar = new ToolBar(screen);

        this.setPosition(0, 0);
        this.setWidth(Gdx.graphics.getWidth());
        this.setHeight(Gdx.graphics.getHeight());

        this.addActor(toolBar);

        // init myself
        loadXml();

        mapInfo.setX(0);
        mapInfo.setY(mapInfoMargin);
        mapInfo.setWidth(Gdx.graphics.getWidth());

        grid = new Texture(Gdx.files.external(DATA_PATH + "Grid.png"));

        GameMap map = screen.getScenario().getGameMap();
        Point mapCenter = screen.getScenario().getGameSurvey().getCameraPosition();
        this.mapCameraPosition = new Vector2(mapCenter.x * mapZoomMax, (map.getHeight() - 1 - mapCenter.y) * mapZoomMax);

        this.addListener(new InputEventListener());
        this.addListener(new GetScrollFocusWhenEntered(this));
        this.addListener(new GetKeyFocusWhenEntered(this));
    }

    public void resize(int width, int height) {
        toolBar.setSize(width, height);
        toolBar.resize(width, height);

        this.setSize(width, height - toolBar.getToolbarHeight());
        mapInfo.setWidth(this.getWidth());
    }

    private Texture getMapTile(String mapName, String fileName) {
		// TODO async load tile images
        if (mapTiles.containsKey(fileName)) {
            return mapTiles.get(fileName);
        }
        Texture t = new Texture(Gdx.files.external(RES_PATH + mapName + File.separator + fileName + ".jpg"));
        mapTiles.put(fileName, t);
        return t;
    }

    private void moveLeft() {
        mapCameraPosition.add(-mapScrollFactor * GlobalVariables.scrollSpeed /
                screen.getScenario().getGameMap().getZoom(), 0);
    }

    private void moveRight() {
        mapCameraPosition.add(mapScrollFactor * GlobalVariables.scrollSpeed /
                screen.getScenario().getGameMap().getZoom(), 0);
    }

    private void moveDown() {
        mapCameraPosition.add(0, -mapScrollFactor * GlobalVariables.scrollSpeed /
                screen.getScenario().getGameMap().getZoom());
    }

    private void moveUp() {
        mapCameraPosition.add(0, mapScrollFactor * GlobalVariables.scrollSpeed /
                screen.getScenario().getGameMap().getZoom());
    }

    private void adjustZoom(int amount) {
        int newZoom = screen.getScenario().getGameMap().getZoom();
        newZoom = MathUtils.clamp(newZoom + amount * mapMouseScrollFactor, mapZoomMin, mapZoomMax);
        screen.getScenario().getGameMap().setZoom(newZoom);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        GameMap map = screen.getScenario().getGameMap();

        if (moveStateX == MoveStateX.LEFT) {
            moveLeft();
        } else if (moveStateX == MoveStateX.RIGHT) {
            moveRight();
        }

        if (moveStateY == MoveStateY.BOTTOM) {
            moveDown();
        } else if (moveStateY == MoveStateY.TOP) {
            moveUp();
        }

        if (zoomState == ZoomState.IN) {
            adjustZoom(1);
        } else if (zoomState == ZoomState.OUT) {
            adjustZoom(-1);
        }

        mapCameraPosition.x = Math.max(getWidth() / 2, mapCameraPosition.x);
        mapCameraPosition.x = Math.min(map.getWidth() * mapZoomMax - getWidth() / 2, mapCameraPosition.x);

        mapCameraPosition.y = Math.max(getHeight() / 2, mapCameraPosition.y);
        mapCameraPosition.y = Math.min(map.getHeight() * mapZoomMax - getHeight() / 2, mapCameraPosition.y);

        screen.getScenario().getGameSurvey().setCameraPosition(
                new Point((int) (mapCameraPosition.x / mapZoomMax), (int) (mapCameraPosition.y / mapZoomMax))
        );
    }

    public void draw(Batch batch, float parentAlpha) {
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
                if (x < 0 || x >= map.getImageCount()) continue;
                if (y < 0 || y >= map.getImageCount()) continue;

                int px = (x - xLo) * imageSize - offsetX;
                int py = (y - yLo) * imageSize - offsetY;

                // map
                Texture texture = getMapTile(map.getFileName(), Integer.toString((map.getImageCount() - 1 - y) * map.getImageCount() + x));
                batch.draw(texture, px, py, imageSize, imageSize);

                // grid
                if (GlobalVariables.showGrid) {
                    for (int i = 0; i < map.getTileInEachImage(); ++i) {
                        for (int j = 0; j < map.getTileInEachImage(); ++j) {
                            batch.draw(grid, px + j * zoom, py + i * zoom, zoom, zoom);
                        }
                    }
                }
            }
        }

        int px = (int) (mousePosition.x + getX() + offsetX) / map.getZoom() + xLo * map.getTileInEachImage();
        int py = map.getHeight() - 1 - ((int) (mousePosition.y + getY() + offsetY) / map.getZoom() + yLo * map.getTileInEachImage());

        TerrainDetail terrain = screen.getScenario().getGameMap().getTerrainAt(px, py);
        if (terrain != null) {
            String text = String.format(mapInfoFormat, terrain.name, px, py);
            mapInfo.setText(text);
        } else {
            mapInfo.setText("");
        }

        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        toolBar.dispose();
        mapTiles.values().forEach(Texture::dispose);
    }

    private class InputEventListener extends InputListener {

        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.MINUS) {
                zoomState = ZoomState.OUT;
            } else if (keycode == Input.Keys.EQUALS) {
                zoomState = ZoomState.IN;
            } else if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
                moveStateY = MoveStateY.TOP;
            } else if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
                moveStateX = MoveStateX.LEFT;
            } else if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
                moveStateY = MoveStateY.BOTTOM;
            } else if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
                moveStateX = MoveStateX.RIGHT;
            } else if (keycode == Input.Keys.Q) {
                GlobalVariables.showGrid = !GlobalVariables.showGrid;
            }

            return true;
        }

        @Override
        public boolean keyUp(InputEvent event, int keycode) {
            if (keycode == Input.Keys.MINUS) {
                zoomState = ZoomState.IDLE;
            } else if (keycode == Input.Keys.EQUALS) {
                zoomState = ZoomState.IDLE;
            } else if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
                moveStateY = MoveStateY.IDLE;
            } else if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
                moveStateX = MoveStateX.IDLE;
            } else if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
                moveStateY = MoveStateY.IDLE;
            } else if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
                moveStateX = MoveStateX.IDLE;
            }

            return true;
        }

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            mousePosition.set(x, y);

            // decide scroll
            moveStateX = MoveStateX.IDLE;
            moveStateY = MoveStateY.IDLE;

            if (x < mapScrollBoundary) {
                moveStateX = MoveStateX.LEFT;
            } else if (x > getWidth() - mapScrollBoundary) {
                moveStateX = MoveStateX.RIGHT;
            }
            if (toolBar.getToolbarHeight() < y && y < toolBar.getToolbarHeight() + mapScrollBoundary) {
                moveStateY = MoveStateY.BOTTOM;
            } else if (y > getHeight() - mapScrollBoundary) {
                moveStateY = MoveStateY.TOP;
            }
            return true;
        }

        @Override
        public boolean scrolled(InputEvent event, float x, float y, int amount) {
            adjustZoom(-amount);
            return true;
        }
    }

}
