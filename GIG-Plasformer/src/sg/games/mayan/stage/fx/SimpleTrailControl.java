package sg.games.mayan.stage.fx;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;

/**
 * Attach this to a node. A trail will start following it. The line which you
 * need to inject should be attached to a Geometry node, which should be
 * attached to the root. Moving that Geometry node around has rather weird
 * results, since the points for the trail are computed in world space.
 *
 * @author cvlad
 */
public class SimpleTrailControl extends AbstractControl {

    float lifeSpan = 5;
    LinkedList<Vector3f> bin = new LinkedList<Vector3f>();
    LinkedList<Double> birthTime = new LinkedList<Double>();
    LineControl line;
    Vector3f lastSpawnPos;
    Vector3f difference = new Vector3f();
    float segmentLength = 0.1f;
    float segmentLengthSqr = this.segmentLength * this.segmentLength;
    float startWidth = 1;
    float endWidth = 1;
    double localTime = 0;

    /* Params */
    private Geometry geom;
    private Material mat;
    private float lastTime;
    float minTime;
    int numFan;
    int maxFan;
    int numPoint;
    float distance;
    Vector3f lastTip;
    Vector3f lastBottom;
    private float rootZ;
    private float angle = 0;
    private float rootX, rootY;
    int numOfStep = 1;
    int maxStep = 20;

    /**
     * Constructor
     *
     * @param line Inject a line which is attached to a Geometry node which is
     * attached to the root node.
     */
    public SimpleTrailControl(final LineControl line) {
        this.line = line;
    }

    @Override
    public void setSpatial(final Spatial spatial) {
        super.setSpatial(spatial);
        this.lastSpawnPos = spatial.getWorldTranslation().clone();
    }

    @Override
    protected void controlUpdate(final float f) {
        simpleUpdateMesh(f);
    }

    @Override
    protected void controlRender(final RenderManager rm, final ViewPort vp) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Control cloneForSpatial(final Spatial sptl) {
        /*
         clone.setLifeSpan(this.lifeSpan);
         clone.setSegmentLength(this.segmentLength);
         sptl.addControl(clone);
         return clone;
         */
        return null;
    }

    public void simpleInitApp() {
        geom = new Geometry("OurMesh", makeMesh(null, null));

        // SETUP Trail
        minTime = 0.05f;
        lastTime = 0;
        numFan = 0;
        maxFan = 18;
        numPoint = 0;

    }

    public void createDefaultMaterial(AssetManager assetManager) {
        assetManager.registerLocator("src/jme3test/model/shape/trail", FileLocator.class); // default
        //this.assetManager.registerLocator("src/jme3test/model/shape/plasma", FileLocator.class); // default
        mat = new Material(assetManager, "Trail.j3md");
        Texture swoosh = assetManager.loadTexture("Swoosh01.png");
        mat.setTexture("Texture", swoosh);
        mat.setColor("Color", new ColorRGBA(1, 0, 0, 1));
        //mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat = new Material(assetManager, "Plasma.j3md");
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.AlphaAdditive);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        //mat.getAdditionalRenderState().setWireframe(true);


        geom.setMaterial(mat);
    }

    public void simpleUpdateMesh(float tpf) {
        /*
         angle += tpf * 4;
         angle %= FastMath.TWO_PI;
         rootX = FastMath.cos(angle) * 2;
         rootY = FastMath.sin(angle) * 2;
         rootZ = FastMath.cos(angle * 2) * 2;
         */
        geom.setLocalTranslation(rootX, 0, rootY);
        //updateMesh();
        if (lastTime > minTime) {

            geom.setMesh(makeMesh(new Vector3f(rootX, rootZ, rootY), new Vector3f(rootX, rootZ + 1, rootY)));
            if (numFan < maxFan) {
                numFan++;
            }
            lastTime = 0;

        } else {
            lastTime += tpf;
        }
    }

    /*
     private void createTrail(Node targetNode, Node rootNode, ColorRGBA color) {
     Vector3f[] points = new Vector3f[10];
     for (int i = 0; i < points.length; i++) {
     float angle = (-1f - i) / (float) points.length * 2f * (float) Math.PI / 8f; // an 8th of a circle
     points[i] = new Vector3f(targetNode.calcCurrentX(angle) - targetNode.calcCurrentX(0), 0, targetNode.calcCurrentY(angle) - targetNode.calcCurrentY(0));
     }
     Curve mesh = new Curve(points, 25);
     mesh.setMode(Mode.Points);
     mesh.setPointSize(2);
     Geometry grid = new Geometry(targetNode.toString() + "-Trail", mesh);
     Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
     mat.setColor("Color", color.mult(0.01f));
     mat.setColor("GlowColor", color.mult(0.8f));
     grid.setMaterial(mat);
     rootNode.attachChild(grid);
     }
    
    
     void updateMesh() {
     ColorRGBA glowColor = new ColorRGBA(0.0f, 0.0f, 0.8f, 1f);
     ColorRGBA color = new ColorRGBA(0.3f, 0.3f, 1.0f, 1f);
     int points = 300;
     float rounds = 10;
     float maxRadius = 5f;
     float maxDepth = 20f;
     CustomMesh mesh = new CustomMesh(points * 2);
     mesh.setMode(Mode.LineStrip);
     ColorRGBA baseColor;
     baseColor = new ColorRGBA(0.0f, 0.0f, 0.8f, 1f);
    
     for (int i = points; i > 0; i--) {
     float percent = (float) i / (float) points;
     float angle = (float) (Math.PI * 2f * rounds * percent);
     float xPos = (float) Math.sin(angle) * maxRadius * percent;
     float yPos = (float) Math.cos(angle) * maxRadius * percent;
     float depth = maxDepth - maxDepth * (float) Math.sqrt(percent);
     mesh.setColor(baseColor.mult(wrap(1.8f - percent * 2f, 0f, 1f)));
     mesh.addVertex(xPos, yPos, depth);
     }
     baseColor = new ColorRGBA(0.3f, 0.3f, 1f, 1f);
     for (int i = 0; i < points; i++) {
     float percent = (float) i / (float) points;
     float angle = (float) (Math.PI * 2f * rounds * percent + Math.PI); // 180 degree offset
     float xPos = (float) Math.sin(angle) * maxRadius * percent;
     float yPos = (float) Math.cos(angle) * maxRadius * percent;
     float depth = maxDepth - maxDepth * (float) Math.sqrt(percent);
     mesh.setColor(baseColor.mult(wrap(1.8f - percent * 2f, 0f, 1f)));
     mesh.addVertex(xPos, yPos, depth);
     }
     mesh.finish();
     Geometry geo = new Geometry("Portal", mesh);
     Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
     mat.setBoolean("VertexColor", true);
     mat.setColor("GlowColor", glowColor);
     geo.setMaterial(mat);
     // geo.move(pos);
     // Matrix3f rotationMatrix = new Matrix3f();
     // rotationMatrix.fromStartEndVectors(Vector3f.UNIT_Z, pos.normalize());
     // geo.setLocalRotation(rotationMatrix);
     rootNode.attachChild(geo);
     }
     */
    // Vertex positions in space
    ArrayList<Vector3f> verticesSave = new ArrayList<Vector3f>(20);

    Mesh makeMesh(Vector3f tip, Vector3f bottom) {

        Mesh m = new Mesh();

        if (tip == null) {
            return m;
        }
        verticesSave.add(0, tip.clone());
        verticesSave.add(1, bottom.clone());

        int lastIndex = (maxFan + 1) * 2;
        if (verticesSave.size() > lastIndex) {
            verticesSave.remove(lastIndex);
            verticesSave.remove(lastIndex - 1);
        }

        if (numFan == 0) {
            return m;
        }
        int numVer = (numFan + 1) * 2;
        // Vertex positions in space
        Vector3f[] vertices = new Vector3f[numVer];
        // Texture coordinates
        Vector2f[] texCoord = new Vector2f[numVer];
        // Indexes. We define the order in which mesh should be constructed
        int[] indexes = new int[numFan * 6];
        vertices[0] = new Vector3f(tip);
        vertices[1] = new Vector3f(bottom);
        texCoord[0] = new Vector2f(1, 0);
        texCoord[1] = new Vector2f(1, 1);

        for (int i = 0; i < numFan; i++) {
            int j = (i + 1) * 2;
            vertices[j] = verticesSave.get(j);
            vertices[j + 1] = verticesSave.get(j + 1);

            float grayScale = 1f / numFan * i;
            texCoord[j] = new Vector2f(1 - grayScale, 0);
            texCoord[j + 1] = new Vector2f(1 - grayScale, 1);

            int k = i * 6;
            j = j - 2;
            indexes[k] = j + 2;
            indexes[k + 1] = j + 0;
            indexes[k + 2] = j + 1;
            indexes[k + 3] = j + 1;
            indexes[k + 4] = j + 3;
            indexes[k + 5] = j + 2;
        }
        // Setting buffers
        m.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        m.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        m.setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createIntBuffer(indexes));
        m.updateBound();

        return m;
    }

    private float wrap(float in, float min, float max) {
        if (in < min) {
            in = min;
        }
        if (in > max) {
            in = max;
        }
        return in;
    }

    /**
     * Sets how fast shall the trail disappear
     *
     * @param lifeSpan Lifespan of the points which make up the trail
     */
    public void setLifeSpan(final float lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    /**
     * Gets how fast shall the trail disappear
     *
     * @return Lifespan of the points which make up the trail
     */
    public float getLifeSpan() {
        return this.lifeSpan;
    }

    /**
     * Sets after how much distance shall a new point be generated. The last
     * point which was added to the trail will be moved around until a new point
     * is generated. So lowering this value generally gives better results.
     *
     * @param segmentLength
     */
    public void setSegmentLength(final float segmentLength) {
        this.segmentLength = segmentLength;
        this.segmentLengthSqr = segmentLength * segmentLength;
    }

    /**
     * Gets after how much distance shall a new point be generated.
     *
     * @return after how much distance shall a new point be generated.
     */
    public float getSegmentLength() {
        return this.segmentLength;
    }

    /**
     * Gets after how much distance shall a new point be generated. Distance is
     * squared.
     *
     * @return after how much distance shall a new point be generated. Distance
     * is squared.
     */
    public float getSegmentLengthSqr() {
        return this.segmentLengthSqr;
    }

    /**
     * Sets the width at the starting point of the trail. So the oldest point in
     * this trail has this width.
     *
     * @param startWidth
     */
    public void setStartWidth(final float startWidth) {
        this.startWidth = startWidth / 2;
    }

    public float getStartWidth() {
        return this.startWidth * 2;
    }

    /**
     * Sets the width at the ending point of the trail. So the newest point in
     * this trail has this width. You know, the point closest to the spatial
     * onto which you attached this control.
     *
     * @param endWidth Width at the ending point of the trail.
     */
    public void setEndWidth(final float endWidth) {
        this.endWidth = endWidth / 2;
    }

    /**
     * Gets the width at the ending point of the trail. So the newest point in
     * this trail has this width. You know, the point closest to the spatial
     * onto which you attached this control.
     *
     * @return Width at the ending point of the trail.
     */
    public float getEndWidth() {
        return this.endWidth * 2;
    }
}