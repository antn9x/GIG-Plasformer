package sg.games.mayan.managers;

import sg.games.mayan.stage.fx.EffectFactory;

/**
 *
 * @author cuong.nguyen
 */
public class EffectManager extends AbstractManager{

    public EffectFactory getDefaultFactory() {
        return EffectFactory.getInstance();
    }
    
}
