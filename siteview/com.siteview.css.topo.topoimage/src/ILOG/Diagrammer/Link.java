package ILOG.Diagrammer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.widgets.model.ImageModel;

public class Link extends GraphicObject implements ILink {
	private Point2D endAnchor;
	private Point2D startAnchor;
	private Point2D endPoint;
	private Point2D startPoint;

	private ConnectionModel link;

	private GraphicObject source;
	private GraphicObject target;

	// 锚点判断
	private int x;
	private int y;
	private int x1;
	private int y1;

	// 锚点连接
	final String TOP_LEFT = "TOP_LEFT";
	final String TOP = "TOP";
	final String TOP_RIGHT = "TOP_RIGHT";
	final String LEFT = "LEFT";
	final String RIGHT = "RIGHT";
	final String BOTTOM_LEFT = "BOTTOM_LEFT";
	final String BOTTOM = "BOTTOM";
	final String BOTTOM_RIGHT = "BOTTOM_RIGHT";

	// 模型

	public Link(GraphicContainer container, String id) {
		super(container, id);
		link = new ConnectionModel(container.getDisplayModel());
	}

	// public void SetSource(GraphicObject source){
	// this.source = source;
	// link.setSource(source.getMode());
	// }
	//
	// public void setTarget(GraphicObject target){
	// this.target = target;
	// link.setTarget(target.getMode());
	// }
	/**
	 * 连接模型
	 * 
	 * @param source
	 * @param target
	 */

	public void connect(GraphicObject source, GraphicObject target) {
		this.source = source;
		this.target = target;
		// link.connect(source.getMode(),"BOTTOM", target.getMode(), "TOP");
	}

	public void connect(int modeWidth, GraphicObject source,
			GraphicObject target) {
		this.source = source;
		this.target = target;

		String xy = "";
		String x1y1 = "";
		x = source.getMode().getX();
		y = source.getMode().getY();
		x1 = target.getMode().getX();
		y1 = target.getMode().getY();
		// 返回连接的锚点
		if (x - x1 <= modeWidth && x - x1 >= -modeWidth) {
			if (y - y1 > 0) {
				xy = TOP;
				x1y1 = BOTTOM;
			}
			if (y - y1 < 0) {
				xy = BOTTOM;
				x1y1 = TOP;
			}
		}
		if (y - y1 <= modeWidth && y - y1 >= -modeWidth) {
			if (x - x1 > 0) {
				xy = LEFT;
				x1y1 = RIGHT;
			}
			if (x - x1 < 0) {
				xy = RIGHT;
				x1y1 = LEFT;
			}
		}
		if (x - x1 > modeWidth && y - y1 > modeWidth) {
			xy = TOP_LEFT;
			x1y1 = BOTTOM_RIGHT;
		}
		if (x - x1 > modeWidth && y - y1 < -modeWidth) {
			xy = BOTTOM_LEFT;
			x1y1 = TOP_RIGHT;
		}
		if (x - x1 < -modeWidth && y - y1 > modeWidth) {
			xy = TOP_RIGHT;
			x1y1 = BOTTOM_LEFT;
		}
		if (x - x1 < -modeWidth && y - y1 < -modeWidth) {
			xy = BOTTOM_RIGHT;
			x1y1 = TOP_LEFT;
		}
		link.connect(source.getMode(), xy, target.getMode(), x1y1);
	}

	public ConnectionModel getLink() {
		return link;
	}

	public void setLink(ConnectionModel link) {
		this.link = link;
	}

	public GraphicObject getSource() {
		return source;
	}

	public GraphicObject getTarget() {
		return target;
	}

	@Override
	public Point2D getEndAnchor() {
		return endAnchor;
	}

	@Override
	public void setEndAnchor(Point2D anchor) {

		endAnchor = anchor;
	}

	@Override
	public Point2D getStartAnchor() {
		return startAnchor;
	}

	@Override
	public void setStartAnchor(Point2D anchor) {
		startAnchor = anchor;
	}

	@Override
	public Point2D getEndPoint() {
		return endPoint;
	}

	@Override
	public void setEndPoint(Point2D point) {
		endPoint = point;
	}

	@Override
	public Point2D getStartPoint() {
		return startPoint;
	}

	@Override
	public void setStartPoint(Point2D point) {
		startPoint = point;
	}

	@Override
	public boolean isNode() {
		return false;
	}

	@Override
	public boolean isLink() {
		return true;
	}

	@Override
	public void transform(float x, float y) {

	}

}
