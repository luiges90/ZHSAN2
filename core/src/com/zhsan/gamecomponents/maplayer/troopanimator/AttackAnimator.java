package com.zhsan.gamecomponents.maplayer.troopanimator;

import com.zhsan.common.Point;
import com.zhsan.gamecomponents.maplayer.MapLayer;
import com.zhsan.gamecomponents.maplayer.TroopAnimationLayer;

/**
 * Created by Peter on 29/8/2015.
 */
public class AttackAnimator implements Animator {

    private boolean completed;

    private MapLayer.DrawingHelpers helpers;
    private TroopAnimationLayer.PendingTroopAnimation animation;

    private int frameCount;

    public AttackAnimator(MapLayer.DrawingHelpers helpers, TroopAnimationLayer.PendingTroopAnimation animation, int frameCount) {
        this.helpers = helpers;
        this.animation = animation;
        this.frameCount = frameCount;
    }

    private int step = 0;

    public Point step() {
        step++;

        Point drawPos = helpers.getPixelFromMapLocation(animation.from);
        completed = step == frameCount;
        return drawPos;
    }

    public boolean isCompleted() {
        return completed;
    }

    public TroopAnimationLayer.PendingTroopAnimation getAnimation() {
        return animation;
    }

    @Override
    public int getCurrentFrame() {
        return step;
    }

}
