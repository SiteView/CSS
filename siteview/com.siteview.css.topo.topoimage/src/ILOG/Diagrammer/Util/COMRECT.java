package ILOG.Diagrammer.Util;

import system.Drawing.Rectangle;

public class COMRECT {
	public Integer left;

	public Integer top;

	public Integer right;

	public Integer bottom;

	public COMRECT() {
	}

	public COMRECT(Rectangle r) {
		this.left = r.get_X();
		this.top = r.get_Y();
		this.right = r.get_Right();
		this.bottom = r.get_Bottom();
	}

	public COMRECT(Integer left, Integer top, Integer right, Integer bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public static COMRECT FromXYWH(Integer x, Integer y, Integer width,
			Integer height) {

		return new COMRECT(x, y, x + width, y + height);

	}

	@Override
	public String toString() {

		return clr.System.StringStaticWrapper.Concat(new java.lang.Object[] {
				"Left = ", this.left, " Top ", this.top, " Right = ",
				this.right, " Bottom = ", this.bottom });

	}

}