package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import system.*;

public final class LLGridSegment {
	private Integer _backtrackDist;

	private Integer _btEndIndex;

	private LLGridSegment _btnext;

	private Integer _btStartIndex;

	private Integer _endIndex;

	private LLGridLine _gridLine;

	private Integer _minSegmentNumber;

	private LLGridSegment _next0;

	private LLGridSegment _next1;

	private Integer _startIndex;

	public LLGridSegment(LLGridLine owner, Integer startIndex,
			Integer endIndex, LLGridSegment next) {
		this._gridLine = owner;
		this._startIndex = startIndex;
		this._endIndex = endIndex;
		this._backtrackDist = 0x7fffffff;
		this._btStartIndex = 0x7fffffff;
		this._btEndIndex = -1;
		this._minSegmentNumber = 0x7fffffff;
		this._next0 = null;
		this._next1 = next;
		this._btnext = null;
	}

	public Integer CalcBacktrackDist(Integer i) {
		Integer backtrackDist = this.GetBacktrackDist();
		if (backtrackDist == 0x7fffffff) {

			return 0x7fffffff;
		}
		if ((i < this._startIndex) || (i > this._endIndex)) {

			return 0x7fffffff;
		}
		if (i < this._btStartIndex) {

			return ((backtrackDist + this._btStartIndex) - i);
		}
		if (i > this._btEndIndex) {

			return ((backtrackDist + i) - this._btEndIndex);
		}

		return backtrackDist;

	}

	public Boolean CanBacktrack() {
		if (this._btStartIndex <= this._startIndex) {

			return (this._btEndIndex < this._endIndex);
		}

		return true;

	}

	public void Clear() {
		this._btnext = null;
		this._backtrackDist = 0x7fffffff;
		this._btStartIndex = 0x7fffffff;
		this._btEndIndex = -1;
		this._minSegmentNumber = 0x7fffffff;

	}

	public void ClearRecursive() {
		this.Clear();
		if (this._next0 != null) {
			this._next0.ClearRecursive();
		}
		if (this._next1 != null) {
			this._next1.ClearRecursive();
		}

	}

	public void Dispose() {
		this._next0 = null;
		this._next1 = null;
		this._btnext = null;
		this._gridLine = null;

	}

	public Integer GetBacktrackDist() {

		return this._backtrackDist;

	}

	public Integer GetBacktrackEndIndex() {

		return this._btEndIndex;

	}

	public Integer GetBacktrackStartIndex() {

		return this._btStartIndex;

	}

	public Integer GetEndIndex() {

		return this._endIndex;

	}

	public LLGridLine GetGridLine() {

		return this._gridLine;

	}

	public Integer GetMinSegmentNumber() {

		return this._minSegmentNumber;

	}

	public LLGridSegment GetNext0() {

		return this._next0;

	}

	public LLGridSegment GetNext1() {

		return this._next1;

	}

	public LLGridSegment GetNextBacktrackSegement() {

		return this._btnext;

	}

	public Integer GetStartIndex() {

		return this._startIndex;

	}

	public Boolean IsDeadlock(LLGrid grid, Integer minIndex, Integer maxIndex) {

		if (!this.IsInvalid()) {
			LLGridLine gridLine = this.GetGridLine();
			if (minIndex < this._startIndex) {
				minIndex = this._startIndex;
			}
			if (maxIndex > this._endIndex) {
				maxIndex = this._endIndex;
			}
			for (Integer i = minIndex; i <= maxIndex; i++) {
				if (gridLine.SearchOrthogonalSegment(grid, i) != null) {

					return false;
				}
			}
		}

		return true;

	}

	public Boolean IsHorizontal() {

		return this.GetGridLine().IsHorizontal();

	}

	public Boolean IsInvalid() {

		return ((this._startIndex == -1) && (this._endIndex == -1));

	}

	public Boolean IsOnBacktrackStack() {

		return (this._btnext != null);

	}

	public void RemoveInvalidNext() {
		LLGridSegment segment = this._next1;
		while ((segment != null) && segment.IsInvalid()) {
			LLGridSegment segment2 = segment.GetNext1();
			segment.Dispose();
			segment = segment2;
		}
		this._next1 = segment;

	}

	public LLGridSegment SearchOrthogonalSegment(LLGrid grid, Integer i) {

		return this.GetGridLine().SearchOrthogonalSegment(grid, i);

	}

	public void SetBacktrackDist(Integer dist) {
		this._backtrackDist = dist;

	}

	public void SetBacktrackEndIndex(Integer endIndex) {
		this._btEndIndex = endIndex;

	}

	public void SetBacktrackStartIndex(Integer startIndex) {
		this._btStartIndex = startIndex;

	}

	public void SetEndIndex(Integer index) {
		this._endIndex = index;

	}

	public void SetMinSegmentNumber(Integer segNumber) {
		this._minSegmentNumber = segNumber;

	}

	public void SetNextBacktrackSegement(LLGridSegment next) {
		this._btnext = next;

	}

	public void SetNexts(LLGridSegment seg0, LLGridSegment seg1) {
		this._next0 = seg0;
		this._next1 = seg1;

	}

	public void SetObstacle(Integer obstacleStartIndex, Integer obstacleEndIndex) {

		if ((!this.IsInvalid() && (obstacleStartIndex <= this._endIndex))
				&& (obstacleEndIndex >= this._startIndex)) {
			if (obstacleStartIndex <= this._startIndex) {
				if (this._endIndex <= obstacleEndIndex) {
					this._startIndex = -1;
					this._endIndex = -1;
				} else {
					this._startIndex = obstacleEndIndex + 1;
				}
			} else if (this._endIndex <= obstacleEndIndex) {
				this._endIndex = obstacleStartIndex - 1;
			} else {
				LLGridSegment segment = new LLGridSegment(this._gridLine,
						obstacleEndIndex + 1, this._endIndex, this._next1);
				this._next1 = segment;
				this._endIndex = obstacleStartIndex - 1;
			}
		}

	}

	public void SetStartIndex(Integer index) {
		this._startIndex = index;

	}

	public void ShiftIndex(Integer delta) {

		if (!this.IsInvalid()) {
			this._startIndex += delta;
			this._endIndex += delta;
			if (this._btStartIndex != 0x7fffffff) {
				this._btStartIndex += delta;
			}
			if (this._btEndIndex != -1) {
				this._btEndIndex += delta;
			}
		}
		if (this._next0 != null) {
			this._next0.ShiftIndex(delta);
		}
		if (this._next1 != null) {
			this._next1.ShiftIndex(delta);
		}

	}

	public void UpdateMinSegmentNumber(Integer segNumber) {
		if (segNumber < this._minSegmentNumber) {
			this._minSegmentNumber = segNumber;
		}

	}

}