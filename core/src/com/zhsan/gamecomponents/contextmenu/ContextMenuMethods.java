package com.zhsan.gamecomponents.contextmenu;

import com.badlogic.gdx.Gdx;
import com.zhsan.gamecomponents.textdialog.TextDialog;
import com.zhsan.screen.GameScreen;

/**
 * Methods called from ContextMenu left click callbacks. All these methods are called by reflection.
 * Created by Peter on 1/4/2015.
 */
public final class ContextMenuMethods {

    private ContextMenuMethods(){}

    public static void SystemMenu_Quit(GameScreen screen, Object object) {
        screen.showTextDialog(TextDialog.TextKeys.EXIT_GAME, new TextDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                Gdx.app.exit();
            }
        });
    }

    public static void SystemMenu_Continue(GameScreen screen, Object object) {
        // no-op. Let ContextMenu close.
    }

    public static void SystemMenu_Save(GameScreen screen, Object object) {
        screen.showSaveGameFrame();
    }

    public static void SystemMenu_Load(GameScreen screen, Object object) {
        screen.showLoadGameFrame();
    }

    public static void MapRightClick_Save(GameScreen screen, Object object) {
        screen.showSaveGameFrame();
    }

    public static void MapRightClick_Load(GameScreen screen, Object object) {
        screen.showLoadGameFrame();
    }

}
