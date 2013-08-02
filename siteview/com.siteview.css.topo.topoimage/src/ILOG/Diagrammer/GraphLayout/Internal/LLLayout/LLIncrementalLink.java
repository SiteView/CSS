package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class LLIncrementalLink {
	private Boolean _fromFixed = false;

	private LLIncrementalNode _ifromNode;

	private LLIncrementalNode _itoNode;

	private java.lang.Object _linkObject;

	private InternalPoint _newFromPoint;

	private InternalPoint _newToPoint;

	private Integer _numPoints;

	private InternalPoint[] _points;

	private Boolean _toFixed = false;

	public LLIncrementalLink(LongLinkLayout layout, java.lang.Object link) {
		this._linkObject = link;
		this.Update(layout);
	}

	public void Clear() {
		if (this._numPoints > 0) {
			this._numPoints = -this._numPoints;
		}

	}

	public LLIncrementalNode GetFromNode() {

		return this._ifromNode;

	}

	public InternalPoint GetFromPoint() {

		return this._points[0];

	}

	public java.lang.Object GetLinkObject() {

		return this._linkObject;

	}

	public InternalPoint GetNewFromPoint() {

		return this._newFromPoint;

	}

	public InternalPoint GetNewToPoint() {

		return this._newToPoint;

	}

	public LLIncrementalNode GetToNode() {

		return this._itoNode;

	}

	public InternalPoint GetToPoint() {
		Integer num = (this._numPoints > 0) ? this._numPoints
				: -this._numPoints;

		return this._points[num - 1];

	}

	public Boolean IsFromPointFixed() {

		return this._fromFixed;

	}

	private Boolean IsHorizontal(InternalPoint p1, InternalPoint p2) {
		float num = p1.X - p2.X;
		float num2 = p1.Y - p2.Y;
		if (num < 0f) {
			num = -num;
		}
		if (num2 < 0f) {
			num2 = -num2;
		}

		return (num >= num2);

	}

	public Boolean IsToPointFixed() {

		return this._toFixed;

	}

	public Boolean NeedsReroute(IGraphModel model, LLGrid grid) {
		Integer num2 = null;
		LLIncrementalNode fromNode = this.GetFromNode();
		LLIncrementalNode toNode = this.GetToNode();
		java.lang.Object linkObject = this.GetLinkObject();
		if (fromNode == null) {

			return true;
		}
		if (toNode == null) {

			return true;
		}
		if (fromNode.GetNodeObject() != model.GetFrom(linkObject)) {

			return true;
		}
		if (toNode.GetNodeObject() != model.GetTo(linkObject)) {

			return true;
		}

		if (fromNode.NeedsReroute()) {

			return true;
		}

		if (toNode.NeedsReroute()) {

			return true;
		}
		InternalPoint[] linkPoints = GraphModelUtil.GetLinkPoints(model,
				linkObject);
		Integer length = linkPoints.length;
		if (length != this._numPoints) {

			return true;
		}
		for (num2 = 0; num2 < length; num2++) {

			if (!linkPoints[num2].equals(this._points[num2])) {

				return true;
			}
		}
		if (length < 2) {

			return true;
		}
		Integer[] limits = new Integer[2];
		for (num2 = 0; num2 < (length - 1); num2++) {
			Integer closestGridIndex = null;
			Integer num4 = null;
			InternalPoint point = linkPoints[num2];
			InternalPoint point2 = linkPoints[num2 + 1];
			float num5 = point.X - point2.X;
			float num6 = point.Y - point2.Y;
			if (num5 == 0f) {

				closestGridIndex = grid.GetClosestGridIndex(0, point.X);
				num5 = grid.GetGridCoord(0, closestGridIndex) - point.X;

				if (((((num2 > 0) && (num2 < (length - 2))) || ((num2 == 0) && !this
						.IsFromPointFixed())) || ((num2 == (length - 2)) && !this
						.IsToPointFixed()))
						&& ((num5 > 0.5f) || (num5 < -0.5f))) {

					return true;
				}

				limits[0] = grid.GetClosestGridIndex(1, point.Y);

				limits[1] = grid.GetClosestGridIndex(1, point2.Y);
				num4 = (limits[0] <= limits[1]) ? 1 : -1;
				if (num2 == 0) {
					InternalRect oldRect = fromNode.GetOldRect();

					while (grid.IsWithinRectObstacle(closestGridIndex,
							limits[0], oldRect)) {
						limits[0] += num4;
					}
				}
				if (num2 == (length - 2)) {
					InternalRect rect = toNode.GetOldRect();

					while (grid.IsWithinRectObstacle(closestGridIndex,
							limits[1], rect)) {
						limits[1] -= num4;
					}
				}

				if (!grid.IsWithoutObstacles(1, closestGridIndex, limits)) {

					return true;
				}
			} else {
				if (num6 != 0f) {

					return true;
				}

				closestGridIndex = grid.GetClosestGridIndex(1, point.Y);
				num6 = grid.GetGridCoord(1, closestGridIndex) - point.Y;

				if (((((num2 > 0) && (num2 < (length - 2))) || ((num2 == 0) && !this
						.IsFromPointFixed())) || ((num2 == (length - 2)) && !this
						.IsToPointFixed()))
						&& ((num6 > 0.5f) || (num6 < -0.5f))) {

					return true;
				}

				limits[0] = grid.GetClosestGridIndex(0, point.X);

				limits[1] = grid.GetClosestGridIndex(0, point2.X);
				num4 = (limits[0] <= limits[1]) ? 1 : -1;
				if (num2 == 0) {
					InternalRect rect3 = fromNode.GetOldRect();

					while (grid.IsWithinRectObstacle(limits[0],
							closestGridIndex, rect3)) {
						limits[0] += num4;
					}
				}
				if (num2 == (length - 2)) {
					InternalRect rect4 = toNode.GetOldRect();

					while (grid.IsWithinRectObstacle(limits[1],
							closestGridIndex, rect4)) {
						limits[1] -= num4;
					}
				}

				if (!grid.IsWithoutObstacles(0, closestGridIndex, limits)) {

					return true;
				}
			}
		}

		return false;

	}

	public void SetNewFromPoint(InternalPoint p) {
		this._newFromPoint = p;

	}

	public void SetNewToPoint(InternalPoint p) {
		this._newToPoint = p;

	}

	public void Update(LongLinkLayout layout) {
		IGraphModel graphModel = layout.GetGraphModel();
		LLIncrementalData incrementalData = layout.GetIncrementalData();
		java.lang.Object linkObject = this.GetLinkObject();

		this._points = GraphModelUtil.GetLinkPoints(graphModel, linkObject);
		this._numPoints = this._points.length;

		this._ifromNode = incrementalData.GetNodeData(graphModel
				.GetFrom(linkObject));

		this._itoNode = incrementalData.GetNodeData(graphModel
				.GetTo(linkObject));

		this._fromFixed = layout.IsFromPointFixed(linkObject);

		this._toFixed = layout.IsToPointFixed(linkObject);
		if (layout.get_IncrementalConnectionPreserving()) {

			this._newFromPoint = this.GetFromPoint();

			this._newToPoint = this.GetToPoint();
		} else {
			this._newFromPoint = null;
			this._newToPoint = null;
		}
		if (this._numPoints >= 2) {
			if (this._ifromNode != null) {
				this._ifromNode.UpdateLink(this, this._points[0], true,
						this.IsHorizontal(this._points[0], this._points[1]));
			}
			if (this._itoNode != null) {
				this._itoNode.UpdateLink(this,
						this._points[this._numPoints - 1], false, this
								.IsHorizontal(
										this._points[this._numPoints - 1],
										this._points[this._numPoints - 2]));
			}
		}

	}

}