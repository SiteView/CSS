package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvOneBendLB extends IlvOneBendLinkShapeType {
	public static LinkShapeType instance = new IlvOneBendLB();

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetLeftSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvOneBendBL.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetBottomSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}