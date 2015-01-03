/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gig.games.platformer.managers;

import gig.games.platformer.gameplay.Player;
import gig.games.platformer.controls.PGCharacterControl;
import gig.games.platformer.gameplay.GameLevel;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author anwedo
 */
public class PGGamePlayManager extends AbstractManager {

    GameLevel currentLevel;
    ArrayList<GameLevel> levels = new ArrayList<GameLevel>(4);
    //character -----------------------------------------------
    Node playerModel;
    Node playerModelFile;
    Node magicFX;
    protected boolean firstPersonView = true;
    PGCharacterControl characterControl;
    // gameplay
    int retryTimes = 0;
    int score = 0;
    int bonus = 0;
    float playTime = 0;
    private Player mainPlayer;

    public PGGamePlayManager() {
    }
}
