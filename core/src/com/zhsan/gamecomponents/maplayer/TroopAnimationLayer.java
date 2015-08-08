package com.zhsan.gamecomponents.maplayer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.zhsan.common.Pair;
import com.zhsan.common.Paths;
import com.zhsan.gameobject.MilitaryKind;
import com.zhsan.gameobject.TroopAnimation;
import com.zhsan.screen.GameScreen;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 8/8/2015.
 */
public class TroopAnimationLayer implements MapLayer {

    public static final String TROOP_RES_PATH = Paths.RESOURCES + "Troop" + File.separator;

    private Map<Pair<MilitaryKind, TroopAnimation>, Texture> troopImages = new HashMap<>();

    @Override
    public void draw(MainMapLayer mapLayer, GameScreen screen, String resPack, DrawingHelpers helpers,
                     int zoom, Batch batch, float parentAlpha) {

    }

    @Override
    public void dispose() {
        troopImages.values().forEach(Texture::dispose);
    }
}
