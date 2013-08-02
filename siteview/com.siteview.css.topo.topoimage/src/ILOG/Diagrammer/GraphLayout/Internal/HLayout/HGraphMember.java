package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import system.*;

public class HGraphMember extends HTBaseGraphMember {
	private Integer _numID;

	public HGraphMember() {
	}

	public HTBaseGraphMember GetNext() {

		return super._next;

	}

	public Integer GetNumID() {

		return this._numID;

	}

	public HGraph GetOwnerGraph() {

		return (HGraph) super.GetOwner();

	}

	public HTBaseGraphMember GetPrev() {

		return super._prev;

	}

	public void SetNumID(Integer numID) {
		this._numID = numID;

	}

	public void SetOwner(HLink link) {
		super._owner = link;

	}

}