package com.zhsan.gamecomponents.maplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhsan.common.Pair;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.gamecomponents.maplayer.troopanimator.Animator;
import com.zhsan.gamecomponents.maplayer.troopanimator.AttackAnimator;
import com.zhsan.gamecomponents.maplayer.troopanimator.MoveAnimator;
import com.zhsan.gameobject.*;
import com.zhsan.screen.GameScreen;

import java.io.File;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Peter on 8/8/2015.
 */
public class TroopAnimationLayer implements MapLayer {

    public enum PendingTroopAnimationType {
        MOVE, ATTACK
    }

    public static class PendingTroopAnimation {
        public final Troop troop;
        public final PendingTroopAnimationType type;
        public final Point from, to;
        public final GameScenario.OnTroopAnimationDone onTroopAnimationDone;

        public PendingTroopAnimation(Troop troop, PendingTroopAnimationType type, Point from, Point to,
                                     GameScenario.OnTroopAnimationDone onTroopAnimationDone) {
            this.troop = troop;
            this.type = type;
            this.from = from;
            this.to = to;
            this.onTroopAnimationDone = onTroopAnimationDone;
        }
    }

    public static final String TROOP_RES_PATH = Paths.RESOURCES + "Troop" + File.separator;

    private Map<Pair<MilitaryKind, TroopAnimation>, Texture> troopImages = new HashMap<>();

    private int idleFrame = 0;

    private BlockingQueue<PendingTroopAnimation> pendingTroopAnimations = new ArrayBlockingQueue<>(1000);

    private List<Animator> runningAnimators = new ArrayList<>();
    private Set<Troop> runningTroops = new HashSet<>();

    private Map<Troop, TroopTitleWidget> troopTitleWidgets = new HashMap<>();
    private Map<Troop, Double> troopDirections = new HashMap<>();

    public void addPendingTroopAnimation(TroopAnimationLayer.PendingTroopAnimation animation) {
        pendingTroopAnimations.add(animation);
    }

    public boolean isNoPendingTroopAnimations() {
        return pendingTroopAnimations.size() == 0;
    }

    private TextureRegion getTroopImage(String resSet, Troop t, TroopAnimation.TroopAnimationKind animationKind, int frame, double dir, GameScenario scen) {
        MilitaryKind kind = t.getMilitary().getKind();
        TroopAnimation animation = scen.getTroopAnimations().get(animationKind.getId());

        Pair<MilitaryKind, TroopAnimation> pair = new Pair<>(kind, animation);

        if (!troopImages.containsKey(pair)) {
            FileHandle f = Gdx.files.external(TROOP_RES_PATH + resSet + File.separator + kind.getId() + File.separator + animation.getFileName());
            Texture texture = new Texture(f);
            troopImages.put(pair, texture);
        }

        int frameIndex = frame / animation.getIdleFrame() % animation.getFrameCount();
        int frameDirection = ((int) ((dir + 22.5) / 45) + 1) % 8;
        int spriteSize = animation.getSpriteSize();
        return new TextureRegion(troopImages.get(pair),
                frameIndex * spriteSize, frameDirection * spriteSize, spriteSize, spriteSize);
    }

    @Override
    public void draw(GameScreen screen, String resPack, DrawingHelpers helpers,
                     int zoom, Batch batch, float parentAlpha) {
        for (PendingTroopAnimation animation : new ArrayList<>(pendingTroopAnimations)) {
            if (runningTroops.contains(animation.troop)) continue;

            runningTroops.add(animation.troop);
            pendingTroopAnimations.remove(animation);
            if (helpers.isMapLocationOnScreen(animation.from) || helpers.isMapLocationOnScreen(animation.to)) {
                if (animation.type == PendingTroopAnimationType.MOVE) {
                    runningAnimators.add(new MoveAnimator(helpers, animation));
                } else if (animation.type == PendingTroopAnimationType.ATTACK) {
                    TroopAnimation ta = screen.getScenario().getTroopAnimations().get(TroopAnimation.TroopAnimationKind.ATTACK.getId());
                    int frameCount = ta.getFrameCount() * ta.getIdleFrame();
                    runningAnimators.add(new AttackAnimator(helpers, animation, frameCount));
                }
            }
        }

        Map<Troop, Point> drawnTroops = new HashMap<>();

        Set<Troop> toDraw = new HashSet<>(screen.getScenario().getTroops().getAll());
        Iterator<Animator> animatorIterator = runningAnimators.iterator();
        while (animatorIterator.hasNext()) {
            Animator animator = animatorIterator.next();

            Troop t = animator.getAnimation().troop;
            toDraw.remove(t);

            Point px = animator.step();
            double direction = Point.getDirection(animator.getAnimation().from, animator.getAnimation().to);
            troopDirections.put(t, direction);
            if (animator instanceof MoveAnimator) {
                TextureRegion image = getTroopImage(resPack, t, TroopAnimation.TroopAnimationKind.IDLE, idleFrame, direction, screen.getScenario());
                batch.draw(image, px.x, px.y, zoom, zoom);
            } else if (animator instanceof AttackAnimator) {
                TextureRegion image = getTroopImage(resPack, t, TroopAnimation.TroopAnimationKind.ATTACK, animator.getCurrentFrame(), direction, screen.getScenario());
                batch.draw(image, px.x, px.y, zoom, zoom);
            }

            drawnTroops.put(t, px);

            if (animator.isCompleted()) {
                animator.getAnimation().onTroopAnimationDone.onTroopAnimationDone();
                animatorIterator.remove();
                runningTroops.remove(t);
            }
        }

        for (Troop t : toDraw) {
            if (helpers.isMapLocationOnScreen(t.getLocation())) {
                double direction;
                if (troopDirections.containsKey(t)) {
                    direction = troopDirections.get(t);
                } else {
                    direction = 0;
                }

                TextureRegion image = getTroopImage(resPack, t, TroopAnimation.TroopAnimationKind.IDLE, idleFrame, direction, screen.getScenario());
                Point px = helpers.getPixelFromMapLocation(t.getLocation());
                batch.draw(image, px.x, px.y, zoom, zoom);

                drawnTroops.put(t, px);
            }
        }

        drawnTroops.entrySet().forEach(t -> {
            TroopTitleWidget widget = troopTitleWidgets.get(t.getKey());
            if (widget == null) {
                widget = new TroopTitleWidget(t.getKey());
                troopTitleWidgets.put(t.getKey(), widget);
            }
            widget.setPosition(t.getValue().x, t.getValue().y + zoom);
            widget.draw(batch, parentAlpha);
        });

        idleFrame++;
    }

    @Override
    public void dispose() {
        troopImages.values().forEach(Texture::dispose);
        troopTitleWidgets.values().forEach(TroopTitleWidget::dispose);
        TroopTitleWidget.disposeAll();
    }

}
