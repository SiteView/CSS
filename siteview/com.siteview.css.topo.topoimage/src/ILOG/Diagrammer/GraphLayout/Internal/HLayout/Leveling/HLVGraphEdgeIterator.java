package ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public final class HLVGraphEdgeIterator extends HTBaseEdgeIterator implements
		HLVEdgeIterator {
	public HLVGraphEdgeIterator(HLVGraph graph, Boolean backwards) {
		super(graph, backwards);
	}

	public HLVEdge Next() {

		return (HLVEdge) super.NextBaseEdge();

	}

	public HLVEdge Prev() {

		return (HLVEdge) super.PrevBaseEdge();

	}

}