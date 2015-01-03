package gig.games.platformer.main;

import gig.games.platformer.state.InGameState;
import gig.games.platformer.managers.EffectManager;
import gig.games.platformer.managers.PGWorldManager;
import gig.games.platformer.managers.PGGamePlayManager;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.system.AppSettings;

/**
 * test
 *
 * @author normenhansen
 */
public class PGMain extends SimpleApplication {

    private static PGMain instance;
    private PGWorldManager worldManager;
    private PGGamePlayManager gamePlayManager;
    private AppState effectManager;

    private PGMain() {
    }

    public static synchronized final PGMain getInstance() {
        if (instance == null) {
            instance = new PGMain();
        }
        return instance;
    }

    public static void main(String[] args) {
        PGMain app = new PGMain();
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60); // set to less than or equal screen refresh rate
        settings.setFrequency(60); // set to screen refresh rate
        settings.setResolution(1024, 768);
        settings.setTitle("My jMonkeyEngine Platformer 3D Game"); // branding: window name
        app.setShowSettings(false); // or don't display splashscreens
        app.setSettings(settings);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.worldManager = new PGWorldManager();
        this.gamePlayManager = new PGGamePlayManager();
        this.effectManager = new EffectManager();
        stateManager.attach(worldManager);
        stateManager.attach(gamePlayManager);
        stateManager.attach(effectManager);
        stateManager.attach(new InGameState());
    }

    public PGWorldManager getWorldManager() {
        return worldManager;
    }

    public PGGamePlayManager getGamePlayManager() {
        return gamePlayManager;
    }

    public AppState getEffectManager() {
        return effectManager;
    }
}
