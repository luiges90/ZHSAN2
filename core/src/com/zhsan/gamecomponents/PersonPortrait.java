package com.zhsan.gamecomponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.zhsan.common.Paths;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 1/7/2015.
 */
public final class PersonPortrait {

    public static final String RES_PATH = Paths.RESOURCES + "PersonPortrait" + File.separator;

    private final String resPack;

    private Map<Integer, Texture> portraits = new HashMap<>();
    private Map<Integer, Texture> smallPortraits = new HashMap<>();

    public PersonPortrait(String resPack) {
        this.resPack = resPack;
    }

    private void loadPortrait(int id){
        Texture portrait = new Texture(Gdx.files.external(
                RES_PATH + resPack + File.separator + String.valueOf(id) + ".jpg"));
        Texture smallPortrait = new Texture(Gdx.files.external(
                RES_PATH + resPack + File.separator + String.valueOf(id) + "s.jpg"));

        portraits.put(id, portrait);
        smallPortraits.put(id, smallPortrait);
    }

    public Texture getPortrait(int id) {
        if (portraits.containsKey(id)) {
            return portraits.get(id);
        }

        loadPortrait(id);

        return portraits.get(id);
    }

    public Texture getSmallPortrait(int id) {
        if (smallPortraits.containsKey(id)) {
            return smallPortraits.get(id);
        }

        loadPortrait(id);

        return smallPortraits.get(id);
    }

    public void dispose() {
        portraits.values().forEach(Texture::dispose);
    }

}
