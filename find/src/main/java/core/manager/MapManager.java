package core.manager;

import core.ActionType;
import core.Constant;
import core.Pos;
import core.findway.BaseFindWay;
import core.findway.EstimateFindWay;
import core.findway.GeneralFindWay;
import core.findway.Way;

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
    private List<Way> wayList = null;
    private Map<String, Pos> barrierMap = new HashMap<String, Pos>();

    private ActionType barrierActionType = ActionType.NONE;

    private String config = "";
    private boolean displayFindRange = false;
    private boolean displayFindWay = false;
    private boolean pause = false;
    private long findTime = 0;

    public MapManager() {
        generateConfig();
    }

    public Map<String, Pos> getBarrierMap() {
        return barrierMap;
    }

    public List<Way> getWayList() {
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

    public void changePause() {
        pause = !pause;
        generateConfig();
    }

    public Pos getMan() {
        return man;
    }

    public Pos getFinalGoal() {
        return finalGoal;
    }

    public void leftMouseDownEvent(int x, int y) {
        Pos pos = new Pos(x, y, true);
        String key = pos.generateKey();
        if (barrierMap.containsKey(key)) {
            barrierMap.remove(key);
            barrierActionType = ActionType.REMOVE_BARRIER;
        } else {
            barrierMap.put(key, pos);
            barrierActionType = ActionType.NEW_BARRIER;
        }
    }

    public void rightMouseDownEvent(int x, int y) {
        finalGoal = new Pos(x, y, true);
        ai(barrierMap);
    }

    public void moveUpEvent() {
        barrierActionType = ActionType.NONE;
    }

    public void mouseMoveEvent(int x, int y) {
        if (barrierActionType == ActionType.NONE) {
            return;
        }

        Pos pos = new Pos(x, y, true);
        String key = pos.generateKey();
        if (barrierActionType == ActionType.NEW_BARRIER && !barrierMap.containsKey(key)) {
            barrierMap.put(key, pos);
        } else if (barrierActionType == ActionType.REMOVE_BARRIER && barrierMap.containsKey(key)) {
            barrierMap.remove(key);
        }
    }

    public void run() {
        if (pause) {
            return;
        }

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

        if (man.realEquals(goal)) {
            goalList.remove(0);
        }
    }

    private void ai(Map<String, Pos> barrierMap) {
        Pos start = new Pos(man.getX(), man.getY(), true);
        long before = System.currentTimeMillis();
        ai.find(start, finalGoal, barrierMap);
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
        config = String.format("1.AI:%s    2.FindRange:%s    3.FindWay:%s    4.Pause:%s    FindCount:%d    FindTime:%d",
                               ai.getClass().getSimpleName(),
                               displayFindRange,
                               displayFindWay,
                               pause,
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
