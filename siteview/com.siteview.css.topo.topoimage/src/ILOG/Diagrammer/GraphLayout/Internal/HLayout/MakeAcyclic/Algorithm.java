package ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;
import system.Collections.*;

public final class Algorithm extends
		ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.Algorithm {
	private Stack _leaves;

	private Stack _roots;

	private ArrayList _selfLoops;

	private Boolean _topologicalOrdering = false;

	private ArrayList _twoCycles;

	public Algorithm(HMAGraph graph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			PercCompleteController percController) {
		this.Init(graph, layout, percController);
		this._selfLoops = new ArrayList(5);
		this._twoCycles = new ArrayList(5);
		this._roots = new Stack();
		this._leaves = new Stack();
		this._topologicalOrdering = false;
	}

	private void BreakRemainingCycles() {
		HMANode node = null;
		HMAGraph graph = this.GetGraph();
		graph.RecalcNodeEdgeData();
		super.AddPercPoints(1);
		super.LayoutStepPerformed();
		HMANodeIterator nodes = graph.GetNodes(false);

		while (nodes.HasNext()) {

			node = nodes.Next();
			if (node.GetIndegree() == 0) {
				this._roots.Push(node);
			} else if (node.GetOutdegree() == 0) {
				this._leaves.Push(node);
			}
		}
		Integer numberOfNodes = graph.GetNumberOfNodes() + 1;
		while (graph.GetNumberOfNodes() > 0) {
			while (this._roots.get_Count() != 0) {
				node = (HMANode) this._roots.Pop();
				this.RemoveOutEdges(node);
				if (node.GetOwnerBaseGraph() != null) {
					graph.RemoveNode(node);
				}
			}
			while (this._leaves.get_Count() != 0) {
				node = (HMANode) this._leaves.Pop();
				this.RemoveInEdges(node);
				if (node.GetOwnerBaseGraph() != null) {
					graph.RemoveNode(node);
				}
			}
			if (graph.GetNumberOfNodes() >= numberOfNodes) {
				throw (new system.Exception("Infinite Loop in Algorithm"));
			}

			numberOfNodes = graph.GetNumberOfNodes();
			if (numberOfNodes > 0) {
				super.AddPercPoints(1);
				super.LayoutStepPerformed();

				node = this.SearchBreakNode();
				this.ReverseAndRemoveInEdges(node);
				this._roots.Push(node);
			}
		}
		graph.RestoreTemporaryRemovedNodes();
		graph.RestoreTemporaryRemovedEdges(false);
		super.LayoutStepPerformed();

	}

	private void BreakTwoCycles() {
		HMAEdge edge = null;
		HMAGraph graph = this.GetGraph();
		graph.ClearNodeEdgeData();
		super.AddPercPoints(1);
		super.LayoutStepPerformed();
		HMANodeIterator nodes = graph.GetNodes(false);

		while (nodes.HasNext()) {
			HMANode node = nodes.Next();
			HMAEdgeIterator outEdges = node.GetOutEdges();

			while (outEdges.HasNext()) {

				edge = outEdges.Next();
				edge.GetHMATarget().AddInEdgeData(edge);
			}

			outEdges = node.GetInEdges();

			while (outEdges.HasNext()) {

				edge = outEdges.Next();
				edge.GetHMASource().AddOutEdgeData(edge);
			}

			outEdges = node.GetOutEdges();

			while (outEdges.HasNext()) {

				edge = outEdges.Next();
				HMANode hMATarget = edge.GetHMATarget();
				float outPriority = hMATarget.GetOutPriority();
				float inPriority = hMATarget.GetInPriority();
				Integer outUnbreakCount = hMATarget.GetOutUnbreakCount();
				Integer inUnbreakCount = hMATarget.GetInUnbreakCount();
				if ((outPriority == 0f) && (outUnbreakCount == 0)) {
					hMATarget.ClearEdgeData();
				} else {
					edge.MarkTwoCycle();
					if ((outUnbreakCount > inUnbreakCount)
							|| ((outUnbreakCount == inUnbreakCount) && (outPriority >= inPriority))) {
						this._twoCycles.Add(edge);
					}
				}
			}

			outEdges = node.GetInEdges();

			while (outEdges.HasNext()) {
				outEdges.Next().GetHMASource().ClearEdgeData();
			}
		}
		super.AddPercPoints(1);
		super.LayoutStepPerformed();
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this._twoCycles);

		while (enumerator.HasMoreElements()) {
			edge = (HMAEdge) enumerator.NextElement();
			edge.MarkReversed();
			edge.Reverse();
		}

	}

	private void CalcTopologicalOrder() {
		HMANode node = null;
		HMAGraph graph = this.GetGraph();
		HMANodeIterator nodes = graph.GetNodes(false);
		Integer num = 0;

		while (nodes.HasNext()) {

			node = nodes.Next();
			if (node.GetIndegree() == 0) {
				this._roots.Push(node);
			}
		}
		while (this._roots.get_Count() != 0) {
			node = (HMANode) this._roots.Pop();
			node.SetOrderingNumber(num++);
			this.RemoveOutEdges(node);
			graph.RemoveNode(node);
		}
		graph.RestoreTemporaryRemovedNodes();
		graph.RestoreTemporaryRemovedEdges(false);

	}

	@Override
	public void Clean() {
		super.Clean();
		this._selfLoops = null;
		this._twoCycles = null;
		this._roots = null;
		this._leaves = null;

	}

	private HMAGraph GetGraph() {

		return (HMAGraph) super._graph;

	}

	public IJavaStyleEnumerator GetSelfLoops() {
		if (this._selfLoops != null) {

			return TranslateUtil.Collection2JavaStyleEnum(this._selfLoops);
		}

		return LayoutUtil.GetVoidEnumeration();

	}

	public IJavaStyleEnumerator GetTwoCycles() {
		if (this._twoCycles != null) {

			return TranslateUtil.Collection2JavaStyleEnum(this._twoCycles);
		}

		return LayoutUtil.GetVoidEnumeration();

	}

	public Boolean IsTopologicalOrdering() {

		return this._topologicalOrdering;

	}

	private void RemoveInEdges(HMANode node) {
		HMAGraph graph = this.GetGraph();
		HMAEdgeIterator inEdges = node.GetInEdges();

		while (inEdges.HasNext()) {
			HMAEdge edge = inEdges.Next();
			HMANode hMASource = edge.GetHMASource();
			graph.RemoveEdge(edge);
			if (hMASource.GetOutdegree() == 0) {
				this._leaves.Push(hMASource);
			}
		}

	}

	private void RemoveOutEdges(HMANode node) {
		HMAGraph graph = this.GetGraph();
		HMAEdgeIterator outEdges = node.GetOutEdges();

		while (outEdges.HasNext()) {
			HMAEdge edge = outEdges.Next();
			HMANode hMATarget = edge.GetHMATarget();
			graph.RemoveEdge(edge);
			if (hMATarget.GetIndegree() == 0) {
				this._roots.Push(hMATarget);
			}
		}

	}

	private void RemoveSelfLoops() {
		HMAGraph graph = this.GetGraph();
		HMAEdgeIterator edges = graph.GetEdges(false);

		while (edges.HasNext()) {
			HMAEdge edge = edges.Next();

			if (edge.IsSelfLoop()) {
				graph.RemoveEdge(edge);
				this._selfLoops.Add(edge);
			}
		}
		super.AddPercPoints(1);
		super.LayoutStepPerformed();

	}

	private void ReverseAndRemoveInEdges(HMANode node) {
		HMAGraph graph = this.GetGraph();
		HMAEdgeIterator inEdges = node.GetInEdges();

		while (inEdges.HasNext()) {
			HMAEdge edge = inEdges.Next();
			HMANode hMASource = edge.GetHMASource();
			graph.RemoveEdge(edge);
			edge.MarkReversed();
			edge.Reverse();
			if (hMASource.GetOutdegree() == 0) {
				this._leaves.Push(hMASource);
			}
		}

	}

	@Override
	public void Run() {
		this.GetGraph().EmptyTemporaryRemovedNodes();
		this.GetGraph().EmptyTemporaryRemovedEdges();
		this.RemoveSelfLoops();
		this.BreakTwoCycles();
		this.BreakRemainingCycles();

		if (this.IsTopologicalOrdering()) {
			this.CalcTopologicalOrder();
		}

	}

	private HMANode SearchBreakNode() {
		float num = 0f;
		float num2 = 0f;
		Integer num3 = 0;
		HMANode node = null;
		float inPriority = 0f;
		float outPriority = 0f;
		Integer inUnbreakCount = 0;
		HMANodeIterator nodes = this.GetGraph().GetNodes(false);

		while (nodes.HasNext()) {
			HMANode node2 = nodes.Next();

			inPriority = node2.GetInPriority();

			outPriority = node2.GetOutPriority();

			inUnbreakCount = node2.GetInUnbreakCount();
			if (((node == null) || (inUnbreakCount < num3))
					|| ((inUnbreakCount == num3) && ((inPriority < num) || ((inPriority == num) && (outPriority > num2))))) {
				node = node2;
				num = inPriority;
				num2 = outPriority;
				num3 = inUnbreakCount;
			}
		}

		return node;

	}

	public void SetTopologicalOrdering(Boolean flag) {
		this._topologicalOrdering = flag;

	}

}