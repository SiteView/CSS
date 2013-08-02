package ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public class HMAEdge extends HTBaseEdge {
	public short _flags;

	private float _priority;

	public HMAEdge(float priority) {
		this._priority = priority;
		this._flags = 0;
	}

	public HMANode GetHMAOpposite(HMANode node) {
		if (node == super._source) {

			return (HMANode) super._target;
		}

		return (HMANode) super._source;

	}

	public HMANode GetHMASource() {

		return (HMANode) super._source;

	}

	public HMANode GetHMATarget() {

		return (HMANode) super._target;

	}

	public float GetPriority() {

		return this._priority;

	}

	public Boolean IsReversed() {

		return ((this._flags & 1) != 0);

	}

	public Boolean IsTwoCycle() {

		return ((this._flags & 2) != 0);

	}

	public Boolean IsUnbreakable() {

		return ((this._flags & 4) != 0);

	}

	public void MakeUnbreakable() {
		this._flags = (short) (this._flags | 4);

	}

	public void MarkReversed() {

		if (this.IsReversed()) {
			this._flags = (short) (this._flags & -2);
		} else {
			this._flags = (short) (this._flags | 1);
		}

	}

	public void MarkTwoCycle() {
		this._flags = (short) (this._flags | 2);

	}

	public void SetPriority(float priority) {
		if (priority <= 0f) {
			priority = 0.1f;
		}
		if (priority > 10000f) {
			priority = 10000f;
		}

		if (((HMAGraph) super.GetOwnerBaseGraph()).IsNodeEdgeDataValid()) {
			((HMANode) super._source).RemoveOutEdgeData(this);
			((HMANode) super._target).RemoveInEdgeData(this);
		}
		this._priority = priority;

		if (((HMAGraph) super.GetOwnerBaseGraph()).IsNodeEdgeDataValid()) {
			((HMANode) super._source).AddOutEdgeData(this);
			((HMANode) super._target).AddInEdgeData(this);
		}

	}

}