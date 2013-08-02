package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.ComponentModel.*;

public class RecursiveMultipleLayout extends RecursiveLayout {
	private int _firstLayoutActive;

	private Boolean _oldFirstLayoutActive = false;

	private Boolean _oldSecondLayoutActive = false;

	private Integer _secondLayoutActive;

	private Integer AMASK = 3;

	private Integer SHIFT_FOR_AUTOLAYOUT = 2;

	public RecursiveMultipleLayout() {
		this.SetLayoutMode(RecursiveLayoutMode.InternalProviderMode, null, null);
	}

	public RecursiveMultipleLayout(ILayoutProvider layoutProvider) {
		super(layoutProvider);
	}

	public RecursiveMultipleLayout(RecursiveMultipleLayout source) {
		super((RecursiveLayout) source);
	}

	public RecursiveMultipleLayout(
			ILOG.Diagrammer.GraphLayout.GraphLayout referenceLayout1,
			ILOG.Diagrammer.GraphLayout.GraphLayout referenceLayout2) {
		super(new MultipleLayout(referenceLayout1, referenceLayout2));
		((MultipleLayout) this.GetReferenceLayout()).SetSilentAttach(true);
	}

	@Override
	public void AfterSublayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		// super.AfterSublayout(layout);
		// if(layout instanceof MultipleLayout){
		// MultipleLayout layout2 = (MultipleLayout)layout;
		// if(layout2.get_FirstGraphLayout() != null){
		// layout2.get_FirstGraphLayout().LayoutStepPerformed -=
		// this.OnSubLayoutStepPerformed;
		// }
		// if(layout2.get_SecondGraphLayout() != null){
		// layout2.get_SecondGraphLayout().LayoutStepPerformed -=
		// this.OnSubLayoutStepPerformed;
		// }
		// Boolean parametersUpToDate = layout.get_ParametersUpToDate();
		// try{
		// layout2.set_FirstGraphLayoutActive(this._oldFirstLayoutActive);
		// layout2.set_SecondGraphLayoutActive(this._oldSecondLayoutActive);
		// }
		// finally{
		// layout.set_ParametersUpToDate(parametersUpToDate);
		// }
		// }

	}

	@Override
	public void BeforeSublayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		// super.BeforeSublayout(layout);
		// if(layout instanceof MultipleLayout){
		// MultipleLayout layout2 = (MultipleLayout)layout;
		// if(layout2.get_FirstGraphLayout() != null){
		// layout2.get_FirstGraphLayout().LayoutStepPerformed +=
		// this.OnSubLayoutStepPerformed;
		// }
		// if(layout2.get_SecondGraphLayout() != null){
		// layout2.get_SecondGraphLayout().LayoutStepPerformed +=
		// this.OnSubLayoutStepPerformed;
		// }
		// this._oldFirstLayoutActive = layout2.get_FirstGraphLayoutActive();
		// this._oldSecondLayoutActive = layout2.get_SecondGraphLayoutActive();
		// Boolean parametersUpToDate = layout.get_ParametersUpToDate();
		// try{
		// if(super._duringAutoLayout){
		// if(this.get_FirstGraphLayoutActiveDuringAutoLayout() ==
		// RecursiveMultipleLayoutActivation.Active){
		// layout2.set_FirstGraphLayoutActive(true);
		// //NOTICE: break ignore!!!
		// }
		// else if(this.get_FirstGraphLayoutActiveDuringAutoLayout() ==
		// RecursiveMultipleLayoutActivation.Inactive){
		// layout2.set_FirstGraphLayoutActive(false);
		// //NOTICE: break ignore!!!
		// }
		// if(this.get_SecondGraphLayoutActiveDuringAutoLayout() ==
		// RecursiveMultipleLayoutActivation.Active){
		// layout2.set_SecondGraphLayoutActive(true);
		//
		// return ;
		// }
		// else if(this.get_SecondGraphLayoutActiveDuringAutoLayout() ==
		// RecursiveMultipleLayoutActivation.Inactive){
		// layout2.set_SecondGraphLayoutActive(false);
		//
		// return ;
		// }
		// else {
		//
		// return ;
		// }
		// }
		// if(this.get_FirstGraphLayoutActive() ==
		// RecursiveMultipleLayoutActivation.Active){
		// layout2.set_FirstGraphLayoutActive(true);
		// //NOTICE: break ignore!!!
		// }
		// else if(this.get_FirstGraphLayoutActive() ==
		// RecursiveMultipleLayoutActivation.Inactive){
		// layout2.set_FirstGraphLayoutActive(false);
		// //NOTICE: break ignore!!!
		// }
		// if(this.get_SecondGraphLayoutActive() ==
		// RecursiveMultipleLayoutActivation.Active){
		// layout2.set_SecondGraphLayoutActive(true);
		//
		// return ;
		// }
		// else if(this.get_SecondGraphLayoutActive() ==
		// RecursiveMultipleLayoutActivation.Inactive){
		// layout2.set_SecondGraphLayoutActive(false);
		//
		// return ;
		// }
		// }
		// finally{
		// layout.set_ParametersUpToDate(parametersUpToDate);
		// }
		// }

	}

	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout Copy() {

		return new RecursiveMultipleLayout(this);

	}

	@Override
	public void CopyParameters(ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		super.CopyParameters(source);
		if (source instanceof RecursiveMultipleLayout) {
			RecursiveMultipleLayout layout = (RecursiveMultipleLayout) source;
			this._firstLayoutActive = layout._firstLayoutActive;
			this._secondLayoutActive = layout._secondLayoutActive;
		}

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout GetFirstGraphLayout(
			java.lang.Object subgraph) {
		MultipleLayout layout = (MultipleLayout) this.GetLayout(subgraph);
		if (layout != null) {

			return layout.get_FirstGraphLayout();
		}

		return null;

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout GetFirstReferenceGraphLayout() {
		MultipleLayout referenceLayout = (MultipleLayout) this
				.GetReferenceLayout();
		if (referenceLayout != null) {

			return referenceLayout.get_FirstGraphLayout();
		}

		return null;

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout GetSecondGraphLayout(
			java.lang.Object subgraph) {
		MultipleLayout layout = (MultipleLayout) this.GetLayout(subgraph);
		if (layout != null) {

			return layout.get_SecondGraphLayout();
		}

		return null;

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout GetSecondReferenceGraphLayout() {
		MultipleLayout referenceLayout = (MultipleLayout) this
				.GetReferenceLayout();
		if (referenceLayout != null) {

			return referenceLayout.get_SecondGraphLayout();
		}

		return null;

	}

	@Override
	public void Init() {
		super.Init();
		this._firstLayoutActive = 5;
		this._secondLayoutActive = 5;

	}

	@Override
	public void OnSubLayoutStepPerformed(java.lang.Object source,
			GraphLayoutStepPerformedEventArgs e) {
		ILOG.Diagrammer.GraphLayout.GraphLayout currentlyRunningLayout = null;
		RecursiveLayoutReport layoutReport = (RecursiveLayoutReport) super
				.GetLayoutReport();
		if (layoutReport != null) {
			currentlyRunningLayout = layoutReport.get_CurrentlyRunningLayout();
		}
		if (e.get_GraphLayout() == currentlyRunningLayout) {
			this.UpdatePercentageComplete(e.get_LayoutReport()
					.get_PercentageComplete());
			this.OnLayoutStepPerformed(false, false);
		} else {
			this.OnLayoutStepPerformedAtSublayouts(e);
		}

	}

	public void PropagateFirstGraphLayoutActive(Boolean active) {
		MultipleLayout referenceLayout = null;
		if (this.GetLayoutMode() == RecursiveLayoutMode.ReferenceLayoutMode) {
			referenceLayout = (MultipleLayout) this.GetReferenceLayout();
			referenceLayout.set_FirstGraphLayoutActive(active);
		} else {
			if (this.GetGraphModel() == null) {
				throw (new system.Exception(
						"A graph model must be attached when you use the method "
								+ LayoutUtil.GetText(
										"propagateFirstGraphLayoutActive",
										"PropagateFirstGraphLayoutActive")));
			}
			IJavaStyleEnumerator layoutsImpl = super.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				referenceLayout = (MultipleLayout) layoutsImpl.NextElement();
				if (referenceLayout != null) {
					referenceLayout.set_FirstGraphLayoutActive(active);
				}
			}
		}

	}

	public void PropagateSecondGraphLayoutActive(Boolean active) {
		MultipleLayout referenceLayout = null;
		if (this.GetLayoutMode() == RecursiveLayoutMode.ReferenceLayoutMode) {
			referenceLayout = (MultipleLayout) this.GetReferenceLayout();
			referenceLayout.set_SecondGraphLayoutActive(active);
		} else {
			if (this.GetGraphModel() == null) {
				throw (new system.Exception(
						"A graph model must be attached when you use the method "
								+ LayoutUtil.GetText(
										"propagateSecondGraphLayoutActive",
										"PropagateSecondGraphLayoutActive")));
			}
			IJavaStyleEnumerator layoutsImpl = super.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				referenceLayout = (MultipleLayout) layoutsImpl.NextElement();
				if (referenceLayout != null) {
					referenceLayout.set_SecondGraphLayoutActive(active);
				}
			}
		}

	}

	@Override
	public void SetLayout(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if ((layout != null) && !(layout instanceof MultipleLayout)) {
			throw (new system.Exception(
					"An instance of IlvMultipleLayout is expected at "
							+ LayoutUtil
									.GetText(
											"IlvRecursiveMultipleLayout.setLayout(Object, IlvGraphLayout)",
											"RecursiveMultipleLayout.SetLayout(object, GraphLayout)")));
		}
		MultipleLayout layout2 = (MultipleLayout) layout;
		if (layout2 != null) {
			layout2.SetSilentAttach(true);
		}
		super.SetLayout(subgraph, layout);

	}

	public void SetLayout(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout1,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout2) {
		MultipleLayout layout = new MultipleLayout(layout1, layout2);
		layout.SetSilentAttach(true);
		super.SetLayout(subgraph, layout);

	}

	@Override
	public void SetLayout(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			Boolean detachPrevious, Boolean traverse) {
		if ((layout != null) && !(layout instanceof MultipleLayout)) {
			throw (new system.Exception(
					"An instance of IlvMultipleLayout is expected at "
							+ LayoutUtil
									.GetText(
											"IlvRecursiveMultipleLayout.setLayout(Object, IlvGraphLayout,boolean, boolean)",
											"RecursiveMultipleLayout.setLayout(object, GraphLayout,bool, bool)")));
		}
		MultipleLayout layout2 = (MultipleLayout) layout;
		if (layout2 != null) {
			layout2.SetSilentAttach(true);
		}
		super.SetLayout(subgraph, layout, detachPrevious, traverse);

	}

	public int get_FirstGraphLayoutActive() {

		return (((int) this._firstLayoutActive) & (RecursiveMultipleLayoutActivation.Inactive | RecursiveMultipleLayoutActivation.Active));
	}

	public void set_FirstGraphLayoutActive(int value) {
		if (((value != RecursiveMultipleLayoutActivation.Active) && (value != RecursiveMultipleLayoutActivation.Inactive))
				&& (value != RecursiveMultipleLayoutActivation.Mixed)) {
			throw (new ArgumentException("Illegal value "
					+ ((Integer) value)
					+ LayoutUtil.GetText(
							" for method setFirstGraphLayoutActive",
							" for FirstGraphLayoutActive property")));
		}
		if (value != this.get_FirstGraphLayoutActive()) {
			this._firstLayoutActive &= -4;
			this._firstLayoutActive |= (Integer) value;
			this.OnParameterChanged("FirstGraphLayoutActive");
		}
	}

	public int get_FirstGraphLayoutActiveDuringAutoLayout() {

		return (int) ((this._firstLayoutActive & 12) >> 2);
	}

	public void set_FirstGraphLayoutActiveDuringAutoLayout(int value) {
		if (((value != RecursiveMultipleLayoutActivation.Active) && (value != RecursiveMultipleLayoutActivation.Inactive))
				&& (value != RecursiveMultipleLayoutActivation.Mixed)) {
			throw (new ArgumentException(
					"Illegal value "
							+ ((Integer) value)
							+ LayoutUtil
									.GetText(
											" for method setFirstGraphLayoutActiveDuringAutoLayout",
											" for FirstGraphLayoutActiveDuringAutoLayout property")));
		}
		if (value != this.get_FirstGraphLayoutActiveDuringAutoLayout()) {
			this._firstLayoutActive &= -13;
			this._firstLayoutActive |= ((Integer) value) << 2;
			this.OnParameterChanged("FirstGraphLayoutActiveDuringAutoLayout");
		}
	}

	public int get_SecondGraphLayoutActive() {

		return (((int) this._secondLayoutActive) & (RecursiveMultipleLayoutActivation.Inactive | RecursiveMultipleLayoutActivation.Active));
	}

	public void set_SecondGraphLayoutActive(int value) {
		if (((value != RecursiveMultipleLayoutActivation.Active) && (value != RecursiveMultipleLayoutActivation.Inactive))
				&& (value != RecursiveMultipleLayoutActivation.Mixed)) {
			throw (new ArgumentException("Illegal value "
					+ ((int) value)
					+ LayoutUtil.GetText(
							" for method setSecondGraphLayoutActive",
							" for SecondGraphLayoutActive property")));
		}
		if (value != this.get_SecondGraphLayoutActive()) {
			this._secondLayoutActive &= -4;
			this._secondLayoutActive |= (Integer) value;
			this.OnParameterChanged("SecondGraphLayoutActive");
		}
	}

	public int get_SecondGraphLayoutActiveDuringAutoLayout() {

		return (int) ((this._secondLayoutActive & 12) >> 2);
	}

	public void set_SecondGraphLayoutActiveDuringAutoLayout(int value) {
		if (((value != RecursiveMultipleLayoutActivation.Active) && (value != RecursiveMultipleLayoutActivation.Inactive))
				&& (value != RecursiveMultipleLayoutActivation.Mixed)) {
			throw (new ArgumentException(
					"Illegal value "
							+ ((Integer) value)
							+ LayoutUtil
									.GetText(
											" for method setSecondGraphLayoutActiveDuringAutoLayout",
											" for SecondGraphLayoutActiveDuringAutoLayout property")));
		}
		if (value != this.get_SecondGraphLayoutActiveDuringAutoLayout()) {
			this._secondLayoutActive &= -13;
			this._secondLayoutActive |= ((Integer) value) << 2;
			this.OnParameterChanged("SecondGraphLayoutActiveDuringAutoLayout");
		}
	}

}