package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * Created by Peter on 21/3/2015.
 */
public class GetKeyFocusWhenEntered extends InputListener {

    private Actor actor;

    public GetKeyFocusWhenEntered(Actor actor) {
        this.actor = actor;
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        actor.getStage().setKeyboardFocus(actor);
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        actor.getStage().setKeyboardFocus(null);
    }

}
