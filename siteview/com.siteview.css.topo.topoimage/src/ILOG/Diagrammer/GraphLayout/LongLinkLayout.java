package ILOG.Diagrammer.GraphLayout;

import system.ArgumentException;
import system.EventHandler;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.ComponentModel.PropertyChangedEventHandler;
import ILOG.Diagrammer.Point2D;
import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelUtil;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.SubgraphData;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;
import ILOG.Diagrammer.GraphLayout.Internal.LLLayout.LLGraph;
import ILOG.Diagrammer.GraphLayout.Internal.LLLayout.LLIncrementalData;

public class LongLinkLayout extends ILOG.Diagrammer.GraphLayout.GraphLayout {
	private long _allowedTimePerLink;

	public Boolean _allowStopImmediately = false;

	private Boolean _combinedInterGraphLinksMode = false;

	private Boolean _crossingReduction = false;

	public long _defaultAllowedTimePerLink = 0x7d0L;

	public Boolean _defaultCombinedInterGraphLinksMode = true;

	public Boolean _defaultCrossingReduction = true;

	public Integer _defaultDestinationPointMode = 0;

	public Boolean _defaultExhaustiveSearching = false;

	public float _defaultGridXBase = 0f;

	public float _defaultGridXOffset = 5f;

	public float _defaultGridYBase = 0f;

	public float _defaultGridYOffset = 5f;

	public Boolean _defaultInterGraphLinksMode = true;

	public Integer _defaultLinkStyle = 2;

	public Integer _defaultMaxBacktrack = 0x7530;

	public float _defaultMinNodeCornerOffset = 3f;

	public float _defaultMinStartEndSegmentLength = 10f;

	public float _defaultMinXOffset = 3f;

	public float _defaultMinYOffset = 3f;

	public static INodeBoxProvider _defaultNodeBoxInterface = null;

	public static INodeSideFilter _defaultNodeSideFilter = null;

	public Integer _defaultNumberCrossingReductionIterations = 2;

	public Integer _defaultOriginPointMode = 0;

	public Boolean _defaultStraightRoute = true;

	public static ILongLinkTerminationPointFilter _defaultTerminationPointFilter = null;

	private Integer _destinationPointMode;

	private Boolean _exhaustiveSearching = false;

	private ArrayList _fallbackLinks;

	private Boolean _fallbackRouteEnabled = false;

	private Integer _globalLinkStyle;

	private float _gridXBase;

	private float _gridXOffset;

	private float _gridYBase;

	private float _gridYOffset;

	private Boolean _incrementalConnectionPreserving = false;

	private LLIncrementalData _incrementalData;

	private Boolean _interGraphLinksMode = false;

	private SubgraphData _interGraphLinksRoutingModel;

	private ArrayList _lineFromObstacles;

	private ArrayList _lineObstacles;

	private ArrayList _lineToObstacles;

	private Boolean _linkObstacleEnabled = false;

	private ArrayList _linksToBeRouted;

	private Integer _maxBackTrack;

	private float _minEndSegmentLength;

	private float _minNodeCornerOffset;

	private float _minStartSegmentLength;

	private float _minXOffset;

	private float _minYOffset;

	private INodeBoxProvider _nodeBoxInterfaceForObstacle;

	private Boolean _nodeObstacleEnabled = false;

	private INodeSideFilter _nodeSideFilter;

	private Integer _numberCrossingReductionIterations;

	private Integer _numberOfSteps;

	private Integer _originPointMode;

	private ArrayList _rectObstacles;

	private Integer _stepCount;

	private Boolean _stoppedBeforeCompletion = false;

	private Boolean _straightRoute = false;

	private ILongLinkTerminationPointFilter _terminationPointFilter;

	private static String DESTINATION_POINT_MODE_PROPERTY = "DestinationPointMode";

	private static String LINK_STYLE_PROPERTY = "LinkStyle";

	private static String ORIGIN_POINT_MODE_PROPERTY = "OriginPointMode";

	public LongLinkLayout() {
	}

	public LongLinkLayout(LongLinkLayout source) {
		super(source);
	}

	public void AddCalcFallbackLink(java.lang.Object link) {
		((LongLinkLayoutReport) super.GetLayoutReport())
				.IncrNumberOfFallbackLinks();
		this._fallbackLinks.Add(link);

	}

	public void AddLineObstacle(Rectangle2D obstacleBoundingBox) {
		if ((obstacleBoundingBox.get_Width() != 0f)
				&& (obstacleBoundingBox.get_Height() != 0f)) {
			throw (new ArgumentException(
					"line obstacle must have zero width or zero height"));
		}
		this._lineObstacles.Add(obstacleBoundingBox);
		this.OnParameterChanged("Obstacles");

	}

	public void AddLineObstacle(Point2D p1, Point2D p2) {
		this._lineFromObstacles.Add(p1);
		this._lineToObstacles.Add(p2);
		this.OnParameterChanged("Obstacles");

	}

	public void AddRectObstacle(Rectangle2D obstacleBoundingBox) {
		this._rectObstacles.Add(obstacleBoundingBox);
		this.OnParameterChanged("Obstacles");

	}

	public void AddStepCount(Integer x) {
		this._stepCount += x;

	}

	@Override
	public void CleanLink(IGraphModel graphModel, java.lang.Object link) {
		super.CleanLink(graphModel, link);
		if (this._incrementalData != null) {
			this._incrementalData.CleanLink(link);
		}

	}

	@Override
	public void CleanNode(IGraphModel graphModel, java.lang.Object node) {
		super.CleanNode(graphModel, node);
		if (this._incrementalData != null) {
			this._incrementalData.CleanNode(node);
		}

	}

	private void ClearIncrementalData() {
		if (this._incrementalData != null) {
			this._incrementalData = new LLIncrementalData();
		}

	}

	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout Copy() {

		return new LongLinkLayout(this);

	}

	@Override
	public void CopyParameters(ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		super.CopyParameters(source);
		if (source instanceof LongLinkLayout) {
			LongLinkLayout layout = (LongLinkLayout) source;
			this.set_LinkStyle(layout.get_LinkStyle());
			this.set_OriginPointMode(layout.get_OriginPointMode());
			this.set_DestinationPointMode(layout.get_DestinationPointMode());
			this.set_HorizontalGridBase(layout.get_HorizontalGridBase());
			this.set_VerticalGridBase(layout.get_VerticalGridBase());
			this.set_HorizontalGridOffset(layout.get_HorizontalGridOffset());
			this.set_VerticalGridOffset(layout.get_VerticalGridOffset());
			this.set_HorizontalMinOffset(layout.get_HorizontalMinOffset());
			this.set_VerticalMinOffset(layout.get_VerticalMinOffset());
			this.set_MinNodeCornerOffset(layout.get_MinNodeCornerOffset());
			this.set_MinStartSegmentLength(layout.get_MinStartSegmentLength());
			this.set_MinEndSegmentLength(layout.get_MinEndSegmentLength());
			this.set_NumberCrossingReductionIterations(layout
					.get_NumberCrossingReductionIterations());
			this.set_AllowedTimePerLink(layout.get_AllowedTimePerLink());
			this.set_ExhaustiveSearching(layout.get_ExhaustiveSearching());
			this.set_CrossingReductionEnabled(layout
					.get_CrossingReductionEnabled());
			this.set_StraightRouteEnabled(layout.get_StraightRouteEnabled());
			this.set_MaxBacktrack(layout.get_MaxBacktrack());
			this.set_NodeObstacleEnabled(layout.get_NodeObstacleEnabled());
			this.set_LinkObstacleEnabled(layout.get_LinkObstacleEnabled());
			this.set_InterGraphLinksMode(layout.get_InterGraphLinksMode());
			this.set_CombinedInterGraphLinksMode(layout
					.get_CombinedInterGraphLinksMode());
			this.set_FallbackRouteEnabled(layout.get_FallbackRouteEnabled());
			this.set_IncrementalMode(layout.get_IncrementalMode());
			this.set_IncrementalConnectionPreserving(layout
					.get_IncrementalConnectionPreserving());
			this.set_NodeBoxProvider(layout.get_NodeBoxProvider());
			this.set_NodeSideFilter(layout.get_NodeSideFilter());
			this.set_TerminationPointFilter(layout.get_TerminationPointFilter());
		}

	}

	@Override
	public GraphLayoutReport CreateLayoutReport() {

		return new LongLinkLayoutReport();

	}

	@Override
	public void Detach() {
		if (this.GetGraphModel() != null) {
			this._interGraphLinksRoutingModel = null;
			this._linksToBeRouted = null;
			this._fallbackLinks.Clear();
			this.ClearIncrementalData();
		}
		super.Detach();

	}

	public ICollection GetCalcFallbackLinks() {

		return TranslateUtil.JavaStyleEnum2Collection(TranslateUtil
				.Collection2JavaStyleEnum(this._fallbackLinks));

	}

	public int GetDestinationPointMode(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				DESTINATION_POINT_MODE_PROPERTY, 0);

	}

	public ArrayList GetFromLineObstacles() {

		return this._lineFromObstacles;

	}

	public LLIncrementalData GetIncrementalData() {

		return this._incrementalData;

	}

	public SubgraphData GetInterGraphLinksRoutingModel() {

		return this._interGraphLinksRoutingModel;

	}

	public ArrayList GetLineObstacles() {

		return this._lineObstacles;

	}

	public int GetLinkStyle(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				LINK_STYLE_PROPERTY, 2);

	}

	private InternalRect GetNodeBox(java.lang.Object node) {

		return GraphModelUtil.BoundingBox(this.GetGraphModel(), node);

	}

	public InternalRect GetNodeBoxForConnection(java.lang.Object node) {
		IGraphModel graphModel = this.GetGraphModel();
		if (this.get_LinkConnectionBoxProvider() != null) {
			if (graphModel instanceof SubgraphData) {

				return TranslateUtil
						.Rectangle2D2InternalRect(((SubgraphData) graphModel)
								.GetNodeBox(
										this.get_LinkConnectionBoxProvider(),
										node));
			}

			return TranslateUtil.GetBox(this.get_LinkConnectionBoxProvider(),
					graphModel, node);
		}
		if (this._nodeBoxInterfaceForObstacle == null) {

			return this.GetNodeBox(node);
		}
		if (graphModel instanceof SubgraphData) {

			return TranslateUtil
					.Rectangle2D2InternalRect(((SubgraphData) graphModel)
							.GetNodeBox(this._nodeBoxInterfaceForObstacle, node));
		}

		return TranslateUtil.GetBox(this._nodeBoxInterfaceForObstacle,
				graphModel, node);

	}

	public InternalRect GetNodeBoxForObstacle(java.lang.Object node) {
		IGraphModel graphModel = this.GetGraphModel();
		if (this._nodeBoxInterfaceForObstacle == null) {

			return this.GetNodeBox(node);
		}
		if (graphModel instanceof SubgraphData) {

			return TranslateUtil
					.Rectangle2D2InternalRect(((SubgraphData) graphModel)
							.GetNodeBox(this._nodeBoxInterfaceForObstacle, node));
		}

		return TranslateUtil.GetBox(this._nodeBoxInterfaceForObstacle,
				graphModel, node);

	}

	public int GetOriginPointMode(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				ORIGIN_POINT_MODE_PROPERTY, 0);

	}

	public ArrayList GetRectObstacles() {

		return this._rectObstacles;

	}

	public Integer GetTerminationPointPenalty(java.lang.Object link,
			java.lang.Object node, Boolean origin, float startX, float startY,
			Integer side, Integer proposedPenalty) {
		ILongLinkTerminationPointFilter terminationPointFilter = this
				.get_TerminationPointFilter();
		if (terminationPointFilter != null) {
			InternalPoint point = new InternalPoint(startX, startY);
			IGraphModel originalGraphModel = this.GetGraphModel();
			if (originalGraphModel instanceof SubgraphData) {
				java.lang.Object original = ((SubgraphData) originalGraphModel)
						.GetOriginal(node);
				java.lang.Object obj3 = ((SubgraphData) originalGraphModel)
						.GetOriginal(link);
				if ((original != null) && (obj3 != null)) {

					originalGraphModel = ((SubgraphData) originalGraphModel)
							.GetOriginalGraphModel(node);
					node = original;
					link = obj3;
				}
			}

			return TranslateUtil.GetPenalty(terminationPointFilter,
					originalGraphModel, link, origin, node, point, (int) side,
					proposedPenalty);
		}
		INodeSideFilter nodeSideFilter = this.get_NodeSideFilter();
		if (nodeSideFilter == null) {

			return proposedPenalty;
		}
		IGraphModel graphModel = this.GetGraphModel();
		if (graphModel instanceof SubgraphData) {
			java.lang.Object obj4 = ((SubgraphData) graphModel)
					.GetOriginal(node);
			java.lang.Object obj5 = ((SubgraphData) graphModel)
					.GetOriginal(link);
			if ((obj4 != null) && (obj5 != null)) {

				graphModel = ((SubgraphData) graphModel)
						.GetOriginalGraphModel(node);
				node = obj4;
				link = obj5;
			}
		}

		if (nodeSideFilter.Accept(this.GetGraphModel(), link, origin, node,
				(int) side)) {

			return proposedPenalty;
		}

		return 0x7fffffff;

	}

	public ArrayList GetToLineObstacles() {

		return this._lineToObstacles;

	}

	@Override
	public void Init() {
		super.Init();
		this._globalLinkStyle = 2;
		this._originPointMode = 0;
		this._destinationPointMode = 0;
		this._gridXBase = 0f;
		this._gridYBase = 0f;
		this._gridXOffset = 5f;
		this._gridYOffset = 5f;
		this._minXOffset = 3f;
		this._minYOffset = 3f;
		this._minNodeCornerOffset = 3f;
		this._minStartSegmentLength = 10f;
		this._minEndSegmentLength = 10f;
		this._numberCrossingReductionIterations = 2;
		this._allowedTimePerLink = 0x7d0L;
		this._exhaustiveSearching = false;
		this._crossingReduction = true;
		this._straightRoute = true;
		this._maxBackTrack = 0x7530;
		this._nodeObstacleEnabled = true;
		this._linkObstacleEnabled = true;
		this._interGraphLinksMode = true;
		this._combinedInterGraphLinksMode = true;
		this._rectObstacles = new ArrayList(3);
		this._lineObstacles = new ArrayList(3);
		this._lineFromObstacles = new ArrayList(3);
		this._lineToObstacles = new ArrayList(3);
		this._fallbackLinks = new ArrayList(3);
		this._fallbackRouteEnabled = true;
		this._nodeBoxInterfaceForObstacle = _defaultNodeBoxInterface;
		this._nodeSideFilter = _defaultNodeSideFilter;
		this._terminationPointFilter = _defaultTerminationPointFilter;
		this._incrementalConnectionPreserving = false;
		this._allowStopImmediately = true;

	}

	public Boolean IsFromPointFixed(java.lang.Object link) {
		int originPointMode = this.get_OriginPointMode();
		if (originPointMode != ConnectionPointMode.Free) {
			if (originPointMode == ConnectionPointMode.Fixed) {

				return true;
			}
			if (originPointMode != ConnectionPointMode.Mixed) {
				throw (new system.Exception("unsupported mode: "
						+ ((Integer) this.get_OriginPointMode())));
			}
		} else {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, true);
		}
		if (this.GetOriginPointMode(link) != ConnectionPointMode.Fixed) {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, true);
		}

		return true;

	}

	public Boolean IsToPointFixed(java.lang.Object link) {
		int destinationPointMode = this.get_DestinationPointMode();
		if (destinationPointMode != ConnectionPointMode.Free) {
			if (destinationPointMode == ConnectionPointMode.Fixed) {

				return true;
			}
			if (destinationPointMode != ConnectionPointMode.Mixed) {
				throw (new system.Exception("unsupported mode: "
						+ ((Integer) this.get_DestinationPointMode())));
			}
		} else {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, false);
		}
		if (this.GetDestinationPointMode(link) != ConnectionPointMode.Fixed) {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, false);
		}

		return true;

	}

	@Override
	public void Layout() {
		Boolean redraw = false;
		if (this.get_LinkStyle() != LinkLayoutLinkStyle.NoReshape) {
			IGraphModel graphModel = this.GetGraphModel();
			if (this.get_InterGraphLinksMode()) {

				if (TranslateUtil.Collection2JavaStyleEnum(
						graphModel.get_InterGraphLinks()).HasMoreElements()) {
					this._interGraphLinksRoutingModel = new SubgraphData();
					this._interGraphLinksRoutingModel.CollectData(this
							.GetGraphModel());
					this._interGraphLinksRoutingModel.SetNormalLinksFixed(!this
							.get_CombinedInterGraphLinksMode());
					this.SetGraphModel(this._interGraphLinksRoutingModel);
				} else if (!this.get_CombinedInterGraphLinksMode()) {
					super.GetLayoutReport().set_Code(
							GraphLayoutReportCode.LayoutDone);

					return;
				}
			}
			try {
				this.LayoutInternal(redraw);
			} finally {
				if (this._interGraphLinksRoutingModel != null) {
					this._interGraphLinksRoutingModel.Clean();
				}
				this.SetGraphModel(graphModel);
				this._interGraphLinksRoutingModel = null;
			}
		}

	}

	private void LayoutInternal(Boolean redraw) {
		this._stoppedBeforeCompletion = false;
		this._allowStopImmediately = true;
		IGraphModel graphModel = this.GetGraphModel();
		LongLinkLayoutReport layoutReport = (LongLinkLayoutReport) super
				.GetLayoutReport();
		layoutReport.SetNumberOfFallbackLinks(0);
		this._numberOfSteps = 1;
		this._stepCount = 0;
		if (this._linksToBeRouted == null) {
			this._numberOfSteps += 13 * graphModel.get_Links().get_Count();
			if (this.get_StraightRouteEnabled()) {
				this._numberOfSteps += 2 * graphModel.get_Links().get_Count();
				;
			}
			if (this.get_CrossingReductionEnabled()) {
				this._numberOfSteps += (2 * (graphModel.get_Links().get_Count() - 1))
						* this.get_NumberCrossingReductionIterations();
			}
		} else {
			this._numberOfSteps += 13 * this._linksToBeRouted.get_Count();
			if (this.get_StraightRouteEnabled()) {
				this._numberOfSteps += 2 * this._linksToBeRouted.get_Count();
			}
			if (this.get_CrossingReductionEnabled()) {
				this._numberOfSteps += (2 * (this._linksToBeRouted.get_Count() - 1))
						* this.get_NumberCrossingReductionIterations();
			}
		}
		if (this.get_IncrementalMode()
				&& (this._interGraphLinksRoutingModel == null)) {
			this._numberOfSteps += graphModel.get_Links().get_Count();
			this._numberOfSteps += 2 * graphModel.get_Nodes().get_Count();
		}
		this._stoppedBeforeCompletion = false;
		this._fallbackLinks.Clear();
		LLGraph graph = new LLGraph(this, redraw);
		super.OnLayoutStepPerformedIfNeeded();
		graph.Attach(graphModel, this);

		if (this.MayContinue()) {
			if (this._linksToBeRouted == null) {
				graph.AttachAllLinks();
			} else {
				graph.AttachLinks(this._linksToBeRouted);
			}
		}

		if (this.MayContinue()) {
			graph.CreateCompleteGrid();
		}

		if (this.MayContinue()) {
			graph.PrepareIncrementalLinks();
		}

		if (this.MayContinue()) {
			graph.RouteAttachedLinks();
		}
		if (this.get_IncrementalMode()
				&& (this._interGraphLinksRoutingModel == null)) {
			this.GetIncrementalData().Update(this);
		}
		this._allowStopImmediately = false;
		graph.Detach();
		graph.Dispose();
		this.IncreasePercentageComplete(100);
		this.OnLayoutStepPerformed(false, false);
		if (this._stoppedBeforeCompletion) {
			layoutReport.set_Code(GraphLayoutReportCode.StoppedAndInvalid);
		} else {
			layoutReport.set_Code(GraphLayoutReportCode.LayoutDone);
		}
		this._allowStopImmediately = true;

	}

	public void MarkForIncremental(java.lang.Object link) {
		if (this.GetIncrementalData() != null) {
			this.GetIncrementalData().Clear(link);
			this.set_ParametersUpToDate(false);
		}

	}

	public Boolean MayContinue() {
		if (!this._stoppedBeforeCompletion) {
			this._stoppedBeforeCompletion = this.IsLayoutTimeElapsed()
					|| this.IsStoppedImmediately();
		}

		return !this._stoppedBeforeCompletion;

	}

	@Override
	public void OnLayoutStepPerformed(Boolean layoutStarted,
			Boolean layoutFinished) {
		if (this._numberOfSteps > 0) {
			this.IncreasePercentageComplete(this._stepCount
					/ this._numberOfSteps);
		}
		super.OnLayoutStepPerformed(layoutStarted, layoutFinished);

	}

	public void RemoveAllLineObstacles() {
		this._lineObstacles.Clear();
		this._lineFromObstacles.Clear();
		this._lineToObstacles.Clear();
		this.OnParameterChanged("Obstacles");

	}

	public void RemoveAllRectObstacles() {
		this._rectObstacles.Clear();
		this.OnParameterChanged("Obstacles");

	}

	public void SetDestinationPointMode(java.lang.Object link, int mode) {
		if ((mode != ConnectionPointMode.Free)
				&& (mode != ConnectionPointMode.Fixed)) {
			throw (new ArgumentException("Unsupported mode: "
					+ ((Integer) mode)));
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				DESTINATION_POINT_MODE_PROPERTY, (int) mode, 0);

	}

	public void SetLinkStyle(java.lang.Object link, int style) {
		if (((style != LinkLayoutLinkStyle.Orthogonal) && (style != LinkLayoutLinkStyle.Direct))
				&& (style != LinkLayoutLinkStyle.NoReshape)) {
			throw (new ArgumentException("unsupported link style: "
					+ ((Integer) style)));
		}

		if (LayoutParametersUtil.SetLinkParameter(this, link,
				LINK_STYLE_PROPERTY, (int) style, 2)) {
			this.MarkForIncremental(link);
		}

	}

	public void SetOriginPointMode(java.lang.Object link, int mode) {
		if ((mode != ConnectionPointMode.Free)
				&& (mode != ConnectionPointMode.Fixed)) {
			throw (new ArgumentException("Unsupported mode: "
					+ ((Integer) mode)));
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				ORIGIN_POINT_MODE_PROPERTY, (int) mode, 0);

	}

	@Override
	public Boolean StopImmediately() {

		if (this.IsStoppedImmediately()) {

			return true;
		}
		if (!this._allowStopImmediately) {

			return false;
		}

		return super.StopImmediately();

	}

	@Override
	public Boolean SupportsAllowedTime() {

		return true;

	}

	@Override
	public Boolean SupportsLinkConnectionBox() {

		return true;

	}

	@Override
	public Boolean SupportsPercentageComplete() {

		return true;

	}

	@Override
	public Boolean SupportsPreserveFixedLinks() {

		return true;

	}

	@Override
	public Boolean SupportsStopImmediately() {

		return true;

	}

	public long get_AllowedTime() {

		return super.get_AllowedTime();
	}

	public void set_AllowedTime(long value) {
		super.set_AllowedTime(value);
	}

	public long get_AllowedTimePerLink() {

		return this._allowedTimePerLink;
	}

	public void set_AllowedTimePerLink(long value) {
		if (value <= 0L) {
			throw (new ArgumentException(
					"Allowed time cannot be negativ or zero!"));
		}
		if (value != this._allowedTimePerLink) {
			this._allowedTimePerLink = value;
			this.OnParameterChanged("AllowedTimePerLink");
		}
	}

	public Boolean get_CombinedInterGraphLinksMode() {

		return this._combinedInterGraphLinksMode;
	}

	public void set_CombinedInterGraphLinksMode(Boolean value) {
		if (value != this._combinedInterGraphLinksMode) {
			this._combinedInterGraphLinksMode = value;
			this.OnParameterChanged("CombinedInterGraphLinksMode");
		}
	}

	public Boolean get_CrossingReductionEnabled() {

		return this._crossingReduction;
	}

	public void set_CrossingReductionEnabled(Boolean value) {
		if (value != this._crossingReduction) {
			this._crossingReduction = value;
			this.OnParameterChanged("CrossingReductionEnabled");
		}
	}

	public int get_DestinationPointMode() {

		return (int) this._destinationPointMode;
	}

	public void set_DestinationPointMode(int value) {
		if (((value != ConnectionPointMode.Free) && (value != ConnectionPointMode.Fixed))
				&& (value != ConnectionPointMode.Mixed)) {
			throw (new ArgumentException("Unsupported mode: "
					+ ((Integer) value)));
		}
		if ((Integer) value != this._destinationPointMode) {
			this._destinationPointMode = (Integer) value;
			this.OnParameterChanged("GlobalDestinationPointMode");
		}
	}

	public Boolean get_ExhaustiveSearching() {

		return this._exhaustiveSearching;
	}

	public void set_ExhaustiveSearching(Boolean value) {
		if (value != this._exhaustiveSearching) {
			this._exhaustiveSearching = value;
			this.OnParameterChanged("ExhaustiveSearching");
		}
	}

	public Boolean get_FallbackRouteEnabled() {

		return this._fallbackRouteEnabled;
	}

	public void set_FallbackRouteEnabled(Boolean value) {
		if (value != this._fallbackRouteEnabled) {
			this._fallbackRouteEnabled = value;
			this.OnParameterChanged("FallbackRouteEnabled");
		}
	}

	public float get_HorizontalGridBase() {

		return this._gridXBase;
	}

	public void set_HorizontalGridBase(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative grid coordinate: " + value));
		}
		if (value != this._gridXBase) {
			this._gridXBase = value;
			this.OnParameterChanged("HorizontalGridBase");
		}
	}

	public float get_HorizontalGridOffset() {

		return this._gridXOffset;
	}

	public void set_HorizontalGridOffset(float value) {
		if (value <= 0f) {
			throw (new ArgumentException("negative or zero grid offset: "
					+ value));
		}
		if (value != this._gridXOffset) {
			this._gridXOffset = value;
			this.OnParameterChanged("HorizontalGridOffset");
		}
	}

	public float get_HorizontalMinOffset() {

		return this._minXOffset;
	}

	public void set_HorizontalMinOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative minimal offset: " + value));
		}
		if (value != this._minXOffset) {
			this._minXOffset = value;
			this.OnParameterChanged("HorizontalMinOffset");
		}
	}

	public Boolean get_IncrementalConnectionPreserving() {

		return this._incrementalConnectionPreserving;
	}

	public void set_IncrementalConnectionPreserving(Boolean value) {
		if (value != this._incrementalConnectionPreserving) {
			this._incrementalConnectionPreserving = value;
			this.OnParameterChanged("IncrementalConnectionPreserving");
		}
		if (this._incrementalData != null) {
			this._incrementalData.Update(this);
		}
	}

	public Boolean get_IncrementalMode() {

		return (this._incrementalData != null);
	}

	public void set_IncrementalMode(Boolean value) {
		if (value) {
			if (this._incrementalData == null) {
				this._incrementalData = new LLIncrementalData();
				this.OnParameterChanged("IncrementalMode");
				this._incrementalData.Update(this);
			}
		} else if (this._incrementalData != null) {
			this._incrementalData = null;
			this.OnParameterChanged("IncrementalMode");
		}
	}

	public Boolean get_InterGraphLinksMode() {

		return this._interGraphLinksMode;
	}

	public void set_InterGraphLinksMode(Boolean value) {
		if (value != this._interGraphLinksMode) {
			this._interGraphLinksMode = value;
			this.OnParameterChanged("InterGraphLinksMode");
		}
	}

	public ILinkConnectionBoxProvider get_LinkConnectionBoxProvider() {

		return super.get_LinkConnectionBoxProvider();
	}

	public void set_LinkConnectionBoxProvider(ILinkConnectionBoxProvider value) {
		ILinkConnectionBoxProvider provider = value;
		if (provider != this.get_LinkConnectionBoxProvider()) {
			this.ClearIncrementalData();
			super.set_LinkConnectionBoxProvider(provider);
		}
	}

	public Boolean get_LinkObstacleEnabled() {

		return this._linkObstacleEnabled;
	}

	public void set_LinkObstacleEnabled(Boolean value) {
		if (value != this._linkObstacleEnabled) {
			this._linkObstacleEnabled = value;
			this.OnParameterChanged("LinkObstacleEnabled");
		}
	}

	public int get_LinkStyle() {

		return (int) this._globalLinkStyle;
	}

	public void set_LinkStyle(int value) {
		if (((value != LinkLayoutLinkStyle.Direct) && (value != LinkLayoutLinkStyle.Orthogonal))
				&& ((value != LinkLayoutLinkStyle.Mixed) && (value != LinkLayoutLinkStyle.NoReshape))) {
			throw (new ArgumentException("unsupported link style: "
					+ ((Integer) value)));
		}
		if ((Integer) value != this._globalLinkStyle) {
			this._globalLinkStyle = (Integer) value;
			this.ClearIncrementalData();
			this.OnParameterChanged("GlobalLinkStyle");
		}
	}

	public Integer get_MaxBacktrack() {

		return this._maxBackTrack;
	}

	public void set_MaxBacktrack(Integer value) {
		if (value < 0) {
			throw (new ArgumentException(
					"Negative maximal number of backtrack steps: " + value));
		}
		if (value != this._maxBackTrack) {
			this._maxBackTrack = value;
			this.OnParameterChanged("MaxBacktrack");
		}
	}

	public float get_MinEndSegmentLength() {

		return this._minEndSegmentLength;
	}

	public void set_MinEndSegmentLength(float value) {
		if (value < 0f) {
			throw (new ArgumentException(
					"Negative minimal end segment length: " + value));
		}
		if (value != this._minEndSegmentLength) {
			this._minEndSegmentLength = value;
			this.ClearIncrementalData();
			this.OnParameterChanged("MinEndSegmentLength");
		}
	}

	public float get_MinNodeCornerOffset() {

		return this._minNodeCornerOffset;
	}

	public void set_MinNodeCornerOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException(
					"Negative minimal node corner offset: " + value));
		}
		if (value != this._minNodeCornerOffset) {
			this._minNodeCornerOffset = value;
			this.OnParameterChanged("MinNodeCornerOffset");
		}
	}

	public float get_MinStartSegmentLength() {

		return this._minStartSegmentLength;
	}

	public void set_MinStartSegmentLength(float value) {
		if (value < 0f) {
			throw (new ArgumentException(
					"Negative minimal start segment length: " + value));
		}
		if (value != this._minStartSegmentLength) {
			this._minStartSegmentLength = value;
			this.ClearIncrementalData();
			this.OnParameterChanged("MinStartSegmentLength");
		}
	}

	public INodeBoxProvider get_NodeBoxProvider() {

		return this._nodeBoxInterfaceForObstacle;
	}

	public void set_NodeBoxProvider(INodeBoxProvider value) {
		if (value != this._nodeBoxInterfaceForObstacle) {
			this._nodeBoxInterfaceForObstacle = value;
			this.OnParameterChanged("NodeBoxInterface");
			this.ClearIncrementalData();
		}
	}

	public Boolean get_NodeObstacleEnabled() {

		return this._nodeObstacleEnabled;
	}

	public void set_NodeObstacleEnabled(Boolean value) {
		if (value != this._nodeObstacleEnabled) {
			this._nodeObstacleEnabled = value;
			this.OnParameterChanged("NodeObstacleEnabled");
		}
	}

	public INodeSideFilter get_NodeSideFilter() {

		return this._nodeSideFilter;
	}

	public void set_NodeSideFilter(INodeSideFilter value) {
		if (value != this._nodeSideFilter) {
			this._nodeSideFilter = value;
			this.OnParameterChanged("NodeSideFilter");
			this.ClearIncrementalData();
		}
	}

	public Integer get_NumberCrossingReductionIterations() {

		return this._numberCrossingReductionIterations;
	}

	public void set_NumberCrossingReductionIterations(Integer value) {
		if (value < 0) {
			throw (new ArgumentException(
					"Negative number of crossing iterations: " + value));
		}
		if (value != this._numberCrossingReductionIterations) {
			this._numberCrossingReductionIterations = value;
			this.OnParameterChanged("NumberCrossingReductionIterations");
		}
	}

	public int get_OriginPointMode() {

		return (int) this._originPointMode;
	}

	public void set_OriginPointMode(int value) {
		if (((value != ConnectionPointMode.Free) && (value != ConnectionPointMode.Fixed))
				&& (value != ConnectionPointMode.Mixed)) {
			throw (new ArgumentException("Unsupported mode: "
					+ ((Integer) value)));
		}
		if ((Integer) value != this._originPointMode) {
			this._originPointMode = (Integer) value;
			this.OnParameterChanged("GlobalOriginPointMode");
		}
	}

	public Boolean get_PreserveFixedLinks() {

		return super.get_PreserveFixedLinks();
	}

	public void set_PreserveFixedLinks(Boolean value) {
		super.set_PreserveFixedLinks(value);
	}

	public Boolean get_StraightRouteEnabled() {

		return this._straightRoute;
	}

	public void set_StraightRouteEnabled(Boolean value) {
		if (value != this._straightRoute) {
			this._straightRoute = value;
			this.OnParameterChanged("StraightRouteEnabled");
		}
	}

	public ILongLinkTerminationPointFilter get_TerminationPointFilter() {

		return this._terminationPointFilter;
	}

	public void set_TerminationPointFilter(ILongLinkTerminationPointFilter value) {
		if (value != this._terminationPointFilter) {
			this._terminationPointFilter = value;
			this.OnParameterChanged("TerminationPointFilter");
			this.ClearIncrementalData();
		}
	}

	public float get_VerticalGridBase() {

		return this._gridYBase;
	}

	public void set_VerticalGridBase(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative grid coordinate: " + value));
		}
		if (value != this._gridYBase) {
			this._gridYBase = value;
			this.OnParameterChanged("VerticalGridBase");
		}
	}

	public float get_VerticalGridOffset() {

		return this._gridYOffset;
	}

	public void set_VerticalGridOffset(float value) {
		if (value <= 0f) {
			throw (new ArgumentException("negative or zero grid offset: "
					+ value));
		}
		if (value != this._gridYOffset) {
			this._gridYOffset = value;
			this.OnParameterChanged("VerticalGridOffset");
		}
	}

	public float get_VerticalMinOffset() {

		return this._minYOffset;
	}

	public void set_VerticalMinOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative minimal offset: " + value));
		}
		if (value != this._minYOffset) {
			this._minYOffset = value;
			this.OnParameterChanged("VerticalMinOffset");
		}
	}

}