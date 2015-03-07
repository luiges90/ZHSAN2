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
        NORMAL, SELECTED, PRESSED, DISABLED
    }

    private final Texture normal, selected, pressed, disabled;

    public StateTexture(Texture normal, Texture selected, Texture pressed, Texture disabled) {
        this.normal = normal;
        this.selected = selected;
        this.pressed = pressed;
        this.disabled = disabled;
    }

    public Texture get(State state) {
        switch (state) {
            case NORMAL:
                return normal;
            case SELECTED:
                return selected;
            case PRESSED:
                return pressed;
            case DISABLED:
                return disabled;
            default: throw new AssertionError();
        }
    }

    public static StateTexture fromXml(String path, Node node) {
        String normal = node.getAttributes().getNamedItem("FileName").getNodeValue();
        String selected = node.getAttributes().getNamedItem("Selected").getNodeValue();
        String pressed = node.getAttributes().getNamedItem("Pressed").getNodeValue();
        String disabled = node.getAttributes().getNamedItem("Disabled").getNodeValue();

        FileHandle f;
        f = Gdx.files.external(path + File.separator + normal);
        Texture tNormal = new Texture(f);

        f = Gdx.files.external(path + File.separator + selected);
        Texture tSelected = new Texture(f);

        f = Gdx.files.external(path + File.separator + pressed);
        Texture tPressed = new Texture(f);

        f = Gdx.files.external(path + File.separator + disabled);
        Texture tDisabled = new Texture(f);

        return new StateTexture(tNormal, tSelected, tPressed, tDisabled);
    }

    public void dispose() {
        normal.dispose();
        selected.dispose();
        pressed.dispose();
        disabled.dispose();
    }

}
