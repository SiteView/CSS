package ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public final class HMAAdjEdgeIterator extends HTBaseAdjEdgeIterator implements
		HMAEdgeIterator {
	public HMAAdjEdgeIterator(HMANode node) {
		super(node);
	}

	public HMAEdge Next() {

		return (HMAEdge) super.NextBaseEdge();

	}

	public HMAEdge Prev() {

		return (HMAEdge) super.PrevBaseEdge();

	}

}