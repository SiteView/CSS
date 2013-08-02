package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class SortAdjacenciesAlgorithm extends HLevelSweepAlgorithm {
	private Boolean _hasOrthogonalLinks = false;

	private Integer _levelFlowDirection;

	private HSegmentSort _segmentSortAlg;

	public SortAdjacenciesAlgorithm(HGraph graph) {
		super.Init(graph);
		this._segmentSortAlg = new HSegmentSort();

		this._levelFlowDirection = graph.GetLevelFlow();

		this._hasOrthogonalLinks = graph.HasOrthogonalLinks();
	}

	private void CalcFromSegmentBypassSortValue(HNode node) {
		HLevel level2 = null;
		float num9 = 0;
		HLevel level = node.GetLevel();
		HSegment firstSegmentFrom = node.GetFirstSegmentFrom();
		HSegment opposite = node.GetOpposite(firstSegmentFrom);
		level2 = level2 = firstSegmentFrom.GetTo().GetLevel();
		float size = level.GetSize(this._levelFlowDirection);
		float coord = level.GetCoord(this._levelFlowDirection);
		float num3 = level2.GetSize(this._levelFlowDirection);
		float num4 = level2.GetCoord(this._levelFlowDirection);
		float num5 = coord;
		float num6 = coord + size;
		float num7 = firstSegmentFrom.GetTo()
				.GetCoord(this._levelFlowDirection);
		float num8 = opposite.GetTo().GetCoord(this._levelFlowDirection);
		num7 -= num4;
		num8 -= num4;
		if (size != 0f) {
			num9 = (node.GetCoord(this._levelFlowDirection) - coord) / size;
		} else {
			num9 = 0f;
		}
		if (num7 < num8) {
			firstSegmentFrom.SetSortValue((((num6 + 1f) + num3) + num9) - num8);
			opposite.SetSortValue(((num5 - 1f) - num9) - num7);
		} else if (num7 > num8) {
			firstSegmentFrom.SetSortValue(((num5 - 1f) - num9) - num8);
			opposite.SetSortValue((((num6 + 1f) + num3) + num9) - num7);
		} else {
			firstSegmentFrom.SetSortValue(node
					.GetCoord(this._levelFlowDirection));
			opposite.SetSortValue(node.GetCoord(this._levelFlowDirection));
		}

	}

	private void CalcFromSegmentSortValue(HLevel level) {
		HNode node = null;
		HNodeIterator nodes = level.GetNodes();
		for (HNode node2 = null; nodes.HasNext(); node2 = node) {

			node = nodes.Next();
			if (this._hasOrthogonalLinks && node.IsUpperBypassNode()) {
				this.CalcFromSegmentBypassSortValue(node);
			} else {
				Integer fromPortNumber = null;
				Boolean flag = null;
				if ((node2 != null) && (node2.GetEastPortAuxNode() == node)) {
					flag = true;
				} else {
					flag = false;
				}
				HSegmentIterator segmentsFrom = node.GetSegmentsFrom();
				Integer num6 = 0;
				Integer num5 = 0;

				while (segmentsFrom.HasNext()) {

					fromPortNumber = segmentsFrom.Next().GetFromPortNumber();
					if (fromPortNumber > num6) {
						num6 = fromPortNumber;
					}
					num5++;
				}
				float coord = node.GetCoord(this._levelFlowDirection);
				float num3 = node.GetSize(this._levelFlowDirection)
						/ ((float) ((num5 + num6) + 2));

				segmentsFrom = node.GetSegmentsFrom();
				float num4 = 0f;

				while (segmentsFrom.HasNext()) {
					HSegment segment = segmentsFrom.Next();

					fromPortNumber = segment.GetFromPortNumber();
					if (fromPortNumber >= 0) {
						if (flag) {
							segment.SetSortValue(coord
									+ ((((num5 + 1) + num6) - fromPortNumber) * num3));
						} else {
							segment.SetSortValue(coord
									+ (((num5 + 1) + fromPortNumber) * num3));
						}
					} else {
						segment.SetSortValue(coord + (num4 * num3));
						num4++;
					}
				}
			}
		}

	}

	private void CalcToSegmentBypassSortValue(HNode node) {
		HLevel level2 = null;
		float num9 = 0;
		HLevel level = node.GetLevel();
		HSegment firstSegmentTo = node.GetFirstSegmentTo();
		HSegment opposite = node.GetOpposite(firstSegmentTo);
		level2 = level2 = firstSegmentTo.GetFrom().GetLevel();
		float size = level.GetSize(this._levelFlowDirection);
		float coord = level.GetCoord(this._levelFlowDirection);
		float num3 = level2.GetSize(this._levelFlowDirection);
		float num4 = level2.GetCoord(this._levelFlowDirection);
		float num5 = coord;
		float num6 = coord + size;
		float num7 = firstSegmentTo.GetFrom()
				.GetCoord(this._levelFlowDirection);
		float num8 = opposite.GetFrom().GetCoord(this._levelFlowDirection);
		num7 -= num4;
		num8 -= num4;
		if (size != 0f) {
			num9 = (node.GetCoord(this._levelFlowDirection) - coord) / size;
		} else {
			num9 = 0f;
		}
		if (num7 < num8) {
			firstSegmentTo.SetSortValue((((num6 + 1f) + num3) + num9) - num8);
			opposite.SetSortValue(((num5 - 1f) - num9) - num7);
		} else if (num7 > num8) {
			firstSegmentTo.SetSortValue(((num5 - 1f) - num9) - num8);
			opposite.SetSortValue((((num6 + 1f) + num3) + num9) - num7);
		} else {
			firstSegmentTo
					.SetSortValue(node.GetCoord(this._levelFlowDirection));
			opposite.SetSortValue(node.GetCoord(this._levelFlowDirection));
		}

	}

	private void CalcToSegmentSortValue(HLevel level) {
		HNode node = null;
		HNodeIterator nodes = level.GetNodes();
		for (HNode node2 = null; nodes.HasNext(); node2 = node) {

			node = nodes.Next();
			if (this._hasOrthogonalLinks && node.IsLowerBypassNode()) {
				this.CalcToSegmentBypassSortValue(node);
			} else {
				Integer toPortNumber = null;
				Boolean flag = null;
				if (node.IsEastWestPortAuxNode()
						&& ((node2 == null) || (node2.GetEastPortAuxNode() != node))) {
					flag = true;
				} else {
					flag = false;
				}
				HSegmentIterator segmentsTo = node.GetSegmentsTo();
				Integer num6 = 0;
				Integer num5 = 0;

				while (segmentsTo.HasNext()) {

					toPortNumber = segmentsTo.Next().GetToPortNumber();
					if (toPortNumber > num6) {
						num6 = toPortNumber;
					}
					num5++;
				}
				float coord = node.GetCoord(this._levelFlowDirection);
				float num3 = node.GetSize(this._levelFlowDirection)
						/ ((float) ((num5 + num6) + 2));

				segmentsTo = node.GetSegmentsTo();
				float num4 = 0f;

				while (segmentsTo.HasNext()) {
					HSegment segment = segmentsTo.Next();

					toPortNumber = segment.GetToPortNumber();
					if (toPortNumber >= 0) {
						if (flag) {
							segment.SetSortValue(coord
									+ ((((num5 + 1) + num6) - toPortNumber) * num3));
						} else {
							segment.SetSortValue(coord
									+ (((num5 + 1) + toPortNumber) * num3));
						}
					} else {
						segment.SetSortValue(coord + (num4 * num3));
						num4++;
					}
				}
			}
		}

	}

	@Override
	public void Clean() {
		super.Clean();
		this._segmentSortAlg = null;

	}

	private void CorrectSortValueAtMultiAuxPortNodes(HLevel level) {
		HNode node2 = null;
		HNodeIterator nodes = level.GetNodes();
		for (HNode node3 = null; nodes.HasNext(); node3 = node2) {

			node2 = nodes.Next();

			if (node2.IsEastWestPortAuxNode()) {
				Integer toPortNumber = null;
				Boolean flag = null;
				if ((node3 == null) || (node3.GetEastPortAuxNode() != node2)) {
					flag = true;
				} else {
					flag = false;
				}
				HSegmentIterator segmentsTo = node2.GetSegmentsTo();
				Integer num5 = 0;

				while (segmentsTo.HasNext()) {

					toPortNumber = segmentsTo.Next().GetToPortNumber();
					if (toPortNumber > num5) {
						num5 = toPortNumber;
					}
				}

				segmentsTo = node2.GetSegmentsTo();

				while (segmentsTo.HasNext()) {
					HSegment segment = segmentsTo.Next();
					HNode from = segment.GetFrom();
					Integer fromPortNumber = segment.GetFromPortNumber();

					toPortNumber = segment.GetToPortNumber();
					if (((fromPortNumber >= 0) && (toPortNumber >= 0))
							&& from.IsEastWestPortAuxNode()) {
						float coord = from.GetCoord(this._levelFlowDirection);
						float size = from.GetSize(this._levelFlowDirection);
						Integer segmentsFromCount = from.GetSegmentsFromCount();
						float num3 = size
								/ ((float) ((segmentsFromCount + num5) + 2));
						if (flag) {
							segment.SetSortValue(coord
									+ ((((segmentsFromCount + 1) + num5) - toPortNumber) * num3));
						} else {
							segment.SetSortValue(coord
									+ (((segmentsFromCount + 1) + toPortNumber) * num3));
						}
					}
				}
			}
		}

	}

	@Override
	public void Run() {
		HGraph graph = super.GetGraph();
		Integer pointsOfStep = 2 * (graph.GetNumberOfLevels() - 1);
		super.GetPercController().StartStep(graph._percForSortAdjacencies,
				pointsOfStep);
		this.SweepForward();
		this.SweepBackward();
		this.SweepForward();

	}

	@Override
	public void TreatBackwardLevel(HLevel prevLevel, HLevel level) {
		this.CalcToSegmentSortValue(prevLevel);
		HNodeIterator nodes = level.GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().SortFromSegments(this._segmentSortAlg);
		}

	}

	@Override
	public void TreatForwardLevel(HLevel prevLevel, HLevel level) {
		this.CalcFromSegmentSortValue(prevLevel);
		this.CorrectSortValueAtMultiAuxPortNodes(level);
		HNodeIterator nodes = level.GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().SortToSegments(this._segmentSortAlg);
		}

	}

}