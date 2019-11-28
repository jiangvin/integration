import core.Constant;
import core.Pos;
import javafx.scene.shape.Polyline;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/27
 */
public class MainPanel extends JPanel {

    MainPanel() {
        new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Constant.MAP_MANAGER.run();
                repaint();
            }
        }).start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.YELLOW);
        List<Pos> wayList = Constant.MAP_MANAGER.getWayList();
        if (wayList != null) {
            for (Pos pos : wayList) {
                int x = pos.getDisplayX() * Constant.SCALE;
                int y = pos.getDisplayY() * Constant.SCALE;
                g2.drawRect(x, y, Constant.SCALE, Constant.SCALE);
            }
        }

        g2.setColor(Color.BLACK);
        Map<String, Pos> map = Constant.MAP_MANAGER.getBarrierMap();
        for (Map.Entry<String, Pos> kv : map.entrySet()) {
            int x = kv.getValue().getDisplayX() * Constant.SCALE;
            int y = kv.getValue().getDisplayY() * Constant.SCALE;
            g2.fill3DRect(x, y, Constant.SCALE, Constant.SCALE, true);
        }

        Pos man = Constant.MAP_MANAGER.getMan();
        List<Pos> goalList = Constant.MAP_MANAGER.getGoalList();
        if (goalList != null && !goalList.isEmpty()) {
            g2.setColor(Color.BLUE);
            int pointCount = goalList.size() + 1;
            int[] xs = new int[pointCount];
            int[] ys = new int[pointCount];
            int offset = Constant.SCALE / 2;
            xs[0] = man.getX() + offset;
            ys[0] = man.getY() + offset;
            for (int i = 0; i < goalList.size(); ++i) {
                xs[i + 1] = goalList.get(i).getX()  + offset;
                ys[i + 1] = goalList.get(i).getY()  + offset;
            }
            g2.drawPolyline(xs, ys, pointCount);
        }

        Pos goal = Constant.MAP_MANAGER.getFinalGoal();
        g2.setColor(Color.RED);
        g2.drawOval(goal.getX(), goal.getY(), Constant.SCALE, Constant.SCALE);

        g2.fillOval(man.getX(), man.getY(), Constant.SCALE, Constant.SCALE);

        g2.setColor(Color.BLACK);
        g2.drawString(Constant.MAP_MANAGER.getConfig(), 2, 12);
    }
}
