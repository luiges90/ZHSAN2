package com.zhsan.gamecomponents.commandframe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.zhsan.common.Point;
import com.zhsan.gamecomponents.GlobalStrings;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.gamecomponents.common.XmlHelper;
import com.zhsan.gamecomponents.gameframe.TabListGameFrame;
import com.zhsan.gameobject.Architecture;
import com.zhsan.gameobject.Person;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by Peter on 28/9/2015.
 */
public class OfficerCommandTab implements CommandTab {

    private ArchitectureCommandFrame parent;

    private Texture background;
    private Point backgroundPos;

    private StateTexture recall, move;
    private Rectangle recallPos, movePos;

    public OfficerCommandTab(ArchitectureCommandFrame parent) {
        this.parent = parent;
    }

    @Override
    public void loadXml(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node n = nodes.item(i);
            if (n.getNodeName().equals("Background")) {
                background = new Texture(Gdx.files.external(
                        ArchitectureCommandFrame.DATA_PATH + XmlHelper.loadAttribute(n, "FileName")
                ));
                backgroundPos = Point.fromXml(n);
            } else if (n.getNodeName().equals("Recall")) {
                recall = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                recallPos = XmlHelper.loadRectangleFromXml(n);
            } else if (n.getNodeName().equals("Move")) {
                move = StateTexture.fromXml(ArchitectureCommandFrame.DATA_PATH, n);
                movePos = XmlHelper.loadRectangleFromXml(n);
            }
        }
    }

    @Override
    public void drawBackground(Batch batch, float parentAlpha) {
        batch.draw(background, parent.getX() + backgroundPos.x, parent.getY() + backgroundPos.y, parent.getWidth(), parent.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(recall.get(), parent.getX() + recallPos.x, parent.getY() + recallPos.y, recallPos.width, recallPos.height);
        batch.draw(move.get(), parent.getX() + movePos.x, parent.getY() + movePos.y, movePos.width, movePos.height);
    }

    @Override
    public void dispose() {
        recall.dispose();
        move.dispose();
    }

    @Override
    public void onMouseMove(float x, float y) {
        if (recallPos.contains(x, y) && parent.getCurrentArchitecture().canRecallPerson()) {
            recall.setState(StateTexture.State.SELECTED);
        } else {
            recall.setState(StateTexture.State.NORMAL);
        }
        if (movePos.contains(x, y) && parent.getCurrentArchitecture().canMovePerson()) {
            move.setState(StateTexture.State.SELECTED);
        } else {
            move.setState(StateTexture.State.NORMAL);
        }
    }

    @Override
    public void onClick(float x, float y) {
        if (recallPos.contains(x, y) && parent.getCurrentArchitecture().canRecallPerson()) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.RECALL_PERSON), TabListGameFrame.ListKindType.PERSON,
                    parent.getCurrentArchitecture().getRecallablePersonList(), TabListGameFrame.Selection.MULTIPLE,
                    selectedItems -> {
                        selectedItems.forEach(o -> {
                            Person p = (Person) o;
                            p.moveToArchitecture(parent.getCurrentArchitecture());
                        });
                    });
            recall.setState(StateTexture.State.NORMAL);
        }
        if (movePos.contains(x, y) && parent.getCurrentArchitecture().canMovePerson()) {
            parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.MOVE_PERSON), TabListGameFrame.ListKindType.PERSON,
                    parent.getCurrentArchitecture().getPersons(), TabListGameFrame.Selection.MULTIPLE,
                    selectedItems -> {
                        parent.getScreen().showTabList(GlobalStrings.getString(GlobalStrings.Keys.SELECT_DESTINATION), TabListGameFrame.ListKindType.ARCHITECTURE,
                                parent.getCurrentArchitecture().getBelongedFaction().getArchitectures().filter(a -> a != parent.getCurrentArchitecture()),
                                TabListGameFrame.Selection.SINGLE,
                                selectedItems1 -> {
                                    selectedItems.forEach(o -> {
                                        Person p = (Person) o;
                                        p.moveToArchitecture((Architecture) selectedItems1.get(0));
                                    });
                                });
                    });
        }
    }

    @Override
    public void invalidate() {

    }

    @Override
    public void onUnselect() {

    }
}
