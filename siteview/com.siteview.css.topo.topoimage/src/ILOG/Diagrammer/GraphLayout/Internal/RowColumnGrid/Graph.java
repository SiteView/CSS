package ILOG.Diagrammer.GraphLayout.Internal.RowColumnGrid;

import system.ArgumentException;
import system.Math;
import system.Collections.ArrayList;
import system.Collections.Hashtable;
import system.Collections.IComparer;
import ILOG.Diagrammer.GraphLayout.IGraphModel;
import ILOG.Diagrammer.GraphLayout.INodeBoxProvider;
import ILOG.Diagrammer.GraphLayout.Internal.ArrayStableSort;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelUtil;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;

public abstract class Graph {
	private Hashtable _bboxCache;

	private Integer _globalHorizAlignment;

	private Integer _globalVertAlignment;

	private ILOG.Diagrammer.GraphLayout.GraphLayout _graphLayout;

	private IGraphModel _graphModel;

	private Boolean _incrementalMode = false;

	private Boolean _invertedCoords = false;

	private Integer _layoutMode = 0;

	private float _maxDimension = 0f;

	private Integer _maxNumberOfNodesPerRowOrColumn = 0;

	private INodeBoxProvider _nodeBoxInterface;

	private IComparer _originalComparator;

	private InternalPoint _position;

	private ArrayStableSort _sortByAscendingArea;

	private ArrayStableSort _sortByAscendingHeight;

	private ArrayStableSort _sortByAscendingIndex;

	private ArrayStableSort _sortByAscendingWidth;

	private ArrayStableSort _sortByDescendingArea;

	private ArrayStableSort _sortByDescendingHeight;

	private ArrayStableSort _sortByDescendingIndex;

	private ArrayStableSort _sortByDescendingWidth;

	public static PredefinedNodeComparator ASCENDING_AREA = CreateDummyComparator();

	public static PredefinedNodeComparator ASCENDING_HEIGHT = CreateDummyComparator();

	public static PredefinedNodeComparator ASCENDING_INDEX = CreateDummyComparator();

	public static PredefinedNodeComparator ASCENDING_WIDTH = CreateDummyComparator();

	public static String ATTACH_PROPERTY = "ilvGraphOnMatrixAttach";

	public static PredefinedNodeComparator AUTOMATIC_ORDERING = CreateDummyComparator();

	public static PredefinedNodeComparator DESCENDING_AREA = CreateDummyComparator();

	public static PredefinedNodeComparator DESCENDING_HEIGHT = CreateDummyComparator();

	public static PredefinedNodeComparator DESCENDING_INDEX = CreateDummyComparator();

	public static PredefinedNodeComparator DESCENDING_WIDTH = CreateDummyComparator();

	public static IComparer NO_ORDERING = null;

	private static PredefinedNodeComparator[] ALL_PREDEFINED_COMPARATORS = new PredefinedNodeComparator[] {
			AUTOMATIC_ORDERING, DESCENDING_HEIGHT, ASCENDING_HEIGHT,
			ASCENDING_WIDTH, DESCENDING_WIDTH, ASCENDING_AREA, DESCENDING_AREA,
			ASCENDING_INDEX, DESCENDING_INDEX, DESCENDING_INDEX };

	static {
		InitPredefinedNodeComparators(ALL_PREDEFINED_COMPARATORS);
	}

	public Graph(ILOG.Diagrammer.GraphLayout.GraphLayout graphLayout) {
		this._graphLayout = graphLayout;
		this._sortByAscendingIndex = new SortNodesByIndex(this, true);
		this._sortByDescendingIndex = new SortNodesByIndex(this, false);
		this._sortByDescendingWidth = new SortNodesByWidth(this, false);
		this._sortByAscendingWidth = new SortNodesByWidth(this, true);
		this._sortByDescendingHeight = new SortNodesByHeight(this, false);
		this._sortByAscendingHeight = new SortNodesByHeight(this, true);
		this._sortByDescendingArea = new SortNodesByArea(this, false);
		this._sortByAscendingArea = new SortNodesByArea(this, true);
	}

	public void Attach(IGraphModel graphModel) {
		this.Detach();
		this._graphModel = graphModel;

	}

	private void CleanBBoxCache() {
		this._bboxCache = null;

	}

	private InternalRect ComputeNodeBox(java.lang.Object node) {
		if (this._nodeBoxInterface != null) {

			return TranslateUtil.GetBox(this._nodeBoxInterface,
					this._graphModel, node);
		}

		return GraphModelUtil.BoundingBox(this._graphModel, node);

	}

	private static PredefinedNodeComparator CreateDummyComparator() {

		return new PredefinedNodeComparator();

	}

	public void Detach() {
		this._graphModel = null;

	}

	public void Dispose() {
		this.Detach();
		this._graphLayout = null;
		this._position = null;
		this._nodeBoxInterface = null;

	}

	public abstract void DoLayout(Boolean redraw);

	public Integer GetGlobalHorizontalAlignment() {

		return this._globalHorizAlignment;

	}

	public Integer GetGlobalVerticalAlignment() {

		return this._globalVertAlignment;

	}

	public IGraphModel GetGraphModel() {

		return this._graphModel;

	}

	public java.lang.Object GetGraphNode(java.lang.Object node) {

		return node;

	}

	public float GetHeight(InternalRect rect) {
		if (!this._invertedCoords) {

			return rect.Height;
		}

		return rect.Width;

	}

	public Integer GetHorizontalAlignment(java.lang.Object node) {
		Integer globalHorizontalAlignment = this.GetGlobalHorizontalAlignment();
		if (globalHorizontalAlignment == 0x63) {

			return this.GetHorizontalIndividualAlignment(node);
		}

		return globalHorizontalAlignment;

	}

	public abstract Integer GetHorizontalIndividualAlignment(
			java.lang.Object node);

	public ILOG.Diagrammer.GraphLayout.GraphLayout GetLayout() {

		return this._graphLayout;

	}

	public Integer GetLayoutMode() {

		return this._layoutMode;

	}

	public float GetMaxDimension() {

		return this._maxDimension;

	}

	public Integer GetMaxNumberOfNodesPerRowOrColumn() {

		return this._maxNumberOfNodesPerRowOrColumn;

	}

	public InternalRect GetNodeBox(java.lang.Object node) {

		return (InternalRect) this._bboxCache.get_Item(node);

	}

	public INodeBoxProvider GetNodeBoxInterface() {

		return this._nodeBoxInterface;

	}

	public IComparer GetNodeComparator() {

		return this._originalComparator;

	}

	public float GetNodeHeight(java.lang.Object node) {
		if (!this._invertedCoords) {

			return this.GetNodeBox(node).Height;
		}

		return this.GetNodeBox(node).Width;

	}

	public abstract Integer GetNodeIndex(java.lang.Object node);

	private ArrayStableSort GetNodeSorter() {
		IComparer nodeComparator = this.GetNodeComparator();
		if (nodeComparator == NO_ORDERING) {

			return null;
		}
		if (nodeComparator == AUTOMATIC_ORDERING) {

			return (this.IsInvertedCoords() ? this._sortByDescendingWidth
					: this._sortByDescendingHeight);
		}
		if (nodeComparator == DESCENDING_HEIGHT) {

			return this._sortByDescendingHeight;
		}
		if (nodeComparator == ASCENDING_HEIGHT) {

			return this._sortByAscendingHeight;
		}
		if (nodeComparator == ASCENDING_WIDTH) {

			return this._sortByAscendingWidth;
		}
		if (nodeComparator == DESCENDING_WIDTH) {

			return this._sortByDescendingWidth;
		}
		if (nodeComparator == ASCENDING_AREA) {

			return this._sortByAscendingArea;
		}
		if (nodeComparator == DESCENDING_AREA) {

			return this._sortByDescendingArea;
		}
		if (nodeComparator == ASCENDING_INDEX) {

			return this._sortByAscendingIndex;
		}
		if (nodeComparator == DESCENDING_INDEX) {

			return this._sortByDescendingIndex;
		}

		return ((nodeComparator != null) ? new SortNodesByComparator(this,
				nodeComparator) : null);

	}

	public float GetNodeWidth(java.lang.Object node) {
		if (!this._invertedCoords) {

			return this.GetNodeBox(node).Width;
		}

		return this.GetNodeBox(node).Height;

	}

	public InternalPoint GetPosition() {

		return this._position;

	}

	public float GetPositionX() {
		if (!this._invertedCoords) {

			return this._position.X;
		}

		return this._position.Y;

	}

	public float GetPositionY() {
		if (!this._invertedCoords) {

			return this._position.Y;
		}

		return this._position.X;

	}

	public Integer GetVerticalAlignment(java.lang.Object node) {
		Integer globalVerticalAlignment = this.GetGlobalVerticalAlignment();
		if (globalVerticalAlignment == 0x63) {

			return this.GetVerticalIndividualAlignment(node);
		}

		return globalVerticalAlignment;

	}

	public abstract Integer GetVerticalIndividualAlignment(java.lang.Object node);

	public float GetWidth(InternalRect rect) {
		if (!this._invertedCoords) {

			return rect.Width;
		}

		return rect.Height;

	}

	public float GetX(InternalRect rect) {
		if (!this._invertedCoords) {

			return rect.X;
		}

		return rect.Y;

	}

	public float GetY(InternalRect rect) {
		if (!this._invertedCoords) {

			return rect.Y;
		}

		return rect.X;

	}

	private static void InitPredefinedNodeComparators(
			PredefinedNodeComparator[] comparators) {
		for (Integer i = 0; i < comparators.length; i++) {
			PredefinedNodeComparator.StoreType(comparators[i]);
		}

	}

	public Boolean IsFixed(java.lang.Object node) {

		return this.GetLayout().GetFixed(node);

	}

	public Boolean IsIncrementalMode() {

		return this._incrementalMode;

	}

	public Boolean IsInvertedCoords() {

		return this._invertedCoords;

	}

	public abstract Boolean IsSupportedLayoutMode(Integer mode);

	public void Layout(Boolean redraw) {
		try {
			this.UpdateBBoxCache();
			this.DoLayout(redraw);
		} finally {
			this.CleanBBoxCache();
		}

	}

	public abstract void LayoutStepPerformed();

	public abstract Boolean MayContinue();

	public void MoveNode(java.lang.Object node, float x, float y, Boolean redraw) {
		IGraphModel graphModel = this.GetGraphModel();
		InternalRect rect = GraphModelUtil.BoundingBox(graphModel, node);
		if (this._nodeBoxInterface != null) {
			InternalRect rect2 = TranslateUtil.GetBox(this._nodeBoxInterface,
					graphModel, node);
			x += rect.X - rect2.X;
			y += rect.Y - rect2.Y;
		}
		if ((Math.Abs((float) (rect.X - x)) > 1E-05)
				|| (Math.Abs((float) (rect.Y - y)) > 1E-05)) {
			graphModel.MoveNode(node, x, y);
		}
		this.LayoutStepPerformed();

	}

	public void SetGlobalHorizontalAlignment(Integer alignment) {
		this._globalHorizAlignment = alignment;

	}

	public void SetGlobalVerticalAlignment(Integer alignment) {
		this._globalVertAlignment = alignment;

	}

	public void SetIncrementalMode(Boolean enable) {
		this._incrementalMode = enable;

	}

	public void SetInvertedCoords(Boolean inverted) {
		this._invertedCoords = inverted;

	}

	public void SetLayoutMode(Integer mode) {

		if (!this.IsSupportedLayoutMode(mode)) {
			throw (new ArgumentException("unsupported layout mode: " + mode));
		}
		this._layoutMode = mode;

	}

	public void SetMaxDimension(float dim) {
		this._maxDimension = dim;

	}

	public void SetMaxNumberOfNodesPerRowOrColumn(Integer nNodes) {
		this._maxNumberOfNodesPerRowOrColumn = nNodes;

	}

	public void SetNodeBoxInterface(INodeBoxProvider nodeBoxInterface) {
		this._nodeBoxInterface = nodeBoxInterface;

	}

	public void SetNodeComparator(IComparer comparator) {
		this._originalComparator = comparator;

	}

	public void SetPosition(InternalPoint point) {
		this._position = point;

	}

	public void SortNodes(ArrayList vectNodes) {
		ArrayStableSort nodeSorter = this.GetNodeSorter();
		if (nodeSorter != null) {
			SortNodesVector(nodeSorter, vectNodes);
		}

	}

	public static void SortNodesVector(ArrayStableSort sorter,
			ArrayList vectNodes) {
		Integer count = vectNodes.get_Count();
		java.lang.Object[] array = new java.lang.Object[count];
		vectNodes.CopyTo(array);
		sorter.Sort(array);
		vectNodes.Clear();
		for (Integer i = 0; i < count; i++) {
			vectNodes.Add(array[i]);
		}

	}

	public void TransferLayoutOptions(InternalRect layoutRegion) {
		this.SetPosition(new InternalPoint(layoutRegion.X, layoutRegion.Y));

	}

	private void UpdateBBoxCache() {
		this._bboxCache = new Hashtable(500);
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this.GetGraphModel().get_Nodes());
		java.lang.Object node = null;
		InternalRect rect = null;

		while (enumerator.HasMoreElements()) {

			node = enumerator.NextElement();

			rect = this.ComputeNodeBox(node);
			this._bboxCache.set_Item(node, rect);
		}

	}

	public void UpdateIncremental() {

	}

	private abstract class BasicSortNodes extends ArrayStableSort {
		public Boolean _ascending = false;

		public BasicSortNodes(Boolean ascending) {
			this._ascending = ascending;
		}

		@Override
		public abstract Boolean Compare(java.lang.Object arg0,
				java.lang.Object arg1);

	}

	private final class SortNodesByArea extends Graph.SortNodesByDimension {
		public Graph __outerThis;

		public SortNodesByArea(Graph input__outerThis, Boolean ascending) {
			super(ascending);
			this.__outerThis = input__outerThis;
		}

		@Override
		public float GetNodeDimension(java.lang.Object node) {

			return (this.__outerThis.GetNodeWidth(node) * this.__outerThis
					.GetNodeHeight(node));

		}

	}

	private class SortNodesByComparator extends ArrayStableSort {
		public Graph __outerThis;

		private IComparer _comparator;

		public SortNodesByComparator(Graph input__outerThis,
				IComparer comparator) {
			this.__outerThis = input__outerThis;
			this._comparator = comparator;
		}

		@Override
		public Boolean Compare(java.lang.Object node1, java.lang.Object node2) {

			node1 = this.__outerThis.GetGraphNode(node1);

			node2 = this.__outerThis.GetGraphNode(node2);

			return (this._comparator.Compare(node1, node2) <= 0);

		}

	}

	private abstract class SortNodesByDimension extends Graph.BasicSortNodes {
		private float _diff;

		private float THREASHOLD = 0.01f;

		public SortNodesByDimension(Boolean ascending) {
			super(ascending);
		}

		@Override
		public Boolean Compare(java.lang.Object node1, java.lang.Object node2) {
			this._diff = this.GetNodeDimension(node1)
					- this.GetNodeDimension(node2);
			if (Math.Abs(this._diff) < 0.01f) {

				return true;
			}
			if (!super._ascending) {

				return (this._diff > 0f);
			}

			return (this._diff <= 0f);

		}

		public abstract float GetNodeDimension(java.lang.Object node);

	}

	private final class SortNodesByHeight extends Graph.SortNodesByDimension {
		public Graph __outerThis;

		public SortNodesByHeight(Graph input__outerThis, Boolean ascending) {
			super(ascending);
			this.__outerThis = input__outerThis;
		}

		@Override
		public float GetNodeDimension(java.lang.Object node) {

			return this.__outerThis.GetNodeBox(node).Height;

		}

	}

	private final class SortNodesByIndex extends Graph.BasicSortNodes {
		public Graph __outerThis;

		private Integer _index1;

		private Integer _index2;

		public SortNodesByIndex(Graph input__outerThis, Boolean ascending) {
			super(ascending);
			this.__outerThis = input__outerThis;
		}

		@Override
		public Boolean Compare(java.lang.Object node1, java.lang.Object node2) {

			this._index1 = this.__outerThis.GetNodeIndex(node1);

			this._index2 = this.__outerThis.GetNodeIndex(node2);
			if (this._index1 < 0) {

				return ((this._index2 < 0) || !super._ascending);
			}
			if (this._index2 < 0) {

				return super._ascending;
			}
			if (!super._ascending) {

				return (this._index1 >= this._index2);
			}

			return (this._index1 <= this._index2);

		}

	}

	private final class SortNodesByWidth extends Graph.SortNodesByDimension {
		public Graph __outerThis;

		public SortNodesByWidth(Graph input__outerThis, Boolean ascending) {
			super(ascending);
			this.__outerThis = input__outerThis;
		}

		@Override
		public float GetNodeDimension(java.lang.Object node) {

			return this.__outerThis.GetNodeBox(node).Width;

		}

	}
}