package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Peter on 2/8/2015.
 */
public class ImageWidget<ExtraType> extends Widget implements Disposable {

    private ExtraType extra;

    private Texture texture;
    private Color borderColor;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public ImageWidget(Texture texture, Color borderColor) {
        this.texture = texture;
        this.borderColor = borderColor;
    }

    public ExtraType getExtra() {
        return extra;
    }

    public ImageWidget setExtra(ExtraType extra) {
        this.extra = extra;
        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();

        if (texture == null) {
            batch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());

            shapeRenderer.setColor(borderColor);
            shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());

            shapeRenderer.end();

            batch.begin();
        } else {
            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
