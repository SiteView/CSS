package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.Math;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public abstract class IlvFourBendSameSide extends IlvFourBendLinkShapeType {
	public IlvFourBendSameSide() {
	}

	@Override
	public void ComputePoints(InternalPoint point0, InternalPoint point1,
			InternalPoint point2, InternalPoint point3, InternalRect fromRect,
			InternalRect toRect, SLNodeSide fromSide, SLNodeSide toSide,
			float minFinalSegment) {
		InternalRect rect = null;
		InternalPoint point = null;
		InternalPoint point4 = null;
		fromSide.SetRadialCoord(point1, fromSide.GetRadialCoord(point0));
		fromSide.SetRadialCoord(point2, fromSide.GetRadialCoord(point3));
		if (fromSide.GetRadialDelta(point0, point3) >= 0f) {
			rect = toRect;
			point = point2;
			point4 = point1;
		} else {
			rect = fromRect;
			point = point1;
			point4 = point2;
		}
		fromSide.SetTangentialCoord(
				point,
				this.IsLeft() ? (fromSide.GetMinTangentialCoord(rect) - minFinalSegment)
						: (fromSide.GetMaxTangentialCoord(rect) + minFinalSegment));
		fromSide.SetTangentialCoord(point4, fromSide.GetTangentialCoord(point));

	}

	@Override
	public abstract SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

	@Override
	public LinkShapeType GetReversedShapeType() {

		return this;

	}

	@Override
	public abstract SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

	public abstract Boolean IsLeft();

}
