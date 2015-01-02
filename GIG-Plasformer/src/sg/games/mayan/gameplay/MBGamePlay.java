package sg.games.mayan.gameplay;

import com.jme3.app.state.AbstractAppState;

/**
 *
 * @author hungcuong
 */
public class MBGamePlay extends AbstractAppState{
//
//    public ArrayList<AtomActor> goodGuys = new ArrayList<AtomActor>();
//    public ArrayList<AtomActor> badGuys = new ArrayList<AtomActor>();
//
//    public ArrayList<AtomActor> getGoodGuys() {
//        return goodGuys;
//    }
//
//    public ArrayList<AtomActor> getBadGuys() {
//        return badGuys;
//    }
//
//    public boolean find(ArrayList<AtomActor> list, int num) {
//        for (int i = 0; i < list.size(); i++) {
//            if (num == list.get(i).getId()) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public AtomActor search(int num) {
//        for (int i = 0; i < badGuys.size(); i++) {
//            if (num == badGuys.get(i).getId()) {
//                return badGuys.get(i);
//            }
//        }
//
//        for (int i = 0; i < goodGuys.size(); i++) {
//            if (num == goodGuys.get(i).getId()) {
//                return goodGuys.get(i);
//            }
//        }
//
//        return null;
//    }
//
//    public AtomActor getAtomActor(String s) {
//        for (int i = 0; i < badGuys.size(); i++) {
//            if (s.equals(Integer.toString(badGuys.get(i).getId()))) {
//                return badGuys.get(i);
//            }
//        }
//
//        for (int i = 0; i < goodGuys.size(); i++) {
//            if (s.equals(Integer.toString(goodGuys.get(i).getId()))) {
//                return goodGuys.get(i);
//            }
//        }
//
//        return null;
//    }
//
//    public ArrayList<AtomActor> getEnemies(int num) {
//        if (find(badGuys, num)) {
//            return goodGuys;
//        }
//
//        return badGuys;
//    }
//
//    public ArrayList<AtomActor> getFriends(int num) {
//        if (find(badGuys, num)) {
//            return badGuys;
//        }
//
//        return goodGuys;
//    }
}
