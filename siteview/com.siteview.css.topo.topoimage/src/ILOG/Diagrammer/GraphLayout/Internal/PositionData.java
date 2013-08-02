package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.GraphLayout.*;
import system.*;

public final class PositionData {
	private Boolean _atLeastOneNodeMoved = false;

	private Integer _firstIndexOfFixedNodes;

	private Integer _firstIndexOfMoveableNodes;

	private IGraphModel _graphModel;

	private ILOG.Diagrammer.GraphLayout.GraphLayout _layout;

	private GraphLayoutData _layoutData;

	private float _maxDelta;

	private InternalRect _nbbox;

	private Integer _numberOfFixedNodes;

	private Integer _numberOfMoveableNodes;

	private float _nx;

	private float _ny;

	private TopologicalData _topologicalData;

	private Boolean[] _vectFixedNodesFlags;

	private Integer[] _vectNodesIds;

	private float[] _vectNodesPositionX;

	private float[] _vectNodesPositionY;

	public PositionData(ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			GraphLayoutData layoutData) {
		this._layout = layout;
		this._layoutData = layoutData;
		this.Initialize();
	}

	private void ComputeNodePosition(java.lang.Object node) {

		this._nbbox = GraphModelUtil.BoundingBox(this._graphModel, node);
		this._nx = this._nbbox.X + (this._nbbox.Width * 0.5f);
		this._ny = this._nbbox.Y + (this._nbbox.Height * 0.5f);

	}

	private void ComputeVectNodesPosition() {
		java.lang.Object[] nodes = this._topologicalData.GetNodes();
		if (nodes != null) {
			for (Integer i = 0; i < nodes.length; i++) {
				java.lang.Object nodeOrLink = nodes[i];
				Integer intIdentifier = this._layoutData
						.GetIntIdentifier(nodeOrLink);
				this.ComputeNodePosition(nodeOrLink);
				this._vectNodesPositionX[intIdentifier] = this._nx;
				this._vectNodesPositionY[intIdentifier] = this._ny;
			}
		}

	}

	public float GetDistX(Integer nodeId1, Integer nodeId2) {

		return (this._vectNodesPositionX[nodeId1] - this._vectNodesPositionX[nodeId2]);

	}

	public float GetDistY(Integer nodeId1, Integer nodeId2) {

		return (this._vectNodesPositionY[nodeId1] - this._vectNodesPositionY[nodeId2]);

	}

	public Integer GetFirstIndexOfFixedNodes() {

		return this._firstIndexOfFixedNodes;

	}

	public Integer GetFirstIndexOfMoveableNodes() {

		return this._firstIndexOfMoveableNodes;

	}

	public float GetMaxMovement() {

		return this._maxDelta;

	}

	public Integer GetNodeId(Integer k) {

		return this._vectNodesIds[k];

	}

	public InternalPoint GetNodesBarycenter() {
		Integer numberOfNodes = this._topologicalData.GetNumberOfNodes();
		if (numberOfNodes < 1) {
			throw (new system.Exception("bad number of nodes: " + numberOfNodes));
		}
		float num2 = 0f;
		float num3 = 0f;
		for (Integer i = 0; i < numberOfNodes; i++) {
			num2 += this._vectNodesPositionX[i];
			num3 += this._vectNodesPositionY[i];
		}

		return new InternalPoint(num2 / ((float) numberOfNodes), num3
				/ ((float) numberOfNodes));

	}

	public Integer GetNumberOfFixedNodes() {

		return this._numberOfFixedNodes;

	}

	public Integer GetNumberOfMoveableNodes() {

		return this._numberOfMoveableNodes;

	}

	public float GetXofNode(Integer nodeId) {

		return this._vectNodesPositionX[nodeId];

	}

	public float GetYofNode(Integer nodeId) {

		return this._vectNodesPositionY[nodeId];

	}

	public Boolean HasBeenIdentifiedAsFixed(Integer nodeId) {
		if (this._numberOfMoveableNodes == 0) {

			return true;
		}
		if (this._numberOfFixedNodes == 0) {

			return false;
		}

		return this._vectFixedNodesFlags[nodeId];

	}

	public Boolean IdentifyFixedAndMoveableNodes(Boolean hasAlwaysFixedNodes,
			Boolean storeFixedNodesFlags) {
		this._numberOfMoveableNodes = 0;
		this._firstIndexOfFixedNodes = 0;
		this._numberOfFixedNodes = 0;
		if (this._layout == null) {

			return false;
		}
		if (this._topologicalData == null) {

			return false;
		}
		Integer numberOfNodes = this._topologicalData.GetNumberOfNodes();
		if (numberOfNodes < 1) {

			return false;
		}
		Boolean preserveFixedNodes = this._layout.get_PreserveFixedNodes();
		Boolean flag2 = false;
		if (preserveFixedNodes || hasAlwaysFixedNodes) {
			if (storeFixedNodesFlags) {
				flag2 = true;
				if ((this._vectFixedNodesFlags == null)
						|| (numberOfNodes != this._vectFixedNodesFlags.length)) {
					this._vectFixedNodesFlags = new Boolean[numberOfNodes];
				}
			}
			for (Integer j = 0; j < numberOfNodes; j++) {

				if (this._layoutData.IsFixed(j)) {
					this._vectNodesIds[this._numberOfFixedNodes++] = j;
					if (flag2) {
						this._vectFixedNodesFlags[j] = true;
					}
				}
			}
		}
		this._firstIndexOfMoveableNodes = this._firstIndexOfFixedNodes
				+ this._numberOfFixedNodes;
		for (Integer i = 0; i < numberOfNodes; i++) {

			if (!this._layoutData.IsFixed(i)) {
				this._vectNodesIds[this._firstIndexOfMoveableNodes
						+ this._numberOfMoveableNodes++] = i;
				if (flag2) {
					this._vectFixedNodesFlags[i] = false;
				}
			}
		}

		return true;

	}

	public void Initialize() {

		this._graphModel = this._layout.GetGraphModel();

		this._topologicalData = this._layoutData.GetTopologicalData();
		this._atLeastOneNodeMoved = false;
		Integer numberOfNodes = this._topologicalData.GetNumberOfNodes();
		if ((this._vectNodesIds == null)
				|| (numberOfNodes != this._vectNodesIds.length)) {
			this._vectNodesPositionX = new float[numberOfNodes];
			this._vectNodesPositionY = new float[numberOfNodes];
			this._vectNodesIds = new Integer[numberOfNodes];
		}
		this.ComputeVectNodesPosition();

	}

	public Boolean IsAtLeastOneNodeMoved() {

		return this._atLeastOneNodeMoved;

	}

	public Boolean MoveNode(Integer nodeId, float x, float y, Boolean redraw) {
		java.lang.Object node = this._topologicalData.GetNode(nodeId);
		InternalRect rect = GraphModelUtil.BoundingBox(this._graphModel, node);
		this._graphModel.MoveNode(node, x - (rect.Width * 0.5f), y
				- (rect.Height * 0.5f));
		this._atLeastOneNodeMoved = true;
		this.SetNodePosition(nodeId, x, y);

		return true;

	}

	public Boolean PlaceNodesAtPosition(Boolean redraw) {

		return this.PlaceNodesAtPosition(redraw, null);

	}

	public Boolean PlaceNodesAtPosition(Boolean redraw,
			InternalPoint centerPoint) {
		float num = 0f;
		float num2 = 0f;
		InternalPoint point = (centerPoint != null) ? this.GetNodesBarycenter()
				: null;
		if (point != null) {
			num = centerPoint.X - point.X;
			num2 = centerPoint.Y - point.Y;
		}
		Boolean flag = true;
		Integer num3 = this._firstIndexOfMoveableNodes
				+ this._numberOfMoveableNodes;
		for (Integer i = this._firstIndexOfMoveableNodes; i < num3; i++) {
			Integer nodeId = this._vectNodesIds[i];

			if (!this.MoveNode(nodeId, this.GetXofNode(nodeId) + num,
					this.GetYofNode(nodeId) + num2, redraw)) {
				flag = false;
			}
		}

		return flag;

	}

	public Boolean PlaceNodesAtPositionInAnimation(Boolean redraw) {

		return this.PlaceNodesAtPositionInAnimation(redraw, null);

	}

	public Boolean PlaceNodesAtPositionInAnimation(Boolean redraw,
			InternalPoint centerPoint) {
		Boolean flag = false;
		TranslateUtil.Noop();
		try {

			flag = this.PlaceNodesAtPosition(redraw, centerPoint);
		} finally {
			TranslateUtil.Noop();
		}

		return flag;

	}

	public void RecordMove(float deltaX, float deltaY) {
		if (deltaX > this._maxDelta) {
			this._maxDelta = deltaX;
		}
		if (deltaY > this._maxDelta) {
			this._maxDelta = deltaY;
		}

	}

	public void ResetMaxMovementCounter() {
		this._maxDelta = 0f;

	}

	public void SetNodePosition(Integer nodeId, float x, float y) {
		this._vectNodesPositionX[nodeId] = x;
		this._vectNodesPositionY[nodeId] = y;

	}

}