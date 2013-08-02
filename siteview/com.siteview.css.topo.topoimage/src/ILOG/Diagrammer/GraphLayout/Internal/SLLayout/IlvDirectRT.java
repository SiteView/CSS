package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvDirectRT extends IlvDirectLinkShapeType {
	public static LinkShapeType instance = new IlvDirectRT();

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetRightSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvDirectTR.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetTopSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}