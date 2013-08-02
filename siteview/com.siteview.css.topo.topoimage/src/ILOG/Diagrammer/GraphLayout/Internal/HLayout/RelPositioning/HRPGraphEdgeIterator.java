package ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import system.*;

public final class HRPGraphEdgeIterator extends HTBaseEdgeIterator implements
		HMAEdgeIterator {
	public HRPGraphEdgeIterator(HRPGraph graph, Boolean backwards) {
		super(graph, backwards);
	}

	public HMAEdge Next() {

		return (HMAEdge) super.NextBaseEdge();

	}

	public HMAEdge Prev() {

		return (HMAEdge) super.PrevBaseEdge();

	}

}