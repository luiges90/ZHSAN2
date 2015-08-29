package com.zhsan.gamecomponents.maplayer.troopanimator;

import com.zhsan.common.Point;
import com.zhsan.gamecomponents.maplayer.TroopAnimationLayer;

/**
 * Created by Peter on 29/8/2015.
 */
public interface Animator {

    public final int FRAME_COUNT = 30;

    public TroopAnimationLayer.PendingTroopAnimation getAnimation();

    public boolean isCompleted();

    public Point step();

    public default int getCurrentFrame() {
        return 0;
    }

}
