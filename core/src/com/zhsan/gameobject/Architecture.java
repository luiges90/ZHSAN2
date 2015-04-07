package com.zhsan.gameobject;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Peter on 7/4/2015.
 */
public class Architecture extends GameObject {

    private String nameImageName;
    private Texture nameImage;

    private ArchitectureKind kind;

    private Architecture(int id) {
        super(id);
    }



}
