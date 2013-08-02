package ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public final class HLVGraphNodeIterator extends HTBaseNodeIterator implements
		HLVNodeIterator {
	public HLVGraphNodeIterator(HLVGraph graph, Boolean backwards) {
		super(graph, backwards);
	}

	public HLVNode Next() {

		return (HLVNode) super.NextBaseNode();

	}

	public HLVNode Prev() {

		return (HLVNode) super.PrevBaseNode();

	}

}