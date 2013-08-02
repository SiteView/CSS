package ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase;

import system.*;

public class HTBaseNodeIterator {
	public HTBaseNode _node;

	public HTBaseNodeIterator(HTBaseNode startNode) {
		this.Init(startNode);
	}

	public HTBaseNodeIterator(HTBaseGraph graph, Boolean backward) {
		if (backward) {
			this._node = graph._lastNode;
		} else {
			this._node = graph._firstNode;
		}
	}

	public Boolean HasNext() {

		return (this._node != null);

	}

	public Boolean HasPrev() {

		return (this._node != null);

	}

	public void Init(HTBaseNode node) {
		this._node = node;

	}

	public HTBaseNode NextBaseNode() {
		HTBaseNode node = this._node;
		this._node = (HTBaseNode) this._node._next;

		return node;

	}

	public HTBaseNode PrevBaseNode() {
		HTBaseNode node = this._node;
		this._node = (HTBaseNode) this._node._prev;

		return node;

	}

}