package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.graphics.Texture;
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
}
