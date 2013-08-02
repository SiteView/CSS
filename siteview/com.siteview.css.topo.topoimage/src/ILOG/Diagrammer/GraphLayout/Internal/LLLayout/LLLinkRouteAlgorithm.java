package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;
import system.Collections.*;

public final class LLLinkRouteAlgorithm {
	private Integer _backtrackDist;

	private LLGridSegment _backtrackSegments;

	private LLTerminationPoint[] _bottomPoints;

	private Integer _crossingsInStraightOrOneBendRoute;

	private LLGridSegment _dummySegment;

	private long _elapseTime;

	private Integer[] _endIndex;

	private LLGridSegment _endSegment;

	private Integer _firstValidBacktrackDist;

	private Integer _forbiddenMaxIndexAtEnd;

	private Integer _forbiddenMaxIndexAtStart;

	private Integer _forbiddenMinIndexAtEnd;

	private Integer _forbiddenMinIndexAtStart;

	private LLGrid _grid;

	private LongLinkLayout _layout;

	private LLTerminationPoint[] _leftPoints;

	private Integer _maxBacktrackDist;

	private Integer _minAdditionalBacktrackDist = 5;

	private ArrayList _points;

	private LLTerminationPoint[] _rightPoints;

	private Boolean _searchExhaustive = false;

	private Integer[] _startIndex;

	private LLGridSegment _startSegment;

	private LLTerminationPoint[] _topPoints;

	private static Integer MAXVAL = 0x7fffffff;

	public LLLinkRouteAlgorithm(LongLinkLayout layout, LLGrid grid) {
		this._layout = layout;
		this._grid = grid;
		this._startIndex = new Integer[2];
		this._endIndex = new Integer[2];
		this._points = new ArrayList(3);
		this._dummySegment = new LLGridSegment(null, 0, 0, null);
		this._searchExhaustive = layout.get_ExhaustiveSearching();
		this._maxBacktrackDist = layout.get_MaxBacktrack();
	}

	public void Dispose() {
		this._points = null;
		this._dummySegment.Dispose();
		this._topPoints = null;
		this._bottomPoints = null;
		this._leftPoints = null;
		this._rightPoints = null;
		this._grid = null;

	}

	private void DivideTerminationPointsBySide(LLTerminationPoint[] points) {
		Integer num = null;
		Integer num2 = 0;
		Integer num3 = 0;
		Integer num4 = 0;
		Integer num5 = 0;
		for (num = 0; num < points.length; num++) {
			if (points[num].GetSide() == 0) {
				num4++;
				// NOTICE: break ignore!!!
			} else if (points[num].GetSide() == 1) {
				num5++;
				// NOTICE: break ignore!!!
			} else if (points[num].GetSide() == 2) {
				num2++;
				// NOTICE: break ignore!!!
			} else if (points[num].GetSide() == 3) {
				num3++;
				// NOTICE: break ignore!!!
			}
		}
		this._topPoints = new LLTerminationPoint[num2];
		this._bottomPoints = new LLTerminationPoint[num3];
		this._leftPoints = new LLTerminationPoint[num4];
		this._rightPoints = new LLTerminationPoint[num5];
		num2 = 0;
		num3 = 0;
		num4 = 0;
		num5 = 0;
		for (num = 0; num < points.length; num++) {
			if (points[num].GetSide() == 0) {
				this._leftPoints[num4++] = points[num];
				// NOTICE: break ignore!!!
			} else if (points[num].GetSide() == 1) {
				this._rightPoints[num5++] = points[num];
				// NOTICE: break ignore!!!
			} else if (points[num].GetSide() == 2) {
				this._topPoints[num2++] = points[num];
				// NOTICE: break ignore!!!
			} else if (points[num].GetSide() == 3) {
				this._bottomPoints[num3++] = points[num];
				// NOTICE: break ignore!!!
			}
		}

	}

	private LLGrid GetGrid() {

		return this._grid;

	}

	private LongLinkLayout GetLayout() {

		return this._layout;

	}

	private ArrayList GetPoints() {

		return this._points;

	}

	private Boolean IsForbidden(Integer index, LLGridSegment segment) {

		return ((((segment == this._startSegment) && (index >= this._forbiddenMinIndexAtStart)) && (index <= this._forbiddenMaxIndexAtStart)) || (((segment == this._endSegment) && (index >= this._forbiddenMinIndexAtEnd)) && (index <= this._forbiddenMaxIndexAtEnd)));

	}

	private Boolean IterateAndRouteBestStraight(
			LLTerminationPointIterator iter, LLLink link) {
		Boolean flag = false;
		Integer startPointIndex = 0;
		Integer endPointIndex = 0;
		Integer mAXVAL = MAXVAL;
		Integer num5 = MAXVAL;
		LLTerminationPoint startPoint = null;
		LLTerminationPoint endPoint = null;
		Integer num6 = 0;
		Integer num7 = 0;

		while (iter.HasNext()) {
			LLTerminationPoint point = iter.GetStartPoint();
			LLTerminationPoint point2 = iter.GetEndPoint();
			Integer num4 = (int) Math.Max(point.GetPenalty(),
					point2.GetPenalty());
			if (num4 > num5) {
				break;
			}
			num5 = num4;

			if (this.SearchStraightRoute(point, point2)) {
				Integer index = 0;
				while (index < link.GetStartPoints().length) {
					if (link.GetStartPoints()[index] == point) {
						num6 = index;
						break;
					}
					index++;
				}
				for (index = 0; index < link.GetEndPoints().length; index++) {
					if (link.GetEndPoints()[index] == point2) {
						num7 = index;
						break;
					}
				}
				if (this._crossingsInStraightOrOneBendRoute == 0) {
					link.SetActStartAndEndCandidates(num6, num7);
					link.StoreRoute(null);

					return true;
				}
				if (this._crossingsInStraightOrOneBendRoute < mAXVAL) {
					mAXVAL = this._crossingsInStraightOrOneBendRoute;
					startPointIndex = num6;
					endPointIndex = num7;
					startPoint = point;
					endPoint = point2;
					flag = true;
				}
			}
			iter.Next();
			if (TranslateUtil.CurrentTimeMillis() > this._elapseTime) {
				break;
			}
		}
		if (flag) {
			this.SearchStraightRoute(startPoint, endPoint);
			link.SetActStartAndEndCandidates(startPointIndex, endPointIndex);
			link.StoreRoute(null);
		}

		return flag;

	}

	private Boolean NeedNewSearch(LLLink link, LLTerminationPoint newPoint,
			LLTerminationPoint oldPoint) {

		if (link.IsSelfLoop()) {

			return true;
		}
		if (oldPoint == null) {

			return true;
		}
		if (this._maxBacktrackDist < 20) {

			return true;
		}
		Boolean flag = true;
		Integer num = this._maxBacktrackDist;
		Integer num2 = this._minAdditionalBacktrackDist;
		Boolean flag2 = this._searchExhaustive;
		this._maxBacktrackDist = 10;
		this._minAdditionalBacktrackDist = 0;
		this._searchExhaustive = false;

		if (this.SearchRoute(newPoint, oldPoint, false)) {
			flag = false;
		}
		this._maxBacktrackDist = num;
		this._minAdditionalBacktrackDist = num2;
		this._searchExhaustive = flag2;

		return flag;

	}

	private Boolean NonzeroRoute(LLTerminationPoint p1, LLTerminationPoint p2,
			Boolean p1IsLast, Boolean p2IsLast) {
		if ((p1.GetIndex(0) == p2.GetIndex(0))
				&& (p1.GetIndex(1) == p2.GetIndex(1))) {
			if (!p1IsLast) {

				return false;
			}
			if (!p2IsLast) {

				return false;
			}
		}

		return true;

	}

	public Boolean Route(LLLink link) {
		LongLinkLayout layout = this.GetLayout();
		this._elapseTime = TranslateUtil.CurrentTimeMillis()
				+ layout.get_AllowedTimePerLink();
		link.CleanRoute();
		this._points.Clear();
		if (layout.get_StraightRouteEnabled() && this.RouteBestOneBend(link)) {

			return true;
		}
		if (link.IsSelfLoop() && this.RouteSelfLoopOptimized(link)) {

			return true;
		}
		LLTerminationPoint[] startPoints = link.GetStartPoints();
		LLTerminationPoint[] endPoints = link.GetEndPoints();
		Integer length = startPoints.length;
		Integer num2 = endPoints.length;
		Integer index = 0;
		Integer num4 = 0;
		LLTerminationPoint oldPoint = null;
		LLTerminationPoint point4 = null;
		while (true) {
			LLTerminationPoint point = null;
			LLTerminationPoint point2 = null;
			Boolean flag = false;
			Boolean flag2 = false;
			Integer num5 = null;
			do {
				if (index >= length) {
					if (num4 >= num2) {

						return false;
					}
					point2 = endPoints[num4];
					num4++;
					flag2 = num4 == num2;
					if ((index > 0) && this.NeedNewSearch(link, point2, point4)) {
						oldPoint = null;
						for (num5 = 0; num5 < index; num5++) {
							point = startPoints[num5];
							flag = num5 == (length - 1);
							if (this.NeedNewSearch(link, point, oldPoint)
									&& this.NonzeroRoute(point, point2, flag,
											flag2)) {
								oldPoint = point;
								point4 = point2;

								if (this.SearchRoute(point, point2, true)) {
									link.SetActStartAndEndCandidates(num5,
											num4 - 1);
									link.StoreRoute(this.GetPoints());

									return true;
								}
							}
						}
					}
					continue;
				}
				if (num4 >= num2) {
					point = startPoints[index];
					index++;
					flag = index == length;
					if ((num4 > 0) && this.NeedNewSearch(link, point, oldPoint)) {
						point4 = null;
						for (num5 = 0; num5 < num4; num5++) {
							point2 = endPoints[num5];
							flag2 = num5 == (num2 - 1);
							if (this.NeedNewSearch(link, point2, point4)
									&& this.NonzeroRoute(point, point2, flag,
											flag2)) {
								oldPoint = point;
								point4 = point2;

								if (this.SearchRoute(point, point2, true)) {
									link.SetActStartAndEndCandidates(index - 1,
											num5);
									link.StoreRoute(this.GetPoints());

									return true;
								}
							}
						}
					}
					continue;
				}
				if (startPoints[index].GetPenalty() >= endPoints[num4]
						.GetPenalty()) {
					point2 = endPoints[num4];
					num4++;
					flag2 = num4 == num2;
					if ((index > 0) && this.NeedNewSearch(link, point2, point4)) {
						oldPoint = null;
						for (num5 = 0; num5 < index; num5++) {
							point = startPoints[num5];
							flag = num5 == (length - 1);
							if (this.NeedNewSearch(link, point, oldPoint)
									&& this.NonzeroRoute(point, point2, flag,
											flag2)) {
								oldPoint = point;
								point4 = point2;

								if (this.SearchRoute(point, point2, true)) {
									link.SetActStartAndEndCandidates(num5,
											num4 - 1);
									link.StoreRoute(this.GetPoints());

									return true;
								}
							}
						}
					}
					continue;
				}
				point = startPoints[index];
				index++;
				flag = index == length;

			} while ((num4 <= 0) || !this.NeedNewSearch(link, point, oldPoint));
			point4 = null;
			for (num5 = 0; num5 < num4; num5++) {
				point2 = endPoints[num5];
				flag2 = num5 == (num2 - 1);
				if (this.NeedNewSearch(link, point2, point4)
						&& this.NonzeroRoute(point, point2, flag, flag2)) {
					oldPoint = point;
					point4 = point2;

					if (this.SearchRoute(point, point2, true)) {
						link.SetActStartAndEndCandidates(index - 1, num5);
						link.StoreRoute(this.GetPoints());

						return true;
					}
				}
			}
		}

	}

	private Boolean RouteBestOneBend(LLLink link) {

		if (link.IsSelfLoop()) {

			return false;
		}
		Boolean flag = false;
		Integer startPointIndex = 0;
		Integer endPointIndex = 0;
		Integer mAXVAL = MAXVAL;
		Integer num5 = MAXVAL;
		LLTerminationPointIterator iterator = new LLTerminationPointIterator(
				link);
		LLTerminationPoint startPoint = null;
		LLTerminationPoint endPoint = null;

		while (iterator.HasNext()) {
			LLTerminationPoint point = iterator.GetStartPoint();
			LLTerminationPoint point2 = iterator.GetEndPoint();
			Integer num4 = (int) Math.Max(point.GetPenalty(),
					point2.GetPenalty());
			if (num4 > num5) {
				break;
			}
			num5 = num4;

			if (this.SearchOneBendRoute(point, point2)) {
				if (this._crossingsInStraightOrOneBendRoute == 0) {
					link.SetActStartAndEndCandidates(
							iterator.GetStartPointIndex(),
							iterator.GetEndPointIndex());
					link.StoreRoute(this.GetPoints());

					return true;
				}
				if (this._crossingsInStraightOrOneBendRoute < mAXVAL) {
					mAXVAL = this._crossingsInStraightOrOneBendRoute;

					startPointIndex = iterator.GetStartPointIndex();

					endPointIndex = iterator.GetEndPointIndex();
					startPoint = point;
					endPoint = point2;
					flag = true;
				}
			}
			iterator.Next();
			if (TranslateUtil.CurrentTimeMillis() > this._elapseTime) {
				break;
			}
		}
		if (flag) {
			this.SearchOneBendRoute(startPoint, endPoint);
			link.SetActStartAndEndCandidates(startPointIndex, endPointIndex);
			link.StoreRoute(this.GetPoints());
		}

		return flag;

	}

	private Boolean RouteBestStraight(LLLink link) {
		LLTerminationPointIterator iterator = null;

		if (link.IsSelfLoop()) {

			return false;
		}
		if ((link.GetStartPoints().length > 3)
				&& (link.GetEndPoints().length > 3)) {
			this.DivideTerminationPointsBySide(link.GetStartPoints());
			LLTerminationPoint[] s = this._topPoints;
			LLTerminationPoint[] pointArray2 = this._bottomPoints;
			LLTerminationPoint[] pointArray3 = this._leftPoints;
			LLTerminationPoint[] pointArray4 = this._rightPoints;
			this.DivideTerminationPointsBySide(link.GetEndPoints());
			LLTerminationPoint[] e = this._topPoints;
			LLTerminationPoint[] pointArray6 = this._bottomPoints;
			LLTerminationPoint[] pointArray7 = this._leftPoints;
			LLTerminationPoint[] pointArray8 = this._rightPoints;
			iterator = new LLTerminationPointIterator(s, pointArray6);

			if (this.IterateAndRouteBestStraight(iterator, link)) {

				return true;
			}
			iterator = new LLTerminationPointIterator(pointArray2, e);

			if (this.IterateAndRouteBestStraight(iterator, link)) {

				return true;
			}
			iterator = new LLTerminationPointIterator(pointArray3, pointArray8);

			if (this.IterateAndRouteBestStraight(iterator, link)) {

				return true;
			}
			iterator = new LLTerminationPointIterator(pointArray4, pointArray7);

			return this.IterateAndRouteBestStraight(iterator, link);
		}
		iterator = new LLTerminationPointIterator(link);

		return this.IterateAndRouteBestStraight(iterator, link);

	}

	private Boolean RouteSelfLoopOptimized(LLLink link) {
		LLTerminationPoint[] startPoints = link.GetStartPoints();
		LLTerminationPoint[] endPoints = link.GetEndPoints();
		Integer length = startPoints.length;
		Integer num2 = endPoints.length;
		if ((length != 0) && (num2 != 0)) {
			if ((length == 1) && (num2 == 1)) {

				return false;
			}
			Integer index = 0;
			Integer num4 = 0;
			Integer penalty = startPoints[0].GetPenalty();
			Integer num7 = endPoints[0].GetPenalty();
			for (index = 0; index < length; index++) {
				LLTerminationPoint point = startPoints[index];
				if (point.GetPenalty() > penalty) {
					break;
				}
				for (num4 = 0; num4 < num2; num4++) {
					LLTerminationPoint point2 = endPoints[num4];
					if (point2.GetPenalty() > num7) {
						break;
					}
					if (point.GetSide() == point2.GetSide()) {
						Integer num5 = null;
						if ((point.GetSide() == 2) || (point.GetSide() == 3)) {
							num5 = 0;
						} else {
							num5 = 1;
						}
						if ((((point.GetIndex(num5) + 1) == point2
								.GetIndex(num5)) && this.NonzeroRoute(point,
								point2, false, false))
								&& this.SearchRoute(point, point2, true)) {
							link.SetActStartAndEndCandidates(index, num4);
							link.StoreRoute(this.GetPoints());

							return true;
						}
					}
				}
			}
		}

		return false;

	}

	public Boolean RouteStraight(LLLink link) {
		LongLinkLayout layout = this.GetLayout();
		this._elapseTime = TranslateUtil.CurrentTimeMillis()
				+ layout.get_AllowedTimePerLink();
		link.CleanRoute();
		this._points.Clear();

		return this.RouteBestStraight(link);

	}

	private void SearchBackward(LLGridSegment segment, Integer gridPointIndex,
			Integer dir) {
		Integer index = segment.GetGridLine().GetIndex();
		if (segment != this._startSegment) {
			LLGrid grid = this.GetGrid();
			Integer num2 = gridPointIndex;
			Integer num3 = gridPointIndex + 1;
			Integer startIndex = segment.GetStartIndex();
			Integer endIndex = segment.GetEndIndex();
			Integer num7 = -1;
			Integer mAXVAL = MAXVAL;
			LLGridSegment segment2 = null;
			while ((num2 >= startIndex) || (num3 <= endIndex)) {
				Integer minSegmentNumber = null;
				LLGridSegment segment3 = null;

				if ((num2 >= startIndex) && !this.IsForbidden(num2, segment)) {

					segment3 = segment.SearchOrthogonalSegment(grid, num2);
					if (segment3 != null) {

						minSegmentNumber = segment3.GetMinSegmentNumber();

						if (!this.IsForbidden(index, segment3)
								&& (minSegmentNumber < mAXVAL)) {
							num7 = num2;
							mAXVAL = minSegmentNumber;
							segment2 = segment3;
						}
					}
				}

				if ((num3 <= endIndex) && !this.IsForbidden(num3, segment)) {

					segment3 = segment.SearchOrthogonalSegment(grid, num3);
					if (segment3 != null) {

						minSegmentNumber = segment3.GetMinSegmentNumber();

						if (!this.IsForbidden(index, segment3)
								&& (minSegmentNumber < mAXVAL)) {
							num7 = num3;
							mAXVAL = minSegmentNumber;
							segment2 = segment3;
						}
					}
				}
				num2--;
				num3++;
			}
			if (dir == 1) {
				this._points
						.Add(new InternalPoint((float) index, (float) num7));
			} else {
				this._points
						.Add(new InternalPoint((float) num7, (float) index));
			}
			this.SearchBackward(segment2, index, 1 - dir);
		}

	}

	private Boolean SearchForward(LLGridSegment segment,
			Integer gridPointIndex, Integer dir, Integer minSegmentNr) {

		if (this.IsForbidden(gridPointIndex, segment)) {

			return false;
		}
		if (segment == null) {

			return false;
		}
		LLGrid grid = this.GetGrid();
		LLGridLine gridLine = segment.GetGridLine();
		Integer index = gridLine.GetIndex();
		Integer startIndex = segment.GetStartIndex();
		Integer endIndex = segment.GetEndIndex();
		if ((gridPointIndex < startIndex) || (gridPointIndex > endIndex)) {

			return false;
		}
		Integer num4 = -1;
		Integer num5 = -1;
		Integer num6 = this._endIndex[dir];
		Integer num7 = segment.CalcBacktrackDist(gridPointIndex);
		Integer backtrackStartIndex = segment.GetBacktrackStartIndex();
		Integer backtrackEndIndex = segment.GetBacktrackEndIndex();
		if (num7 >= this._backtrackDist) {
			if (gridPointIndex <= this._endIndex[dir]) {
				num4 = gridPointIndex;
				num5 = (num6 < endIndex) ? num6 : endIndex;
			} else {
				num4 = (num6 > startIndex) ? num6 : startIndex;
				num5 = gridPointIndex;
			}
			if (num7 == MAXVAL) {
				segment.SetBacktrackStartIndex(num4);
				segment.SetBacktrackEndIndex(num5);
			} else {
				if (num4 < backtrackStartIndex) {
					segment.SetBacktrackStartIndex(num4);
				} else {
					num4 = backtrackEndIndex + 1;
				}
				if (num5 > backtrackEndIndex) {
					segment.SetBacktrackEndIndex(num5);
				} else {
					num5 = backtrackStartIndex - 1;
				}
			}
			segment.UpdateMinSegmentNumber(minSegmentNr);
			segment.SetBacktrackDist(this._backtrackDist);
			grid.MarkDirty(gridLine);
		}
		if (segment == this._endSegment) {

			return !this.IsForbidden(gridPointIndex, segment);
		}

		if (segment.CanBacktrack() && !segment.IsOnBacktrackStack()) {
			segment.SetNextBacktrackSegement(this._backtrackSegments);
			this._backtrackSegments = segment;
		}
		Boolean flag = false;
		if (((num4 >= 0) && (num5 >= 0)) && (num4 <= num5)) {
			LLGridSegment segment2 = null;
			minSegmentNr = segment.GetMinSegmentNumber() + 1;
			if (num6 <= num4) {
				for (Integer j = num4; j <= num5; j++) {

					if (!this.IsForbidden(j, segment)) {

						segment2 = segment.SearchOrthogonalSegment(grid, j);

						flag |= this.SearchForward(segment2, index, 1 - dir,
								minSegmentNr);
						if (flag && !this._searchExhaustive) {

							return flag;
						}
					}
				}

				return flag;
			}
			if (num6 >= num5) {
				for (Integer k = num5; k >= num4; k--) {

					if (!this.IsForbidden(k, segment)) {

						segment2 = segment.SearchOrthogonalSegment(grid, k);

						flag |= this.SearchForward(segment2, index, 1 - dir,
								minSegmentNr);
						if (flag && !this._searchExhaustive) {

							return flag;
						}
					}
				}

				return flag;
			}
			Integer num12 = num6;
			for (Integer i = num6 + 1; (num12 >= num4) || (i <= num5); i++) {

				if ((num12 >= num4) && !this.IsForbidden(num12, segment)) {

					segment2 = segment.SearchOrthogonalSegment(grid, num12);

					flag |= this.SearchForward(segment2, index, 1 - dir,
							minSegmentNr);
					if (flag && !this._searchExhaustive) {

						return flag;
					}
				}

				if ((i <= num5) && !this.IsForbidden(i, segment)) {

					segment2 = segment.SearchOrthogonalSegment(grid, i);

					flag |= this.SearchForward(segment2, index, 1 - dir,
							minSegmentNr);
					if (flag && !this._searchExhaustive) {

						return flag;
					}
				}
				num12--;
			}
		}

		return flag;

	}

	private Boolean SearchForwardWithBacktrack(LLGridSegment startSegment,
			Integer startPointIndex, Integer dir) {
		this._firstValidBacktrackDist = -1;
		this._backtrackDist = 0;
		this._backtrackSegments = this._dummySegment;
		Boolean flag = this
				.SearchForward(startSegment, startPointIndex, dir, 0);
		if (flag) {
			this._firstValidBacktrackDist = 0;
		}
		if ((!flag || this._searchExhaustive)
				|| (this._minAdditionalBacktrackDist != 0)) {
			while ((this._backtrackDist < this._maxBacktrackDist)
					&& (TranslateUtil.CurrentTimeMillis() <= this._elapseTime)) {
				this._backtrackDist++;
				LLGridSegment nextBacktrackSegement = this._backtrackSegments;
				LLGridSegment segment2 = null;
				Integer num = 0;
				while (nextBacktrackSegement != this._dummySegment) {

					if (!nextBacktrackSegement.CanBacktrack()) {
						if (segment2 == null) {

							this._backtrackSegments = nextBacktrackSegement
									.GetNextBacktrackSegement();
							nextBacktrackSegement
									.SetNextBacktrackSegement(null);
							nextBacktrackSegement = this._backtrackSegments;
						} else {
							segment2.SetNextBacktrackSegement(nextBacktrackSegement
									.GetNextBacktrackSegement());
							nextBacktrackSegement
									.SetNextBacktrackSegement(null);

							nextBacktrackSegement = segment2
									.GetNextBacktrackSegement();
						}
					} else {
						num++;

						nextBacktrackSegement = nextBacktrackSegement
								.GetNextBacktrackSegement();
					}
				}
				if (num == 0) {

					return flag;
				}
				LLGridSegment[] segmentArray = new LLGridSegment[num];
				Integer[] numArray = new Integer[num];
				Integer[] numArray2 = new Integer[num];
				Integer index = 0;
				nextBacktrackSegement = this._backtrackSegments;
				while (nextBacktrackSegement != this._dummySegment) {
					LLGridSegment segment3 = nextBacktrackSegement
							.GetNextBacktrackSegement();
					nextBacktrackSegement.SetNextBacktrackSegement(null);
					segmentArray[index] = nextBacktrackSegement;
					numArray[index] = nextBacktrackSegement
							.GetBacktrackStartIndex() - 1;
					numArray2[index] = nextBacktrackSegement
							.GetBacktrackEndIndex() + 1;
					index++;
					nextBacktrackSegement = segment3;
				}
				this._backtrackSegments = this._dummySegment;
				for (index = 0; index < num; index++) {
					nextBacktrackSegement = segmentArray[index];
					dir = nextBacktrackSegement.IsHorizontal() ? 0 : 1;
					if ((nextBacktrackSegement.CanBacktrack() && (nextBacktrackSegement
							.GetStartIndex() <= numArray[index]))
							&& (nextBacktrackSegement.GetBacktrackStartIndex() > numArray[index])) {

						flag |= this.SearchForward(nextBacktrackSegement,
								numArray[index], dir, MAXVAL);
						if (flag) {
							if (this._firstValidBacktrackDist == -1) {
								this._firstValidBacktrackDist = this._backtrackDist;
							}
							if (!this._searchExhaustive
									&& ((this._firstValidBacktrackDist + this._minAdditionalBacktrackDist) <= this._backtrackDist)) {
								break;
							}
						}
					}
					if ((nextBacktrackSegement.CanBacktrack() && (nextBacktrackSegement
							.GetEndIndex() >= numArray2[index]))
							&& (nextBacktrackSegement.GetBacktrackEndIndex() < numArray2[index])) {

						flag |= this.SearchForward(nextBacktrackSegement,
								numArray2[index], dir, MAXVAL);
						if (flag) {
							if (this._firstValidBacktrackDist == -1) {
								this._firstValidBacktrackDist = this._backtrackDist;
							}
							if (!this._searchExhaustive
									&& ((this._firstValidBacktrackDist + this._minAdditionalBacktrackDist) <= this._backtrackDist)) {
								break;
							}
						}
					}
				}
				if ((flag && !this._searchExhaustive)
						&& ((this._firstValidBacktrackDist + this._minAdditionalBacktrackDist) <= this._backtrackDist)) {

					return flag;
				}
			}

			return flag;
		}

		return true;

	}

	private Boolean SearchOneBendRoute(LLTerminationPoint startPoint,
			LLTerminationPoint endPoint) {
		LLGridSegment segment = startPoint.GetSegment();
		LLGridSegment segment2 = endPoint.GetSegment();
		if (segment == null) {

			return false;
		}
		if (segment2 == null) {

			return false;
		}
		LLGridLine gridLine = segment.GetGridLine();
		LLGridLine line2 = segment2.GetGridLine();
		if (gridLine.IsHorizontal() == line2.IsHorizontal()) {

			return false;
		}
		Integer index = line2.GetIndex();
		Integer end = gridLine.GetIndex();
		if ((segment.GetStartIndex() > index)
				|| (segment.GetEndIndex() < index)) {

			return false;
		}
		if ((segment2.GetStartIndex() > end) || (segment2.GetEndIndex() < end)) {

			return false;
		}
		if ((startPoint.GetForbiddenMinIndex() <= index)
				&& (startPoint.GetForbiddenMaxIndex() >= index)) {

			return false;
		}
		if ((endPoint.GetForbiddenMinIndex() <= end)
				&& (endPoint.GetForbiddenMaxIndex() >= end)) {

			return false;
		}
		this._points.Clear();

		if (segment.IsHorizontal()) {
			this._points.Add(new InternalPoint((float) index, (float) end));
		} else {
			this._points.Add(new InternalPoint((float) end, (float) index));
		}
		Integer start = startPoint.GetIndex(gridLine.IsHorizontal() ? 0 : 1);

		this._crossingsInStraightOrOneBendRoute = gridLine.CalcApproxCrossings(
				this.GetGrid(), start, index);

		start = endPoint.GetIndex(line2.IsHorizontal() ? 0 : 1);
		this._crossingsInStraightOrOneBendRoute += line2.CalcApproxCrossings(
				this.GetGrid(), start, end);

		return true;

	}

	private Boolean SearchRoute(LLTerminationPoint startPoint,
			LLTerminationPoint endPoint, Boolean storeBends) {
		this._grid.ClearDirty();
		this._points.Clear();

		this._startIndex[0] = startPoint.GetIndex(0);

		this._startIndex[1] = startPoint.GetIndex(1);

		this._endIndex[0] = endPoint.GetIndex(0);

		this._endIndex[1] = endPoint.GetIndex(1);
		Integer dir = 1;
		Integer num2 = 1;

		this._startSegment = startPoint.GetSegment();

		this._endSegment = endPoint.GetSegment();
		if (this._startSegment == null) {

			return false;
		}
		if (this._endSegment == null) {

			return false;
		}

		this._forbiddenMinIndexAtStart = startPoint.GetForbiddenMinIndex();

		this._forbiddenMaxIndexAtStart = startPoint.GetForbiddenMaxIndex();

		this._forbiddenMinIndexAtEnd = endPoint.GetForbiddenMinIndex();

		this._forbiddenMaxIndexAtEnd = endPoint.GetForbiddenMaxIndex();

		if (this._startSegment.IsHorizontal()) {
			dir = 0;
		}

		if (this._endSegment.IsHorizontal()) {
			num2 = 0;
		}
		if (this._startSegment != this._endSegment) {
			Integer startIndex = this._startSegment.GetStartIndex();
			Integer endIndex = this._startSegment.GetEndIndex();
			if ((this._forbiddenMaxIndexAtStart >= startIndex)
					&& (this._forbiddenMinIndexAtStart <= endIndex)) {
				if (startIndex < this._forbiddenMinIndexAtStart) {
					this._startSegment
							.SetBacktrackStartIndex(this._forbiddenMinIndexAtStart);
				} else {
					this._startSegment.SetBacktrackStartIndex(startIndex);
				}
				if (endIndex > this._forbiddenMaxIndexAtStart) {
					this._startSegment
							.SetBacktrackEndIndex(this._forbiddenMaxIndexAtStart);
				} else {
					this._startSegment.SetBacktrackEndIndex(endIndex);
				}
				this._startSegment.SetBacktrackDist(0);
				this._grid.MarkDirty(this._startSegment.GetGridLine());
			}

			startIndex = this._endSegment.GetStartIndex();

			endIndex = this._endSegment.GetEndIndex();
			if ((this._forbiddenMaxIndexAtEnd >= startIndex)
					&& (this._forbiddenMinIndexAtEnd <= endIndex)) {
				if (startIndex < this._forbiddenMinIndexAtEnd) {
					this._endSegment
							.SetBacktrackStartIndex(this._forbiddenMinIndexAtEnd);
				} else {
					this._endSegment.SetBacktrackStartIndex(startIndex);
				}
				if (endIndex > this._forbiddenMaxIndexAtEnd) {
					this._endSegment
							.SetBacktrackEndIndex(this._forbiddenMaxIndexAtEnd);
				} else {
					this._endSegment.SetBacktrackEndIndex(endIndex);
				}
				this._endSegment.SetBacktrackDist(0);
				this._grid.MarkDirty(this._endSegment.GetGridLine());
			}

			if (!this.SearchForwardWithBacktrack(this._startSegment,
					this._startIndex[dir], dir)) {

				return false;
			}
			if (storeBends) {
				this._endSegment.SetMinSegmentNumber(MAXVAL);
				this._endSegment.SetBacktrackDist(MAXVAL);
				this.SearchBackward(this._endSegment, this._endIndex[num2],
						num2);
			}
		}

		return true;

	}

	private Boolean SearchStraightRoute(LLTerminationPoint startPoint,
			LLTerminationPoint endPoint) {
		LLGridSegment segment = startPoint.GetSegment();
		LLGridSegment segment2 = endPoint.GetSegment();
		if ((segment == segment2) && (segment != null)) {
			this._points.Clear();
			LLGridLine gridLine = segment.GetGridLine();

			this._crossingsInStraightOrOneBendRoute = gridLine
					.CalcApproxCrossings(this.GetGrid(), startPoint
							.GetIndex(gridLine.IsHorizontal() ? 0 : 1),
							endPoint.GetIndex(gridLine.IsHorizontal() ? 0 : 1));

			return true;
		}

		return false;

	}

	public final class LLTerminationPointIterator {
		private Integer actEPIndex;

		private Integer actSPIndex;

		private LLTerminationPoint[] endPointCand;

		private Boolean iterateAtStart = false;

		private Integer maxEPIndex;

		private Integer maxSPIndex;

		private Integer numberEndPointCands;

		private Integer numberStartPointCands;

		private LLTerminationPoint[] startPointCand;

		public LLTerminationPointIterator(LLLink link) {
			this(link.GetStartPoints(), link.GetEndPoints());
		}

		public LLTerminationPointIterator(LLTerminationPoint[] s,
				LLTerminationPoint[] e) {
			this.startPointCand = s;
			this.endPointCand = e;
			this.numberStartPointCands = this.startPointCand.length;
			this.numberEndPointCands = this.endPointCand.length;
			this.maxSPIndex = 0;
			this.maxEPIndex = 0;
			this.actSPIndex = 0;
			this.actEPIndex = 0;
			this.iterateAtStart = true;
		}

		public LLTerminationPoint GetEndPoint() {

			return this.endPointCand[this.actEPIndex];

		}

		public Integer GetEndPointIndex() {

			return this.actEPIndex;

		}

		public LLTerminationPoint GetStartPoint() {

			return this.startPointCand[this.actSPIndex];

		}

		public Integer GetStartPointIndex() {

			return this.actSPIndex;

		}

		public Boolean HasNext() {

			return ((this.maxSPIndex < this.numberStartPointCands) && (this.maxEPIndex < this.numberEndPointCands));

		}

		private void IncrementMaxStartOrEnd() {
			this.maxSPIndex++;
			this.maxEPIndex++;
			if (this.maxSPIndex < this.numberStartPointCands) {
				if (this.maxEPIndex < this.numberEndPointCands) {
					if (this.startPointCand[this.maxSPIndex].GetPenalty() < this.endPointCand[this.maxEPIndex]
							.GetPenalty()) {
						this.actSPIndex = this.maxSPIndex;
						this.iterateAtStart = false;
						this.maxEPIndex--;
						this.actEPIndex = 0;
					} else if (this.startPointCand[this.maxSPIndex]
							.GetPenalty() > this.endPointCand[this.maxEPIndex]
							.GetPenalty()) {
						this.actEPIndex = this.maxEPIndex;
						this.iterateAtStart = true;
						this.maxSPIndex--;
						this.actSPIndex = 0;
					} else if (this.startPointCand[this.maxSPIndex].GetOrder() < this.endPointCand[this.maxEPIndex]
							.GetOrder()) {
						this.actSPIndex = this.maxSPIndex;
						this.iterateAtStart = false;
						this.maxEPIndex--;
						this.actEPIndex = 0;
					} else {
						this.actEPIndex = this.maxEPIndex;
						this.iterateAtStart = true;
						this.maxSPIndex--;
						this.actSPIndex = 0;
					}
				} else {
					this.actSPIndex = this.maxSPIndex;
					this.iterateAtStart = false;
					this.maxEPIndex--;
					this.actEPIndex = 0;
				}
			} else if (this.maxEPIndex < this.numberEndPointCands) {
				this.actEPIndex = this.maxEPIndex;
				this.iterateAtStart = true;
				this.maxSPIndex--;
				this.actSPIndex = 0;
			}

		}

		public void Next() {
			if (this.iterateAtStart) {
				if (this.actSPIndex < this.maxSPIndex) {
					this.actSPIndex++;
				} else {
					this.IncrementMaxStartOrEnd();
				}
			} else if (this.actEPIndex < this.maxEPIndex) {
				this.actEPIndex++;
			} else {
				this.IncrementMaxStartOrEnd();
			}

		}

	}
}