package com.zhsan.gamecomponents.maplayer.tileanimator;

import com.zhsan.common.Point;
import com.zhsan.gamecomponents.maplayer.TroopAnimationLayer;
import com.zhsan.gameobject.TroopAnimation;

/**
 * Created by Peter on 9/9/2015.
 */
public interface TileAnimator {

    public final int FRAME_COUNT = 30;

    public boolean isCompleted();

    public Point step();

    public TroopAnimation getAnimation();

    public int getCurrentFrame();

}
