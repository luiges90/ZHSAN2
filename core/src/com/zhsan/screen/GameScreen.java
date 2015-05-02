package com.zhsan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Point;
import com.zhsan.gamecomponents.contextmenu.ContextMenu;
import com.zhsan.gamecomponents.MapLayer;
import com.zhsan.gamecomponents.gameframe.FileGameFrame;
import com.zhsan.gamecomponents.textdialog.ConfirmationDialog;
import com.zhsan.gamecomponents.textdialog.TextDialog;
import com.zhsan.gamecomponents.toolbar.ToolBar;
import com.zhsan.gameobject.GameScenario;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameScreen extends WidgetGroup {

    private GameScenario scen;

    private MapLayer mapLayer;
    private ContextMenu contextMenu;

    private FileGameFrame saveGameFrame, loadGameFrame;
    private TextDialog textDialog;
    private ConfirmationDialog confirmationDialog;
    private ToolBar toolBar;

    public GameScreen(GameScenario scen) {
        this.scen = scen;

        toolBar = new ToolBar(this);
        toolBar.setPosition(0, 0);
        toolBar.setWidth(Gdx.graphics.getWidth());

        mapLayer = new MapLayer(this);
        mapLayer.setPosition(0, getToolBarHeight());
        mapLayer.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - getToolBarHeight());
        this.addActor(mapLayer);

        contextMenu = new ContextMenu(this);
        contextMenu.setPosition(0, getToolBarHeight());
        contextMenu.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - getToolBarHeight());
        this.addActor(contextMenu);

        textDialog = new TextDialog(this);
        this.addActor(textDialog);

        confirmationDialog = new ConfirmationDialog(this);
        this.addActor(confirmationDialog);

        this.addActor(toolBar);
    }

    public void resize(int width, int height) {
        mapLayer.setPosition(0, getToolBarHeight());
        mapLayer.setSize(width, height - getToolBarHeight());
        mapLayer.resize(width, height);

        contextMenu.setPosition(0, getToolBarHeight());
        contextMenu.setSize(width, height - getToolBarHeight());
        contextMenu.resize(width, height);

        toolBar.setPosition(0, 0);
        toolBar.setWidth(width);
        toolBar.resize(width, height);

        if (saveGameFrame != null) {
            saveGameFrame.resize(width, height);
        }
    }

    public void showContextMenu(ContextMenu.MenuKindType type, Point position) {
        contextMenu.show(type, scen, position);
    }

    public void showContextMenu(ContextMenu.MenuKindType type, Object item, Point position) {
        contextMenu.show(type, item, position);
    }

    public void showSaveGameFrame() {
        if (saveGameFrame == null) {
            saveGameFrame = new FileGameFrame(FileGameFrame.Usage.SAVE, scen::save);
            this.addActor(saveGameFrame);
        } else {
            saveGameFrame.show();
        }
    }

    public void showLoadGameFrame() {
        if (loadGameFrame == null) {
            loadGameFrame = new FileGameFrame(FileGameFrame.Usage.LOAD, file -> {
                scen = new GameScenario(file);
            });
            this.addActor(loadGameFrame);
        } else {
            loadGameFrame.show();
        }
    }

    public GameScenario getScenario() {
        return scen;
    }

    public void showTextDialog(String content, TextDialog.OnDismissListener onDismissListener) {
        textDialog.show(content, onDismissListener);
    }

    public void showTextDialog(TextDialog.TextKeys key, TextDialog.OnDismissListener onDismissListener) {
        textDialog.show(key, onDismissListener);
    }

    public void showConfirmationDialog(String content, ConfirmationDialog.OnDismissListener onDismissListener) {
        confirmationDialog.show(content, onDismissListener);
    }

    public void showConfirmationDialog(TextDialog.TextKeys key, ConfirmationDialog.OnDismissListener onDismissListener) {
        confirmationDialog.show(key, onDismissListener);
    }

    public int getToolBarHeight() {
        return toolBar.getToolbarHeight();
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public MapLayer getMapLayer() {
        return mapLayer;
    }

    public void dispose() {
        toolBar.dispose();
        mapLayer.dispose();
        contextMenu.dispose();
        textDialog.dispose();
    }

}
