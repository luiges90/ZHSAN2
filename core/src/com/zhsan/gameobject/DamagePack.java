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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DamagePack that = (DamagePack) o;

        if (quantity != that.quantity) return false;
        if (destroyed != that.destroyed) return false;
        if (object != null ? !object.equals(that.object) : that.object != null) return false;
        return !(location != null ? !location.equals(that.location) : that.location != null);

    }

    @Override
    public int hashCode() {
        int result = object != null ? object.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + quantity;
        result = 31 * result + (destroyed ? 1 : 0);
        return result;
    }
}
