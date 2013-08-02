package ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase;

import system.*;

public class HTBaseGraphMember {
	public HTBaseGraphMember _next = null;

	public java.lang.Object _owner = null;

	public HTBaseGraphMember _prev = null;

	public HTBaseGraphMember() {
	}

	public java.lang.Object GetOwner() {

		return this._owner;

	}

	public HTBaseGraph GetOwnerBaseGraph() {
		java.lang.Object owner = this._owner;
		while ((owner != null) && (owner instanceof HTBaseGraphMember)) {

			owner = ((HTBaseGraphMember) owner).GetOwner();
		}
		if (owner instanceof HTBaseGraph) {

			return (HTBaseGraph) owner;
		}

		return null;

	}

	public void InsertAfter(java.lang.Object owner, HTBaseGraphMember prev) {
		this._owner = owner;
		this._prev = prev;
		if (prev != null) {
			HTBaseGraphMember member = prev._next;
			prev._next = this;
			if (member != null) {
				member._prev = this;
				this._next = member;
			}
		}

	}

	public void InsertBefore(java.lang.Object owner, HTBaseGraphMember next) {
		this._owner = owner;
		this._next = next;
		if (next != null) {
			HTBaseGraphMember member = next._prev;
			next._prev = this;
			if (member != null) {
				member._next = this;
				this._prev = member;
			}
		}

	}

	public Boolean IsContainedIn(HTBaseGraphMember list) {
		while (list != null) {
			if (list == this) {

				return true;
			}
			list = list._next;
		}

		return false;

	}

	public void Remove() {
		if (this._prev != null) {
			this._prev._next = this._next;
		}
		if (this._next != null) {
			this._next._prev = this._prev;
		}
		this._prev = null;
		this._next = null;
		this._owner = null;

	}

}