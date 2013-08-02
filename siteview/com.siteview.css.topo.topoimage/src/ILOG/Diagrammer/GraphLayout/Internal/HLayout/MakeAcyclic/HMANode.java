package ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public class HMANode extends HTBaseNode {
	private float _inprio = 0f;

	private Integer _numUnbreakableInEdges = 0;

	private Integer _numUnbreakableOutEdges = 0;

	private Integer _orderingNumber = -1;

	private float _outprio = 0f;

	public HMANode() {
	}

	@Override
	public void AddInEdge(HTBaseEdge edge) {
		super.AddInEdge(edge);

		if (((HMAGraph) super.GetOwnerBaseGraph()).IsNodeEdgeDataValid()) {
			this.AddInEdgeData((HMAEdge) edge);
		}

	}

	public void AddInEdgeData(HMAEdge edge) {
		this._inprio += edge.GetPriority();
		;

		if (edge.IsUnbreakable()) {
			this._numUnbreakableInEdges++;
		}

	}

	@Override
	public void AddOutEdge(HTBaseEdge edge) {
		super.AddOutEdge(edge);

		if (((HMAGraph) super.GetOwnerBaseGraph()).IsNodeEdgeDataValid()) {
			this.AddOutEdgeData((HMAEdge) edge);
		}

	}

	public void AddOutEdgeData(HMAEdge edge) {
		this._outprio += edge.GetPriority();

		if (edge.IsUnbreakable()) {
			this._numUnbreakableOutEdges++;
		}

	}

	public void ClearEdgeData() {
		this._inprio = this._outprio = 0f;
		this._numUnbreakableInEdges = this._numUnbreakableOutEdges = 0;

	}

	public HMAEdgeIterator GetHMAEdges() {

		return new HMAAdjEdgeIterator(this);

	}

	public HMAEdgeIterator GetInEdges() {

		return new HMAInEdgeIterator(this);

	}

	public float GetInPriority() {

		return this._inprio;

	}

	public Integer GetInUnbreakCount() {

		return this._numUnbreakableInEdges;

	}

	public Integer GetOrderingNumber() {

		return this._orderingNumber;

	}

	public HMAEdgeIterator GetOutEdges() {

		return new HMAOutEdgeIterator(this);

	}

	public float GetOutPriority() {

		return this._outprio;

	}

	public Integer GetOutUnbreakCount() {

		return this._numUnbreakableOutEdges;

	}

	@Override
	public void RemoveInEdge(HTBaseEdge edge) {
		super.RemoveInEdge(edge);

		if (((HMAGraph) super.GetOwnerBaseGraph()).IsNodeEdgeDataValid()) {
			this.RemoveInEdgeData((HMAEdge) edge);
		}

	}

	public void RemoveInEdgeData(HMAEdge edge) {
		this._inprio -= edge.GetPriority();
		;
		if ((this._inprio < 0f) || (super.GetIndegree() == 0)) {
			this._inprio = 0f;
		}

		if (edge.IsUnbreakable()) {
			this._numUnbreakableInEdges--;
		}

	}

	@Override
	public void RemoveOutEdge(HTBaseEdge edge) {
		super.RemoveOutEdge(edge);

		if (((HMAGraph) super.GetOwnerBaseGraph()).IsNodeEdgeDataValid()) {
			this.RemoveOutEdgeData((HMAEdge) edge);
		}

	}

	public void RemoveOutEdgeData(HMAEdge edge) {
		this._outprio -= edge.GetPriority();
		if ((this._outprio < 0f) || (super.GetOutdegree() == 0)) {
			this._outprio = 0f;
		}

		if (edge.IsUnbreakable()) {
			this._numUnbreakableOutEdges--;
		}

	}

	public void SetMinInPriority(float minPriority) {
		HMAEdgeIterator inEdges = this.GetInEdges();

		while (inEdges.HasNext()) {
			HMAEdge edge = inEdges.Next();
			if (edge.GetPriority() < minPriority) {
				edge.SetPriority(minPriority);
			}
		}

	}

	public void SetMinOutPriority(float minPriority) {
		HMAEdgeIterator outEdges = this.GetOutEdges();

		while (outEdges.HasNext()) {
			HMAEdge edge = outEdges.Next();
			if (edge.GetPriority() < minPriority) {
				edge.SetPriority(minPriority);
			}
		}

	}

	public void SetOrderingNumber(Integer number) {
		this._orderingNumber = number;

	}

}