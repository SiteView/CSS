package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class HConstraintSort extends ArrayStableSort {
	public HConstraintSort() {
	}

	@Override
	public Boolean Compare(java.lang.Object o1, java.lang.Object o2) {
		if (o1 == null) {

			return false;
		}

		return ((o2 == null) || (((HierarchicalConstraint) o1).get_Priority() >= ((HierarchicalConstraint) o2)
				.get_Priority()));

	}

}