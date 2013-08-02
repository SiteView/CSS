package ILOG.Diagrammer.Util;

import java.lang.reflect.Array;

import system.*;
import system.Collections.*;
import system.ComponentModel.*;

public abstract class BaseSet implements ICollection, IEnumerable {
	private IDictionary _dictionary;

	private Integer _hashCode;

	public BaseSet(IDictionary dictionary) {
		this._dictionary = dictionary;
	}

	public Boolean Add(java.lang.Object v) {

		if (!this._dictionary.Contains(v)) {
			this._dictionary.Add(v, null);
			this._hashCode += v.hashCode();

			return true;
		}

		return false;

	}

	public void Clear() {
		this._hashCode = 0;
		this._dictionary.Clear();

	}

	public Boolean Contains(java.lang.Object v) {

		return this._dictionary.Contains(v);

	}

	public void CopyTo(java.lang.Object array, int index) {
		((ICollection) this._dictionary.get_Keys()).CopyTo(array, index);

	}

	@Override
	public boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ICollection)
				|| (this.get_Count() != ((ICollection) obj).get_Count())) {

			return false;
		}
		IEnumerator enumerator = ((ICollection) obj).GetEnumerator();

		while (enumerator.MoveNext()) {

			if (!this.Contains(enumerator.get_Current())) {

				return false;
			}
		}

		return true;

	}

	public IEnumerator GetEnumerator() {

		return ((ICollection) this._dictionary.get_Keys()).GetEnumerator();

	}

	@Override
	public int hashCode() {

		return this._hashCode;

	}

	public void Remove(java.lang.Object v) {

		if (this._dictionary.Contains(v)) {
			this._dictionary.Remove(v);
			this._hashCode -= v.hashCode();
		}

	}

	public int get_Count() {

		return this._dictionary.get_Count();
	}

	IDictionary get_Dictionary() {

		return this._dictionary;
	}

	public boolean get_IsSynchronized() {

		return false;
	}

	public java.lang.Object get_SyncRoot() {

		return this;
	}

}