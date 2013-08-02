package ILOG.Diagrammer.Util;

import system.*;
import system.Collections.*;
import system.ComponentModel.*;

class SortedSet extends BaseSet implements ICloneable {
	public SortedSet() {
		super(new SortedList());
	}

	public SortedSet(SortedSet source) {
		super((IDictionary) ((SortedList) source.get_Dictionary()).Clone());
	}

	public SortedSet(IComparer comparer) {
		super(new SortedList(comparer));
	}

	public SortedSet(Integer initialCapacity) {
		super(new SortedList(initialCapacity));
	}

	public SortedSet(IComparer comparer, Integer initialCapacity) {
		super(new SortedList(comparer, initialCapacity));
	}

	public java.lang.Object Clone() {

		return new SortedSet(this);

	}

}