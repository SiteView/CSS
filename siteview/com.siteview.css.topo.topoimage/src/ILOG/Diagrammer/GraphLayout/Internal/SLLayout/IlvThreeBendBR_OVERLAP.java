package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

public final class IlvThreeBendBR_OVERLAP extends IlvThreeBendBR {
	public static LinkShapeType instance = new IlvThreeBendBR_OVERLAP();

	@Override
	public LinkShapeType GetReversedShapeType() {

		return IlvThreeBendRB_OVERLAP.instance;

	}

	@Override
	public Boolean IsOverlapping() {

		return true;

	}

}
