package ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase;

import system.*;

public class HTBaseAdjEdgeIterator {
	public HTBaseEdge _inedge;

	public HTBaseEdge _outedge;

	public HTBaseAdjEdgeIterator(HTBaseNode node) {
		this._inedge = node._firstInEdge;
		this._outedge = node._firstOutEdge;
	}

	public Boolean HasNext() {
		if (this._inedge == null) {

			return (this._outedge != null);
		}

		return true;

	}

	public Boolean HasPrev() {

		return false;

	}

	public void Init(HTBaseEdge edge) {

	}

	public HTBaseEdge NextBaseEdge() {
		if (this._inedge != null) {
			HTBaseEdge edge = this._inedge;
			this._inedge = this._inedge._nextInEdge;

			return edge;
		}
		HTBaseEdge edge2 = this._outedge;
		this._outedge = this._outedge._nextOutEdge;

		return edge2;

	}

	public HTBaseEdge PrevBaseEdge() {

		return null;

	}

}