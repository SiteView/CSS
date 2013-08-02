package ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;
import system.Collections.*;

public final class Algorithm extends
		ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.Algorithm {
	private Integer _maxLevelNumber;

	private Stack _roots;

	public Algorithm(HLVGraph graph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			PercCompleteController percController) {
		this.Init(graph, layout, percController);
		this._roots = new Stack();
		this._maxLevelNumber = 0;
	}

	private Integer CalcBaryLevel(HLVNode node, float roundOffset) {
		Integer num = 0;
		Integer num2 = this._maxLevelNumber;
		float num6 = 0f;
		float num7 = 0f;
		Integer levelNumber = node.GetLevelNumber();
		HLVEdgeIterator inAndOutEdges = node.GetInAndOutEdges();

		while (inAndOutEdges.HasNext()) {
			HLVEdge edge = inAndOutEdges.Next();
			HLVNode opposite = edge.GetOpposite(node);
			Integer num3 = opposite.GetLevelNumber();
			float priority = edge.GetPriority();
			num6 += priority;
			num7 += priority * num3;

			if (!opposite.IsPropagationGroupEndNode()
					&& !opposite.IsPropagationGroupStartNode()) {
				Integer num4 = null;
				if (num3 == levelNumber) {

					return levelNumber;
				}
				if (num3 < levelNumber) {
					num4 = num3 + edge.GetMinSpan();
					if (num4 > num) {
						num = num4;
					}
				} else {
					num4 = num3 - edge.GetMinSpan();
					if ((num4 < num2) && (num4 >= 0)) {
						num2 = num4;
					}
				}
			}
		}
		if (num6 == 0f) {

			return levelNumber;
		}
		Integer num8 = (int) ((num7 / num6) + roundOffset);
		if (num8 < num) {

			return num;
		}
		if (num8 > num2) {

			return num2;
		}

		return num8;

	}

	private Integer CalcHighestPossibleLevel(HLVNode node) {
		Integer num = this._maxLevelNumber + 1;
		Integer levelNumber = node.GetLevelNumber();
		HLVEdgeIterator inAndOutEdges = node.GetInAndOutEdges();

		while (inAndOutEdges.HasNext()) {
			HLVEdge edge = inAndOutEdges.Next();
			HLVNode opposite = edge.GetOpposite(node);
			Integer num2 = opposite.GetLevelNumber();

			if (!opposite.IsPropagationGroupStartNode()) {
				if (num2 == levelNumber) {

					return levelNumber;
				}
				if (num2 > levelNumber) {
					Integer num3 = num2 - edge.GetMinSpan();
					if ((num3 < num) && (num3 >= 0)) {
						num = num3;
					}
				}
			}
		}
		if (num == (this._maxLevelNumber + 1)) {

			return levelNumber;
		}

		return num;

	}

	public void CalcInitialLevelNumbers() {
		HLVNode node = null;
		HLVGraph graph = this.GetGraph();
		this.CollectRoots();
		while (this._roots.get_Count() != 0) {
			node = (HLVNode) this._roots.Pop();
			Integer minLevelNumber = node.GetMinLevelNumber();
			if (minLevelNumber < 0) {
				minLevelNumber = 0;
			}
			if (minLevelNumber > this._maxLevelNumber) {
				this._maxLevelNumber = minLevelNumber;
			}
			node.SetLevelNumber(minLevelNumber);
			HLVEdgeIterator outEdges = node.GetOutEdges();

			while (outEdges.HasNext()) {
				HLVEdge edge = outEdges.Next();
				HLVNode hLVTarget = edge.GetHLVTarget();
				graph.RemoveEdge(edge);
				if ((hLVTarget != node) && (hLVTarget.GetIndegree() == 0)) {
					this._roots.Push(hLVTarget);
				}
			}
			graph.RemoveNode(node);
		}
		HLVNodeIterator nodes = (HLVNodeIterator) graph.GetNodes(true);

		while (nodes.HasNext()) {

			node = nodes.Next();
			node.SetLevelNumber(node.LimitLevelNumber(0));
		}
		graph.RestoreTemporaryRemovedNodes();
		graph.RestoreTemporaryRemovedEdges(false);
		super.AddPercPoints(10);
		super.LayoutStepPerformed();

	}

	private Integer CalcLowestPossibleLevel(HLVNode node) {
		Integer num = -1;
		Integer levelNumber = node.GetLevelNumber();
		HLVEdgeIterator inAndOutEdges = node.GetInAndOutEdges();

		while (inAndOutEdges.HasNext()) {
			HLVEdge edge = inAndOutEdges.Next();
			HLVNode opposite = edge.GetOpposite(node);
			Integer num2 = opposite.GetLevelNumber();

			if (!opposite.IsPropagationGroupEndNode()) {
				if (num2 == levelNumber) {

					return levelNumber;
				}
				if (num2 < levelNumber) {
					Integer num3 = num2 + edge.GetMinSpan();
					if (num3 > num) {
						num = num3;
					}
				}
			}
		}
		if (num == -1) {

			return levelNumber;
		}

		return num;

	}

	@Override
	public void Clean() {
		super.Clean();
		this._roots = null;

	}

	private void CollectRoots() {
		HLVNodeIterator nodes = (HLVNodeIterator) this.GetGraph().GetNodes(
				false);

		while (nodes.HasNext()) {
			HLVNode node = nodes.Next();
			if (node.GetIndegree() == 0) {
				this._roots.Push(node);
			}
		}
		super.LayoutStepPerformed();

	}

	private HLVGraph GetGraph() {

		return (HLVGraph) super._graph;

	}

	private void PullToBaryLevel(Integer maxIteration, Boolean roundUp) {
		HLVGraph graph = this.GetGraph();
		Boolean flag = true;
		Integer num = 0;
		float roundOffset = 0f;
		if (roundUp) {
			roundOffset = 0.5f;
		}
		while (flag && (num < maxIteration)) {
			flag = false;
			num++;
			HLVNodeIterator nodes = (HLVNodeIterator) graph.GetNodes(false);

			while (nodes.HasNext()) {
				HLVNode node = nodes.Next();
				Integer val = this.CalcBaryLevel(node, roundOffset);

				val = node.LimitLevelNumber(val);
				if (val != node.GetLevelNumber()) {
					if (val > this._maxLevelNumber) {
						this._maxLevelNumber = val;
					}
					node.SetLevelNumber(val);
					flag = true;
				}
			}
			super.AddPercPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void PullToHigherLevel() {
		HLVNode[] sortedNodes = new HLVNodeSort(this.GetGraph())
				.GetSortedNodes();
		for (Integer i = sortedNodes.length - 1; i >= 0; i--) {
			HLVNode node = sortedNodes[i];
			Integer val = this.CalcHighestPossibleLevel(node);

			val = node.LimitLevelNumber(val);
			if (val > this._maxLevelNumber) {
				this._maxLevelNumber = val;
			}
			node.SetLevelNumber(val);
		}
		super.AddPercPoints(10);
		super.LayoutStepPerformed();

	}

	private void PullToLowerLevel() {
		HLVNodeSort sort = new HLVNodeSort(this.GetGraph());
		for (HLVNode node : sort.GetSortedNodes()) {
			Integer val = this.CalcLowestPossibleLevel(node);

			val = node.LimitLevelNumber(val);
			if (val > this._maxLevelNumber) {
				this._maxLevelNumber = val;
			}
			node.SetLevelNumber(val);
		}
		super.AddPercPoints(10);
		super.LayoutStepPerformed();

	}

	@Override
	public void Run() {
		this.CalcInitialLevelNumbers();
		this.PullToHigherLevel();
		this.PullToLowerLevel();
		this.PullToBaryLevel(5, true);

	}

	public class HLVNodeSort extends QuickSort {
		public HLVNode[] _nodes;

		public HLVNodeSort(HLVGraph graph) {
			this._nodes = new HLVNode[graph.GetNumberOfNodes()];
			HLVNodeIterator nodes = (HLVNodeIterator) graph.GetNodes(false);
			Integer num = 0;

			while (nodes.HasNext()) {

				this._nodes[num++] = nodes.Next();
			}
		}

		@Override
		public Integer Compare(Integer loc1, Integer loc2) {

			return (this._nodes[loc1].GetLevelNumber() - this._nodes[loc2]
					.GetLevelNumber());

		}

		public HLVNode[] GetSortedNodes() {
			this.Sort();

			return this._nodes;

		}

		public void Sort() {
			super.Sort(this._nodes.length);

		}

		@Override
		public void Swap(Integer loc1, Integer loc2) {
			HLVNode node = this._nodes[loc1];
			this._nodes[loc1] = this._nodes[loc2];
			this._nodes[loc2] = node;

		}

	}
}