import core.Constant;
import core.CoordsType;
import core.manager.ViewManager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/27
 */

public class MouseEventAdapter extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
        ViewManager view = Constant.VIEW_MANAGER;
        switch (e.getButton()) {
            case 1:
                Constant.MAP_MANAGER.leftMouseDownEvent(view.getRealCoords(e.getX(), CoordsType.X),
                                                        view.getRealCoords(e.getY(), CoordsType.Y));
                break;
            case 3:
                Constant.MAP_MANAGER.rightMouseDownEvent(view.getRealCoords(e.getX(), CoordsType.X),
                                                         view.getRealCoords(e.getY(), CoordsType.Y));
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
        ViewManager view = Constant.VIEW_MANAGER;
        Constant.MAP_MANAGER.mouseMoveEvent(view.getRealCoords(e.getX(), CoordsType.X),
                                            view.getRealCoords(e.getY(), CoordsType.Y));
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            Constant.VIEW_MANAGER.rescaleDisplay(true);
        } else if (e.getWheelRotation() > 0) {
            Constant.VIEW_MANAGER.rescaleDisplay(false);
        }
    }
}
