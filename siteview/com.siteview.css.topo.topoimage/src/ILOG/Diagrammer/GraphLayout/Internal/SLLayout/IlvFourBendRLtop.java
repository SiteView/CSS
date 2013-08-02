package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.Math;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public final class IlvFourBendRLtop extends IlvFourBendLinkShapeType {
	public static LinkShapeType instance = new IlvFourBendRLtop();

	@Override
	public void ComputePoints(InternalPoint point0, InternalPoint point1,
			InternalPoint point2, InternalPoint point3, InternalRect fromRect,
			InternalRect toRect, SLNodeSide fromSide, SLNodeSide toSide,
			float minFinalSegment) {
		float y = Math.Min(fromRect.Y, toRect.Y) - minFinalSegment;
		if (point0.Y <= point3.Y) {
			point3.Move(Math.Min(fromRect.X, toRect.X) - minFinalSegment,
					point3.Y);
		} else {
			point0.Move(
					Math.Max((float) (fromRect.X + fromRect.Width),
							(float) (toRect.X + toRect.Width))
							+ minFinalSegment, point0.Y);
		}
		point1.Move(point0.X, y);
		point2.Move(point3.X, y);

	}

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetRightSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvFourBendLRtop.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetLeftSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}