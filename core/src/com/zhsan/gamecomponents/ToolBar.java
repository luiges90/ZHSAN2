package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.GetKeyFocusWhenEntered;
import com.zhsan.gamecomponents.common.GetScrollFocusWhenEntered;
import com.zhsan.gameobject.GameMap;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 23/3/2015.
 */
public class ToolBar extends WidgetGroup {

    public static final String RES_PATH = Paths.RESOURCES + "ToolBar" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    private Texture background;
    private int backgroundHeight;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "ToolBarData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node bg = dom.getElementsByTagName("Background").item(0);

            backgroundHeight = Integer.parseInt(bg.getAttributes().getNamedItem("Height").getNodeValue());
            background = new Texture(Gdx.files.external(DATA_PATH +
                    bg.getAttributes().getNamedItem("FileName").getNodeValue()));

        } catch (Exception e) {
            throw new FileReadException(DATA_PATH + "MapSetting.xml", e);
        }
    }

    public ToolBar(GameScreen screen, int width, int height) {
        this.screen = screen;
        this.setWidth(width);
        this.setHeight(height);

        loadXml();

        this.addListener(new InputEventListener());
    }

    public int getToolbarHeight() {
        return backgroundHeight;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(background, 0, 0, getWidth(), backgroundHeight);
    }

    public void dispose() {
        background.dispose();
    }

    private class InputEventListener extends InputListener {

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            return false;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return super.touchDown(event, x, y, pointer, button);
        }
    }
}
