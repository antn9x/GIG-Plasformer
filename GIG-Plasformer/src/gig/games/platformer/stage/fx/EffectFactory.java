/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gig.games.platformer.stage.fx;

import com.jme3.effect.ParticleEmitter;

/**
 *
 * @author anwedo
 */
public class EffectFactory {

    /**
     * Singleton reference of EffectFactory.
     */
    private static EffectFactory instance;

    /**
     * Constructs singleton instance of EffectFactory.
     */
    private EffectFactory() {
    }

    /**
     * Provides reference to singleton object of EffectFactory.
     *
     * @return Singleton instance of EffectFactory.
     */
    public static synchronized final EffectFactory getInstance() {
        if (instance == null) {
            instance = new EffectFactory();
        }
        return instance;
    }

    public ParticleEmitter createFlame() {
        return null;
    }
}
