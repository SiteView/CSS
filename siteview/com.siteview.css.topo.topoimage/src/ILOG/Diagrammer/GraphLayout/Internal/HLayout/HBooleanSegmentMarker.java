package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class HBooleanSegmentMarker {
	private Boolean[] _markers;

	public HBooleanSegmentMarker(HGraph graph) {
		this._markers = new Boolean[graph.GetNumberOfSegments()];
		graph.UpdateSegmentIDs();
	}

	public void Clean() {
		this._markers = null;

	}

	public Boolean Get(HSegment segment) {

		return this._markers[segment.GetNumID()];

	}

	public void Set(HSegment segment, Boolean marked) {
		this._markers[segment.GetNumID()] = marked;

	}

}