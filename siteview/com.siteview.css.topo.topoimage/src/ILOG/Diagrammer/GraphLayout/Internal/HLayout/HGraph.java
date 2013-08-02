package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning.*;
import system.*;
import system.Math;
import system.Collections.*;

public final class HGraph {
	private ILinkConnectionBoxProvider _connBoxInterface;

	private CalcConnectors _connectorAlg;

	private Integer _connectorStyle;

	private HRPNode[] _constraintNodes;

	private ArrayList _convertedIntergraphLinks;

	private Boolean _crossingReductionDuringIncremental = false;

	private Boolean _dummyFirstLevel = false;

	private HGraphMemberList _eastwestlinks;

	private Integer _flowDirection;

	private Integer _globalLinkStyle;

	private HierarchicalLayout _graphlayout;

	private IGraphModel _graphModel;

	private Boolean _hasFixedLinkSides = false;

	private Boolean _hasFixedNodes = false;

	private Boolean _hasOrthogonalLinks = false;

	private Boolean _hasPortNumbers = false;

	private Boolean _hasPortSides = false;

	private Boolean _incrAbsLevelPosition = false;

	private Boolean _incrementalMode = false;

	private Integer _levelJustification;

	private HGraphMemberList _levels;

	private HLevel[] _levelsArray;

	private HGraphMemberList _links;

	private Boolean _longLinkCrossingReductionDuringIncremental = false;

	private float _maxLinkPriority;

	private float[] _minDistLinkLink;

	private float[] _minDistNodeLink;

	private float[] _minDistNodeNode;

	private HGraphMemberList _nodes;

	private Integer _numberOfSwimLanes;

	private float[] _offsetForMinDist;

	private float[] _oldBaryCenter;

	private PercCompleteController _percCompleteController;

	public float _percForAttach = 6f;

	public float _percForBuildLevels = 3f;

	public float _percForCalcConnectors = 2f;

	public float _percForCrossReduction = 4f;

	public float _percForDetachLinks = 14f;

	public float _percForDetachNodes = 38f;

	public float _percForEastWestExpansion = 1f;

	public float _percForFinalPositionCorrection = 1.5f;

	public float _percForFixEastWestPorts = 1f;

	public float _percForNodePlacement = 13.5f;

	public float _percForOptimizeLinks = 1.5f;

	public float _percForOrthLinkReposition = 1f;

	public float _percForOrthRouting = 3.5f;

	public float _percForPartitioning = 4.5f;

	public float _percForPolyLinkReposition = 2.5f;

	public float _percForSortAdjacencies = 1f;

	public float _percForSortAdjByCoordAlgorithm = 1f;

	private HGraphMemberList _segments;

	private HSwimLane _swimLanes;

	private String ATTACH_PROPERTY;

	public HGraph(HierarchicalLayout graphlayout) {
		this._graphlayout = graphlayout;
		this._graphModel = null;
		this._nodes = new HGraphMemberList();
		this._links = new HGraphMemberList();
		this._segments = new HGraphMemberList();
		this._levels = new HGraphMemberList();
		this._levelsArray = null;
		this._swimLanes = null;
		this._eastwestlinks = null;
		this._connectorAlg = null;
		this._flowDirection = 0;
		this._levelJustification = 0;
		this._minDistNodeNode = new float[] { 30f, 30f };
		this._minDistNodeLink = new float[] { 15f, 15f };
		this._minDistLinkLink = new float[] { 5f, 5f };
		this._offsetForMinDist = new float[] { 0f, 0f };
		this._oldBaryCenter = new float[] { 0f, 0f };
		this._connectorStyle = 0;
		this._globalLinkStyle = 0;
		this._incrementalMode = false;
		this._crossingReductionDuringIncremental = false;
		this._longLinkCrossingReductionDuringIncremental = false;
		this._incrAbsLevelPosition = false;
		this._hasFixedNodes = false;
		this._hasOrthogonalLinks = false;
		this._hasPortSides = false;
		this._hasPortNumbers = false;
		this._hasFixedLinkSides = false;
		this._dummyFirstLevel = false;
		this._maxLinkPriority = 1f;
		this._constraintNodes = null;
		this._connBoxInterface = null;
		this._numberOfSwimLanes = 0;
		this._percCompleteController = new PercCompleteController();
	}

	public void AddConvertedIntergraphLink(HNode fromNode, HNode toNode) {
		if (((fromNode != null) && (toNode != null)) && (fromNode != toNode)) {
			HLink ownerLink = null;
			HSegmentIterator segmentsFrom = fromNode.GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {

				ownerLink = segmentsFrom.Next().GetOwnerLink();
				if (ownerLink.GetTo() == toNode) {
					ownerLink.SetPriority(ownerLink.GetPriority() + 0.01f);

					return;
				}
			}

			ownerLink = this.NewHLink(fromNode, toNode);
			ownerLink.SetPriority(0.01f);
			if (this._convertedIntergraphLinks == null) {
				this._convertedIntergraphLinks = new ArrayList();
			}
			this._convertedIntergraphLinks.Add(ownerLink);
		}

	}

	public HSwimLane AddDummySwimLane() {
		this._swimLanes = new HSwimLane(this._swimLanes, null);
		this._numberOfSwimLanes++;

		return this._swimLanes;

	}

	public void AddLink(HLink link) {
		this._links.Add(this, link);
		link.ActAfterAddToGraph();

	}

	public void AddNode(HNode node) {
		this._nodes.Add(this, node);
		node.ActAfterAddToGraph();

	}

	public void AddSegment(HSegment segment) {
		this._segments.Add(this, segment);
		segment.ActAfterAddToGraph();

	}

	public void AddSwimLane(HierarchicalSwimLaneConstraint constraint) {
		if (constraint.GetGroup().get_Count() != 0) {
			this._swimLanes = new HSwimLane(this._swimLanes, constraint);
			this._numberOfSwimLanes++;
			IJavaStyleEnumerator enumerator = new JavaStyleEnumerator(
					constraint.GetGroup().GetEnumerator());
			Integer num = 0;

			while (enumerator.HasMoreElements()) {
				HNode node = this.GetNode(enumerator.NextElement());
				if ((node != null) && (node.GetSwimLane() == null)) {
					node.SetSwimLane(this._swimLanes);
					num++;
				}
			}
			if (num == 0) {

				this._swimLanes = this._swimLanes.GetNext();
			}
		}

	}

	public void AllocConstraintNode(HNode node, HRPGraph constraintNetwork) {
		if (this._constraintNodes == null) {
			this._constraintNodes = new HRPNode[this.GetNumberOfNodes()];
		}
		HRPNode node2 = new HRPNode(1);
		constraintNetwork.AddNode(node2);
		this._constraintNodes[node.GetNumID()] = node2;

	}

	public void Attach(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.GraphLayout layoutAlgorithm) {
		this.Clean();
		this._graphModel = graphModel;
		HierarchicalLayout layout = this.GetLayout();
		this._connBoxInterface = layout.get_LinkConnectionBoxProvider();
		this.ATTACH_PROPERTY = "__ilvGraphLayoutAttach"
				+ layoutAlgorithm.GetInstanceId();
		Integer count = graphModel.get_Nodes().get_Count();
		Integer num2 = graphModel.get_Links().get_Count();
		this._percCompleteController.StartStep(this._percForAttach, count
				+ num2);
		this._connectorAlg = new CalcConnectors(this);
		this.CreateNodes();
		this.CreateLinks();
		layout.GetConstraintManager().BeforeLayout(this);
		this.HandleAllSwimLaneConstraints();
		this.Mirror();
		this.LayoutStepPerformed();

	}

	public void CalcBaryCenter(float[] baryCenter) {
		Integer num = 0;
		double[] numArray = new double[2];
		numArray[0] = numArray[1] = 0.0;
		HNodeIterator nodes = this.GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();

			if ((!node.IsDummyNode() && !node.IsInvalid())
					&& !node.IsEastWestPortAuxNode()) {

				numArray[0] += node.GetCenter(0);

				numArray[1] += node.GetCenter(1);
				num++;
			}
		}
		if (num > 0) {
			baryCenter[0] = (float) (numArray[0] / ((double) num));
			baryCenter[1] = (float) (numArray[1] / ((double) num));
		} else {
			baryCenter[0] = 0f;
			baryCenter[1] = 0f;
		}

	}

	public InternalRect CalcBoundingBox() {
		HNodeIterator nodes = this.GetNodes();
		InternalRect rect = new InternalRect(0f, 0f, 0f, 0f);
		InternalRect rect2 = null;
		Boolean flag = false;

		while (nodes.HasNext()) {
			HNode node = nodes.Next();

			rect.X = node.GetX();

			rect.Y = node.GetY();

			rect.Width = node.GetWidth();

			rect.Height = node.GetHeight();
			if (flag) {
				rect2.Add(rect);
			} else {
				rect2 = rect;
				rect = new InternalRect(0f, 0f, 0f, 0f);
				flag = true;
			}
		}

		return rect2;

	}

	public void CalcSwimLaneBoundsInEdgeFlow() {

		if (this.HasSwimLanes()) {
			Integer edgeFlow = this.GetEdgeFlow();
			float maxValue = Float.MAX_VALUE;
			float minValue = Float.MIN_VALUE;
			HNodeIterator nodes = this.GetNodes();

			while (nodes.HasNext()) {
				HNode node = nodes.Next();

				if ((!node.IsInvalid() && (node.GetLevel() != null))
						&& (node.GetSwimLane() != null)) {
					float coord = node.GetCoord(edgeFlow);
					if (coord < maxValue) {
						maxValue = coord;
					}

					coord += node.GetSize(edgeFlow);
					if (coord > minValue) {
						minValue = coord;
					}
				}
			}
			IJavaStyleEnumerator swimLanes = this.GetSwimLanes();

			while (swimLanes.HasMoreElements()) {
				HSwimLane lane = (HSwimLane) swimLanes.NextElement();
				lane.SetCoord(edgeFlow, maxValue);
				lane.SetSize(edgeFlow, minValue - maxValue);
			}
		}

	}

	public void Clean() {
		this._graphModel = null;
		this._nodes = new HGraphMemberList();
		this._links = new HGraphMemberList();
		this._segments = new HGraphMemberList();
		this._levels = new HGraphMemberList();
		this._levelsArray = null;
		this._swimLanes = null;
		this._eastwestlinks = null;
		if (this._connectorAlg != null) {
			this._connectorAlg.Clean();
			this._connectorAlg = null;
		}

	}

	public void CleanupConstraintNetworks() {
		HLevelIterator levels = this.GetLevels();

		while (levels.HasNext()) {
			levels.Next().CleanupConstraintNetwork();
		}
		this._constraintNodes = null;

	}

	private void CollectSubgraphData(IGraphModel rootModel, IGraphModel model,
			HNode node, Hashtable nodeTable) {
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(model.get_Nodes());

		while (enumerator.HasMoreElements()) {
			nodeTable.set_Item(enumerator.NextElement(), node);
		}

		enumerator = TranslateUtil.Collection2JavaStyleEnum(model
				.get_Subgraphs());

		while (enumerator.HasMoreElements()) {
			IGraphModel graphModel = rootModel.GetGraphModel(enumerator
					.NextElement());
			this.CollectSubgraphData(rootModel, graphModel, node, nodeTable);
		}

	}

	public void CreateConvertedIntergraphLinks() {
		Hashtable nodeTable = new Hashtable(500);
		IGraphModel root = this._graphModel.get_Root();
		if (root == null) {
			root = this._graphModel;
		}
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this._graphModel.get_Subgraphs());

		while (enumerator.HasMoreElements()) {
			java.lang.Object obj2 = enumerator.NextElement();
			HNode node = this.GetNode(obj2);
			if (node != null) {
				IGraphModel graphModel = root.GetGraphModel(obj2);
				this.CollectSubgraphData(root, graphModel, node, nodeTable);
			}
		}

		enumerator = TranslateUtil.Collection2JavaStyleEnum(this._graphModel
				.get_InterGraphLinks());

		while (enumerator.HasMoreElements()) {
			java.lang.Object link = enumerator.NextElement();
			java.lang.Object from = this._graphModel.GetFrom(link);
			java.lang.Object to = this._graphModel.GetTo(link);
			HNode fromNode = this.GetNode(from);
			if (fromNode == null) {
				fromNode = (HNode) nodeTable.get_Item(from);
			}
			HNode toNode = this.GetNode(to);
			if (toNode == null) {
				toNode = (HNode) nodeTable.get_Item(to);
			}
			this.AddConvertedIntergraphLink(fromNode, toNode);
		}

	}

	public void CreateLevels(Integer maxLevelNumber) {
		this._levelsArray = new HLevel[maxLevelNumber + 1];
		for (Integer i = 0; i <= maxLevelNumber; i++) {
			this._levelsArray[i] = new HLevel();
			this._levels.Add(this, this._levelsArray[i]);
		}

	}

	private void CreateLinks() {
		Integer globalLinkStyle = this.GetGlobalLinkStyle();
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this._graphModel.get_Links());
		Boolean linkWidthUsed = this._graphlayout.get_LinkWidthUsed();
		Boolean preserveFixedLinks = this._graphlayout.get_PreserveFixedLinks();
		Boolean flag3 = true;
		Boolean flag4 = false;
		Boolean flag5 = this.IsIncrementalMode();
		this._maxLinkPriority = 1f;
		this._hasOrthogonalLinks = false;
		this._hasPortSides = false;
		this._hasPortNumbers = false;
		this._hasFixedLinkSides = false;

		while (enumerator.HasMoreElements()) {
			java.lang.Object nodeOrLink = enumerator.NextElement();
			this._percCompleteController.AddPoints(1);

			if (!preserveFixedLinks || !this._graphlayout.GetFixed(nodeOrLink)) {
				float linkWidth = 0;
				Integer linkStyle = null;
				if (linkWidthUsed) {

					linkWidth = this._graphModel.GetLinkWidth(nodeOrLink);
				} else {
					linkWidth = 0f;
				}
				if (globalLinkStyle != 0x62) {
					linkStyle = globalLinkStyle;
				} else {
					linkStyle = (Integer) this._graphlayout
							.GetLinkStyle(nodeOrLink);
				}
				Integer num5 = linkStyle;
				if (num5 == 0) {
					linkStyle = 0x66;
					// NOTICE: break ignore!!!
				} else if (num5 == 1) {
					linkStyle = 0x65;
					// NOTICE: break ignore!!!
				} else if (num5 == 2) {
					linkStyle = 100;
					// NOTICE: break ignore!!!
				} else if (num5 == 3) {
					linkStyle = 0x63;
					// NOTICE: break ignore!!!
				} else {
					if (num5 == 0x63) {
						linkStyle = 0x63;
					}
					// NOTICE: break ignore!!!
				}
				HNode fromNode = this.GetNode(this._graphModel
						.GetFrom(nodeOrLink));
				HNode node = this.GetNode(this._graphModel.GetTo(nodeOrLink));
				if ((fromNode != null) && (node != null)) {
					HLink val = this.NewHLink(fromNode, node);
					if (flag5) {
						val.SetPointsForIncremental(GraphModelUtil
								.GetLinkPoints(this._graphModel, nodeOrLink));
					}

					if (this._graphlayout.IsMarkedForIncremental(nodeOrLink)) {
						val.MarkForIncremental();
					}
					float linkPriority = this._graphlayout
							.GetLinkPriority(nodeOrLink);
					val.SetPriority(linkPriority);
					if (linkPriority > this._maxLinkPriority) {
						this._maxLinkPriority = linkPriority;
					}
					val.SetThickness(linkWidth);
					val.SetLinkStyle(linkStyle);

					if (this._graphlayout.IsFromPointFixed(nodeOrLink)) {
						val.SetFromSideFixed(true);
					}

					if (this._graphlayout.IsToPointFixed(nodeOrLink)) {
						val.SetToSideFixed(true);
					}
					val.SetFromPortNumber(this._graphlayout
							.GetFromPortIndex(nodeOrLink));
					val.SetToPortNumber(this._graphlayout
							.GetToPortIndex(nodeOrLink));
					val.SetOrigFromPortSide(val
							.TranslateSide((Integer) this._graphlayout
									.GetFromPortSide(nodeOrLink)));
					val.SetOrigToPortSide(val
							.TranslateSide((Integer) this._graphlayout
									.GetToPortSide(nodeOrLink)));
					if (val.IsFromSideFixed() || val.IsToSideFixed()) {
						val.TranslateFixedEndPoints(nodeOrLink);
						this._hasFixedLinkSides = true;
					}
					Boolean flag6 = val.IsFromPortSideSpecified();
					Boolean flag7 = val.IsToPortSideSpecified();
					Boolean flag8 = val.IsFromPortNumberSpecified();
					Boolean flag9 = val.IsToPortNumberSpecified();
					if (flag6 || flag7) {
						this._hasPortSides = true;
					}
					if (flag8 || flag9) {
						this._hasPortNumbers = true;
					}
					if (flag6 && !flag8) {
						flag4 = true;
					}
					if (flag7 && !flag9) {
						flag4 = true;
					}
					if (linkStyle == 100) {
						this._hasOrthogonalLinks = true;
					}
					if (linkStyle != 0x65) {
						flag3 = false;
					}
					this._graphModel.SetProperty(nodeOrLink,
							this.ATTACH_PROPERTY, val);
				}
				this.LayoutStepPerformed();
			}
		}
		if (this._graphlayout.get_ConnectorStyle() == HierarchicalLayoutConnectorStyle.Automatic) {
			if (flag3) {
				this.SetConnectorStyle(0x63);
			}
			if (this._hasOrthogonalLinks || flag4) {
				this.SetConnectorStyle(0x65);
			}
		}

	}

	private void CreateNodes() {
		HierarchicalLayout layout = this.GetLayout();
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this._graphModel.get_Nodes());
		Boolean preserveFixedNodes = layout.get_PreserveFixedNodes();
		Integer incrementalNodeMovementMode = (Integer) layout
				.get_IncrementalNodeMovementMode();
		Boolean flag2 = false;

		while (enumerator.HasMoreElements()) {
			java.lang.Object nodeOrLink = enumerator.NextElement();
			this._percCompleteController.AddPoints(1);
			if (preserveFixedNodes && layout.GetFixed(nodeOrLink)) {
				continue;
			}
			Integer specNodeLevelIndex = layout
					.GetSpecNodeLevelIndex(nodeOrLink);
			Integer specNodePositionIndex = layout
					.GetSpecNodePositionIndex(nodeOrLink);
			Integer[] numberOfPorts = layout.GetNumberOfPorts(nodeOrLink);
			HNode node = this.NewHNode();
			node.SetSpecLevelNumber(specNodeLevelIndex);
			node.SetSpecPositionInLevel(specNodePositionIndex);
			node.SetNumberOfPorts(numberOfPorts);
			this.TransferCoordinates(nodeOrLink, node);

			if (this.IsIncrementalMode()) {
				Integer num4 = incrementalNodeMovementMode;
				if (num4 == 0x63) {
					num4 = (Integer) layout
							.GetIncrementalNodeMovementMode(nodeOrLink);
				}

				if (layout.IsMarkedForIncremental(nodeOrLink)) {
					node.MarkForIncremental();
				} else {
					if (num4 == 1) {
						node.SetFixedForIncremental(0);
						this._hasFixedNodes = true;
						if (this.GetLevelFlow() == 0) {
							flag2 = true;
						}
						this._graphModel.SetProperty(nodeOrLink,
								this.ATTACH_PROPERTY, node);
						this.LayoutStepPerformed();
						continue;
					} else if (num4 == 2) {
						node.SetFixedForIncremental(1);
						this._hasFixedNodes = true;
						if (this.GetLevelFlow() == 1) {
							flag2 = true;
						}
						this._graphModel.SetProperty(nodeOrLink,
								this.ATTACH_PROPERTY, node);
						this.LayoutStepPerformed();
						continue;
					} else if (num4 == 3) {
						node.SetFixedForIncremental(0);
						node.SetFixedForIncremental(1);
						flag2 = true;
						this._hasFixedNodes = true;
						this._graphModel.SetProperty(nodeOrLink,
								this.ATTACH_PROPERTY, node);
						this.LayoutStepPerformed();
						continue;
					}
				}
			}
			Label_0135: this._graphModel.SetProperty(nodeOrLink,
					this.ATTACH_PROPERTY, node);
			this.LayoutStepPerformed();
		}
		if (flag2) {
			this.SetCrossingReductionDuringIncremental(false);
		}

		if (this.IsIncrementalMode()) {
			this.CalcBaryCenter(this._oldBaryCenter);
		}

	}

	public void Detach(Boolean redraw) {
		Boolean flag = this._graphlayout.MayContinue();
		if (this._graphModel != null) {
			java.lang.Object obj2 = null;
			Integer num3 = 0;
			Integer count = this._graphModel.get_Nodes().get_Count();
			Integer pointsOfStep = this._graphModel.get_Links().get_Count();
			this._percCompleteController.StartStep(this._percForDetachNodes,
					(3 * this.GetNumberOfLevels()) + count);
			if (flag) {
				HLevelIterator levels = this.GetLevels();

				while (levels.HasNext()) {
					levels.Next().StoreLevelPositionsInNodes(0, true);
					this._percCompleteController.AddPoints(3);
					this.LayoutStepPerformed();
				}
			}

			if (this.HasDummyFirstLevel()) {
				num3 = -1;
			}
			IJavaStyleEnumerator swimLanes = TranslateUtil
					.Collection2JavaStyleEnum(this._graphModel.get_Nodes());

			while (swimLanes.HasMoreElements()) {

				obj2 = swimLanes.NextElement();
				HNode node = this.GetNode(obj2);
				this._percCompleteController.AddPoints(1);
				if (node != null) {
					float x = 0;
					float y = 0;
					InternalRect rect = GraphModelUtil.BoundingBox(
							this._graphModel, obj2);
					if (rect.Width >= 0f) {

						x = node.GetX();
					} else {
						x = node.GetX() - rect.Width;
					}
					if (rect.Height >= 0f) {

						y = node.GetY();
					} else {
						y = node.GetY() - rect.Height;
					}

					x += node.GetOffsetToRealTopLeft(0);

					y += node.GetOffsetToRealTopLeft(1);
					if (flag) {
						this._graphlayout.SetCalcNodeLevelIndex(obj2,
								node.GetLevelNumber() + num3);
						this._graphlayout.SetCalcNodePositionIndex(obj2,
								node.GetPositionInLevel());
						this._graphModel.MoveNode(obj2, x, y);
					}
					this._graphModel.SetProperty(obj2, this.ATTACH_PROPERTY,
							null);
					this.LayoutStepPerformed();
				}
			}
			this.CalcSwimLaneBoundsInEdgeFlow();

			swimLanes = this.GetSwimLanes();

			while (swimLanes.HasMoreElements()) {
				((HSwimLane) swimLanes.NextElement()).SetBoundingBox();
			}
			this._graphlayout.ClearAllIncrementalNodeBoxesForExpand();
			this._percCompleteController.StartFinalStep(
					this._percForDetachLinks, pointsOfStep);

			swimLanes = TranslateUtil.Collection2JavaStyleEnum(this._graphModel
					.get_Links());
			InternalPoint point = null;
			InternalPoint point2 = null;
			InternalRect auxRect = null;
			if (flag) {

				while (swimLanes.HasMoreElements()) {

					obj2 = swimLanes.NextElement();
					this._percCompleteController.AddPoints(1);
					this.ReshapeLink(obj2, point, point2, auxRect, redraw);
					this._graphModel.SetProperty(obj2, this.ATTACH_PROPERTY,
							null);
					this.LayoutStepPerformed();
				}
			}
		}
		this._percCompleteController.StopAll();
		this.Clean();

	}

	public void FinalPositionCorrection() {
		this._percCompleteController.StartStep(
				this._percForFinalPositionCorrection, 3);
		this.Mirror();

		if (!this.HasFixedNodes()) {
			InternalPoint internalPosition = this.GetLayout()
					.GetInternalPosition();
			if (internalPosition != null) {
				InternalRect rect = this.CalcBoundingBox();
				this.ShiftBy(internalPosition.X - rect.X, internalPosition.Y
						- rect.Y);
			} else if (this.IsIncrementalMode()) {
				float[] baryCenter = new float[2];
				this.CalcBaryCenter(baryCenter);
				this.ShiftBy(this._oldBaryCenter[0] - baryCenter[0],
						this._oldBaryCenter[1] - baryCenter[1]);
			} else {
				InternalRect rect2 = this.CalcBoundingBox();
				this.ShiftBy(-rect2.X, -rect2.Y);
			}
		}
		this._percCompleteController.AddPoints(1);
		this.LayoutStepPerformed();

	}

	public CalcConnectors GetConnectorAlgorithm() {

		return this._connectorAlg;

	}

	public Integer GetConnectorStyle() {

		return this._connectorStyle;

	}

	public HRPNode GetConstraintNode(HNode node) {
		if (this._constraintNodes == null) {

			return null;
		}

		return this._constraintNodes[node.GetNumID()];

	}

	public HLinkIterator GetEastWestLinks() {

		return new AnonClass_5(this);

	}

	public Integer GetEdgeFlow() {

		return (this._flowDirection % 2);

	}

	public HLevel GetFirstLevel() {

		return (HLevel) this._levels.GetFirst();

	}

	public Integer GetFlowDirection() {

		return this._flowDirection;

	}

	public Integer GetGlobalLinkStyle() {

		return this._globalLinkStyle;

	}

	public IGraphModel GetGraphModel() {

		return this._graphModel;

	}

	public HLevel GetLastLevel() {

		return (HLevel) this._levels.GetLast();

	}

	public HNode GetLastNode() {

		return (HNode) this._nodes.GetLast();

	}

	public HSegment GetLastSegment() {

		return (HSegment) this._segments.GetLast();

	}

	public HierarchicalLayout GetLayout() {

		return this._graphlayout;

	}

	public HLevel GetLevel(Integer numID) {

		return this._levelsArray[numID];

	}

	public Integer GetLevelFlow() {

		return ((this._flowDirection + 1) % 2);

	}

	public Integer GetLevelJustification() {

		return this._levelJustification;

	}

	public HLevelIterator GetLevels() {

		return new AnonClass_4(this);

	}

	public HLevelIterator GetLevelsBackward() {
		HLevelIterator levels = this.GetLevels();
		levels.Init(this.GetLastLevel());

		return levels;

	}

	public HLink GetLink(java.lang.Object link) {

		return (HLink) this._graphModel.GetProperty(link, this.ATTACH_PROPERTY);

	}

	public HLinkIterator GetLinks() {

		return new AnonClass_2(this);

	}

	public float GetMaxLinkPriority() {

		return this._maxLinkPriority;

	}

	public float GetMinDist(Integer index, HNode node1, HNode node2) {
		float offsetForMinDist = 0;
		if ((node1.GetSwimLane() == null) != (node2.GetSwimLane() == null)) {
			offsetForMinDist = 0f;
		} else if (node1.IsDummyNode()) {

			if (node2.IsDummyNode()) {
				offsetForMinDist = (this.GetMinDistBetweenLinks(index) + this
						.GetOffsetForMinDist(index))
						+ (0.5f * (node1.GetOwnerLink().GetThickness() + node2
								.GetOwnerLink().GetThickness()));
			} else if (node2.IsEastWestPortAuxNode()) {
				offsetForMinDist = (0.5f * node1.GetOwnerLink().GetThickness())
						+ this.GetOffsetForMinDist(index);
			} else {
				offsetForMinDist = (this.GetMinDistBetweenNodeAndLink(index) + this
						.GetOffsetForMinDist(index))
						+ (0.5f * node1.GetOwnerLink().GetThickness());
			}
		} else if (node1.IsEastWestPortAuxNode()) {

			if (node2.IsDummyNode()) {
				offsetForMinDist = (0.5f * node2.GetOwnerLink().GetThickness())
						+ this.GetOffsetForMinDist(index);
			} else if (node2.IsEastWestPortAuxNode()) {

				offsetForMinDist = this.GetOffsetForMinDist(index);
			} else if ((node1 == node2.GetEastPortAuxNode())
					|| (node1 == node2.GetWestPortAuxNode())) {
				offsetForMinDist = this.GetMinDistBetweenNodeAndLink(index)
						- this.GetMinDistBetweenLinks(index);
			} else {
				offsetForMinDist = (this.GetMinDistBetweenNodeAndLink(index) - this
						.GetMinDistBetweenLinks(index))
						+ this.GetOffsetForMinDist(index);
			}
		} else if (node2.IsDummyNode()) {
			offsetForMinDist = (this.GetMinDistBetweenNodeAndLink(index) + this
					.GetOffsetForMinDist(index))
					+ (0.5f * node2.GetOwnerLink().GetThickness());
		} else if (node2.IsEastWestPortAuxNode()) {
			if ((node2 == node1.GetEastPortAuxNode())
					|| (node2 == node1.GetWestPortAuxNode())) {
				offsetForMinDist = this.GetMinDistBetweenNodeAndLink(index)
						- this.GetMinDistBetweenLinks(index);
			} else {
				offsetForMinDist = (this.GetMinDistBetweenNodeAndLink(index) - this
						.GetMinDistBetweenLinks(index))
						+ this.GetOffsetForMinDist(index);
			}
		} else {
			offsetForMinDist = this.GetMinDistBetweenNodes(index)
					+ this.GetOffsetForMinDist(index);
		}
		if (offsetForMinDist < 0f) {
			offsetForMinDist = 0f;
		}

		return offsetForMinDist;

	}

	public float GetMinDistBetweenLinks(Integer direction) {

		return this._minDistLinkLink[direction % 2];

	}

	public float GetMinDistBetweenNodeAndLink(Integer direction) {

		return this._minDistNodeLink[direction % 2];

	}

	public float GetMinDistBetweenNodes(Integer direction) {

		return this._minDistNodeNode[direction % 2];

	}

	public HNode GetNode(java.lang.Object node) {

		return (HNode) this._graphModel.GetProperty(node, this.ATTACH_PROPERTY);

	}

	public HNodeIterator GetNodes() {

		return new AnonClass_1(this);

	}

	public HNode[] GetNodesSortedByCoordForIncremental(Integer dir) {
		Integer numberOfNodes = this.GetNumberOfNodes();
		Integer index = 0;
		HNode[] a = new HNode[numberOfNodes];
		HNodeIterator nodes = this.GetNodes();

		while (nodes.HasNext()) {

			a[index] = nodes.Next();
			a[index].SetSortValue(a[index].GetCenter(dir)
					- (0.5f * a[index].GetOldSizeInEdgeFlow()));
			index++;
		}
		new HNodeSort().Sort(a);

		return a;

	}

	public Integer GetNumberOfEastWestLinks() {
		if (this._eastwestlinks == null) {

			return 0;
		}

		return this._eastwestlinks.GetSize();

	}

	public Integer GetNumberOfLevels() {

		return this._levels.GetSize();

	}

	public Integer GetNumberOfLinks() {

		return this._links.GetSize();

	}

	public Integer GetNumberOfNodes() {

		return this._nodes.GetSize();

	}

	public Integer GetNumberOfSegments() {

		return this._segments.GetSize();

	}

	public Integer GetNumberOfSwimLanes() {

		return this._numberOfSwimLanes;

	}

	public float GetOffsetForMinDist(Integer direction) {

		return this._offsetForMinDist[direction];

	}

	public PercCompleteController GetPercController() {

		return this._percCompleteController;

	}

	public HSegmentIterator GetSegments() {

		return new AnonClass_3(this);

	}

	public HSwimLane[] GetSortedSwimLanes() {
		Integer numberOfSwimLanes = this.GetNumberOfSwimLanes();
		HSwimLane[] laneArray = new HSwimLane[numberOfSwimLanes];
		HSwimLane[] laneArray2 = laneArray;
		Integer num2 = 0;
		IJavaStyleEnumerator swimLanes = this.GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			laneArray2[num2++] = (HSwimLane) swimLanes.NextElement();
		}
		new AnonClass_7(laneArray2).Sort(0, numberOfSwimLanes - 1);

		return laneArray;

	}

	public IJavaStyleEnumerator GetSwimLanes() {

		return new AnonClass_6(this);

	}

	public void HandleAllSwimLaneConstraints() {
		IJavaStyleEnumerator constraints = this.GetLayout()
				.GetConstraintManager().GetConstraints();

		while (constraints.HasMoreElements()) {
			HierarchicalConstraint constraint = (HierarchicalConstraint) constraints
					.NextElement();
			if (constraint.IsValidForLayout()
					&& (constraint instanceof HierarchicalSwimLaneConstraint)) {
				this.AddSwimLane((HierarchicalSwimLaneConstraint) constraint);
			}
		}

	}

	public Boolean HasDummyFirstLevel() {

		return this._dummyFirstLevel;

	}

	public Boolean HasFixedLinkSides() {

		return this._hasFixedLinkSides;

	}

	public Boolean HasFixedNodes() {

		return this._hasFixedNodes;

	}

	public Boolean HasFixedNodesInLevels(Integer index) {
		HLevelIterator levels = this.GetLevels();

		while (levels.HasNext()) {
			HLevel level = levels.Next();
			level.UpdateInfo();

			if (level.HasFixedNodes(index)) {

				return true;
			}
		}

		return false;

	}

	public Boolean HasOrthogonalLinks() {

		return this._hasOrthogonalLinks;

	}

	public Boolean HasPortNumbers() {

		return this._hasPortNumbers;

	}

	public Boolean HasPorts() {

		if (!this.HasPortNumbers()) {

			return this.HasPortSides();
		}

		return true;

	}

	public Boolean HasPortSides() {

		return this._hasPortSides;

	}

	public Boolean HasSwimLanes() {

		return (this._swimLanes != null);

	}

	public Boolean IsCrossingReductionDuringIncremental() {

		return this._crossingReductionDuringIncremental;

	}

	public Boolean IsIncrementalAbsoluteLevelPositioning() {

		return this._incrAbsLevelPosition;

	}

	public Boolean IsIncrementalMode() {

		return this._incrementalMode;

	}

	public Boolean IsLongLinkCrossingReductionDuringIncremental() {

		return this._longLinkCrossingReductionDuringIncremental;

	}

	public void LayoutStepPerformed() {
		if (this._graphlayout != null) {
			this._graphlayout.OnLayoutStepPerformedIfNeeded();
		}

	}

	public void MakeEastWestLink(HLink link) {
		this.RemoveLink(link);
		link.ActWhenMakeEastWestLink();
		if (this._eastwestlinks == null) {
			this._eastwestlinks = new HGraphMemberList();
		}
		this._eastwestlinks.Add(this, link);

	}

	public void Mirror() {
		if (this.GetFlowDirection() == 0) {
			this.Mirror(1);

			return;
		} else if (this.GetFlowDirection() == 1) {
			// NOTICE: break ignore!!!
		} else if (this.GetFlowDirection() == 2) {
			this.Mirror(0);

			return;
		} else if (this.GetFlowDirection() == 3) {
			this.Mirror(0);
			this.Mirror(1);
			// NOTICE: break ignore!!!
		} else {

			return;
		}

	}

	public void Mirror(Integer coordinateIndex) {
		HNodeIterator nodes = this.GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().Mirror(coordinateIndex);
		}
		this._percCompleteController.AddPoints(1);
		this.LayoutStepPerformed();
		HLinkIterator links = this.GetLinks();

		while (links.HasNext()) {
			links.Next().Mirror(coordinateIndex, false);
		}

		links = this.GetEastWestLinks();

		while (links.HasNext()) {
			links.Next().Mirror(coordinateIndex, true);
		}
		IJavaStyleEnumerator swimLanes = this.GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			((HSwimLane) swimLanes.NextElement()).Mirror(coordinateIndex);
		}
		this._percCompleteController.AddPoints(1);
		this.LayoutStepPerformed();

	}

	public HNode NewDummyHNode(HLink ownerLink) {
		HNode node = this.NewHNode();
		node.SetOwner(ownerLink);
		node.SetSwimLane(ownerLink.GetTo().GetSwimLane());

		return node;

	}

	public HLink NewHLink(HNode fromNode, HNode toNode) {
		HLink link = new HLink(fromNode, toNode);
		this.AddLink(link);

		return link;

	}

	public HNode NewHNode() {
		HNode node = new HNode();
		this.AddNode(node);

		return node;

	}

	public HSegment NewHSegment(HNode fromNode, HNode toNode, HLink ownerLink) {
		HSegment segment = new HSegment(fromNode, toNode);
		this._segments.Add(this, segment);
		segment.SetOwner(ownerLink);

		return segment;

	}

	public void RemoveConvertedIntergraphLinks() {
		if (this._convertedIntergraphLinks != null) {
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(this._convertedIntergraphLinks);

			while (enumerator.HasMoreElements()) {
				HLink link = (HLink) enumerator.NextElement();
				this.RemoveLink(link);
			}
			this._convertedIntergraphLinks = null;
		}

	}

	public void RemoveLastLevel() {
		this._levelsArray[this._levels.GetSize() - 1] = null;
		this._levels.RemoveLast();

	}

	public void RemoveLink(HLink link) {
		link.ActBeforeRemoveFromGraph();
		this._links.Remove(link);

	}

	public void RemoveNode(HNode node) {
		node.ActBeforeRemoveFromGraph();
		this._nodes.Remove(node);

	}

	public void RemoveSegment(HSegment segment) {
		segment.ActBeforeRemoveFromGraph();
		this._segments.Remove(segment);

	}

	public void ReserveDummyFirstLevel() {
		this._dummyFirstLevel = true;

	}

	private void ReshapeLink(java.lang.Object linkObj, InternalPoint auxPnt1,
			InternalPoint auxPnt2, InternalRect auxRect, Boolean redraw) {
		HLink link = this.GetLink(linkObj);
		if (link != null) {
			InternalPoint fromPoint = link.GetFromPoint();
			InternalPoint toPoint = link.GetToPoint();
			if (link.GetLinkStyle() == 0x65) {
				if (link.IsFromSideFixed() && (fromPoint != null)) {
					fromPoint = new FixedPoint(fromPoint);
				}
				if (link.IsToSideFixed() && (toPoint != null)) {
					toPoint = new FixedPoint(toPoint);
				}
				this.ReshapeLink(linkObj, fromPoint, toPoint, null,
						link.GetLinkStyle(), redraw);

				return;
			} else if (link.GetLinkStyle() == 0x66) {
				// NOTICE: break ignore!!!
			} else {
				InternalPoint[] intermediatePoints = link
						.GetIntermediatePoints();
				if (link.IsFromSideFixed() && (fromPoint != null)) {
					fromPoint = new FixedPoint(fromPoint);
				}
				if (link.IsToSideFixed() && (toPoint != null)) {
					toPoint = new FixedPoint(toPoint);
				}
				this.ReshapeLink(linkObj, fromPoint, toPoint,
						intermediatePoints, link.GetLinkStyle(), redraw);
			}
		}

	}

	private void ReshapeLink(java.lang.Object linkObj, InternalPoint from,
			InternalPoint to, InternalPoint[] points, Integer linkStyle,
			Boolean redraw) {
		if (((linkStyle == 100) && (points != null)) && (points.length > 0)) {
			InternalPoint[] linkPoints = null;
			if (from instanceof FixedPoint) {

				linkPoints = GraphModelUtil.GetLinkPoints(this._graphModel,
						linkObj);
				InternalPoint point = linkPoints[0];
				float num = Math.Abs((float) (point.X - points[0].X));
				float num2 = Math.Abs((float) (point.Y - points[0].Y));
				if (num < num2) {
					points[0].X = point.X;
				} else {
					points[0].Y = point.Y;
				}
			}
			if (to instanceof FixedPoint) {
				if (linkPoints == null) {

					linkPoints = GraphModelUtil.GetLinkPoints(this._graphModel,
							linkObj);
				}
				InternalPoint point2 = linkPoints[linkPoints.length - 1];
				Integer index = points.length - 1;
				float num4 = Math.Abs((float) (point2.X - points[index].X));
				float num5 = Math.Abs((float) (point2.Y - points[index].Y));
				if (num4 < num5) {
					points[index].X = point2.X;
				} else {
					points[index].Y = point2.Y;
				}
			}
		}
		if (linkStyle == 0x62 || linkStyle == 0x66) {
			linkStyle = 3;
			// NOTICE: break ignore!!!
		} else if (linkStyle == 0x63) {
			linkStyle = 3;
			// NOTICE: break ignore!!!
		} else if (linkStyle == 100) {
			linkStyle = 2;
			// NOTICE: break ignore!!!
		} else if (linkStyle == 0x65) {
			linkStyle = 1;
			// NOTICE: break ignore!!!
		}
		GraphModelUtil.ReshapeLink(this._graphModel, this._graphlayout,
				linkObj, linkStyle, from, points, 0, (points == null) ? 0
						: points.length, to);

	}

	public void SetConnectorStyle(Integer style) {
		this._connectorStyle = style;

	}

	public void SetCrossingReductionDuringIncremental(Boolean mode) {
		this._crossingReductionDuringIncremental = mode;

	}

	public void SetFlowDirection(Integer flowDirection) {
		this._flowDirection = flowDirection;

	}

	public void SetGlobalLinkStyle(Integer style) {
		this._globalLinkStyle = style;

	}

	public void SetIncrementalAbsoluteLevelPositioning(Boolean mode) {
		this._incrAbsLevelPosition = mode;

	}

	public void SetIncrementalMode(Boolean mode) {
		this._incrementalMode = mode;

	}

	public void SetLevelJustification(Integer levelJustification) {
		this._levelJustification = levelJustification;

	}

	public void SetLongLinkCrossingReductionDuringIncremental(Boolean mode) {
		this._longLinkCrossingReductionDuringIncremental = mode;

	}

	public void SetMinDistBetweenLinks(Integer direction, float dist) {
		if (dist <= 0f) {
			dist = 1E-08f;
		}
		this._minDistLinkLink[direction % 2] = dist;

	}

	public void SetMinDistBetweenNodeAndLink(Integer direction, float dist) {
		if (dist <= 0f) {
			dist = 1E-08f;
		}
		this._minDistNodeLink[direction % 2] = dist;

	}

	public void SetMinDistBetweenNodes(Integer direction, float dist) {
		if (dist <= 0f) {
			dist = 1E-08f;
		}
		this._minDistNodeNode[direction % 2] = dist;

	}

	public void SetOffsetForMinDist(Integer direction, float offset) {
		this._offsetForMinDist[direction] = offset;

	}

	public void ShiftBy(float dx, float dy) {
		HNodeIterator nodes = this.GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().ShiftBy(dx, dy);
		}
		HLinkIterator links = this.GetLinks();

		while (links.HasNext()) {
			links.Next().ShiftBy(dx, dy, false);
		}

		links = this.GetEastWestLinks();

		while (links.HasNext()) {
			links.Next().ShiftBy(dx, dy, true);
		}
		IJavaStyleEnumerator swimLanes = this.GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			((HSwimLane) swimLanes.NextElement()).ShiftBy(dx, dy);
		}

	}

	public void ToggleLevelsSwimLaneMode() {
		HLevelIterator levels = this.GetLevels();

		while (levels.HasNext()) {
			levels.Next().SwapNodesFieldForSwimLanes();
		}

	}

	private void TransferCoordinates(java.lang.Object nodeObj, HNode node) {
		InternalRect rect = null;
		InternalRect realBBox = GraphModelUtil.BoundingBox(this._graphModel,
				nodeObj);
		if (this._connBoxInterface != null) {

			rect = TranslateUtil.GetBox(this._connBoxInterface,
					this._graphModel, nodeObj);
		} else {
			rect = realBBox;
		}
		node.SetBoundingBoxes(rect, realBBox);
		node.SetOldSizeInEdgeFlow(TranslateUtil
				.Rectangle2D2InternalRect(this._graphlayout
						.GetIncrementalNodeBoxForExpand(nodeObj)));

	}

	public void UpdateLevelIDs() {
		this._levels.UpdateNumIDs();

	}

	public void UpdateLinkIDs() {
		this._links.UpdateNumIDs();

	}

	public void UpdateNodeIDs() {
		this._nodes.UpdateNumIDs();

	}

	public void UpdateSegmentIDs() {
		this._segments.UpdateNumIDs();

	}

	private class AnonClass_1 implements HNodeIterator {
		private HGraph __outerThis;

		public HNode _next;

		public AnonClass_1(HGraph input__outerThis) {
			this.__outerThis = input__outerThis;
			this._next = (HNode) this.__outerThis._nodes.GetFirst();
		}

		public Boolean HasNext() {

			return (this._next != null);

		}

		public void Init(HNode node) {
			this._next = node;

		}

		public HNode Next() {
			HNode node = this._next;
			this._next = (HNode) this._next.GetNext();

			return node;

		}

	}

	private class AnonClass_2 implements HLinkIterator {
		private HGraph __outerThis;

		public HLink _next;

		public AnonClass_2(HGraph input__outerThis) {
			this.__outerThis = input__outerThis;
			this._next = (HLink) this.__outerThis._links.GetFirst();
		}

		public Boolean HasNext() {

			return (this._next != null);

		}

		public HLink Next() {
			HLink link = this._next;
			this._next = (HLink) this._next.GetNext();

			return link;

		}

	}

	private class AnonClass_3 implements HSegmentIterator {
		private HGraph __outerThis;

		public HSegment _next;

		public AnonClass_3(HGraph input__outerThis) {
			this.__outerThis = input__outerThis;
			this._next = (HSegment) this.__outerThis._segments.GetFirst();
		}

		public Boolean HasNext() {

			return (this._next != null);

		}

		public HSegment Next() {
			HSegment segment = this._next;
			this._next = (HSegment) this._next.GetNext();

			return segment;

		}

		public void Remove() {
			throw (new system.Exception("HSegmentIterator in graph"));

		}

	}

	private class AnonClass_4 implements HLevelIterator {
		private HGraph __outerThis;

		public HLevel _level;

		public AnonClass_4(HGraph input__outerThis) {
			this.__outerThis = input__outerThis;
			this._level = (HLevel) this.__outerThis._levels.GetFirst();
		}

		public Boolean HasNext() {

			return (this._level != null);

		}

		public Boolean HasPrev() {

			return (this._level != null);

		}

		public void Init(HLevel level) {
			this._level = level;

		}

		public HLevel Next() {
			HLevel level = this._level;
			this._level = (HLevel) this._level.GetNext();

			return level;

		}

		public HLevel Prev() {
			HLevel level = this._level;
			this._level = (HLevel) this._level.GetPrev();

			return level;

		}

	}

	private class AnonClass_5 implements HLinkIterator {
		private HGraph __outerThis;

		public HLink _next;

		public AnonClass_5(HGraph input__outerThis) {
			this.__outerThis = input__outerThis;
			this._next = (this.__outerThis._eastwestlinks == null) ? null
					: ((HLink) this.__outerThis._eastwestlinks.GetFirst());
		}

		public Boolean HasNext() {

			return (this._next != null);

		}

		public HLink Next() {
			HLink link = this._next;
			this._next = (HLink) this._next.GetNext();

			return link;

		}

	}

	private class AnonClass_6 implements IJavaStyleEnumerator {
		private HGraph __outerThis;

		public HSwimLane next;

		public AnonClass_6(HGraph input__outerThis) {
			this.__outerThis = input__outerThis;
			this.next = this.__outerThis._swimLanes;
		}

		public Boolean HasMoreElements() {

			return (this.next != null);

		}

		public java.lang.Object NextElement() {
			HSwimLane next = this.next;

			this.next = this.next.GetNext();

			return next;

		}

	}

	private class AnonClass_7 extends QuickSort {
		private HSwimLane[] __lanes;

		public AnonClass_7(HSwimLane[] input__lanes) {
			this.__lanes = input__lanes;
		}

		@Override
		public Integer Compare(Integer p1, Integer p2) {

			return (this.__lanes[p1].GetOrderingNumber() - this.__lanes[p2]
					.GetOrderingNumber());

		}

		@Override
		public void Swap(Integer p1, Integer p2) {
			HSwimLane lane = this.__lanes[p1];
			this.__lanes[p1] = this.__lanes[p2];
			this.__lanes[p2] = lane;

		}

	}
}