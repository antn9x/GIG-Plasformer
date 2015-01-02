package sg.games.mayan.state;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class InGameState extends BaseGameState {

    private boolean gamePause;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        setEnabled(true);

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            goInGame();
        } else {
            goOutGame();
        }
    }

    void goInGame() {
    }

    void pauseGame() {

    }

    void goOutGame() {
    }

    void resumeGame() {
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
    }



}
