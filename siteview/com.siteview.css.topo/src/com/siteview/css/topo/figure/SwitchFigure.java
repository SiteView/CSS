package com.siteview.css.topo.figure;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.widgets.figureparts.RoundScale;
import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Pattern;
import org.osgi.framework.Bundle;

import com.siteview.snmp.pojo.Edge;

/**
 * pc
 * @author Administrator
 *
 */
public class SwitchFigure extends Figure{

	private final static Font DEFAULT_LABEL_FONT = CustomMediaFactory.getInstance().getFont(
			new FontData("Arial", 12, SWT.BOLD));	

	private double min = 0;
	private double max = 100;
	private double value = 50;
	private Label valueLabel;
	private List<Rectangle> topoNodes = new ArrayList<Rectangle>();
	private List<Edge> edges = new ArrayList<Edge>();
	private final static double SPACE_ANGLE = 45;
	private MyRectangle rec;
	public final static double ALPHA = SPACE_ANGLE * Math.PI/180;  
	private Color thumbColor = WHITE_COLOR;
	private boolean effect3D = true;
	public final static double HW_RATIO = (1- Math.sin(ALPHA)/2)/(2*Math.cos(ALPHA));

	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	private final static Color GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_GRAY);
	private final static Color BLUE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_GREEN);
	
	private final static Color RED_COLOR = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_RED);
	Dimension labelSize;
	private Color color = BLUE_COLOR;
	public SwitchFigure(List<Edge> edges){
		this();
		//初始化数据
	}
	public SwitchFigure(){
		super();
		
//		valueLabel = new Label();		
//		valueLabel.setFont(DEFAULT_LABEL_FONT);
//		valueLabel.setText("IP:19.168.3.90");
//		valueLabel.setForegroundColor(RED_COLOR);
//		rec = new MyRectangle();
//		labelSize = valueLabel.getPreferredSize();
//		//���ò���
//		setLayoutManager(new TopologyLayout());
//		add(valueLabel, TopologyLayout.VALUE_LABEL);
//		add(rec,TopologyLayout.REC);
		
		XYLayout layout = new XYLayout();
		setLayoutManager(layout);
		
		MyRectangle mr = new MyRectangle();
		Rectangle r = new Rectangle();
		add(mr);
		setConstraint(mr, r);
		
	}
	@Override
	protected void paintClientArea(Graphics graphics) {
		super.paintClientArea(graphics);
		if(!isEnabled()) {
			graphics.setBackgroundColor(GRAY_COLOR);
			graphics.fillRectangle(bounds);
		}		
//		graphics.setBackgroundColor(RED_COLOR);
//		graphics.fillRectangle(getClientArea());
//
//		graphics.setBackgroundColor(BLUE_COLOR);
//
//		double coercedValue = value;
//		if (value < min)
//			coercedValue = min;
//		else if (value > max)
//			coercedValue = max;
//		int valueLength = (int) ((coercedValue - min) * getClientArea().height / (max - min));
//		graphics.fillRectangle(getClientArea().x, getClientArea().y
//				+ getClientArea().height - valueLength, getClientArea().width,
//				valueLength);
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		//获取当前编辑区域的坐标或者size
		Rectangle bound = getBounds().getCopy();

		//当前项目中获取image的实体类 方法
		final Bundle bundle = Platform.getBundle("com.siteview.css.topo");
		final URL url = bundle.getEntry("icons/bmp_Switch_Blue.bmp");
		Image image = ImageDescriptor.createFromURL(url).createImage();
//		System.out.println(image.getImageData().width + " "
//				+ image.getImageData().height);
//		graphics.setBackgroundPattern(new Pattern(Display.getCurrent(), image));
		//把图片填充到编辑区域中
		graphics.drawImage(image,bound.x,bound.y); 
		
		graphics.setForegroundColor(thumbColor);
		
	}
	
	public void changeBackGroundColor(double value){
		if(value>50){
			this.color = RED_COLOR;//rec.setForegroundColor(RED_COLOR);
		}else if(value >30){
			this.color = WHITE_COLOR;//rec.setForegroundColor(BLUE_COLOR);
		}else{
			this.color = GRAY_COLOR;//rec.setForegroundColor(GRAY_COLOR);
		}
		repaint();
	}
	class MyRectangle extends RectangleFigure{
		
		public MyRectangle(){
		}
		@Override
		protected void fillShape(Graphics graphics) {
			graphics.setAntialias(SWT.ON);
			Pattern pattern = null;
			graphics.setBackgroundColor(thumbColor);
			boolean support3D = GraphicsUtil.testPatternSupported(graphics);
			if(support3D && effect3D){
				try {
					graphics.setBackgroundColor(color);
					//graphics.setForegroundColor(RED_COLOR);
					
//					Rectangle bound = getBounds().getCopy();
//
//					//当前项目中获取image的实体类 方法
//					final Bundle bundle = Platform.getBundle("com.siteview.css.topo");
//					final URL url = bundle.getEntry("icons/bmp_PC_Blue.bmp");
//					Image image = ImageDescriptor.createFromURL(url).createImage();
//					System.out.println(image.getImageData().width + " "
//							+ image.getImageData().height);
//					graphics.setBackgroundPattern(new Pattern(Display.getCurrent(), image));
//					//把图片填充到编辑区域中
//					graphics.drawImage(image,bound.x,bound.y); 
					
					
					super.fillShape(graphics);
//					pattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), bounds.x, bounds.y,
//							bounds.x + bounds.width, bounds.y + bounds.height, 
//							RED_COLOR, 0, RED_COLOR, 255);
//					graphics.setBackgroundPattern(pattern);
				} catch (Exception e) {
					support3D = false;
					e.printStackTrace();
					System.out.println(e.getMessage());
					pattern.dispose();
				}				
			}			
			super.fillShape(graphics);
//			if(effect3D && support3D)
//				pattern.dispose();
			graphics.setForegroundColor(thumbColor);
		}		
	}
	public void setValue(double value) {
		this.value = value;
//		valueLabel.setText(String.valueOf(value));	
		repaint();
	}

	public double getValue() {
		return value;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}
	
	
	public void setValueLabelVisibility(boolean visible) {
		valueLabel.setVisible(visible);
	}
		
		
		
	public Rectangle computeContain ;
	class TopologyLayout extends AbstractLayout{
		
	
		private static final int GAP_BTW_BULB_SCALE = 4;
	
		/** Used as a constraint for the scale. */
		public static final String SCALE = "scale";   //$NON-NLS-1$
		/** Used as a constraint for the bulb. */
		public static final String BULB = "bulb"; //$NON-NLS-1$
		/** Used as a constraint for the Ramp */
		public static final String REC = "rec";      //$NON-NLS-1$
		/** Used as a constraint for the thumb */
		public static final String THUMB = "thumb";      //$NON-NLS-1$
		/** Used as a constraint for the value label*/
		public static final String VALUE_LABEL = "valueLabel";      //$NON-NLS-1$
		
		private RoundScale scale;
	//	private RoundScaledRamp ramp;
	//	private Bulb bulb;
	//	private Thumb thumb;
		private Label valueLabel;
		private MyRectangle rec;
	
	
		@Override
		protected Dimension calculatePreferredSize(IFigure container, int w,
				int h) {
			Insets insets = container.getInsets();
			Dimension d = new Dimension(256, 256);
			d.expand(insets.getWidth(), insets.getHeight());
			return d;
		}

		public void layout(IFigure container) {
			
			
			Rectangle area = container.getClientArea();			
			Point centerP = area.getCenter();
			
			area.width = Math.min(area.width, area.height);
			area.height = area.width;
	//		area.shrink(BORDER_WIDTH, BORDER_WIDTH);
			
			//Point center = area.getCenter();
			Rectangle bulbBounds = null;
			computeContain = new Rectangle(area.x,area.y,100,100);
			if(rec != null){
				rec.setBounds(computeContain);
			}
			
//			if(scale != null) {				
	//			scale.setBounds(area);
//				bulbBounds = area.getCopy();
//				bulbBounds.shrink(area.width/2 - scale.getInnerRadius() + GAP_BTW_BULB_SCALE, 
//						area.height/2 - scale.getInnerRadius() + GAP_BTW_BULB_SCALE);
//			}
	//		
	//		if(scale != null && ramp != null && ramp.isVisible()) {
	//			Rectangle rampBounds = area.getCopy();
	//			ramp.setBounds(rampBounds.shrink(area.width/2 - scale.getInnerRadius() - ramp.getRampWidth()+2,
	//					area.height/2 - scale.getInnerRadius() - ramp.getRampWidth()+2));
	//		}
			
			if(valueLabel != null && valueLabel.isVisible()) {
				valueLabel.setVisible(true);
								
				valueLabel.setBounds(new Rectangle(area.x + (area.width -labelSize.width)/2,area.y+area.height-labelSize.height,
						labelSize.width, labelSize.height));
			}
			
	//		if(bulb != null && scale != null && bulb.isVisible()) {				
	//			bulb.setBounds(bulbBounds);				
	//		}
	//		
	//		if(scale != null && thumb != null && thumb.isVisible()){
	//			Point thumbCenter = new Point(bulbBounds.x + bulbBounds.width*7.0/8.0, 
	//					bulbBounds.y + bulbBounds.height/2);
	//			double valuePosition = 360 - scale.getValuePosition(getCoercedValue(), false);				
	//			thumbCenter = PointsUtil.rotate(thumbCenter,	valuePosition, center);
	//			int thumbDiameter = bulbBounds.width/6;
	//			
	//			thumb.setBounds(new Rectangle(thumbCenter.x - thumbDiameter/2,
	//					thumbCenter.y - thumbDiameter/2,
	//					thumbDiameter, thumbDiameter));
	//		}						
		}


		@Override
		public void setConstraint(IFigure child, Object constraint) {
			if (constraint.equals(VALUE_LABEL))
				valueLabel = (Label)child;
			else if(constraint.equals(REC)){
				rec = (MyRectangle)child;
			}
		}		
	}
	public void setLabelValue(String value){
		this.valueLabel.setText(value);
	}
	public void setXYPoint(Point p){
		this.valueLabel.setLocation(p);
		
	}
	public void setXYPoint(Double x,Double y){
		setXYPoint(new Point(x, y));
	}
	
}
