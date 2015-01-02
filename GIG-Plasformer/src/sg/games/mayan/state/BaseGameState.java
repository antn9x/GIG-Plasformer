package sg.games.mayan.state;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import java.util.logging.Logger;
import sg.games.mayan.main.MBMain;

/**
 *
 * @author cuong.nguyen
 */
public class BaseGameState extends AbstractAppState{
    protected static final Logger logger = Logger.getLogger(InGameState.class.getName());
    protected MBMain app;
    protected Node rootNode;
    protected AssetManager assetManager;
    protected AppStateManager stateManager;    
    protected Node guiNode;
    protected InputManager inputManager;
    protected AudioRenderer audioRenderer;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (MBMain) app;
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.guiNode = this.app.getGuiNode();
        this.inputManager = app.getInputManager();
        this.audioRenderer = app.getAudioRenderer();
    }
}
