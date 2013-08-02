package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public final class IlvFourBendLRmiddle extends IlvFourBendLinkShapeType {
	public static LinkShapeType instance = new IlvFourBendLRmiddle();

	@Override
	public void ComputePoints(InternalPoint point0, InternalPoint point1,
			InternalPoint point2, InternalPoint point3, InternalRect fromRect,
			InternalRect toRect, SLNodeSide fromSide, SLNodeSide toSide,
			float minFinalSegment) {
		float y = LinkShapeType.GetYMiddle(point0, point3, fromRect, toRect);
		point0.Move(fromRect.X - minFinalSegment, point0.Y);
		point3.Move((toRect.X + toRect.Width) + minFinalSegment, point3.Y);
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

		return IlvFourBendRLmiddle.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetRightSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}