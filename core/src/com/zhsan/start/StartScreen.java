package com.zhsan.start;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.zhsan.common.Paths;
import com.zhsan.common.Utility;
import com.zhsan.common.exception.XmlException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 6/3/2015.
 */
public class StartScreen extends Actor {

    private static final String RES_PATH = Paths.RESOURCES + "Start" + File.separator;

    private Texture txStart;

    private Rectangle start, load, setting, credit, exit;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "Start.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            start = Utility.readRectangleFromXml(dom.getElementsByTagName("StartButton").item(0));
            start.y = txStart.getHeight() - start.y - start.height;

            load = Utility.readRectangleFromXml(dom.getElementsByTagName("LoadButton").item(0));
            load.y = txStart.getHeight() - load.y - load.height;

            setting = Utility.readRectangleFromXml(dom.getElementsByTagName("SettingButton").item(0));
            setting.y = txStart.getHeight() - setting.y - setting.height;

            credit = Utility.readRectangleFromXml(dom.getElementsByTagName("CreditButton").item(0));
            credit.y = txStart.getHeight() - credit.y - credit.height;

            exit = Utility.readRectangleFromXml(dom.getElementsByTagName("ExitButton").item(0));
            exit.y = txStart.getHeight() - exit.y - exit.height;
        } catch (Exception e) {
            throw new XmlException(RES_PATH + "Start.xml", e);
        }
    }

    public StartScreen() {
        txStart = new Texture(Gdx.files.external(RES_PATH + "Start.jpg"));
        loadXml();

        this.setBounds(0, 0, txStart.getWidth(), txStart.getHeight());
        this.addListener(new InputListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleTouchDown(x, y);
                return true;
            }
        });
    }

    public void draw(Batch batch, float parentAlpha) {
        batch.draw(txStart, 0, 0);
    }

    private void openStart() {

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

}
