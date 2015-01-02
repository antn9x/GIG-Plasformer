package sg.games.mayan.gameplay.controls;

import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import sg.games.mayan.main.MBMain;
import sg.games.mayan.managers.MBWorldManager;

/**
 *
 * @author hungcuong
 */
public class WeaponControl extends AbstractControl implements PhysicsCollisionListener {
    private MBMain app = MBMain.getInstance();
    private MBWorldManager worldManager;
    private BetterCharacterControl character;
    private Sphere bullet;
    private SphereCollisionShape bulletCollisionShape;
    private final ParticleEmitter effect;
    private ParticleEmitter flame;
    

    public WeaponControl(BetterCharacterControl character) {
        this.character = character;
        
        this.worldManager = app.getWorldManager();
        this.effect = app.getEffectManager().getDefaultFactory().createFlame();
    }
    private Material matBullet;

    private void bulletControl() {

        Camera cam = app.getCamera();
        Node rootNode = worldManager.getWorldNode();

        Geometry bulletg = new Geometry("bullet", bullet);
        bulletg.setMaterial(matBullet);
        bulletg.setShadowMode(ShadowMode.CastAndReceive);
        bulletg.setLocalTranslation(getSpatial().getLocalTranslation().add(cam.getDirection().mult(5)));
        /*
         RigidBodyControl bulletControl = new BombControl(bulletCollisionShape, 1);
         bulletControl.setCcdMotionThreshold(0.1f);
         bulletControl.setLinearVelocity(cam.getDirection().mult(80));
         bulletg.addControl(bulletControl);
         rootNode.attachChild(bulletg);

         worldManager.getPhysicsSpace().add(bulletControl);
         * */
    }

    public void collision(PhysicsCollisionEvent event) {
        /*
         if (event.getObjectA() instanceof BombControl) {
         final Spatial node = event.getNodeA();
         effect.killAllParticles();
         effect.setLocalTranslation(node.getLocalTranslation());
         effect.emitAllParticles();
         } else if (event.getObjectB() instanceof BombControl) {
         final Spatial node = event.getNodeB();
         effect.killAllParticles();
         effect.setLocalTranslation(node.getLocalTranslation());
         effect.emitAllParticles();
         }
         */
    }

//    Node createSword() {
//        AssetManager assetManager = app.getAssetManager();
//        Node swordNode = (Node) assetManager.loadModel("Models/Weapon/sword/sword.j3o");
//
//        return swordNode;
//    }

    private void prepareBullet() {

        bullet = new Sphere(32, 32, 0.4f, true, false);
        bullet.setTextureMode(TextureMode.Projected);
        bulletCollisionShape = new SphereCollisionShape(0.4f);
        matBullet = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matBullet.setColor("Color", ColorRGBA.Green);
        matBullet.setColor("m_GlowColor", ColorRGBA.Green);
        worldManager.getPhysicsSpace().addCollisionListener(this);
    }

    public WeaponControl cloneForSpatial(Spatial spatial) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return this;
    }
    Node swordNode;
    SkeletonControl skeletonControl;
    boolean weaponInHand;

    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        skeletonControl = spatial.getControl(SkeletonControl.class);
        //skeletonControl.getAttachmentsNode("hand_L").attachChild(worldManager.createRedBox());
//        swordNode = createSword();
        toBack();

    }

    void createFlame() {
        flame = app.getEffectManager().getDefaultFactory().createFlame();
        skeletonControl.getAttachmentsNode("hand_L").attachChild(flame);
        flame.emitAllParticles();
    }

    public void toHand() {
        skeletonControl.getAttachmentsNode("hand_L").attachChild(swordNode);
        swordNode.setLocalRotation(new Quaternion().fromAngles(0, 0 * FastMath.DEG_TO_RAD, 180 * FastMath.DEG_TO_RAD));
        swordNode.setLocalTranslation(0f, 0.4f, -2f);
        swordNode.setLocalScale(0.8f);
        weaponInHand = true;
    }

    public void toBack() {
        skeletonControl.getAttachmentsNode("spine").attachChild(swordNode);
        swordNode.setLocalRotation(new Quaternion().fromAngles(15 * FastMath.DEG_TO_RAD, 100 * FastMath.DEG_TO_RAD, 90 * FastMath.DEG_TO_RAD));
        swordNode.setLocalTranslation(-3f, -4f, -12f);
        swordNode.setLocalScale(0.8f);
        weaponInHand = false;
    }

    public boolean isWeaponInHand() {
        return weaponInHand;
    }

    public void switchIt() {
        if (weaponInHand) {
            toBack();
        } else {
            toHand();
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
