package ILOG.Diagrammer.GraphLayout.Internal;

import system.*;

public abstract class PriorityQueue {
	private Integer _numElements;

	private java.lang.Object[] _queue;

	public PriorityQueue() {
	}

	public void Add(java.lang.Object obj) {
		if (this._numElements == this._queue.length) {
			this.Resize();
		}
		this._queue[this._numElements++] = obj;
		if (this._numElements > 1) {
			this.ValidateInvariantsUpwards(this._numElements - 1);
		}

	}

	public void Clear() {
		if (this._queue != null) {
			for (Integer i = 0; i <= this._numElements; i++) {
				this._queue[i] = null;
			}
			this._numElements = 0;
		}

	}

	public java.lang.Object GetMin() {

		return this._queue[0];

	}

	public abstract float GetValue(java.lang.Object obj);

	public void Init(Integer size) {
		this._queue = new java.lang.Object[(size > 0) ? size : 1];
		this._numElements = 0;

	}

	public Boolean IsEmpty() {

		return (this._numElements == 0);

	}

	public java.lang.Object PopMin() {
		java.lang.Object min = this.GetMin();
		this.RemoveMin();

		return min;

	}

	public void RemoveMin() {

		if (!this.IsEmpty()) {
			this._queue[0] = this._queue[this._numElements - 1];
			this._queue[--this._numElements] = null;
			if (this._numElements > 1) {
				this.ValidateInvariantsDownwards(0);
			}
		}

	}

	private void Resize() {
		java.lang.Object[] objArray = new java.lang.Object[2 * this._queue.length];
		for (Integer i = 0; i < this._numElements; i++) {
			objArray[i] = this._queue[i];
		}
		this._queue = objArray;

	}

	public Integer Size() {

		return this._numElements;

	}

	private void ValidateInvariantsDownwards(Integer k) {
		Integer index = (2 * k) + 1;
		float num2 = this.GetValue(this._queue[k]);
		while (index < this._numElements) {
			float num3 = this.GetValue(this._queue[index]);
			if ((index + 1) < this._numElements) {
				float num4 = this.GetValue(this._queue[index + 1]);
				if (num3 > num4) {
					index++;
					num3 = num4;
				}
			}
			if (num2 <= num3) {

				return;
			}
			java.lang.Object obj2 = this._queue[index];
			this._queue[index] = this._queue[k];
			this._queue[k] = obj2;
			k = index;
			index = (2 * k) + 1;
		}

	}

	private void ValidateInvariantsUpwards(Integer k) {
		float num2 = this.GetValue(this._queue[k]);
		while (k > 0) {
			Integer index = (k - 1) / 2;
			if (this.GetValue(this._queue[index]) <= num2) {

				return;
			}
			java.lang.Object obj2 = this._queue[index];
			this._queue[index] = this._queue[k];
			this._queue[k] = obj2;
			k = index;
		}

	}

}