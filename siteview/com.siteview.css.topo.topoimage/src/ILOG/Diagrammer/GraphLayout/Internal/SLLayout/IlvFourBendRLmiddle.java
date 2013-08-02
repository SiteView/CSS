package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public final class IlvFourBendRLmiddle extends IlvFourBendLinkShapeType {
	public static LinkShapeType instance = new IlvFourBendRLmiddle();

	@Override
	public void ComputePoints(InternalPoint point0, InternalPoint point1,
			InternalPoint point2, InternalPoint point3, InternalRect fromRect,
			InternalRect toRect, SLNodeSide fromSide, SLNodeSide toSide,
			float minFinalSegment) {
		float y = LinkShapeType.GetYMiddle(point0, point3, fromRect, toRect);
		if (fromRect.Y > (toRect.Y + toRect.Height)) {
			y = ((fromRect.Y + toRect.Y) + toRect.Height) * 0.5f;
		} else if (toRect.Y > (fromRect.Y + fromRect.Height)) {
			y = ((toRect.Y + fromRect.Y) + fromRect.Height) * 0.5f;
		} else {
			y = (point0.Y + point3.Y) * 0.5f;
		}
		point0.Move((fromRect.X + fromRect.Width) + minFinalSegment, point0.Y);
		point3.Move(toRect.X - minFinalSegment, point3.Y);
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

		return IlvFourBendLRmiddle.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetLeftSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}