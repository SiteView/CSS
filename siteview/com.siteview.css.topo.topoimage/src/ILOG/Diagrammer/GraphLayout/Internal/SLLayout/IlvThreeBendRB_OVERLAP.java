package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvThreeBendRB_OVERLAP extends IlvThreeBendRB {
	public static LinkShapeType instance = new IlvThreeBendRB_OVERLAP();

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvThreeBendBR_OVERLAP.instance;

	}

	@Override
	public Boolean IsOverlapping() {

		return true;

	}

}
