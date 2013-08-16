package com.siteview.itsm.nnm.widgets.figure;

import java.util.List;

import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.ui.util.CustomMediaFactory;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;

import com.siteview.itsm.nnm.scan.core.snmp.pojo.Edge;
import com.siteview.itsm.nnm.widgets.factory.ImageFactory;


/**
 * pc
 * @author Administrator
 *
 */
public class TopologyFigure extends Figure{

	private double min = 0;
	private double max = 100;
	private double value = 50;
	private Label valueLabel;
	private final static double SPACE_ANGLE = 45;
	public final static double ALPHA = SPACE_ANGLE * Math.PI/180;  
	private Color thumbColor = WHITE_COLOR;
	private boolean effect3D = true;
	public final static double HW_RATIO = (1- Math.sin(ALPHA)/2)/(2*Math.cos(ALPHA));

	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	private final static Color GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_GRAY);
	private final static Color GREEN_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_GREEN);
	
	private final static Color RED_COLOR = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_RED);
	Dimension labelSize;
	private Color color = GREEN_COLOR;
	public TopologyFigure(List<Edge> edges){
		this();
		//初始化数据
	}
	public TopologyFigure(){
		super();
		
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
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		//获取当前编辑区域的坐标或者size
		Rectangle bound = getBounds().getCopy();
		//判断是哪个模型调用的这个Figure
		
		//把图片填充到编辑区域中
		graphics.drawImage(ImageFactory.getPcBlueImage(),bound.x,bound.y); 
		
		graphics.setForegroundColor(thumbColor);
		
	}
	
	public void changeBackGroundColor(double value){
		if(value>50){
			this.color = RED_COLOR;
		}else if(value >30){
			this.color = WHITE_COLOR;
		}else{
			this.color = GRAY_COLOR;
		}
		repaint();
	}
	class MyRectangle extends RectangleFigure{
		
		public MyRectangle(){
		}
		@SuppressWarnings("null")
		@Override
		protected void fillShape(Graphics graphics) {
			graphics.setAntialias(SWT.ON);
			Pattern pattern = null;
			graphics.setBackgroundColor(thumbColor);
			boolean support3D = GraphicsUtil.testPatternSupported(graphics);
			if(support3D && effect3D){
				try {
					graphics.setBackgroundColor(color);
					super.fillShape(graphics);
				} catch (Exception e) {
					support3D = false;
					e.printStackTrace();
					System.out.println(e.getMessage());
					pattern.dispose();
				}				
			}			
			super.fillShape(graphics);
			graphics.setForegroundColor(thumbColor);
		}		
	}
	public void setValue(double value) {
		this.value = value;
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
			
			area.width = Math.min(area.width, area.height);
			area.height = area.width;
			computeContain = new Rectangle(area.x,area.y,100,100);
			if(rec != null){
				rec.setBounds(computeContain);
			}
			if(valueLabel != null && valueLabel.isVisible()) {
				valueLabel.setVisible(true);
								
				valueLabel.setBounds(new Rectangle(area.x + (area.width -labelSize.width)/2,area.y+area.height-labelSize.height,
						labelSize.width, labelSize.height));
			}
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
	@SuppressWarnings("deprecation")
	public void setXYPoint(Double x,Double y){
		setXYPoint(new Point(x, y));
	}
	
}