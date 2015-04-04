package com.zhsan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.gameframe.GameFrame;
import com.zhsan.gamecomponents.gameframe.NewGameFrame;
import com.zhsan.gamecomponents.common.XmlHelper;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 6/3/2015.
 */
public class StartScreen extends WidgetGroup {

    private static final String RES_PATH = Paths.RESOURCES + "Start" + File.separator;

    private Texture txStart;

    private Rectangle start, load, setting, credit, exit;

    private GameFrame newGameFrame;
    private NewGameFrame.OnScenarioChosenListener listener;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "Start.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            start = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("StartButton").item(0));
            start.y = txStart.getHeight() - start.y - start.height;

            load = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("LoadButton").item(0));
            load.y = txStart.getHeight() - load.y - load.height;

            setting = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("SettingButton").item(0));
            setting.y = txStart.getHeight() - setting.y - setting.height;

            credit = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("CreditButton").item(0));
            credit.y = txStart.getHeight() - credit.y - credit.height;

            exit = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("ExitButton").item(0));
            exit.y = txStart.getHeight() - exit.y - exit.height;
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "Start.xml", e);
        }
    }

    public StartScreen(NewGameFrame.OnScenarioChosenListener listener) {
        txStart = new Texture(Gdx.files.external(RES_PATH + "Start.jpg"));
        loadXml();

        this.listener = listener;

        this.setBounds(0, 0, txStart.getWidth(), txStart.getHeight());
        this.addListener(new InputListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleTouchDown(x, y);
                return false;
            }
        });
    }

    public void draw(Batch batch, float parentAlpha) {
        batch.draw(txStart, 0, 0);
        super.draw(batch, parentAlpha);
    }

    private void openStart() {
        if (newGameFrame == null) {
            newGameFrame = new NewGameFrame(listener);
            this.addActor(newGameFrame);
        } else {
            newGameFrame.setVisible(true);
        }
    }

    private void openLoad() {

    }

    private void openSettings() {

    }

    private void openCredits() {

    }

    private void exit() {
        Gdx.app.exit();
    }

    private void handleTouchDown(float x, float y) {
        if (start.contains(x, y)) {
            openStart();
        } else if (load.contains(x, y)) {
            openLoad();
        } else if (setting.contains(x, y)) {
            openSettings();
        } else if (credit.contains(x, y)) {
            openCredits();
        } else if (exit.contains(x, y)) {
            exit();
        }
    }

    public void dispose() {
        if (newGameFrame != null) {
            newGameFrame.dispose();
        }
        txStart.dispose();
    }

}
