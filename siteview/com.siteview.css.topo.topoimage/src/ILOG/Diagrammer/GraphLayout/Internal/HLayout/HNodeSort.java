package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class HNodeSort extends ArrayStableSort {
	public HNodeSort() {
	}

	@Override
	public Boolean Compare(java.lang.Object o1, java.lang.Object o2) {

		return (((HNode) o1).GetSortValue() <= ((HNode) o2).GetSortValue());

	}

}