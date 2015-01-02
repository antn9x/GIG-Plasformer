package sg.games.mayan.main;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import sg.games.mayan.managers.EffectManager;
import sg.games.mayan.managers.MBGamePlayManager;
import sg.games.mayan.managers.MBWorldManager;
import sg.games.mayan.state.InGameState;

/**
 * Main class.
 *
 * @author atomixnmc
 */
public class MBMain extends SimpleApplication {

    /**
     * Singleton reference of MBMain.
     */
    private static MBMain instance;
    private MBWorldManager worldManager;
    private MBGamePlayManager gamePlayManager;
    private EffectManager effectManager;

    /**
     * Constructs singleton instance of MBMain.
     */
    private MBMain() {
    }

    /**
     * Provides reference to singleton object of MBMain.
     *
     * @return Singleton instance of MBMain.
     */
    public static synchronized final MBMain getInstance() {
        if (instance == null) {
            instance = new MBMain();
        }
        return instance;
    }

    public static void main(String[] args) {
        // Set the init setting of an 3D application
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Mayan boy");
        settings.setWidth(1024);
        settings.setHeight(768);
        // start it up
        MBMain app = MBMain.getInstance();
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.start();

    }

    @Override
    public void simpleInitApp() {

        this.worldManager = new MBWorldManager();
        this.gamePlayManager = new MBGamePlayManager();
        this.effectManager = new EffectManager();
        stateManager.attach(worldManager);
        stateManager.attach(gamePlayManager);
        stateManager.attach(effectManager);
        stateManager.attach(new InGameState());
    }

    public MBWorldManager getWorldManager() {
        return worldManager;
    }

    public MBGamePlayManager getGamePlayManager() {
        return gamePlayManager;
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }
}
