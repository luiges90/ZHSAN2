package com.zhsan.gamecomponents.maplayer.tileanimator;

import com.zhsan.common.Point;
import com.zhsan.gamecomponents.maplayer.MapLayer;
import com.zhsan.gamecomponents.maplayer.TroopAnimationLayer;
import com.zhsan.gamecomponents.maplayer.troopanimator.Animator;
import com.zhsan.gameobject.TroopAnimation;

/**
 * Created by Peter on 29/8/2015.
 */
public class OneShotAnimator implements TileAnimator {

    private int step;
    private Point point;
    private TroopAnimation kind;

    public OneShotAnimator(Point point, TroopAnimation kind) {
        this.point = point;
        this.kind = kind;
    }

    @Override
    public boolean isCompleted() {
        return step == kind.getFrameCount() * kind.getIdleFrame();
    }

    @Override
    public Point step() {
        step++;
        return point;
    }

    @Override
    public TroopAnimation getAnimation() {
        return kind;
    }

    @Override
    public int getCurrentFrame() {
        return step;
    }
}
