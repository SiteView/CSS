package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Collections.*;

public final class OrthogonalIncidentLinksRefiner extends IncidentLinksRefiner {
	public OrthogonalIncidentLinksRefiner(ShortLinkAlgorithm layout) {
		super(layout);
	}

	@Override
	public void Refine(ArrayList vectLinks,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData,
			SLNodeSide nodeSide, float linkOffset, float minFinalSegmentLength,
			Boolean reversedOrder, Boolean withFixedConnectionPoints) {
		Integer length = (vectLinks != null) ? vectLinks.get_Count() : 0;
		if (length >= 2) {
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data = null;
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data2 = null;
			Integer num2 = length - 1;
			Boolean stopIfDifferentDirection = !withFixedConnectionPoints;
			for (Integer i = 0; i < num2; i++) {
				data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) LayoutUtil
						.ElementAt(vectLinks, i, length, reversedOrder);
				data2 = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) LayoutUtil
						.ElementAt(vectLinks, i + 1, length, reversedOrder);

				if ((!data2.IsFixed() && !super.RefineLinksOrthogonal(data,
						data2, data.IsOrigin(nodeData, nodeSide),
						data2.IsOrigin(nodeData, nodeSide), nodeSide, true,
						linkOffset, stopIfDifferentDirection))
						&& stopIfDifferentDirection) {
					break;
				}
			}
			for (Integer j = num2; j > 0; j--) {
				data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) LayoutUtil
						.ElementAt(vectLinks, j, length, reversedOrder);
				data2 = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) LayoutUtil
						.ElementAt(vectLinks, j - 1, length, reversedOrder);

				if ((!data2.IsFixed() && !super.RefineLinksOrthogonal(data,
						data2, data.IsOrigin(nodeData, nodeSide),
						data2.IsOrigin(nodeData, nodeSide), nodeSide, false,
						linkOffset, stopIfDifferentDirection))
						&& stopIfDifferentDirection) {

					return;
				}
			}
		}

	}

}