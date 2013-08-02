package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class HIntSegmentMarker {
	private Integer[] _markers;

	public HIntSegmentMarker(HGraph graph) {
		this._markers = new Integer[graph.GetNumberOfSegments()];
		graph.UpdateSegmentIDs();
	}

	public void Clean() {
		this._markers = null;

	}

	public Integer Get(HSegment segment) {

		return this._markers[segment.GetNumID()];

	}

	public void Set(HSegment segment, Integer markerNumber) {
		this._markers[segment.GetNumID()] = markerNumber;

	}

	public void SetAll(Integer markerNumber) {
		for (Integer i = 0; i < this._markers.length; i++) {
			this._markers[i] = markerNumber;
		}

	}

}