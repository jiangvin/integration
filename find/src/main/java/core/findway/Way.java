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

    private double parentEffort;

    private double effort;

    public Way(Way parentWay, Pos pos) {
        this.parentEffort = parentWay.getEffort();
        this.parentWay = parentWay;
        this.pos = pos;
        this.effort = parentEffort + BaseFindWay.getDistance(parentWay.getPos(), pos);
    }

    public Way(Way parentWay, Pos pos, double effortAdjust) {
        this.parentEffort = parentWay.getEffort();
        this.parentWay = parentWay;
        this.pos = pos;
        this.effort = parentEffort + BaseFindWay.getDistance(parentWay.getPos(), pos) + effortAdjust;
    }

    public Way(Pos pos) {
        this.parentEffort = 0;
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
