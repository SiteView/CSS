package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvThreeBendLT_OVERLAP extends IlvThreeBendLT {
	public static LinkShapeType instance = new IlvThreeBendLT_OVERLAP();

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvThreeBendTL_OVERLAP.instance;

	}

	@Override
	public Boolean IsOverlapping() {

		return true;

	}

}