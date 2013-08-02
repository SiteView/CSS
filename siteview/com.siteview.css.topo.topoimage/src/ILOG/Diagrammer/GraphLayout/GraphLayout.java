package ILOG.Diagrammer.GraphLayout;

//using ILOG.Diagrammer;
import java.util.ArrayList;
import java.util.Iterator;

import org.csstudio.opibuilder.model.DisplayModel;

import system.ArgumentException;
import system.EventArgs;
import system.EventHandler;
import system.GC;
import system.Math;
import system.Type;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.ComponentModel.ISite;
import system.ComponentModel.PropertyChangedEventHandler;
import system.Reflection.ConstructorInfo;
import ILOG.Diagrammer.GraphicContainer;
import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.IDiagramView;
import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.Size2D;
import ILOG.Diagrammer.GraphLayout.Internal.DefaultPropertyContainer;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelData;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelUtil;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.LayoutUtil;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;
import ILOG.Diagrammer.Util.HashSet;

public abstract class GraphLayout {
	private Integer _actSublayoutPoints = 0;

	private long _allowedTime = 0;

	private Boolean _autoLayout = false;

	private IAutoLayoutHandler _autoLayoutHandler = null;

	private IGraphModel _connectedCompCollection = null;

	private Boolean _connectedCompLayoutEnabled = false;

	private GraphLayoutReport _connectedCompLayoutReport;

	public ILOG.Diagrammer.GraphLayout.GraphLayout _connectedComponentsLayout;

	private ILOG.Diagrammer.GraphLayout.CoordinatesMode _coordMode;

	public long _defaultAllowedTime = 0x7d00L;

	public Boolean _defaultAutoLayout = false;

	public static IAutoLayoutHandler _defaultAutoLayoutHandler = null;

	public ILOG.Diagrammer.GraphLayout.CoordinatesMode _defaultCoordMode = ILOG.Diagrammer.GraphLayout.CoordinatesMode.InverseViewCoordinates;

	public Boolean _defaultFitToViewEnabled = true;

	public Boolean _defaultInputCheckEnabled = true;

	public static ILinkConnectionBoxProvider _defaultLinkConnectionBoxInterface = null;

	public long _defaultMinBusyTime = 50L;

	public Boolean _defaultPreserveFixedLinks = false;

	public Boolean _defaultPreserveFixedNodes = false;

	public static InternalRect _defaultRectangleToFit = null;

	public Integer _defaultSeedValue = 0;

	public Boolean _defaultUserDefinedSeedOption = false;

	private Boolean _duringConnectedCompLayout = false;

	private InternalRect _estimatedLayoutRegion;

	private Boolean _fitToViewEnabled = false;

	private Boolean _geometryUpToDate = false;

	private IGraphModel _graphModel;

	private Boolean _inputCheckEnabled = false;

	private Boolean _isConnectedGraph = false;

	private Boolean _isConnectivityTestDone = false;

	private Boolean _isLayoutRunning = false;

	private Boolean _isStoppedImmediately = false;

	private Boolean _isStoppedPrematurely = false;

	private long _lastLayoutStepPerformedTime;

	private GraphLayoutRegionMode _layoutRegionMode;

	private GraphLayoutReport _layoutReport;

	private Boolean _linkClipping = false;

	private ILinkConnectionBoxProvider _linkConnectionBoxInterface;

	private Integer _maxSublayoutPoints = 0;

	private long _minBusyTime = 0;

	private String _name;

	private Integer _numberOfSublayouts;

	private InternalRect _oldLayoutRegion;

	private Integer _originalCoordModeOfModel;

	private Boolean _parametersUpToDate = false;

	private ILOG.Diagrammer.GraphLayout.GraphLayout _parentLayout;

	private Boolean _preserveFixedLinksOption = false;

	private Boolean _preserveFixedNodesOption = false;

	private IPropertyContainer _properties;

	private java.lang.Object _providersThatContainMe;

	private InternalRect _rectangleToFit;

	private InternalRect _rectangleToFitArgument;

	private RecursiveLayout _recursiveLayout;

	private Integer _seedValue;

	private Boolean _structureUpToDate = false;

	private Integer _sublayoutCounter;

	private Integer[] _sublayoutPoints;

	private Integer _uniqueId = 0;

	private static Integer _uniqueIdCounter = -1;

	private Boolean _userDefinedSeedOption = false;

	private IDiagramView _viewToFit;

	private static String FIXED_PROPERTY = "Fixed";

	private static String IGNORED_PROPERTY = "Ignored";

	private HashSet modelsWeListenTo;

	private String NODE_OR_LINK_PROPERTY_NAME = "";

	private static String PROPERTY_BASE_NAME = "__ilvGraphLayout";

	private static String RADIAL_OFFSET_PROPERTY = "RadialOffset";

	public static String realContainerProperty = "GraphLayout_RealContainer";

	private static String TANGENTIAL_OFFSET_PROPERTY = "TangentialOffset";

	/* TODO: Event Declare */
	public ArrayList<EventHandler> Attached = new ArrayList<EventHandler>();
	/* TODO: Event Declare */
	public ArrayList<EventHandler> Detached = new ArrayList<EventHandler>();
	/* TODO: Event Declare */
	public ArrayList<EventHandler> Disposed = new ArrayList<EventHandler>();
	/* TODO: Event Declare */
	// public ArrayList<GraphLayoutPropertyChangedEventHandler>
	// LayoutPropertyChanged =new
	// ArrayList<GraphLayoutPropertyChangedEventHandler>();
	/* TODO: Event Declare */
	public ArrayList<GraphLayoutStepPerformedEventHandler> LayoutStepPerformed = new ArrayList<GraphLayoutStepPerformedEventHandler>();
	/* TODO: Event Declare */
	public ArrayList<PropertyChangedEventHandler> PropertyChanged = new ArrayList<PropertyChangedEventHandler>();

	public GraphLayout() {
		this.modelsWeListenTo = new HashSet();
		this._layoutRegionMode = GraphLayoutRegionMode.ViewBounds;
		this.Init();
	}

	public GraphLayout(ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		this.modelsWeListenTo = new HashSet();
		this._layoutRegionMode = GraphLayoutRegionMode.ViewBounds;
		this.Init();
		this.CopyParameters(source);
		this.set_ParametersUpToDate(false);
	}

	public void AddLayoutProviderThatContainsMe(
			DefaultLayoutProvider layoutProvider) {
		if (this._providersThatContainMe == null) {
			this._providersThatContainMe = layoutProvider;
		} else if (this._providersThatContainMe instanceof DefaultLayoutProvider) {
			if (this._providersThatContainMe != layoutProvider) {
				ArrayList list = new ArrayList(2);
				list.add(this._providersThatContainMe);
				list.add(layoutProvider);
				this._providersThatContainMe = list;
			}
		} else {
			if (!(this._providersThatContainMe instanceof ArrayList)) {
				throw (new system.Exception("Illegal situation"));
			}
			ArrayList list2 = (ArrayList) this._providersThatContainMe;

			if (!list2.contains(layoutProvider)) {
				list2.add(layoutProvider);
			}
		}

	}

	public void AddToModelEventHandlers(IGraphModel model) {

		if (!this.modelsWeListenTo.Contains(model)) {

			GraphModelContentsChangedEventHandler eh = new GraphModelContentsChangedEventHandler() {

				@Override
				public void handle(Object sender,
						GraphModelContentsChangedEventArgs e) {
					ModelContentsChanged(sender, e);
				}

			};

			model.ContentsChanged.add(eh);
			this.modelsWeListenTo.Add(model);
		}

	}

	public void AfterLayoutOfSubgraph(IGraphModel subgraph) {

	}

	public void Attach(GraphicContainer grapher) {
		// ArrayList<GraphicObject> list = grapher.getNodes();
		// Iterator<GraphicObject> iterator = list.iterator();
		// while (iterator.hasNext()) {
		// System.out.println(iterator.next());
		// }
		if (grapher == null) {
			throw (new ArgumentException("graph cannot be null"));
		}
		IGraphModel graphModel = this.GetGraphModel();
		if (((graphModel == null) || !(graphModel instanceof GraphicContainerAdapter))
				|| ((((GraphicContainerAdapter) graphModel)
						.GetOriginatingLayout() != this) || (((GraphicContainerAdapter) graphModel)
						.get_GraphicContainer() != grapher))) {
			GraphicContainerAdapter adapter = new GraphicContainerAdapter(
					grapher);
			this.Attach(adapter);
			adapter.SetOriginatingLayout(this);
		}

	}

	public void Attach(DisplayModel displayMode) {
		// if (displayMode == null) {
		// throw (new ArgumentException("graph cannot be null"));
		// }
	}

	public void Attach(IGraphModel graphModel) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		if (graphModel != this._graphModel) {
			if (this._graphModel != null) {
				this.Detach();
			}
			this._graphModel = graphModel;
			// this.AddToModelEventHandlers(this._graphModel);
			this._graphModel.OnAttach(this);
			if (this._connectedCompCollection != null) {
				this._connectedCompCollection.Dispose();
				this._connectedCompCollection = null;
			}
			this._structureUpToDate = false;
			this._geometryUpToDate = false;
			this._parametersUpToDate = false;
			this._estimatedLayoutRegion = null;
			this.OnAttached();
		}

	}

	private void OnAttached() {
		// TODO Auto-generated method stub
		this.UpdateLayoutRegion();

	}

	public void BeforeLayout() {
		GraphModelUtil.BeforeLayout(this.GetGraphModel(), this, true);

	}

	public void BeforeLayoutOfSubgraph(IGraphModel subgraph) {

	}

	private void CalcSublayoutPointsForPercentageComplete(
			IGraphModel connectedCompCollection) {
		this._numberOfSublayouts = connectedCompCollection.get_Nodes()
				.get_Count() + 1;
		this._sublayoutPoints = new Integer[this._numberOfSublayouts];
		this._maxSublayoutPoints = 0;
		Integer index = 0;
		Integer num3 = 0;
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(connectedCompCollection.get_Nodes());

		while (enumerator.HasMoreElements()) {
			IGraphModel model = (IGraphModel) enumerator.NextElement();
			Integer num2 = model.get_Nodes().get_Count()
					+ model.get_Links().get_Count();
			num3 += num2;
			this._maxSublayoutPoints += model.get_Links().get_Count();
			this._sublayoutPoints[index++] = 3 * num2;
		}
		this._sublayoutPoints[index] = num3;
		this._maxSublayoutPoints += num3;
		this._actSublayoutPoints = 0;

	}

	// public boolean CanExtend(java.lang.Object extendee)
	// {
	// if(extendee instanceof GraphicObject){
	// GraphicContainerAdapter graphModel =
	// (GraphicContainerAdapter)this.GetGraphModel();
	// if(graphModel != null){
	//
	// if(!graphModel.IsNode(extendee)){
	//
	//
	// return graphModel.IsLinkOrInterGraphLink(extendee);
	// }
	//
	// return true;
	// }
	// }
	//
	// return false;
	//
	// }

	public void CleanGraphModel(IGraphModel graphModel) {
		if (graphModel == null) {
			throw (new ArgumentException("graph model cannot be null"));
		}
		this.RemoveFromModelEventHandlers(graphModel);

	}

	private void CleanLayoutProvidersThatContainMe(IGraphModel graphModel) {
		if (this._providersThatContainMe != null) {
			java.lang.Object obj2 = this._providersThatContainMe;
			this._providersThatContainMe = null;
			Boolean internalGraphModelChecking = GraphModelData.Get(graphModel)
					.get_InternalGraphModelChecking();
			try {
				GraphModelData.SetInternalGraphModelChecking(graphModel, false);
				if (obj2 instanceof DefaultLayoutProvider) {
					DefaultLayoutProvider provider = (DefaultLayoutProvider) obj2;
					provider.SetPreferredLayout(graphModel, null);
				} else if (obj2 instanceof ArrayList) {
					IJavaStyleEnumerator enumerator = TranslateUtil
							.Collection2JavaStyleEnum((ICollection) obj2);

					while (enumerator.HasMoreElements()) {
						((DefaultLayoutProvider) enumerator.NextElement())
								.SetPreferredLayout(graphModel, null);
					}
				}
			} finally {
				GraphModelData.SetInternalGraphModelChecking(graphModel,
						internalGraphModelChecking);
			}
		}

	}

	public void CleanLink(IGraphModel graphModel, java.lang.Object link) {
		this.CleanNodeOrLink(graphModel, link);

	}

	public void CleanNode(IGraphModel graphModel, java.lang.Object node) {
		this.CleanNodeOrLink(graphModel, node);

	}

	private void CleanNodeOrLink(IGraphModel graphModel,
			java.lang.Object nodeOrLink) {
		if (graphModel == null) {
			throw (new ArgumentException("graph model cannot be null"));
		}
		graphModel.SetProperty(nodeOrLink, this.NODE_OR_LINK_PROPERTY_NAME,
				null);

	}

	private void ContentsChangedImpl(GraphModelContentsChangedEventArgs e) {
		if (!this._isLayoutRunning) {
			this._estimatedLayoutRegion = null;

			if (!e.IsEndUpdate()) {
				if ((e.get_Action() & GraphModelContentsChangedEventAction.StructureChanged) != 0) {
					this.set_StructureUpToDate(false);
					this._isConnectivityTestDone = false;
					if (this._connectedComponentsLayout != null) {
						this._connectedComponentsLayout.Detach();
					}
					if (this._connectedCompCollection != null) {
						this._connectedCompCollection.Dispose();
						this._connectedCompCollection = null;
					}
				} else if ((e.get_Action() & GraphModelContentsChangedEventAction.GeometryChanged) != 0) {
					this.set_GeometryUpToDate(false);
					if (this._connectedComponentsLayout != null) {
						this._connectedComponentsLayout
								.set_GeometryUpToDate(false);
					}
				}
				java.lang.Object nodeOrLink = e.get_NodeOrLink();
				if (nodeOrLink != null) {
					if ((e.get_Action() & GraphModelContentsChangedEventAction.NodeRemoved) != 0) {
						this.CleanNode(e.get_Model(), nodeOrLink);
					} else if ((e.get_Action() & GraphModelContentsChangedEventAction.LinkRemoved) != 0) {
						this.CleanLink(e.get_Model(), nodeOrLink);
					}
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
					this.PerformAutoLayout();
					parametersUpToDate = this.get_ParametersUpToDate();
				} finally {
					this.set_AutoLayout(true);
					this.set_ParametersUpToDate(parametersUpToDate);
				}
			}
		}

	}

	public abstract ILOG.Diagrammer.GraphLayout.GraphLayout Copy();

	private void CopyLayoutOfConnectedComponents(
			ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		ILOG.Diagrammer.GraphLayout.GraphLayout layout = null;

		if (source.SupportsLayoutOfConnectedComponents()) {
			layout = source._connectedComponentsLayout;
		}
		if (layout != this._connectedComponentsLayout) {
			if (layout == null) {
				this.set_LayoutOfConnectedComponents(null);
			} else if ((this._connectedComponentsLayout != null)
					&& (this._connectedComponentsLayout.getClass().getName()
							.equals(layout.getClass().getName()))) {
				this._connectedComponentsLayout.CopyParameters(layout);
			} else {
				this.set_LayoutOfConnectedComponents(layout.Copy());
			}
		}

	}

	public void CopyParameters(ILOG.Diagrammer.GraphLayout.GraphLayout source) {

		if (this.SupportsAllowedTime()) {
			this.set_AllowedTime(source.get_AllowedTime());
		}
		this.set_MinBusyTime(source.get_MinBusyTime());

		if (this.SupportsPreserveFixedNodes()) {
			this.set_PreserveFixedNodes(source.get_PreserveFixedNodes());
		}

		if (this.SupportsPreserveFixedLinks()) {
			this.set_PreserveFixedLinks(source.get_PreserveFixedLinks());
		}
		this.set_AutoLayout(source.get_AutoLayout());
		this.set_AutoLayoutHandler(source.get_AutoLayoutHandler());

		if (this.SupportsRandomGenerator()) {
			this.set_SeedValueForRandomGenerator(source
					.get_SeedValueForRandomGenerator());
			this.set_UseSeedValueForRandomGenerator(source
					.get_UseSeedValueForRandomGenerator());
		}
		this.set_InputCheckEnabled(source.get_InputCheckEnabled());

		if (this.SupportsLayoutOfConnectedComponents()) {
			this.set_LayoutOfConnectedComponentsEnabled(source
					.get_LayoutOfConnectedComponentsEnabled());
		}
		this.set_CoordinatesMode(source.get_CoordinatesMode());
		this.set_LinkClipping(source.get_LinkClipping());

		if (this.SupportsLinkConnectionBox()) {
			this.set_LinkConnectionBoxProvider(source
					.get_LinkConnectionBoxProvider());
		}

		if (this.SupportsLayoutRegion()) {
			InternalRect rect = source._rectangleToFit;
			this._rectangleToFit = (rect != null) ? new InternalRect(
					source._rectangleToFit.X, source._rectangleToFit.Y,
					source._rectangleToFit.Width, source._rectangleToFit.Height)
					: null;
			rect = source._rectangleToFitArgument;
			this._rectangleToFitArgument = (rect != null) ? new InternalRect(
					source._rectangleToFitArgument.X,
					source._rectangleToFitArgument.Y,
					source._rectangleToFitArgument.Width,
					source._rectangleToFitArgument.Height) : null;
			this._viewToFit = source._viewToFit;
			this._fitToViewEnabled = source._fitToViewEnabled;
			this.set_LayoutRegionMode(source.get_LayoutRegionMode());
		}

		if (this.SupportsLayoutOfConnectedComponents()) {
			this.CopyLayoutOfConnectedComponents(source);
		}

	}

	public static java.lang.Object CreateDefaultProvider(Type propertyType,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		Type type = propertyType;
		String name = type.get_Name();

		if (name.startsWith("I")) {

			name = name.substring(1);
		}
		Type type2 = Type.GetType(Type.GetType(
				ILOG.Diagrammer.GraphLayout.GraphLayout.class.getName())
				.get_Namespace()
				+ ".Default" + name);
		if (type2 != null) {
			ConstructorInfo constructor = type2
					.GetConstructor(new Type[] { Type
							.GetType(ILOG.Diagrammer.GraphLayout.GraphLayout.class
									.getName()) });
			if (constructor != null) {

				return constructor.Invoke(new java.lang.Object[] { layout });
			}
		}

		return null;

	}

	public GraphLayoutReport CreateLayoutReport() {

		return new GraphLayoutReport();

	}

	public void Detach() {
		if (this._graphModel != null) {
			this.OnDetached();
			this.set_StructureUpToDate(false);
			this.set_GeometryUpToDate(false);
			this.set_ParametersUpToDate(false);
			this.CleanGraphModel(this._graphModel);
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(this._graphModel.get_Nodes());

			while (enumerator.HasMoreElements()) {
				this.CleanNode(this._graphModel, enumerator.NextElement());
			}

			enumerator = TranslateUtil
					.Collection2JavaStyleEnum(this._graphModel.get_Links());

			while (enumerator.HasMoreElements()) {
				this.CleanLink(this._graphModel, enumerator.NextElement());
			}

			enumerator = TranslateUtil
					.Collection2JavaStyleEnum(this._graphModel
							.get_InterGraphLinks());

			while (enumerator.HasMoreElements()) {
				this.CleanLink(this._graphModel, enumerator.NextElement());
			}
			if (this._connectedCompCollection != null) {
				this._connectedCompCollection.Dispose();
				this._connectedCompCollection = null;
			}
			this._isConnectivityTestDone = false;
			if (this._connectedComponentsLayout != null) {
				this._connectedComponentsLayout.Detach();
			}
			IGraphModel graphModel = this._graphModel;
			this._graphModel = null;
			if (this._recursiveLayout != null) {
				ILOG.Diagrammer.GraphLayout.GraphLayout layout = this._recursiveLayout;
				this._recursiveLayout = null;
				layout.Detach();
			}
			this.CleanLayoutProvidersThatContainMe(graphModel);
			graphModel.OnDetach(this);
			if ((graphModel instanceof GraphicContainerAdapter)
					&& (((GraphicContainerAdapter) graphModel)
							.GetOriginatingLayout() == this)) {
				graphModel.Dispose();
			}
		}

	}

	public void Dispose() {
		this.Dispose(true);
		GC.SuppressFinalize(this);

	}

	public void Dispose(Boolean disposing) {
		// if(disposing && (this.GetProperty("IComponent_Disposing") == null)){
		// try{
		// this.SetProperty("IComponent_Disposing","");
		// if((this.get_Site() != null) && (this.get_Site().get_Container() !=
		// null)){
		// this.get_Site().get_Container().Remove(this);
		// }
		// this.Detach();
		// if(this.Disposed != null){
		// // this.Disposed(this, EventArgs.Empty);
		// }
		// }
		// finally{
		// this.SetProperty("IComponent_Disposing",null);
		// }
		// }

	}

	private void DoLayoutOfConnectedComponents(IGraphModel graphModel,
			Boolean force, Boolean redraw) {
		int code = 0;
		IGraphModel model = graphModel;
		IGraphModel connectedComponents = this.GetConnectedComponents(
				graphModel, this._isConnectivityTestDone);
		this.IncreasePercentageComplete(2);
		this.OnLayoutStepPerformedIfNeeded();
		this.CalcSublayoutPointsForPercentageComplete(connectedComponents);
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(connectedComponents.get_Nodes());
		if (this._numberOfSublayouts < 2) {
			throw (new GraphLayoutException(
					"Unexpected (too small) number of connected components: "
							+ this._numberOfSublayouts));
		}
		int initialCode = GraphLayoutReportCode.InitialCode;
		try {
			this._duringConnectedCompLayout = true;
			this._sublayoutCounter = 0;

			while (enumerator.HasMoreElements()) {
				this.SetGraphModel((IGraphModel) enumerator.NextElement());

				graphModel = this.GetGraphModel();
				this.BeforeLayoutOfSubgraph(graphModel);
				GraphModelUtil.ApplyLayout(graphModel, this, redraw);
				code = this._layoutReport.get_Code();
				if (code > initialCode) {
					initialCode = code;
				}
				this.AfterLayoutOfSubgraph(graphModel);
				this._actSublayoutPoints += this._sublayoutPoints[this._sublayoutCounter++];
				;
			}
		} finally {
			this.SetGraphModel(model);
		}
		ILOG.Diagrammer.GraphLayout.GraphLayout layoutOfConnectedComponents = this
				.get_LayoutOfConnectedComponents();
		if ((connectedComponents != null)
				&& (layoutOfConnectedComponents.GetGraphModel() != connectedComponents)) {
			layoutOfConnectedComponents.Attach(connectedComponents);
		}
		this._connectedCompLayoutReport = null;
		if (layoutOfConnectedComponents.GetGraphModel() != null) {

			this._connectedCompLayoutReport = layoutOfConnectedComponents
					.PerformLayout(force);
		}
		this._actSublayoutPoints += this._sublayoutPoints[this._sublayoutCounter++];
		code = this._layoutReport.get_Code();
		if (code > initialCode) {
			initialCode = code;
		}
		this._layoutReport.set_Code(initialCode);

	}

	protected void finalize() {
		this.Dispose(false);

	}

	// private void FireLayoutPropertyChanged(Boolean uptodate, java.lang.Object
	// nodeOrLink, String parameterName)
	// {
	// GraphLayoutPropertyChangedEventArgs e = null;
	// if((this.LayoutPropertyChanged != null) || (this.PropertyChanged !=
	// null)){
	// e = new GraphLayoutPropertyChangedEventArgs(this, nodeOrLink,
	// parameterName, uptodate);
	// }
	// if(this.LayoutPropertyChanged != null){
	// for(GraphLayoutPropertyChangedEventHandler h :
	// this.LayoutPropertyChanged)
	// h.handle(this, e);
	// }
	// if((nodeOrLink == null) && (this.PropertyChanged != null)){
	// for(PropertyChangedEventHandler h: this.PropertyChanged){
	// h.DynamicInvoke(new java.lang.Object[]{this, e});
	// }
	//
	// }
	//
	// }

	public Rectangle2D GetCalcLayoutRegion() {

		return this.GetCalcLayoutRegionImpl();

	}

	private Rectangle2D GetCalcLayoutRegionImpl() {
		Rectangle2D specLayoutRegion = this.GetSpecLayoutRegion();
		if ((!(specLayoutRegion == Rectangle2D.Invalid) && (specLayoutRegion
				.get_Width() >= 1E-20f))
				&& (specLayoutRegion.get_Height() >= 1E-20f)) {

			return specLayoutRegion;
		}
		if (this.GetGraphModel() == null) {

			return specLayoutRegion;
		}
		if (this._estimatedLayoutRegion == null) {

			this._estimatedLayoutRegion = LayoutUtil
					.GetEstimatedLayoutRegion(this.GetGraphModel());
		}
		if (((this._estimatedLayoutRegion == null) || (this._estimatedLayoutRegion.Width < 1E-20f))
				|| (this._estimatedLayoutRegion.Height < 1E-20f)) {

			return new Rectangle2D(0f, 0f, 1000f, 1000f);
		}

		return TranslateUtil
				.InternalRect2Rectangle2D(this._estimatedLayoutRegion);

	}

	private IGraphModel GetConnectedComponents(IGraphModel graphModel,
			Boolean isConnectivityTestDone) {
		if (this._connectedCompCollection == null) {

			this._connectedCompCollection = GraphLayoutUtil
					.GetConnectedComponents(graphModel, isConnectivityTestDone);
		}

		return this._connectedCompCollection;

	}

	private IDiagramView GetFirstManagerView() {

		return null;

	}

	public Boolean GetFixed(java.lang.Object nodeOrLink) {

		return LayoutParametersUtil.GetNodeOrLinkParameter(this, nodeOrLink,
				FIXED_PROPERTY, false);

	}

	public GraphicContainer GetGraphicContainer() {
		GraphicContainerAdapter graphModel = (GraphicContainerAdapter) this
				.GetGraphModel();
		if (graphModel != null) {

			return graphModel.get_GraphicContainer();
		}

		return null;

	}

	public IGraphModel GetGraphModel() {

		return this._graphModel;

	}

	// public Boolean GetIgnored(java.lang.Object nodeOrLink)
	// {
	// GraphicObject obj2 = (GraphicObject)nodeOrLink;
	// if(obj2 == null){
	//
	// return false;
	// }
	// java.lang.Object obj3 =
	// obj2.get_Properties().get_Item(this.NODE_OR_LINK_PROPERTY_NAME +
	// IGNORED_PROPERTY);
	//
	// return ((obj3 != null) && ((Boolean)obj3));
	//
	// }

	public Integer GetInstanceId() {

		return this._uniqueId;

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout GetLayout(
			java.lang.Object subgraph) {

		return this.GetRecursiveLayout().GetLayout(subgraph);

	}

	public GraphLayoutReport GetLayoutOfConnectedComponentsReport() {
		ILOG.Diagrammer.GraphLayout.GraphLayout layout = this._connectedComponentsLayout;
		if (layout == null) {

			return null;
		}

		if (!layout.IsLayoutRunning()) {

			return this._connectedCompLayoutReport;
		}

		return layout.GetLayoutReport();

	}

	public GraphLayoutReport GetLayoutReport() {

		return this._layoutReport;

	}

	public IEnumerator GetLayouts(Boolean preOrder) {

		return this.GetRecursiveLayout().GetLayouts(preOrder);

	}

	public Integer GetOriginalCoordModeOfModel() {

		return this._originalCoordModeOfModel;

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout GetParentLayout() {

		return this._parentLayout;

	}

	public java.lang.Object GetProperty(String key) {
		if (this._properties == null) {

			return null;
		}

		return this._properties.GetProperty(key);

	}

	public java.lang.Object GetProperty(IGraphModel graphModel,
			java.lang.Object nodeOrLink, String key) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		IPropertyContainer property = (IPropertyContainer) graphModel
				.GetProperty(nodeOrLink, this.NODE_OR_LINK_PROPERTY_NAME);
		if (property == null) {

			return null;
		}

		return property.GetProperty(key);

	}

	// public Margins GetRadialOffset(java.lang.Object node)
	// {
	// if(this.GetGraphicContainer() != null){
	// java.lang.Object obj2 = LayoutParametersUtil.GetNodeParameter(this, node,
	// RADIAL_OFFSET_PROPERTY);
	// if(obj2 != null){
	//
	// return (Margins)obj2;
	// }
	// }
	//
	// return Margins.Empty;
	//
	// }

	public RecursiveLayout GetRecursiveLayout() {
		IGraphModel graphModel = this.GetGraphModel();
		if (graphModel == null) {
			throw (new system.Exception(
					LayoutUtil.NO_ATTACHED_GRAPH_MODEL_OR_NULL));
		}
		if (this instanceof RecursiveLayout) {

			return (RecursiveLayout) this;
		}
		if (this._recursiveLayout == null) {
			this._recursiveLayout = new RecursiveLayout(this);
			Boolean internalGraphModelChecking = GraphModelData.Get(graphModel)
					.get_InternalGraphModelChecking();
			try {
				GraphModelData.SetInternalGraphModelChecking(graphModel, false);
				this._recursiveLayout.Attach(graphModel);
			} finally {
				GraphModelData.SetInternalGraphModelChecking(graphModel,
						internalGraphModelChecking);
			}
		}

		return this._recursiveLayout;

	}

	public long GetRemainingAllowedTime() {
		if ((this._layoutReport != null) && this.SupportsAllowedTime()) {

			return (this.get_AllowedTime() - (TranslateUtil.CurrentTimeMillis() - this._layoutReport
					.GetStartLayoutTime()));
		}

		return -1L;

	}

	public Rectangle2D GetSpecLayoutRegion() {

		if (!this.SupportsLayoutRegion()) {

			return TranslateUtil.InternalRect2Rectangle2D(null);
		}

		return this.GetSpecLayoutRegionImpl();

	}

	private Rectangle2D GetSpecLayoutRegionImpl() {
		Rectangle2D rect = new Rectangle2D();
		IDiagramView view = null;
		if (this._fitToViewEnabled) {
			view = (this._viewToFit == null) ? this.GetFirstManagerView()
					: this._viewToFit;
			if (view == null) {

				return Rectangle2D.Invalid;
			}
			if (this._rectangleToFit == null) {
				Size2D viewSize = view.get_ViewSize();
				rect.set_X(0f);
				rect.set_Y(0f);
				rect.set_Width(viewSize.get_Width());
				rect.set_Height(viewSize.get_Height());
				if (this.get_CoordinatesMode() != ILOG.Diagrammer.GraphLayout.CoordinatesMode.ViewCoordinates) {

					// rect =
					// view.get_Transform().Inverse().TransformRectangle(rect);
				}
				if ((rect.get_Width() >= 1E-20f)
						&& (rect.get_Height() >= 1E-20f)) {

					return rect;
				}

				return Rectangle2D.Invalid;
			}
			rect.set_X(this._rectangleToFit.X);
			rect.set_Y(this._rectangleToFit.Y);
			rect.set_Width(this._rectangleToFit.Width);
			rect.set_Height(this._rectangleToFit.Height);
			if (this.get_CoordinatesMode() != ILOG.Diagrammer.GraphLayout.CoordinatesMode.ViewCoordinates) {

				// rect =
				// view.get_Transform().Inverse().TransformRectangle(rect);
			}

			return rect;
		}
		if (this._rectangleToFit != null) {
			rect.set_X(this._rectangleToFit.X);
			rect.set_Y(this._rectangleToFit.Y);
			rect.set_Width(this._rectangleToFit.Width);
			rect.set_Height(this._rectangleToFit.Height);

			return rect;
		}

		return Rectangle2D.Invalid;

	}

	// public Margins GetTangentialOffset(java.lang.Object node)
	// {
	// if(this.GetGraphicContainer() != null){
	// java.lang.Object obj2 = LayoutParametersUtil.GetNodeParameter(this, node,
	// TANGENTIAL_OFFSET_PROPERTY);
	// if(obj2 != null){
	//
	// return (Margins)obj2;
	// }
	// }
	//
	// return Margins.Empty;
	//
	// }

	public void IncreasePercentageComplete(Integer newPercentage) {
		ILOG.Diagrammer.GraphLayout.GraphLayout parentLayout = this
				.GetParentLayout();
		if (parentLayout != null) {
			parentLayout.IncreasePercentageComplete(newPercentage);
		} else if (((newPercentage >= 0) && (newPercentage <= 100))
				&& (this._layoutReport != null)) {
			Integer percentage = newPercentage;
			if (this._duringConnectedCompLayout
					&& (this._sublayoutCounter < this._numberOfSublayouts)) {
				percentage = (100 * (this._actSublayoutPoints + ((newPercentage * this._sublayoutPoints[this._sublayoutCounter]) / 100)))
						/ this._maxSublayoutPoints;
			}
			if (this._layoutReport.get_PercentageComplete() < percentage) {
				this._layoutReport.SetPercentageComplete(percentage);
			}
		}

	}

	public void Init() {
		this.InitPropertiesNames();
		this._allowedTime = 0x7d00L;
		this._minBusyTime = 50L;
		this._preserveFixedNodesOption = false;
		this._preserveFixedLinksOption = false;
		this._autoLayout = false;
		this._autoLayoutHandler = _defaultAutoLayoutHandler;
		this._seedValue = 0;
		this._userDefinedSeedOption = false;
		this._inputCheckEnabled = true;

		this._connectedCompLayoutEnabled = this
				.IsLayoutOfConnectedComponentsEnabledByDefault();
		this._coordMode = ILOG.Diagrammer.GraphLayout.CoordinatesMode.InverseViewCoordinates;
		this._linkClipping = true;
		this._linkConnectionBoxInterface = _defaultLinkConnectionBoxInterface;
		this._rectangleToFit = _defaultRectangleToFit;
		this._rectangleToFitArgument = _defaultRectangleToFit;
		this._fitToViewEnabled = true;
		this._isConnectedGraph = true;
		this._numberOfSublayouts = 1;
		this._maxSublayoutPoints = 1;
		this._sublayoutPoints = new Integer[] { 1 };

	}

	private void InitPropertiesNames() {
		_uniqueIdCounter++;
		this._uniqueId = _uniqueIdCounter;

		this.NODE_OR_LINK_PROPERTY_NAME = PROPERTY_BASE_NAME
				+ this.GetInstanceId();

	}

	public Boolean IsFitToView() {

		return this._fitToViewEnabled;

	}

	public Boolean IsLayoutNeeded() {
		IGraphModel graphModel = this.GetGraphModel();

		return ((graphModel != null) && graphModel.IsLayoutNeeded(this));

	}

	public Boolean IsLayoutOfConnectedComponentsEnabledByDefault() {

		return false;

	}

	private Boolean IsLayoutRegionChanged(InternalRect oldRect,
			InternalRect newRect) {
		if (oldRect == null) {

			return (newRect != null);
		}
		if ((newRect != null)
				&& (((((float) Math.Round((double) oldRect.X)) == ((float) Math
						.Round((double) newRect.X))) && (((float) Math
						.Round((double) oldRect.Y)) == ((float) Math
						.Round((double) newRect.Y)))) && (((float) Math
						.Round((double) oldRect.Width)) == ((float) Math
						.Round((double) newRect.Width))))) {

			return (((float) Math.Round((double) oldRect.Height)) != ((float) Math
					.Round((double) newRect.Height)));
		}

		return true;

	}

	public Boolean IsLayoutRunning() {

		return this._isLayoutRunning;

	}

	public Boolean IsLayoutTimeElapsed() {

		if (!this.SupportsAllowedTime()) {

			return false;
		}
		if (this._layoutReport == null) {

			return false;
		}

		return ((TranslateUtil.CurrentTimeMillis() - this._layoutReport
				.GetStartLayoutTime()) > this.get_AllowedTime());

	}

	public Boolean IsStoppedImmediately() {

		return this._isStoppedImmediately;

	}

	public abstract void Layout();

	public void ModelContentsChanged(java.lang.Object sender,
			GraphModelContentsChangedEventArgs e) {
		this.ContentsChangedImpl(e);

	}

	// public void OnAttached()
	// {
	// this.UpdateLayoutRegion();
	// if(this.Attached != null){
	// for(EventHandler h:this.Attached ){
	// h.DynamicInvoke(new java.lang.Object[]{this, EventArgs.Empty});
	// }
	// }
	//
	// }

	public void OnDetached() {
		if (this.Detached != null) {
			for (EventHandler h : this.Detached) {
				h.DynamicInvoke(new java.lang.Object[] { this, EventArgs.Empty });
			}
		}

	}

	public void OnLayoutStepPerformed(Boolean layoutStarted,
			Boolean layoutFinished) {
		ILOG.Diagrammer.GraphLayout.GraphLayout parentLayout = this
				.GetParentLayout();
		if (parentLayout != null) {
			parentLayout.OnLayoutStepPerformed(false, false);
		} else if (this.LayoutStepPerformed != null) {
			GraphLayoutStepPerformedEventArgs e = new GraphLayoutStepPerformedEventArgs(
					this);
			e.SetLayoutStarted(layoutStarted);
			e.SetLayoutFinished(layoutFinished);
			for (GraphLayoutStepPerformedEventHandler h : LayoutStepPerformed) {
				h.handle(this, e);
			}
		}

	}

	public void OnLayoutStepPerformedIfNeeded() {
		if (!this._isStoppedPrematurely) {
			long num = TranslateUtil.CurrentTimeMillis();
			if ((num > (this._lastLayoutStepPerformedTime + this
					.get_MinBusyTime()))
					|| (num < this._lastLayoutStepPerformedTime)) {
				this.OnLayoutStepPerformed(false, false);

				this._lastLayoutStepPerformedTime = TranslateUtil
						.CurrentTimeMillis();
				if ((this.SupportsAllowedTime() && this.IsLayoutTimeElapsed())
						|| (this.SupportsStopImmediately() && this
								.IsStoppedImmediately())) {
					this._isStoppedPrematurely = true;
				}
			}
		}

	}

	public void OnParameterChanged(String parameterName) {
		// this.OnParameterChangedImpl(null,parameterName);

	}

	public void OnParameterChanged(java.lang.Object nodeOrLink,
			String parameterName) {
		// this.OnParameterChangedImpl(nodeOrLink,parameterName);

	}

	// private void OnParameterChangedImpl(java.lang.Object nodeOrLink, String
	// parameterName)
	// {
	// this.set_ParametersUpToDate(false);
	// if(((parameterName == null) || (parameterName.length() == 0)) ||
	// (parameterName.charAt(0) != '_')){
	// this.FireLayoutPropertyChanged(false,nodeOrLink,parameterName);
	// }
	//
	// }

	public void PerformAutoLayout() {
		IAutoLayoutHandler autoLayoutHandler = this.get_AutoLayoutHandler();
		if (autoLayoutHandler != null) {
			autoLayoutHandler.PerformAutoLayout(this, null);
		} else {
			try {
				this.PerformLayout(true);
			} catch (system.Exception exception) {
				throw (exception);
			}
		}

	}

	public GraphLayoutReport PerformLayout() {

		return this.PerformLayout(false);

	}

	public GraphLayoutReport PerformLayout(Boolean force) {

		return this.PerformNonRecursiveLayout(force, false);

	}

	public int PerformLayout(Boolean force, Boolean traverse) {
		if (traverse) {

			return this.PerformRecursiveLayout(force, false);
		}

		return this.PerformLayout(force).get_Code();

	}

	private GraphLayoutReport PerformNonRecursiveLayout(Boolean force,
			Boolean redraw) {
		IGraphModel graphModel = this.GetGraphModel();
		if (graphModel == null) {
			throw (new GraphLayoutException(
					LayoutUtil.NO_ATTACHED_GRAPH_MODEL_OR_NULL));
		}
		if (this._isLayoutRunning) {
			throw (new system.Exception(
					"You cannot start this layout while it is still running"));
		}
		this._connectedCompLayoutReport = null;
		this._isLayoutRunning = true;
		try {
			this.SetLayoutReport(this.CreateLayoutReport());
			if (this._layoutReport == null) {
				throw (new GraphLayoutException(
						"Failed to create the layout report."));
			}
			this._layoutReport.SetStartLayoutTime(TranslateUtil
					.CurrentTimeMillis());
			this._layoutReport.SetPercentageComplete(0);
			this._isStoppedImmediately = false;
			this._isStoppedPrematurely = false;
			this._lastLayoutStepPerformedTime = 0L;
			Boolean beforeLayoutWasDone = false;
			Boolean exceptionDuringLayout = false;
			try {

				if (LayoutUtil.IsEmpty(graphModel)) {
					this._layoutReport
							.set_Code(GraphLayoutReportCode.EmptyGraph);

					return this._layoutReport;
				}
				beforeLayoutWasDone = true;
				this.BeforeLayout();
				this._isConnectivityTestDone = false;
				this._isConnectedGraph = true;

				if (!force && !this.IsLayoutNeeded()) {
					this._layoutReport
							.set_Code(GraphLayoutReportCode.NotNeeded);

					return this._layoutReport;
				}
				this._layoutReport
						.set_Code(GraphLayoutReportCode.LayoutStarted);
				this.OnLayoutStepPerformed(true, false);
				this._layoutReport.set_Code(GraphLayoutReportCode.InitialCode);

				if ((((this._duringConnectedCompLayout || !this
						.SupportsLayoutOfConnectedComponents()) || !this
						.get_LayoutOfConnectedComponentsEnabled()) || (this._isConnectivityTestDone && this._isConnectedGraph))
						|| ((this._isConnectedGraph = GraphLayoutUtil
								.IsConnected(graphModel)) && (this._isConnectivityTestDone = true))) {
					GraphModelUtil.SetContentsAdjusting(graphModel, true, true);
					this.BeforeLayoutOfSubgraph(graphModel);
					GraphModelUtil.ApplyLayout(graphModel, this, redraw);
					this.AfterLayoutOfSubgraph(graphModel);
				} else {
					this.DoLayoutOfConnectedComponents(graphModel, force,
							redraw);
				}
			} catch (system.Exception exception) {
				exceptionDuringLayout = true;
				throw (exception);
			} finally {
				this.ToDoAfterLayout(beforeLayoutWasDone,
						exceptionDuringLayout, redraw);
				GraphModelUtil.SetContentsAdjusting(graphModel, false, true);
			}
		} finally {
			this._isLayoutRunning = false;
			this._duringConnectedCompLayout = false;
		}

		return this._layoutReport;

	}

	// 做TREE的时候修改的
	// private GraphLayoutReport PerformNonRecursiveLayout(bool force, bool
	// redraw)
	// {
	// IGraphModel graphModel = this.GetGraphModel();
	// try
	// {
	// GraphModelUtil.ApplyLayout(graphModel, this, redraw);
	// }
	// catch (Exception exception)
	// {
	// }
	// return this._layoutReport;
	// }
	private int PerformRecursiveLayout(Boolean force, Boolean redraw) {

		return this.GetRecursiveLayout().PerformLayout(force).get_Code();

	}

	public int PerformSublayout(java.lang.Object subgraph,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout, Boolean force) {

		return layout.PerformLayout(force, false);

	}

	private void RecordLayoutRegion() {

		if (this.SupportsLayoutRegion()) {
			InternalRect rect = TranslateUtil.Rectangle2D2InternalRect(this
					.GetCalcLayoutRegion());
			this._oldLayoutRegion = rect;
		}

	}

	// private void ReferenceViewChanged(java.lang.Object sender, EventArgs e)
	// {
	// GraphicContainerAdapter adapter = (GraphicContainerAdapter)sender;
	// if(adapter.get_ReferenceView() != null){
	// this.UpdateLayoutRegion();
	// //adapter.ReferenceViewChanged -= this.ReferenceViewChanged;
	// }
	//
	// }

	public void RemoveFromModelEventHandlers(IGraphModel model) {

		if (this.modelsWeListenTo.Contains(model)) {
			// model.ContentsChanged -= this.ModelContentsChanged;
			this.modelsWeListenTo.Remove(model);
		}

	}

	public void RemoveLayoutProviderThatContainsMe(
			DefaultLayoutProvider layoutProvider) {
		if (this._providersThatContainMe != null) {
			if (this._providersThatContainMe == layoutProvider) {
				this._providersThatContainMe = null;
			} else if (this._providersThatContainMe instanceof ArrayList) {
				ArrayList list = (ArrayList) this._providersThatContainMe;
				list.remove(layoutProvider);
				if (list.size() == 1) {
					this._providersThatContainMe = list.get(0);
				}
			}
		}

	}

	public void SetFitToView(Boolean flag) {
		this._fitToViewEnabled = flag;

	}

	public void SetFixed(java.lang.Object nodeOrLink, Boolean f) {
		if (this._graphModel != null) {

			if (this._graphModel.IsNode(nodeOrLink)) {

				if (!this.SupportsPreserveFixedNodes()) {
					throw (new system.Exception(
							"this layout class does not support the fixed nodes preservation"));
				}
			}

			else if ((this._graphModel.IsLink(nodeOrLink) || this._graphModel
					.IsInterGraphLink(nodeOrLink))
					&& !this.SupportsPreserveFixedLinks()) {
				throw (new system.Exception(
						"this layout class does not support the fixed links preservation"));
			}
		}
		LayoutParametersUtil.SetNodeOrLinkParameter(this, nodeOrLink,
				FIXED_PROPERTY, f, false);

	}

	public void SetGraphModel(IGraphModel graphModel) {
		this._graphModel = graphModel;
		this.set_StructureUpToDate(false);
		this.set_GeometryUpToDate(false);

	}

	// public void SetIgnored(java.lang.Object nodeOrLink, Boolean ignored)
	// {
	// GraphicObject obj2 = (GraphicObject)nodeOrLink;
	// if(obj2 != null){
	// String key = this.NODE_OR_LINK_PROPERTY_NAME + IGNORED_PROPERTY;
	// if(ignored){
	// obj2.get_Properties().set_Item(key,true);
	// }
	// else{
	// obj2.get_Properties().Remove(key);
	// }
	// }
	//
	// }

	public void SetLayoutReport(GraphLayoutReport report) {
		this._layoutReport = report;

	}

	public void SetOriginalCoordModeOfModel(Integer mode) {
		this._originalCoordModeOfModel = mode;

	}

	public void SetParentLayout(ILOG.Diagrammer.GraphLayout.GraphLayout parent) {
		this._parentLayout = parent;

	}

	public java.lang.Object SetProperty(String key, java.lang.Object val) {
		if (this._properties == null) {
			this._properties = new DefaultPropertyContainer();
		}

		return this._properties.SetProperty(key, val);

	}

	public java.lang.Object SetProperty(IGraphModel graphModel,
			java.lang.Object nodeOrLink, String key, java.lang.Object val) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		IPropertyContainer property = (IPropertyContainer) graphModel
				.GetProperty(nodeOrLink, this.NODE_OR_LINK_PROPERTY_NAME);
		if ((property == null) && (val != null)) {
			property = new DefaultPropertyContainer();
			graphModel.SetProperty(nodeOrLink, this.NODE_OR_LINK_PROPERTY_NAME,
					property);
		}
		if (property == null) {

			return null;
		}

		return property.SetProperty(key, val);

	}

	// public void SetRadialOffset(java.lang.Object node, Margins offset)
	// {
	// if(this.GetGraphicContainer() != null){
	// if(offset != Margins.Empty){
	// LayoutParametersUtil.SetNodeParameter(this,node,RADIAL_OFFSET_PROPERTY,offset);
	// }
	// else{
	// LayoutParametersUtil.SetNodeParameter(this,node,RADIAL_OFFSET_PROPERTY,null);
	// }
	// }
	//
	// }

	// public void SetTangentialOffset(java.lang.Object node, Margins offset)
	// {
	// if(this.GetGraphicContainer() != null){
	// if(offset != Margins.Empty){
	// LayoutParametersUtil.SetNodeParameter(this,node,TANGENTIAL_OFFSET_PROPERTY,offset);
	// }
	// else{
	// LayoutParametersUtil.SetNodeParameter(this,node,TANGENTIAL_OFFSET_PROPERTY,null);
	// }
	// }
	//
	// }

	public Boolean StopImmediately() {

		if (!this.SupportsStopImmediately()) {

			return false;
		}
		if (this._isStoppedImmediately) {

			return true;
		}
		this._isStoppedImmediately = true;
		Boolean flag = true;
		ILOG.Diagrammer.GraphLayout.GraphLayout layout = this._connectedComponentsLayout;
		if (((layout != null) && layout.IsLayoutRunning())
				&& layout.SupportsStopImmediately()) {

			flag &= layout.StopImmediately();
		}

		return flag;

	}

	public Boolean SupportsAllowedTime() {

		return false;

	}

	public Boolean SupportsLayoutOfConnectedComponents() {

		return false;

	}

	public Boolean SupportsLayoutRegion() {

		return false;

	}

	public Boolean SupportsLinkConnectionBox() {

		return false;

	}

	public Boolean SupportsPercentageComplete() {

		return false;

	}

	public Boolean SupportsPreserveFixedLinks() {

		return false;

	}

	public Boolean SupportsPreserveFixedNodes() {

		return false;

	}

	public Boolean SupportsRandomGenerator() {

		return false;

	}

	public Boolean SupportsStopImmediately() {

		return false;

	}

	private void ToDoAfterLayout(Boolean beforeLayoutWasDone,
			Boolean exceptionDuringLayout, Boolean redraw) {
		if (beforeLayoutWasDone) {
			GraphModelUtil.AfterLayout(this.GetGraphModel(), this, redraw);
		}
		int code = this._layoutReport.get_Code();
		if (!exceptionDuringLayout
				&& ((code == GraphLayoutReportCode.LayoutDone) || (code == GraphLayoutReportCode.StoppedAndValid))) {
			this.RecordLayoutRegion();
			this.set_StructureUpToDate(true);
			this.set_GeometryUpToDate(true);
			this.set_ParametersUpToDate(true);
		}
		this._layoutReport.SetEndLayoutTime(TranslateUtil.CurrentTimeMillis());
		try {
			this.IncreasePercentageComplete(100);
			this._layoutReport.set_Code(GraphLayoutReportCode.LayoutFinished);
			this.OnLayoutStepPerformed(false, false);
		} finally {
			this._layoutReport
					.set_Code(exceptionDuringLayout ? GraphLayoutReportCode.ExceptionDuringLayout
							: code);
			this.OnLayoutStepPerformed(false, true);
		}

	}

	public void UnfixAllLinks() {

		if (!this.SupportsPreserveFixedLinks()) {
			throw (new system.Exception(
					"this layout class does not support the fixed links preservation"));
		}
		if (this._graphModel == null) {
			throw (new system.Exception(
					LayoutUtil.NO_ATTACHED_GRAPH_MODEL_OR_NULL));
		}
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this._graphModel.get_Links());

		while (enumerator.HasMoreElements()) {
			this.SetFixed(enumerator.NextElement(), false);
		}

		enumerator = TranslateUtil.Collection2JavaStyleEnum(this._graphModel
				.get_InterGraphLinks());

		while (enumerator.HasMoreElements()) {
			this.SetFixed(enumerator.NextElement(), false);
		}

	}

	public void UnfixAllNodes() {

		if (!this.SupportsPreserveFixedNodes()) {
			throw (new system.Exception(
					"this layout class does not support the fixed nodes preservation"));
		}
		if (this._graphModel == null) {
			throw (new system.Exception(
					LayoutUtil.NO_ATTACHED_GRAPH_MODEL_OR_NULL));
		}
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this._graphModel.get_Nodes());

		while (enumerator.HasMoreElements()) {
			this.SetFixed(enumerator.NextElement(), false);
		}

	}

	public void UpdateLayoutRegion() {
		GraphLayoutRegionMode viewBounds = this._layoutRegionMode;
		if (((this._rectangleToFitArgument == null) || (this._rectangleToFitArgument.Width < 0f))
				|| (this._rectangleToFitArgument.Height < 0f)) {
			viewBounds = GraphLayoutRegionMode.ViewBounds;
		}
		if (viewBounds == GraphLayoutRegionMode.RectangleInGraphicContainerCoordinates) {
			this._rectangleToFit = this._rectangleToFitArgument;
			this.SetFitToView(false);
			this._viewToFit = null;
		} else {
			this._viewToFit = null;
			GraphicContainerAdapter graphModel = (GraphicContainerAdapter) this
					.GetGraphModel();
			if (graphModel != null) {
				this._viewToFit = graphModel.get_ReferenceView();
				if (this._viewToFit == null) {
					// graphModel.ReferenceViewChanged +=
					// this.ReferenceViewChanged;
				}
			}
			if (this._viewToFit != null) {
				if (viewBounds == GraphLayoutRegionMode.RectangleInViewCoordinates) {
					this._rectangleToFit = this._rectangleToFitArgument;
				} else {
					this._rectangleToFit = null;
				}
				this.SetFitToView(true);
			} else {
				this._rectangleToFit = this._rectangleToFitArgument;
				this.SetFitToView(false);
			}
		}

	}

	public long get_AllowedTime() {

		if (!this.SupportsAllowedTime()) {

			return 0x7fffffffffffffffL;
		}

		return this._allowedTime;
	}

	public void set_AllowedTime(long value) {

		if (!this.SupportsAllowedTime()) {
			throw (new system.Exception(
					"this layout class does not support the allowed time mechanism"));
		}
		if (value <= 0L) {
			throw (new ArgumentException(
					"Allowed time cannot be negative or zero!"));
		}
		if (this._allowedTime != value) {
			this._allowedTime = value;
			this.OnParameterChanged("AllowedTime");
		}
	}

	public Boolean get_AutoLayout() {

		return this._autoLayout;
	}

	public void set_AutoLayout(Boolean value) {
		if (value != this._autoLayout) {
			this._autoLayout = value;
			this.OnParameterChanged("AutoLayout");
		}
	}

	public IAutoLayoutHandler get_AutoLayoutHandler() {

		return this._autoLayoutHandler;
	}

	public void set_AutoLayoutHandler(IAutoLayoutHandler value) {
		if (value != this._autoLayoutHandler) {
			this._autoLayoutHandler = value;
			this.OnParameterChanged("AutoLayoutHandler");
		}
	}

	public ILOG.Diagrammer.GraphLayout.CoordinatesMode get_CoordinatesMode() {

		return this._coordMode;
	}

	public void set_CoordinatesMode(
			ILOG.Diagrammer.GraphLayout.CoordinatesMode value) {
		if (((value != ILOG.Diagrammer.GraphLayout.CoordinatesMode.GraphicContainerCoordinates) && (value != ILOG.Diagrammer.GraphLayout.CoordinatesMode.ViewCoordinates))
				&& (value != ILOG.Diagrammer.GraphLayout.CoordinatesMode.InverseViewCoordinates)) {
			throw (new ArgumentException("Unsupported coordinates mode: "
					+ (value.toString())));
		}
		if (value != this._coordMode) {
			this._coordMode = value;
			this.set_GeometryUpToDate(false);
			this.OnParameterChanged("CoordinatesMode");
		}
	}

	public Boolean get_GeometryUpToDate() {

		return this._geometryUpToDate;
	}

	public void set_GeometryUpToDate(Boolean value) {
		this._geometryUpToDate = value;
		if (!value) {
			this._estimatedLayoutRegion = null;
		}
	}

	public Boolean get_InputCheckEnabled() {

		return this._inputCheckEnabled;
	}

	public void set_InputCheckEnabled(Boolean value) {
		if (value != this._inputCheckEnabled) {
			this._inputCheckEnabled = value;
			this.OnParameterChanged("InputCheckEnabled");
		}
	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout get_LayoutOfConnectedComponents() {

		if (!this.SupportsLayoutOfConnectedComponents()) {

			return null;
		}
		if (this._connectedComponentsLayout == null) {
			GridLayout layout = new GridLayout();
			layout.SetParentLayout(this);
			layout.set_LayoutMode(GridLayoutMode.TileToRows);
			layout.set_LayoutRegion(new Rectangle2D(0f, 0f, 800f, 800f));
			this._connectedComponentsLayout = layout;
		}

		return this._connectedComponentsLayout;
	}

	public void set_LayoutOfConnectedComponents(
			ILOG.Diagrammer.GraphLayout.GraphLayout value) {

		if (!this.SupportsLayoutOfConnectedComponents()) {
			throw (new system.Exception(
					"this layout class does not support the connected component layout mechanism"));
		}
		if (this._connectedComponentsLayout != null) {
			this._connectedComponentsLayout.SetParentLayout(null);
			this._connectedComponentsLayout.Detach();
		}
		if (value != this._connectedComponentsLayout) {
			if (value != null) {
				value.SetParentLayout(this);
			}
			this._connectedComponentsLayout = value;
			this.set_StructureUpToDate(false);
			this.OnParameterChanged("LayoutOfConnectedComponents");
		}
	}

	public Boolean get_LayoutOfConnectedComponentsEnabled() {

		return this._connectedCompLayoutEnabled;
	}

	public void set_LayoutOfConnectedComponentsEnabled(Boolean value) {

		if (!this.SupportsLayoutOfConnectedComponents()) {
			throw (new system.Exception(
					"this layout class does not support the connected component layout mechanism"));
		}
		if (this._connectedCompLayoutEnabled != value) {
			this._connectedCompLayoutEnabled = value;
			this.set_StructureUpToDate(false);
			this.OnParameterChanged("LayoutOfConnectedComponentsEnabled");
		}
	}

	public Rectangle2D get_LayoutRegion() {

		return TranslateUtil
				.InternalRect2Rectangle2D(this._rectangleToFitArgument);
	}

	public void set_LayoutRegion(Rectangle2D value) {
		if (value != this.get_LayoutRegion()) {

			if (!this.SupportsLayoutRegion()) {
				throw (new system.Exception(
						"this layout class does not support the layout region mechanism"));
			}

			this._rectangleToFitArgument = TranslateUtil
					.Rectangle2D2InternalRect(value);
			this.UpdateLayoutRegion();
			this.OnParameterChanged("LayoutRegion");
		}
	}

	public GraphLayoutRegionMode get_LayoutRegionMode() {

		return this._layoutRegionMode;
	}

	public void set_LayoutRegionMode(GraphLayoutRegionMode value) {
		if (value != this._layoutRegionMode) {

			if (!this.SupportsLayoutRegion()) {
				throw (new system.Exception(
						"this layout class does not support the layout region mechanism"));
			}
			this._layoutRegionMode = value;
			this.UpdateLayoutRegion();
			this.OnParameterChanged("LayoutRegionMode");
		}
	}

	public Boolean get_LinkClipping() {

		return this._linkClipping;
	}

	public void set_LinkClipping(Boolean value) {
		if (value != this._linkClipping) {
			this._linkClipping = value;
			this.OnParameterChanged("LinkClipping");
		}
	}

	public ILinkConnectionBoxProvider get_LinkConnectionBoxProvider() {

		return this._linkConnectionBoxInterface;
	}

	public void set_LinkConnectionBoxProvider(ILinkConnectionBoxProvider value) {

		if (!this.SupportsLinkConnectionBox()) {
			throw (new system.Exception(
					"this layout class does not support the link connection box mechanism"));
		}
		if (value != this._linkConnectionBoxInterface) {
			this._linkConnectionBoxInterface = value;
			this.OnParameterChanged("LinkConnectionBoxInterface");
		}
	}

	public long get_MinBusyTime() {

		return this._minBusyTime;
	}

	public void set_MinBusyTime(long value) {
		if (this._minBusyTime != value) {
			this._minBusyTime = value;
			this.OnParameterChanged("MinBusyTime");
		}
	}

	public String get_Name() {
		String name = this._name;
		if (clr.System.StringStaticWrapper.IsNullOrEmpty(name)
				&& (this.get_Site() != null)) {
			name = this.get_Site().get_Name();
		}
		if (name != null) {

			return name;
		}

		return "";
	}

	public void set_Name(String value) {
		this._name = value;
	}

	public Boolean get_ParametersUpToDate() {
		if (!this._parametersUpToDate) {

			return false;
		}
		if (this.SupportsLayoutOfConnectedComponents()
				&& this.get_LayoutOfConnectedComponentsEnabled()) {
			ILOG.Diagrammer.GraphLayout.GraphLayout layout = this._connectedComponentsLayout;
			if ((layout != null) && !layout.get_ParametersUpToDate()) {

				this._isConnectedGraph = GraphLayoutUtil.IsConnected(this
						.GetGraphModel());
				this._isConnectivityTestDone = true;
				if (!this._isConnectedGraph) {
					this._parametersUpToDate = false;

					return false;
				}
			}
		}

		if (this.SupportsLayoutRegion()) {
			InternalRect newRect = TranslateUtil.Rectangle2D2InternalRect(this
					.GetCalcLayoutRegion());

			if (this.IsLayoutRegionChanged(this._oldLayoutRegion, newRect)) {
				this._parametersUpToDate = false;

				return false;
			}
		}

		return true;
	}

	public void set_ParametersUpToDate(Boolean value) {
		if (value != this._parametersUpToDate) {
			this._parametersUpToDate = value;
			// this.FireLayoutPropertyChanged(value,null,"ParametersUpToDate");
		}
	}

	public Boolean get_PreserveFixedLinks() {

		if (!this.SupportsPreserveFixedLinks()) {

			return false;
		}

		return this._preserveFixedLinksOption;
	}

	public void set_PreserveFixedLinks(Boolean value) {

		if (!this.SupportsPreserveFixedLinks()) {
			throw (new system.Exception(
					"this layout class does not support the fixed links preservation"));
		}
		if (this._preserveFixedLinksOption != value) {
			this._preserveFixedLinksOption = value;
			this.OnParameterChanged("PreserveFixedLinks");
		}
	}

	public Boolean get_PreserveFixedNodes() {

		if (!this.SupportsPreserveFixedNodes()) {

			return false;
		}

		return this._preserveFixedNodesOption;
	}

	public void set_PreserveFixedNodes(Boolean value) {

		if (!this.SupportsPreserveFixedNodes()) {
			throw (new system.Exception(
					"this layout class does not support the fixed nodes preservation"));
		}
		if (this._preserveFixedNodesOption != value) {
			this._preserveFixedNodesOption = value;
			this.OnParameterChanged("PreserveFixedNodes");
		}
	}

	public Integer get_SeedValueForRandomGenerator() {

		return this._seedValue;
	}

	public void set_SeedValueForRandomGenerator(Integer value) {

		if (!this.SupportsRandomGenerator()) {
			throw (new system.Exception(
					"this layout class does not support the random generator seed value mechanism"));
		}
		if (value != this._seedValue) {
			this._seedValue = value;
			this.OnParameterChanged("SeedValueForRandomGenerator");
		}
	}

	public ISite get_Site() {

		return ((ISite) this.GetProperty("IComponent_Site"));
	}

	public void set_Site(ISite value) {
		this.SetProperty("IComponent_Site", value);
	}

	public Boolean get_StructureUpToDate() {

		return this._structureUpToDate;
	}

	public void set_StructureUpToDate(Boolean value) {
		this._structureUpToDate = value;
		if (!value) {
			this._estimatedLayoutRegion = null;
		}
	}

	public Boolean get_UseSeedValueForRandomGenerator() {

		return this._userDefinedSeedOption;
	}

	public void set_UseSeedValueForRandomGenerator(Boolean value) {

		if (!this.SupportsRandomGenerator()) {
			throw (new system.Exception(
					"this layout class does not support the random generator seed value mechanism"));
		}
		if (this._userDefinedSeedOption != value) {
			this._userDefinedSeedOption = value;
			this.OnParameterChanged("UseSeedValueForRandomGenerator");
		}
	}

}