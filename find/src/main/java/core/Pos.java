package core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/27
 */
@Data
@AllArgsConstructor
public class Pos {
    private int x;
    private int y;

    public Pos(int x, int y, boolean adjust) {
        this.x = x;
        this.y = y;

        if (adjust) {
            adjustPosFromDisplayPos();
        }
    }

    public String generateKey() {
        return String.format("%d_%d", getDisplayX(), getDisplayY());
    }

    public int getDisplayX() {
        if (x < 0) {
            return (x + 1) / Constant.SCALE - 1;
        } else {
            return x / Constant.SCALE;
        }
    }

    public int getDisplayY() {
        if (y < 0) {
            return (y + 1) / Constant.SCALE - 1;
        } else {
            return y / Constant.SCALE;
        }
    }

    private void adjustPosFromDisplayPos() {
        x = getDisplayX() * Constant.SCALE;
        y = getDisplayY() * Constant.SCALE;
    }

    public boolean displayEquals(Pos pos) {
        return getDisplayX() == pos.getDisplayX() && getDisplayY() == pos.getDisplayY();
    }

    public boolean realEquals(Pos pos) {
        return this.x == pos.x && this.y == pos.y;
    }
}
