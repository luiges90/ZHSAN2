package com.zhsan.gamecomponents.toolbar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.common.Utility;
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

    private Texture background;
    private int backgroundHeight;

    private GameSystem gameSystem;
    private Rectangle gameSystemPos;
    private BitmapFont.HAlignment gameSystemAlign;

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

            Node gameSystemNode = dom.getElementsByTagName("GameSystem").item(0);
            gameSystemPos = Utility.readRectangleFromXml(gameSystemNode);
            gameSystemAlign = Utility.loadHAlignmentFromXML(gameSystemNode);
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "ToolBarData.xml", e);
        }
    }

    public ToolBar(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);

        loadXml();

        gameSystem = new GameSystem(Utility.adjustRectangleByHAlignment(gameSystemPos, gameSystemAlign, width));
        this.addActor(gameSystem);

        this.addListener(new InputEventListener());
    }

    public int getToolbarHeight() {
        return backgroundHeight;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(background, 0, 0, getWidth(), backgroundHeight);

        super.draw(batch, parentAlpha);
    }

    public void resize(int width, int height) {
        Rectangle actualGameSystemPos =
                Utility.adjustRectangleByHAlignment(gameSystemPos, gameSystemAlign, width);
        gameSystem.setPosition(actualGameSystemPos.x, actualGameSystemPos.y);
        gameSystem.setSize(actualGameSystemPos.width, actualGameSystemPos.height);
    }

    public void dispose() {
        background.dispose();
        gameSystem.dispose();
    }

    private class InputEventListener extends InputListener {

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return super.touchDown(event, x, y, pointer, button);
        }
    }
}
