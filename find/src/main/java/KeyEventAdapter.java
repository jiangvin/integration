import core.Constant;
import core.ViewMoveType;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/28
 */
public class KeyEventAdapter extends KeyAdapter {
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyChar()) {
            case '1':
                Constant.MAP_MANAGER.changeAi();
                break;
            case '2':
                Constant.MAP_MANAGER.changeDisplayFindRange();
                break;
            case '3':
                Constant.MAP_MANAGER.changeDisplayFindWay();
                break;
            case '4':
                Constant.MAP_MANAGER.changePause();
                break;
            case 'q':
                Constant.VIEW_MANAGER.rescaleDisplay(true);
                break;
            case 'e':
                Constant.VIEW_MANAGER.rescaleDisplay(false);
                break;
            case 'w':
                Constant.VIEW_MANAGER.viewMove(ViewMoveType.UP);
                break;
            case 'a':
                Constant.VIEW_MANAGER.viewMove(ViewMoveType.LEFT);
                break;
            case 's':
                Constant.VIEW_MANAGER.viewMove(ViewMoveType.DOWN);
                break;
            case 'd':
                Constant.VIEW_MANAGER.viewMove(ViewMoveType.RIGHT);
                break;
            default:
                break;
        }
    }
}
