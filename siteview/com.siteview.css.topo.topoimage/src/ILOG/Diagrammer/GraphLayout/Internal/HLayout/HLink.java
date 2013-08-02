package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;

public final class HLink extends HGraphMember {
	private HSegment _endSegment;

	private float[] _fromCoord;

	public Boolean _fromFixed = false;

	private float _fromForkPoint;

	private Integer _fromPortNumber;

	private Integer _fromPortSide;

	private Integer _linkStyle;

	public float _offsetToFixedFromPoint;

	public float _offsetToFixedToPoint;

	private Integer _origFromPortSide;

	private Integer _origToPortSide;

	private InternalPoint[] _pointsForIncremental;

	private HSegment _startSegment;

	private float _thickness;

	private float[] _toCoord;

	public Boolean _toFixed = false;

	private float _toForkPoint;

	private Integer _toPortNumber;

	private Integer _toPortSide;

	public Integer EAST = 1;

	public Integer EAST_OR_WEST = -3;

	public Integer NORTH = 0;

	public Integer NORTH_OR_SOUTH = -2;

	public Integer SOUTH = 2;

	public Integer UNSPECIFIED = -1;

	public Integer WEST = 3;

	public HLink(HNode fromNode, HNode toNode) {
		this._startSegment = this._endSegment = new HSegment(fromNode, toNode);
		this._thickness = 0f;
		this._fromCoord = new float[2];
		this._toCoord = new float[2];
		this._fromPortNumber = -1;
		this._toPortNumber = -1;
		this._fromPortSide = -1;
		this._toPortSide = -1;
		this._fromForkPoint = Float.MAX_VALUE;
		this._toForkPoint = Float.MAX_VALUE;
	}

	public void ActAfterAddToGraph() {
		HNode from = this.GetFrom();
		HSegment opposite = this._startSegment;
		HGraph ownerGraph = this.GetOwnerGraph();
		while (opposite != null) {
			ownerGraph.AddSegment(opposite);
			opposite.SetOwner(this);
			if (opposite == this._endSegment) {

				return;
			}

			from = opposite.GetOpposite(from);
			ownerGraph.AddNode(from);
			from.SetOwner(this);

			opposite = from.GetOpposite(opposite);
		}

	}

	public void ActBeforeRemoveFromGraph() {
		HNode from = this.GetFrom();
		HSegment opposite = this._startSegment;
		HGraph ownerGraph = this.GetOwnerGraph();
		while (opposite != null) {
			ownerGraph.RemoveSegment(opposite);
			if (opposite == this._endSegment) {

				return;
			}

			from = opposite.GetOpposite(from);

			opposite = from.GetOpposite(opposite);
			ownerGraph.RemoveNode(from);
		}

	}

	public void ActWhenMakeEastWestLink() {
		HNode from = this.GetFrom();
		HNode to = this.GetTo();

		if (this._startSegment.GetFrom()
				.ContainsFromSegment(this._startSegment)) {
			this._startSegment.GetFrom().FromRemove(this._startSegment);
		}

		if (this._startSegment.GetTo().ContainsToSegment(this._startSegment)) {
			this._startSegment.GetTo().ToRemove(this._startSegment);
		}

		if (this._endSegment.GetFrom().ContainsFromSegment(this._endSegment)) {
			this._endSegment.GetFrom().FromRemove(this._endSegment);
		}

		if (this._endSegment.GetTo().ContainsToSegment(this._endSegment)) {
			this._endSegment.GetTo().ToRemove(this._endSegment);
		}
		this._startSegment = this._endSegment;

		if (this._startSegment.IsReversed()) {
			this._startSegment.ReverseWithoutUpdate();
		}
		this._startSegment.SetFrom(from);
		this._startSegment.SetTo(to);

	}

	public HNode AddDummyNode(HSegment segment) {
		HNode fromNode = null;
		HSegment segment2 = null;
		Boolean flag = segment.IsReversed();
		if (flag) {
			segment.ReverseWithoutUpdate();
		}
		HNode to = segment.GetTo();
		HGraph ownerGraph = this.GetOwnerGraph();

		fromNode = ownerGraph.NewDummyHNode(this);
		fromNode.SetSwimLane(this.GetTo().GetSwimLane());
		HSegment segment3 = ownerGraph.NewHSegment(fromNode, to, this);
		segment3.CopyProperties(segment);
		if (flag) {
			segment.ReverseWithoutUpdate();
			segment3.ReverseWithoutUpdate();

			segment2 = to.FromRemove(segment);
			segment.SetFrom(fromNode);
			fromNode.FromAdd(segment, null);
			fromNode.ToAdd(segment3, null);
			to.FromAdd(segment3, segment2);
		} else {

			segment2 = to.ToRemove(segment);
			segment.SetTo(fromNode);
			fromNode.ToAdd(segment, null);
			fromNode.FromAdd(segment3, null);
			to.ToAdd(segment3, segment2);
		}
		if (this._startSegment == segment) {
			if (this._endSegment == segment) {
				this._endSegment = segment3;

				return fromNode;
			}

			if (!to.IsDummyNode()) {
				this._startSegment = segment3;
			}

			return fromNode;
		}

		if ((this._endSegment == segment) && !to.IsDummyNode()) {
			this._endSegment = segment3;
		}

		return fromNode;

	}

	public void AddTwoDummyNodesAtEastWestLink() {
		HNode from = new HNode();
		HNode to = new HNode();
		HSegment startSegment = this.GetStartSegment();
		HSegment segment = new HSegment(from, to);
		HSegment segment3 = new HSegment(to, this.GetTo());
		from.SetOwner(this);
		to.SetOwner(this);
		from.SetSwimLane(this.GetTo().GetSwimLane());
		to.SetSwimLane(this.GetTo().GetSwimLane());
		startSegment.SetOwner(this);
		segment.SetOwner(this);
		segment3.SetOwner(this);
		startSegment.SetTo(from);
		from.ToAdd(startSegment, null);
		from.FromAdd(segment, null);
		to.ToAdd(segment, null);
		to.FromAdd(segment3, null);
		this._endSegment = segment3;

	}

	private Integer CalcSideOfFixedConnectionPoint(HNode node, float[] p) {
		Integer num3 = null;
		Integer num4 = null;
		Integer num5 = null;
		Integer num6 = null;
		HGraph ownerGraph = node.GetOwnerGraph();
		Integer levelFlow = ownerGraph.GetLevelFlow();
		Integer index = 1 - levelFlow;
		if (ownerGraph.GetFlowDirection() == 0) {
			num3 = 1;
			num4 = 3;
			num5 = 0;
			num6 = 2;
			// NOTICE: break ignore!!!
		} else if (ownerGraph.GetFlowDirection() == 1) {
			num3 = 3;
			num4 = 1;
			num5 = 0;
			num6 = 2;
			// NOTICE: break ignore!!!
		} else if (ownerGraph.GetFlowDirection() == 2) {
			num3 = 3;
			num4 = 1;
			num5 = 2;
			num6 = 0;
			// NOTICE: break ignore!!!
		} else {
			num3 = 1;
			num4 = 3;
			num5 = 2;
			num6 = 0;
			// NOTICE: break ignore!!!
		}

		float num7 = Math.Abs((float) (p[index] - node.GetCoord(index)));
		Integer num9 = num5;
		float num8 = Math.Abs((float) ((node.GetCoord(index) + node
				.GetSize(index)) - p[index]));
		if (num8 < num7) {
			num9 = num6;
			num7 = num8;
		}

		num8 = Math.Abs((float) (p[levelFlow] - node.GetCoord(levelFlow)));
		if (num8 < num7) {
			num9 = num3;
			num7 = num8;
		}

		num8 = Math.Abs((float) ((node.GetCoord(levelFlow) + node
				.GetSize(levelFlow)) - p[levelFlow]));
		if (num8 < num7) {
			num9 = num4;
			num7 = num8;
		}

		return num9;

	}

	public float GetCrossCoordAt(float coordInEdgeFlow) {
		HGraph ownerGraph = this.GetOwnerGraph();
		InternalPoint[] pointsForIncremental = this.GetPointsForIncremental();
		Integer edgeFlow = ownerGraph.GetEdgeFlow();
		if ((pointsForIncremental != null)
				&& (pointsForIncremental.length != 0)) {
			double x = pointsForIncremental[0].X;
			double y = pointsForIncremental[0].Y;
			for (Integer i = 1; i < pointsForIncremental.length; i++) {
				double num3 = pointsForIncremental[i].X;
				double num4 = pointsForIncremental[i].Y;
				if (edgeFlow == 1) {
					if (((y <= coordInEdgeFlow) && (coordInEdgeFlow <= num4))
							|| ((num4 <= coordInEdgeFlow) && (coordInEdgeFlow <= y))) {
						if (y == num4) {

							return (float) x;
						}

						return (float) ((((num3 - x) / (num4 - y)) * (coordInEdgeFlow - y)) + x);
					}
				} else if (((x <= coordInEdgeFlow) && (coordInEdgeFlow <= num3))
						|| ((num3 <= coordInEdgeFlow) && (coordInEdgeFlow <= x))) {
					if (x == num3) {

						return (float) y;
					}

					return (float) ((((num4 - y) / (num3 - x)) * (coordInEdgeFlow - x)) + y);
				}
				x = num3;
				y = num4;
			}
		}

		return 0f;

	}

	public HSegment GetEndSegment() {

		return this._endSegment;

	}

	public HNode GetFrom() {
		HSegment startSegment = this.GetStartSegment();

		if (startSegment.IsReversed()) {

			return startSegment.GetTo();
		}

		return startSegment.GetFrom();

	}

	public float GetFromCoord(Integer index) {

		return this._fromCoord[index];

	}

	public float[] GetFromCoordField() {

		return this._fromCoord;

	}

	public float GetFromForkPoint() {

		return this._fromForkPoint;

	}

	public InternalPoint GetFromPoint() {

		return new InternalPoint(this._fromCoord[0], this._fromCoord[1]);

	}

	public Integer GetFromPortNumber() {

		return this._fromPortNumber;

	}

	public Integer GetFromPortSide() {

		return this._fromPortSide;

	}

	public InternalPoint[] GetIntermediatePoints() {
		HNode node = null;
		this.Optimize();
		Integer num = 0;
		HNodeIterator nodes = this.GetNodes();

		while (nodes.HasNext()) {

			node = nodes.Next();

			if (!node.IsInvalid()) {
				num++;
				if ((node.GetWidth() != 0f) || (node.GetHeight() != 0f)) {
					num++;
				}
			}
		}
		InternalPoint[] pointArray = new InternalPoint[num];

		nodes = this.GetNodes();
		Integer num2 = 0;

		while (nodes.HasNext()) {

			node = nodes.Next();

			if (!node.IsInvalid()) {
				pointArray[num2++] = new InternalPoint(node.GetX(), node.GetY());
				if ((node.GetWidth() != 0f) || (node.GetHeight() != 0f)) {
					pointArray[num2++] = new InternalPoint(node.GetX()
							+ node.GetWidth(), node.GetY() + node.GetHeight());
				}
			}
		}

		return pointArray;

	}

	public Integer GetLinkStyle() {

		return this._linkStyle;

	}

	public Integer GetMultiLinkIndex() {

		return (int) this.GetStartSegment().GetSortValue();

	}

	public HNodeIterator GetNodes() {

		if (this.HasDummyNodes()) {

			return new AnonClass_1(this);
		}

		return new AnonClass_2();

	}

	public float GetOffsetToFixedFromPoint() {

		return this._offsetToFixedFromPoint;

	}

	public float GetOffsetToFixedToPoint() {

		return this._offsetToFixedToPoint;

	}

	public Integer GetOrigFromPortSide() {

		return this._origFromPortSide;

	}

	public Integer GetOrigToPortSide() {

		return this._origToPortSide;

	}

	public InternalPoint[] GetPointsForIncremental() {

		return this._pointsForIncremental;

	}

	public float GetPriority() {

		return this._startSegment.GetPriority();

	}

	public HSegment GetStartSegment() {

		return this._startSegment;

	}

	public float GetThickness() {

		return this._thickness;

	}

	public HNode GetTo() {
		HSegment endSegment = this.GetEndSegment();

		if (endSegment.IsReversed()) {

			return endSegment.GetFrom();
		}

		return endSegment.GetTo();

	}

	public float GetToCoord(Integer index) {

		return this._toCoord[index];

	}

	public float[] GetToCoordField() {

		return this._toCoord;

	}

	public float GetToForkPoint() {

		return this._toForkPoint;

	}

	public InternalPoint GetToPoint() {

		return new InternalPoint(this._toCoord[0], this._toCoord[1]);

	}

	public Integer GetToPortNumber() {

		return this._toPortNumber;

	}

	public Integer GetToPortSide() {

		return this._toPortSide;

	}

	public Boolean HasDummyNodes() {

		return (this._startSegment != this._endSegment);

	}

	public Boolean IsCrossingAt(float coordInEdgeFlow) {
		HGraph ownerGraph = this.GetOwnerGraph();
		InternalPoint[] pointsForIncremental = this.GetPointsForIncremental();
		Integer edgeFlow = ownerGraph.GetEdgeFlow();
		if ((pointsForIncremental != null)
				&& (pointsForIncremental.length != 0)) {
			float x = pointsForIncremental[0].X;
			float y = pointsForIncremental[0].Y;
			for (Integer i = 1; i < pointsForIncremental.length; i++) {
				float num3 = pointsForIncremental[i].X;
				float num4 = pointsForIncremental[i].Y;
				if (edgeFlow == 1) {
					if (((y <= coordInEdgeFlow) && (coordInEdgeFlow <= num4))
							|| ((num4 <= coordInEdgeFlow) && (coordInEdgeFlow <= y))) {

						return true;
					}
				} else if (((x <= coordInEdgeFlow) && (coordInEdgeFlow <= num3))
						|| ((num3 <= coordInEdgeFlow) && (coordInEdgeFlow <= x))) {

					return true;
				}
				x = num3;
				y = num4;
			}
		}

		return false;

	}

	public Boolean IsFromPortNumberSpecified() {

		return (this._fromPortNumber != -1);

	}

	public Boolean IsFromPortSideSpecified() {

		return (this._origFromPortSide != -1);

	}

	public Boolean IsFromSideFixed() {

		return this._fromFixed;

	}

	public Boolean IsOrthogonal() {

		return (this._linkStyle == 100);

	}

	public Boolean IsToPortNumberSpecified() {

		return (this._toPortNumber != -1);

	}

	public Boolean IsToPortSideSpecified() {

		return (this._origToPortSide != -1);

	}

	public Boolean IsToSideFixed() {

		return this._toFixed;

	}

	public void MarkForIncremental() {
		this._pointsForIncremental = null;

	}

	public void Mirror(Integer coordinateIndex, Boolean updatePath) {
		this._fromCoord[coordinateIndex] = -this._fromCoord[coordinateIndex];
		this._toCoord[coordinateIndex] = -this._toCoord[coordinateIndex];
		if (this._pointsForIncremental != null) {
			for (Integer i = 0; i < this._pointsForIncremental.length; i++) {
				if (coordinateIndex == 0) {
					this._pointsForIncremental[i].X = -this._pointsForIncremental[i].X;
				} else {
					this._pointsForIncremental[i].Y = -this._pointsForIncremental[i].Y;
				}
			}
		}
		if (updatePath) {
			HNodeIterator nodes = this.GetNodes();

			while (nodes.HasNext()) {
				nodes.Next().Mirror(coordinateIndex);
			}
		}

	}

	private void Optimize() {
		float x = 0;
		float y = 0;
		float num6 = 0;
		float num7 = 0;
		HNodeIterator nodes = this.GetNodes();
		HNode node = null;
		HNode node2 = null;
		HNode node3 = null;
		float fromCoord = this.GetFromCoord(0);
		float num2 = this.GetFromCoord(1);
		float minStartSegmentLength = this.GetOwnerGraph().GetLayout()
				.get_MinStartSegmentLength();

		while (nodes.HasNext()) {

			node = nodes.Next();

			if (!node.IsInvalid()) {

				x = node.GetX();

				y = node.GetY();
				num6 = (x > fromCoord) ? (x - fromCoord) : (fromCoord - x);
				num7 = (y > num2) ? (y - num2) : (num2 - y);
				if (((num6 <= minStartSegmentLength) && (num7 <= minStartSegmentLength))
						&& ((node.GetWidth() == 0f) && (node.GetHeight() == 0f))) {
					node.MarkInvalid();
				}
				if ((node.GetWidth() != 0f) || (node.GetHeight() != 0f)) {
					x = node.GetX() + node.GetWidth();
					y = node.GetY() + node.GetHeight();
				}
				fromCoord = x;
				num2 = y;
				minStartSegmentLength = 0f;
			}

			if (!node.IsInvalid()) {
				node3 = node;
			}
		}
		if (((node3 != null) && (node3.GetWidth() == 0f))
				&& (node3.GetHeight() == 0f)) {
			minStartSegmentLength = this.GetOwnerGraph().GetLayout()
					.get_MinEndSegmentLength();

			x = this.GetToCoord(0);

			y = this.GetToCoord(1);
			num6 = (x > fromCoord) ? (x - fromCoord) : (fromCoord - x);
			num7 = (y > num2) ? (y - num2) : (num2 - y);
			if ((num6 <= minStartSegmentLength)
					&& (num7 <= minStartSegmentLength)) {
				node3.MarkInvalid();
			}
		}

		nodes = this.GetNodes();

		fromCoord = this.GetFromCoord(0);

		num2 = this.GetFromCoord(1);
		Boolean flag = false;
		Boolean flag2 = false;

		while (nodes.HasNext()) {

			node = nodes.Next();

			if (!node.IsInvalid()) {
				if (node.GetWidth() != 0f) {
					flag = node.GetHeight() == 0f;
					flag2 = false;
					node2 = null;
					fromCoord = node.GetX() + node.GetWidth();
					num2 = node.GetY() + node.GetHeight();
				} else {
					if (node.GetHeight() != 0f) {
						flag = false;
						flag2 = true;
						node2 = null;

						fromCoord = node.GetX();
						num2 = node.GetY() + node.GetHeight();
						continue;
					}

					x = node.GetX();

					y = node.GetY();
					if ((x == fromCoord) && (y == num2)) {
						node.MarkInvalid();
						continue;
					}
					if (node2 != null) {
						if (flag2 && (fromCoord == x)) {
							node2.MarkInvalid();
						}
						if (flag && (num2 == y)) {
							node2.MarkInvalid();
						}
					}
					node2 = node;
					flag2 = x == fromCoord;
					flag = y == num2;
					fromCoord = x;
					num2 = y;
				}
			}
		}
		if (node2 != null) {

			x = this.GetToCoord(0);

			y = this.GetToCoord(1);
			if (flag2 && (fromCoord == x)) {
				node2.MarkInvalid();
			}
			if (flag && (num2 == y)) {
				node2.MarkInvalid();
			}
		}

	}

	public void RemoveDummyNode(HNode dummyNode) {
		HSegment segment3 = null;
		HGraph ownerGraph = this.GetOwnerGraph();
		HSegment firstSegmentTo = dummyNode.GetFirstSegmentTo();
		if (firstSegmentTo == null) {

			firstSegmentTo = dummyNode.GetFirstSegmentFrom();
		}
		HSegment opposite = dummyNode.GetOpposite(firstSegmentTo);
		HNode from = opposite.GetOpposite(dummyNode);
		dummyNode.Remove(firstSegmentTo);
		dummyNode.Remove(opposite);
		if (firstSegmentTo.GetFrom() == dummyNode) {
			if (opposite.GetFrom() == from) {

				segment3 = from.FromRemove(opposite);
			} else {
				from.ToRemove(opposite);
				segment3 = null;
			}
			firstSegmentTo.SetFrom(from);
			from.FromAdd(firstSegmentTo, segment3);
		} else {
			if (opposite.GetTo() == from) {

				segment3 = from.ToRemove(opposite);
			} else {
				from.FromRemove(opposite);
				segment3 = null;
			}
			firstSegmentTo.SetTo(from);
			from.ToAdd(firstSegmentTo, null);
		}
		ownerGraph.RemoveNode(dummyNode);
		ownerGraph.RemoveSegment(opposite);
		if (opposite == this._startSegment) {
			this._startSegment = firstSegmentTo;
		}
		if (opposite == this._endSegment) {
			this._endSegment = firstSegmentTo;
		}

	}

	public void SetFromCoord(Integer index, float coord) {
		this._fromCoord[index] = coord;

	}

	public void SetFromCoordWhenFixed() {
		Integer levelFlow = this.GetOwnerGraph().GetLevelFlow();
		Integer index = 1 - levelFlow;
		HNode from = this.GetFrom();

		if (from.IsEastWestPortAuxNode()) {

			from = from.GetEastWestPortAuxOwner();
		}
		if (this.GetOrigFromPortSide() == 0) {
			this.SetFromCoord(levelFlow, from.GetCoord(levelFlow)
					+ this._offsetToFixedFromPoint);
			this.SetFromCoord(index, from.GetCoord(index));

			return;
		} else if (this.GetOrigFromPortSide() == 1) {
			this.SetFromCoord(levelFlow,
					from.GetCoord(levelFlow) + from.GetSize(levelFlow));
			this.SetFromCoord(index, from.GetCoord(index)
					+ this._offsetToFixedFromPoint);

			return;
		} else if (this.GetOrigFromPortSide() == 2) {
			this.SetFromCoord(levelFlow, from.GetCoord(levelFlow)
					+ this._offsetToFixedFromPoint);
			this.SetFromCoord(index, from.GetCoord(index) + from.GetSize(index));

			return;
		} else if (this.GetOrigFromPortSide() == 3) {
			this.SetFromCoord(levelFlow, from.GetCoord(levelFlow));
			this.SetFromCoord(index, from.GetCoord(index)
					+ this._offsetToFixedFromPoint);

			return;
		}

	}

	public void SetFromCoordWhenFree(Integer index, float coord) {

		if (!this.IsFromSideFixed()) {
			this.SetFromCoord(index, coord);
		}

	}

	public void SetFromForkPoint(float val) {
		this._fromForkPoint = val;

	}

	public void SetFromPortNumber(Integer portNumber) {
		this._fromPortNumber = portNumber;

	}

	public void SetFromPortSide(Integer side) {
		this._fromPortSide = side;

	}

	public void SetFromSideFixed(Boolean f) {
		this._fromFixed = f;

	}

	public void SetLinkStyle(Integer style) {
		this._linkStyle = style;

	}

	public void SetMultiLinkIndex(Integer index) {
		this.GetStartSegment().SetSortValue((float) index);
		this.GetEndSegment().SetSortValue((float) index);

	}

	public void SetOrigFromPortSide(Integer side) {
		this._origFromPortSide = side;
		this.SetFromPortSide(side);

	}

	public void SetOrigToPortSide(Integer side) {
		this._origToPortSide = side;
		this.SetToPortSide(side);

	}

	public void SetPointsForIncremental(InternalPoint[] points) {
		this._pointsForIncremental = points;

	}

	public void SetPriority(float prio) {
		this._startSegment.SetPriority(prio);

	}

	public void SetThickness(float thickness) {
		this._thickness = thickness;

	}

	public void SetToCoord(Integer index, float coord) {
		this._toCoord[index] = coord;

	}

	public void SetToCoordWhenFixed() {
		Integer levelFlow = this.GetOwnerGraph().GetLevelFlow();
		Integer index = 1 - levelFlow;
		HNode to = this.GetTo();

		if (to.IsEastWestPortAuxNode()) {

			to = to.GetEastWestPortAuxOwner();
		}
		if (this.GetOrigToPortSide() == 0) {
			this.SetToCoord(levelFlow, to.GetCoord(levelFlow)
					+ this._offsetToFixedToPoint);
			this.SetToCoord(index, to.GetCoord(index));

			return;
		} else if (this.GetOrigToPortSide() == 1) {
			this.SetToCoord(levelFlow,
					to.GetCoord(levelFlow) + to.GetSize(levelFlow));
			this.SetToCoord(index, to.GetCoord(index)
					+ this._offsetToFixedToPoint);

			return;
		} else if (this.GetOrigToPortSide() == 2) {
			this.SetToCoord(levelFlow, to.GetCoord(levelFlow)
					+ this._offsetToFixedToPoint);
			this.SetToCoord(index, to.GetCoord(index) + to.GetSize(index));

			return;
		} else if (this.GetOrigToPortSide() == 3) {
			this.SetToCoord(levelFlow, to.GetCoord(levelFlow));
			this.SetToCoord(index, to.GetCoord(index)
					+ this._offsetToFixedToPoint);

			return;
		}

	}

	public void SetToCoordWhenFree(Integer index, float coord) {

		if (!this.IsToSideFixed()) {
			this.SetToCoord(index, coord);
		}

	}

	public void SetToForkPoint(float val) {
		this._toForkPoint = val;

	}

	public void SetToPortNumber(Integer portNumber) {
		this._toPortNumber = portNumber;

	}

	public void SetToPortSide(Integer side) {
		this._toPortSide = side;

	}

	public void SetToSideFixed(Boolean f) {
		this._toFixed = f;

	}

	public void ShiftBy(float dx, float dy, Boolean updatePath) {
		this._fromCoord[0] += dx;
		this._fromCoord[1] += dy;
		this._toCoord[0] += dx;
		this._toCoord[1] += dy;
		if (updatePath) {
			HNodeIterator nodes = this.GetNodes();

			while (nodes.HasNext()) {
				nodes.Next().ShiftBy(dx, dy);
			}
		}

	}

	public void SwapForkAndEndPoints(Integer levelDir) {
		float num = this._fromCoord[levelDir];
		this._fromCoord[levelDir] = this._fromForkPoint;
		this._fromForkPoint = num;
		num = this._toCoord[levelDir];
		this._toCoord[levelDir] = this._toForkPoint;
		this._toForkPoint = num;

	}

	public void TranslateFixedEndPoints(java.lang.Object linkObject) {
		HNode from = null;
		Integer num4 = null;
		HGraph ownerGraph = this.GetOwnerGraph();
		IGraphModel graphModel = ownerGraph.GetGraphModel();
		Integer flowDirection = ownerGraph.GetFlowDirection();
		Integer levelFlow = ownerGraph.GetLevelFlow();
		Integer index = 1 - levelFlow;
		float[] p = new float[2];
		InternalPoint[] pointsForIncremental = this.GetPointsForIncremental();
		if (pointsForIncremental == null) {

			pointsForIncremental = GraphModelUtil.GetLinkPoints(graphModel,
					linkObject);
		}
		InternalPoint point = pointsForIncremental[0];
		if (this.IsFromSideFixed() && (p != null)) {
			p[0] = point.X;
			p[1] = point.Y;

			from = this.GetFrom();

			num4 = this.CalcSideOfFixedConnectionPoint(from, p);
			this.SetOrigFromPortSide(num4);
			this.SetFromPortNumber(-1);
			if (num4 == 0 || num4 == 2) {
				if (flowDirection == 0 || flowDirection == 3) {
					this._offsetToFixedFromPoint = (from.GetCoord(levelFlow) + from
							.GetSize(levelFlow)) - p[levelFlow];
				} else
					this._offsetToFixedFromPoint = p[levelFlow]
							- from.GetCoord(levelFlow);

			} else {
				if (flowDirection == 2 || flowDirection == 3) {
					this._offsetToFixedFromPoint = (from.GetCoord(index) + from
							.GetSize(index)) - p[index];
				} else
					this._offsetToFixedFromPoint = p[index]
							- from.GetCoord(index);
			}
		}
		Label_0110: point = pointsForIncremental[pointsForIncremental.length - 1];
		if (this.IsToSideFixed() && (p != null)) {
			p[0] = point.X;
			p[1] = point.Y;

			from = this.GetTo();

			num4 = this.CalcSideOfFixedConnectionPoint(from, p);
			this.SetOrigToPortSide(num4);
			this.SetToPortNumber(-1);
			if (num4 == 0 || num4 == 2) {
				if (flowDirection == 0 || flowDirection == 3) {
					this._offsetToFixedToPoint = (from.GetCoord(levelFlow) + from
							.GetSize(levelFlow)) - p[levelFlow];

					return;
				}
				this._offsetToFixedToPoint = p[levelFlow]
						- from.GetCoord(levelFlow);

				return;
			}
			if (flowDirection == 2 || flowDirection == 3) {
				this._offsetToFixedToPoint = (from.GetCoord(index) + from
						.GetSize(index)) - p[index];

				return;
			}
			this._offsetToFixedToPoint = p[index] - from.GetCoord(index);
		}

	}

	public Integer TranslateSide(Integer side) {
		if (side == 6) {

			return 1;
		} else if (side == 7) {

			return 3;
		} else if (side == 8) {

			return 0;
		} else if (side == 9) {

			return 2;
		}

		return -1;

	}

	private class AnonClass_1 implements HNodeIterator {
		private HLink __outerThis;

		public HNode node;

		public HSegment segment;

		public AnonClass_1(HLink input__outerThis) {
			this.__outerThis = input__outerThis;

			this.segment = this.__outerThis.GetStartSegment();
			this.node = this.segment.GetFrom().IsDummyNode() ? this.segment
					.GetFrom() : this.segment.GetTo();
		}

		public Boolean HasNext() {

			return this.node.IsDummyNode();

		}

		public void Init(HNode node) {
			throw (new system.Exception("HNodeIterator.init(node) in link"));

		}

		public HNode Next() {
			HNode node = this.node;

			this.segment = this.node.GetOpposite(this.segment);

			this.node = this.segment.GetOpposite(this.node);

			return node;

		}

	}

	private class AnonClass_2 implements HNodeIterator {
		public AnonClass_2() {
		}

		public Boolean HasNext() {

			return false;

		}

		public void Init(HNode node) {

		}

		public HNode Next() {

			return null;

		}

	}
}