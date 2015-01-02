package sg.games.mayan.gameplay.controls;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.BetterCharacterControl;

import com.jme3.input.ChaseCamera;
import com.jme3.input.controls.InputListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import sg.games.mayan.stage.fx.CamShaker;
import java.util.HashMap;
import java.util.logging.Logger;

import sg.games.mayan.gameplay.MBGameCharacter;
import sg.games.mayan.main.MBMain;

/**
 *
 * @author hungcuong
 */
/*
 * This character control try to immitate the Character movement in the game 
 * PrinceOfPersian : the two thrones
 * with ...
 */
public class MBCharacterControl extends AbstractControl implements AnimEventListener, InputListener {
    // Shortcuts

    private static final Logger logger = Logger.getLogger(MBCharacterControl.class.getName());
    MBMain app = MBMain.getInstance();
    // FIXME: Replace with AnimBehaviours
    // Other controls
    BetterCharacterControl physicControl;
    Node playerModel;
    SkeletonControl skeletonControl;
    WeaponControl weaponControl;
    //camera
    Camera cam;
    ChaseCamera chaseCam;
    CamShaker camShaker;
    //animation    
    AnimControl animationControl;
    AnimChannel animationChannel;
    AnimChannel shootingChannel;
    AnimChannel lowerBody, upperBody;
    HashMap<String, String> animNameMap = new HashMap<String, String>();
    // AnimNameMap provide a safe way to change animation names!
    /* Animation atributes */
    Vector3f walkDirection = new Vector3f();
    boolean left = false, right = false, up = false, down = false;
    /* Steering and locomotion, physics */
    Vector3f travelPerFrame;
    Vector3f oldPos;
    float defaultMoveSpeed;
    float moveSpeed;
    float changeMoveSpeed = 0.5f;
    float changeViewSpeed = 0.5f;
    float mass = 60f;
    float bodyHeight = 1.8f;
    float radiusSize = 0.5f;
    Vector3f defaulJumpForce = new Vector3f(0.0f, 270f, 0.0f);
    Vector3f defaulGravity = new Vector3f(0.0f, -9.81f * 10, 0.0f);
    // State flags and status
    boolean prepareJump;
    boolean jumping;
    boolean rolling;
    float airTime = 0;
    float distanceOfFall = 0;
    int currentAttackType = 1;
    // IK
//    private IKControl ikControls;
//    private IKControl handIK;
//    private IKControl footIK;
    // Rigs BoundingVolume
    // FIXME: Replace with RagDolls
//    private BoundingVolume leftHandBound;
//    private BoundingVolume rightHandBound;
//    private BoundingVolume leftFootBound;
//    private BoundingVolume rightFootBound;
    // collision 
    private float collisionTimePassed = 0;
    private float collisionTimeInterval = 1;
    private float awareItemDistance = 5;
    private Spatial selectedEntitySpatial;
    private MBGameCharacter character;

    public MBCharacterControl() {
        awareItemDistance = radiusSize * 2;
    }

    void moveChar(float tpf) {
        if (oldPos != null) {
            this.travelPerFrame = getLocation().subtract(oldPos);
        } else {
            this.travelPerFrame = new Vector3f();
        }
        /*
         if (isOnWater()) {
         moveSpeed = 0.04f;
         }*/
        // Gameplay
        Vector3f camDir = cam.getDirection().clone();
        Vector3f camLeft = cam.getLeft().clone();
        camDir.multLocal(moveSpeed);
        camLeft.multLocal(moveSpeed);

        camDir.y = 0;
        camLeft.y = 0;
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        if (!physicControl.isOnGround()) {
            //System.out.println(airTime);
            airTime = airTime + tpf;
            distanceOfFall += travelPerFrame.length();
            //System.out.println(" Fall :" + distanceOfFall);
            if (airTime > 0.05f) {
                jumping = true;
            }

        } else {
            airTime = 0;
            distanceOfFall = 0;
            jumping = false;
            rolling = false;
        }

        Vector3f tempViewDir = physicControl.getViewDirection().clone();
        Vector3f tempWalkDir = physicControl.getWalkDirection().clone();
        physicControl.setViewDirection(tempViewDir.interpolateLocal(walkDirection, 1 / changeViewSpeed * tpf));
        physicControl.setWalkDirection(tempViewDir.interpolateLocal(tempWalkDir, 1 / changeMoveSpeed * tpf));

        this.oldPos = getLocation().clone();
    }
    // FIXME: Replace with AnimBehavours

    public void updateAnimation(float tpf) {
        if (jumping) {

            if (!isCurrentAnim("Jump")) {
                setAnim("Jump", 0f, 1.0f, LoopMode.DontLoop);
                if (!rolling) {
                    //setAnim("Jump", 0.1f, 1.0f, LoopMode.DontLoop);
                    //System.out.println("Jumping");
                }


                //animationChannel.setTime(animationChannel.getAnimMaxTime() / 3 * 2);

            } else {
                /*
                 if (airTime > 1f) {
                 // On the air
                 if (!isCurrentAnim("FlyRolling")) {
                 setAnim("FlyRolling", 0.8f, 1.2f, LoopMode.Loop);
                 rolling = true;
                 }
                 }
                 */
            }
        } else if (walkDirection.length() == 0) {
            // Standing
            if (!isCurrentAnim("Stand")) {
                setAnim("Stand", 0, 2f, LoopMode.Loop);
                moveSpeed = defaultMoveSpeed * 0.1f;
            }
        } else {

            if (airTime > 0.1f) {
                // On the air
                /*
                 if (!isCurrentAnim("Walk")) {
                 setAnim("Walk", 0.2f, 2f, LoopMode.Loop);
                 moveSpeed = 1f;
                 }
                 */
            } else if (!isCurrentAnim("Run")) {
                setAnim("Run", 1.2f, 1f, LoopMode.Loop);
                moveSpeed = defaultMoveSpeed * 2f;
            }
        }
    }

    public boolean isCurrentAnim(String animName) {
        String animName2 = animNameMap.get(animName);
        if (animName2 != null) {
            if (animationChannel.getAnimationName() != null) {
                return animationChannel.getAnimationName().equalsIgnoreCase(animName2);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setAnim(String animName, float transitionTime, float speed, LoopMode loop) {
        if (animNameMap.get(animName) == null) {
            //throw new RuntimeException("Can not find animName :" + animName);
        } else {

            animationChannel.setAnim(animNameMap.get(animName), transitionTime);
            animationChannel.setLoopMode(loop);
            animationChannel.setSpeed(speed);
        }
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("CharLeft")) {
            if (value) {
                left = true;
            } else {
                left = false;
            }
        } else if (binding.equals("CharRight")) {
            if (value) {
                right = true;
            } else {
                right = false;
            }
        } else if (binding.equals("CharUp")) {
            if (value) {
                up = true;
            } else {
                up = false;
            }
        } else if (binding.equals("CharDown")) {
            if (value) {
                down = true;
            } else {
                down = false;
            }
        } else if (binding.equals("CharSpace")) {
            if (!value) {
                System.out.println("Jumped");
                talk("Jumped");
                jumping = true;
                camShaker.shake();
                physicControl.jump();
            } else {
                //prepareJump = true;
            }
        } else if (binding.equals("CharShoot") && !value) {
            if (weaponControl != null) {
                if (weaponControl.isWeaponInHand()) {
                    attack(1);
                }
            }
        } else if (binding.equals("CharWeapon") && !value) {
            //weaponControl.switchIt();
        }
    }

    void setupCamera() {
        cam = app.getCamera();
        camShaker = new CamShaker(cam, playerModel);
    }

    private void setupAnimationController() {

        animNameMap.put("Walk", "Walk");
        animNameMap.put("Jump", "Jump");
        animNameMap.put("Stand", "Idle1");
        animNameMap.put("Run", "Walk");

        animationControl = playerModel.getControl(AnimControl.class);
        animationControl.addListener(this);
        setupBodyAnim();

        //setupPartsAnim();
        /*
         shootingChannel = animationControl.createChannel();
         shootingChannel.addFromRootBone(
         "upper_arm_R");
         * 
         */

    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        /*
         if (channel == shootingChannel) {
         channel.setAnim("stand");
         } else if (channel == upperBody) {
         animationControl.clearChannels();
         setupBodyAnim();

         }
         */
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }

    void setupBodyAnim() {
        animationChannel = animationControl.createChannel();
    }

    void setupPartsAnim() {

        lowerBody = animationControl.createChannel();
        lowerBody.addFromRootBone("thigh_L");
        lowerBody.addFromRootBone("thigh_R");

        upperBody = animationControl.createChannel();
        upperBody.addFromRootBone("spine");

        /*
         // will blend over 15 seconds to stand
         feet.setAnim("Walk", 15);
         feet.setSpeed(0.25f);
         feet.setLoopMode(LoopMode.Cycle);
        
         // left hand will pull
         upperBody.addFromRootBone("spine");
         upperBody.setAnim("Magic1");
         upperBody.setSpeed(0.5f);
         upperBody.setLoopMode(LoopMode.Cycle);
         */
    }

    void attachWeapon() {
        //WorldManager worldManager = stageManager.getWorldManager();
        //ParticleEmitter flame = gameEffectManager.createFlame();
        //skeletonControl.getAttachmentsNode("hand_L").attachChild(flame);
        //flame.emitAllParticles();
        //skeletonControl.getAttachmentsNode("hand_L").attachChild(worldManager.createRedBox());
        //Node swordNode = WeaponControl.createSword();
    }

    private void createWeapon() {
        weaponControl = new WeaponControl(physicControl);
        playerModel.addControl(weaponControl);
    }

    private void createPhysicCharacter() {
        //CapsuleCollisionShape capsule = new CapsuleCollisionShape(0.6f, 0.9f);
        physicControl = new BetterCharacterControl(radiusSize, bodyHeight, mass);

        physicControl.setJumpForce(defaulJumpForce);
        physicControl.setGravity(defaulGravity);
        playerModel.addControl(physicControl);

        /*
         TrailControl trailControl = new TrailControl(character, stageManager);
         playerModel.addControl(trailControl);
         */

    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        playerModel = (Node) spatial;
        setupCamera();
        setupAnimationController();
        createPhysicCharacter();
        attachPhysic();
    }

    public void attachPhysic() {
        System.out.println("Add physic control for Character");
        app.getWorldManager().getPhysicsSpace().add(physicControl);
        logger.warning("Add physic control for Character");
    }
    /* Enviroment awares */

    public boolean isOnWater() {
        float y = spatial.getLocalTranslation().y;
        if (y > 0.4f && y < 1.6f) {
            return true;
        } else {
            return false;
        }
    }
    /* Normal animations */

    void attack(float speed) {
        String add = "";
        if (currentAttackType < 3) {
            currentAttackType++;
            add = Integer.toString(currentAttackType);
        } else {
            currentAttackType = 1;
        }
        setupPartsAnim();
        upperBody.setAnim("Attack_Sword" + add);
        upperBody.setLoopMode(LoopMode.DontLoop);
        upperBody.setSpeed(speed);
    }

    void defend(float speed) {
    }
    /* Advanced movement */

    public void walkBlend() {
    }

    public void duck() {
    }

    public void hop() {
    }

    public void crouch() {
    }

    public void roll() {
    }

    public void wallJump() {
    }

    public void wallRun() {
    }

    public void ledgeGrad() {
    }

    public void pickItem() {
    }

    public void switchHand() {
    }

    public void swingBody() {
    }

    public void backFlip() {
    }

    public void getBalancePoint() {
    }

    public void swim() {
    }

    /* Check collision */
    void checkCollision(Node root, float tpf) {
        //FIXME: Use Regulator
        if (collisionTimePassed > collisionTimeInterval) {
            collisionTimePassed = 0;
            doCheckCollision(root);
        } else {
            collisionTimePassed += tpf;
        }
    }

    void doCheckCollision(Node root) {
        root.depthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                checkEntity(geom);
            }

            @Override
            public void visit(Node node) {
                checkEntity(node);
            }
        });
    }

    public void checkEntityId(Node node) {
        /*
         String parentEntityId;
         if (sp.getParent()==null){
         parentEntityId = null;
         } else {
         parentEntityId = sp.getParent().getUserData("entityId");
         }
         */
        String entityId = node.getUserData("entityId");

        if (entityId != null) {
            //check Bound collision
            pickedEntity(node);
        }
    }

    public void checkEntityId(Geometry geo) {
        String entityId = geo.getUserData("entityId");

        if (entityId != null) {
            //check Bound collision
            boolean isInside = geo.getModelBound().contains(spatial.getWorldTranslation().clone());
            if (isInside) {
                //pickedEntity(sp);
            } else {
            }
            pickedEntity(geo);
        }
    }

    public void checkEntity(Spatial sp) {
        float dis = sp.getWorldTranslation().distance(spatial.getWorldTranslation());
        if (dis < awareItemDistance) {
            if (sp instanceof Geometry) {
                checkEntityId((Geometry) sp);
            } else {
                checkEntityId((Node) sp);
            }

        }
    }

    public void pickedEntity(Spatial sp) {
        if (sp != selectedEntitySpatial) {
            String entityId = sp.getUserData("entityId");
            String entityType = sp.getUserData("entityType");

            talk(sp.getName() + " is :" + entityId);

            // FIXME: Apply real effect
            if (entityType.equalsIgnoreCase("banana")) {
                this.character.heal(10);
//                getStageManager().applyEffect(sp, "Ding");
            }
            sp.removeFromParent();
            selectedEntitySpatial = sp;
        }
    }

    public void talk(String msg) {
        System.out.println("" + msg);
//        inGameUI.alert(msg);
    }

    boolean inRange() {
        return false;
    }

    boolean ifEntity() {
        return true;
    }

    String getEntityInfo(Spatial sp) {
        return sp.getName();
    }
    /* Routines */

    @Override
    protected void controlUpdate(float tpf) {
        moveChar(tpf);
        updateAnimation(tpf);
        checkCollision(app.getWorldManager().getWorldNode(), tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    /* setter & getter */

    public void setCharacter(MBGameCharacter character) {
        this.character = character;
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        //
        return new MBCharacterControl();
    }

    public void setLocation(Vector3f location) {
        physicControl.warp(location);
        oldPos = playerModel.getLocalTranslation().clone();
    }

    public void setMoveSpeed(float speed) {
        this.defaultMoveSpeed = speed;
        moveSpeed = defaultMoveSpeed * 1f;
    }

    public void setJumpForce(Vector3f force) {
        physicControl.setJumpForce(force);
    }

    public Vector3f getLocation() {
        return playerModel.getLocalTranslation();
    }

    public float getDistanceOfFall() {
        return distanceOfFall;
    }

    public Vector3f getDefaulGravity() {
        return defaulGravity;
    }

    public Vector3f getDefaulJumpForce() {
        return defaulJumpForce;
    }

    public float getDefaultMoveSpeed() {
        return defaultMoveSpeed;
    }

    public float getAwareItemDistance() {
        return awareItemDistance;
    }
}
