package ILOG.Diagrammer.GraphLayout.Internal.RowColumnGrid;

import system.ArgumentException;
import system.Math;
import ILOG.Diagrammer.GraphLayout.IGraphModel;
import ILOG.Diagrammer.GraphLayout.Internal.GrowableArray;

public final class Matrix {
	private Integer _availableSize;

	private Integer[] _firstOccupiedPlace = new Integer[2];

	private GrowableArray _matrix;

	private Boolean _usePlaceFromDeadNodes = true;

	private Integer CAPACITY_INCREMENT = 100;

	public Matrix(GraphOnMatrix gridGraph) {
		Integer numberOfRowsOrColumns = gridGraph.GetNumberOfRowsOrColumns();
		this._matrix = new GrowableArray(numberOfRowsOrColumns, 100);
		this._availableSize = numberOfRowsOrColumns;
	}

	public void Dispose() {
		this._matrix = null;
		this._availableSize = 0;

	}

	public Integer GetAvailableSize() {

		return this._availableSize;

	}

	public Integer[] GetFirstOccupiedPlace(NodeOnMatrix nodeOnMatrix,
			Integer index1, Integer index2) {
		Integer num = index1 + nodeOnMatrix.GetWidth();
		Integer num2 = index2 + nodeOnMatrix.GetHeight();
		Boolean outsideAvailableSizeAllowed = false;
		if (nodeOnMatrix.GetWidth() >= this.GetAvailableSize()) {
			if (index1 != 0) {
				throw (new ArgumentException(
						"extra large nodes must be left aligned"));
			}
			outsideAvailableSizeAllowed = true;
		}
		if (!outsideAvailableSizeAllowed && (num > this.GetAvailableSize())) {
			throw (new ArgumentException("cannot go outside available area"));
		}
		for (Integer i = index1; i < num; i++) {
			for (Integer j = index2; j < num2; j++) {

				if (!this.IsFreeInternal(i, j, outsideAvailableSizeAllowed)) {
					this._firstOccupiedPlace[0] = i;
					this._firstOccupiedPlace[1] = j;

					return this._firstOccupiedPlace;
				}
			}
		}

		return null;

	}

	private NodeOnMatrix GetNodeOnMatrixInternal(Integer index1, Integer index2) {
		if (index1 <= (this._matrix.Length() - 1)) {
			GrowableArray element = (GrowableArray) this._matrix
					.GetElement(index1);
			if ((element != null) && (index2 <= (element.Length() - 1))) {

				return (NodeOnMatrix) element.GetElement(index2);
			}
		}

		return null;

	}

	public Boolean IsFree(NodeOnMatrix nodeOnMatrix, Integer index1,
			Integer index2) {
		Integer num = index1 + nodeOnMatrix.GetWidth();
		Integer num2 = index2 + nodeOnMatrix.GetHeight();
		Boolean outsideAvailableSizeAllowed = false;
		if (nodeOnMatrix.GetWidth() > this.GetAvailableSize()) {
			if (index1 != 0) {

				return false;
			}
			outsideAvailableSizeAllowed = true;
		}
		if (!outsideAvailableSizeAllowed && (num > this.GetAvailableSize())) {

			return false;
		}
		for (Integer i = index1; i < num; i++) {
			for (Integer j = index2; j < num2; j++) {

				if (!this.IsFreeInternal(i, j, outsideAvailableSizeAllowed)) {

					return false;
				}
			}
		}

		return true;

	}

	private Boolean IsFreeInternal(Integer index1, Integer index2,
			Boolean outsideAvailableSizeAllowed) {
		if (outsideAvailableSizeAllowed) {
			if (index1 > (this._matrix.Length() - 1)) {

				return true;
			}
		} else if (index1 > (this.GetAvailableSize() - 1)) {

			return false;
		}
		GrowableArray element = (GrowableArray) this._matrix.GetElement(index1);
		if (element == null) {

			return true;
		}
		if (index2 > (element.Length() - 1)) {

			return true;
		}
		NodeOnMatrix matrix = (NodeOnMatrix) element.GetElement(index2);

		return ((matrix == null) || (this._usePlaceFromDeadNodes && matrix
				.IsDied()));

	}

	public void RemoveNodeFromMatrix(NodeOnMatrix nodeOnMatrix) {
		this.RemoveNodeFromMatrix(nodeOnMatrix, nodeOnMatrix.GetIndex1(),
				nodeOnMatrix.GetIndex2(), nodeOnMatrix.GetWidth(),
				nodeOnMatrix.GetHeight());

	}

	public void RemoveNodeFromMatrix(NodeOnMatrix nodeOnMatrix, Integer index1,
			Integer index2, Integer width, Integer height) {
		Integer num = index1 + width;
		Integer num2 = index2 + height;
		if (index1 < 0) {
			index1 = 0;
		}
		if (index2 < 0) {
			index2 = 0;
		}

		num = (int) Math.Min(num, this._matrix.Length());
		for (Integer i = index1; i < num; i++) {
			for (Integer j = index2; j < num2; j++) {
				this.RemoveNodeFromMatrixInternal(nodeOnMatrix, i, j);
			}
		}

	}

	private void RemoveNodeFromMatrixInternal(NodeOnMatrix nodeOnMatrix,
			Integer index1, Integer index2) {
		if (this.GetNodeOnMatrixInternal(index1, index2) == nodeOnMatrix) {
			this.SetNodeOnMatrixInternal(null, index1, index2, false);
		}

	}

	private void SetAvailableSize(Integer size) {
		if (size != this.GetAvailableSize()) {
			GrowableArray array = this._matrix;
			this._matrix = new GrowableArray(size, 100);
			this._availableSize = size;
			Integer num = array.Length();
			if (num > size) {
				num = size;
			}
			for (Integer i = 0; i < num; i++) {
				this._matrix.SetElement(array.GetElement(i), i);
			}
		}

	}

	public void SetNodeOnMatrix(NodeOnMatrix nodeOnMatrix) {
		Integer num = nodeOnMatrix.GetIndex1();
		Integer num2 = nodeOnMatrix.GetIndex2();

		if (!nodeOnMatrix.IsFixed() && ((num < 0) || (num2 < 0))) {
			throw (new system.Exception(
					"indexes cannot be negative, except for fixed nodes"));
		}
		Integer num3 = num + nodeOnMatrix.GetWidth();
		Integer num4 = num2 + nodeOnMatrix.GetHeight();
		if (num < 0) {
			num = 0;
		}
		if (num2 < 0) {
			num2 = 0;
		}

		if (!nodeOnMatrix.IsFixed()) {

			num3 = (int) Math.Min(num3, this.GetAvailableSize());
		}
		for (Integer i = num; i < num3; i++) {
			for (Integer j = num2; j < num4; j++) {
				this.SetNodeOnMatrixInternal(nodeOnMatrix, i, j, false);
			}
		}

	}

	public void SetNodeOnMatrix(NodeOnMatrix nodeOnMatrix, Integer index1,
			Integer index2) {
		nodeOnMatrix.SetIndex1(index1);
		nodeOnMatrix.SetIndex2(index2);
		Integer num = index1 + nodeOnMatrix.GetWidth();
		Integer num2 = index2 + nodeOnMatrix.GetHeight();

		if (!nodeOnMatrix.IsFixed()) {

			num = (int) Math.Min(num, this.GetAvailableSize());
		}
		for (Integer i = index1; i < num; i++) {
			for (Integer j = index2; j < num2; j++) {
				this.SetNodeOnMatrixInternal(nodeOnMatrix, i, j, false);
			}
		}

	}

	private void SetNodeOnMatrixInternal(NodeOnMatrix nodeOnMatrix,
			Integer index1, Integer index2, Boolean moveOverlappingFloatingNode) {
		if (index1 > (this._matrix.Length() - 1)) {
			this._matrix.EnsureCapacity(index1 + 1);
		}
		GrowableArray element = (GrowableArray) this._matrix.GetElement(index1);
		if (element == null) {
			element = new GrowableArray(index2 + 1, 100);
			this._matrix.SetElement(element, index1);
		} else if (index2 > (element.Length() - 1)) {
			element.EnsureCapacity(index2 + 1);
		}
		if (moveOverlappingFloatingNode) {
			NodeOnMatrix matrix = (NodeOnMatrix) element.GetElement(index2);

			if (((matrix != null) && (matrix != nodeOnMatrix))
					&& (!matrix.IsFixed() && matrix.IsAlreadyPlaced())) {
				this.RemoveNodeFromMatrix(matrix);
				matrix.SetNotAlreadyPlaced();
			}
		}
		element.SetElement(nodeOnMatrix, index2);

	}

	public void SetUsePlaceFromDeadNodes(Boolean usePlace) {
		this._usePlaceFromDeadNodes = usePlace;

	}

	private void UpdateFixedNode(NodeOnMatrix nodeOnMatrix, Integer oldIndex1,
			Integer oldIndex2, Integer oldWidth, Integer oldHeight,
			Integer newIndex1, Integer newIndex2, Integer newWidth,
			Integer newHeight) {
		if (((oldIndex1 != newIndex1) || (oldIndex2 != newIndex2))
				|| ((oldWidth != newWidth) || (oldHeight != newHeight))) {
			if (oldIndex1 < 0) {
				oldIndex1 = 0;
			}
			if (oldIndex2 < 0) {
				oldIndex2 = 0;
			}
			for (Integer i = oldIndex1; i < (oldIndex1 + oldWidth); i++) {
				for (Integer k = oldIndex2; k < (oldIndex2 + oldHeight); k++) {
					this.RemoveNodeFromMatrixInternal(nodeOnMatrix, i, k);
				}
			}
			Integer num3 = newIndex1 + newWidth;
			Integer num4 = newIndex2 + newHeight;
			if (newIndex1 < 0) {
				newIndex1 = 0;
			}
			if (newIndex2 < 0) {
				newIndex2 = 0;
			}
			for (Integer j = newIndex1; j < num3; j++) {
				for (Integer m = newIndex2; m < num4; m++) {
					this.SetNodeOnMatrixInternal(nodeOnMatrix, j, m, true);
				}
			}
		}

	}

	private void UpdateFloatingNode(NodeOnMatrix nodeOnMatrix,
			Integer oldIndex1, Integer oldIndex2, Integer oldWidth,
			Integer oldHeight, Integer newWidth, Integer newHeight) {
		if ((oldWidth == newWidth) && (oldHeight == newHeight)) {
			nodeOnMatrix.SetIndex1(oldIndex1);
			nodeOnMatrix.SetIndex2(oldIndex2);
		} else {
			this.RemoveNodeFromMatrix(nodeOnMatrix, oldIndex1, oldIndex2,
					oldWidth, oldHeight);
			if ((newWidth > oldWidth) || (newHeight > oldHeight)) {
				nodeOnMatrix.SetNotAlreadyPlaced();
			} else {
				nodeOnMatrix.SetIndex1(oldIndex1);
				nodeOnMatrix.SetIndex2(oldIndex2);
				Integer num = oldIndex1 + newWidth;
				Integer num2 = oldIndex2 + newHeight;
				if (oldIndex1 < 0) {
					oldIndex1 = 0;
				}
				if (oldIndex2 < 0) {
					oldIndex2 = 0;
				}
				for (Integer i = oldIndex1; i < num; i++) {
					for (Integer j = oldIndex2; j < num2; j++) {
						this.SetNodeOnMatrixInternal(nodeOnMatrix, i, j, true);
					}
				}
			}
		}

	}

	public void UpdateIncremental(GraphOnMatrix graphOnMatrix) {
		this.SetAvailableSize(graphOnMatrix.GetNumberOfRowsOrColumns());

	}

	public void UpdateNodeOnMatrix(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			GraphOnMatrix graphOnMatrix, NodeOnMatrix nodeOnMatrix) {
		Integer num = nodeOnMatrix.GetIndex1();
		Integer num2 = nodeOnMatrix.GetIndex2();
		Integer width = nodeOnMatrix.GetWidth();
		Integer height = nodeOnMatrix.GetHeight();
		nodeOnMatrix.ComputeLocationOnMatrix(graphOnMatrix);
		Integer num5 = nodeOnMatrix.GetIndex1();
		Integer num6 = nodeOnMatrix.GetIndex2();
		Integer newWidth = nodeOnMatrix.GetWidth();
		Integer newHeight = nodeOnMatrix.GetHeight();

		if (nodeOnMatrix.IsFixed()) {
			this.UpdateFixedNode(nodeOnMatrix, num, num2, width, height, num5,
					num6, newWidth, newHeight);
		} else {
			this.UpdateFloatingNode(nodeOnMatrix, num, num2, width, height,
					newWidth, newHeight);
		}

	}

}