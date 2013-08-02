package ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public interface HMANodeIterator {
	Boolean HasNext();

	Boolean HasPrev();

	void Init(HTBaseNode node);

	HMANode Next();

	HMANode Prev();

}