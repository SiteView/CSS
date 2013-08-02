package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvFourBendLLright extends IlvFourBendSameSide {
	public static LinkShapeType instance = new IlvFourBendLLright();

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetLeftSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetLeftSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

	@Override
	public Boolean IsLeft() {

		return false;

	}

}
