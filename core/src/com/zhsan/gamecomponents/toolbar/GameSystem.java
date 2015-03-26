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
import com.zhsan.common.exception.FileReadException;
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

    private Texture button, buttonSelected;
    private Rectangle buttonPosition;
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

            button = new Texture(Gdx.files.external(
                    DATA_PATH + n.getAttributes().getNamedItem("FileName").getNodeValue()));
            buttonSelected = new Texture(Gdx.files.external
                    (DATA_PATH + n.getAttributes().getNamedItem("Selected").getNodeValue()));
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "GameSystemData.xml", e);
        }
    }

    public GameSystem(GameScreen screen, Rectangle buttonPosition) {
        this.buttonPosition = buttonPosition;
        this.screen = screen;

        this.setWidth(Gdx.graphics.getWidth());
        this.setHeight(Gdx.graphics.getHeight());

        loadXml();

        this.addListener(new Listener());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(isMouseOnButton ? buttonSelected : button, buttonPosition.getX(), buttonPosition.getY());
    }

    public void dispose() {
        button.dispose();
        buttonSelected.dispose();
    }

    private class Listener extends InputListener {

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            isMouseOnButton = buttonPosition.contains(x, y);
            return isMouseOnButton;
        }
    }

}
