package gig.games.plasformer;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings cfg = new AppSettings(true);
        cfg.setFrameRate(60); // set to less than or equal screen refresh rate
//        cfg.setVSync(true);   // prevents page tearing
        cfg.setFrequency(60); // set to screen refresh rate
        cfg.setResolution(1024, 768);

//        cfg.setSamples(2);    // anti-aliasing
        cfg.setTitle("My jMonkeyEngine Platformer 3D Game"); // branding: window name

//        cfg.setSettingsDialogImage("Interface/MySplashscreen.png");
        app.setShowSettings(false); // or don't display splashscreens
        app.setSettings(cfg);
        app.start();
    }
    private Node characterNode;
    private BulletAppState bulletAppState;
    private BetterCharacterControl physicsCharacter;

    @Override
    public void simpleInitApp() {
        // activate physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);
        characterNode = (Node) assetManager.loadModel("Models/characters/Practice Rigging Man.j3o");
        rootNode.attachChild(characterNode);
        /**
         * A white, spot light source.
         */
        PointLight lamp = new PointLight();
        lamp.setPosition(new Vector3f(0, 10, 10));
        lamp.setColor(ColorRGBA.White);
        rootNode.addLight(lamp);
        //
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Textures/graas2.jpg"));

        Box floorBox = new Box(20, 0.25f, 20);
        Geometry floorGeometry = new Geometry("Floor", floorBox);
        floorGeometry.setMaterial(material);
        floorGeometry.setLocalTranslation(0, -0.25f, 0);
        floorGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(floorGeometry);
        bulletAppState.getPhysicsSpace().add(floorGeometry);
        viewPort.setBackgroundColor(ColorRGBA.Blue);

        physicsCharacter = new BetterCharacterControl(0.3f, 0.5f, 0.3f);
        characterNode.addControl(physicsCharacter);
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        //control cam
        flyCam.setRotationSpeed(0);
        cam.setLocation(new Vector3f(25, 5, 5));
        cam.setRotation(Quaternion.IDENTITY);
        cam.lookAt(characterNode.getLocalTranslation(), Vector3f.UNIT_Y);

    }
    private float eslapedTime = 0;

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        eslapedTime += tpf;
        if (eslapedTime > 2) {
            eslapedTime = 0;
            addTree(new Vector3f(10, 2, 0));
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    void addTree(Vector3f pos) {
        /* A colored lit cube. Needs light source! */
        Box boxMesh = new Box(1f, 1f, 1f);
        Geometry boxGeo = new Geometry("Colored Box", boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        boxMat.setBoolean("UseMaterialColors", true);
        boxMat.setColor("Ambient", ColorRGBA.Green);
        boxMat.setColor("Diffuse", ColorRGBA.Green);
        boxGeo.setMaterial(boxMat);
        rootNode.attachChild(boxGeo);

        boxGeo.setLocalTranslation(pos);
        boxGeo.addControl(new RigidBodyControl(new BoxCollisionShape(new Vector3f(1, 1, 1)), 0f));
        bulletAppState.getPhysicsSpace().add(boxGeo);
        boxGeo.addControl(new BoxController());
    }
}
