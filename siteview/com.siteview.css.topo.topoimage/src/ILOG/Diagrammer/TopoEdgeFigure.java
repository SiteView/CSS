package ILOG.Diagrammer;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.graphics.Color;

public class TopoEdgeFigure extends PolylineConnection {

	private double min = 0;
	private double max = 100;
	private final static Color RED_COLOR = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_RED);
	private final static Color GREEN_COLOR = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_GREEN);
	private Color thisColor = GREEN_COLOR;
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

	public TopoEdgeFigure() {
		Panel tooltip = new Panel();
		System.out.println("****************************************************************************************ConnectionFigure()");
		tooltip.setBackgroundColor(thisColor);
		tooltip.setSize(200,200);
		tooltip.add(new Label("ddd"));
		tooltip.add(new Label("kkdsflkdsf"));
		tooltip.setLayoutManager(new FlowLayout());
		add(new Label("个误会误会我我我"));
		setToolTip(tooltip);
	}
	public void changeBgColor(double value){
		if(value>50){
			this.thisColor = RED_COLOR;
		}else{
			this.thisColor = GREEN_COLOR;
		}
		repaint();
	}
	@Override
	public void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
	}
}
