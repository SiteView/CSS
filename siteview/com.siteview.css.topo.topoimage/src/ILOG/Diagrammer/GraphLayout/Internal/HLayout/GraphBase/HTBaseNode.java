package ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase;

import system.*;

public class HTBaseNode extends HTBaseGraphMember {
	public HTBaseEdge _firstInEdge = null;

	public HTBaseEdge _firstOutEdge = null;

	public Integer _indegree = 0;

	public Integer _outdegree = 0;

	public HTBaseNode() {
	}

	public void AddInEdge(HTBaseEdge edge) {
		edge.InsertBeforeInEdge(this, this._firstInEdge);
		this._firstInEdge = edge;
		this._indegree++;

	}

	public void AddOutEdge(HTBaseEdge edge) {
		edge.InsertBeforeOutEdge(this, this._firstOutEdge);
		this._firstOutEdge = edge;
		this._outdegree++;

	}

	public Boolean ContainsInEdge(HTBaseEdge edge) {

		return ((edge != null) && edge.IsContainedInIncoming(this._firstInEdge));

	}

	public Boolean ContainsOutEdge(HTBaseEdge edge) {

		return ((edge != null) && edge
				.IsContainedInOutgoing(this._firstOutEdge));

	}

	public Integer GetDegree() {

		return (this._indegree + this._outdegree);

	}

	public Integer GetIndegree() {

		return this._indegree;

	}

	public Integer GetOutdegree() {

		return this._outdegree;

	}

	public void RemoveInEdge(HTBaseEdge edge) {
		if (this._firstInEdge == edge) {
			this._firstInEdge = edge._nextInEdge;
		}
		edge.RemoveInEdge();
		this._indegree--;

	}

	public void RemoveOutEdge(HTBaseEdge edge) {
		if (this._firstOutEdge == edge) {
			this._firstOutEdge = edge._nextOutEdge;
		}
		edge.RemoveOutEdge();
		this._outdegree--;

	}

}