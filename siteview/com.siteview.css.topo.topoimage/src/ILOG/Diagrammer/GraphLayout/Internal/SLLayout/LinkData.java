package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.ArgumentException;
import system.Math;
import system.Collections.ArrayList;
import ILOG.Diagrammer.GraphLayout.IGraphModel;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelUtil;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.SubgraphData;

public class LinkData extends
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeOrLinkData {
	private LinkShapeType _currentShapeType;

	private short _flags;

	private ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData _fromNodeData;

	private SLNodeSide _fromNodeSide;

	private ArrayList _intersectingObjects;

	private Integer _linkIndex;

	private LinkShape _linkShape;

	public float _linkWidth;

	private ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData _masterLink;

	private LinkShapeType[] _shapeTypes;

	private ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData _toNodeData;

	private SLNodeSide _toNodeSide;

	private Integer ADDED_IN_FROM_NODE_SIDE = 0x100;

	private Integer ADDED_IN_TO_NODE_SIDE = 0x200;

	public Integer FIXED_LINK_INDEX = -2;

	private Integer FROM_POINT_FIXED = 1;

	private Integer FROM_SIDE_FIXED = 4;

	public Integer INITIAL_INDEX = -1;

	private Integer INTERGRAPH_LINK = 0x800;

	private Integer IS_SLAVE = 0x20;

	private Integer OVERLAPPING_NODES = 0x400;

	private Integer QUASI_SELF_INTERGRAPH_LINK_DESTINATION = 0x2000;

	private Integer QUASI_SELF_INTERGRAPH_LINK_ORIGIN = 0x1000;

	private Integer SHAPE_POINTS_UP_TO_DATE = 0x80;

	private Integer SHAPE_TYPE_FIXED = 0x10;

	private Integer SHAPE_TYPE_UP_TO_DATE = 0x40;

	private Integer TO_POINT_FIXED = 2;

	private Integer TO_SIDE_FIXED = 8;

	public LinkData(ShortLinkAlgorithm layout, java.lang.Object link) {
		this(layout, link, null);
	}

	public LinkData(
			ShortLinkAlgorithm layout,
			java.lang.Object link,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData firstSlaveLink) {
		super(link);
		this._linkIndex = -1;
		this.Update(layout, link, firstSlaveLink);
	}

	private static void ApplyDirectOffsetCorrection(InternalPoint point0,
			InternalPoint point1, InternalPoint point2,
			InternalPoint masterPoint1, SLNodeSide nodeSide, float offset) {
		if (offset < 0f) {
			offset = -offset;
		}
		float tangentialCoord = nodeSide.GetTangentialCoord(point1);
		float num2 = nodeSide.GetTangentialCoord(point2) - tangentialCoord;
		float num3 = nodeSide.GetRadialCoord(point2)
				- nodeSide.GetRadialCoord(point1);
		if ((num2 != 0f) || (num3 != 0f)) {
			float num4 = (float) Math
					.Sqrt((double) ((num2 * num2) + (num3 * num3)));
			if (num4 >= 1E-07) {
				float num5 = offset / num4;
				float radialDelta = num5 * num2;
				float num7 = nodeSide.GetTangentialCoord(masterPoint1);
				if (tangentialCoord > num7) {
					radialDelta = -radialDelta;
				}
				nodeSide.TranslateOutside(point1, radialDelta);
			}
		}

	}

	public void ApplyOffset(InternalPoint masterFromPoint,
			InternalPoint masterToPoint, InternalPoint[] masterBendPoints,
			Integer nBends, float offset, float offsetOrig, float offsetDest,
			Boolean goingLeftOrigin, Boolean reversed,
			Boolean onlyFirstAndLastSeg) {
		if (masterBendPoints != null) {
			if (this._linkShape.GetNumberOfPoints() != (nBends + 2)) {
				throw (new system.Exception(
						clr.System.StringStaticWrapper
								.Concat(new java.lang.Object[] {
										"cannot apply offset if different number of points: ",
										this._linkShape.GetNumberOfPoints(),
										" and ", nBends + 2 })));
			}
			if (masterBendPoints.length < nBends) {
				throw (new system.Exception(
						clr.System.StringStaticWrapper
								.Concat(new java.lang.Object[] {
										"length of masterBendPoints (",
										masterBendPoints.length,
										") is smaller than nBends = ", nBends })));
			}
		} else {
			nBends = this._linkShape.GetNumberOfPoints() - 2;
		}
		Boolean onLeftSide = goingLeftOrigin;
		Boolean origin = !reversed;
		Boolean flag3 = this._currentShapeType.IsOrthogonal();
		InternalPoint pointAt = this.GetPointAt(origin, 0);
		InternalPoint point2 = this.GetPointAt(origin, 1);
		InternalPoint point3 = this.GetPointAt(origin, nBends);
		InternalPoint point4 = this.GetPointAt(origin, nBends + 1);
		InternalPoint point5 = null;
		InternalPoint point = null;
		if (masterFromPoint != null) {
			pointAt.SetLocation(masterFromPoint);
			point = masterBendPoints[nBends - 1];
			point3.SetLocation(point);
			point5 = masterBendPoints[0];
		}
		ApplyOrthogonalOffset(pointAt, point2, masterFromPoint, point5,
				offsetOrig, onLeftSide);
		if (flag3 && !onlyFirstAndLastSeg) {
			if (masterBendPoints != null) {
				for (Integer i = 1; i < nBends; i++) {
					ApplyOrthogonalOffset(this.GetPointAt(origin, i),
							this.GetPointAt(origin, i + 1),
							masterBendPoints[i - 1], masterBendPoints[i],
							offset, onLeftSide);
				}
			} else {
				for (Integer j = 1; j < nBends; j++) {
					ApplyOrthogonalOffset(this.GetPointAt(origin, j),
							this.GetPointAt(origin, j + 1), null, null, offset,
							onLeftSide);
				}
			}
		}
		ApplyOrthogonalOffset(point3, point4, point, masterToPoint, offsetDest,
				onLeftSide);
		if (!flag3) {
			ApplyDirectOffsetCorrection(pointAt, point2, point3, point5,
					this.GetNodeSide(origin), offsetOrig);
			ApplyDirectOffsetCorrection(point4, point3, point2, point,
					this.GetNodeSide(!origin), offsetDest);
		}
		this.ShapePointsModified();

	}

	private static void ApplyOrthogonalOffset(InternalPoint point1,
			InternalPoint point2, InternalPoint masterPoint1,
			InternalPoint masterPoint2, float offset, Boolean onLeftSide) {
		float num = 0f;
		float num2 = 0f;
		if (masterPoint1 != null) {
			num = masterPoint2.X - masterPoint1.X;
			num2 = masterPoint2.Y - masterPoint1.Y;
		} else {
			num = point2.X - point1.X;
			num2 = point2.Y - point1.Y;
		}
		if (num == 0f) {
			if (num2 == 0f) {
				point2.SetLocation(point1);
			} else {
				if ((onLeftSide && (num2 <= 0f))
						|| (!onLeftSide && (num2 > 0f))) {
					offset = -offset;
				}
				if (masterPoint1 == null) {
					point1.X += offset;
					point2.X += offset;
				} else {
					point1.X = masterPoint1.X + offset;
					point2.X = masterPoint2.X + offset;
					point2.Y = masterPoint2.Y;
				}
			}
		} else {
			if (num2 != 0f) {
				throw (new system.Exception(
						"ApplyOrthogonalOffset can only be applied to orthogonal shapes"));
			}
			if ((onLeftSide && (num >= 0f)) || (!onLeftSide && (num < 0f))) {
				offset = -offset;
			}
			if (masterPoint1 == null) {
				point1.Y += offset;
				point2.Y += offset;
			} else {
				point1.Y = masterPoint1.Y + offset;
				point2.Y = masterPoint2.Y + offset;
				point2.X = masterPoint2.X;
			}
		}

	}

	public void CleanMasterSlaveInfo(
			float linkOffset,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData firstSlaveLink) {
		this.SetSlave(false);
		this._masterLink = null;

	}

	private void ComputeAllowedShapes(ShortLinkAlgorithm layout,
			LinkShapeProducer shapeProducer) {
		LinkShapeType[] allowedLinkShapes = shapeProducer.GetAllowedLinkShapes(
				layout, this);
		if (allowedLinkShapes == null) {
			throw (new system.Exception("null allowed link shapes"));
		}
		this.SetShapeTypes(allowedLinkShapes);

	}

	public void ComputeLinkRegion(float minFinalSegmentLength,
			float linkOffset, Integer linkBundlesMode) {
		if (super.boundingBox == null) {
			super.boundingBox = new InternalRect(0f, 0f, 0f, 0f);
		}
		InternalRect linkConnectionRect = this.GetFromNode()
				.GetLinkConnectionRect();
		InternalRect rect2 = this.GetToNode().GetLinkConnectionRect();
		InternalRect boundingBox = super.boundingBox;
		float x = (linkConnectionRect.X <= rect2.X) ? linkConnectionRect.X
				: rect2.X;
		float y = (linkConnectionRect.Y <= rect2.Y) ? linkConnectionRect.Y
				: rect2.Y;
		float num3 = ((linkConnectionRect.X + linkConnectionRect.Width) >= (rect2.X + rect2.Width)) ? (linkConnectionRect.X + linkConnectionRect.Width)
				: (rect2.X + rect2.Width);
		float num4 = ((linkConnectionRect.Y + linkConnectionRect.Height) >= (rect2.Y + rect2.Height)) ? (linkConnectionRect.Y + linkConnectionRect.Height)
				: (rect2.Y + rect2.Height);

		if (this.IsFromPointFixed()) {
			InternalPoint connectionPoint = this.GetConnectionPoint(true);
			if (connectionPoint.X < x) {
				x = connectionPoint.X;
			} else if (connectionPoint.X > num3) {
				num3 = connectionPoint.X;
			}
			if (connectionPoint.Y < y) {
				y = connectionPoint.Y;
			} else if (connectionPoint.Y > num4) {
				num4 = connectionPoint.Y;
			}
		}

		if (this.IsToPointFixed()) {
			InternalPoint point2 = this.GetConnectionPoint(false);
			if (point2.X < x) {
				x = point2.X;
			} else if (point2.X > num3) {
				num3 = point2.X;
			}
			if (point2.Y < y) {
				y = point2.Y;
			} else if (point2.Y > num4) {
				num4 = point2.Y;
			}
		}
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData fromNode = this
				.GetFromNode();
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData toNode = this
				.GetToNode();
		float num5 = minFinalSegmentLength;
		if (linkBundlesMode != 0) {
			float num6 = fromNode.GetTotalIncidentLinkWidth()
					+ ((fromNode.GetDegree() - 1) * linkOffset);
			float num7 = toNode.GetTotalIncidentLinkWidth()
					+ ((toNode.GetDegree() - 1) * linkOffset);
			num5 += (num6 >= num7) ? num6 : num7;
		}
		if (num5 != 0f) {
			x -= num5;
			y -= num5;
			num3 += num5;
			num4 += num5;
		}
		boundingBox.Reshape(x, y, num3 - x, num4 - y);

	}

	public void CopyIncrementalData(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		this._currentShapeType = linkData.GetCurrentShapeType();

		this._fromNodeData = linkData.GetFromNode();

		this._toNodeData = linkData.GetToNode();

		this._fromNodeSide = linkData.GetNodeSide(true);

		this._toNodeSide = linkData.GetNodeSide(false);
		if (this._fromNodeSide != null) {
			this._fromNodeSide.Clean();
		}
		if (this._toNodeSide != null) {
			this._toNodeSide.Clean();
		}

	}

	public void CopyPointLocations(InternalPoint[] points, Boolean origin) {
		Integer numberOfPoints = this._linkShape.GetNumberOfPoints();
		for (Integer i = 0; i < numberOfPoints; i++) {
			points[i].SetLocation(this.GetPointAt(origin, i));
		}

	}

	public ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData GetConnectionNode(
			Boolean origin) {
		if (!origin) {

			return this._toNodeData;
		}

		return this._fromNodeData;

	}

	public InternalPoint GetConnectionPoint(Boolean origin) {
		if (!origin) {

			return this._linkShape.GetTo();
		}

		return this._linkShape.GetFrom();

	}

	public LinkShapeType GetCurrentShapeType() {

		return this._currentShapeType;

	}

	public ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData GetFromNode() {

		return this._fromNodeData;

	}

	public SLNodeSide GetFromNodeSide() {

		return this._fromNodeSide;

	}

	public Integer GetIndex() {

		return this._linkIndex;

	}

	public ArrayList GetIntersectingObjects() {

		return this._intersectingObjects;

	}

	public LinkShape GetLinkShape() {

		return this._linkShape;

	}

	public float GetLinkWidth() {

		return this._linkWidth;

	}

	public float GetLinkWidth(float linkOffset) {

		return this._linkWidth;

	}

	public ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData GetMasterLink() {

		return this._masterLink;

	}

	public SLNodeSide GetNodeSide(Boolean origin) {
		if (!origin) {

			return this._toNodeSide;
		}

		return this._fromNodeSide;

	}

	public Integer GetNumberOfIndividualLinks() {

		return 1;

	}

	public Integer GetOppositeSide(Boolean origin) {

		return this.GetNodeSide(!origin).GetSide();

	}

	public InternalPoint GetPointAt(Boolean origin, Integer index) {

		return this._linkShape.GetPointAt(origin, index);

	}

	public LinkShapeType GetShapeType(Integer index) {
		if (this._shapeTypes == null) {
			throw (new system.Exception("setShapeTypes must be called before"));
		}
		if (index >= this._shapeTypes.length) {
			throw (new ArgumentException(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] { "index ", index,
									" >= number of shape types = ",
									this._shapeTypes.length })));
		}

		return this._shapeTypes[index];

	}

	private LinkShapeType[] GetShapeTypes() {

		return this._shapeTypes;

	}

	public Integer GetShapeTypesCount() {
		if (this._shapeTypes == null) {

			return 0;
		}

		return this._shapeTypes.length;

	}

	public Integer GetSide(Boolean origin) {

		return this.GetNodeSide(origin).GetSide();

	}

	public SLNodeSide GetSideOfPoint(InternalPoint point, Boolean origin) {
		if (!origin) {

			return this._toNodeData.GetSideOfPoint(point,
					this.IsQuasiSelfInterGraphLinkDestination());
		}

		return this._fromNodeData.GetSideOfPoint(point,
				this.IsQuasiSelfInterGraphLinkOrigin());

	}

	public ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData GetToNode() {

		return this._toNodeData;

	}

	public SLNodeSide GetToNodeSide() {

		return this._toNodeSide;

	}

	private Boolean HasBeenAlreadyLaidOut() {

		return ((((this._fromNodeSide != null) && (this._toNodeSide != null)) && (this._fromNodeData != null)) && (this._toNodeData != null));

	}

	public Boolean HasFixedConnectionPoint() {

		if (!this.IsFixed() && !this.IsFromPointFixed()) {

			return this.IsToPointFixed();
		}

		return true;

	}

	private Boolean HasSameEndNodeBoxesAsPreviously() {

		return (this._fromNodeData.HasSameBoxesAsPreviously() && this._toNodeData
				.HasSameBoxesAsPreviously());

	}

	private Boolean IsAddedInFromNodeSide() {

		return ((this._flags & 0x100) != 0);

	}

	private Boolean IsAddedInToNodeSide() {

		return ((this._flags & 0x200) != 0);

	}

	public Boolean IsConnectionPointFixed(Boolean origin) {

		if (this.IsFixed()) {

			return true;
		}
		if (!origin) {

			return this.IsToPointFixed();
		}

		return this.IsFromPointFixed();

	}

	public Boolean IsFixed() {

		return (this._linkIndex == -2);

	}

	public Boolean IsFromPointFixed() {

		return ((this._flags & 1) != 0);

	}

	public Boolean IsFromSideFixed() {

		return ((this._flags & 4) != 0);

	}

	public Boolean IsGoingLeft(Boolean origin) {
		Integer num2 = null;
		Integer num3 = null;
		Integer numberOfPoints = this._linkShape.GetNumberOfPoints();
		if (numberOfPoints < 2) {
			throw (new system.Exception("link with less than 2 points: "
					+ numberOfPoints));
		}
		if (numberOfPoints >= 3) {
			if (origin) {
				num2 = 1;
				num3 = 2;
			} else {
				num2 = numberOfPoints - 2;
				num3 = numberOfPoints - 3;
			}
		} else if (origin) {
			num2 = 0;
			num3 = 1;
		} else {
			num2 = numberOfPoints - 1;
			num3 = numberOfPoints - 2;
		}
		InternalPoint pointAt = this.GetPointAt(origin, num2);
		InternalPoint point2 = this.GetPointAt(origin, num3);

		return (this.GetNodeSide(origin).GetTangentDelta(pointAt, point2) >= 0f);

	}

	public Boolean IsInterGraphLink() {

		return ((this._flags & 0x800) != 0);

	}

	@Override
	public Boolean IsIntersectingObjectValid(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {
		if ((super.get_nodeOrLink() != null)
				&& (super.get_nodeOrLink() == linkData.get_nodeOrLink())) {

			return false;
		}
		if (linkData == this) {
			throw (new system.Exception(
					"Internal error: should not be called  for the same "
							+ linkData));
		}

		return true;

	}

	@Override
	public Boolean IsLink() {

		return true;

	}

	public Boolean IsMaster() {

		return false;

	}

	@Override
	public Boolean IsNode() {

		return false;

	}

	public Boolean IsOrigin(SLNodeSide nodeSide) {
		if (this._fromNodeData == null) {
			throw (new system.Exception(
					"shouldn't be called before setting _fromNodeSide"));
		}

		return (this._fromNodeSide == nodeSide);

	}

	public Boolean IsOrigin(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData,
			SLNodeSide nodeSide) {

		if (!this.IsSelfLink()) {

			return (this._fromNodeData == nodeData);
		}

		return (this._fromNodeSide == nodeSide);

	}

	public Boolean IsOrthogonal() {

		return (((this._linkShape != null) && (this._currentShapeType != null)) && this._currentShapeType
				.IsOrthogonal());

	}

	public Boolean IsOverlapping() {

		return ((this._flags & 0x400) != 0);

	}

	public Boolean IsQuasiSelfInterGraphLinkDestination() {

		return ((this._flags & 0x2000) != 0);

	}

	public Boolean IsQuasiSelfInterGraphLinkOrigin() {

		return ((this._flags & 0x1000) != 0);

	}

	public Boolean IsSameSideSelfLink() {

		return ((this._fromNodeData.get_nodeOrLink() == this._toNodeData
				.get_nodeOrLink()) && (this._fromNodeSide == this._toNodeSide));

	}

	public Boolean IsSelfLink() {

		return (this._fromNodeData.get_nodeOrLink() == this._toNodeData
				.get_nodeOrLink());

	}

	private Boolean IsShapePointsUpToDate() {

		return ((this._flags & 0x80) != 0);

	}

	public Boolean IsShapeTypeFixed() {

		return ((this._flags & 0x10) != 0);

	}

	private Boolean IsShapeTypeUpToDate() {

		return ((this._flags & 0x40) != 0);

	}

	public Boolean IsSlave() {

		return ((this._flags & 0x20) != 0);

	}

	public Boolean IsToPointFixed() {

		return ((this._flags & 2) != 0);

	}

	public Boolean IsToSideFixed() {

		return ((this._flags & 8) != 0);

	}

	public void PrepareForBundleLayout(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData masterLinkData,
			Boolean copyPointLocations) {
		SLNodeSide toNodeSide = null;
		SLNodeSide fromNodeSide = null;
		Boolean reversed = masterLinkData.GetFromNode() != this._fromNodeData;
		if (this._linkShape == null) {
			this._linkShape = new LinkShape(masterLinkData.GetLinkShape(),
					reversed, copyPointLocations);
		} else {
			this._linkShape.CopyFrom(masterLinkData.GetLinkShape(), reversed,
					copyPointLocations);
		}

		this._linkIndex = masterLinkData.GetIndex();

		this._currentShapeType = masterLinkData.GetCurrentShapeType();

		this._shapeTypes = masterLinkData.GetShapeTypes();
		if (reversed) {

			this._currentShapeType = this._currentShapeType
					.GetReversedShapeType();

			toNodeSide = masterLinkData.GetToNodeSide();

			fromNodeSide = masterLinkData.GetFromNodeSide();
		} else {

			toNodeSide = masterLinkData.GetFromNodeSide();

			fromNodeSide = masterLinkData.GetToNodeSide();
		}
		this.SetNodeSides(toNodeSide, fromNodeSide);
		this.SetShapeTypeUpToDate(false);

	}

	public void RestoreCurrentShape(ShortLinkAlgorithm layout,
			float minFinalSegment, float linkOffset, Boolean reversed) {

		if (!this.IsFixed()) {

			if (this.IsSlave()) {
				throw (new system.Exception(
						"Internal error: shouldn't be called for slave links"));
			}
			if (this._currentShapeType == null) {
				this.ComputeAllowedShapes(layout,
						layout.GetLinkShapeProducer(super.get_nodeOrLink()));
			}
			LinkShapeType type = reversed ? this._currentShapeType
					.GetReversedShapeType() : this._currentShapeType;

			if (!this.IsShapeTypeUpToDate()) {
				this.SetNodeSides(type.GetFromNodeSide(this),
						type.GetToNodeSide(this));
				this.SetShapeTypeUpToDate(true);
			}

			if (!this.IsShapePointsUpToDate()) {
				type.ComputeIntermediatePoints(layout, this, minFinalSegment,
						reversed);
				this.SetShapePointsUpToDate(true);
			}
		}

	}

	private void SetAddedInFromNodeSide(Boolean added) {
		if (added) {
			this._flags = (short) (this._flags | 0x100);
		} else {
			this._flags = (short) (this._flags & -257);
		}

	}

	private void SetAddedInToNodeSide(Boolean added) {
		if (added) {
			this._flags = (short) (this._flags | 0x200);
		} else {
			this._flags = (short) (this._flags & -513);
		}

	}

	public void SetCurrentShapeType(LinkShapeType shapeType) {
		if (this._currentShapeType != shapeType) {
			this._currentShapeType = shapeType;
			if (this._linkShape != null) {
				this._linkShape.InvalidateBBox();
			}
			this.SetShapeTypeUpToDate(false);
		}

	}

	public void SetDestinationLinkOffset(float offset) {

	}

	private void SetFixed(Boolean f) {
		this._linkIndex = f ? -2 : -1;

	}

	private void SetFromPointFixed(Boolean f) {
		if (f) {
			this._flags = (short) (this._flags | 1);
		} else {
			this._flags = (short) (this._flags & -2);
		}

	}

	private void SetFromSideFixed(Boolean f) {
		if (f) {
			this._flags = (short) (this._flags | 4);
		} else {
			this._flags = (short) (this._flags & -5);
		}

	}

	public void SetIndex(Integer index) {
		this._linkIndex = index;

	}

	private void SetInterGraphLink(Boolean s) {
		if (s) {
			this._flags = (short) (this._flags | 0x800);
		} else {
			this._flags = (short) (this._flags & -2049);
		}

	}

	public void SetIntersectingObjects(ArrayList vect) {
		this._intersectingObjects = vect;

	}

	public void SetMasterLink(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData masterLink) {
		this._masterLink = masterLink;

	}

	public void SetNodeSides(SLNodeSide fromNodeSide, SLNodeSide toNodeSide) {
		this.SetNodeSides(fromNodeSide, toNodeSide, false);

	}

	public void SetNodeSides(SLNodeSide fromNodeSide, SLNodeSide toNodeSide,
			Boolean forceAddition) {
		if ((fromNodeSide == null) || (toNodeSide == null)) {
			throw (new system.Exception("from/to node side cannot be null"));
		}
		Boolean flag = this.IsAddedInFromNodeSide();
		Boolean flag2 = this.IsAddedInToNodeSide();
		if ((((fromNodeSide != this._fromNodeSide) || (toNodeSide != this._toNodeSide)) || (forceAddition || !flag))
				|| !flag2) {

			if (this.IsSlave()) {
				this._fromNodeSide = fromNodeSide;
				this._toNodeSide = toNodeSide;
			} else {
				Boolean flag3 = false;
				Boolean flag4 = false;
				Boolean flag5 = false;

				if (this.IsSelfLink()) {
					if (this._fromNodeSide == this._toNodeSide) {
						flag4 = true;
					}
					if (fromNodeSide == toNodeSide) {
						flag5 = true;
					}
				}
				if ((this._fromNodeSide != fromNodeSide) || !flag) {
					if (this._fromNodeSide != null) {
						this._fromNodeSide.RemoveLink(this, true);
					}
					this._fromNodeSide = fromNodeSide;
					this._fromNodeSide.AddLink(this, true);
					this.SetAddedInFromNodeSide(true);
					flag3 = true;
				}
				if ((this._toNodeSide != toNodeSide) || !flag2) {
					if ((this._toNodeSide != null) && !flag4) {
						this._toNodeSide.RemoveLink(this, false);
					}
					this._toNodeSide = toNodeSide;
					if (!flag5) {
						this._toNodeSide.AddLink(this, false);
					}
					this.SetAddedInToNodeSide(true);
					flag3 = true;
				}
				if (flag3) {
					if (this._linkShape != null) {
						this._linkShape.InvalidateBBox();
					}
					this.SetShapeTypeUpToDate(false);
				}
			}
		}

	}

	public void SetOriginLinkOffset(float offset) {

	}

	public void SetOverlapping(Boolean overlapping) {
		if (overlapping) {
			this._flags = (short) (this._flags | 0x400);
		} else {
			this._flags = (short) (this._flags & -1025);
		}

	}

	private void SetQuasiSelfInterGraphLinkDestination(Boolean s) {
		if (s) {
			this._flags = (short) (this._flags | 0x2000);
		} else {
			this._flags = (short) (this._flags & -8193);
		}

	}

	private void SetQuasiSelfInterGraphLinkOrigin(Boolean s) {
		if (s) {
			this._flags = (short) (this._flags | 0x1000);
		} else {
			this._flags = (short) (this._flags & -4097);
		}

	}

	private void SetShapePointsUpToDate(Boolean uptodate) {
		if (uptodate) {
			this._flags = (short) (this._flags | 0x80);
		} else {
			this._flags = (short) (this._flags & -129);
		}

	}

	private void SetShapeTypeFixed(Boolean f) {
		if (f) {
			this._flags = (short) (this._flags | 0x10);
		} else {
			this._flags = (short) (this._flags & -17);
		}

	}

	private void SetShapeTypes(LinkShapeType[] shapeTypes) {
		this._shapeTypes = shapeTypes;
		if (shapeTypes != null) {
			this.SetCurrentShapeType(shapeTypes[0]);
		}
		this.SetShapeTypeUpToDate(false);

	}

	private void SetShapeTypeUpToDate(Boolean uptodate) {
		if (uptodate) {
			this._flags = (short) (this._flags | 0x40);
		} else {
			this._flags = (short) (this._flags & -65);
			this.SetShapePointsUpToDate(false);
		}

	}

	public void SetSlave(Boolean slave) {
		if (slave) {
			this._flags = (short) (this._flags | 0x20);
		} else {
			this._flags = (short) (this._flags & -33);
		}

	}

	private void SetToPointFixed(Boolean f) {
		if (f) {
			this._flags = (short) (this._flags | 2);
		} else {
			this._flags = (short) (this._flags & -3);
		}

	}

	private void SetToSideFixed(Boolean f) {
		if (f) {
			this._flags = (short) (this._flags | 8);
		} else {
			this._flags = (short) (this._flags & -9);
		}

	}

	public void ShapePointsModified() {
		this.SetShapePointsUpToDate(false);
		if (this._linkShape != null) {
			this._linkShape.InvalidateBBox();
		}

	}

	public void ShapeTypeModified() {
		this.SetShapeTypeUpToDate(false);

	}

	public void Update(
			ShortLinkAlgorithm layout,
			java.lang.Object link,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData firstSlaveLink) {
		IGraphModel graphModel = layout.GetGraphModel();

		this._linkWidth = graphModel.GetLinkWidth(super.get_nodeOrLink());
		Boolean incrementalMode = layout.IsIncrementalMode();
		float linkOffset = layout.GetLinkOffset();
		Boolean f = layout.IsFixedOrNoReshape(link);
		Boolean flag3 = layout.IsFromPointFixed(link);
		Boolean flag4 = layout.IsToPointFixed(link);
		this.SetOverlapping(false);
		this.SetQuasiSelfInterGraphLinkOrigin(false);
		this.SetQuasiSelfInterGraphLinkDestination(false);
		if (incrementalMode) {
			SLNodeSide fromNodeSide = this.GetFromNodeSide();
			SLNodeSide toNodeSide = this.GetToNodeSide();
			if ((fromNodeSide == null) || (toNodeSide == null)) {
				ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData masterLink = this
						.GetMasterLink();
				if (masterLink != null) {

					fromNodeSide = masterLink.GetFromNodeSide();

					toNodeSide = masterLink.GetToNodeSide();
				}
			}
			this._fromNodeSide = fromNodeSide;
			this._toNodeSide = toNodeSide;
		}
		this.CleanMasterSlaveInfo(linkOffset, firstSlaveLink);
		this.SetFixed(f);
		this.SetFromPointFixed(flag3);
		this.SetToPointFixed(flag4);
		this.SetFromSideFixed(false);
		this.SetToSideFixed(false);
		this.SetShapeTypeFixed(false);
		this.SetAddedInFromNodeSide(false);
		this.SetAddedInToNodeSide(false);
		this.ShapePointsModified();
		if (incrementalMode) {
			this.UpdateIncrementalLinkReshapeRelatedData(layout);
			if (this.IsShapeTypeFixed() && (this._linkShape != null)) {
				this.UpdateNodeSides(true, false);
				this.UpdateInterGraphLinkInfo(layout);

				return;
			}
		}

		f = this.IsFixed();

		flag3 = this.IsFromPointFixed();

		flag4 = this.IsToPointFixed();

		if (!this.IsShapeTypeFixed()) {
			this._currentShapeType = null;
			this.ShapeTypeModified();
		}
		java.lang.Object from = graphModel.GetFrom(super.get_nodeOrLink());
		java.lang.Object to = graphModel.GetTo(super.get_nodeOrLink());

		this._fromNodeData = layout
				.GetOrCreateNodeData(graphModel, from, false);

		this._toNodeData = layout.GetOrCreateNodeData(graphModel, to, false);
		InternalRect linkConnectionRect = this._fromNodeData
				.GetLinkConnectionRect();
		InternalRect rect2 = this._toNodeData.GetLinkConnectionRect();
		InternalPoint fromPoint = (f || flag3) ? GraphModelUtil.GetPointAt(
				graphModel, super.get_nodeOrLink(), 0) : new InternalPoint(
				linkConnectionRect.X + (linkConnectionRect.Width * 0.5f),
				linkConnectionRect.Y + (linkConnectionRect.Height * 0.5f));
		InternalPoint toPoint = (f || flag4) ? GraphModelUtil.GetPointAt(
				graphModel, super.get_nodeOrLink(),
				graphModel.GetLinkPoints(super.get_nodeOrLink()).length - 1)
				: new InternalPoint(rect2.X + (rect2.Width * 0.5f), rect2.Y
						+ (rect2.Height * 0.5f));
		this.UpdateInterGraphLinkInfo(layout);
		if (f) {

			super.boundingBox = GraphModelUtil.BoundingBox(graphModel,
					super.get_nodeOrLink());
			this._linkShape = new LinkShape(layout, this, fromPoint, toPoint);
		} else {
			this._linkShape = new LinkShape(layout.GetLinkShapeProducer(link)
					.GetMaxNumberOfBends());
			this._linkShape.SetFrom(fromPoint);
			this._linkShape.SetTo(toPoint);
			this.UpdateNodeSides(incrementalMode, false);
		}

	}

	private void UpdateIncrementalLinkReshapeRelatedData(
			ShortLinkAlgorithm layout) {

		if (this.HasBeenAlreadyLaidOut()) {
			Integer num = this.HasSameEndNodeBoxesAsPreviously() ? layout
					.GetIncrementalUnmodifiedLinkReshapeMode(super
							.get_nodeOrLink()) : layout
					.GetIncrementalModifiedLinkReshapeMode(super
							.get_nodeOrLink());
			if (num == 0) {

				return;
			} else if (num == 3) {
				this.SetFixed(true);

				return;
			} else if (num == 4) {
				if (this._currentShapeType != null) {
					this.SetIndex(0x7fffffff);
					this.SetShapeTypeFixed(true);
				}

				return;
			} else if (num == 5) {
				this.SetFromSideFixed(true);
				this.SetToSideFixed(true);

				return;
			} else if (num == 6) {
				this.SetFromPointFixed(true);
				this.SetToPointFixed(true);

				return;
			}
			throw (new system.Exception(
					"unexpected incremental link reshape mode: " + num));
		}

	}

	private void UpdateInterGraphLinkInfo(ShortLinkAlgorithm layout) {
		this.SetInterGraphLink(false);
		this.SetQuasiSelfInterGraphLinkOrigin(false);
		this.SetQuasiSelfInterGraphLinkDestination(false);
		SubgraphData interGraphLinksRoutingModel = layout
				.GetInterGraphLinksRoutingModel();
		if (interGraphLinksRoutingModel != null) {
			Boolean s = layout.GetOriginalGraphModel().IsInterGraphLink(
					interGraphLinksRoutingModel.GetOriginal(super
							.get_nodeOrLink()));
			this.SetInterGraphLink(s);
			if (s) {

				if (interGraphLinksRoutingModel.NodeInsideGraph(
						this._fromNodeData.get_nodeOrLink(),
						this._toNodeData.get_nodeOrLink())) {
					this.SetQuasiSelfInterGraphLinkDestination(true);
				} else if (interGraphLinksRoutingModel.NodeInsideGraph(
						this._toNodeData.get_nodeOrLink(),
						this._fromNodeData.get_nodeOrLink())) {
					this.SetQuasiSelfInterGraphLinkOrigin(true);
				}
			}
		}

	}

	private void UpdateNodeSides(Boolean incrementalMode,
			Boolean sameShapeForMultipleLinks) {

		if (incrementalMode && (!sameShapeForMultipleLinks || !this.IsSlave())) {
			SLNodeSide fromNodeSide = this.GetFromNodeSide();
			if (fromNodeSide != null) {
				fromNodeSide.AddLink(this, true);
				this.SetAddedInFromNodeSide(true);
			}

			if (!this.IsSameSideSelfLink()) {

				fromNodeSide = this.GetToNodeSide();
				if (fromNodeSide != null) {
					fromNodeSide.AddLink(this, false);
					this.SetAddedInToNodeSide(true);
				}
			}
		}

	}

}