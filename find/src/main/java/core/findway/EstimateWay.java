package core.findway;

import core.Pos;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/29
 */
public class EstimateWay extends Way {
    public EstimateWay(Way parentWay, Pos pos, double effortAdjust) {
        super(parentWay, pos);
        this.effort += effortAdjust;
        generateDisplayStr();
    }
}
