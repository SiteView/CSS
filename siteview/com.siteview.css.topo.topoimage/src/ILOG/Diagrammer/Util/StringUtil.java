package ILOG.Diagrammer.Util;

final class StringUtil {
	private StringUtil() {
	}

	public static String[] Split(String s, char[] delimiters) {
		String[] strArray = s.split(new String(delimiters));
		Integer num = 0;
		for (Integer i = 0; i < strArray.length; i++) {
			if (strArray[i].length() == 0) {
				num++;
			}
		}
		if (num == 0) {

			return strArray;
		}
		String[] strArray2 = new String[strArray.length - num];
		Integer num3 = 0;
		for (Integer j = 0; j < strArray.length; j++) {
			if (strArray[j].length() != 0) {
				strArray2[num3++] = strArray[j];
			}
		}

		return strArray2;

	}

}