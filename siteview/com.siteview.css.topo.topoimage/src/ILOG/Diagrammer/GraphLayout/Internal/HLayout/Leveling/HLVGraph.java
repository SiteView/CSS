package ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import system.*;

public class HLVGraph extends HMAGraph {
	public HLVGraph() {
	}

	public void AddEdgeWithGroupCheck(HLVEdge edge, HLVNode source,
			HLVNode target) {
		HLVEdgeIterator inEdges = target.GetInEdges();

		while (inEdges.HasNext()) {
			HLVNode hLVSource = inEdges.Next().GetHLVSource();

			if (hLVSource.IsPropagationGroupStartNode()
					&& !this.BelongsToSameGroup(source, hLVSource)) {
				HLVEdge edge2 = new HLVEdge(0f);
				edge2.SetMinSpan(-2147483648);
				super.AddEdge(edge2, source, hLVSource);
			}
		}
		super.AddEdge(edge, source, target);

	}

	private Boolean BelongsToSameGroup(HLVNode node, HLVNode groupStartNode) {
		HLVEdgeIterator inEdges = node.GetInEdges();

		while (inEdges.HasNext()) {
			if (inEdges.Next().GetHLVSource() == groupStartNode) {

				return true;
			}
		}

		return false;

	}

	public HMAEdgeIterator GetEdges(Boolean backwards) {

		return new HLVGraphEdgeIterator(this, backwards);

	}

	public HMANodeIterator GetNodes(Boolean backwards) {

		return new HLVGraphNodeIterator(this, backwards);

	}

	public void UpdatePropagationFlags() {
		HLVNodeIterator nodes = (HLVNodeIterator) this.GetNodes(false);

		while (nodes.HasNext()) {
			nodes.Next().UpdatePropagationFlags();
		}

	}

}