package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;
import system.Collections.*;

public final class LLFallbackRouteAlgorithm {
	private LLGrid _grid;

	private LongLinkLayout _layout;

	private ArrayList _points;

	public LLFallbackRouteAlgorithm(LongLinkLayout layout, LLGrid grid) {
		this._layout = layout;
		this._grid = grid;
		this._points = new ArrayList(3);
	}

	private void AddPoint(Integer dir, Integer indexInDir, Integer otherIndex) {
		if (dir == 0) {
			this._points.Add(new InternalPoint((float) indexInDir,
					(float) otherIndex));
		} else {
			this._points.Add(new InternalPoint((float) otherIndex,
					(float) indexInDir));
		}

	}

	public void Dispose() {
		this._points = null;
		this._layout = null;
		this._grid = null;

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

	public Boolean Route(LLLink link) {
		LongLinkLayout layout = this.GetLayout();
		link.CleanRoute();
		this._points.Clear();
		LLTerminationPoint[] startPoints = link.GetStartPoints();
		LLTerminationPoint[] endPoints = link.GetEndPoints();
		Integer length = startPoints.length;
		Integer num2 = endPoints.length;
		InternalRect fromRect = link.GetFromRect();
		InternalRect toRect = link.GetToRect();
		Integer[] startPref = new Integer[4];
		Integer[] endPref = new Integer[4];
		float minStartSegmentLength = layout.get_MinStartSegmentLength();
		float minEndSegmentLength = layout.get_MinEndSegmentLength();
		LLGrid grid = this.GetGrid();
		if ((length <= 0) || (num2 <= 0)) {
			Integer num11 = null;
			Integer num12 = null;
			Integer num14 = null;
			Integer num15 = null;
			startPref[3] = num11 = 0;
			startPref[2] = num12 = num11;
			startPref[0] = startPref[1] = num12;
			endPref[3] = num14 = 0;
			endPref[2] = num15 = num14;
			endPref[0] = endPref[1] = num15;
			LLTerminationCandAlgorithm.CalcSidePrefInternal(fromRect.X
					- minStartSegmentLength, (fromRect.X + fromRect.Width)
					+ minStartSegmentLength, toRect.X - minEndSegmentLength,
					(toRect.X + toRect.Width) + minEndSegmentLength, 1, 3,
					startPref, endPref);
			LLTerminationCandAlgorithm.CalcSidePrefInternal(fromRect.Y
					- minStartSegmentLength, (fromRect.Y + fromRect.Height)
					+ minStartSegmentLength, toRect.Y - minEndSegmentLength,
					(toRect.Y + toRect.Height) + minEndSegmentLength, 0, 2,
					startPref, endPref);
		}
		if (length <= 0) {
			startPoints = new LLTerminationPoint[1];
			Integer num5 = -1;
			Integer num6 = 0;
			for (Integer i = 0; i < 4; i++) {
				if (startPref[i] > num5) {
					num5 = startPref[i];
					num6 = i;
				}
			}
			if (num6 == 0) {
				startPoints[0] = new LLTerminationPoint(grid.GetGridIndex(0,
						fromRect.X + (0.5f * fromRect.Width), true),
						grid.GetGridIndex(1,
								fromRect.Y - minStartSegmentLength, false), 0,
						0, 0, 0, 0, 2, null);
				// NOTICE: break ignore!!!
			} else if (num6 == 1) {
				startPoints[0] = new LLTerminationPoint(grid.GetGridIndex(0,
						fromRect.X - minStartSegmentLength, false),
						grid.GetGridIndex(1, fromRect.Y
								+ (0.5f * fromRect.Width), true), 0, 0, 0, 0,
						0, 0, null);
				// NOTICE: break ignore!!!
			} else if (num6 == 2) {
				startPoints[0] = new LLTerminationPoint(grid.GetGridIndex(0,
						(fromRect.X + fromRect.Width) + minStartSegmentLength,
						true), grid.GetGridIndex(1, fromRect.Y
						+ (0.5f * fromRect.Width), true), 0, 0, 0, 0, 0, 1,
						null);
				// NOTICE: break ignore!!!
			} else {
				startPoints[0] = new LLTerminationPoint(grid.GetGridIndex(0,
						fromRect.X + (0.5f * fromRect.Width), true),
						grid.GetGridIndex(1, (fromRect.Y + fromRect.Width)
								+ minStartSegmentLength, true), 0, 0, 0, 0, 0,
						3, null);
				// NOTICE: break ignore!!!
			}
			link.SetStartPoints(startPoints, 1);
			length = link.GetStartPoints().length;
		}
		if (num2 <= 0) {
			endPoints = new LLTerminationPoint[1];
			Integer num8 = -1;
			Integer num9 = 0;
			for (Integer j = 0; j < 4; j++) {
				if (endPref[j] > num8) {
					num8 = endPref[j];
					num9 = j;
				}
			}
			if (num9 == 0) {
				endPoints[0] = new LLTerminationPoint(grid.GetGridIndex(0,
						toRect.X + (0.5f * toRect.Width), true),
						grid.GetGridIndex(1, toRect.Y - minEndSegmentLength,
								false), 0, 0, 0, 0, 0, 2, null);
				// NOTICE: break ignore!!!
			} else if (num9 == 1) {
				endPoints[0] = new LLTerminationPoint(grid.GetGridIndex(0,
						toRect.X - minEndSegmentLength, false),
						grid.GetGridIndex(1, toRect.Y + (0.5f * toRect.Width),
								true), 0, 0, 0, 0, 0, 0, null);
				// NOTICE: break ignore!!!
			} else if (num9 == 2) {
				endPoints[0] = new LLTerminationPoint(grid.GetGridIndex(0,
						(toRect.X + toRect.Width) + minEndSegmentLength, true),
						grid.GetGridIndex(1, toRect.Y + (0.5f * toRect.Width),
								true), 0, 0, 0, 0, 0, 1, null);
				// NOTICE: break ignore!!!
			} else {
				endPoints[0] = new LLTerminationPoint(grid.GetGridIndex(0,
						toRect.X + (0.5f * toRect.Width), true),
						grid.GetGridIndex(1, (toRect.Y + toRect.Width)
								+ minEndSegmentLength, true), 0, 0, 0, 0, 0, 3,
						null);
				// NOTICE: break ignore!!!
			}
			link.SetEndPoints(endPoints, 1);
			num2 = link.GetEndPoints().length;
		}
		LLTerminationPoint startPoint = link.GetStartPoints()[0];
		LLTerminationPoint endPoint = link.GetEndPoints()[0];
		link.SetActStartAndEndCandidates(0, 0);
		if ((link.IsSelfLoop() && (startPoint.GetIndex(0) == endPoint
				.GetIndex(0)))
				&& (startPoint.GetIndex(1) == endPoint.GetIndex(1))) {
			if (num2 >= 2) {
				endPoint = link.GetEndPoints()[1];
				link.SetActStartAndEndCandidates(0, 1);
			} else if (length >= 2) {
				startPoint = link.GetStartPoints()[1];
				link.SetActStartAndEndCandidates(1, 0);
			}
		}
		if (startPoint.GetSide() == 0) {
			if (endPoint.GetSide() == 0) {
				this.RouteSameSide(link, startPoint, endPoint, 0, false);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 1) {
				this.RouteOppositeSide(link, startPoint, endPoint, 0, false);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 2) {
				this.RouteAdjacentSide(link, startPoint, endPoint, 0, false,
						false);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 3) {
				this.RouteAdjacentSide(link, startPoint, endPoint, 0, false,
						true);
				// NOTICE: break ignore!!!
			}
			// NOTICE: break ignore!!!
		} else if (startPoint.GetSide() == 1) {
			if (endPoint.GetSide() == 0) {
				this.RouteOppositeSide(link, startPoint, endPoint, 0, true);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 1) {
				this.RouteSameSide(link, startPoint, endPoint, 0, true);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 2) {
				this.RouteAdjacentSide(link, startPoint, endPoint, 0, true,
						false);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 3) {
				this.RouteAdjacentSide(link, startPoint, endPoint, 0, true,
						true);
				// NOTICE: break ignore!!!
			}
			// NOTICE: break ignore!!!
		} else if (startPoint.GetSide() == 2) {
			if (endPoint.GetSide() == 0) {
				this.RouteAdjacentSide(link, startPoint, endPoint, 1, false,
						false);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 1) {
				this.RouteAdjacentSide(link, startPoint, endPoint, 1, false,
						true);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 2) {
				this.RouteSameSide(link, startPoint, endPoint, 1, false);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 3) {
				this.RouteOppositeSide(link, startPoint, endPoint, 1, false);
				// NOTICE: break ignore!!!
			}
			// NOTICE: break ignore!!!
		} else if (startPoint.GetSide() == 3) {
			if (endPoint.GetSide() == 0) {
				this.RouteAdjacentSide(link, startPoint, endPoint, 1, true,
						false);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 1) {
				this.RouteAdjacentSide(link, startPoint, endPoint, 1, true,
						true);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 2) {
				this.RouteOppositeSide(link, startPoint, endPoint, 1, true);
				// NOTICE: break ignore!!!
			} else if (endPoint.GetSide() == 3) {
				this.RouteSameSide(link, startPoint, endPoint, 1, true);
				// NOTICE: break ignore!!!
			}
			// NOTICE: break ignore!!!
		}
		link.StoreRoute(this.GetPoints());

		return true;

	}

	private void RouteAdjacentSide(LLLink link, LLTerminationPoint startPoint,
			LLTerminationPoint endPoint, Integer dir, Boolean startLargerSide,
			Boolean endLargerSide) {
		LLGrid grid = this.GetGrid();
		Integer num = 1 - dir;
		Integer index = startPoint.GetIndex(num);
		Integer num3 = startPoint.GetIndex(dir);
		Integer num4 = endPoint.GetIndex(num);
		Integer otherIndex = endPoint.GetIndex(dir);
		if (((startLargerSide && (otherIndex >= num3)) || (!startLargerSide && (otherIndex <= num3)))
				&& ((endLargerSide && (index >= num4)) || (!endLargerSide && (index <= num4)))) {
			this.AddPoint(num, index, otherIndex);
		} else {
			Integer indexInDir = num4;
			Integer num7 = num3;
			InternalRect fromRect = link.GetFromRect();
			Integer num8 = grid.GetGridIndexAtMinDist(num,
					LLGrid.GetMinCoord(fromRect, num), false);
			Integer num9 = grid.GetGridIndexAtMinDist(num,
					LLGrid.GetMaxCoord(fromRect, num), true);
			if ((num4 > num8) && (num4 < num9)) {
				Integer num10 = null;
				if (startLargerSide) {

					num10 = grid.GetGridIndexAtMinDist(dir,
							LLGrid.GetMaxCoord(fromRect, dir), true);
					if (otherIndex <= num10) {
						if (endLargerSide) {
							indexInDir = num9;
						} else {
							indexInDir = num8;
						}
					}
				} else {

					num10 = grid.GetGridIndexAtMinDist(dir,
							LLGrid.GetMinCoord(fromRect, dir), false);
					if (otherIndex >= num10) {
						if (endLargerSide) {
							indexInDir = num9;
						} else {
							indexInDir = num8;
						}
					}
				}
			}
			InternalRect toRect = link.GetToRect();
			Integer num11 = grid.GetGridIndexAtMinDist(dir,
					LLGrid.GetMinCoord(toRect, dir), false);
			Integer num12 = grid.GetGridIndexAtMinDist(dir,
					LLGrid.GetMaxCoord(toRect, dir), true);
			if ((num3 > num11) && (num3 < num12)) {
				Integer num13 = null;
				if (endLargerSide) {

					num13 = grid.GetGridIndexAtMinDist(num,
							LLGrid.GetMaxCoord(toRect, num), true);
					if (index <= num13) {
						if (startLargerSide) {
							num7 = num12;
						} else {
							num7 = num11;
						}
					}
				} else {

					num13 = grid.GetGridIndexAtMinDist(num,
							LLGrid.GetMinCoord(toRect, num), false);
					if (index >= num13) {
						if (startLargerSide) {
							num7 = num12;
						} else {
							num7 = num11;
						}
					}
				}
			}
			this.AddPoint(num, indexInDir, otherIndex);
			this.AddPoint(num, indexInDir, num7);
			this.AddPoint(num, index, num7);
		}

	}

	private void RouteOppositeSide(LLLink link, LLTerminationPoint startPoint,
			LLTerminationPoint endPoint, Integer dir, Boolean startLargerSide) {
		LLGrid grid = this.GetGrid();
		Integer num = 1 - dir;
		Integer index = startPoint.GetIndex(num);
		Integer otherIndex = startPoint.GetIndex(dir);
		Integer indexInDir = endPoint.GetIndex(num);
		Integer num5 = endPoint.GetIndex(dir);
		InternalRect fromRect = link.GetFromRect();
		InternalRect toRect = link.GetToRect();
		Integer num6 = grid.GetGridIndexAtMinDist(dir,
				LLGrid.GetMinCoord(fromRect, dir), false);
		Integer num7 = grid.GetGridIndexAtMinDist(dir,
				LLGrid.GetMaxCoord(fromRect, dir), true);
		Integer num8 = grid.GetGridIndexAtMinDist(dir,
				LLGrid.GetMinCoord(toRect, dir), false);
		Integer num9 = grid.GetGridIndexAtMinDist(dir,
				LLGrid.GetMaxCoord(toRect, dir), true);
		if ((index != indexInDir)
				|| ((!startLargerSide || (num7 > (num8 + 1))) && (startLargerSide || (num6 < (num9 - 1))))) {
			if ((startLargerSide && (otherIndex <= num5))
					|| (!startLargerSide && (otherIndex >= num5))) {
				if (index != indexInDir) {
					Integer num10 = (otherIndex + num5) / 2;
					this.AddPoint(num, indexInDir, num10);
					this.AddPoint(num, index, num10);
				}
			} else {
				Integer num11 = grid.GetGridIndexAtMinDist(num,
						LLGrid.GetMinCoord(fromRect, num), false);
				Integer num12 = grid.GetGridIndexAtMinDist(num,
						LLGrid.GetMaxCoord(fromRect, num), true);
				Integer num13 = grid.GetGridIndexAtMinDist(num,
						LLGrid.GetMinCoord(toRect, num), false);
				Integer num14 = grid.GetGridIndexAtMinDist(num,
						LLGrid.GetMaxCoord(toRect, num), true);
				Integer num15 = (int) ((Math.Min(num12, num14) + Math.Max(
						num11, num13)) / 2);
				if (num15 >= Math.Min(num12, num14)) {
					this.AddPoint(num, indexInDir, num5);
					this.AddPoint(num, num15, num5);
					this.AddPoint(num, num15, otherIndex);
					this.AddPoint(num, index, otherIndex);
				} else {
					Integer num18 = null;
					Integer num16 = otherIndex;
					Integer num17 = num5;
					Integer num19 = (int) Math.Max(num12, num14);
					Integer num20 = (int) Math.Min(num11, num13);
					if ((((2 * num19) - index) - indexInDir) < ((index + indexInDir) - (2 * num20))) {
						num18 = num19;
						if (num12 < num14) {
							if (startLargerSide && (num9 > num16)) {
								num16 = num9;
							} else if (!startLargerSide && (num8 < num16)) {
								num16 = num8;
							}
						} else if (!startLargerSide && (num7 > num17)) {
							num17 = num7;
						} else if (startLargerSide && (num6 < num17)) {
							num17 = num6;
						}
					} else {
						num18 = num20;
						if (num12 > num14) {
							if (startLargerSide && (num9 > num16)) {
								num16 = num9;
							} else if (!startLargerSide && (num8 < num16)) {
								num16 = num8;
							}
						} else if (!startLargerSide && (num7 > num17)) {
							num17 = num7;
						} else if (startLargerSide && (num6 < num17)) {
							num17 = num6;
						}
					}
					this.AddPoint(num, indexInDir, num17);
					this.AddPoint(num, num18, num17);
					this.AddPoint(num, num18, num16);
					this.AddPoint(num, index, num16);
				}
			}
		}

	}

	private void RouteSameSide(LLLink link, LLTerminationPoint startPoint,
			LLTerminationPoint endPoint, Integer dir, Boolean largerSide) {
		Integer num10 = null;
		LLGrid grid = this.GetGrid();
		Integer num = 1 - dir;
		Integer index = startPoint.GetIndex(num);
		Integer num3 = startPoint.GetIndex(dir);
		Integer indexInDir = endPoint.GetIndex(num);
		Integer num5 = endPoint.GetIndex(dir);
		InternalRect fromRect = link.GetFromRect();
		InternalRect toRect = link.GetToRect();
		Integer num6 = grid.GetGridIndexAtMinDist(num,
				LLGrid.GetMinCoord(fromRect, num), false);
		Integer num7 = grid.GetGridIndexAtMinDist(num,
				LLGrid.GetMaxCoord(fromRect, num), true);
		Integer num8 = grid.GetGridIndexAtMinDist(num,
				LLGrid.GetMinCoord(toRect, num), false);
		Integer num9 = grid.GetGridIndexAtMinDist(num,
				LLGrid.GetMaxCoord(toRect, num), true);
		if (largerSide) {

			num10 = (int) Math.Max(
					grid.GetGridIndexAtMinDist(dir,
							LLGrid.GetMaxCoord(fromRect, dir), true),
					grid.GetGridIndexAtMinDist(dir,
							LLGrid.GetMaxCoord(toRect, dir), true));
		} else {

			num10 = (int) Math.Min(
					grid.GetGridIndexAtMinDist(dir,
							LLGrid.GetMinCoord(fromRect, dir), false),
					grid.GetGridIndexAtMinDist(dir,
							LLGrid.GetMinCoord(toRect, dir), false));
		}
		if ((((link.IsSelfLoop() || (num3 == num5)) || ((largerSide && (num3 >= num10)) && (num5 >= num10))) || ((!largerSide && (num3 <= num10)) && (num5 <= num10)))
				|| (((index <= num8) || (index >= num9)) && ((indexInDir <= num6) || (indexInDir >= num7)))) {
			Integer num11 = null;
			if (largerSide) {

				num11 = (int) Math.Max(num3, num5);
			} else {

				num11 = (int) Math.Min(num3, num5);
			}
			this.AddPoint(num, indexInDir, num11);
			this.AddPoint(num, index, num11);
		} else {
			Integer num12 = null;
			if ((largerSide && (num3 > num5)) || (!largerSide && (num3 < num5))) {
				if (indexInDir > ((num6 + num7) / 2)) {
					num12 = num7;
				} else {
					num12 = num6;
				}
			} else if (index > ((num8 + num9) / 2)) {
				num12 = num9;
			} else {
				num12 = num8;
			}
			this.AddPoint(num, indexInDir, num5);
			this.AddPoint(num, num12, num5);
			this.AddPoint(num, num12, num3);
			this.AddPoint(num, index, num3);
		}

	}

}