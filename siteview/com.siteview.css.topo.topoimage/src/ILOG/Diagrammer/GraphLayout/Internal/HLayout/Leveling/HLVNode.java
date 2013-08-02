package ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import system.*;

public final class HLVNode extends HMANode {
	private Integer _distToOtherGroupNode;

	private short _flags;

	private Integer _maxLevelNumber;

	private Integer _minLevelNumber;

	private HLVNode _otherGroupNode;

	public Integer MAX = 0x7fffffff;

	public Integer MIN = 0;

	public Integer UNSPECIFIED = -1;

	public HLVNode() {
		super.SetOrderingNumber(-1);
		this._minLevelNumber = 0;
		this._maxLevelNumber = 0x7fffffff;
		this._otherGroupNode = null;
		this._distToOtherGroupNode = 0;
		this._flags = 0;
	}

	public void ConnectToOtherGroupNode(HLVNode node, Integer dist) {
		this._otherGroupNode = node;
		this._distToOtherGroupNode = dist;

	}

	public HLVEdgeIterator GetInAndOutEdges() {

		return new HLVAdjEdgeIterator(this);

	}

	public HLVEdgeIterator GetInEdges() {

		return new HLVInEdgeIterator(this);

	}

	public Integer GetLevelNumber() {

		return super.GetOrderingNumber();

	}

	public Integer GetMaxLevelNumber() {

		return this._maxLevelNumber;

	}

	public Integer GetMinLevelNumber() {

		return this._minLevelNumber;

	}

	public HLVEdgeIterator GetOutEdges() {

		return new HLVOutEdgeIterator(this);

	}

	public Boolean IsPropagationGroupEndNode() {

		return ((this._distToOtherGroupNode < 0) && (this._otherGroupNode != null));

	}

	public Boolean IsPropagationGroupStartNode() {

		return ((this._distToOtherGroupNode > 0) && (this._otherGroupNode != null));

	}

	public Integer LimitLevelNumber(Integer val) {
		if (val < this._minLevelNumber) {

			return this._minLevelNumber;
		}
		if (val > this._maxLevelNumber) {

			return this._maxLevelNumber;
		}

		return val;

	}

	private Boolean NeedsBackwardPropagationToStartNodes() {

		return ((this._flags & 2) != 0);

	}

	private Boolean NeedsForwardPropagationToEndNodes() {

		return ((this._flags & 1) != 0);

	}

	public void PropagateAfterSettingMaxLevel(Integer val) {

		if (this.IsPropagationGroupEndNode()) {
			this.PropagateBackward(val);
		} else if (this.NeedsBackwardPropagationToStartNodes()) {
			this.PropagateBackwardToStartNodes(val);
		}

		if (this.IsPropagationGroupStartNode()) {
			HLVNode node = this._otherGroupNode;
			val += this._distToOtherGroupNode;
			node.SetMaxLevelNumber(val);
		}

	}

	public void PropagateAfterSettingMinLevel(Integer val) {

		if (this.IsPropagationGroupStartNode()) {
			this.PropagateForward(val);
		} else if (this.NeedsForwardPropagationToEndNodes()) {
			this.PropagateForwardToEndNodes(val);
		}

		if (this.IsPropagationGroupEndNode()) {
			HLVNode node = this._otherGroupNode;
			val += this._distToOtherGroupNode;
			node.SetMinLevelNumber(val);
		}

	}

	public void PropagateBackward(Integer val) {
		HLVEdgeIterator inEdges = this.GetInEdges();

		while (inEdges.HasNext()) {
			HLVEdge edge = inEdges.Next();
			Integer minSpan = edge.GetMinSpan();
			edge.GetHLVSource().SetMaxLevelNumber(val - minSpan);
		}

	}

	public void PropagateBackwardToStartNodes(Integer val) {
		HLVEdgeIterator inEdges = this.GetInEdges();

		while (inEdges.HasNext()) {
			HLVEdge edge = inEdges.Next();
			Integer minSpan = edge.GetMinSpan();
			HLVNode hLVSource = edge.GetHLVSource();

			if (hLVSource.IsPropagationGroupStartNode()) {
				hLVSource.SetMaxLevelNumber(val - minSpan);
			}
		}

	}

	public void PropagateForward(Integer val) {
		HLVEdgeIterator outEdges = this.GetOutEdges();

		while (outEdges.HasNext()) {
			HLVEdge edge = outEdges.Next();
			Integer minSpan = edge.GetMinSpan();
			edge.GetHLVTarget().SetMinLevelNumber(val + minSpan);
		}

	}

	public void PropagateForwardToEndNodes(Integer val) {
		HLVEdgeIterator outEdges = this.GetOutEdges();

		while (outEdges.HasNext()) {
			HLVEdge edge = outEdges.Next();
			Integer minSpan = edge.GetMinSpan();
			HLVNode hLVTarget = edge.GetHLVTarget();

			if (hLVTarget.IsPropagationGroupEndNode()) {
				hLVTarget.SetMinLevelNumber(val + minSpan);
			}
		}

	}

	public void SetLevelNumber(Integer val) {
		super.SetOrderingNumber(val);

		if (!this.IsPropagationGroupStartNode()) {
			this.PropagateForward(val);
		}
		this.PropagateAfterSettingMinLevel(val);
		this.PropagateAfterSettingMaxLevel(val);

	}

	public void SetMaxLevelNumber(Integer val) {
		if (((this.GetLevelNumber() == -1) && (val >= this._minLevelNumber))
				&& (val < this._maxLevelNumber)) {
			this._maxLevelNumber = val;
			this.PropagateAfterSettingMaxLevel(val);
		}

	}

	public void SetMinLevelNumber(Integer val) {
		if (((this.GetLevelNumber() == -1) && (val <= this._maxLevelNumber))
				&& (val > this._minLevelNumber)) {
			this._minLevelNumber = val;
			this.PropagateAfterSettingMinLevel(val);
		}

	}

	public void UpdatePropagationFlags() {

		if (this.IsPropagationGroupStartNode()) {
			HLVEdgeIterator outEdges = this.GetOutEdges();

			while (outEdges.HasNext()) {
				outEdges.Next().GetHLVTarget()
						.UseBackwardPropagationToStartNodes();
			}
		}

		if (this.IsPropagationGroupEndNode()) {
			HLVEdgeIterator inEdges = this.GetInEdges();

			while (inEdges.HasNext()) {
				inEdges.Next().GetHLVSource().UseForwardPropagationToEndNodes();
			}
		}

	}

	private void UseBackwardPropagationToStartNodes() {
		this._flags = (short) (this._flags | 2);

	}

	private void UseForwardPropagationToEndNodes() {
		this._flags = (short) (this._flags | 1);

	}

}