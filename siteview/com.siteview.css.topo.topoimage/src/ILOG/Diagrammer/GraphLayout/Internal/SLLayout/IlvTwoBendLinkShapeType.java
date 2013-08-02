package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public abstract class IlvTwoBendLinkShapeType extends
		IlvOrthogonalLinkShapeType {
	public IlvTwoBendLinkShapeType() {
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
		InternalPoint point2 = super.GetOrCreatePoint(intermediatePoints, 1);
		point2.SetLocation(toPoint);
		if (fromSide.GetDirection() == toSide.GetDirection()) {
			if (fromSide.GetRadialDelta(orCreatePoint, point2) <= 0f) {
				fromSide.TranslateOutside(orCreatePoint, minFinalSegment);
				fromSide.CopyRadialCoord(orCreatePoint, point2);
			} else {
				toSide.TranslateOutside(point2, minFinalSegment);
				fromSide.CopyRadialCoord(point2, orCreatePoint);
			}
		} else {
			float radCoord = (fromSide.GetRadialCoord(fromPoint) + toSide
					.GetRadialCoord(toPoint)) * 0.5f;
			fromSide.SetRadialCoord(orCreatePoint, radCoord);
			toSide.SetRadialCoord(point2, radCoord);
		}

	}

	@Override
	public abstract SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

	@Override
	public Integer GetNumberOfBends() {

		return 2;

	}

	@Override
	public abstract LinkShapeType GetReversedShapeType();

	@Override
	public abstract SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

}
