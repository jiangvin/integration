package core.findway;

import core.Pos;
import lombok.Getter;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/28
 */
public class Way {
    @Getter
    private Pos pos;

    @Getter
    private Way parentWay;

    @Getter
    double effort;

    private String effortForDisplay;

    public Way(Way parentWay, Pos pos) {
        this.parentWay = parentWay;
        this.pos = pos;
        this.effort = parentWay.getEffort() + BaseFindWay.getDistance(parentWay.getPos(), pos);
        generateDisplayStr();
    }

    public Way(Pos pos) {
        this.parentWay = null;
        this.pos = pos;
        this.effort = 0;
        generateDisplayStr();
    }

    void generateDisplayStr() {
        effortForDisplay = String.format("%.1f", effort);
    }

    public String getEffortForDisplay() {
        return effortForDisplay;
    }
}
