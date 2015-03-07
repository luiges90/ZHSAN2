package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Fonts;
import com.zhsan.common.Paths;
import com.zhsan.common.Utility;
import com.zhsan.common.exception.XmlException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.TextBackgroundElement;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 7/3/2015.
 */
public class GameFrame extends WidgetGroup {

    public static final String RES_PATH = Paths.COMPONENTS + "GameFrame" + File.separator;

    public interface OnClick {
        public void onOkClicked();
        public void onCancelClicked();
    }

    private static class Edge {
        public final int width;
        public final Texture image;

        private Edge(Node node) {
            width = Integer.parseInt(node.getAttributes().getNamedItem("Width").getNodeValue());

            FileHandle f = Gdx.files.external(RES_PATH + File.separator + "Data" + File.separator +
                node.getAttributes().getNamedItem("FileName").getNodeValue());
            image = new Texture(f);
        }

        void dispose() {
            image.dispose();
        }
    }

    private String title;

    private OnClick listener;

    private Edge leftEdge, rightEdge, topEdge, bottomEdge;
    private Texture background;
    private TextBackgroundElement titleElement;
    private int okWidth, okHeight, cancelWidth, cancelHeight;
    private StateTexture okTexture, cancelTexture;
    private Sound okSound, cancelSound;
    private Texture topLeft, topRight, bottomLeft, bottomRight;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "GameFrameData.xml");
        String dataPath = RES_PATH + File.separator + "Data" + File.separator;

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            leftEdge = new Edge(dom.getElementsByTagName("LeftEdge").item(0));
            rightEdge = new Edge(dom.getElementsByTagName("RightEdge").item(0));
            topEdge = new Edge(dom.getElementsByTagName("TopEdge").item(0));
            bottomEdge = new Edge(dom.getElementsByTagName("BottomEdge").item(0));

            FileHandle fh;
            fh = Gdx.files.external(dataPath +
                    dom.getElementsByTagName("BackGround").item(0).getAttributes().getNamedItem("FileName").getNodeValue());
            background = new Texture(fh);

            titleElement = TextBackgroundElement.fromXml(dataPath, dom.getElementsByTagName("Title").item(0));

            Node ok = dom.getElementsByTagName("OKButton").item(0);
            Node cancel = dom.getElementsByTagName("CancelButton").item(0);
            okWidth = Integer.parseInt(ok.getAttributes().getNamedItem("Width").getNodeValue());
            okHeight = Integer.parseInt(ok.getAttributes().getNamedItem("Height").getNodeValue());
            cancelWidth = Integer.parseInt(cancel.getAttributes().getNamedItem("Width").getNodeValue());
            cancelHeight = Integer.parseInt(cancel.getAttributes().getNamedItem("Height").getNodeValue());

            okTexture = StateTexture.fromXml(dataPath, ok);
            cancelTexture = StateTexture.fromXml(dataPath, cancel);

            Node sound = dom.getElementsByTagName("SoundFile").item(0);
            fh = Gdx.files.external(dataPath + sound.getAttributes().getNamedItem("OK").getNodeValue());
            okSound = Gdx.audio.newSound(fh);

            fh = Gdx.files.external(dataPath + sound.getAttributes().getNamedItem("Cancel").getNodeValue());
            cancelSound = Gdx.audio.newSound(fh);

            fh = Gdx.files.external(dataPath +
                    dom.getElementsByTagName("TopLeft").item(0).getAttributes().getNamedItem("FileName").getNodeValue());
            topLeft = new Texture(fh);

            fh = Gdx.files.external(dataPath +
                    dom.getElementsByTagName("TopRight").item(0).getAttributes().getNamedItem("FileName").getNodeValue());
            topRight = new Texture(fh);

            fh = Gdx.files.external(dataPath +
                    dom.getElementsByTagName("BottomLeft").item(0).getAttributes().getNamedItem("FileName").getNodeValue());
            bottomLeft = new Texture(fh);

            fh = Gdx.files.external(dataPath +
                    dom.getElementsByTagName("BottomRight").item(0).getAttributes().getNamedItem("FileName").getNodeValue());
            bottomRight = new Texture(fh);
        } catch (Exception e) {
            throw new XmlException(RES_PATH + "GameFrameData.xml", e);
        }
    }

    public GameFrame(String title, OnClick listener) {
        loadXml();

        this.title = title;
        this.listener = listener;
    }

    public void draw(Batch batch, float parentAlpha) {
        float top = getHeight() - topEdge.image.getHeight();
        float topWidth = getWidth() - topLeft.getWidth() - topRight.getWidth();
        batch.draw(topLeft, 0, top, topLeft.getWidth(), topEdge.width);
        batch.draw(topEdge.image, topLeft.getWidth(), top, topWidth, topEdge.width);
        batch.draw(topRight, getWidth() - topRight.getWidth(), top, topRight.getWidth(), topEdge.width);

        titleElement.applyColorSize();
        BitmapFont.TextBounds bounds = titleElement.getFont().getWrappedBounds(title, topWidth);
        titleElement.getFont().drawWrapped(batch, title, topLeft.getWidth(), getHeight() - (topEdge.width / 2 - bounds.height / 2),
                topWidth, titleElement.getAlign());
    }

    public void dispose() {
        leftEdge.dispose();
        rightEdge.dispose();
        topEdge.dispose();
        bottomEdge.dispose();
        background.dispose();
        okTexture.dispose();
        cancelTexture.dispose();
        okSound.dispose();
        cancelSound.dispose();
        topLeft.dispose();
        topRight.dispose();
        bottomLeft.dispose();
        bottomRight.dispose();
    }

}
