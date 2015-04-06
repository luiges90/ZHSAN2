package com.zhsan.gamecomponents.common.textwidget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

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
        this.addListener(new InputListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                selected = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                selected = false;
            }
        });
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



}
