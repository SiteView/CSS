package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class HBooleanNodeMarker {
	private Boolean[] _markers;

	public HBooleanNodeMarker(HGraph graph) {
		this._markers = new Boolean[graph.GetNumberOfNodes()];
		graph.UpdateNodeIDs();
	}

	public void Clean() {
		this._markers = null;

	}

	public Boolean Get(HNode node) {

		return this._markers[node.GetNumID()];

	}

	public void Set(HNode node, Boolean marked) {
		this._markers[node.GetNumID()] = marked;

	}

}