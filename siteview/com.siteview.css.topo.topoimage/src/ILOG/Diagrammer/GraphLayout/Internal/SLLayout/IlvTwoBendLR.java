package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvTwoBendLR extends IlvTwoBendDifferentSide {
	public static LinkShapeType instance = new IlvTwoBendLR();

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetLeftSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvTwoBendRL.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetRightSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}