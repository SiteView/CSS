package ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public final class HMAGraphNodeIterator extends HTBaseNodeIterator implements
		HMANodeIterator {
	public HMAGraphNodeIterator(HMAGraph graph, Boolean backwards) {
		super(graph, backwards);
	}

	public HMANode Next() {

		return (HMANode) super.NextBaseNode();

	}

	public HMANode Prev() {

		return (HMANode) super.PrevBaseNode();

	}

}