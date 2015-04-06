package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by Peter on 4/4/2015.
 */
public final class WidgetUtility {
    private WidgetUtility(){}

    public static Table setupScrollpane(float x, float y, float paneWidth, float paneHeight, ScrollPane target, Texture scrollButton) {
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegion(scrollButton));

        target.setStyle(scrollPaneStyle);
        target.setFadeScrollBars(false);
        target.setOverscroll(false, false);
        target.setFlickScroll(false);

        target.addListener(new GetScrollFocusWhenEntered(target));

        Table scenarioPaneContainer = new Table();
        scenarioPaneContainer.setX(x);
        scenarioPaneContainer.setY(y);
        scenarioPaneContainer.setWidth(paneWidth);
        scenarioPaneContainer.setHeight(paneHeight);
        scenarioPaneContainer.add(target).fill().expand();
        return scenarioPaneContainer;
    }

    public static BitmapFont cloneBitmapFont(BitmapFont old) {
        BitmapFont.BitmapFontData data = new BitmapFont.BitmapFontData();
        data.ascent = old.getData().ascent;
        data.capHeight = old.getData().capHeight;
        data.descent = old.getData().descent;
        data.down = old.getData().down;
        data.flipped = old.getData().flipped;
        data.fontFile = old.getData().fontFile;
        data.lineHeight = old.getData().lineHeight;
        data.scaleX = old.getData().scaleX;
        data.scaleY = old.getData().scaleY;
        data.spaceWidth = old.getData().spaceWidth;
        data.xHeight = old.getData().xHeight;
        data.imagePaths = old.getData().imagePaths;
        for (int i = 0; i < old.getData().glyphs.length; ++i) {
            data.glyphs[i] = old.getData().glyphs[i];
        }

        return new BitmapFont(data, old.getRegions(), old.usesIntegerPositions());
    }

}
