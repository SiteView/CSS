package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvThreeBendBL_OVERLAP extends IlvThreeBendBL {
	public static LinkShapeType instance = new IlvThreeBendBL_OVERLAP();

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvThreeBendLB_OVERLAP.instance;

	}

	@Override
	public Boolean IsOverlapping() {

		return true;

	}

}