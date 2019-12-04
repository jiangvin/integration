import core.Constant;
import core.CoordsType;
import core.Pos;
import core.findway.Way;
import core.manager.ViewManager;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.FontMetrics;
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
        ViewManager view = Constant.VIEW_MANAGER;

        drawWay(g2);

        drawBarrier(g2);

        Pos man = Constant.MAP_MANAGER.getMan();
        List<Pos> goalList = Constant.MAP_MANAGER.getGoalList();
        if (goalList != null && !goalList.isEmpty()) {
            g2.setColor(Color.BLUE);
            int pointCount = goalList.size() + 1;
            int[] xs = new int[pointCount];
            int[] ys = new int[pointCount];
            int offset = view.getDisplaySize(0.5);
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
                    view.getDisplaySize(),
                    view.getDisplaySize());

        g2.fillOval(view.getDisplayCoords(man.getX(), CoordsType.X),
                    view.getDisplayCoords(man.getY(), CoordsType.Y),
                    view.getDisplaySize(),
                    view.getDisplaySize());

        g2.setColor(Color.BLACK);
        g2.drawString(Constant.MAP_MANAGER.getConfig(), 2, 12);
        g2.drawString(view.getConfig(), 2, getHeight() - 2);
    }

    private void drawBarrier(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        Map<String, Pos> map = Constant.MAP_MANAGER.getBarrierMap();
        for (Map.Entry<String, Pos> kv : map.entrySet()) {
            int x = Constant.VIEW_MANAGER.getDisplayCoords(kv.getValue().getX(), CoordsType.X);
            int y = Constant.VIEW_MANAGER.getDisplayCoords(kv.getValue().getY(), CoordsType.Y);
            g2.fill3DRect(x,
                          y,
                          Constant.VIEW_MANAGER.getDisplaySize(),
                          Constant.VIEW_MANAGER.getDisplaySize(),
                          true);
        }
    }

    private void drawWay(Graphics2D g2) {
        List<Way> wayList = Constant.MAP_MANAGER.getWayList();
        if (wayList != null) {
            for (Way way : wayList) {
                int x = Constant.VIEW_MANAGER.getDisplayCoords(way.getPos().getX(), CoordsType.X);
                int y = Constant.VIEW_MANAGER.getDisplayCoords(way.getPos().getY(), CoordsType.Y);
                g2.setColor(Color.YELLOW);
                g2.drawRect(x,
                            y,
                            Constant.VIEW_MANAGER.getDisplaySize(),
                            Constant.VIEW_MANAGER.getDisplaySize());
                if (Constant.VIEW_MANAGER.needShowDetail()) {
                    g2.setColor(Color.BLACK);
                    int offset = Constant.VIEW_MANAGER.getDisplaySize(0.5);
                    FontMetrics fm = g2.getFontMetrics();
                    int widthOffset = fm.stringWidth(way.getEffortForDisplay()) / 2;
                    g2.drawString(way.getEffortForDisplay(), x + offset - widthOffset, y + offset + 5);
                }
            }
        }
    }
}
