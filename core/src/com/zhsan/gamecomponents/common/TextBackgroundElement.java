package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.zhsan.common.Fonts;
import org.w3c.dom.Node;

/**
 * Created by Peter on 7/3/2015.
 */
public final class TextBackgroundElement {

    private final TextElement element;

    private final Texture background;
    private final int width;
    private final int height;

    public TextBackgroundElement(TextElement element, Texture background, int width, int height) {
        this.element = element;
        this.background = background;
        this.width = width;
        this.height = height;
    }

    public static TextBackgroundElement fromXml(String path, Node node) {
        TextElement element = TextElement.fromXml(node);

        FileHandle fh = Gdx.files.external(path + node.getAttributes().getNamedItem("FileName").getNodeValue());
        Texture background = new Texture(fh);

        int width = Integer.parseInt(node.getAttributes().getNamedItem("Width").getNodeValue());
        int height = Integer.parseInt(node.getAttributes().getNamedItem("Height").getNodeValue());

        return new TextBackgroundElement(element, background, width, height);
    }

    public BitmapFont getFont() {
        return element.getFont();
    }

    public void applyColorSize() {
        element.applyColorSize();
    }

    public BitmapFont.HAlignment getAlign() {
        return element.getAlign();
    }

    public Texture getBackground() {
        return background;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
