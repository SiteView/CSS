package ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import system.*;

public final class HLVEdge extends HMAEdge {
	private Integer _minSpan;

	public HLVEdge(float priority) {
		super(priority);
		this._minSpan = 0;
	}

	public HLVEdge(float priority, Integer minSpan) {
		super(priority);
		this._minSpan = minSpan;
	}

	public HLVNode GetHLVSource() {

		return (HLVNode) super._source;

	}

	public HLVNode GetHLVTarget() {

		return (HLVNode) super._target;

	}

	public Integer GetMinSpan() {

		return this._minSpan;

	}

	public HLVNode GetOpposite(HLVNode node) {
		if (node == super._source) {

			return (HLVNode) super._target;
		}

		return (HLVNode) super._source;

	}

	public void SetMinSpan(Integer span) {
		this._minSpan = span;

	}

}