package sg.games.mayan.stage.cam;

import com.jme3.app.state.AbstractAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author hungcuong
 */
public class PlatformGameChaseCamera extends AbstractAppState{

    FlyByCamera flyCam;
    ChaseCamera chaseCam;
    Camera cam;
    Node playerModel;
    InputManager inputManager;

    public PlatformGameChaseCamera(Camera cam, FlyByCamera flyCam, Node playerModel, InputManager inputManager) {
        this.cam = cam;
        this.flyCam = flyCam;
        this.playerModel = playerModel;
        this.inputManager = inputManager;
    }

    public void setupChaseCamera() {
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, playerModel, inputManager);
        chaseCam.setDefaultDistance(100f);
        chaseCam.setZoomSensitivity(0.2f);
        chaseCam.setMinDistance(80f);
        chaseCam.setMaxDistance(350f);

        chaseCam.setSmoothMotion(true);

    }

    public ChaseCamera getChaseCam() {
        return chaseCam;
    }

    public FlyByCamera getFlyCam() {
        return flyCam;
    }

    public Camera getCam() {
        return cam;
    }

    public InputManager getInputManager() {
        return inputManager;
    }
}
