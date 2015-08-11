package com.zhsan.gamecomponents.maplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhsan.common.Pair;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.gameobject.GameScenario;
import com.zhsan.gameobject.MilitaryKind;
import com.zhsan.gameobject.Troop;
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

    private int drawFrame = 0;

    private TextureRegion getTroopImage(String resSet, Troop t, GameScenario scen) {
        MilitaryKind kind = t.getMilitary().getKind();
        TroopAnimation animation = scen.getTroopAnimations().get(TroopAnimation.TroopAnimationKind.IDLE.getId());

        Pair<MilitaryKind, TroopAnimation> pair = new Pair<>(kind, animation);

        if (!troopImages.containsKey(pair)) {
            FileHandle f = Gdx.files.external(TROOP_RES_PATH + resSet + File.separator + kind.getId() + File.separator + animation.getFileName());
            Texture texture = new Texture(f);
            troopImages.put(pair, texture);
        }

        int frameIndex = drawFrame / animation.getIdleFrame() % animation.getFrameCount();
        int frameDirection = 0;
        int spriteSize = animation.getSpriteSize();
        return new TextureRegion(troopImages.get(pair),
                frameIndex * spriteSize, frameDirection * spriteSize, spriteSize, spriteSize);
    }

    @Override
    public void draw(GameScreen screen, String resPack, DrawingHelpers helpers,
                     int zoom, Batch batch, float parentAlpha) {
        for (Troop t : screen.getScenario().getTroops()) {
            if (helpers.isMapLocationOnScreen(t.getLocation())) {
                TextureRegion image = getTroopImage(resPack, t, screen.getScenario());
                Point px = helpers.getPixelFromMapLocation(t.getLocation());
                batch.draw(image, px.x, px.y, zoom, zoom);
            }
        }
        drawFrame++;
    }

    @Override
    public void dispose() {
        troopImages.values().forEach(Texture::dispose);
    }
}
