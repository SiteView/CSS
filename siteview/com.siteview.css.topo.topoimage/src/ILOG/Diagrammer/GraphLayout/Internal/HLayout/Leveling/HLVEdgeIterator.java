package ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.HMAEdgeIterator;
import system.*;

public interface HLVEdgeIterator extends HMAEdgeIterator {
	Boolean HasNext();

	Boolean HasPrev();

	void Init(HTBaseEdge edge);

	HLVEdge Next();

	HLVEdge Prev();

}