package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import system.Collections.ArrayList;
import ILOG.Diagrammer.GraphLayout.IGraphModel;
import ILOG.Diagrammer.GraphLayout.LinkLayoutLinkStyle;
import ILOG.Diagrammer.GraphLayout.LongLinkLayout;
import ILOG.Diagrammer.GraphLayout.Internal.FixedPoint;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelUtil;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;

public final class LLLink {
	private Integer _actEndPointIndex;

	private Integer _actStartPointIndex;

	private Integer[] _bbox;

	private Boolean _direct = false;

	private LLTerminationPoint[] _endPoints;

	private java.lang.Object _fromNode;

	private InternalPoint _fromPoint;

	private InternalRect _fromRect;

	private Boolean _frozen = false;

	private LLGraph _graph;

	private float[] _gridBaseCoord;

	private java.lang.Object _linkObject;

	private Boolean[] _nextObstacle;

	private Integer _preferedSide;

	private Boolean[] _prevObstacle;

	private Integer[] _segments;

	private double _sortValue;

	private Integer _startDir;

	private LLTerminationPoint[] _startPoints;

	private java.lang.Object _toNode;

	private InternalPoint _toPoint;

	private InternalRect _toRect;

	public LLLink(LLGraph graph, java.lang.Object link) {
		this._graph = graph;
		this._linkObject = link;
		IGraphModel model = graph.GetModel();
		LongLinkLayout layout = graph.GetLayout();

		this._fromNode = model.GetFrom(link);

		this._toNode = model.GetTo(link);

		this._fromRect = layout.GetNodeBoxForConnection(this._fromNode);

		this._toRect = layout.GetNodeBoxForConnection(this._toNode);
		Boolean flag = layout.IsFromPointFixed(link);
		Boolean flag2 = layout.IsToPointFixed(link);
		if (flag || flag2) {
			InternalPoint[] linkPoints = GraphModelUtil.GetLinkPoints(model,
					link);
			if (flag) {
				this._fromPoint = linkPoints[0];
			} else {
				this._fromPoint = null;
			}
			if (flag2) {
				this._toPoint = linkPoints[linkPoints.length - 1];
			} else {
				this._toPoint = null;
			}
		}
		this._startPoints = null;
		this._endPoints = null;
		Integer linkStyle = (Integer) layout.get_LinkStyle();
		this._direct = (linkStyle == 4)
				|| ((linkStyle == 0x63) && (layout.GetLinkStyle(link) == LinkLayoutLinkStyle.Direct));

		if (this.IsSelfLoop()) {
			this._direct = false;
		}
		this._segments = null;
		this._prevObstacle = null;
		this._nextObstacle = null;
		this._gridBaseCoord = new float[] { 0f, 0f };
		this._bbox = null;
		this._frozen = false;
		this._preferedSide = 0;
		this._sortValue = 0.0;
	}

	public void CalcRealEndPointOnGrid(Integer[] p) {
		float x = 0;
		Integer num2 = null;
		LLGrid grid = this.GetGraph().GetGrid();
		LLTerminationPoint actEndPoint = this.GetActEndPoint();
		LLGridSegment segment = actEndPoint.GetSegment();
		InternalPoint toPoint = this.GetToPoint();
		Boolean flag = this.GetSegmentEndDir() == 0;
		if ((segment != null) && (segment.GetGridLine() != null)) {

			flag = segment.IsHorizontal();
		}
		if (flag) {
			if (toPoint == null) {
				x = this._toRect.X + (0.5f * this._toRect.Width);
			} else {
				x = toPoint.X;
			}

			p[1] = actEndPoint.GetIndex(1);
			num2 = 0;
		} else {
			if (toPoint == null) {
				x = this._toRect.Y + (0.5f * this._toRect.Height);
			} else {
				x = toPoint.Y;
			}

			p[0] = actEndPoint.GetIndex(0);
			num2 = 1;
		}
		float num3 = this._gridBaseCoord[num2] - grid.GetMinCoord(num2);
		float num4 = grid.GetGridCoord(num2, actEndPoint.GetIndex(num2)) + num3;

		p[num2] = grid.GetGridIndex(num2, x - num3, num4 >= x);

	}

	public void CalcRealStartPointOnGrid(Integer[] p) {
		float x = 0;
		Integer num2 = null;
		LLGrid grid = this.GetGraph().GetGrid();
		LLTerminationPoint actStartPoint = this.GetActStartPoint();
		LLGridSegment segment = actStartPoint.GetSegment();
		InternalPoint fromPoint = this.GetFromPoint();
		Boolean flag = this.GetSegmentStartDir() == 0;
		if ((segment != null) && (segment.GetGridLine() != null)) {

			flag = segment.IsHorizontal();
		}
		if (flag) {
			if (fromPoint == null) {
				x = this._fromRect.X + (0.5f * this._fromRect.Width);
			} else {
				x = fromPoint.X;
			}

			p[1] = actStartPoint.GetIndex(1);
			num2 = 0;
		} else {
			if (fromPoint == null) {
				x = this._fromRect.Y + (0.5f * this._fromRect.Height);
			} else {
				x = fromPoint.Y;
			}

			p[0] = actStartPoint.GetIndex(0);
			num2 = 1;
		}
		float num3 = this._gridBaseCoord[num2] - grid.GetMinCoord(num2);
		float num4 = grid.GetGridCoord(num2, actStartPoint.GetIndex(num2))
				+ num3;

		p[num2] = grid.GetGridIndex(num2, x - num3, num4 >= x);

	}

	public void CleanRoute() {
		this._segments = null;
		this._prevObstacle = null;
		this._nextObstacle = null;
		this.MarkLinkChanged();

	}

	private void CleanUnusedStartAndEndPoints() {
		LLTerminationPoint[] pointArray = new LLTerminationPoint[] { this
				.GetActStartPoint() };
		this._actStartPointIndex = 0;
		this._startPoints = pointArray;
		pointArray = new LLTerminationPoint[] { this.GetActEndPoint() };
		this._actEndPointIndex = 0;
		this._endPoints = pointArray;

	}

	public void Dispose() {
		this._fromPoint = null;
		this._toPoint = null;
		this._startPoints = null;
		this._endPoints = null;
		this._segments = null;
		this._prevObstacle = null;
		this._nextObstacle = null;
		this._bbox = null;
		this._graph = null;
		this._linkObject = null;
		this._fromNode = null;
		this._toNode = null;
		this._gridBaseCoord = null;

	}

	public void DrawLinkForAnimation() {

		if (!this.IsFrozen() && !this.NeedsRoute()) {
			this.GetGraph().GetLayout().GetGraphModel();
			TranslateUtil.Noop();
			try {
				this.ReshapeLink(false, true);
			} finally {
				TranslateUtil.Noop();
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
			}
		}

	}

	public Boolean EndsAtObstacle(Integer i) {
		if ((i >= 0) && (i < this._nextObstacle.length)) {

			return this._nextObstacle[i];
		}

		return true;

	}

	public LLTerminationPoint GetActEndPoint() {

		return this._endPoints[this._actEndPointIndex];

	}

	public LLTerminationPoint GetActStartPoint() {

		return this._startPoints[this._actStartPointIndex];

	}

	public Integer[] GetBounds() {
		if (this._bbox == null) {
			this.UpdateBoundingBox();
		}

		return this._bbox;

	}

	public LLTerminationPoint[] GetEndPoints() {

		return this._endPoints;

	}

	public java.lang.Object GetFromNode() {

		return this._fromNode;

	}

	public InternalPoint GetFromPoint() {

		return this._fromPoint;

	}

	public InternalRect GetFromRect() {

		return this._fromRect;

	}

	private LLGraph GetGraph() {

		return this._graph;

	}

	public Integer GetGridCorrection(Integer dir) {
		LLGrid grid = this.GetGraph().GetGrid();
		float num = this._gridBaseCoord[dir] - grid.GetMinCoord(dir);
		float gridDist = grid.GetGridDist(dir);

		return (int) ((num + (0.4 * gridDist)) / ((double) gridDist));

	}

	public java.lang.Object GetLinkObject() {

		return this._linkObject;

	}

	public Boolean GetNextBend(Integer[] point, Integer i) {
		if (i == 0) {
			this.CalcRealStartPointOnGrid(point);

			point[0] += this.GetGridCorrection(0);

			point[1] += this.GetGridCorrection(1);

			return true;
		}
		if (i <= this._segments.length) {
			point[this.GetSegmentDir(i - 1)] += this._segments[i - 1];

			return true;
		}

		return false;

	}

	public Boolean[] GetNextObstacleFlags() {

		return this._prevObstacle;

	}

	public Integer GetPreferedSide() {

		return this._preferedSide;

	}

	public Boolean GetPrevBend(Integer[] point, Integer i) {
		if (i == this._segments.length) {
			this.CalcRealEndPointOnGrid(point);

			point[0] += this.GetGridCorrection(0);

			point[1] += this.GetGridCorrection(1);

			return true;
		}
		if (i >= 0) {
			point[this.GetSegmentDir(i)] -= this._segments[i];

			return true;
		}

		return false;

	}

	public Boolean[] GetPrevObstacleFlags() {

		return this._prevObstacle;

	}

	public Integer GetSegmentDir(Integer i) {

		return ((this._startDir + i) % 2);

	}

	public Integer GetSegmentEndDir() {

		return (((this._startDir + this._segments.length) - 1) % 2);

	}

	public Integer[] GetSegments() {

		return this._segments;

	}

	public Integer GetSegmentStartDir() {

		return this._startDir;

	}

	public double GetSortValue() {

		return this._sortValue;

	}

	public LLTerminationPoint[] GetStartPoints() {

		return this._startPoints;

	}

	public java.lang.Object GetToNode() {

		return this._toNode;

	}

	public InternalPoint GetToPoint() {

		return this._toPoint;

	}

	public InternalRect GetToRect() {

		return this._toRect;

	}

	public Boolean IsCandidateForStraightRoute() {

		return (((this._fromRect.X <= (this._toRect.X + this._toRect.Width)) && (this._toRect.X <= (this._fromRect.X + this._fromRect.Width))) || ((this._fromRect.Y <= (this._toRect.Y + this._toRect.Height)) && (this._toRect.Y <= (this._fromRect.Y + this._fromRect.Height))));

	}

	public Boolean IsDirect() {

		return this._direct;

	}

	public Boolean IsFromPointFixed() {

		return (this._fromPoint != null);

	}

	public Boolean IsFrozen() {

		return this._frozen;

	}

	public Boolean IsSelfLoop() {

		return (this._fromNode == this._toNode);

	}

	public Boolean IsToPointFixed() {

		return (this._toPoint != null);

	}

	public Boolean IsWithinFromNodeRange(Integer dir, Integer gridIndex,
			float minCornerOffset) {

		return this.IsWithinRectRange(this._fromRect, dir, gridIndex,
				minCornerOffset);

	}

	public Boolean IsWithinRectRange(InternalRect rect, Integer dir,
			Integer gridIndex, float minCornerOffset) {
		float num = 0;
		float num2 = 0;
		LLGrid grid = this.GetGraph().GetGrid();
		if (dir == 0) {
			if (rect.Width >= 0f) {
				num = rect.X + minCornerOffset;
				num2 = (rect.X + rect.Width) - minCornerOffset;
			} else {
				num = (rect.X + rect.Width) + minCornerOffset;
				num2 = rect.X - minCornerOffset;
			}
		} else if (rect.Height >= 0f) {
			num = rect.Y + minCornerOffset;
			num2 = (rect.Y + rect.Height) - minCornerOffset;
		} else {
			num = (rect.Y + rect.Height) + minCornerOffset;
			num2 = rect.Y - minCornerOffset;
		}
		Integer num3 = grid.GetGridIndex(dir, num, true);
		Integer num4 = grid.GetGridIndex(dir, num2, false);
		if (gridIndex < num3) {

			return false;
		}
		if (gridIndex > num4) {

			return false;
		}

		return true;

	}

	public Boolean IsWithinToNodeRange(Integer dir, Integer gridIndex,
			float minCornerOffset) {

		return this.IsWithinRectRange(this._toRect, dir, gridIndex,
				minCornerOffset);

	}

	public void MarkLinkChanged() {
		this._bbox = null;

	}

	public Boolean MayIntersect(Integer[] inputBox) {
		if (this._bbox == null) {
			this.UpdateBoundingBox();
		}

		return ((((this._bbox[0] <= inputBox[1]) && (inputBox[0] <= this._bbox[1])) && (this._bbox[2] <= inputBox[3])) && (inputBox[2] <= this._bbox[3]));

	}

	public Boolean MayIntersect(LLLink link) {
		if (this._bbox == null) {
			this.UpdateBoundingBox();
		}
		if (link._bbox == null) {
			link.UpdateBoundingBox();
		}

		return ((((this._bbox[0] <= link._bbox[1]) && (link._bbox[0] <= this._bbox[1])) && (this._bbox[2] <= link._bbox[3])) && (link._bbox[2] <= this._bbox[3]));

	}

	public Boolean NeedsRoute() {

		return (this._segments == null);

	}

	public void ReshapeLink(Boolean updateGrid, Boolean redraw) {

		if (!this.IsFrozen() && !this.NeedsRoute()) {
			Integer num = null;
			Integer index = null;
			Integer num3 = null;
			Integer num4 = null;
			Integer num5 = null;
			Integer num6 = null;
			InternalPoint[] pointArray = null;
			Boolean flag2 = null;
			Boolean flag3 = null;
			float gridCoord = 0;
			float num8 = 0;
			float num13 = 0;
			float num14 = 0;
			LLGraph graph = this.GetGraph();
			LongLinkLayout layout = graph.GetLayout();
			IGraphModel graphModel = layout.GetGraphModel();
			java.lang.Object linkObject = this.GetLinkObject();
			InternalPoint fromPoint = this.GetFromPoint();
			InternalPoint toPoint = this.GetToPoint();
			LLGrid grid = graph.GetGrid();
			Boolean flag = false;

			if (this.IsDirect()) {
				LLTerminationPoint actStartPoint = this.GetActStartPoint();
				LLTerminationPoint actEndPoint = this.GetActEndPoint();
				Integer side = actStartPoint.GetSide();
				Integer num10 = actEndPoint.GetSide();
				flag2 = (side == 0) || (side == 1);
				flag3 = (num10 == 0) || (num10 == 1);

				index = actStartPoint.GetIndex(0);

				num4 = actStartPoint.GetIndex(1);

				num3 = actEndPoint.GetIndex(0);

				num5 = actEndPoint.GetIndex(1);

				index += this.GetGridCorrection(0);

				num4 += this.GetGridCorrection(1);

				num3 += this.GetGridCorrection(0);

				num5 += this.GetGridCorrection(1);
				num6 = 0;
				if (index == num3) {
					if (flag2) {
						num6++;
					}
					if (flag3) {
						num6++;
					}
				} else if (num4 == num5) {
					if (!flag2) {
						num6++;
					}
					if (!flag3) {
						num6++;
					}
				} else {
					num6 = 2;
				}
				if ((((num6 == 0) && (fromPoint != null)) && ((toPoint != null) && (fromPoint.X != toPoint.X)))
						&& (fromPoint.Y != toPoint.Y)) {
					flag = true;
					num6 = 2;
				}
				pointArray = new InternalPoint[num6];
				if (flag || (num6 == 2)) {

					gridCoord = grid.GetGridCoord(0, index);

					num8 = grid.GetGridCoord(1, num4);
					pointArray[0] = new InternalPoint(gridCoord, num8);

					gridCoord = grid.GetGridCoord(0, num3);

					num8 = grid.GetGridCoord(1, num5);
					pointArray[1] = new InternalPoint(gridCoord, num8);
				} else if (index == num3) {
					if (flag2) {

						gridCoord = grid.GetGridCoord(0, index);

						num8 = grid.GetGridCoord(1, num4);
					} else {

						gridCoord = grid.GetGridCoord(0, num3);

						num8 = grid.GetGridCoord(1, num5);
					}
					if (num6 == 1) {
						pointArray[0] = new InternalPoint(gridCoord, num8);
					}
				} else if (num4 == num5) {
					if (!flag2) {

						gridCoord = grid.GetGridCoord(0, index);

						num8 = grid.GetGridCoord(1, num4);
					} else {

						gridCoord = grid.GetGridCoord(0, num3);

						num8 = grid.GetGridCoord(1, num5);
					}
					if (num6 == 1) {
						pointArray[0] = new InternalPoint(gridCoord, num8);
					}
				}
			} else {
				num6 = this._segments.length - 1;
				if ((((num6 == 0) && (fromPoint != null)) && ((toPoint != null) && (fromPoint.X != toPoint.X)))
						&& (fromPoint.Y != toPoint.Y)) {
					flag = true;
					num6 = 2;
				}
				pointArray = new InternalPoint[num6];
				flag2 = this.GetSegmentStartDir() == 0;
				flag3 = this.GetSegmentEndDir() == 0;
				if (flag) {
					if (flag2) {
						float x = (fromPoint.X + toPoint.X) / 2f;
						pointArray[0] = new InternalPoint(x, fromPoint.Y);
						pointArray[1] = new InternalPoint(x, toPoint.Y);
					} else {
						float y = (fromPoint.Y + toPoint.Y) / 2f;
						pointArray[0] = new InternalPoint(fromPoint.X, y);
						pointArray[1] = new InternalPoint(toPoint.X, y);
					}
				}
				Integer[] point = new Integer[2];
				this.GetNextBend(point, 0);
				index = point[0];
				num4 = point[1];
				if (!flag) {
					for (num = 0; (num < num6)
							&& this.GetNextBend(point, num + 1); num++) {

						gridCoord = grid.GetGridCoord(0, point[0]);

						num8 = grid.GetGridCoord(1, point[1]);
						pointArray[num] = new InternalPoint(gridCoord, num8);
					}
				}
				this.GetNextBend(point, num6 + 1);
				num3 = point[0];
				num5 = point[1];
			}
			if (fromPoint == null) {
				fromPoint = new InternalPoint(0f, 0f);
				if (num6 > 0) {
					num13 = pointArray[0].X;
					num14 = pointArray[0].Y;
				} else {

					num13 = grid.GetGridCoord(0, num3);

					num14 = grid.GetGridCoord(1, num5);
				}
				if (flag2) {
					fromPoint.Y = num14;
					if (num13 > this._fromRect.X) {
						fromPoint.X = this._fromRect.X + this._fromRect.Width;
					} else {
						fromPoint.X = this._fromRect.X;
					}
				} else {
					fromPoint.X = num13;
					if (num14 > this._fromRect.Y) {
						fromPoint.Y = this._fromRect.Y + this._fromRect.Height;
					} else {
						fromPoint.Y = this._fromRect.Y;
					}
				}
			}
			if (toPoint == null) {
				toPoint = new InternalPoint(0f, 0f);
				if (num6 > 0) {
					num13 = pointArray[num6 - 1].X;
					num14 = pointArray[num6 - 1].Y;
				} else {

					num13 = grid.GetGridCoord(0, index);

					num14 = grid.GetGridCoord(1, num4);
				}
				if (flag3) {
					toPoint.Y = num14;
					if (num13 > this._toRect.X) {
						toPoint.X = this._toRect.X + this._toRect.Width;
					} else {
						toPoint.X = this._toRect.X;
					}
				} else {
					toPoint.X = num13;
					if (num14 > this._toRect.Y) {
						toPoint.Y = this._toRect.Y + this._toRect.Height;
					} else {
						toPoint.Y = this._toRect.Y;
					}
				}
			}
			if (num6 > 0) {
				if (flag2) {
					pointArray[0].Y = fromPoint.Y;
				} else {
					pointArray[0].X = fromPoint.X;
				}
				if (flag3) {
					pointArray[num6 - 1].Y = toPoint.Y;
				} else {
					pointArray[num6 - 1].X = toPoint.X;
				}
			}
			Boolean flag4 = layout.IsFromPointFixed(linkObject);
			Boolean flag5 = layout.IsToPointFixed(linkObject);
			Integer linkStyle = this.IsDirect() ? 4 : 2;
			GraphModelUtil.ReshapeLink(graphModel, layout, linkObject,
					linkStyle, flag4 ? ((fromPoint != null) ? new FixedPoint(
							fromPoint) : null) : fromPoint, pointArray, 0,
					num6, flag5 ? ((toPoint != null) ? new FixedPoint(toPoint)
							: null) : toPoint);
			if (updateGrid) {
				if (num6 > 0) {
					grid.SetObstacle(fromPoint, pointArray[0]);
					grid.SetObstacle(pointArray[num6 - 1], toPoint);
					for (num = 1; num < num6; num++) {
						grid.SetObstacle(pointArray[num - 1], pointArray[num]);
					}
				} else {
					grid.SetObstacle(fromPoint, toPoint);
				}
			}
		}

	}

	public void SetActEndCandidate(Integer endPointIndex) {
		this._actEndPointIndex = endPointIndex;

	}

	public void SetActStartAndEndCandidates(Integer startPointIndex,
			Integer endPointIndex) {
		this._actStartPointIndex = startPointIndex;
		this._actEndPointIndex = endPointIndex;

	}

	public void SetActStartCandidate(Integer startPointIndex) {
		this._actStartPointIndex = startPointIndex;

	}

	public void SetEndPoints(LLTerminationPoint[] points, Integer numberOfPoints) {
		this._endPoints = new LLTerminationPoint[numberOfPoints];
		for (Integer i = 0; i < numberOfPoints; i++) {
			this._endPoints[i] = points[i];
		}
		LLGrid grid = this.GetGraph().GetGrid();

		this._gridBaseCoord[0] = grid.GetMinCoord(0);

		this._gridBaseCoord[1] = grid.GetMinCoord(1);

	}

	public void SetFromPoint(InternalPoint p) {
		this._fromPoint = p;

	}

	public void SetFrozen(Boolean frozen) {
		this._frozen = frozen;

	}

	public void SetPreferedSide(Integer side) {
		this._preferedSide = side;

	}

	public void SetSegments(Integer[] segments, Boolean[] prevObstacleFlags,
			Boolean[] nextObstacleFlags) {
		this._segments = segments;
		this._prevObstacle = prevObstacleFlags;
		this._nextObstacle = nextObstacleFlags;

	}

	public void SetSegmentStartDir(Integer dir) {
		this._startDir = dir;

	}

	public void SetSortValue(double val) {
		this._sortValue = val;

	}

	public void SetStartPoints(LLTerminationPoint[] points,
			Integer numberOfPoints) {
		this._startPoints = new LLTerminationPoint[numberOfPoints];
		for (Integer i = 0; i < numberOfPoints; i++) {
			this._startPoints[i] = points[i];
		}
		LLGrid grid = this.GetGraph().GetGrid();

		this._gridBaseCoord[0] = grid.GetMinCoord(0);

		this._gridBaseCoord[1] = grid.GetMinCoord(1);

	}

	public void SetToPoint(InternalPoint p) {
		this._toPoint = p;

	}

	public Boolean StartsAtObstacle(Integer i) {
		if ((i >= 0) && (i < this._prevObstacle.length)) {

			return this._prevObstacle[i];
		}

		return true;

	}

	public void StoreRoute(ArrayList points) {
		InternalPoint point = null;
		LLGridSegment segment = null;
		Integer num = 1;
		if (points != null) {
			num = points.get_Count() + 1;
		}
		this._segments = new Integer[num];
		this._prevObstacle = new Boolean[num];
		this._nextObstacle = new Boolean[num];
		Integer index = num - 1;
		Integer num3 = -1;
		Integer[] p = new Integer[2];
		Integer[] numArray2 = new Integer[2];
		LLGrid grid = this.GetGraph().GetGrid();
		Boolean flag = true;
		this.CalcRealEndPointOnGrid(numArray2);
		if (points != null) {
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(points);

			while (enumerator.HasMoreElements()) {
				point = (InternalPoint) enumerator.NextElement();
				p[0] = (int) point.X;
				p[1] = (int) point.Y;
				if ((p[0] != numArray2[0]) || (p[1] != numArray2[1])) {
					if (p[0] == numArray2[0]) {
						num3 = 1;
					} else {
						num3 = 0;
					}
					break;
				}
				numArray2[0] = p[0];
				numArray2[1] = p[1];
				flag = !flag;
			}
		}
		this.CalcRealStartPointOnGrid(p);
		if (num3 == -1) {
			if (p[0] == numArray2[0]) {
				num3 = 1;
			} else {
				num3 = 0;
			}
		}
		if (!flag) {
			num3 = 1 - num3;
		}
		this.CalcRealEndPointOnGrid(numArray2);
		if (points != null) {
			IJavaStyleEnumerator enumerator2 = TranslateUtil
					.Collection2JavaStyleEnum(points);

			while (enumerator2.HasMoreElements()) {
				point = (InternalPoint) enumerator2.NextElement();
				p[0] = (int) point.X;
				p[1] = (int) point.Y;
				this._segments[index] = numArray2[num3] - p[num3];
				this._nextObstacle[index] = true;
				if ((0 <= p[1 - num3])
						&& (p[1 - num3] < grid.GetNumberOfGridLines(1 - num3))) {

					segment = grid.GetGridLine(1 - num3, p[1 - num3])
							.SearchSegment(numArray2[num3]);
				} else {
					segment = null;
				}
				if (segment != null) {
					if (p[num3] < numArray2[num3]) {
						if (segment.GetEndIndex() > numArray2[num3]) {
							this._nextObstacle[index] = false;
						}
					} else if ((p[num3] > numArray2[num3])
							&& (segment.GetStartIndex() < numArray2[num3])) {
						this._nextObstacle[index] = false;
					}
				}
				this._prevObstacle[index] = true;
				if ((0 <= p[1 - num3])
						&& (p[1 - num3] < grid.GetNumberOfGridLines(1 - num3))) {

					segment = grid.GetGridLine(1 - num3, p[1 - num3])
							.SearchSegment(p[num3]);
				} else {
					segment = null;
				}
				if (segment != null) {
					if (p[num3] < numArray2[num3]) {
						if (segment.GetStartIndex() < p[num3]) {
							this._prevObstacle[index] = false;
						}
					} else if ((p[num3] > numArray2[num3])
							&& (segment.GetEndIndex() > p[num3])) {
						this._prevObstacle[index] = false;
					}
				}
				num3 = 1 - num3;
				numArray2[0] = p[0];
				numArray2[1] = p[1];
				index--;
			}
		}
		this.CalcRealStartPointOnGrid(p);
		this._segments[0] = numArray2[num3] - p[num3];
		this._startDir = num3;
		this._nextObstacle[0] = true;
		if ((0 <= p[1 - num3])
				&& (p[1 - num3] < grid.GetNumberOfGridLines(1 - num3))) {

			segment = grid.GetGridLine(1 - num3, p[1 - num3]).SearchSegment(
					numArray2[num3]);
		} else {
			segment = null;
		}
		if (segment != null) {
			if (p[num3] < numArray2[num3]) {
				if (segment.GetEndIndex() > numArray2[num3]) {
					this._nextObstacle[0] = false;
				}
			} else if ((p[num3] > numArray2[num3])
					&& (segment.GetStartIndex() < numArray2[num3])) {
				this._nextObstacle[0] = false;
			}
		}
		this._prevObstacle[0] = true;
		if ((0 <= p[1 - num3])
				&& (p[1 - num3] < grid.GetNumberOfGridLines(1 - num3))) {

			segment = grid.GetGridLine(1 - num3, p[1 - num3]).SearchSegment(
					p[num3]);
		} else {
			segment = null;
		}
		if (segment != null) {
			if (p[num3] < numArray2[num3]) {
				if (segment.GetStartIndex() < p[num3]) {
					this._prevObstacle[0] = false;
				}
			} else if ((p[num3] > numArray2[num3])
					&& (segment.GetEndIndex() > p[num3])) {
				this._prevObstacle[0] = false;
			}
		}
		this.CleanUnusedStartAndEndPoints();
		this.MarkLinkChanged();

	}

	public void UpdateBoundingBox() {
		this._bbox = new Integer[] { 0x7fffffff, -1, 0x7fffffff, -1 };
		Integer[] point = new Integer[2];
		Integer num = 0;

		while (this.GetNextBend(point, num++)) {
			if (point[0] < this._bbox[0]) {
				this._bbox[0] = point[0];
			}
			if (point[0] > this._bbox[1]) {
				this._bbox[1] = point[0];
			}
			if (point[1] < this._bbox[2]) {
				this._bbox[2] = point[1];
			}
			if (point[1] > this._bbox[3]) {
				this._bbox[3] = point[1];
			}
		}

	}

	public void UpdateGrid() {

		if ((!this.NeedsRoute() && !this.IsFrozen()) && !this.IsDirect()) {
			this.UpdateGridImpl();
		}

	}

	public void UpdateGridIfIntersects(Integer[] bounds) {

		if (((!this.NeedsRoute() && !this.IsFrozen()) && !this.IsDirect())
				&& this.MayIntersect(bounds)) {
			this.UpdateGridImpl();
		}

	}

	private void UpdateGridImpl() {
		LLGrid grid = this.GetGraph().GetGrid();
		Integer[] numArray = new Integer[2];
		InternalPoint point = new InternalPoint(0f, 0f);
		InternalPoint point2 = new InternalPoint(0f, 0f);
		Integer num = 0;
		this.GetNextBend(numArray, num++);

		point.X = grid.GetGridCoord(0, numArray[0]);

		point.Y = grid.GetGridCoord(1, numArray[1]);

		while (this.GetNextBend(numArray, num++)) {

			point2.X = grid.GetGridCoord(0, numArray[0]);

			point2.Y = grid.GetGridCoord(1, numArray[1]);
			grid.SetObstacle(point, point2);
			point.X = point2.X;
			point.Y = point2.Y;
		}

	}

}