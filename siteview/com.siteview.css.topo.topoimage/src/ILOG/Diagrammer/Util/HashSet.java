package ILOG.Diagrammer.Util;

import system.*;
import system.ComponentModel.*;
import system.Collections.*;

public class HashSet extends BaseSet implements ICloneable {
	public HashSet() {
		super(new Hashtable());
	}

	public HashSet(HashSet source) {
		super((IDictionary) ((Hashtable) source.get_Dictionary()).Clone());
	}

	public HashSet(Integer initialCapacity) {
		super(new Hashtable(initialCapacity));
	}

	public HashSet(Integer initialCapacity, float loadFactor) {
		super(new Hashtable(initialCapacity, loadFactor));
	}

	public java.lang.Object Clone() {

		return new HashSet(this);

	}

}