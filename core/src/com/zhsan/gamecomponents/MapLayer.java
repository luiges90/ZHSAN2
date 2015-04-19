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
import com.zhsan.gamecomponents.contextmenu.ContextMenu;
import com.zhsan.gamecomponents.toolbar.ToolBar;
import com.zhsan.gameobject.*;
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

    public static final String CAPTION_FOLDER_NAME = "Caption";

    private enum MoveStateX {
        IDLE, LEFT, RIGHT
    }
    private enum MoveStateY {
        IDLE, TOP, BOTTOM
    }
    private enum ZoomState {
        IDLE, IN, OUT
    }

    public static final String MAP_ROOT_PATH = Paths.RESOURCES + "Map" + File.separator;
    public static final String ARCHITECTURE_RES_PATH = Paths.RESOURCES + "Architecture" + File.separator;

    public static final String DATA_PATH = MAP_ROOT_PATH + "Data" + File.separator;

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
    private Map<String, Texture> architectureNameImages = new HashMap<>();
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

    private float captionSize;

    private void loadXml() {
        FileHandle f = Gdx.files.external(MAP_ROOT_PATH + "MapLayerData.xml");

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

            captionSize = Float.parseFloat(XmlHelper.loadAttribute(dom.getElementsByTagName("Caption").item(0), "Size"));

        } catch (Exception e) {
            throw new FileReadException(MAP_ROOT_PATH + "MapLayerData.xml", e);
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
        Texture t = new Texture(Gdx.files.external(MAP_ROOT_PATH + mapName + File.separator + fileName + ".jpg"));
        mapTiles.put(fileName, t);
        return t;
    }

    public void setMapCameraPosition(Point p) {
        this.mapCameraPosition = new Vector2(p.x * mapZoomMax, (screen.getScenario().getGameMap().getHeight() - 1 - p.y) * mapZoomMax);
        screen.getScenario().getGameSurvey().setCameraPosition(
                new Point((int) (mapCameraPosition.x / mapZoomMax), (int) (mapCameraPosition.y / mapZoomMax))
        );
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

    private int mapDrawOffsetX, mapDrawOffsetY, imageLoX, imageLoY;

    private Point mouseOnMapPosition() {
        GameMap map = screen.getScenario().getGameMap();

        int px = (int) (mousePosition.x + getX() + mapDrawOffsetX) / map.getZoom() + imageLoX * map.getTileInEachImage();
        int py = map.getHeight() - 1 - ((int) (mousePosition.y + getY() + mapDrawOffsetY) / map.getZoom() + imageLoY * map.getTileInEachImage());
        return new Point(px, py);
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

        mapDrawOffsetX = offsetX;
        mapDrawOffsetY = offsetY;
        imageLoX = xLo;
        imageLoY = yLo;

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
            Point p = mouseOnMapPosition();
            int px = p.x;
            int py = p.y;

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
            String resPack = screen.getScenario().getGameSurvey().getResourcePackName();
            if (xLo * map.getTileInEachImage() <= mapCenter.x && mapCenter.x <= (xHi + 1) * map.getTileInEachImage() &&
                    yLo * map.getTileInEachImage() <= (map.getHeight() - mapCenter.y + 1) &&
                    (map.getHeight() - mapCenter.y + 1) <= (yHi + 1) * map.getTileInEachImage()) {
                // draw architecture main
                Pair<ArchitectureImageQuantifier, Texture> image =
                        getArchitectureImage(resPack, a.getKind(), a.getLocation());

                int mainX = (mapCenter.x - xLo * map.getTileInEachImage()) * zoom - offsetX + zoom / 2;
                int mainY = ((map.getHeight() - 1 - mapCenter.y) - yLo * map.getTileInEachImage()) * zoom - offsetY + zoom / 2;
                int mainSizeX, mainSizeY;
                int mainSizeYNoOffset;
                switch (image.getLeft().quantifier) {
                    case DEFAULT:
                        mainSizeX = (int) (zoom * (1 + a.getKind().getDrawOffsetWidth()));
                        mainSizeY = (int) (zoom * (1 + a.getKind().getDrawOffsetLength()));
                        mainSizeYNoOffset = zoom;
                        break;
                    case HORIZONTAL:
                        mainSizeX = (int) (zoom * (image.getLeft().size + a.getKind().getDrawOffsetLength()));
                        mainSizeY = (int) (zoom * (1 + a.getKind().getDrawOffsetWidth()));
                        mainSizeYNoOffset = zoom;
                        break;
                    case VERTICAL:
                        mainSizeX = (int) (zoom * (1 + a.getKind().getDrawOffsetWidth()));
                        mainSizeY = (int) (zoom * (image.getLeft().size + a.getKind().getDrawOffsetLength()));
                        mainSizeYNoOffset = zoom * image.getLeft().size;
                        break;
                    case DIAGONAL_SQUARE:
                        mainSizeX = (int) (zoom * (image.getLeft().size * 2 + 1 + a.getKind().getDrawOffsetWidth()));
                        mainSizeY = (int) (zoom * (image.getLeft().size * 2 + 1 + a.getKind().getDrawOffsetLength()));
                        mainSizeYNoOffset = zoom * (image.getLeft().size * 2 + 1);
                        break;
                    default:
                        mainSizeX = (int) (zoom * (1 + a.getKind().getDrawOffsetWidth()));
                        mainSizeY = (int) (zoom * (1 + a.getKind().getDrawOffsetLength()));
                        mainSizeYNoOffset = zoom;
                        break;
                }
                batch.draw(image.getRight(), mainX - mainSizeX / 2, mainY - mainSizeY / 2, mainSizeX, mainSizeY);

                // draw header
                String name = a.getNameImageName();
                if (!architectureNameImages.containsKey(name)) {
                    FileHandle fh = Gdx.files.external(ARCHITECTURE_RES_PATH + resPack + File.separator + CAPTION_FOLDER_NAME + File.separator + name + ".png");
                    if (!fh.exists()) {
                        fh = Gdx.files.external(ARCHITECTURE_RES_PATH + GameSurvey.DEFAULT_RESOURCE_PACK + File.separator + CAPTION_FOLDER_NAME + File.separator + name + ".png");
                    }
                    Texture nameImage = new Texture(fh);
                    architectureNameImages.put(name, nameImage);
                }
                Texture nameImage = architectureNameImages.get(name);
                int nameImageHeight = (int) (zoom * captionSize);
                int nameImageWidth = (int) ((float) nameImage.getWidth() * nameImageHeight / nameImage.getHeight());
                batch.draw(nameImage, mainX - nameImageWidth / 2, mainY + mainSizeYNoOffset / 2 - nameImageHeight / 2,
                        nameImageWidth, nameImageHeight);
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
                if (!f.exists()) {
                    f = Gdx.files.external(ARCHITECTURE_RES_PATH + GameSurvey.DEFAULT_RESOURCE_PACK + File.separator + name);
                    if (!f.exists()) {
                        f = Gdx.files.external(ARCHITECTURE_RES_PATH + GameSurvey.DEFAULT_RESOURCE_PACK + File.separator + defaultName);
                    }
                }
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
        architectureImages.values().forEach(Texture::dispose);
        architectureNameImages.values().forEach(Texture::dispose);
    }

    private class InputEventListener extends InputListener {

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            mousePosition.set(x, y);

            Point pos = mouseOnMapPosition();
            Architecture a = screen.getScenario().getArchitectureAt(pos);

            if (a != null) {
                if (button == Input.Buttons.LEFT) {
                    screen.showContextMenu(ContextMenu.MenuKindType.ARCHITECTURE_LEFT_MENU, a, new Point(mousePosition));
                } else if (button == Input.Buttons.RIGHT) {
                    screen.showContextMenu(ContextMenu.MenuKindType.ARCHITECTURE_RIGHT_MENU, a, new Point(mousePosition));
                }
            } else {
                if (button == Input.Buttons.RIGHT) {
                    screen.showContextMenu(ContextMenu.MenuKindType.MAP_RIGHT_MENU, screen.getScenario(), new Point(mousePosition));
                }
            }

            return false;
        }

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
