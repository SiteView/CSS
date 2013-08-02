package ILOG.Diagrammer.GraphLayout;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import system.ArgumentException;
import system.EventArgs;
import system.EventHandler;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.ComponentModel.PropertyChangedEventHandler;
import ILOG.Diagrammer.IDiagramView;
import ILOG.Diagrammer.Point2D;
import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.Size2D;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.LayoutUtil;
import ILOG.Diagrammer.GraphLayout.Internal.PercCompleteController;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;
import ILOG.Diagrammer.GraphLayout.Internal.TLayout.TGraph;
import ILOG.Diagrammer.GraphLayout.Internal.TLayout.TLinkParameters;
import ILOG.Diagrammer.GraphLayout.Internal.TLayout.TNodeIterator;
import ILOG.Diagrammer.GraphLayout.Internal.TLayout.TNodeParameters;
import ILOG.Diagrammer.GraphLayout.Internal.TLayout.TRadial;
import ILOG.Diagrammer.GraphLayout.Internal.TLayout.TSpanTree;

public class TreeLayout extends ILOG.Diagrammer.GraphLayout.GraphLayout {
	private Boolean _allLevelsAlternating = false;

	private Boolean _allowStopImmediately = false;

	private double[] _angleTransTable;

	private double _angleTransTableAspectRatio;

	private float _aspectRatio;

	private float _branchOffset;

	public ArrayList _calcBackwardTreeLinks;

	public ArrayList _calcForwardTreeLinks;

	public ArrayList _calcNonTreeLinks;

	public ArrayList _calcRoots;

	private int _connectorStyle;

	public int _defaultAlignment = TreeLayoutAlignment.Center;

	public Boolean _defaultAllLevelsAlternating = false;

	public float _defaultAspectRatio = 1f;

	public float _defaultBranchOffset = 40f;

	public int _defaultConnectorStyle = TreeLayoutConnectorStyle.Automatic;

	public Boolean _defaultFirstCircleEvenlySpacing = false;

	public int _defaultFlowDirection = LayoutFlowDirection.Right;

	public Boolean _defaultIncrementalMode = true;

	public int _defaultLayoutMode = TreeLayoutMode.Free;

	public int _defaultLevelAlignment = TreeLayoutLevelAlignment.Center;

	public int _defaultLinkStyle = TreeLayoutLinkStyle.Straight;

	public Integer _defaultMaxChildrenAngle = 0;

	public Boolean _defaultNonTreeLinksStraight = false;

	public float _defaultOrthForkPercentage = 45f;

	public float _defaultOverlapPercentage = 30f;

	public float _defaultParentChildOffset = 40f;

	public Boolean _defaultRespectNodeSizes = true;

	public float _defaultSiblingOffset = 40f;

	public float _defaultTipOverBranchOffset = 40f;

	private Boolean _firstCircleEvenlySpacing = false;

	private int _flowDirection;

	private int _globalAlignment;

	private int _globalLinkStyle;

	private Boolean _incrementalMode = false;

	private Boolean _isInvisibleRootUsed = false;

	private Boolean _isRootPosition = false;

	private int _layoutMode;

	private int _levelAlignment;

	private Integer _maxChildrenAngle;

	private Boolean _nonTreeLinksStraight = false;

	private float _orthForkPercentage;

	private float _overlapPercentage;

	private float _parentChildOffset;

	private PercCompleteController _percCompleteControler;

	private InternalPoint _position;

	private Boolean _respectNodeSizes = false;

	private float _siblingOffset;

	public ArrayList _specRoots;

	private Boolean _stoppedBeforeCompletion = false;

	private float _tipOverBranchOffset;

	// private GraphLayoutConstraintCollection<TreeConstraint> constraints;

	public String LINK_PROPERTY;

	public String NODE_PROPERTY;

	/* TODO: Event Declare */
	public List<EventHandler> ConstraintsChanged = new Vector<EventHandler>();

	public TreeLayout() {
		this._angleTransTableAspectRatio = 1.0;
	}

	public TreeLayout(TreeLayout source) {
		super(source);
		this._angleTransTableAspectRatio = 1.0;
	}

	public void AddCalcBackwardTreeLink(java.lang.Object link) {
		if (this._calcBackwardTreeLinks != null) {
			TLinkParameters.GetOrAllocate(this, link).SetCalcBackwardTreeLink(
					true);
			this._calcBackwardTreeLinks.Add(link);
		}

	}

	public void AddCalcForwardTreeLink(java.lang.Object link) {
		if (this._calcForwardTreeLinks != null) {
			TLinkParameters.GetOrAllocate(this, link).SetCalcForwardTreeLink(
					true);
			this._calcForwardTreeLinks.Add(link);
		}

	}

	public void AddCalcNonTreeLink(java.lang.Object link) {
		if (this._calcNonTreeLinks != null) {
			TLinkParameters.GetOrAllocate(this, link).SetCalcNonTreeLink(true);
			this._calcNonTreeLinks.Add(link);
		}

	}

	public void AddCalcRoot(java.lang.Object node) {
		TNodeParameters orAllocate = TNodeParameters.GetOrAllocate(this, node);

		if (!orAllocate.IsCalcRoot()) {
			orAllocate.SetCalcRoot(true);
			this._calcRoots.Add(node);
		}

	}

	private void CleanCalcBackwardTreeLinks() {
		if (this._calcBackwardTreeLinks != null) {
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(this._calcBackwardTreeLinks);

			while (enumerator.HasMoreElements()) {
				java.lang.Object link = enumerator.NextElement();
				TLinkParameters parameters = TLinkParameters.Get(this, link);
				if ((parameters != null) && parameters.IsCalcBackwardTreeLink()) {
					parameters.SetCalcBackwardTreeLink(false);

					if (!parameters.IsNeeded()) {
						parameters.Dispose(this, link);
					}
				}
			}
			this._calcBackwardTreeLinks.Clear();
		}

	}

	private void CleanCalcForwardTreeLinks() {
		if (this._calcForwardTreeLinks != null) {
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(this._calcForwardTreeLinks);

			while (enumerator.HasMoreElements()) {
				java.lang.Object link = enumerator.NextElement();
				TLinkParameters parameters = TLinkParameters.Get(this, link);
				if ((parameters != null) && parameters.IsCalcForwardTreeLink()) {
					parameters.SetCalcForwardTreeLink(false);

					if (!parameters.IsNeeded()) {
						parameters.Dispose(this, link);
					}
				}
			}
			this._calcForwardTreeLinks.Clear();
		}

	}

	private void CleanCalcNonTreeLinks() {
		if (this._calcNonTreeLinks != null) {
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(this._calcNonTreeLinks);

			while (enumerator.HasMoreElements()) {
				java.lang.Object link = enumerator.NextElement();
				TLinkParameters parameters = TLinkParameters.Get(this, link);
				if ((parameters != null) && parameters.IsCalcNonTreeLink()) {
					parameters.SetCalcNonTreeLink(false);

					if (!parameters.IsNeeded()) {
						parameters.Dispose(this, link);
					}
				}
			}
			this._calcNonTreeLinks.Clear();
		}

	}

	private void CleanCalcRoots() {
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this.GetCalcRoots());

		while (enumerator.HasMoreElements()) {
			java.lang.Object node = enumerator.NextElement();
			TNodeParameters parameters = TNodeParameters.Get(this, node);
			if ((parameters != null) && parameters.IsCalcRoot()) {
				parameters.SetCalcRoot(false);

				if (!parameters.IsNeeded()) {
					parameters.Dispose(this, node);
				}
			}
		}
		this._calcRoots.Clear();

	}

	@Override
	public void CleanLink(IGraphModel graphModel, java.lang.Object link) {
		super.CleanLink(graphModel, link);
		this.RemoveCalcLinkFromCategory(link);
		TLinkParameters parameters = TLinkParameters.Get(this, link);
		if (parameters != null) {
			parameters.Dispose(this, link);
		}

	}

	@Override
	public void CleanNode(IGraphModel graphModel, java.lang.Object node) {
		super.CleanNode(graphModel, node);
		this.RemoveSpecRoot(node);
		this.RemoveCalcRoot(node);
		TNodeParameters parameters = TNodeParameters.Get(this, node);
		if (parameters != null) {
			parameters.Dispose(this, node);
		}

	}

	private void CleanSpecRoots() {
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this.GetSpecRoots());

		while (enumerator.HasMoreElements()) {
			java.lang.Object node = enumerator.NextElement();
			TNodeParameters parameters = TNodeParameters.Get(this, node);
			if ((parameters != null) && parameters.IsSpecRoot()) {
				parameters.SetSpecRoot(false);
			}
		}
		this._specRoots.Clear();

	}

	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout Copy() {

		return new TreeLayout(this);

	}

	@Override
	public void CopyParameters(ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		super.CopyParameters(source);
		if (source instanceof TreeLayout) {
			TreeLayout layout = (TreeLayout) source;
			this.set_LinkStyle(layout.get_LinkStyle());
			this.set_ConnectorStyle(layout.get_ConnectorStyle());
			this.set_Alignment(layout.get_Alignment());
			this.set_LayoutMode(layout.get_LayoutMode());
			this.set_FlowDirection(layout.get_FlowDirection());
			this.set_LevelAlignment(layout.get_LevelAlignment());
			this.set_SiblingOffset(layout.get_SiblingOffset());
			this.set_ParentChildOffset(layout.get_ParentChildOffset());
			this.set_BranchOffset(layout.get_BranchOffset());
			this.set_TipOverBranchOffset(layout.get_TipOverBranchOffset());
			this.set_AspectRatio(layout.get_AspectRatio());
			this.set_OverlapPercentage(layout.get_OverlapPercentage());
			this.set_OrthForkPercentage(layout.get_OrthForkPercentage());
			this.set_MaxChildrenAngle(layout.get_MaxChildrenAngle());
			this.set_FirstCircleEvenlySpacing(layout
					.get_FirstCircleEvenlySpacing());
			this.set_RespectNodeSizes(layout.get_RespectNodeSizes());
			this.set_IncrementalMode(layout.get_IncrementalMode());
			this.set_AllLevelsAlternating(layout.get_AllLevelsAlternating());
			this.set_NonTreeLinksStraight(layout.get_NonTreeLinksStraight());
			this.set_InvisibleRootUsed(layout.get_InvisibleRootUsed());
			this.set_CategorizingLinks(layout.get_CategorizingLinks());
		}

	}

	@Override
	public void Detach() {
		this.CleanSpecRoots();
		this.CleanCalcRoots();
		this.CleanCalcForwardTreeLinks();
		this.CleanCalcBackwardTreeLinks();
		this.CleanCalcNonTreeLinks();
		super.Detach();

	}

	public int GetAlignment(java.lang.Object node) {

		return (int) TNodeParameters.GetAlignment(this, node);

	}

	public ICollection GetCalcBackwardTreeLinks() {
		if (this._calcBackwardTreeLinks == null) {

			return TranslateUtil.JavaStyleEnum2Collection(LayoutUtil
					.GetVoidEnumeration());
		}

		return TranslateUtil.JavaStyleEnum2Collection(TranslateUtil
				.Collection2JavaStyleEnum(this._calcBackwardTreeLinks));

	}

	public ICollection GetCalcForwardTreeLinks() {
		if (this._calcForwardTreeLinks == null) {

			return TranslateUtil.JavaStyleEnum2Collection(LayoutUtil
					.GetVoidEnumeration());
		}

		return TranslateUtil.JavaStyleEnum2Collection(TranslateUtil
				.Collection2JavaStyleEnum(this._calcForwardTreeLinks));

	}

	public ICollection GetCalcNonTreeLinks() {
		if (this._calcNonTreeLinks == null) {

			return TranslateUtil.JavaStyleEnum2Collection(LayoutUtil
					.GetVoidEnumeration());
		}

		return TranslateUtil.JavaStyleEnum2Collection(TranslateUtil
				.Collection2JavaStyleEnum(this._calcNonTreeLinks));

	}

	public ICollection GetCalcRoots() {

		return TranslateUtil.JavaStyleEnum2Collection(TranslateUtil
				.Collection2JavaStyleEnum(this._calcRoots));

	}

	public java.lang.Object GetEastNeighbor(java.lang.Object node) {
		TNodeParameters parameters = TNodeParameters.Get(this, node);
		if (parameters != null) {

			return parameters.GetEastNeighbor();
		}

		return null;

	}

	public int GetLinkStyle(java.lang.Object link) {

		return (int) TLinkParameters.GetLinkStyle(this, link);

	}

	public Integer GetRootPreference(java.lang.Object node) {

		return TNodeParameters.GetRootPreference(this, node);

	}

	public ICollection GetSpecRoots() {

		return TranslateUtil.JavaStyleEnum2Collection(TranslateUtil
				.Collection2JavaStyleEnum(this._specRoots));

	}

	public java.lang.Object GetWestNeighbor(java.lang.Object node) {
		TNodeParameters parameters = TNodeParameters.Get(this, node);
		if (parameters != null) {

			return parameters.GetWestNeighbor();
		}

		return null;

	}

	@Override
	public void Init() {
		super.Init();
		this.InitPropertiesNames();
		this._globalLinkStyle = TreeLayoutLinkStyle.Straight;
		this._connectorStyle = TreeLayoutConnectorStyle.Automatic;
		this._globalAlignment = TreeLayoutAlignment.Center;
		this._layoutMode = TreeLayoutMode.Free;
		this._flowDirection = LayoutFlowDirection.Right;
		this._levelAlignment = TreeLayoutLevelAlignment.Center;
		this._siblingOffset = 40f;
		this._parentChildOffset = 40f;
		this._branchOffset = 40f;
		this._tipOverBranchOffset = 40f;
		this._aspectRatio = 1f;
		this._overlapPercentage = 30f;
		this._orthForkPercentage = 45f;
		this._maxChildrenAngle = 0;
		this._firstCircleEvenlySpacing = false;
		this._respectNodeSizes = true;
		this._incrementalMode = true;
		this._allLevelsAlternating = false;
		this._nonTreeLinksStraight = false;
		this._isRootPosition = true;
		this._position = null;
		this._isInvisibleRootUsed = false;
		this._allowStopImmediately = true;
		this._specRoots = new ArrayList(3);
		this._calcRoots = new ArrayList(3);
		this._calcForwardTreeLinks = null;
		this._calcBackwardTreeLinks = null;
		this._calcNonTreeLinks = null;

	}

	private void InitPropertiesNames() {
		this.NODE_PROPERTY = "__ilvTreeLayoutNode" + super.GetInstanceId();
		this.LINK_PROPERTY = "__ilvTreeLayoutLink" + super.GetInstanceId();

	}

	public Boolean IsCalcRoot(java.lang.Object node) {
		if (node == null) {

			return false;
		}
		TNodeParameters parameters = TNodeParameters.Get(this, node);
		if (parameters == null) {

			return false;
		}

		return parameters.IsCalcRoot();

	}

	public Boolean IsClippingEnabled() {

		return this.get_LinkClipping();

	}

	public Boolean IsSpecRoot(java.lang.Object node) {
		if (node == null) {

			return false;
		}
		TNodeParameters parameters = TNodeParameters.Get(this, node);
		if (parameters == null) {

			return false;
		}

		return parameters.IsSpecRoot();

	}

	// public override void Layout()
	// {
	// bool redraw = false;
	// this._stoppedBeforeCompletion = false;
	// this._allowStopImmediately = true;
	// IGraphModel graphModel = this.GetGraphModel();
	// GraphLayoutReport layoutReport = base.GetLayoutReport();
	// this.CleanCalcRoots();
	// this.CleanCalcForwardTreeLinks();
	// this.CleanCalcBackwardTreeLinks();
	// this.CleanCalcNonTreeLinks();
	// TGraph graph = new TGraph(this);
	// base.OnLayoutStepPerformedIfNeeded();
	// this.TransferLayoutOptions(graph);
	// this._percCompleteControler = graph.GetPercController();
	// this._percCompleteControler.SetStepEstimation(graph.CalcInitPercParameter());
	// graph.Attach(graphModel);
	// if (this.MayContinue())
	// {
	// TSpanTree tree = new TSpanTree(graph);
	// tree.Run();
	// tree.DisposeIt();
	// }
	// if (graph.GetNumberOfNodes() > 1)
	// {
	// TreeLayoutMode layoutMode = this.LayoutMode;
	// switch (layoutMode)
	// {
	// case TreeLayoutMode.TipOver:
	// case TreeLayoutMode.TipLeavesOver:
	// case TreeLayoutMode.TipRootsOver:
	// case TreeLayoutMode.TipRootsAndLeavesOver:
	// {
	// TAutoTipOver over = new TAutoTipOver(graph);
	// over.Run((int)layoutMode);
	// over.DisposeIt();
	// goto Label_0104;
	// }
	// }
	// if (this.MayContinue())
	// {
	// graph.CalcPrelimCoordInDirTowardsLeaves();
	// }
	// if (this.MayContinue())
	// {
	// graph.CalcCoords();
	// }
	// if (this.MayContinue())
	// {
	// graph.CalcFinalCoordInDirTowardsLeaves();
	// }
	// if (this.MayContinue())
	// {
	// graph.CalcFinalCoordRadial();
	// }
	// }
	// Label_0104:
	// graph.FinalPositionCorrection();
	// this._allowStopImmediately = false;
	// graph.Detach(redraw);
	// graph.Dispose();
	// this.IncreasePercentageComplete(100);
	// this.OnLayoutStepPerformed(false, false);
	// if (this._stoppedBeforeCompletion)
	// {
	// layoutReport.Code = GraphLayoutReportCode.StoppedAndInvalid;
	// }
	// else
	// {
	// layoutReport.Code = GraphLayoutReportCode.LayoutDone;
	// }
	// this._allowStopImmediately = true;
	// }
	@Override
	public void Layout() {
		Boolean redraw = false;

		IGraphModel graphModel = this.GetGraphModel();
		ICollection link = graphModel.get_Links();
		ICollection node = graphModel.get_Nodes();
		TGraph graph = new TGraph(this);
		this.TransferLayoutOptions(graph);
		graph.Attach(graphModel);

		TSpanTree tree = new TSpanTree(graph);
		tree.Run();
		tree.DisposeIt();
		graph.CalcPrelimCoordInDirTowardsLeaves();
		graph.CalcCoords();
		graph.CalcFinalCoordRadial();
		graph.FinalPositionCorrection();
		graph.Detach(redraw);
		graph.Dispose();
	}

	public static void main(String[] args) {
		new TreeLayout().Layout();
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

	private void ConstraintsChanged(TreeLayout treeLayout, EventArgs empty) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnLayoutStepPerformed(Boolean layoutStarted,
			Boolean layoutFinished) {
		if (this._percCompleteControler != null) {
			this._percCompleteControler.NotifyPercentageComplete(this);
		}
		super.OnLayoutStepPerformed(layoutStarted, layoutFinished);

	}

	private void RemoveCalcLinkFromCategory(java.lang.Object link) {
		if (this._calcNonTreeLinks != null) {
			TLinkParameters parameters = TLinkParameters.Get(this, link);
			if (parameters != null) {

				if (parameters.IsCalcForwardTreeLink()) {
					parameters.SetCalcForwardTreeLink(false);
					this._calcForwardTreeLinks.Remove(link);
				}

				if (parameters.IsCalcBackwardTreeLink()) {
					parameters.SetCalcBackwardTreeLink(false);
					this._calcBackwardTreeLinks.Remove(link);
				}

				if (parameters.IsCalcNonTreeLink()) {
					parameters.SetCalcNonTreeLink(false);
					this._calcNonTreeLinks.Remove(link);
				}
			}
		}

	}

	private void RemoveCalcRoot(java.lang.Object node) {
		TNodeParameters parameters = TNodeParameters.Get(this, node);
		if ((parameters != null) && parameters.IsCalcRoot()) {
			parameters.SetCalcRoot(false);
			this._calcRoots.Remove(node);
		}

	}

	private void RemoveSpecRoot(java.lang.Object node) {
		TNodeParameters parameters = TNodeParameters.Get(this, node);
		if ((parameters != null) && parameters.IsSpecRoot()) {
			parameters.SetSpecRoot(false);
			this._specRoots.Remove(node);
		}

	}

	public void SetAlignment(java.lang.Object node, int alignment) {
		LayoutParametersUtil.CheckInputNode(this, node, "input node");
		if ((((alignment != TreeLayoutAlignment.Center) && (alignment != TreeLayoutAlignment.BorderCenter)) && ((alignment != TreeLayoutAlignment.East) && (alignment != TreeLayoutAlignment.West)))
				&& ((alignment != TreeLayoutAlignment.TipOver) && (alignment != TreeLayoutAlignment.TipOverBothSides))) {
			throw (new ArgumentException("unsupported alignment option: "
					+ ((Integer) alignment)));
		}
		TNodeParameters.SetAlignment(this, node, (Integer) alignment);

	}

	public void SetAspectRatio(IDiagramView view) {
		float num = 1f;
		Size2D viewSize = view.get_ViewSize();
		if ((viewSize.get_Height() != 0f) && (viewSize.get_Width() != 0f)) {
			num = viewSize.get_Width() / viewSize.get_Height();
		}
		this.set_AspectRatio((num > 0f) ? num : -num);

	}

	public void SetAspectRatio(Rectangle2D rect) {
		float num = 1f;
		if ((rect.get_Height() != 0.0) && (rect.get_Width() != 0.0)) {
			num = rect.get_Width() / rect.get_Height();
		}
		this.set_AspectRatio((num > 0f) ? num : -num);

	}

	private void SetEastNeighbor(java.lang.Object node,
			java.lang.Object eastNeighbor) {
		TNodeParameters orAllocate = TNodeParameters.GetOrAllocate(this, node);
		orAllocate.SetEastNeighbor(eastNeighbor);

		if (!orAllocate.IsNeeded()) {
			orAllocate.Dispose(this, node);
		}

	}

	public void SetEastWestNeighboring(java.lang.Object eastNode,
			java.lang.Object westNode) {
		if (eastNode != null) {
			LayoutParametersUtil.CheckInputNode(this, eastNode,
					"input east node");
		}
		if (westNode != null) {
			LayoutParametersUtil.CheckInputNode(this, westNode,
					"input west node");
		}
		if (eastNode != westNode) {
			Boolean flag = false;
			if (westNode != null) {
				java.lang.Object eastNeighbor = this.GetEastNeighbor(westNode);
				this.SetEastNeighbor(westNode, eastNode);
				if ((eastNode != eastNeighbor) && (eastNeighbor != null)) {
					this.SetWestNeighbor(eastNeighbor, null);
				}
				if (eastNeighbor != this.GetEastNeighbor(westNode)) {
					flag = true;
				}
			}
			if (eastNode != null) {
				java.lang.Object westNeighbor = this.GetWestNeighbor(eastNode);
				this.SetWestNeighbor(eastNode, westNode);
				if ((westNode != westNeighbor) && (westNeighbor != null)) {
					this.SetEastNeighbor(westNeighbor, null);
				}
				if (westNeighbor != this.GetWestNeighbor(eastNode)) {
					flag = true;
				}
			}
			if (flag) {
				this.OnParameterChanged("EastWestNeighoring");
			}
		}

	}

	public void SetLinkStyle(java.lang.Object link, int style) {
		LayoutParametersUtil.CheckInputLink(this, link, "input link");
		if (((style != TreeLayoutLinkStyle.Orthogonal) && (style != TreeLayoutLinkStyle.Straight))
				&& (style != TreeLayoutLinkStyle.NoReshape)) {
			throw (new ArgumentException("unsupported link style option: "
					+ ((Integer) style)));
		}
		TLinkParameters.SetLinkStyle(this, link, (Integer) style);

	}

	public void SetPosition(Point2D point, Boolean isRootPosition) {
		InternalPoint point2 = this._position;
		Boolean flag = this._isRootPosition;
		this._position = new InternalPoint(point.get_X(), point.get_Y());
		this._isRootPosition = isRootPosition;

		if ((point2 != null) && !point.equals(point2)) {
			this.OnParameterChanged("Position");
		} else if (point2 == null) {
			this.OnParameterChanged("Position");
		} else if (isRootPosition != flag) {
			this.OnParameterChanged("Position");
		}

	}

	public void SetRoot(java.lang.Object node) {
		if (node != null) {
			this.SetRootPreference(node, 0x2710);
			TNodeParameters parameters = TNodeParameters.Get(this, node);

			if (!parameters.IsSpecRoot()) {
				parameters.SetSpecRoot(true);
				this._specRoots.Add(node);
				this.OnParameterChanged("Root");
			}
		}

	}

	public void SetRootPreference(java.lang.Object node, Integer preference) {
		LayoutParametersUtil.CheckInputNode(this, node, "root node");
		if (preference > 0x2710) {
			preference = 0x2710;
		}
		if (preference < 0x2710) {
			this.RemoveSpecRoot(node);
		}
		TNodeParameters.SetRootPreference(this, node, preference);

	}

	public void SetWestEastNeighboring(java.lang.Object westNode,
			java.lang.Object eastNode) {
		this.SetEastWestNeighboring(eastNode, westNode);

	}

	private void SetWestNeighbor(java.lang.Object node,
			java.lang.Object westNeighbor) {
		TNodeParameters orAllocate = TNodeParameters.GetOrAllocate(this, node);
		orAllocate.SetWestNeighbor(westNeighbor);

		if (!orAllocate.IsNeeded()) {
			orAllocate.Dispose(this, node);
		}

	}

	// private Boolean ShouldSerializeConstraints()
	// {
	//
	// return ((this.constraints != null) && (this.constraints.get_Count() >
	// 0));
	//
	// }

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

	private void TransferLayoutOptions(TGraph graph) {
		Integer layoutMode = (Integer) this.get_LayoutMode();
		graph.SetRadialLayout((layoutMode == 2) || (layoutMode == 3));
		graph.SetLevelLayout((layoutMode == 1) || graph.IsRadialLayout());
		graph.SetAllowAlternating(layoutMode == 3);
		if (this.get_FlowDirection() == LayoutFlowDirection.Left) {
			graph.SetFlowDirection(2);
			// NOTICE: break ignore!!!
		} else if (this.get_FlowDirection() == LayoutFlowDirection.Right) {
			graph.SetFlowDirection(0);
			// NOTICE: break ignore!!!
		} else if (this.get_FlowDirection() == LayoutFlowDirection.Top) {
			graph.SetFlowDirection(3);
			// NOTICE: break ignore!!!
		} else if (this.get_FlowDirection() == LayoutFlowDirection.Bottom) {
			graph.SetFlowDirection(1);
			// NOTICE: break ignore!!!
		}
		if (this.get_LevelAlignment() == TreeLayoutLevelAlignment.North) {
			graph.SetLevelJustification(-1);
			// NOTICE: break ignore!!!
		} else if (this.get_LevelAlignment() == TreeLayoutLevelAlignment.South) {
			graph.SetLevelJustification(1);
			// NOTICE: break ignore!!!
		} else if (this.get_LevelAlignment() == TreeLayoutLevelAlignment.Center) {
			graph.SetLevelJustification(0);
			// NOTICE: break ignore!!!
		}
		if (this.get_FlowDirection() == LayoutFlowDirection.Left
				|| this.get_FlowDirection() == LayoutFlowDirection.Right) {
			graph.SetMinDistBetweenNodes(1, this.get_SiblingOffset());
			graph.SetMinDistBetweenBranches(1, this.get_BranchOffset());
			graph.SetMinDistBetweenNodes(0, this.get_ParentChildOffset());
			graph.SetMinDistBetweenBranches(0, this.get_TipOverBranchOffset());
			// NOTICE: break ignore!!!
		} else if (this.get_FlowDirection() == LayoutFlowDirection.Top
				|| this.get_FlowDirection() == LayoutFlowDirection.Bottom) {
			graph.SetMinDistBetweenNodes(0, this.get_SiblingOffset());
			graph.SetMinDistBetweenBranches(0, this.get_BranchOffset());
			graph.SetMinDistBetweenNodes(1, this.get_ParentChildOffset());
			graph.SetMinDistBetweenBranches(1, this.get_TipOverBranchOffset());
			// NOTICE: break ignore!!!
		}
		Integer connectorStyle = (Integer) this.get_ConnectorStyle();
		if (connectorStyle == 0) {

			if (this.IsClippingEnabled()) {
				connectorStyle = 2;
			} else {
				connectorStyle = 1;
			}
		}
		graph.SetConnectorStyle(connectorStyle);
		graph.SetOverlapFactor(this.get_OverlapPercentage() / 100f);
		graph.SetOrthForkFactor(this.get_OrthForkPercentage() / 100f);
		if ((this.get_FlowDirection() == LayoutFlowDirection.Left)
				|| (this.get_FlowDirection() == LayoutFlowDirection.Right)) {
			graph.SetAspectRatio(1.0 / ((double) this.get_AspectRatio()));
		} else {
			graph.SetAspectRatio((double) this.get_AspectRatio());
		}
		this.UpdateAngleTransTable(graph);

	}

	private void UpdateAngleTransTable(TGraph graph) {
		double aspectRatio = graph.GetAspectRatio();
		if (graph.IsRadialLayout()
				&& (aspectRatio != this._angleTransTableAspectRatio)) {

			this._angleTransTable = TRadial.GetEllipsoidTransTable(aspectRatio);
			this._angleTransTableAspectRatio = aspectRatio;
			graph.SetAngleTranslationTable(this._angleTransTable);
		}

	}

	public int get_Alignment() {

		return this._globalAlignment;
	}

	public void set_Alignment(int value) {
		if ((((value != TreeLayoutAlignment.Center) && (value != TreeLayoutAlignment.BorderCenter)) && ((value != TreeLayoutAlignment.East) && (value != TreeLayoutAlignment.West)))
				&& (((value != TreeLayoutAlignment.TipOver) && (value != TreeLayoutAlignment.TipOverBothSides)) && (value != TreeLayoutAlignment.Mixed))) {
			throw (new ArgumentException("unsupported alignment option: "
					+ ((Integer) value)));
		}
		if (value != this._globalAlignment) {
			this._globalAlignment = value;
			this.OnParameterChanged("GlobalAlignment");
		}
	}

	public Boolean get_AllLevelsAlternating() {

		return this._allLevelsAlternating;
	}

	public void set_AllLevelsAlternating(Boolean value) {
		if (value != this._allLevelsAlternating) {
			this._allLevelsAlternating = value;
			this.OnParameterChanged("AllLevelsAlternating");
		}
	}

	public long get_AllowedTime() {

		return super.get_AllowedTime();
	}

	public void set_AllowedTime(long value) {
		super.set_AllowedTime(value);
	}

	public float get_AspectRatio() {

		return this._aspectRatio;
	}

	public void set_AspectRatio(float value) {
		if (value <= 0f) {
			throw (new ArgumentException("negative or zero aspect ratio: "
					+ value));
		}
		if (value != this._aspectRatio) {
			this._aspectRatio = value;
			this.OnParameterChanged("AspectRatio");
		}
	}

	public float get_BranchOffset() {

		return this._branchOffset;
	}

	public void set_BranchOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative branch offset: " + value));
		}
		if (value != this._branchOffset) {
			this._branchOffset = value;
			this.OnParameterChanged("BranchOffset");
		}
	}

	public Boolean get_CategorizingLinks() {

		return (this._calcForwardTreeLinks != null);
	}

	public void set_CategorizingLinks(Boolean value) {
		if (value) {
			if (this._calcForwardTreeLinks == null) {
				this._calcForwardTreeLinks = new ArrayList(3);
				this._calcBackwardTreeLinks = new ArrayList(3);
				this._calcNonTreeLinks = new ArrayList(3);
				this.OnParameterChanged("CategorizingLinks");
			}
		} else if (this._calcForwardTreeLinks != null) {
			this.CleanCalcForwardTreeLinks();
			this.CleanCalcBackwardTreeLinks();
			this.CleanCalcNonTreeLinks();
			this._calcForwardTreeLinks = null;
			this._calcBackwardTreeLinks = null;
			this._calcNonTreeLinks = null;
			this.OnParameterChanged("CategorizingLinks");
		}
	}

	public int get_ConnectorStyle() {

		return this._connectorStyle;
	}

	public void set_ConnectorStyle(int value) {
		if (((value != TreeLayoutConnectorStyle.Automatic) && (value != TreeLayoutConnectorStyle.Centered))
				&& ((value != TreeLayoutConnectorStyle.Clipped) && (value != TreeLayoutConnectorStyle.EvenlySpaced))) {
			throw (new ArgumentException("unsupported style option: "
					+ ((Integer) value)));
		}
		if (value != this._connectorStyle) {
			this._connectorStyle = value;
			this.OnParameterChanged("ConnectorStyle");
		}
	}

	// public GraphLayoutConstraintCollection<TreeConstraint> get_Constraints(){
	// if(this.constraints == null){
	// this.constraints = new
	// GraphLayoutConstraintCollection<TreeConstraint>(this);
	// }
	//
	// return this.constraints;
	// }

	public Boolean get_FirstCircleEvenlySpacing() {

		return this._firstCircleEvenlySpacing;
	}

	public void set_FirstCircleEvenlySpacing(Boolean value) {
		if (value != this._firstCircleEvenlySpacing) {
			this._firstCircleEvenlySpacing = value;
			this.OnParameterChanged("FirstCircleEvenlySpacing");
		}
	}

	public int get_FlowDirection() {

		return this._flowDirection;
	}

	public void set_FlowDirection(int value) {
		if (((value != LayoutFlowDirection.Left) && (value != LayoutFlowDirection.Right))
				&& ((value != LayoutFlowDirection.Top) && (value != LayoutFlowDirection.Bottom))) {
			throw (new ArgumentException("unsupported flow direction option: "
					+ ((int) value)));
		}
		if (value != this._flowDirection) {
			this._flowDirection = value;
			this.OnParameterChanged("FlowDirection");
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

	public Boolean get_InvisibleRootUsed() {

		return this._isInvisibleRootUsed;
	}

	public void set_InvisibleRootUsed(Boolean value) {
		if (value != this._isInvisibleRootUsed) {
			this._isInvisibleRootUsed = value;
			this.OnParameterChanged("InvisibleRootUsed");
		}
	}

	public int get_LayoutMode() {

		return this._layoutMode;
	}

	public void set_LayoutMode(int value) {
		if ((((value != TreeLayoutMode.TipOver) && (value != TreeLayoutMode.TipLeavesOver)) && ((value != TreeLayoutMode.TipRootsOver) && (value != TreeLayoutMode.TipRootsAndLeavesOver)))
				&& (((value != TreeLayoutMode.Free) && (value != TreeLayoutMode.Level)) && ((value != TreeLayoutMode.Radial) && (value != TreeLayoutMode.AlternatingRadial)))) {
			throw (new ArgumentException("unsupported layout mode: "
					+ ((Integer) value)));
		}
		if (value != this._layoutMode) {
			this._layoutMode = value;
			this.OnParameterChanged("LayoutMode");
		}
	}

	public Boolean get_LayoutOfConnectedComponentsEnabled() {

		return super.get_LayoutOfConnectedComponentsEnabled();
	}

	public void set_LayoutOfConnectedComponentsEnabled(Boolean value) {
		super.set_LayoutOfConnectedComponentsEnabled(value);
	}

	public int get_LevelAlignment() {

		return this._levelAlignment;
	}

	public void set_LevelAlignment(int value) {
		if (((value != TreeLayoutLevelAlignment.Center) && (value != TreeLayoutLevelAlignment.North))
				&& (value != TreeLayoutLevelAlignment.South)) {
			throw (new ArgumentException("unsupported level alignment option: "
					+ ((int) value)));
		}
		if (value != this._levelAlignment) {
			this._levelAlignment = value;
			this.OnParameterChanged("LevelAlignment");
		}
	}

	public ILinkConnectionBoxProvider get_LinkConnectionBoxProvider() {

		return super.get_LinkConnectionBoxProvider();
	}

	public void set_LinkConnectionBoxProvider(ILinkConnectionBoxProvider value) {
		super.set_LinkConnectionBoxProvider(value);
	}

	public int get_LinkStyle() {

		return this._globalLinkStyle;
	}

	public void set_LinkStyle(int value) {
		if (((value != TreeLayoutLinkStyle.Orthogonal) && (value != TreeLayoutLinkStyle.Straight))
				&& ((value != TreeLayoutLinkStyle.Mixed) && (value != TreeLayoutLinkStyle.NoReshape))) {
			throw (new ArgumentException("unsupported link style option: "
					+ ((Integer) value)));
		}
		if (value != this._globalLinkStyle) {
			this._globalLinkStyle = value;
			this.OnParameterChanged("GlobalLinkStyle");
		}
	}

	public Integer get_MaxChildrenAngle() {

		return this._maxChildrenAngle;
	}

	public void set_MaxChildrenAngle(Integer value) {
		if (value < 0) {
			value = 0;
		}
		if (value > 360) {
			value = 360;
		}
		if (value != this._maxChildrenAngle) {
			this._maxChildrenAngle = value;
			this.OnParameterChanged("MaxChildrenAngle");
		}
	}

	public Boolean get_NonTreeLinksStraight() {

		return this._nonTreeLinksStraight;
	}

	public void set_NonTreeLinksStraight(Boolean value) {
		if (value != this._nonTreeLinksStraight) {
			this._nonTreeLinksStraight = value;
			this.OnParameterChanged("NonTreeLinksStraight");
		}
	}

	public float get_OrthForkPercentage() {

		return this._orthForkPercentage;
	}

	public void set_OrthForkPercentage(float value) {
		if (value < 0f) {
			value = 0f;
		}
		if (value > 100f) {
			value = 100f;
		}
		if (value != this._orthForkPercentage) {
			this._orthForkPercentage = value;
			this.OnParameterChanged("OrthForkPercentage");
		}
	}

	public float get_OverlapPercentage() {

		return this._overlapPercentage;
	}

	public void set_OverlapPercentage(float value) {
		if (value < 0f) {
			value = 0f;
		}
		if (value > 100f) {
			value = 100f;
		}
		if (value != this._overlapPercentage) {
			this._overlapPercentage = value;
			this.OnParameterChanged("OverlapPercentage");
		}
	}

	public float get_ParentChildOffset() {

		return this._parentChildOffset;
	}

	public void set_ParentChildOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative parent child offset: "
					+ value));
		}
		if (value != this._parentChildOffset) {
			this._parentChildOffset = value;
			this.OnParameterChanged("ParentChildOffset");
		}
	}

	public Point2D get_Position() {

		return TranslateUtil
				.InternalPoint2Point2D((this._position != null) ? new InternalPoint(
						this._position.X, this._position.Y) : null);
	}

	public void set_Position(Point2D value) {
		InternalPoint point = this._position;
		this._position = new InternalPoint(value.get_X(), value.get_Y());

		if ((point != null) && !value.equals(point)) {
			this.OnParameterChanged("Position");
		} else if (point == null) {
			this.OnParameterChanged("Position");
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

	public Boolean get_RespectNodeSizes() {

		return this._respectNodeSizes;
	}

	public void set_RespectNodeSizes(Boolean value) {
		if (value != this._respectNodeSizes) {
			this._respectNodeSizes = value;
			this.OnParameterChanged("RespectNodeSizes");
		}
	}

	public Boolean get_RootPosition() {

		return this._isRootPosition;
	}

	public void set_RootPosition(Boolean value) {
		if (value != this._isRootPosition) {
			this._isRootPosition = value;
			this.OnParameterChanged("RootPosition");
		}
	}

	public float get_SiblingOffset() {

		return this._siblingOffset;
	}

	public void set_SiblingOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative sibling offset: " + value));
		}
		if (value != this._siblingOffset) {
			this._siblingOffset = value;
			this.OnParameterChanged("SiblingOffset");
		}
	}

	public float get_TipOverBranchOffset() {

		return this._tipOverBranchOffset;
	}

	public void set_TipOverBranchOffset(float value) {
		if (value < 0f) {
			throw (new ArgumentException("negative tip-over branch offset: "
					+ value));
		}
		if (value != this._tipOverBranchOffset) {
			this._tipOverBranchOffset = value;
			this.OnParameterChanged("TipOverBranchOffset");
		}
	}

}