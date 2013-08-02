package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;

public final class LLCrossRedAlgorithm {
	private LLLink _bendLink;

	private Integer[] _bgridCorr = new Integer[2];

	private Integer[] _blimits = new Integer[2];

	private Integer[] _bp0 = new Integer[2];

	private Integer[] _bp1 = new Integer[2];

	private Integer[] _bp2 = new Integer[2];

	public Integer[] _bypassPoint1;

	public Integer[] _bypassPoint2;

	private LLCrossPoint _crossing1;

	private LLCrossPoint _crossing2;

	private Boolean _exchangeAtBendLinkStart = false;

	private Boolean _exchangeAtStraightLinkStart = false;

	private LLGrid _grid;

	private LongLinkLayout _layout;

	private LLCrossPoint _listOfCrossPoints;

	private Boolean _moveStraightToLower = false;

	public Boolean[] _nextObstacleFlags;

	public Boolean[] _oldNextObstacleFlags;

	public Boolean[] _oldPrevObstacleFlags;

	public Boolean[] _otherNextObstacleFlags;

	public Boolean[] _otherPrevObstacleFlags;

	public Boolean[] _prevObstacleFlags;

	public Integer[] _segArray;

	private Integer[] _sgridCorr = new Integer[2];

	private Integer[] _slimits = new Integer[2];

	private Integer[] _sp1 = new Integer[2];

	private Integer[] _sp2 = new Integer[2];

	private LLLink _straightLink;

	public LLCrossRedAlgorithm(LongLinkLayout layout, LLGrid grid) {
		this._layout = layout;
		this._grid = grid;
		this._listOfCrossPoints = null;
		this._bypassPoint1 = new Integer[2];
		this._bypassPoint2 = new Integer[2];
	}

	private Integer CalcCrossings(LLLink link1, LLLink link2) {
		Integer num = 0;
		if (this._listOfCrossPoints != null) {
			this._listOfCrossPoints.Dispose();
		}
		this._listOfCrossPoints = null;
		Integer num2 = 0;
		Integer num3 = 0;
		Integer[] point = new Integer[2];
		Integer[] numArray2 = new Integer[2];
		Integer[] numArray3 = new Integer[2];
		Integer[] numArray4 = new Integer[2];
		Integer num4 = 2;
		link1.GetNextBend(point, 0);
		numArray2[0] = point[0];
		numArray2[1] = point[1];
		link1.GetNextBend(point, 1);
		numArray3[0] = point[0];
		numArray3[1] = point[1];

		while (link1.GetNextBend(point, num4++)) {
			numArray4[0] = point[0];
			numArray4[1] = point[1];

			num += this.CalcSegmentCrossings(link1, num2, num3, numArray2,
					numArray3, numArray4, link2);
			num2++;
			Integer num5 = ((numArray2[0] - numArray3[0]) + numArray2[1])
					- numArray3[1];
			if (num5 < 0) {
				num5 = -num5;
			}
			num3 += num5;
			numArray2[0] = numArray3[0];
			numArray2[1] = numArray3[1];
			numArray3[0] = numArray4[0];
			numArray3[1] = numArray4[1];
		}

		return num;

	}

	private Integer CalcSegmentCrossings(LLLink link1,
			Integer segmentA1A2Index, Integer distToA1, Integer[] pA1,
			Integer[] pA2, Integer[] pA3, LLLink link2) {
		Integer num = 0;
		Integer num2 = 0;
		Integer num3 = 0;
		Integer[] numArray = new Integer[2];
		Integer[] numArray2 = new Integer[2];
		Integer[] numArray3 = new Integer[2];
		Integer[] numArray4 = new Integer[2];
		Integer num4 = 2;
		LLCrossPoint point = new LLCrossPoint();
		Integer[] limit = new Integer[2];
		LLGrid grid = this.GetGrid();
		link2.GetNextBend(numArray, 0);
		numArray2[0] = numArray[0];
		numArray2[1] = numArray[1];
		link2.GetNextBend(numArray, 1);
		numArray3[0] = numArray[0];
		numArray3[1] = numArray[1];

		while (link2.GetNextBend(numArray, num4++)) {
			numArray4[0] = numArray[0];
			numArray4[1] = numArray[1];
			Boolean flag = false;

			if (point.CheckCrossing(segmentA1A2Index, distToA1, pA1, pA2, pA3,
					num2, num3, numArray2, numArray3, numArray4)) {
				Integer horizontalTestSegment = null;
				flag = true;
				if (flag) {

					horizontalTestSegment = point
							.GetHorizontalTestSegment(limit);

					flag = grid.IsWithoutObstacles(0, horizontalTestSegment,
							limit);
				}
				if (flag) {

					horizontalTestSegment = point.GetVerticalTestSegment(limit);

					flag = grid.IsWithoutObstacles(1, horizontalTestSegment,
							limit);
				}
				if (flag) {

					horizontalTestSegment = point
							.GetHorizontalFreeSegment(limit);
					if (this.IsForbidden(horizontalTestSegment, limit, 0, link1)
							|| this.IsForbidden(horizontalTestSegment, limit,
									0, link2)) {
						flag = false;
					}
				}
				if (flag) {

					horizontalTestSegment = point.GetVerticalFreeSegment(limit);
					if (this.IsForbidden(horizontalTestSegment, limit, 1, link1)
							|| this.IsForbidden(horizontalTestSegment, limit,
									1, link2)) {
						flag = false;
					}
				}
			}
			if (flag) {
				num++;
				point.SetNext(this._listOfCrossPoints);
				this._listOfCrossPoints = point;
				point = new LLCrossPoint();
			}
			num2++;
			Integer num5 = ((numArray2[0] - numArray3[0]) + numArray2[1])
					- numArray3[1];
			if (num5 < 0) {
				num5 = -num5;
			}
			num3 += num5;
			numArray2[0] = numArray3[0];
			numArray2[1] = numArray3[1];
			numArray3[0] = numArray4[0];
			numArray3[1] = numArray4[1];
		}
		point.Dispose();

		return num;

	}

	private Integer CalcStraightBendTerminationNumberOfCrossings(
			Integer sTermIndex, Integer bTermIndex, Integer bendSegIndex,
			Integer bendSegLimit1, Integer bendSegLimit2) {
		LLGrid grid = this.GetGrid();
		Integer segmentStartDir = this._straightLink.GetSegmentStartDir();
		Integer dir = 1 - segmentStartDir;
		Integer num3 = 0;
		LLGridLine gridLine = grid.GetGridLine(dir, sTermIndex);
		if (gridLine != null) {

			num3 += gridLine.CalcApproxCrossings(grid, this._slimits[0],
					this._slimits[1]);
		}

		gridLine = grid.GetGridLine(dir, bTermIndex);
		if (gridLine != null) {

			num3 += gridLine.CalcApproxCrossings(grid, this._blimits[0],
					this._blimits[1]);
		}

		gridLine = grid.GetGridLine(segmentStartDir, bendSegIndex);
		if (gridLine != null) {

			num3 += gridLine.CalcApproxCrossings(grid, bendSegLimit1,
					bendSegLimit2);
		}

		return num3;

	}

	private Boolean CanTerminate(LLLink link, LLTerminationPoint p,
			Boolean fromSide) {

		return this.CanTerminate(link, p, fromSide, p.GetIndex(0),
				p.GetIndex(1), 0);

	}

	private Boolean CanTerminate(LLLink link, LLTerminationPoint p,
			Boolean fromSide, Integer coord0, Integer coord1, Integer dir) {
		float gridCoord = 0;
		float num3 = 0;
		LLGrid grid = this.GetGrid();
		Integer num = fromSide ? link.GetActStartPoint().GetPenalty() : link
				.GetActEndPoint().GetPenalty();
		if (fromSide) {
			if (link.GetFromPoint() != null) {

				return false;
			}
		} else if (link.GetToPoint() != null) {

			return false;
		}
		if (dir == 0) {

			gridCoord = grid.GetGridCoord(0, coord0);

			num3 = grid.GetGridCoord(1, coord1);
		} else {

			gridCoord = grid.GetGridCoord(0, coord1);

			num3 = grid.GetGridCoord(1, coord0);
		}

		return (this.GetLayout().GetTerminationPointPenalty(
				link.GetLinkObject(),
				fromSide ? link.GetFromNode() : link.GetToNode(), fromSide,
				gridCoord, num3, p.GetSide(), p.GetPenalty()) == num);

	}

	public Integer CheckExchangeBendAndStraight() {
		Integer num4 = null;
		Integer segmentStartDir = this._straightLink.GetSegmentStartDir();
		Integer index = 1 - segmentStartDir;
		Integer gridIndex = this._bp0[index];
		float minNodeCornerOffset = this.GetLayout().get_MinNodeCornerOffset();
		if (this._exchangeAtStraightLinkStart) {
			num4 = this._slimits[1];

			if (!this._straightLink.IsWithinToNodeRange(index, gridIndex,
					minNodeCornerOffset)) {

				return 0x7fffffff;
			}

			if (!this.CanTerminate(this._bendLink,
					this._straightLink.GetActStartPoint(),
					this._exchangeAtBendLinkStart)) {

				return 0x7fffffff;
			}
		} else {
			num4 = this._slimits[0];

			if (!this._straightLink.IsWithinFromNodeRange(index, gridIndex,
					minNodeCornerOffset)) {

				return 0x7fffffff;
			}

			if (!this.CanTerminate(this._bendLink,
					this._straightLink.GetActEndPoint(),
					this._exchangeAtBendLinkStart)) {

				return 0x7fffffff;
			}
		}

		if (!this.CanTerminate(this._straightLink,
				this._straightLink.GetActStartPoint(), true, gridIndex,
				this._slimits[0], index)) {

			return 0x7fffffff;
		}

		if (!this.CanTerminate(this._straightLink,
				this._straightLink.GetActEndPoint(), false, gridIndex,
				this._slimits[1], index)) {

			return 0x7fffffff;
		}
		LLGrid grid = this.GetGrid();
		Integer[] limits = new Integer[2];
		if (this._bp0[segmentStartDir] < this._bp1[segmentStartDir]) {
			limits[1] = this._bp1[segmentStartDir] + 1;

			limits[0] = (int) Math.Max(limits[1], num4);
		} else {
			limits[1] = this._bp1[segmentStartDir] - 1;

			limits[0] = (int) Math.Min(limits[1], num4);
		}

		if (!grid.IsWithoutObstacles(segmentStartDir, gridIndex, limits)) {

			return 0x7fffffff;
		}
		if (this._sp1[index] < this._bp0[index]) {
			limits[0] = this._sp1[index] + 1;
			limits[1] = this._bp0[index];
		} else {
			limits[0] = this._bp0[index];
			limits[1] = this._sp1[index] - 1;
		}

		if (this.IsForbidden(this._bp1[segmentStartDir], limits, index,
				this._bendLink)) {

			return 0x7fffffff;
		}

		return this.CalcStraightBendTerminationNumberOfCrossings(
				this._bp1[index], this._sp1[index], this._bp1[segmentStartDir],
				this._sp1[index], this._bp2[index]);

	}

	public Integer CheckMoveBend(Integer[] gridIndexResult) {
		InternalRect fromRect = null;
		LLTerminationPoint actStartPoint = null;
		Integer segmentStartDir = this._straightLink.GetSegmentStartDir();
		Integer index = 1 - segmentStartDir;
		Integer bTermIndex = this._bp0[index];
		Integer num5 = -1;
		LLGrid grid = this.GetGrid();
		if (this._exchangeAtBendLinkStart) {

			actStartPoint = this._bendLink.GetActStartPoint();

			fromRect = this._bendLink.GetFromRect();
		} else {

			actStartPoint = this._bendLink.GetActEndPoint();

			fromRect = this._bendLink.GetToRect();
		}
		float num6 = 0f;
		Integer num7 = 0x7fffffff;
		Integer num8 = 0;
		Integer[] limits = new Integer[2];
		Integer num11 = actStartPoint.GetIndex(segmentStartDir);
		float minNodeCornerOffset = this.GetLayout().get_MinNodeCornerOffset();
		Integer num13 = (int) Math.Max(grid.GetMinGridIndexAtSide(fromRect,
				index, minNodeCornerOffset), grid.GetMinGridIndexAtSide(
				fromRect, index, minNodeCornerOffset));
		Integer num14 = (int) Math.Min(grid.GetMaxGridIndexAtSide(fromRect,
				index, minNodeCornerOffset), grid.GetMaxGridIndexAtSide(
				fromRect, index, minNodeCornerOffset));
		for (Integer i = num13; i <= num14; i++) {
			if ((((this._moveStraightToLower && (bTermIndex < i)) && (i < this._bp2[index])) || ((!this._moveStraightToLower && (bTermIndex > i)) && (i > this._bp2[index])))
					&& ((this._bendLink.IsWithinRectRange(fromRect, index, i,
							minNodeCornerOffset) && this.CanTerminate(
							this._bendLink, actStartPoint,
							this._exchangeAtBendLinkStart, i, num11, index)) && grid
							.IsWithoutObstacles(segmentStartDir, i,
									this._blimits))) {
				if (i < bTermIndex) {
					limits[0] = i + 1;
					limits[1] = bTermIndex;
				} else {
					limits[0] = bTermIndex;
					limits[1] = i - 1;
				}

				if (!this.IsForbidden(this._bp1[segmentStartDir], limits,
						index, this._bendLink)) {
					if (num5 == -1) {

						num5 = this
								.CalcStraightBendTerminationNumberOfCrossings(
										this._sp1[index], bTermIndex,
										this._bp1[segmentStartDir],
										this._bp1[index], this._bp2[index]);
					}
					Integer num9 = this
							.CalcStraightBendTerminationNumberOfCrossings(
									this._sp1[index], i,
									this._bp1[segmentStartDir], i,
									this._bp2[index]);
					float num10 = LLTerminationPoint.GetDeviation(grid,
							fromRect, i, index);
					if ((num9 < num5)
							&& ((num9 < num7) || ((num9 == num7) && (num10 < num6)))) {
						num7 = num9;
						num6 = num10;
						num8 = i;
					}
				}
			}
		}
		gridIndexResult[0] = num8;

		return num7;

	}

	public Integer CheckMoveStraight(Integer[] gridIndexResult) {
		Integer segmentStartDir = this._straightLink.GetSegmentStartDir();
		Integer index = 1 - segmentStartDir;
		Integer sTermIndex = this._sp1[index];
		Integer num5 = -1;
		LLGrid grid = this.GetGrid();
		InternalRect fromRect = this._straightLink.GetFromRect();
		InternalRect toRect = this._straightLink.GetToRect();
		LLTerminationPoint actStartPoint = this._straightLink
				.GetActStartPoint();
		LLTerminationPoint actEndPoint = this._straightLink.GetActEndPoint();
		float num6 = 0f;
		Integer num7 = 0x7fffffff;
		Integer num8 = 0;
		float minNodeCornerOffset = this.GetLayout().get_MinNodeCornerOffset();
		Integer num12 = (int) Math.Max(grid.GetMinGridIndexAtSide(fromRect,
				index, minNodeCornerOffset), grid.GetMinGridIndexAtSide(toRect,
				index, minNodeCornerOffset));
		Integer num13 = (int) Math.Min(grid.GetMaxGridIndexAtSide(fromRect,
				index, minNodeCornerOffset), grid.GetMaxGridIndexAtSide(toRect,
				index, minNodeCornerOffset));
		Integer num14 = actStartPoint.GetIndex(segmentStartDir);
		Integer num15 = actEndPoint.GetIndex(segmentStartDir);
		for (Integer i = num12; i <= num13; i++) {
			if ((((i < sTermIndex) && this._moveStraightToLower) || ((i > sTermIndex) && !this._moveStraightToLower))
					&& (((this._straightLink.IsWithinFromNodeRange(index, i,
							minNodeCornerOffset) && this._straightLink
							.IsWithinToNodeRange(index, i, minNodeCornerOffset)) && (this
							.CanTerminate(this._straightLink, actStartPoint,
									true, i, num14, index) && this
							.CanTerminate(this._straightLink, actEndPoint,
									false, i, num15, index))) && grid
							.IsWithoutObstacles(segmentStartDir, i,
									this._slimits))) {
				if (num5 == -1) {

					num5 = this.CalcStraightBendTerminationNumberOfCrossings(
							sTermIndex, this._bp0[index],
							this._bp1[segmentStartDir], this._bp1[index],
							this._bp2[index]);
				}
				Integer num9 = this
						.CalcStraightBendTerminationNumberOfCrossings(i,
								this._bp0[index], this._bp1[segmentStartDir],
								this._bp1[index], this._bp2[index]);
				float num10 = Math.Max(LLTerminationPoint.GetDeviation(grid,
						fromRect, i, index), LLTerminationPoint.GetDeviation(
						grid, toRect, i, index));
				if ((num9 < num5)
						&& ((num9 < num7) || ((num9 == num7) && (num10 < num6)))) {
					num7 = num9;
					num6 = num10;
					num8 = i;
				}
			}
		}
		gridIndexResult[0] = num8;

		return num7;

	}

	public void Dispose() {
		this._grid = null;
		this._bypassPoint1 = null;
		this._bypassPoint2 = null;

	}

	public void ExchangeBendAndStraightLink() {
		LLTerminationPoint actStartPoint = null;
		LLTerminationPoint point3 = null;
		LLTerminationPoint actEndPoint = null;
		Integer num = null;
		Boolean[] prevObstacleFlags = null;
		Integer num2 = null;
		Integer num4 = null;
		LLTerminationPoint p = new LLTerminationPoint();
		if (this._exchangeAtStraightLinkStart) {

			actStartPoint = this._straightLink.GetActStartPoint();

			actEndPoint = this._straightLink.GetActEndPoint();
			num2 = this._slimits[1];
		} else {

			actStartPoint = this._straightLink.GetActEndPoint();

			actEndPoint = this._straightLink.GetActStartPoint();
			num2 = this._slimits[0];
		}
		if (this._exchangeAtBendLinkStart) {

			point3 = this._bendLink.GetActStartPoint();
			num = 1;

			prevObstacleFlags = this._bendLink.GetPrevObstacleFlags();
			num4 = 1;
		} else {

			point3 = this._bendLink.GetActEndPoint();
			num = this._bendLink.GetSegments().length - 2;

			prevObstacleFlags = this._bendLink.GetNextObstacleFlags();
			num4 = -1;
		}
		LLGrid grid = this.GetGrid();
		Integer segmentStartDir = this._straightLink.GetSegmentStartDir();
		Integer index = 1 - segmentStartDir;
		Integer[] limits = new Integer[] { this._sp1[segmentStartDir],
				this._sp2[segmentStartDir] };
		grid.RemoveObstacle(segmentStartDir, this._sp1[index], limits,
				this._straightLink, 0);
		limits[0] = this._bp1[index];
		limits[1] = this._sp1[index];
		grid.RemoveObstacle(index, this._bp1[segmentStartDir], limits,
				this._bendLink, num);
		this._bendLink.GetSegments()[num] += num4 * (limits[0] - limits[1]);
		prevObstacleFlags[num] = false;
		Integer num3 = this._bp0[index];
		actEndPoint.SetIndex(index, num3 - this._sgridCorr[index]);
		actEndPoint.SetIndex(segmentStartDir, num2
				- this._sgridCorr[segmentStartDir]);
		Integer offsetX = this._sgridCorr[0] - this._bgridCorr[0];
		Integer offsetY = this._sgridCorr[1] - this._bgridCorr[1];
		p.CopyData(actStartPoint, 0, 0);
		actStartPoint.CopyData(point3, -offsetX, -offsetY);
		point3.CopyData(p, offsetX, offsetY);
		this._straightLink.UpdateGrid();
		this._bendLink.UpdateGrid();
		grid.SetObstacle(this._straightLink.GetFromRect());
		grid.SetObstacle(this._straightLink.GetToRect());
		grid.SetObstacle(this._bendLink.GetFromRect());
		grid.SetObstacle(this._bendLink.GetToRect());
		p.Dispose();

	}

	private void FillSegmentsForCrossingPair(Integer[] segArray,
			Boolean[] prevObstacleFlags, Boolean[] nextObstacleFlags,
			LLLink fillLink, LLLink otherLink, Boolean fillLinkA,
			Boolean bypassC1, Boolean bypassC2) {
		Integer crossSegIndexA = null;
		Integer crossSegIndexB = null;
		Integer distFromStartA = null;
		Integer distFromStartB = null;
		Integer num5 = null;
		Integer num6 = null;
		Boolean flag = null;
		Boolean flag2 = null;
		Boolean flag3 = null;
		Integer[] numArray = null;
		Integer[] numArray2 = null;
		LLCrossPoint point = null;
		LLCrossPoint point2 = null;
		Integer num7 = null;
		Integer num8 = null;
		Integer num10 = null;
		this._segArray = segArray;
		this._prevObstacleFlags = prevObstacleFlags;
		this._nextObstacleFlags = nextObstacleFlags;
		if (fillLinkA) {

			crossSegIndexA = this._crossing1.GetCrossSegIndexA();

			crossSegIndexB = this._crossing2.GetCrossSegIndexA();

			distFromStartA = this._crossing1.GetDistFromStartA();

			distFromStartB = this._crossing2.GetDistFromStartA();
		} else {

			crossSegIndexA = this._crossing1.GetCrossSegIndexB();

			crossSegIndexB = this._crossing2.GetCrossSegIndexB();

			distFromStartA = this._crossing1.GetDistFromStartB();

			distFromStartB = this._crossing2.GetDistFromStartB();
		}
		if (crossSegIndexA < crossSegIndexB) {
			num5 = crossSegIndexA;
			num6 = crossSegIndexB;
			flag = false;
		} else {
			num6 = crossSegIndexA;
			num5 = crossSegIndexB;
			if (crossSegIndexA == crossSegIndexB) {
				if (distFromStartA <= distFromStartB) {
					flag = false;
				} else {
					flag = true;
				}
			} else {
				flag = true;
			}
		}
		if (flag) {
			flag2 = bypassC2;
			flag3 = bypassC1;
			numArray = this._bypassPoint2;
			numArray2 = this._bypassPoint1;
			point = this._crossing2;
			point2 = this._crossing1;
			if (fillLinkA) {

				num7 = this._crossing2.GetCrossSegIndexB();

				num8 = this._crossing1.GetCrossSegIndexB();
			} else {

				num7 = this._crossing2.GetCrossSegIndexA();

				num8 = this._crossing1.GetCrossSegIndexA();
			}
		} else {
			flag2 = bypassC1;
			flag3 = bypassC2;
			numArray = this._bypassPoint1;
			numArray2 = this._bypassPoint2;
			point = this._crossing1;
			point2 = this._crossing2;
			if (fillLinkA) {

				num7 = this._crossing1.GetCrossSegIndexB();

				num8 = this._crossing2.GetCrossSegIndexB();
			} else {

				num7 = this._crossing1.GetCrossSegIndexA();

				num8 = this._crossing2.GetCrossSegIndexA();
			}
		}
		Integer[] segments = fillLink.GetSegments();
		Integer[] srcSegArray = otherLink.GetSegments();

		this._oldPrevObstacleFlags = fillLink.GetPrevObstacleFlags();

		this._oldNextObstacleFlags = fillLink.GetNextObstacleFlags();

		this._otherPrevObstacleFlags = otherLink.GetPrevObstacleFlags();

		this._otherNextObstacleFlags = otherLink.GetNextObstacleFlags();
		segArray[0] = 0;
		Integer i = this.TransferSegments(segments, this._oldPrevObstacleFlags,
				this._oldNextObstacleFlags, 0, 0, num5);

		i = this.HandleFirstCrossing(fillLink.GetSegmentDir(num5), point,
				point2, num7, num8, flag2, numArray, i);
		if (num7 == num8) {
			num10 = num7;
		} else if (num7 < num8) {
			num10 = num7 + 1;
		} else {
			num10 = num7 - 1;
		}

		i = this.TransferSegments(srcSegArray, this._otherPrevObstacleFlags,
				this._otherNextObstacleFlags, i, num10, num8);
		if ((flag2 && flag3) && (num10 == num8)) {
			this._nextObstacleFlags[i] = false;
			this._prevObstacleFlags[i + 1] = false;
			i += 2;
		}

		i = this.HandleSecondCrossing(fillLink.GetSegmentDir(num6), point2,
				num6, num7, num8, flag3, numArray2, i);

		i = this.TransferSegments(segments, this._oldPrevObstacleFlags,
				this._oldNextObstacleFlags, i, num6 + 1, segments.length);
		prevObstacleFlags[0] = true;
		nextObstacleFlags[nextObstacleFlags.length - 1] = true;

	}

	private void FillSegmentsForTerminationExchange(Integer[] segArray,
			Boolean[] prevObstacleFlags, Boolean[] nextObstacleFlags,
			LLLink fillLink, LLLink otherLink, Integer fillSegIndexAtCrossing,
			Integer otherSegIndexAtCrossing, Boolean bypassC,
			Boolean exchangeAtStart, Boolean bothLinkSameDir) {
		Integer num = null;
		this._segArray = segArray;
		this._prevObstacleFlags = prevObstacleFlags;
		this._nextObstacleFlags = nextObstacleFlags;
		Integer[] segments = fillLink.GetSegments();
		Integer[] srcSegArray = otherLink.GetSegments();

		this._oldPrevObstacleFlags = fillLink.GetPrevObstacleFlags();

		this._oldNextObstacleFlags = fillLink.GetNextObstacleFlags();

		this._otherPrevObstacleFlags = otherLink.GetPrevObstacleFlags();

		this._otherNextObstacleFlags = otherLink.GetNextObstacleFlags();
		segArray[0] = 0;
		if (exchangeAtStart) {
			if (bothLinkSameDir) {

				num = this.TransferSegments(srcSegArray,
						this._otherPrevObstacleFlags,
						this._otherNextObstacleFlags, 0, 0,
						otherSegIndexAtCrossing);
			} else {

				num = this.TransferSegments(srcSegArray,
						this._otherPrevObstacleFlags,
						this._otherNextObstacleFlags, 0,
						srcSegArray.length - 1, otherSegIndexAtCrossing);
			}
		} else {

			num = this.TransferSegments(segments, this._oldPrevObstacleFlags,
					this._oldNextObstacleFlags, 0, 0, fillSegIndexAtCrossing);
		}
		Integer num2 = bothLinkSameDir ? 1 : -1;
		if (exchangeAtStart) {

			num = this.HandleSecondCrossing(
					fillLink.GetSegmentDir(fillSegIndexAtCrossing),
					this._crossing1, fillSegIndexAtCrossing,
					otherSegIndexAtCrossing - num2, otherSegIndexAtCrossing,
					bypassC, this._bypassPoint1, num);
		} else {

			num = this.HandleFirstCrossing(
					fillLink.GetSegmentDir(fillSegIndexAtCrossing),
					this._crossing1, null, otherSegIndexAtCrossing,
					otherSegIndexAtCrossing + num2, bypassC,
					this._bypassPoint1, num);
		}
		if (exchangeAtStart) {

			num = this.TransferSegments(segments, this._oldPrevObstacleFlags,
					this._oldNextObstacleFlags, num,
					fillSegIndexAtCrossing + 1, segments.length);
		} else if (bothLinkSameDir) {

			num = this.TransferSegments(srcSegArray,
					this._otherPrevObstacleFlags, this._otherNextObstacleFlags,
					num, otherSegIndexAtCrossing + 1, srcSegArray.length);
		} else {

			num = this.TransferSegments(srcSegArray,
					this._otherPrevObstacleFlags, this._otherNextObstacleFlags,
					num, otherSegIndexAtCrossing - 1, -1);
		}
		prevObstacleFlags[0] = true;
		nextObstacleFlags[nextObstacleFlags.length - 1] = true;

	}

	private Boolean FindPairOfReducableCrossings() {
		for (LLCrossPoint point = this._listOfCrossPoints; point != null; point = point
				.GetNext()) {
			if (point.GetCrossType() != -1) {
				for (LLCrossPoint point2 = point.GetNext(); point2 != null; point2 = point2
						.GetNext()) {
					if (point2.GetCrossType() != -1) {
						Boolean flag3 = (point.GetCrossIndex(0) != point2
								.GetCrossIndex(0))
								|| (point.GetCrossIndex(1) != point2
										.GetCrossIndex(1));

						flag3 &= point.LetSurvive(point2);
						if (flag3 & point2.LetSurvive(point)) {
							Boolean flag = point.GetDistFromStartA() < point2
									.GetDistFromStartA();
							Boolean flag2 = point.GetDistFromStartB() < point2
									.GetDistFromStartB();
							if (point.IsOppositeFlow() == point2
									.IsOppositeFlow()) {
								if (point.IsOppositeFlow() && (flag != flag2)) {
									this._crossing1 = point;
									this._crossing2 = point2;

									return true;
								}

								if (!point.IsOppositeFlow() && (flag == flag2)) {
									this._crossing1 = point;
									this._crossing2 = point2;

									return true;
								}
							}
						}
					}
				}
			}
		}

		return false;

	}

	private Boolean FindSingleReducableCrossing(Boolean sameDir) {
		for (LLCrossPoint point = this._listOfCrossPoints; point != null; point = point
				.GetNext()) {
			if ((point.GetCrossType() != -1)
					&& (point.IsOppositeFlow() == !sameDir)) {
				this._crossing1 = point;

				return true;
			}
		}

		return false;

	}

	private LLGrid GetGrid() {

		return this._grid;

	}

	private LongLinkLayout GetLayout() {

		return this._layout;

	}

	private Integer HandleBypassCrossing(Integer dir, LLCrossPoint crossPoint,
			Integer[] bypassPoint, Integer i) {
		LLGridSegment segment = null;
		Integer index = 1 - dir;
		i--;
		this._nextObstacleFlags[i] = true;
		this._segArray[i] += bypassPoint[index]
				- crossPoint.GetCrossIndex(index);
		LLGridLine gridLine = this.GetGrid().GetGridLine(dir, bypassPoint[dir]);
		if (gridLine != null) {

			segment = gridLine.SearchSegment(bypassPoint[index]);
			if (segment != null) {
				if ((this._segArray[i] < 0)
						&& (segment.GetStartIndex() < bypassPoint[index])) {
					this._nextObstacleFlags[i] = false;
				} else if ((this._segArray[i] > 0)
						&& (segment.GetEndIndex() > bypassPoint[index])) {
					this._nextObstacleFlags[i] = false;
				}
			}
		}
		i++;
		this._prevObstacleFlags[i] = true;
		this._segArray[i] = crossPoint.GetCrossIndex(dir) - bypassPoint[dir];

		gridLine = this.GetGrid().GetGridLine(index, bypassPoint[index]);
		if (gridLine != null) {

			segment = gridLine.SearchSegment(bypassPoint[dir]);
			if (segment == null) {

				return i;
			}
			if ((this._segArray[i] > 0)
					&& (segment.GetStartIndex() < bypassPoint[dir])) {
				this._prevObstacleFlags[i] = false;

				return i;
			}
			if ((this._segArray[i] < 0)
					&& (segment.GetEndIndex() > bypassPoint[dir])) {
				this._prevObstacleFlags[i] = false;
			}
		}

		return i;

	}

	private Integer HandleFirstCrossing(Integer dir, LLCrossPoint firstCP,
			LLCrossPoint secondCP, Integer otherIndexAtFirstCP,
			Integer otherIndexAtSecondCP, Boolean bypass,
			Integer[] bypassPoint, Integer i) {
		if (bypass) {

			i = this.HandleBypassCrossing(dir, firstCP, bypassPoint, i);

			return i;
		}
		Integer num = 1 - dir;
		this._prevObstacleFlags[i] = this._oldPrevObstacleFlags[i];
		this._nextObstacleFlags[i] = false;
		this._segArray[i++] = firstCP.GetCrossIndex(dir)
				- firstCP.GetCrossContIndex(dir);
		if (otherIndexAtFirstCP == otherIndexAtSecondCP) {
			this._prevObstacleFlags[i] = false;
			this._nextObstacleFlags[i] = false;
			this._segArray[i++] = secondCP.GetCrossIndex(num)
					- firstCP.GetCrossIndex(num);
		} else {
			this._prevObstacleFlags[i] = false;
			if (otherIndexAtFirstCP < otherIndexAtSecondCP) {
				this._nextObstacleFlags[i] = this._otherNextObstacleFlags[otherIndexAtFirstCP];
			} else {
				this._nextObstacleFlags[i] = this._otherPrevObstacleFlags[otherIndexAtFirstCP];
			}
			this._segArray[i++] = firstCP.GetCrossContIndex(num)
					- firstCP.GetCrossIndex(num);
		}
		if (i < this._segArray.length) {
			this._segArray[i] = 0;
		}

		return i;

	}

	private Integer HandleSecondCrossing(Integer dir, LLCrossPoint secondCP,
			Integer fillIndexAtSecondCP, Integer otherIndexAtFirstCP,
			Integer otherIndexAtSecondCP, Boolean bypass,
			Integer[] bypassPoint, Integer i) {
		if (bypass) {

			i = this.HandleBypassCrossing(1 - dir, secondCP, bypassPoint, i);

			return i;
		}
		Integer num = 1 - dir;
		if (otherIndexAtFirstCP != otherIndexAtSecondCP) {
			if (this._segArray[i] == 0) {
				if (otherIndexAtFirstCP < otherIndexAtSecondCP) {
					this._prevObstacleFlags[i] = this._otherPrevObstacleFlags[otherIndexAtSecondCP];
				} else {
					this._prevObstacleFlags[i] = this._otherNextObstacleFlags[otherIndexAtSecondCP];
				}
			}
			this._nextObstacleFlags[i] = false;
			this._segArray[i++] += secondCP.GetCrossIndex(num)
					- secondCP.GetCrossContIndex(num);
		}
		this._prevObstacleFlags[i] = false;
		this._nextObstacleFlags[i] = this._oldNextObstacleFlags[fillIndexAtSecondCP];
		this._segArray[i++] = secondCP.GetCrossContIndex(dir)
				- secondCP.GetCrossIndex(dir);
		if (i < this._segArray.length) {
			this._segArray[i] = 0;
		}

		return i;

	}

	private Boolean IsForbidden(Integer i, Integer[] limits, Integer dir,
			LLLink link) {
		Integer gridCorrection = link.GetGridCorrection(dir);
		LLTerminationPoint actStartPoint = link.GetActStartPoint();
		if ((link.GetSegmentStartDir() == dir)
				&& ((actStartPoint.GetIndex(1 - dir) + link
						.GetGridCorrection(1 - dir)) == i)) {
			Integer num2 = (actStartPoint.GetForbiddenMinIndex() + gridCorrection) - 1;
			Integer num3 = (actStartPoint.GetForbiddenMaxIndex() + gridCorrection) + 1;
			if ((limits[0] <= num3) && (limits[1] >= num2)) {

				return true;
			}
		}

		actStartPoint = link.GetActEndPoint();
		if ((link.GetSegmentEndDir() == dir)
				&& ((actStartPoint.GetIndex(1 - dir) + link
						.GetGridCorrection(1 - dir)) == i)) {
			Integer num4 = (actStartPoint.GetForbiddenMinIndex() + gridCorrection) - 1;
			Integer num5 = (actStartPoint.GetForbiddenMaxIndex() + gridCorrection) + 1;
			if ((limits[0] <= num5) && (limits[1] >= num4)) {

				return true;
			}
		}

		return false;

	}

	public Boolean IsStraightTerminationCrossing(LLLink link1, LLLink link2) {
		if (link1.GetSegments().length == 1) {
			if (link2.GetSegments().length <= 1) {

				return false;
			}
			this._straightLink = link1;
			this._bendLink = link2;
		} else if ((link2.GetSegments().length == 1)
				&& (link1.GetSegments().length > 1)) {
			this._straightLink = link2;
			this._bendLink = link1;
		} else {

			return false;
		}
		if (this._straightLink.GetFromNode() == this._bendLink.GetFromNode()) {
			this._exchangeAtStraightLinkStart = true;
			this._exchangeAtBendLinkStart = true;
		} else if (this._straightLink.GetToNode() == this._bendLink
				.GetFromNode()) {
			this._exchangeAtStraightLinkStart = false;
			this._exchangeAtBendLinkStart = true;
		} else if (this._straightLink.GetFromNode() == this._bendLink
				.GetToNode()) {
			this._exchangeAtStraightLinkStart = true;
			this._exchangeAtBendLinkStart = false;
		} else if (this._straightLink.GetToNode() == this._bendLink.GetToNode()) {
			this._exchangeAtStraightLinkStart = false;
			this._exchangeAtBendLinkStart = false;
		} else {

			return false;
		}
		Integer segmentStartDir = this._straightLink.GetSegmentStartDir();
		this._straightLink.GetNextBend(this._sp1, 0);
		this._sp2[0] = this._sp1[0];
		this._sp2[1] = this._sp1[1];
		this._straightLink.GetNextBend(this._sp2, 1);
		if (this._exchangeAtBendLinkStart) {
			if (segmentStartDir == (1 - this._bendLink.GetSegmentStartDir())) {

				return false;
			}
			this._bendLink.GetNextBend(this._bp0, 0);
			this._bp1[0] = this._bp0[0];
			this._bp1[1] = this._bp0[1];
			this._bendLink.GetNextBend(this._bp1, 1);
			this._bp2[0] = this._bp1[0];
			this._bp2[1] = this._bp1[1];
			this._bendLink.GetNextBend(this._bp2, 2);
		} else {
			if (segmentStartDir == (1 - this._bendLink.GetSegmentEndDir())) {

				return false;
			}
			Integer length = this._bendLink.GetSegments().length;
			this._bendLink.GetPrevBend(this._bp0, length);
			this._bp1[0] = this._bp0[0];
			this._bp1[1] = this._bp0[1];
			this._bendLink.GetPrevBend(this._bp1, length - 1);
			this._bp2[0] = this._bp1[0];
			this._bp2[1] = this._bp1[1];
			this._bendLink.GetPrevBend(this._bp2, length - 2);
		}
		Integer index = 1 - segmentStartDir;
		if (this._bp1[segmentStartDir] <= Math.Min(this._sp1[segmentStartDir],
				this._sp2[segmentStartDir])) {

			return false;
		}
		if (this._bp1[segmentStartDir] >= Math.Max(this._sp1[segmentStartDir],
				this._sp2[segmentStartDir])) {

			return false;
		}
		if (this._sp1[index] <= Math.Min(this._bp1[index], this._bp2[index])) {

			return false;
		}
		if (this._sp1[index] >= Math.Max(this._bp1[index], this._bp2[index])) {

			return false;
		}
		this._moveStraightToLower = this._bp1[index] < this._bp2[index];

		this._sgridCorr[0] = this._straightLink.GetGridCorrection(0);

		this._sgridCorr[1] = this._straightLink.GetGridCorrection(1);

		this._bgridCorr[0] = this._bendLink.GetGridCorrection(0);

		this._bgridCorr[1] = this._bendLink.GetGridCorrection(1);

		this._slimits[0] = this._straightLink.GetActStartPoint().GetIndex(
				segmentStartDir);

		this._slimits[1] = this._straightLink.GetActEndPoint().GetIndex(
				segmentStartDir);
		this._slimits[0] += this._sgridCorr[segmentStartDir];
		this._slimits[1] += this._sgridCorr[segmentStartDir];
		if (this._exchangeAtBendLinkStart) {

			this._blimits[0] = this._bendLink.GetActStartPoint().GetIndex(
					segmentStartDir);
		} else {

			this._blimits[0] = this._bendLink.GetActEndPoint().GetIndex(
					segmentStartDir);
		}
		this._blimits[0] += this._bgridCorr[segmentStartDir];
		this._blimits[1] = this._bp1[segmentStartDir];

		return true;

	}

	public void MarkAndPostProcess(LLLink link1, LLLink link2) {
		link1.MarkLinkChanged();
		link2.MarkLinkChanged();

	}

	public void MoveBendLink(Integer TPBDir) {
		LLTerminationPoint actStartPoint = null;
		Boolean[] prevObstacleFlags = null;
		Integer num = null;
		Integer num2 = null;
		Integer num3 = null;
		if (this._exchangeAtBendLinkStart) {

			actStartPoint = this._bendLink.GetActStartPoint();
			num = 0;
			num2 = 1;

			prevObstacleFlags = this._bendLink.GetPrevObstacleFlags();
			num3 = 1;
		} else {

			actStartPoint = this._bendLink.GetActEndPoint();
			num = this._bendLink.GetSegments().length - 1;
			num2 = this._bendLink.GetSegments().length - 2;

			prevObstacleFlags = this._bendLink.GetNextObstacleFlags();
			num3 = -1;
		}
		LLGrid grid = this.GetGrid();
		Integer segmentStartDir = this._straightLink.GetSegmentStartDir();
		Integer index = 1 - segmentStartDir;
		Integer[] limits = new Integer[] { this._bp0[segmentStartDir],
				this._bp1[segmentStartDir] };
		grid.RemoveObstacle(segmentStartDir, this._bp0[index], limits,
				this._bendLink, num);
		limits[0] = this._bp1[index];
		limits[1] = TPBDir;
		grid.RemoveObstacle(index, this._bp1[segmentStartDir], limits,
				this._bendLink, num2);
		this._bendLink.GetSegments()[num2] += num3 * (limits[0] - limits[1]);
		prevObstacleFlags[num2] = false;
		actStartPoint.SetIndex(index, TPBDir - this._bgridCorr[index]);
		this._bendLink.UpdateGrid();
		grid.SetObstacle(this._bendLink.GetFromRect());
		grid.SetObstacle(this._bendLink.GetToRect());

	}

	public void MoveStraightLink(Integer TPBDir) {
		LLGrid grid = this.GetGrid();
		Integer segmentStartDir = this._straightLink.GetSegmentStartDir();
		Integer index = 1 - segmentStartDir;
		Integer[] limits = new Integer[] { this._sp1[segmentStartDir],
				this._sp2[segmentStartDir] };
		grid.RemoveObstacle(segmentStartDir, this._sp1[index], limits,
				this._straightLink, 0);
		Integer val = TPBDir - this._sgridCorr[index];
		this._straightLink.GetActStartPoint().SetIndex(index, val);
		this._straightLink.GetActEndPoint().SetIndex(index, val);
		this._straightLink.UpdateGrid();
		grid.SetObstacle(this._straightLink.GetFromRect());
		grid.SetObstacle(this._straightLink.GetToRect());

	}

	private void ReduceCrossingByTerminationExchange(LLLink link1,
			LLLink link2, Boolean exchangeAtLink1Start,
			Boolean exchangeAtLink2Start) {
		if (this._crossing1.GetCrossType() != -1) {
			Integer num3 = null;
			Integer num4 = null;
			LLTerminationPoint actStartPoint = null;
			LLTerminationPoint actEndPoint = null;
			Boolean bothLinkSameDir = exchangeAtLink1Start == exchangeAtLink2Start;
			Integer[] segments = link1.GetSegments();
			Integer[] numArray2 = link2.GetSegments();
			this._crossing1.CalcBypassPoint(this._bypassPoint1);
			Integer crossSegIndexA = this._crossing1.GetCrossSegIndexA();
			Integer crossSegIndexB = this._crossing1.GetCrossSegIndexB();
			Boolean bypassC = this._crossing1.NeedBypassIfFirst(link1,
					crossSegIndexA);
			if (exchangeAtLink1Start) {
				bypassC = !bypassC;
			}
			if (exchangeAtLink1Start) {
				num4 = crossSegIndexA;
				num3 = (segments.length - crossSegIndexA) - 1;
			} else {
				num3 = crossSegIndexA;
				num4 = (segments.length - crossSegIndexA) - 1;
			}
			if (exchangeAtLink2Start) {
				num3 += crossSegIndexB;
				num4 += (numArray2.length - crossSegIndexB) - 1;
			} else {
				num4 += crossSegIndexB;
				num3 += (numArray2.length - crossSegIndexB) - 1;
			}
			if (!bypassC) {
				num3 += 2;
			}
			Integer[] segArray = new Integer[num3];
			Boolean[] prevObstacleFlags = new Boolean[num3];
			Boolean[] nextObstacleFlags = new Boolean[num3];
			if (bypassC) {
				num4 += 2;
			}
			Integer[] numArray4 = new Integer[num4];
			Boolean[] flagArray3 = new Boolean[num4];
			Boolean[] flagArray4 = new Boolean[num4];
			Integer[] limit = new Integer[2];
			LLGrid grid = this.GetGrid();
			Integer horizontalFreeSegment = this._crossing1
					.GetHorizontalFreeSegment(limit);
			Integer i = this._crossing1.GetCrossSegIndexA();
			grid.RemoveObstacle(0, horizontalFreeSegment, limit, link1, i);

			i = this._crossing1.GetCrossSegIndexB();
			grid.RemoveObstacle(0, horizontalFreeSegment, limit, link2, i);

			horizontalFreeSegment = this._crossing1
					.GetVerticalFreeSegment(limit);

			i = this._crossing1.GetCrossSegIndexA();
			grid.RemoveObstacle(1, horizontalFreeSegment, limit, link1, i);

			i = this._crossing1.GetCrossSegIndexB();
			grid.RemoveObstacle(1, horizontalFreeSegment, limit, link2, i);
			this.FillSegmentsForTerminationExchange(segArray,
					prevObstacleFlags, nextObstacleFlags, link1, link2,
					crossSegIndexA, crossSegIndexB, bypassC,
					exchangeAtLink1Start, bothLinkSameDir);
			this.FillSegmentsForTerminationExchange(numArray4, flagArray3,
					flagArray4, link2, link1, crossSegIndexB, crossSegIndexA,
					!bypassC, exchangeAtLink2Start, bothLinkSameDir);
			LLTerminationPoint p = new LLTerminationPoint();
			Integer offsetX = link1.GetGridCorrection(0)
					- link2.GetGridCorrection(0);
			Integer offsetY = link1.GetGridCorrection(1)
					- link2.GetGridCorrection(1);
			Integer segmentStartDir = link1.GetSegmentStartDir();
			Integer dir = link2.GetSegmentStartDir();
			if (exchangeAtLink1Start) {

				actStartPoint = link1.GetActStartPoint();
				if (bothLinkSameDir) {

					segmentStartDir = link2.GetSegmentStartDir();
				} else {

					segmentStartDir = link2.GetSegmentEndDir();
				}
			} else {

				actStartPoint = link1.GetActEndPoint();
			}
			if (exchangeAtLink2Start) {

				actEndPoint = link2.GetActStartPoint();
				if (bothLinkSameDir) {

					dir = link1.GetSegmentStartDir();
				} else {

					dir = link1.GetSegmentEndDir();
				}
			} else {

				actEndPoint = link2.GetActEndPoint();
			}
			p.CopyData(actStartPoint, 0, 0);
			actStartPoint.CopyData(actEndPoint, -offsetX, -offsetY);
			actEndPoint.CopyData(p, offsetX, offsetY);
			p.Dispose();
			link1.SetSegments(segArray, prevObstacleFlags, nextObstacleFlags);
			link2.SetSegments(numArray4, flagArray3, flagArray4);
			link1.SetSegmentStartDir(segmentStartDir);
			link2.SetSegmentStartDir(dir);

			horizontalFreeSegment = this._crossing1
					.GetHorizontalTestSegment(limit);
			grid.SetGridLineObstacle(true, horizontalFreeSegment, limit);

			horizontalFreeSegment = this._crossing1
					.GetVerticalTestSegment(limit);
			grid.SetGridLineObstacle(false, horizontalFreeSegment, limit);
			this._crossing1.MakeUnfeasable();
		}

	}

	private void ReduceCrossingPair(LLLink link1, LLLink link2) {
		Integer num5 = null;
		Integer num6 = null;
		Integer num7 = null;
		Integer num8 = null;
		Integer[] segments = link1.GetSegments();
		Integer[] numArray2 = link2.GetSegments();
		this._crossing1.CalcBypassPoint(this._bypassPoint1);
		this._crossing2.CalcBypassPoint(this._bypassPoint2);
		Integer crossSegIndexA = this._crossing1.GetCrossSegIndexA();
		Integer crossSegIndex = this._crossing2.GetCrossSegIndexA();
		Integer crossSegIndexB = this._crossing1.GetCrossSegIndexB();
		Integer num4 = this._crossing2.GetCrossSegIndexB();
		Boolean flag = this._crossing1.NeedBypassIfFirst(link1, crossSegIndexA);

		Boolean flag2 = !this._crossing2
				.NeedBypassIfFirst(link1, crossSegIndex);
		if (this._crossing1.GetDistFromStartA() > this._crossing2
				.GetDistFromStartA()) {
			flag = !flag;
			flag2 = !flag2;
		}
		if (crossSegIndexA <= crossSegIndex) {
			num5 = (crossSegIndex - crossSegIndexA) + 1;
			num7 = ((crossSegIndexA + segments.length) - crossSegIndex) - 1;
		} else {
			num5 = (crossSegIndexA - crossSegIndex) + 1;
			num7 = ((crossSegIndex + segments.length) - crossSegIndexA) - 1;
		}
		if (crossSegIndexB <= num4) {
			num6 = (num4 - crossSegIndexB) + 1;
			num8 = ((crossSegIndexB + numArray2.length) - num4) - 1;
		} else {
			num6 = (crossSegIndexB - num4) + 1;
			num8 = ((num4 + numArray2.length) - crossSegIndexB) - 1;
		}
		Integer num9 = num7 + num6;
		if (flag) {
			num9--;
		} else {
			num9++;
		}
		if (flag2) {
			num9--;
		} else {
			num9++;
		}
		if ((flag && flag2) && (num6 == 2)) {
			num9 += 2;
		}
		Integer[] segArray = new Integer[num9];
		Boolean[] prevObstacleFlags = new Boolean[num9];
		Boolean[] nextObstacleFlags = new Boolean[num9];
		num9 = num8 + num5;
		if (flag) {
			num9++;
		} else {
			num9--;
		}
		if (flag2) {
			num9++;
		} else {
			num9--;
		}
		if ((!flag && !flag2) && (num5 == 2)) {
			num9 += 2;
		}
		Integer[] numArray4 = new Integer[num9];
		Boolean[] flagArray3 = new Boolean[num9];
		Boolean[] flagArray4 = new Boolean[num9];
		Integer[] limit = new Integer[2];
		LLGrid grid = this.GetGrid();
		Integer horizontalFreeSegment = this._crossing1
				.GetHorizontalFreeSegment(limit);
		Integer i = this._crossing1.GetCrossSegIndexA();
		grid.RemoveObstacle(0, horizontalFreeSegment, limit, link1, i);

		i = this._crossing1.GetCrossSegIndexB();
		grid.RemoveObstacle(0, horizontalFreeSegment, limit, link2, i);

		horizontalFreeSegment = this._crossing1.GetVerticalFreeSegment(limit);

		i = this._crossing1.GetCrossSegIndexA();
		grid.RemoveObstacle(1, horizontalFreeSegment, limit, link1, i);

		i = this._crossing1.GetCrossSegIndexB();
		grid.RemoveObstacle(1, horizontalFreeSegment, limit, link2, i);

		horizontalFreeSegment = this._crossing2.GetHorizontalFreeSegment(limit);

		i = this._crossing2.GetCrossSegIndexA();
		grid.RemoveObstacle(0, horizontalFreeSegment, limit, link1, i);

		i = this._crossing2.GetCrossSegIndexB();
		grid.RemoveObstacle(0, horizontalFreeSegment, limit, link2, i);

		horizontalFreeSegment = this._crossing2.GetVerticalFreeSegment(limit);

		i = this._crossing2.GetCrossSegIndexA();
		grid.RemoveObstacle(1, horizontalFreeSegment, limit, link1, i);

		i = this._crossing2.GetCrossSegIndexB();
		grid.RemoveObstacle(1, horizontalFreeSegment, limit, link2, i);
		this.FillSegmentsForCrossingPair(segArray, prevObstacleFlags,
				nextObstacleFlags, link1, link2, true, flag, flag2);
		this.FillSegmentsForCrossingPair(numArray4, flagArray3, flagArray4,
				link2, link1, false, !flag, !flag2);
		link1.SetSegments(segArray, prevObstacleFlags, nextObstacleFlags);
		link2.SetSegments(numArray4, flagArray3, flagArray4);

		horizontalFreeSegment = this._crossing1.GetHorizontalTestSegment(limit);
		grid.SetGridLineObstacle(true, horizontalFreeSegment, limit);

		horizontalFreeSegment = this._crossing1.GetVerticalTestSegment(limit);
		grid.SetGridLineObstacle(false, horizontalFreeSegment, limit);

		horizontalFreeSegment = this._crossing2.GetHorizontalTestSegment(limit);
		grid.SetGridLineObstacle(true, horizontalFreeSegment, limit);

		horizontalFreeSegment = this._crossing2.GetVerticalTestSegment(limit);
		grid.SetGridLineObstacle(false, horizontalFreeSegment, limit);
		this._crossing1.MakeUnfeasable();
		this._crossing2.MakeUnfeasable();
		if ((flag && flag2) && (num6 == 2)) {
			link1.UpdateGrid();
		}
		if ((!flag && !flag2) && (num5 == 2)) {
			link2.UpdateGrid();
		}

	}

	public Boolean ReduceStraightTerminationCrossing() {
		Integer num2 = 0x7fffffff;
		Integer num3 = -1;
		Integer[] gridIndexResult = new Integer[1];
		Integer[] numArray2 = new Integer[1];
		Integer num = this.CheckExchangeBendAndStraight();
		if (num < num2) {
			num3 = 0;
			num2 = num;
		}

		num = this.CheckMoveStraight(gridIndexResult);
		if (num < num2) {
			num3 = 1;
			num2 = num;
		}
		if (this.CheckMoveBend(numArray2) < num2) {
			num3 = 2;
		}
		if (num3 == 0) {
			this.ExchangeBendAndStraightLink();
			// NOTICE: break ignore!!!
		} else if (num3 == 1) {
			this.MoveStraightLink(gridIndexResult[0]);
			// NOTICE: break ignore!!!
		} else if (num3 == 2) {
			this.MoveBendLink(numArray2[0]);
			// NOTICE: break ignore!!!
		}

		return (num2 < 0x7fffffff);

	}

	public Boolean Run(LLLink link1, LLLink link2) {

		if (!link1.MayIntersect(link2)) {

			return false;
		}
		if (this.IsStraightTerminationCrossing(link1, link2)
				&& this.ReduceStraightTerminationCrossing()) {
			this.MarkAndPostProcess(link1, link2);

			return true;
		}
		Integer num = 0;
		Boolean flag = false;
		Boolean flag2 = true;
		Integer num2 = this.CalcCrossings(link1, link2);
		while (flag2) {
			flag2 = false;
			while ((num2 > 1) && this.FindPairOfReducableCrossings()) {
				this.ReduceCrossingPair(link1, link2);
				flag = true;

				num2 = this.CalcCrossings(link1, link2);
				num++;
				if (num > 100) {
					break;
				}
			}
			if (num2 > 0) {
				if (((link1.GetFromNode() == link2.GetFromNode()) && this
						.CanTerminate(link1, link2.GetActStartPoint(), true))
						&& (this.CanTerminate(link2, link1.GetActStartPoint(),
								true) && this.FindSingleReducableCrossing(true))) {
					this.ReduceCrossingByTerminationExchange(link1, link2,
							true, true);
					flag = true;
					flag2 = true;

					num2 = this.CalcCrossings(link1, link2);
				} else if (((link1.GetToNode() == link2.GetToNode()) && this
						.CanTerminate(link1, link2.GetActEndPoint(), false))
						&& (this.CanTerminate(link2, link1.GetActEndPoint(),
								false) && this
								.FindSingleReducableCrossing(true))) {
					this.ReduceCrossingByTerminationExchange(link1, link2,
							false, false);
					flag = true;
					flag2 = true;

					num2 = this.CalcCrossings(link1, link2);
				} else if (((link1.GetFromNode() == link2.GetToNode()) && this
						.CanTerminate(link1, link2.GetActEndPoint(), true))
						&& (this.CanTerminate(link2, link1.GetActStartPoint(),
								false) && this
								.FindSingleReducableCrossing(false))) {
					this.ReduceCrossingByTerminationExchange(link1, link2,
							true, false);
					flag = true;
					flag2 = true;

					num2 = this.CalcCrossings(link1, link2);
				} else if (((link1.GetToNode() == link2.GetFromNode()) && this
						.CanTerminate(link1, link2.GetActStartPoint(), false))
						&& (this.CanTerminate(link2, link1.GetActEndPoint(),
								true) && this
								.FindSingleReducableCrossing(false))) {
					this.ReduceCrossingByTerminationExchange(link1, link2,
							false, true);
					flag = true;
					flag2 = true;

					num2 = this.CalcCrossings(link1, link2);
				}
				num++;
				if (num > 100) {
					break;
				}
			}
		}
		if (flag) {
			this.MarkAndPostProcess(link1, link2);
		}

		return flag;

	}

	private Integer TransferSegments(Integer[] srcSegArray,
			Boolean[] srcPrevObstacleFlags, Boolean[] srcNextObstacleFlags,
			Integer i, Integer start, Integer end) {
		Integer num = null;
		if (start < end) {
			if (this._segArray[i] == 0) {
				this._prevObstacleFlags[i] = srcPrevObstacleFlags[start];
				this._nextObstacleFlags[i] = srcNextObstacleFlags[start];
				this._segArray[i++] = srcSegArray[start];
			} else {
				this._nextObstacleFlags[i] = srcNextObstacleFlags[start];
				this._segArray[i++] += srcSegArray[start];
			}
			for (num = start + 1; num < end; num++) {
				this._prevObstacleFlags[i] = srcPrevObstacleFlags[num];
				this._nextObstacleFlags[i] = srcNextObstacleFlags[num];
				this._segArray[i++] = srcSegArray[num];
			}
			if (i < this._segArray.length) {
				this._segArray[i] = 0;
			}

			return i;
		}
		if (start > end) {
			if (this._segArray[i] == 0) {
				this._prevObstacleFlags[i] = srcNextObstacleFlags[start];
				this._nextObstacleFlags[i] = srcPrevObstacleFlags[start];
				this._segArray[i++] = -srcSegArray[start];
			} else {
				this._nextObstacleFlags[i] = srcPrevObstacleFlags[start];
				this._segArray[i++] += -srcSegArray[start];
			}
			for (num = start - 1; num > end; num--) {
				this._prevObstacleFlags[i] = srcNextObstacleFlags[num];
				this._nextObstacleFlags[i] = srcPrevObstacleFlags[num];
				this._segArray[i++] = -srcSegArray[num];
			}
			if (i < this._segArray.length) {
				this._segArray[i] = 0;
			}
		}

		return i;

	}

}