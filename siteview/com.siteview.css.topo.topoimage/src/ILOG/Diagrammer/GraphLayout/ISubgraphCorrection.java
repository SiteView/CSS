package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.*;
import system.*;

public interface ISubgraphCorrection {
	void Correct(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			Rectangle2D boundingBox);

}