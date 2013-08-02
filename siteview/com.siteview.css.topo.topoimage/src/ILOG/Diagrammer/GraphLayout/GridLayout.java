package ILOG.Diagrammer.GraphLayout;

import system.ArgumentException;
import system.Collections.IComparer;
import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;
import ILOG.Diagrammer.GraphLayout.Internal.RowColumnGrid.Graph;
import ILOG.Diagrammer.GraphLayout.Internal.RowColumnGrid.GraphOnMatrix;
import ILOG.Diagrammer.GraphLayout.Internal.RowColumnGrid.GraphOnRowsOrColumns;

public class GridLayout extends ILOG.Diagrammer.GraphLayout.GraphLayout {
	private float _bottomMargin = 0;

	private IComparer _comparator = null;

	private Boolean _criticalParamMatrixUpToDate = false;

	private Boolean _criticalParamRowColumnUpToDate = false;

	public float _defaultBottomMargin = 5f;

	public Integer _defaultHorizontalAlignment = 0;

	public float _defaultHorizontalGridOffset = 40f;

	public Boolean _defaultIncrementalMode = false;

	public Integer _defaultLayoutMode = 2;

	public float _defaultLeftMargin = 5f;

	public Integer _defaultMaxNumberOfNodesPerRowOrColumn = 0x7fffffff;

	public static INodeBoxProvider _defaultNodeBoxInterface = null;

	public float _defaultRightMargin = 5f;

	public float _defaultTopMargin = 5f;

	public Integer _defaultVerticalAlignment = 0;

	public float _defaultVerticalGridOffset = 40f;

	private Integer _globalHorizontalAlignment = 0;

	private Integer _globalVerticalAlignment = 0;

	private Graph _graph = null;

	private float _horizontalGridOffset = 0;

	private Boolean _incrementalMode = false;

	private Integer _layoutMode = 0;

	private float _leftMargin = 0f;

	private Integer _maxNumberOfNodesPerRowOrColumn = 0;

	private INodeBoxProvider _nodeBoxInterface = null;

	private Boolean _preliminaryStopped = false;

	private InternalRect _previousLayoutRegion = new InternalRect();

	private float _rightMargin = 0;

	private float _topMargin = 0;

	private float _verticalGridOffset = 0;

	public static IComparer AscendingArea = Graph.ASCENDING_AREA;

	public static IComparer AscendingHeight = Graph.ASCENDING_HEIGHT;

	public static IComparer AscendingIndex = Graph.ASCENDING_INDEX;

	public static IComparer AscendingWidth = Graph.ASCENDING_WIDTH;

	public static IComparer AutomaticOrdering = Graph.AUTOMATIC_ORDERING;

	public static IComparer DescendingArea = Graph.DESCENDING_AREA;

	public static IComparer DescendingHeight = Graph.DESCENDING_HEIGHT;

	public static IComparer DescendingIndex = Graph.DESCENDING_INDEX;

	public static IComparer DescendingWidth = Graph.DESCENDING_WIDTH;

	public static IComparer _defaultComparator = AutomaticOrdering;

	private static String HORIZONTAL_ALIGNMENT_PROPERTY = "HorizontalAlignment";

	private static String INDEX_PROPERTY = "Index";

	public Integer NoIndex = -1;

	public static IComparer NoOrdering = Graph.NO_ORDERING;

	private static String VERTICAL_ALIGNMENT_PROPERTY = "VerticalAlignment";

	public GridLayout() {
	}

	public GridLayout(GridLayout source) {
		super(source);
	}

	public void CallLayoutStepPerformedInternal() {
		if (!this._preliminaryStopped) {
			super.OnLayoutStepPerformedIfNeeded();
			this._preliminaryStopped = this.IsLayoutTimeElapsed()
					|| this.IsStoppedImmediately();
		}

	}

	private void CleanInternalData() {
		if (this._graph != null) {
			this._graph.Detach();
			this._graph.Dispose();
			this._graph = null;
		}

	}

	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout Copy() {

		return new GridLayout(this);

	}

	@Override
	public void CopyParameters(ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		super.CopyParameters(source);
		if (source instanceof GridLayout) {
			GridLayout layout = (GridLayout) source;
			this.set_NodeComparator(layout.get_NodeComparator());
			this.set_HorizontalAlignment(layout.get_HorizontalAlignment());
			this.set_VerticalAlignment(layout.get_VerticalAlignment());
			this.set_LayoutMode(layout.get_LayoutMode());
			this.set_IncrementalMode(layout.get_IncrementalMode());
			this.set_HorizontalGridOffset(layout.get_HorizontalGridOffset());
			this.set_VerticalGridOffset(layout.get_VerticalGridOffset());
			this.set_TopMargin(layout.get_TopMargin());
			this.set_BottomMargin(layout.get_BottomMargin());
			this.set_LeftMargin(layout.get_LeftMargin());
			this.set_RightMargin(layout.get_RightMargin());
			this.set_MaxNumberOfNodesPerRowOrColumn(layout
					.get_MaxNumberOfNodesPerRowOrColumn());
			this.set_NodeBoxProvider(layout.get_NodeBoxProvider());
		}

	}

	private Graph CreateGraph() {
		if (this.get_LayoutMode() == GridLayoutMode.TileToRows
				|| this.get_LayoutMode() == GridLayoutMode.TileToColumns) {

			return new GraphOnRowsOrColumnsImp(this, this);
		} else if (this.get_LayoutMode() == GridLayoutMode.TileToGridFixedWidth
				|| this.get_LayoutMode() == GridLayoutMode.TileToGridFixedHeight) {

			return new GraphOnMatrixImpl(this);
		}
		throw (new system.Exception("unsuported layout mode: "
				+ ((Integer) this.get_LayoutMode())));

	}

	@Override
	public void Detach() {
		this.CleanInternalData();
		super.Detach();

	}

	public int GetHorizontalAlignment(java.lang.Object node) {

		return (int) LayoutParametersUtil.GetNodeParameter(this, node,
				HORIZONTAL_ALIGNMENT_PROPERTY, 0);

	}

	public int GetIndex(java.lang.Object node) {

		return (int) LayoutParametersUtil.GetNodeParameter(this, node,
				INDEX_PROPERTY, -1);

	}

	public int GetVerticalAlignment(java.lang.Object node) {

		return (int) LayoutParametersUtil.GetNodeParameter(this, node,
				VERTICAL_ALIGNMENT_PROPERTY, 0);

	}

	@Override
	public void Init() {
		super.Init();
		this._comparator = _defaultComparator;
		this._globalHorizontalAlignment = 0;
		this._globalVerticalAlignment = 0;
		this._layoutMode = 2;
		this._incrementalMode = false;
		this._horizontalGridOffset = 40f;
		this._verticalGridOffset = 40f;
		this._topMargin = 5f;
		this._bottomMargin = 5f;
		this._leftMargin = 5f;
		this._rightMargin = 5f;
		this._maxNumberOfNodesPerRowOrColumn = 0x7fffffff;
		this._nodeBoxInterface = _defaultNodeBoxInterface;
		this._previousLayoutRegion = new InternalRect(0f, 0f, 0f, 0f);

	}

	private Boolean IsCriticalParametersMatrixUpToDate() {

		return this._criticalParamMatrixUpToDate;

	}

	private Boolean IsCriticalParametersRowColumnUpToDate() {

		return this._criticalParamRowColumnUpToDate;

	}

	private Boolean IsCriticalParametersUpToDate(InternalRect layoutRegion) {
		if (this.get_LayoutMode() == GridLayoutMode.TileToRows
				|| this.get_LayoutMode() == GridLayoutMode.TileToColumns) {

			if (!this.IsCriticalParametersRowColumnUpToDate()) {

				return false;
			}

			return this.IsSameLayoutRegion(layoutRegion);
		} else if (this.get_LayoutMode() == GridLayoutMode.TileToGridFixedWidth
				|| this.get_LayoutMode() == GridLayoutMode.TileToGridFixedHeight) {

			if (!this.IsCriticalParametersMatrixUpToDate()) {

				return false;
			}

			return this.IsSameLayoutRegion(layoutRegion);
		}

		return false;

	}

	private Boolean IsSameLayoutRegion(InternalRect layoutRegion) {

		return ((this._previousLayoutRegion != null) && layoutRegion
				.equals(this._previousLayoutRegion));

	}

	@Override
	public void Layout() {
		Boolean redraw = false;
		IGraphModel graphModel = this.GetGraphModel();
		GraphLayoutReport layoutReport = super.GetLayoutReport();
		InternalRect layoutRegion = TranslateUtil.Rectangle2D2InternalRect(this
				.GetCalcLayoutRegion());
		this._preliminaryStopped = false;

		if (((this._graph == null) || (this._graph.GetGraphModel() != graphModel))
				|| (!this.get_IncrementalMode() || !this
						.IsCriticalParametersUpToDate(layoutRegion))) {

			this._graph = this.CreateGraph();
			this._graph.Attach(graphModel);
		}
		this._previousLayoutRegion.Reshape(0f, 0f, 0f, 0f);
		super.OnLayoutStepPerformedIfNeeded();
		this.TransferLayoutOptions(this._graph, layoutRegion);
		this._graph.Layout(redraw);
		this.OnLayoutStepPerformed(false, false);
		this._previousLayoutRegion.Reshape(layoutRegion.X, layoutRegion.Y,
				layoutRegion.Width, layoutRegion.Height);
		if (this._preliminaryStopped) {
			layoutReport.set_Code(GraphLayoutReportCode.StoppedAndInvalid);
		} else {
			layoutReport.set_Code(GraphLayoutReportCode.LayoutDone);
			if (this.get_LayoutMode() == GridLayoutMode.TileToRows
					|| this.get_LayoutMode() == GridLayoutMode.TileToColumns) {
				this.SetCriticalParametersRowColumnUpToDate(true);

				return;
			} else if (this.get_LayoutMode() == GridLayoutMode.TileToGridFixedWidth
					|| this.get_LayoutMode() == GridLayoutMode.TileToGridFixedHeight) {
				this.SetCriticalParametersMatrixUpToDate(null, true);

				return;
			}
			throw (new system.Exception("unsuported layout mode: "
					+ ((Integer) this.get_LayoutMode())));
		}

	}

	public Boolean MayContinue() {

		return !this._preliminaryStopped;

	}

	private void ResetNodeComparator() {
		this.set_NodeComparator(AutomaticOrdering);

	}

	private void SetCriticalParametersMatrixUpToDate(String parameterName,
			Boolean option) {
		this._criticalParamMatrixUpToDate = option;
		if (!option && (parameterName != null)) {
			this.OnParameterChanged(parameterName);
		} else {
			this.set_ParametersUpToDate(option);
		}

	}

	private void SetCriticalParametersRowColumnUpToDate(Boolean option) {
		this._criticalParamRowColumnUpToDate = option;
		this.set_ParametersUpToDate(option);

	}

	private void SetCriticalParametersUpToDate(String parameterName,
			Boolean uptodate) {
		if (!uptodate) {
			this.CleanInternalData();
		}
		this.SetCriticalParametersMatrixUpToDate(parameterName, uptodate);
		this.SetCriticalParametersRowColumnUpToDate(uptodate);

	}

	public void SetHorizontalAlignment(java.lang.Object node, int alignment) {
		if (((alignment != GridLayoutHorizontalAlignment.Center) && (alignment != GridLayoutHorizontalAlignment.Left))
				&& (alignment != GridLayoutHorizontalAlignment.Right)) {
			throw (new ArgumentException(
					"unsupported horizontal alignment option: "
							+ ((Integer) alignment)));
		}
		LayoutParametersUtil.SetNodeParameter(this, node,
				HORIZONTAL_ALIGNMENT_PROPERTY, (int) alignment, 0);

	}

	public void SetIndex(java.lang.Object node, int index) {
		if ((index < 0) && (index != -1)) {
			throw (new ArgumentException("invalid index: " + index));
		}
		LayoutParametersUtil.SetNodeParameter(this, node, INDEX_PROPERTY,
				index, -1);

	}

	public void SetVerticalAlignment(java.lang.Object node, int alignment) {
		if (((alignment != GridLayoutVerticalAlignment.Center) && (alignment != GridLayoutVerticalAlignment.Top))
				&& (alignment != GridLayoutVerticalAlignment.Bottom)) {
			throw (new ArgumentException(
					"unsupported vertical alignment option: "
							+ ((Integer) alignment)));
		}
		LayoutParametersUtil.SetNodeParameter(this, node,
				VERTICAL_ALIGNMENT_PROPERTY, (int) alignment, 0);

	}

	private Boolean ShouldSerializeNodeComparator() {

		return (this.get_NodeComparator() != AutomaticOrdering);

	}

	@Override
	public Boolean SupportsAllowedTime() {

		return true;

	}

	@Override
	public Boolean SupportsLayoutRegion() {

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

	private void TransferLayoutOptions(Graph graph, InternalRect layoutRegion) {
		graph.TransferLayoutOptions(layoutRegion);

	}

	public long get_AllowedTime() {

		return super.get_AllowedTime();
	}

	public void set_AllowedTime(long value) {
		super.set_AllowedTime(value);
	}

	public float get_BottomMargin() {

		return this._bottomMargin;
	}

	public void set_BottomMargin(float value) {
		if (value != this._bottomMargin) {
			this._bottomMargin = value;
			this.SetCriticalParametersUpToDate("BottomMargin", false);
		}
	}

	public int get_HorizontalAlignment() {

		return (int) this._globalHorizontalAlignment;
	}

	public void set_HorizontalAlignment(int value) {
		if (((value != GridLayoutHorizontalAlignment.Center) && (value != GridLayoutHorizontalAlignment.Left))
				&& ((value != GridLayoutHorizontalAlignment.Right) && (value != GridLayoutHorizontalAlignment.Mixed))) {
			throw (new ArgumentException(
					"unsupported global horizontal alignment option: "
							+ ((Integer) value)));
		}
		if ((Integer) value != this._globalHorizontalAlignment) {
			this._globalHorizontalAlignment = (int) value;
			this.OnParameterChanged("GlobalHorizontalAlignment");
		}
	}

	public float get_HorizontalGridOffset() {

		return this._horizontalGridOffset;
	}

	public void set_HorizontalGridOffset(float value) {
		if (value < 0.01f) {
			throw (new ArgumentException("too small horizontal grid offset: "
					+ value));
		}
		if (value != this._horizontalGridOffset) {
			this._horizontalGridOffset = value;
			this.SetCriticalParametersMatrixUpToDate("HorizontalGridOffset",
					false);
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

	public int get_LayoutMode() {

		return (int) this._layoutMode;
	}

	public void set_LayoutMode(int value) {
		if (((value != GridLayoutMode.TileToColumns) && (value != GridLayoutMode.TileToRows))
				&& ((value != GridLayoutMode.TileToGridFixedWidth) && (value != GridLayoutMode.TileToGridFixedHeight))) {
			throw (new ArgumentException("unsupported layout mode: "
					+ ((Integer) value)));
		}
		if ((Integer) value != this._layoutMode) {
			this._layoutMode = (Integer) value;
			this.SetCriticalParametersUpToDate("LayoutMode", false);
		}
	}

	public Rectangle2D get_LayoutRegion() {

		return super.get_LayoutRegion();
	}

	public void set_LayoutRegion(Rectangle2D value) {
		super.set_LayoutRegion(value);
	}

	public GraphLayoutRegionMode get_LayoutRegionMode() {

		return super.get_LayoutRegionMode();
	}

	public void set_LayoutRegionMode(GraphLayoutRegionMode value) {
		super.set_LayoutRegionMode(value);
	}

	public float get_LeftMargin() {

		return this._leftMargin;
	}

	public void set_LeftMargin(float value) {
		if (value != this._leftMargin) {
			this._leftMargin = value;
			this.SetCriticalParametersUpToDate("LeftMargin", false);
		}
	}

	public Integer get_MaxNumberOfNodesPerRowOrColumn() {

		return this._maxNumberOfNodesPerRowOrColumn;
	}

	public void set_MaxNumberOfNodesPerRowOrColumn(Integer value) {
		if (value < 1) {
			throw (new ArgumentException(
					"maximum number of nodes must be at least 1"));
		}
		if (value != this._maxNumberOfNodesPerRowOrColumn) {
			this._maxNumberOfNodesPerRowOrColumn = value;
			this.SetCriticalParametersUpToDate(
					"MaxNumberOfNodesPerRowOrColumn", false);
		}
	}

	public INodeBoxProvider get_NodeBoxProvider() {

		return this._nodeBoxInterface;
	}

	public void set_NodeBoxProvider(INodeBoxProvider value) {
		if (this._nodeBoxInterface != value) {
			this._nodeBoxInterface = value;
			this.OnParameterChanged("NodeBoxInterface");
		}
	}

	public IComparer get_NodeComparator() {

		return this._comparator;
	}

	public void set_NodeComparator(IComparer value) {
		if (value != this._comparator) {
			this._comparator = value;
			this.SetCriticalParametersUpToDate("NodeComparator", false);
		}
	}

	public Boolean get_PreserveFixedNodes() {

		return super.get_PreserveFixedNodes();
	}

	public void set_PreserveFixedNodes(Boolean value) {
		super.set_PreserveFixedNodes(value);
	}

	public float get_RightMargin() {

		return this._rightMargin;
	}

	public void set_RightMargin(float value) {
		if (value != this._rightMargin) {
			this._rightMargin = value;
			this.SetCriticalParametersUpToDate("RightMargin", false);
		}
	}

	public float get_TopMargin() {

		return this._topMargin;
	}

	public void set_TopMargin(float value) {
		if (value != this._topMargin) {
			this._topMargin = value;
			this.SetCriticalParametersUpToDate("TopMargin", false);
		}
	}

	public int get_VerticalAlignment() {

		return (int) this._globalVerticalAlignment;
	}

	public void set_VerticalAlignment(int value) {
		if (((value != GridLayoutVerticalAlignment.Center) && (value != GridLayoutVerticalAlignment.Top))
				&& ((value != GridLayoutVerticalAlignment.Bottom) && (value != GridLayoutVerticalAlignment.Mixed))) {
			throw (new ArgumentException(
					"unsupported global vertical alignment option: "
							+ ((Integer) value)));
		}
		if ((Integer) value != this._globalVerticalAlignment) {
			this._globalVerticalAlignment = (int) value;
			this.OnParameterChanged("GlobalVerticalAlignment");
		}
	}

	public float get_VerticalGridOffset() {

		return this._verticalGridOffset;
	}

	public void set_VerticalGridOffset(float value) {
		if (value < 0.01f) {
			throw (new ArgumentException("too small vertical grid offset: "
					+ value));
		}
		if (value != this._verticalGridOffset) {
			this._verticalGridOffset = value;
			this.SetCriticalParametersMatrixUpToDate("VerticalGridOffset",
					false);
		}
	}

	private class GraphOnMatrixImpl extends GraphOnMatrix {
		public GridLayout _gridLayout;

		public GraphOnMatrixImpl(GridLayout layout) {
			super(layout);
			this._gridLayout = layout;
		}

		@Override
		public Integer GetHorizontalIndividualAlignment(java.lang.Object node) {

			return (Integer) this._gridLayout.GetHorizontalAlignment(node);

		}

		@Override
		public Integer GetNodeIndex(java.lang.Object node) {

			return this._gridLayout.GetIndex(node);

		}

		@Override
		public Integer GetVerticalIndividualAlignment(java.lang.Object node) {

			return (Integer) this._gridLayout.GetVerticalAlignment(node);

		}

		@Override
		public void LayoutStepPerformed() {
			this._gridLayout.CallLayoutStepPerformedInternal();

		}

		@Override
		public Boolean MayContinue() {

			return this._gridLayout.MayContinue();

		}

		@Override
		public void TransferLayoutOptions(InternalRect layoutRegion) {
			super.TransferLayoutOptions(layoutRegion);
			super.SetIncrementalMode(this._gridLayout.get_IncrementalMode());
			super.SetGlobalHorizontalAlignment((Integer) this._gridLayout
					.get_HorizontalAlignment());
			super.SetGlobalVerticalAlignment((Integer) this._gridLayout
					.get_VerticalAlignment());
			super.SetNodeBoxInterface(this._gridLayout.get_NodeBoxProvider());
			super.SetHorizontalGridOffset(this._gridLayout
					.get_HorizontalGridOffset());
			super.SetVerticalGridOffset(this._gridLayout
					.get_VerticalGridOffset());
			super.SetTopMargin(this._gridLayout.get_TopMargin());
			super.SetBottomMargin(this._gridLayout.get_BottomMargin());
			super.SetRightMargin(this._gridLayout.get_RightMargin());
			super.SetLeftMargin(this._gridLayout.get_LeftMargin());
			this.SetLayoutMode((Integer) this._gridLayout.get_LayoutMode());
			super.SetNodeComparator(this._gridLayout.get_NodeComparator());
			if (this._gridLayout.get_LayoutMode() == GridLayoutMode.TileToGridFixedWidth) {
				super.SetInvertedCoords(false);
				super.SetMaxDimension(layoutRegion.Width);

				return;
			} else if (this._gridLayout.get_LayoutMode() == GridLayoutMode.TileToGridFixedHeight) {
				super.SetInvertedCoords(true);
				super.SetMaxDimension(layoutRegion.Height);

				return;
			}
			throw (new system.Exception("unsupported layout mode: "
					+ ((Integer) this._gridLayout.get_LayoutMode())));

		}

	}

	private class GraphOnRowsOrColumnsImp extends GraphOnRowsOrColumns {
		public GridLayout __outerThis;

		public GridLayout _gridLayout;

		public GraphOnRowsOrColumnsImp(GridLayout input__outerThis,
				GridLayout layout) {
			super(layout);
			this.__outerThis = input__outerThis;
			this._gridLayout = layout;
		}

		@Override
		public Integer GetHorizontalIndividualAlignment(java.lang.Object node) {

			return (Integer) this._gridLayout.GetHorizontalAlignment(node);

		}

		@Override
		public Integer GetNodeIndex(java.lang.Object node) {

			return this.__outerThis.GetIndex(node);

		}

		@Override
		public Integer GetVerticalIndividualAlignment(java.lang.Object node) {

			return (Integer) this._gridLayout.GetVerticalAlignment(node);

		}

		public Boolean IsPreserveNodesOrder() {

			return (this._gridLayout.get_NodeComparator() != GridLayout.NoOrdering);

		}

		@Override
		public void LayoutStepPerformed() {
			this._gridLayout.CallLayoutStepPerformedInternal();

		}

		@Override
		public Boolean MayContinue() {

			return this._gridLayout.MayContinue();

		}

		@Override
		public void TransferLayoutOptions(InternalRect layoutRegion) {
			super.TransferLayoutOptions(layoutRegion);
			super.SetIncrementalMode(this._gridLayout.get_IncrementalMode());
			super.SetGlobalHorizontalAlignment((Integer) this._gridLayout
					.get_HorizontalAlignment());
			super.SetGlobalVerticalAlignment((Integer) this._gridLayout
					.get_VerticalAlignment());
			super.SetNodeBoxInterface(this._gridLayout.get_NodeBoxProvider());
			super.SetHGap(this._gridLayout.get_LeftMargin()
					+ this._gridLayout.get_RightMargin());
			super.SetVGap(this._gridLayout.get_TopMargin()
					+ this._gridLayout.get_BottomMargin());
			this.SetLayoutMode((Integer) this._gridLayout.get_LayoutMode());
			super.SetNodeComparator(this._gridLayout.get_NodeComparator());
			if (this._gridLayout.get_LayoutMode() == GridLayoutMode.TileToRows) {
				super.SetInvertedCoords(false);
				super.SetMaxDimension(layoutRegion.Width);
				// NOTICE: break ignore!!!
			} else if (this._gridLayout.get_LayoutMode() == GridLayoutMode.TileToColumns) {
				super.SetInvertedCoords(true);
				super.SetMaxDimension(layoutRegion.Height);
				// NOTICE: break ignore!!!
			} else {
				throw (new system.Exception("unsupported layout mode: "
						+ ((Integer) this._gridLayout.get_LayoutMode())));
			}
			super.SetMaxNumberOfNodesPerRowOrColumn(this._gridLayout
					.get_MaxNumberOfNodesPerRowOrColumn());

		}

	}
}