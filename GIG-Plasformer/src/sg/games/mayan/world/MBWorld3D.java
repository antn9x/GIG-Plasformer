package sg.games.mayan.world;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import sg.games.mayan.gameplay.GameLevel;
import sg.games.mayan.main.MBMain;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class MBWorld3D extends GameLevel {
    MBMain app = MBMain.getInstance();
    AssetManager assetManager;
    private ArrayList<House> houses;

    public MBWorld3D(String name, String path) {
    }

    /*
     public MBWorld3D(MBWorldManager worldManager) {
     }
     */
    //public Collection<Geometry> geoCollection;
    public void generateLevel() {
        assetManager = app.getAssetManager();
        houses = new ArrayList<House>();
        // Random house

        int totalLength = 0;
        int wholeLength = 1000;
        //int normalHeight = 3;
        int normalBgDepth = 40;


        int maxHeight = 100;
        int maxHeightGap = 10;
        int normalWidthGap = 6;
        // Make house along the horizontal line
        while (totalLength < wholeLength) {
            int height, slength, width;
            height = FastMath.nextRandomInt(10, 40);
            slength = FastMath.nextRandomInt(20, 40);
            width = slength - normalWidthGap * FastMath.nextRandomInt(0, 1); //FastMath.nextRandomInt(2, slength - 1); 

            House newHouse = new House(width, height);
            //FIXME: Geometry processing if need
            addHouse(newHouse, totalLength, houses.size());
            houses.add(newHouse);
            //TODO: Add some items?
            addFood(newHouse, totalLength);
            // Add enemies

            //TODO: Add placement
            // Ladder
            // Wind mill
            // Trap

            //next
            totalLength += slength;
        }

        // Background city landscape
        Box box = new Box(Vector3f.ZERO, wholeLength / 2, 100f, 1f);
        Geometry bgGeo = new Geometry("A Textured Box", box);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture("Textures/City/bg/city-background-01.jpg");
        bgMat.setTexture("ColorMap", tex);
        bgGeo.setMaterial(bgMat);
        bgGeo.setLocalTranslation(wholeLength / 2 - wholeLength / 12f, 60, -40);
        levelNode.attachChild(bgGeo);

        // LIGHTS
        /**
         * A white ambient light source.
         */
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        levelNode.addLight(ambient);

        /**
         * A white, directional light source
         */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        levelNode.addLight(sun);

        //   
        //Set the startpos in the first house
        House firstHouse = houses.get(0);
        startPos = new Vector3f(firstHouse.width / 2, firstHouse.height + 3, 0);

    }

    void addHouse(House house, int totalLength, int index) {
        int normalHouseThich = 10;

        float hw = (float) house.width / 2f;
        float hh = (float) house.height / 2f;

        // A Single house
        Box houseMesh = new Box(new Vector3f(0, 0, 0), hw, hh, normalHouseThich);
        Geometry houseGeo = new Geometry("House" + index, houseMesh);
        Material unshadeMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture houseTex = assetManager.loadTexture("Textures/City/Building-Texture-0" + (index % 5 + 1) + ".jpg");
        unshadeMat.setTexture("ColorMap", houseTex);
        houseGeo.setMaterial(unshadeMat);
        houseGeo.setLocalTranslation(hw + totalLength, hh, 0);
        levelNode.attachChild(houseGeo);

        // Physics
        houseGeo.addControl(new RigidBodyControl(new BoxCollisionShape(new Vector3f(hw, hh, normalHouseThich)), 0));
    }

    /** 
     * Add random food
     */ 
    void addFood(House house, int totalLength) {
        float hw = (float) house.width / 2f;
        float hh = (float) house.height / 2f;
        /*
        
        //FIX<E: Create the geometry. Should be Replaced with real models
        Box foodMesh = new Box(new Vector3f(0, 0, 0), 0.4f, 0.4f, 0.4f);
        Geometry foodGeo = new Geometry("Food", foodMesh);
        Material foodMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        foodMat.setColor("Color", ColorRGBA.Blue);
        foodGeo.setMaterial(foodMat);
        
        foodGeo.setLocalTranslation(hw + totalLength + 1, house.height + 0.5f, 0);
        */
        Spatial banana = assetManager.loadModel("Models/Props/Banana Bomb/Banana Bomb.j3o");
        banana.setUserData("entityId", "Food1");
        banana.setUserData("entityType", "Banana");
        banana.scale(0.5f);
        banana.setLocalTranslation(hw + totalLength + 1, house.height + 0.5f, 0);
        levelNode.attachChild(banana);
    }

    void addProps(House h, String type) {
    }

    @Override
    public void loadLevel() {
        super.loadLevel();
        generateLevel();
    }
}
