package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public class HGraphMemberList {
	private HGraphMember _first = null;

	private Boolean _IDsUpToDate = true;

	private HGraphMember _last = null;

	private Integer _size = 0;

	public HGraphMemberList() {
	}

	public void Add(HGraph owner, HGraphMember obj) {
		if (this._last != null) {
			obj.InsertAfter(owner, this._last);
			this._last = obj;
		} else {
			obj.InsertBefore(owner, null);
			this._first = this._last = obj;
		}
		obj.SetNumID(this._size);
		this._size++;

	}

	public Boolean Contains(HGraphMember obj) {

		return ((obj != null) && obj.IsContainedIn(this._first));

	}

	public HGraphMember GetFirst() {

		return this._first;

	}

	public HGraphMember GetLast() {

		return this._last;

	}

	public Integer GetSize() {

		return this._size;

	}

	public Boolean IsIDsUpToDate() {

		return this._IDsUpToDate;

	}

	public void Remove(HGraphMember obj) {
		if (this._first == obj) {
			this._first = (HGraphMember) obj.GetNext();
		}
		if (this._last == obj) {
			this._last = (HGraphMember) obj.GetPrev();
		}
		obj.Remove();
		this._size--;
		this._IDsUpToDate = false;

	}

	public void RemoveLast() {
		Boolean flag = this._IDsUpToDate;
		this.Remove(this._last);
		this._IDsUpToDate = flag;

	}

	public void UpdateNumIDs() {
		if (!this._IDsUpToDate) {
			Integer num = 0;
			for (HGraphMember member = this._first; member != null; member = (HGraphMember) member
					.GetNext()) {
				member.SetNumID(num++);
			}
			this._IDsUpToDate = true;
		}

	}

}