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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void adjustPosFromDisplayPos() {
        x = x / Constant.SCALE * Constant.SCALE;
        y = y / Constant.SCALE * Constant.SCALE;
    }

    public boolean displayEquals(Pos pos) {
        return getDisplayX() == pos.getDisplayX() && getDisplayY() == pos.getDisplayY();
    }

    public boolean equals(Pos pos) {
        return this.x == pos.x && this.y == pos.y;
    }
}
