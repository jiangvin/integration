package com.integration.provider.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author 蒋文龙(Vin)
 * Created on 2018/12/20
 */

public class MapManager {
    @Data
    public class Point {
        @NonNull private long x;
        @NonNull private long y;
    }

    private static final int MAX_DISTANCE = 1000;
    private static final int GRID_DISTANCE = 50;
    private static final int MAX_GRID_INDEX = MAX_DISTANCE / GRID_DISTANCE;

    private ConcurrentHashMap<String, ConcurrentHashMap<String, Point>> map = new ConcurrentHashMap<>();

    @Getter
    private long pointCount;

    public Point findNearest(long x, long y) {
        List<Point> points = new ArrayList<>();

        //先计算出网格的X,Y
        int startGridX = (int)(x / GRID_DISTANCE);
        int startGridY = (int)(y / GRID_DISTANCE);

        //先找到初始网格的所有点
        int gridX = startGridX;
        int gridY = startGridY;
        addPointToList(points, gridX, gridY);

        for (int index = 1; index <= MAX_GRID_INDEX; ++index) {
            //每一层遍历的起点始终在上一层终点的右边，原因自己画图体会
            //顺时针螺旋遍历，初始往下，遍历到右上角时结束，一层一层找
            addPointToList(points, ++gridX, gridY);
            int gridXAdd = 0;
            int gridYAdd = -1;
            //开始遍历
            do {
                gridX += gridXAdd;
                gridY += gridYAdd;
                addPointToList(points, gridX, gridY);

                if (gridX == startGridX + index && gridY == startGridY - index) {
                    //到了右下角，开始往左走
                    gridXAdd = -1;
                    gridYAdd = 0;
                } else if (gridX == startGridX - index && gridY == startGridY - index) {
                    //到了左下角，开始往上走
                    gridXAdd = 0;
                    gridYAdd = 1;
                } else if (gridX == startGridX - index && gridY == startGridY + index) {
                    //到了左上角，开始往右走
                    gridXAdd = 1;
                    gridYAdd = 0;
                } else if (gridX == startGridX + index && gridY == startGridY + index) {
                    //到了右上角，结束
                    break;
                }
            } while (true);

            //遍历完成，开始找最近的点
            double nearestDistance = MAX_DISTANCE;
            Point nearest = null;
            for (Point p : points) {
                double distance = Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2));
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = p;
                }
            }
            if (nearest != null) {
                //找到了
                return nearest;
            } else {
                //没找到，清除points,继续找
                points.clear();
            }
        }
        //最大寻找距离范围内没有点
        return null;
    }

    public void AddPoint(long x, long y) {
        String pointKey = generateKey(x, y);

        //先计算出网格的X,Y,key
        long gridX = x / GRID_DISTANCE;
        long gridY = y / GRID_DISTANCE;
        String gridKey = generateKey(gridX, gridY);
        if (map.containsKey(gridKey)) {
            //网格存在，看有没有点
            ConcurrentHashMap<String, Point> pointMap = map.get(gridKey);
            if (!pointMap.containsKey(pointKey)) {
                pointMap.put(pointKey, new Point(x, y));
                ++pointCount;
            }
        } else {
            //网格不存在，直接添加
            ConcurrentHashMap<String, Point> pointMap = new ConcurrentHashMap<>();
            pointMap.put(pointKey, new Point(x, y));
            map.put(gridKey, pointMap);
            ++pointCount;
        }
    }

    private String generateKey(long x, long y) {
        return String.format("%d,%d", x, y);
    }

    private void addPointToList(List<Point> list, int gridX, int gridY) {
        String key = generateKey(gridX, gridY);
        if (!map.containsKey(key)) {
            return;
        }
        list.addAll(map.get(key).values());
    }
}
