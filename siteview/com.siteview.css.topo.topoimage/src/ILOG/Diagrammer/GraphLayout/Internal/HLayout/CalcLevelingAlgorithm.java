package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import system.*;
import system.Collections.*;

public final class CalcLevelingAlgorithm extends HGraphAlgorithm {
	private ConstraintManager _constraintManager;

	private Boolean _incrementalLayout = false;

	private HLVGraph _levelingGraph;

	private HLVNode[] _levelingNodes;

	private Boolean _needFirstBypassLevel = false;

	private HLVNode _northExtremityLevelingNode;

	private HNode _northExtremityNode;

	private Integer[] _sameLevelFlags;

	private HLVNode _southExtremityLevelingNode;

	private HNode _southExtremityNode;

	public CalcLevelingAlgorithm(HGraph graph,
			ConstraintManager constraintManager) {
		this.Init(graph);
		this._levelingGraph = new HLVGraph();
		Integer numberOfNodes = graph.GetNumberOfNodes();
		this._sameLevelFlags = new Integer[numberOfNodes];
		this._levelingNodes = new HLVNode[numberOfNodes];
		this._constraintManager = constraintManager;

		this._incrementalLayout = graph.IsIncrementalMode();
		this._needFirstBypassLevel = false;
	}

	private void AddEdge(HLVNode source, HLVNode target,
			Boolean sourceMarkedForIncremental,
			Boolean targetMarkedForIncremental, float priority, Integer minSpan) {
		if (target == this._northExtremityLevelingNode) {
			target = source;
			source = this._northExtremityLevelingNode;
		}
		if (source == this._southExtremityLevelingNode) {
			source = target;
			target = this._southExtremityLevelingNode;
		}
		if ((((source != null) && (target != null)) && (source != target))
				&& ((!this._incrementalLayout || sourceMarkedForIncremental) || targetMarkedForIncremental)) {
			if (priority > 10000f) {
				priority = 10000f;
			}
			this.GetLevelingGraph().AddEdgeWithGroupCheck(
					new HLVEdge(priority, minSpan), source, target);
		}

	}

	private void CheckBypassLevel() {
		this._needFirstBypassLevel = false;
		HLinkIterator links = super.GetGraph().GetLinks();

		while (links.HasNext()) {
			HLink link = links.Next();
			if ((link.GetFrom().GetLevelNumber() == 0)
					&& (link.GetFromPortSide() == 0)) {
				this._needFirstBypassLevel = true;
			}
			if ((link.GetTo().GetLevelNumber() == 0)
					&& (link.GetToPortSide() == 0)) {
				this._needFirstBypassLevel = true;
			}
			if (this._needFirstBypassLevel) {

				return;
			}
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	@Override
	public void Clean() {
		super.Clean();
		this._levelingGraph = null;
		this._sameLevelFlags = null;
		this._levelingNodes = null;
		this._constraintManager = null;

	}

	private void CreateLevelingNodes() {
		for (Integer i = 0; i < this._levelingNodes.length; i++) {
			if (this._sameLevelFlags[i] == i) {
				this._levelingNodes[i] = new HLVNode();
				this.GetLevelingGraph().AddNode(this._levelingNodes[i]);
			} else {
				Integer index = this.FindLevelingNodeIndex(i);
				this._levelingNodes[i] = this._levelingNodes[index];
			}
		}
		this._sameLevelFlags = null;

	}

	private Integer FindLevelingNodeIndex(HNode node) {

		return this.FindLevelingNodeIndex(node.GetNumID());

	}

	private Integer FindLevelingNodeIndex(Integer numID) {
		Integer index = numID;
		while (this._sameLevelFlags[index] != index) {
			index = this._sameLevelFlags[index];
		}
		this._sameLevelFlags[numID] = index;

		return index;

	}

	private HLVNode GetGroupEntryNode(HierarchicalNodeGroup g, float priority) {
		if (g.GetEntryNode() == null) {
			HLVNode node = new HLVNode();
			this.GetLevelingGraph().AddNode(node);
			g.SetEntryNode(node);
			IJavaStyleEnumerator enumerator = new JavaStyleEnumerator(
					g.GetEnumerator());

			while (enumerator.HasMoreElements()) {
				HLVNode levelingNode = this.GetLevelingNode(enumerator
						.NextElement());
				HLVEdge edge = new HLVEdge(0f, 0);
				edge.MakeUnbreakable();
				this.GetLevelingGraph().AddEdge(edge, node, levelingNode);
			}
		} else {
			g.GetEntryNode().SetMinOutPriority(priority);
		}

		return g.GetEntryNode();

	}

	private HLVNode GetGroupExitNode(HierarchicalNodeGroup g, float priority) {
		if (g.GetExitNode() == null) {
			HLVNode node = new HLVNode();
			this.GetLevelingGraph().AddNode(node);
			g.SetExitNode(node);
			IJavaStyleEnumerator enumerator = new JavaStyleEnumerator(
					g.GetEnumerator());

			while (enumerator.HasMoreElements()) {
				HLVNode levelingNode = this.GetLevelingNode(enumerator
						.NextElement());
				HLVEdge edge = new HLVEdge(0f, 0);
				edge.MakeUnbreakable();
				this.GetLevelingGraph().AddEdge(edge, levelingNode, node);
			}
		} else {
			g.GetExitNode().SetMinInPriority(priority);
		}

		return g.GetExitNode();

	}

	private HLVGraph GetLevelingGraph() {

		return this._levelingGraph;

	}

	private HLVNode GetLevelingNode(HNode node) {

		return this._levelingNodes[node.GetNumID()];

	}

	private HLVNode GetLevelingNode(java.lang.Object node) {

		return this._levelingNodes[super.GetGraph().GetNode(node).GetNumID()];

	}

	private Integer GetMarkedForIncrementalSize(HierarchicalNodeGroup g) {
		HGraph graph = super.GetGraph();
		Integer num = 0;
		IJavaStyleEnumerator enumerator = new JavaStyleEnumerator(
				g.GetEnumerator());

		while (enumerator.HasMoreElements()) {

			if (graph.GetNode(enumerator.NextElement())
					.IsMarkedForIncremental()) {
				num++;
			}
		}

		return num;

	}

	private void HandleGroupSpreadConstraints() {
		IJavaStyleEnumerator constraints = this._constraintManager
				.GetConstraints();

		while (constraints.HasMoreElements()) {
			HierarchicalConstraint constraint = (HierarchicalConstraint) constraints
					.NextElement();
			if (constraint.IsValidForLayout()
					&& (constraint instanceof HierarchicalGroupSpreadConstraint)) {
				HierarchicalGroupSpreadConstraint constraint2 = (HierarchicalGroupSpreadConstraint) constraint;
				HierarchicalNodeGroup g = constraint2.GetGroup();
				Integer spreadSize = constraint2.get_SpreadSize();
				if ((spreadSize > 0)
						&& (!this._incrementalLayout || ((g.get_Count() - this
								.GetMarkedForIncrementalSize(g)) <= 1))) {
					HLVNode groupEntryNode = this.GetGroupEntryNode(g, 10000f);
					HLVNode groupExitNode = this.GetGroupExitNode(g, 10000f);
					groupEntryNode.ConnectToOtherGroupNode(groupExitNode,
							spreadSize);
					groupExitNode.ConnectToOtherGroupNode(groupEntryNode,
							-spreadSize);
				}
			}
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void HandleIncrementalLeveling(ArrayList incrLevels) {
		HGraph graph = super.GetGraph();
		Integer edgeFlow = graph.GetEdgeFlow();
		float levelRange = 0.5f * graph.GetMinDistBetweenNodes(edgeFlow);
		HNode[] nodesSortedByCoordForIncremental = graph
				.GetNodesSortedByCoordForIncremental(edgeFlow);
		super.LayoutStepPerformed();
		Integer num5 = 0;
		for (Integer i = 0; i < nodesSortedByCoordForIncremental.length; i++) {
			HNode node = nodesSortedByCoordForIncremental[i];

			if (!node.IsMarkedForIncremental()) {
				node.SetSpecLevelNumber(-1);
				node.SetSpecPositionInLevel(-1);
				IncrementalLevel level2 = null;
				float num4 = 0f;
				for (Integer j = num5; j < incrLevels.get_Count(); j++) {
					IncrementalLevel level = (IncrementalLevel) incrLevels
							.get_Item(j);

					if (level.MayContain(node, levelRange)) {
						float num3 = level.GetReferenceCoord()
								- node.GetCenter(edgeFlow);
						if (num3 < 0f) {
							num3 = -num3;
						}
						if (level2 == null) {
							level2 = level;
							num4 = num3;
						} else if (num3 < num4) {
							level2 = level;
							num4 = num3;
						}
					} else {
						num5 = j + 1;
					}
				}
				if (level2 != null) {
					level2.AddNode(node);
				} else {
					incrLevels.Add(new IncrementalLevel(this, node));
				}
			}
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void HandleIncrementalLevelingOrder(ArrayList incrementalLevels) {
		HLVNode levelingNode = null;
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(incrementalLevels);
		HLVGraph levelingGraph = this.GetLevelingGraph();
		for (HLVNode node = null; enumerator.HasMoreElements(); node = levelingNode) {

			levelingNode = this.GetLevelingNode(((IncrementalLevel) enumerator
					.NextElement()).GetReferenceNode());
			if (node != null) {
				HLVEdge edge = new HLVEdge(10000f, 1);
				edge.MakeUnbreakable();
				levelingGraph.AddEdgeWithGroupCheck(edge, node, levelingNode);
			}
		}
		super.GetPercController().AddPoints(3);
		super.LayoutStepPerformed();

	}

	private void HandleLevelRangeConstraints() {
		IJavaStyleEnumerator constraints = this._constraintManager
				.GetConstraints();
		HGraph graph = super.GetGraph();

		while (constraints.HasMoreElements()) {
			HierarchicalConstraint constraint = (HierarchicalConstraint) constraints
					.NextElement();
			if (constraint.IsValidForLayout()
					&& (constraint instanceof HierarchicalLevelRangeConstraint)) {
				HierarchicalLevelRangeConstraint constraint2 = (HierarchicalLevelRangeConstraint) constraint;
				Integer minLevel = constraint2.get_MinLevel();
				Integer maxLevel = constraint2.get_MaxLevel();
				java.lang.Object subject = constraint2.GetSubject();
				if (subject instanceof HierarchicalNodeGroup) {
					IJavaStyleEnumerator enumerator2 = new JavaStyleEnumerator(
							((HierarchicalNodeGroup) subject).GetEnumerator());

					while (enumerator2.HasMoreElements()) {
						this.SetMinMaxLevel(
								graph.GetNode(enumerator2.NextElement()),
								minLevel, maxLevel);
					}
				} else {
					this.SetMinMaxLevel(graph.GetNode(subject), minLevel,
							maxLevel);
				}
			}
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void HandleLinks() {
		HLinkIterator links = super.GetGraph().GetLinks();

		while (links.HasNext()) {
			Integer num3 = null;
			HLVNode levelingNode = null;
			HLVNode node2 = null;
			HLink link = links.Next();
			Integer fromPortSide = link.GetFromPortSide();
			Integer toPortSide = link.GetToPortSide();
			if (((fromPortSide == 0) && (toPortSide != 0))
					|| ((fromPortSide != 2) && (toPortSide == 2))) {

				levelingNode = this.GetLevelingNode(link.GetTo());

				node2 = this.GetLevelingNode(link.GetFrom());
				num3 = 1;
			} else if (((fromPortSide == 0) && (toPortSide == 0))
					|| ((fromPortSide == 2) && (toPortSide == 2))) {
				levelingNode = (HLVNode) (node2 = null);
				num3 = 0;
			} else {

				levelingNode = this.GetLevelingNode(link.GetFrom());

				node2 = this.GetLevelingNode(link.GetTo());
				num3 = 1;
			}
			this.AddEdge(levelingNode, node2, link.GetFrom()
					.IsMarkedForIncremental(), link.GetTo()
					.IsMarkedForIncremental(), link.GetStartSegment()
					.GetPriority(), num3);
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void HandleNorthSouthExtremityConstraintsPass1() {
		this._northExtremityNode = null;
		this._southExtremityNode = null;
		HGraph graph = super.GetGraph();
		IJavaStyleEnumerator constraints = this._constraintManager
				.GetConstraints();

		while (constraints.HasMoreElements()) {
			HierarchicalConstraint constraint = (HierarchicalConstraint) constraints
					.NextElement();
			if (constraint.IsValidForLayout()
					&& (constraint instanceof HierarchicalExtremityConstraint)) {
				HNode node = null;
				HierarchicalExtremityConstraint constraint2 = (HierarchicalExtremityConstraint) constraint;
				if (constraint2.get_Side() == HierarchicalLayoutSide.North) {

					node = graph.GetNode(constraint2.GetNode());
					if (node.IsMarkedForIncremental()
							|| !this._incrementalLayout) {
						if (this._northExtremityNode == null) {
							this._northExtremityNode = node;
						} else {
							this.UnionLevelNodeIndex(node,
									this._northExtremityNode);
						}
					}
				} else if (constraint2.get_Side() == HierarchicalLayoutSide.South) {

					node = graph.GetNode(constraint2.GetNode());
					if (node.IsMarkedForIncremental()
							|| !this._incrementalLayout) {
						if (this._southExtremityNode == null) {
							this._southExtremityNode = node;
							continue;
						}
						this.UnionLevelNodeIndex(node, this._southExtremityNode);
					}
				}
			}
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void HandleNorthSouthExtremityConstraintsPass2() {
		HLVGraph levelingGraph = this.GetLevelingGraph();
		if (this._northExtremityNode != null) {
			HLVNode levelingNode = this
					.GetLevelingNode(this._northExtremityNode);
			HLVNodeIterator nodes = (HLVNodeIterator) levelingGraph
					.GetNodes(false);

			while (nodes.HasNext()) {
				HLVNode target = nodes.Next();
				if (target != levelingNode) {
					HLVEdge edge = new HLVEdge(10000f, 1);
					levelingGraph.AddEdge(edge, levelingNode, target);
				}
			}
		}
		if (this._southExtremityNode != null) {
			HLVNode node3 = this.GetLevelingNode(this._southExtremityNode);
			HLVNodeIterator iterator2 = (HLVNodeIterator) levelingGraph
					.GetNodes(false);

			while (iterator2.HasNext()) {
				HLVNode source = iterator2.Next();
				if (source != node3) {
					HLVEdge edge2 = new HLVEdge(10000f, 1);
					levelingGraph.AddEdgeWithGroupCheck(edge2, source, node3);
				}
			}
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void HandleRelativeLevelConstraints() {
		HGraph graph = super.GetGraph();
		IJavaStyleEnumerator constraints = this._constraintManager
				.GetConstraints();

		while (constraints.HasMoreElements()) {
			HierarchicalConstraint constraint = (HierarchicalConstraint) constraints
					.NextElement();
			if (constraint.IsValidForLayout()
					&& (constraint instanceof HierarchicalRelativeLevelConstraint)) {
				HLVNode groupExitNode = null;
				HLVNode groupEntryNode = null;
				Boolean flag2 = null;
				HierarchicalRelativeLevelConstraint constraint2 = (HierarchicalRelativeLevelConstraint) constraint;
				java.lang.Object lowerSubject = constraint2.GetLowerSubject();
				java.lang.Object higherSubject = constraint2.GetHigherSubject();
				Boolean sourceMarkedForIncremental = flag2 = true;
				float priority = constraint2.get_Priority();
				if (lowerSubject instanceof HierarchicalNodeGroup) {

					groupExitNode = this.GetGroupExitNode(
							(HierarchicalNodeGroup) lowerSubject, priority);
				} else {

					groupExitNode = this.GetLevelingNode(lowerSubject);

					sourceMarkedForIncremental = graph.GetNode(lowerSubject)
							.IsMarkedForIncremental();
				}
				if (higherSubject instanceof HierarchicalNodeGroup) {

					groupEntryNode = this.GetGroupEntryNode(
							(HierarchicalNodeGroup) higherSubject, priority);
				} else {

					groupEntryNode = this.GetLevelingNode(higherSubject);

					flag2 = graph.GetNode(higherSubject)
							.IsMarkedForIncremental();
				}
				this.AddEdge(groupExitNode, groupEntryNode,
						sourceMarkedForIncremental, flag2, priority, 1);
			}
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void HandleSameLevelConstraints() {
		HGraph graph = super.GetGraph();
		IJavaStyleEnumerator constraints = this._constraintManager
				.GetConstraints();

		while (constraints.HasMoreElements()) {
			HierarchicalConstraint constraint = (HierarchicalConstraint) constraints
					.NextElement();

			if (constraint.IsValidForLayout()) {
				if (constraint instanceof HierarchicalSameLevelConstraint) {
					HierarchicalSameLevelConstraint constraint2 = (HierarchicalSameLevelConstraint) constraint;
					HNode node = graph.GetNode(constraint2.GetFirstNode());
					HNode node2 = graph.GetNode(constraint2.GetSecondNode());
					if ((!this._incrementalLayout || node
							.IsMarkedForIncremental())
							|| node2.IsMarkedForIncremental()) {
						this.UnionLevelNodeIndex(node, node2);
					}
				} else if (constraint instanceof HierarchicalGroupSpreadConstraint) {
					HierarchicalGroupSpreadConstraint constraint3 = (HierarchicalGroupSpreadConstraint) constraint;
					HierarchicalNodeGroup g = constraint3.GetGroup();
					if (((constraint3.get_SpreadSize() == 0) && (g != null))
							&& (!this._incrementalLayout || ((g.get_Count() - this
									.GetMarkedForIncrementalSize(g)) <= 1))) {
						HNode node3 = null;
						IJavaStyleEnumerator enumerator2 = new JavaStyleEnumerator(
								constraint3.GetGroup().GetEnumerator());

						while (enumerator2.HasMoreElements()) {
							if (node3 == null) {

								node3 = graph
										.GetNode(enumerator2.NextElement());
							} else {
								this.UnionLevelNodeIndex(node3, graph
										.GetNode(enumerator2.NextElement()));
							}
						}
					}
				}
			}
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void HandleSpecLevelIndex() {
		HNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			Integer specLevelNumber = node.GetSpecLevelNumber();
			this.SetMinMaxLevel(node, specLevelNumber, specLevelNumber);
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void InitLevelingNodeIndex() {
		Integer numberOfNodes = super.GetGraph().GetNumberOfNodes();
		for (Integer i = 0; i < numberOfNodes; i++) {
			this._sameLevelFlags[i] = i;
		}

	}

	public Boolean NeedFirstBypassLevel() {

		return this._needFirstBypassLevel;

	}

	@Override
	public void Run() {
		ArrayList incrLevels = new ArrayList(2);
		HGraph graph = super.GetGraph();
		Integer numberOfNodes = graph.GetNumberOfNodes();
		Integer num3 = (graph.GetNumberOfLinks() - numberOfNodes) / 5;
		if (num3 < 0) {
			num3 = 0;
		}
		super.GetPercController().StartStep(graph._percForPartitioning,
				(num3 + 50) + (numberOfNodes / 15));
		graph.UpdateNodeIDs();
		IJavaStyleEnumerator groups = this._constraintManager.GetGroups();

		while (groups.HasMoreElements()) {
			((HierarchicalNodeGroup) groups.NextElement())
					.InitializeBeforeLeveling();
		}
		this.InitLevelingNodeIndex();
		if (this._incrementalLayout) {
			this.HandleIncrementalLeveling(incrLevels);
		}
		this.HandleNorthSouthExtremityConstraintsPass1();
		this.HandleSameLevelConstraints();
		this.CreateLevelingNodes();
		if (this._northExtremityNode != null) {

			this._northExtremityLevelingNode = this
					.GetLevelingNode(this._northExtremityNode);
		} else {
			this._northExtremityLevelingNode = null;
		}
		if (this._southExtremityNode != null) {

			this._southExtremityLevelingNode = this
					.GetLevelingNode(this._southExtremityNode);
		} else {
			this._southExtremityLevelingNode = null;
		}
		if (!this._incrementalLayout) {
			this.HandleGroupSpreadConstraints();
		}
		this.HandleNorthSouthExtremityConstraintsPass2();
		this.HandleRelativeLevelConstraints();
		this.HandleLinks();
		if (this._incrementalLayout) {
			this.HandleIncrementalLevelingOrder(incrLevels);
		}
		ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.Algorithm algorithm = new ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.Algorithm(
				this.GetLevelingGraph(), graph.GetLayout(),
				super.GetPercController());
		algorithm.Run();
		this.GetLevelingGraph().UpdatePropagationFlags();
		this.HandleSpecLevelIndex();
		this.HandleLevelRangeConstraints();
		algorithm.Clean();
		ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling.Algorithm algorithm2 = new ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling.Algorithm(
				this.GetLevelingGraph(), graph.GetLayout(),
				super.GetPercController());
		algorithm2.Run();
		algorithm2.Clean();
		this.TransferResult();
		this.CheckBypassLevel();
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void SetMinMaxLevel(HNode node, Integer minLevel, Integer maxLevel) {
		if (!this._incrementalLayout || node.IsMarkedForIncremental()) {
			HLVNode levelingNode = this.GetLevelingNode(node);
			if (minLevel >= 0) {
				levelingNode.SetMinLevelNumber(minLevel);
			}
			if (maxLevel >= 0) {
				levelingNode.SetMaxLevelNumber(maxLevel);
			}
		}

	}

	private void TransferResult() {
		HNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			Integer levelNumber = this.GetLevelingNode(node).GetLevelNumber();
			node.SetLevelNumber(levelNumber);
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void UnionLevelNodeIndex(HNode node1, HNode node2) {
		Integer index = this.FindLevelingNodeIndex(node1);
		Integer num2 = this.FindLevelingNodeIndex(node2);
		if (index < num2) {
			this._sameLevelFlags[num2] = index;
		} else if (index > num2) {
			this._sameLevelFlags[index] = num2;
		}

	}

	public final class IncrementalLevel {
		public CalcLevelingAlgorithm __outerThis;

		private Integer _dir;

		private Integer _numberOfNodes;

		private HNode _referenceNode;

		private double _sumCenterCoords;

		private double _sumMaxCoords;

		private double _sumMinCoords;

		public IncrementalLevel(CalcLevelingAlgorithm input__outerThis,
				HNode node) {
			this.__outerThis = input__outerThis;

			this._dir = node.GetOwnerGraph().GetEdgeFlow();
			float oldSizeInEdgeFlow = node.GetOldSizeInEdgeFlow();
			float num2 = node.GetCenter(this._dir) - (0.5f * oldSizeInEdgeFlow);
			this._sumMinCoords = num2;
			num2 += oldSizeInEdgeFlow;
			this._sumMaxCoords = num2;

			this._sumCenterCoords = node.GetCenter(this._dir);
			this._numberOfNodes = 1;
			this._referenceNode = node;
		}

		public void AddNode(HNode node) {
			float oldSizeInEdgeFlow = node.GetOldSizeInEdgeFlow();
			float num2 = node.GetCenter(this._dir) - (0.5f * oldSizeInEdgeFlow);
			this._sumMinCoords += num2;
			num2 += oldSizeInEdgeFlow;
			this._sumMaxCoords += num2;
			this._sumCenterCoords += node.GetCenter(this._dir);
			this._numberOfNodes++;
			this.__outerThis.UnionLevelNodeIndex(node, this._referenceNode);

		}

		public float GetReferenceCoord() {

			return (float) (this._sumCenterCoords / ((double) this._numberOfNodes));

		}

		public HNode GetReferenceNode() {

			return this._referenceNode;

		}

		public Boolean MayContain(HNode node, float levelRange) {
			float oldSizeInEdgeFlow = node.GetOldSizeInEdgeFlow();
			float num2 = node.GetCenter(this._dir) - (0.5f * oldSizeInEdgeFlow);
			float num3 = num2 + oldSizeInEdgeFlow;
			if (((this._sumMinCoords / ((double) this._numberOfNodes)) <= num3)
					&& (num2 <= (this._sumMaxCoords / ((double) this._numberOfNodes)))) {

				return true;
			}
			float num4 = this.GetReferenceCoord() - levelRange;
			float num5 = num4 + (2f * levelRange);

			return ((num4 <= num3) && (num2 <= num5));

		}

	}
}