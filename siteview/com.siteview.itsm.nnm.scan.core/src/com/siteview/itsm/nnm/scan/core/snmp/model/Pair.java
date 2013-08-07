package com.siteview.itsm.nnm.scan.core.snmp.model;

import java.io.Serializable;
import java.util.Comparator;

import com.siteview.itsm.nnm.scan.core.snmp.util.ScanUtils;
import com.siteview.itsm.nnm.scan.core.snmp.util.Utils;

/**
 * һ���򵥵ļ�ֵ�Ե����ݽṹ
 * ����getFirst getSecond����������ȡ��һ�����͵ڶ������ݡ�
 * @author haiming.wang
 *
 * @param <K>
 * @param <V>
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class Pair<K extends Comparable,V> implements Serializable,Comparable<Pair<K,V>>{
	
	private K key;
	
	private V value;
	
	public Pair(){
	}
	
	public Pair(K k,V t){
		key = k;
		value = t;
	}
	public void setFirst(K first){
		key = first;
	}
	public void setSecond(V second){
		value = second;
	}
	public K getFirst(){
		return key;
	}
	public V getSecond(){
		return value;
	}
	public boolean isEmpty(){
		return ((key == null) || (value == null));
	}
	@Override
	public String toString() {
		return "[first value = " +getFirst() + "second value = " + getSecond()+"]";
	}


	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Pair<K, V> o) {
		if(Utils.isIp(o.getFirst().toString())){
			return ScanUtils.ipToLong(this.getFirst().toString()) > ScanUtils.ipToLong(o.getFirst().toString()) ? 1:-1;
		}
		return this.getFirst().compareTo(o.getFirst());
	}
}
