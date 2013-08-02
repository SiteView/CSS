package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning.*;
import system.*;
import system.Collections.*;

public final class BuildRelPosNetsAlgorithm extends HGraphAlgorithm {
	private HRPColoring _coloringAlg = new HRPColoring(null);

	private ConstraintManager _constraintManager;

	private Boolean _handleDummyNodesIncremental = true;

	private Boolean _incrementalLayout = false;

	private HRPOrdering _orderingAlg = new HRPOrdering(null);

	private HNodeSort _sortAlg = new HNodeSort();

	private HRPTransform _transformAlg = new HRPTransform(null);

	public BuildRelPosNetsAlgorithm(HGraph graph,
			ConstraintManager constraintManager) {
		this.Init(graph);
		graph.GetNumberOfNodes();
		this._constraintManager = constraintManager;

		this._incrementalLayout = graph.IsIncrementalMode()
				&& !graph.IsCrossingReductionDuringIncremental();

		this._handleDummyNodesIncremental = this._incrementalLayout
				&& !graph.IsLongLinkCrossingReductionDuringIncremental();
	}

	@Override
	public void Clean() {
		super.Clean();
		this._constraintManager = null;

	}

	private HierarchicalConstraint[] CollectConstraints(Type constrclass) {
		HierarchicalConstraint constraint = null;
		Integer num = 0;
		IJavaStyleEnumerator constraints = this._constraintManager
				.GetConstraints();

		while (constraints.HasMoreElements()) {
			constraint = (HierarchicalConstraint) constraints.NextElement();
			if (constraint.IsValidForLayout()
					&& constrclass.IsInstanceOfType(constraint)) {
				num++;
			}
		}
		HierarchicalConstraint[] a = new HierarchicalConstraint[num];
		num = 0;

		constraints = this._constraintManager.GetConstraints();

		while (constraints.HasMoreElements()) {
			constraint = (HierarchicalConstraint) constraints.NextElement();
			if (constraint.IsValidForLayout()
					&& constrclass.IsInstanceOfType(constraint)) {
				a[num++] = constraint;
			}
		}
		new HConstraintSort().Sort(a);

		return a;

	}

	private ArrayList[] GetDistributionByLevel(ArrayList nodes) {
		HNode node = null;
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(nodes);

		while (enumerator.HasMoreElements()) {
			node = (HNode) enumerator.NextElement();
			node.SetMarker(0);
			node.GetLevel().SetReferenceIndex(-1);
		}
		Integer num = 0;

		enumerator = TranslateUtil.Collection2JavaStyleEnum(nodes);

		while (enumerator.HasMoreElements()) {
			node = (HNode) enumerator.NextElement();
			HLevel level = node.GetLevel();
			if (level.GetReferenceIndex() == -1) {
				level.SetReferenceIndex(num++);
			}
		}
		ArrayList[] listArray = new ArrayList[num];
		for (Integer i = 0; i < num; i++) {
			listArray[i] = new ArrayList(2);
		}

		enumerator = TranslateUtil.Collection2JavaStyleEnum(nodes);

		while (enumerator.HasMoreElements()) {
			node = (HNode) enumerator.NextElement();
			if (node.GetMarker() == 0) {
				listArray[node.GetLevel().GetReferenceIndex()].Add(node);
				node.SetMarker(1);
			}
		}

		return listArray;

	}

	private ArrayList[] GetDistributionByLevel(java.lang.Object nodeOrGroup) {
		if (nodeOrGroup instanceof HierarchicalNodeGroup) {

			return this.GetDistributionByLevel(this
					.GetHNodeVector((HierarchicalNodeGroup) nodeOrGroup));
		}
		HNode node = super.GetGraph().GetNode(nodeOrGroup);

		if (this.IsIncremental(node)) {

			return new ArrayList[0];
		}
		ArrayList[] listArray = new ArrayList[] { new ArrayList(1) };
		listArray[0].Add(node);

		return listArray;

	}

	private ArrayList GetHNodeVector(HierarchicalNodeGroup g) {
		HGraph graph = super.GetGraph();
		ArrayList list = new ArrayList(g.get_Count());
		IJavaStyleEnumerator enumerator = new JavaStyleEnumerator(
				g.GetEnumerator());

		while (enumerator.HasMoreElements()) {
			HNode node = graph.GetNode(enumerator.NextElement());

			if (!this.IsIncremental(node)) {
				list.Add(node);
			}
		}

		return list;

	}

	private ArrayList GetHRPNodeVector(ArrayList nodeVector) {
		HGraph graph = super.GetGraph();
		ArrayList list = new ArrayList(nodeVector.get_Count());
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(nodeVector);

		while (enumerator.HasMoreElements()) {
			list.Add(graph.GetConstraintNode((HNode) enumerator.NextElement()));
		}

		return list;

	}

	private void HandleEastWestExtremityConstraints() {
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
				if (constraint2.get_Side() == HierarchicalLayoutSide.East) {

					node = graph.GetNode(constraint2.GetNode());

					if (!this.IsIncremental(node)) {
						node.GetLevel().SetEastExtremeNode(node);
					}
				} else if (constraint2.get_Side() == HierarchicalLayoutSide.West) {

					node = graph.GetNode(constraint2.GetNode());

					if (!this.IsIncremental(node)) {
						node.GetLevel().SetWestExtremeNode(node);
					}
				}
			}
		}
		HLevelIterator levels = graph.GetLevels();

		while (levels.HasNext()) {
			HLevel level = levels.Next();
			if ((level.GetEastExtremeNode() != null)
					|| (level.GetWestExtremeNode() != null)) {
				level.GetOrAllocateConstraintNetwork();
			}
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void HandleIncremental() {
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			this.HandleIncremental(levels.Next());
		}

	}

	private void HandleIncremental(HLevel level) {
		HGraph graph = super.GetGraph();
		HNodeIterator nodes = level.GetNodes();
		Integer index = 0;

		while (nodes.HasNext()) {

			if (this.IsIncremental(nodes.Next())) {
				index++;
			}
		}
		if (index != 0) {
			HNode[] a = new HNode[index];

			nodes = level.GetNodes();
			index = 0;

			while (nodes.HasNext()) {
				HNode node = nodes.Next();

				if (this.IsIncremental(node)) {
					a[index++] = node;
				}
			}
			float num2 = 1f;
			Integer levelFlow = graph.GetLevelFlow();
			index = 0;
			while (index < a.length) {
				a[index].SetSortValue(num2 * a[index].GetCenter(levelFlow));
				index++;
			}
			this._sortAlg.Sort(a);
			HRPGraph orAllocateConstraintNetwork = level
					.GetOrAllocateConstraintNetwork();
			for (index = 0; index < (a.length - 1); index++) {
				orAllocateConstraintNetwork.AddEdge(10000f, true,
						graph.GetConstraintNode(a[index]),
						graph.GetConstraintNode(a[index + 1]));
			}
		}

	}

	private void HandleRelativePositionConstraint(
			HierarchicalRelativePositionConstraint constraint) {
		ArrayList[] distributionByLevel = this
				.GetDistributionByLevel(constraint.GetLowerSubject());
		ArrayList[] listArray2 = this.GetDistributionByLevel(constraint
				.GetHigherSubject());
		float priority = constraint.get_Priority();
		for (Integer i = 0; i < distributionByLevel.length; i++) {
			if (distributionByLevel[i].get_Count() > 0) {
				HLevel level = ((HNode) distributionByLevel[i].get_Item(0))
						.GetLevel();
				for (Integer j = 0; j < listArray2.length; j++) {
					if ((listArray2[j].get_Count() > 0)
							&& (((HNode) listArray2[j].get_Item(0)).GetLevel() == level)) {
						this.HandleRelativePositionConstraint(level,
								distributionByLevel[i], listArray2[j], priority);
					}
				}
			}
		}

	}

	private void HandleRelativePositionConstraint(HLevel level,
			ArrayList lowerNodes, ArrayList higherNodes, float priority) {
		HRPGraph orAllocateConstraintNetwork = level
				.GetOrAllocateConstraintNetwork();
		ArrayList hRPNodeVector = this.GetHRPNodeVector(lowerNodes);
		ArrayList target = this.GetHRPNodeVector(higherNodes);
		this._orderingAlg.Init(orAllocateConstraintNetwork);
		this._orderingAlg.Run(priority, hRPNodeVector, target);

	}

	private void HandleRelativePositionConstraints() {
		HierarchicalConstraint[] constraintArray = this
				.CollectConstraints(Type
						.GetType(HierarchicalRelativePositionConstraint.class
								.getName()));
		for (Integer i = 0; i < constraintArray.length; i++) {
			this.HandleRelativePositionConstraint((HierarchicalRelativePositionConstraint) constraintArray[i]);
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void HandleSideBySideConstraint(
			HierarchicalSideBySideConstraint constraint) {
		ArrayList hNodeVector = this.GetHNodeVector(constraint.GetGroup());
		ArrayList[] distributionByLevel = this
				.GetDistributionByLevel(hNodeVector);
		for (Integer i = 0; i < distributionByLevel.length; i++) {
			if (distributionByLevel[i].get_Count() > 1) {
				HNode node = (HNode) distributionByLevel[i].get_Item(0);
				HRPGraph orAllocateConstraintNetwork = node.GetLevel()
						.GetOrAllocateConstraintNetwork();
				ArrayList hRPNodeVector = this
						.GetHRPNodeVector(distributionByLevel[i]);
				this._coloringAlg.Init(orAllocateConstraintNetwork);

				if (this._coloringAlg.Run(hRPNodeVector)) {
					this._transformAlg.Init(orAllocateConstraintNetwork);
					this._transformAlg.Run(hRPNodeVector);
				}
			}
		}

	}

	private void HandleSideBySideConstraints() {
		HierarchicalConstraint[] constraintArray = this.CollectConstraints(Type
				.GetType(HierarchicalSideBySideConstraint.class.getName()));
		for (Integer i = 0; i < constraintArray.length; i++) {
			this.HandleSideBySideConstraint((HierarchicalSideBySideConstraint) constraintArray[i]);
		}
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private Boolean IsIncremental(HNode node) {
		if (!this._incrementalLayout) {

			return false;
		}

		if (node.IsMarkedForIncremental()) {

			return false;
		}
		if (node.IsDummyNode() && !this._handleDummyNodesIncremental) {

			return false;
		}

		return true;

	}

	private void MakeAcyclic(HRPGraph graph) {
		Algorithm algorithm = new Algorithm(graph,
				super.GetGraph().GetLayout(), super.GetPercController());
		algorithm.Run();
		algorithm.Clean();

	}

	@Override
	public void Run() {
		this.HandleEastWestExtremityConstraints();
		this.HandleSideBySideConstraints();
		this.HandleRelativePositionConstraints();
		if (this._incrementalLayout) {
			this.HandleIncremental();
		}
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			HRPGraph constraintNetwork = levels.Next().GetConstraintNetwork();
			if (constraintNetwork != null) {
				this.MakeAcyclic(constraintNetwork);
			}
		}

	}

}