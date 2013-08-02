package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class SortAdjByCoordAlgorithm extends HLevelSweepAlgorithm {
	private Integer _levelFlowDirection;

	private HSegmentSort _segmentSortAlg;

	public SortAdjByCoordAlgorithm(HGraph graph) {
		super.Init(graph);
		this._segmentSortAlg = new HSegmentSort();

		this._levelFlowDirection = graph.GetLevelFlow();
	}

	@Override
	public void Clean() {
		super.Clean();
		this._segmentSortAlg = null;

	}

	private float GetMinDelta(HNode node, HSegmentIterator iter) {
		float sortValue = 0;
		float num = node.GetSize(this._levelFlowDirection) + 1f;
		for (float i = node.GetCoord(this._levelFlowDirection) - 1f; iter
				.HasNext(); i = sortValue) {

			sortValue = iter.Next().GetSortValue();
			if (((sortValue - i) > 0f) && ((sortValue - i) < num)) {
				num = sortValue - i;
			}
		}

		return num;

	}

	@Override
	public void Run() {
		HGraph graph = super.GetGraph();
		Integer pointsOfStep = 2 * (graph.GetNumberOfLevels() - 1);
		super.GetPercController().StartStep(
				graph._percForSortAdjByCoordAlgorithm, pointsOfStep);
		this.SweepForward();
		this.SweepBackward();

	}

	private void SortFromSegments(HLevel level) {
		HNodeIterator nodes = level.GetNodes();

		while (nodes.HasNext()) {
			HSegment segment = null;
			HNode node = nodes.Next();
			HSegmentIterator segmentsFrom = node.GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();
				segment.SetSortValue(segment
						.GetFromCoord(this._levelFlowDirection));
			}
			node.SortFromSegments(this._segmentSortAlg);
			float minDelta = this.GetMinDelta(node, node.GetSegmentsFrom());

			segmentsFrom = node.GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();
				float sortValue = segment
						.GetFromCoord(this._levelFlowDirection)
						+ (minDelta / ((float) (segment.GetTo()
								.GetPositionInLevel() + 2)));
				segment.SetSortValue(sortValue);
			}
			node.SortFromSegments(this._segmentSortAlg);
		}

	}

	private void SortToSegments(HLevel level) {
		HNodeIterator nodes = level.GetNodes();

		while (nodes.HasNext()) {
			HSegment segment = null;
			HNode node = nodes.Next();
			HSegmentIterator segmentsTo = node.GetSegmentsTo();

			while (segmentsTo.HasNext()) {

				segment = segmentsTo.Next();
				segment.SetSortValue(segment
						.GetToCoord(this._levelFlowDirection));
			}
			node.SortToSegments(this._segmentSortAlg);
			float minDelta = this.GetMinDelta(node, node.GetSegmentsTo());

			segmentsTo = node.GetSegmentsTo();

			while (segmentsTo.HasNext()) {

				segment = segmentsTo.Next();
				float sortValue = segment.GetToCoord(this._levelFlowDirection)
						+ (minDelta / ((float) (segment.GetFrom()
								.GetPositionInLevel() + 2)));
				segment.SetSortValue(sortValue);
			}
			node.SortToSegments(this._segmentSortAlg);
		}

	}

	@Override
	public void TreatBackwardLevel(HLevel prevLevel, HLevel level) {
		this.SortToSegments(prevLevel);

	}

	@Override
	public void TreatForwardLevel(HLevel prevLevel, HLevel level) {
		this.SortFromSegments(prevLevel);

	}

}