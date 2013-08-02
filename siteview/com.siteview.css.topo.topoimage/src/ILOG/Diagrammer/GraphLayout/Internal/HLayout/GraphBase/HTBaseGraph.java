package ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase;

import system.*;

public class HTBaseGraph {
	public HTBaseEdge _firstEdge = null;

	public HTBaseNode _firstNode = null;

	public HTBaseEdge _lastEdge = null;

	public HTBaseNode _lastNode = null;

	private Integer _numberOfEdges = 0;

	private Integer _numberOfNodes = 0;

	public HTBaseGraph() {
	}

	public void AddEdge(HTBaseEdge edge) {
		this.AddEdge(edge, edge._source, edge._target);

	}

	public void AddEdge(HTBaseEdge edge, HTBaseNode source, HTBaseNode target) {
		if (this._lastEdge != null) {
			edge.InsertAfter(this, this._lastEdge);
			this._lastEdge = edge;
		} else {
			edge.InsertBefore(this, null);
			this._firstEdge = this._lastEdge = edge;
		}
		source.AddOutEdge(edge);
		target.AddInEdge(edge);
		this._numberOfEdges++;

	}

	public void AddNode(HTBaseNode node) {
		if (this._lastNode != null) {
			node.InsertAfter(this, this._lastNode);
			this._lastNode = node;
		} else {
			node.InsertBefore(this, null);
			this._firstNode = this._lastNode = node;
		}
		this._numberOfNodes++;

	}

	public Boolean ContainsEdge(HTBaseEdge edge) {

		return ((edge != null) && edge.IsContainedIn(this._firstEdge));

	}

	public Boolean ContainsNode(HTBaseNode node) {

		return ((node != null) && node.IsContainedIn(this._firstNode));

	}

	public HTBaseEdge GetEdgeBetween(HTBaseNode source, HTBaseNode target,
			Boolean directed) {
		if (source.GetOutdegree() <= target.GetIndegree()) {
			for (HTBaseEdge edge = source._firstOutEdge; edge != null; edge = edge._nextOutEdge) {
				if (edge._target == target) {

					return edge;
				}
			}
		} else {
			for (HTBaseEdge edge2 = target._firstInEdge; edge2 != null; edge2 = edge2._nextInEdge) {
				if (edge2._source == source) {

					return edge2;
				}
			}
		}
		if (!directed) {

			return this.GetEdgeBetween(target, source, true);
		}

		return null;

	}

	public Integer GetNumberOfEdges() {

		return this._numberOfEdges;

	}

	public Integer GetNumberOfNodes() {

		return this._numberOfNodes;

	}

	public void RemoveEdge(HTBaseEdge edge) {
		edge._source.RemoveOutEdge(edge);
		edge._target.RemoveInEdge(edge);
		if (this._firstEdge == edge) {
			this._firstEdge = (HTBaseEdge) edge._next;
		}
		if (this._lastEdge == edge) {
			this._lastEdge = (HTBaseEdge) edge._prev;
		}
		edge.Remove();
		this._numberOfEdges--;

	}

	public void RemoveInEdges(HTBaseNode node) {
		HTBaseEdge edge = null;
		for (HTBaseEdge edge2 = node._firstInEdge; edge2 != null; edge2 = edge) {
			edge = edge2._nextInEdge;
			this.RemoveEdge(edge2);
		}

	}

	public void RemoveNode(HTBaseNode node) {
		this.RemoveInEdges(node);
		this.RemoveOutEdges(node);
		if (this._firstNode == node) {
			this._firstNode = (HTBaseNode) node._next;
		}
		if (this._lastNode == node) {
			this._lastNode = (HTBaseNode) node._prev;
		}
		node.Remove();
		this._numberOfNodes--;

	}

	public void RemoveOutEdges(HTBaseNode node) {
		HTBaseEdge edge = null;
		for (HTBaseEdge edge2 = node._firstOutEdge; edge2 != null; edge2 = edge) {
			edge = edge2._nextOutEdge;
			this.RemoveEdge(edge2);
		}

	}

}