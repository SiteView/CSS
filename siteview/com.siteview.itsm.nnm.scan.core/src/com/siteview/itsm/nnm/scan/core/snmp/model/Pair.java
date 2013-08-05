package com.siteview.itsm.nnm.scan.core.snmp.model;

/**
 * һ���򵥵ļ�ֵ�Ե����ݽṹ
 * ����getFirst getSecond����������ȡ��һ�����͵ڶ������ݡ�
 * @author haiming.wang
 *
 * @param <K>
 * @param <V>
 */
public class Pair<K,V> {
	
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
}
