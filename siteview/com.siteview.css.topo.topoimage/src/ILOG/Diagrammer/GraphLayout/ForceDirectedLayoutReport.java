package ILOG.Diagrammer.GraphLayout;

import system.*;
import system.ComponentModel.*;

public class ForceDirectedLayoutReport extends GraphLayoutReport {
	private float _maxMovePerIteration;

	private Integer _numberOfIterations;

	public ForceDirectedLayoutReport() {
	}

	public Integer GetNumberOfIterations() {

		return this._numberOfIterations;

	}

	public void SetMaxMovePerIteration(float maxMove) {
		this._maxMovePerIteration = maxMove;

	}

	public void SetNumberOfIterations(Integer number) {
		this._numberOfIterations = number;

	}

	public float get_MaxMovePerIteration() {

		return this._maxMovePerIteration;
	}

}