package ILOG.Diagrammer.GraphLayout.Internal;

import system.*;
import system.Collections.*;

public final class KeyDataInt {
	private Hashtable _hash;

	private Boolean _isMemorySavings = false;

	private KeyDataObject _objKey = new KeyDataObject(-1);

	private Integer[] _vectHash;

	private Integer NOT_PRESENT = -1;

	public KeyDataInt(Integer maxKey, Boolean isMemorySavings) {
		this._isMemorySavings = isMemorySavings;
		if (isMemorySavings) {
			this._hash = new Hashtable(this.ComputeInitialSizeForHashMap(
					maxKey, isMemorySavings));
		} else {
			this._vectHash = new Integer[maxKey];
		}
	}

	private Integer ComputeInitialSizeForHashMap(Integer size,
			Boolean isMemorySavings) {
		if (size >= 100) {

			return 400;
		}

		return 50;

	}

	public Integer Get(Integer intKey) {
		if (this._isMemorySavings) {
			this._objKey.Set(intKey);
			java.lang.Object obj2 = this._hash.get_Item(this._objKey);
			if (obj2 != null) {

				return ((KeyDataObject) obj2).Get();
			}

			return -1;
		}
		Integer num = this._vectHash[intKey];
		if (num != 0) {

			return (num - 1);
		}

		return -1;

	}

	public void Put(Integer intKey, Integer intValue) {
		if (this._isMemorySavings) {
			this._hash.set_Item(new KeyDataObject(intKey), new KeyDataObject(
					intValue));
		} else {
			this._vectHash[intKey] = intValue + 1;
		}

	}

	private class KeyDataObject {
		private Integer _intValue;

		public KeyDataObject(Integer intValue) {
			this._intValue = intValue;
		}

		@Override
		public boolean equals(java.lang.Object obj) {

			return (this._intValue == ((KeyDataInt.KeyDataObject) obj).Get());

		}

		public Integer Get() {

			return this._intValue;

		}

		@Override
		public int hashCode() {

			return this._intValue;

		}

		public void Set(Integer intValue) {
			this._intValue = intValue;

		}

	}
}