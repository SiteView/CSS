package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public abstract class IlvFourBendLinkShapeType extends
		IlvOrthogonalLinkShapeType {

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
		fromSide.TranslateOutside(orCreatePoint, minFinalSegment);
		InternalPoint point = super.GetOrCreatePoint(intermediatePoints, 3);
		point.SetLocation(toPoint);
		toSide.TranslateOutside(point, minFinalSegment);
		InternalPoint point3 = super.GetOrCreatePoint(intermediatePoints, 1);
		InternalPoint point4 = super.GetOrCreatePoint(intermediatePoints, 2);
		this.ComputePoints(orCreatePoint, point3, point4, point, fromRect,
				toRect, fromSide, toSide, minFinalSegment);

	}

	public abstract void ComputePoints(InternalPoint point0,
			InternalPoint point1, InternalPoint point2, InternalPoint point3,
			InternalRect fromRect, InternalRect toRect, SLNodeSide fromSide,
			SLNodeSide toSide, float minFinalSegment);

	@Override
	public abstract SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

	@Override
	public Integer GetNumberOfBends() {

		return 4;

	}

	@Override
	public abstract LinkShapeType GetReversedShapeType();

	@Override
	public abstract SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

}
