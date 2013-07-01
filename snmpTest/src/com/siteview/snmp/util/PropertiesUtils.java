package com.siteview.snmp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {
	static Properties prop = new Properties();
	public static void load(String filename){
		try {
			clear();
			prop.load(new FileInputStream(new File(filename)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void clear(){
		prop.clear();
	}
	public static String getValue(String key){
		if(prop.get(key) != null){
			return prop.getProperty(key);
		}
		return "";
	}
	
}
