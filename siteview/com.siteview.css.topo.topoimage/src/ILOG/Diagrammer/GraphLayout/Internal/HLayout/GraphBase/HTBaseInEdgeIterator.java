package ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase;

import system.*;

public class HTBaseInEdgeIterator {
	public HTBaseEdge _edge;

	public HTBaseInEdgeIterator(HTBaseNode node) {
		this.Init(node._firstInEdge);
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
		this._edge = this._edge._nextInEdge;

		return edge;

	}

	public HTBaseEdge PrevBaseEdge() {
		HTBaseEdge edge = this._edge;
		this._edge = this._edge._prevInEdge;

		return edge;

	}

}