/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gig.games.platformer.managers;

import gig.games.platformer.stage.fx.EffectFactory;

/**
 *
 * @author anwedo
 */
public class EffectManager extends AbstractManager {

    public EffectFactory getDefaultFactory() {
        return EffectFactory.getInstance();
    }
}
