package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class HFloatNodeMarker {
	private float[] _markers;

	public HFloatNodeMarker(HGraph graph) {
		this._markers = new float[graph.GetNumberOfNodes()];
		graph.UpdateNodeIDs();
	}

	public void Add(HNode node, float incNumber) {
		this._markers[node.GetNumID()] += incNumber;

	}

	public void Clean() {
		this._markers = null;

	}

	public float Get(HNode node) {

		return this._markers[node.GetNumID()];

	}

	public void Set(HNode node, float markerNumber) {
		this._markers[node.GetNumID()] = markerNumber;

	}

	public void SetAll(float markerNumber) {
		for (Integer i = 0; i < this._markers.length; i++) {
			this._markers[i] = markerNumber;
		}

	}

}