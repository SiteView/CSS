package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvFourBendBBright extends IlvFourBendSameSide {
	public static LinkShapeType instance = new IlvFourBendBBright();

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetBottomSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetBottomSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

	@Override
	public Boolean IsLeft() {

		return false;

	}

}