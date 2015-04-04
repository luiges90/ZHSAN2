package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.zhsan.screen.GameScreen;

/**
 * Methods called from ContextMenu left click callbacks. All these methods are called by reflection.
 * Created by Peter on 1/4/2015.
 */
public final class ContextMenuMethods {

    private ContextMenuMethods(){}

    public static void SystemMenu_Quit(GameScreen screen, Object object) {
        Gdx.app.exit();
    }

    public static void SystemMenu_Continue(GameScreen screen, Objecct object) {
        // no-op. Let ContextMenu close.
    }

}
