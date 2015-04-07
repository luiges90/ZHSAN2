package com.zhsan.gameobject;

/**
 * Created by Peter on 17/3/2015.
 */
public abstract class GameObject {

    public final int id;

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

}
