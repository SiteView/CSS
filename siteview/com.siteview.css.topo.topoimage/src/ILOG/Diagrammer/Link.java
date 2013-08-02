package ILOG.Diagrammer;

import org.csstudio.opibuilder.model.ConnectionModel;

public class Link extends GraphicObject implements ILink {
	private Point2D endAnchor;
	private Point2D startAnchor;
	private Point2D endPoint;
	private Point2D startPoint;

	private ConnectionModel link;

	private GraphicObject source;
	private GraphicObject target;

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

	public void connect(GraphicObject source, GraphicObject target) {
		this.source = source;
		this.target = target;
		link.connect(source.getMode(), "BOTTOM", target.getMode(), "TOP");
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
