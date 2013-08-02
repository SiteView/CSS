package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvFourBendTTleft extends IlvFourBendSameSide {
	public static LinkShapeType instance = new IlvFourBendTTleft();

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetTopSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetTopSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

	@Override
	public Boolean IsLeft() {

		return true;

	}

}
