package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;
import system.Collections.*;

public abstract class SLNodeSide {
	private Boolean _directionFlag = false;

	public Boolean _isReversedDirection = false;

	private Boolean _layoutUpToDate = false;

	private ArrayList _vectFixedLinks;

	private ArrayList _vectLinksDest;

	private ArrayList _vectLinksOrig;

	private ArrayList _vectNonFixedLinks;

	private float SMALL_DELTA = 0.01f;

	public SLNodeSide() {
		this(false);
	}

	public SLNodeSide(Boolean reversedDirection) {
		this._isReversedDirection = reversedDirection;
	}

	public void AddLink(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			Boolean origin) {
		if ((linkData.IsFixed() || (origin && linkData.IsFromPointFixed()))
				|| (!origin && linkData.IsToPointFixed())) {
			if (this._vectFixedLinks == null) {
				this._vectFixedLinks = new ArrayList(origin ? linkData
						.GetFromNode().GetDegree() : linkData.GetToNode()
						.GetDegree());
			}
			this._vectFixedLinks.Add(linkData);
		} else {
			if (this._vectNonFixedLinks == null) {
				this._vectNonFixedLinks = new ArrayList(origin ? linkData
						.GetFromNode().GetDegree() : linkData.GetToNode()
						.GetDegree());
			}
			this._vectNonFixedLinks.Add(linkData);
		}
		this._layoutUpToDate = false;

	}

	public void Clean() {
		if (this._vectLinksOrig != null) {
			this._vectLinksOrig.Clear();
		}
		if (this._vectLinksDest != null) {
			this._vectLinksDest.Clear();
		}
		if (this._vectFixedLinks != null) {
			this._vectFixedLinks.Clear();
		}
		if (this._vectNonFixedLinks != null) {
			this._vectNonFixedLinks.Clear();
		}
		this._layoutUpToDate = false;

	}

	public abstract Boolean CompareLinksByOppositeDirection(
			Integer oppositeDirection, InternalPoint refPoint1,
			InternalPoint refPoint2);

	public Boolean CompareOrthogonalLinks(ShortLinkAlgorithm layout,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData link1,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData link2,
			Boolean origin1, Boolean origin2) {
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data2 = null;
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data3 = null;
		Boolean flag5 = null;
		Boolean flag6 = null;
		Integer num5 = null;
		Integer num6 = null;
		InternalPoint pointAt = link1.GetPointAt(origin1, 1);
		InternalPoint point2 = link1.GetPointAt(origin1, 2);
		InternalPoint point3 = link2.GetPointAt(origin2, 1);
		InternalPoint point4 = link2.GetPointAt(origin2, 2);
		Boolean flag = this.GetTangentDelta(pointAt, point2) >= 0f;
		Boolean flag2 = this.GetTangentDelta(point3, point4) >= 0f;
		if (flag) {
			if (!flag2) {

				return true;
			}
		} else if (flag2) {

			return false;
		}
		Boolean origin = !origin1;
		Boolean flag4 = !origin2;
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData connectionNode = link1
				.GetConnectionNode(origin);
		SLNodeSide nodeSide = link1.GetNodeSide(origin);
		if (((link2.GetConnectionNode(flag4) == connectionNode) && (nodeSide == link2
				.GetNodeSide(flag4)))
				&& (link1.GetCurrentShapeType().GetNumberOfBends() == link2
						.GetCurrentShapeType().GetNumberOfBends())) {
			InternalPoint connectionPoint = link1.GetConnectionPoint(origin);
			InternalPoint point6 = link2.GetConnectionPoint(flag4);

			return (connectionPoint.equals(point6) || this
					.CompareLinksByOppositeDirection(nodeSide.GetDirection(),
							connectionPoint, point6));
		}
		Integer numberOfPoints = link1.GetLinkShape().GetNumberOfPoints();
		Integer num2 = link2.GetLinkShape().GetNumberOfPoints();
		if ((numberOfPoints < 3) || (num2 < 3)) {
			throw (new system.Exception(
					"cannot refine a link with less than three points"));
		}
		if ((numberOfPoints == 3) && (num2 == 3)) {

			return this.CompareLinksByOppositeDirection(
					nodeSide.GetDirection(), link1.GetConnectionPoint(origin),
					link2.GetConnectionPoint(flag4));
		}
		if ((numberOfPoints == 3) && (num2 >= 4)) {
			InternalPoint point7 = link2.GetPointAt(origin2, 3);
			if ((this.GetRadialDelta(point4, point7) < 0f)
					&& (this.GetRadialDelta(pointAt, point3) < 0f)) {

				return !flag;
			}
		} else if ((numberOfPoints >= 4) && (num2 == 3)) {
			InternalPoint point8 = link1.GetPointAt(origin1, 3);
			if ((this.GetRadialDelta(point2, point8) < 0f)
					&& (this.GetRadialDelta(point3, pointAt) < 0f)) {

				return flag;
			}
		}
		InternalPoint[] points = layout.GetTempPoints1();
		InternalPoint[] pointArray2 = layout.GetTempPoints2();
		if (flag) {
			data2 = link1;
			data3 = link2;
			flag5 = origin1;
			flag6 = origin2;
			num5 = numberOfPoints;
			num6 = num2;
		} else {
			data2 = link2;
			data3 = link1;
			flag5 = origin2;
			flag6 = origin1;
			num5 = num2;
			num6 = numberOfPoints;
		}
		data2.CopyPointLocations(points, flag5);
		data3.CopyPointLocations(pointArray2, flag6);
		float linkWidth = data2.GetLinkWidth();
		float num4 = data3.GetLinkWidth();
		InternalPoint point9 = null;
		InternalPoint point10 = null;
		if (num5 >= 4) {
			point9 = points[3];
			if (num5 >= 5) {
				point10 = points[4];
			}
		}
		InternalPoint point11 = null;
		InternalPoint point12 = null;
		InternalPoint point13 = null;
		if (num6 >= 4) {
			point11 = pointArray2[3];
			if (num6 >= 5) {
				point12 = pointArray2[4];
				if (num6 >= 6) {
					point13 = pointArray2[5];
				}
			}
		}

		if (this.RefineLinksOrthogonalWithPoints(layout, points[1], points[2],
				point9, point10, pointArray2[1], pointArray2[2], point11,
				point12, point13, linkWidth, num4, flag, flag2,
				data2.IsSameSideSelfLink(), num5 - 2, num6 - 2,
				data3.boundingBox, link2.GetNodeSide(flag4), 0.01f, false, true)) {

			return false;
		}

		return !this.IntersectsOrthogonalWithParallelStart(layout, points,
				pointArray2, num5, num6, flag);

	}

	public void ComputeConnectionPoint(InternalRect linkConnectionRect,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			Boolean origin, float tangentialDistForConnectionPoint) {
		LinkShape linkShape = linkData.GetLinkShape();
		InternalPoint point = origin ? linkShape.GetFrom() : linkShape.GetTo();
		float tanCoord = this.GetMinTangentialCoord(linkConnectionRect)
				+ tangentialDistForConnectionPoint;
		float radialCoord = this.GetRadialCoord(linkConnectionRect);
		this.SetTangentialCoord(point, tanCoord);
		this.SetRadialCoord(point, radialCoord);
		Integer num3 = linkShape.GetNumberOfPoints() - 2;
		if (num3 >= 1) {
			InternalPoint pointAt = linkShape.GetPointAt(origin, 1);
			this.SetTangentialCoord(pointAt, tanCoord);
		}

	}

	public abstract void CopyRadialCoord(InternalPoint source,
			InternalPoint dest);

	private void FillVectLinksOrigDest() {
		if (this._vectLinksOrig != null) {
			this._vectLinksOrig.Clear();
		}
		if (this._vectLinksDest != null) {
			this._vectLinksDest.Clear();
		}
		if (this._vectNonFixedLinks != null) {
			Integer count = this._vectNonFixedLinks.get_Count();
			for (Integer i = 0; i < count; i++) {
				this.FillVectLinksOrigDest(
						(ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) this._vectNonFixedLinks
								.get_Item(i), count);
			}
		}

	}

	private void FillVectLinksOrigDest(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			Integer nLinks) {
		Boolean origin = linkData.IsOrigin(this);
		if (linkData.IsSameSideSelfLink()
				|| this.IsGoingTowardOrigin(linkData, origin)) {
			if (this._vectLinksOrig == null) {
				this._vectLinksOrig = new ArrayList(nLinks);
			}
			this._vectLinksOrig.Add(linkData);
		} else {
			if (this._vectLinksDest == null) {
				this._vectLinksDest = new ArrayList(nLinks);
			}
			this._vectLinksDest.Add(linkData);
		}
		this._layoutUpToDate = false;

	}

	public Integer GetDirection() {
		if (!this._isReversedDirection) {

			return this.GetSide();
		}

		return this.GetOppositeSide();

	}

	public abstract float GetMaxRadialCoord(InternalRect rect);

	public abstract float GetMaxTangentialCoord(InternalRect rect);

	public abstract float GetMinRadialCoord(InternalRect rect);

	public abstract float GetMinTangentialCoord(InternalRect rect);

	public Integer GetOppositeDirection() {
		if (!this._isReversedDirection) {

			return this.GetOppositeSide();
		}

		return this.GetSide();

	}

	public abstract Integer GetOppositeSide();

	public abstract float GetRadialCoord(InternalPoint point);

	public abstract float GetRadialCoord(InternalRect linkConnectionRect);

	public float GetRadialDelta(InternalPoint point1, InternalPoint point2) {

		return this.GetRadialDelta(this.GetRadialCoord(point1),
				this.GetRadialCoord(point2));

	}

	public float GetRadialDelta(InternalPoint point, InternalRect rect) {

		return this.GetRadialDelta(this.GetRadialCoord(point),
				this.GetRadialCoord(rect));

	}

	public abstract float GetRadialDelta(float radCoord1, float radCoord2);

	public abstract Integer GetSide();

	public abstract Integer GetSidePosition(Integer side);

	public abstract float GetTangentDelta(InternalPoint point1,
			InternalPoint point2);

	public abstract float GetTangentialCoord(InternalPoint point);

	private Boolean IntersectsOrthogonalWithParallelStart(
			ShortLinkAlgorithm layout, InternalPoint[] points1,
			InternalPoint[] points2, Integer nPoints1, Integer nPoints2,
			Boolean goingLeft1) {
		Integer num = nPoints1 - 1;
		Integer num2 = nPoints2 - 1;
		InternalPoint point3 = layout.GetTempPoint1();
		InternalPoint point4 = layout.GetTempPoint2();
		float tanDelta = goingLeft1 ? -0.01f : 0.01f;
		for (Integer i = 0; i < num; i++) {
			InternalPoint point = points1[i];
			InternalPoint point2 = points1[i + 1];
			for (Integer j = ((i % 2) == 0) ? 1 : 0; j < num2; j += 2) {
				point3.SetLocation(points2[j]);
				point4.SetLocation(points2[j + 1]);
				this.TranslateLeft(point3, tanDelta);
				this.TranslateLeft(point4, tanDelta);

				if (LayoutUtil.IntersectsOrthogonal(point.X, point.Y, point2.X,
						point2.Y, point3.X, point3.Y, point4.X, point4.Y)) {

					return true;
				}
			}
		}

		return false;

	}

	private Boolean IsGoingTowardOrigin(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			Boolean origin) {
		InternalPoint pointAt = linkData.GetPointAt(origin, 1);
		InternalPoint point2 = linkData.GetPointAt(origin, 2);
		float tangentDelta = this.GetTangentDelta(pointAt, point2);
		if (tangentDelta > 0f) {

			return true;
		}
		if (tangentDelta < 0f) {

			return false;
		}
		this._directionFlag = !this._directionFlag;

		return this._directionFlag;

	}

	public abstract Boolean IsOnLeft(Boolean goingLeft);

	public void Layout(ShortLinkAlgorithm layout,
			NodeSideLayouter sideLayouter, IncidentLinksSorter linkSorter,
			IncidentLinksRefiner linksRefiner,
			ILinkConnectionBoxProvider connectionBoxInterface,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData,
			float linkOffset, float minFinalSegmentLength, Integer bundleMode) {
		if (!this._layoutUpToDate) {
			this.FillVectLinksOrigDest();
			sideLayouter.Layout(layout, linkSorter, linksRefiner,
					connectionBoxInterface, nodeData, this,
					this._vectLinksOrig, this._vectLinksDest,
					this._vectFixedLinks, linkOffset, minFinalSegmentLength,
					bundleMode);
			this._layoutUpToDate = true;
		}

	}

	public abstract void MoveToDefaultConnectionPoint(InternalPoint point,
			InternalRect rect);

	private Boolean OverlappingRadially(InternalPoint point1,
			InternalPoint point2, InternalPoint point3, InternalPoint point4,
			float halfWidth1, float halfWidth2) {

		return LayoutUtil.OverlappingCoords(this.GetRadialCoord(point1),
				this.GetRadialCoord(point2), this.GetRadialCoord(point3),
				this.GetRadialCoord(point4), halfWidth1, halfWidth2);

	}

	private Boolean OverlappingTangentially(InternalPoint point1,
			InternalPoint point2, InternalPoint point3, InternalPoint point4,
			float halfWidth1, float halfWidth2) {

		return LayoutUtil.OverlappingCoords(this.GetTangentialCoord(point1),
				this.GetTangentialCoord(point2),
				this.GetTangentialCoord(point3),
				this.GetTangentialCoord(point4), halfWidth1, halfWidth2);

	}

	public void Refine(ShortLinkAlgorithm layout,
			NodeSideLayouter sideLayouter, IncidentLinksRefiner linksRefiner,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData,
			float linkOffset, float minFinalSegmentLength, Integer bundleMode) {
		sideLayouter.Refine(linksRefiner, this._vectLinksOrig,
				this._vectLinksDest, this._vectFixedLinks, nodeData, this,
				linkOffset, minFinalSegmentLength, bundleMode);

	}

	public Boolean RefineLinksOrthogonalWithPoints(ShortLinkAlgorithm layout,
			InternalPoint point11, InternalPoint point12,
			InternalPoint point13, InternalPoint point14,
			InternalPoint point21, InternalPoint point22,
			InternalPoint point23, InternalPoint point24,
			InternalPoint point25, float linkWidth1, float linkWidth2,
			Boolean goingLeft1, Boolean goingLeft2,
			Boolean firstLinkIsSameSideSelfLink, Integer nBends1,
			Integer nBends2, InternalRect linkRegion2, SLNodeSide oppositeSide,
			float linkOffset, Boolean sameOpposite,
			Boolean checkDangerousRefinement) {
		float num = linkWidth1 * 0.5f;
		float num2 = linkWidth2 * 0.5f;
		float radialDelta = (linkOffset + num) + num2;
		Boolean flag = this.OverlappingTangentially(point11, point12, point21,
				point22, num, num2);
		InternalPoint point = layout.GetTempPoint1();
		if (flag) {
			point.SetLocation(point11);
			this.TranslateOutside(point, radialDelta);
			if (this.GetRadialDelta(point21, point) > 0f) {
				if ((point23 == null) && checkDangerousRefinement) {

					return true;
				}
				if (((nBends2 == 2) && (this.GetRadialDelta(point23, point21) < 0f))
						&& (this.GetRadialDelta(point, point23) < num2)) {
					if (checkDangerousRefinement) {

						return true;
					}
					point.SetLocation(point23);
					this.TranslateOutside(point, -num2);
				}
				this.SetRadialCoord(point21, this.GetRadialCoord(point));
				this.SetRadialCoord(point22, this.GetRadialCoord(point));

				if (checkDangerousRefinement
						&& !linkRegion2.Inside(point21.X, point21.Y)) {

					return true;
				}
			}
			if (((nBends2 >= 3) && (point13 != null))
					&& (goingLeft1 == goingLeft2)) {
				float num4 = this.GetRadialDelta(point12, point13);
				float num5 = this.GetRadialDelta(point22, point23);
				if ((((num4 < 0f) && (num5 < 0f)) || ((num4 > 0f) && (num5 > 0f)))
						&& this.OverlappingRadially(point12, point13, point22,
								point23, num, num2)) {
					point.SetLocation(point12);
					this.TranslateLeft(point,
							goingLeft2 ? ((num4 <= 0f) ? radialDelta
									: -radialDelta)
									: ((num4 >= 0f) ? radialDelta
											: -radialDelta));
					float tangentDelta = this.GetTangentDelta(point22, point);
					Boolean flag2 = false;
					if (goingLeft2) {
						if (num4 <= 0f) {
							if (tangentDelta > 0f) {
								flag2 = true;
							}
						} else if (tangentDelta < 0f) {
							flag2 = true;
						}
					} else if (num4 <= 0f) {
						if (tangentDelta < 0f) {
							flag2 = true;
						}
					} else if (tangentDelta > 0f) {
						flag2 = true;
					}
					if (flag2) {
						float num7 = this.GetTangentDelta(point23, point24);
						float num8 = this.GetTangentDelta(point, point24);
						if ((((num8 >= 0f) && (num7 >= 0f)) && ((nBends2 != 3) || (num8 >= num2)))
								|| (((num8 <= 0f) && (num7 < 0f)) && ((nBends2 != 3) || (-num8 >= num2)))) {
							float tangentialCoord = this
									.GetTangentialCoord(point);
							this.SetTangentialCoord(point22, tangentialCoord);
							this.SetTangentialCoord(point23, tangentialCoord);

							if (checkDangerousRefinement
									&& !linkRegion2
											.Inside(point22.X, point22.Y)) {

								return true;
							}
						}
					}
					if (((nBends2 >= 4) && (point14 != null))
							&& this.OverlappingTangentially(point13, point14,
									point23, point24, num, num2)) {
						point.SetLocation(point13);
						float num10 = this.GetTangentDelta(point13, point14);
						this.TranslateOutside(
								point,
								goingLeft2 ? ((num4 >= 0f) ? ((num10 >= 0f) ? radialDelta
										: -radialDelta)
										: ((num10 >= 0f) ? radialDelta
												: -radialDelta))
										: ((num4 >= 0f) ? ((num10 <= 0f) ? radialDelta
												: -radialDelta)
												: ((num10 <= 0f) ? radialDelta
														: -radialDelta)));
						float num11 = this.GetRadialDelta(point23, point);
						if ((goingLeft2 && (((num10 >= 0f) && (num11 > 0f)) || ((num10 <= 0f) && (num11 < 0f))))
								|| (!goingLeft2 && (((num10 >= 0f) && (num11 < 0f)) || ((num10 <= 0f) && (num11 > 0f))))) {
							if (nBends2 == 4) {
								float num12 = this.GetRadialDelta(point24,
										point25);
								float num13 = this.GetRadialDelta(point,
										point25);
								if ((((num12 < 0f) && (num13 >= 0f)) || ((num12 > 0f) && (num13 <= 0f)))
										|| ((Math.Abs(num12) >= num2) && (Math
												.Abs(num13) < num2))) {
									if (checkDangerousRefinement) {

										return true;
									}
									point.SetLocation(point25);
									oppositeSide.TranslateOutside(point, num2);
								}
							}
							float radialCoord = this.GetRadialCoord(point);
							this.SetRadialCoord(point23, radialCoord);
							this.SetRadialCoord(point24, radialCoord);

							if (checkDangerousRefinement
									&& !linkRegion2
											.Inside(point23.X, point23.Y)) {

								return true;
							}
						}
					}
				}
			}
		}

		return false;

	}

	public Boolean RemoveLink(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			Boolean origin) {
		Boolean flag = false;
		if ((linkData.IsFixed() || (origin && linkData.IsFromPointFixed()))
				|| (!origin && linkData.IsToPointFixed())) {
			if (this._vectFixedLinks != null) {

				flag = TranslateUtil.Remove(this._vectFixedLinks, linkData);
			}
		} else if (this._vectNonFixedLinks != null) {

			flag = TranslateUtil.Remove(this._vectNonFixedLinks, linkData);
		}
		this._layoutUpToDate = false;

		return flag;

	}

	public void SetLayoutUpToDate(Boolean uptodate) {
		this._layoutUpToDate = uptodate;

	}

	public abstract void SetRadialCoord(InternalPoint point, float radCoord);

	public abstract void SetTangentialCoord(InternalPoint point, float tanCoord);

	public abstract void TranslateLeft(InternalPoint point, float tanDelta);

	public abstract void TranslateOutside(InternalPoint point, float radialDelta);

	public final static class BottomSide extends SLNodeSide {
		public BottomSide() {
		}

		public BottomSide(Boolean reversedOrientation) {
			super(reversedOrientation);
		}

		@Override
		public Boolean CompareLinksByOppositeDirection(
				Integer oppositeDirection, InternalPoint point1,
				InternalPoint point2) {
			Boolean flag = null;
			if (oppositeDirection == 0) {
				flag = point1.Y >= point2.Y;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 1) {
				flag = point1.Y <= point2.Y;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 2) {
				flag = point1.X <= point2.X;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 3) {
				flag = point1.X >= point2.X;
				// NOTICE: break ignore!!!
			} else {
				throw (new system.Exception("unsupported node side: "
						+ oppositeDirection));
			}
			if (!super._isReversedDirection) {

				return flag;
			}

			return !flag;

		}

		@Override
		public void CopyRadialCoord(InternalPoint source, InternalPoint dest) {
			dest.Y = source.Y;

		}

		@Override
		public float GetMaxRadialCoord(InternalRect rect) {

			return (rect.Y + rect.Height);

		}

		@Override
		public float GetMaxTangentialCoord(InternalRect rect) {

			return (rect.X + rect.Width);

		}

		@Override
		public float GetMinRadialCoord(InternalRect rect) {

			return rect.Y;

		}

		@Override
		public float GetMinTangentialCoord(InternalRect rect) {

			return rect.X;

		}

		@Override
		public Integer GetOppositeSide() {

			return 2;

		}

		@Override
		public float GetRadialCoord(InternalPoint point) {

			return point.Y;

		}

		@Override
		public float GetRadialCoord(InternalRect linkConnectionRect) {

			return (linkConnectionRect.Y + linkConnectionRect.Height);

		}

		@Override
		public float GetRadialDelta(float radCoord1, float radCoord2) {
			if (!super._isReversedDirection) {

				return (radCoord2 - radCoord1);
			}

			return (radCoord1 - radCoord2);

		}

		@Override
		public Integer GetSide() {

			return 3;

		}

		@Override
		public Integer GetSidePosition(Integer side) {
			if (side == 0 || side == 1) {

				return side;
			} else if (side == 2) {

				return 3;
			} else if (side == 3) {

				return 2;
			}

			return 5;

		}

		@Override
		public float GetTangentDelta(InternalPoint point1, InternalPoint point2) {

			return (point1.X - point2.X);

		}

		@Override
		public float GetTangentialCoord(InternalPoint point) {

			return point.X;

		}

		@Override
		public Boolean IsOnLeft(Boolean goingLeft) {

			return !goingLeft;

		}

		@Override
		public void MoveToDefaultConnectionPoint(InternalPoint point,
				InternalRect rect) {
			point.Move(rect.X + (rect.Width * 0.5f), rect.Y + rect.Height);

		}

		@Override
		public void SetRadialCoord(InternalPoint point, float radCoord) {
			point.Y = radCoord;

		}

		@Override
		public void SetTangentialCoord(InternalPoint point, float tanCoord) {
			point.X = tanCoord;

		}

		@Override
		public void TranslateLeft(InternalPoint point, float tanDelta) {
			point.X -= tanDelta;

		}

		@Override
		public void TranslateOutside(InternalPoint point, float radialDelta) {
			if (super._isReversedDirection) {
				radialDelta = -radialDelta;
			}
			point.Y += radialDelta;

		}

	}

	public final static class LeftSide extends SLNodeSide {
		public LeftSide() {
		}

		public LeftSide(Boolean reversedOrientation) {
			super(reversedOrientation);
		}

		@Override
		public Boolean CompareLinksByOppositeDirection(
				Integer oppositeDirection, InternalPoint point1,
				InternalPoint point2) {
			Boolean flag = null;
			if (oppositeDirection == 0) {
				flag = point1.Y >= point2.Y;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 1) {
				flag = point1.Y <= point2.Y;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 2) {
				flag = point1.X <= point2.X;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 3) {
				flag = point1.X >= point2.X;
				// NOTICE: break ignore!!!
			} else {
				throw (new system.Exception("unsupported node side: "
						+ oppositeDirection));
			}
			if (!super._isReversedDirection) {

				return flag;
			}

			return !flag;

		}

		@Override
		public void CopyRadialCoord(InternalPoint source, InternalPoint dest) {
			dest.X = source.X;

		}

		@Override
		public float GetMaxRadialCoord(InternalRect rect) {

			return (rect.X + rect.Width);

		}

		@Override
		public float GetMaxTangentialCoord(InternalRect rect) {

			return (rect.Y + rect.Height);

		}

		@Override
		public float GetMinRadialCoord(InternalRect rect) {

			return rect.X;

		}

		@Override
		public float GetMinTangentialCoord(InternalRect rect) {

			return rect.Y;

		}

		@Override
		public Integer GetOppositeSide() {

			return 1;

		}

		@Override
		public float GetRadialCoord(InternalPoint point) {

			return point.X;

		}

		@Override
		public float GetRadialCoord(InternalRect linkConnectionRect) {

			return linkConnectionRect.X;

		}

		@Override
		public float GetRadialDelta(float radCoord1, float radCoord2) {
			if (!super._isReversedDirection) {

				return (radCoord1 - radCoord2);
			}

			return (radCoord2 - radCoord1);

		}

		@Override
		public Integer GetSide() {

			return 0;

		}

		@Override
		public Integer GetSidePosition(Integer side) {
			if (side == 0) {

				return 2;
			} else if (side == 1) {

				return 3;
			} else if (side == 2) {

				return 0;
			} else if (side == 3) {

				return 1;
			}

			return 5;

		}

		@Override
		public float GetTangentDelta(InternalPoint point1, InternalPoint point2) {

			return (point1.Y - point2.Y);

		}

		@Override
		public float GetTangentialCoord(InternalPoint point) {

			return point.Y;

		}

		@Override
		public Boolean IsOnLeft(Boolean goingLeft) {

			return !goingLeft;

		}

		@Override
		public void MoveToDefaultConnectionPoint(InternalPoint point,
				InternalRect rect) {
			point.Move(rect.X, rect.Y + (rect.Height * 0.5f));

		}

		@Override
		public void SetRadialCoord(InternalPoint point, float radCoord) {
			point.X = radCoord;

		}

		@Override
		public void SetTangentialCoord(InternalPoint point, float tanCoord) {
			point.Y = tanCoord;

		}

		@Override
		public void TranslateLeft(InternalPoint point, float tanDelta) {
			point.Y -= tanDelta;

		}

		@Override
		public void TranslateOutside(InternalPoint point, float radialDelta) {
			if (super._isReversedDirection) {
				radialDelta = -radialDelta;
			}
			point.X -= radialDelta;

		}

	}

	public final static class RightSide extends SLNodeSide {
		public RightSide() {
		}

		public RightSide(Boolean reversedOrientation) {
			super(reversedOrientation);
		}

		@Override
		public Boolean CompareLinksByOppositeDirection(
				Integer oppositeDirection, InternalPoint point1,
				InternalPoint point2) {
			Boolean flag = null;
			if (oppositeDirection == 0) {
				flag = point1.Y <= point2.Y;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 1) {
				flag = point1.Y >= point2.Y;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 2) {
				flag = point1.X >= point2.X;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 3) {
				flag = point1.X <= point2.X;
				// NOTICE: break ignore!!!
			} else {
				throw (new system.Exception("unsupported node side: "
						+ oppositeDirection));
			}
			if (!super._isReversedDirection) {

				return flag;
			}

			return !flag;

		}

		@Override
		public void CopyRadialCoord(InternalPoint source, InternalPoint dest) {
			dest.X = source.X;

		}

		@Override
		public float GetMaxRadialCoord(InternalRect rect) {

			return (rect.X + rect.Width);

		}

		@Override
		public float GetMaxTangentialCoord(InternalRect rect) {

			return (rect.Y + rect.Height);

		}

		@Override
		public float GetMinRadialCoord(InternalRect rect) {

			return rect.X;

		}

		@Override
		public float GetMinTangentialCoord(InternalRect rect) {

			return rect.Y;

		}

		@Override
		public Integer GetOppositeSide() {

			return 0;

		}

		@Override
		public float GetRadialCoord(InternalPoint point) {

			return point.X;

		}

		@Override
		public float GetRadialCoord(InternalRect linkConnectionRect) {

			return (linkConnectionRect.X + linkConnectionRect.Width);

		}

		@Override
		public float GetRadialDelta(float radCoord1, float radCoord2) {
			if (!super._isReversedDirection) {

				return (radCoord2 - radCoord1);
			}

			return (radCoord1 - radCoord2);

		}

		@Override
		public Integer GetSide() {

			return 1;

		}

		@Override
		public Integer GetSidePosition(Integer side) {
			if (side == 0) {

				return 3;
			} else if (side == 1) {

				return 2;
			} else if (side == 2) {

				return 0;
			} else if (side == 3) {

				return 1;
			}

			return 5;

		}

		@Override
		public float GetTangentDelta(InternalPoint point1, InternalPoint point2) {

			return (point1.Y - point2.Y);

		}

		@Override
		public float GetTangentialCoord(InternalPoint point) {

			return point.Y;

		}

		@Override
		public Boolean IsOnLeft(Boolean goingLeft) {

			return goingLeft;

		}

		@Override
		public void MoveToDefaultConnectionPoint(InternalPoint point,
				InternalRect rect) {
			point.Move(rect.X + rect.Width, rect.Y + (rect.Height * 0.5f));

		}

		@Override
		public void SetRadialCoord(InternalPoint point, float radCoord) {
			point.X = radCoord;

		}

		@Override
		public void SetTangentialCoord(InternalPoint point, float tanCoord) {
			point.Y = tanCoord;

		}

		@Override
		public void TranslateLeft(InternalPoint point, float tanDelta) {
			point.Y -= tanDelta;

		}

		@Override
		public void TranslateOutside(InternalPoint point, float radialDelta) {
			if (super._isReversedDirection) {
				radialDelta = -radialDelta;
			}
			point.X += radialDelta;

		}

	}

	public final static class TopSide extends SLNodeSide {
		public TopSide() {
		}

		public TopSide(Boolean reversedDirection) {
			super(reversedDirection);
		}

		@Override
		public Boolean CompareLinksByOppositeDirection(
				Integer oppositeDirection, InternalPoint point1,
				InternalPoint point2) {
			Boolean flag = null;
			if (oppositeDirection == 0) {
				flag = point1.Y <= point2.Y;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 1) {
				flag = point1.Y >= point2.Y;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 2) {
				flag = point1.X >= point2.X;
				// NOTICE: break ignore!!!
			} else if (oppositeDirection == 3) {
				flag = point1.X <= point2.X;
				// NOTICE: break ignore!!!
			} else {
				throw (new system.Exception("unsupported node side: "
						+ oppositeDirection));
			}
			if (!super._isReversedDirection) {

				return flag;
			}

			return !flag;

		}

		@Override
		public void CopyRadialCoord(InternalPoint source, InternalPoint dest) {
			dest.Y = source.Y;

		}

		@Override
		public float GetMaxRadialCoord(InternalRect rect) {

			return (rect.Y + rect.Height);

		}

		@Override
		public float GetMaxTangentialCoord(InternalRect rect) {

			return (rect.X + rect.Width);

		}

		@Override
		public float GetMinRadialCoord(InternalRect rect) {

			return rect.Y;

		}

		@Override
		public float GetMinTangentialCoord(InternalRect rect) {

			return rect.X;

		}

		@Override
		public Integer GetOppositeSide() {

			return 3;

		}

		@Override
		public float GetRadialCoord(InternalPoint point) {

			return point.Y;

		}

		@Override
		public float GetRadialCoord(InternalRect linkConnectionRect) {

			return linkConnectionRect.Y;

		}

		@Override
		public float GetRadialDelta(float radCoord1, float radCoord2) {
			if (!super._isReversedDirection) {

				return (radCoord1 - radCoord2);
			}

			return (radCoord2 - radCoord1);

		}

		@Override
		public Integer GetSide() {

			return 2;

		}

		@Override
		public Integer GetSidePosition(Integer side) {

			return side;

		}

		@Override
		public float GetTangentDelta(InternalPoint point1, InternalPoint point2) {

			return (point1.X - point2.X);

		}

		@Override
		public float GetTangentialCoord(InternalPoint point) {

			return point.X;

		}

		@Override
		public Boolean IsOnLeft(Boolean goingLeft) {

			return goingLeft;

		}

		@Override
		public void MoveToDefaultConnectionPoint(InternalPoint point,
				InternalRect rect) {
			point.Move(rect.X + (rect.Width * 0.5f), rect.Y);

		}

		@Override
		public void SetRadialCoord(InternalPoint point, float radCoord) {
			point.Y = radCoord;

		}

		@Override
		public void SetTangentialCoord(InternalPoint point, float tanCoord) {
			point.X = tanCoord;

		}

		@Override
		public void TranslateLeft(InternalPoint point, float tanDelta) {
			point.X -= tanDelta;

		}

		@Override
		public void TranslateOutside(InternalPoint point, float radialDelta) {
			if (super._isReversedDirection) {
				radialDelta = -radialDelta;
			}
			point.Y -= radialDelta;

		}

	}
}