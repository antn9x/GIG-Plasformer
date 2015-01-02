package sg.games.mayan.gameplay;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author cuong.nguyen
 */
public class GameLevel {
    
    int index;
    int order;
    String name;
    String description;
    String path;
    String icon;
    String image;
    int requiredStars;
    int stars;
    int status;
    boolean locked;

    protected Node levelNode;
    protected Vector3f startPos;
    protected Transform initialTransform;
    
    public void loadLevel(){
        
    }
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getRequiredStars() {
        return requiredStars;
    }

    public void setRequiredStars(int requiredStars) {
        this.requiredStars = requiredStars;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }    

    public Transform getInitialTransform() {
        return initialTransform;
    }

    public Node getLevelNode() {
        return levelNode;
    }

    public Vector3f getStartPos() {
        return startPos;
    }
}
