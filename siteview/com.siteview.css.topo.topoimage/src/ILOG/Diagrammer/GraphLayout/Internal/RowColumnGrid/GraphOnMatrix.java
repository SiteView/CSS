package ILOG.Diagrammer.GraphLayout.Internal.RowColumnGrid;

import system.Math;
import system.Collections.ArrayList;
import ILOG.Diagrammer.GraphLayout.IGraphModel;
import ILOG.Diagrammer.GraphLayout.Internal.ArrayStableSort;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;

public abstract class GraphOnMatrix extends Graph {
	private float _bottomMargin;

	private float _horizontalGridOffset;

	private float _leftMargin;

	private Matrix _matrix;

	private float _rightMargin;

	private ArrayStableSort _sortByDescendingMatrixNodeHeight;

	private float _topMargin;

	private float _totalHorizMargin;

	private float _totalVertMargin;

	private ArrayList _vectNodesOnMatrix;

	private float _verticalGridOffset;

	private Integer STOPPED_INDEX = -2147483647;

	public Integer TILE_TO_GRID_FIXED_HEIGHT = 3;

	public Integer TILE_TO_GRID_FIXED_WIDTH = 2;

	public GraphOnMatrix(ILOG.Diagrammer.GraphLayout.GraphLayout graphLayout) {
		super(graphLayout);
		this._sortByDescendingMatrixNodeHeight = new SortNodesByDescendingMatrixNodeHeight();
	}

	@Override
	public void Detach() {
		this._matrix = null;
		super.Detach();

	}

	@Override
	public void Dispose() {
		super.Dispose();
		if (this._matrix != null) {
			this._matrix.Dispose();
			this._matrix = null;
		}
		if (this._vectNodesOnMatrix != null) {
			this._vectNodesOnMatrix.Clear();
		}

	}

	@Override
	public void DoLayout(Boolean redraw) {
		this._totalHorizMargin = this.GetLeftMargin() + this.GetRightMargin();
		this._totalVertMargin = this.GetTopMargin() + this.GetBottomMargin();
		if (this._matrix == null) {
			this._matrix = new Matrix(this);
		} else if (super.IsIncrementalMode()) {
			if (this._matrix.GetAvailableSize() != this
					.GetNumberOfRowsOrColumns()) {
				this._matrix = new Matrix(this);
			} else {
				this._matrix.UpdateIncremental(this);
			}
		} else {
			this._matrix = new Matrix(this);
		}
		this._matrix.SetUsePlaceFromDeadNodes(true);
		this.ProcessNodes(redraw);
		this.LayoutStepPerformed();

	}

	public float GetBottomMargin() {

		if (!super.IsInvertedCoords()) {

			return this._bottomMargin;
		}

		return this._rightMargin;

	}

	@Override
	public java.lang.Object GetGraphNode(java.lang.Object node) {
		if (node instanceof NodeOnMatrix) {

			return ((NodeOnMatrix) node).GetGraphNode();
		}

		return node;

	}

	public float GetHorizontalGridOffset() {

		if (!super.IsInvertedCoords()) {

			return this._horizontalGridOffset;
		}

		return this._verticalGridOffset;

	}

	@Override
	public abstract Integer GetHorizontalIndividualAlignment(
			java.lang.Object arg0);

	public static Integer GetIndexOfCell(float coord, float gridSize) {

		return (int) Math.Round(Math.Floor((double) (coord / gridSize)));

	}

	public float GetLeftMargin() {

		if (!super.IsInvertedCoords()) {

			return this._leftMargin;
		}

		return this._topMargin;

	}

	@Override
	public InternalRect GetNodeBox(java.lang.Object node) {

		return super.GetNodeBox(this.GetGraphNode(node));

	}

	@Override
	public abstract Integer GetNodeIndex(java.lang.Object arg0);

	public static Integer GetNumberOfOverlappedCells(float dim, float gridSize) {

		return (int) Math.Round(Math.Ceiling((double) (dim / gridSize)));

	}

	public Integer GetNumberOfRowsOrColumns() {

		if (!super.IsInvertedCoords()) {

			return (int) Math.Max(1, (int) Math.Floor((double) (super
					.GetMaxDimension() / this.GetHorizontalGridOffset())));
		}

		return (int) Math.Max(1, (int) Math.Floor((double) (super
				.GetMaxDimension() / this.GetVerticalGridOffset())));

	}

	public float GetRightMargin() {

		if (!super.IsInvertedCoords()) {

			return this._rightMargin;
		}

		return this._bottomMargin;

	}

	public float GetTopMargin() {

		if (!super.IsInvertedCoords()) {

			return this._topMargin;
		}

		return this._leftMargin;

	}

	public float GetVerticalGridOffset() {

		if (!super.IsInvertedCoords()) {

			return this._verticalGridOffset;
		}

		return this._horizontalGridOffset;

	}

	@Override
	public abstract Integer GetVerticalIndividualAlignment(java.lang.Object arg0);

	@Override
	public Boolean IsSupportedLayoutMode(Integer mode) {
		if (mode != 2) {

			return (mode == 3);
		}

		return true;

	}

	@Override
	public abstract void LayoutStepPerformed();

	@Override
	public abstract Boolean MayContinue();

	private void MoveNode(NodeOnMatrix nodeOnMatrix, Boolean redraw) {
		InternalPoint point = null;
		float num5 = 0;
		float num6 = 0;
		float num = (nodeOnMatrix.GetIndex1() * this.GetHorizontalGridOffset())
				+ this.GetLeftMargin();
		float num2 = (nodeOnMatrix.GetIndex2() * this.GetVerticalGridOffset())
				+ this.GetTopMargin();
		float num3 = this.GetHorizontalGridOffset() * nodeOnMatrix.GetWidth();
		float num4 = this.GetVerticalGridOffset() * nodeOnMatrix.GetHeight();
		java.lang.Object graphNode = nodeOnMatrix.GetGraphNode();
		InternalRect nodeBox = this.GetNodeBox(graphNode);
		Boolean flag = super.IsInvertedCoords();
		if (!flag) {
			if (this.GetHorizontalAlignment(graphNode) == 0) {
				num += ((num3 - this._totalHorizMargin) - super
						.GetWidth(nodeBox)) * 0.5f;
				if (this.GetVerticalAlignment(graphNode) == 0) {
					num2 += ((num4 - this._totalVertMargin) - super
							.GetHeight(nodeBox)) * 0.5f;
				} else if (this.GetVerticalAlignment(graphNode) == 1
						|| this.GetVerticalAlignment(graphNode) == 2
						|| this.GetVerticalAlignment(graphNode) == 3) {
				} else if (this.GetVerticalAlignment(graphNode) == 4) {
					num2 += (num4 - this._totalVertMargin)
							- super.GetHeight(nodeBox);
				}
			} else if (this.GetHorizontalAlignment(graphNode) == 1) {
				if (this.GetVerticalAlignment(graphNode) == 0) {
					num2 += ((num4 - this._totalVertMargin) - super
							.GetHeight(nodeBox)) * 0.5f;
				} else if (this.GetVerticalAlignment(graphNode) == 1
						|| this.GetVerticalAlignment(graphNode) == 2
						|| this.GetVerticalAlignment(graphNode) == 3) {
				} else if (this.GetVerticalAlignment(graphNode) == 4) {
					num2 += (num4 - this._totalVertMargin)
							- super.GetHeight(nodeBox);
				}
			} else if (this.GetHorizontalAlignment(graphNode) == 2) {
				num += (num3 - this._totalHorizMargin)
						- super.GetWidth(nodeBox);
				if (this.GetVerticalAlignment(graphNode) == 0) {
					num2 += ((num4 - this._totalVertMargin) - super
							.GetHeight(nodeBox)) * 0.5f;
				} else if (this.GetVerticalAlignment(graphNode) == 1
						|| this.GetVerticalAlignment(graphNode) == 2
						|| this.GetVerticalAlignment(graphNode) == 3) {
				} else if (this.GetVerticalAlignment(graphNode) == 4) {
					num2 += (num4 - this._totalVertMargin)
							- super.GetHeight(nodeBox);
				}
			}
		} else {
			if (this.GetHorizontalAlignment(graphNode) == 0) {
				num2 += ((num4 - this._totalVertMargin) - super
						.GetHeight(nodeBox)) * 0.5f;
				// NOTICE: break ignore!!!
			} else if (this.GetHorizontalAlignment(graphNode) == 2) {
				num2 += (num4 - this._totalVertMargin)
						- super.GetHeight(nodeBox);
				// NOTICE: break ignore!!!
			}
			if (this.GetVerticalAlignment(graphNode) == 0) {
				num += ((num3 - this._totalHorizMargin) - super
						.GetWidth(nodeBox)) * 0.5f;
			} else if (this.GetVerticalAlignment(graphNode) == 1
					|| this.GetVerticalAlignment(graphNode) == 2
					|| this.GetVerticalAlignment(graphNode) == 3) {
			} else if (this.GetVerticalAlignment(graphNode) == 4) {
				num += (num3 - this._totalHorizMargin)
						- super.GetWidth(nodeBox);
			} else {
			}
		}

		Label_01C2: point = super.GetPosition();
		if (flag) {
			num5 = num2 + point.X;
			num6 = num + point.Y;
		} else {
			num5 = num + point.X;
			num6 = num2 + point.Y;
		}
		super.MoveNode(graphNode, num5, num6, redraw);

	}

	private void ProcessBigFloatingNodes(ArrayList vectNodes,
			Boolean checkAvailablePlace, Integer startIndex2, Boolean redraw) {
		Integer num = (vectNodes == null) ? 0 : vectNodes.get_Count();
		if (num >= 1) {
			NodeOnMatrix nodeOnMatrix = null;
			Integer num2 = 0;
			Integer num3 = startIndex2;
			Integer num4 = this._matrix.GetAvailableSize() - 1;
			Integer height = -10;
			if (!checkAvailablePlace) {
				for (Integer i = 0; i < num; i++) {

					if (!this.MayContinue()) {

						return;
					}
					nodeOnMatrix = (NodeOnMatrix) vectNodes.get_Item(i);
					if (((num2 + nodeOnMatrix.GetWidth()) - 1) > num4) {
						if (height < 0) {
							throw (new system.Exception(
									"Error: extra big floating node processed as normal big floating node (width = "
											+ nodeOnMatrix.GetWidth() + ")"));
						}
						num2 = 0;
						num3 += height;
						height = -10;
					}
					this._matrix.SetNodeOnMatrix(nodeOnMatrix, num2, num3);
					this.MoveNode(nodeOnMatrix, redraw);

					num2 += nodeOnMatrix.GetWidth();
					if (nodeOnMatrix.GetHeight() > height) {

						height = nodeOnMatrix.GetHeight();
					}
				}
			} else {
				Integer num7 = 0;
				Integer[] numArray = null;
				Boolean flag = false;
				nodeOnMatrix = (NodeOnMatrix) vectNodes.get_Item(num7);
				while (!flag) {

					if (!this.MayContinue()) {

						return;
					}
					if (((num2 + nodeOnMatrix.GetWidth()) - 1) > num4) {
						if (height < 0) {
							height = 1;
						}
						num2 = 0;
						num3 += height;
						height = -10;
					}

					numArray = this._matrix.GetFirstOccupiedPlace(nodeOnMatrix,
							num2, num3);
					if (numArray == null) {
						this._matrix.SetNodeOnMatrix(nodeOnMatrix, num2, num3);
						this.MoveNode(nodeOnMatrix, redraw);

						num2 += nodeOnMatrix.GetWidth();
						num7++;
						if (num7 < num) {
							nodeOnMatrix = (NodeOnMatrix) vectNodes
									.get_Item(num7);
							if (nodeOnMatrix.GetHeight() > height) {

								height = nodeOnMatrix.GetHeight();
							}
						} else {
							flag = true;
						}
					} else {
						num2 = numArray[0] + 1;
					}
				}
			}
		}

	}

	private Integer ProcessExtraBigFloatingNodes(ArrayList vectNodes,
			Boolean checkAvailablePlace, Boolean redraw) {
		NodeOnMatrix matrix = null;
		Integer num = (vectNodes != null) ? vectNodes.get_Count() : 0;
		if (num < 1) {

			return 0;
		}
		Integer num2 = 0;
		if (!checkAvailablePlace) {
			for (Integer i = 0; i < num; i++) {

				if (!this.MayContinue()) {

					return -2147483647;
				}
				matrix = (NodeOnMatrix) vectNodes.get_Item(i);
				this._matrix.SetNodeOnMatrix(matrix, 0, num2);
				this.MoveNode(matrix, redraw);

				num2 += matrix.GetHeight();
			}

			return num2;
		}
		Integer num4 = 0;
		Integer[] numArray = null;
		Boolean flag = false;
		matrix = (NodeOnMatrix) vectNodes.get_Item(num4);
		while (!flag) {

			if (!this.MayContinue()) {

				return -2147483647;
			}

			numArray = this._matrix.GetFirstOccupiedPlace(matrix, 0, num2);
			if (numArray == null) {

				if (!this.MayContinue()) {

					return -2147483647;
				}
				this._matrix.SetNodeOnMatrix(matrix, 0, num2);
				this.MoveNode(matrix, redraw);

				num2 += matrix.GetHeight();
				num4++;
				if (num4 < num) {
					matrix = (NodeOnMatrix) vectNodes.get_Item(num4);
				} else {
					flag = true;
				}
			} else {
				num2 = numArray[1] + 1;
			}
		}

		return num2;

	}

	private void ProcessNodes(Boolean redraw) {

		if (this.MayContinue()) {
			IGraphModel graphModel = super.GetGraphModel();
			ILOG.Diagrammer.GraphLayout.GraphLayout layout = this.GetLayout();
			Boolean flag = super.IsIncrementalMode()
					&& (this._vectNodesOnMatrix != null);
			Boolean flag2 = false;
			Boolean preserveFixedNodes = layout.SupportsPreserveFixedNodes()
					&& layout.get_PreserveFixedNodes();
			ArrayList vectNodes = new ArrayList(500);
			ArrayList list2 = new ArrayList(500);
			ArrayList list3 = null;
			if (this._vectNodesOnMatrix == null) {
				this._vectNodesOnMatrix = new ArrayList(500);
			} else if (flag) {
				this.UpdateOldNodesOnMatrix(preserveFixedNodes);
			}
			this._vectNodesOnMatrix.Clear();
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(layout.GetGraphModel()
							.get_Nodes());

			if (this.MayContinue()) {

				while (enumerator.HasMoreElements()) {
					java.lang.Object nodeOrLink = enumerator.NextElement();
					NodeOnMatrix val = null;
					if (flag) {
						val = (NodeOnMatrix) layout.GetProperty(graphModel,
								nodeOrLink, Graph.ATTACH_PROPERTY);
					}
					if (val == null) {
						val = new NodeOnMatrix(nodeOrLink, preserveFixedNodes
								&& layout.GetFixed(nodeOrLink));
						layout.SetProperty(graphModel, nodeOrLink,
								Graph.ATTACH_PROPERTY, val);
						val.ComputeLocationOnMatrix(this);
					}
					this._vectNodesOnMatrix.Add(val);
					if (flag && val.IsAlreadyPlaced()) {

						if (!val.IsFixed()) {
							this.MoveNode(val, redraw);

							if (!this.MayContinue()) {

								return;
							}
						}
					} else if (val.IsFixed()) {
						flag2 = true;
						this._matrix.SetNodeOnMatrix(val);
					} else if (val.FitsInOneCell()) {
						vectNodes.Add(val);
					} else if (val.GetWidth() >= this._matrix
							.GetAvailableSize()) {
						if (list3 == null) {
							list3 = new ArrayList(500);
						}
						list3.Add(val);
					} else {
						list2.Add(val);
					}
				}
				Integer num = this.ProcessExtraBigFloatingNodes(list3, flag2
						|| flag, redraw);
				if ((num != -2147483647) && this.MayContinue()) {
					this.SortNodesByHeight(list2);

					if (this.MayContinue()) {
						this.ProcessBigFloatingNodes(list2, flag2 || flag,
								flag2 ? 0 : num, redraw);

						if (this.MayContinue()) {
							Integer num2 = (list3 != null) ? list3.get_Count()
									: 0;
							Integer count = list2.get_Count();
							Boolean checkAvailablePlace = ((flag || flag2) || (this
									.GetNodeComparator() != Graph.NO_ORDERING))
									|| (count > 0);
							if ((!flag2 && (num2 == 0)) && (count == 0)) {
								this.SortNodes(vectNodes);
							}
							this.ProcessRegularFloatingNodes(vectNodes,
									checkAvailablePlace, 0, flag2 ? 0 : num,
									redraw);

							if (this.MayContinue()) {
								this.LayoutStepPerformed();
							}
						}
					}
				}
			}
		}

	}

	private void ProcessRegularFloatingNodes(ArrayList vectNodes,
			Boolean checkAvailablePlace, Integer startIndex1,
			Integer startIndex2, Boolean redraw) {
		Integer num = (vectNodes == null) ? 0 : vectNodes.get_Count();
		if (num >= 1) {
			NodeOnMatrix nodeOnMatrix = null;
			Integer num2 = startIndex1;
			Integer num3 = startIndex2;
			Integer num4 = this._matrix.GetAvailableSize() - 1;
			if (!checkAvailablePlace) {
				for (Integer i = 0; i < num; i++) {

					if (!this.MayContinue()) {

						return;
					}
					nodeOnMatrix = (NodeOnMatrix) vectNodes.get_Item(i);
					if (num2 > num4) {
						num2 = 0;
						num3++;
					}
					this._matrix.SetNodeOnMatrix(nodeOnMatrix, num2, num3);
					this.MoveNode(nodeOnMatrix, redraw);
					num2++;
				}
			} else {
				Integer num6 = 0;
				Boolean flag = false;
				nodeOnMatrix = (NodeOnMatrix) vectNodes.get_Item(num6);
				while (!flag) {

					if (!this.MayContinue()) {

						return;
					}
					if (num2 > num4) {
						num2 = 0;
						num3++;
					}

					if (this._matrix.IsFree(nodeOnMatrix, num2, num3)) {
						this._matrix.SetNodeOnMatrix(nodeOnMatrix, num2, num3);
						this.MoveNode(nodeOnMatrix, redraw);
						num6++;
						if (num6 < num) {
							nodeOnMatrix = (NodeOnMatrix) vectNodes
									.get_Item(num6);
						} else {
							flag = true;
						}
					}
					num2++;
				}
			}
		}

	}

	public void SetBottomMargin(float margin) {
		this._bottomMargin = margin;

	}

	public void SetHorizontalGridOffset(float offset) {
		this._horizontalGridOffset = offset;

	}

	public void SetLeftMargin(float margin) {
		this._leftMargin = margin;

	}

	public void SetRightMargin(float margin) {
		this._rightMargin = margin;

	}

	public void SetTopMargin(float margin) {
		this._topMargin = margin;

	}

	public void SetVerticalGridOffset(float offset) {
		this._verticalGridOffset = offset;

	}

	private void SortNodesByHeight(ArrayList vectNodes) {
		Graph.SortNodesVector(this._sortByDescendingMatrixNodeHeight, vectNodes);

	}

	@Override
	public void UpdateIncremental() {
		if (this._matrix != null) {
			this._matrix.UpdateIncremental(this);
		}

	}

	private void UpdateOldNodesOnMatrix(Boolean preserveFixedNodes) {
		if (this._vectNodesOnMatrix != null) {
			NodeOnMatrix matrix = null;
			Integer count = this._vectNodesOnMatrix.get_Count();
			ILOG.Diagrammer.GraphLayout.GraphLayout layout = this.GetLayout();
			IGraphModel graphModel = super.GetGraphModel();
			for (Integer i = 0; i < count; i++) {
				matrix = (NodeOnMatrix) this._vectNodesOnMatrix.get_Item(i);

				if (!graphModel.IsNode(matrix.GetGraphNode())) {
					matrix.NodeDied();
				} else {
					matrix.SetFixed(preserveFixedNodes
							&& layout.GetFixed(matrix.GetGraphNode()));
				}
			}
			for (Integer j = 0; j < count; j++) {
				matrix = (NodeOnMatrix) this._vectNodesOnMatrix.get_Item(j);

				if (!matrix.IsDied() && matrix.IsAlreadyPlaced()) {
					this._matrix.UpdateNodeOnMatrix(graphModel, layout, this,
							matrix);
				}
			}
		}

	}

	private final class SortNodesByDescendingMatrixNodeHeight extends
			ArrayStableSort {
		@Override
		public Boolean Compare(java.lang.Object nodeOnMatrix1,
				java.lang.Object nodeOnMatrix2) {

			return (((NodeOnMatrix) nodeOnMatrix1).GetHeight() >= ((NodeOnMatrix) nodeOnMatrix2)
					.GetHeight());

		}

	}
}