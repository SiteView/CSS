package com.siteview.snmp.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.siteview.snmp.pojo.FlowInfo;

public class FlowStorage {

	/**
	 * 查询的流水号
	 */
	private int serialNum;
	/**
	 * 
	 */
	public Map<String,List<FlowInfo>> flowMap = new HashMap<String, List<FlowInfo>>();
	
	public synchronized void reset(){
		if(flowMap.size()>=1000){
			flowMap.clear();
		}
	}
	static int i = 0;
	public static void main(String[] args) {
		Stack<Integer> stack = new Stack<Integer>();
		int tmp=0;
		int a = 0;
		while(true){
		if(!stack.isEmpty()){
			tmp = stack.pop();
			System.out.println(i-tmp);
		}
		stack.push(i++);}
		
	}
}
