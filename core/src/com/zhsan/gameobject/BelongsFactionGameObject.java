package com.zhsan.gameobject;

/**
 * Created by Peter on 4/12/2015.
 */
public interface BelongsFactionGameObject extends GameObject {

    public Section getBelongedSection();

    public Faction getBelongedFaction();

}
