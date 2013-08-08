package com.siteview.css.topo.editparts;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopoParsing {

	private Map<String, String> map = new HashMap<String, String>();

	/**
	 * Task������gml
	 * 
	 */
	public Map<String, String> readFileByChars() {
		// System.out.println("���ļ�~=======================");
		Reader reader = null;
		try {
			// һ�ζ�һ���ַ�
			reader = new InputStreamReader(new FileInputStream(
					"C:\\����ͼ\\css1.gml"));
			int tempchar;
			StringBuffer d = new StringBuffer("");
			while ((tempchar = reader.read()) != -1) {
				if (((char) tempchar) != '\r' && ((char) tempchar) != '\t'
						&& ((char) tempchar) != '\n') {
					d.append((char) tempchar);
				}
			}
			reader.close();

			Pattern pattern = Pattern.compile("node\\[.*\\]");
			Matcher matcher = pattern.matcher(d.toString().replaceAll(" ", ""));
			if (matcher.find()) {
				String[] spit = matcher.group(0).split("node");
				if (spit != null) {
					for (String str : spit) {
						if (str.indexOf("id") == -1)
							continue;
						// System.out.println(str);
						String key = str.substring(str.indexOf("id") + 2,
								str.indexOf("label"));
						String values = str.substring(
								str.indexOf("graphics") + 1,
								str.indexOf("LabelGraphics"));
						String x = values.substring(values.indexOf("x") + 1,
								values.indexOf("y"));
						String y = values.substring(values.indexOf("y") + 1,
								values.indexOf("w"));
						map.put(key, x + "-" + y);
					}
				}
			}
			// Iterator it = map.keySet().iterator();
			// while (it.hasNext()) {
			// String key = (String) it.next();
			// String value = map.get(key);
			// System.out.println("key="+key+"-----value="+value);
			// }
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
