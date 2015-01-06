/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gig.games.platformer.managers;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
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
public class PGWorldManager extends AbstractManager {

    private Node worldNode;
    BulletAppState physicsState;

    public PGWorldManager() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        physicsState = new BulletAppState();
        stateManager.attach(physicsState);
        physicsState.setDebugEnabled(true);
        this.worldNode = rootNode;
        /**
         * A white, spot light source.
         */
        PointLight lamp = new PointLight();
        lamp.setPosition(new Vector3f(0, 10, 10));
        lamp.setColor(ColorRGBA.White);
        worldNode.addLight(lamp);
        //Plan || Ground
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Textures/graas2.jpg"));
        Box floorBox = new Box(20, 0.25f, 20);
        Geometry floorGeometry = new Geometry("Floor", floorBox);
        floorGeometry.setMaterial(material);
        floorGeometry.setLocalTranslation(0, -0.25f, 0);
        RigidBodyControl floorControl = new RigidBodyControl(0);
        floorGeometry.addControl(floorControl);
        worldNode.attachChild(floorGeometry); 
        getPhysicsSpace().add(floorControl);
    }

    public void attachWorld() {
//        worldTestHelper.createGizmo();
//        worldNode.attachChild(levelNode);
    }

    public PhysicsSpace getPhysicsSpace() {
        return physicsState.getPhysicsSpace();
    }

    public Node getWorldNode() {
        return worldNode;
    }
}
