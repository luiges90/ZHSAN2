package com.zhsan.gamecomponents.toolbar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.Utility;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.contextmenu.ContextMenu;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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

    private StateTexture gameSystem;
    private Rectangle gameSystemPos, actualGameSystemPos;
    private BitmapFont.HAlignment gameSystemAlign;

    private SmallMap smallMap;

    private StateTexture smallMapButton;
    private Rectangle smallMapPos, actualSmallMapPos;
    private BitmapFont.HAlignment smallMapAlign;

    private GameScreen screen;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "ToolBarData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node bg = dom.getElementsByTagName("Background").item(0);

            backgroundHeight = Integer.parseInt(XmlHelper.loadAttribute(bg, "Height"));
            background = new Texture(Gdx.files.external(DATA_PATH +
                    XmlHelper.loadAttribute(bg, "FileName")));

            Node gameSystemNode = dom.getElementsByTagName("GameSystem").item(0);
            gameSystemPos = XmlHelper.loadRectangleFromXml(gameSystemNode);
            gameSystemAlign = XmlHelper.loadHAlignmentFromXml(gameSystemNode);
            gameSystem = StateTexture.fromXml(DATA_PATH, gameSystemNode);

            Node smallMapNode = dom.getElementsByTagName("SmallMap").item(0);
            smallMapPos = XmlHelper.loadRectangleFromXml(smallMapNode);
            smallMapAlign = XmlHelper.loadHAlignmentFromXml(smallMapNode);
            smallMapButton = StateTexture.fromXml(DATA_PATH, smallMapNode);
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "ToolBarData.xml", e);
        }
    }

    public ToolBar(GameScreen screen) {
        this.screen = screen;

        loadXml();

        this.setHeight(backgroundHeight);

        smallMap = new SmallMap(screen, smallMapAlign, backgroundHeight);
        smallMap.setVisible(false);

        this.addActor(smallMap);

        this.addListener(new Listener());
    }

    public int getToolbarHeight() {
        return backgroundHeight;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (actualGameSystemPos == null) {
            actualGameSystemPos = Utility.adjustRectangleByHAlignment(gameSystemPos, gameSystemAlign, getWidth());
        }
        if (actualSmallMapPos == null) {
            actualSmallMapPos = Utility.adjustRectangleByHAlignment(smallMapPos, smallMapAlign, getWidth());
        }

        batch.draw(background, 0, 0, getWidth(), backgroundHeight);

        batch.draw(gameSystem.get(), actualGameSystemPos.getX(), actualGameSystemPos.getY(),
                actualGameSystemPos.getWidth(), actualGameSystemPos.getHeight());
        batch.draw(smallMapButton.get(), actualSmallMapPos.getX(), actualSmallMapPos.getY(),
                actualSmallMapPos.getWidth(), actualSmallMapPos.getHeight());

        super.draw(batch, parentAlpha);
    }

    public void resize(int width, int height) {
        actualGameSystemPos = Utility.adjustRectangleByHAlignment(gameSystemPos, gameSystemAlign, getWidth());
        actualSmallMapPos = Utility.adjustRectangleByHAlignment(smallMapPos, smallMapAlign, getWidth());

        smallMap.resize(width, height);
    }

    public void dispose() {
        background.dispose();
        gameSystem.dispose();
        smallMapButton.dispose();
        smallMap.dispose();
    }

    private class Listener extends InputListener {

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            if (actualGameSystemPos.contains(x, y)) {
                gameSystem.setState(StateTexture.State.SELECTED);
            }
            return true;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int mouseButton) {
            if (actualGameSystemPos.contains(x, y)) {
                gameSystem.setState(StateTexture.State.NORMAL);
                screen.showContextMenu(ContextMenu.MenuKindType.SYSTEM_MENU, null);
            }
            if (actualSmallMapPos.contains(x, y)) {
                if (smallMapButton.getState() == StateTexture.State.NORMAL) {
                    smallMapButton.setState(StateTexture.State.SELECTED);
                    smallMap.setVisible(true);
                } else {
                    smallMapButton.setState(StateTexture.State.NORMAL);
                    smallMap.setVisible(false);
                }
            }
            return true;
        }
    }

}
