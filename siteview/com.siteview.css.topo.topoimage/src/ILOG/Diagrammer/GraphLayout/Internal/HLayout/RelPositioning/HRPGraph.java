package ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import system.*;
import system.Collections.*;

public class HRPGraph extends HMAGraph {
	public HRPGraph() {
	}

	public void AddEdge(float priority, ArrayList source, ArrayList target) {
		if ((source.get_Count() != 0) && (target.get_Count() != 0)) {
			HRPNode node3 = null;
			HRPNode node4 = null;
			HRPNode node5 = null;
			HMAEdge edge = null;
			HRPNode node = (HRPNode) source.get_Item(0);
			HRPNode parent = node.GetParent();
			if (source.get_Count() == 1) {
				node3 = node;
			} else {
				node3 = new HRPNode(0);
				node3.SetParent(parent);
				this.AddNode(node3);
				IJavaStyleEnumerator enumerator = TranslateUtil
						.Collection2JavaStyleEnum(source);

				while (enumerator.HasMoreElements()) {
					node5 = (HRPNode) enumerator.NextElement();
					edge = new HMAEdge(0f);
					edge.MakeUnbreakable();
					this.AddEdge(edge, node5, node3);
				}
			}
			if (target.get_Count() == 1) {
				node4 = (HRPNode) target.get_Item(0);
			} else {
				node4 = new HRPNode(0);
				node4.SetParent(parent);
				this.AddNode(node4);
				IJavaStyleEnumerator enumerator2 = TranslateUtil
						.Collection2JavaStyleEnum(target);

				while (enumerator2.HasMoreElements()) {
					node5 = (HRPNode) enumerator2.NextElement();
					edge = new HMAEdge(0f);
					edge.MakeUnbreakable();
					this.AddEdge(edge, node4, node5);
				}
			}
			this.AddEdge(new HMAEdge(priority), node3, node4);
		}

	}

	public void AddEdge(float priority, Boolean unbreakable, HRPNode source,
			HRPNode target) {
		if (source != target) {
			if (source.GetParent() == target.GetParent()) {
				if (source.IsNeighbored()) {

					if (!source.HasOrderedNeighbors()) {
						HRPNode leftmostOrExtremeNeighbor = source
								.GetLeftmostOrExtremeNeighbor();
						HRPNode neighbor = leftmostOrExtremeNeighbor;
						HRPNode node = null;
						while ((neighbor != source) && (neighbor != target)) {
							Integer oppositeNeighborIndex = neighbor
									.GetOppositeNeighborIndex(node);
							node = neighbor;

							neighbor = neighbor
									.GetNeighbor(oppositeNeighborIndex);
						}
						if (neighbor == source) {
							leftmostOrExtremeNeighbor
									.SetRightNeighbor(leftmostOrExtremeNeighbor
											.GetOppositeNeighbor(null));

							return;
						}
						leftmostOrExtremeNeighbor
								.SetLeftNeighbor(leftmostOrExtremeNeighbor
										.GetOppositeNeighbor(null));

						return;
					}
				} else {
					HMAEdge edge = new HMAEdge(priority);
					if (unbreakable) {
						edge.MakeUnbreakable();
					}
					this.AddEdge(edge, source, target);
				}
				return;
			}
			source.Mark();
			HRPNode markedAncestor = target.GetMarkedAncestor();
			source.Unmark();
			if (source == markedAncestor) {

				return;
			}
			if (target != markedAncestor) {
				while (source.GetParent() != markedAncestor) {

					source = source.GetParent();
				}
				while (target.GetParent() != markedAncestor) {

					target = target.GetParent();
				}
				if (source.IsNeighbored()) {

					if (!source.HasOrderedNeighbors()) {
						HRPNode leftmostOrExtremeNeighbor = source
								.GetLeftmostOrExtremeNeighbor();
						HRPNode neighbor = leftmostOrExtremeNeighbor;
						HRPNode node = null;
						while ((neighbor != source) && (neighbor != target)) {
							Integer oppositeNeighborIndex = neighbor
									.GetOppositeNeighborIndex(node);
							node = neighbor;

							neighbor = neighbor
									.GetNeighbor(oppositeNeighborIndex);
						}
						if (neighbor == source) {
							leftmostOrExtremeNeighbor
									.SetRightNeighbor(leftmostOrExtremeNeighbor
											.GetOppositeNeighbor(null));

							return;
						}
						leftmostOrExtremeNeighbor
								.SetLeftNeighbor(leftmostOrExtremeNeighbor
										.GetOppositeNeighbor(null));

						return;
					}
				} else {
					HMAEdge edge = new HMAEdge(priority);
					if (unbreakable) {
						edge.MakeUnbreakable();
					}
					this.AddEdge(edge, source, target);
				}
				return;
			}
		}

		return;

	}

	public void AssignPositions(Integer position, HRPNode leftmostCorner) {
		HRPNodeIterator neighborChain = leftmostCorner.GetNeighborChain();

		while (neighborChain.HasNext()) {
			neighborChain.Next().SetPositionNumber(position++);
		}

	}

	public Boolean CanNeighbor(HRPNode node1, HRPNode node2, Boolean node1IsLeft) {
		if (node1IsLeft) {
			HRPNode rightNeighbor = node1.GetRightNeighbor();
			HRPNode leftNeighbor = node2.GetLeftNeighbor();
			if (rightNeighbor == node2) {

				return true;
			}
			if (node1.GetLeftNeighbor() == node2) {

				return false;
			}
			if (node1.HasOrderedNeighbors() && (rightNeighbor != null)) {

				return false;
			}
			if (node2.HasOrderedNeighbors() && (leftNeighbor != null)) {

				return false;
			}
		}
		if (node1.GetNeighbor(0) == node2) {

			return true;
		}
		if (node1.GetNeighbor(1) == node2) {

			return true;
		}
		if ((node1.GetNeighbor(0) != null) && (node1.GetNeighbor(1) != null)) {

			return false;
		}
		if ((node2.GetNeighbor(0) != null) && (node2.GetNeighbor(1) != null)) {

			return false;
		}

		return !node1.IsThisOrChainedNeighbor(node2);

	}

	public HMAEdgeIterator GetEdges(Boolean backwards) {

		return new HRPGraphEdgeIterator(this, backwards);

	}

	public HRPNodeIterator GetNodes(Boolean backwards) {

		return new HRPGraphNodeIterator(this, backwards);

	}

	public void Init(HRPNode westNode, HRPNode eastNode) {
		Integer numberOfNodes = super.GetNumberOfNodes();
		HRPNode node = new HRPNode(numberOfNodes);
		HRPNode node2 = node;
		this.AddNode(node);
		if ((eastNode != null) || (westNode != null)) {
			if (eastNode != null) {
				numberOfNodes--;
			}
			if (westNode != null) {
				numberOfNodes--;
			}
			node2 = new HRPNode(numberOfNodes);
			this.AddNode(node2);
			node2.SetParent(node);
		}
		HRPNodeIterator nodes = this.GetNodes(false);

		while (nodes.HasNext()) {
			HRPNode node3 = nodes.Next();
			if (((node3 != node) && (node3 != node2))
					&& ((node3 != eastNode) && (node3 != westNode))) {
				node3.SetParent(node2);
			}
		}
		if (westNode != null) {
			westNode.SetParent(node);
			this.Neighbor(westNode, node2, true);
		}
		if (eastNode != null) {
			eastNode.SetParent(node);
			this.Neighbor(node2, eastNode, true);
		}

	}

	public void Neighbor(HRPNode node1, HRPNode node2, Boolean node1IsLeft) {
		node1.SetNeighbor(node2);
		if (node1IsLeft) {
			node2.SetLeftNeighbor(node1);
		} else {
			node2.SetNeighbor(node1);
		}

	}

}