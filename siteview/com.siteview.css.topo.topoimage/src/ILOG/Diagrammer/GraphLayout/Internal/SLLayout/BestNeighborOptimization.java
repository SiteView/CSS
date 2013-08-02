package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.Collections.ArrayList;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;

public abstract class BestNeighborOptimization extends
		CombinatorialOptimization {
	private Integer _allowedNumberOfIterations;

	private long _allowedTime;

	private long _startTime;

	private Boolean _stoppedBeforeCompletion = false;

	public BestNeighborOptimization(ArrayList variables,
			Integer allowedNumberOfIterations, long allowedTime) {
		super(variables);
		this._allowedNumberOfIterations = allowedNumberOfIterations;
		this._allowedTime = allowedTime;
	}

	@Override
	public abstract Boolean ApplyVariant(java.lang.Object arg0, Integer arg1,
			Integer arg2);

	@Override
	public abstract float GetCost(java.lang.Object arg0);

	@Override
	public abstract Integer GetVariantsCount(java.lang.Object arg0);

	public Boolean IsStoppedBeforeCompletion() {

		return this._stoppedBeforeCompletion;

	}

	public Boolean MayContinue(Integer currentNumberOfIterations) {
		if (this._stoppedBeforeCompletion) {

			return false;
		}
		Boolean flag = (TranslateUtil.CurrentTimeMillis() - this._startTime) <= this._allowedTime;
		if (!flag) {
			this._stoppedBeforeCompletion = true;
		}

		return (flag && (currentNumberOfIterations < this._allowedNumberOfIterations));

	}

	@Override
	public void Optimize() {
		Integer count = super._variables.get_Count();
		if (count >= 1) {
			Integer currentNumberOfIterations = 0;
			Boolean flag = false;

			this._startTime = TranslateUtil.CurrentTimeMillis();
			this._stoppedBeforeCompletion = false;

			while (this.MayContinue(currentNumberOfIterations)) {
				for (Integer i = 0; i < count; i++) {
					java.lang.Object variable = super._variables.get_Item(i);
					Integer variantsCount = this.GetVariantsCount(variable);
					if (variantsCount >= 1) {
						Integer variant = 0;
						Integer num6 = variant;
						Integer num4 = 0;
						float maxValue = Float.MAX_VALUE;
						Integer num9 = variant;
						Integer num10 = -1;
						while (num4 < variantsCount) {
							this.ApplyVariant(variable, i, variant);
							float cost = this.GetCost(variable);
							num10 = variant;
							num4++;
							if ((cost >= 0f)
									&& this.IsBetterVariant(variable, variant,
											cost, num9, maxValue)) {
								num9 = variant;
								maxValue = cost;

								if (super.IsOptimalCost(cost)) {
									break;
								}
							}
							variant++;
							if (variant >= variantsCount) {
								variant = 0;
							}
						}
						if (num9 != num10) {
							this.ApplyVariant(variable, i, num9);
						}
						if (num9 != num6) {
							flag = true;
						}
						this.VariantStepPerformed();
					}
				}
				if (!flag) {

					return;
				}
				flag = false;
				if (count == 1) {

					return;
				}
				currentNumberOfIterations++;
			}
		}

	}

	public void StopImmediately() {
		this._stoppedBeforeCompletion = true;

	}

	public void VariantStepPerformed() {

	}

}