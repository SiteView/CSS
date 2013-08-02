package ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase;

import system.*;

public class HTBaseEdge extends HTBaseGraphMember {
	public HTBaseEdge _nextInEdge = null;

	public HTBaseEdge _nextOutEdge = null;

	public HTBaseEdge _prevInEdge = null;

	public HTBaseEdge _prevOutEdge = null;

	public HTBaseNode _source = null;

	public HTBaseNode _target = null;

	public HTBaseEdge() {
	}

	public void InsertAfterInEdge(HTBaseNode target, HTBaseEdge prevInEdge) {
		this._target = target;
		this._prevInEdge = prevInEdge;
		if (prevInEdge != null) {
			HTBaseEdge edge = prevInEdge._nextInEdge;
			prevInEdge._nextInEdge = this;
			if (edge != null) {
				edge._prevInEdge = this;
				this._nextInEdge = edge;
			}
		}

	}

	public void InsertAfterOutEdge(HTBaseNode source, HTBaseEdge prevOutEdge) {
		this._source = source;
		this._prevOutEdge = prevOutEdge;
		if (prevOutEdge != null) {
			HTBaseEdge edge = prevOutEdge._nextOutEdge;
			prevOutEdge._nextOutEdge = this;
			if (edge != null) {
				edge._prevOutEdge = this;
				this._nextOutEdge = edge;
			}
		}

	}

	public void InsertBeforeInEdge(HTBaseNode target, HTBaseEdge nextInEdge) {
		this._target = target;
		this._nextInEdge = nextInEdge;
		if (nextInEdge != null) {
			HTBaseEdge edge = nextInEdge._prevInEdge;
			nextInEdge._prevInEdge = this;
			if (edge != null) {
				edge._nextInEdge = this;
				this._prevInEdge = edge;
			}
		}

	}

	public void InsertBeforeOutEdge(HTBaseNode source, HTBaseEdge nextOutEdge) {
		this._source = source;
		this._nextOutEdge = nextOutEdge;
		if (nextOutEdge != null) {
			HTBaseEdge edge = nextOutEdge._prevOutEdge;
			nextOutEdge._prevOutEdge = this;
			if (edge != null) {
				edge._nextOutEdge = this;
				this._prevOutEdge = edge;
			}
		}

	}

	public Boolean IsContainedInIncoming(HTBaseEdge list) {
		while (list != null) {
			if (list == this) {

				return true;
			}
			list = list._nextInEdge;
		}

		return false;

	}

	public Boolean IsContainedInOutgoing(HTBaseEdge list) {
		while (list != null) {
			if (list == this) {

				return true;
			}
			list = list._nextOutEdge;
		}

		return false;

	}

	public Boolean IsSelfLoop() {

		return (this._source == this._target);

	}

	public void RemoveInEdge() {
		if (this._prevInEdge != null) {
			this._prevInEdge._nextInEdge = this._nextInEdge;
		}
		if (this._nextInEdge != null) {
			this._nextInEdge._prevInEdge = this._prevInEdge;
		}
		this._prevInEdge = null;
		this._nextInEdge = null;

	}

	public void RemoveOutEdge() {
		if (this._prevOutEdge != null) {
			this._prevOutEdge._nextOutEdge = this._nextOutEdge;
		}
		if (this._nextOutEdge != null) {
			this._nextOutEdge._prevOutEdge = this._prevOutEdge;
		}
		this._prevOutEdge = null;
		this._nextOutEdge = null;

	}

	public void Reverse() {
		if (super.GetOwner() != null) {
			HTBaseNode node = this._source;
			HTBaseNode node2 = this._target;
			node.RemoveOutEdge(this);
			node2.RemoveInEdge(this);
			node.AddInEdge(this);
			node2.AddOutEdge(this);
		} else {
			HTBaseNode node3 = this._target;
			this._target = this._source;
			this._source = node3;
		}

	}

}