package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;

public final class LLGrid {
	private float[] _baseCoord;

	private LLGridLine _dirtyLines;

	private LLGridLine _dummyLine;

	private float[] _gridDist;

	private LLGridLine[] _gridLinesX;

	private LLGridLine[] _gridLinesY;

	private Boolean _immediateReorganizationNeeded = false;

	private float[] _minDist;

	private Integer[] _numberOfGridPoints;

	public LLGrid() {
	}

	private Boolean CheckGridPointForLine(Integer i, Integer j,
			InternalPoint p1, InternalPoint p2) {
		float x = 0;
		float num4 = 0;
		float y = 0;
		float num6 = 0;
		float maxValue = 0;
		float num10 = 0;
		float gridCoord = this.GetGridCoord(0, i);
		float num2 = this.GetGridCoord(1, j);
		if (p1.X < p2.X) {
			x = p1.X;
			num4 = p2.X;
		} else {
			x = p2.X;
			num4 = p1.X;
		}
		if (p1.Y < p2.Y) {
			y = p1.Y;
			num6 = p2.Y;
		} else {
			y = p2.Y;
			num6 = p1.Y;
		}
		float num7 = p2.X - p1.X;
		float num8 = p2.Y - p1.Y;
		if (num7 == 0f) {
			maxValue = Float.MAX_VALUE;
			num10 = p1.X;
		} else if (num8 == 0f) {
			maxValue = p1.Y;
			num10 = Float.MAX_VALUE;
		} else {
			maxValue = p1.Y + (((gridCoord - p1.X) * num8) / num7);
			num10 = p1.X + (((num2 - p1.Y) * num7) / num8);
		}
		if ((x <= num10) && (num10 <= num4)) {
			float num11 = num10 - gridCoord;
			if (num11 < 0f) {
				num11 = -num11;
			}
			if (num11 < this._minDist[0]) {

				return true;
			}
		}
		if ((y <= maxValue) && (maxValue <= num6)) {
			float num12 = maxValue - num2;
			if (num12 < 0f) {
				num12 = -num12;
			}
			if (num12 < this._minDist[1]) {

				return true;
			}
		}

		return false;

	}

	public void ClearDirty() {
		LLGridLine nextDirty = null;
		for (LLGridLine line = this._dirtyLines; line != this._dummyLine; line = nextDirty) {

			nextDirty = line.GetNextDirty();
			line.Clear();
		}
		this._dirtyLines = this._dummyLine;

	}

	public void Dispose() {
		Integer num = null;
		this._baseCoord = null;
		this._gridDist = null;
		this._minDist = null;
		for (num = 0; num < this._numberOfGridPoints[0]; num++) {
			this._gridLinesX[num].Dispose();
		}
		for (num = 0; num < this._numberOfGridPoints[1]; num++) {
			this._gridLinesY[num].Dispose();
		}
		this._numberOfGridPoints = null;
		this._dummyLine.Dispose();
		this._dummyLine = null;
		this._dirtyLines = null;

	}

	public Boolean EnsureBorder(Integer n) {
		Integer num7 = null;
		LLGridLine[] lineArray = null;
		LLGridSegment segment = null;
		Integer num = this._numberOfGridPoints[0];
		Integer num2 = this._numberOfGridPoints[1];
		if (num == 0) {

			return false;
		}
		if (num2 == 0) {

			return false;
		}
		Integer ownDelta = 0;
		Integer num4 = 0;
		Integer segmentDelta = 0;
		Integer num6 = 0;
		for (num7 = 0; num7 < n; num7++) {

			if ((num7 >= num) || !this._gridLinesX[num7].IsFree(num2 - 1)) {
				ownDelta = n - num7;
				break;
			}
		}
		for (num7 = 0; num7 < n; num7++) {

			if ((num7 >= num)
					|| !this._gridLinesX[(num - 1) - num7].IsFree(num2 - 1)) {
				num4 = n - num7;
				break;
			}
		}
		for (num7 = 0; num7 < n; num7++) {

			if ((num7 >= num2) || !this._gridLinesY[num7].IsFree(num - 1)) {
				segmentDelta = n - num7;
				break;
			}
		}
		num7 = 0;
		while (num7 < n) {

			if ((num7 >= num2)
					|| !this._gridLinesY[(num2 - 1) - num7].IsFree(num - 1)) {
				num6 = n - num7;
				break;
			}
			num7++;
		}
		Integer num8 = (num + ownDelta) + num4;
		Integer num9 = (num2 + segmentDelta) + num6;
		if ((num8 == num) && (num9 == num2)) {

			return false;
		}
		for (num7 = 0; num7 < num; num7++) {
			this._gridLinesX[num7].ShiftIndex(ownDelta, segmentDelta);
		}
		for (num7 = 0; num7 < num2; num7++) {
			this._gridLinesY[num7].ShiftIndex(segmentDelta, ownDelta);
		}
		if ((ownDelta + num4) != 0) {
			lineArray = new LLGridLine[num8];
			for (num7 = 0; num7 < ownDelta; num7++) {
				lineArray[num7] = new LLGridLine(num9 - 1, num7, false);
				this.MarkDirty(lineArray[num7]);
			}
			for (num7 = 0; num7 < num; num7++) {
				lineArray[num7 + ownDelta] = this._gridLinesX[num7];
			}
			for (num7 = num + ownDelta; num7 < num8; num7++) {
				lineArray[num7] = new LLGridLine(num9 - 1, num7, false);
				this.MarkDirty(lineArray[num7]);
			}
			this._gridLinesX = lineArray;
			this._numberOfGridPoints[0] = num8;
			this._baseCoord[0] -= ownDelta * this._gridDist[0];
		}
		if ((segmentDelta + num6) != 0) {
			lineArray = new LLGridLine[num9];
			for (num7 = 0; num7 < segmentDelta; num7++) {
				lineArray[num7] = new LLGridLine(num8 - 1, num7, true);
				this.MarkDirty(lineArray[num7]);
			}
			for (num7 = 0; num7 < num2; num7++) {
				lineArray[num7 + segmentDelta] = this._gridLinesY[num7];
			}
			for (num7 = num2 + segmentDelta; num7 < num9; num7++) {
				lineArray[num7] = new LLGridLine(num8 - 1, num7, true);
				this.MarkDirty(lineArray[num7]);
			}
			this._gridLinesY = lineArray;
			this._numberOfGridPoints[1] = num9;
			this._baseCoord[1] -= segmentDelta * this._gridDist[1];
		}
		if (ownDelta != 0) {
			for (num7 = 0; num7 < num9; num7++) {

				segment = this._gridLinesY[num7].SearchSegment(ownDelta);
				if (segment == null) {
					this._gridLinesY[num7].AddSegment(0, ownDelta - 1);
				} else {
					segment.SetStartIndex(0);
				}
			}
		}
		if (num4 != 0) {
			for (num7 = 0; num7 < num9; num7++) {

				segment = this._gridLinesY[num7]
						.SearchSegment((num8 - num4) - 1);
				if (segment == null) {
					this._gridLinesY[num7].AddSegment(num8 - num4, num8 - 1);
				} else {
					segment.SetEndIndex(num8 - 1);
				}
			}
		}
		if (segmentDelta != 0) {
			for (num7 = 0; num7 < num8; num7++) {

				segment = this._gridLinesX[num7].SearchSegment(segmentDelta);
				if (segment == null) {
					this._gridLinesX[num7].AddSegment(0, segmentDelta - 1);
				} else {
					segment.SetStartIndex(0);
				}
			}
		}
		if (num6 != 0) {
			for (num7 = 0; num7 < num8; num7++) {

				segment = this._gridLinesX[num7]
						.SearchSegment((num9 - num6) - 1);
				if (segment == null) {
					this._gridLinesX[num7].AddSegment(num9 - num6, num9 - 1);
				} else {
					segment.SetEndIndex(num9 - 1);
				}
			}
		}

		return true;

	}

	public Integer GetClosestGridIndex(Integer dir, float coord) {
		Integer i = this.GetGridIndex(dir, coord, false);
		Integer num2 = this.GetGridIndex(dir, coord, true);
		float num3 = coord - this.GetGridCoord(dir, i);
		float num4 = this.GetGridCoord(dir, num2) - coord;
		if (num3 < num4) {

			return i;
		}

		return num2;

	}

	public float GetGridCoord(Integer dir, Integer i) {

		return (this._baseCoord[dir] + (i * this._gridDist[dir]));

	}

	public float GetGridDist(Integer dir) {

		return this._gridDist[dir];

	}

	public Integer GetGridIndex(Integer dir, float coord, Boolean larger) {
		float num = (coord - this.GetMinCoord(dir)) / this._gridDist[dir];
		if (larger) {

			return (int) Math.Ceiling((double) num);
		}

		return (int) Math.Floor((double) num);

	}

	public Integer GetGridIndexAtMinDist(Integer dir, float coord,
			Boolean larger) {
		Integer num2 = null;
		float num = (coord - this.GetMinCoord(dir)) / this._gridDist[dir];
		if (larger) {
			num2 = (int) Math.Floor((double) num);
			while ((this.GetGridCoord(dir, num2) - coord) < this._minDist[dir]) {
				num2++;
			}

			return num2;
		}
		num2 = (int) Math.Ceiling((double) num);
		while ((coord - this.GetGridCoord(dir, num2)) < this._minDist[dir]) {
			num2--;
		}

		return num2;

	}

	public LLGridLine GetGridLine(Integer dir, Integer i) {
		if (dir == 0) {

			return this._gridLinesX[i];
		}

		return this._gridLinesY[i];

	}

	public float GetMaxCoord(Integer dir) {

		return (this._baseCoord[dir] + (this._numberOfGridPoints[dir] * this._gridDist[dir]));

	}

	public static float GetMaxCoord(InternalRect rect, Integer dir) {
		if (dir != 0) {

			return (rect.Y + rect.Height);
		}

		return (rect.X + rect.Width);

	}

	public Integer GetMaxGridIndexAtSide(InternalRect rect, Integer dir,
			float minCornerOffset) {
		float coord = GetMaxCoord(rect, dir) - minCornerOffset;
		float size = GetSize(rect, dir);
		if (size >= (2f * minCornerOffset)) {

			return this.GetGridIndex(dir, coord, false);
		}
		Integer num3 = this.GetGridIndex(dir, (coord + minCornerOffset) - size,
				true);
		Integer num4 = this.GetGridIndex(dir, coord + minCornerOffset, false);
		if (num3 < num4) {

			return ((num3 + num4) / 2);
		}

		return num4;

	}

	public float GetMinCoord(Integer dir) {

		return this._baseCoord[dir];

	}

	public static float GetMinCoord(InternalRect rect, Integer dir) {
		if (dir != 0) {

			return rect.Y;
		}

		return rect.X;

	}

	public Integer GetMinGridIndexAtSide(InternalRect rect, Integer dir,
			float minCornerOffset) {
		float coord = GetMinCoord(rect, dir) + minCornerOffset;
		float size = GetSize(rect, dir);
		if (size >= (2f * minCornerOffset)) {

			return this.GetGridIndex(dir, coord, true);
		}
		Integer num3 = this.GetGridIndex(dir, coord - minCornerOffset, true);
		Integer num4 = this.GetGridIndex(dir, (coord - minCornerOffset) + size,
				false);
		if (num3 < num4) {

			return ((num3 + num4) / 2);
		}

		return num3;

	}

	public Integer GetNumberOfGridLines(Integer dir) {

		return this._numberOfGridPoints[dir];

	}

	public static float GetSize(InternalRect rect, Integer dir) {
		if (dir != 0) {

			return rect.Height;
		}

		return rect.Width;

	}

	public void Init(float baseCoordX, float baseCoordY, float gridDistX,
			float gridDistY, float minDistX, float minDistY, Integer gridSizeX,
			Integer gridSizeY) {
		Integer num = null;
		this._baseCoord = new float[] { baseCoordX, baseCoordY };
		this._gridDist = new float[] { (gridDistX > 1f) ? gridDistX : 1f,
				(gridDistY > 1f) ? gridDistY : 1f };
		this._minDist = new float[] { (minDistX > 1f) ? minDistX : 1f,
				(minDistY > 1f) ? minDistY : 1f };
		this._numberOfGridPoints = new Integer[] { gridSizeX, gridSizeY };
		this._gridLinesX = new LLGridLine[gridSizeX];
		this._gridLinesY = new LLGridLine[gridSizeY];
		for (num = 0; num < gridSizeX; num++) {
			this._gridLinesX[num] = new LLGridLine(gridSizeY - 1, num, false);
		}
		for (num = 0; num < gridSizeY; num++) {
			this._gridLinesY[num] = new LLGridLine(gridSizeX - 1, num, true);
		}
		this._dummyLine = new LLGridLine(1, -1, false);
		this._dirtyLines = this._dummyLine;
		this._immediateReorganizationNeeded = false;

	}

	public Boolean IsImmediateReorganizationNeeded() {

		return this._immediateReorganizationNeeded;

	}

	public Boolean IsWithinRectObstacle(Integer xindex, Integer yindex,
			InternalRect rect) {
		Integer num = this.GetGridIndexAtMinDist(0, rect.X, false);
		Integer num2 = this.GetGridIndexAtMinDist(0, rect.X + rect.Width, true);
		Integer num3 = this.GetGridIndexAtMinDist(1, rect.Y, false);
		Integer num4 = this
				.GetGridIndexAtMinDist(1, rect.Y + rect.Height, true);
		num++;
		num2--;
		num3++;
		num4--;

		return ((((num <= xindex) && (xindex <= num2)) && (num3 <= yindex)) && (yindex <= num4));

	}

	public Boolean IsWithoutObstacles(Integer dir, Integer index,
			Integer[] limits) {
		LLGridLine gridLine = this.GetGridLine(1 - dir, index);
		if (gridLine == null) {

			return false;
		}
		LLGridSegment segment = gridLine.SearchSegment(limits[0]);
		if (segment == null) {

			return false;
		}
		if (segment.GetStartIndex() > limits[1]) {

			return false;
		}
		if (segment.GetEndIndex() < limits[1]) {

			return false;
		}

		return true;

	}

	public void MarkDirty(LLGridLine gridLine) {

		if (!gridLine.IsDirty()) {
			gridLine.SetNextDirty(this._dirtyLines);
			this._dirtyLines = gridLine;
		}

	}

	public void RemoveObstacle(Integer dir, Integer index, Integer[] limits,
			LLLink link, Integer i) {
		if (link.GetSegmentDir(i) == dir) {
			LLGridLine gridLine = this.GetGridLine(1 - dir, index);
			Boolean flag = true;
			if (gridLine != null) {
				LLGridSegment segment = null;
				Integer num = (int) Math.Min(limits[0], limits[1]);
				Integer num2 = (int) Math.Max(limits[0], limits[1]);
				if (link.GetSegments()[i] >= 0) {

					if (!link.StartsAtObstacle(i)) {

						segment = gridLine.SearchSegment(num - 1);
						if (segment != null) {
							segment.SetEndIndex(num2);
							flag = false;
						}
					}

					if (flag && !link.EndsAtObstacle(i)) {

						segment = gridLine.SearchSegment(num2 + 1);
						if (segment != null) {
							segment.SetStartIndex(num);
							flag = false;
						}
					}
					if (flag) {
						gridLine.AddSegment(num, num2);
					}
				} else {

					if (!link.EndsAtObstacle(i)) {

						segment = gridLine.SearchSegment(num - 1);
						if (segment != null) {
							segment.SetEndIndex(num2);
							flag = false;
						}
					}

					if (flag && !link.StartsAtObstacle(i)) {

						segment = gridLine.SearchSegment(num2 + 1);
						if (segment != null) {
							segment.SetStartIndex(num);
							flag = false;
						}
					}
					if (flag) {
						gridLine.AddSegment(num, num2);
					}
				}
				this.MarkDirty(gridLine);
			}
		}

	}

	public void ReorganizeAllBeforeSearch() {
		Integer num3 = null;
		Integer num = this._numberOfGridPoints[0];
		Integer num2 = this._numberOfGridPoints[1];
		for (num3 = 0; num3 < num; num3++) {
			this._gridLinesX[num3].ReorganizeBeforeSearch(false);
		}
		for (num3 = 0; num3 < num2; num3++) {
			this._gridLinesY[num3].ReorganizeBeforeSearch(false);
		}
		this._dirtyLines = this._dummyLine;

	}

	public void ReorganizeDirtyBeforeSearch() {
		LLGridLine nextDirty = null;
		for (LLGridLine line = this._dirtyLines; line != this._dummyLine; line = nextDirty) {

			nextDirty = line.GetNextDirty();
			line.ReorganizeBeforeSearch(false);
		}
		this._dirtyLines = this._dummyLine;

	}

	public LLGridSegment SearchClosestSegmentAlive(Integer index0,
			Integer index1, Integer dir, Boolean larger) {
		LLGridSegment segment2 = null;
		Integer num = null;
		Integer endIndex = null;
		if ((index0 < 0) || (index0 >= this._numberOfGridPoints[dir])) {

			return null;
		}
		if ((index1 < 0) || (index1 >= this._numberOfGridPoints[1 - dir])) {

			return null;
		}
		LLGridSegment segment = this.GetGridLine(1 - dir, index1)
				.SearchClosestSegmentAlive(this, index0, larger);
		Integer num3 = this._numberOfGridPoints[dir];
		if (larger) {
			num = (index0 > 0) ? index0 : 0;
			endIndex = num3;
		} else {
			num = (index0 < num3) ? index0 : (num3 - 1);
			endIndex = -1;
		}
		if (segment != null) {
			if (index0 >= segment.GetStartIndex()) {
				if (index0 <= segment.GetEndIndex()) {

					return segment;
				}

				endIndex = segment.GetEndIndex();
			} else {

				endIndex = segment.GetStartIndex();
			}
		}
		if (larger) {
			for (Integer j = num; j < endIndex; j++) {

				segment2 = this.GetGridLine(dir, j).SearchSegment(index1);

				if ((segment2 != null)
						&& !segment2.IsDeadlock(this, 0, 0x7fffffff)) {

					return segment2;
				}
			}

			return segment;
		}
		for (Integer i = num; i > endIndex; i--) {

			segment2 = this.GetGridLine(dir, i).SearchSegment(index1);

			if ((segment2 != null) && !segment2.IsDeadlock(this, 0, 0x7fffffff)) {

				return segment2;
			}
		}

		return segment;

	}

	public void SetGridLineObstacle(Boolean isHorizontal, Integer index,
			Integer[] limits) {
		if (isHorizontal) {
			this._gridLinesY[index].SetObstacle(this, limits[0], limits[1]);
		} else {
			this._gridLinesX[index].SetObstacle(this, limits[0], limits[1]);
		}

	}

	public void SetImmediateReorganizationNeeded(Boolean flag) {
		this._immediateReorganizationNeeded = flag;

	}

	public void SetObstacle(InternalPoint point) {
		float x = point.X;
		Integer i = (int) Math
				.Ceiling((double) ((x - this.GetMinCoord(0)) / this._gridDist[0]));
		while ((x - this.GetGridCoord(0, i)) < this._minDist[0]) {
			i--;
		}
		Integer num2 = i;
		while ((this.GetGridCoord(0, num2) - x) < this._minDist[0]) {
			num2++;
		}
		x = point.Y;
		Integer num3 = (int) Math
				.Ceiling((double) ((x - this.GetMinCoord(1)) / this._gridDist[1]));
		while ((x - this.GetGridCoord(1, num3)) < this._minDist[1]) {
			num3--;
		}
		Integer num4 = num3;
		while ((this.GetGridCoord(1, num4) - x) < this._minDist[1]) {
			num4++;
		}
		i++;
		num2--;
		num3++;
		num4--;
		if (i < 0) {
			i = 0;
		}
		if (num3 < 0) {
			num3 = 0;
		}
		if (num2 >= this._numberOfGridPoints[0]) {
			num2 = this._numberOfGridPoints[0] - 1;
		}
		if (num4 >= this._numberOfGridPoints[1]) {
			num4 = this._numberOfGridPoints[1] - 1;
		}
		this.StoreRectObstacle(i, num2, num3, num4);

	}

	public void SetObstacle(InternalRect rect) {
		Integer num = this.GetGridIndexAtMinDist(0, rect.X, false);
		Integer num2 = this.GetGridIndexAtMinDist(0, rect.X + rect.Width, true);
		if (num < num2) {
			Integer num3 = this.GetGridIndexAtMinDist(1, rect.Y, false);
			Integer num4 = this.GetGridIndexAtMinDist(1, rect.Y + rect.Height,
					true);
			if (num3 < num4) {
				num++;
				num2--;
				num3++;
				num4--;
				if (num < 0) {
					num = 0;
				}
				if (num3 < 0) {
					num3 = 0;
				}
				if (num2 >= this._numberOfGridPoints[0]) {
					num2 = this._numberOfGridPoints[0] - 1;
				}
				if (num4 >= this._numberOfGridPoints[1]) {
					num4 = this._numberOfGridPoints[1] - 1;
				}
				this.StoreRectObstacle(num, num2, num3, num4);
			}
		}

	}

	public void SetObstacle(InternalPoint p1, InternalPoint p2) {
		this.SetObstacle(p1);
		if ((p1.X != p2.X) || (p1.Y != p2.Y)) {
			this.SetObstacle(p2);
			Boolean flag = false;
			Boolean flag2 = false;
			float num = p2.X - p1.X;
			float num2 = p2.Y - p1.Y;
			float num3 = (num > 0f) ? num : -num;
			float num4 = (num2 > 0f) ? num2 : -num2;
			if (num4 == 0f) {
				flag = true;
			} else if (num3 == 0f) {
				flag2 = true;
			} else if (num3 > num4) {
				float num5 = num2 / num;
				if ((-0.2f < num5) && (num5 < 0.2f)) {
					flag = true;
				}
			} else {
				float num6 = num / num2;
				if ((-0.2f < num6) && (num6 < 0.2f)) {
					flag2 = true;
				}
			}
			if (flag || flag2) {
				float num11 = (num > 0f) ? p1.X : p2.X;
				Integer i = (int) Math.Floor((double) ((num11 - this
						.GetMinCoord(0)) / this._gridDist[0]));
				while ((num11 - this.GetGridCoord(0, i)) >= this._minDist[0]) {
					i++;
				}
				num11 = (num > 0f) ? p2.X : p1.X;
				Integer num8 = (int) Math.Ceiling((double) ((num11 - this
						.GetMinCoord(0)) / this._gridDist[0]));
				while ((this.GetGridCoord(0, num8) - num11) >= this._minDist[0]) {
					num8--;
				}
				num11 = (num2 > 0f) ? p1.Y : p2.Y;
				Integer num9 = (int) Math.Floor((double) ((num11 - this
						.GetMinCoord(1)) / this._gridDist[1]));
				while ((num11 - this.GetGridCoord(1, num9)) >= this._minDist[1]) {
					num9++;
				}
				num11 = (num2 > 0f) ? p2.Y : p1.Y;
				Integer num10 = (int) Math.Ceiling((double) ((num11 - this
						.GetMinCoord(1)) / this._gridDist[1]));
				while ((this.GetGridCoord(1, num10) - num11) >= this._minDist[1]) {
					num10--;
				}
				if (i < 0) {
					i = 0;
				}
				if (num9 < 0) {
					num9 = 0;
				}
				if (num8 >= this._numberOfGridPoints[0]) {
					num8 = this._numberOfGridPoints[0] - 1;
				}
				if (num10 >= this._numberOfGridPoints[1]) {
					num10 = this._numberOfGridPoints[1] - 1;
				}
				for (Integer j = i; j <= num8; j++) {
					for (Integer k = num9; k <= num10; k++) {

						if (this.CheckGridPointForLine(j, k, p1, p2)) {
							if (flag) {
								this._gridLinesY[k].SetObstacle(this, j, j);
							} else {
								this._gridLinesX[j].SetObstacle(this, k, k);
							}
						}
					}
				}
			}
		}

	}

	private void StoreRectObstacle(Integer gridLineX0, Integer gridLineX1,
			Integer gridLineY0, Integer gridLineY1) {
		Integer num = null;
		if (gridLineY1 >= (gridLineY0 - 1)) {
			for (num = gridLineX0; num <= gridLineX1; num++) {
				this._gridLinesX[num].SetObstacle(this, gridLineY0, gridLineY1);
			}
		}
		if (gridLineX1 >= (gridLineX0 - 1)) {
			for (num = gridLineY0; num <= gridLineY1; num++) {
				this._gridLinesY[num].SetObstacle(this, gridLineX0, gridLineX1);
			}
		}

	}

}