package core.findway;

import core.Pos;

import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/28
 */
public class EstimateFindWay extends GeneralFindWay {
    private Pos end;

    @Override
    public void find(Pos start, Pos end, Map<String, Pos> barrierMap) {
        this.end = end;
        super.find(start, end, barrierMap);
    }

    @Override
    protected boolean addNewWay(Way parentWay, Pos pos) {
        String key = pos.generateKey();
        if (barrierMap.containsKey(key)) {
            return false;
        }

        double effortAdjust = getDistance(pos, end) - getDistance(parentWay.getPos(), end);

        Way newWay = new EstimateWay(parentWay, pos, effortAdjust);
        if (wayMap.containsKey(key) && wayMap.get(key).getEffort() <= newWay.getEffort()) {
            return false;
        }

        wayMap.put(key, newWay);
        addTerminals(newWay);
        return true;
    }
}
