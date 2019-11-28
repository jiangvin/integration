package core;

import core.findway.BaseFindWay;
import core.findway.EstimateFindWay;
import core.findway.GeneralFindWay;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/27
 */
public class MapManager {

    private BaseFindWay ai = new GeneralFindWay();

    private Pos man = new Pos(Constant.START_X, Constant.START_Y);
    private Pos finalGoal = new Pos(Constant.START_X, Constant.START_Y);
    private List<Pos> goalList = null;
    private List<Pos> wayList = null;
    private Map<String, Pos> barrierMap = new HashMap<String, Pos>();

    private BarrierActionType barrierActionType = BarrierActionType.NONE;

    private String config = "";
    private boolean displayFindRange = false;
    private boolean displayFindWay = false;
    private long findTime = 0;

    public Map<String, Pos> getBarrierMap() {
        return barrierMap;
    }

    public List<Pos> getWayList() {
        if (!displayFindRange) {
            return null;
        }
        return wayList;
    }

    public void changeAi() {
        if (ai.getClass().toString().equals(GeneralFindWay.class.toString())) {
            ai = new EstimateFindWay();
        } else {
            ai = new GeneralFindWay();
        }
        generateConfig();
    }

    public void changeDisplayFindRange() {
        displayFindRange = !displayFindRange;
        generateConfig();
    }

    public void changeDisplayFindWay() {
        displayFindWay = !displayFindWay;
        generateConfig();
    }

    public Pos getMan() {
        return man;
    }

    public Pos getFinalGoal() {
        return finalGoal;
    }

    public void leftMouseDownEvent(int x, int y) {
        Pos pos = new Pos(x, y);
        String key = pos.generateKey();
        if (barrierMap.containsKey(key)) {
            barrierMap.remove(key);
            barrierActionType = BarrierActionType.REMOVE;
        } else {
            barrierMap.put(key, pos);
            barrierActionType = BarrierActionType.NEW;
        }
    }

    public void rightMouseDownEvent(int x, int y) {
        finalGoal = new Pos(x, y);
        finalGoal.adjustPosFromDisplayPos();
        ai(barrierMap);
    }

    public void moveUpEvent() {
        barrierActionType = BarrierActionType.NONE;
    }

    public void mouseMoveEvent(int x, int y) {
        if (barrierActionType == BarrierActionType.NONE) {
            return;
        }

        Pos pos = new Pos(x, y);
        String key = pos.generateKey();
        if (barrierActionType == BarrierActionType.NEW && !barrierMap.containsKey(key)) {
            barrierMap.put(key, pos);
        } else if (barrierActionType == BarrierActionType.REMOVE && barrierMap.containsKey(key)) {
            barrierMap.remove(key);
        }
    }

    public void run() {
        if (goalList == null || goalList.isEmpty()) {
            return;
        }

        Pos goal = goalList.get(0);
        if (barrierMap.containsKey(goal.generateKey())) {
            ai(barrierMap);
            return;
        }

        if (man.getX() < goal.getX()) {
            if (goal.getX() - man.getX() > Constant.SPEED) {
                man.setX(man.getX() + Constant.SPEED);
            } else {
                man.setX(goal.getX());
            }
        } else if (man.getX() > goal.getX()) {
            if (man.getX() - goal.getX() > Constant.SPEED) {
                man.setX(man.getX() - Constant.SPEED);
            } else {
                man.setX(goal.getX());
            }
        }

        if (man.getY() < goal.getY()) {
            if (goal.getY() - man.getY() > Constant.SPEED) {
                man.setY(man.getY() + Constant.SPEED);
            } else {
                man.setY(goal.getY());
            }
        } else if (man.getY() > goal.getY()) {
            if (man.getY() - goal.getY() > Constant.SPEED) {
                man.setY(man.getY() - Constant.SPEED);
            } else {
                man.setY(goal.getY());
            }
        }

        if (man.equals(goal)) {
            goalList.remove(0);
        }
    }

    private void ai(Map<String, Pos> barrierMap) {
        long before = System.currentTimeMillis();
        ai.find(man, finalGoal, barrierMap);
        findTime = System.currentTimeMillis() - before;
        goalList = ai.getGoalList();
        wayList = ai.getWayList();
        generateConfig();
    }

    private void generateConfig() {
        int findCount = 0;
        if (wayList != null) {
            findCount = wayList.size();
        }
        config = String.format("AI:%s    DisplayFindRange:%s    DisplayFindWay:%s    FindCount:%d    FindTime:%d",
                               ai.getClass().getSimpleName(),
                               displayFindRange,
                               displayFindWay,
                               findCount,
                               findTime);
    }

    public String getConfig() {
        return config;
    }

    public List<Pos> getGoalList() {
        if (!displayFindWay) {
            return null;
        }
        return goalList;
    }
}
