package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public abstract class IlvThreeBendLinkShapeType extends
		IlvOrthogonalLinkShapeType {
	public IlvThreeBendLinkShapeType() {
	}

	@Override
	public void ComputeIntermediatePoints(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, SLNodeSide fromSide,
			SLNodeSide toSide, InternalPoint fromPoint, InternalPoint toPoint,
			float minFinalSegment) {
		InternalPoint[] intermediatePoints = linkData.GetLinkShape()
				.GetIntermediatePoints();
		InternalPoint orCreatePoint = super.GetOrCreatePoint(
				intermediatePoints, 0);
		orCreatePoint.SetLocation(fromPoint);
		InternalPoint point = super.GetOrCreatePoint(intermediatePoints, 2);
		point.SetLocation(toPoint);
		if (linkData.IsOverlapping() || this.IsOverlapping()) {
			if (fromSide.GetRadialDelta(orCreatePoint, toRect) > 0f) {
				fromSide.SetRadialCoord(orCreatePoint,
						fromSide.GetRadialCoord(toRect));
			}
			if (toSide.GetRadialDelta(point, fromRect) > 0f) {
				toSide.SetRadialCoord(point, toSide.GetRadialCoord(fromRect));
			}
		}
		fromSide.TranslateOutside(orCreatePoint, minFinalSegment);
		toSide.TranslateOutside(point, minFinalSegment);
		InternalPoint point3 = super.GetOrCreatePoint(intermediatePoints, 1);
		fromSide.SetRadialCoord(point3, fromSide.GetRadialCoord(orCreatePoint));
		toSide.SetRadialCoord(point3, toSide.GetRadialCoord(point));

	}

	@Override
	public abstract SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

	@Override
	public Integer GetNumberOfBends() {

		return 3;

	}

	@Override
	public abstract LinkShapeType GetReversedShapeType();

	@Override
	public abstract SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

	public Boolean IsOverlapping() {

		return false;

	}

}
