package com.zhsan.gamecomponents.maplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.zhsan.common.Point;
import com.zhsan.gameobject.GameScenario;
import com.zhsan.gameobject.Troop;
import com.zhsan.screen.GameScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 11/8/2015.
 */
public class HighlightLayer implements MapLayer {

    private GameScenario scenario;

    private Texture moveTo;
    private List<Point> moveToHighlight = new ArrayList<>();

    public HighlightLayer(GameScenario scenario) {
        this.moveTo = new Texture(Gdx.files.external(MainMapLayer.DATA_PATH + "MoveTo.png"));
        this.scenario = scenario;
    }

    @Override
    public void draw(GameScreen screen, String resPack, DrawingHelpers helpers,
                     int zoom, Batch batch, float parentAlpha) {
        moveToHighlight.stream().filter(helpers::isMapLocationOnScreen).forEach(p -> {
            Point px = helpers.getPixelFromMapLocation(p);
            batch.draw(moveTo, px.x, px.y, zoom, zoom);
        });
    }

    @Override
    public void dispose() {
        moveTo.dispose();
    }

    @Override
    public void onStartSelectingLocation(Troop troop) {
        moveToHighlight = scenario.getPathFinder().getPointsWithinCost(
                troop.getLocation(), troop.getMilitary().getKind().getMovability());
    }

    @Override
    public void onEndSelectingLocation() {
        moveToHighlight.clear();
    }
}
