package ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public final class HRPGraphNodeIterator extends HTBaseNodeIterator implements
		HRPNodeIterator {
	public HRPGraphNodeIterator(HRPGraph graph, Boolean backwards) {
		super(graph, backwards);
	}

	public HRPNode Next() {

		return (HRPNode) super.NextBaseNode();

	}

	public HRPNode Prev() {

		return (HRPNode) super.PrevBaseNode();

	}

}