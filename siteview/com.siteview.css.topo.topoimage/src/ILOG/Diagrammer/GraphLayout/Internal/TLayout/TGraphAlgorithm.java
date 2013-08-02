package ILOG.Diagrammer.GraphLayout.Internal.TLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public class TGraphAlgorithm {
	private TGraph _graph;

	private PercCompleteController _percCompleteController;

	public TGraphAlgorithm(TGraph graph) {
		this._graph = graph;

		this._percCompleteController = graph.GetPercController();
	}

	public void AddPercPoints(Integer points) {
		this._percCompleteController.AddPoints(points);

	}

	public void DisposeIt() {
		this._graph = null;

	}

	public TGraph GetGraph() {

		return this._graph;

	}

	public PercCompleteController GetPercController() {

		return this._percCompleteController;

	}

	public void IncrPercPointEstimation(Integer delta) {
		this._percCompleteController.IncrementPointEstimation(delta);

	}

	public void LayoutStepPerformed() {
		this.GetGraph().LayoutStepPerformed();

	}

	public Boolean MayContinue() {

		return this._graph.GetLayout().MayContinue();

	}

	public void SetStepEstimation(Integer numberOfSteps) {
		this._percCompleteController.SetStepEstimation(numberOfSteps);

	}

	public void StartStep(float percentageOfStep, Integer pointsOfStep) {
		this._percCompleteController.StartStep(percentageOfStep, pointsOfStep);

	}

}