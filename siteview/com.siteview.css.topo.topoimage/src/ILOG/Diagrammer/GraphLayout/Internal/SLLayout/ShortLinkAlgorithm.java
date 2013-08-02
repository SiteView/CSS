package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import ILOG.Diagrammer.GraphLayout.Internal.GenericQuadtree.*;
import system.*;
import system.Collections.*;

public final class ShortLinkAlgorithm {
	private Boolean _animate = false;

	private MultiLinkBundleLayouter _bundleLayouter;

	private Integer _bundleMode;

	private CostComputer _costComputer;

	private LinkShapeProducer _directShapeProducer;

	private Boolean _duringLongLayout = false;

	private Integer _globalConnectorStyle;

	private Integer _globalDestinationPointMode;

	private Integer _globalIncrementalModifiedLinkReshapeMode;

	private Integer _globalIncrementalUnmodifiedLinkReshapeMode;

	private Integer _globalLinkStyle;

	private Integer _globalOriginPointMode;

	private Boolean _incrementalMode = false;

	private SubgraphData _interGraphLinksRoutingModel;

	private IntersectApplyObject _intersectApplyObject;

	private Boolean _isPreserveFixedLinks = false;

	private float _linkOffset;

	private IncidentLinksRefiner _linkRefiner;

	private IncidentLinksSorter _linkSorter;

	private LongLinkLayout _longLinkLayout;

	private float _minFinalSegment;

	private Boolean _multipleLinksFound = false;

	private NodeSideLayouter _nodeSideLayouter;

	private IGenericQuadtreeObject _objectInterface;

	private BestNeighborOptimization _optimizer;

	public IGraphModel _originalGraphModel;

	private LinkShapeProducer _orthogonalShapeProducer;

	private GenericIndexedSet _quadtree;

	private Boolean _redraw = false;

	private Boolean _sameShapeForMultipleLinks = false;

	private LinkShapeProducer _shapeProducer;

	private ShortLinkLayout _shortLinkLayout;

	private Boolean _stoppedBeforeCompletion = false;

	private InternalPoint _tempPoint1;

	private InternalPoint _tempPoint2;

	private InternalPoint[] _tempPoints1;

	private InternalPoint[] _tempPoints2;

	private InternalRect _tempRect1;

	private InternalRect _tempRect2;

	private ArrayList _vectNodesWithIncidentLinks;

	private ArrayList _vectNonFixedShapeLinksWithIntersecting;

	private ArrayList _vectNonFixedShapeLinksWithoutIntersecting;

	private ArrayList _vectNonFixedUniqueShapeLinks;

	private static String LINK_DATA_PROPERTY = "LinkData";

	private static String NODE_DATA_PROPERTY = "NodeData";

	public ShortLinkAlgorithm(ShortLinkLayout layout) {
		this._shortLinkLayout = layout;
		this._tempPoint1 = new InternalPoint(0f, 0f);
		this._tempPoint2 = new InternalPoint(0f, 0f);
		Integer num = 6;
		this._tempPoints1 = new InternalPoint[num];
		this._tempPoints2 = new InternalPoint[num];
		for (Integer i = 0; i < this._tempPoints1.length; i++) {
			this._tempPoints1[i] = new InternalPoint(0f, 0f);
		}
		for (Integer j = 0; j < this._tempPoints2.length; j++) {
			this._tempPoints2[j] = new InternalPoint(0f, 0f);
		}
		this._tempRect1 = new InternalRect(0f, 0f, 0f, 0f);
		this._tempRect2 = new InternalRect(0f, 0f, 0f, 0f);
		this._objectInterface = new QObjectInterface();
		this._orthogonalShapeProducer = new OrthogonalLinkShapeProducer(this);
		this._directShapeProducer = new DirectLinkShapeProducer(this);
		this._nodeSideLayouter = new NodeSideLayouter();
		this._bundleLayouter = new MultiLinkBundleLayouter();
		this._costComputer = new CostComputer();

		this._originalGraphModel = this._shortLinkLayout.GetGraphModel();
		this._longLinkLayout = new AnonClass_1(this);
		this._longLinkLayout.SetParentLayout(this.GetShortLinkLayout());
		this._longLinkLayout.set_OriginPointMode(ConnectionPointMode.Mixed);
		this._longLinkLayout
				.set_DestinationPointMode(ConnectionPointMode.Mixed);
		this._longLinkLayout.set_PreserveFixedLinks(true);
		this._longLinkLayout.set_InterGraphLinksMode(layout
				.get_InterGraphLinksMode());
		this._longLinkLayout.set_CombinedInterGraphLinksMode(layout
				.get_CombinedInterGraphLinksMode());
	}

	private Boolean CanBeMasterOrSlave(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return ((!linkData.IsFixed() && !linkData.IsFromPointFixed()) && !linkData
				.IsToPointFixed());

	}

	private void CleanInternalData() {
		this._optimizer = null;
		this._orthogonalShapeProducer.Clean();
		this._directShapeProducer.Clean();
		if (this._linkSorter != null) {
			this._linkSorter.Clean();
		}
		if (this._linkRefiner != null) {
			this._linkRefiner.Clean();
		}
		if (this._nodeSideLayouter != null) {
			this._nodeSideLayouter.Clean();
		}
		if (this._bundleLayouter != null) {
			this._bundleLayouter.Clean();
		}
		this._vectNodesWithIncidentLinks = null;
		this._vectNonFixedShapeLinksWithIntersecting = null;
		this._vectNonFixedShapeLinksWithoutIntersecting = null;
		this._vectNonFixedUniqueShapeLinks = null;
		this._quadtree = null;
		this._intersectApplyObject = null;
		this._originalGraphModel = null;
		this._interGraphLinksRoutingModel = null;

	}

	private void ComputeConnectionPoints(Boolean withSlaveLinks) {
		this.ComputeConnectionPoints(withSlaveLinks, true);

	}

	private void ComputeConnectionPoints(Boolean withSlaveLinks,
			Boolean restoreShape) {
		if (restoreShape) {
			this.RestoreCurrentShape(
					this._vectNonFixedShapeLinksWithIntersecting,
					withSlaveLinks);
			this.RestoreCurrentShape(
					this._vectNonFixedShapeLinksWithoutIntersecting,
					withSlaveLinks);
			this.RestoreCurrentShape(this._vectNonFixedUniqueShapeLinks,
					withSlaveLinks);
		}
		if (this._bundleMode != 0) {
			Integer count = this._vectNodesWithIncidentLinks.get_Count();
			for (Integer i = 0; i < count; i++) {
				ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData) this._vectNodesWithIncidentLinks
						.get_Item(i);
				data.LayoutNodeSides(this, this._nodeSideLayouter,
						this._linkSorter, null,
						this._shortLinkLayout.get_LinkConnectionBoxProvider(),
						this._linkOffset, this._minFinalSegment,
						this._bundleMode);
			}
			if ((this._linkRefiner != null) && (this._bundleMode == 2)) {
				for (Integer j = 0; j < count; j++) {
					((ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData) this._vectNodesWithIncidentLinks
							.get_Item(j)).RefineNodeSides(this,
							this._nodeSideLayouter, this._linkRefiner,
							this._linkOffset, this._minFinalSegment,
							this._bundleMode);
				}
			}
		}
		this._shortLinkLayout.OnLayoutStepPerformedIfNeeded();

	}

	private ArrayList ComputeIntersectingObjects(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {
		InternalRect boundingBox = linkData.boundingBox;
		this._intersectApplyObject.Init();
		this._quadtree.MapIntersects(boundingBox, this._intersectApplyObject,
				linkData);

		return this._intersectApplyObject.GetIntersectingObjects();

	}

	private void ComputeLinkConnectionPoints(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			SLNodeSide oldFromSide, SLNodeSide newFromSide,
			SLNodeSide oldToSide, SLNodeSide newToSide) {
		if (this._bundleMode != 0) {
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData fromNode = linkData
					.GetFromNode();
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData toNode = linkData
					.GetToNode();
			ILinkConnectionBoxProvider linkConnectionBoxProvider = this._shortLinkLayout
					.get_LinkConnectionBoxProvider();
			if (oldFromSide != null) {
				oldFromSide.Layout(this, this._nodeSideLayouter,
						this._linkSorter, null, linkConnectionBoxProvider,
						fromNode, this._linkOffset, this._minFinalSegment,
						this._bundleMode);
			}
			if (newFromSide != oldFromSide) {
				newFromSide.Layout(this, this._nodeSideLayouter,
						this._linkSorter, null, linkConnectionBoxProvider,
						fromNode, this._linkOffset, this._minFinalSegment,
						this._bundleMode);
			}
			if ((oldToSide != null)
					&& ((toNode != fromNode) || ((oldToSide != oldFromSide) && (oldToSide != newFromSide)))) {
				oldToSide.Layout(this, this._nodeSideLayouter,
						this._linkSorter, null, linkConnectionBoxProvider,
						toNode, this._linkOffset, this._minFinalSegment,
						this._bundleMode);
			}
			if ((newToSide != oldToSide)
					&& ((toNode != fromNode) || ((newToSide != oldFromSide) && (newToSide != newFromSide)))) {
				newToSide.Layout(this, this._nodeSideLayouter,
						this._linkSorter, null, linkConnectionBoxProvider,
						toNode, this._linkOffset, this._minFinalSegment,
						this._bundleMode);
			}
			if ((this._linkRefiner != null) && (this._bundleMode == 2)) {
				if (oldFromSide != null) {
					oldFromSide.Refine(this, this._nodeSideLayouter,
							this._linkRefiner, fromNode, this._linkOffset,
							this._minFinalSegment, this._bundleMode);
				}
				if (newFromSide != oldFromSide) {
					newFromSide.Refine(this, this._nodeSideLayouter,
							this._linkRefiner, fromNode, this._linkOffset,
							this._minFinalSegment, this._bundleMode);
				}
				if ((oldToSide != null)
						&& ((toNode != fromNode) || ((oldToSide != oldFromSide) && (oldToSide != newFromSide)))) {
					oldToSide.Refine(this, this._nodeSideLayouter,
							this._linkRefiner, toNode, this._linkOffset,
							this._minFinalSegment, this._bundleMode);
				}
				if ((newToSide != oldToSide)
						&& ((toNode != fromNode) || ((newToSide != oldFromSide) && (newToSide != newFromSide)))) {
					newToSide.Refine(this, this._nodeSideLayouter,
							this._linkRefiner, toNode, this._linkOffset,
							this._minFinalSegment, this._bundleMode);
				}
			}
		}

	}

	public void Detach() {
		if (this.GetGraphModel() != null) {
			this.CleanInternalData();
		}
		if (this._longLinkLayout != null) {
			this._longLinkLayout.Detach();
		}

	}

	private void DoLayout(IGraphModel graphModel,
			GraphLayoutReport layoutReport, Boolean redraw) {

		if (this.MayContinue()) {
			this.ComputeConnectionPoints(this._sameShapeForMultipleLinks);
		}
		Integer allowedNumberOfIterations = this._shortLinkLayout
				.get_AllowedNumberOfIterations();
		if (allowedNumberOfIterations > 0) {
			if (this._animate) {
				TranslateUtil.Noop();
				try {
					this.FinalShapeLink(graphModel, false, true, this._redraw);
				} finally {
					TranslateUtil.Noop();
				}
			}
			long allowedTime = this._shortLinkLayout.get_AllowedTime();
			this.Optimize(this._vectNonFixedShapeLinksWithIntersecting,
					allowedNumberOfIterations, allowedTime);
			this.Optimize(this._vectNonFixedShapeLinksWithoutIntersecting, 1,
					allowedTime);
			this._shortLinkLayout.OnLayoutStepPerformedIfNeeded();
			this.ComputeConnectionPoints(this._sameShapeForMultipleLinks);
			this._shortLinkLayout.OnLayoutStepPerformedIfNeeded();
		}
		Boolean flag = (this._shortLinkLayout.get_LinkToNodeCrossingPenalty() > 0f)
				&& this._shortLinkLayout.get_LinkOverlapNodesForbidden();
		this.FinalShapeLink(graphModel, !flag, true, redraw);
		if (flag && this.MayContinue()) {
			this._duringLongLayout = true;
			this._shortLinkLayout.SetGraphModel(this._originalGraphModel);
			if (this._interGraphLinksRoutingModel != null) {
				this._interGraphLinksRoutingModel.Clean();
				this._interGraphLinksRoutingModel = null;
			}
			try {
				this.DoLongLinkLayout(this._originalGraphModel, redraw);
			} finally {
				this._duringLongLayout = false;
			}
		}
		layoutReport
				.set_Code(this._stoppedBeforeCompletion ? GraphLayoutReportCode.StoppedAndInvalid
						: GraphLayoutReportCode.LayoutDone);

	}

	private void DoLongLinkLayout(IGraphModel graphModel, Boolean redraw) {
		if (this._longLinkLayout.GetGraphModel() != graphModel) {
			Boolean internalGraphModelChecking = GraphModelData.Get(graphModel)
					.get_InternalGraphModelChecking();
			GraphModelData.SetInternalGraphModelChecking(graphModel, false);
			this._longLinkLayout.Attach(graphModel);
			GraphModelData.SetInternalGraphModelChecking(graphModel,
					internalGraphModelChecking);
		}
		this._longLinkLayout.set_AllowedTime(this._shortLinkLayout
				.GetRemainingAllowedTime());
		this._longLinkLayout.PerformLayout(true);
		this.RemoveProperties();

	}

	public void FinalShapeLink(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			Boolean slaveExpansion, Boolean redraw) {
		if ((this._multipleLinksFound && slaveExpansion) && linkData.IsMaster()) {
			if (!(linkData instanceof MultiLinkData)) {
				throw (new system.Exception(
						"public error: isMaster() returns true on " + linkData));
			}
			this._bundleLayouter.Layout(this, graphModel,
					(MultiLinkData) linkData, this._bundleMode,
					this._linkOffset, this._minFinalSegment, redraw);
		} else {
			LinkShape linkShape = linkData.GetLinkShape();
			InternalPoint from = linkShape.GetFrom();
			InternalPoint to = linkShape.GetTo();
			if (linkData.IsFromPointFixed() && (from != null)) {
				from = new FixedPoint(from);
			}
			if (linkData.IsToPointFixed() && (to != null)) {
				to = new FixedPoint(to);
			}
			Integer linkStyle = this._globalLinkStyle;
			if (linkStyle == 0x63) {
				linkStyle = (Integer) this._shortLinkLayout
						.GetLinkStyle(linkData.get_nodeOrLink());
			}
			GraphModelUtil.ReshapeLink(graphModel, this._shortLinkLayout,
					linkData.get_nodeOrLink(), linkStyle, from,
					linkShape.GetIntermediatePoints(), 0,
					linkShape.GetNumberOfPoints() - 2, to);
		}

	}

	private void FinalShapeLink(IGraphModel graphModel,
			Boolean removeProperties, Boolean slaveExpansion, Boolean redraw) {
		this.FinalShapeLinks(graphModel,
				this._vectNonFixedShapeLinksWithIntersecting, removeProperties,
				slaveExpansion, redraw);
		this.FinalShapeLinks(graphModel,
				this._vectNonFixedShapeLinksWithoutIntersecting,
				removeProperties, slaveExpansion, redraw);
		this.FinalShapeLinks(graphModel, this._vectNonFixedUniqueShapeLinks,
				removeProperties, slaveExpansion, redraw);

	}

	private void FinalShapeLinks(IGraphModel graphModel, ArrayList vectLinks,
			Boolean removeProperties, Boolean slaveExpansion, Boolean redraw) {
		if (vectLinks != null) {
			Integer count = vectLinks.get_Count();
			for (Integer i = 0; i < count; i++) {
				ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) vectLinks
						.get_Item(i);
				this.FinalShapeLink(graphModel, linkData, slaveExpansion,
						redraw);
				if (removeProperties) {
					linkData.SetIntersectingObjects(null);
				}
			}
		}

	}

	public LongLinkLayout GetAuxiliaryLongLinkLayout() {

		return this._longLinkLayout;

	}

	public Integer GetConnectorStyle(java.lang.Object node) {
		if (this._globalConnectorStyle != 0x63) {

			return this._globalConnectorStyle;
		}

		return (Integer) this._shortLinkLayout.GetConnectorStyle(node);

	}

	public CostComputer GetCostComputer() {

		return this._costComputer;

	}

	public IGraphModel GetGraphModel() {

		return this._shortLinkLayout.GetGraphModel();

	}

	public Integer GetIncrementalModifiedLinkReshapeMode(java.lang.Object link) {
		if (this._globalIncrementalModifiedLinkReshapeMode != 0x63) {

			return this._globalIncrementalModifiedLinkReshapeMode;
		}

		return (Integer) this._shortLinkLayout
				.GetIncrementalModifiedLinkReshapeMode(link);

	}

	public Integer GetIncrementalUnmodifiedLinkReshapeMode(java.lang.Object link) {
		if (this._globalIncrementalUnmodifiedLinkReshapeMode != 0x63) {

			return this._globalIncrementalUnmodifiedLinkReshapeMode;
		}

		return (Integer) this._shortLinkLayout
				.GetIncrementalUnmodifiedLinkReshapeMode(link);

	}

	public SubgraphData GetInterGraphLinksRoutingModel() {

		return this._interGraphLinksRoutingModel;

	}

	private InternalRect GetLinkConnectionRectangle(IGraphModel graphModel,
			java.lang.Object node) {
		ILinkConnectionBoxProvider linkConnectionBoxProvider = this._shortLinkLayout
				.get_LinkConnectionBoxProvider();
		if (linkConnectionBoxProvider == null) {

			return this.GetNodeBox(graphModel, node);
		}
		if (graphModel instanceof SubgraphData) {

			return TranslateUtil
					.Rectangle2D2InternalRect(((SubgraphData) graphModel)
							.GetNodeBox(linkConnectionBoxProvider, node));
		}

		return TranslateUtil
				.GetBox(linkConnectionBoxProvider, graphModel, node);

	}

	public float GetLinkCost(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			Boolean global) {
		if (this._animate) {
			TranslateUtil.Noop();
			try {
				this.FinalShapeLink(graphModel, false, true, this._redraw);
			} finally {
				TranslateUtil.Noop();
			}
		}

		return this._costComputer.GetLinkCost(graphModel,
				this._interGraphLinksRoutingModel, linkData, global);

	}

	public ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData GetLinkData(
			java.lang.Object link) {

		return (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) LayoutParametersUtil
				.GetLinkProperty(this._shortLinkLayout, link,
						LINK_DATA_PROPERTY);

	}

	public float GetLinkNodeCost(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return this._costComputer.GetLinkCost(graphModel,
				this._interGraphLinksRoutingModel, linkData, true, false);

	}

	public float GetLinkOffset() {

		return this._linkOffset;

	}

	public LinkShapeProducer GetLinkShapeProducer(java.lang.Object link) {
		if (this._shapeProducer != null) {

			return this._shapeProducer;
		}
		if (this._shortLinkLayout.GetLinkStyle(link) == LinkLayoutLinkStyle.NoReshape
				|| this._shortLinkLayout.GetLinkStyle(link) == LinkLayoutLinkStyle.Direct) {

			return this._directShapeProducer;
		} else if (this._shortLinkLayout.GetLinkStyle(link) == LinkLayoutLinkStyle.Orthogonal) {

			return this._orthogonalShapeProducer;
		}
		throw (new system.Exception(
				clr.System.StringStaticWrapper.Concat(new java.lang.Object[] {
						"unsupported individual link style: ",
						(Integer) this._shortLinkLayout.GetLinkStyle(link),
						" for link: ", link })));

	}

	private InternalRect GetNodeBox(IGraphModel graphModel,
			java.lang.Object node) {
		INodeBoxProvider nodeBoxProvider = this._shortLinkLayout
				.get_NodeBoxProvider();
		if (nodeBoxProvider == null) {

			return GraphModelUtil.BoundingBox(graphModel, node);
		}
		if (graphModel instanceof SubgraphData) {

			return TranslateUtil
					.Rectangle2D2InternalRect(((SubgraphData) graphModel)
							.GetNodeBox(nodeBoxProvider, node));
		}

		return TranslateUtil.GetBox(nodeBoxProvider, graphModel, node);

	}

	private ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData GetNodeData(
			java.lang.Object node) {

		return (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData) LayoutParametersUtil
				.GetNodeProperty(this._shortLinkLayout, node,
						NODE_DATA_PROPERTY);

	}

	private ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData GetOrCreateLinkData(
			IGraphModel graphModel, java.lang.Object link) {
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData = this
				.GetLinkData(link);
		if ((linkData == null) || (linkData.get_nodeOrLink() != link)) {
			linkData = new ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData(
					this, link);
			LayoutParametersUtil.SetLinkProperty(this._shortLinkLayout, link,
					null, false, LINK_DATA_PROPERTY, linkData);
		} else {
			linkData.Update(this, link, null);
		}
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData data2 = this
				.GetOrCreateNodeData(graphModel, graphModel.GetFrom(link),
						false);
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData data3 = this
				.GetOrCreateNodeData(graphModel, graphModel.GetTo(link), false);
		data2.SetTotalIncidentLinkWidth(data2.GetTotalIncidentLinkWidth()
				+ linkData.GetLinkWidth());
		data3.SetTotalIncidentLinkWidth(data3.GetTotalIncidentLinkWidth()
				+ linkData.GetLinkWidth());

		return linkData;

	}

	public ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData GetOrCreateNodeData(
			IGraphModel graphModel, java.lang.Object node, Boolean update) {
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData = this
				.GetNodeData(node);
		if ((nodeData == null) || (nodeData.get_nodeOrLink() != node)) {
			if (!update) {
				throw (new system.Exception(
						"node data not found and update = false"));
			}
			nodeData = new ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData(
					node);
			LayoutParametersUtil.SetNodeProperty(this._shortLinkLayout, node,
					null, false, NODE_DATA_PROPERTY, nodeData);
		}
		if (update) {
			nodeData.Update(this.GetNodeBox(graphModel, node),
					this.GetLinkConnectionRectangle(graphModel, node),
					this.GetConnectorStyle(node), graphModel,
					this._incrementalMode);
		}

		return nodeData;

	}

	public IGraphModel GetOriginalGraphModel() {

		return this._originalGraphModel;

	}

	public ShortLinkLayout GetShortLinkLayout() {

		return this._shortLinkLayout;

	}

	public InternalPoint GetTempPoint1() {

		return this._tempPoint1;

	}

	public InternalPoint GetTempPoint2() {

		return this._tempPoint2;

	}

	public InternalPoint[] GetTempPoints1() {

		return this._tempPoints1;

	}

	public InternalPoint[] GetTempPoints2() {

		return this._tempPoints2;

	}

	public InternalRect GetTempRect1() {

		return this._tempRect1;

	}

	public InternalRect GetTempRect2() {

		return this._tempRect2;

	}

	private void InitializeInternalData(IGraphModel graphModel) {
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData orCreateLinkData = null;
		this.UpdateUtilities();
		Integer count = graphModel.get_Links().get_Count();
		Integer capacity = graphModel.get_Nodes().get_Count();
		if (this._vectNodesWithIncidentLinks == null) {
			this._vectNodesWithIncidentLinks = new ArrayList(capacity);
		} else {
			this._vectNodesWithIncidentLinks.set_Capacity(capacity);
			this._vectNodesWithIncidentLinks.Clear();
		}
		if (this._vectNonFixedShapeLinksWithIntersecting == null) {
			this._vectNonFixedShapeLinksWithIntersecting = new ArrayList(count);
		} else {
			this._vectNonFixedShapeLinksWithIntersecting.set_Capacity(count);
			this._vectNonFixedShapeLinksWithIntersecting.Clear();
		}
		if (this._vectNonFixedShapeLinksWithoutIntersecting == null) {
			this._vectNonFixedShapeLinksWithoutIntersecting = new ArrayList(
					count);
		} else {
			this._vectNonFixedShapeLinksWithoutIntersecting.set_Capacity(count);
			this._vectNonFixedShapeLinksWithoutIntersecting.Clear();
		}
		if (this._vectNonFixedUniqueShapeLinks == null) {
			this._vectNonFixedUniqueShapeLinks = new ArrayList(count);
		} else {
			this._vectNonFixedUniqueShapeLinks.set_Capacity(count);
			this._vectNonFixedUniqueShapeLinks.Clear();
		}
		if (this._quadtree == null) {
			this._quadtree = new GenericIndexedSet(20, 20,
					this._objectInterface);
		} else {
			this._quadtree.DeleteAll();
		}
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(graphModel.get_Nodes());

		while (enumerator.HasMoreElements()) {
			java.lang.Object node = enumerator.NextElement();
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData data = this
					.GetOrCreateNodeData(graphModel, node, true);
			this._quadtree.AddObject(data);
			if (data.GetDegree() > 0) {
				this._vectNodesWithIncidentLinks.Add(data);
			}
		}
		IJavaStyleEnumerator enumerator2 = TranslateUtil
				.Collection2JavaStyleEnum(graphModel.get_Links());
		ArrayList list = new ArrayList(count);

		while (enumerator2.HasMoreElements()) {
			java.lang.Object link = enumerator2.NextElement();

			orCreateLinkData = this.GetOrCreateLinkData(graphModel, link);
			list.Add(orCreateLinkData);
		}
		count = list.get_Count();
		if (this._sameShapeForMultipleLinks) {
			ArrayList list2 = new ArrayList(list.get_Count());
			for (Integer j = 0; j < count; j++) {
				orCreateLinkData = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) list
						.get_Item(j);
				orCreateLinkData.ComputeLinkRegion(this._minFinalSegment,
						this._linkOffset, this._bundleMode);

				if (!orCreateLinkData.IsSlave()) {
					MultiLinkData data4 = this.SearchMultipleLinks(graphModel,
							orCreateLinkData);
					ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data3 = (data4 == null) ? orCreateLinkData
							: data4;
					list2.Add(data3);
					this._quadtree.AddObject(data3);
				}
			}
			list = list2;
			list2 = null;
		} else {
			for (Integer k = 0; k < count; k++) {
				orCreateLinkData = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) list
						.get_Item(k);
				orCreateLinkData.ComputeLinkRegion(this._minFinalSegment,
						this._linkOffset, this._bundleMode);
				this._quadtree.AddObject(orCreateLinkData);
			}
		}
		Integer num5 = 0;
		count = list.get_Count();
		for (Integer i = 0; i < count; i++) {
			orCreateLinkData = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) list
					.get_Item(i);

			if (!orCreateLinkData.IsFixed()) {
				if (this._incrementalMode) {
					if (this._sameShapeForMultipleLinks
							&& orCreateLinkData.IsSlave()) {
						this._multipleLinksFound = true;
					}

					if (orCreateLinkData.IsShapeTypeFixed()) {
						this._vectNonFixedUniqueShapeLinks
								.Add(orCreateLinkData);
						orCreateLinkData.SetIndex(0x7fffffff);
						continue;
					}
				}
				orCreateLinkData.RestoreCurrentShape(this,
						this._minFinalSegment, this._linkOffset, false);
				if (orCreateLinkData.GetShapeTypesCount() > 1) {
					ArrayList vect = this
							.ComputeIntersectingObjects(orCreateLinkData);
					if (vect != null) {
						this._vectNonFixedShapeLinksWithIntersecting
								.Add(orCreateLinkData);
					} else {
						this._vectNonFixedShapeLinksWithoutIntersecting
								.Add(orCreateLinkData);
					}
					orCreateLinkData.SetIntersectingObjects(vect);
				} else {
					this._vectNonFixedUniqueShapeLinks.Add(orCreateLinkData);
				}
				orCreateLinkData.SetIndex(num5++);

			}
		}
		this._shortLinkLayout.OnLayoutStepPerformedIfNeeded();

	}

	public Boolean IsFixedOrNoReshape(java.lang.Object link) {

		if (!this._shortLinkLayout.IsLayoutRunning()) {
			this.Update(false);
		}

		if (((this._interGraphLinksRoutingModel == null) || !this._interGraphLinksRoutingModel
				.IsFixedLink(link))
				&& ((!this._isPreserveFixedLinks || !this._shortLinkLayout
						.GetFixed(link)) && (this._globalLinkStyle != 0))) {

			return ((this._globalLinkStyle == 0x63) && (this._shortLinkLayout
					.GetLinkStyle(link) == LinkLayoutLinkStyle.NoReshape));
		}

		return true;

	}

	public Boolean IsFromPointFixed(java.lang.Object link) {
		if (this._globalOriginPointMode == 0) {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, true);
		} else if (this._globalOriginPointMode == 3) {

			return true;
		} else if (this._globalOriginPointMode == 0x63) {
			if (this._shortLinkLayout.GetOriginPointMode(link) != ConnectionPointMode.Fixed) {

				return this.GetGraphModel()
						.HasPinnedConnectionPoint(link, true);
			}

			return true;
		}
		throw (new system.Exception("unsupported mode: "
				+ this._globalOriginPointMode));

	}

	public Boolean IsIncrementalMode() {

		return this._incrementalMode;

	}

	public Boolean IsToPointFixed(java.lang.Object link) {
		if (this._globalDestinationPointMode == 0) {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, false);
		} else if (this._globalDestinationPointMode == 3) {

			return true;
		} else if (this._globalDestinationPointMode == 0x63) {
			if (this._shortLinkLayout.GetDestinationPointMode(link) != ConnectionPointMode.Fixed) {

				return this.GetGraphModel().HasPinnedConnectionPoint(link,
						false);
			}

			return true;
		}
		throw (new system.Exception("unsupported mode: "
				+ this._globalDestinationPointMode));

	}

	public void Layout(Boolean redraw) {
		if (!this._duringLongLayout) {

			this._originalGraphModel = this.GetGraphModel();
			this.Update(redraw);
			if (this._shortLinkLayout.get_InterGraphLinksMode()) {

				if (TranslateUtil.Collection2JavaStyleEnum(
						this._originalGraphModel.get_InterGraphLinks())
						.HasMoreElements()) {
					this._interGraphLinksRoutingModel = new SubgraphData();
					this._interGraphLinksRoutingModel.CollectData(this
							.GetGraphModel());
					this._interGraphLinksRoutingModel
							.SetNormalLinksFixed(!this._shortLinkLayout
									.get_CombinedInterGraphLinksMode());
					this._shortLinkLayout
							.SetGraphModel(this._interGraphLinksRoutingModel);
				} else if (!this._shortLinkLayout
						.get_CombinedInterGraphLinksMode()) {
					this._shortLinkLayout.GetLayoutReport().set_Code(
							GraphLayoutReportCode.LayoutDone);

					return;
				}
			}
			try {
				this.UpdateBeforeLayout();
				this.DoLayout(this.GetGraphModel(),
						this._shortLinkLayout.GetLayoutReport(), redraw);
			} finally {
				if (this._interGraphLinksRoutingModel != null) {
					this._interGraphLinksRoutingModel.Clean();
				}
				this._shortLinkLayout.SetGraphModel(this._originalGraphModel);
				this.CleanInternalData();
			}
		}

	}

	private IncidentLinksRefiner MakeIncidentLinksRefiner() {
		if (this._globalLinkStyle == 2) {

			return new OrthogonalIncidentLinksRefiner(this);
		} else if (this._globalLinkStyle == 4 || this._globalLinkStyle == 0x63) {

			return new DirectIncidentLinksRefiner(this);
		}
		throw (new system.Exception("unsupported link style: "
				+ this._globalLinkStyle));

	}

	private IncidentLinksSorter MakeIncidentLinksSorter() {
		if (this._globalLinkStyle == 2) {

			return new OrthogonalIncidentLinksSorter(this);
		} else if (this._globalLinkStyle == 4 || this._globalLinkStyle == 0x63) {

			return new DirectIncidentLinksSorter(this);
		}
		throw (new system.Exception("unsupported link style: "
				+ this._globalLinkStyle));

	}

	private Boolean MayContinue() {
		if (this._stoppedBeforeCompletion) {

			return false;
		}

		Boolean flag = !this._shortLinkLayout.IsLayoutTimeElapsedPackage();
		if (!flag) {
			this._stoppedBeforeCompletion = true;
		}

		return flag;

	}

	public void NotifyMixedLinkStyle() {
		this._shapeProducer = null;

	}

	private void Optimize(ArrayList vectLinks, Integer numberOfIter,
			long allowedTime) {
		if ((vectLinks != null) && (vectLinks.get_Count() != 0)) {
			this._optimizer = new LinkLayoutBestNeighborOptimization(this,
					numberOfIter, allowedTime, vectLinks);
			this._optimizer.Optimize();

			if (this._optimizer.IsStoppedBeforeCompletion()) {
				this._stoppedBeforeCompletion = true;
			}
			this._optimizer = null;
		}

	}

	private void RemoveProperties() {
		this.RemoveProperties(this._vectNonFixedShapeLinksWithIntersecting);
		this.RemoveProperties(this._vectNonFixedShapeLinksWithoutIntersecting);
		this.RemoveProperties(this._vectNonFixedUniqueShapeLinks);

	}

	private void RemoveProperties(ArrayList vectLinks) {
		if (vectLinks != null) {
			Integer count = vectLinks.get_Count();
			for (Integer i = 0; i < count; i++) {
				((ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) vectLinks
						.get_Item(i)).SetIntersectingObjects(null);
			}
		}

	}

	public void ReshapeLink(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			LinkShapeType shapeType, Boolean isLinkBundlingRequired) {
		SLNodeSide fromNodeSide = linkData.GetFromNodeSide();
		SLNodeSide toNodeSide = linkData.GetToNodeSide();
		SLNodeSide newFromSide = shapeType.GetFromNodeSide(linkData);
		SLNodeSide newToSide = shapeType.GetToNodeSide(linkData);
		linkData.SetCurrentShapeType(shapeType);
		linkData.RestoreCurrentShape(this, this._minFinalSegment,
				this._linkOffset, false);
		if (isLinkBundlingRequired) {
			this.ComputeLinkConnectionPoints(linkData, fromNodeSide,
					newFromSide, toNodeSide, newToSide);
		}

	}

	private void RestoreCurrentShape(ArrayList vectLinks, Boolean withSlaveLinks) {
		Integer count = vectLinks.get_Count();
		for (Integer i = 0; i < count; i++) {
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) vectLinks
					.get_Item(i);

			if ((!withSlaveLinks || !this._multipleLinksFound)
					|| !data.IsSlave()) {
				data.RestoreCurrentShape(this, this._minFinalSegment,
						this._linkOffset, false);
			}
		}

	}

	private MultiLinkData SearchMultipleLinks(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {
		MultiLinkData multiLinkData = null;

		if (this.CanBeMasterOrSlave(linkData)) {
			java.lang.Object nodeOrLink = linkData.GetFromNode()
					.get_nodeOrLink();

			multiLinkData = this.SearchMultipleLinks(graphModel, TranslateUtil
					.Collection2JavaStyleEnum(graphModel
							.GetLinksFrom(nodeOrLink)), null, linkData);

			multiLinkData = this.SearchMultipleLinks(graphModel,
					TranslateUtil.Collection2JavaStyleEnum(graphModel
							.GetLinksTo(nodeOrLink)), multiLinkData, linkData);
		}

		return multiLinkData;

	}

	private MultiLinkData SearchMultipleLinks(IGraphModel graphModel,
			IJavaStyleEnumerator links, MultiLinkData multiLinkData,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		while (links.HasMoreElements()) {
			java.lang.Object link = links.NextElement();
			if ((link != linkData.get_nodeOrLink())
					&& (GraphModelUtil.GetOpposite(graphModel, link, linkData
							.GetFromNode().get_nodeOrLink()) == linkData
							.GetToNode().get_nodeOrLink())) {
				ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data = this
						.GetLinkData(link);

				if (!data.IsSlave() && this.CanBeMasterOrSlave(data)) {
					if (multiLinkData == null) {
						multiLinkData = new MultiLinkData(this, linkData,
								this._linkOffset);
						multiLinkData.ComputeLinkRegion(this._minFinalSegment,
								this._linkOffset, this._bundleMode);
					}
					multiLinkData.AddSlave(data);
					this._multipleLinksFound = true;
				}
			}
		}

		return multiLinkData;

	}

	public void StopImmediately() {
		this._stoppedBeforeCompletion = true;
		if (this._optimizer != null) {
			this._optimizer.StopImmediately();
		}

	}

	private void Update(Boolean redraw) {
		this._stoppedBeforeCompletion = false;
		this._animate = false;
		this._incrementalMode = this._shortLinkLayout.get_IncrementalMode();
		this._sameShapeForMultipleLinks = this._shortLinkLayout
				.get_SameShapeForMultipleLinks();
		this._redraw = redraw;
		this._multipleLinksFound = false;
		this._linkOffset = this._shortLinkLayout.get_LinkOffset();
		this._minFinalSegment = this._shortLinkLayout
				.get_MinFinalSegmentLength();
		this._bundleMode = (Integer) this._shortLinkLayout
				.get_LinkBundlesMode();
		this._globalLinkStyle = (Integer) this._shortLinkLayout.get_LinkStyle();
		this._globalOriginPointMode = (Integer) this._shortLinkLayout
				.get_OriginPointMode();
		this._globalDestinationPointMode = (Integer) this._shortLinkLayout
				.get_DestinationPointMode();
		this._globalIncrementalUnmodifiedLinkReshapeMode = (Integer) this._shortLinkLayout
				.get_IncrementalUnmodifiedLinkReshapeMode();
		this._globalIncrementalModifiedLinkReshapeMode = (Integer) this._shortLinkLayout
				.get_IncrementalModifiedLinkReshapeMode();
		this._globalConnectorStyle = (Integer) this._shortLinkLayout
				.get_ConnectorStyle();
		this._isPreserveFixedLinks = this._shortLinkLayout
				.get_PreserveFixedLinks();

	}

	private void UpdateBeforeLayout() {
		if (this._interGraphLinksRoutingModel != null) {
			this._animate = false;
			this._incrementalMode = false;
		}
		if (!this._shortLinkLayout.get_ParametersUpToDate()) {
			this._incrementalMode = false;
		}
		if (this._intersectApplyObject == null) {
			this._intersectApplyObject = new IntersectApplyObject();
		}
		this.InitializeInternalData(this._shortLinkLayout.GetGraphModel());

		this._linkSorter = this.MakeIncidentLinksSorter();

		this._linkRefiner = this.MakeIncidentLinksRefiner();

	}

	private void UpdateLinkShapeProducer() {
		this._orthogonalShapeProducer.Update(this);
		this._directShapeProducer.Update(this);
		if (this._globalLinkStyle == 2) {
			this._shapeProducer = this._orthogonalShapeProducer;

			return;
		} else if (this._globalLinkStyle == 4) {
			this._shapeProducer = this._directShapeProducer;

			return;
		} else if (this._globalLinkStyle == 0x63) {
			this._shapeProducer = null;

			return;
		}
		throw (new system.Exception("unsupported global link style: "
				+ this._globalLinkStyle));

	}

	private void UpdateUtilities() {
		this.UpdateLinkShapeProducer();
		this._costComputer.Update(this);
		this._nodeSideLayouter.Update(this);

	}

	private class AnonClass_1 extends LongLinkLayout {
		private ShortLinkAlgorithm __outerThis;

		public AnonClass_1(ShortLinkAlgorithm input__outerThis) {
			this.__outerThis = input__outerThis;
		}

		@Override
		public int GetDestinationPointMode(java.lang.Object link) {

			return ConnectionPointMode.Fixed;

		}

		@Override
		public Boolean GetFixed(java.lang.Object link) {
			IGraphModel graphModel = this.GetGraphModel();
			if (graphModel instanceof SubgraphData) {

				link = ((SubgraphData) graphModel).GetOriginal(link);
			}

			if (!this.__outerThis.IsFixedOrNoReshape(link)) {
				ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData = this.__outerThis
						.GetLinkData(link);
				if (linkData != null) {

					return (this.__outerThis.GetLinkNodeCost(
							this.__outerThis._originalGraphModel, linkData) <= 0f);
				}
			}

			return true;

		}

		@Override
		public int GetOriginPointMode(java.lang.Object link) {

			return ConnectionPointMode.Fixed;

		}

	}

	private final class IntersectApplyObject implements GenericApplyObject {
		private ArrayList _intersectingObjects;

		public IntersectApplyObject() {
		}

		public void Apply(java.lang.Object obj, java.lang.Object arg) {
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeOrLinkData data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeOrLinkData) obj;
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) arg;

			if (data.IsIntersectingObjectValid(linkData)) {
				if (this._intersectingObjects == null) {
					this._intersectingObjects = new ArrayList(50);
				}
				this._intersectingObjects.Add(data);
			}

		}

		public ArrayList GetIntersectingObjects() {

			return this._intersectingObjects;

		}

		public void Init() {
			this._intersectingObjects = null;

		}

	}

	public final class QObjectInterface implements IGenericQuadtreeObject {
		public InternalRect BoundingBox(java.lang.Object obj) {

			return ((ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeOrLinkData) obj).boundingBox;

		}

	}
}