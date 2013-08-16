package com.siteview.css.topo.figure;

import java.io.File;
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
import com.siteview.itsm.nnm.scan.core.snmp.util.IoUtils;

public class RouterFigure extends Figure {
	private Color thumbColor = WHITE_COLOR;
	private boolean effect3D = true;

	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance()
			.getColor(CustomMediaFactory.COLOR_WHITE);
	private final static Color GREEN_COLOR = CustomMediaFactory.getInstance()
			.getColor(CustomMediaFactory.COLOR_GREEN);

	private final static Color RED_COLOR = CustomMediaFactory.getInstance()
			.getColor(CustomMediaFactory.COLOR_RED);
	
	
	private final static Color s_light_creen = new Color(null,98,182,54);
	private final static Color light_creen = new Color(null,29,220,1);
	private final static Color COLOR_GREEN =CustomMediaFactory.getInstance()
			.getColor(CustomMediaFactory.COLOR_GREEN);
	public static String SYSOID = "";
	
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

		//String filename = "D://panel//1.3.6.1.4.1.3320.1.116.0.vdx";
		String filename = IoUtils.getProductPath() + "panel" + File.separator + SYSOID +".vdx";
//		String filename = IoUtils.getPlatformPath()+"panel/" + SYSOID + ".vdx";	
		if (!new File(filename).exists()) {
			throw new RuntimeException("visio文件不存在！");
		}
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

	@Override
	public void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		//获取当前编辑区域的坐标或者size
		Rectangle bound = getBounds().getCopy();

		//当前项目中获取image的实体类 方法
		final Bundle bundle = Platform.getBundle("com.siteview.css.topo");
		final URL url = bundle.getEntry("icons/" + SYSOID + ".png");
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
		
		if (value > 80) {
			for (int i = 0; i <= ports.size(); i++) {
				portColors.put(String.valueOf(i), s_light_creen);
			}
			for (int i = 0; i <= lamps.size(); i++) {
				lampColors.put(String.valueOf(i), s_light_creen);
			}
			
		} else if (value > 60) {
			for (int i = 0; i <= ports.size(); i++) {
				portColors.put(String.valueOf(i), light_creen);
			}
			for (int i = 0; i <= lamps.size(); i++) {
				lampColors.put(String.valueOf(i), light_creen);
			}
			
		} else if (value > 40){
			for (int i = 0; i <= ports.size(); i++) {
				portColors.put(String.valueOf(i), s_light_creen);
			}
			for (int i = 0; i <= lamps.size(); i++) {
				lampColors.put(String.valueOf(i), s_light_creen);
			}
		}else if (value > 20){
			for (int i = 0; i <= ports.size(); i++) {
				portColors.put(String.valueOf(i), light_creen);
			}
			for (int i = 0; i <= lamps.size(); i++) {
				lampColors.put(String.valueOf(i), light_creen);
			}
		}else{
			for (int i = 0; i <= ports.size(); i++) {
				portColors.put(String.valueOf(i), COLOR_GREEN);
			}
			for (int i = 0; i <= lamps.size(); i++) {
				lampColors.put(String.valueOf(i), COLOR_GREEN);
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
