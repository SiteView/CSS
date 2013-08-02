package ILOG.Diagrammer.GraphLayout;

import system.*;

public interface INodeSideFilter {
	Boolean Accept(IGraphModel graphModel, java.lang.Object link,
			Boolean origin, java.lang.Object node, int side);

}