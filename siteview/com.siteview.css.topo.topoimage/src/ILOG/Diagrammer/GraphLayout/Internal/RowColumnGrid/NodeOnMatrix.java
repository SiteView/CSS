package ILOG.Diagrammer.GraphLayout.Internal.RowColumnGrid;

import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;

public final class NodeOnMatrix {
	private Integer _height = 1;

	private Integer _index1 = -2147483647;

	private Integer _index2 = -2147483647;

	private Boolean _isFixed = false;

	private java.lang.Object _node;

	private Integer _width = 1;

	private Integer NOT_COMPUTED = -2147483647;

	public NodeOnMatrix(java.lang.Object node, Boolean f) {
		this._node = node;
		this._isFixed = f;
	}

	public void ComputeLocationOnMatrix(GraphOnMatrix graphOnMatrix) {
		java.lang.Object graphNode = this.GetGraphNode();
		InternalRect nodeBox = graphOnMatrix.GetNodeBox(graphNode);

		if (this.IsFixed()) {

			this._index1 = GraphOnMatrix.GetIndexOfCell(
					graphOnMatrix.GetX(nodeBox) - graphOnMatrix.GetPositionX(),
					graphOnMatrix.GetHorizontalGridOffset());

			this._index2 = GraphOnMatrix.GetIndexOfCell(
					graphOnMatrix.GetY(nodeBox) - graphOnMatrix.GetPositionY(),
					graphOnMatrix.GetVerticalGridOffset());
			this._width = (GraphOnMatrix.GetIndexOfCell(
					(graphOnMatrix.GetX(nodeBox) + graphOnMatrix
							.GetWidth(nodeBox)) - graphOnMatrix.GetPositionX(),
					graphOnMatrix.GetHorizontalGridOffset()) - this._index1) + 1;
			this._height = (GraphOnMatrix
					.GetIndexOfCell(
							(graphOnMatrix.GetY(nodeBox) + graphOnMatrix
									.GetHeight(nodeBox))
									- graphOnMatrix.GetPositionY(),
							graphOnMatrix.GetVerticalGridOffset()) - this._index2) + 1;
		} else {
			this._index1 = -2147483647;
			this._index2 = -2147483647;

			this._width = GraphOnMatrix.GetNumberOfOverlappedCells(
					(graphOnMatrix.GetWidth(nodeBox) + graphOnMatrix
							.GetLeftMargin()) + graphOnMatrix.GetRightMargin(),
					graphOnMatrix.GetHorizontalGridOffset());

			this._height = GraphOnMatrix.GetNumberOfOverlappedCells(
					(graphOnMatrix.GetHeight(nodeBox) + graphOnMatrix
							.GetTopMargin()) + graphOnMatrix.GetBottomMargin(),
					graphOnMatrix.GetVerticalGridOffset());
		}

	}

	public void Dispose() {
		this._node = null;

	}

	public Boolean FitsInOneCell() {

		return ((this._width == 1) && (this._height == 1));

	}

	public java.lang.Object GetGraphNode() {

		return this._node;

	}

	public Integer GetHeight() {

		return this._height;

	}

	public Integer GetIndex1() {

		return this._index1;

	}

	public Integer GetIndex2() {

		return this._index2;

	}

	public Integer GetWidth() {

		return this._width;

	}

	public Boolean IsAlreadyPlaced() {

		return ((this._index1 != -2147483647) && (this._index2 != -2147483647));

	}

	public Boolean IsDied() {

		return (this._node == null);

	}

	public Boolean IsFixed() {

		return this._isFixed;

	}

	public void NodeDied() {
		this._node = null;

	}

	public void SetFixed(Boolean f) {
		this._isFixed = f;

	}

	public void SetIndex1(Integer index) {
		this._index1 = index;

	}

	public void SetIndex2(Integer index) {
		this._index2 = index;

	}

	public void SetNotAlreadyPlaced() {
		this._index1 = -2147483647;
		this._index2 = -2147483647;

	}

}