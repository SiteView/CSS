package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.*;
import system.*;

public interface INodeBoxProvider {
	Rectangle2D GetBox(IGraphModel graphModel, java.lang.Object node);

}