package com.siteview.css.topo.test;

/*
 * @(#)Graph.java	1.16 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)Graph.java	1.16 04/07/26
 */

import java.util.*;
import java.awt.*;
import java.applet.Applet;
import java.awt.event.*;

/**
 * 创建节点
 * @author zhangxinnan
 */
class Node {
    double x;
    double y;
    double dx;
    double dy;
    boolean fixed;//固定不变的
    /**获得图形的名称*/
    String lbl;
}

/**
 *  创建边缘
 * @author zhangxinnan
 */
class Edge {
	/**开始的模型*/
    int from;
    /**结束的模型*/
    int to;
    /**连接线长度*/
    double len;
}

/**
 * 创建绘图面板
 * @author zhangxinnan
 */
class GraphPanel extends Panel implements Runnable, MouseListener, MouseMotionListener {
    Graph graph;
    
    int nnodes;
    Node nodes[] = new Node[100];

    int nedges;
    Edge edges[] = new Edge[200];

    Thread relaxer;
    boolean stress;
    boolean random;

    int numMouseButtonsDown = 0;

    GraphPanel(Graph graph) {
		this.graph = graph;
		addMouseListener(this);
    }
    //查找节点
    int findNode(String lbl) {//x-a,x-b,a-c,a-f,b-d,b-e
    	System.out.println("初始值="+nnodes+"   lbl="+lbl);
		for (int i = 0 ; i < nnodes ; i++) {
			System.out.println("现在获得的lbl是："+nodes[i].lbl+"   i="+i);
		    if (nodes[i].lbl.equals(lbl)) {
		    	System.out.println("返回的i为:"+i);
		    	return i;
		    }
		}
		return addNode(lbl);
    }
    //添加节点 设置节点坐标
    int addNode(String lbl) {
		Node n = new Node();
		n.x = 10 + 380*Math.random();
		n.y = 10 + 380*Math.random();
		n.lbl = lbl;
		nodes[nnodes] = n;//nodes[0]=x;设置节点坐标
		//System.out.println(nodes[0].x +"~"+nodes[0].y);
		System.out.println("nnodes="+nnodes+"   设置了坐标："+lbl);
		return nnodes++;
    }
    //添加边线  0-1,0-2,0-3,1-4,1-5,1-6,2-7,2-8,2-9,3-10,3-11,3-12
    void addEdge(String from, String to, int len) {//x-a,x-b,a-c,a-f,b-d,b-e
    	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~from="+from+" to="+to+" len="+len);
		Edge e = new Edge();
		System.out.println("=================from=============");
		e.from = findNode(from);
		System.out.println("=================to===============");
		e.to = findNode(to);
		e.len = len;
		edges[nedges++] = e;
    }

    public void load(){
    	
    }
    public void run() {
    	System.out.println("--------------------------------------------");
        Thread me = Thread.currentThread();
        relax();
		while (relaxer == me) {
//		    relax();
//		    if (random && (Math.random() < 0.03)) {
//			Node n = nodes[(int)(Math.random() * nnodes)];
//			if (!n.fixed) {
//			    n.x += 100*Math.random() - 50;
//			    n.y += 100*Math.random() - 50;
//			}
//			graph.play(graph.getCodeBase(), "audio/drip.au");
//		    }
//		    try {
//			Thread.sleep(100);
//		    } catch (InterruptedException e) {
//			break;
//		    }
		}
    }

    synchronized void relax() {
	for (int i = 0 ; i < nedges ; i++) {
	    Edge e = edges[i];
	    double vx = nodes[e.to].x - nodes[e.from].x;
	    double vy = nodes[e.to].y - nodes[e.from].y;
	    double len = Math.sqrt(vx * vx + vy * vy);
            len = (len == 0) ? .0001 : len;
	    double f = (edges[i].len - len) / (len * 3);
	    double dx = f * vx;
	    double dy = f * vy;

	    nodes[e.to].dx += dx;
	    nodes[e.to].dy += dy;
	    nodes[e.from].dx += -dx;
	    nodes[e.from].dy += -dy;
	}

	for (int i = 0 ; i < nnodes ; i++) {
	    Node n1 = nodes[i];
	    double dx = 0;
	    double dy = 0;

	    for (int j = 0 ; j < nnodes ; j++) {
			if (i == j) {
			    continue;
			}
			Node n2 = nodes[j];
			double vx = n1.x - n2.x;
			double vy = n1.y - n2.y;
			double len = vx * vx + vy * vy;
			if (len == 0) {
			    dx += Math.random();
			    dy += Math.random();
			} else if (len < 100*100) {
			    dx += vx / len;
			    dy += vy / len;
			}
	    }
	    double dlen = dx * dx + dy * dy;
	    if (dlen > 0) {
			dlen = Math.sqrt(dlen) / 2;
			n1.dx += dx / dlen;
			n1.dy += dy / dlen;
	    }
	}

	Dimension d = getSize();
	for (int i = 0 ; i < nnodes ; i++) {
	    Node n = nodes[i];
	    if (!n.fixed) {
			n.x += Math.max(-5, Math.min(5, n.dx));
			n.y += Math.max(-5, Math.min(5, n.dy));
        }
            if (n.x < 0) {
                n.x = 0;
            } else if (n.x > d.width) {
                n.x = d.width;
            }
            if (n.y < 0) {
                n.y = 0;
            } else if (n.y > d.height) {
                n.y = d.height;
            }
	    n.dx /= 2;
	    n.dy /= 2;
	}
		repaint();
    }

    Node pick;
    boolean pickfixed;
    Image offscreen;
    Dimension offscreensize;
    Graphics offgraphics;

    final Color fixedColor = Color.red;
    final Color selectColor = Color.green;
    final Color edgeColor = Color.black;
    final Color nodeColor = new Color(250, 220, 100);
    final Color stressColor = Color.darkGray;
    final Color arcColor1 = Color.black;
    final Color arcColor2 = Color.pink;
    final Color arcColor3 = Color.red;

    /**
     * 绘制图形
     * @param g
     * @param n
     * @param fm
     */
    public void paintNode(Graphics g, Node n, FontMetrics fm) {
			int x = (int)n.x;
			int y = (int)n.y;
			g.setColor((n == pick) ? selectColor : (n.fixed ? fixedColor : nodeColor));
			int w = fm.stringWidth(n.lbl) + 10;
			int h = fm.getHeight() + 4;
			g.fillRect(x - w/2, y - h / 2, w, h);//填充的矩形.这里可以放一张图片..
			g.setColor(Color.cyan);//边框字体颜色
			g.drawRect(x - w/2, y - h / 2, w-1, h-1);//绘制矩形
			g.drawString(n.lbl, x - (w-10)/2, (y - (h-4)/2) + fm.getAscent());//写在图片里面的文字	
    }

    /**
     * 创建一个可随意拖动的图片
     */
    public synchronized void update(Graphics g) {
		Dimension d = getSize();
		if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
		    offscreen = createImage(d.width, d.height);
		    offscreensize = d;
		    if (offgraphics != null) {
		        offgraphics.dispose();
		    }
		    offgraphics = offscreen.getGraphics();
		    offgraphics.setFont(getFont());
		}

		offgraphics.setColor(getBackground());
		offgraphics.fillRect(0, 0, d.width, d.height);
		for (int i = 0 ; i < nedges ; i++) {
		    Edge e = edges[i];
		    int x1 = (int)nodes[e.from].x;
		    int y1 = (int)nodes[e.from].y;
		    int x2 = (int)nodes[e.to].x;
		    int y2 = (int)nodes[e.to].y;
		    int len = (int)Math.abs(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)) - e.len);
		    offgraphics.setColor((len < 10) ? arcColor1 : (len < 20 ? arcColor2 : arcColor3)) ;
		    offgraphics.drawLine(x1, y1, x2, y2);
		    if (stress) {
				String lbl = String.valueOf(len);
				offgraphics.setColor(stressColor);
				offgraphics.drawString(lbl, x1 + (x2-x1)/2, y1 + (y2-y1)/2);
				offgraphics.setColor(edgeColor);
		    }
		}
	
		FontMetrics fm = offgraphics.getFontMetrics();
		for (int i = 0 ; i < nnodes ; i++) {
		    paintNode(offgraphics, nodes[i], fm);
		}
		g.drawImage(offscreen, 0, 0, null);
    }

    //鼠标事件
    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
//	        numMouseButtonsDown++;
//	        addMouseMotionListener(this);
//		double bestdist = Double.MAX_VALUE;
//	
//		int x = e.getX();
//		int y = e.getY();
//		for (int i = 0 ; i < nnodes ; i++) {
//		    Node n = nodes[i];
//		    double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
//		    if (dist < bestdist) {
//			pick = n;
//			bestdist = dist;
//		    }
//		}
//		pickfixed = pick.fixed;
//		pick.fixed = true;
//		pick.x = x;
//		pick.y = y;
//	
//		repaint();
//		e.consume();
    }

    public void mouseReleased(MouseEvent e) {
//        numMouseButtonsDown--;
//        removeMouseMotionListener(this);
//
//        pick.fixed = pickfixed;
//        pick.x = e.getX();
//        pick.y = e.getY();
//        if (numMouseButtonsDown == 0) {
//            pick = null;
//        }
//
//        repaint();
//        e.consume();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
//		pick.x = e.getX();
//		pick.y = e.getY();
//		repaint();
//		e.consume();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void start() {
		relaxer = new Thread(this);
		relaxer.start();
    }

    public void stop() {
    	relaxer = null;
    }

}

/**
 * 图形展示
 * @author zhangxinnan
 */
public class Graph extends Applet implements ActionListener, ItemListener {
    GraphPanel panel;
    Panel controlPanel;

    Button scramble = new Button("Scramble");
    Button shake = new Button("Shake");
    Checkbox stress = new Checkbox("Stress");
    Checkbox random = new Checkbox("Random");

    public void init() {
		setLayout(new BorderLayout());
	
		panel = new GraphPanel(this);
		add("Center", panel);
		controlPanel = new Panel();
		add("South", controlPanel);
	
		controlPanel.add(scramble); scramble.addActionListener(this);
		controlPanel.add(shake); shake.addActionListener(this);
		controlPanel.add(stress); stress.addItemListener(this);
		controlPanel.add(random); random.addItemListener(this);
	
		//String edges = getParameter("edges");
	//	String edges="路由1-设备1,路由1-设备2,路由1-设备3,路由1-设备4,路由2-设备5,路由2-设备6,路由2-设备7,路由2-设备8,路由2-设备9,路由2-设备10,路由2-设备11,路由2-设备12,路由2-设备13,路由2-设备14,路由2-设备15,路由2-设备16,路由2-设备17,路由2-设备18,路由2-设备19,路由2-设备20,服务器-路由1,服务器-路由2";
		String edges = "0-1,0-2,0-3,1-4,1-5,1-6,2-7,2-8,2-9,3-10,3-11,3-12";
		//String edges="x-a,x-b,a-c,a-f,b-d,b-e";//x-a,x-b,a-c,a-f,b-d,b-e
		for (StringTokenizer t = new StringTokenizer(edges, ",") ; t.hasMoreTokens() ; ) {
		    String str = t.nextToken();
		    int i = str.indexOf('-');
		    if (i > 0) {
		    	int len =50;
		    	int j = str.indexOf('/');
			if (j > 0) {
			    len = Integer.valueOf(str.substring(j+1)).intValue();
			    str = str.substring(0, j);
			}
			panel.addEdge(str.substring(0,i), str.substring(i+1), len);//用一个循环不停的调用..
		    }
		}
/*		Dimension d = getSize();
		//查找到父节点.把父节点设置成红色
		//String center = getParameter("center");
		String center="x";
		if (center != null){
		    Node n = panel.nodes[panel.findNode(center)];
		    n.x = d.width / 2;
		    n.y = d.height / 2;
		    n.fixed = true;
		}*/
		
		setVisible(true);
    }

    public void destroy() {
        remove(panel);
        remove(controlPanel);
    }

    public void start() {
    	panel.start();
    }

    public void stop() {
    	panel.stop();
    }

    public void actionPerformed(ActionEvent e) {
    	Object src = e.getSource();

		if (src == scramble) {
		    play(getCodeBase(), "audio/computer.au");
		    Dimension d = getSize();
		    for (int i = 0 ; i < panel.nnodes ; i++) {
			Node n = panel.nodes[i];
			if (!n.fixed) {
			    n.x = 10 + (d.width-20)*Math.random();
			    n.y = 10 + (d.height-20)*Math.random();
			}
		    }
		    return;
	}

		if (src == shake) {
		    play(getCodeBase(), "audio/gong.au");
		    Dimension d = getSize();
		    for (int i = 0 ; i < panel.nnodes ; i++) {
			Node n = panel.nodes[i];
			if (!n.fixed) {
			    n.x += 80*Math.random() - 40;
			    n.y += 80*Math.random() - 40;
			}
		    }
		}

    }

    public void itemStateChanged(ItemEvent e) {
		Object src = e.getSource();
		boolean on = e.getStateChange() == ItemEvent.SELECTED;
		if (src == stress) panel.stress = on;
		else if (src == random) panel.random = on;
    }

    public String getAppletInfo() {
    	return "Title: GraphLayout \nAuthor: <unknown>";
    }

    public String[][] getParameterInfo() {
		String[][] info = {
		    {"edges", "delimited string", "A comma-delimited list of all the edges.  It takes the form of 'C-N1,C-N2,C-N3,C-NX,N1-N2/M12,N2-N3/M23,N3-NX/M3X,...' where C is the name of center node (see 'center' parameter) and NX is a node attached to the center node.  For the edges connecting nodes to each other (and not to the center node) you may (optionally) specify a length MXY separated from the edge name by a forward slash."},
		    {"center", "string", "The name of the center node."}
		};
		return info;
    }
}