package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;

public final class DirectIncidentLinksSorter extends IncidentLinksSorter {
	public DirectIncidentLinksSorter(ShortLinkAlgorithm layout) {
		super(layout);
	}

	@Override
	public Boolean CompareLinks(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData link1,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData link2,
			Boolean origin1, Boolean origin2, SLNodeSide nodeSide) {
		Boolean origin = !origin1;
		Boolean flag2 = !origin2;
		if ((link1.GetConnectionNode(origin) == link2.GetConnectionNode(flag2))
				&& (link1.GetNodeSide(origin) == link2.GetNodeSide(flag2))) {
			InternalPoint connectionPoint = link1.GetConnectionPoint(origin);
			InternalPoint point2 = link2.GetConnectionPoint(flag2);

			return (connectionPoint.equals(point2) || nodeSide
					.CompareLinksByOppositeDirection(link1.GetNodeSide(origin)
							.GetDirection(), connectionPoint, point2));
		}
		InternalPoint pointAt = link1.GetPointAt(origin1, 1);
		InternalPoint point4 = link1.GetPointAt(origin1, 2);
		InternalPoint point5 = link2.GetPointAt(origin2, 1);
		InternalPoint point6 = link2.GetPointAt(origin2, 2);
		float tangentDelta = nodeSide.GetTangentDelta(pointAt, point4);
		float num2 = nodeSide.GetTangentDelta(point5, point6);
		if ((tangentDelta * num2) < 0f) {

			return (tangentDelta >= num2);
		}
		LinkShapeType currentShapeType = link1.GetCurrentShapeType();
		LinkShapeType type2 = link2.GetCurrentShapeType();
		Boolean flag3 = currentShapeType.IsOrthogonal();
		Boolean flag4 = type2.IsOrthogonal();
		if (flag3) {
			if (flag4) {

				return nodeSide.CompareOrthogonalLinks(super._layout, link1,
						link2, origin1, origin2);
			}
			if (link1.IsSelfLink()
					&& (currentShapeType.GetNumberOfBends() == 3)) {

				return (tangentDelta >= 0f);
			}
		} else if ((flag4 && link2.IsSelfLink())
				&& (type2.GetNumberOfBends() == 3)) {

			return (num2 < 0f);
		}
		float radialDelta = nodeSide.GetRadialDelta(pointAt, point4);

		return ((nodeSide.GetRadialDelta(point5, point6) * tangentDelta) >= (radialDelta * num2));

	}

}