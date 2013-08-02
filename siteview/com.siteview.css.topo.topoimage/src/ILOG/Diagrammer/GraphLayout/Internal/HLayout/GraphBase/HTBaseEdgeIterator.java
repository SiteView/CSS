package ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase;

import system.*;

public class HTBaseEdgeIterator {
	public HTBaseEdge _edge;

	public HTBaseEdgeIterator(HTBaseEdge startEdge) {
		this.Init(startEdge);
	}

	public HTBaseEdgeIterator(HTBaseGraph graph, Boolean backward) {
		if (backward) {
			this._edge = graph._lastEdge;
		} else {
			this._edge = graph._firstEdge;
		}
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
		this._edge = (HTBaseEdge) this._edge._next;

		return edge;

	}

	public HTBaseEdge PrevBaseEdge() {
		HTBaseEdge edge = this._edge;
		this._edge = (HTBaseEdge) this._edge._prev;

		return edge;

	}

}