package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class LLTerminationPoint {
	private Integer _forbiddenMaxIndex;

	private Integer _forbiddenMinIndex;

	private Integer[] _index;

	private Integer _internalOrder;

	private Integer _penalty;

	private LLGridSegment _segment;

	private Integer _side;

	public LLTerminationPoint() {
		this._index = new Integer[2];
	}

	public LLTerminationPoint(Integer index0, Integer index1,
			Integer forbiddenMinIndex, Integer forbiddenMaxIndex, Integer dir0,
			Integer penalty, Integer order, Integer side, LLGridSegment segment) {
		this._index = new Integer[2];
		this._index[dir0] = index0;
		this._index[1 - dir0] = index1;
		this._penalty = penalty;
		this._internalOrder = order;
		this._side = side;
		this._segment = segment;
		if (forbiddenMinIndex <= forbiddenMaxIndex) {
			this._forbiddenMinIndex = forbiddenMinIndex;
			this._forbiddenMaxIndex = forbiddenMaxIndex;
		} else {
			this._forbiddenMinIndex = forbiddenMaxIndex;
			this._forbiddenMaxIndex = forbiddenMinIndex;
		}
	}

	public void CopyData(LLTerminationPoint p, Integer offsetX, Integer offsetY) {
		this._index[0] = p._index[0] + offsetX;
		this._index[1] = p._index[1] + offsetY;
		this._penalty = p._penalty;
		this._internalOrder = p._internalOrder;
		this._side = p._side;
		this._segment = p._segment;
		this._forbiddenMinIndex = p._forbiddenMinIndex;
		this._forbiddenMaxIndex = p._forbiddenMaxIndex;

	}

	public void Dispose() {
		this._index = null;
		this._segment = null;

	}

	public float GetDeviation(LLGrid grid, InternalRect rect) {

		if (this._segment.IsHorizontal()) {

			return GetDeviation(grid, rect, this.GetIndex(1), 0);
		}

		return GetDeviation(grid, rect, this.GetIndex(0), 1);

	}

	public static float GetDeviation(LLGrid grid, InternalRect rect,
			Integer index, Integer dir) {
		float num = 0;
		if (dir == 0) {
			if (rect.Height == 0f) {

				return 0f;
			}
			num = (grid.GetGridCoord(1, index) - rect.Y) - (0.5f * rect.Height);
			if (num < 0.0) {
				num = -num;
			}

			return ((200f * num) / rect.Height);
		}
		if (rect.Width == 0f) {

			return 0f;
		}
		num = (grid.GetGridCoord(0, index) - rect.X) - (0.5f * rect.Width);
		if (num < 0.0) {
			num = -num;
		}

		return ((200f * num) / rect.Width);

	}

	public Integer GetForbiddenMaxIndex() {

		return this._forbiddenMaxIndex;

	}

	public Integer GetForbiddenMinIndex() {

		return this._forbiddenMinIndex;

	}

	public Integer GetIndex(Integer dir) {

		return this._index[dir];

	}

	public Integer GetOrder() {

		return this._internalOrder;

	}

	public Integer GetPenalty() {

		return this._penalty;

	}

	public LLGridSegment GetSegment() {
		if ((this._segment != null) && (this._segment.GetGridLine() == null)) {
			this._segment = null;
		}

		return this._segment;

	}

	public Integer GetSide() {

		return this._side;

	}

	public void SetIndex(Integer dir, Integer val) {
		this._index[dir] = val;

	}

	public void Sort(LLTerminationPoint[] array) {
		if (array.length > 1) {
			TerminationPointSortAlg alg = new TerminationPointSortAlg(array);
			alg.Sort(array.length);
			alg.Dispose();
		}

	}

	public void Sort(LLTerminationPoint[] array, Integer[] sidePref) {
		if (array.length > 1) {
			TerminationPointSideSortAlg alg = new TerminationPointSideSortAlg(
					array, sidePref);
			alg.Sort(array.length);
			alg.Dispose();
		}

	}

	public class TerminationPointSideSortAlg extends QuickSort {
		public LLTerminationPoint[] _array;

		public Integer[] _sidePref;

		public TerminationPointSideSortAlg(LLTerminationPoint[] array,
				Integer[] sidePref) {
			this._array = array;
			this._sidePref = sidePref;
		}

		@Override
		public Integer Compare(Integer loc1, Integer loc2) {
			if (this._array[loc1] == this._array[loc2]) {

				return 0;
			}
			if (this._array[loc1] == null) {

				return 1;
			}
			if (this._array[loc2] == null) {

				return -1;
			}
			Integer num = 0;
			Integer num2 = 0;
			if (this._array[loc1].GetSide() == 0) {
				num = this._sidePref[1];
				// NOTICE: break ignore!!!
			} else if (this._array[loc1].GetSide() == 1) {
				num = this._sidePref[3];
				// NOTICE: break ignore!!!
			} else if (this._array[loc1].GetSide() == 2) {
				num = this._sidePref[0];
				// NOTICE: break ignore!!!
			} else if (this._array[loc1].GetSide() == 3) {
				num = this._sidePref[2];
				// NOTICE: break ignore!!!
			}
			if (this._array[loc2].GetSide() == 0) {
				num2 = this._sidePref[1];
				// NOTICE: break ignore!!!
			} else if (this._array[loc2].GetSide() == 1) {
				num2 = this._sidePref[3];
				// NOTICE: break ignore!!!
			} else if (this._array[loc2].GetSide() == 2) {
				num2 = this._sidePref[0];
				// NOTICE: break ignore!!!
			} else if (this._array[loc2].GetSide() == 3) {
				num2 = this._sidePref[2];
				// NOTICE: break ignore!!!
			}
			if (num != num2) {

				return (num2 - num);
			}
			if (this._array[loc1].GetPenalty() != this._array[loc2]
					.GetPenalty()) {

				return (this._array[loc1].GetPenalty() - this._array[loc2]
						.GetPenalty());
			}

			return (this._array[loc1].GetOrder() - this._array[loc2].GetOrder());

		}

		public void Dispose() {
			this._sidePref = null;

		}

		@Override
		public void Swap(Integer loc1, Integer loc2) {
			LLTerminationPoint point = this._array[loc1];
			this._array[loc1] = this._array[loc2];
			this._array[loc2] = point;

		}

	}

	public class TerminationPointSortAlg extends QuickSort {
		public LLTerminationPoint[] _array;

		public TerminationPointSortAlg(LLTerminationPoint[] array) {
			this._array = array;
		}

		@Override
		public Integer Compare(Integer loc1, Integer loc2) {
			if (this._array[loc1] == this._array[loc2]) {

				return 0;
			}
			if (this._array[loc1] == null) {

				return 1;
			}
			if (this._array[loc2] == null) {

				return -1;
			}
			if (this._array[loc1].GetPenalty() == this._array[loc2]
					.GetPenalty()) {

				return (this._array[loc1].GetOrder() - this._array[loc2]
						.GetOrder());
			}

			return (this._array[loc1].GetPenalty() - this._array[loc2]
					.GetPenalty());

		}

		public void Dispose() {

		}

		@Override
		public void Swap(Integer loc1, Integer loc2) {
			LLTerminationPoint point = this._array[loc1];
			this._array[loc1] = this._array[loc2];
			this._array[loc2] = point;

		}

	}
}