package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class OrthLinkRepositionAlgorithm extends HLevelAlgorithm {
	private float _leftDelta;

	private float _minDelta;

	private float _rightDelta;

	private HSegment[] _segments;

	public OrthLinkRepositionAlgorithm(HGraph graph) {
		Integer levelFlow = graph.GetLevelFlow();

		this._minDelta = graph.GetMinDistBetweenLinks(levelFlow);
	}

	public Boolean CanCauseConflict(HSegment segment) {

		if (!segment.GetOwnerLink().IsOrthogonal()) {

			return false;
		}
		Integer levelFlowDir = super.GetLevelFlowDir();

		return (segment.GetFromCoord(levelFlowDir) > (segment
				.GetToCoord(levelFlowDir) + 0.0002f));

	}

	@Override
	public void Clean() {
		super.Clean();
		this._segments = null;

	}

	public void CreateSegmentField() {
		HSegmentIterator segmentsFrom = super.GetUpperLevel().GetSegmentsFrom();
		Integer index = 0;

		while (segmentsFrom.HasNext()) {
			segmentsFrom.Next();
			index++;
		}
		this._segments = new HSegment[index];

		segmentsFrom = super.GetUpperLevel().GetSegmentsFrom();
		for (index = 0; segmentsFrom.HasNext(); index++) {
			HSegment segment = segmentsFrom.Next();
			this._segments[index] = segment;
			segment.SetSortValue((float) index);
		}

	}

	private float GetMinDist(HNode node1, HNode node2) {

		return super.GetGraph().GetMinDist(super.GetLevelFlowDir(), node1,
				node2);

	}

	public Boolean HasConflict(HNode node) {
		HSegmentIterator segmentsTo = node.GetSegmentsTo();
		this._leftDelta = 0f;
		this._rightDelta = 0f;
		Boolean flag = false;

		while (segmentsTo.HasNext()) {
			HSegment segment = segmentsTo.Next();

			if (this.HasConflict(segment)) {
				flag = true;
			}
		}

		return flag;

	}

	public Boolean HasConflict(HSegment segment) {

		if (!segment.GetOwnerLink().IsOrthogonal()) {

			return false;
		}
		Integer levelFlowDir = super.GetLevelFlowDir();
		float toCoord = segment.GetToCoord(levelFlowDir);
		Boolean flag = false;
		if (segment.GetFromCoord(levelFlowDir) < toCoord) {
			float num3 = toCoord - this._minDelta;
			float num4 = toCoord + this._minDelta;
			Integer num8 = ((int) segment.GetSortValue()) + 1;
			while (num8 < this._segments.length) {
				HSegment segment2 = this._segments[num8++];
				float fromCoord = segment2.GetFromCoord(levelFlowDir);
				float num6 = fromCoord - num3;
				if (num6 > 0f) {
					float num7 = num4 - fromCoord;
					if (num7 <= 0f) {

						return flag;
					}

					if (this.CanCauseConflict(segment2)) {
						if (num7 > this._leftDelta) {
							this._leftDelta = num7;
						}
						if (num6 > this._rightDelta) {
							this._rightDelta = num6;
						}
						flag = true;
					}
				}
			}
		}

		return flag;

	}

	@Override
	public void Init(HLevel upperLevel, HLevel lowerLevel) {
		super.Init(upperLevel, lowerLevel);

	}

	private Boolean IsPartOfStraightSegment(HNode node) {
		Integer levelFlowDir = super.GetLevelFlowDir();

		if (!node.IsDummyNode()) {

			return false;
		}
		if (node.GetSegmentsFromCount() != 1) {

			return false;
		}
		HNode to = node.GetFirstSegmentFrom().GetTo();

		if (!to.IsDummyNode()) {

			return false;
		}
		if (to.GetSegmentsToCount() != 1) {

			return false;
		}

		return (to.GetCoord(levelFlowDir) == node.GetCoord(levelFlowDir));

	}

	public void MarkDummyNodesOfStraightSegments() {
		HNodeIterator nodes = super.GetLowerLevel().GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();

			if (this.IsPartOfStraightSegment(node)) {
				node.SetMarker(0x1267);
			} else {
				node.SetMarker(0);
			}
		}

	}

	private void MoveNode(HNode node, float coord) {
		Boolean flag = this.IsPartOfStraightSegment(node)
				|| ((node.GetLevel() == super.GetLowerLevel()) && (node
						.GetMarker() == 0x1267));
		node.SetCoordWithLinkUpdate(super.GetLevelFlowDir(), coord);
		if (flag) {
			this.StraightenLink(node.GetFirstSegmentFrom().GetTo(), coord);
		}

	}

	public Boolean MoveNodes(HNode startNode, float startPos) {
		HNode node2 = null;
		Integer levelFlowDir = super.GetLevelFlowDir();
		HNodeIterator nodes = super.GetLowerLevel().GetNodes();
		HNode node = null;
		float coord = startPos;
		nodes.Init(startNode);

		while (nodes.HasNext()) {

			node2 = nodes.Next();
			if (node != null) {

				coord += this.GetMinDist(node, node2);
			}
			if (node2.GetCoord(levelFlowDir) >= coord) {
				break;
			}

			if (node2.IsFixedForIncremental(levelFlowDir)) {

				return false;
			}

			coord += node2.GetSize(levelFlowDir);
			node = node2;
		}
		node = null;
		coord = startPos;

		nodes = super.GetLowerLevel().GetNodes();
		nodes.Init(startNode);

		while (nodes.HasNext()) {

			node2 = nodes.Next();
			if (node != null) {

				coord += this.GetMinDist(node, node2);
			}
			if (node2.GetCoord(levelFlowDir) >= coord) {
				break;
			}
			this.MoveNode(node2, coord);

			coord += node2.GetSize(levelFlowDir);
			node = node2;
		}

		return true;

	}

	public void ResolveAllConflicts() {
		Integer levelFlowDir = super.GetLevelFlowDir();
		HNodeIterator nodes = super.GetLowerLevel().GetNodes();
		HNode node = null;
		float minValue = Float.MIN_VALUE;

		while (nodes.HasNext()) {
			HNode node2 = nodes.Next();
			if (node != null) {

				minValue += this.GetMinDist(node, node2);
			}
			this.ResolveConflict(node2, minValue);
			minValue = node2.GetCoord(levelFlowDir)
					+ node2.GetSize(levelFlowDir);
			node = node2;
		}

	}

	public void ResolveConflict(HNode node, float minCoord) {
		Integer levelFlowDir = super.GetLevelFlowDir();

		if (!node.IsFixedForIncremental(levelFlowDir)) {
			float coord = node.GetCoord(levelFlowDir);
			Boolean flag = true;
			Integer num5 = 0;
			for (Boolean flag2 = true; this.HasConflict(node) && flag2; flag2 &= num5 < 0x3e8) {
				num5++;
				float num3 = coord - this._leftDelta;
				float startPos = coord + this._rightDelta;
				float num6 = 1f;
				while ((num3 == coord) && (num6 < 1E+16f)) {
					num6 *= 2f;
					num3 = coord - (this._leftDelta * num6);
				}
				num6 = 1f;
				while ((startPos == coord) && (num6 < 1E+16f)) {
					num6 *= 2f;
					startPos = coord + (this._rightDelta * num6);
				}
				if (flag && (num3 >= minCoord)) {
					this.MoveNode(node, num3);
				} else {

					flag2 = this.MoveNodes(node, startPos);
					flag = false;
				}

				coord = node.GetCoord(levelFlowDir);
			}
		}

	}

	@Override
	public void Run() {
		this.CreateSegmentField();
		this.MarkDummyNodesOfStraightSegments();
		this.ResolveAllConflicts();
		this._segments = null;

	}

	private void StraightenLink(HNode node, float coord) {
		Integer levelFlowDir = super.GetLevelFlowDir();
		HNode prevNeighbor = node.GetPrevNeighbor();
		HNode nextNeighbor = node.GetNextNeighbor();
		if (((prevNeighbor == null) || (coord >= ((prevNeighbor
				.GetCoord(levelFlowDir) + prevNeighbor.GetSize(levelFlowDir)) + this
				.GetMinDist(prevNeighbor, node))))
				&& ((nextNeighbor == null) || (coord <= ((nextNeighbor
						.GetCoord(levelFlowDir) - node.GetSize(levelFlowDir)) - this
						.GetMinDist(node, nextNeighbor))))) {
			this.MoveNode(node, coord);
		}

	}

}