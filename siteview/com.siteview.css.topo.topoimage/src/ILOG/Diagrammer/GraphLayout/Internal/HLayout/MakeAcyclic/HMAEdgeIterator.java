package ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public interface HMAEdgeIterator {
	Boolean HasNext();

	Boolean HasPrev();

	void Init(HTBaseEdge edge);

	HMAEdge Next();

	HMAEdge Prev();

}