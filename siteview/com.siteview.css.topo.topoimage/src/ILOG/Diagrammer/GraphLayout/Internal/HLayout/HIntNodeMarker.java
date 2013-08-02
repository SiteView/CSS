package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class HIntNodeMarker {
	private Integer[] _markers;

	public HIntNodeMarker(HGraph graph) {
		this._markers = new Integer[graph.GetNumberOfNodes()];
		graph.UpdateNodeIDs();
	}

	public void Add(HNode node, Integer incNumber) {
		this._markers[node.GetNumID()] += incNumber;

	}

	public void Clean() {
		this._markers = null;

	}

	public Integer Get(HNode node) {

		return this._markers[node.GetNumID()];

	}

	public void Set(HNode node, Integer markerNumber) {
		this._markers[node.GetNumID()] = markerNumber;

	}

	public void SetAll(Integer markerNumber) {
		for (Integer i = 0; i < this._markers.length; i++) {
			this._markers[i] = markerNumber;
		}

	}

}