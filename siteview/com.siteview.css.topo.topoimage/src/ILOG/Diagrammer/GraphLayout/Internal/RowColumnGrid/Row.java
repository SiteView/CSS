package ILOG.Diagrammer.GraphLayout.Internal.RowColumnGrid;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;
import system.Collections.*;

public final class Row {
	private float _maxHeight;

	private ArrayList _nodes = new ArrayList(50);

	private float _totalWidth;

	private Integer MAX_STEPS = 40;

	public Row() {
	}

	public void AddNode(GraphOnRowsOrColumns graph, java.lang.Object node,
			InternalRect nodeBox, float hOffset) {
		this._nodes.Add(node);
		float height = graph.GetHeight(nodeBox);
		if (height > this._maxHeight) {
			this._maxHeight = height;
		}
		this._totalWidth += graph.GetWidth(nodeBox);
		if (this.Size() > 1) {
			this._totalWidth += hOffset;
		}

	}

	public void Clear() {
		this._nodes.Clear();
		this._totalWidth = 0f;
		this._maxHeight = 0f;

	}

	public java.lang.Object ElementAt(Integer i) {
		if (this._nodes == null) {

			return null;
		}

		return this._nodes.get_Item(i);

	}

	public Integer FillWithNodes(ArrayList floatingNodes,
			GraphOnRowsOrColumns graph, Integer startIndex, float hOffset,
			float availableWidthOnRow, Boolean redraw) {
		Integer count = floatingNodes.get_Count();
		Integer indexNode = startIndex;
		Boolean flag = graph.GetNodeComparator() == Graph.NO_ORDERING;
		Integer maxNumberOfNodesPerRow = (int) Math.Max(1,
				graph.GetMaxNumberOfNodesPerRowOrColumn());
		while (((indexNode < count) && (this.Size() < maxNumberOfNodesPerRow))
				&& (this.GetTotalWidth() < availableWidthOnRow)) {
			java.lang.Object node = flag ? this.GetNextNode1(indexNode, graph,
					floatingNodes, hOffset, availableWidthOnRow,
					maxNumberOfNodesPerRow) : this.GetNextNode2(indexNode,
					graph, floatingNodes, hOffset, availableWidthOnRow,
					maxNumberOfNodesPerRow);
			if (node == null) {

				return indexNode;
			}
			this.AddNode(graph, node, graph.GetNodeBox(node), hOffset);
			indexNode++;
		}

		return indexNode;

	}

	public float GetMaxHeight() {

		return this._maxHeight;

	}

	private java.lang.Object GetNextNode1(Integer indexNode,
			GraphOnRowsOrColumns graph, ArrayList floatingNodes, float hOffset,
			float availableWidthOnRow, Integer maxNumberOfNodesPerRow) {
		java.lang.Object node = floatingNodes.get_Item(indexNode);
		if ((this.Size() < maxNumberOfNodesPerRow)
				&& (((this.GetTotalWidth() + graph.GetNodeWidth(node)) + ((this
						.Size() > 0) ? hOffset : 0f)) <= availableWidthOnRow)) {

			return node;
		}
		if (this.Size() <= 0) {

			return node;
		}

		return null;

	}

	private java.lang.Object GetNextNode2(Integer indexNode,
			GraphOnRowsOrColumns graph, ArrayList floatingNodes, float hOffset,
			float availableWidthOnRow, Integer maxNumberOfNodesPerRow) {
		Integer num = (int) Math.Min(floatingNodes.get_Count(), indexNode + 40);
		float totalWidth = this.GetTotalWidth();
		for (Integer i = indexNode; i < num; i++) {
			java.lang.Object node = floatingNodes.get_Item(i);
			if ((this.Size() < maxNumberOfNodesPerRow)
					&& (((totalWidth + graph.GetNodeWidth(node)) + ((this
							.Size() > 0) ? hOffset : 0f)) <= availableWidthOnRow)) {
				if (i != indexNode) {
					this.SwapNodes(floatingNodes, i, indexNode);
				}

				return floatingNodes.get_Item(indexNode);
			}
		}
		if (this.Size() <= 0) {

			return floatingNodes.get_Item(indexNode);
		}

		return null;

	}

	public float GetTotalWidth() {

		return this._totalWidth;

	}

	public void Layout(GraphOnRowsOrColumns graph, float startX, float yRow,
			float hOffset, Integer globalVertAlignment,
			Boolean isReversedOrder, Boolean redraw) {
		Integer num = this.Size();
		if (num >= 1) {
			java.lang.Object obj2 = null;
			InternalRect nodeBox = null;
			float x = startX;
			if (isReversedOrder) {
				for (Integer i = 0; i < num; i++) {
					obj2 = this._nodes.get_Item(i);

					nodeBox = graph.GetNodeBox(obj2);

					x -= graph.GetWidth(nodeBox);
					this.MoveNode(graph, obj2, nodeBox, x, yRow, redraw);
					x -= hOffset;
				}
			} else {
				for (Integer j = 0; j < num; j++) {
					obj2 = this._nodes.get_Item(j);

					nodeBox = graph.GetNodeBox(obj2);
					this.MoveNode(graph, obj2, nodeBox, x, yRow, redraw);
					x += graph.GetWidth(nodeBox) + hOffset;
				}
			}
		}

	}

	private void MoveNode(GraphOnRowsOrColumns graph, java.lang.Object node,
			InternalRect nodeBox, float x, float yRow, Boolean redraw) {
		float y = x;
		float num2 = yRow;

		if (!graph.IsInvertedCoords()) {
			if (graph.GetVerticalAlignment(node) == 0) {
				num2 += (this._maxHeight - graph.GetHeight(nodeBox)) * 0.5f;
				graph.MoveNode(node, y, num2, redraw);
				return;
			} else if (graph.GetVerticalAlignment(node) == 1
					|| graph.GetVerticalAlignment(node) == 2
					|| graph.GetVerticalAlignment(node) == 3) {
				graph.MoveNode(node, y, num2, redraw);
				return;
			} else if (graph.GetVerticalAlignment(node) == 4) {
				num2 += this._maxHeight - graph.GetHeight(nodeBox);
				graph.MoveNode(node, y, num2, redraw);
				return;
			}
		} else {
			if (graph.GetHorizontalAlignment(node) == 0) {
				num2 += (this._maxHeight - graph.GetHeight(nodeBox)) * 0.5f;
				// NOTICE: break ignore!!!
			} else if (graph.GetHorizontalAlignment(node) == 2) {
				num2 += this._maxHeight - graph.GetHeight(nodeBox);
				// NOTICE: break ignore!!!
			}
			graph.MoveNode(node, num2, y, redraw);

			return;
		}
		Label_00AE: graph.MoveNode(node, y, num2, redraw);
		return;

	}

	public void RemoveElement(java.lang.Object node) {
		if (this._nodes != null) {
			TranslateUtil.Remove(this._nodes, node);
		}

	}

	public void RemoveElementAt(Integer i) {
		if (this._nodes != null) {
			this._nodes.Remove(i);
		}

	}

	public Integer Size() {
		if (this._nodes == null) {

			return 0;
		}

		return this._nodes.get_Count();

	}

	private void SwapNodes(ArrayList vectNodes, Integer loc1, Integer loc2) {
		java.lang.Object obj2 = vectNodes.get_Item(loc1);
		vectNodes.set_Item(loc1, vectNodes.get_Item(loc2));
		vectNodes.set_Item(loc2, obj2);

	}

}