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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 26/4/2015.
 */
public class TextDialog extends WidgetGroup {

    public static enum TextKeys {
        EXIT_GAME("ExitGame"),
        EXIT_SAVE_GAME("ExitSaveGame")
        ;

        private final String xmlName;
        TextKeys(String name) {
            this.xmlName = name;
        }
    }

    private static TextKeys getKeyFromXml(String s) {
        for (TextKeys k : TextKeys.values()) {
            if (k.xmlName.equals(s)) {
                return k;
            }
        }
        throw new IllegalArgumentException("Key " + s + " not found.");
    }

    public interface OnDismissListener {
        public void onDismiss();
    }

    public static final String RES_PATH = Paths.RESOURCES + "TextDialog" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    private int marginBottom;

    private Texture background;

    private TextWidget<Void> content;
    private Rectangle contentPos;

    private Texture nextButton;
    private Rectangle nextPos;

    private String text;
    private OnDismissListener onDismissListener;

    private EnumMap<TextKeys, String> strings;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "TextDialogData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            marginBottom = Integer.parseInt(dom.getElementsByTagName("Margin").item(0).
                    getAttributes().getNamedItem("bottom").getNodeValue());

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

    private void loadText() {
        FileHandle f = Gdx.files.external(RES_PATH + "TextDialogText.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        strings = new EnumMap<>(TextKeys.class);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            NodeList list = dom.getElementsByTagName("TextDialogTexts");
            NamedNodeMap attributes = list.item(0).getAttributes();
            for (int i = 0; i < attributes.getLength(); ++i) {
                strings.put(getKeyFromXml(attributes.item(i).getNodeName()), attributes.item(i).getNodeValue());
            }
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "TextDialogText.xml", e);
        }
    }

    public TextDialog(GameScreen screen) {
        this.screen = screen;

        this.setWidth(Gdx.graphics.getWidth());
        this.setHeight(Gdx.graphics.getHeight());

        loadXml();
        loadText();

        this.setVisible(false);
        this.addListener(new Listener());
    }

    protected Rectangle getDialogPosition() {
        int width = background.getWidth();
        int height = background.getHeight();
        float x = getWidth() / 2 - width / 2;
        float y = screen.getToolBarHeight() + marginBottom;
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (this.isVisible()) {
            Rectangle pos = getDialogPosition();
            batch.draw(background, pos.x, pos.y, pos.width, pos.height);

            content.setText(text);
            content.setPosition(pos.x + contentPos.getX(), pos.y + contentPos.getY());
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

    public void show(TextKeys s, OnDismissListener onDismissListener) {
        this.show(getStringFromKey(s), onDismissListener);
    }

    protected final String getStringFromKey(TextKeys s) {
        return strings.get(s);
    }

    public void dispose() {
        background.dispose();
        nextButton.dispose();
        content.dispose();
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
