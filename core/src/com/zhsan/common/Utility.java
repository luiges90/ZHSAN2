package com.zhsan.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 7/3/2015.
 */
public class Utility {
    private Utility() {}

    public static Rectangle adjustRectangleByHAlignment(Rectangle in, BitmapFont.HAlignment align, float containerWidth) {
        Rectangle out = new Rectangle(in);
        switch (align) {
            case LEFT:
                break;
            case RIGHT:
                out.x = containerWidth - out.width - out.x;
                break;
            case CENTER:
                out.x = containerWidth / 2 - out.width / 2 + out.x;
                break;
        }
        return out;
    }

}
