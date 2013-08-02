package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.Math;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public final class IlvFourBendBTleft extends IlvFourBendLinkShapeType {
	public static LinkShapeType instance = new IlvFourBendBTleft();

	@Override
	public void ComputePoints(InternalPoint point0, InternalPoint point1,
			InternalPoint point2, InternalPoint point3, InternalRect fromRect,
			InternalRect toRect, SLNodeSide fromSide, SLNodeSide toSide,
			float minFinalSegment) {
		float x = Math.Min(fromRect.X, toRect.X) - minFinalSegment;
		if (point0.X > point3.X) {
			point0.Move(
					point0.X,
					Math.Max((float) (fromRect.Y + fromRect.Height),
							(float) (toRect.Y + toRect.Height))
							+ minFinalSegment);
		} else {
			point3.Move(point3.X, Math.Min(fromRect.Y, toRect.Y)
					- minFinalSegment);
		}
		point1.Move(x, point0.Y);
		point2.Move(x, point3.Y);

	}

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetBottomSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvFourBendTBleft.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetTopSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}