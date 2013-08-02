package ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase;

import system.*;

public class HTBaseOutEdgeIterator {
	public HTBaseEdge _edge;

	public HTBaseOutEdgeIterator(HTBaseNode node) {
		this.Init(node._firstOutEdge);
	}

	public Boolean HasNext() {

		return (this._edge != null);

	}

	public Boolean HasPrev() {

		return (this._edge != null);

	}

	public void Init(HTBaseEdge edge) {
		this._edge = edge;

	}

	public HTBaseEdge NextBaseEdge() {
		HTBaseEdge edge = this._edge;
		this._edge = this._edge._nextOutEdge;

		return edge;

	}

	public HTBaseEdge PrevBaseEdge() {
		HTBaseEdge edge = this._edge;
		this._edge = this._edge._prevOutEdge;

		return edge;

	}

}