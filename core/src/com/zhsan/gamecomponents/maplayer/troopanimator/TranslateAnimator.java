package com.zhsan.gamecomponents.maplayer.troopanimator;

import com.zhsan.common.Point;
import com.zhsan.gamecomponents.maplayer.MapLayer;
import com.zhsan.gamecomponents.maplayer.TroopAnimationLayer;

/**
 * Created by Peter on 29/8/2015.
 */
public class TranslateAnimator implements Animator {

    private boolean completed;

    private MapLayer.DrawingHelpers helpers;
    private TroopAnimationLayer.PendingTroopAnimation animation;

    public TranslateAnimator(MapLayer.DrawingHelpers helpers, TroopAnimationLayer.PendingTroopAnimation animation) {
        this.helpers = helpers;
        this.animation = animation;
    }

    private int step = 0;

    public Point step() {
        step++;
        float ratio = (float) step / FRAME_COUNT;

        Point drawStart = helpers.getPixelFromMapLocation(animation.from);
        Point drawEnd = helpers.getPixelFromMapLocation(animation.to);
        Point drawPos = new Point((int) (drawStart.x * (1 - ratio) + drawEnd.x * ratio), (int) (drawStart.y * (1 - ratio) + drawEnd.y * ratio));
        completed = step == FRAME_COUNT;
        return drawPos;
    }

    public boolean isCompleted() {
        return completed;
    }

    public TroopAnimationLayer.PendingTroopAnimation getAnimation() {
        return animation;
    }
}
