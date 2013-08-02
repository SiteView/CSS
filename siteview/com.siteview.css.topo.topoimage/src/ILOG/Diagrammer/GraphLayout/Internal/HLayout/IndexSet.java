package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class IndexSet {
	private Integer[] _preds;

	private Integer _startIndex = -1;

	private Integer[] _succs;

	public IndexSet(Integer size) {
		this._preds = new Integer[size];
		this._succs = new Integer[size];
	}

	public void Clean() {
		this._preds = null;
		this._succs = null;

	}

	public Boolean Contains(Integer index) {
		if ((index < 0) || (index >= this._succs.length)) {

			return false;
		}
		if (((index != this._startIndex) && (this._preds[index] == -1))
				&& (this._succs[index] == -1)) {

			return false;
		}

		return true;

	}

	public void Discard(Integer index) {
		if ((index >= 0) && (index < this._succs.length)) {
			if (index == this._startIndex) {
				this._startIndex = this._succs[index];
			}
			if (this._preds[index] != -1) {
				this._succs[this._preds[index]] = this._succs[index];
			}
			if (this._succs[index] != -1) {
				this._preds[this._succs[index]] = this._preds[index];
			}
			this._preds[index] = -1;
			this._succs[index] = -1;
		}

	}

	public IntEnumeration Elements() {

		return new AnonClass_1(this);

	}

	public void InitEmpty() {
		this._startIndex = -1;
		for (Integer i = 0; i < this._preds.length; i++) {
			this._preds[i] = -1;
			this._succs[i] = -1;
		}

	}

	public void InitFull() {
		this._startIndex = 0;
		for (Integer i = 0; i < this._preds.length; i++) {
			this._preds[i] = i - 1;
			this._succs[i] = i + 1;
		}
		this._succs[this._succs.length - 1] = -1;

	}

	public void Insert(Integer index) {

		if (((index >= 0) && (index < this._succs.length))
				&& !this.Contains(index)) {
			this._succs[index] = this._startIndex;
			if (this._startIndex != -1) {
				this._preds[this._startIndex] = index;
			}
			this._startIndex = index;
		}

	}

	private class AnonClass_1 implements IntEnumeration {
		private IndexSet __outerThis;

		public Integer index;

		public AnonClass_1(IndexSet input__outerThis) {
			this.__outerThis = input__outerThis;
			this.index = this.__outerThis._startIndex;
		}

		public Boolean HasMoreElements() {

			return (this.index >= 0);

		}

		public Integer NextElement() {
			Integer index = this.index;
			this.index = this.__outerThis._succs[index];

			return index;

		}

	}
}