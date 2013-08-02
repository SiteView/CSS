package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public final class IlvFourBendTBmiddle extends IlvFourBendLinkShapeType {
	public static LinkShapeType instance = new IlvFourBendTBmiddle();

	@Override
	public void ComputePoints(InternalPoint point0, InternalPoint point1,
			InternalPoint point2, InternalPoint point3, InternalRect fromRect,
			InternalRect toRect, SLNodeSide fromSide, SLNodeSide toSide,
			float minFinalSegment) {
		float x = LinkShapeType.GetXMiddle(point0, point3, fromRect, toRect);
		point0.Move(point0.X, fromRect.Y - minFinalSegment);
		point3.Move(point3.X, (toRect.Y + toRect.Height) + minFinalSegment);
		point1.Move(x, point0.Y);
		point2.Move(x, point3.Y);

	}

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetTopSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvFourBendBTmiddle.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetBottomSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}