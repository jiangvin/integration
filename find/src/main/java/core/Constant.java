package core;

import core.manager.MapManager;
import core.manager.ViewManager;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/27
 */
public class Constant {
    public static final MapManager MAP_MANAGER = new MapManager();
    public static final ViewManager VIEW_MANAGER = new ViewManager();

    /**
     * 每个物件的体积单位(不考虑缩放倍数，则每个单位占10个像素)
     */
    public static final int SCALE = 10;

    /**
     * 镜头移动，缩放相关
     */
    public static final double MIN_DISPLAY_SCALE = 0.5;
    public static final int VIEW_MOVE = 50;

    /**
     * 寻路消耗相关
     */
    public static final int MAX_FOUNT_COUNT = 10000;

    /**
     * 红点初始位置相关
     */
    public static final int START_X = 100;
    public static final int START_Y = 100;
    public static final int SPEED = 3;
}
