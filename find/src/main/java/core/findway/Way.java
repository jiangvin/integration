package core.findway;

import core.Pos;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/28
 */
public class Way {
    private Pos pos;

    private Way parentWay;

    double effort;

    public Way(Way parentWay, Pos pos) {
        this.parentWay = parentWay;
        this.pos = pos;
        this.effort = parentWay.getEffort() + BaseFindWay.getDistance(parentWay.getPos(), pos);
    }

    public Way(Pos pos) {
        this.parentWay = null;
        this.pos = pos;
        this.effort = 0;
    }

    public Pos getPos() {
        return pos;
    }

    public double getEffort() {
        return effort;
    }

    public Way getParentWay() {
        return parentWay;
    }
}
