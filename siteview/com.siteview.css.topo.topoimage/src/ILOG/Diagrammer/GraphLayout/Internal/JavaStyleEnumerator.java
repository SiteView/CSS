package ILOG.Diagrammer.GraphLayout.Internal;

import system.Collections.ICollection;
import system.Collections.IEnumerator;

public class JavaStyleEnumerator implements IJavaStyleEnumerator {
	private ICollection _col;

	private Boolean _createdFromCollection = false;

	private IEnumerator _e;

	private Boolean _hasMore = false;

	public JavaStyleEnumerator(ICollection col) {
		this._col = col;
		this._createdFromCollection = true;
		this.Init(col.GetEnumerator());
	}

	public JavaStyleEnumerator(IEnumerator e) {
		this.Init(e);
	}

	public ICollection GetCollection() {

		return this._col;

	}

	public Boolean HasMoreElements() {

		return this._hasMore;

	}

	private void Init(IEnumerator e) {
		this._e = e;

		this._hasMore = e.MoveNext();

	}

	public Boolean IsCreatedFromCollection() {

		return this._createdFromCollection;

	}

	public java.lang.Object NextElement() {
		java.lang.Object current = this._e.get_Current();

		this._hasMore = this._e.MoveNext();

		return current;

	}

}