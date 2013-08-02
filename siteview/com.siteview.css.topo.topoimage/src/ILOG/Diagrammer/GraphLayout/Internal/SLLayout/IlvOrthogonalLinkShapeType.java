package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public abstract class IlvOrthogonalLinkShapeType extends LinkShapeType {
	public IlvOrthogonalLinkShapeType() {
	}

	@Override
	public abstract void ComputeIntermediatePoints(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0,
			InternalRect arg1, InternalRect arg2, SLNodeSide arg3,
			SLNodeSide arg4, InternalPoint arg5, InternalPoint arg6, float arg7);

	@Override
	public abstract SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

	@Override
	public abstract Integer GetNumberOfBends();

	@Override
	public abstract LinkShapeType GetReversedShapeType();

	@Override
	public abstract SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData arg0);

	@Override
	public Boolean IsOrthogonal() {

		return true;

	}

}