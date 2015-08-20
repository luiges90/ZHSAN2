package com.zhsan.gamecomponents.contextmenu;

import com.badlogic.gdx.Gdx;
import com.zhsan.gamecomponents.gameframe.TabListGameFrame;
import com.zhsan.gamecomponents.textdialog.ConfirmationDialog;
import com.zhsan.gamecomponents.textdialog.TextDialog;
import com.zhsan.gameobject.Architecture;
import com.zhsan.gameobject.GameObjectList;
import com.zhsan.gameobject.GameScenario;
import com.zhsan.gameobject.Troop;
import com.zhsan.screen.GameScreen;

import java.util.Objects;

/**
 * Methods called from ContextMenu left click callbacks. All these methods are called by reflection.
 * Created by Peter on 1/4/2015.
 */
public final class ContextMenuMethods {

    private ContextMenuMethods(){}

    public static void SystemMenu_Quit(GameScreen screen, Object object) {
        screen.showConfirmationDialog(TextDialog.TextKeys.EXIT_GAME, confirmed -> {
            if (confirmed) {
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

    public static void MapRightClick_DateGo_1Day(GameScreen screen, Object object) {
        screen.getDayRunner().runDays(1);
    }

    public static void MapRightClick_DateGo_2Days(GameScreen screen, Object object) {
        screen.getDayRunner().runDays(2);
    }

    public static void MapRightClick_DateGo_5Days(GameScreen screen, Object object) {
        screen.getDayRunner().runDays(5);
    }

    public static void MapRightClick_DateGo_10Days(GameScreen screen, Object object) {
        screen.getDayRunner().runDays(10);
    }

    public static void MapRightClick_DateGo_30Days(GameScreen screen, Object object) {
        screen.getDayRunner().runDays(30);
    }

    public static void MapRightClick_Information_AllArchitectures(GameScreen screen, Object object) {
        screen.showTabList(TabListGameFrame.ListKindType.ARCHITECTURE, screen.getScenario().getArchitectures());
    }

    public static void MapRightClick_Information_AllPersons(GameScreen screen, Object object) {
        screen.showTabList(TabListGameFrame.ListKindType.PERSON, screen.getScenario().getAvailablePersons());
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

    public static void ArchitectureRightClick_Architecture_AllPersons(GameScreen screen, Object object) {
        screen.showTabList(TabListGameFrame.ListKindType.PERSON, ((Architecture) object).getPersons());
    }

    public static void ArchitectureRightClick_Architecture_AllUnhiredPersons(GameScreen screen, Object object) {
        screen.showTabList(TabListGameFrame.ListKindType.PERSON, ((Architecture) object).getUnhiredPersons());
    }

    public static void ArchitectureRightClick_Architecture_AllMilitaries(GameScreen screen, Object object) {
        screen.showTabList(TabListGameFrame.ListKindType.MILITARY, ((Architecture) object).getMilitaries());
    }

    public static void ArchitectureRightClick_Faction_AllPersons(GameScreen screen, Object object) {
        screen.showTabList(TabListGameFrame.ListKindType.PERSON, ((Architecture) object).getBelongedFaction().getPersons());
    }

    public static void ArchitectureRightClick_Faction_AllMilitaries(GameScreen screen, Object object) {
        screen.showTabList(TabListGameFrame.ListKindType.MILITARY, ((Architecture) object).getBelongedFaction().getMilitaries());
    }

    public static void ArchitectureRightClick_Faction_AllTroops(GameScreen screen, Object object) {
        screen.showTabList(TabListGameFrame.ListKindType.TROOP, ((Architecture) object).getBelongedFaction().getTroops());
    }

    public static void TroopLeftClick_Move(GameScreen screen, Object object) {
        screen.getMapLayer().startSelectingLocation((Troop) object, p -> {
            if (p != null) {
                ((Troop) object).giveMoveToOrder(p);
            }
        });
    }

    public static void TroopLeftClick_Attack(GameScreen screen, Object object) {
        Troop troop = (Troop) object;
        screen.getMapLayer().startSelectingLocation(troop, p -> {
            if (p != null) {
                Architecture a = screen.getScenario().getArchitectureAt(p);
                Troop t = screen.getScenario().getTroopAt(p);
                if (a == null && t == null) {
                    troop.giveAttackOrder(p);
                } else if (t == null) {
                    // TODO disambiguate point by another menu
                    troop.giveAttackOrder(a);
                } else if (a == null) {
                    // TODO disambiguate point by another menu
                    troop.giveAttackOrder(t);
                } else {
                    // TODO disambiguate by another menu
                    troop.giveAttackOrder(t);
                }
            }
        });
    }

    public static void TroopRightClick_TroopDetail(GameScreen screen, Object object) {
        screen.showTabList(TabListGameFrame.ListKindType.TROOP, GameObjectList.singleton((Troop) object));
    }

    public static void TroopRightClick_TroopMilitary(GameScreen screen, Object object) {
        screen.showTabList(TabListGameFrame.ListKindType.MILITARY, GameObjectList.singleton(((Troop) object).getMilitary()));
    }

}
