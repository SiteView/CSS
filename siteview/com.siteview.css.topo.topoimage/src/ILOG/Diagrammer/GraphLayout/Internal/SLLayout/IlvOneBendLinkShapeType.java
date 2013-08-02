package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public abstract class IlvOneBendLinkShapeType extends
		IlvOrthogonalLinkShapeType {
	public IlvOneBendLinkShapeType() {
	}

	@Override
	public void ComputeIntermediatePoints(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, SLNodeSide fromSide,
			SLNodeSide toSide, InternalPoint fromPoint, InternalPoint toPoint,
			float minFinalSegment) {
		InternalPoint orCreatePoint = super.GetOrCreatePoint(linkData
				.GetLinkShape().GetIntermediatePoints(), 0);
		fromSide.SetTangentialCoord(orCreatePoint,
				fromSide.GetTangentialCoord(fromPoint));
		toSide.SetTangentialCoord(orCreatePoint,
				toSide.GetTangentialCoord(toPoint));

	}

	@Override
	public abstract SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

	@Override
	public Integer GetNumberOfBends() {

		return 1;

	}

	@Override
	public abstract LinkShapeType GetReversedShapeType();

	@Override
	public abstract SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

}
