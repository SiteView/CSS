package ILOG.Diagrammer.Util;

import system.Math;
import system.Drawing.Rectangle;
import system.Drawing.RectangleF;

class DrawUtil {
	private static Integer _clearType = -1;

	public static Rectangle CropRectangle(RectangleF r) {
		float x = r.get_X();
		float y = r.get_Y();
		float num3 = (float) Math.Floor((double) x);
		float num4 = (float) Math.Floor((double) y);
		float num5 = (x - num3) + r.get_Width();
		float num6 = (y - num4) + r.get_Height();
		Rectangle re = new Rectangle();
		re.__Ctor__((int) num3, (int) num4, (int) Math.Ceiling((double) num5),
				(int) Math.Ceiling((double) num6));

		return re;// new Rectangle((int)num3, (int)num4,
					// (int)Math.Ceiling((double)num5),
					// (int)Math.Ceiling((double)num6));

	}

	// public static Boolean get_HasClearType(){
	// if(_clearType == -1){
	// _clearType = SystemInformation.
	// }
	//
	// return (_clearType == 2);
	// }

}