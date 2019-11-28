import core.Constant;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/27
 */

public class MouseEventAdapter extends MouseAdapter {

    @Override
    public void mousePressed(MouseEvent e) {
        switch (e.getButton()) {
            case 1:
                Constant.MAP_MANAGER.leftMouseDownEvent(e.getX(), e.getY());
                break;
            case 3:
                Constant.MAP_MANAGER.rightMouseDownEvent(e.getX(), e.getY());
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Constant.MAP_MANAGER.moveUpEvent();
    }

    public void mouseDragged(MouseEvent e) {
        Constant.MAP_MANAGER.mouseMoveEvent(e.getX(), e.getY());
    }
}
