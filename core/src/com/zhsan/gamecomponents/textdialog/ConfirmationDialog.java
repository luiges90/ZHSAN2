package com.zhsan.gamecomponents.textdialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 2/5/2015.
 */
public class ConfirmationDialog extends TextDialog {

    public interface OnDismissListener {
        public void onDismiss(boolean confirmed);
    }

    public static final String RES_PATH = TextDialog.RES_PATH + "ConfirmationDialog" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    private Texture background;
    private Rectangle backgroundPos;

    private StateTexture yesButton, noButton;
    private Sound yesSound, noSound;
    private Rectangle yesPos, noPos;
    private Rectangle actualYesPos, actualNoPos;

    private OnDismissListener onDismissListener;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "ConfirmationDialogData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node bgNode = dom.getElementsByTagName("Background").item(0);
            background = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(bgNode, "FileName")));
            backgroundPos = XmlHelper.loadRectangleFromXml(bgNode);

            Node yesNode = dom.getElementsByTagName("YesTexture").item(0);
            yesButton = StateTexture.fromXml(DATA_PATH, yesNode);
            yesSound = Gdx.audio.newSound(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(yesNode, "SoundFile")));
            yesPos = XmlHelper.loadRectangleFromXml(yesNode);

            Node noNode = dom.getElementsByTagName("NoTexture").item(0);
            noButton = StateTexture.fromXml(DATA_PATH, noNode);
            noSound = Gdx.audio.newSound(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(noNode, "SoundFile")));
            noPos = XmlHelper.loadRectangleFromXml(noNode);

        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "ConfirmationDialogData.xml", e);
        }
    }

    public ConfirmationDialog(GameScreen screen) {
        super(screen);
        this.screen = screen;

        loadXml();

        this.setVisible(false);

        for (EventListener i : this.getListeners()) {
            this.removeListener(i);
        }
        this.addListener(new Listener());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (this.isVisible()) {
            Rectangle pos = getDialogPosition();

            batch.draw(background, pos.x + backgroundPos.x, pos.y + pos.height - backgroundPos.y,
                    backgroundPos.width, backgroundPos.height);

            actualYesPos = new Rectangle(pos.x + backgroundPos.x + yesPos.x, pos.y + pos.height - backgroundPos.y + yesPos.y,
                    yesPos.width, yesPos.height);
            actualNoPos = new Rectangle(pos.x + backgroundPos.x + noPos.x, pos.y + pos.height - backgroundPos.y + noPos.y,
                    noPos.width, noPos.height);

            batch.draw(yesButton.get(), actualYesPos.x, actualYesPos.y, actualYesPos.width, actualYesPos.height);
            batch.draw(noButton.get(), actualNoPos.x, actualNoPos.y, actualNoPos.width, actualNoPos.height);
        }
    }

    public void show(String s, OnDismissListener onDismissListener) {
        super.show(s, null);
        this.onDismissListener = onDismissListener;
    }

    public void show(TextKeys s, OnDismissListener onDismissListener) {
        this.show(getStringFromKey(s), onDismissListener);
    }

    public void dispose() {
        super.dispose();
        background.dispose();
        yesButton.dispose();
        noButton.dispose();
        yesSound.dispose();
        noSound.dispose();
    }

    public class Listener extends InputListener {
        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            yesButton.setState(actualYesPos.contains(x, y) ? StateTexture.State.SELECTED : StateTexture.State.NORMAL);
            noButton.setState(actualNoPos.contains(x, y) ? StateTexture.State.SELECTED : StateTexture.State.NORMAL);
            return false;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (button == Input.Buttons.LEFT) {
                if (actualYesPos.contains(x, y)) {
                    yesSound.play();
                    ConfirmationDialog.this.setVisible(false);
                    onDismissListener.onDismiss(true);
                } else if (actualNoPos.contains(x, y)) {
                    noSound.play();
                    ConfirmationDialog.this.setVisible(false);
                    onDismissListener.onDismiss(false);
                }
            } else if (button == Input.Buttons.RIGHT) {
                noSound.play();
                ConfirmationDialog.this.setVisible(false);
                onDismissListener.onDismiss(false);
            }
            return true;
        }
    }

}
