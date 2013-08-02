package ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public class HRPSolvePriorityQueue extends PriorityQueue {
	@Override
	public float GetValue(java.lang.Object obj) {

		return ((HRPNode) obj).GetBaryCenter();

	}

}