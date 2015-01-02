package sg.games.mayan.state;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import sg.games.mayan.managers.MBGameGUIManager;
import sg.games.mayan.main.MBMain;


/**
 *
 * @author cuong.nguyenmanh2
 */
public class MainMenuState extends BaseGameState {


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        setEnabled(true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
        } else {
        }
    }
}
