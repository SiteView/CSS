package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public abstract class HLevelAlgorithm {
	private Integer _edgeFlowDir;

	private HGraph _graph;

	private Integer _levelFlowDir;

	private HLevel _lowerLevel;

	private HLevel _upperLevel;

	public HLevelAlgorithm() {
	}

	public void Clean() {
		this._upperLevel = null;
		this._lowerLevel = null;
		this._graph = null;

	}

	public Integer GetEdgeFlowDir() {

		return this._edgeFlowDir;

	}

	public HGraph GetGraph() {

		return this._graph;

	}

	public Integer GetLevelFlowDir() {

		return this._levelFlowDir;

	}

	public HLevel GetLowerLevel() {

		return this._lowerLevel;

	}

	public HLevel GetUpperLevel() {

		return this._upperLevel;

	}

	public void Init(HLevel upperLevel, HLevel lowerLevel) {

		this._graph = upperLevel.GetOwnerGraph();
		this._upperLevel = upperLevel;
		this._lowerLevel = lowerLevel;

		this._levelFlowDir = this._graph.GetLevelFlow();

		this._edgeFlowDir = this._graph.GetEdgeFlow();

	}

	public abstract void Run();

}