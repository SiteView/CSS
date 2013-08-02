package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class HIntLevelMarker {
	private Integer[] _markers;

	public HIntLevelMarker(HGraph graph) {
		this._markers = new Integer[graph.GetNumberOfLevels()];
		graph.UpdateLevelIDs();
	}

	public void Add(HLevel level, Integer incNumber) {
		this._markers[level.GetNumID()] += incNumber;

	}

	public void Clean() {
		this._markers = null;

	}

	public Integer Get(HLevel level) {

		return this._markers[level.GetNumID()];

	}

}