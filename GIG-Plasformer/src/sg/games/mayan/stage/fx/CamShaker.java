/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.games.mayan.stage.fx;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class CamShaker extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    private Vector3f shakeOffset = new Vector3f(0, 0, 0);
    private float damp = 1f;
    private float limit_lo = .1f;
    private float limit_hi = 4f;
    private Camera cam;
    private Spatial target;
    static public final float SMALL_AMOUNT = .5f;
    static public final float DEFAULT_AMOUNT = 1f;
    static public final float LARGE_AMOUNT = 2f;
    private float shakeAmp = 0f;

    public void update(final float tpf) {
        if (!enabled) {
            return;
        }

        if (shakeAmp == 0) {
            return;
        }

        Vector3f newShakeOffset = new Vector3f(FastMath.rand.nextFloat() * shakeAmp, FastMath.rand.nextFloat() * shakeAmp, FastMath.rand.nextFloat() * shakeAmp);
        cam.setLocation(cam.getLocation().add(newShakeOffset).subtract(shakeOffset));

        shakeAmp *= damp * tpf;

        if (shakeAmp <= limit_lo) {
            shakeAmp = 0;
        }

        //System.out.println(diff);
        //currentPosition.addLocal(velocity);
        //cam.setLocation(cam.getLocation().add(diff));
    }

    public void shake() {
        shake(DEFAULT_AMOUNT);
    }

    public void shake(float amount) {
        shakeAmp += amount;
        if (shakeAmp > limit_hi) {
            shakeAmp = limit_hi;
        }
    }

    public CamShaker(Camera cam, Spatial target) {
        this.cam = cam;
        this.target = target;
        target.addControl(this);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    public Control cloneForSpatial(Spatial spatial) {
        CamShaker control = new CamShaker(this.cam, this.target);
        //TODO: copy parameters to new Control
        control.setSpatial(spatial);
        return control;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat(“name”, defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, “name”, defaultValue);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //throw new UnsupportedOperationException(“Not supported yet.”);
    }
}
