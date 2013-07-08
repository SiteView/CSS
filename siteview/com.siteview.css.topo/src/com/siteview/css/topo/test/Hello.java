package com.siteview.css.topo.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Hello extends JPanel {
    private java.util.List<MyLabel> labels = new java.util.ArrayList<MyLabel>();

    public Hello() {
        setLayout(null);

        // 创建labels
        addLabel(50, 50);
        addLabel(250, 100);
        addLabel(100, 200);
        addLabel(250, 300);

        // 建立label之间的连接.
        labels.get(0).addConnectToLabel(labels.get(1));
        labels.get(0).addConnectToLabel(labels.get(2));
        labels.get(0).addConnectToLabel(labels.get(3));
    }

    private void addLabel(int x, int y) {
        MyLabel label = new MyLabel();
        label.setLocation(x, y);

        this.add(label);
        labels.add(label);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (MyLabel label : labels) {
            for (MyLabel toLabel : label.getConnectToLabels()) {
                Line2D line = new Line2D.Float(label.getCentralPoint(), toLabel.getCentralPoint());
                g2d.draw(line);

                // 接下来计算line与label的交点，然后在交点处画三角形.
            }
        }
    }

    private static void createAndShowGui() {
        JFrame frame = new JFrame("");
        frame.getContentPane().add(new Hello());
        // Set frame's close operation and location in the screen.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        createAndShowGui();
    }
}

class MyLabel extends JLabel {
    private Point delta = new Point();
    private java.util.List<MyLabel> connectToLabels = new java.util.LinkedList<MyLabel>();

    public MyLabel() {
        setSize(80, 30);
        setOpaque(true);
        setBackground(Color.YELLOW);
        setHorizontalAlignment(CENTER);
        handleEvents();
    }

    public Point2D getCentralPoint() {
        Point pos = this.getLocation();
        Point2D cp = new Point2D.Double();
        cp.setLocation(pos.x + getWidth() / 2, pos.y + getHeight() / 2);
        return cp;
    }

    public void addConnectToLabel(MyLabel label) {
        connectToLabels.add(label);
    }

    public java.util.List<MyLabel> getConnectToLabels() {
        return connectToLabels;
    }

    @Override
    public void setLocation(Point pos) {
        setLocation(pos.x, pos.y);
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        setText("[" + x + ", " + y + "]");
    }

    private void handleEvents() {
        MouseAdapter adapter = new MouseEventHandler();
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    class MouseEventHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            Point p = e.getPoint();
            delta.setLocation(p.x, p.y);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();
            Point pos = new Point();
            pos.setLocation(p.x - delta.x, p.y - delta.y);

            SwingUtilities.convertPointToScreen(pos, MyLabel.this);
            SwingUtilities.convertPointFromScreen(pos, getParent());
            setLocation(pos);
            getParent().repaint();
        }
    }
}
