package com.zhsan.gamecomponents.maplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.gameobject.Architecture;
import com.zhsan.gameobject.ArchitectureKind;
import com.zhsan.gameobject.GameSurvey;
import com.zhsan.screen.GameScreen;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Peter on 4/8/2015.
 */
public class ArchitectureLayer implements MapLayer {

    public static final String CAPTION_FOLDER_NAME = "Caption";
    public static final String ARCHITECTURE_RES_PATH = Paths.RESOURCES + "Architecture" + File.separator;

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

    private float captionSize;

    public ArchitectureLayer(float captionSize) {
        this.captionSize = captionSize;
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

    @Override
    public void draw(GameScreen screen, String resPack, DrawingHelpers helpers, int zoom,
                     Batch batch, float parentAlpha) {
        for (Architecture a : screen.getScenario().getArchitectures()) {
            Point mapCenter = Point.getCenter(a.getLocations());
            if (helpers.isMapLocationOnScreen(mapCenter)) {
                // draw architecture main
                Pair<ArchitectureImageQuantifier, Texture> image =
                        getArchitectureImage(resPack, a.getKind(), a.getLocations());

                Point main = helpers.getPixelFromMapLocation(mapCenter);
                int mainX = main.x + zoom / 2;
                int mainY = main.y + zoom / 2;
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
                        mainSizeX = (int) (zoom * (image.getLeft().size * 2 - 1 + a.getKind().getDrawOffsetWidth()));
                        mainSizeY = (int) (zoom * (image.getLeft().size * 2 - 1 + a.getKind().getDrawOffsetLength()));
                        mainSizeYNoOffset = zoom * (image.getLeft().size * 2 - 1);
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
    }

    @Override
    public void dispose() {
        architectureImages.values().forEach(Texture::dispose);
        architectureNameImages.values().forEach(Texture::dispose);
    }

}
