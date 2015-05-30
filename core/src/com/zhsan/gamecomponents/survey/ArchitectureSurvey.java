package com.zhsan.gamecomponents.survey;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 30/5/2015.
 */
public class ArchitectureSurvey extends Survey {

    public static final String RES_PATH = Survey.RES_PATH + "Architecture" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    private Texture background, factionTexture;
    private Point factionTexturePosition;
    private TextWidget<Void> name, kind, faction, population, army, endurance, fund, food, personCount, noFactionCount,
                            technology, morale, militaryPopulation, facility;

    private TextWidget<Void> addTextWidget(Node node) {
        TextWidget<Void> text = new TextWidget<>(TextWidget.Setting.fromXml(node));
        text.setPosition(Integer.parseInt(XmlHelper.loadAttribute(node, "X")),
                Integer.parseInt(XmlHelper.loadAttribute(node, "Y")));
        this.addActor(text);
        return text;
    }

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "TextDialogData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            background = new Texture(Gdx.files.external(XmlHelper.loadAttribute(
                    dom.getElementsByTagName("BackgroundTextureFile").item(0), "FileName"
            )));

            Node factionTextureNode = dom.getElementsByTagName("FactionTextureFile").item(0);
            factionTexture = new Texture(Gdx.files.external(XmlHelper.loadAttribute(factionTextureNode, "FileName")));
            factionTexturePosition = Point.fromXml(factionTextureNode);

            name = addTextWidget(dom.getElementsByTagName("NameText").item(0));
            kind = addTextWidget(dom.getElementsByTagName("KindText").item(0));
            faction = addTextWidget(dom.getElementsByTagName("FactionText").item(0));
            population = addTextWidget(dom.getElementsByTagName("PopulationText").item(0));
            army = addTextWidget(dom.getElementsByTagName("ArmyText").item(0));
            endurance = addTextWidget(dom.getElementsByTagName("EnduranceText").item(0));
            fund = addTextWidget(dom.getElementsByTagName("FundText").item(0));
            food = addTextWidget(dom.getElementsByTagName("FoodText").item(0));
            personCount = addTextWidget(dom.getElementsByTagName("PersonCountText").item(0));
            noFactionCount = addTextWidget(dom.getElementsByTagName("NoFactionPersonCountText").item(0));
            technology = addTextWidget(dom.getElementsByTagName("TechnologyText").item(0));
            morale = addTextWidget(dom.getElementsByTagName("MoraleText").item(0));
            militaryPopulation = addTextWidget(dom.getElementsByTagName("MilitaryPopulationText").item(0));
            facility = addTextWidget(dom.getElementsByTagName("FacilityCountText").item(0));
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "TextDialogData.xml", e);
        }
    }

    public ArchitectureSurvey(GameScreen screen) {
        this.screen = screen;

        loadXml();

        this.setWidth(background.getWidth());
        this.setHeight(background.getHeight());
    }

    @Override
    protected void drawContent(Batch batch, float parentAlpha) {
        batch.draw(background, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        background.dispose();
        factionTexture.dispose();
    }

}
