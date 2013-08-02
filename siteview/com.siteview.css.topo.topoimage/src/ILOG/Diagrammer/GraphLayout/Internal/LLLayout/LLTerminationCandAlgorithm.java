package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;

public final class LLTerminationCandAlgorithm {
	private LLTerminationPoint _fallbackPoint;

	private LLGrid _grid;

	private LongLinkLayout _layout;

	private Integer _numberResPoints;

	private LLTerminationPoint[] _resPoints;

	private static float FMAXVAL = Float.MAX_VALUE;

	private static Integer MAXVAL = 0x7fffffff;

	public LLTerminationCandAlgorithm(LongLinkLayout layout, LLGrid grid) {
		this._layout = layout;
		this._grid = grid;
		this._resPoints = null;
	}

	private void CalcSidePreferences(InternalRect startRect,
			InternalRect endRect, Integer[] startPref, Integer[] endPref) {
		LongLinkLayout layout = this.GetLayout();
		float minStartSegmentLength = layout.get_MinStartSegmentLength();
		float minEndSegmentLength = layout.get_MinEndSegmentLength();
		CalcSidePrefInternal(startRect.X - minStartSegmentLength,
				(startRect.X + startRect.Width) + minStartSegmentLength,
				endRect.X - minEndSegmentLength, (endRect.X + endRect.Width)
						+ minEndSegmentLength, 1, 3, startPref, endPref);
		CalcSidePrefInternal(startRect.Y - minStartSegmentLength,
				(startRect.Y + startRect.Height) + minStartSegmentLength,
				endRect.Y - minEndSegmentLength, (endRect.Y + endRect.Height)
						+ minEndSegmentLength, 0, 2, startPref, endPref);

	}

	public static void CalcSidePrefInternal(float startMin, float startMax,
			float endMin, float endMax, Integer lowerIndex, Integer upperIndex,
			Integer[] startPref, Integer[] endPref) {
		float num = 0.5f * (startMin + startMax);
		float num2 = 0.5f * (endMin + endMax);
		if (num <= num2) {
			if (startMin >= endMin) {
				startPref[lowerIndex] = 3;
				startPref[upperIndex] = 2;
				endPref[lowerIndex] = 3;
				endPref[upperIndex] = 2;
			} else if (startMax >= endMax) {
				startPref[lowerIndex] = 2;
				startPref[upperIndex] = 3;
				endPref[lowerIndex] = 2;
				endPref[upperIndex] = 3;
			} else if (startMax >= endMin) {
				startPref[lowerIndex] = 3;
				startPref[upperIndex] = 2;
				endPref[lowerIndex] = 2;
				endPref[upperIndex] = 3;
			} else {
				startPref[lowerIndex] = 4;
				startPref[upperIndex] = 1;
				endPref[lowerIndex] = 1;
				endPref[upperIndex] = 4;
			}
		} else if (endMin >= startMin) {
			startPref[lowerIndex] = 3;
			startPref[upperIndex] = 2;
			endPref[lowerIndex] = 3;
			endPref[upperIndex] = 2;
		} else if (endMax >= startMax) {
			startPref[lowerIndex] = 2;
			startPref[upperIndex] = 3;
			endPref[lowerIndex] = 2;
			endPref[upperIndex] = 3;
		} else if (endMax >= startMin) {
			startPref[lowerIndex] = 2;
			startPref[upperIndex] = 3;
			endPref[lowerIndex] = 3;
			endPref[upperIndex] = 2;
		} else {
			startPref[lowerIndex] = 1;
			startPref[upperIndex] = 4;
			endPref[lowerIndex] = 4;
			endPref[upperIndex] = 1;
		}

	}

	public void Dispose() {
		this._layout = null;
		this._grid = null;
		this._resPoints = null;

	}

	private void GenerateTerminationPoints(InternalRect terminationRect,
			Integer[] sidePref, LLLink link, java.lang.Object node,
			float minLen, Boolean origin) {
		this._fallbackPoint = null;
		float minNodeCornerOffset = this.GetLayout().get_MinNodeCornerOffset();
		LLGrid grid = this.GetGrid();
		Integer minGridIndex = grid.GetMinGridIndexAtSide(terminationRect, 0,
				minNodeCornerOffset);
		int numberOfGridLines = (int) Math
				.Max((grid.GetMaxGridIndexAtSide(terminationRect, 0,
						minNodeCornerOffset) - minGridIndex) + 1, 0);
		int num5 = grid.GetMinGridIndexAtSide(terminationRect, 1,
				minNodeCornerOffset);
		int num7 = (int) Math.Max((grid.GetMaxGridIndexAtSide(terminationRect,
				1, minNodeCornerOffset) - num5) + 1, 0);
		this._resPoints = new LLTerminationPoint[(2 * numberOfGridLines)
				+ (2 * num7)];
		this._numberResPoints = 0;
		this.SearchTerminationPoints(terminationRect.Y, -minLen, 2,
				minGridIndex, numberOfGridLines, sidePref[0], link, node,
				origin);
		this.SearchTerminationPoints(
				terminationRect.Y + terminationRect.Height, minLen, 3,
				minGridIndex, numberOfGridLines, sidePref[2], link, node,
				origin);
		this.SearchTerminationPoints(terminationRect.X, -minLen, 0, num5, num7,
				sidePref[1], link, node, origin);
		this.SearchTerminationPoints(terminationRect.X + terminationRect.Width,
				minLen, 1, num5, num7, sidePref[3], link, node, origin);
		if (this._numberResPoints > 1) {
			this._resPoints[0].Sort(this._resPoints);
		}
		if ((this._numberResPoints == 0) && (this._fallbackPoint != null)) {
			this._resPoints[this._numberResPoints++] = this._fallbackPoint;
		}

	}

	private LLGrid GetGrid() {

		return this._grid;

	}

	private LongLinkLayout GetLayout() {

		return this._layout;

	}

	public void Run(LLLink link) {
		LongLinkLayout layout = this.GetLayout();
		Integer[] startPref = new Integer[4];
		Integer[] endPref = new Integer[4];
		for (Integer i = 0; i < 4; i++) {
			startPref[i] = endPref[i] = 0;
		}
		InternalRect fromRect = link.GetFromRect();
		InternalRect toRect = link.GetToRect();
		InternalPoint fromPoint = link.GetFromPoint();
		InternalPoint toPoint = link.GetToPoint();

		if (((fromPoint == null) || (toPoint == null)) && !link.IsSelfLoop()) {
			this.CalcSidePreferences(fromRect, toRect, startPref, endPref);
		}
		float minStartSegmentLength = layout.get_MinStartSegmentLength();
		this._fallbackPoint = null;
		if (fromPoint == null) {
			this.GenerateTerminationPoints(fromRect, startPref, link,
					link.GetFromNode(), minStartSegmentLength, true);
		} else {
			this.SearchSingleTerminationPoint(fromPoint.X, fromPoint.Y,
					fromRect, minStartSegmentLength);
		}
		link.SetStartPoints(this._resPoints, this._numberResPoints);
		minStartSegmentLength = layout.get_MinEndSegmentLength();
		if (toPoint == null) {
			this.GenerateTerminationPoints(toRect, endPref, link,
					link.GetToNode(), minStartSegmentLength, false);
		} else {
			this.SearchSingleTerminationPoint(toPoint.X, toPoint.Y, toRect,
					minStartSegmentLength);
		}
		link.SetEndPoints(this._resPoints, this._numberResPoints);

	}

	private void SearchSingleTerminationPoint(float x, float y,
			InternalRect terminationRect, float minSegDist) {
		Integer index = null;
		Integer num13 = null;
		Integer num14 = null;
		float num18 = 0;
		float num19 = 0;
		LLGrid grid = this.GetGrid();
		float num3 = terminationRect.X;
		float num4 = terminationRect.X + terminationRect.Width;
		float num5 = terminationRect.Y;
		float num6 = terminationRect.Y + terminationRect.Height;
		Integer i = 0;
		Integer startIndex = 0;
		Integer forbiddenMinIndex = -1;
		Integer forbiddenMaxIndex = -1;
		Integer side = 0;
		float fMAXVAL = FMAXVAL;
		float num17 = FMAXVAL;
		LLGridSegment segment = null;
		Boolean flag = false;
		Integer num15 = 0;
		Integer num = grid.GetGridIndex(0, x - minSegDist, false);
		Integer closestGridIndex = grid.GetClosestGridIndex(1, y);
		if (x <= num4) {
			num18 = num3 - x;
			if (num18 < 0f) {
				num18 = -num18;
			}
			if ((y < num5) || (num6 < y)) {
				num18 = 100000f - num18;
			}
		} else {
			num18 = FMAXVAL;
		}
		LLGridSegment segment2 = grid.SearchClosestSegmentAlive(num,
				closestGridIndex, 0, false);
		if (segment2 != null) {
			num13 = -1;
			num14 = -1;

			if (!segment2.IsHorizontal()) {

				index = segment2.GetGridLine().GetIndex();
			} else if (segment2.GetStartIndex() > num) {

				index = segment2.GetStartIndex();
			} else if (segment2.GetEndIndex() < num) {

				index = segment2.GetEndIndex();
			} else {
				index = num;
				num13 = num + 1;

				num14 = grid.GetGridIndex(0, x, false);
				if (num14 < num13) {
					num14 = num13;
				}
			}
			num19 = grid.GetGridCoord(0, index) - (x - minSegDist);
			if (num19 < 0f) {
				num19 = -num19;
			}
			if ((num18 < fMAXVAL) || ((num18 == fMAXVAL) && (num19 < num17))) {
				side = num15;
				fMAXVAL = num18;
				num17 = num19;
				i = index;
				startIndex = closestGridIndex;
				segment = segment2;
				forbiddenMinIndex = num13;
				forbiddenMaxIndex = num14;

				flag = !segment2.IsHorizontal();
			}
		}
		num15 = 1;

		num = grid.GetGridIndex(0, x + minSegDist, true);
		if (x >= num3) {
			num18 = num4 - x;
			if (num18 < 0f) {
				num18 = -num18;
			}
			if ((y < num5) || (num6 < y)) {
				num18 = 100000f - num18;
			}
		} else {
			num18 = FMAXVAL;
		}

		segment2 = grid.SearchClosestSegmentAlive(num, closestGridIndex, 0,
				true);
		if (segment2 != null) {
			num13 = -1;
			num14 = -1;

			if (!segment2.IsHorizontal()) {

				index = segment2.GetGridLine().GetIndex();
			} else if (segment2.GetStartIndex() > num) {

				index = segment2.GetStartIndex();
			} else if (segment2.GetEndIndex() < num) {

				index = segment2.GetEndIndex();
			} else {
				index = num;

				num13 = grid.GetGridIndex(0, x, true);
				num14 = num - 1;
				if (num14 < num13) {
					num13 = num14;
				}
			}
			num19 = grid.GetGridCoord(0, index) - (x + minSegDist);
			if (num19 < 0f) {
				num19 = -num19;
			}
			if ((num18 < fMAXVAL) || ((num18 == fMAXVAL) && (num19 < num17))) {
				side = num15;
				fMAXVAL = num18;
				num17 = num19;
				i = index;
				startIndex = closestGridIndex;
				segment = segment2;
				forbiddenMinIndex = num13;
				forbiddenMaxIndex = num14;

				flag = !segment2.IsHorizontal();
			}
		}
		num15 = 2;

		num = grid.GetClosestGridIndex(0, x);

		closestGridIndex = grid.GetGridIndex(1, y - minSegDist, false);
		if (y <= num6) {
			num18 = num5 - y;
			if (num18 < 0f) {
				num18 = -num18;
			}
			if ((x < num3) || (num4 < x)) {
				num18 = 100000f - num18;
			}
		} else {
			num18 = FMAXVAL;
		}

		segment2 = grid.SearchClosestSegmentAlive(closestGridIndex, num, 1,
				false);
		if (segment2 != null) {
			num13 = -1;
			num14 = -1;

			if (segment2.IsHorizontal()) {

				index = segment2.GetGridLine().GetIndex();
			} else if (segment2.GetStartIndex() > closestGridIndex) {

				index = segment2.GetStartIndex();
			} else if (segment2.GetEndIndex() < closestGridIndex) {

				index = segment2.GetEndIndex();
			} else {
				index = closestGridIndex;
				num13 = closestGridIndex + 1;

				num14 = grid.GetGridIndex(1, y, false);
				if (num14 < num13) {
					num14 = num13;
				}
			}
			num19 = grid.GetGridCoord(1, index) - (y - minSegDist);
			if (num19 < 0f) {
				num19 = -num19;
			}
			if ((num18 < fMAXVAL) || ((num18 == fMAXVAL) && (num19 < num17))) {
				side = num15;
				fMAXVAL = num18;
				num17 = num19;
				i = num;
				startIndex = index;
				segment = segment2;
				forbiddenMinIndex = num13;
				forbiddenMaxIndex = num14;

				flag = segment2.IsHorizontal();
			}
		}
		num15 = 3;

		closestGridIndex = grid.GetGridIndex(1, y + minSegDist, true);
		if (y >= num5) {
			num18 = num6 - y;
			if (num18 < 0f) {
				num18 = -num18;
			}
			if ((x < num3) || (num4 < x)) {
				num18 = 100000f - num18;
			}
		} else {
			num18 = FMAXVAL;
		}

		segment2 = grid.SearchClosestSegmentAlive(closestGridIndex, num, 1,
				true);
		if (segment2 != null) {
			num13 = -1;
			num14 = -1;

			if (segment2.IsHorizontal()) {

				index = segment2.GetGridLine().GetIndex();
			} else if (segment2.GetStartIndex() > closestGridIndex) {

				index = segment2.GetStartIndex();
			} else if (segment2.GetEndIndex() < closestGridIndex) {

				index = segment2.GetEndIndex();
			} else {
				index = closestGridIndex;

				num13 = grid.GetGridIndex(1, y, true);
				num14 = closestGridIndex - 1;
				if (num14 < num13) {
					num13 = num14;
				}
			}
			num19 = grid.GetGridCoord(1, index) - (y + minSegDist);
			if (num19 < 0f) {
				num19 = -num19;
			}
			if ((num18 < fMAXVAL) || ((num18 == fMAXVAL) && (num19 < num17))) {
				side = num15;
				fMAXVAL = num18;
				num17 = num19;
				i = num;
				startIndex = index;
				segment = segment2;
				forbiddenMinIndex = num13;
				forbiddenMaxIndex = num14;

				flag = segment2.IsHorizontal();
			}
		}
		if (segment != null) {
			if (flag) {

				if (segment.IsHorizontal()) {
					LLGridLine gridLine = grid.GetGridLine(0, i);
					gridLine.AddSegment(startIndex, startIndex);

					segment = gridLine.SearchSegment(startIndex);
				} else {
					LLGridLine line2 = grid.GetGridLine(1, startIndex);
					line2.AddSegment(i, i);

					segment = line2.SearchSegment(i);
				}
			}
			this._resPoints = new LLTerminationPoint[] { new LLTerminationPoint(
					i, startIndex, forbiddenMinIndex, forbiddenMaxIndex, 0, 0,
					0, side, segment) };
			this._numberResPoints = 1;
		} else {
			this._numberResPoints = 0;
		}

	}

	private void SearchTerminationPoints(float terminationCoord, float minLen,
			Integer side, Integer minGridIndex, Integer numberOfGridLines,
			Integer sidePreference, LLLink link, java.lang.Object node,
			Boolean origin) {
		if (numberOfGridLines > 0) {
			Boolean flag = null;
			Integer num = null;
			if (side == 0) {
				num = 0;
				flag = false;
				// NOTICE: break ignore!!!
			} else if (side == 1) {
				num = 0;
				flag = true;
				// NOTICE: break ignore!!!
			} else if (side == 3) {
				num = 1;
				flag = true;
				// NOTICE: break ignore!!!
			} else {
				num = 1;
				flag = false;
				// NOTICE: break ignore!!!
			}
			LLGrid grid = this.GetGrid();
			Integer i = grid.GetGridIndexAtMinDist(num, terminationCoord, flag);
			Integer num3 = grid.GetGridIndex(num, terminationCoord + minLen,
					flag);
			Integer num4 = grid.GetNumberOfGridLines(num);
			Integer num5 = grid.GetNumberOfGridLines(1 - num);
			if (flag) {
				if (num3 < i) {
					num3 = i;
				}
			} else if (num3 > i) {
				num3 = i;
			}
			if ((num3 >= 0) && (num3 < num4)) {
				Integer num9 = 0;
				Integer order = sidePreference * this._resPoints.length;
				Integer proposedPenalty = 0;
				Integer num14 = num3 - i;
				if (num14 < 0) {
					num14 = -num14;
				}
				Integer num15 = minGridIndex + ((numberOfGridLines - 1) / 2);
				for (Integer j = 0; j < numberOfGridLines; j++) {
					Integer num8 = num15 + num9;
					if ((num8 < 0) || (num8 >= num5)) {
						if (num9 <= 0) {
							num9 = -num9 + 1;
						} else {
							num9 = -num9;
						}
					} else {
						proposedPenalty = 0;
						LLGridSegment segment = grid.GetGridLine(1 - num, num8)
								.SearchSegment(i);
						if (segment == null) {
							proposedPenalty = num14;
						} else if ((segment.GetStartIndex() > num3)
								|| (segment.GetEndIndex() < num3)) {
							proposedPenalty = num14;
							if (flag) {
								proposedPenalty -= segment.GetEndIndex() - i;
							} else {
								proposedPenalty -= i - segment.GetStartIndex();
							}
							if (proposedPenalty <= 0) {
								proposedPenalty = 1;
							}
						}

						segment = grid.GetGridLine(1 - num, num8)
								.SearchClosestSegment(num3, flag);
						if (segment != null) {
							Integer startIndex = null;
							Integer forbiddenMinIndex = -1;
							Integer forbiddenMaxIndex = -1;
							if (segment.GetStartIndex() > num3) {

								startIndex = segment.GetStartIndex();
								proposedPenalty += startIndex - num3;
							} else if (segment.GetEndIndex() < num3) {

								startIndex = segment.GetEndIndex();
								proposedPenalty += num3 - startIndex;
							} else {
								startIndex = num3;
								if (flag) {
									forbiddenMinIndex = i;
									forbiddenMaxIndex = num3 - 1;
									if (forbiddenMaxIndex < forbiddenMinIndex) {
										forbiddenMinIndex = forbiddenMaxIndex;
									}
								} else {
									forbiddenMinIndex = num3 + 1;
									forbiddenMaxIndex = i;
									if (forbiddenMaxIndex < forbiddenMinIndex) {
										forbiddenMaxIndex = forbiddenMinIndex;
									}
								}
							}

							proposedPenalty = this.GetLayout()
									.GetTerminationPointPenalty(
											link.GetLinkObject(),
											node,
											origin,
											grid.GetGridCoord(0,
													(num == 0) ? startIndex
															: num8),
											grid.GetGridCoord(1,
													(num == 1) ? startIndex
															: num8), side,
											proposedPenalty);
							if (proposedPenalty == MAXVAL) {
								if (this._fallbackPoint == null) {
									this._fallbackPoint = new LLTerminationPoint(
											startIndex, num8,
											forbiddenMinIndex,
											forbiddenMaxIndex, num, 0, order,
											side, segment);
								}
							} else {
								this._resPoints[this._numberResPoints++] = new LLTerminationPoint(
										startIndex, num8, forbiddenMinIndex,
										forbiddenMaxIndex, num,
										proposedPenalty, order, side, segment);
							}
						}
						if (num9 <= 0) {
							num9 = -num9 + 1;
						} else {
							num9 = -num9;
						}
						order++;
					}
				}
			}
		}

	}

}