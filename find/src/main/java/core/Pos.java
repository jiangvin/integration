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

    private void adjustPosFromDisplayPos() {
        x = x / Constant.SCALE * Constant.SCALE;
        y = y / Constant.SCALE * Constant.SCALE;
    }

    public boolean displayEquals(Pos pos) {
        return getDisplayX() == pos.getDisplayX() && getDisplayY() == pos.getDisplayY();
    }

    public boolean realEquals(Pos pos) {
        return this.x == pos.x && this.y == pos.y;
    }
}
