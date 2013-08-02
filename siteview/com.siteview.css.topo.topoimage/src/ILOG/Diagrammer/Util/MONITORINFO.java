package ILOG.Diagrammer.Util;

import system.*;
import system.ComponentModel.*;
import system.Runtime.InteropServices.*;

class MONITORINFO {
	private Integer cbSize = Marshal.SizeOf(Type.GetType(MONITORINFO.class
			.getName()));

	private RECT rcMonitor = new RECT(0, 0, 0, 0);

	public RECT rcWork = new RECT(0, 0, 0, 0);

	public Integer dwFlags = 0;

}