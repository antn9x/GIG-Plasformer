package sg.games.mayan.main;

/**
 *
 * @author cuong.nguyenmanh2
 */
public enum Configs {

    Phone(800, 480), PC(1024, 768);

    private Configs(int width, int height) {
        this.width = width;
        this.height = height;
    }
    public int width;
    public int height;
}
