package ILOG.Diagrammer.GraphLayout;

import system.ArgumentException;
import system.EventArgs;
import system.EventHandler;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import ILOG.Diagrammer.Point2D;
import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelUtil;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.PercCompleteController;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.BuildLevelsAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.BuildRelPosNetsAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.CalcLevelingAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.ConstraintManager;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.CrossRedSwimLaneAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.CrossingReductionAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.CrossingReductionSwapAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.EastWestExpansionAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.EastWestLinkRemovalAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.EastWestLinkRoutingAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.ForkPointAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HGraph;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HGraphAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HLevelAlgApplicator;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HLevelAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.NodePlacementAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.OptimizeLinksAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.OrthLinkRepositionAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.OrthRoutingAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.PolyLinkRepositionAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.SortAdjByCoordAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.SortAdjacenciesAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.StraightenChainAlgorithm;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.SwimLanePlacementAlgorithm;

public class HierarchicalLayout extends ILOG.Diagrammer.GraphLayout.GraphLayout {
	private Boolean _allowStopImmediately = false;

	private Boolean _backtrackCrossingReductionEnabled = false;

	private int _connectorStyle;

	private ConstraintManager _constraintManager;

	private Boolean _crossingReductionDuringIncremental = false;

	public Boolean _defaultBacktrackCrossingReductionEnabled = true;

	public int _defaultConnectorStyle = HierarchicalLayoutConnectorStyle.Automatic;

	public Boolean _defaultCrossingReductionDuringIncremental = false;

	public int _defaultDestinationPointMode = ConnectionPointMode.Free;

	public int _defaultFlowDirection = LayoutFlowDirection.Right;

	public float _defaultForkSegmentLength = 10f;

	public Boolean _defaultFromFork = false;

	public Boolean _defaultIncrAbsLevelPosition = true;

	public float _defaultIncrAbsLevelPositionRange = 20f;

	public float _defaultIncrAbsLevelPositionTendency = 70f;

	public Boolean _defaultIncrementalMode = false;

	public int _defaultIncrementalNodeMovementMode = HierarchicalLayoutMovementMode.Free;

	public Boolean _defaultIntergraphLinkConnectivity = false;

	public int _defaultLevelJustification = HierarchicalLayoutLevelJustification.Center;

	public Boolean _defaultLinkCrossingFineTuningEnabled = true;

	public Boolean _defaultLinkCrossingReductionDuringIncremental = false;

	public float _defaultLinkOffsetX = 15f;

	public float _defaultLinkOffsetY = 15f;

	public Boolean _defaultLinkStraighteningEnabled = true;

	public int _defaultLinkStyle = HierarchicalLayoutLinkStyle.Polyline;

	public Boolean _defaultLinkWidthUsed = false;

	public float _defaultMaxInterLevelApertureAngle = 90f;

	public Boolean _defaultMedianCrossingValueEnabled = true;

	public float _defaultMinStartEndSegmentLength = 0f;

	public Boolean _defaultNeighborLinksAligned = true;

	public float _defaultNodeLinkOffsetX = 20f;

	public float _defaultNodeLinkOffsetY = 20f;

	public float _defaultNodeOffsetX = 40f;

	public float _defaultNodeOffsetY = 40f;

	public Integer _defaultNumberOfLinkCrossingSweeps = 5;

	public int _defaultOriginPointMode = ConnectionPointMode.Free;

	public Boolean _defaultPolylineLinkOverlapReductionEnabled = true;

	public static InternalPoint _defaultPosition = null;

	public Boolean _defaultToFork = false;

	private int _flowDirection;

	private Boolean _fromFork = false;

	private int _globalDestinationPointMode;

	private int _globalIncrementalNodeMovementMode;

	private int _globalLinkStyle;

	private int _globalOriginPointMode;

	private Boolean _incrAbsLevelPosition = false;

	private float _incrAbsLevelPositionRange;

	private float _incrAbsLevelPositionTendency;

	private Boolean _incrementalMode = false;

	private Boolean _intergraphLinkConnectivity = false;

	private int _levelJustification;

	private Boolean _linkCrossingFineTuningEnabled = false;

	private float _linkOffsetX;

	private float _linkOffsetY;

	private Boolean _linkStraighteningEnabled = false;

	private Boolean _linkWidthUsed = false;

	private Boolean _longLinkCrossingReductionDuringIncremental = false;

	private float _maxInterLevelApertureAngle;

	private Boolean _medianCrossingValueEnabled = false;

	private float _minEndSegmentLength;

	private float _minForkSegmentLength;

	private float _minStartSegmentLength;

	private Boolean _neighborLinksAligned = false;

	private float _nodeLinkOffsetX;

	private float _nodeLinkOffsetY;

	private float _nodeOffsetX;

	private float _nodeOffsetY;

	private Integer _numberOfLinkCrossingSweeps;

	private PercCompleteController _percCompleteController;

	private Boolean _polylineLinkOverlapReductionEnabled = false;

	private InternalPoint _position;

	private float _preferredForkAxisLength;

	private Boolean _qnuBacktrackCrossingReductionEnabled = false;

	private Boolean _qnuLinkCrossingFineTuningEnabled = false;

	private Boolean _qnuLinkStraighteningEnabled = false;

	private Boolean _qnuMedianCrossingValueEnabled = false;

	private Integer _qnuNumberOfLinkCrossingSweeps;

	private Boolean _quickAndUgly = false;

	private Boolean _stoppedBeforeCompletion = false;

	private Boolean _toFork = false;

	private static String CALC_LEVEL_INDEX_PROPERTY = "CalcNodeLevelIndex";

	private static String CALC_POSITION_INDEX_PROPERTY = "CalcNodePositionIndex";

	// private GraphLayoutConstraintCollection<HierarchicalConstraint>
	// constraints;

	private static String DESTINATION_POINT_MODE_PROPERTY = "DestinationPointMode";

	private static String INCREMENTAL_MARK_PROPERTY = "MarkedForIncremental";

	private static String INCREMENTAL_MOVEMENT_MODE_PROPERTY = "IncrementalNodeMovementMode";

	private static String LINK_FROM_PORT_INDEX_PROPERTY = "FromPortIndex";

	private static String LINK_FROM_PORT_SIDE_PROPERTY = "FromPortSide";

	private static String LINK_PRIORITY_PROPERTY = "LinkPriority";

	private static String LINK_STYLE_PROPERTY = "LinkStyle";

	private static String LINK_TO_PORT_INDEX_PROPERTY = "ToPortIndex";

	private static String LINK_TO_PORT_SIDE_PROPERTY = "ToPortSide";

	private static java.lang.Object MARK_FLAG = true;

	private static String NODE_BOX_INCR_EXPAND_PORPERTY = "IncrementalNodeBoxForExpand";

	private static String NODE_CONSTRAINT0_PROPERTY = "_NodeConstraint0_";

	private static String NODE_CONSTRAINT1_PROPERTY = "_NodeConstraint1_";

	private static String NODE_CONSTRAINT2_PROPERTY = "_NodeConstraint2_";

	private static String NODE_PORT_NUMBER_PROPERTY = "_NodePortNumber";

	private static String ORIGIN_POINT_MODE_PROPERTY = "OriginPointMode";

	private static String SPEC_LEVEL_INDEX_PROPERTY = "SpecNodeLevelIndex";

	private static String SPEC_POSITION_INDEX_PROPERTY = "SpecNodePositionIndex";

	public Integer Unspecificed = -1;

	public static Point2D UnspecifiedPosition = Point2D.Invalid;

	public static Rectangle2D UnspecifiedRectangle = Rectangle2D.Invalid;

	/* TODO: Event Declare */
	public java.util.ArrayList<EventHandler> ConstraintsChanged = new java.util.ArrayList<EventHandler>();

	public HierarchicalLayout() {
	}

	public HierarchicalLayout(HierarchicalLayout source) {
		super(source);
	}

	public void AddConstraint(HierarchicalConstraint constraint) {
		this.GetConstraintManager().AddConstraint(constraint);
		this.OnParameterChanged("Constraints");

	}

	public void AddConstraintOrGroupToSubjectNode(java.lang.Object node,
			Integer i, java.lang.Object constraintOrGroup) {
		String str = null;
		if (i == 0) {
			str = NODE_CONSTRAINT0_PROPERTY;
			// NOTICE: break ignore!!!
		} else if (i == 1) {
			str = NODE_CONSTRAINT1_PROPERTY;
			// NOTICE: break ignore!!!
		} else {
			str = NODE_CONSTRAINT2_PROPERTY;
			// NOTICE: break ignore!!!
		}
		ArrayList list = (ArrayList) LayoutParametersUtil.GetNodeProperty(this,
				node, str);
		if (list == null) {
			list = new ArrayList(2);
			LayoutParametersUtil.SetNodeProperty(this, node, null, false, str,
					list);
		}
		list.Add(constraintOrGroup);

	}

	@Override
	public void CleanLink(IGraphModel graphModel, java.lang.Object link) {
		super.CleanLink(graphModel, link);

	}

	@Override
	public void CleanNode(IGraphModel graphModel, java.lang.Object node) {
		super.CleanNode(graphModel, node);

	}

	public void ClearAllIncrementalNodeBoxesForExpand() {
		IGraphModel graphModel = this.GetGraphModel();
		if (graphModel != null) {
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(graphModel.get_Nodes());

			while (enumerator.HasMoreElements()) {
				java.lang.Object node = enumerator.NextElement();
				LayoutParametersUtil.SetNodeParameter(this, node,
						NODE_BOX_INCR_EXPAND_PORPERTY, null);
			}
		}

	}

	public void ClearAllMarksForIncremental() {
		IGraphModel graphModel = this.GetGraphModel();
		if (graphModel != null) {
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(GraphModelUtil
							.GetNodesAndLinks(graphModel));

			while (enumerator.HasMoreElements()) {
				java.lang.Object nodeOrLink = enumerator.NextElement();
				LayoutParametersUtil.SetNodeOrLinkProperty(this, nodeOrLink,
						null, false, false, INCREMENTAL_MARK_PROPERTY, null);
			}
		}

	}

	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout Copy() {

		return new HierarchicalLayout(this);

	}

	@Override
	public void CopyParameters(ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		super.CopyParameters(source);
		if (source instanceof HierarchicalLayout) {
			HierarchicalLayout layout = (HierarchicalLayout) source;
			this.set_LinkStyle(layout.get_LinkStyle());
			this.set_ConnectorStyle(layout.get_ConnectorStyle());
			this.set_FromFork(layout.get_FromFork());
			this.set_ToFork(layout.get_ToFork());
			this.set_MinForkSegmentLength(layout.get_MinForkSegmentLength());
			this.set_PreferredForkAxisLength(layout
					.get_PreferredForkAxisLength());
			this.set_OriginPointMode(layout.get_OriginPointMode());
			this.set_DestinationPointMode(layout.get_DestinationPointMode());
			this.set_FlowDirection(layout.get_FlowDirection());
			this.set_LevelJustification(layout.get_LevelJustification());
			this.set_HorizontalNodeOffset(layout.get_HorizontalNodeOffset());
			this.set_VerticalNodeOffset(layout.get_VerticalNodeOffset());
			this.set_HorizontalLinkOffset(layout.get_HorizontalLinkOffset());
			this.set_VerticalLinkOffset(layout.get_VerticalLinkOffset());
			this.set_HorizontalNodeLinkOffset(layout
					.get_HorizontalNodeLinkOffset());
			this.set_VerticalNodeLinkOffset(layout.get_VerticalNodeLinkOffset());
			this.set_MaxInterLevelApertureAngle(layout
					.get_MaxInterLevelApertureAngle());
			this.set_LinkWidthUsed(layout.get_LinkWidthUsed());
			this.set_MinStartSegmentLength(layout.get_MinStartSegmentLength());
			this.set_MinEndSegmentLength(layout.get_MinEndSegmentLength());
			this.set_IncrementalMode(layout.get_IncrementalMode());
			this.set_CrossingReductionDuringIncremental(layout
					.get_CrossingReductionDuringIncremental());
			this.set_LongLinkCrossingReductionDuringIncremental(layout
					.get_LongLinkCrossingReductionDuringIncremental());
			this.set_IncrementalAbsoluteLevelPositioning(layout
					.get_IncrementalAbsoluteLevelPositioning());
			this.set_IncrementalAbsoluteLevelPositionTendency(layout
					.get_IncrementalAbsoluteLevelPositionTendency());
			this.set_IncrementalAbsoluteLevelPositionRange(layout
					.get_IncrementalAbsoluteLevelPositionRange());
			this.set_IncrementalNodeMovementMode(layout
					.get_IncrementalNodeMovementMode());
			this.set_PolylineLinkOverlapReductionEnabled(layout
					.get_PolylineLinkOverlapReductionEnabled());
			this.set_NeighborLinksAligned(layout.get_NeighborLinksAligned());
			this.set_IntergraphConnectivityMode(layout
					.get_IntergraphConnectivityMode());
			this.set_LinkStraighteningEnabled(layout
					.get_LinkStraighteningEnabled());
			this.set_BacktrackCrossingReductionEnabled(layout
					.get_BacktrackCrossingReductionEnabled());
			this.set_MedianCrossingValueEnabled(layout
					.get_MedianCrossingValueEnabled());
			this.set_NumberOfLinkCrossingSweeps(layout
					.get_NumberOfLinkCrossingSweeps());
			this.set_LinkCrossingFineTuningEnabled(layout
					.get_LinkCrossingFineTuningEnabled());
		}

	}

	@Override
	public void Detach() {
		this.RemoveAllConstraints();
		super.Detach();

	}

	public ArrayList GetAndRemoveConstraintOrGroupVector(java.lang.Object node,
			Integer i) {
		String str = null;
		if (i == 0) {
			str = NODE_CONSTRAINT0_PROPERTY;
			// NOTICE: break ignore!!!
		} else if (i == 1) {
			str = NODE_CONSTRAINT1_PROPERTY;
			// NOTICE: break ignore!!!
		} else {
			str = NODE_CONSTRAINT2_PROPERTY;
			// NOTICE: break ignore!!!
		}
		ArrayList list = (ArrayList) LayoutParametersUtil.GetNodeProperty(this,
				node, str);
		LayoutParametersUtil
				.SetNodeProperty(this, node, null, false, str, null);

		return list;

	}

	public Integer GetCalcNodeLevelIndex(java.lang.Object node) {
		java.lang.Object obj2 = LayoutParametersUtil.GetNodeParameter(this,
				node, CALC_LEVEL_INDEX_PROPERTY);
		if (obj2 != null) {

			return (Integer) obj2;
		}

		return -1;

	}

	public Integer GetCalcNodePositionIndex(java.lang.Object node) {
		java.lang.Object obj2 = LayoutParametersUtil.GetNodeParameter(this,
				node, CALC_POSITION_INDEX_PROPERTY);
		if (obj2 != null) {

			return (Integer) obj2;
		}

		return -1;

	}

	public ConstraintManager GetConstraintManager() {

		return this._constraintManager;

	}

	public ICollection GetConstraints() {

		return TranslateUtil.JavaStyleEnum2Collection(this
				.GetConstraintManager().GetConstraints());

	}

	public int GetDestinationPointMode(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				DESTINATION_POINT_MODE_PROPERTY, 0);

	}

	public Integer GetEastNumberOfPorts(java.lang.Object node) {

		return this.GetNumberOfPorts(node, HierarchicalLayoutSide.East);

	}

	public Integer GetFromPortIndex(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				LINK_FROM_PORT_INDEX_PROPERTY, -1);

	}

	public int GetFromPortSide(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				LINK_FROM_PORT_SIDE_PROPERTY, -1);

	}

	public Rectangle2D GetIncrementalNodeBoxForExpand(
			java.lang.Object expandedNode) {

		return TranslateUtil.InternalRect2Rectangle2D(this
				.GetInternalIncrementalNodeBoxForExpand(expandedNode));

	}

	public int GetIncrementalNodeMovementMode(java.lang.Object node) {

		return (int) LayoutParametersUtil.GetNodeParameter(this, node,
				INCREMENTAL_MOVEMENT_MODE_PROPERTY, 0);

	}

	public InternalRect GetInternalIncrementalNodeBoxForExpand(
			java.lang.Object expandedNode) {

		return (InternalRect) LayoutParametersUtil.GetNodeParameter(this,
				expandedNode, NODE_BOX_INCR_EXPAND_PORPERTY);

	}

	public InternalPoint GetInternalPosition() {

		return this._position;

	}

	public float GetLinkPriority(java.lang.Object link) {

		return LayoutParametersUtil.GetLinkParameter(this, link,
				LINK_PRIORITY_PROPERTY, (float) 1f);

	}

	public int GetLinkStyle(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				LINK_STYLE_PROPERTY, 3);

	}

	public ICollection GetNodeGroups() {

		return TranslateUtil.JavaStyleEnum2Collection(this
				.GetConstraintManager().GetGroups());

	}

	public Integer GetNorthNumberOfPorts(java.lang.Object node) {

		return this.GetNumberOfPorts(node, HierarchicalLayoutSide.North);

	}

	public Integer[] GetNumberOfPorts(java.lang.Object node) {

		return (Integer[]) LayoutParametersUtil.GetNodeParameter(this, node,
				NODE_PORT_NUMBER_PROPERTY);

	}

	public Integer GetNumberOfPorts(java.lang.Object node, int side) {
		if ((((side != HierarchicalLayoutSide.Unspecified) && (side != HierarchicalLayoutSide.East)) && ((side != HierarchicalLayoutSide.West) && (side != HierarchicalLayoutSide.North)))
				&& (side != HierarchicalLayoutSide.South)) {
			throw (new ArgumentException("unsupported side option: "
					+ ((int) side)));
		}
		Integer[] numberOfPorts = this.GetNumberOfPorts(node);
		if (numberOfPorts != null) {
			if (side == HierarchicalLayoutSide.East) {

				return numberOfPorts[1];
			} else if (side == HierarchicalLayoutSide.West) {

				return numberOfPorts[3];
			} else if (side == HierarchicalLayoutSide.North) {

				return numberOfPorts[0];
			} else if (side == HierarchicalLayoutSide.South) {

				return numberOfPorts[2];
			}
		}

		return -1;

	}

	public int GetOriginPointMode(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				ORIGIN_POINT_MODE_PROPERTY, 0);

	}

	public Integer GetSouthNumberOfPorts(java.lang.Object node) {

		return this.GetNumberOfPorts(node, HierarchicalLayoutSide.South);

	}

	public Integer GetSpecNodeLevelIndex(java.lang.Object node) {

		return (int) LayoutParametersUtil.GetNodeParameter(this, node,
				SPEC_LEVEL_INDEX_PROPERTY, -1);

	}

	public Integer GetSpecNodePositionIndex(java.lang.Object node) {

		return (int) LayoutParametersUtil.GetNodeParameter(this, node,
				SPEC_POSITION_INDEX_PROPERTY, -1);

	}

	public Integer GetToPortIndex(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				LINK_TO_PORT_INDEX_PROPERTY, -1);

	}

	public int GetToPortSide(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				LINK_TO_PORT_SIDE_PROPERTY, -1);

	}

	public Integer GetWestNumberOfPorts(java.lang.Object node) {

		return this.GetNumberOfPorts(node, HierarchicalLayoutSide.West);

	}

	@Override
	public void Init() {
		super.Init();
		this._globalLinkStyle = HierarchicalLayoutLinkStyle.Polyline;
		this._connectorStyle = HierarchicalLayoutConnectorStyle.Automatic;
		this._fromFork = false;
		this._toFork = false;
		this._minForkSegmentLength = 10f;
		this._preferredForkAxisLength = 10f;
		this._globalOriginPointMode = ConnectionPointMode.Free;
		this._globalDestinationPointMode = ConnectionPointMode.Free;
		this._flowDirection = LayoutFlowDirection.Right;
		this._levelJustification = HierarchicalLayoutLevelJustification.Center;
		this._nodeOffsetX = 40f;
		this._nodeOffsetY = 40f;
		this._linkOffsetX = 15f;
		this._linkOffsetY = 15f;
		this._nodeLinkOffsetX = 20f;
		this._nodeLinkOffsetY = 20f;
		this._maxInterLevelApertureAngle = 90f;
		this._linkWidthUsed = false;
		this._incrementalMode = false;
		this._crossingReductionDuringIncremental = false;
		this._longLinkCrossingReductionDuringIncremental = false;
		this._incrAbsLevelPosition = true;
		this._incrAbsLevelPositionTendency = 70f;
		this._incrAbsLevelPositionRange = 20f;
		this._globalIncrementalNodeMovementMode = HierarchicalLayoutMovementMode.Free;
		this._minStartSegmentLength = 0f;
		this._minEndSegmentLength = 0f;
		this._polylineLinkOverlapReductionEnabled = true;
		this._neighborLinksAligned = true;
		this._intergraphLinkConnectivity = false;
		this._linkStraighteningEnabled = true;
		this._backtrackCrossingReductionEnabled = true;
		this._medianCrossingValueEnabled = true;
		this._numberOfLinkCrossingSweeps = 5;
		this._linkCrossingFineTuningEnabled = true;
		this._position = _defaultPosition;
		this._allowStopImmediately = true;
		this._constraintManager = new ConstraintManager();
		super.set_AllowedTime(0x5f5e100L);

	}

	public Boolean IsFromPointFixed(java.lang.Object link) {
		int originPointMode = this.get_OriginPointMode();
		if (originPointMode != ConnectionPointMode.Free) {
			if (originPointMode == ConnectionPointMode.Fixed) {

				return true;
			}
			if (originPointMode != ConnectionPointMode.Mixed) {
				throw (new system.Exception("unsupported mode: "
						+ ((int) this.get_OriginPointMode())));
			}
		} else {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, true);
		}
		if (this.GetOriginPointMode(link) != ConnectionPointMode.Fixed) {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, true);
		}

		return true;

	}

	public Boolean IsMarkedForIncremental(java.lang.Object nodeOrLink) {

		return (LayoutParametersUtil.GetNodeOrLinkProperty(this, nodeOrLink,
				INCREMENTAL_MARK_PROPERTY) != null);

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
		this._stoppedBeforeCompletion = false;
		this._allowStopImmediately = true;
		IGraphModel graphModel = this.GetGraphModel();
		GraphLayoutReport layoutReport = super.GetLayoutReport();
		HGraph graph = new HGraph(this);

		this._percCompleteController = graph.GetPercController();
		this._percCompleteController.SetStepEstimation(0x11);
		super.OnLayoutStepPerformedIfNeeded();
		this.TransferLayoutOptions(graph);
		graph.Attach(graphModel, this);
		if (graph.GetNumberOfNodes() > 0) {
			ConstraintManager constraintManager = this.GetConstraintManager();
			try {
				if (this.get_IntergraphConnectivityMode()) {
					graph.CreateConvertedIntergraphLinks();
				}
				HGraphAlgorithm algorithm = null;
				HLevelAlgorithm levelAlg = null;
				CalcLevelingAlgorithm algorithm3 = null;

				if (this.MayContinue()) {
					algorithm3 = new CalcLevelingAlgorithm(graph,
							constraintManager);
					algorithm3.Run();

					if (algorithm3.NeedFirstBypassLevel()) {
						graph.ReserveDummyFirstLevel();
					}
					super.OnLayoutStepPerformedIfNeeded();
				}

				if (this.MayContinue()) {
					algorithm = new BuildLevelsAlgorithm(graph);
					algorithm3.Clean();
					algorithm3 = null;
					algorithm.Run();
					algorithm.Clean();
				}

				if (this.MayContinue()) {
					algorithm = new BuildRelPosNetsAlgorithm(graph,
							constraintManager);
					algorithm.Run();
					algorithm.Clean();
				}

				if (this.MayContinue()) {
					algorithm = new CrossingReductionAlgorithm(graph);
					algorithm.Run();
					algorithm.Clean();
				}
				if (this.MayContinue() && graph.HasSwimLanes()) {
					algorithm = new CrossRedSwimLaneAlgorithm(graph);
					algorithm.Run();
					algorithm.Clean();
				}
				if (this.MayContinue()
						&& this.get_LinkCrossingFineTuningEnabled()) {
					levelAlg = new CrossingReductionSwapAlgorithm();
					algorithm = new HLevelAlgApplicator(graph, levelAlg, false);
					algorithm.Run();
					algorithm.Clean();
				}
				if (this.MayContinue() && graph.HasPortSides()) {
					algorithm = new EastWestExpansionAlgorithm(graph);
					algorithm.Run();
					algorithm.Clean();
				} else {
					this._percCompleteController.StartStep(
							graph._percForEastWestExpansion, 1);
				}
				graph.RemoveConvertedIntergraphLinks();

				if (this.MayContinue()) {
					this._percCompleteController.StartStep(1f,
							graph.GetNumberOfLevels() - 1);
					levelAlg = new EastWestLinkRemovalAlgorithm();
					algorithm = new HLevelAlgApplicator(graph, levelAlg, false);
					algorithm.Run();
					algorithm.Clean();
					graph.UpdateNodeIDs();
				}
				SwimLanePlacementAlgorithm swimLaneAlg = new SwimLanePlacementAlgorithm(
						graph);

				if (this.MayContinue()) {
					algorithm = new NodePlacementAlgorithm(graph, swimLaneAlg);
					algorithm.Run();
					algorithm.Clean();
				}

				if (this.MayContinue()) {
					algorithm = new SortAdjacenciesAlgorithm(graph);
					algorithm.Run();
					algorithm.Clean();
				}

				if (this.MayContinue()) {
					graph.GetConnectorAlgorithm().Run();
					swimLaneAlg.SetUseConnectionPoints(true);
				}
				if (this.get_LinkStraighteningEnabled() && this.MayContinue()) {
					swimLaneAlg.CalcSwimLaneBorders(false);
					swimLaneAlg.CalcSwimLaneBounds();

					if (graph.HasSwimLanes()) {
						graph.ToggleLevelsSwimLaneMode();
					}
					algorithm = new StraightenChainAlgorithm(graph, true);
					algorithm.Run();
					algorithm.Clean();

					if (graph.HasSwimLanes()) {
						graph.ToggleLevelsSwimLaneMode();
					}
				}

				if (this.MayContinue()) {
					algorithm = new SortAdjByCoordAlgorithm(graph);
					algorithm.Run();
					algorithm.Clean();
				}
				if (graph.HasOrthogonalLinks() && this.MayContinue()) {
					ForkPointAlgorithm algorithm5 = new ForkPointAlgorithm(
							graph);
					algorithm5.Run();
					algorithm5.SwapForkAndEndPoints();
					swimLaneAlg.CalcSwimLaneBorders(false);
					swimLaneAlg.CalcSwimLaneBounds();
					this._percCompleteController.StartStep(
							graph._percForOrthLinkReposition,
							graph.GetNumberOfLevels() - 1);

					if (graph.HasSwimLanes()) {
						graph.ToggleLevelsSwimLaneMode();
					}
					levelAlg = new OrthLinkRepositionAlgorithm(graph);
					algorithm = new HLevelAlgApplicator(graph, levelAlg, false);
					algorithm.Run();
					algorithm.Clean();
					levelAlg.Clean();

					if (graph.HasSwimLanes()) {
						graph.ToggleLevelsSwimLaneMode();
					}

					if (graph.HasSwimLanes()) {
						swimLaneAlg.CalcFinalSwimLaneBounds();
						algorithm5.SwapForkAndEndPoints();
						algorithm5.Run();
						algorithm5.SwapForkAndEndPoints();
					}
					algorithm = new OrthRoutingAlgorithm(graph);
					algorithm.Run();
					algorithm.Clean();
					algorithm5.SwapForkAndEndPoints();
					algorithm5.CreateForks();
					algorithm5.Clean();
				} else if (this._percCompleteController != null) {
					this._percCompleteController.StartStep(
							graph._percForOrthLinkReposition, 1);
					this._percCompleteController.StartStep(
							graph._percForOrthRouting, 1);
					swimLaneAlg.CalcFinalSwimLaneBounds();
				}
				swimLaneAlg.Clean();
				if (this.MayContinue()
						&& this.get_PolylineLinkOverlapReductionEnabled()) {
					this._percCompleteController.StartStep(
							graph._percForPolyLinkReposition,
							graph.GetNumberOfLevels() - 1);
					levelAlg = new PolyLinkRepositionAlgorithm();
					algorithm = new HLevelAlgApplicator(graph, levelAlg, false);
					algorithm.Run();
					algorithm.Clean();
					levelAlg.Clean();
				}

				if (this.MayContinue()) {
					algorithm = new OptimizeLinksAlgorithm(graph);
					algorithm.Run();
					algorithm.Clean();
				}

				if (this.MayContinue()) {
					graph.GetConnectorAlgorithm().FixEastWestPorts();
				}

				if (this.MayContinue()) {
					algorithm = new EastWestLinkRoutingAlgorithm(graph);
					algorithm.Run();
					algorithm.Clean();
				}

				if (this.MayContinue()) {
					graph.FinalPositionCorrection();
				}
			} finally {
				constraintManager.AfterLayout(graph);
			}
		}
		this._allowStopImmediately = false;
		graph.Detach(redraw);
		graph.Clean();
		if (this.get_IncrementalMode()) {
			this.ClearAllMarksForIncremental();
		}
		this.IncreasePercentageComplete(100);
		this.OnLayoutStepPerformed(false, false);
		if (this._stoppedBeforeCompletion) {
			layoutReport.set_Code(GraphLayoutReportCode.StoppedAndInvalid);
		} else {
			layoutReport.set_Code(GraphLayoutReportCode.LayoutDone);
		}
		this._allowStopImmediately = true;

	}

	public void MarkForIncremental(java.lang.Object nodeOrLink) {
		LayoutParametersUtil.SetNodeOrLinkProperty(this, nodeOrLink,
				"The input node or link", true, true,
				INCREMENTAL_MARK_PROPERTY, MARK_FLAG);

	}

	public Boolean MayContinue() {
		if (!this._stoppedBeforeCompletion) {
			this._stoppedBeforeCompletion = this.IsLayoutTimeElapsed()
					|| this.IsStoppedImmediately();
		}

		return !this._stoppedBeforeCompletion;

	}

	public void OnConstraintsChanged() {
		if (this.ConstraintsChanged != null) {
			this.ConstraintsChanged(this, EventArgs.Empty);
		}

	}

	private void ConstraintsChanged(HierarchicalLayout hierarchicalLayout,
			EventArgs empty) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnLayoutStepPerformed(Boolean layoutStarted,
			Boolean layoutFinished) {
		if (this._percCompleteController != null) {
			this._percCompleteController.NotifyPercentageComplete(this);
		}
		super.OnLayoutStepPerformed(layoutStarted, layoutFinished);

	}

	public void RemoveAllConstraints() {
		this.SetConstraintManager(null);
		this.OnParameterChanged("Constraints");

	}

	public void RemoveConstraint() {
		this.GetConstraintManager().RemoveNextConstraint(null);
		this.OnParameterChanged("Constraints");

	}

	public void RemoveConstraint(HierarchicalConstraint constraint) {
		this.GetConstraintManager().RemoveConstraint(constraint);
		this.OnParameterChanged("Constraints");

	}

	public void SetCalcNodeLevelIndex(java.lang.Object node, Integer index) {
		LayoutParametersUtil.SetNodeParameter(this, node,
				CALC_LEVEL_INDEX_PROPERTY, index);

	}

	public void SetCalcNodePositionIndex(java.lang.Object node, Integer index) {
		LayoutParametersUtil.SetNodeParameter(this, node,
				CALC_POSITION_INDEX_PROPERTY, index);

	}

	public void SetConstraintManager(ConstraintManager mgr) {
		if (mgr != null) {
			this._constraintManager = mgr;
		} else {
			this._constraintManager = new ConstraintManager();
		}

	}

	public void SetDestinationPointMode(java.lang.Object link, int mode) {
		if (((mode != ConnectionPointMode.Free) && (mode != ConnectionPointMode.Fixed))
				&& (mode != ConnectionPointMode.Mixed)) {
			throw (new ArgumentException("Unsupported mode: "
					+ ((Integer) mode)));
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				DESTINATION_POINT_MODE_PROPERTY, (int) mode, 0);

	}

	public void SetEastNumberOfPorts(java.lang.Object node,
			Integer numberOfPorts) {
		this.SetNumberOfPorts(node, HierarchicalLayoutSide.East, numberOfPorts);

	}

	public void SetFromPortIndex(java.lang.Object link, Integer portIndex) {
		if (portIndex < 0) {
			portIndex = -1;
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				LINK_FROM_PORT_INDEX_PROPERTY, (int) portIndex, -1);

	}

	public void SetFromPortSide(java.lang.Object link, int side) {
		if (side == ~HierarchicalLayoutSide.Unspecified) {
			side = HierarchicalLayoutSide.Unspecified;
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				LINK_FROM_PORT_SIDE_PROPERTY, (int) side, -1);

	}

	public void SetIncrementalNodeBoxForExpand(java.lang.Object expandedNode,
			Rectangle2D rect) {
		InternalRect rect2 = TranslateUtil.Rectangle2D2InternalRect(rect);
		LayoutParametersUtil.SetNodeParameter(this, expandedNode,
				NODE_BOX_INCR_EXPAND_PORPERTY, rect2);

	}

	public void SetIncrementalNodeMovementMode(java.lang.Object node, int mode) {
		if ((((mode != HierarchicalLayoutMovementMode.Free) && (mode != HierarchicalLayoutMovementMode.FixedInX)) && ((mode != HierarchicalLayoutMovementMode.FixedInY) && (mode != HierarchicalLayoutMovementMode.Fixed)))
				&& (mode != HierarchicalLayoutMovementMode.Mixed)) {
			throw (new ArgumentException("unsupported mode: " + ((int) mode)));
		}
		LayoutParametersUtil.SetNodeParameter(this, node,
				INCREMENTAL_MOVEMENT_MODE_PROPERTY, (int) mode, 0);

	}

	public void SetLinkPriority(java.lang.Object link, float priority) {
		if (priority > 10000f) {
			priority = 10000f;
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				LINK_PRIORITY_PROPERTY, priority, 1f);

	}

	public void SetLinkStyle(java.lang.Object link, int style) {
		if ((((style != HierarchicalLayoutLinkStyle.Polyline) && (style != HierarchicalLayoutLinkStyle.Orthogonal)) && ((style != HierarchicalLayoutLinkStyle.Straight) && (style != HierarchicalLayoutLinkStyle.Mixed)))
				&& (style != HierarchicalLayoutLinkStyle.NoReshape)) {
			throw (new ArgumentException("unsupported style option: "
					+ ((Integer) style)));
		}
		LayoutParametersUtil.SetLinkParameter(this, link, LINK_STYLE_PROPERTY,
				(int) style, 3);

	}

	public void SetNorthNumberOfPorts(java.lang.Object node,
			Integer numberOfPorts) {
		this.SetNumberOfPorts(node, HierarchicalLayoutSide.North, numberOfPorts);

	}

	public void SetNumberOfPorts(java.lang.Object node, int side,
			Integer numberOfPorts) {
		if ((((side != HierarchicalLayoutSide.Unspecified) && (side != HierarchicalLayoutSide.East)) && ((side != HierarchicalLayoutSide.West) && (side != HierarchicalLayoutSide.North)))
				&& (side != HierarchicalLayoutSide.South)) {
			throw (new ArgumentException("unsupported side option: "
					+ ((Integer) side)));
		}
		Integer num = this.GetNumberOfPorts(node, side);
		Integer[] numArray = this.GetNumberOfPorts(node);
		if (numArray == null) {
			Integer num2 = null;
			Integer num3 = null;
			numArray = new Integer[4];
			numArray[3] = num2 = -1;
			numArray[2] = num3 = num2;
			numArray[0] = numArray[1] = num3;
		}
		String str = "";
		if (side == HierarchicalLayoutSide.East) {
			numArray[1] = numberOfPorts;
			str = "East";
			// NOTICE: break ignore!!!
		} else if (side == HierarchicalLayoutSide.West) {
			numArray[3] = numberOfPorts;
			str = "West";
			// NOTICE: break ignore!!!
		} else if (side == HierarchicalLayoutSide.North) {
			numArray[0] = numberOfPorts;
			str = "North";
			// NOTICE: break ignore!!!
		} else if (side == HierarchicalLayoutSide.South) {
			numArray[2] = numberOfPorts;
			str = "South";
			// NOTICE: break ignore!!!
		}
		if (((numArray[0] < 0) && (numArray[1] < 0))
				&& ((numArray[2] < 0) && (numArray[3] < 0))) {
			numArray = null;
		}
		LayoutParametersUtil.SetNodeParameter(this, node,
				NODE_PORT_NUMBER_PROPERTY, numArray);
		if (num != this.GetNumberOfPorts(node, side)) {
			this.OnParameterChanged(node, str + "NumberOfPorts");
		}

	}

	public void SetOriginPointMode(java.lang.Object link, int mode) {
		if (((mode != ConnectionPointMode.Free) && (mode != ConnectionPointMode.Fixed))
				&& (mode != ConnectionPointMode.Mixed)) {
			throw (new ArgumentException("Unsupported mode: "
					+ ((Integer) mode)));
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				ORIGIN_POINT_MODE_PROPERTY, (int) mode, 0);

	}

	public void SetQuickAndUgly(Boolean flag) {
		if (flag != this._quickAndUgly) {
			if (flag) {
				this._qnuLinkStraighteningEnabled = this
						.get_LinkStraighteningEnabled();
				this._qnuBacktrackCrossingReductionEnabled = this
						.get_BacktrackCrossingReductionEnabled();
				this._qnuMedianCrossingValueEnabled = this
						.get_MedianCrossingValueEnabled();
				this._qnuNumberOfLinkCrossingSweeps = this
						.get_NumberOfLinkCrossingSweeps();
				this._qnuLinkCrossingFineTuningEnabled = this
						.get_LinkCrossingFineTuningEnabled();
				this.set_LinkStraighteningEnabled(false);
				this.set_BacktrackCrossingReductionEnabled(false);
				this.set_MedianCrossingValueEnabled(false);
				this.set_NumberOfLinkCrossingSweeps(1);
				this.set_LinkCrossingFineTuningEnabled(false);
			} else {
				this.set_LinkStraighteningEnabled(this._qnuLinkStraighteningEnabled);
				this.set_BacktrackCrossingReductionEnabled(this._qnuBacktrackCrossingReductionEnabled);
				this.set_MedianCrossingValueEnabled(this._qnuMedianCrossingValueEnabled);
				this.set_NumberOfLinkCrossingSweeps(this._qnuNumberOfLinkCrossingSweeps);
				this.set_LinkCrossingFineTuningEnabled(this._qnuLinkCrossingFineTuningEnabled);
			}
			this._quickAndUgly = flag;
		}

	}

	public void SetSouthNumberOfPorts(java.lang.Object node,
			Integer numberOfPorts) {
		this.SetNumberOfPorts(node, HierarchicalLayoutSide.South, numberOfPorts);

	}

	public void SetSpecNodeLevelIndex(java.lang.Object node, Integer index) {
		if (index < 0) {
			index = -1;
		}
		LayoutParametersUtil.SetNodeParameter(this, node,
				SPEC_LEVEL_INDEX_PROPERTY, (int) index, -1);

	}

	public void SetSpecNodePositionIndex(java.lang.Object node, Integer index) {
		if (index < 0) {
			index = -1;
		}
		LayoutParametersUtil.SetNodeParameter(this, node,
				SPEC_POSITION_INDEX_PROPERTY, (int) index, -1);

	}

	public void SetToPortIndex(java.lang.Object link, Integer portIndex) {
		if (portIndex < 0) {
			portIndex = -1;
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				LINK_TO_PORT_INDEX_PROPERTY, (int) portIndex, -1);

	}

	public void SetToPortSide(java.lang.Object link, int side) {
		if (side == ~HierarchicalLayoutSide.Unspecified) {
			side = HierarchicalLayoutSide.Unspecified;
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				LINK_TO_PORT_SIDE_PROPERTY, (int) side, -1);

	}

	public void SetWestNumberOfPorts(java.lang.Object node,
			Integer numberOfPorts) {
		this.SetNumberOfPorts(node, HierarchicalLayoutSide.West, numberOfPorts);

	}

	private Boolean ShouldSerializeConstraints() {

		return false;
		// return ((this.constraints != null) && (this.constraints.get_Count() >
		// 0));

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
	public Boolean SupportsLayoutOfConnectedComponents() {

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
	public Boolean SupportsPreserveFixedNodes() {

		return true;

	}

	@Override
	public Boolean SupportsStopImmediately() {

		return true;

	}

	private void TransferLayoutOptions(HGraph graph) {
		Integer levelJustification = 1;
		Integer direction = 0;
		if (this.get_FlowDirection() == LayoutFlowDirection.Left) {
			graph.SetFlowDirection(2);
			levelJustification = -1;
			direction = 0;
			// NOTICE: break ignore!!!
		} else if (this.get_FlowDirection() == LayoutFlowDirection.Right) {
			graph.SetFlowDirection(0);
			direction = 0;
			// NOTICE: break ignore!!!
		} else if (this.get_FlowDirection() == LayoutFlowDirection.Top) {
			graph.SetFlowDirection(3);
			levelJustification = -1;
			direction = 1;
			// NOTICE: break ignore!!!
		} else if (this.get_FlowDirection() == LayoutFlowDirection.Bottom) {
			graph.SetFlowDirection(1);
			direction = 1;
			// NOTICE: break ignore!!!
		}
		if (this.get_LevelJustification() == HierarchicalLayoutLevelJustification.Left) {
			graph.SetLevelJustification(-1 * levelJustification);
			// NOTICE: break ignore!!!
		} else if (this.get_LevelJustification() == HierarchicalLayoutLevelJustification.Right) {
			graph.SetLevelJustification(levelJustification);
			// NOTICE: break ignore!!!
		} else if (this.get_LevelJustification() == HierarchicalLayoutLevelJustification.Top) {
			graph.SetLevelJustification(-1 * levelJustification);
			// NOTICE: break ignore!!!
		} else if (this.get_LevelJustification() == HierarchicalLayoutLevelJustification.Bottom) {
			graph.SetLevelJustification(levelJustification);
			// NOTICE: break ignore!!!
		} else if (this.get_LevelJustification() == HierarchicalLayoutLevelJustification.Center) {
			graph.SetLevelJustification(0);
			// NOTICE: break ignore!!!
		}
		if (this.get_LinkStyle() == HierarchicalLayoutLinkStyle.Straight) {
			graph.SetGlobalLinkStyle(0x65);
			// NOTICE: break ignore!!!
		} else if (this.get_LinkStyle() == HierarchicalLayoutLinkStyle.Orthogonal) {
			graph.SetGlobalLinkStyle(100);
			// NOTICE: break ignore!!!
		} else if (this.get_LinkStyle() == HierarchicalLayoutLinkStyle.Polyline) {
			graph.SetGlobalLinkStyle(0x63);
			// NOTICE: break ignore!!!
		} else if (this.get_LinkStyle() == HierarchicalLayoutLinkStyle.Mixed) {
			graph.SetGlobalLinkStyle(0x62);
			// NOTICE: break ignore!!!
		} else {
			graph.SetGlobalLinkStyle(0x66);
			// NOTICE: break ignore!!!
		}
		if (this.get_ConnectorStyle() == HierarchicalLayoutConnectorStyle.Centered) {
			graph.SetConnectorStyle(0x63);
			// NOTICE: break ignore!!!
		} else if (this.get_ConnectorStyle() == HierarchicalLayoutConnectorStyle.Clipped) {
			graph.SetConnectorStyle(100);
			// NOTICE: break ignore!!!
		} else if (this.get_ConnectorStyle() == HierarchicalLayoutConnectorStyle.EvenlySpaced) {
			graph.SetConnectorStyle(0x65);
			// NOTICE: break ignore!!!
		} else {
			if (this.get_LinkStyle() == HierarchicalLayoutLinkStyle.Orthogonal) {
				graph.SetConnectorStyle(0x65);
			} else if (this.get_LinkStyle() == HierarchicalLayoutLinkStyle.Mixed) {
				graph.SetConnectorStyle(100);
			} else if (this.get_LinkStyle() == HierarchicalLayoutLinkStyle.Straight) {
				graph.SetConnectorStyle(0x63);
			} else {
				graph.SetConnectorStyle(100);
			}
			// NOTICE: break ignore!!!
		}
		graph.SetIncrementalMode(this.get_IncrementalMode());
		graph.SetCrossingReductionDuringIncremental(this
				.get_CrossingReductionDuringIncremental());
		graph.SetLongLinkCrossingReductionDuringIncremental(this
				.get_LongLinkCrossingReductionDuringIncremental());
		graph.SetIncrementalAbsoluteLevelPositioning(this
				.get_IncrementalAbsoluteLevelPositioning());
		graph.SetMinDistBetweenNodes(0, this.get_HorizontalNodeOffset());
		graph.SetMinDistBetweenLinks(0, this.get_HorizontalLinkOffset());
		graph.SetMinDistBetweenNodeAndLink(0,
				this.get_HorizontalNodeLinkOffset());
		graph.SetMinDistBetweenNodes(1, this.get_VerticalNodeOffset());
		graph.SetMinDistBetweenLinks(1, this.get_VerticalLinkOffset());
		graph.SetMinDistBetweenNodeAndLink(1, this.get_VerticalNodeLinkOffset());
		float dist = 1.01f * this.get_MinStartSegmentLength();
		if (graph.GetMinDistBetweenNodeAndLink(direction) < dist) {
			graph.SetMinDistBetweenNodeAndLink(direction, dist);
		}
		dist = 1.01f * this.get_MinEndSegmentLength();
		if (graph.GetMinDistBetweenNodeAndLink(direction) < dist) {
			graph.SetMinDistBetweenNodeAndLink(direction, dist);
		}

	}

	public void ValidateConstraints() {
		Integer numberOfConstraints = this.GetConstraintManager()
				.GetNumberOfConstraints();
		if (numberOfConstraints != 0) {
			IGraphModel graphModel = this.GetGraphModel();
			if ((graphModel == null)
					|| (graphModel.get_Nodes().get_Count() == 0)) {
				this.RemoveAllConstraints();
			} else {
				this.GetConstraintManager().ValidateConstraints(graphModel);
				this.GetConstraintManager().ValidateGroups(graphModel);
				if (numberOfConstraints != this.GetConstraintManager()
						.GetNumberOfConstraints()) {
					this.OnParameterChanged("Constraints");
				}
			}
		}

	}

	public long get_AllowedTime() {

		return super.get_AllowedTime();
	}

	public void set_AllowedTime(long value) {
		super.set_AllowedTime(value);
	}

	public Boolean get_BacktrackCrossingReductionEnabled() {

		return this._backtrackCrossingReductionEnabled;
	}

	public void set_BacktrackCrossingReductionEnabled(Boolean value) {
		if (value != this._backtrackCrossingReductionEnabled) {
			this._backtrackCrossingReductionEnabled = value;
			this.OnParameterChanged("BacktrackCrossingReductionEnabled");
		}
	}

	public int get_ConnectorStyle() {

		return this._connectorStyle;
	}

	public void set_ConnectorStyle(int value) {
		if (((value != HierarchicalLayoutConnectorStyle.Automatic) && (value != HierarchicalLayoutConnectorStyle.Centered))
				&& ((value != HierarchicalLayoutConnectorStyle.Clipped) && (value != HierarchicalLayoutConnectorStyle.EvenlySpaced))) {
			throw (new ArgumentException("unsupported style option: "
					+ ((int) value)));
		}
		if (value != this._connectorStyle) {
			this._connectorStyle = value;
			this.OnParameterChanged("ConnectorStyle");
		}
	}

	// public GraphLayoutConstraintCollection<HierarchicalConstraint>
	// get_Constraints(){
	// if(this.constraints == null){
	// this.constraints = new
	// GraphLayoutConstraintCollection<HierarchicalConstraint>(this);
	// }
	//
	// return this.constraints;
	// }

	public Boolean get_CrossingReductionDuringIncremental() {

		return this._crossingReductionDuringIncremental;
	}

	public void set_CrossingReductionDuringIncremental(Boolean value) {
		if (value != this._crossingReductionDuringIncremental) {
			this._crossingReductionDuringIncremental = value;
			this.OnParameterChanged("CrossingReductionDuringIncremental");
		}
	}

	public int get_DestinationPointMode() {

		return this._globalDestinationPointMode;
	}

	public void set_DestinationPointMode(int value) {
		if (((value != ConnectionPointMode.Free) && (value != ConnectionPointMode.Fixed))
				&& (value != ConnectionPointMode.Mixed)) {
			throw (new ArgumentException("Unsupported mode: " + ((int) value)));
		}
		if (value != this._globalDestinationPointMode) {
			this._globalDestinationPointMode = value;
			this.OnParameterChanged("GlobalDestinationPointMode");
		}
	}

	public int get_FlowDirection() {

		return this._flowDirection;
	}

	public void set_FlowDirection(int value) {
		if (((value != LayoutFlowDirection.Left) && (value != LayoutFlowDirection.Right))
				&& ((value != LayoutFlowDirection.Top) && (value != LayoutFlowDirection.Bottom))) {
			throw (new ArgumentException("unsupported flow direction option: "
					+ ((Integer) value)));
		}
		if (value != this._flowDirection) {
			this._flowDirection = value;
			this.OnParameterChanged("FlowDirection");
		}
	}

	public Boolean get_FromFork() {

		return this._fromFork;
	}

	public void set_FromFork(Boolean value) {
		if (this._fromFork != value) {
			this._fromFork = value;
			this.OnParameterChanged("FromFork");
		}
	}

	public float get_HorizontalLinkOffset() {

		return this._linkOffsetX;
	}

	public void set_HorizontalLinkOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative horizontal link offset: "
					+ value));
		}
		if (value != this._linkOffsetX) {
			this._linkOffsetX = value;
			this.OnParameterChanged("HorizontalLinkOffset");
		}
	}

	public float get_HorizontalNodeLinkOffset() {

		return this._nodeLinkOffsetX;
	}

	public void set_HorizontalNodeLinkOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException(
					"negative horizontal node link offset: " + value));
		}
		if (value != this._nodeLinkOffsetX) {
			this._nodeLinkOffsetX = value;
			this.OnParameterChanged("HorizontalNodeLinkOffset");
		}
	}

	public float get_HorizontalNodeOffset() {

		return this._nodeOffsetX;
	}

	public void set_HorizontalNodeOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative horizontal node offset: "
					+ value));
		}
		if (value != this._nodeOffsetX) {
			this._nodeOffsetX = value;
			this.OnParameterChanged("HorizontalNodeOffset");
		}
	}

	public Boolean get_IncrementalAbsoluteLevelPositioning() {

		return this._incrAbsLevelPosition;
	}

	public void set_IncrementalAbsoluteLevelPositioning(Boolean value) {
		if (value != this._incrAbsLevelPosition) {
			this._incrAbsLevelPosition = value;
			this.OnParameterChanged("IncrementalAbsoluteLevelPositioning");
		}
	}

	public float get_IncrementalAbsoluteLevelPositionRange() {

		return this._incrAbsLevelPositionRange;
	}

	public void set_IncrementalAbsoluteLevelPositionRange(float value) {
		if (value < 0f) {
			throw (new ArgumentException("illegal negative range: " + value));
		}
		if (value != this._incrAbsLevelPositionRange) {
			this._incrAbsLevelPositionRange = value;
			this.OnParameterChanged("IncrementalAbsoluteLevelPositionRange");
		}
	}

	public float get_IncrementalAbsoluteLevelPositionTendency() {

		return this._incrAbsLevelPositionTendency;
	}

	public void set_IncrementalAbsoluteLevelPositionTendency(float value) {
		if ((value < 0f) || (value > 100f)) {
			throw (new ArgumentException("illegal percentage value: " + value));
		}
		if (value != this._incrAbsLevelPositionTendency) {
			this._incrAbsLevelPositionTendency = value;
			this.OnParameterChanged("IncrementalAbsoluteLevelPositionTendency");
		}
	}

	public Boolean get_IncrementalMode() {

		return this._incrementalMode;
	}

	public void set_IncrementalMode(Boolean value) {
		if (value != this._incrementalMode) {
			this._incrementalMode = value;
			this.OnParameterChanged("IncrementalMode");
		}
	}

	public int get_IncrementalNodeMovementMode() {

		return this._globalIncrementalNodeMovementMode;
	}

	public void set_IncrementalNodeMovementMode(int value) {
		if ((((value != HierarchicalLayoutMovementMode.Free) && (value != HierarchicalLayoutMovementMode.FixedInX)) && ((value != HierarchicalLayoutMovementMode.FixedInY) && (value != HierarchicalLayoutMovementMode.Fixed)))
				&& (value != HierarchicalLayoutMovementMode.Mixed)) {
			throw (new ArgumentException("unsupported mode: "
					+ ((Integer) value)));
		}
		if (value != this._globalIncrementalNodeMovementMode) {
			this._globalIncrementalNodeMovementMode = value;
			this.OnParameterChanged("GlobalIncrementalNodeMovementMode");
		}
	}

	public Boolean get_IntergraphConnectivityMode() {

		return this._intergraphLinkConnectivity;
	}

	public void set_IntergraphConnectivityMode(Boolean value) {
		if (value != this._intergraphLinkConnectivity) {
			this._intergraphLinkConnectivity = value;
			this.OnParameterChanged("IntergraphConnectivityMode");
		}
	}

	public Boolean get_LayoutOfConnectedComponentsEnabled() {

		return super.get_LayoutOfConnectedComponentsEnabled();
	}

	public void set_LayoutOfConnectedComponentsEnabled(Boolean value) {
		super.set_LayoutOfConnectedComponentsEnabled(value);
	}

	public int get_LevelJustification() {

		return this._levelJustification;
	}

	public void set_LevelJustification(int value) {
		if ((((value != HierarchicalLayoutLevelJustification.Left) && (value != HierarchicalLayoutLevelJustification.Right)) && ((value != HierarchicalLayoutLevelJustification.Top) && (value != HierarchicalLayoutLevelJustification.Bottom)))
				&& (value != HierarchicalLayoutLevelJustification.Center)) {
			throw (new ArgumentException(
					"unsupported level justification option: " + ((int) value)));
		}
		if (value != this._levelJustification) {
			this._levelJustification = value;
			this.OnParameterChanged("LevelJustification");
		}
	}

	public ILinkConnectionBoxProvider get_LinkConnectionBoxProvider() {

		return super.get_LinkConnectionBoxProvider();
	}

	public void set_LinkConnectionBoxProvider(ILinkConnectionBoxProvider value) {
		super.set_LinkConnectionBoxProvider(value);
	}

	public Boolean get_LinkCrossingFineTuningEnabled() {

		return this._linkCrossingFineTuningEnabled;
	}

	public void set_LinkCrossingFineTuningEnabled(Boolean value) {
		if (value != this._linkCrossingFineTuningEnabled) {
			this._linkCrossingFineTuningEnabled = value;
			this.OnParameterChanged("LinkCrossingFineTuningEnabled");
		}
	}

	public Boolean get_LinkStraighteningEnabled() {

		return this._linkStraighteningEnabled;
	}

	public void set_LinkStraighteningEnabled(Boolean value) {
		if (value != this._linkStraighteningEnabled) {
			this._linkStraighteningEnabled = value;
			this.OnParameterChanged("LinkStraighteningEnabled");
		}
	}

	public int get_LinkStyle() {

		return this._globalLinkStyle;
	}

	public void set_LinkStyle(int value) {
		if ((((value != HierarchicalLayoutLinkStyle.Polyline) && (value != HierarchicalLayoutLinkStyle.Orthogonal)) && ((value != HierarchicalLayoutLinkStyle.Straight) && (value != HierarchicalLayoutLinkStyle.Mixed)))
				&& (value != HierarchicalLayoutLinkStyle.NoReshape)) {
			throw (new ArgumentException("unsupported style option: "
					+ ((Integer) value)));
		}
		if (value != this._globalLinkStyle) {
			this._globalLinkStyle = value;
			this.OnParameterChanged("GlobalLinkStyle");
		}
	}

	public Boolean get_LinkWidthUsed() {

		return this._linkWidthUsed;
	}

	public void set_LinkWidthUsed(Boolean value) {
		if (value != this._linkWidthUsed) {
			this._linkWidthUsed = value;
			this.OnParameterChanged("LinkWidthUsed");
		}
	}

	public Boolean get_LongLinkCrossingReductionDuringIncremental() {

		return this._longLinkCrossingReductionDuringIncremental;
	}

	public void set_LongLinkCrossingReductionDuringIncremental(Boolean value) {
		if (value != this._longLinkCrossingReductionDuringIncremental) {
			this._longLinkCrossingReductionDuringIncremental = value;
			this.OnParameterChanged("LongLinkCrossingReductionDuringIncremental");
		}
	}

	public float get_MaxInterLevelApertureAngle() {

		return this._maxInterLevelApertureAngle;
	}

	public void set_MaxInterLevelApertureAngle(float value) {
		if ((value < 10f) || (value > 90f)) {
			throw (new ArgumentException(
					"aperture angle must be between 10 and 90"));
		}
		if (value != this._maxInterLevelApertureAngle) {
			this._maxInterLevelApertureAngle = value;
			this.OnParameterChanged("MaxInterLevelApertureAngle");
		}
	}

	public Boolean get_MedianCrossingValueEnabled() {

		return this._medianCrossingValueEnabled;
	}

	public void set_MedianCrossingValueEnabled(Boolean value) {
		if (value != this._medianCrossingValueEnabled) {
			this._medianCrossingValueEnabled = value;
			this.OnParameterChanged("MedianCrossingValueEnabled");
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
			this.OnParameterChanged("MinEndSegmentLength");
		}
	}

	public float get_MinForkSegmentLength() {

		return this._minForkSegmentLength;
	}

	public void set_MinForkSegmentLength(float value) {
		if (this._minForkSegmentLength != value) {
			this._minForkSegmentLength = value;
			this.OnParameterChanged("MinForkSegmentLength");
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
			this.OnParameterChanged("MinStartSegmentLength");
		}
	}

	public Boolean get_NeighborLinksAligned() {

		return this._neighborLinksAligned;
	}

	public void set_NeighborLinksAligned(Boolean value) {
		if (value != this._neighborLinksAligned) {
			this._neighborLinksAligned = value;
			this.OnParameterChanged("NeighborLinksAligned");
		}
	}

	public Integer get_NumberOfLinkCrossingSweeps() {

		return this._numberOfLinkCrossingSweeps;
	}

	public void set_NumberOfLinkCrossingSweeps(Integer value) {
		if (value < 1) {
			value = 1;
		}
		if (value != this._numberOfLinkCrossingSweeps) {
			this._numberOfLinkCrossingSweeps = value;
			this.OnParameterChanged("NumberOfLinkCrossingSweeps");
		}
	}

	public int get_OriginPointMode() {

		return this._globalOriginPointMode;
	}

	public void set_OriginPointMode(int value) {
		if (((value != ConnectionPointMode.Free) && (value != ConnectionPointMode.Fixed))
				&& (value != ConnectionPointMode.Mixed)) {
			throw (new ArgumentException("Unsupported mode: "
					+ ((Integer) value)));
		}
		if (value != this._globalOriginPointMode) {
			this._globalOriginPointMode = value;
			this.OnParameterChanged("GlobalOriginPointMode");
		}
	}

	public Boolean get_PolylineLinkOverlapReductionEnabled() {

		return this._polylineLinkOverlapReductionEnabled;
	}

	public void set_PolylineLinkOverlapReductionEnabled(Boolean value) {
		if (value != this._polylineLinkOverlapReductionEnabled) {
			this._polylineLinkOverlapReductionEnabled = value;
			this.OnParameterChanged("PolylineLinkOverlapReductionEnabled");
		}
	}

	public Point2D get_Position() {

		return TranslateUtil.InternalPoint2Point2D(this._position);
	}

	public void set_Position(Point2D value) {
		InternalPoint point2 = this._position;
		InternalPoint point = TranslateUtil.Point2D2InternalPoint(value);
		this._position = point;

		if (((point != null) && (point2 != null)) && !point.equals(point2)) {
			this.OnParameterChanged("Position");
		} else if ((point == null) && (point2 != null)) {
			this.OnParameterChanged("Position");
		} else if ((point != null) && (point2 == null)) {
			this.OnParameterChanged("Position");
		}
	}

	public float get_PreferredForkAxisLength() {

		return this._preferredForkAxisLength;
	}

	public void set_PreferredForkAxisLength(float value) {
		if (this._preferredForkAxisLength != value) {
			this._preferredForkAxisLength = value;
			this.OnParameterChanged("PreferredForkAxisLength");
		}
	}

	public Boolean get_PreserveFixedLinks() {

		return super.get_PreserveFixedLinks();
	}

	public void set_PreserveFixedLinks(Boolean value) {
		super.set_PreserveFixedLinks(value);
	}

	public Boolean get_PreserveFixedNodes() {

		return super.get_PreserveFixedNodes();
	}

	public void set_PreserveFixedNodes(Boolean value) {
		super.set_PreserveFixedNodes(value);
	}

	public Boolean get_ToFork() {

		return this._toFork;
	}

	public void set_ToFork(Boolean value) {
		if (this._toFork != value) {
			this._toFork = value;
			this.OnParameterChanged("ToFork");
		}
	}

	public float get_VerticalLinkOffset() {

		return this._linkOffsetY;
	}

	public void set_VerticalLinkOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative vertical link offset: "
					+ value));
		}
		if (value != this._linkOffsetY) {
			this._linkOffsetY = value;
			this.OnParameterChanged("VerticalLinkOffset");
		}
	}

	public float get_VerticalNodeLinkOffset() {

		return this._nodeLinkOffsetY;
	}

	public void set_VerticalNodeLinkOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative vertical node link offset: "
					+ value));
		}
		if (value != this._nodeLinkOffsetY) {
			this._nodeLinkOffsetY = value;
			this.OnParameterChanged("VerticalNodeLinkOffset");
		}
	}

	public float get_VerticalNodeOffset() {

		return this._nodeOffsetY;
	}

	public void set_VerticalNodeOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative vertical node offset: "
					+ value));
		}
		if (value != this._nodeOffsetY) {
			this._nodeOffsetY = value;
			this.OnParameterChanged("VerticalNodeOffset");
		}
	}

}