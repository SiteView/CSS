package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.GraphLayout.*;
import system.*;

public final class PercCompleteController {
	private Integer _numberOfPointsDone;

	private Integer _numberOfPointsNeeded;

	private Integer _numberOfStepsDone;

	private Integer _numberOfStepsNeeded;

	private float _percentageOfCurrentStepFromAll;

	private float _percentageOfPreviousSteps;

	public PercCompleteController() {
		this.Init();
	}

	public void AddPoints(Integer delta) {
		this._numberOfPointsDone += delta;
		if (this._numberOfPointsDone > this._numberOfPointsNeeded) {
			this._numberOfPointsDone = this._numberOfPointsNeeded;
		}

	}

	public Integer GetPoints() {

		return this._numberOfPointsDone;

	}

	public void IncrementPointEstimation(Integer delta) {
		this._numberOfPointsNeeded += delta;

	}

	public void IncrementStepEstimation(Integer delta) {
		this.SetStepEstimation(this._numberOfStepsNeeded + delta);

	}

	public void Init() {
		this._numberOfStepsNeeded = 0;
		this._numberOfStepsDone = 0;
		this._percentageOfPreviousSteps = 0f;
		this._percentageOfCurrentStepFromAll = 0f;
		this._numberOfPointsNeeded = 0;
		this._numberOfPointsDone = 0;

	}

	public void NotifyPercentageComplete(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		float num = this._percentageOfPreviousSteps
				+ ((this._percentageOfCurrentStepFromAll * this._numberOfPointsDone) / ((float) this._numberOfPointsNeeded));
		layout.IncreasePercentageComplete((int) num);

	}

	public void SetPointEstimation(Integer numberOfPointsNeeded) {
		this._numberOfPointsNeeded = numberOfPointsNeeded;

	}

	public void SetStepEstimation(Integer numberOfStepsNeeded) {
		if (numberOfStepsNeeded > 0) {
			this._percentageOfPreviousSteps *= ((float) this._numberOfStepsNeeded)
					/ ((float) numberOfStepsNeeded);
			this._numberOfStepsNeeded = numberOfStepsNeeded;
		}

	}

	public void StartFinalStep(float percentageOfStep, Integer pointsOfStep) {
		this._numberOfStepsDone++;
		this._percentageOfPreviousSteps += this._percentageOfCurrentStepFromAll;
		this._percentageOfCurrentStepFromAll = percentageOfStep;
		this.SetPointEstimation(pointsOfStep);
		this._numberOfPointsDone = 0;

	}

	public void StartStep(float percentageOfStep, Integer pointsOfStep) {
		this._numberOfStepsDone++;
		this._percentageOfPreviousSteps += this._percentageOfCurrentStepFromAll;
		if (this._numberOfStepsDone >= this._numberOfStepsNeeded) {
			this.SetStepEstimation(this._numberOfStepsNeeded + 1);
		}
		this._percentageOfCurrentStepFromAll = percentageOfStep;
		this.SetPointEstimation(pointsOfStep);
		this._numberOfPointsNeeded = pointsOfStep;
		this._numberOfPointsDone = 0;

	}

	public void StopAll() {
		this._numberOfStepsDone++;
		this._percentageOfPreviousSteps += this._percentageOfCurrentStepFromAll;

	}

}