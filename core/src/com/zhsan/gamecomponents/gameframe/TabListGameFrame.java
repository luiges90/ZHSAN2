package com.zhsan.gamecomponents.gameframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.StateTexture;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Peter on 5/5/2015.
 */
public class TabListGameFrame extends GameFrame {

    public static final String RES_PATH = GameFrame.RES_PATH + "TabList" + File.separator;
    public static final String DATA_PATH = RES_PATH  + "Data" + File.separator;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "TabListData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "TabListData.xml", e);
        }
    }

    public TabListGameFrame(String title, @Nullable OnClick buttonListener) {
        super(title, buttonListener);

        loadXml();
    }

}
