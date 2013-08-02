package ILOG.Diagrammer.GraphLayout;

import java.util.ArrayList;

import system.ArgumentException;
import system.Math;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Collections.Stack;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelData;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelUtil;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.JavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.LayoutUtil;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;
import ILOG.Diagrammer.GraphLayout.Internal.RLayout.GraphLayoutRecUtil;
import ILOG.Diagrammer.Util.HashSet;

public class RecursiveLayout extends GraphLayout {
	private Integer _actLayout;

	private Integer _basePercentage;

	public Boolean _duringAutoLayout = false;

	private Boolean _force = false;

	private ILayoutProvider _layoutProvider;

	private HashSet _listenedSubModelsDuringAutoLayout;

	private Boolean _listenedSubModelsDuringAutoLayoutNeedsRefresh = false;

	private Boolean _loadLayoutMode = false;

	private RecursiveLayoutMode _mode;

	private Integer _numStepsInSublayout;

	private float _numSubLayouts;

	private long _oldAllowedTime;

	private ILOG.Diagrammer.GraphLayout.GraphLayout _referenceLayout;

	private Boolean _savePreferredLayouts = false;

	private long _startTime;

	private Boolean _stoppedBeforeCompletion = false;

	private ISubgraphCorrection _subgraphCorrection;

	private HashSet _subgraphsToLayout;

	private Boolean _usePercentageCompletionSubstitute = false;

	/* TODO: Event Declare */
	public ArrayList<GraphLayoutStepPerformedEventHandler> SubLayoutStepPerformed = new ArrayList<GraphLayoutStepPerformedEventHandler>();

	public RecursiveLayout() {
		this._listenedSubModelsDuringAutoLayout = new HashSet();
		this.SetLayoutMode(RecursiveLayoutMode.InternalProviderMode, null, null);
	}

	public RecursiveLayout(
			ILOG.Diagrammer.GraphLayout.GraphLayout referenceLayout) {
		this._listenedSubModelsDuringAutoLayout = new HashSet();
		this.SetLayoutMode(RecursiveLayoutMode.ReferenceLayoutMode,
				referenceLayout, null);
	}

	public RecursiveLayout(ILayoutProvider layoutProvider) {
		this._listenedSubModelsDuringAutoLayout = new HashSet();
		this.SetLayoutMode(RecursiveLayoutMode.SpecifiedProviderMode, null,
				layoutProvider);
	}

	public RecursiveLayout(RecursiveLayout source) {
		super(source);
		this._listenedSubModelsDuringAutoLayout = new HashSet();
	}

	private void AddAsListenerToSubModel(IGraphModel model) {

		if ((model != this.GetGraphModel())
				&& !this._listenedSubModelsDuringAutoLayout.Contains(model)) {
			super.AddToModelEventHandlers(model);
			this._listenedSubModelsDuringAutoLayout.Add(model);
		}

	}

	public void AddGraphLayoutPropertyChangedEventHandler(
			GraphLayoutPropertyChangedEventHandler handler,
			Boolean includeSelf, Boolean traverse) {
		// if(includeSelf){
		// super.LayoutPropertyChanged += this.handler;
		// }
		// if(traverse){
		// if(this.GetGraphModel() == null){
		// throw(new
		// system.Exception("A graph model must be attached when you use the method AddGraphLayoutPropertyChangedEventHandler with traverse = true"));
		// }
		// IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);
		//
		// while(layoutsImpl.HasMoreElements()){
		// ILOG.Diagrammer.GraphLayout.GraphLayout layout =
		// (ILOG.Diagrammer.GraphLayout.GraphLayout)layoutsImpl.NextElement();
		// if(layout != null){
		// layout.LayoutPropertyChanged += this.handler;
		// }
		// }
		// }

	}

	public void AddGraphLayoutStepPerformedEventHandler(
			GraphLayoutStepPerformedEventHandler handler, Boolean includeSelf,
			Boolean traverse) {
		// if(includeSelf){
		// super.LayoutStepPerformed += handler;
		// }
		// if(traverse){
		// if(this.GetGraphModel() == null){
		// throw(new
		// system.Exception("A graph model must be attached when you use the method AddGraphLayoutStepPerformedEventHandler with traverse = true"));
		// }
		// IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);
		//
		// while(layoutsImpl.HasMoreElements()){
		// ILOG.Diagrammer.GraphLayout.GraphLayout layout =
		// (ILOG.Diagrammer.GraphLayout.GraphLayout)layoutsImpl.NextElement();
		// if(layout != null){
		// layout.LayoutStepPerformed += this.handler;
		// }
		// }
		// }

	}

	private void AfterAttach(IGraphModel graphModel) {
		if (this.GetLayoutMode() == RecursiveLayoutMode.ReferenceLayoutMode) {
			ILOG.Diagrammer.GraphLayout.GraphLayout referenceLayout = this
					.GetReferenceLayout();
			DefaultLayoutProvider layoutProvider = (DefaultLayoutProvider) this
					.GetLayoutProvider();
			Boolean internalGraphModelChecking = GraphModelData.Get(graphModel)
					.get_InternalGraphModelChecking();
			try {
				GraphModelData.SetInternalGraphModelChecking(graphModel, false);
				layoutProvider.SetPreferredLayout(graphModel, referenceLayout);
			} finally {
				GraphModelData.SetInternalGraphModelChecking(graphModel,
						internalGraphModelChecking);
			}
			if (referenceLayout.GetGraphModel() != graphModel) {
				if (referenceLayout.GetGraphModel() != null) {
					referenceLayout.Detach();
				}
				referenceLayout.Attach(graphModel);
			}
		}

	}

	public void AfterSublayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		// Boolean parametersUpToDate = layout.get_ParametersUpToDate();
		// try{
		//
		// if(layout.SupportsAllowedTime()){
		// layout.set_AllowedTime(this._oldAllowedTime);
		// }
		// layout.LayoutStepPerformed += this.OnSubLayoutStepPerformed;
		// }
		// finally{
		// layout.set_ParametersUpToDate(parametersUpToDate);
		// }

	}

	@Override
	public void Attach(IGraphModel graphModel) {
		super.Attach(graphModel);
		this.AfterAttach(graphModel);

	}

	private void BeforeDetach() {
		IGraphModel graphModel = this.GetGraphModel();
		if (graphModel != null) {
			ILayoutProvider layoutProvider = this.GetLayoutProvider();
			if ((layoutProvider != null)
					&& (layoutProvider instanceof DefaultLayoutProvider)) {
				((DefaultLayoutProvider) layoutProvider).DetachLayouts(
						graphModel, true);
			} else {
				IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

				while (layoutsImpl.HasMoreElements()) {
					ILOG.Diagrammer.GraphLayout.GraphLayout layout = (ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
							.NextElement();
					if ((layout != null) && (layout.GetGraphModel() != null)) {
						layout.Detach();
					}
				}
			}
		}

	}

	public void BeforeSublayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		// Boolean parametersUpToDate = layout.get_ParametersUpToDate();
		// try{
		//
		// if(layout.SupportsAllowedTime()){
		// long num = TranslateUtil.CurrentTimeMillis() - this._startTime;
		// this._oldAllowedTime = layout.get_AllowedTime();
		// layout.set_AllowedTime(Math.Max(Math.Min(this._oldAllowedTime,this.get_AllowedTime()
		// - num),1L));
		// }
		// layout.LayoutStepPerformed += this.OnSubLayoutStepPerformed;
		//
		// this._usePercentageCompletionSubstitute =
		// !layout.SupportsPercentageComplete();
		// this._numStepsInSublayout = 0;
		// }
		// finally{
		// layout.set_ParametersUpToDate(parametersUpToDate);
		// }

	}

	private void CheckGraphModelListener(IGraphModel model,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if ((layout == null) && (model != this.GetGraphModel())) {
			this.RemoveAsListenerFromSubModel(model);
		}

	}

	private void CollectSubgraphsToLayout(java.lang.Object subgraph,
			Boolean traverse) {
		if (subgraph == null) {
			this._subgraphsToLayout = null;
		} else {
			IGraphModel graphModel = this.GetGraphModel();
			if (graphModel == null) {
				throw (new system.Exception(
						LayoutUtil.NO_ATTACHED_GRAPH_MODEL_OR_NULL));
			}
			IGraphModel model = graphModel.GetGraphModel(subgraph);
			this._subgraphsToLayout = new HashSet();
			if (!traverse) {
				this._subgraphsToLayout.Add(subgraph);
			} else {
				this.CollectSubgraphsToLayoutFromModel(graphModel, subgraph,
						model);
			}
		}

	}

	private void CollectSubgraphsToLayoutFromModel(IGraphModel rootModel,
			java.lang.Object graph, IGraphModel model) {
		if ((graph != null) && (model != null)) {
			this._subgraphsToLayout.Add(graph);
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(model.get_Subgraphs());

			while (enumerator.HasMoreElements()) {
				java.lang.Object subgraph = enumerator.NextElement();
				IGraphModel graphModel = rootModel.GetGraphModel(subgraph);
				this.CollectSubgraphsToLayoutFromModel(rootModel, subgraph,
						graphModel);
			}
		}

	}

	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout Copy() {

		return new RecursiveLayout(this);

	}

	@Override
	public void CopyParameters(ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		super.CopyParameters(source);
		if (source instanceof RecursiveLayout) {
			RecursiveLayout layout = (RecursiveLayout) source;
			this._savePreferredLayouts = layout._savePreferredLayouts;
			this._loadLayoutMode = layout._loadLayoutMode;
			this._subgraphCorrection = layout._subgraphCorrection;

			this._mode = layout.GetLayoutMode();
			if (this._mode == RecursiveLayoutMode.ReferenceLayoutMode) {
				this.CopyReferenceLayout(layout._referenceLayout);
				this._layoutProvider = new AnonClass_1(this);

				return;
			} else if (this._mode == RecursiveLayoutMode.InternalProviderMode) {
				this._layoutProvider = new AnonClass_2(this);

				return;
			} else if (this._mode == RecursiveLayoutMode.SpecifiedProviderMode) {

				this._layoutProvider = layout.GetLayoutProvider();

				return;
			} else {

				return;
			}
		}

	}

	private void CopyReferenceLayout(
			ILOG.Diagrammer.GraphLayout.GraphLayout sourceReferenceLayout) {
		if (sourceReferenceLayout != this._referenceLayout) {
			if (sourceReferenceLayout == null) {
				if (this._referenceLayout != null) {
					this._referenceLayout.Detach();
				}
				this._referenceLayout = null;
			} else if (this._referenceLayout == null) {

				this._referenceLayout = sourceReferenceLayout.Copy();
			} else if (sourceReferenceLayout.getClass().getName()
					.equals(this._referenceLayout.getClass().getName())) {
				this._referenceLayout.CopyParameters(sourceReferenceLayout);
			} else {
				IGraphModel graphModel = this._referenceLayout.GetGraphModel();
				this._referenceLayout.Detach();

				this._referenceLayout = sourceReferenceLayout.Copy();
				if (graphModel != null) {
					this._referenceLayout.Attach(graphModel);
				}
			}
		}

	}

	@Override
	public GraphLayoutReport CreateLayoutReport() {

		return new RecursiveLayoutReport();

	}

	@Override
	public void Detach() {
		this.BeforeDetach();
		super.Detach();
		this.RefreshAsListenerOfSubModels();

	}

	private ILOG.Diagrammer.GraphLayout.GraphLayout GetCleanLayout(
			IGraphModel model, ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if (this.GetLayoutMode() == RecursiveLayoutMode.ReferenceLayoutMode) {

			if ((layout == null)
					|| !layout
							.getClass()
							.getName()
							.equals(this.GetReferenceLayout().getClass()
									.getName())) {
				if ((model == null) && (layout != null)) {

					model = layout.GetGraphModel();
				}

				layout = this.GetReferenceLayout().Copy();
				DefaultLayoutProvider layoutProvider = (DefaultLayoutProvider) this
						.GetLayoutProvider();
				if (model != null) {
					layoutProvider.SetPreferredLayout(model, layout);
				}
			} else if (layout != this.GetReferenceLayout()) {
				layout.CopyParameters(this.GetReferenceLayout());
			}
		}
		if ((layout != null) && this.get_AutoLayout()) {
			layout.set_AutoLayout(false);
			if (model == null) {

				model = layout.GetGraphModel();
			}
			if (model != null) {
				this.AddAsListenerToSubModel(model);
			}
		}

		return layout;

	}

	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout GetLayout(
			java.lang.Object subgraph) {
		IGraphModel model2 = null;
		IGraphModel graphModel = this.GetGraphModel();
		if (graphModel == null) {
			throw (new system.Exception(
					"A graph model must be attached before you use the method "
							+ LayoutUtil.GetText("getLayout", "GetLayout")));
		}
		ILayoutProvider layoutProvider = this.GetLayoutProvider();
		if ((subgraph == null) || (subgraph == super.GetGraphicContainer())) {
			model2 = graphModel;
			Boolean internalGraphModelChecking = GraphModelData.Get(graphModel)
					.get_InternalGraphModelChecking();
			try {
				GraphModelData.SetInternalGraphModelChecking(graphModel, false);

				return this.GetCleanLayout(model2,
						layoutProvider.GetGraphLayout(model2));
			} finally {
				GraphModelData.SetInternalGraphModelChecking(graphModel,
						internalGraphModelChecking);
			}
		}

		model2 = graphModel.GetGraphModel(subgraph);

		return this.GetCleanLayout(model2,
				layoutProvider.GetGraphLayout(model2));

	}

	public RecursiveLayoutMode GetLayoutMode() {

		return this._mode;

	}

	public ILayoutProvider GetLayoutProvider() {

		return this._layoutProvider;

	}

	public GraphLayoutReport GetLayoutReport(java.lang.Object subgraph) {
		if (this.GetGraphModel() == null) {
			throw (new system.Exception(
					"A graph model must be attached when you use the method "
							+ LayoutUtil.GetText("getLayoutReport",
									"GetLayoutReport")));
		}
		ILOG.Diagrammer.GraphLayout.GraphLayout layout = this
				.GetLayout(subgraph);
		if (layout == null) {

			return null;
		}

		return layout.GetLayoutReport();

	}

	@Override
	public IEnumerator GetLayouts(Boolean preOrder) {
		IGraphModel graphModel = this.GetGraphModel();
		if (graphModel == null) {
			throw (new system.Exception(
					"A graph model must be attached before you use the method GetLayout"));
		}
		ILayoutProvider layoutProvider = this.GetLayoutProvider();
		if (preOrder) {

			return new RLayoutsPreorderEnumerator(this, graphModel,
					layoutProvider);
		}

		return new RLayoutsPostorderEnumerator(this, graphModel, layoutProvider);

	}

	public IJavaStyleEnumerator GetLayoutsImpl(Boolean preOrder) {

		return new JavaStyleEnumerator(this.GetLayouts(preOrder));

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout GetReferenceLayout() {

		return this._referenceLayout;

	}

	@Override
	public void Init() {
		super.Init();
		this._savePreferredLayouts = false;
		this._loadLayoutMode = true;
		this._subgraphCorrection = null;
		this._duringAutoLayout = false;

	}

	@Override
	public void Layout() {
		int initialCode = GraphLayoutReportCode.InitialCode;
		IGraphModel graphModel = this.GetGraphModel();
		if (graphModel == null) {
			throw (new system.Exception(
					LayoutUtil.NO_ATTACHED_GRAPH_MODEL_OR_NULL));
		}

		this._startTime = TranslateUtil.CurrentTimeMillis();
		this._numSubLayouts = 0f;
		IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(false);

		while (layoutsImpl.HasMoreElements()) {
			layoutsImpl.NextElement();
			this._numSubLayouts++;
		}
		this._basePercentage = 0;
		this._actLayout = 0;
		this._stoppedBeforeCompletion = false;

		initialCode = GraphLayoutRecUtil.PerformLayout(graphModel,
				this.GetLayoutProvider(), this, this._force, true, true);
		RecursiveLayoutReport layoutReport = (RecursiveLayoutReport) super
				.GetLayoutReport();
		layoutReport.SetCurrentlyRunningLayout(null);
		this.IncreasePercentageComplete(100);
		this.OnLayoutStepPerformed(false, false);
		if (this._stoppedBeforeCompletion
				&& (initialCode < GraphLayoutReportCode.StoppedAndInvalid)) {
			initialCode = GraphLayoutReportCode.StoppedAndInvalid;
		}
		layoutReport.set_Code(initialCode);

	}

	private Boolean MayContinue() {
		if (!this._stoppedBeforeCompletion) {

			if (!this.IsLayoutTimeElapsed() && !this.IsStoppedImmediately()) {

				return true;
			}
			this._stoppedBeforeCompletion = true;
		}

		return false;

	}

	@Override
	public void ModelContentsChanged(java.lang.Object sender,
			GraphModelContentsChangedEventArgs e) {
		this.ModelContentsChangedImpl(sender, e);

	}

	private void ModelContentsChangedImpl(java.lang.Object sender,
			GraphModelContentsChangedEventArgs e) {

		if (!super.IsLayoutRunning()) {
			IGraphModel model = e.get_Model();
			if (this.get_AutoLayout()) {
				if (((e.get_Action() & GraphModelContentsChangedEventAction.NodeAdded) != 0)
						&& model.IsSubgraph(e.get_NodeOrLink())) {
					IGraphModel graphModel = model.GetGraphModel(e
							.get_NodeOrLink());
					this.AddAsListenerToSubModel(graphModel);
				} else if ((e.get_Action() & GraphModelContentsChangedEventAction.NodeRemoved) != 0) {
					this._listenedSubModelsDuringAutoLayoutNeedsRefresh = true;
				}
			}

			if (!e.IsUpdating()
					&& this._listenedSubModelsDuringAutoLayoutNeedsRefresh) {
				this.RefreshAsListenerOfSubModels();
			}
			if (model == this.GetGraphModel()) {
				try {
					this._duringAutoLayout = true;
					super.ModelContentsChanged(sender, e);

					return;
				} finally {
					this._duringAutoLayout = false;
				}
			}
			Boolean parametersUpToDate = false;

			if (((!e.IsUpdating() && this.get_AutoLayout()) && this
					.IsLayoutNeeded())
					&& (((((e.get_Action() & GraphModelContentsChangedEventAction.NodeGeometryChanged) != 0) || ((e
							.get_Action() & GraphModelContentsChangedEventAction.LinkGeometryChanged) != 0)) || (((e
							.get_Action() & GraphModelContentsChangedEventAction.NodeAdded) != 0) || ((e
							.get_Action() & GraphModelContentsChangedEventAction.NodeRemoved) != 0))) || (((e
							.get_Action() & GraphModelContentsChangedEventAction.LinkAdded) != 0) || ((e
							.get_Action() & GraphModelContentsChangedEventAction.LinkRemoved) != 0)))) {
				try {
					this.set_AutoLayout(false);
					this._duringAutoLayout = true;
					this.PerformAutoLayout();
					parametersUpToDate = this.get_ParametersUpToDate();
				} finally {
					this._duringAutoLayout = false;
					this.set_AutoLayout(true);
					this.set_ParametersUpToDate(parametersUpToDate);
				}
			}
		}

	}

	public void OnLayoutStepPerformedAtSublayouts(
			GraphLayoutStepPerformedEventArgs e) {
		// if(this.SubLayoutStepPerformed != null){
		// this.SubLayoutStepPerformed(this, e);
		// }

	}

	public void OnSubLayoutStepPerformed(java.lang.Object source,
			GraphLayoutStepPerformedEventArgs e) {
		this.UpdatePercentageComplete(e.get_LayoutReport()
				.get_PercentageComplete());
		this.OnLayoutStepPerformed(false, false);
		this.OnLayoutStepPerformedAtSublayouts(e);

	}

	@Override
	public GraphLayoutReport PerformLayout(Boolean force) {
		this._force = force;

		return super.PerformLayout(force);

	}

	@Override
	public int PerformLayout(Boolean force, Boolean traverse) {
		this._force = force;

		return super.PerformLayout(force, false);

	}

	public GraphLayoutReport PerformLayout(java.lang.Object subgraph,
			Boolean force, Boolean traverse) {
		this.CollectSubgraphsToLayout(subgraph, traverse);
		GraphLayoutReport report = this.PerformLayout(force);
		this._subgraphsToLayout = null;

		return report;

	}

	@Override
	public int PerformSublayout(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout, Boolean force) {

		return this.PerformSublayoutImpl(subgraph, layout, force, true);

	}

	private int PerformSublayoutImpl(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout, Boolean force,
			Boolean redraw) {
		Boolean flag = true;
		if (this._subgraphsToLayout != null) {

			flag = this._subgraphsToLayout.Contains(subgraph);
		}

		if (!this.MayContinue()) {

			return GraphLayoutReportCode.StoppedAndInvalid;
		}
		int stoppedAndInvalid = GraphLayoutReportCode.StoppedAndInvalid;
		InternalRect rect = null;
		if ((flag && (subgraph != null)) && (this._subgraphCorrection != null)) {
			IGraphModel parent = layout.GetGraphModel().get_Parent();
			if (parent != null) {

				rect = GraphModelUtil.BoundingBox(parent, subgraph);
			}
		}
		((RecursiveLayoutReport) super.GetLayoutReport())
				.SetCurrentlyRunningLayout(layout);
		this.BeforeSublayout(layout);
		try {
			if (flag) {

				stoppedAndInvalid = super.PerformSublayout(subgraph, layout,
						force);
			} else {
				stoppedAndInvalid = GraphLayoutReportCode.NotNeeded;
			}
		} finally {
			this.AfterSublayout(layout);
		}
		if ((flag && (subgraph != null))
				&& ((rect != null) && (this._subgraphCorrection != null))) {
			this._subgraphCorrection.Correct(subgraph, layout,
					TranslateUtil.InternalRect2Rectangle2D(rect));
		}
		this._actLayout++;
		this._basePercentage = (int) ((this._actLayout * 100f) / this._numSubLayouts);
		if (this._basePercentage > 100) {
			this._basePercentage = 100;
		}
		this.IncreasePercentageComplete(this._basePercentage);

		return stoppedAndInvalid;

	}

	public void PropagateLayoutOfConnectedComponents(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		ILOG.Diagrammer.GraphLayout.GraphLayout referenceLayout = null;
		if (this.GetLayoutMode() == RecursiveLayoutMode.ReferenceLayoutMode) {

			referenceLayout = this.GetReferenceLayout();

			if (referenceLayout.SupportsLayoutOfConnectedComponents()) {
				referenceLayout.set_LayoutOfConnectedComponents(layout);
			}
		} else {
			if (this.GetGraphModel() == null) {
				throw (new system.Exception(
						"A graph model must be attached when you use the method "
								+ LayoutUtil.GetText(
										"propagateLayoutOfConnectedComponents",
										"PropagateLayoutOfConnectedComponents")));
			}
			IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				referenceLayout = (ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
						.NextElement();
				if ((referenceLayout != null)
						&& referenceLayout
								.SupportsLayoutOfConnectedComponents()) {
					referenceLayout
							.set_LayoutOfConnectedComponents((layout != null) ? layout
									.Copy() : null);
				}
			}
		}

	}

	public void PropagateLayoutOfConnectedComponentsEnabled(Boolean enable) {
		ILOG.Diagrammer.GraphLayout.GraphLayout referenceLayout = null;
		if (this.GetLayoutMode() == RecursiveLayoutMode.ReferenceLayoutMode) {

			referenceLayout = this.GetReferenceLayout();

			if (referenceLayout.SupportsLayoutOfConnectedComponents()) {
				referenceLayout.set_LayoutOfConnectedComponentsEnabled(enable);
			}
		} else {
			if (this.GetGraphModel() == null) {
				throw (new system.Exception(
						"A graph model must be attached when you use the method "
								+ LayoutUtil
										.GetText(
												"propagateLayoutOfConnectedComponentsEnabled",
												"PropagateLayoutOfConnectedComponentsEnabled")));
			}
			IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				referenceLayout = (ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
						.NextElement();
				if ((referenceLayout != null)
						&& referenceLayout
								.SupportsLayoutOfConnectedComponents()) {
					referenceLayout
							.set_LayoutOfConnectedComponentsEnabled(enable);
				}
			}
		}

	}

	public void PropagateLinkConnectionBoxInterface(
			ILinkConnectionBoxProvider linkConnectionBoxInterface) {
		ILOG.Diagrammer.GraphLayout.GraphLayout referenceLayout = null;
		if (this.GetLayoutMode() == RecursiveLayoutMode.ReferenceLayoutMode) {

			referenceLayout = this.GetReferenceLayout();

			if (referenceLayout.SupportsLinkConnectionBox()) {
				referenceLayout
						.set_LinkConnectionBoxProvider(linkConnectionBoxInterface);
			}
		} else {
			if (this.GetGraphModel() == null) {
				throw (new system.Exception(
						"A graph model must be attached when you use the method "
								+ LayoutUtil.GetText(
										"propagateLinkConnectionBoxInterface",
										"PropagateLinkConnectionBoxInterface")));
			}
			IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				referenceLayout = (ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
						.NextElement();
				if ((referenceLayout != null)
						&& referenceLayout.SupportsLinkConnectionBox()) {
					referenceLayout
							.set_LinkConnectionBoxProvider(linkConnectionBoxInterface);
				}
			}
		}

	}

	private void RefreshAsListenerOfSubModels() {
		this._listenedSubModelsDuringAutoLayoutNeedsRefresh = false;
		Integer count = this._listenedSubModelsDuringAutoLayout.get_Count();
		IGraphModel[] array = new IGraphModel[count];
		this._listenedSubModelsDuringAutoLayout.CopyTo(array, 0);
		for (Integer i = 0; i < count; i++) {
			this.RemoveAsListenerFromSubModel(array[i]);
		}
		if ((this.GetGraphModel() != null) && this.get_AutoLayout()) {
			IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				IGraphModel graphModel = ((ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
						.NextElement()).GetGraphModel();
				this.AddAsListenerToSubModel(graphModel);
			}
		}

	}

	private void RemoveAsListenerFromSubModel(IGraphModel model) {
		if ((model != this.GetGraphModel())
				&& this._listenedSubModelsDuringAutoLayout.Contains(model)) {
			super.RemoveFromModelEventHandlers(model);
			this._listenedSubModelsDuringAutoLayout.Remove(model);
		}

	}

	public void RemoveGraphLayoutPropertyChangedEventHandler(
			GraphLayoutPropertyChangedEventHandler handler,
			Boolean includeSelf, Boolean traverse) {
		// if(includeSelf){
		// super.LayoutPropertyChanged -= this.handler;
		// }
		// if(traverse){
		// if(this.GetGraphModel() == null){
		// throw(new
		// system.Exception("A graph model must be attached when you use the method RemoveGraphLayoutPropertyChangedEventHandler with traverse = true"));
		// }
		// IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);
		//
		// while(layoutsImpl.HasMoreElements()){
		// ILOG.Diagrammer.GraphLayout.GraphLayout layout =
		// (ILOG.Diagrammer.GraphLayout.GraphLayout)layoutsImpl.NextElement();
		// if(layout != null){
		// layout.LayoutPropertyChanged -= this.handler;
		// }
		// }
		// }

	}

	public void RemoveGraphLayoutStepPerformedEventHandler(
			GraphLayoutStepPerformedEventHandler handler, Boolean includeSelf,
			Boolean traverse) {
		// if(includeSelf){
		// super.LayoutStepPerformed -= this.handler;
		// }
		// if(traverse){
		// if(this.GetGraphModel() == null){
		// throw(new
		// system.Exception("A graph model must be attached when you use the method RemoveGraphLayoutStepPerformedEventHandler with traverse = true"));
		// }
		// IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);
		//
		// while(layoutsImpl.HasMoreElements()){
		// ILOG.Diagrammer.GraphLayout.GraphLayout layout =
		// (ILOG.Diagrammer.GraphLayout.GraphLayout)layoutsImpl.NextElement();
		// if(layout != null){
		// layout.LayoutStepPerformed -= this.handler;
		// }
		// }
		// }

	}

	public void SetLayout(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		this.SetLayout(subgraph, layout, true, false);

	}

	public void SetLayout(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			Boolean detachPrevious, Boolean traverse) {
		if (this.GetLayoutMode() != RecursiveLayoutMode.InternalProviderMode) {
			throw (new system.Exception("The method "
					+ LayoutUtil.GetText("setLayout", "SetLayout")
					+ " can only be used in public provider mode"));
		}
		IGraphModel graphModel = this.GetGraphModel();
		if (graphModel == null) {
			throw (new system.Exception(
					"A graph model must be attached before you use the method "
							+ LayoutUtil.GetText("setLayout", "SetLayout")));
		}
		DefaultLayoutProvider layoutProvider = (DefaultLayoutProvider) this
				.GetLayoutProvider();
		if ((subgraph == null) || (subgraph == super.GetGraphicContainer())) {
			Boolean internalGraphModelChecking = GraphModelData.Get(graphModel)
					.get_InternalGraphModelChecking();
			try {
				GraphModelData.SetInternalGraphModelChecking(graphModel, false);
				layoutProvider.SetPreferredLayout(graphModel, layout,
						detachPrevious, traverse);
			} finally {
				GraphModelData.SetInternalGraphModelChecking(graphModel,
						internalGraphModelChecking);
			}
		} else {
			layoutProvider.SetPreferredLayout(
					graphModel.GetGraphModel(subgraph), layout, detachPrevious,
					traverse);
		}

	}

	public void SetLayoutMode(RecursiveLayoutMode mode,
			ILOG.Diagrammer.GraphLayout.GraphLayout referenceLayout,
			ILayoutProvider layoutProvider) {
		if (this.GetGraphModel() != null) {
			this.BeforeDetach();
		}
		this._mode = mode;
		if (mode == RecursiveLayoutMode.ReferenceLayoutMode) {
			if (referenceLayout == null) {
				throw (new ArgumentException(
						"The reference layout cannot be null"));
			}
			if (referenceLayout instanceof RecursiveLayout) {
				throw (new ArgumentException(
						"A recursive layout cannot have another recursive layout as reference layout."));
			}
			this._referenceLayout = referenceLayout;
			this._layoutProvider = new AnonClass_3(this);
			// NOTICE: break ignore!!!
		} else if (mode == RecursiveLayoutMode.InternalProviderMode) {
			this._referenceLayout = null;
			this._layoutProvider = new AnonClass_4(this);
			// NOTICE: break ignore!!!
		} else if (mode == RecursiveLayoutMode.SpecifiedProviderMode) {
			if (layoutProvider == null) {
				throw (new ArgumentException(
						"The layout provider cannot be null"));
			}
			this._referenceLayout = null;
			this._layoutProvider = layoutProvider;
			// NOTICE: break ignore!!!
		}
		if (this.GetGraphModel() != null) {
			this.AfterAttach(this.GetGraphModel());
		}

	}

	@Override
	public Boolean StopImmediately() {
		Boolean flag = super.StopImmediately();
		if (this.GetGraphModel() != null) {
			IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				ILOG.Diagrammer.GraphLayout.GraphLayout layout = (ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
						.NextElement();
				if ((layout != null) && layout.IsLayoutRunning()) {

					flag &= layout.StopImmediately();
				}
			}
		}

		return flag;

	}

	@Override
	public Boolean SupportsAllowedTime() {

		return true;

	}

	@Override
	public Boolean SupportsPercentageComplete() {

		return true;

	}

	@Override
	public Boolean SupportsStopImmediately() {

		return true;

	}

	public void UpdatePercentageComplete(Integer percentageCompleteFromEvent) {
		float num = 0;
		this._numStepsInSublayout++;
		if (this._usePercentageCompletionSubstitute) {
			num = (1.010101f * this._numStepsInSublayout)
					/ (1f + (((float) this._numStepsInSublayout) / 99f));
		} else {
			num = percentageCompleteFromEvent;
		}
		this.IncreasePercentageComplete(this._basePercentage
				+ ((int) (num / this._numSubLayouts)));

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
			this.RefreshAsListenerOfSubModels();
			if (this.GetLayoutMode() == RecursiveLayoutMode.ReferenceLayoutMode) {
				this.GetReferenceLayout().set_AutoLayout(false);
			}
			if (this.GetGraphModel() != null) {
				IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

				while (layoutsImpl.HasMoreElements()) {
					ILOG.Diagrammer.GraphLayout.GraphLayout layout = (ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
							.NextElement();
					if (layout != null) {
						layout.set_AutoLayout(false);
					}
				}
			}
		}
	}

	public ILOG.Diagrammer.GraphLayout.CoordinatesMode get_CoordinatesMode() {

		return super.get_CoordinatesMode();
	}

	public void set_CoordinatesMode(
			ILOG.Diagrammer.GraphLayout.CoordinatesMode value) {
		ILOG.Diagrammer.GraphLayout.CoordinatesMode mode = value;
		super.set_CoordinatesMode(mode);
		if (this.GetLayoutMode() == RecursiveLayoutMode.ReferenceLayoutMode) {
			this.GetReferenceLayout().set_CoordinatesMode(mode);
		} else if (this.GetGraphModel() != null) {
			IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				ILOG.Diagrammer.GraphLayout.GraphLayout layout = (ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
						.NextElement();
				if (layout != null) {
					layout.set_CoordinatesMode(mode);
				}
			}
		}
	}

	public Boolean get_GeometryUpToDate() {
		if (this.GetGraphModel() != null) {
			IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				ILOG.Diagrammer.GraphLayout.GraphLayout layout = (ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
						.NextElement();
				if ((layout != null) && !layout.get_GeometryUpToDate()) {

					return false;
				}
			}
		}

		return super.get_GeometryUpToDate();
	}

	public void set_GeometryUpToDate(Boolean value) {
		super.set_GeometryUpToDate(value);
	}

	public long get_MinBusyTime() {

		return super.get_MinBusyTime();
	}

	public void set_MinBusyTime(long value) {
		long num = value;
		super.set_MinBusyTime(num);
		if (this.GetLayoutMode() == RecursiveLayoutMode.ReferenceLayoutMode) {
			this.GetReferenceLayout().set_MinBusyTime(num);
		} else if (this.GetGraphModel() != null) {
			IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				ILOG.Diagrammer.GraphLayout.GraphLayout layout = (ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
						.NextElement();
				if (layout != null) {
					layout.set_MinBusyTime(num);
				}
			}
		}
	}

	public Boolean get_ParametersUpToDate() {
		if (this.GetGraphModel() != null) {
			IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				ILOG.Diagrammer.GraphLayout.GraphLayout layout = (ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
						.NextElement();
				if ((layout != null) && !layout.get_ParametersUpToDate()) {

					return false;
				}
			}
		}

		return super.get_ParametersUpToDate();
	}

	public void set_ParametersUpToDate(Boolean value) {
		super.set_ParametersUpToDate(value);
	}

	public Boolean get_StructureUpToDate() {
		if (this.GetGraphModel() != null) {
			IJavaStyleEnumerator layoutsImpl = this.GetLayoutsImpl(true);

			while (layoutsImpl.HasMoreElements()) {
				ILOG.Diagrammer.GraphLayout.GraphLayout layout = (ILOG.Diagrammer.GraphLayout.GraphLayout) layoutsImpl
						.NextElement();
				if ((layout != null) && !layout.get_StructureUpToDate()) {

					return false;
				}
			}
		}

		return super.get_StructureUpToDate();
	}

	public void set_StructureUpToDate(Boolean value) {
		super.set_StructureUpToDate(value);
	}

	public ISubgraphCorrection get_SubgraphCorrectionInterface() {

		return this._subgraphCorrection;
	}

	public void set_SubgraphCorrectionInterface(ISubgraphCorrection value) {
		if (this._subgraphCorrection != value) {
			this._subgraphCorrection = value;
			this.OnParameterChanged("SubgraphCorrectionInterface");
		}
	}

	private class AnonClass_1 extends DefaultLayoutProvider {
		private RecursiveLayout __outerThis;

		public AnonClass_1(RecursiveLayout input__outerThis) {
			this.__outerThis = input__outerThis;
		}

		@Override
		public void StorePreferredLayout(IGraphModel graphModel,
				ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
			super.StorePreferredLayout(graphModel, layout);
			this.__outerThis.CheckGraphModelListener(graphModel, layout);

		}

	}

	private class AnonClass_2 extends RecursiveLayoutProvider {
		private RecursiveLayout __outerThis;

		public AnonClass_2(RecursiveLayout input__outerThis) {
			this.__outerThis = input__outerThis;
		}

		@Override
		public void StorePreferredLayout(IGraphModel graphModel,
				ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
			super.StorePreferredLayout(graphModel, layout);
			this.__outerThis.CheckGraphModelListener(graphModel, layout);

		}

	}

	private class AnonClass_3 extends DefaultLayoutProvider {
		private RecursiveLayout __outerThis;

		public AnonClass_3(RecursiveLayout input__outerThis) {
			this.__outerThis = input__outerThis;
		}

		@Override
		public void StorePreferredLayout(IGraphModel graphModel,
				ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
			super.StorePreferredLayout(graphModel, layout);
			this.__outerThis.CheckGraphModelListener(graphModel, layout);

		}

	}

	private class AnonClass_4 extends RecursiveLayoutProvider {
		private RecursiveLayout __outerThis;

		public AnonClass_4(RecursiveLayout input__outerThis) {
			this.__outerThis = input__outerThis;
		}

		@Override
		public void StorePreferredLayout(IGraphModel graphModel,
				ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
			super.StorePreferredLayout(graphModel, layout);
			this.__outerThis.CheckGraphModelListener(graphModel, layout);

		}

	}

	public class RLayoutsPostorderEnumerator implements IEnumerator {
		private ILayoutProvider _layoutProvider;

		private ILOG.Diagrammer.GraphLayout.GraphLayout _nextLayout;

		private Stack _nextStack;

		private RecursiveLayout _rlayout;

		private IGraphModel _rootModel;

		private Stack _siblingEnumStack;

		private IGraphModel _startModel;

		public RLayoutsPostorderEnumerator(RecursiveLayout rlayout,
				IGraphModel model, ILayoutProvider layoutProvider) {
			this._rlayout = rlayout;
			this._rootModel = (model.get_Root() != null) ? model.get_Root()
					: model;
			this._layoutProvider = layoutProvider;
			this._startModel = model;
			this.Reset();
		}

		public boolean MoveNext() {
			this.MoveToNextValidLayout();

			return (this._nextLayout != null);

		}

		private void MoveToNextValidLayout() {
			if (this._nextStack.get_Count() > 0) {

				this._nextLayout = this._layoutProvider.GetGraphLayout(this
						.NextModel());
			} else {
				this._nextLayout = null;
			}
			while ((this._nextLayout == null)
					&& (this._nextStack.get_Count() > 0)) {

				this._nextLayout = this._layoutProvider.GetGraphLayout(this
						.NextModel());
			}

		}

		private IGraphModel NextModel() {
			IGraphModel model = (IGraphModel) this._nextStack.Pop();
			if (this._siblingEnumStack.get_Count() > 0) {
				IEnumerator enumerator = (IEnumerator) this._siblingEnumStack
						.Pop();

				if (enumerator.MoveNext()) {
					this._siblingEnumStack.Push(enumerator);
					this.PushNext(this._rootModel.GetGraphModel(enumerator
							.get_Current()));
				}
			}

			return model;

		}

		private void PushNext(IGraphModel model) {
			this._nextStack.Push(model);
			ICollection subgraphs = model.get_Subgraphs();
			if (subgraphs.get_Count() > 0) {
				IEnumerator enumerator = subgraphs.GetEnumerator();
				enumerator.MoveNext();
				this._siblingEnumStack.Push(enumerator);
				this.PushNext(this._rootModel.GetGraphModel(enumerator
						.get_Current()));
			}

		}

		public void Reset() {
			this._siblingEnumStack = new Stack();
			this._nextStack = new Stack();
			this._nextLayout = null;
			this.PushNext(this._startModel);

		}

		public java.lang.Object get_Current() {

			return this._rlayout.GetCleanLayout(null, this._nextLayout);
		}

	}

	public class RLayoutsPreorderEnumerator implements IEnumerator {
		private Stack _childrenEnumStack;

		private ILayoutProvider _layoutProvider;

		private ILOG.Diagrammer.GraphLayout.GraphLayout _nextLayout;

		private IGraphModel _nextModel;

		private RecursiveLayout _rlayout;

		private IGraphModel _rootModel;

		private Boolean _started = false;

		private IGraphModel _startModel;

		public RLayoutsPreorderEnumerator(RecursiveLayout rlayout,
				IGraphModel model, ILayoutProvider layoutProvider) {
			this._rlayout = rlayout;
			this._startModel = model;
			this._rootModel = (model.get_Root() != null) ? model.get_Root()
					: model;
			this._layoutProvider = layoutProvider;
			this.Reset();
		}

		public boolean MoveNext() {
			this.MoveToNextModel();
			this.MoveToValidLayout();

			return (this._nextLayout != null);

		}

		private void MoveToNextModel() {
			if (!this._started) {
				this._nextModel = this._startModel;
				this._started = true;
			} else {
				ICollection subgraphs = this._nextModel.get_Subgraphs();
				if (subgraphs.get_Count() <= 0) {
					while (this._childrenEnumStack.get_Count() > 0) {
						IEnumerator enumerator2 = (IEnumerator) this._childrenEnumStack
								.Pop();

						if (enumerator2.MoveNext()) {

							this._nextModel = this._rootModel
									.GetGraphModel(enumerator2.get_Current());
							this._childrenEnumStack.Push(enumerator2);

							return;
						}
					}
					this._nextModel = null;
				} else {
					IEnumerator enumerator = subgraphs.GetEnumerator();
					enumerator.MoveNext();

					this._nextModel = this._rootModel.GetGraphModel(enumerator
							.get_Current());
					this._childrenEnumStack.Push(enumerator);
				}
			}

		}

		private void MoveToValidLayout() {
			if (this._nextModel != null) {

				this._nextLayout = this._layoutProvider
						.GetGraphLayout(this._nextModel);
			} else {
				this._nextLayout = null;
			}
			while ((this._nextLayout == null) && (this._nextModel != null)) {
				this.MoveToNextModel();
				if (this._nextModel != null) {

					this._nextLayout = this._layoutProvider
							.GetGraphLayout(this._nextModel);
				}
			}

		}

		public void Reset() {
			this._childrenEnumStack = new Stack();
			this._nextModel = null;
			this._nextLayout = null;
			this._started = false;

		}

		public java.lang.Object get_Current() {

			return this._rlayout.GetCleanLayout(this._nextModel,
					this._nextLayout);
		}

	}
}