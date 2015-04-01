package com.zhsan.gamecomponents.common.textwidget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Peter on 1/4/2015.
 */
public class SelectableTextWidget<ExtraType> extends TextWidget<ExtraType> {

    private ShapeRenderer shapeRenderer;
    private boolean selected = false;
    private Color selectedOutlineColor;

    public SelectableTextWidget(Setting setting, String title, Color selectedOutlineColor) {
        super(setting, title);
        this.shapeRenderer = new ShapeRenderer();
        this.selectedOutlineColor = selectedOutlineColor;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (selected) {
            batch.end();
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(selectedOutlineColor);
            shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
            shapeRenderer.end();
            batch.begin();
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
