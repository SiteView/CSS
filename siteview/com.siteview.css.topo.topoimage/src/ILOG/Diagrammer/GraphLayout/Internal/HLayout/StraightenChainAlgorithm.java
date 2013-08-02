package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Collections.*;

public final class StraightenChainAlgorithm extends HGraphAlgorithm {
	private Boolean _hasSwimLanes = false;

	private float _intervalMaxForCurrentNode;

	private float _intervalMaxForStartNode;

	private float _intervalMinForCurrentNode;

	private float _intervalMinForStartNode;

	private Integer _levelFlowDir;

	private float _offsetFromStartNode;

	private float _prefPosition1;

	private float _prefPosition2;

	private Boolean _useConnectionPoints = false;

	public StraightenChainAlgorithm(HGraph graph, Boolean useConnectionPoints) {
		super.Init(graph);
		this._useConnectionPoints = useConnectionPoints;

		this._levelFlowDir = graph.GetLevelFlow();
		this._hasSwimLanes = false;
	}

	@Override
	public void Clean() {
		super.Clean();

	}

	private float GetConnectionPointOffset(HSegment segment, Boolean fromSide) {
		float fromCoord = 0;
		HNode node = fromSide ? segment.GetFrom() : segment.GetTo();
		Boolean flag = fromSide ? segment.IsFromSideFixed() : segment
				.IsToSideFixed();
		Boolean flag2 = super.GetGraph().GetConnectorStyle() == 100;

		if ((!this._useConnectionPoints || (node.IsDummyNode() && (node
				.GetSize(this._levelFlowDir) == 0f)))
				|| ((flag2 && !flag) && !node.IsEastWestPortAuxNode())) {

			return (0.5f * node.GetSize(this._levelFlowDir));
		}
		if (fromSide) {

			fromCoord = segment.GetFromCoord(this._levelFlowDir);
		} else {

			fromCoord = segment.GetToCoord(this._levelFlowDir);
		}

		return (fromCoord - node.GetCoord(this._levelFlowDir));

	}

	private HSegment GetFirstSegmentFrom(HNode node) {
		if (this._hasSwimLanes) {
			HSegmentIterator segmentsFrom = node.GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {
				HSegment segment = segmentsFrom.Next();
				if (segment.GetPriority() != 0f) {

					return segment;
				}
			}

			return null;
		}
		if (node.GetSegmentsFromCount() == 0) {

			return null;
		}

		return node.GetFirstSegmentFrom();

	}

	private HSegment GetFirstSegmentTo(HNode node) {
		if (this._hasSwimLanes) {
			HSegmentIterator segmentsTo = node.GetSegmentsTo();

			while (segmentsTo.HasNext()) {
				HSegment segment = segmentsTo.Next();
				if (segment.GetPriority() != 0f) {

					return segment;
				}
			}

			return null;
		}
		if (node.GetSegmentsToCount() == 0) {

			return null;
		}

		return node.GetFirstSegmentTo();

	}

	private float GetMinDist(HNode node1, HNode node2) {

		return super.GetGraph().GetMinDist(this._levelFlowDir, node1, node2);

	}

	private float GetPrefEndPosition(HNode node) {
		HSegment firstSegmentFrom = this.GetFirstSegmentFrom(node);
		if (firstSegmentFrom == null) {

			return node.GetCoord(this._levelFlowDir);
		}

		return ((firstSegmentFrom.GetTo().GetCoord(this._levelFlowDir) + this
				.GetConnectionPointOffset(firstSegmentFrom, false)) - this
				.GetConnectionPointOffset(firstSegmentFrom, true));

	}

	private float GetPrefStartPosition(HNode node) {
		HSegment firstSegmentTo = this.GetFirstSegmentTo(node);
		if (firstSegmentTo == null) {

			return node.GetCoord(this._levelFlowDir);
		}

		return ((firstSegmentTo.GetFrom().GetCoord(this._levelFlowDir) + this
				.GetConnectionPointOffset(firstSegmentTo, true)) - this
				.GetConnectionPointOffset(firstSegmentTo, false));

	}

	private Integer GetSegmentsFromCount(HNode node) {
		if (!this._hasSwimLanes) {

			return node.GetSegmentsFromCount();
		}
		Integer num = 0;
		HSegmentIterator segmentsFrom = node.GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {
			if (segmentsFrom.Next().GetPriority() != 0f) {
				num++;
			}
		}

		return num;

	}

	private Integer GetSegmentsToCount(HNode node) {
		if (!this._hasSwimLanes) {

			return node.GetSegmentsToCount();
		}
		Integer num = 0;
		HSegmentIterator segmentsTo = node.GetSegmentsTo();

		while (segmentsTo.HasNext()) {
			if (segmentsTo.Next().GetPriority() != 0f) {
				num++;
			}
		}

		return num;

	}

	private Boolean IsLongChainEndNode(HNode node) {

		if (!node.IsFixedForIncremental(this._levelFlowDir)) {
			Integer segmentsFromCount = null;
			Integer segmentsToCount = this.GetSegmentsToCount(node);
			HNode to = null;
			if (this._hasSwimLanes) {
				segmentsFromCount = 0;
				HSegmentIterator segmentsFrom = node.GetSegmentsFrom();

				while (segmentsFrom.HasNext()) {
					HSegment segment = segmentsFrom.Next();
					if (segment.GetPriority() != 0f) {
						segmentsFromCount++;

						to = segment.GetTo();
					}
				}
			} else {

				segmentsFromCount = node.GetSegmentsFromCount();
				if (segmentsFromCount > 0) {

					to = node.GetFirstSegmentFrom().GetTo();
				}
			}
			if (segmentsFromCount == 0) {

				return (segmentsToCount == 1);
			} else if (segmentsFromCount == 1) {
				if (segmentsToCount > 1) {

					return false;
				}

				if (this.IsLongChainInnerNode(to)) {

					return false;
				}

				if (this.IsLongChainEndNode(to)) {

					return false;
				}

				return true;
			}
		}

		return false;

	}

	private Boolean IsLongChainInnerNode(HNode node) {

		if (node.IsFixedForIncremental(this._levelFlowDir)) {

			return false;
		}

		return ((this.GetSegmentsFromCount(node) == 1) && (this
				.GetSegmentsToCount(node) == 1));

	}

	private Boolean IsLongChainStartNode(HNode node) {

		if (!node.IsFixedForIncremental(this._levelFlowDir)) {
			Integer segmentsToCount = null;
			Integer segmentsFromCount = this.GetSegmentsFromCount(node);
			HNode from = null;
			if (this._hasSwimLanes) {
				segmentsToCount = 0;
				HSegmentIterator segmentsTo = node.GetSegmentsTo();

				while (segmentsTo.HasNext()) {
					HSegment segment = segmentsTo.Next();
					if (segment.GetPriority() != 0f) {
						segmentsToCount++;

						from = segment.GetFrom();
					}
				}
			} else {

				segmentsToCount = node.GetSegmentsToCount();
				if (segmentsToCount > 0) {

					from = node.GetFirstSegmentTo().GetFrom();
				}
			}
			if (segmentsToCount == 0) {

				return (segmentsFromCount == 1);
			} else if (segmentsToCount == 1) {
				if (segmentsFromCount > 1) {

					return false;
				}

				if (this.IsLongChainInnerNode(from)) {

					return false;
				}

				if (this.IsLongChainStartNode(from)) {

					return false;
				}

				return true;
			}
		}

		return false;

	}

	private float LargestPossibleCoordinate(HNode node) {
		HNode nextNeighbor = node.GetNextNeighbor();
		if (nextNeighbor != null) {
			Integer index = this._levelFlowDir;

			return (nextNeighbor.GetCoord(index) - (this.GetMinDist(
					nextNeighbor, node) + node.GetSize(index)));
		}

		return Float.MAX_VALUE;

	}

	public Boolean MoveNode(HNode node, float coord) {
		float num = node.GetCoord(this._levelFlowDir);
		if (num != coord) {
			if (this._useConnectionPoints) {
				node.SetCoordWithLinkUpdate(this._levelFlowDir, coord);
			} else {
				node.SetCoord(this._levelFlowDir, coord);
			}
			float num2 = num - node.GetCoord(this._levelFlowDir);
			if ((-0.1f > num2) || (num2 > 0.1f)) {

				return true;
			}
		}

		return false;

	}

	@Override
	public void Run() {
		HGraph graph = super.GetGraph();

		this._hasSwimLanes = graph.HasSwimLanes();
		if (this._useConnectionPoints) {
			super.GetPercController().StartStep(graph._percForOptimizeLinks,
					2 * graph.GetNumberOfLinks());
		}
		this.UpdateLevels();
		this.StraightenLongChains();

	}

	public void SetStartInterval(HNode startNode) {
		float num = this.SmallestPossibleCoordinate(startNode);
		float num2 = this.LargestPossibleCoordinate(startNode);
		this._intervalMinForCurrentNode = num;
		this._intervalMaxForCurrentNode = num2;
		this._intervalMinForStartNode = num;
		this._intervalMaxForStartNode = num2;
		this._offsetFromStartNode = 0f;

		this._prefPosition1 = this.GetPrefStartPosition(startNode);

		this._prefPosition2 = this.GetPrefEndPosition(startNode);

	}

	private float SmallestPossibleCoordinate(HNode node) {
		HNode prevNeighbor = node.GetPrevNeighbor();
		if (prevNeighbor != null) {
			Integer index = this._levelFlowDir;
			float num2 = prevNeighbor.GetCoord(index)
					+ prevNeighbor.GetSize(index);

			return (num2 + this.GetMinDist(prevNeighbor, node));
		}

		return Float.MIN_VALUE;

	}

	private Boolean StraightenLongChain(HNode startNode) {
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();
		Boolean flag = false;
		this.SetStartInterval(startNode);
		HSegment firstSegmentFrom = this.GetFirstSegmentFrom(startNode);
		if (firstSegmentFrom == null) {

			return this.TryStraightenLongChainPart(startNode, null);
		}
		HNode to = firstSegmentFrom.GetTo();

		while (this.IsLongChainInnerNode(to)) {

			if (!this.UpdateInterval(to)) {

				flag |= this.TryStraightenLongChainPart(startNode, to);
				startNode = to;
				this.SetStartInterval(to);
			}

			to = this.GetFirstSegmentFrom(to).GetTo();
		}
		if (this.IsLongChainEndNode(to) && this.UpdateInterval(to)) {

			return (flag | this.TryStraightenLongChainPart(startNode, null));
		}

		return (flag | this.TryStraightenLongChainPart(startNode, to));

	}

	private void StraightenLongChains() {
		HNode node = null;
		HGraph graph = super.GetGraph();
		ArrayList coll = new ArrayList();
		HLevelIterator levels = graph.GetLevels();

		while (levels.HasNext()) {
			HNodeIterator nodes = levels.Next().GetNodes();

			while (nodes.HasNext()) {

				node = nodes.Next();

				if (this.IsLongChainStartNode(node)) {
					coll.Add(node);
				}
			}
		}
		Integer num = 2 * coll.get_Count();
		Boolean flag = true;
		while (flag && (num > 0)) {
			num--;
			flag = false;
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(coll);

			while (enumerator.HasMoreElements()) {
				node = (HNode) enumerator.NextElement();

				flag |= this.StraightenLongChain(node);
			}
		}

	}

	private Boolean TryStraightenLongChainPart(HNode startNode, HNode endNode) {
		HSegment firstSegmentTo = null;
		float num3 = 0;
		HSegment firstSegmentFrom = this.GetFirstSegmentFrom(startNode);
		Boolean flag = false;
		if ((firstSegmentFrom == null) || (firstSegmentFrom.GetTo() == endNode)) {

			firstSegmentTo = this.GetFirstSegmentTo(startNode);
			if ((firstSegmentTo != null)
					&& ((firstSegmentFrom == null) || firstSegmentTo
							.GetOwnerLink().IsOrthogonal())) {
				num3 = this._prefPosition1;
				if ((this._intervalMinForStartNode < num3)
						&& (num3 < this._intervalMaxForStartNode)) {

					return this.MoveNode(startNode, num3);
				}
			}
			if ((firstSegmentFrom != null)
					&& ((firstSegmentTo == null) || firstSegmentFrom
							.GetOwnerLink().IsOrthogonal())) {
				num3 = this._prefPosition2;
				if ((this._intervalMinForStartNode < num3)
						&& (num3 < this._intervalMaxForStartNode)) {

					return this.MoveNode(startNode, num3);
				}
			}

			return false;
		}
		num3 = this._prefPosition2;
		if ((num3 < this._intervalMinForStartNode)
				|| (num3 > this._intervalMaxForStartNode)) {
			num3 = this._prefPosition1;
		}
		if (num3 < this._intervalMinForStartNode) {
			num3 = this._intervalMinForStartNode;
		}
		if (num3 > this._intervalMaxForStartNode) {
			num3 = this._intervalMaxForStartNode;
		}
		HNode to = startNode;
		while (to != endNode) {

			flag |= this.MoveNode(to, num3);
			if (flag && this._useConnectionPoints) {

				firstSegmentTo = this.GetFirstSegmentTo(to);

				if ((firstSegmentTo != null) && !to.IsDummyNode()) {
					float num4 = firstSegmentTo.GetToCoord(this._levelFlowDir)
							- firstSegmentTo.GetFromCoord(this._levelFlowDir);
					if ((-1E-05f < num4) && (num4 < 1E-05f)) {
						this.MoveNode(to, to.GetCoord(this._levelFlowDir)
								+ num4);
					}
					num4 = firstSegmentTo.GetToCoord(this._levelFlowDir)
							- firstSegmentTo.GetFromCoord(this._levelFlowDir);
					if ((-1E-05f < num4) && (num4 < 1E-05f)) {
						firstSegmentTo.GetToPoint()[this._levelFlowDir] += num4;
					}
				}
			}

			firstSegmentFrom = this.GetFirstSegmentFrom(to);
			if (firstSegmentFrom == null) {

				return flag;
			}

			to = firstSegmentFrom.GetTo();
			float connectionPointOffset = this.GetConnectionPointOffset(
					firstSegmentFrom, false);
			float num2 = this.GetConnectionPointOffset(firstSegmentFrom, true);
			num3 = (num3 + num2) - connectionPointOffset;
		}

		return flag;

	}

	public Boolean UpdateInterval(HNode node) {
		HSegment firstSegmentTo = this.GetFirstSegmentTo(node);
		float num = this.SmallestPossibleCoordinate(node);
		float num2 = this.LargestPossibleCoordinate(node);
		float num3 = this.GetConnectionPointOffset(firstSegmentTo, true)
				- this.GetConnectionPointOffset(firstSegmentTo, false);
		this._offsetFromStartNode += num3;
		if (this._intervalMinForCurrentNode != Float.MIN_VALUE) {
			this._intervalMinForCurrentNode += num3;
		}
		if (this._intervalMaxForCurrentNode != Float.MAX_VALUE) {
			this._intervalMaxForCurrentNode += num3;
		}
		if ((num2 < this._intervalMinForCurrentNode)
				|| (num > this._intervalMaxForCurrentNode)) {

			return false;
		}
		this._prefPosition2 = this.GetPrefEndPosition(node)
				- this._offsetFromStartNode;
		if (this._intervalMinForCurrentNode < num) {
			this._intervalMinForStartNode = num - this._offsetFromStartNode;
			this._intervalMinForCurrentNode = num;
		}
		if (this._intervalMaxForCurrentNode > num2) {
			this._intervalMaxForStartNode = num2 - this._offsetFromStartNode;
			this._intervalMaxForCurrentNode = num2;
		}

		return true;

	}

	private void UpdateLevels() {
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			levels.Next().UpdateInfo();
		}

	}

}