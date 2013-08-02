package ILOG.Diagrammer.GraphLayout;

import system.*;

public interface GraphModelContentsChangedEventAction {
	public static int BeginUpdate = 0x100;
	public static int EndUpdate = 0x200;
	public static int GeometryChanged = 2;
	public static int LinkAdded = 0x10;
	public static int LinkGeometryChanged = 0x80;
	public static int LinkRemoved = 0x20;
	public static int NodeAdded = 4;
	public static int NodeGeometryChanged = 0x40;
	public static int NodeRemoved = 8;
	public static int StructureChanged = 1;
	public static int Updating = 0x400;
}