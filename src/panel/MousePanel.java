package panel;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MousePanel extends ImgPanel//JPanel
{
    int x_pos = -1, y_pos = -1, click_times;

    ArrayList<Point> pointList;

    public int getPointX() {
        return x_pos;
    }

    public int getPointY() {
        return y_pos;
    }

    public int getClick_times() {
        return click_times;
    }

    public void setClick_times(int click_times) {
        this.click_times = click_times;
    }

    public ArrayList<Point> getPointList() {
        return pointList;
    }

    public void setPointList(ArrayList<Point> pointList) {
        this.pointList = pointList;
    }

    public void clearPointList() {
        this.pointList = new ArrayList<Point>();
        this.pointList.clear();
    }

    public MousePanel() {
        addMouseListener(new MouseListener() {
            //mouseClicked():鼠标单击
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                    x_pos = e.getX();
                    y_pos = e.getY();
                    if (x_pos > MousePanel.super.getImgWidth() || y_pos > MousePanel.super.getImgHeight()) {
                        JOptionPane.showMessageDialog(null, "选点超出图片区域！", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Point point = new Point(x_pos, y_pos);
                    pointList.add(point);
                    click_times++;
                    repaint();
                }
            }

            //mouseEntered():鼠标进入时
            @Override
            public void mouseEntered(MouseEvent e) {
            }

            //mouseExited():鼠标离开时
            @Override
            public void mouseExited(MouseEvent e) {
            }

            //mousePressed():鼠标按下去
            @Override
            public void mousePressed(MouseEvent e) {

            }

            //mouseReleased():鼠标松开时
            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
            }

            @Override
            public void mouseDragged(MouseEvent e) {

            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (x_pos == -1 && y_pos == -1) {
            return;
        }
        g.drawString("current location is:[" + x_pos + "," + y_pos + "]", x_pos, y_pos);//在界面上显示
        Color color = null;
        //g.drawRect(x_pos - 25, y_pos - 25, 50, 50);
        for (Point point : pointList) {
            int m = pointList.indexOf(point) % 8;
            switch (m) {
                case 0:
                    color = Color.RED;
                    break;
                case 1:
                    color = Color.BLUE;
                    break;
                case 2:
                    color = Color.CYAN;
                    break;
                case 3:
                    color = Color.GREEN;
                    break;
                case 4:
                    color = Color.MAGENTA;
                    break;
                case 5:
                    color = Color.ORANGE;
                    break;
                case 6:
                    color = Color.PINK;
                    break;
                case 7:
                    color = Color.YELLOW;
                    break;
            }
            g.setColor(color);
            g.fillOval(point.x, point.y, 6, 6);
        }
    }
}