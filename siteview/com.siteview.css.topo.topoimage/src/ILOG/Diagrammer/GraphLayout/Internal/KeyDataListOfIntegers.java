package ILOG.Diagrammer.GraphLayout.Internal;

import system.*;
import system.Collections.*;

public final class KeyDataListOfIntegers {
	private Hashtable _hash;

	private KeyDataObject _objKey = new KeyDataObject(-1);

	private ListOfIntegers[] _vectHash;

	private Boolean isMemorySavings = false;

	public KeyDataListOfIntegers(Integer size, Boolean isMemorySavings) {
		this.isMemorySavings = isMemorySavings;
		if (isMemorySavings) {
			this._hash = new Hashtable(this.ComputeInitialSizeOfHashtable(size,
					isMemorySavings));
		} else {
			this._vectHash = new ListOfIntegers[size];
		}
	}

	private Integer ComputeInitialSizeOfHashtable(Integer size,
			Boolean isMemorySavings) {
		if (size < 100) {

			return 50;
		}

		return 200;

	}

	public ListOfIntegers Get(Integer intKey) {
		if (this.isMemorySavings) {
			this._objKey.Set(intKey);

			return (ListOfIntegers) this._hash.get_Item(this._objKey);
		}

		return this._vectHash[intKey];

	}

	public Integer GetBeginInters(Integer cycleId1, Integer cycleId2,
			Integer numberOfNodes) {
		Integer intKey = TopologicalData.ComputeUniqueIndex(cycleId1, cycleId2,
				numberOfNodes);
		ListOfIntegers integers = this.Get(intKey);
		if (cycleId1 < cycleId2) {

			return integers.GetBeginIntersSmallBig();
		}

		return integers.GetBeginIntersBigSmall();

	}

	public Integer GetEndInters(Integer cycleId1, Integer cycleId2,
			Integer numberOfNodes) {
		Integer intKey = TopologicalData.ComputeUniqueIndex(cycleId1, cycleId2,
				numberOfNodes);
		ListOfIntegers integers = this.Get(intKey);
		if (cycleId1 < cycleId2) {

			return integers.GetEndIntersSmallBig();
		}

		return integers.GetEndIntersBigSmall();

	}

	public void Put(Integer intKey, ListOfIntegers val) {
		if (this.isMemorySavings) {
			this._hash.set_Item(new KeyDataObject(intKey), val);
		} else {
			this._vectHash[intKey] = val;
		}

	}

	public void SetBeginEndInters(Integer cycleId1, Integer cycleId2,
			Integer begin, Integer end, Integer numberOfNodes) {
		Integer intKey = TopologicalData.ComputeUniqueIndex(cycleId1, cycleId2,
				numberOfNodes);
		ListOfIntegers integers = this.Get(intKey);
		if (cycleId1 < cycleId2) {
			integers.SetBeginIntersSmallBig(begin);
			integers.SetEndIntersSmallBig(end);
		} else {
			integers.SetBeginIntersBigSmall(begin);
			integers.SetEndIntersBigSmall(end);
		}

	}

	private final class KeyDataObject {
		private Integer _intValue;

		public KeyDataObject(Integer intValue) {
			this._intValue = intValue;
		}

		@Override
		public boolean equals(java.lang.Object obj) {

			return (this._intValue == ((KeyDataListOfIntegers.KeyDataObject) obj)
					.Get());

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