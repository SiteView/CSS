package com.siteview.css.topo.figure;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.siteview.css.topo.pojo.DataFromVisio;
import com.siteview.css.topo.pojo.Lamp;
import com.siteview.css.topo.pojo.Port;

public class RouterFigure extends Figure {
	private Color thumbColor = WHITE_COLOR;
	private boolean effect3D = true;

	private final static Color BLUE_COLOR = CustomMediaFactory.getInstance()
			.getColor(CustomMediaFactory.COLOR_BLUE);
	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance()
			.getColor(CustomMediaFactory.COLOR_WHITE);
	private final static Color GRAY_COLOR = CustomMediaFactory.getInstance()
			.getColor(CustomMediaFactory.COLOR_GRAY);
	private final static Color GREEN_COLOR = CustomMediaFactory.getInstance()
			.getColor(CustomMediaFactory.COLOR_GREEN);

	private final static Color RED_COLOR = CustomMediaFactory.getInstance()
			.getColor(CustomMediaFactory.COLOR_RED);
	private XYLayout layout;
	Dimension labelSize;
	Port port = null;
	Lamp lamp = null;
	Point p = null;
	// List<PortFigure> portFigures = new ArrayList<PortFigure>();
	// List<LampFigure> LampFigures = new ArrayList<LampFigure>();

	Map<String, PortRec> portMaps = new HashMap<String, PortRec>();
	Map<String, Color> portColors = new HashMap<String, Color>();

	Map<String, LampRec> lampMaps = new HashMap<String, LampRec>();
	Map<String, Color> lampColors = new HashMap<String, Color>();

	public List<Port> ports = new ArrayList<Port>();
	public List<Lamp> lamps = new ArrayList<Lamp>();
	public RouterFigure() {
		super();

		// add by yuzhenbiao
		layout = new XYLayout();

		DataFromVisio dfv = new DataFromVisio();

		String filename = "D://panel//1.3.6.1.4.1.9.1.516.vdx";
		dfv.getCoordinatesForXML(filename);
		ports = dfv.ports;
		lamps = dfv.lamps;

		for (int i = 0; i < ports.size(); i++) {
			p = new Point();
			port = new Port();
			port = ports.get(i);
			setLayoutManager(layout);

			PortRec pf = new PortRec();
			pf.setId(port.getId());
			pf.setSize((int) port.getWidth(), (int) port.getHeigth());
			p.setX((int) port.getP().getX());
			p.setY((int) port.getP().getY());
			pf.setLocation(p);

			portMaps.put(String.valueOf(pf.getId()), pf);
			portColors.put(String.valueOf(pf.getId()), GREEN_COLOR);

			Rectangle r = new Rectangle(0, 0, 0, 0);
			r.setX((int) port.getP().getX());
			r.setY((int) port.getP().getY());
			r.setWidth((int) port.getWidth());
			r.setHeight((int) port.getHeigth());

			add(pf);
			setConstraint(pf, r);
		}

		for (int i = 0; i < lamps.size(); i++) {
			lamp = new Lamp();
			p = new Point();
			lamp = lamps.get(i);

			setLayoutManager(layout);

			// LampFigure lf = new LampFigure();
			LampRec lf = new LampRec();

			lf.setId(lamp.getId());
			lf.setSize((int) lamp.getWidth(), (int) lamp.getHeigth());
			p.setX((int) lamp.getP().getX());
			p.setY((int) lamp.getP().getY());
			lf.setLocation(p);

			lampMaps.put(String.valueOf(lf.getId()), lf);
			lampColors.put(String.valueOf(lf.getId()), GREEN_COLOR);

			Rectangle r = new Rectangle(0, 0, 0, 0);
			r.setX((int) lamp.getP().getX());
			r.setY((int) lamp.getP().getY());
			r.setWidth((int) lamp.getWidth());
			r.setHeight((int) lamp.getHeigth());

			add(lf);
			setConstraint(lf, r);

		}

		// setBackgroundColor(WHITE_COLOR);
		// setBorder(new LineBorder(5));

	}

	/*
	 * protected void paintClientArea(Graphics graphics) {
	 * super.paintClientArea(graphics);
	 * 
	 * graphics.setBackgroundColor(GREEN_COLOR);
	 * graphics.setForegroundColor(RED_COLOR); // add by yuzhenbiao 加载图片 final
	 * Bundle bundle = Platform .getBundle("com.siteview.css.topo"); final URL
	 * url = bundle .getEntry("icons/1.3.6.1.4.1.2011.2.62.2.2.png"); Image
	 * image = ImageDescriptor.createFromURL(url) .createImage();
	 * System.out.println(image.getImageData().width + " " +
	 * image.getImageData().height); graphics.setBackgroundPattern(new
	 * Pattern(null, image));
	 * 
	 * //graphics.fillRectangle(bounds);
	 * graphics.setForegroundColor(thumbColor);
	 * 
	 * Image image = new Image(Display.getCurrent(), bounds.width,
	 * bounds.height); GC gc = SingleSourceHelper.getImageGC(image);
	 * 
	 * // Point p1 = new Point( bounds.x/0.8, (int)
	 * ((55.0/95.0)*bounds.height)); // // Point p2 = new Point(p1.x + (int)
	 * ((23.0* 1.5/95.0)*bounds.height), // p1.y + (int)
	 * ((23.0/95.0)*bounds.height)); // Rectangle smallEndBounds =
	 * bar.getSmallEndBounds(); // int[] echelonShadow = new int[]{ // p1.x,
	 * p1.y, p2.x, p2.y, // smallEndBounds.x + smallEndBounds.width,
	 * smallEndBounds.y + smallEndBounds.height/2, // smallEndBounds.x+
	 * smallEndBounds.width/2, smallEndBounds.y }; gc.setBackground(GRAY_COLOR);
	 * 
	 * gc.fillRectangle(image.getBounds()); Transform tr = new
	 * Transform(Display.getCurrent()); tr.translate(-bounds.x,
	 * -bounds.y/1.05f); //gc.setTransform(tr);
	 * SingleSourceHelper.setGCTransform(gc, tr);
	 * //gc.setBackground(BLACK_COLOR); //gc.fillPolygon(echelonShadow);
	 * 
	 * 
	 * }
	 */

	@Override
	public void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		//获取当前编辑区域的坐标或者size
		Rectangle bound = getBounds().getCopy();

		//当前项目中获取image的实体类 方法
		final Bundle bundle = Platform.getBundle("com.siteview.css.topo");
		final URL url = bundle.getEntry("icons/1.3.6.1.4.1.9.1.516.png");
		Image image = ImageDescriptor.createFromURL(url).createImage();
		System.out.println(image.getImageData().width + " "
				+ image.getImageData().height);
//		graphics.setBackgroundPattern(new Pattern(Display.getCurrent(), image));
		//把图片填充到编辑区域中
		graphics.drawImage(image,bound.x,bound.y+1); 
		
		graphics.setForegroundColor(thumbColor);

	}

	class PortRec extends RectangleFigure {

		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public PortRec() {

		}

		@Override
		protected void fillShape(Graphics graphics) {
			graphics.setAntialias(SWT.ON);
			graphics.setBackgroundColor(thumbColor);
			boolean support3D = GraphicsUtil.testPatternSupported(graphics);
			if (support3D && effect3D) {
				try {
					graphics.setBackgroundColor(portColors.get(String
							.valueOf(this.getId())));
					graphics.setForegroundColor(RED_COLOR);
					super.fillShape(graphics);
				} catch (Exception e) {
					support3D = false;
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
			}
			super.fillShape(graphics);

			graphics.setForegroundColor(thumbColor);
		}

	}

	public void changeBackGroundColor(double value) {
		System.out.println("*****************change color*************");
		
		if (value > 50) {
			for (int i = 1; i <= ports.size(); i++) {
				portColors.put(String.valueOf(i), BLUE_COLOR);
				lampColors.put(String.valueOf(i), BLUE_COLOR);
			}
			
		} else if (value > 30) {
			for (int i = 1; i <= ports.size(); i++) {
				portColors.put(String.valueOf(i), GRAY_COLOR);
				lampColors.put(String.valueOf(i), GRAY_COLOR);
			}
			
		} else {
			for (int i = 1; i <= ports.size(); i++) {
				portColors.put(String.valueOf(i), RED_COLOR);
				lampColors.put(String.valueOf(i), RED_COLOR);
			}
		}
		repaint();
	}

	class LampRec extends RectangleFigure {

		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public LampRec() {

		}

		@Override
		protected void fillShape(Graphics graphics) {
			graphics.setAntialias(SWT.ON);
			graphics.setBackgroundColor(thumbColor);
			boolean support3D = GraphicsUtil.testPatternSupported(graphics);
			if (support3D && effect3D) {
				try {
					graphics.setBackgroundColor(lampColors.get(String
							.valueOf(this.getId())));
					graphics.setForegroundColor(RED_COLOR);
					super.fillShape(graphics);
				} catch (Exception e) {
					support3D = false;
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
			}
			super.fillShape(graphics);

			graphics.setForegroundColor(thumbColor);
		}

	}

}
