package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import system.*;

public final class LLGridLine {
	private Integer _index;

	private Boolean _isBinTree = false;

	private Boolean _isHorizontal = false;

	private LLGridLine _nextDirty;

	private LLGridSegment _segments;

	public LLGridLine(Integer maxGridPointIndex, Integer index,
			Boolean isHorizontal) {
		this._index = index;
		this._isHorizontal = isHorizontal;
		this._isBinTree = false;
		this._segments = new LLGridSegment(this, 0, maxGridPointIndex, null);
		this._nextDirty = null;
	}

	public void AddSegment(Integer startIndex, Integer endIndex) {
		LLGridSegment segment = new LLGridSegment(this, startIndex, endIndex,
				null);
		if (this._segments == null) {
			this._segments = segment;
		} else if (this._isBinTree) {
			LLGridSegment segment2 = this.SearchClosestSegment(startIndex,
					false);
			if (segment2 != null) {
				segment.SetNexts(null, segment2.GetNext1());
				segment2.SetNexts(segment2.GetNext0(), segment);
			} else {

				segment2 = this.SearchClosestSegment(endIndex, true);
				segment2.SetNexts(segment, segment2.GetNext1());
			}
		} else {
			LLGridSegment segment3 = this.SearchClosestSegment(startIndex,
					false);
			if (segment3 != null) {
				segment.SetNexts(null, segment3.GetNext1());
				segment3.SetNexts(null, segment);
			} else {
				segment.SetNexts(null, this._segments);
				this._segments = segment;
			}
		}

	}

	private LLGridSegment BintreeConversionTraversal(LLGridSegment[] segTab,
			Integer startIndex, Integer endIndex) {
		if (endIndex < startIndex) {

			return null;
		}
		Integer index = (startIndex + endIndex) / 2;
		LLGridSegment segment = segTab[index];
		segment.SetNexts(
				this.BintreeConversionTraversal(segTab, startIndex, index - 1),
				this.BintreeConversionTraversal(segTab, index + 1, endIndex));

		return segment;

	}

	public Integer CalcApproxCrossings(LLGrid grid, Integer start, Integer end) {
		Integer num = 0;
		Integer num2 = (start < end) ? start : end;
		Integer num3 = (start > end) ? start : end;
		for (Integer i = num2; i < num3; i++) {
			if (this.SearchOrthogonalSegment(grid, i) == null) {
				num++;
			}
		}

		return num;

	}

	public void Clear() {
		if (this._segments != null) {
			this._segments.ClearRecursive();
		}
		this._nextDirty = null;

	}

	private void ConvertToBinTree(Integer numSegments) {
		if ((numSegments > 0) && !this._isBinTree) {
			LLGridSegment[] segTab = new LLGridSegment[numSegments];
			LLGridSegment segment = this._segments;
			Integer num = 0;
			while (segment != null) {
				segTab[num++] = segment;

				segment = segment.GetNext1();
			}

			this._segments = this.BintreeConversionTraversal(segTab, 0,
					numSegments - 1);
			this._isBinTree = true;
		}

	}

	private void ConvertToList() {
		if (this._isBinTree) {
			LLGridSegment segment = this._segments;
			this._segments = null;
			this.ListConversionTraversal(segment);
			this._isBinTree = false;
		}

	}

	public void Dispose() {
		LLGridSegment segment2 = null;
		this.ConvertToList();
		for (LLGridSegment segment = this._segments; segment != null; segment = segment2) {

			segment2 = segment.GetNext1();
			segment.Dispose();
		}
		this._segments = null;
		this._nextDirty = null;

	}

	public Integer GetIndex() {

		return this._index;

	}

	public LLGridLine GetNextDirty() {

		return this._nextDirty;

	}

	public Boolean IsDirty() {

		return (this._nextDirty != null);

	}

	public Boolean IsFree(Integer maxGridPoint) {
		if (this._segments == null) {

			return false;
		}
		if (this._segments.GetStartIndex() > 0) {

			return false;
		}
		if (this._segments.GetEndIndex() < maxGridPoint) {

			return false;
		}

		return true;

	}

	public Boolean IsHorizontal() {

		return this._isHorizontal;

	}

	private void ListConversionTraversal(LLGridSegment segment) {
		if (segment != null) {
			this.ListConversionTraversal(segment.GetNext1());
			LLGridSegment segment2 = this._segments;
			this._segments = segment;
			this.ListConversionTraversal(segment.GetNext0());
			segment.SetNexts(null, segment2);
		}

	}

	public void ReorganizeBeforeSearch(Boolean keepDirty) {
		this.ConvertToList();
		LLGridSegment segment = this._segments;
		while ((segment != null) && segment.IsInvalid()) {
			LLGridSegment segment2 = segment.GetNext1();
			segment.Dispose();
			segment = segment2;
		}
		this._segments = segment;
		Integer numSegments = 0;
		while (segment != null) {
			segment.Clear();
			segment.RemoveInvalidNext();
			numSegments++;

			segment = segment.GetNext1();
		}
		if (numSegments >= 8) {
			this.ConvertToBinTree(numSegments);
		}
		if (!keepDirty) {
			this._nextDirty = null;
		}

	}

	public LLGridSegment SearchClosestSegment(Integer i, Boolean larger) {
		LLGridSegment segment = this._segments;
		if (this._isBinTree) {
			LLGridSegment segment2 = null;
			while (segment != null) {
				if ((segment.GetStartIndex() <= i)
						&& (i <= segment.GetEndIndex())) {

					return segment;
				}
				if (larger && (i < segment.GetStartIndex())) {
					segment2 = segment;
				}
				if (!larger && (segment.GetEndIndex() < i)) {
					segment2 = segment;
				}
				if (i < segment.GetStartIndex()) {

					segment = segment.GetNext0();
				} else {

					segment = segment.GetNext1();
				}
			}

			return segment2;
		}
		LLGridSegment segment3 = null;
		while (segment != null) {
			if ((segment.GetStartIndex() <= i) && (i <= segment.GetEndIndex())) {

				return segment;
			}
			if (i < segment.GetStartIndex()) {
				if (larger) {

					return segment;
				}

				return segment3;
			}
			segment3 = segment;

			segment = segment.GetNext1();
		}
		if (!larger) {

			return segment3;
		}

		return null;

	}

	public LLGridSegment SearchClosestSegmentAlive(LLGrid grid, Integer i,
			Boolean larger) {
		Integer num = null;
		Integer num2 = null;
		LLGridSegment seg = this._segments;
		if (larger) {
			num = i;
			num2 = 0x7fffffff;
		} else {
			num = 0;
			num2 = i;
		}
		if (!this._isBinTree) {
			LLGridSegment segment2 = null;
			while ((seg != null) && seg.IsDeadlock(grid, num, num2)) {

				seg = seg.GetNext1();
			}
			while (seg != null) {
				if ((seg.GetStartIndex() <= i) && (i <= seg.GetEndIndex())) {

					return seg;
				}
				if (i < seg.GetStartIndex()) {
					if (larger) {

						return seg;
					}

					return segment2;
				}
				segment2 = seg;
				for (seg = seg.GetNext1(); (seg != null)
						&& seg.IsDeadlock(grid, num, num2); seg = seg
						.GetNext1()) {
				}
			}
			if (!larger) {

				return segment2;
			}

			return null;
		}
		if (!larger) {
			while (seg != null) {
				if (seg.GetStartIndex() <= i) {
					break;
				}

				seg = seg.GetNext0();
			}
		} else {
			while (seg != null) {
				if (seg.GetEndIndex() >= i) {
					break;
				}

				seg = seg.GetNext1();
			}
		}

		return this.SearchClosestSegmentAliveBinTree(grid, i, larger, num,
				num2, seg, null);

	}

	public LLGridSegment SearchClosestSegmentAliveBinTree(LLGrid grid,
			Integer i, Boolean larger, Integer minIndex, Integer maxIndex,
			LLGridSegment seg, LLGridSegment bestSeg) {
		if (seg != null) {

			if (!seg.IsDeadlock(grid, minIndex, maxIndex)) {
				if ((seg.GetStartIndex() <= i) && (i <= seg.GetEndIndex())) {

					return seg;
				}
				if (larger) {
					if ((i < seg.GetStartIndex())
							&& ((bestSeg == null) || (seg.GetStartIndex() < bestSeg
									.GetStartIndex()))) {
						bestSeg = seg;
					}
				} else if ((seg.GetEndIndex() < i)
						&& ((bestSeg == null) || (bestSeg.GetEndIndex() < seg
								.GetEndIndex()))) {
					bestSeg = seg;
				}
			}

			bestSeg = this.SearchClosestSegmentAliveBinTree(grid, i, larger,
					minIndex, maxIndex, seg.GetNext0(), bestSeg);

			bestSeg = this.SearchClosestSegmentAliveBinTree(grid, i, larger,
					minIndex, maxIndex, seg.GetNext1(), bestSeg);
		}

		return bestSeg;

	}

	public LLGridSegment SearchOrthogonalSegment(LLGrid grid, Integer i) {
		Integer num = null;
		if (this._isHorizontal) {
			num = 0;
		} else {
			num = 1;
		}

		return grid.GetGridLine(num, i).SearchSegment(this._index);

	}

	public LLGridSegment SearchSegment(Integer i) {
		LLGridSegment segment = this._segments;
		if (!this._isBinTree) {
			while (segment != null) {
				if ((segment.GetStartIndex() <= i)
						&& (i <= segment.GetEndIndex())) {

					return segment;
				}

				segment = segment.GetNext1();
			}
		} else {
			while (segment != null) {
				if ((segment.GetStartIndex() <= i)
						&& (i <= segment.GetEndIndex())) {

					return segment;
				}
				if (i < segment.GetStartIndex()) {

					segment = segment.GetNext0();
				} else {

					segment = segment.GetNext1();
				}
			}
		}

		return null;

	}

	public void SetNextDirty(LLGridLine next) {
		this._nextDirty = next;

	}

	public void SetObstacle(LLGrid grid, Integer obstacleStartIndex,
			Integer obstacleEndIndex) {
		LLGridSegment segment = this._segments;
		grid.MarkDirty(this);
		Boolean flag = false;
		if (!this._isBinTree) {
			while ((segment != null)
					&& (segment.GetStartIndex() <= obstacleEndIndex)) {
				segment.SetObstacle(obstacleStartIndex, obstacleEndIndex);

				flag |= segment.IsInvalid();

				segment = segment.GetNext1();
			}
		} else {

			flag = this.SetObstacleRec(this._segments, obstacleStartIndex,
					obstacleEndIndex);
		}
		if (flag && grid.IsImmediateReorganizationNeeded()) {
			this.ReorganizeBeforeSearch(true);
		}

	}

	private Boolean SetObstacleRec(LLGridSegment segment,
			Integer obstacleStartIndex, Integer obstacleEndIndex) {
		if (segment == null) {

			return false;
		}
		segment.SetObstacle(obstacleStartIndex, obstacleEndIndex);
		Boolean flag = segment.IsInvalid();
		if (segment.IsInvalid()
				|| (segment.GetStartIndex() > obstacleStartIndex)) {

			flag |= this.SetObstacleRec(segment.GetNext0(), obstacleStartIndex,
					obstacleEndIndex);
		}

		if (!segment.IsInvalid() && (segment.GetEndIndex() >= obstacleEndIndex)) {

			return flag;
		}

		return (flag | this.SetObstacleRec(segment.GetNext1(),
				obstacleStartIndex, obstacleEndIndex));

	}

	public void ShiftIndex(Integer ownDelta, Integer segmentDelta) {
		this._index += ownDelta;
		if ((segmentDelta != 0) && (this._segments != null)) {
			this._segments.ShiftIndex(segmentDelta);
		}

	}

}