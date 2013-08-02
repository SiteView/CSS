package ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Collections.*;

public final class HRPOrdering {
	public HRPGraph _graph;

	private Integer BLUE = 1;

	private Integer RED = 3;

	private Integer VIOLET = 2;

	private Integer WHITE = 0;

	public HRPOrdering(HRPGraph graph) {
		this.Init(graph);
	}

	private void CollectRedBlueVioletChildren(IJavaStyleEnumerator e) {

		while (e.HasMoreElements()) {
			HRPNode child = (HRPNode) e.NextElement();
			child.GetParent().UpdateOrderingInfoFromChild(child);
		}

	}

	private void CreateNeighborOrdering(float priority, HRPNode node) {

		if (!node.HasOrderedNeighbors()) {
			HRPNode leftmostOrExtremeNeighbor = node
					.GetLeftmostOrExtremeNeighbor();
			Integer num = 0;
			Integer num2 = 0x7fffffff;
			Integer num3 = -1;
			Integer num4 = 0x7fffffff;
			Integer num5 = -1;
			Integer num6 = 0x7fffffff;
			Integer num7 = -1;
			HRPNodeIterator neighborChain = leftmostOrExtremeNeighbor
					.GetNeighborChain();

			while (neighborChain.HasNext()) {
				if (neighborChain.Next().GetColor() == 1) {
					num3 = num;
					// NOTICE: break ignore!!!
				} else if (neighborChain.Next().GetColor() == 2) {
					num7 = num;
					// NOTICE: break ignore!!!
				} else if (neighborChain.Next().GetColor() == 3) {
					num5 = num;
					// NOTICE: break ignore!!!
				}
				num++;
			}
			num--;

			while (neighborChain.HasPrev()) {
				if (neighborChain.Prev().GetColor() == 1) {
					num2 = num;
					// NOTICE: break ignore!!!
				} else if (neighborChain.Prev().GetColor() == 2) {
					num6 = num;
					// NOTICE: break ignore!!!
				} else if (neighborChain.Prev().GetColor() == 3) {
					num4 = num;
					// NOTICE: break ignore!!!
				}
				num--;
			}
			num = (((num3 > -1) ? 1 : 0) + ((num5 > -1) ? 1 : 0))
					+ ((num7 > -1) ? 1 : 0);
			if (num >= 2) {
				if (((num3 < num6) && (num3 < num4)) && (num7 < num4)) {
					leftmostOrExtremeNeighbor
							.SetRightNeighbor(leftmostOrExtremeNeighbor
									.GetOppositeNeighbor(null));
				} else if (((num5 < num6) && (num5 < num2)) && (num7 < num2)) {
					leftmostOrExtremeNeighbor
							.SetLeftNeighbor(leftmostOrExtremeNeighbor
									.GetOppositeNeighbor(null));
				}
			}
		}

	}

	private void CreateOrdering(IJavaStyleEnumerator e, float priority) {

		while (e.HasMoreElements()) {
			this.CreateOrdering(priority, (HRPNode) e.NextElement());
		}

	}

	private void CreateOrdering(float priority, HRPNode node) {
		while (node != null) {
			if (node.GetColor() == 0) {

				return;
			}
			if (node.GetNumberOfOrderingChildren() == 0) {
				node.UnmarkForOrdering();

				node = node.GetParent();
			} else {
				ArrayList blueChildren = node.GetBlueChildren();
				ArrayList redChildren = node.GetRedChildren();
				ArrayList violetChildren = node.GetVioletChildren();
				HRPNode node2 = null;
				if (blueChildren.get_Count() > 0) {
					node2 = (HRPNode) blueChildren.get_Item(0);
				} else if (redChildren.get_Count() > 0) {
					node2 = (HRPNode) redChildren.get_Item(0);
				} else {
					node2 = (HRPNode) violetChildren.get_Item(0);
				}

				if (node2.IsNeighbored()) {
					this.CreateNeighborOrdering(priority, node2);
				} else if (violetChildren.get_Count() > 0) {
					this._graph.AddEdge(priority, blueChildren, violetChildren);
					this._graph.AddEdge(priority, violetChildren, redChildren);
				} else {
					this._graph.AddEdge(priority, blueChildren, redChildren);
				}
				node.UnmarkForOrdering();

				node = node.GetParent();
			}
		}

	}

	public void Init(HRPGraph graph) {
		this._graph = graph;

	}

	private void MarkForOrdering(IJavaStyleEnumerator e, Integer color) {

		while (e.HasMoreElements()) {
			((HRPNode) e.NextElement()).MarkForOrdering(color, false);
		}

	}

	public void Run(float priority, ArrayList source, ArrayList target) {
		if ((source.get_Count() != 0) && (target.get_Count() != 0)) {
			this.MarkForOrdering(
					TranslateUtil.Collection2JavaStyleEnum(source), 1);
			this.MarkForOrdering(
					TranslateUtil.Collection2JavaStyleEnum(target), 3);
			this.CollectRedBlueVioletChildren(TranslateUtil
					.Collection2JavaStyleEnum(source));
			this.CollectRedBlueVioletChildren(TranslateUtil
					.Collection2JavaStyleEnum(target));
			this.CreateOrdering(TranslateUtil.Collection2JavaStyleEnum(source),
					priority);
			this.CreateOrdering(TranslateUtil.Collection2JavaStyleEnum(target),
					priority);
		}

	}

}