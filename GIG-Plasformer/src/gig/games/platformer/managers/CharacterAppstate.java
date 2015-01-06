/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gig.games.platformer.managers;

import com.jme3.app.state.AbstractAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.FlyByCamera;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author anwedo
 */
class CharacterAppstate extends AbstractAppState implements ActionListener {

    boolean rotate = false;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 1);
    boolean leftStrafe = false;
    boolean rightStrafe = false;
    boolean forward = false;
    boolean backward = false;
    boolean leftRotate = false;
    boolean rightRotate = false;
    private boolean lockView = false;
    private BulletAppState bulletAppState;
    private BetterCharacterControl physicsCharacter;
    private Node characterNode;

    ;
    private FlyByCamera flyCam;
    public CharacterAppstate(Node characterNode,  BetterCharacterControl physicsCharacter) {
     
        this.physicsCharacter = physicsCharacter;
        this.characterNode = characterNode;
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Strafe Left")) {
            if (value) {
                leftStrafe = true;
            } else {
                leftStrafe = false;
            }
        } else if (binding.equals("Strafe Right")) {
            if (value) {
                rightStrafe = true;
            } else {
                rightStrafe = false;
            }
        } else if (binding.equals("Rotate Left")) {
            if (value) {
                leftRotate = true;
            } else {
                leftRotate = false;
            }
        } else if (binding.equals("Rotate Right")) {
            if (value) {
                rightRotate = true;
            } else {
                rightRotate = false;
            }
        } else if (binding.equals("Walk Forward")) {
            if (value) {
                forward = true;
            } else {
                forward = false;
            }
        } else if (binding.equals("Walk Backward")) {
            if (value) {
                backward = true;
            } else {
                backward = false;
            }
        } else if (binding.equals("Jump")) {
            physicsCharacter.jump();
        } else if (binding.equals("Duck")) {
            if (value) {
                physicsCharacter.setDucked(true);
            } else {
                physicsCharacter.setDucked(false);
            }
        } else if (binding.equals("Lock View")) {
            if (value && lockView) {
                lockView = false;
            } else if (value && !lockView) {
                lockView = true;
            }
//            flyCam.setEnabled(!lockView);
            //            camNode.setEnabled(lockView);
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        // Get current forward and left vectors of model by using its rotation
        // to rotate the unit vectors
        Vector3f modelForwardDir = characterNode.getWorldRotation().mult(Vector3f.UNIT_Z);
        Vector3f modelLeftDir = characterNode.getWorldRotation().mult(Vector3f.UNIT_X);
        // WalkDirection is global!
        // You *can* make your character fly with this.
        walkDirection.set(0, 0, 0);
        if (leftStrafe) {
            walkDirection.addLocal(modelLeftDir.mult(3));
        } else if (rightStrafe) {
            walkDirection.addLocal(modelLeftDir.negate().multLocal(3));
        }
        if (forward) {
            walkDirection.addLocal(modelForwardDir.mult(3));
        } else if (backward) {
            walkDirection.addLocal(modelForwardDir.negate().multLocal(3));
        }
        physicsCharacter.setWalkDirection(walkDirection);
        // ViewDirection is local to characters physics system!
        // The final world rotation depends on the gravity and on the state of
        // setApplyPhysicsLocal()
        if (leftRotate) {
            Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateL.multLocal(viewDirection);
        } else if (rightRotate) {
            Quaternion rotateR = new Quaternion().fromAngleAxis(-FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateR.multLocal(viewDirection);
        }
        physicsCharacter.setViewDirection(viewDirection);
    }
}
