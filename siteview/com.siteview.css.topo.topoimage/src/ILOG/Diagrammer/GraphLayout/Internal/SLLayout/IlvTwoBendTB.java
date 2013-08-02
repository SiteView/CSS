package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvTwoBendTB extends IlvTwoBendDifferentSide {
	public static LinkShapeType instance = new IlvTwoBendTB();

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetTopSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvTwoBendBT.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetBottomSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}