package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;
import system.ComponentModel.*;

public class MultipleLayout extends ILOG.Diagrammer.GraphLayout.GraphLayout {
	private Integer _actLayout;

	private Integer _basePercentage;

	public Boolean _defaultLayoutActive = true;

	private Boolean _firstGraphLayoutActive = false;

	private Boolean _firstGraphLayoutPersistent = false;

	private ILOG.Diagrammer.GraphLayout.GraphLayout _firstLayout;

	private Integer _numStepsInSublayout;

	private float _numSubLayouts;

	private Boolean _secondGraphLayoutActive = false;

	private Boolean _secondGraphLayoutPersistent = false;

	private ILOG.Diagrammer.GraphLayout.GraphLayout _secondLayout;

	private Boolean _silentAttach = false;

	private Boolean _usePercentageCompletionSubstitute = false;

	public MultipleLayout() {
	}

	public MultipleLayout(MultipleLayout source) {
		super(source);
	}

	public MultipleLayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout1,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout2) {
		this.set_FirstGraphLayout(layout1);
		this.set_SecondGraphLayout(layout2);
	}

	@Override
	public void Attach(IGraphModel graphModel) {
		super.Attach(graphModel);
		if (this._firstLayout != null) {
			this._firstLayout.Attach(graphModel);
		}
		if (this._secondLayout != null) {
			this._secondLayout.Attach(graphModel);
		}

	}

	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout Copy() {

		return new MultipleLayout(this);

	}

	@Override
	public void CopyParameters(ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		super.CopyParameters(source);
		if (source instanceof MultipleLayout) {
			MultipleLayout layout = (MultipleLayout) source;
			this._silentAttach = layout._silentAttach;
			ILOG.Diagrammer.GraphLayout.GraphLayout firstGraphLayout = layout
					.get_FirstGraphLayout();
			ILOG.Diagrammer.GraphLayout.GraphLayout secondGraphLayout = this
					.get_FirstGraphLayout();
			if (firstGraphLayout != secondGraphLayout) {
				if (firstGraphLayout == null) {
					this.set_FirstGraphLayout(null);
				} else if (secondGraphLayout == null) {
					this.set_FirstGraphLayout(firstGraphLayout.Copy());
				} else if (firstGraphLayout.getClass().getName()
						.equals(secondGraphLayout.getClass().getName())) {
					secondGraphLayout.CopyParameters(firstGraphLayout);
				} else {
					IGraphModel graphModel = secondGraphLayout.GetGraphModel();
					this.set_FirstGraphLayout(firstGraphLayout.Copy());
					if (graphModel != null) {
						this.get_FirstGraphLayout().Attach(graphModel);
					}
				}
			}
			firstGraphLayout = layout.get_SecondGraphLayout();
			secondGraphLayout = this.get_SecondGraphLayout();
			if (firstGraphLayout != secondGraphLayout) {
				if (firstGraphLayout == null) {
					this.set_SecondGraphLayout(null);
				} else if (secondGraphLayout == null) {
					this.set_SecondGraphLayout(firstGraphLayout.Copy());
				} else if (firstGraphLayout.getClass().getName()
						.equals(secondGraphLayout.getClass().getName())) {
					secondGraphLayout.CopyParameters(firstGraphLayout);
				} else {
					IGraphModel model2 = secondGraphLayout.GetGraphModel();
					this.set_SecondGraphLayout(firstGraphLayout.Copy());
					if (model2 != null) {
						this.get_SecondGraphLayout().Attach(model2);
					}
				}
			}
			this.set_FirstGraphLayoutActive(layout.get_FirstGraphLayoutActive());
			this.set_SecondGraphLayoutActive(layout
					.get_SecondGraphLayoutActive());
			this.set_FirstGraphLayoutPersistent(layout
					.get_FirstGraphLayoutPersistent());
			this.set_SecondGraphLayoutPersistent(layout
					.get_SecondGraphLayoutPersistent());
		}

	}

	@Override
	public GraphLayoutReport CreateLayoutReport() {

		return new MultipleLayoutReport();

	}

	@Override
	public void Detach() {
		if (this.GetGraphModel() != null) {
			if (this._firstLayout != null) {
				this._firstLayout.Detach();
			}
			if (this._secondLayout != null) {
				this._secondLayout.Detach();
			}
		}
		super.Detach();

	}

	@Override
	public void Init() {
		super.Init();
		this._firstGraphLayoutPersistent = true;
		this._secondGraphLayoutPersistent = true;
		this._firstGraphLayoutActive = true;
		this._secondGraphLayoutActive = true;
		this._silentAttach = false;

	}

	@Override
	public void Layout() {
		Integer code = 0;
		MultipleLayoutReport layoutReport = (MultipleLayoutReport) super
				.GetLayoutReport();
		layoutReport._firstLayoutReport = null;
		layoutReport._secondLayoutReport = null;
		long num2 = TranslateUtil.CurrentTimeMillis();
		long allowedTime = 0L;
		this._basePercentage = 0;
		this._numSubLayouts = 0f;
		if (this._firstLayout != null) {
			this._numSubLayouts++;
		}
		if (this._secondLayout != null) {
			this._numSubLayouts++;
		}
		this._actLayout = 0;
		this.UnregisterGraphModelListeners();
		try {
			Boolean parametersUpToDate = null;
			if ((this._firstLayout != null)
					&& this.get_FirstGraphLayoutActive()) {
				parametersUpToDate = this._firstLayout.get_ParametersUpToDate();
				try {

					if (this.SupportsAllowedTime()) {
						allowedTime = this._firstLayout.get_AllowedTime();
						this._firstLayout.set_AllowedTime(this
								.get_AllowedTime());
					}
					// this._firstLayout.LayoutStepPerformed +=
					// this.OnSubGraphLayoutStepPerformed;

					this._usePercentageCompletionSubstitute = !this._firstLayout
							.SupportsPercentageComplete();
					this._numStepsInSublayout = 0;
				} finally {
					this._firstLayout
							.set_ParametersUpToDate(parametersUpToDate);
				}
				try {

					layoutReport._firstLayoutReport = this._firstLayout
							.PerformLayout(true);
				} finally {
					parametersUpToDate = this._firstLayout
							.get_ParametersUpToDate();
					try {

						if (this.SupportsAllowedTime()) {
							this._firstLayout.set_AllowedTime(allowedTime);
						}
						// this._firstLayout.LayoutStepPerformed -=
						// this.OnSubGraphLayoutStepPerformed;
					} finally {
						this._firstLayout
								.set_ParametersUpToDate(parametersUpToDate);
					}
				}
				this._actLayout++;
				this._basePercentage = (int) ((this._actLayout * 100f) / this._numSubLayouts);
				this.IncreasePercentageComplete(this._basePercentage);
				if (code < (Integer) layoutReport._firstLayoutReport.get_Code()) {
					code = (Integer) layoutReport._firstLayoutReport.get_Code();
				}
			}
			if (((this._secondLayout != null) && this
					.get_SecondGraphLayoutActive()) && this.MayContinue()) {
				parametersUpToDate = this._secondLayout
						.get_ParametersUpToDate();
				try {

					if (this.SupportsAllowedTime()) {
						allowedTime = this._secondLayout.get_AllowedTime();
						long num4 = TranslateUtil.CurrentTimeMillis() - num2;
						this._secondLayout.set_AllowedTime((long) Math.Max(
								(long) (this.get_AllowedTime() - num4),
								(long) 1L));
					}
					// this._secondLayout.LayoutStepPerformed +=
					// this.OnSubGraphLayoutStepPerformed;

					this._usePercentageCompletionSubstitute = !this._secondLayout
							.SupportsPercentageComplete();
					this._numStepsInSublayout = 0;
				} finally {
					this._secondLayout
							.set_ParametersUpToDate(parametersUpToDate);
				}
				try {

					layoutReport._secondLayoutReport = this._secondLayout
							.PerformLayout(true);
				} finally {
					parametersUpToDate = this._secondLayout
							.get_ParametersUpToDate();
					try {

						if (this.SupportsAllowedTime()) {
							this._secondLayout.set_AllowedTime(allowedTime);
						}
						// this._secondLayout.LayoutStepPerformed -=
						// this.OnSubGraphLayoutStepPerformed;
					} finally {
						this._secondLayout
								.set_ParametersUpToDate(parametersUpToDate);
					}
				}
				this._actLayout++;
				this._basePercentage = (int) ((this._actLayout * 100f) / this._numSubLayouts);
				this.IncreasePercentageComplete(this._basePercentage);
				if (code < (Integer) layoutReport._secondLayoutReport
						.get_Code()) {
					code = (Integer) layoutReport._secondLayoutReport
							.get_Code();
				}
			}

			if (!this.MayContinue() && (code < 7)) {
				code = 7;
			}
			layoutReport.set_Code((int) code);
		} finally {
			this.RegisterGraphModelListeners();
		}

	}

	private Boolean MayContinue() {

		return (!this.IsLayoutTimeElapsed() && !this.IsStoppedImmediately());

	}

	public void OnSubGraphLayoutStepPerformed(java.lang.Object source,
			GraphLayoutStepPerformedEventArgs e) {
		this.UpdateLayoutStepData(e);
		this.OnLayoutStepPerformed(false, false);

	}

	private void RegisterGraphModelListeners() {
		if (this._firstLayout != null) {
			this._firstLayout.AddToModelEventHandlers(this._firstLayout
					.GetGraphModel());
		}
		if (this._secondLayout != null) {
			this._secondLayout.AddToModelEventHandlers(this._secondLayout
					.GetGraphModel());
		}

	}

	private void ResetFirstGraphLayout() {
		this.set_FirstGraphLayout(null);

	}

	private void ResetSecondGraphLayout() {
		this.set_SecondGraphLayout(null);

	}

	@Override
	public void SetGraphModel(IGraphModel graphModel) {
		super.SetGraphModel(graphModel);
		if (this._firstLayout != null) {
			this._firstLayout.SetGraphModel(graphModel);
		}
		if (this._secondLayout != null) {
			this._secondLayout.SetGraphModel(graphModel);
		}

	}

	public void SetSilentAttach(Boolean flag) {
		this._silentAttach = flag;

	}

	private Boolean ShouldSerializeFirstGraphLayout() {

		return this.get_FirstGraphLayoutPersistent();

	}

	private Boolean ShouldSerializeSecondGraphLayout() {

		return this.get_SecondGraphLayoutPersistent();

	}

	@Override
	public Boolean StopImmediately() {
		Boolean flag = super.StopImmediately();
		if ((this._firstLayout != null) && this._firstLayout.IsLayoutRunning()) {

			flag &= this._firstLayout.StopImmediately();
		}
		if ((this._secondLayout != null)
				&& this._secondLayout.IsLayoutRunning()) {

			flag &= this._secondLayout.StopImmediately();
		}

		return flag;

	}

	@Override
	public Boolean SupportsAllowedTime() {

		if ((this._firstLayout != null)
				&& !this._firstLayout.SupportsAllowedTime()) {

			return false;
		}

		if ((this._secondLayout != null)
				&& !this._secondLayout.SupportsAllowedTime()) {

			return false;
		}

		return true;

	}

	@Override
	public Boolean SupportsLayoutOfConnectedComponents() {

		if ((this._firstLayout != null)
				&& !this._firstLayout.SupportsLayoutOfConnectedComponents()) {

			return false;
		}

		if ((this._secondLayout != null)
				&& !this._secondLayout.SupportsLayoutOfConnectedComponents()) {

			return false;
		}
		if ((this._firstLayout == null) && (this._secondLayout == null)) {

			return false;
		}

		return true;

	}

	@Override
	public Boolean SupportsPercentageComplete() {

		return true;

	}

	@Override
	public Boolean SupportsStopImmediately() {

		if ((this._firstLayout != null)
				&& !this._firstLayout.SupportsStopImmediately()) {

			return false;
		}

		if ((this._secondLayout != null)
				&& !this._secondLayout.SupportsStopImmediately()) {

			return false;
		}

		return true;

	}

	private void UnregisterGraphModelListeners() {
		if (this._firstLayout != null) {
			this._firstLayout.RemoveFromModelEventHandlers(this._firstLayout
					.GetGraphModel());
		}
		if (this._secondLayout != null) {
			this._secondLayout.RemoveFromModelEventHandlers(this._secondLayout
					.GetGraphModel());
		}

	}

	private void UpdateLayoutStepData(GraphLayoutStepPerformedEventArgs e) {
		float percentageComplete = 0;
		this._numStepsInSublayout++;
		if (this._usePercentageCompletionSubstitute) {
			percentageComplete = (1.010101f * this._numStepsInSublayout)
					/ (1f + (((float) this._numStepsInSublayout) / 99f));
		} else {
			percentageComplete = e.get_LayoutReport().get_PercentageComplete();
		}
		this.IncreasePercentageComplete(this._basePercentage
				+ ((int) (percentageComplete / this._numSubLayouts)));
		MultipleLayoutReport layoutReport = (MultipleLayoutReport) super
				.GetLayoutReport();
		if (layoutReport != null) {
			if (this._actLayout == 0) {
				layoutReport._firstLayoutReport = e.get_LayoutReport();
			} else if (this._actLayout == 1) {
				layoutReport._secondLayoutReport = e.get_LayoutReport();
			}
		}

	}

	public long get_AllowedTime() {

		return super.get_AllowedTime();
	}

	public void set_AllowedTime(long value) {
		super.set_AllowedTime(value);
	}

	public Boolean get_AutoLayout() {

		return super.get_AutoLayout();
	}

	public void set_AutoLayout(Boolean value) {
		Boolean flag = value;
		if (flag != this.get_AutoLayout()) {
			super.set_AutoLayout(flag);
			if (this._firstLayout != null) {
				this._firstLayout.set_AutoLayout(false);
			}
			if (this._secondLayout != null) {
				this._secondLayout.set_AutoLayout(false);
			}
		}
	}

	public void set_CoordinatesMode(
			ILOG.Diagrammer.GraphLayout.CoordinatesMode value) {
		super.set_CoordinatesMode(value);
		if (this._firstLayout != null) {
			this._firstLayout.set_CoordinatesMode(value);
		}
		if (this._secondLayout != null) {
			this._secondLayout.set_CoordinatesMode(value);
		}
	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout get_FirstGraphLayout() {

		return this._firstLayout;
	}

	public void set_FirstGraphLayout(
			ILOG.Diagrammer.GraphLayout.GraphLayout value) {
		if (this._firstLayout != value) {
			if (this._firstLayout != null) {
				this._firstLayout.Detach();
			}
			this._firstLayout = value;
			this.OnParameterChanged("FirstGraphLayout");
		}
	}

	public Boolean get_FirstGraphLayoutActive() {

		return this._firstGraphLayoutActive;
	}

	public void set_FirstGraphLayoutActive(Boolean value) {
		if (this._firstGraphLayoutActive != value) {
			this._firstGraphLayoutActive = value;
			this.OnParameterChanged("FirstGraphLayoutActive");
		}
	}

	public Boolean get_FirstGraphLayoutPersistent() {

		return this._firstGraphLayoutPersistent;
	}

	public void set_FirstGraphLayoutPersistent(Boolean value) {
		if (this._firstGraphLayoutPersistent != value) {
			this._firstGraphLayoutPersistent = value;
			this.OnParameterChanged("FirstGraphLayoutPersistent");
		}
	}

	public Boolean get_GeometryUpToDate() {
		if ((this._firstLayout != null)
				&& !this._firstLayout.get_GeometryUpToDate()) {

			return false;
		}
		if ((this._secondLayout != null)
				&& !this._secondLayout.get_GeometryUpToDate()) {

			return false;
		}

		return super.get_GeometryUpToDate();
	}

	public void set_GeometryUpToDate(Boolean value) {
		Boolean flag = value;
		if (flag) {
			if (this._firstLayout != null) {
				this._firstLayout.set_GeometryUpToDate(true);
			}
			if (this._secondLayout != null) {
				this._secondLayout.set_GeometryUpToDate(true);
			}
		}
		super.set_GeometryUpToDate(flag);
	}

	public Boolean get_InputCheckEnabled() {

		return super.get_InputCheckEnabled();
	}

	public void set_InputCheckEnabled(Boolean value) {
		Boolean flag = value;
		super.set_InputCheckEnabled(flag);
		if (this._firstLayout != null) {
			this._firstLayout.set_InputCheckEnabled(flag);
		}
		if (this._secondLayout != null) {
			this._secondLayout.set_InputCheckEnabled(flag);
		}
	}

	public Boolean get_LayoutOfConnectedComponentsEnabled() {

		return super.get_LayoutOfConnectedComponentsEnabled();
	}

	public void set_LayoutOfConnectedComponentsEnabled(Boolean value) {
		super.set_LayoutOfConnectedComponentsEnabled(value);
	}

	public long get_MinBusyTime() {

		return super.get_MinBusyTime();
	}

	public void set_MinBusyTime(long value) {
		long num = value;
		super.set_MinBusyTime(num);
		if (this._firstLayout != null) {
			this._firstLayout.set_MinBusyTime(num);
		}
		if (this._secondLayout != null) {
			this._secondLayout.set_MinBusyTime(num);
		}
	}

	public Boolean get_ParametersUpToDate() {
		if ((this._firstLayout != null)
				&& !this._firstLayout.get_ParametersUpToDate()) {

			return false;
		}
		if ((this._secondLayout != null)
				&& !this._secondLayout.get_ParametersUpToDate()) {

			return false;
		}

		return super.get_ParametersUpToDate();
	}

	public void set_ParametersUpToDate(Boolean value) {
		Boolean flag = value;
		if (flag) {
			if (this._firstLayout != null) {
				this._firstLayout.set_ParametersUpToDate(true);
			}
			if (this._secondLayout != null) {
				this._secondLayout.set_ParametersUpToDate(true);
			}
		}
		super.set_ParametersUpToDate(flag);
	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout get_SecondGraphLayout() {

		return this._secondLayout;
	}

	public void set_SecondGraphLayout(
			ILOG.Diagrammer.GraphLayout.GraphLayout value) {
		if (this._secondLayout != value) {
			if (this._secondLayout != null) {
				this._secondLayout.Detach();
			}
			this._secondLayout = value;
			this.OnParameterChanged("SecondGraphLayout");
		}
	}

	public Boolean get_SecondGraphLayoutActive() {

		return this._secondGraphLayoutActive;
	}

	public void set_SecondGraphLayoutActive(Boolean value) {
		if (this._secondGraphLayoutActive != value) {
			this._secondGraphLayoutActive = value;
			this.OnParameterChanged("SecondGraphLayoutActive");
		}
	}

	public Boolean get_SecondGraphLayoutPersistent() {

		return this._secondGraphLayoutPersistent;
	}

	public void set_SecondGraphLayoutPersistent(Boolean value) {
		if (this._secondGraphLayoutPersistent != value) {
			this._secondGraphLayoutPersistent = value;
			this.OnParameterChanged("SecondGraphLayoutPersistent");
		}
	}

	public Boolean get_StructureUpToDate() {
		if ((this._firstLayout != null)
				&& !this._firstLayout.get_StructureUpToDate()) {

			return false;
		}
		if ((this._secondLayout != null)
				&& !this._secondLayout.get_StructureUpToDate()) {

			return false;
		}

		return super.get_StructureUpToDate();
	}

	public void set_StructureUpToDate(Boolean value) {
		Boolean flag = value;
		if (flag) {
			if (this._firstLayout != null) {
				this._firstLayout.set_StructureUpToDate(true);
			}
			if (this._secondLayout != null) {
				this._secondLayout.set_StructureUpToDate(true);
			}
		}
		super.set_StructureUpToDate(flag);
	}

}