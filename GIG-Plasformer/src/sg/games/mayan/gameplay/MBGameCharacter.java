package sg.games.mayan.gameplay;

import sg.games.mayan.gameplay.entities.Weapon;
import sg.games.mayan.gameplay.entities.GameCharacter;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class MBGameCharacter extends GameCharacter {

    String name;
    int health = 100;
    // Game charater props
    Weapon weapon;

    public MBGameCharacter(String name) {
        super(name);
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public int getHealth() {
        return health;
    }

    public void hurt(int dam) {
        health -= dam;

        if (health < 0) {
            //moveAction.speed = 0;
        }
    }

    public boolean isDead() {
        return false;
    }

    /*
     void updateSkill() {
     for (GameAction sk : requestedAction) {
     if (sk instanceof SkillAction) {
     if (((SkillAction) sk).getSkillName().equals("weapon")) {
     if (((SkillAction) sk).getSkillProperty().equals("shoot")) {
     //weapon.fire(true);
     } else if (((SkillAction) sk).getSkillProperty().equals("zoom")) {
     //weapon.reload(true);
     } else if (((SkillAction) sk).getSkillProperty().equals("reload")) {
     //weapon.zoom(true);
     }
     }
     }
     }
     }
     * 
     */
    public void heal(int point) {
        if (health + point < 100) {
            this.health += point;
        } else {
            health = 100;
        }
    }
}
