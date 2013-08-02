package ILOG.Diagrammer.GraphLayout.Internal;

import system.*;

public abstract class ArrayStableSort {
	public ArrayStableSort() {
	}

	public abstract Boolean Compare(java.lang.Object o1, java.lang.Object o2);

	private void Merge(java.lang.Object[] src, java.lang.Object[] dst,
			Integer lo, Integer hi, Integer mid, Boolean asc) {
		Integer index = asc ? lo : hi;
		Integer num2 = asc ? 1 : -1;
		Integer num3 = lo;
		Integer num4 = hi;
		while ((num3 < mid) && (num4 >= mid)) {

			if (this.Compare(src[num3], src[num4])) {
				dst[index] = src[num3++];
			} else {
				dst[index] = src[num4--];
			}
			index += num2;
		}
		while (num3 < mid) {
			dst[index] = src[num3++];
			index += num2;
		}
		while (num4 >= mid) {
			dst[index] = src[num4--];
			index += num2;
		}

	}

	private Boolean Mergeruns(java.lang.Object[] src, java.lang.Object[] dst,
			Integer[] runs, Integer n) {
		Integer num = 0;
		Integer num2 = 0;
		Integer lo = 0;
		Integer index = 0;
		Boolean asc = true;
		while (num < n) {
			lo = num;
			Integer mid = runs[num2++];
			num = runs[num2++];
			this.Merge(src, dst, lo, num - 1, mid, asc);
			asc = !asc;
			runs[index++] = num;
		}
		runs[index] = n;

		return (lo == 0);

	}

	public void Sort(java.lang.Object[] a) {
		Integer length = a.length;
		if (length == 1) {

			return;
		} else if (length == 2) {

			if (!this.Compare(a[0], a[1])) {
				java.lang.Object obj2 = a[0];
				a[0] = a[1];
				a[1] = obj2;
			}

			return;
		}
		java.lang.Object[] dst = new java.lang.Object[length];
		Integer[] runs = new Integer[length + 2];
		Integer index = 0;
		Integer num3 = 0;
		Integer num4 = 0;
		while (index < length) {
			java.lang.Object obj3 = null;
			java.lang.Object obj4 = null;
			Integer num5 = null;
			Integer num6 = null;
			do {
				obj3 = a[index++];
			} while ((index < length) && this.Compare(obj3, a[index]));
			runs[num3++] = index;
			num4 = index;
			while ((index < length) && this.Compare(a[index], obj3)) {

				if (!this.Compare(obj3, a[index])) {
					if (num4 < (index - 1)) {
						num5 = num4;
						num6 = index - 1;
						while (num5 < num6) {
							obj4 = a[num5];
							a[num5] = a[num6];
							a[num6] = obj4;
							num5++;
							num6--;
						}
					}
					num4 = index;
				}
				obj3 = a[index++];
			}
			if (num4 < (index - 1)) {
				num5 = num4;
				for (num6 = index - 1; num5 < num6; num6--) {
					obj4 = a[num5];
					a[num5] = a[num6];
					a[num6] = obj4;
					num5++;
				}
			}
			runs[num3++] = index;
		}
		runs[num3] = length;
		for (Boolean flag = false; !flag; flag = this.Mergeruns(a, dst, runs,
				length) | this.Mergeruns(dst, a, runs, length)) {
		}

	}

}