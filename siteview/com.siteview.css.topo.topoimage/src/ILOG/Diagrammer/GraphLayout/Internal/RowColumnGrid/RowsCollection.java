package ILOG.Diagrammer.GraphLayout.Internal.RowColumnGrid;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;
import system.Collections.*;

public final class RowsCollection {
	private ArrayList _vectRows = new ArrayList(50);

	public RowsCollection() {
	}

	public void FillWithNodes(ArrayList floatingNodes,
			GraphOnRowsOrColumns graph, Boolean redraw) {
		Integer num9 = null;
		Row row = null;
		Boolean flag2 = null;
		float hGap = graph.GetHGap();
		float vGap = graph.GetVGap();
		Integer globalVerticalAlignment = graph.GetGlobalVerticalAlignment();
		float availableWidthOnRow = graph.GetAvailableWidthOnRow();
		Boolean flag = graph.IsIncrementalMode();
		float positionX = graph.GetPositionX();
		float startX = positionX + availableWidthOnRow;
		float positionY = graph.GetPositionY();
		Integer startIndex = 0;
		if (flag) {
			Integer num10 = this._vectRows.get_Count();
			Boolean flag3 = false;
			for (Integer i = 0; i < num10; i++) {
				row = (Row) this._vectRows.get_Item(i);

				if (!graph.MayContinue()) {

					return;
				}
				num9 = startIndex;

				startIndex = row.FillWithNodes(floatingNodes, graph,
						startIndex, hGap, availableWidthOnRow, redraw);
				if (num9 != startIndex) {
					flag3 = true;
				}
				if (flag3) {

					flag2 = this.IsReversedOrder(graph, row, i,
							availableWidthOnRow);
					if (flag2) {
						row.Layout(graph, startX, positionY, hGap,
								globalVerticalAlignment, flag2, redraw);
					} else {
						row.Layout(graph, positionX, positionY, hGap,
								globalVerticalAlignment, flag2, redraw);
					}
				}
				if (row.Size() > 0) {
					positionY += row.GetMaxHeight() + vGap;
				} else {
					flag3 = true;
				}
			}
		}
		Integer count = floatingNodes.get_Count();
		Integer rowIndex = this._vectRows.get_Count() - 1;
		while (startIndex < count) {
			row = new Row();

			if (!graph.MayContinue()) {

				return;
			}
			num9 = startIndex;

			startIndex = row.FillWithNodes(floatingNodes, graph, startIndex,
					hGap, availableWidthOnRow, redraw);
			if (num9 != startIndex) {
				rowIndex++;

				flag2 = this.IsReversedOrder(graph, row, rowIndex,
						availableWidthOnRow);
				if (flag2) {
					row.Layout(graph, startX, positionY, hGap,
							globalVerticalAlignment, flag2, redraw);
				} else {
					row.Layout(graph, positionX, positionY, hGap,
							globalVerticalAlignment, flag2, redraw);
				}
				this._vectRows.Add(row);
				positionY += row.GetMaxHeight() + vGap;
			}
		}

	}

	public float GetMaxTotalWidth() {
		float num = 0f;
		Integer rowsCount = this.GetRowsCount();
		for (Integer i = 0; i < rowsCount; i++) {

			num = Math.Max(num,
					((Row) this._vectRows.get_Item(i)).GetTotalWidth());
		}

		return num;

	}

	public Row GetRow(Integer rowIndex) {

		return (Row) this._vectRows.get_Item(rowIndex);

	}

	public Integer GetRowsCount() {
		if (this._vectRows != null) {

			return this._vectRows.get_Count();
		}

		return 0;

	}

	private static Boolean IsOverlappingFixedNodes(GraphOnRowsOrColumns graph,
			java.lang.Object node, ArrayList fixedNodes, Boolean invertedCoords) {
		Integer count = fixedNodes.get_Count();
		InternalRect nodeBox = graph.GetNodeBox(node);
		if (invertedCoords) {
			nodeBox.X -= graph.GetVGap();
			nodeBox.Width += 2f * graph.GetVGap();
			nodeBox.Y -= graph.GetHGap();
			nodeBox.Height += 2f * graph.GetHGap();
		} else {
			nodeBox.X -= graph.GetHGap();
			nodeBox.Width += 2f * graph.GetHGap();
			nodeBox.Y -= graph.GetHGap();
			nodeBox.Height += 2f * graph.GetHGap();
		}
		for (Integer i = 0; i < count; i++) {
			java.lang.Object obj2 = fixedNodes.get_Item(i);
			InternalRect rect2 = graph.GetNodeBox(obj2);
			if (((nodeBox.X <= (rect2.X + rect2.Width)) && (rect2.X <= (nodeBox.X + nodeBox.Width)))
					&& ((nodeBox.Y <= (rect2.Y + rect2.Height)) && (rect2.Y <= (nodeBox.Y + nodeBox.Height)))) {

				return true;
			}
		}

		return false;

	}

	private Boolean IsReversedOrder(GraphOnRowsOrColumns graph, Row row,
			Integer rowIndex, float availableWidthOnRow) {
		Integer nNodesOnRow = row.Size();

		return graph.IsReversedOrder(rowIndex, nNodesOnRow,
				(nNodesOnRow < 1) ? null : graph.GetNodeBox(row.ElementAt(0)),
				availableWidthOnRow);

	}

	public void ProcessFixedNodes(ArrayList fixedNodes,
			GraphOnRowsOrColumns graph, Boolean redraw) {
		if (((fixedNodes != null) && ((fixedNodes.get_Count() >= 1) || (this._vectRows != null)))
				&& (this._vectRows.get_Count() >= 1)) {
			Row row = null;
			Integer num8 = null;
			float hGap = graph.GetHGap();
			float vGap = graph.GetVGap();
			Integer globalVerticalAlignment = graph
					.GetGlobalVerticalAlignment();
			float availableWidthOnRow = graph.GetAvailableWidthOnRow();
			float positionX = graph.GetPositionX();
			float startX = positionX + availableWidthOnRow;
			float positionY = graph.GetPositionY();
			ArrayList floatingNodes = new ArrayList(200);
			Integer count = this._vectRows.get_Count();
			Boolean invertedCoords = graph.IsInvertedCoords();
			for (Integer i = 0; i < count; i++) {
				java.lang.Object obj2 = null;
				row = (Row) this._vectRows.get_Item(i);

				if (!graph.MayContinue()) {

					return;
				}

				num8 = row.Size();
				Integer num10 = 0;
				for (Integer k = 0; k < num8; k++) {

					obj2 = row.ElementAt(k);

					if (IsOverlappingFixedNodes(graph, obj2, fixedNodes,
							invertedCoords)) {
						floatingNodes.Add(obj2);
						num10++;
					}
				}
				if (num10 > 0) {
					num8 = floatingNodes.get_Count();
					for (Integer m = num8 - 1; m >= (num8 - num10); m--) {
						obj2 = floatingNodes.get_Item(m);
						row.RemoveElement(obj2);
					}
				}
			}
			num8 = floatingNodes.get_Count();
			for (Integer j = 0; j < count; j++) {
				row = (Row) this._vectRows.get_Item(j);

				if (!graph.MayContinue()) {

					return;
				}
				if ((j < (count - 1)) || (row.Size() > 0)) {
					positionY += row.GetMaxHeight() + vGap;
				}
			}
			if (num8 > 0) {
				Boolean flag = null;
				Integer num16 = null;
				InternalRect bbox = new InternalRect(0f, 0f, 0f, 0f);
				LayoutUtil.BoundingBox(graph.GetGraphModel(),
						TranslateUtil.Collection2JavaStyleEnum(fixedNodes),
						bbox);
				Integer startIndex = 0;
				float num17 = (graph.GetY(bbox) + graph.GetHeight(bbox)) + vGap;
				if (count > 0) {
					Row row2 = (Row) this._vectRows.get_Item(count - 1);
					float maxHeight = row2.GetMaxHeight();
					if (num17 <= ((positionY - maxHeight) - vGap)) {
						num16 = startIndex;

						startIndex = row2.FillWithNodes(floatingNodes, graph,
								startIndex, hGap, availableWidthOnRow, redraw);
						if (startIndex != num16) {
							float yRow = (positionY - maxHeight) - vGap;

							flag = this.IsReversedOrder(graph, row2, count - 1,
									availableWidthOnRow);
							if (flag) {
								row2.Layout(graph, startX, yRow, hGap,
										globalVerticalAlignment, flag, redraw);
							} else {
								row2.Layout(graph, positionX, yRow, hGap,
										globalVerticalAlignment, flag, redraw);
							}
							positionY = yRow + (row2.GetMaxHeight() + vGap);
						}
					}
				}

				positionY = Math.Max(positionY, num17);
				Integer rowIndex = this._vectRows.get_Count() - 1;
				while (startIndex < num8) {
					row = new Row();

					if (!graph.MayContinue()) {

						return;
					}
					num16 = startIndex;
					rowIndex++;

					startIndex = row.FillWithNodes(floatingNodes, graph,
							startIndex, hGap, availableWidthOnRow, redraw);
					if (startIndex != num16) {

						flag = this.IsReversedOrder(graph, row, rowIndex,
								availableWidthOnRow);
						if (flag) {
							row.Layout(graph, startX, positionY, hGap,
									globalVerticalAlignment, flag, redraw);
						} else {
							row.Layout(graph, positionX, positionY, hGap,
									globalVerticalAlignment, flag, redraw);
						}
						positionY += row.GetMaxHeight() + vGap;
						this._vectRows.Add(row);
					}
				}
			}
		}

	}

	private void Reinitialize() {
		this._vectRows.Clear();

	}

	public void Update(GraphOnRowsOrColumns graph, Boolean redraw) {

		if (graph.IsIncrementalMode()) {
			this.UpdateIncremental(graph, redraw);
		} else {
			this.Reinitialize();
		}

	}

	private void UpdateIncremental(GraphOnRowsOrColumns graph, Boolean redraw) {
		float hGap = graph.GetHGap();
		float vGap = graph.GetVGap();
		Integer globalVerticalAlignment = graph.GetGlobalVerticalAlignment();
		float availableWidthOnRow = graph.GetAvailableWidthOnRow();
		Integer num5 = (int) Math.Max(1,
				graph.GetMaxNumberOfNodesPerRowOrColumn());
		Integer count = this._vectRows.get_Count();
		IGraphModel graphModel = graph.GetGraphModel();
		float positionX = graph.GetPositionX();
		float startX = positionX + availableWidthOnRow;
		float positionY = graph.GetPositionY();
		ILOG.Diagrammer.GraphLayout.GraphLayout layout = graph.GetLayout();
		Boolean flag = layout.SupportsPreserveFixedNodes()
				&& layout.get_PreserveFixedNodes();
		ArrayList list = new ArrayList(50);
		ArrayList list2 = null;
		for (Integer i = 0; i < count; i++) {
			java.lang.Object obj2 = null;

			if (!graph.MayContinue()) {

				return;
			}
			Row row = (Row) this._vectRows.get_Item(i);
			Integer num7 = row.Size();
			list.Clear();
			float num9 = 0f;
			Integer num8 = 0;
			for (Integer j = 0; j < num7; j++) {

				obj2 = row.ElementAt(j);

				if (graphModel.IsNode(obj2)
						&& ((!flag || !graph.IsFixedNodeValid(obj2)) || !graph
								.IsFixed(obj2))) {
					num8++;
					if (num8 > num5) {
						break;
					}

					num9 += graph.GetWidth(graph.GetNodeBox(obj2));
					if (num8 > 1) {
						num9 += hGap;
					}
					if (num9 > availableWidthOnRow) {
						break;
					}
					layout.SetProperty(graphModel, obj2, Graph.ATTACH_PROPERTY,
							Type.GetType(RowsCollection.class.getName()));
					list.Add(obj2);
				}
			}
			row.Clear();
			num7 = list.get_Count();
			for (Integer k = 0; k < num7; k++) {
				obj2 = list.get_Item(k);
				row.AddNode(graph, obj2, graph.GetNodeBox(obj2), hGap);
			}
			if (row.Size() < 1) {
				if (list2 == null) {
					list2 = new ArrayList(50);
				}
				list2.Add(row);
			} else {
				Boolean isReversedOrder = this.IsReversedOrder(graph, row, i,
						availableWidthOnRow);
				if (isReversedOrder) {
					row.Layout(graph, startX, positionY, hGap,
							globalVerticalAlignment, isReversedOrder, redraw);
				} else {
					row.Layout(graph, positionX, positionY, hGap,
							globalVerticalAlignment, isReversedOrder, redraw);
				}
				positionY += row.GetMaxHeight() + vGap;
			}
		}
		if (list2 != null) {
			Integer num16 = list2.get_Count();
			for (Integer m = 0; m < num16; m++) {
				TranslateUtil.Remove(this._vectRows, list2.get_Item(m));
			}
		}

	}

}