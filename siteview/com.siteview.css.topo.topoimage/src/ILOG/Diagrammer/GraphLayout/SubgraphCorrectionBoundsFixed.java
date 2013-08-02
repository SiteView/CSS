package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.*;
import system.*;

public class SubgraphCorrectionBoundsFixed extends
		SubgraphCorrectionBarycenterFixed {
	@Override
	public void Correct(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			Rectangle2D boundingBox) {
		if (subgraph != null) {
			IGraphModel parent = layout.GetGraphModel().get_Parent();
			if (parent != null) {
				if ((parent instanceof GraphicContainerAdapter)
						&& (subgraph instanceof GraphicObject)) {
					GraphicObject obj2 = (GraphicObject) subgraph;
					obj2.set_Bounds(boundingBox);
				} else {
					super.Correct(subgraph, layout, boundingBox);
				}
			}
		}

	}

}