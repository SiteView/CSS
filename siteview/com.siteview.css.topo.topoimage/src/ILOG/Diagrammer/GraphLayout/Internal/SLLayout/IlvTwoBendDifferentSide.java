package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public abstract class IlvTwoBendDifferentSide extends IlvTwoBendLinkShapeType {
	public IlvTwoBendDifferentSide() {
	}

	@Override
	public abstract SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

	@Override
	public abstract LinkShapeType GetReversedShapeType();

	@Override
	public abstract SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

}