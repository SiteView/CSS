package ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import system.*;

public final class HRPOutEdgeIterator extends HTBaseOutEdgeIterator implements
		HMAEdgeIterator {
	public HRPOutEdgeIterator(HMANode node) {
		super(node);
	}

	public HMAEdge Next() {

		return (HMAEdge) super.NextBaseEdge();

	}

	public HMAEdge Prev() {

		return (HMAEdge) super.PrevBaseEdge();

	}

}