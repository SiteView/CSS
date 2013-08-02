package ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public final class HLVOutEdgeIterator extends HTBaseOutEdgeIterator implements
		HLVEdgeIterator {
	public HLVOutEdgeIterator(HLVNode node) {
		super(node);
	}

	public HLVEdge Next() {

		return (HLVEdge) super.NextBaseEdge();

	}

	public HLVEdge Prev() {

		return (HLVEdge) super.PrevBaseEdge();

	}

}