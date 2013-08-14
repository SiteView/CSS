package com.siteview.itsm.nnm.scan.core.snmp.flowmonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import com.siteview.itsm.nnm.scan.core.snmp.common.SnmpPara;
import com.siteview.itsm.nnm.scan.core.snmp.constants.CommonDef;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Edge;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.FlowInfo;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.InterfaceTable;
import com.siteview.itsm.nnm.scan.core.snmp.util.ThreadTaskPool;
import com.siteview.itsm.nnm.scan.core.snmp.util.Utils;

/**
 * 流量监控的控制器
 * @author haiming.wang
 *
 */
public class MonitorControler {

	public MonitorControler(){
		
	}
	public static int interval = 5000;
	/**
	 * 界面通过这个变量停止流量监控
	 */
	public static volatile boolean isStop = false;
	private Monitor monitor = new Monitor();
	public static Map<String,List<FlowInfo>> flowMap = new ConcurrentHashMap<String,List<FlowInfo>>();
	//流量信息 key:设备IP地址 value:设备各端口总流出、入流量
	public Stack<Map<String,List<FlowInfo>>> flowCountStack = new Stack<Map<String,List<FlowInfo>>>();
	
	public static Map<String,List<FlowInfo>> realTimeFlow = new ConcurrentHashMap<String,List<FlowInfo>>();
	
	public static boolean stop = false;
	/**
	 * 需要监控的设备列表
	 */
	private Map<String, IDBody> deviceList = new ConcurrentHashMap<String, IDBody>();
	/**
	 * 
	 * @param deviceList
	 * @param edges
	 */
	private List<Edge> edges = new ArrayList<Edge>();
	/**
	 * 根据ip和端口索引获取总流量
	 * @param ip ip地址
	 * @param index 端口索引
	 * @return
	 */
	public static long getFlowMap(String ip,int index){
		if(!realTimeFlow.isEmpty()){
			List<FlowInfo> infos = realTimeFlow.get(ip);
			for(FlowInfo  i : infos){
				if(i.getIfIndex() == index){
					return (i.getInFlow() + i.getOutFlow());
				}
			}
		}
		return 0;
	}
	public MonitorControler(Map<String, IDBody> deviceList,List<Edge> edges){
		this.deviceList = deviceList;
		this.edges = edges;
	}
	public void start(){
		Thread thread = new Thread(
				new Runnable() {
					
					@Override
					public void run() {
						while(!isStop){
							handler();
							calculateFlow();
							try {
								Thread.sleep(interval);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						isStop = false;
					}
				});
		thread.start();
	}
	/**
	 * 计算实时流量 上一次扫描保存的总流量 - 本次扫描得到的总流量
	 * 如果 本次扫描得到的流量为0 (总流量己经达到端口的设定最大流量值) 用上一次计算得到的实时流量值（不修改流量信息）
	 */
	private void calculateFlow(){
		Map<String,List<FlowInfo>> previous = null;
		//判断流量栈是否为空
		if(!flowCountStack.isEmpty()){
			//取出上次保存的流量信息
			previous = flowCountStack.pop();
		}
		//如果上次保存的流量信息不为空
		if(previous != null){
			for(Entry<String, List<FlowInfo>> i : previous.entrySet()){
				//本次扫描的流量集合
				List<FlowInfo> thisFlowList = flowMap.get(i.getKey());
				
				//上次保存的流量集合
				List<FlowInfo> previousFlowList = i.getValue();
				if(thisFlowList == null || thisFlowList.size() == 1){
					continue;
				}
				//实时流量集合
				List<FlowInfo> realTimeFlowList = new ArrayList<FlowInfo>();
				for(FlowInfo j : thisFlowList){
					for(FlowInfo k : previousFlowList){
						if(j.getIfIndex() == k.getIfIndex()){
							FlowInfo fInfo = new FlowInfo();
							long realinflow = j.getInFlow();
							long realoutflow = j.getOutFlow();
							/**
							 * 如果扫描到的流量信息为0 
							 */
							if((realinflow == 0) && (realoutflow == 0)){
								break;
							}
							realinflow = realinflow - k.getInFlow();
							realoutflow = realoutflow - k.getOutFlow();
							List<FlowInfo> realflow = realTimeFlow.get(i.getKey());
							//判断ip地址对应的实时流量信息是否为空 如果不为空将设置新的实时流量信息
							if(realflow != null && !realflow.isEmpty()){
								boolean hasIndex = false;
								for(FlowInfo tmpinfo : realflow){
									if(tmpinfo.getIfIndex() == j.getIfIndex()){
										tmpinfo.setInFlow(realinflow);
										tmpinfo.setOutFlow(realoutflow);
										tmpinfo.setTime(System.currentTimeMillis());
										hasIndex = true;
									}
								}
								//如果上次保存的流量信息没有此端口
								if(!hasIndex){
									fInfo.setDesRight(k.getDesRight());
									fInfo.setIfMac(k.getIfMac());
									fInfo.setSrcLift(k.getSrcLift());
									fInfo.setTime(System.currentTimeMillis());
									fInfo.setInFlow(realinflow);
									fInfo.setOutFlow(realoutflow);
									fInfo.setIfIndex(k.getIfIndex());
									realTimeFlowList.add(fInfo);
								}
							}else{//如果为空生成新的流量信息并加入到集合
								fInfo.setDesRight(k.getDesRight());
								fInfo.setIfMac(k.getIfMac());
								fInfo.setSrcLift(k.getSrcLift());
								fInfo.setTime(System.currentTimeMillis());
								fInfo.setInFlow(realinflow);
								fInfo.setOutFlow(realoutflow);
								fInfo.setIfIndex(k.getIfIndex());
								realTimeFlowList.add(fInfo);
							}
							//这里需要将实时流量信息保存到数据库？？？？？？？？？？？？？？？？？？？？？？？？？？？？
							break;
						}
					}
				}
				realTimeFlow.put(i.getKey(), realTimeFlowList);
			}
		}
		Map<String,  List<FlowInfo>> te = new HashMap<String,  List<FlowInfo>>();
		Utils.mapAddAll(te, flowMap);
		flowCountStack.push(te);
		flowMap.clear();
	}
	//扫描接口表
	private void handler(){
		for(final Entry<String, IDBody> entry : deviceList.entrySet()){
			final IDBody body = entry.getValue();
			//监控路由设备
			if(body.getDevType().equals(CommonDef.ROUTE_SWITCH)
					|| body.getDevType().equals(CommonDef.ROUTER)
					|| body.getDevType().equals(CommonDef.SWITCH)){
				ThreadTaskPool.getInstance().excute(new Runnable() {
					@Override
					public void run() {
						SnmpPara spr = new SnmpPara();
						spr.setCommunity(body.getCommunity_get());
						spr.setIp(entry.getKey());
						spr.setRetry(2);
						spr.setTimeout(200);
						spr.setSnmpver("0");
						//获取设备接口表信息
						Map<String, InterfaceTable> tables = monitor.monitor(spr);
						List<FlowInfo> infos = new ArrayList<FlowInfo>();
						for (Edge edge : edges) {// 循环边数据
							// 如果边的左边节点ip不等于当前扫描IP继续下次循环
							if (!edge.getIp_left().equals(spr.getIp())) {
								continue;
							}
							InterfaceTable iftab = tables.get(edge.getInf_left());
							if (iftab == null) {
								continue;
							}
							/**
							 * 找到边的左边接口索引对应的接口生成流量对象
							 */
							if (iftab.getIfIndex() == Integer.parseInt(edge
									.getInf_left())
									&& (edge.getIp_left().equals(entry.getKey()))) {
								
								FlowInfo info = new FlowInfo();
								info.setSrcLift(entry.getKey());
								info.setInFlow(iftab.getIfInoctets());
								info.setOutFlow(iftab.getIfOutOctets());
								info.setDesRight(edge.getIp_right());
								info.setIfMac(iftab.getIfPhysAddress());
								info.setIfIndex(iftab.getIfIndex());
								info.setTime(System.currentTimeMillis());
								//判断端口是否己保存上次的总流量数据
								infos.add(info);
							}
						}
						flowMap.put(entry.getKey(), infos);
					}
				});
			}
		}
	}
}
