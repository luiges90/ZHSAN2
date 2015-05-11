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

    private String name;

    protected GameObject(int id) {
        this.id = id;
    }

    protected final void setName(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Object getField(String fname) {
        if (fname.equals("Id")) {
            return id;
        } else if (fname.equals("Name")) {
            return name;
        } else {
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
                ", name='" + name + '\'' +
                '}';
    }
}
