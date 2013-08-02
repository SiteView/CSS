package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public abstract class IlvDirectLinkShapeType extends LinkShapeType {

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

		orCreatePoint = super.GetOrCreatePoint(intermediatePoints, 1);
		orCreatePoint.SetLocation(toPoint);
		toSide.TranslateOutside(orCreatePoint, minFinalSegment);

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

	@Override
	public Boolean IsOrthogonal() {

		return false;

	}

}