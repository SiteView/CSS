package com.siteview.snmp.model;

public class Pair<K,T> {
	private K key;
	private T value;
	public Pair(){
	}
	public Pair(K k,T t){
		key = k;
		value = t;
	}
	public void setFirst(K first){
		key = first;
	}
	public void setSecond(T second){
		value = second;
	}
	public K getFirst(){
		return key;
	}
	public T getSecond(){
		return value;
	}
	public boolean isEmpty(){
		return ((key == null) || (value == null));
	}
}
