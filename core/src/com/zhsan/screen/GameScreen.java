package com.zhsan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.GlobalVariables;
import com.zhsan.common.Point;
import com.zhsan.gamecomponents.PersonPortrait;
import com.zhsan.gamecomponents.ScreenBlind;
import com.zhsan.gamecomponents.commandframe.ArchitectureCommandFrame;
import com.zhsan.gamecomponents.contextmenu.ContextMenu;
import com.zhsan.gamecomponents.maplayer.MainMapLayer;
import com.zhsan.gamecomponents.gameframe.FileGameFrame;
import com.zhsan.gamecomponents.gameframe.TabListGameFrame;
import com.zhsan.gamecomponents.maplayer.TroopAnimationLayer;
import com.zhsan.gamecomponents.textdialog.ConfirmationDialog;
import com.zhsan.gamecomponents.textdialog.TextDialog;
import com.zhsan.gamecomponents.toolbar.ToolBar;
import com.zhsan.gameobject.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameScreen extends WidgetGroup {

    public interface RunningDaysListener {
        public void started(int daysLeft);

        public void passed(int daysLeft);

        public void stopped();
    }

    private GameScenario scen;

    private MainMapLayer mapLayer;
    private ContextMenu contextMenu;

    private FileGameFrame saveGameFrame, loadGameFrame;
    private TextDialog textDialog;
    private ConfirmationDialog confirmationDialog;
    private ToolBar toolBar;

    private TabListGameFrame tabListGameFrame;

    private ArchitectureCommandFrame architectureCommandFrame;

    private ScreenBlind screenBlind;

    private DayRunner dayRunner;

    private PersonPortrait personPortrait;

    private void addOverlayedMapLayerScrollListener(WidgetGroup widget) {
        widget.addListener(new InputListener(){
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                mapLayer.handleMouseMoved(event, widget.getX() + x , widget.getY() + y);
                return false;
            }
        });
    }

    public GameScreen(GameScenario scen) {
        this.scen = scen;
        this.dayRunner = new DayRunner();

        personPortrait = new PersonPortrait(scen.getGameSurvey().getResourcePackName());

        toolBar = new ToolBar(this);
        toolBar.setPosition(0, 0);
        toolBar.setWidth(Gdx.graphics.getWidth());

        mapLayer = new MainMapLayer(this);
        mapLayer.setPosition(0, getToolBarHeight());
        mapLayer.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - getToolBarHeight());
        this.addActor(mapLayer);

        screenBlind = new ScreenBlind(this);
        screenBlind.setPosition(0, Gdx.graphics.getHeight() - screenBlind.getHeight());
        addOverlayedMapLayerScrollListener(screenBlind);
        this.addActor(screenBlind);

        contextMenu = new ContextMenu(this);
        contextMenu.setPosition(0, getToolBarHeight());
        contextMenu.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - getToolBarHeight());
        this.addActor(contextMenu);

        this.addActor(toolBar);

        architectureCommandFrame = new ArchitectureCommandFrame(this);
        architectureCommandFrame.setVisible(false);
        architectureCommandFrame.setPosition(0, Gdx.graphics.getHeight() - architectureCommandFrame.getHeight());
        addOverlayedMapLayerScrollListener(architectureCommandFrame);
        this.addActor(architectureCommandFrame);

        textDialog = new TextDialog(this);
        this.addActor(textDialog);

        confirmationDialog = new ConfirmationDialog(this);
        this.addActor(confirmationDialog);

        tabListGameFrame = new TabListGameFrame(this);
        this.addActor(tabListGameFrame);

        new Thread(this::runAi).start();
    }

    public void resize(int width, int height) {
        toolBar.setPosition(0, 0);
        toolBar.setWidth(width);
        toolBar.resize(width, height);

        mapLayer.setPosition(0, getToolBarHeight());
        mapLayer.setSize(width, height - getToolBarHeight());
        mapLayer.resize(width, height);

        contextMenu.setPosition(0, getToolBarHeight());
        contextMenu.setSize(width, height - getToolBarHeight());
        contextMenu.resize(width, height);

        tabListGameFrame.setPosition(0, 0);
        tabListGameFrame.setSize(width, height);
        tabListGameFrame.resize(width, height);

        screenBlind.setPosition(0, Gdx.graphics.getHeight() - screenBlind.getHeight());
        architectureCommandFrame.setPosition(0, Gdx.graphics.getHeight() - architectureCommandFrame.getHeight());

        if (saveGameFrame != null) {
            saveGameFrame.resize(width, height);
        }
        if (loadGameFrame != null) {
            loadGameFrame.resize(width, height);
        }

    }

    public void showTabList(TabListGameFrame.ListKindType type, GameObjectList<?> showingData) {
        dayRunner.pauseRunDays();
        tabListGameFrame.show(type, showingData);
    }

    public void showTabList(String title, TabListGameFrame.ListKindType type, GameObjectList<?> showingData,
                            TabListGameFrame.Selection selection, TabListGameFrame.OnItemSelectedListener onItemSelectedListener) {
        dayRunner.pauseRunDays();
        tabListGameFrame.show(title, type, showingData, selection, onItemSelectedListener);
    }

    public void showContextMenu(ContextMenu.MenuKindType type, Point position) {
        contextMenu.show(type, scen, position);
    }

    public void showContextMenu(ContextMenu.MenuKindType type, Object item, Point position) {
        showContextMenu(type, -1, item, position);
    }

    public void showContextMenu(ContextMenu.MenuKindType type, int xmlId, Object item, Point position) {
        dayRunner.pauseRunDays();
        contextMenu.show(type, xmlId, item, position);
    }

    public void showArchitectureCcommandFrame(Architecture architecture) {
        architectureCommandFrame.show(architecture);
    }

    public void hideArchitectureCommandFrame() {
        architectureCommandFrame.hide();
    }

    public void showSaveGameFrame() {
        dayRunner.pauseRunDays();
        if (saveGameFrame == null) {
            saveGameFrame = new FileGameFrame(FileGameFrame.Usage.SAVE, scen::save);
            this.addActor(saveGameFrame);
        } else {
            saveGameFrame.show();
        }
    }

    public void showLoadGameFrame() {
        dayRunner.pauseRunDays();
        if (loadGameFrame == null) {
            loadGameFrame = new FileGameFrame(FileGameFrame.Usage.LOAD, file -> scen = new GameScenario(file, false, -1));
            this.addActor(loadGameFrame);
        } else {
            loadGameFrame.show();
        }
    }

    public boolean allowMapMove() {
        return !tabListGameFrame.isVisible() && !contextMenu.isVisible() && !architectureCommandFrame.isVisible() &&
                (saveGameFrame == null || !saveGameFrame.isVisible()) &&
                (loadGameFrame == null || !loadGameFrame.isVisible());
    }

    public boolean allowRunDays() {
        return !tabListGameFrame.isVisible() && !contextMenu.isVisible() &&
                (saveGameFrame == null || !saveGameFrame.isVisible()) &&
                (loadGameFrame == null || !loadGameFrame.isVisible());
    }

    public GameScenario getScenario() {
        return scen;
    }

    public void showTextDialog(String content, TextDialog.OnDismissListener onDismissListener) {
        dayRunner.pauseRunDays();
        textDialog.show(content, onDismissListener);
    }

    public void showTextDialog(TextDialog.TextKeys key, TextDialog.OnDismissListener onDismissListener) {
        dayRunner.pauseRunDays();
        textDialog.show(key, onDismissListener);
    }

    public void showConfirmationDialog(String content, ConfirmationDialog.OnDismissListener onDismissListener) {
        dayRunner.pauseRunDays();
        confirmationDialog.show(content, onDismissListener);
    }

    public void showConfirmationDialog(TextDialog.TextKeys key, ConfirmationDialog.OnDismissListener onDismissListener) {
        dayRunner.pauseRunDays();
        confirmationDialog.show(key, onDismissListener);
    }

    public int getToolBarHeight() {
        return toolBar.getToolbarHeight();
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public MainMapLayer getMapLayer() {
        return mapLayer;
    }

    public DayRunner getDayRunner() {
        return dayRunner;
    }

    public Texture getPortrait(int id) {
        return personPortrait.getPortrait(id);
    }

    public Texture getSmallPortrait(int id) {
        return personPortrait.getSmallPortrait(id);
    }

    private ExecutorService pool = Executors.newCachedThreadPool();
    private void runAi() {
        GameObjectList<Faction> factions = scen.getFactions();
        List<Callable<Void>> runnables = new ArrayList<>();
        factions.filter(f -> f != scen.getCurrentPlayer()).forEach(f -> runnables.add(() -> {
            f.ai();
            return null;
        }));

        try {
            pool.invokeAll(runnables, GlobalVariables.aiTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public class DayRunner {

        private List<RunningDaysListener> runningDaysListeners = new ArrayList<>();

        public void addRunningDaysListener(RunningDaysListener l) {
            runningDaysListeners.add(l);
        }

        private Thread dayRunner;
        private AtomicBoolean pauseDayRunner = new AtomicBoolean(false);
        private AtomicBoolean stopDayRunner = new AtomicBoolean(false);
        private volatile boolean dayRunning = false;
        private volatile int moreDays;
        private final Object dayPauseLock = new Object();

        public void continueRunDays() {
            if (dayRunning) {
                pauseRunDays();
            } else {
                runDays(dayRunner != null && dayRunner.isAlive() ? 0 : getDaysOnDateRunner());
            }
        }

        public void runDays(int days) {
            if (dayRunner != null && dayRunner.isAlive()) {
                synchronized (GameScreen.this) {
                    moreDays = days;
                }
                pauseDayRunner.set(false);
                synchronized (dayPauseLock) {
                    dayPauseLock.notifyAll();
                }
                return;
            }

            Runnable dayRunnable = () -> {
                for (RunningDaysListener x : runningDaysListeners) {
                    x.started(days);
                }

                for (int i = 0; i < days; ++i) {
                    dayRunning = true;

                    getScenario().advanceDay(new GameScenario.OnTroopDone() {
                        @Override
                        public void onTroopStepDone(Troop t, Point oldLoc, Point newLoc) {
                            mapLayer.addPendingTroopAnimation(
                                    new TroopAnimationLayer.PendingTroopAnimation(t, TroopAnimationLayer.PendingTroopAnimationType.MOVE,
                                            oldLoc, newLoc));
                        }

                        @Override
                        public void onAttackStepDone(Troop t, Troop target, List<DamagePack> damages) {
                            // TODO add attack animation
                        }
                    });
                    while (!mapLayer.isNoPendingTroopAnimations()); // wait animation thread to clear its queue

                    runAi();

                    synchronized (GameScreen.this) {
                        i -= moreDays;
                        if (days - i - 1 > GlobalVariables.maxRunningDays) {
                            i += days - i - GlobalVariables.maxRunningDays - 1;
                        }
                        moreDays = 0;
                    }

                    for (RunningDaysListener x : runningDaysListeners) {
                        x.passed(days - i - 1);
                    }

                    architectureCommandFrame.invalidateData();

                    while (pauseDayRunner.get()) {
                        synchronized (dayPauseLock) {
                            try {
                                dayRunning = false;
                                dayPauseLock.wait();
                            } catch (InterruptedException e) {
                                // ignore
                            }
                        }
                    }

                    if (stopDayRunner.compareAndSet(true, false)) {
                        break;
                    }
                }

                dayRunning = false;
                for (RunningDaysListener x : runningDaysListeners) {
                    x.stopped();
                }
            };
            dayRunner = new Thread(dayRunnable, "Day runner");
            dayRunner.start();
        }

        public void pauseRunDays() {
            if (dayRunning) {
                pauseDayRunner.set(true);
            }
        }

        public void stopRunDays() {
            if (dayRunning) {
                pauseDayRunner.set(false);
                stopDayRunner.set(true);
                synchronized (dayPauseLock) {
                    dayPauseLock.notifyAll();
                }
            }
        }

        public boolean isDayRunning() {
            return dayRunning;
        }

    }

    private int getDaysOnDateRunner() {
        return toolBar.getDaysToGo();
    }

    public void dispose() {
        personPortrait.dispose();
        toolBar.dispose();
        mapLayer.dispose();
        contextMenu.dispose();
        textDialog.dispose();
        confirmationDialog.dispose();
        tabListGameFrame.dispose();
        screenBlind.dispose();
        architectureCommandFrame.dispose();
        if (saveGameFrame != null) {
            saveGameFrame.dispose();
        }
        if (loadGameFrame != null) {
            loadGameFrame.dispose();
        }
    }

}
