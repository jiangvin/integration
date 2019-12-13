package core.findway;

import core.Constant;
import core.Pos;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static core.Constant.MAX_FOUNT_COUNT;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/27
 */
public class GeneralFindWay extends BaseFindWay {

    Map<String, Way> wayMap = new HashMap<String, Way>();

    /**
     * LinkedList方便删除和追加，但不方便从中间查找
     */
    private List<Way> terminals = new LinkedList<Way>();

    Map<String, Pos> barrierMap = null;

    private Way goalWay = null;

    @Override
    public void find(Pos start, Pos end, Map<String, Pos> barrierMap) {

        //初始化
        this.wayMap.clear();
        this.terminals.clear();
        this.goalWay = null;
        this.barrierMap = barrierMap;

        Way startWay = new Way(start);
        wayMap.put(start.generateKey(), startWay);
        terminals.add(startWay);

        //开始查找
        while (!terminals.isEmpty()) {
            //每次取第一个开始计算
            Way way = terminals.get(0);
            terminals.remove(0);

            //已经找到，比较消耗值,若目标的消耗值比当前最低的都小，则查找完毕，退出循环
            if (goalWay != null && goalWay.getEffort() <= way.getEffort()) {
                break;
            }

            //找到了，但不能马上退出循环，因为可能还有更优解
            if (way.getPos().displayEquals(end)) {
                goalWay = way;
                continue;
            }

            //没找到，但消耗值超标，退出循环
            if (wayMap.size() > MAX_FOUNT_COUNT) {
                break;
            }

            findTerminals(way);
        }
    }

    private void findTerminals(Way way) {
        Pos pos = way.getPos();
        int displayX = pos.getDisplayX();
        int displayY = pos.getDisplayY();

        boolean canUp;
        boolean canLeft;
        boolean canRight;
        boolean canDown;

        //up
        Pos upPos = new Pos(displayX * Constant.SCALE, (displayY - 1) * Constant.SCALE);
        canUp = addNewWay(way, upPos);

        //left
        Pos leftPos = new Pos((displayX - 1) * Constant.SCALE, displayY * Constant.SCALE);
        canLeft = addNewWay(way, leftPos);

        //down
        Pos downPos = new Pos(displayX * Constant.SCALE, (displayY + 1) * Constant.SCALE);
        canDown = addNewWay(way, downPos);

        //right
        Pos rightPos = new Pos((displayX + 1) * Constant.SCALE, displayY * Constant.SCALE);
        canRight = addNewWay(way, rightPos);

        //up-left
        if (canUp && canLeft) {
            Pos upLeftPos = new Pos((displayX - 1) * Constant.SCALE, (displayY - 1) * Constant.SCALE);
            addNewWay(way, upLeftPos);
        }

        //up-right
        if (canUp && canRight) {
            Pos upRightPos = new Pos((displayX + 1) * Constant.SCALE, (displayY - 1) * Constant.SCALE);
            addNewWay(way, upRightPos);
        }

        //down-left
        if (canDown && canLeft) {
            Pos downLeftPos = new Pos((displayX - 1) * Constant.SCALE, (displayY + 1) * Constant.SCALE);
            addNewWay(way, downLeftPos);
        }

        //down-right
        if (canDown && canRight) {
            Pos downRightPos = new Pos((displayX + 1) * Constant.SCALE, (displayY + 1) * Constant.SCALE);
            addNewWay(way, downRightPos);
        }
    }

    protected boolean addNewWay(Way parentWay, Pos pos) {
        String key = pos.generateKey();
        if (barrierMap.containsKey(key)) {
            return false;
        }

        Way newWay = new Way(parentWay, pos);
        if (wayMap.containsKey(key) && wayMap.get(key).getEffort() <= newWay.getEffort()) {
            return false;
        }

        wayMap.put(key, newWay);
        addTerminals(newWay);
        return true;
    }

    void addTerminals(Way way) {
        //按照effort从小到大的顺序添加
        for (int i = 0; i < terminals.size(); ++i) {
            if (way.getEffort() < terminals.get(i).getEffort()) {
                terminals.add(i, way);
                return;
            }
        }
        terminals.add(way);
    }

    @Override
    public List<Pos> getGoalList() {
        if (goalWay == null) {
            return null;
        }

        List<Pos> goalList = new LinkedList<Pos>();
        Way way = goalWay;
        while (way != null) {
            goalList.add(0, way.getPos());
            way = way.getParentWay();
        }

        //删除第一个，因为是起点
        goalList.remove(0);

        return goalList;
    }

    @Override
    public List<Way> getWayList() {
        if (wayMap == null || wayMap.isEmpty()) {
            return null;
        }

        List<Way> list = new LinkedList<Way>();
        for (Map.Entry<String, Way> wayEntry : wayMap.entrySet()) {
            list.add(wayEntry.getValue());
        }

        return list;
    }
}
