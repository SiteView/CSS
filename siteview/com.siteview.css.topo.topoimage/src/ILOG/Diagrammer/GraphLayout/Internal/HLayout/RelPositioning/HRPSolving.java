package ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import system.*;

public final class HRPSolving extends
		ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.Algorithm {
	public PriorityQueue _queue;

	public HRPNode _rootOfNesting;

	public HRPSolving(HRPGraph graph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			PercCompleteController percController) {
		this.Init(graph, layout, percController);
		this._queue = new HRPSolvePriorityQueue();
	}

	private void CollectStartNodes() {
		HRPNodeIterator nodes = this.GetGraph().GetNodes(false);

		while (nodes.HasNext()) {
			HRPNode node = nodes.Next();
			if (node.GetIndegree() == 0) {
				this._queue.Add(node);
			}
			if (node.GetParent() == null) {
				this._rootOfNesting = node;
			}
		}
		super.AddPercPoints(1);
		super.LayoutStepPerformed();

	}

	private void CreateOrderingChain() {
		HRPGraph graph = this.GetGraph();

		while (!this._queue.IsEmpty()) {
			HRPNode node = (HRPNode) this._queue.PopMin();
			HRPNode parent = node.GetParent();
			if (parent != null) {

				if (node.IsNeighbored()) {
					this.CreateOrderingForNeighborChain(node);
				} else {
					parent.AppendToOrder(node);
				}
			}
			HMAEdgeIterator outEdges = node.GetOutEdges();

			while (outEdges.HasNext()) {
				HMAEdge edge = outEdges.Next();
				HRPNode hMATarget = (HRPNode) edge.GetHMATarget();
				graph.RemoveEdge(edge);
				if ((hMATarget != node) && (hMATarget.GetIndegree() == 0)) {
					this._queue.Add(hMATarget);
				}
			}
			graph.RemoveNode(node);
		}
		super.AddPercPoints(1);
		super.LayoutStepPerformed();

	}

	private void CreateOrderingForNeighborChain(HRPNode node) {
		HRPNode parent = node.GetParent();
		if (parent.GetFirstChildInOrder() == null) {
			HRPNodeIterator neighborChain = null;
			HRPNode leftmostOrExtremeNeighbor = node
					.GetLeftmostOrExtremeNeighbor();

			if (leftmostOrExtremeNeighbor.HasOrderedNeighbors()) {

				neighborChain = leftmostOrExtremeNeighbor.GetNeighborChain();

				while (neighborChain.HasNext()) {
					parent.AppendToOrder(neighborChain.Next());
				}
			} else {
				float[] array = new float[parent.GetNumberOfChildren()];
				Integer num = 0;

				neighborChain = leftmostOrExtremeNeighbor.GetNeighborChain();

				while (neighborChain.HasNext()) {

					array[num++] = neighborChain.Next().GetBaryCenter();
				}
				Integer num2 = this.NumberOfInversions(array);
				Integer num3 = this.NumberOfNonInversions(array);
				if (num2 < num3) {

					neighborChain = leftmostOrExtremeNeighbor
							.GetNeighborChain();

					while (neighborChain.HasNext()) {
						parent.AppendToOrder(neighborChain.Next());
					}
				} else {

					neighborChain = leftmostOrExtremeNeighbor
							.GetExtremeNeighbor(
									leftmostOrExtremeNeighbor
											.GetOppositeNeighborIndex(null))
							.GetNeighborChain();

					while (neighborChain.HasNext()) {
						parent.AppendToOrder(neighborChain.Next());
					}
				}
			}
		}

	}

	private HRPGraph GetGraph() {

		return (HRPGraph) super._graph;

	}

	public void Init(HRPGraph graph) {
		super._graph = graph;
		this._rootOfNesting = null;
		HRPNodeIterator nodes = this.GetGraph().GetNodes(false);

		while (nodes.HasNext()) {
			nodes.Next().Init();
		}
		this._queue.Init(this.GetGraph().GetNumberOfNodes());

	}

	private Integer NumberOfInversions(float[] array) {
		Integer num = 0;
		for (Integer i = 0; i < array.length; i++) {
			for (Integer j = i + 1; j < array.length; j++) {
				if (array[i] > array[j]) {
					num++;
				}
			}
		}

		return num;

	}

	private Integer NumberOfNonInversions(float[] array) {
		Integer num = 0;
		for (Integer i = 0; i < array.length; i++) {
			for (Integer j = i + 1; j < array.length; j++) {
				if (array[i] < array[j]) {
					num++;
				}
			}
		}

		return num;

	}

	@Override
	public void Run() {
		this.CollectStartNodes();
		this.CreateOrderingChain();
		this.GetGraph().RestoreTemporaryRemovedNodes();
		this.GetGraph().RestoreTemporaryRemovedEdges(false);
		this.StorePositionNumbers(this._rootOfNesting, 0);
		super.AddPercPoints(1);
		super.LayoutStepPerformed();

	}

	private void StorePositionNumbers(HRPNode node, Integer number) {
		while (node != null) {
			node.SetPositionNumber(number);
			this.StorePositionNumbers(node.GetFirstChildInOrder(), number);

			number += node.GetNumberOfRepresentedNodes();

			node = node.GetSiblingInOrder();
		}

	}

}