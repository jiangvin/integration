package core;

import core.manager.MapManager;
import core.manager.ViewManager;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/27
 */
public class Constant {
    public static final int SCALE = 10;

    public static final MapManager MAP_MANAGER = new MapManager();
    public static final ViewManager VIEW_MANAGER = new ViewManager();

    public static final double MIN_DISPLAY_SCALE = 0.5;
    public static final int VIEW_MOVE = 50;

    public static final int MAX_EFFORT_TIMES = 10;

    public static final int START_X = 100;
    public static final int START_Y = 100;
    public static final int SPEED = 3;
}
