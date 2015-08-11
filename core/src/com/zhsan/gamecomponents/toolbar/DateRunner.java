package com.zhsan.gamecomponents.toolbar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.GlobalVariables;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
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

    public static final String RES_PATH = ToolBar.RES_PATH + "DateRunner" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    private StateTexture play, pause, stop;
    private StateTexture upArrow1, downArrow1, upArrow2, downArrow2;

    private Rectangle d1Up, d1Down, d1Num, d2Up, d2Down, d2Num;
    private Rectangle playPos, stopPos, daysLeftPos;
    private Point position;

    private TextWidget<Void> daysToGoText1, daysToGoText2, daysLeftText;

    private int daysLeft, daysToGo;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "DateRunnerData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            position = Point.fromXml(dom.getElementsByTagName("Position").item(0));

            upArrow1 = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("UpperArrowTexture").item(0));
            downArrow1 = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("LowerArrowTexture").item(0));
            upArrow2 = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("UpperArrowTexture").item(0));
            downArrow2 = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("LowerArrowTexture").item(0));
            play = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("PlayTexture").item(0));
            pause = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("PauseTexture").item(0));
            stop = StateTexture.fromXml(DATA_PATH, dom.getElementsByTagName("StopTexture").item(0));

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
            daysToGoText1 = new TextWidget<>(TextWidget.Setting.fromXml(daysToGoNode));
            daysToGoText2 = new TextWidget<>(TextWidget.Setting.fromXml(daysToGoNode));
            daysToGo = Integer.parseInt(XmlHelper.loadAttribute(daysToGoNode, "DefaultDays"));
            daysLeftText = new TextWidget<>(TextWidget.Setting.fromXml(
                    dom.getElementsByTagName("DaysLeft").item(0)
            ));
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "DateRunnerData.xml", e);
        }
    }

    private final void setSizeToWrapWidget() {
        // set size of widget so that it can capture input events properly
        Rectangle[] allRect = new Rectangle[]{d1Up, d1Down, d1Num, d2Up, d2Down, d2Num, playPos, stopPos, daysLeftPos};

        float width = 0;
        for (Rectangle r : allRect) {
            width = Math.max(width, r.x + r.width);
        }

        float height = 0;
        for (Rectangle r : allRect) {
            height = Math.max(height, r.y + r.height);
        }

        this.setSize(width, height);
    }

    public DateRunner(GameScreen screen) {
        this.screen = screen;

        loadXml();

        this.setPosition(position.x, position.y);
        setSizeToWrapWidget();

        this.addListener(new Listener());

        screen.getDayRunner().addRunningDaysListener(new GameScreen.RunningDaysListener() {
            @Override
            public void started(int i) {
                daysLeft = i;
            }

            @Override
            public void passed(int i) {
                daysLeft = i;
            }

            @Override
            public void stopped() {
                daysLeft = 0;
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // digit 1
        batch.draw(upArrow1.get(), d1Up.x + getX(), d1Up.y + getY(), d1Up.width, d1Up.height);
        batch.draw(downArrow1.get(), d1Down.x + getX(), d1Down.y + getY(), d1Down.width, d1Down.height);

        daysToGoText1.setText(String.valueOf(daysToGo / 10));
        daysToGoText1.setPosition(d1Num.x + getX(), d1Num.y + getY());
        daysToGoText1.setSize(d1Num.width, d1Num.height);
        daysToGoText1.draw(batch, parentAlpha);

        // digit 2
        batch.draw(upArrow2.get(), d2Up.x + getX(), d2Up.y + getY(), d2Up.width, d2Up.height);
        batch.draw(downArrow2.get(), d2Down.x + getX(), d2Down.y + getY(), d2Down.width, d2Down.height);

        daysToGoText2.setText(String.valueOf(daysToGo % 10));
        daysToGoText2.setPosition(d2Num.x + getX(), d2Num.y + getY());
        daysToGoText2.setSize(d2Num.width, d2Num.height);
        daysToGoText2.draw(batch, parentAlpha);

        // controls
        batch.draw(screen.getDayRunner().isDayRunning() ? pause.get() : play.get(), playPos.x + getX(), playPos.y + getY(), playPos.width, playPos.height);
        batch.draw(stop.get(), stopPos.x + getX(), stopPos.y + getY(), stopPos.width, stopPos.height);

        // days left
        daysLeftText.setText(String.format("%02d", daysLeft));
        daysLeftText.setPosition(daysLeftPos.x + getX(), daysLeftPos.y + getY());
        daysLeftText.setSize(daysLeftPos.width, daysLeftPos.height);
        daysLeftText.draw(batch, parentAlpha);
    }

    public void dispose() {
        play.dispose();
        pause.dispose();
        stop.dispose();
        upArrow1.dispose();
        downArrow1.dispose();
        upArrow2.dispose();
        downArrow2.dispose();
        daysToGoText1.dispose();
        daysToGoText2.dispose();
        daysLeftText.dispose();
    }

    public int getDaysToGo() {
        return daysToGo;
    }

    private class Listener extends InputListener {

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            play.setState(StateTexture.State.NORMAL);
            pause.setState(StateTexture.State.NORMAL);
            stop.setState(StateTexture.State.NORMAL);
            upArrow1.setState(StateTexture.State.NORMAL);
            upArrow2.setState(StateTexture.State.NORMAL);
            downArrow1.setState(StateTexture.State.NORMAL);
            downArrow2.setState(StateTexture.State.NORMAL);
        }

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            if (playPos.contains(x, y)) {
                play.setState(StateTexture.State.SELECTED);
                pause.setState(StateTexture.State.SELECTED);
            } else {
                play.setState(StateTexture.State.NORMAL);
                pause.setState(StateTexture.State.NORMAL);
            }

            if (stopPos.contains(x, y)) {
                stop.setState(StateTexture.State.SELECTED);
            } else {
                stop.setState(StateTexture.State.NORMAL);
            }

            if (d1Up.contains(x, y)) {
                upArrow1.setState(StateTexture.State.SELECTED);
            } else {
                upArrow1.setState(StateTexture.State.NORMAL);
            }

            if (d1Down.contains(x, y)) {
                downArrow1.setState(StateTexture.State.SELECTED);
            } else {
                downArrow1.setState(StateTexture.State.NORMAL);
            }

            if (d2Up.contains(x, y)) {
                upArrow2.setState(StateTexture.State.SELECTED);
            } else {
                upArrow2.setState(StateTexture.State.NORMAL);
            }

            if (d2Down.contains(x, y)) {
                downArrow2.setState(StateTexture.State.SELECTED);
            } else {
                downArrow2.setState(StateTexture.State.NORMAL);
            }

            return true;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (d1Up.contains(x, y)) {
                upArrow1.setState(StateTexture.State.NORMAL);
                daysToGo += 10;
            }

            if (d1Down.contains(x, y)) {
                downArrow1.setState(StateTexture.State.NORMAL);
                daysToGo -= 10;
            }

            if (d2Up.contains(x, y)) {
                upArrow2.setState(StateTexture.State.NORMAL);
                daysToGo += 1;
            }

            if (d2Down.contains(x, y)) {
                downArrow2.setState(StateTexture.State.NORMAL);
                daysToGo -= 1;
            }

            daysToGo = MathUtils.clamp(daysToGo, 0, GlobalVariables.maxRunningDays);

            if (screen.allowRunDays()) {
                if (playPos.contains(x, y)) {
                    play.setState(StateTexture.State.NORMAL);
                    pause.setState(StateTexture.State.NORMAL);
                    if (!screen.getDayRunner().isDayRunning()) {
                        screen.getDayRunner().runDays(daysLeft == 0 ? daysToGo : 0);
                    } else {
                        screen.getDayRunner().pauseRunDays();
                    }
                }

                if (stopPos.contains(x, y)) {
                    stop.setState(StateTexture.State.NORMAL);
                    screen.getDayRunner().stopRunDays();
                }
            }

            return true;
        }
    }
}
