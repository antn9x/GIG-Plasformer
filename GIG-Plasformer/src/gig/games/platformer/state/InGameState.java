/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gig.games.platformer.state;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author anwedo
 */
public class InGameState extends BaseGameState {

    private boolean gamePause;
    private Node player;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        setEnabled(true);
        app.getViewPort().setBackgroundColor(ColorRGBA.Blue);
        player = (Node) assetManager.loadModel("Models/characters/Practice Rigging Man.j3o");
        rootNode.attachChild(player);
        /**
         * A white, spot light source.
         */
        PointLight lamp = new PointLight();
        lamp.setPosition(new Vector3f(0, 10, 10));
        lamp.setColor(ColorRGBA.White);
        rootNode.addLight(lamp);
        //Plan || Ground
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Textures/graas2.jpg"));
        Box floorBox = new Box(20, 0.25f, 20);
        Geometry floorGeometry = new Geometry("Floor", floorBox);
        floorGeometry.setMaterial(material);
        floorGeometry.setLocalTranslation(0, -0.25f, 0);
        floorGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(floorGeometry);
        //camera control
        app.getCamera().setLocation(new Vector3f(10, 10, 10));
        app.getCamera().lookAt(player.getLocalTranslation(), Vector3f.UNIT_Y);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            goInGame();
        } else {
            goOutGame();
        }
    }

    void goInGame() {
    }

    void pauseGame() {
    }

    void goOutGame() {
    }

    void resumeGame() {
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        float addZ = 1 * tpf;
        Vector3f pos = player.getLocalTranslation().clone();
        player.setLocalTranslation(pos.add(0, 0, addZ));
    }
}
