package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvThreeBendLB_OVERLAP extends IlvThreeBendLB {
	public static LinkShapeType instance = new IlvThreeBendLB_OVERLAP();

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvThreeBendBL_OVERLAP.instance;

	}

	@Override
	public Boolean IsOverlapping() {

		return true;

	}

}
