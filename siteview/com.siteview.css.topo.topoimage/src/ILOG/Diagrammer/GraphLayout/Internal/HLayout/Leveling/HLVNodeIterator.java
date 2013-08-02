package ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.HMANodeIterator;
import system.*;

public interface HLVNodeIterator extends HMANodeIterator {
	Boolean HasNext();

	Boolean HasPrev();

	void Init(HTBaseNode node);

	HLVNode Next();

	HLVNode Prev();

}