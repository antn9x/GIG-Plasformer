package sg.games.mayan.managers;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import java.util.ArrayList;
import sg.games.mayan.gameplay.controls.MBCharacterControl;
import sg.games.mayan.gameplay.GameLevel;
import sg.games.mayan.gameplay.Player;
import sg.games.mayan.gameplay.entities.GameCharacter;
import sg.games.mayan.world.MBWorld3D;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class MBGamePlayManager extends AbstractManager {
    GameLevel currentLevel;
    ArrayList<GameLevel> levels = new ArrayList<GameLevel>(4);
    //character -----------------------------------------------

    Node playerModel;
    Node playerModelFile;
    Node magicFX;
    protected boolean firstPersonView = true;
    MBCharacterControl characterControl;

    // gameplay
    int retryTimes = 0;
    int score = 0;
    int bonus = 0;
    float playTime = 0;
    private Player mainPlayer;

    public void goInGame() {
//        doReadyToPlay();
    }

    public void goOutGame() {
    }

    public void pauseGame() {
    }

    public void resumeGame() {
    }

    public void applyEffect(Spatial sp, String effectName) {
        if (effectName.equals("Ding")) {
        }
    }

    private void setupKeys() {
        /*
         getInputManager().addMapping("spawnEnemy", new KeyTrigger(KeyInput.KEY_I));
         getInputManager().addListener(stageActionListener, "spawnEnemy");

         getInputManager().addMapping("spawnFriend", new KeyTrigger(KeyInput.KEY_Y));
         getInputManager().addListener(stageActionListener, "spawnFriend");
         */
        inputManager.addMapping("debugPhysic", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(stageActionListener, "debugPhysic");
        inputManager.addMapping("wireframe", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(characterControl, "wireframe");
        inputManager.addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CharUp", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CharDown", new KeyTrigger(KeyInput.KEY_S));

        inputManager.addMapping("CharSpace", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("CharShoot", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("CharWeapon", new KeyTrigger(KeyInput.KEY_V));


        inputManager.addListener(characterControl, "CharLeft");
        inputManager.addListener(characterControl, "CharRight");
        //inputManager.addListener(characterControl, "CharUp");
        //inputManager.addListener(characterControl, "CharDown");

        inputManager.addListener(characterControl, "CharSpace");
        inputManager.addListener(characterControl, "CharShoot");
        inputManager.addListener(characterControl, "CharWeapon");

        // FOR MOUSE
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(commonActionListener, "shoot");
    }
    
    private ActionListener stageActionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("debugPhysic") && !keyPressed) {
//                worldManager.tooglePhysicDebug();
            }

        }
    };
    
    private ActionListener commonActionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("toggleView") && !keyPressed) {
                firstPersonView = !firstPersonView;
            }

            if (name.equals("spawnEnemy") && !keyPressed) {
                //getEnemies(id).add(new Ghoul(currentStage, worldManager));
            }

            if (name.equals("spawnFriend") && !keyPressed) {
                //getFriends(id).add(new Ghoul(currentStage, worldManager));
            }

            if (name.equals("shoot") && !keyPressed) {
//                Camera cam = stageManager.getCurrentActiveCamera();
//                Node shootables = currentLevel.getLevelNode();
            }
        }
    };

    protected void setupLevels() {
        levels.add(new MBWorld3D("Level1", "Scenes/Scene1.j3o"));
    }
    //    public void doReadyToPlay() {
//        worldManager.getPhysicsSpace().setGravity(new Vector3f(0f, -9.81f, 0f));
//    }
    public void initLevel(GameLevel currentLevel1){
        
    }
    
    public void startLevel(GameLevel level) {
        reinitLevelState();
        currentLevel.getLevelNode().attachChild(getCharacterModel());
    }

    public void reinitLevelState() {
        Vector3f startPos = currentLevel.getStartPos();
        characterControl.setLocation(startPos);

        score = 0;
        bonus = 0;
        playTime = 0;
        retryTimes++;
    }

    public void restartLevel() {
    }

    public void nextLevel() {
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);


        // FIXME: Testing purpose. Update the platform camera
        Vector3f location = characterControl.getLocation();
        app.getCamera().setLocation(location.add(0, 5, 30));
        app.getCamera().lookAt(location, Vector3f.UNIT_Y);
        /*
         for (DefaultVehicle aVehicle:demonVehicles){
            
         }
         */

        //Check the game conditions and status
        if (location.y < 0) {
            restartLevel();
        }

    }

    public void configEntities() {
        // Config Entity Manager
    }


    public void configGamePlay() {
        setupLevels();
        this.currentLevel = levels.get(0);
        initLevel(currentLevel);

        //Setup character
        playerModel = playerModelFile;

        //FIXME: Testing purpose. Should be removed!

        playerModel.setShadowMode(ShadowMode.Cast);
        playerModel.setLocalScale(1.5f);

        mainPlayer = new Player("Ninja");
        mainPlayer.init();

        GameCharacter playerMainCharacter = new GameCharacter("Mayan");
        mainPlayer.setPlayerMainCharacter(playerMainCharacter);

        characterControl = new MBCharacterControl();
        playerMainCharacter.initCharacter(playerModel, characterControl);
        //createCharacterEffect(playerMainCharacter.getModelNode());
        // Set initial speed and force
        characterControl.setMoveSpeed(10f);
        characterControl.setJumpForce(new Vector3f(0, 800, 0));
        /*
         mainPlayerVehicle = new DefaultVehicle(steerManager);
         SimpleVehicleControl sControl = new SimpleVehicleControl(steerManager);
         sControl.setVehicle(mainPlayerVehicle);
         sControl.setSpatialToVehicle(true);
         mainPlayer.getModelNode().addControl(sControl);
         */

        setupCamera();
        setupKeys();
    }

    void setupCamera() {
        app.getFlyByCamera().setEnabled(false);
        // Setup Camera
        //PlatformGameChaseCamera rpgCam = new PlatformGameChaseCamera(stageManager.getCurrentActiveCamera(), flyCam, playerModel, inputManager);
        //rpgCam.setupChaseCamera();

    }

//    public void setupCamera() {
//        getInputManager().setCursorVisible(false);
//        getCamera().setLocation(new Vector3f(0, 15, 40));
//        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
//        getApp().getFlyByCamera().setMoveSpeed(50f);
//    }
    void createCharacterEffect(Node modelNode) {
        // add some effect 
        //magicFX.setLocalScale(0.01f, 0.01f, 0.01f);
        //magicFX.setLocalTranslation(0f, 0.4f, 0f);
        //modelNode.attachChild(magicFX);

        // A light
        PointLight lamp = new PointLight();
        Vector3f lightPos = new Vector3f().add(1, 2, 2);
        lamp.setPosition(lightPos);
        lamp.setColor(ColorRGBA.White);
        modelNode.addLight(lamp);

        // Surround 1 model
        Cylinder t = new Cylinder(5, 20, 2, 1, 2, false, false);

        Geometry powerUpGeom = new Geometry("Cylinder", t);
        powerUpGeom.setLocalTranslation(0, 2, 0);
        /*
         Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         //TextureKey key = new TextureKey("Interface/Logo/Monkey.jpg", true);
         TextureKey key = new TextureKey("Textures/FX/Trail/Swoosh02.png", true);

         key.setGenerateMips(true);
         Texture tex = assetManager.loadTexture(key);
         tex.setMinFilter(Texture.MinFilter.Trilinear);
        
         mat.setTexture("ColorMap", tex);
         //mat.setColor("Color", new ColorRGBA(1, 0, 0, 1));
         //mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         //mat = new Material(assetManager, "Plasma.j3md");
         mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
         mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
        
         */

        Material mat = assetManager.loadMaterial("Materials/FX/SurroundFx1.j3m");

        powerUpGeom.setMaterial(mat);
        Quaternion upRot = new Quaternion();
        upRot.lookAt(Vector3f.UNIT_Y, Vector3f.UNIT_X);
        powerUpGeom.setLocalRotation(upRot);
        powerUpGeom.setQueueBucket(RenderQueue.Bucket.Transparent);
        modelNode.attachChild(powerUpGeom);
        // Surround 2 model

        // Trail
        /*
         Geometry trailGeometry = new Geometry();
         LineControl line = new LineControl(new LineControl.Algo1CamDirBB(), true);
         trailGeometry.addControl(line);
         TrailControl trail = new TrailControl(line);
         modelNode.addControl(trail);
         Material trailMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         trailMat.setColor("Color", ColorRGBA.Blue);
         trailMat.setTexture("ColorMap", assetManager.loadTexture("Textures/FX/Trail/Trail1.png"));
         trailMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
         trailGeometry.setMaterial(trailMat);
         modelNode.attachChild(trailGeometry);
         */
    }

    public void loadGamePlay() {
        // load all GameCharacter models
        // WorldManager will help but
        // for simple case , this 's enough

        //playerModelFile = (Node) assetManager.loadModel("Models/Characters/Girl/Girl_d2.j3o");
        playerModelFile = (Node) assetManager.loadModel("Models/Characters/Ninja/Ninja.mesh.j3o");
        /*
         magicFX = (Node) assetManager.loadModel("Models/Magic/MagicFx.j3o");
         */

        //playerModelFile = (Node) assetManager.loadModel("");

    }

    public void createRandomDemon() {
        Spatial orgDemon = assetManager.loadModel("Models/Character/NPC/Demon1/demon1.j3o");
        orgDemon.setShadowMode(ShadowMode.Cast);
        int num = 7;


        for (int i = 0; i < num; i++) {
            float x = FastMath.nextRandomFloat() * 50;
            float z = FastMath.nextRandomFloat() * 50;

            Spatial cloneDemon = orgDemon.clone();
            cloneDemon.setLocalScale(0.15f);
            cloneDemon.setLocalTranslation(x, 0f, z);
            currentLevel.getLevelNode().attachChild(cloneDemon);
        }

    }

    private Object getCurrentPlayer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Spatial getCharacterModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
