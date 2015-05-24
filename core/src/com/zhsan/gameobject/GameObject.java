package com.zhsan.gameobject;

import com.zhsan.resources.GlobalStrings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by Peter on 17/3/2015.
 */
public abstract class GameObject {

    private final int id;

    protected GameObject(int id) {
        this.id = id;
    }

    public abstract String getName();

    public int getId() {
        return id;
    }

    public Object getField(String fname) {
        switch (fname) {
            case "Id":
                return id;
            case "Name":
                return getName();
            default:
                try {
                    Method m = this.getClass().getMethod("get" + fname);
                    return m.invoke(this);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    return null;
                }
        }
    }

    public String getFieldString(String name) {
        Object o = getField(name);
        if (o == null) {
            return GlobalStrings.getString(GlobalStrings.Keys.NO_CONTENT);
        } else if (o instanceof GameObject) {
            return ((GameObject) o).getName();
        } else {
            return Objects.toString(o);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "id=" + id +
                '}';
    }
}
