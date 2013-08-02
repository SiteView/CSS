package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvTwoBendRL extends IlvTwoBendDifferentSide {
	public static LinkShapeType instance = new IlvTwoBendRL();

	@Override
	public SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetFromNode().GetRightSide(true,
				linkData.IsQuasiSelfInterGraphLinkOrigin());

	}

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvTwoBendLR.instance;

	}

	@Override
	public SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return linkData.GetToNode().GetLeftSide(true,
				linkData.IsQuasiSelfInterGraphLinkDestination());

	}

}