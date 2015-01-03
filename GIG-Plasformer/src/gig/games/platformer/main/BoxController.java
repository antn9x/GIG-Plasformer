/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gig.games.platformer.main;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author anwedo
 */
public class BoxController extends AbstractControl {

    @Override
    protected void controlUpdate(float tpf) {
        float addZ = 3 * tpf;
        Vector3f pos = getSpatial().getLocalTranslation().clone();
        getSpatial().setLocalTranslation(pos.add(0, 0, addZ));
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
