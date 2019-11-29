import core.Constant;
import core.CoordsType;
import core.Pos;
import core.ViewController;

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
        ViewController view = Constant.VIEW_CONTROLLER;

        g2.setColor(Color.YELLOW);
        List<Pos> wayList = Constant.MAP_MANAGER.getWayList();
        if (wayList != null) {
            for (Pos pos : wayList) {
                int x = view.getDisplayCoords(pos.getX(), CoordsType.X);
                int y = view.getDisplayCoords(pos.getY(), CoordsType.Y);
                g2.drawRect(x, y, view.getDisplayScale(), view.getDisplayScale());
            }
        }

        g2.setColor(Color.BLACK);
        Map<String, Pos> map = Constant.MAP_MANAGER.getBarrierMap();
        for (Map.Entry<String, Pos> kv : map.entrySet()) {
            int x = view.getDisplayCoords(kv.getValue().getX(), CoordsType.X);
            int y = view.getDisplayCoords(kv.getValue().getY(), CoordsType.Y);
            g2.fill3DRect(x, y, view.getDisplayScale(), view.getDisplayScale(), true);
        }

        Pos man = Constant.MAP_MANAGER.getMan();
        List<Pos> goalList = Constant.MAP_MANAGER.getGoalList();
        if (goalList != null && !goalList.isEmpty()) {
            g2.setColor(Color.BLUE);
            int pointCount = goalList.size() + 1;
            int[] xs = new int[pointCount];
            int[] ys = new int[pointCount];
            int offset = view.getDisplayScale(0.5);
            xs[0] = view.getDisplayCoords(man.getX(), CoordsType.X) + offset;
            ys[0] = view.getDisplayCoords(man.getY(), CoordsType.Y) + offset;
            for (int i = 0; i < goalList.size(); ++i) {
                xs[i + 1] = view.getDisplayCoords(goalList.get(i).getX(), CoordsType.X)  + offset;
                ys[i + 1] = view.getDisplayCoords(goalList.get(i).getY(), CoordsType.Y)  + offset;
            }
            g2.drawPolyline(xs, ys, pointCount);
        }

        Pos goal = Constant.MAP_MANAGER.getFinalGoal();
        g2.setColor(Color.RED);
        g2.drawOval(view.getDisplayCoords(goal.getX(), CoordsType.X),
                    view.getDisplayCoords(goal.getY(), CoordsType.Y),
                    view.getDisplayScale(),
                    view.getDisplayScale());

        g2.fillOval(view.getDisplayCoords(man.getX(), CoordsType.X),
                    view.getDisplayCoords(man.getY(), CoordsType.Y),
                    view.getDisplayScale(),
                    view.getDisplayScale());

        g2.setColor(Color.BLACK);
        g2.drawString(Constant.MAP_MANAGER.getConfig(), 2, 12);
        g2.drawString(view.getConfig(), 2, 24);
    }
}
