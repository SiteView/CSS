package ILOG.Diagrammer.GraphLayout.Internal.RowColumnGrid;

import system.Collections.ArrayList;
import ILOG.Diagrammer.GraphLayout.IGraphModel;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;

public abstract class GraphOnRowsOrColumns extends Graph {
	private float _hGap;

	private RowsCollection _rowsCollection;

	private float _vGap;

	public Integer TILE_TO_COLUMNS = 1;

	public Integer TILE_TO_ROWS = 0;

	public GraphOnRowsOrColumns(
			ILOG.Diagrammer.GraphLayout.GraphLayout graphLayout) {
		super(graphLayout);
	}

	@Override
	public void Detach() {
		super.Detach();
		this._rowsCollection = null;

	}

	@Override
	public void Dispose() {
		super.Dispose();
		this._rowsCollection = null;

	}

	@Override
	public void DoLayout(Boolean redraw) {
		IGraphModel graphModel = super.GetGraphModel();
		ILOG.Diagrammer.GraphLayout.GraphLayout layout = this.GetLayout();
		Boolean flag = layout.SupportsPreserveFixedNodes()
				&& layout.get_PreserveFixedNodes();
		Boolean flag2 = super.IsIncrementalMode();
		if (this._rowsCollection == null) {
			this._rowsCollection = new RowsCollection();
		} else {
			this._rowsCollection.Update(this, redraw);
		}

		if (this.MayContinue()) {
			ArrayList vectNodes = new ArrayList(500);
			ArrayList fixedNodes = flag ? new ArrayList(500) : null;
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(super.GetGraphModel().get_Nodes());

			while (enumerator.HasMoreElements()) {
				java.lang.Object nodeOrLink = enumerator.NextElement();
				if (flag2
						&& (layout.GetProperty(graphModel, nodeOrLink,
								Graph.ATTACH_PROPERTY) != null)) {
					layout.SetProperty(graphModel, nodeOrLink,
							Graph.ATTACH_PROPERTY, null);
				} else {
					if ((flag && this.IsFixedNodeValid(nodeOrLink))
							&& super.IsFixed(nodeOrLink)) {
						fixedNodes.Add(nodeOrLink);
						continue;
					}

					if (this.IsFloatingNodeValid(nodeOrLink)) {
						vectNodes.Add(nodeOrLink);
					}
				}
			}

			if (this.MayContinue()) {
				if ((vectNodes.get_Count() < 1) && (!flag2 || !flag)) {
					this.LayoutStepPerformed();
				} else {
					this.SortNodes(vectNodes);

					if (this.MayContinue()) {
						this._rowsCollection.FillWithNodes(vectNodes, this,
								redraw);

						if (this.MayContinue()) {
							this._rowsCollection.ProcessFixedNodes(fixedNodes,
									this, redraw);
							this.LayoutStepPerformed();
						}
					}
				}
			}
		}

	}

	public float GetAvailableWidthOnRow() {

		return super.GetMaxDimension();

	}

	public java.lang.Object GetFirstNode(Integer rowIndex) {
		if ((this._rowsCollection != null)
				&& (this._rowsCollection.GetRowsCount() != 0)) {

			return this._rowsCollection.GetRow(rowIndex).ElementAt(0);
		}

		return null;

	}

	public float GetHGap() {

		if (!super.IsInvertedCoords()) {

			return this._hGap;
		}

		return this._vGap;

	}

	@Override
	public abstract Integer GetHorizontalIndividualAlignment(
			java.lang.Object arg0);

	public java.lang.Object GetLastNode(Integer rowIndex) {
		if ((this._rowsCollection == null)
				|| (this._rowsCollection.GetRowsCount() == 0)) {

			return null;
		}
		Row row = this._rowsCollection.GetRow(rowIndex);

		return row.ElementAt(row.Size() - 1);

	}

	public float GetMaxTotalWidth() {
		if (this._rowsCollection == null) {

			return 0f;
		}

		return this._rowsCollection.GetMaxTotalWidth();

	}

	@Override
	public abstract Integer GetNodeIndex(java.lang.Object arg0);

	public float GetRowHeight(Integer rowIndex) {
		if (this._rowsCollection == null) {

			return 0f;
		}

		return this._rowsCollection.GetRow(rowIndex).GetMaxHeight();

	}

	public Integer GetRowsCount() {
		if (this._rowsCollection == null) {

			return 0;
		}

		return this._rowsCollection.GetRowsCount();

	}

	@Override
	public abstract Integer GetVerticalIndividualAlignment(java.lang.Object arg0);

	public float GetVGap() {

		if (!super.IsInvertedCoords()) {

			return this._vGap;
		}

		return this._hGap;

	}

	public Boolean IsFixedNodeValid(java.lang.Object node) {

		return true;

	}

	public Boolean IsFloatingNodeValid(java.lang.Object node) {

		return true;

	}

	public Boolean IsReversedOrder(Integer rowIndex, Integer nNodesOnRow,
			InternalRect bboxFirstNode, float availableWidthOnRow) {

		return false;

	}

	@Override
	public Boolean IsSupportedLayoutMode(Integer mode) {
		if (mode != 0) {

			return (mode == 1);
		}

		return true;

	}

	@Override
	public abstract void LayoutStepPerformed();

	@Override
	public abstract Boolean MayContinue();

	public void SetHGap(float gap) {
		this._hGap = gap;

	}

	public void SetVGap(float gap) {
		this._vGap = gap;

	}

}