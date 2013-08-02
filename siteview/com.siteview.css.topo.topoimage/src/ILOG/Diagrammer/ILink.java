package ILOG.Diagrammer;

public interface ILink {
	public Point2D getEndAnchor();

	public void setEndAnchor(Point2D anchor);

	public Point2D getStartAnchor();

	public void setStartAnchor(Point2D anchor);

	public Point2D getEndPoint();

	public void setEndPoint(Point2D point);

	public Point2D getStartPoint();

	public void setStartPoint(Point2D point);

}
