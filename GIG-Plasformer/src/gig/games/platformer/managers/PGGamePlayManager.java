/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gig.games.platformer.managers;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
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
    private BetterCharacterControl physicsCharacter;
    private CharacterAppstate ccAppState;

    public PGGamePlayManager() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        player = (Node) assetManager.loadModel("Models/characters/Practice Rigging Man.j3o");
        player.setLocalTranslation(0, 2, 0);
        this.app.getRootNode().attachChild(player);
        physicsCharacter = new BetterCharacterControl(0.3f, 1.5f, 0.1f);
        player.addControl(physicsCharacter);
        ccAppState = new CharacterAppstate(player, physicsCharacter);
        this.stateManager.attach(ccAppState);
        this.app.getWorldManager().getPhysicsSpace().add(physicsCharacter);
        //camera control
        app.getCamera().setLocation(player.getLocalTranslation().add(-5, 2, 4));
        app.getCamera().lookAt(player.getLocalTranslation().add(0, 0, 2), Vector3f.UNIT_Y);
//        app.getCamera().setRotation(new Quaternion(0, 0.5f, 0, 0));
        setupKeys();
    }

    public Node getPlayer() {
        return player;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        float addZ = 1 * tpf;
        Vector3f pos = player.getLocalTranslation().clone();
//        player.setLocalTranslation(pos.add(0, 0, 1));
    }

    private void setupKeys() {
        this.inputManager.addMapping("Strafe Left",
                new KeyTrigger(KeyInput.KEY_U),
                new KeyTrigger(KeyInput.KEY_Z));
        this.inputManager.addMapping("Strafe Right",
                new KeyTrigger(KeyInput.KEY_O),
                new KeyTrigger(KeyInput.KEY_X));
        this.inputManager.addMapping("Rotate Left",
                new KeyTrigger(KeyInput.KEY_J),
                new KeyTrigger(KeyInput.KEY_LEFT));
        this.inputManager.addMapping("Rotate Right",
                new KeyTrigger(KeyInput.KEY_L),
                new KeyTrigger(KeyInput.KEY_RIGHT));
        this.inputManager.addMapping("Walk Forward",
                new KeyTrigger(KeyInput.KEY_I),
                new KeyTrigger(KeyInput.KEY_UP));
        this.inputManager.addMapping("Walk Backward",
                new KeyTrigger(KeyInput.KEY_K),
                new KeyTrigger(KeyInput.KEY_DOWN));
        this.inputManager.addMapping("Jump",
                new KeyTrigger(KeyInput.KEY_F),
                new KeyTrigger(KeyInput.KEY_SPACE));
        this.inputManager.addMapping("Duck",
                new KeyTrigger(KeyInput.KEY_G),
                new KeyTrigger(KeyInput.KEY_LSHIFT),
                new KeyTrigger(KeyInput.KEY_RSHIFT));
        this.inputManager.addMapping("Lock View",
                new KeyTrigger(KeyInput.KEY_RETURN));
        this.inputManager.addListener(ccAppState, "Strafe Left", "Strafe Right");
        this.inputManager.addListener(ccAppState, "Rotate Left", "Rotate Right");
        this.inputManager.addListener(ccAppState, "Walk Forward", "Walk Backward");
        this.inputManager.addListener(ccAppState, "Jump", "Duck", "Lock View");
    }
}
