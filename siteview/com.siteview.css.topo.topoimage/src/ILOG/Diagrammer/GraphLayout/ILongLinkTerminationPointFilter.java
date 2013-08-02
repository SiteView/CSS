package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.*;
import system.*;

public interface ILongLinkTerminationPointFilter {
	Integer GetPenalty(IGraphModel graphModel, java.lang.Object link,
			Boolean origin, java.lang.Object node, Point2D point, int side,
			Integer proposedPenalty);

}