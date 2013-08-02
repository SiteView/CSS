package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvOneBendBR extends IlvOneBendLinkShapeType {
	public static LinkShapeType instance = new IlvOneBendBR();

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetBottomSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvOneBendRB.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetRightSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}
