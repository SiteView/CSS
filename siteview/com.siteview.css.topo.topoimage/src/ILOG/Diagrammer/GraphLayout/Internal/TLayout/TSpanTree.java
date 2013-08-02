package ILOG.Diagrammer.GraphLayout.Internal.TLayout;

import system.*;

public final class TSpanTree extends TGraphAlgorithm {
	private Integer _firstUnvisitedIndex;

	public TSpanTree(TGraph graph) {
		super(graph);
		this._firstUnvisitedIndex = 0;
	}

	private void CalcSpanningTree(TNode root) {
		TGraph graph = super.GetGraph();
		Integer num = this._firstUnvisitedIndex;
		graph.SwapPosition(root, this._firstUnvisitedIndex++);
		while (num < this._firstUnvisitedIndex) {
			TNode parent = graph.GetNode(num++);
			super.AddPercPoints(1);
			super.LayoutStepPerformed();
			for (Integer i = 0; i < parent.GetNumberOfChildren(); i++) {
				TNode child = parent.GetChild(i);

				if (!child.IsVisited(this._firstUnvisitedIndex)) {
					child.SetOriginalParent(parent);
					graph.SwapPosition(child, this._firstUnvisitedIndex++);
				} else {
					parent.RemoveChild(child);
					i--;
				}
			}
		}

	}

	private Integer CalcTipOverBothSidesDummyNodes(TNode node) {

		if (!node.IsTipOverBothSides()) {

			return 0;
		}
		if (node.GetNumberOfChildren() <= 1) {

			return 0;
		}

		return ((node.GetNumberOfChildren() + 1) / 2);

	}

	private void CreateTipOverBothSidesDummyNodes(TNode node) {
		Integer numberOfChildren = node.GetNumberOfChildren();

		if (node.IsTipOverBothSides()) {
			if (numberOfChildren <= 1) {
				node.SetAlignmentStyle(0);
			} else {
				Integer num2 = null;
				TGraph graph = super.GetGraph();
				TNode[] nodeArray = new TNode[numberOfChildren];
				for (num2 = 0; num2 < numberOfChildren; num2++) {

					nodeArray[num2] = node.GetChild(num2);
				}
				node.SetChildrenCapacity(1);
				TNode child = null;
				Integer dirTowardsLeaves = graph.GetDirTowardsLeaves();
				float size = 0f;
				Boolean flag = false;
				num2 = 0;
				TNode node2 = graph.CreateDummyNode();
				node.AddChild(node2);
				node2.SetImmediateParent(node);
				while (num2 < numberOfChildren) {
					if (num2 >= (numberOfChildren - 2)) {
						flag = true;
					}
					node2.SetChildrenCapacity(flag ? (numberOfChildren - num2)
							: 3);

					size = nodeArray[num2].GetSize(dirTowardsLeaves);
					node2.SetSize(1 - dirTowardsLeaves, 0f);
					node2.SetSize(dirTowardsLeaves, size);
					nodeArray[num2].SetImmediateParent(node2);
					node2.AddChild(nodeArray[num2]);
					node2.SetEastWestNeighbor(0, nodeArray[num2]);
					num2++;
					if (num2 >= numberOfChildren) {

						return;
					}
					if (!flag) {

						child = graph.CreateDummyNode();
						node2.AddChild(child);
						child.SetImmediateParent(node2);
					}

					size = nodeArray[num2].GetSize(dirTowardsLeaves);
					if (size > node2.GetSize(dirTowardsLeaves)) {
						node2.SetSize(dirTowardsLeaves, size);
					}
					nodeArray[num2].SetImmediateParent(node2);
					node2.AddChild(nodeArray[num2]);
					node2.SetEastWestNeighbor(1, nodeArray[num2]);
					num2++;
					node2 = child;
				}
			}
		}

	}

	@Override
	public void DisposeIt() {
		super.DisposeIt();

	}

	private void RearrangeTipOverBothSides() {
		TGraph graph = super.GetGraph();

		if (!graph.IsRadialLayout()) {
			Integer num3 = null;
			Integer numberOfNodes = graph.GetNumberOfNodes();
			Integer numNewNodes = 0;
			for (num3 = 0; num3 < numberOfNodes; num3++) {

				numNewNodes += this.CalcTipOverBothSidesDummyNodes(graph
						.GetNode(num3));
			}
			if (numNewNodes > 0) {
				graph.IncreaseNodeCapacity(numNewNodes);
				for (num3 = 0; num3 < numberOfNodes; num3++) {
					this.CreateTipOverBothSidesDummyNodes(graph.GetNode(num3));
				}
			}
		}

	}

	private void ResortRootNodes() {
		TGraph graph = super.GetGraph();
		Integer numberOfNodes = graph.GetNumberOfNodes();
		Integer num2 = 0;
		for (Integer i = 0; i < numberOfNodes; i++) {
			TNode node = graph.GetNode(i);
			if (node.GetImmediateParent() == null) {
				graph.SwapPosition(node, num2++);
			}
		}

	}

	public void Run() {
		Integer num = null;
		TNode node = null;
		TGraph graph = super.GetGraph();
		Integer numberOfNodes = graph.GetNumberOfNodes();
		super.StartStep(graph.GetPercForSpanTree(), 7 * numberOfNodes);
		for (num = 0; num < numberOfNodes; num++) {

			node = graph.GetNode(num);
			node.SetIndex(num);
			node.SortChildren(graph, true);
			super.AddPercPoints(1);
			super.LayoutStepPerformed();
		}
		this._firstUnvisitedIndex = 0;
		TNode invisibleRoot = graph.GetInvisibleRoot();
		if (invisibleRoot != null) {
			this._firstUnvisitedIndex = 1;
		}

		node = this.SearchRootNode();
		Integer maxNumberOfChildren = 0;
		while (node != null) {
			maxNumberOfChildren++;
			this.CalcSpanningTree(node);

			node = this.SearchRootNode();
		}
		this.ResortRootNodes();
		if (invisibleRoot != null) {
			if (maxNumberOfChildren > 1) {
				invisibleRoot.SetChildrenCapacity(maxNumberOfChildren);
				num = 1;
				while ((num < numberOfNodes)
						&& (graph.GetNode(num).GetImmediateParent() == null)) {

					node = graph.GetNode(num);
					invisibleRoot.AddChild(node);
					node.SetOriginalParent(invisibleRoot);
					num++;
				}
			} else if (numberOfNodes > 1) {
				graph.SwapPosition(invisibleRoot, 1);
			}
		}
		super.AddPercPoints(numberOfNodes);
		super.LayoutStepPerformed();
		Boolean flag = graph.IsRadialLayout();
		for (num = 0; (num < numberOfNodes)
				&& (graph.GetNode(num).GetImmediateParent() == null); num++) {

			node = graph.GetNode(num);
			if (flag) {
				node.SetEastWestNeighbor(0, null);
				node.SetEastWestNeighbor(1, null);
			}
			node.ValidateEastWestNeighbor();
		}
		super.AddPercPoints(numberOfNodes);
		super.LayoutStepPerformed();
		for (num = 0; num < numberOfNodes; num++) {
			graph.GetNode(num).SortChildren(graph, false);
			super.AddPercPoints(1);
			super.LayoutStepPerformed();
		}
		this.RearrangeTipOverBothSides();
		super.LayoutStepPerformed();

		numberOfNodes = graph.GetNumberOfNodes();
		for (num = 0; (num < numberOfNodes)
				&& (graph.GetNode(num).GetImmediateParent() == null); num++) {
			graph.GetNode(num).InitPrevSiblings();
		}
		super.AddPercPoints(numberOfNodes);
		super.LayoutStepPerformed();
		graph.CalcMaxDepth();
		super.AddPercPoints(numberOfNodes);
		super.LayoutStepPerformed();

	}

	private TNode SearchRootNode() {
		TGraph graph = super.GetGraph();
		Integer numberOfNodes = graph.GetNumberOfNodes();
		Integer rootNodePreference = 0;
		TNode node = null;
		for (Integer i = this._firstUnvisitedIndex; i < numberOfNodes; i++) {
			TNode node2 = graph.GetNode(i);
			if ((node == null)
					|| (node2.GetRootNodePreference() > rootNodePreference)) {
				node = node2;

				rootNodePreference = node2.GetRootNodePreference();
			}
		}

		return node;

	}

}