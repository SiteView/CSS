package ILOG.Diagrammer.GraphLayout.Internal;

import system.*;

public abstract class QuickSort {
	public QuickSort() {
	}

	public abstract Integer Compare(Integer loc1, Integer loc2);

	private void DoQuickSort(Integer left, Integer right) {
		if (right <= left) {

			return;
		}
		Integer num = (left + right) / 2;
		if (num != right) {
			this.Swap(num, right);
		}
		Integer num2 = right;
		Integer num3 = left;
		while (num3 < num2) {
			if (this.Compare(num3, right) == 0) {
				this.Swap(num3, --num2);
			} else {
				num3++;
			}
		}
		num3 = left - 1;
		Integer num4 = num2;
		Label_0052: do {
			while (++num3 < num2) {
				if (this.Compare(num3, right) > 0) {
					break;
				}
			}
			while (num4 > 0) {
				if (this.Compare(--num4, right) < 0) {
					break;
				}
			}
			if (num3 < num4) {
				this.Swap(num3, num4);
			}
		} while (num3 < num4);
		num4 = num3;
		while (num2 <= right) {
			this.Swap(num4++, num2++);
		}
		this.DoQuickSort(left, num3 - 1);
		this.DoQuickSort(num4, right);

	}

	public void Sort(Integer numberOfElements) {
		this.Sort(0, numberOfElements - 1);

	}

	public void Sort(Integer firstIndex, Integer lastIndex) {
		Integer num = lastIndex - firstIndex;
		if (num >= 1) {
			if (num == 1) {
				if (this.Compare(firstIndex, lastIndex) > 0) {
					this.Swap(firstIndex, lastIndex);
				}
			} else {
				this.DoQuickSort(firstIndex, lastIndex);
			}
		}

	}

	public abstract void Swap(Integer loc1, Integer loc2);

}