package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class HSegmentSort extends ArrayStableSort {
	public HSegmentSort() {
	}

	@Override
	public Boolean Compare(java.lang.Object o1, java.lang.Object o2) {
		if (o1 == null) {

			return false;
		}

		return ((o2 == null) || (((HSegment) o1).GetSortValue() <= ((HSegment) o2)
				.GetSortValue()));

	}

}