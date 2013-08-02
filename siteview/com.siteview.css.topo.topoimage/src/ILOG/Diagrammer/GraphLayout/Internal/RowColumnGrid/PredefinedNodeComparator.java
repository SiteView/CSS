package ILOG.Diagrammer.GraphLayout.Internal.RowColumnGrid;

import system.ArgumentException;
import system.Collections.IComparer;

public class PredefinedNodeComparator implements IComparer {
	private String _type = "";

	private static String ASCENDING_AREA = "ASCENDING_AREA";

	private static String ASCENDING_HEIGHT = "ASCENDING_HEIGHT";

	private static String ASCENDING_INDEX = "ASCENDING_INDEX";

	private static String ASCENDING_WIDTH = "ASCENDING_WIDTH";

	private static String AUTOMATIC_ORDERING = "AUTOMATIC_ORDERING";

	private static String DESCENDING_AREA = "DESCENDING_AREA";

	private static String DESCENDING_HEIGHT = "DESCENDING_HEIGHT";

	private static String DESCENDING_INDEX = "DESCENDING_INDEX";

	private static String DESCENDING_WIDTH = "DESCENDING_WIDTH";

	private static String NO_ORDERING = "NO_ORDERING";

	public PredefinedNodeComparator() {
	}

	public int Compare(java.lang.Object o1, java.lang.Object o2) {
		throw (new system.Exception(
				"This instance of Comparator should not be used directly, but only for customizing a layout"));

	}

	public static IComparer GetFieldFromName(String name) {
		if ((name != null) && (name.length() > 0)) {

			if (name.equals(NO_ORDERING)) {

				return Graph.NO_ORDERING;
			}

			if (name.equals(AUTOMATIC_ORDERING)) {

				return Graph.AUTOMATIC_ORDERING;
			}

			if (name.equals(DESCENDING_HEIGHT)) {

				return Graph.DESCENDING_HEIGHT;
			}

			if (name.equals(ASCENDING_HEIGHT)) {

				return Graph.ASCENDING_HEIGHT;
			}

			if (name.equals(ASCENDING_WIDTH)) {

				return Graph.ASCENDING_WIDTH;
			}

			if (name.equals(DESCENDING_WIDTH)) {

				return Graph.DESCENDING_WIDTH;
			}

			if (name.equals(ASCENDING_AREA)) {

				return Graph.ASCENDING_AREA;
			}

			if (name.equals(DESCENDING_AREA)) {

				return Graph.DESCENDING_AREA;
			}

			if (name.equals(ASCENDING_INDEX)) {

				return Graph.ASCENDING_INDEX;
			}

			if (name.equals(DESCENDING_INDEX)) {

				return Graph.DESCENDING_INDEX;
			}
		}
		throw (new system.Exception(
				"Unsupported type of predefined comparator: " + name));

	}

	public String GetName() {
		if (this == Graph.NO_ORDERING) {

			return NO_ORDERING;
		}
		if (this == Graph.AUTOMATIC_ORDERING) {

			return AUTOMATIC_ORDERING;
		}
		if (this == Graph.DESCENDING_HEIGHT) {

			return DESCENDING_HEIGHT;
		}
		if (this == Graph.ASCENDING_HEIGHT) {

			return ASCENDING_HEIGHT;
		}
		if (this == Graph.ASCENDING_WIDTH) {

			return ASCENDING_WIDTH;
		}
		if (this == Graph.DESCENDING_WIDTH) {

			return DESCENDING_WIDTH;
		}
		if (this == Graph.ASCENDING_AREA) {

			return ASCENDING_AREA;
		}
		if (this == Graph.DESCENDING_AREA) {

			return DESCENDING_AREA;
		}
		if (this == Graph.ASCENDING_INDEX) {

			return ASCENDING_INDEX;
		}
		if (this == Graph.DESCENDING_INDEX) {

			return DESCENDING_INDEX;
		}

		return null;

	}

	public static String[] GetPredefinedComparatorNames() {

		return new String[] { NO_ORDERING, AUTOMATIC_ORDERING,
				DESCENDING_HEIGHT, ASCENDING_HEIGHT, ASCENDING_WIDTH,
				DESCENDING_WIDTH, ASCENDING_AREA, DESCENDING_AREA,
				ASCENDING_INDEX, DESCENDING_INDEX };

	}

	public IComparer GetReadComparator() {

		return GetFieldFromName(this._type);

	}

	public static Boolean IsPredefinedComparatorName(String name) {
		if ((name == null) || (name.trim().length() == 0)) {
			throw (new ArgumentException("name cannot be null or empty"));
		}
		String[] predefinedComparatorNames = GetPredefinedComparatorNames();
		for (Integer i = 0; i < predefinedComparatorNames.length; i++) {

			if (name.equals(predefinedComparatorNames[i])) {

				return true;
			}
		}

		return false;

	}

	public static void StoreType(PredefinedNodeComparator comparator) {
		if (comparator != null) {
			if (comparator == Graph.NO_ORDERING) {
				comparator._type = NO_ORDERING;
			} else if (comparator == Graph.AUTOMATIC_ORDERING) {
				comparator._type = AUTOMATIC_ORDERING;
			} else if (comparator == Graph.DESCENDING_HEIGHT) {
				comparator._type = DESCENDING_HEIGHT;
			} else if (comparator == Graph.ASCENDING_HEIGHT) {
				comparator._type = ASCENDING_HEIGHT;
			} else if (comparator == Graph.ASCENDING_WIDTH) {
				comparator._type = ASCENDING_WIDTH;
			} else if (comparator == Graph.DESCENDING_WIDTH) {
				comparator._type = DESCENDING_WIDTH;
			} else if (comparator == Graph.ASCENDING_AREA) {
				comparator._type = ASCENDING_AREA;
			} else if (comparator == Graph.DESCENDING_AREA) {
				comparator._type = DESCENDING_AREA;
			} else if (comparator == Graph.ASCENDING_INDEX) {
				comparator._type = ASCENDING_INDEX;
			} else {
				if (comparator != Graph.DESCENDING_INDEX) {
					throw (new system.Exception(
							"Unsupported predefined comparator: " + comparator));
				}
				comparator._type = DESCENDING_INDEX;
			}
		}

	}

}