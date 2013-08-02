package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvTwoBendRR extends IlvTwoBendSameSide {
	public static LinkShapeType instance = new IlvTwoBendRR();

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetRightSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetRightSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}