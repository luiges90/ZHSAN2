package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.TextWidget;
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

    private OnClick buttonListener;

    private Edge leftEdge, rightEdge, topEdge, bottomEdge;
    private Texture background;
    private TextWidget titleElement;
    private int okWidth, okHeight, cancelWidth, cancelHeight, okRight, okBottom, cancelRight, cancelBottom;
    private Rectangle ok, cancel;
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

            titleElement = TextWidget.fromXml(dom.getElementsByTagName("Title").item(0));
            addActor(titleElement);

            Node okNode = dom.getElementsByTagName("OKButton").item(0);
            Node cancelNode = dom.getElementsByTagName("CancelButton").item(0);
            okWidth = Integer.parseInt(okNode.getAttributes().getNamedItem("Width").getNodeValue());
            okHeight = Integer.parseInt(okNode.getAttributes().getNamedItem("Height").getNodeValue());
            okRight = Integer.parseInt(okNode.getAttributes().getNamedItem("MarginRight").getNodeValue());
            okBottom = Integer.parseInt(okNode.getAttributes().getNamedItem("MarginBottom").getNodeValue());
            cancelWidth = Integer.parseInt(cancelNode.getAttributes().getNamedItem("Width").getNodeValue());
            cancelHeight = Integer.parseInt(cancelNode.getAttributes().getNamedItem("Height").getNodeValue());
            cancelRight = Integer.parseInt(cancelNode.getAttributes().getNamedItem("MarginRight").getNodeValue());
            cancelBottom = Integer.parseInt(cancelNode.getAttributes().getNamedItem("MarginBottom").getNodeValue());

            okTexture = StateTexture.fromXml(dataPath, okNode);
            cancelTexture = StateTexture.fromXml(dataPath, cancelNode);

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
            throw new FileReadException(RES_PATH + "GameFrameData.xml", e);
        }
    }

    public GameFrame(String title, OnClick buttonListener) {
        loadXml();

        this.title = title;
        this.buttonListener = buttonListener;

        this.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                handleMouseMove(x, y);
                event.stop();
                return false;
            }

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleTouchDown(x, y);
                event.stop();
                return false;
            }
        });
    }

    protected final float getTopBound() {
        return getHeight() - topEdge.width;
    }

    protected final float getBottomBound() {
        return bottomEdge.width;
    }

    protected final float getLeftBound() {
        return leftEdge.width;
    }

    protected final float getRightBound() {
        return getWidth() - rightEdge.width;
    }

    private void updateTitleSize() {
        titleElement.setText(title);
        titleElement.setX(getLeftBound());
        titleElement.setY(getTopBound());
        titleElement.setWidth(getRightBound() - getLeftBound());
        titleElement.setHeight(getHeight() - getTopBound());
        titleElement.invalidateHierarchy();
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        updateTitleSize();
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        updateTitleSize();
    }

    public void draw(Batch batch, float parentAlpha) {
        // edges
        float top = getTopBound();
        float bottom = getBottomBound();
        float left = getLeftBound();
        float right = getRightBound();

        batch.draw(topEdge.image, left, top, right - left, getHeight() - top);
        batch.draw(bottomEdge.image, left, 0, right - left, bottom);
        batch.draw(leftEdge.image, 0, bottom, left, top - bottom);
        batch.draw(rightEdge.image, right, bottom, getWidth() - right, top - bottom);

        // corners
        batch.draw(topLeft, 0, top, left, getHeight() - top);
        batch.draw(topRight, right, top, getWidth() - right, getHeight() - top);
        batch.draw(bottomLeft, 0, 0, left, bottom);
        batch.draw(bottomRight, right, 0, getWidth() - right, bottom);

        // background
        batch.draw(background, left, bottom, right - left, top - bottom);

        // ok button
        if (ok == null) {
            ok = new Rectangle();
        }
        ok.setWidth(okWidth);
        ok.setHeight(okHeight);
        ok.setX(getWidth() - okRight - okWidth);
        ok.setY(okBottom);
        batch.draw(okTexture.get(), ok.x, ok.y, ok.width, ok.height);

        // cancel button
        if (cancel == null) {
            cancel = new Rectangle();
        }
        cancel.setWidth(cancelWidth);
        cancel.setHeight(cancelHeight);
        cancel.setX(getWidth() - cancelRight - cancelWidth);
        cancel.setY(cancelBottom);
        batch.draw(cancelTexture.get(), cancel.x, cancel.y, cancel.width, cancel.height);

        // content
        drawChildren(batch, parentAlpha);
    }

    private void handleMouseMove(float x, float y) {
        if (ok.contains(x, y)) {
            okTexture.setState(StateTexture.State.SELECTED);
        } else {
            okTexture.setState(StateTexture.State.NORMAL);
        }
        if (cancel.contains(x, y)) {
            cancelTexture.setState(StateTexture.State.SELECTED);
        } else {
            cancelTexture.setState(StateTexture.State.NORMAL);
        }
    }

    private void handleTouchDown(float x, float y) {
        if (ok.contains(x, y)) {
            okTexture.setState(StateTexture.State.NORMAL);
            buttonListener.onOkClicked();
            this.setVisible(false);
        }
        if (cancel.contains(x, y)) {
            cancelTexture.setState(StateTexture.State.NORMAL);
            buttonListener.onCancelClicked();
            this.setVisible(false);
        }
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
