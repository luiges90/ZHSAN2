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
import com.zhsan.gameobject.Architecture;
import com.zhsan.gameobject.ArchitectureKind;
import com.zhsan.gameobject.GameMap;
import com.zhsan.gameobject.TerrainDetail;
import com.zhsan.screen.GameScreen;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.List;
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

    public static final String MAP_TILE_PATH = Paths.RESOURCES + "Map" + File.separator;
    public static final String ARCHITECTURE_RES_PATH = Paths.RESOURCES + "Architecture" + File.separator;

    public static final String DATA_PATH = MAP_TILE_PATH + "Data" + File.separator;

    private static final class ArchitectureImageQuantifier {
        private enum Quantifier { DEFAULT, DIAGONAL_SQUARE, HORIZONTAL, VERTICAL }

        public final Quantifier quantifier;
        public final int size;

        public ArchitectureImageQuantifier(@NotNull Quantifier quantifier, int size) {
            this.quantifier = quantifier;
            this.size = size;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ArchitectureImageQuantifier that = (ArchitectureImageQuantifier) o;

            if (size != that.size) return false;
            return quantifier == that.quantifier;

        }

        @Override
        public int hashCode() {
            int result = quantifier.hashCode();
            result = 31 * result + size;
            return result;
        }
    }

    private Map<Pair<ArchitectureKind, ArchitectureImageQuantifier>, Texture> architectureImages = new HashMap<>();
    private Map<String, Texture> mapTiles = new HashMap<>();

    private int mapZoomMin, mapZoomMax, mapScrollBoundary, mapMouseScrollFactor;
    private float mapScrollFactor;

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
        FileHandle f = Gdx.files.external(MAP_TILE_PATH + "MapLayerData.xml");

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
            throw new FileReadException(MAP_TILE_PATH + "MapLayerData.xml", e);
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
        Texture t = new Texture(Gdx.files.external(MAP_TILE_PATH + mapName + File.separator + fileName + ".jpg"));
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
        // draw map tiles
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

        {
            int px = (int) (mousePosition.x + getX() + offsetX) / map.getZoom() + xLo * map.getTileInEachImage();
            int py = map.getHeight() - 1 - ((int) (mousePosition.y + getY() + offsetY) / map.getZoom() + yLo * map.getTileInEachImage());

            TerrainDetail terrain = map.getTerrainAt(px, py);
            if (terrain != null) {
                String text = String.format(mapInfoFormat, terrain.getName(), px, py);
                mapInfo.setText(text);
            } else {
                mapInfo.setText("");
            }
        }

        // draw architectures
        for (Architecture a : screen.getScenario().getArchitectures()) {
            Point mapCenter = Point.getCenter(a.getLocation());
            if (xLo * map.getTileInEachImage() <= mapCenter.x && mapCenter.x <= (xHi + 1) * map.getTileInEachImage() &&
                    yLo * map.getTileInEachImage() <= (map.getHeight() - mapCenter.y + 1) &&
                    (map.getHeight() - mapCenter.y + 1) <= (yHi + 1) * map.getTileInEachImage()) {
                Pair<ArchitectureImageQuantifier, Texture> image =
                        getArchitectureImage(screen.getScenario().getGameSurvey().getResourcePackName(),
                        a.getKind(), a.getLocation());

                int px = (mapCenter.x - xLo * map.getTileInEachImage()) * zoom - offsetX + zoom / 2;
                int py = ((map.getHeight() - 1 - mapCenter.y) - yLo * map.getTileInEachImage()) * zoom - offsetY + zoom / 2;
                int sx, sy;
                switch (image.getLeft().quantifier) {
                    case DEFAULT:
                        sx = (int) (zoom * (1 + a.getKind().getDrawOffsetWidth()));
                        sy = (int) (zoom * (1 + a.getKind().getDrawOffsetLength()));
                        break;
                    case HORIZONTAL:
                        sx = (int) (zoom * (image.getLeft().size + a.getKind().getDrawOffsetLength()));
                        sy = (int) (zoom * (1 + a.getKind().getDrawOffsetWidth()));
                        break;
                    case VERTICAL:
                        sx = (int) (zoom * (1 + a.getKind().getDrawOffsetWidth()));
                        sy = (int) (zoom * (image.getLeft().size + a.getKind().getDrawOffsetLength()));
                        break;
                    case DIAGONAL_SQUARE:
                        sx = (int) (zoom * (image.getLeft().size * 2 + 1 + a.getKind().getDrawOffsetWidth()));
                        sy = (int) (zoom * (image.getLeft().size * 2 + 1 + a.getKind().getDrawOffsetLength()));
                        break;
                    default:
                        sx = (int) (zoom * (1 + a.getKind().getDrawOffsetWidth()));
                        sy = (int) (zoom * (1 + a.getKind().getDrawOffsetLength()));
                        break;
                }
                batch.draw(image.getRight(), px - sx / 2, py - sy / 2, sx, sy);
            }

        }

        // draw childrens
        super.draw(batch, parentAlpha);
    }

    private Texture getArchitectureImage(String resSet, ArchitectureKind kind, ArchitectureImageQuantifier quantifier) {
        if (!architectureImages.containsKey(new ImmutablePair<>(kind, quantifier))) {
            String name = String.valueOf(kind.getId());
            String defaultName = name;
            switch (quantifier.quantifier) {
                case DIAGONAL_SQUARE:
                    name += "-d" + quantifier.size;
                    break;
                case HORIZONTAL:
                    name += "-h" + quantifier.size;
                    break;
                case VERTICAL:
                    name += "-v" + quantifier.size;
                    break;
                case DEFAULT:
                    break;
            }
            name += ".png";
            defaultName += ".png";
            FileHandle f = Gdx.files.external(ARCHITECTURE_RES_PATH + resSet + File.separator + name);
            if (!f.exists()) {
                f = Gdx.files.external(ARCHITECTURE_RES_PATH + resSet + File.separator + defaultName);
            }
            Texture t = new Texture(f);
            architectureImages.put(new ImmutablePair<>(kind, quantifier), t);
        }
        return architectureImages.get(new ImmutablePair<>(kind, quantifier));
    }

    private Pair<ArchitectureImageQuantifier, Texture> getArchitectureImage(String resSet, ArchitectureKind kind, List<Point> shape) {
        if (shape.size() == 1) {
            ArchitectureImageQuantifier q = new ArchitectureImageQuantifier(ArchitectureImageQuantifier.Quantifier.DEFAULT, 0);
            return new ImmutablePair<>(q, getArchitectureImage(resSet, kind, q));
        }

        // is horizontal?
        int h = shape.get(0).y;
        boolean horizontal = true;
        for (Point p : shape) {
            if (p.y != h) {
                horizontal = false;
            }
        }
        if (horizontal) {
            ArchitectureImageQuantifier q = new ArchitectureImageQuantifier(ArchitectureImageQuantifier.Quantifier.HORIZONTAL, shape.size());
            return new ImmutablePair<>(q, getArchitectureImage(resSet, kind, q));
        }

        // is vertical?
        int v = shape.get(0).x;
        boolean vertical = true;
        for (Point p : shape) {
            if (p.x != v) {
                vertical = false;
            }
        }
        if (vertical) {
            ArchitectureImageQuantifier q = new ArchitectureImageQuantifier(ArchitectureImageQuantifier.Quantifier.VERTICAL, shape.size());
            return new ImmutablePair<>(q, getArchitectureImage(resSet, kind, q));
        }

        // is diagonal square?
        Point center = Point.getCenter(shape);
        if (shape.contains(center)) {
            for (int i = 1; ; ++i) {
                int count = 0;
                for (int j = 0; j < i; ++j) {
                    // bottom to right diagonal
                    if (shape.contains(new Point(center.x + j, center.y - i + j))) {
                        count++;
                    }
                    // right to top diagonal
                    if (shape.contains(new Point(center.x + i - j, center.y + j))) {
                        count++;
                    }
                    // top to left diagonal
                    if (shape.contains(new Point(center.x - j, center.y + i - j))) {
                        count++;
                    }
                    // left to bottom diagonal
                    if (shape.contains(new Point(center.x - i + j, center.y - j))) {
                        count++;
                    }
                }
                if (count == 0) {
                    // all empty, a diagonal square ends.
                    ArchitectureImageQuantifier q = new ArchitectureImageQuantifier(ArchitectureImageQuantifier.Quantifier.DIAGONAL_SQUARE, i);
                    return new ImmutablePair<>(q, getArchitectureImage(resSet, kind, q));
                } else if (count < 4 * i) {
                    // not a diagonal square
                    ArchitectureImageQuantifier q = new ArchitectureImageQuantifier(ArchitectureImageQuantifier.Quantifier.DEFAULT, 0);
                    return new ImmutablePair<>(q, getArchitectureImage(resSet, kind, q));
                } // else 4 * i == count, diagonal square size increase, next iteration
            }
        }

        // no match
        ArchitectureImageQuantifier q = new ArchitectureImageQuantifier(ArchitectureImageQuantifier.Quantifier.DEFAULT, 0);
        return new ImmutablePair<>(q, getArchitectureImage(resSet, kind, q));
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

            if (x < mapScrollBoundary && y > toolBar.getToolbarHeight()) {
                moveStateX = MoveStateX.LEFT;
            } else if (x > getWidth() - mapScrollBoundary && y > toolBar.getToolbarHeight()) {
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
