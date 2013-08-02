package ILOG.Diagrammer.GraphLayout.Internal;

import system.*;

public final class GrowableArray {
	private Integer _capacityIncrement;

	private java.lang.Object[] _elementData;

	public GrowableArray(Integer size, Integer capacityIncrement) {
		this._elementData = new java.lang.Object[size];
		this._capacityIncrement = capacityIncrement;
	}

	public void EnsureCapacity(Integer minCapacity) {
		Integer length = this._elementData.length;
		if (minCapacity > length) {
			java.lang.Object[] sourceArray = this._elementData;
			Integer num2 = (this._capacityIncrement > 0) ? (length + this._capacityIncrement)
					: (length * 2);
			if (num2 < minCapacity) {
				num2 = minCapacity;
			}
			this._elementData = new java.lang.Object[num2];
			clr.System.ArrayStaticWrapper.Copy(sourceArray, 0,
					this._elementData, 0, sourceArray.length);
		}

	}

	public java.lang.Object GetElement(Integer index) {
		if (index > (this._elementData.length - 1)) {
			throw (new ArgumentException(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] {
									"index ",
									index,
									" cannot be larger than current capacity - 1 (",
									this._elementData.length - 1, ")" })));
		}

		return this._elementData[index];

	}

	public Integer Length() {

		return this._elementData.length;

	}

	public void SetElement(java.lang.Object obj, Integer index) {
		if (index > (this._elementData.length - 1)) {
			throw (new ArgumentException(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] {
									"index ",
									index,
									" cannot be larger than current capacity - 1 (",
									this._elementData.length - 1, ")" })));
		}
		this._elementData[index] = obj;

	}

}