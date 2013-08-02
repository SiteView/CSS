package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvDirectBL extends IlvDirectLinkShapeType {
	public static LinkShapeType instance = new IlvDirectBL();

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetBottomSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvDirectLB.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetLeftSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}