package com.zhsan.gamecomponents.maplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.gameobject.Facility;
import com.zhsan.gameobject.FacilityKind;
import com.zhsan.gameobject.GameSurvey;
import com.zhsan.screen.GameScreen;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 4/8/2015.
 */
public class FacilityLayer implements MapLayer {

    public static final String FACILITY_RES_PATH = Paths.RESOURCES + "Facility" + File.separator;

    private Map<FacilityKind, Texture> facilityKindImages = new HashMap<>();

    private Texture getFacilityImage(String resSet, FacilityKind kind) {
        if (!facilityKindImages.containsKey(kind)) {
            FileHandle f = Gdx.files.external(FACILITY_RES_PATH + resSet + File.separator + kind.getId() + ".png");
            if (!f.exists()) {
                f = Gdx.files.external(FACILITY_RES_PATH + GameSurvey.DEFAULT_RESOURCE_PACK + File.separator + kind.getId() + ".png");
            }

            Texture t = new Texture(f);
            facilityKindImages.put(kind, t);
        }
        return facilityKindImages.get(kind);
    }

    @Override
    public void draw(MainMapLayer mapLayer, GameScreen screen, String resPack, DrawingHelpers helpers, int zoom, Batch batch, float parentAlpha) {
        for (Facility f : screen.getScenario().getFacilities()) {
            if (helpers.isMapLocationOnScreen(f.getLocation())) {
                Texture facilityImage = getFacilityImage(resPack, f.getKind());
                Point px = helpers.getPixelFromMapLocation(f.getLocation());
                batch.draw(facilityImage, px.x, px.y, zoom, zoom);
            }
        }
    }

    @Override
    public void dispose() {
        facilityKindImages.values().forEach(Texture::dispose);
    }
}
