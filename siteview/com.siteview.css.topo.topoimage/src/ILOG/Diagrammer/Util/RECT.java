package ILOG.Diagrammer.Util;

import system.*;
import system.ComponentModel.*;
import system.Drawing.*;
import system.Runtime.InteropServices.*;

public class RECT {
	public Integer left;

	public Integer top;

	public Integer right;

	public Integer bottom;

	public RECT(Integer x, Integer y, Integer w, Integer h) {
		this.left = x;
		this.top = y;
		this.right = x + w;
		this.bottom = y + h;
	}

	public RECT(Rectangle r) {
		this.left = r.get_X();
		this.top = r.get_Y();
		this.right = r.get_Right();
		this.bottom = r.get_Bottom();
	}

}