package ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Collections.*;

public final class HRPTransform {
	public HRPGraph _graph;

	public Integer _numMarkedLeaves;

	private Integer BLACK = 1;

	private Integer BLACKWHITE = 3;

	private Integer GRAY = 2;

	private Integer WHITE = 0;

	private Integer WHITEBLACK = 4;

	public HRPTransform(HRPGraph graph) {
		this.Init(graph);
	}

	private void CreateBlackSplitNode(HRPNode node) {
		HRPNode node2 = node;
		if (node.GetNumberOfBlackChildren() > 0) {
			if (this.IsGroupComplete(node)
					|| ((node.GetNumberOfGraytoneChildren() == 1) && (node
							.GetNumberOfWhiteChildren() > 0))) {

				node2 = this.NewNode();
				node2.SetColor(1);
				node2.AllocColorExtension();
				node2.SetParent(node);
			} else if (node.GetNumberOfGraytoneChildren() == 0) {

				if (!node.IsNeighbored()) {
					HRPNode newParent = this.NewNode();
					newParent.SetColor(node.GetColor());
					newParent.AllocColorExtension();
					newParent.UpdateMarkedInfoOnAdd(node);
					newParent.SetParent(node.GetParent());
					node.SetParent(newParent);

					node2 = this.NewNode();
					node2.AllocColorExtension();
					node2.SetColor(1);
					node2.SetParent(newParent);
				} else {

					node2 = this.NewNode();
					node2.AllocColorExtension();
					node2.SetColor(1);
					node2.SetParent(node.GetParent());
				}
				Integer indexOfBlackSideNeighbor = node
						.GetIndexOfBlackSideNeighbor();
				HRPNode neighbor = node.GetNeighbor(indexOfBlackSideNeighbor);
				node.SetNeighbor(indexOfBlackSideNeighbor, node2);
				node2.SetNeighbor(1 - indexOfBlackSideNeighbor, node);
				node2.CopyOrdering(node);
				if (neighbor != null) {
					node2.SetNeighbor(indexOfBlackSideNeighbor, neighbor);
					neighbor.ReplaceNeighbor(node, node2);
				}
			}
		}
		node.SetBlackSplitNode(node2);

	}

	private HRPNode GetOrCreateBlackSplitNode(HRPNode node) {
		HRPNode blackSplitNode = node.GetBlackSplitNode();
		if (blackSplitNode != null) {

			return blackSplitNode;
		}
		this.CreateBlackSplitNode(node);

		return node.GetBlackSplitNode();

	}

	public void Init(HRPGraph graph) {
		this._graph = graph;
		this._numMarkedLeaves = 0;

	}

	private Boolean IsGroupComplete(HRPNode node) {

		return (node.GetNumberOfMarkedLeaves() == this._numMarkedLeaves);

	}

	private HRPNode NewNode() {
		HRPNode node = new HRPNode(0);
		this._graph.AddNode(node);

		return node;

	}

	private void RemoveNode(HRPNode node) {
		this._graph.RemoveNode(node);
		node.Dispose();

	}

	private void ReplaceNode(HRPNode source, HRPNode target) {
		HRPNode neighbor = source.GetNeighbor(0);
		HRPNode node = source.GetNeighbor(1);
		target.SetNeighbor(0, neighbor);
		target.SetNeighbor(1, node);
		target.SetParent(source.GetParent());
		target.CopyOrdering(source);
		if (neighbor != null) {
			neighbor.ReplaceNeighbor(source, target);
		}
		if (node != null) {
			node.ReplaceNeighbor(source, target);
		}

	}

	public void Run(ArrayList nodes) {
		if (nodes.get_Count() > 1) {
			this._numMarkedLeaves = nodes.get_Count();
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(nodes);

			while (enumerator.HasMoreElements()) {
				HRPNode node = (HRPNode) enumerator.NextElement();
				this.Transform(node);
				this.Trace("\n");
			}

			enumerator = TranslateUtil.Collection2JavaStyleEnum(nodes);

			while (enumerator.HasMoreElements()) {
				((HRPNode) enumerator.NextElement()).UnmarkForColoring();
			}
		}

	}

	private void SetParentOfChain(HRPNode chainNode, HRPNode parent) {
		HRPNodeIterator neighborChain = chainNode.GetNeighborChain();

		while (neighborChain.HasNext()) {
			neighborChain.Next().SetParent(parent);
		}

	}

	private void Trace(String msg) {

	}

	private void Transform(HRPNode node) {
		HRPNode parent = node.GetParent();

		if (!this.IsGroupComplete(node) && (parent != null)) {
			if (parent.GetColor() == 1) {
				this.Transform(parent);
			} else if (node.IsNeighbored()) {
				HRPNode node3 = parent.GetParent();

				if (((parent.GetNumberOfGraytoneChildren() == 0) && !this
						.IsGroupComplete(parent)) && (node3 != null)) {
					HRPNode extremeNeighbor = node.GetExtremeNeighbor(0);
					HRPNode othercorner = node.GetExtremeNeighbor(1);
					if (extremeNeighbor.GetColor() != 1) {
						HRPNode node6 = extremeNeighbor;
						extremeNeighbor = othercorner;
						othercorner = node6;
					}
					if (node3.GetNumberOfChildren() == 1) {
						this.TransformSingleChild(parent);
					}

					if (parent.IsNeighbored()) {
						this.TransformSubchain(parent, extremeNeighbor,
								othercorner);
					} else {
						this.TransformFreeGrayChain(parent, extremeNeighbor,
								othercorner);
					}
				}
			} else {
				if (parent.GetNumberOfChildren() == 1) {
					this.TransformSingleChild(node);
				}
				this.TransformFreeBlackChild(node);
			}
		}

	}

	private void TransformFreeBlackChild(HRPNode node) {
		HRPNode parent = node.GetParent();
		if (parent != null) {
			HRPNode orCreateBlackSplitNode = this
					.GetOrCreateBlackSplitNode(parent);
			if (orCreateBlackSplitNode != parent) {
				this.Trace("Move Black (T3,T6,T8) ");
				node.SetParent(orCreateBlackSplitNode);
				if (orCreateBlackSplitNode.GetParent() == parent) {
					orCreateBlackSplitNode.UpdateMarkedInfoOnAdd(node);
				} else if (orCreateBlackSplitNode.GetParent() == parent
						.GetParent()) {
					parent.UpdateMarkedInfoOnRemove(node);
					orCreateBlackSplitNode.UpdateMarkedInfoOnAdd(node);
				} else {
					orCreateBlackSplitNode.GetParent().UpdateMarkedInfoOnAdd(
							node);
					orCreateBlackSplitNode.UpdateMarkedInfoOnAdd(node);
				}
				if (parent.GetNumberOfBlackChildren() == 0) {
					if (orCreateBlackSplitNode.GetNumberOfChildren() == 1) {
						this.TransformSingleChild(node);
						if (parent.GetColor() != 0) {
							parent.SetBlackSplitNode(node);
						}
						this.Transform(node);
					} else {
						this.Transform(orCreateBlackSplitNode);
					}
				}
			}
		}

	}

	private void TransformFreeGrayChain(HRPNode node, HRPNode blackcorner,
			HRPNode othercorner) {
		HRPNode parent = node.GetParent();
		if (parent != null) {
			HRPNode orCreateBlackSplitNode = this
					.GetOrCreateBlackSplitNode(parent);
			if (orCreateBlackSplitNode == parent) {
				if (parent.GetNumberOfGraytoneChildren() == 1) {
					HRPNode node4 = null;
					if (parent.GetNumberOfBlackChildren() > 0) {
						node4 = blackcorner;
						this.Trace("T4 ");
					} else {
						node4 = othercorner;
						this.Trace("T5 ");
					}
					parent.UpdateMarkedInfoOnRemove(node);
					node.UpdateMarkedInfoOnAdd(parent);
					this.ReplaceNode(parent, node);
					parent.SetParent(node);
					parent.SetNeighbor(0, node4);
					parent.SetNeighbor(1, null);
					node4.ReplaceNeighbor(null, parent);
					node4.EnsureOrderedNeighborsIfNecessary();

					if (node.IsNeighbored() && !this.IsGroupComplete(node)) {
						if (node4 == blackcorner) {
							this.TransformSubchain(node, parent, othercorner);
						} else {
							this.TransformSubchain(node, blackcorner, parent);
						}
					} else {
						this.Transform(blackcorner);
					}
				} else if (parent.GetGrayChainBlackCorner() == null) {
					parent.SetGrayChainBlackCorner(blackcorner);
				} else {
					this.Trace("T7 ");
					HRPNode grayChainBlackCorner = parent
							.GetGrayChainBlackCorner();
					HRPNode node6 = grayChainBlackCorner.GetParent();
					node6.UpdateMarkedInfoOnAdd(node);
					this.SetParentOfChain(blackcorner, node6);
					grayChainBlackCorner.ReplaceNeighbor(null, blackcorner);
					blackcorner.ReplaceNeighbor(null, grayChainBlackCorner);
					grayChainBlackCorner.EnsureOrderedNeighborsIfNecessary();
					blackcorner.EnsureOrderedNeighborsIfNecessary();
					this.RemoveNode(node);
					if (parent.GetNumberOfWhiteChildren() == 0) {
						this.TransformSingleChild(node);
					}
				}
			} else if (parent.GetNumberOfGraytoneChildren() == 1) {
				this.Trace("T6 ");
				node.UpdateMarkedInfoOnAdd(orCreateBlackSplitNode);
				orCreateBlackSplitNode.SetParent(node);
				orCreateBlackSplitNode.SetNeighbor(0, blackcorner);
				blackcorner.ReplaceNeighbor(null, orCreateBlackSplitNode);
				blackcorner.EnsureOrderedNeighborsIfNecessary();

				if (!this.IsGroupComplete(parent)) {
					this.Trace("T5inT6 ");
					parent.UpdateMarkedInfoOnRemove(node);
					node.UpdateMarkedInfoOnAdd(parent);
					this.ReplaceNode(parent, node);
					parent.SetParent(node);
					parent.SetNeighbor(0, othercorner);
					parent.SetNeighbor(1, null);
					othercorner.ReplaceNeighbor(null, parent);
					othercorner.EnsureOrderedNeighborsIfNecessary();

					if (node.IsNeighbored()) {
						this.TransformSubchain(node, orCreateBlackSplitNode,
								parent);
					} else {
						this.Transform(orCreateBlackSplitNode);
					}
				}
			} else if (parent.GetGrayChainBlackCorner() == null) {
				parent.SetGrayChainBlackCorner(blackcorner);
			} else {
				this.Trace("T8 ");
				HRPNode node7 = parent.GetGrayChainBlackCorner();
				HRPNode node8 = node7.GetParent();
				node8.UpdateMarkedInfoOnAdd(node);
				node8.UpdateMarkedInfoOnAdd(orCreateBlackSplitNode);
				this.SetParentOfChain(blackcorner, node8);
				orCreateBlackSplitNode.SetParent(node8);
				orCreateBlackSplitNode.SetNeighbor(0, blackcorner);
				orCreateBlackSplitNode.SetNeighbor(1, node7);
				node7.ReplaceNeighbor(null, orCreateBlackSplitNode);
				blackcorner.ReplaceNeighbor(null, orCreateBlackSplitNode);
				node7.EnsureOrderedNeighborsIfNecessary();
				blackcorner.EnsureOrderedNeighborsIfNecessary();
				this.RemoveNode(node);
				if (parent.GetNumberOfWhiteChildren() == 0) {
					this.TransformSingleChild(node);
				}
			}
		}

	}

	private void TransformSingleChild(HRPNode node) {
		HRPNode parent = node.GetParent();
		if (parent != null) {
			this.Trace("T1 ");
			this.ReplaceNode(parent, node);
			this.RemoveNode(parent);
			if ((node.GetParent() != null)
					&& (node.GetParent().GetNumberOfChildren() == 1)) {
				this.TransformSingleChild(node);
			}
		}

	}

	private void TransformSubchain(HRPNode node, HRPNode blackcorner,
			HRPNode othercorner) {
		this.Trace("T2 ");
		HRPNode leftNeighbor = node.GetLeftNeighbor();
		HRPNode rightNeighbor = node.GetRightNeighbor();
		this.SetParentOfChain(blackcorner, node.GetParent());
		Integer indexOfBlackSideNeighbor = node.GetIndexOfBlackSideNeighbor();
		HRPNode neighbor = node.GetNeighbor(indexOfBlackSideNeighbor);
		HRPNode newNeighbor = node.GetNeighbor(1 - indexOfBlackSideNeighbor);
		if (neighbor != null) {
			blackcorner.ReplaceNeighbor(null, neighbor);
			neighbor.ReplaceNeighbor(node, blackcorner);
		}
		if (newNeighbor != null) {
			othercorner.ReplaceNeighbor(null, newNeighbor);
			newNeighbor.ReplaceNeighbor(node, othercorner);
		}
		this.RemoveNode(node);
		if (leftNeighbor != null) {
			leftNeighbor.EnsureOrderedNeighborsIfNecessary();
		} else if (rightNeighbor != null) {
			rightNeighbor.EnsureOrderedNeighborsIfNecessary();
		}
		blackcorner.EnsureOrderedNeighborsIfNecessary();
		othercorner.EnsureOrderedNeighborsIfNecessary();
		this.Transform(blackcorner);

	}

}