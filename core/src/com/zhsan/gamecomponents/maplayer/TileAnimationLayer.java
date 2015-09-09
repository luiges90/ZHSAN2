package com.zhsan.gamecomponents.maplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.gamecomponents.maplayer.tileanimator.OneShotAnimator;
import com.zhsan.gamecomponents.maplayer.tileanimator.TileAnimator;
import com.zhsan.gameobject.GameScenario;
import com.zhsan.gameobject.TroopAnimation;
import com.zhsan.screen.GameScreen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Peter on 9/9/2015.
 */
public class TileAnimationLayer implements MapLayer {

    public static final String RES_PATH = Paths.RESOURCES + "TileEffect" + File.separator;

    private BlockingQueue<TileAnimator> runningAnimations = new ArrayBlockingQueue<>(1000);

    private Map<Integer, Texture> tileImages = new HashMap<>();

    public void showTileAnimation(Point location, TroopAnimation animation) {
        runningAnimations.add(new OneShotAnimator(location, animation));
    }

    private TextureRegion getTileImage(TroopAnimation animation, int frame) {
        if (!tileImages.containsKey(animation.getId())) {
            FileHandle f = Gdx.files.external(RES_PATH + animation.getFileName());
            Texture texture = new Texture(f);
            tileImages.put(animation.getId(), texture);
        }
        int frameIndex = frame / animation.getIdleFrame() % animation.getFrameCount();
        int spriteSize = animation.getSpriteSize();
        return new TextureRegion(tileImages.get(animation.getId()),
                frameIndex * spriteSize, 0, spriteSize, spriteSize);
    }

    @Override
    public void draw(GameScreen screen, String resPack, DrawingHelpers helpers, int zoom, Batch batch, float parentAlpha) {
        new ArrayList<>(runningAnimations).forEach(tileAnimator -> {
            Point mapLoc = tileAnimator.step();
            int frame = tileAnimator.getCurrentFrame();

            if (helpers.isMapLocationOnScreen(mapLoc)) {
                Point drawAt = helpers.getPixelFromMapLocation(mapLoc);
                batch.draw(getTileImage(tileAnimator.getAnimation(), frame), drawAt.x, drawAt.y, zoom, zoom);
            } else {
                runningAnimations.remove(tileAnimator);
            }

            if (tileAnimator.isCompleted()) {
                runningAnimations.remove(tileAnimator);
            }
        });
    }

    @Override
    public void dispose() {
        tileImages.values().forEach(Texture::dispose);
    }

}
