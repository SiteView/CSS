package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.ArgumentException;
import system.Collections.ArrayList;

public abstract class CombinatorialOptimization {
	public ArrayList _variables;

	public Integer[] _variants;

	public CombinatorialOptimization(ArrayList variables) {
		if (variables == null) {
			throw (new ArgumentException("variables cannot be null"));
		}
		this._variables = variables;
		this._variants = new Integer[this._variables.get_Count()];
	}

	public abstract Boolean ApplyVariant(java.lang.Object variable,
			Integer variableIndex, Integer variant);

	public abstract float GetCost(java.lang.Object variable);

	public Integer GetVariant(Integer indexVariable) {

		return this._variants[indexVariable];

	}

	public abstract Integer GetVariantsCount(java.lang.Object variable);

	public Boolean IsBetterVariant(java.lang.Object variable, Integer variant1,
			float cost1, Integer variant2, float cost2) {

		return (cost1 < cost2);

	}

	public Boolean IsOptimalCost(float cost) {

		return (cost <= 0f);

	}

	public abstract void Optimize();

	public void SetVariant(Integer indexVariable, Integer variant) {
		this._variants[indexVariable] = variant;

	}

}