package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import org.w3c.dom.Node;

import java.io.File;
import java.util.EnumMap;
import java.util.function.BiConsumer;

/**
 * Created by Peter on 7/3/2015.
 */
public class StateTexture {

    public static enum State {
        NORMAL, SELECTED, DISABLED
    }

    private final Texture normal, selected, disabled;
    private State state = State.NORMAL;

    public StateTexture(Texture normal, Texture selected, Texture disabled) {
        this.normal = normal;
        this.selected = selected;
        this.disabled = disabled;
    }

    public StateTexture(StateTexture old) {
        this.normal = old.normal;
        this.selected = old.selected;
        this.disabled = old.disabled;
    }

    public void setState(State s) {
        if (s == State.DISABLED && disabled == null) {
            throw new IllegalStateException("No disabled texture is set for this StateTexture");
        }
        this.state = s;
    }

    public Texture get() {
        switch (state) {
            case NORMAL:
                return normal;
            case SELECTED:
                return selected;
            case DISABLED:
                return disabled;
            default: throw new AssertionError();
        }
    }

    public State getState() {
        return state;
    }

    public static StateTexture fromXml(String path, Node node) {
        String normal = XmlHelper.loadAttribute(node, "FileName");
        String selected = XmlHelper.loadAttribute(node, "Selected");
        String disabled = XmlHelper.loadAttribute(node, "Disabled", null);

        FileHandle f;
        f = Gdx.files.external(path + File.separator + normal);
        Texture tNormal = new Texture(f);

        f = Gdx.files.external(path + File.separator + selected);
        Texture tSelected = new Texture(f);

        Texture tDisabled = null;
        if (disabled != null) {
            f = Gdx.files.external(path + File.separator + disabled);
            tDisabled = new Texture(f);
        }

        return new StateTexture(tNormal, tSelected, tDisabled);
    }

    public void dispose() {
        normal.dispose();
        selected.dispose();
        if (disabled != null) {
            disabled.dispose();
        }
    }

}
