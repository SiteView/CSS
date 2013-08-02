package ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public final class HMAGraphEdgeIterator extends HTBaseEdgeIterator implements
		HMAEdgeIterator {
	public HMAGraphEdgeIterator(HMAGraph graph, Boolean backwards) {
		super(graph, backwards);
	}

	public HMAEdge Next() {

		return (HMAEdge) super.NextBaseEdge();

	}

	public HMAEdge Prev() {

		return (HMAEdge) super.PrevBaseEdge();

	}

}