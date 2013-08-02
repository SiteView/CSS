package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public abstract class IlvTwoBendSameSide extends IlvTwoBendLinkShapeType {
	public IlvTwoBendSameSide() {
	}

	@Override
	public abstract SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

	@Override
	public LinkShapeType GetReversedShapeType() {

		return this;

	}

	@Override
	public abstract SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

}
