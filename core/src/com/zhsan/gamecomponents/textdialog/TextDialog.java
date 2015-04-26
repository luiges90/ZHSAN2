package com.zhsan.gamecomponents.textdialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.Utility;
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
 * Created by Peter on 26/4/2015.
 */
public class TextDialog extends WidgetGroup {

    public interface OnDismissListener {
        public void onDismiss();
    }

    public static final String RES_PATH = Paths.RESOURCES + "TextDialog" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    private Texture background;

    private TextWidget<Void> content;
    private Rectangle contentPos;

    private Texture nextButton;
    private Rectangle nextPos;

    private String text;
    private OnDismissListener onDismissListener;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "TextDialogData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node bg = dom.getElementsByTagName("Background").item(0);
            background = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(bg, "FileName")));

            Node contentNode = dom.getElementsByTagName("ContentText").item(0);
            content = new TextWidget<>(TextWidget.Setting.fromXml(contentNode));
            contentPos = XmlHelper.loadRectangleFromXml(contentNode);

            Node nextNode = dom.getElementsByTagName("PageButton").item(0);
            nextButton = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(nextNode, "FileName")));
            nextPos = XmlHelper.loadRectangleFromXml(nextNode);
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "TextDialogData.xml", e);
        }
    }

    public TextDialog(GameScreen screen) {
        this.screen = screen;

        this.setWidth(Gdx.graphics.getWidth());
        this.setHeight(Gdx.graphics.getHeight());

        loadXml();

        this.setVisible(false);
        this.addListener(new Listener());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (this.isVisible()) {
            int width = background.getWidth();
            int height = background.getHeight();
            float x = getWidth() / 2 - width / 2;
            float y = getHeight() / 2 - height / 2;

            batch.draw(background, x, y, width, height);

            content.setText(text);
            content.setPosition(x + contentPos.getX(), y + contentPos.getY());
            content.setSize(contentPos.getWidth(), contentPos.getHeight());

            content.draw(batch, parentAlpha);
        }

        super.draw(batch, parentAlpha);
    }

    public void show(String s, OnDismissListener onDismissListener) {
        text = s;
        this.onDismissListener = onDismissListener;
        this.setVisible(true);
    }

    public void dispose() {
        background.dispose();
        nextButton.dispose();
    }

    public class Listener extends InputListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            TextDialog.this.setVisible(false);
            onDismissListener.onDismiss();
            return true;
        }
    }

}
