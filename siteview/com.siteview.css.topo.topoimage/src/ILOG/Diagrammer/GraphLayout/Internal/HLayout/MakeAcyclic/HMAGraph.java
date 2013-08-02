package ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public class HMAGraph extends HTBaseGraph {
	public HTBaseEdge _firstRemovedEdge = null;

	public HTBaseNode _firstRemovedNode = null;

	public Boolean _nodeEdgeDataValid = false;

	public HMAGraph() {
	}

	public void ClearNodeEdgeData() {
		HMANodeIterator nodes = this.GetNodes(false);

		while (nodes.HasNext()) {
			nodes.Next().ClearEdgeData();
		}
		this._nodeEdgeDataValid = false;

	}

	public void EmptyTemporaryRemovedEdges() {
		this._firstRemovedEdge = null;

	}

	public void EmptyTemporaryRemovedNodes() {
		this._firstRemovedNode = null;

	}

	public HMAEdgeIterator GetEdges(Boolean backwards) {

		return new HMAGraphEdgeIterator(this, backwards);

	}

	public HMANodeIterator GetNodes(Boolean backwards) {

		return new HMAGraphNodeIterator(this, backwards);

	}

	public Boolean IsNodeEdgeDataValid() {

		return this._nodeEdgeDataValid;

	}

	public void RecalcNodeEdgeData() {
		this.ClearNodeEdgeData();
		HMAEdgeIterator edges = this.GetEdges(false);

		while (edges.HasNext()) {
			HMAEdge edge = edges.Next();
			edge.GetHMASource().AddOutEdgeData(edge);
			edge.GetHMATarget().AddInEdgeData(edge);
		}
		this._nodeEdgeDataValid = true;

	}

	@Override
	public void RemoveEdge(HTBaseEdge edge) {
		super.RemoveEdge(edge);
		edge.InsertBefore(null, this._firstRemovedEdge);
		this._firstRemovedEdge = edge;

	}

	@Override
	public void RemoveNode(HTBaseNode node) {
		super.RemoveNode(node);
		node.InsertBefore(null, this._firstRemovedNode);
		this._firstRemovedNode = node;

	}

	public void RestoreTemporaryRemovedEdges(Boolean withSelfLoops) {
		HTBaseEdgeIterator iterator = new HTBaseEdgeIterator(
				this._firstRemovedEdge);
		this._firstRemovedEdge = null;

		while (iterator.HasNext()) {
			HTBaseEdge edge = iterator.NextBaseEdge();

			if (withSelfLoops || !edge.IsSelfLoop()) {
				edge.Remove();
				super.AddEdge(edge);
			} else if (this._firstRemovedEdge == null) {
				this._firstRemovedEdge = edge;
			}
		}

	}

	public void RestoreTemporaryRemovedNodes() {
		HTBaseNodeIterator iterator = new HTBaseNodeIterator(
				this._firstRemovedNode);
		this._firstRemovedNode = null;

		while (iterator.HasNext()) {
			HTBaseNode node = iterator.NextBaseNode();
			node.Remove();
			this.AddNode(node);
		}

	}

}