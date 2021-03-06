package core.manager;

import core.Constant;
import core.CoordsType;
import core.ViewMoveType;

import java.math.BigDecimal;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/28
 */
public class ViewManager {
    private double displayScale = 1;
    private double scaleWithAdjust = displayScale;

    private int top = 0;
    private int left = 0;

    /**
     * 用于鼠标控制镜头
     */
    private int xMouse = 0;
    private int yMouse = 0;

    private String config = "";

    public ViewManager() {
        generateConfig();
    }

    public int getDisplayCoords(double value, CoordsType type) {
        int offset = getOffset(type);
        return (int)((value - offset) * scaleWithAdjust + 0.5);
    }

    private int getOffset(CoordsType type) {
        switch (type) {
            case X:
                return left;
            case Y:
                return top;
            default:
                return 0;
        }
    }

    public int getRealCoords(double value, CoordsType type) {
        int offset = getOffset(type);
        return (int)(value / scaleWithAdjust + 0.5) + offset;
    }

    public int getDisplaySize() {
        return getDisplaySize(1);
    }

    public int getDisplaySize(double resize) {
        return getDisplayCoords(Constant.SCALE * resize, CoordsType.NONE);
    }

    public void rescaleDisplay(boolean zoomIn) {
        if (zoomIn) {
            displayScale *= 1.1;
        } else if (displayScale > Constant.MIN_DISPLAY_SCALE) {
            displayScale /= 1.1;
        }
        BigDecimal bg = new BigDecimal(displayScale);
        scaleWithAdjust = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        generateConfig();
    }

    public void viewMove(ViewMoveType type) {
        int moveValue = getRealCoords(Constant.VIEW_MOVE, CoordsType.NONE);
        switch (type) {
            case UP:
                top -= moveValue;
                break;
            case DOWN:
                top += moveValue;
                break;
            case LEFT:
                left -= moveValue;
                break;
            case RIGHT:
                left += moveValue;
                break;
            default:
                break;
        }
        generateConfig();
    }

    public void setMousePos(int x, int y) {
        xMouse = x;
        yMouse = y;
    }

    public void viewMove(int x, int y) {
        left -= getRealCoords(x, CoordsType.X) - getRealCoords(xMouse, CoordsType.X);
        top -= getRealCoords(y, CoordsType.Y) - getRealCoords(yMouse, CoordsType.Y);
        generateConfig();
    }

    public String getConfig() {
        return config;
    }

    private void generateConfig() {
        config = String.format("Top:%d    Left:%d    Scale:%.1f", top, left, scaleWithAdjust);
    }

    public boolean needShowDetail() {
        return displayScale > 4.0;
    }
}
