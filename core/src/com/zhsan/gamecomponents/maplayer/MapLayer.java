package com.zhsan.gamecomponents.maplayer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.zhsan.common.Point;
import com.zhsan.gameobject.Troop;
import com.zhsan.screen.GameScreen;

/**
 * Created by Peter on 4/8/2015.
 */
interface MapLayer {

    interface DrawingHelpers {
        boolean isMapLocationOnScreen(Point p);
        Point getPixelFromMapLocation(Point p);
    }

    public void draw(GameScreen screen, String resPack, DrawingHelpers helpers, int zoom,
                     Batch batch, float parentAlpha);

    public void dispose();

    public default void onStartSelectingLocation(Troop troop) {
        // no-op
    }

}
