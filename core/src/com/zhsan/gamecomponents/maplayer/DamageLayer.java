package com.zhsan.gamecomponents.maplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhsan.common.GlobalVariables;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.common.Utility;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.gameframe.GameFrame;
import com.zhsan.gameobject.DamagePack;
import com.zhsan.screen.GameScreen;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Peter on 5/9/2015.
 */
public class DamageLayer implements MapLayer {

    private static class PackTime {
        private final DamagePack pack;
        private final int beginTime;

        public PackTime(DamagePack pack, int beginTime) {
            this.pack = pack;
            this.beginTime = beginTime;
        }
    }

    private enum CombatNumberRow {GAIN_TROOP, LOSE_TROOP}

    private static final int INCREASE_COL = 10;
    private static final int DECREASE_COL = 11;

    private Texture combatNumber;
    private int width, height;

    private List<DamagePack> pendingPacks = new ArrayList<>();
    private List<PackTime> showingPacks = new ArrayList<>();

    public static final String RES_PATH = Paths.RESOURCES + "CombatNumber" + File.separator;
    public static final String DATA_PATH = RES_PATH  + "Data" + File.separator;

    private void loadXml() {
        FileHandle f = Gdx.files.external(RES_PATH + "CombatNumberData.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node node = dom.getElementsByTagName("CombatNumber").item(0);
            combatNumber = new Texture(Gdx.files.external(DATA_PATH + XmlHelper.loadAttribute(node, "FileName")));
            width = Integer.parseInt(XmlHelper.loadAttribute(node, "Width"));
            height = Integer.parseInt(XmlHelper.loadAttribute(node, "Height"));
        } catch (Exception e) {
            throw new FileReadException(RES_PATH + "CombatNumberData.xml", e);
        }
    }

    public DamageLayer() {
        loadXml();
    }

    public void addDamagePack(List<DamagePack> pack) {
        pendingPacks.addAll(pack);
    }

    private final int[] d10 = {1, 10, 100, 1000, 10000, 100000, 1000000, 100000000, 1000000000, 1000000000};
    private int[] extractDigits(int n) {
        int x = Math.abs(n);

        int[] r = new int[d10.length];
        int digits = 0;
        for (int i = 0; i < d10.length; ++i) {
            r[i] = x / d10[i] % 10;
            if (r[i] > 0) {
                digits++;
            }
        }

        if (digits == 0) {
            digits = 1;
        }
        return Utility.reverse(Arrays.copyOfRange(r, 0, digits - 1));
    }

    private void drawDamage(DamagePack pack, DrawingHelpers helpers, Batch batch, float parentAlpha) {
        if (helpers.isMapLocationOnScreen(pack.location)) {
            Point drawAt = helpers.getPixelFromMapLocation(pack.location);
            int[] digits = extractDigits(pack.quantity);

            CombatNumberRow row;
            if (pack.quantity >= 0) {
                row = CombatNumberRow.GAIN_TROOP;
            } else {
                row = CombatNumberRow.LOSE_TROOP;
            }

            TextureRegion sign;
            if (pack.quantity > 0) {
                sign = new TextureRegion(combatNumber, INCREASE_COL * width, row.ordinal() * height, width, height);
            } else {
                sign = new TextureRegion(combatNumber, DECREASE_COL * width, row.ordinal() * height, width, height);
            }
            batch.draw(sign, drawAt.x, drawAt.y);

            for (int i = 0; i < digits.length; ++i){
                TextureRegion n = new TextureRegion(combatNumber, digits[i] * width, row.ordinal() * height, width, height);
                batch.draw(n, drawAt.x + i * width, drawAt.y);
            }
        }
    }

    private static int drawTime = 0;

    @Override
    public void draw(GameScreen screen, String resPack, DrawingHelpers helpers, int zoom, Batch batch, float parentAlpha) {
        showingPacks.addAll(pendingPacks.stream().map(pack -> new PackTime(pack, drawTime)).collect(Collectors.toList()));
        Iterator<PackTime> it = showingPacks.iterator();
        while (it.hasNext()) {
            PackTime pt = it.next();
            if (drawTime - pt.beginTime >= GlobalVariables.damageShowTime) {
                it.remove();
            } else {
                drawDamage(pt.pack, helpers, batch, parentAlpha);
            }
        }
        drawTime++;
    }

    @Override
    public void dispose() {
        combatNumber.dispose();
    }
}
