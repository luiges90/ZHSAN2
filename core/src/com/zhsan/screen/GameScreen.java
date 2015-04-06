package com.zhsan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Point;
import com.zhsan.gamecomponents.ContextMenu;
import com.zhsan.gamecomponents.MapLayer;
import com.zhsan.gamecomponents.gameframe.FileGameFrame;
import com.zhsan.gameobject.GameScenario;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameScreen extends WidgetGroup {

    private GameScenario scen;

    private MapLayer mapLayer;
    private ContextMenu contextMenu;

    private FileGameFrame saveGameFrame, loadGameFrame;

    public GameScreen(GameScenario scen) {
        this.scen = scen;

        mapLayer = new MapLayer(this);
        this.addActor(mapLayer);

        contextMenu = new ContextMenu(this);
        this.addActor(contextMenu);
    }

    public void showContextMenu(ContextMenu.MenuKindType type, Point position) {
        contextMenu.show(type, scen, position);
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
        if (saveGameFrame == null) {
            saveGameFrame = new FileGameFrame(FileGameFrame.Usage.LOAD, file -> {

            });
            this.addActor(saveGameFrame);
        } else {
            saveGameFrame.show();
        }
    }

    public GameScenario getScenario() {
        return scen;
    }

    public void resize(int width, int height) {
        mapLayer.resize(width, height);
        contextMenu.resize(width, height);
        if (saveGameFrame != null) {
            saveGameFrame.resize(width, height);
        }
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        mapLayer.dispose();
        contextMenu.dispose();
    }

}
