package com.zhsan.gamecomponents.toolbar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.ContextMenu;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 25/3/2015.
 */
public class GameSystem extends WidgetGroup {

    public static final String RES_PATH = ToolBar.RES_PATH + "GameSystem" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private StateTexture button;
    private boolean isMouseOnButton;

    private GameScreen screen;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "GameSystemData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node n = dom.getElementsByTagName("ButtonTexture").item(0);
            button = StateTexture.fromXml(DATA_PATH, n);
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "GameSystemData.xml", e);
        }
    }

    public GameSystem(GameScreen screen, Rectangle buttonPosition) {
        this.screen = screen;

        this.setX(buttonPosition.x);
        this.setY(buttonPosition.y);
        this.setWidth(buttonPosition.width);
        this.setHeight(buttonPosition.height);

        loadXml();

        this.addListener(new Listener());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(button.get(), this.getX(), this.getY());
    }

    public void dispose() {
        button.dispose();
    }

    private class Listener extends InputListener {

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            button.setState(StateTexture.State.SELECTED);
            return true;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int mouseButton) {
            button.setState(StateTexture.State.NORMAL);
            screen.showContextMenu(ContextMenu.MenuKindType.SYSTEM_MENU, null);
            return true;
        }
    }

}
