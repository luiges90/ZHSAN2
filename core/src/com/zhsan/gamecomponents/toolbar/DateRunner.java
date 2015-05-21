package com.zhsan.gamecomponents.toolbar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 21/5/2015.
 */
public class DateRunner extends WidgetGroup {

    public static final String RES_PATH = Paths.RESOURCES + "DateRunner" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    private StateTexture play, pause, stop;
    private StateTexture upArrow, downArrow;

    private Rectangle d1Up, d1Down, d1Num, d2Up, d2Down, d2Num;
    private Rectangle playPos, stopPos, daysLeftPos;

    private TextWidget<Void> daysToGoText, daysLeftText;

    private int daysLeft, daysToGo;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "DateRunnerData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            upArrow = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("UpperArrowTexture").item(0));
            downArrow = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("LowerArrowTexture").item(0));
            play = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("PlayTexture").item(0));
            pause = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("PauseTexture").item(0));
            stop = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("PauseTexture").item(0));

            d1Up = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("FirstDigitUpperArrowPosition").item(0));
            d1Down = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("FirstDigitLowerArrowPosition").item(0));
            d1Num = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("FirstDigitTextPosition").item(0));
            d2Up = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("SecondDigitUpperArrowPosition").item(0));
            d2Down = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("SecondDigitLowerArrowPosition").item(0));
            d2Num = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("SecondDigitTextPosition").item(0));

            playPos = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("PlayPosition").item(0));
            stopPos = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("StopPosition").item(0));
            daysLeftPos = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("DaysLeftTextPosition").item(0));

            Node daysToGoNode = dom.getElementsByTagName("DaysToGo").item(0);
            daysToGoText = new TextWidget<>(TextWidget.Setting.fromXml(daysToGoNode));
            daysToGo = Integer.parseInt(XmlHelper.loadAttribute(daysToGoNode, "DefaultDays"));
            daysLeftText = new TextWidget<>(TextWidget.Setting.fromXml(
                    dom.getElementsByTagName("DaysLeft").item(0)
            ));
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "DateRunnerData.xml", e);
        }
    }

    public DateRunner(GameScreen screen) {
        this.screen = screen;

        this.addListener(new Listener());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);


    }

    public void dispose() {
        play.dispose();
        pause.dispose();
        stop.dispose();
        upArrow.dispose();
        downArrow.dispose();
        daysToGoText.dispose();
        daysLeftText.dispose();
    }

    private class Listener extends InputListener {

    }
}
