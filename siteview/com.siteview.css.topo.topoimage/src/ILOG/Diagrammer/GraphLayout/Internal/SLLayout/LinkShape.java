package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.ArgumentException;
import system.Math;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelUtil;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public final class LinkShape {
	private InternalRect _bbox;

	private InternalPoint[] _bendPoints;

	private InternalPoint _from;

	private Boolean _invalidBBox = false;

	private Integer _nPoints;

	private InternalPoint _to;

	public LinkShape(Integer maxNumberOfBends) {
		this._nPoints = 2;
		this._bbox = new InternalRect(0f, 0f, 0f, 0f);
		this._invalidBBox = true;
		if (maxNumberOfBends < 0) {
			throw (new ArgumentException(
					"maxNumberOfBends cannot be negative: " + maxNumberOfBends));
		}
		if (maxNumberOfBends > 0) {

			this._bendPoints = this.AllocatePoints(maxNumberOfBends);
			for (Integer i = 0; i < maxNumberOfBends; i++) {
				this._bendPoints[i] = new InternalPoint(0f, 0f);
			}
		}
	}

	public LinkShape(LinkShape masterShape, Boolean reversed,
			Boolean copyPointLocations) {
		this._nPoints = 2;
		this._bbox = new InternalRect(0f, 0f, 0f, 0f);
		this._invalidBBox = true;
		this.CopyFrom(masterShape, reversed, copyPointLocations);
	}

	public LinkShape(ShortLinkAlgorithm layout,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalPoint fromPoint, InternalPoint toPoint) {
		this._nPoints = 2;
		this._bbox = new InternalRect(0f, 0f, 0f, 0f);
		this._invalidBBox = true;

		if (!linkData.IsFixed()) {
			throw (new system.Exception(
					"this constructor must be used only for fixed links"));
		}

		if (linkData.IsMaster()) {
			throw (new system.Exception(
					"this constructor is not intended for multilink bundles"));
		}
		java.lang.Object nodeOrLink = linkData.get_nodeOrLink();
		InternalPoint[] linkPoints = GraphModelUtil.GetLinkPoints(
				layout.GetGraphModel(), nodeOrLink);
		Integer maxNumberOfPoints = linkPoints.length - 2;
		if (maxNumberOfPoints < 0) {
			throw (new system.Exception(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] {
									"The method getLinkPoints called on ",
									layout.GetGraphModel(), " for the link ",
									nodeOrLink,
									" returned an array of length ",
									maxNumberOfPoints + 2,
									" while it must return an array of length at least 2" })));
		}
		if (maxNumberOfPoints > 0) {

			this._bendPoints = this.AllocatePoints(maxNumberOfPoints);
			for (Integer i = 1; i <= maxNumberOfPoints; i++) {
				this._bendPoints[i - 1] = new InternalPoint(linkPoints[i].X,
						linkPoints[i].Y);
			}
			this._nPoints = maxNumberOfPoints + 2;
		}
		this.SetFrom(fromPoint);
		this.SetTo(toPoint);
		this._invalidBBox = true;
		SLNodeSide sideOfPoint = linkData.GetSideOfPoint(fromPoint, true);
		SLNodeSide toNodeSide = linkData.GetSideOfPoint(toPoint, false);
		linkData.SetNodeSides(sideOfPoint, toNodeSide, true);
	}

	private InternalPoint[] AllocatePoints() {

		return new InternalPoint[4];

	}

	private InternalPoint[] AllocatePoints(Integer maxNumberOfPoints) {

		return new InternalPoint[(int) Math.Max(maxNumberOfPoints, 4)];

	}

	public InternalRect BoundingBox() {
		if (this._invalidBBox) {
			this.ComputeBBox(this._bbox);
		}

		return this._bbox;

	}

	private void ComputeBBox(InternalRect bbox) {
		bbox.X = this._from.X;
		bbox.Y = this._from.Y;
		bbox.Width = 0f;
		bbox.Height = 0f;
		bbox.Add(this._to.X, this._to.Y);
		if (this._bendPoints != null) {
			Integer num = this._nPoints - 2;
			for (Integer i = 0; i < num; i++) {
				InternalPoint point = this._bendPoints[i];
				if (point != null) {
					bbox.Add(point.X, point.Y);
				}
			}
		}
		this._invalidBBox = false;

	}

	public void CopyFrom(LinkShape masterShape, Boolean reversed,
			Boolean copyPointLocations) {

		this._nPoints = masterShape.GetNumberOfPoints();
		InternalPoint[] intermediatePoints = masterShape
				.GetIntermediatePoints();
		if (reversed) {
			if ((intermediatePoints != null) && (this._nPoints > 2)) {
				if ((this._bendPoints == null)
						|| (this._bendPoints.length < (this._nPoints - 2))) {

					this._bendPoints = this.AllocatePoints();
				}
				if (copyPointLocations) {
					Integer num = 0;
					for (Integer i = this._nPoints - 3; i >= 0; i--) {
						this._bendPoints[num++] = new InternalPoint(
								intermediatePoints[i].X,
								intermediatePoints[i].Y);
					}
				}
			}
			InternalPoint to = masterShape.GetTo();
			if (to != null) {
				InternalPoint from = this.GetFrom();
				if (from != null) {
					if (copyPointLocations) {
						from.SetLocation(to);
					}
				} else {
					this.SetFrom(new InternalPoint(to.X, to.Y));
				}
			}

			to = masterShape.GetFrom();
			if (to != null) {
				InternalPoint point3 = this.GetTo();
				if (point3 != null) {
					if (copyPointLocations) {
						point3.SetLocation(to);
					}
				} else {
					this.SetTo(new InternalPoint(to.X, to.Y));
				}
			}
		} else {
			if ((intermediatePoints != null) && (this._nPoints > 2)) {
				if ((this._bendPoints == null)
						|| (this._bendPoints.length < (this._nPoints - 2))) {

					this._bendPoints = this.AllocatePoints();
				}
				if (copyPointLocations) {
					for (Integer j = this._nPoints - 3; j >= 0; j--) {
						this._bendPoints[j] = new InternalPoint(
								intermediatePoints[j].X,
								intermediatePoints[j].Y);
					}
				}
			}
			InternalPoint point = masterShape.GetFrom();
			if (point != null) {
				InternalPoint point5 = this.GetFrom();
				if (point5 != null) {
					if (copyPointLocations) {
						point5.SetLocation(point);
					}
				} else {
					this.SetFrom(new InternalPoint(point.X, point.Y));
				}
			}

			point = masterShape.GetTo();
			if (point != null) {
				InternalPoint point6 = this.GetTo();
				if (point6 != null) {
					if (copyPointLocations) {
						point6.SetLocation(point);
					}
				} else {
					this.SetTo(new InternalPoint(point.X, point.Y));
				}
			}
		}
		this.InvalidateBBox();

	}

	public InternalPoint GetFrom() {

		return this._from;

	}

	public InternalPoint[] GetIntermediatePoints() {

		return this._bendPoints;

	}

	public Integer GetNumberOfPoints() {

		return this._nPoints;

	}

	public InternalPoint GetPointAt(Integer index) {
		if (index == 0) {

			return this._from;
		}
		if (index == (this._nPoints - 1)) {

			return this._to;
		}
		if ((index <= 0) || (index >= this._nPoints)) {
			throw (new ArgumentException(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] {
									"bad index: ",
									index,
									" nPoints: ",
									this._nPoints,
									" points.length: ",
									(this._bendPoints != null) ? this._bendPoints.length
											: 0 })));
		}

		return this._bendPoints[index - 1];

	}

	public InternalPoint GetPointAt(Boolean origin, Integer index) {
		if (!origin) {

			return this.GetPointAt((this.GetNumberOfPoints() - index) - 1);
		}

		return this.GetPointAt(index);

	}

	public InternalPoint GetTo() {

		return this._to;

	}

	public void InvalidateBBox() {
		this._invalidBBox = true;

	}

	public void SetFrom(InternalPoint point) {
		this._from = point;
		this._invalidBBox = true;

	}

	public void SetNumberOfPoints(Integer nPoints) {
		this._nPoints = nPoints;

	}

	public void SetTo(InternalPoint point) {
		this._to = point;
		this._invalidBBox = true;

	}

}