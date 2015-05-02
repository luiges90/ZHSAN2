package com.zhsan.gamecomponents.contextmenu;

import com.badlogic.gdx.Gdx;
import com.zhsan.gamecomponents.textdialog.ConfirmationDialog;
import com.zhsan.gamecomponents.textdialog.TextDialog;
import com.zhsan.screen.GameScreen;

/**
 * Methods called from ContextMenu left click callbacks. All these methods are called by reflection.
 * Created by Peter on 1/4/2015.
 */
public final class ContextMenuMethods {

    private ContextMenuMethods(){}

    public static void SystemMenu_Quit(GameScreen screen, Object object) {
        screen.showConfirmationDialog(TextDialog.TextKeys.EXIT_GAME, new ConfirmationDialog.OnDismissListener() {
            @Override
            public void onDismiss(boolean confirmed) {
                if (confirmed) {
                    Gdx.app.exit();
                }
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

    public static void MapRightClick_System(GameScreen screen, Object object) {
        screen.showContextMenu(ContextMenu.MenuKindType.SYSTEM_MENU, null);
    }

    public static void MapRightClick_Save(GameScreen screen, Object object) {
        screen.showSaveGameFrame();
    }

    public static void MapRightClick_Load(GameScreen screen, Object object) {
        screen.showLoadGameFrame();
    }

}
