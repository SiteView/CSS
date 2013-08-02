package ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.HTBaseNode;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.HMANodeIterator;

public interface HRPNodeIterator extends HMANodeIterator {
	Boolean HasNext();

	Boolean HasPrev();

	void Init(HTBaseNode node);

	HRPNode Next();

	HRPNode Prev();

}