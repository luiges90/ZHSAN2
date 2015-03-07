package com.zhsan.start;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.zhsan.AcceptInput;
import com.zhsan.common.Paths;
import com.zhsan.common.Utility;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;

/**
 * Created by Peter on 6/3/2015.
 */
public class StartScreen implements AcceptInput {

    private static final String RES_PATH = Paths.RESOURCES + "Start" + File.separator;

    private Texture txStart;

    private Rectangle start, load, setting, credit, exit;

    public StartScreen() {
        txStart = new Texture(Gdx.files.external(RES_PATH + "Start.jpg"));

        FileHandle f = Gdx.files.external(RES_PATH + "Start.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            start = Utility.readRectangleFromXml(dom.getElementsByTagName("StartButton").item(0));
            load = Utility.readRectangleFromXml(dom.getElementsByTagName("LoadButton").item(0));
            setting = Utility.readRectangleFromXml(dom.getElementsByTagName("SettingButton").item(0));
            credit = Utility.readRectangleFromXml(dom.getElementsByTagName("CreditButton").item(0));
            exit = Utility.readRectangleFromXml(dom.getElementsByTagName("ExitButton").item(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Point screenToImage(int x, int y) {
        int winWidth = Gdx.graphics.getWidth();
        int winHeight = Gdx.graphics.getHeight();
        return new Point(x - (winWidth / 2 - txStart.getWidth() / 2),
                y - (winHeight / 2 - txStart.getHeight() / 2));
    }

    public void render(SpriteBatch batch) {
        batch.draw(txStart, -txStart.getWidth() / 2, -txStart.getHeight() / 2);
    }

    public void dispose() {
        txStart.dispose();
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

    @Override
    public InputProcessor getInputProcessor() {
        return new InputAdapter(){
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                Point imgPt = screenToImage(x, y);
                if (start.contains(imgPt.x, imgPt.y)) {
                    openStart();
                } else if (load.contains(imgPt.x, imgPt.y)) {
                    openLoad();
                } else if (setting.contains(imgPt.x, imgPt.y)) {
                    openSettings();
                } else if (credit.contains(imgPt.x, imgPt.y)) {
                    openCredits();
                } else if (exit.contains(imgPt.x, imgPt.y)) {
                    exit();
                }
                return true;
            }
        };
    }
}
