package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvThreeBendTR_OVERLAP extends IlvThreeBendTR {
	public static LinkShapeType instance = new IlvThreeBendTR_OVERLAP();

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvThreeBendRT_OVERLAP.instance;

	}

	@Override
	public Boolean IsOverlapping() {

		return true;

	}

}