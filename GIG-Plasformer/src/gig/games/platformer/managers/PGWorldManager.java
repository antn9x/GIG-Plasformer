/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gig.games.platformer.managers;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;

/**
 *
 * @author anwedo
 */
public class PGWorldManager extends AbstractManager {

    private Node worldNode;
    BulletAppState physicsState;

    public PGWorldManager() {
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
