package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvThreeBendRT_OVERLAP extends IlvThreeBendRT {
	public static LinkShapeType instance = new IlvThreeBendRT_OVERLAP();

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvThreeBendTR_OVERLAP.instance;

	}

	@Override
	public Boolean IsOverlapping() {

		return true;

	}

}