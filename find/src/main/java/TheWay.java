import javax.swing.JFrame;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/27
 */
public class TheWay {
    public static void main(String[] args) {
        JFrame frame = new JFrame("TheWay");

        MainPanel mainPanel = new MainPanel();
        MouseEventAdapter mouseEventAdapter = new MouseEventAdapter();
        KeyEventAdapter keyEventAdapter = new KeyEventAdapter();
        mainPanel.addMouseListener(mouseEventAdapter);
        mainPanel.addMouseMotionListener(mouseEventAdapter);
        mainPanel.addMouseWheelListener(mouseEventAdapter);
        mainPanel.addKeyListener(keyEventAdapter);
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);
        mainPanel.setFocusable(true);
    }
}
