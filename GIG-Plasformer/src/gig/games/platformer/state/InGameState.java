/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gig.games.platformer.state;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import gig.games.platformer.main.PGMain;
import gig.games.platformer.managers.PGWorldManager;

/**
 *
 * @author anwedo
 */
public class InGameState extends BaseGameState {

    private boolean gamePause;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        setEnabled(true);
        app.getViewPort().setBackgroundColor(ColorRGBA.Blue);        
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
}
