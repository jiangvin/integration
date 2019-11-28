import core.Constant;

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
            default:
                break;
        }
    }
}
