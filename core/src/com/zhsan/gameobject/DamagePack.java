package com.zhsan.gameobject;

import com.zhsan.common.Point;

/**
 * Created by Peter on 22/8/2015.
 */
public class DamagePack {
    public final GameObject object;
    public final Point location;
    public final int quantity;
    public final boolean destroyed;

    public DamagePack(GameObject object, Point location, int quantity, boolean destroyed) {
        this.object = object;
        this.location = location;
        this.quantity = quantity;
        this.destroyed = destroyed;
    }
}
