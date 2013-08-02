package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public class SubgraphCorrectionBarycenterFixed implements ISubgraphCorrection {
	public void Correct(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			Rectangle2D boundingBox) {
		this.CorrectImpl(subgraph, layout, boundingBox, true);

	}

	private void CorrectImpl(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			Rectangle2D boundingBox, Boolean redraw) {
		if (subgraph != null) {
			IGraphModel parent = layout.GetGraphModel().get_Parent();
			if (parent != null) {
				InternalRect rect = GraphModelUtil
						.BoundingBox(parent, subgraph);
				parent.MoveNode(
						subgraph,
						boundingBox.get_X()
								+ (0.5f * (boundingBox.get_Width() - rect.Width)),
						boundingBox.get_Y()
								+ (0.5f * (boundingBox.get_Height() - rect.Height)));
			}
		}

	}

}