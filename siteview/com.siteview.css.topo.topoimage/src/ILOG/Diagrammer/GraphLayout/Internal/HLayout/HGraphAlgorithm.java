package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public abstract class HGraphAlgorithm {
	private HGraph _graph;

	private PercCompleteController _percCompleteController;

	public HGraphAlgorithm() {
	}

	public void Clean() {
		this._graph = null;

	}

	public HGraph GetGraph() {

		return this._graph;

	}

	public PercCompleteController GetPercController() {

		return this._percCompleteController;

	}

	public void Init(HGraph graph) {
		this._graph = graph;

		this._percCompleteController = graph.GetPercController();

	}

	public void LayoutStepPerformed() {
		this._graph.LayoutStepPerformed();

	}

	public abstract void Run();

}