/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gig.games.platformer.managers;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import gig.games.platformer.gameplay.Player;
import gig.games.platformer.controls.PGCharacterControl;
import gig.games.platformer.gameplay.GameLevel;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author anwedo
 */
public class PGGamePlayManager extends AbstractManager {

    GameLevel currentLevel;
    ArrayList<GameLevel> levels = new ArrayList<GameLevel>(4);
    //character -----------------------------------------------
    Node playerModel;
    Node playerModelFile;
    Node magicFX;
    protected boolean firstPersonView = true;
    PGCharacterControl characterControl;
    // gameplay
    int retryTimes = 0;
    int score = 0;
    int bonus = 0;
    float playTime = 0;
    private Player mainPlayer;
    private Node player;

    public PGGamePlayManager() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        player = (Node) assetManager.loadModel("Models/characters/Practice Rigging Man.j3o");
        rootNode.attachChild(player);
        //camera control
        app.getCamera().setLocation(new Vector3f(-10, 2, 10));
        app.getCamera().lookAt(player.getLocalTranslation().add(0, 0, 4), Vector3f.UNIT_Y);
//        app.getCamera().setRotation(new Quaternion(0, 0.5f, 0, 0));


    }

    public Node getPlayer() {
        return player;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        float addZ = 1 * tpf;
        Vector3f pos = player.getLocalTranslation().clone();
        player.setLocalTranslation(pos.add(0, 0, addZ));
    }
}
