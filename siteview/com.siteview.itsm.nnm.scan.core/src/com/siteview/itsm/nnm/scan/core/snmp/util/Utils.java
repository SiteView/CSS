package com.siteview.itsm.nnm.scan.core.snmp.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snmp4j.smi.VariableBinding;

import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;

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
	public static <V> void collectionCopyAll(Collection<V> target,Collection<V> src){
		if(target == null || src == null){
			throw new NullPointerException();
		}
		if(src.size() == 0) return;
		for(V v :src){
			target.add(v);
		}
	}
	/**
	 * 比较两个集合的内容是否相等，按顺序比较。
	 * 集合中的对象必须实现 Comparable接口
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T extends Comparable<T>> boolean compare(List<T> a, List<T> b) {  
		  
        if (a.size() != b.size())  
            return false;  
        Collections.sort(a);  
        Collections.sort(b);  
        for (int i = 0; i < a.size(); i++) {  
            if (!a.get(i).equals(b.get(i)))  
                return false;  
        }  
        return true;  
    }
	/**
	 * ������ݵ�Map����
	 * @param target
	 * @param source
	 */
	public static <K,V> void mapAddAll(Map<K,V> target,Map<K,V> source){
		if(target == null || source == null) throw new NullPointerException();
		if(source.size() == 0) return;
		Set<K> keys = source.keySet();
		for(K k :keys){
			target.put(k, source.get(k));
		}
	}
	@SuppressWarnings("unused")
	public static void msain(String[] args) {
		Map<String, Pair<String, String>> target = new HashMap<String, Pair<String, String>>(); 
		Map<String, Pair<String, String>> src = new HashMap<String, Pair<String, String>>();
		
		Pair<String,String> t1 = new Pair<String, String>("t1", "t1");
		Pair<String,String> t2 = new Pair<String, String>("t2", "t2");
		target.put("t1", t1);
		target.put("t2", t2);
		
		Pair<String,String> c1 = new Pair<String, String>("c1", "c1");
		Pair<String,String> c2 = new Pair<String, String>("c2", "c2");
		src.put("c1", t1);
		src.put("c2", t2);
		Map<String, String> c = new HashMap<String,String>();
		mapAddAll(target, src);
		Set<String> keys = target.keySet();
		for(String key : keys){
			System.out.println("key: " + key + " value : " + target.get(key));
		}
	}
	public static Vector<VariableBinding> createVaribles(String middleFix){
		Vector<VariableBinding> vbs = new Vector<VariableBinding>();
		return vbs;
	}
	public static String parseToHexString(String value){
		if("".equals(value) || "0".equals(value)){
			return "00";
		}
		String hexStr =  Integer.toHexString(Integer.parseInt(value));
		if(hexStr.length() == 1){
			hexStr = "0" + hexStr;
		}
		return hexStr;
	}
	/**
	 *  判断传入的字符串是否为ip地址格式。
	 * @param src
	 * @return
	 */
	public static boolean isIp(String src){
		if((src.indexOf(".") < 0) || (src.split("\\.").length != 4)){
			return false;
		}
		String ipTest = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
		Pattern pattern = Pattern.compile(ipTest);
		Matcher matcher = pattern.matcher(src);
		return matcher.matches();
	}
	/**
	 * 格式化mac地址
	 * @param mac 没有分隔符的mac地址 如 FFFFFFFFFFFF
	 * @param seq 分隔符 如":"
	 * @return FF:FF:FF:FF:FF:FF
	 */
	public static String formatMac(String mac,String seq){
		int start = 0;
		int tmpL = 2;
		StringBuffer sb = new StringBuffer("");
		if(mac.length() == 12){
			while(tmpL <=12){
				sb.append(mac.substring(start,tmpL)).append(seq);
				start = tmpL;
				tmpL += 2;
			}
			return sb.toString();
		}
		return mac;
	}
}
