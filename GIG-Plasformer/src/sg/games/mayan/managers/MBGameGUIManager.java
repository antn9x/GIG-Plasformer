package sg.games.mayan.managers;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import sg.games.mayan.main.MBMain;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class MBGameGUIManager extends AbstractManager {

    public MBGameGUIManager(MBMain app) {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        setupCommonScreens();
    }    

    public void setupCommonScreens() {

    }

    public void goInGame() {


    }

    public void pauseGame() {
    }

    public void goOutGame() {
    }

    public void resumeGame() {
    }
}
