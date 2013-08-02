package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.GraphLayout.*;
import system.*;

public final class GraphLayoutData {
	private FixedNodeInterface _fixedNodeInterface;

	private ILOG.Diagrammer.GraphLayout.GraphLayout _layout;

	private PositionData _positionData;

	private TopologicalData _topologicalData;

	private static String NUMBER_PROPERTY = "__GraphLayoutNumber";

	public GraphLayoutData(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		this(layout, null);
	}

	public GraphLayoutData(ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			FixedNodeInterface fixedNodeInterface) {
		this._layout = layout;
		this._fixedNodeInterface = fixedNodeInterface;
	}

	public void BeforeLayout(Boolean needsLinksBetweenNodes,
			Boolean needsNeighbours, Boolean oneConnectedComp,
			Boolean memorySavings) {
		if (!this._layout.get_StructureUpToDate()
				|| (this._topologicalData == null)) {
			this.UpdateTopologicalData(oneConnectedComp);
			if (needsLinksBetweenNodes) {
				this._topologicalData
						.ComputeWhatLinkBetweenTwoNodes(memorySavings);
			}
			if (needsNeighbours) {
				this._topologicalData.InitializeNeighbours();
			}
		}
		if ((!this._layout.get_StructureUpToDate() || !this._layout
				.get_GeometryUpToDate()) || (this._positionData == null)) {
			this.UpdatePositionData();
		}

	}

	public Integer GetIntIdentifier(java.lang.Object nodeOrLink) {
		java.lang.Object obj2 = LayoutParametersUtil.GetNodeOrLinkProperty(
				this._layout, nodeOrLink, NUMBER_PROPERTY);
		if (obj2 == null) {

			return -1;
		}

		return (Integer) obj2;

	}

	public PositionData GetPositionData() {

		return this._positionData;

	}

	public TopologicalData GetTopologicalData() {

		return this._topologicalData;

	}

	public Boolean IsFixed(Integer nodeId) {
		if ((nodeId < 0)
				|| (nodeId >= this._topologicalData.GetNumberOfNodes())) {

			return false;
		}
		if (this._topologicalData == null) {

			return false;
		}
		java.lang.Object node = this._topologicalData.GetNode(nodeId);
		if (this._fixedNodeInterface != null) {

			return this._fixedNodeInterface.IsFixed(node);
		}

		return (this._layout.get_PreserveFixedNodes() && this._layout
				.GetFixed(node));

	}

	public void SetIntIdentifier(java.lang.Object nodeOrLink, Integer id) {
		if (id < 0) {
			id = -1;
		}
		LayoutParametersUtil.SetNodeOrLinkProperty(this._layout, nodeOrLink,
				null, false, false, NUMBER_PROPERTY, (id < 0) ? 0 : id);

	}

	private void UpdatePositionData() {
		if (this._positionData == null) {
			this._positionData = new PositionData(this._layout, this);
		} else {
			this._positionData.Initialize();
		}

	}

	private void UpdateTopologicalData(Boolean oneConnectedComp) {
		if (this._topologicalData == null) {
			this._topologicalData = new TopologicalData(this._layout, this,
					oneConnectedComp);
		} else {
			this._topologicalData.Initialize(oneConnectedComp);
		}

	}

}