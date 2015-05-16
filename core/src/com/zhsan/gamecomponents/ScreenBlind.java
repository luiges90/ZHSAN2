package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
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
import java.time.format.DateTimeFormatter;

/**
 * Created by Peter on 16/5/2015.
 */
public class ScreenBlind extends WidgetGroup {

    public static final String RES_PATH = Paths.RESOURCES + "ScreenBlind" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private Texture background;
    private Rectangle backgroundPos;

    private Texture spring, summer, autumn, winter;

    private TextWidget date, faction;
    private Rectangle datePos, factionPos;
    private Rectangle seasonPos;

    private GameScreen screen;

    private DateTimeFormatter dateFormatter;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "ScreenBlindData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node bg = dom.getElementsByTagName("Background").item(0);
            background = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(bg, "FileName")));
            backgroundPos = XmlHelper.loadRectangleFromXml(bg);

            Node season = dom.getElementsByTagName("Season").item(0);
            spring = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(season, "Spring")));
            summer = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(season, "Summer")));
            autumn = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(season, "Autumn")));
            winter = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(season, "Winter")));

            Node dateNode = dom.getElementsByTagName("DateClient").item(0);
            date = new TextWidget(TextWidget.Setting.fromXml(dateNode));
            datePos = XmlHelper.loadRectangleFromXml(dateNode);
            dateFormatter = DateTimeFormatter.ofPattern(XmlHelper.loadAttribute(dateNode, "Pattern"));

            Node factionNode = dom.getElementsByTagName("FactionClient").item(0);
            faction = new TextWidget(TextWidget.Setting.fromXml(factionNode));
            factionPos = XmlHelper.loadRectangleFromXml(factionNode);

            seasonPos = XmlHelper.loadRectangleFromXml(dom.getElementsByTagName("SeasonClient").item(0));

        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "ScreenBlindData.xml", e);
        }
    }

    public ScreenBlind(GameScreen screen) {
        this.screen = screen;

        loadXml();

        this.setPosition(backgroundPos.x, backgroundPos.y);
        this.setSize(backgroundPos.width, backgroundPos.height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(background, backgroundPos.x, backgroundPos.y, backgroundPos.width, backgroundPos.height);

        Texture season = null;
        switch (screen.getScenario().getSeason()) {
            case SPRING:
                season = spring;
                break;
            case SUMMER:
                season = summer;
                break;
            case AUTUMN:
                season = autumn;
                break;
            case WINTER:
                season = winter;
                break;
        }
        batch.draw(season, seasonPos.x, seasonPos.y, seasonPos.width, seasonPos.height);

        date.setText(dateFormatter.format(screen.getScenario().getGameDate()));
        date.setPosition(datePos.x, datePos.y);
        date.setSize(datePos.width, datePos.height);
        date.draw(batch, parentAlpha);

        faction.setText(screen.getScenario().getGameData().getCurrentPlayer().getName());
        faction.setPosition(factionPos.x, factionPos.y);
        faction.setSize(factionPos.width, factionPos.height);
        faction.draw(batch, parentAlpha);
    }

    public void dispose() {
        background.dispose();
        spring.dispose();
        summer.dispose();
        autumn.dispose();
        winter.dispose();
        date.dispose();
        faction.dispose();
    }

}
