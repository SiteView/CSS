package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvThreeBendTL_OVERLAP extends IlvThreeBendTL {
	public static LinkShapeType instance = new IlvThreeBendTL_OVERLAP();

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvThreeBendLT_OVERLAP.instance;

	}

	@Override
	public Boolean IsOverlapping() {

		return true;

	}

}