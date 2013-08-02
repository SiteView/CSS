package ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public abstract class Algorithm {
	public HTBaseGraph _graph;

	private ILOG.Diagrammer.GraphLayout.GraphLayout _layout;

	private PercCompleteController _percController;

	public Algorithm() {
	}

	public void AddPercPoints(Integer points) {
		if (this._percController != null) {
			this._percController.AddPoints(points);
		}

	}

	public void Clean() {
		this._graph = null;
		this._layout = null;
		this._percController = null;

	}

	public PercCompleteController GetPercController() {

		return this._percController;

	}

	public void Init(HTBaseGraph graph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			PercCompleteController percController) {
		this._graph = graph;
		this._layout = layout;
		this._percController = percController;

	}

	public void LayoutStepPerformed() {
		if (this._layout != null) {
			this._layout.OnLayoutStepPerformedIfNeeded();
		}

	}

	public abstract void Run();

}