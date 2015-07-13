package com.zhsan.gamecomponents.commandframe;

import com.badlogic.gdx.graphics.g2d.Batch;
import org.w3c.dom.NodeList;

/**
 * Created by Peter on 13/7/2015.
 */
public interface CommandTab {

    public void loadXml(NodeList nodes);

    public void drawBackground(Batch batch, float parentAlpha);

    public void draw(Batch batch, float parentAlpha);

    public void dispose();

    public void onMouseMove(float x, float y);

    public void onClick(float x, float y);

}
