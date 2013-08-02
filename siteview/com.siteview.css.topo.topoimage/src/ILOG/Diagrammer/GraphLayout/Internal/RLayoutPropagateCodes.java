package ILOG.Diagrammer.GraphLayout.Internal;

import system.*;

public interface RLayoutPropagateCodes {
	public static int Ambiguous = 2;
	public static int ClassMismatch = 4;
	public static int Exception = 0x10;
	public static int MethodMismatch = 8;
	public static int Success = 1;
}