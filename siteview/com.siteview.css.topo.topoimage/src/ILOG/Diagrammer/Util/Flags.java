package ILOG.Diagrammer.Util;

import system.*;

final class Flags {
	private Integer _flags;

	public Flags() {
	}

	public Flags(Integer flags) {
		this._flags = flags;
	}

	public Boolean GetFlag(Integer flag) {

		return ((this._flags & flag) != 0);

	}

	public void SetFlag(Integer flag, Boolean value) {
		if (value) {
			this._flags |= flag;
		} else {
			this._flags &= ~flag;
		}

	}

}