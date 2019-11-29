package core;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/27
 */
public class Pos {
    private int x;
    private int y;

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Pos(int x, int y, boolean adjust) {
        this.x = x;
        this.y = y;

        if (adjust) {
            adjustPosFromDisplayPos();
        }
    }

    public String generateKey() {
        int displayX = x / Constant.SCALE;
        int displayY = y / Constant.SCALE;
        return String.format("%d_%d", displayX, displayY);
    }

    public int getDisplayX() {
        return x / Constant.SCALE;
    }

    public int getDisplayY() {
        return y / Constant.SCALE;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    void setX(int x) {
        this.x = x;
    }

    void setY(int y) {
        this.y = y;
    }

    private void adjustPosFromDisplayPos() {
        x = x / Constant.SCALE * Constant.SCALE;
        y = y / Constant.SCALE * Constant.SCALE;
    }

    public boolean displayEquals(Pos pos) {
        return getDisplayX() == pos.getDisplayX() && getDisplayY() == pos.getDisplayY();
    }

    boolean equals(Pos pos) {
        return this.x == pos.x && this.y == pos.y;
    }
}
