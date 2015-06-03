package com.zhsan.gamecomponents.commandframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.EnumMap;

/**
 * Created by Peter on 2/6/2015.
 */
public class ArchitectureCommandFrame extends CommandFrame {

    private enum TabType {
        INTERNAL, MILITARY, OFFICER, TACTICS, FACILITY
    }

    public static final String RES_PATH = CommandFrame.RES_PATH + "Architecture" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    private StateTexture internal, military, officer, tactics, facility;
    private Rectangle internalPos, militaryPos, officerPos, tacticsPos, facilityPos;

    private EnumMap<TabType, Texture> backgrounds = new EnumMap<>(TabType.class);

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "ArchitectureCommandFrameData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            NodeList tabNodes = dom.getElementsByTagName("Tabs").item(0).getChildNodes();
            for (int i = 0; i < tabNodes.getLength(); ++i) {
                Node n = tabNodes.item(i);
                if (n.getNodeName().equals("Internal")) {
                    internal = StateTexture.fromXml(DATA_PATH, n);
                    internalPos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Military")) {
                    military = StateTexture.fromXml(DATA_PATH, n);
                    militaryPos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Officer")) {
                    officer = StateTexture.fromXml(DATA_PATH, n);
                    officerPos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Tactics")) {
                    tactics = StateTexture.fromXml(DATA_PATH, n);
                    tacticsPos = XmlHelper.loadRectangleFromXml(n);
                } else if (n.getNodeName().equals("Facility")) {
                    facility = StateTexture.fromXml(DATA_PATH, n);
                    facilityPos = XmlHelper.loadRectangleFromXml(n);
                }
            }

        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "ArchitectureCommandFrameData.xml", e);
        }
    }

    public ArchitectureCommandFrame(GameScreen screen) {
        super();

        loadXml();

        this.screen = screen;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(internal.get(), getX() + internalPos.x, getY() + internalPos.y, internalPos.width, internalPos.height);
        batch.draw(military.get(), getX() + militaryPos.x, getY() + militaryPos.y, militaryPos.width, militaryPos.height);
        batch.draw(officer.get(), getX() + officerPos.x, getY() + officerPos.y, officerPos.width, officerPos.height);
        batch.draw(tactics.get(), getX() + tacticsPos.x, getY() + tacticsPos.y, tacticsPos.width, tacticsPos.height);
        batch.draw(facility.get(), getX() + facilityPos.x, getY() + facilityPos.y, facilityPos.width, facilityPos.height);
    }

    public void dispose() {
        super.dispose();
    }
    
}
