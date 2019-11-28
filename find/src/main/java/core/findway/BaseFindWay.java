package core.findway;

import core.Pos;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.List;
import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/28
 */
public abstract class BaseFindWay {
    static double getDistance(Pos start, Pos end) {
        return Math.sqrt(Math.pow(end.getDisplayX() - start.getDisplayX(), 2) + Math.pow(end.getDisplayY() - start.getDisplayY(), 2));
    }


    /**
     * 查找的主函数
     * @param start start
     * @param end end
     * @param barrierMap 障碍物
     */
    public abstract void find(Pos start, Pos end, Map<String, Pos> barrierMap);

    /**
     * 获取结果
     * @return result list
     */
    public abstract List<Pos> getGoalList();

    /**
     * 获取查找消耗
     * @return result list
     */
    public abstract List<Pos> getWayList();
}
