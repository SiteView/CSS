package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.Collections.ArrayList;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;

public abstract class IncidentLinksRefiner {
	private ShortLinkAlgorithm _layout;

	public IncidentLinksRefiner(ShortLinkAlgorithm layout) {
		this._layout = layout;
	}

	public void Clean() {

	}

	public ShortLinkAlgorithm GetLayoutAlgorithm() {

		return this._layout;

	}

	public abstract void Refine(ArrayList vectLinks,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData,
			SLNodeSide nodeSide, float linkOffset, float minFinalSegmentLength,
			Boolean reversedOrder, Boolean withFixedConnectionPoints);

	public Boolean RefineLinksOrthogonal(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData link1,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData link2,
			Boolean origin1, Boolean origin2, SLNodeSide nodeSide,
			Boolean goingLeft, float linkOffset,
			Boolean stopIfDifferentDirection) {

		if (((link1 != link2) && !link2.IsSameSideSelfLink())
				&& !link2.IsFixed()) {
			Integer numberOfPoints = link1.GetLinkShape().GetNumberOfPoints();
			Integer num2 = link2.GetLinkShape().GetNumberOfPoints() - 2;
			if (num2 < 1) {
				throw (new system.Exception(
						"cannot refine a link with less than three points"));
			}
			InternalPoint pointAt = link1.GetPointAt(origin1, 1);
			InternalPoint point2 = (numberOfPoints > 2) ? link1.GetPointAt(
					origin1, 2) : pointAt;
			InternalPoint point3 = link2.GetPointAt(origin2, 1);
			InternalPoint point4 = link2.GetPointAt(origin2, 2);
			Boolean flag = nodeSide.GetTangentDelta(pointAt, point2) >= 0f;
			Boolean flag2 = nodeSide.GetTangentDelta(point3, point4) >= 0f;
			if (num2 < 2) {
				if (!goingLeft) {

					return (!flag && !flag2);
				}

				return (flag && flag2);
			}
			Boolean firstLinkIsSameSideSelfLink = link1.IsSameSideSelfLink();
			if (!(goingLeft ? (firstLinkIsSameSideSelfLink || (flag && flag2))
					: (firstLinkIsSameSideSelfLink || (!flag && !flag2)))
					&& ((stopIfDifferentDirection || (goingLeft != flag2)) || !goingLeft)) {

				return false;
			}
			link2.ShapePointsModified();
			Integer num3 = link1.GetLinkShape().GetNumberOfPoints() - 2;
			InternalPoint point5 = null;
			InternalPoint point6 = null;
			if (num3 >= 2) {

				point5 = link1.GetPointAt(origin1, 3);
				if (num3 >= 3) {

					point6 = link1.GetPointAt(origin1, 4);
				}
			}
			InternalPoint point7 = null;
			InternalPoint point8 = null;
			InternalPoint point9 = null;
			if (num2 >= 2) {

				point7 = link2.GetPointAt(origin2, 3);
				if (num2 >= 3) {

					point8 = link2.GetPointAt(origin2, 4);
					if (num2 >= 4) {

						point9 = link2.GetPointAt(origin2, 5);
					}
				}
			}
			Boolean sameOpposite = (link1.GetConnectionNode(!origin1) == link2
					.GetConnectionNode(!origin2))
					&& (link1.GetNodeSide(!origin1) == link2
							.GetNodeSide(!origin2));
			nodeSide.RefineLinksOrthogonalWithPoints(this._layout, pointAt,
					point2, point5, point6, point3, point4, point7, point8,
					point9, link1.GetLinkWidth(), link2.GetLinkWidth(), flag,
					flag2, firstLinkIsSameSideSelfLink, num3, num2,
					link2.boundingBox, link2.GetNodeSide(!origin2), linkOffset,
					sameOpposite, true);
		}

		return true;

	}

}