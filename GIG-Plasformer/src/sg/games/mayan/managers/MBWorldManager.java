package sg.games.mayan.managers;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;


/**
 *
 * @author cuong.nguyenmanh2
 */
public class MBWorldManager extends AbstractManager {
    private Node worldNode;
    BulletAppState physicsState;
//    protected WorldTestHelper worldTestHelper;

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
