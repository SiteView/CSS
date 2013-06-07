package com.siteview.snmp.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import com.siteview.snmp.constants.OIDConstants;

public class Utils {

	public static String buildFirstLetterCapMethodName(String value){
		if(value !=null && value.trim().length()>0){
			char[] cs = value.toCharArray();
			cs[0] = getCapitalized(cs[0]);
			return new String(cs);
		}
		return "";
	}
	public static double getDoubleValueFromString(String value){
		return (isEmptyOrBlank(value)?0.0d:Double.valueOf(value));
	}
	public static boolean isEmptyOrBlank(String value){
		if(value == null || "".equals(value) || "".equals(value.trim())){
			return true;
		}
		return false;
	}
	/**
	 *传入的参数不能为空
	 * @param value
	 * @return
	 */
	public static int getIntValueFromNotNullString(String value){
		if(isEmptyOrBlank(value)){
			throw new NullPointerException("value is null");
		}
		return Integer.parseInt(value);
	}
	/**
	 * 如果传入的参数为空就返回0
	 * @param value
	 * @return
	 */
	public static int getIntValueFromAllowNullString(String value){
		if(isEmptyOrBlank(value)){
			return 0;
		}
		return Integer.parseInt(value);
	}
	public static char getCapitalized(char value){
		 return Character.toUpperCase(value);
	}
	@SuppressWarnings("unchecked")
	public static Vector<VariableBinding> createVaribles(String middleFix){
		Vector<VariableBinding> vbs = new Vector<VariableBinding>();
//		Class clazz = OIDConstants.class;
//		Field[] fields = clazz.getDeclaredFields();
//		for(Field f:fields){
//			String name = f.getName();
//			if(name.indexOf("middleFix") > 0){
//				String[] names = name.split("_");
//				String methedName = "get"
//						+ Utils.buildFirstLetterCapMethodName(names[0].toLowerCase()) 
//						+ Utils.buildFirstLetterCapMethodName(names[1].toLowerCase())
//						+ Utils.buildFirstLetterCapMethodName(names[2].toLowerCase());
//				try {
//					Method m = clazz.getMethod(methedName, null);
//					OID oid = (OID)m.invoke(clazz, (Object[])null);
//					vbs.add(new VariableBinding(oid));
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					e.printStackTrace();
//				} catch (NoSuchMethodException e) {
//					e.printStackTrace();
//				} catch (SecurityException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		return vbs;
	}
}
