package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.Math;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public final class IlvFourBendLRbottom extends IlvFourBendLinkShapeType {
	public static LinkShapeType instance = new IlvFourBendLRbottom();

	@Override
	public void ComputePoints(InternalPoint point0, InternalPoint point1,
			InternalPoint point2, InternalPoint point3, InternalRect fromRect,
			InternalRect toRect, SLNodeSide fromSide, SLNodeSide toSide,
			float minFinalSegment) {
		float y = Math.Max((float) (fromRect.Y + fromRect.Height),
				(float) (toRect.Y + toRect.Height)) + minFinalSegment;
		if (point0.Y > point3.Y) {
			point3.Move(
					Math.Max((float) (fromRect.X + fromRect.Width),
							(float) (toRect.X + toRect.Width))
							+ minFinalSegment, point3.Y);
		} else {
			point0.Move(Math.Min(fromRect.X, toRect.X) - minFinalSegment,
					point0.Y);
		}
		point1.Move(point0.X, y);
		point2.Move(point3.X, y);

	}

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetLeftSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvFourBendRLbottom.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetRightSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}