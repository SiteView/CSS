package com.siteview.snmp.flowmonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.constants.CommonDef;
import com.siteview.snmp.pojo.Edge;
import com.siteview.snmp.pojo.FlowInfo;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.pojo.InterfaceTable;
import com.siteview.snmp.util.ThreadTaskPool;

/**
 * 流量监控的控制器
 * @author haiming.wang
 *
 */
public class MonitorControler {

	public MonitorControler(){
		
	}
	/**
	 * 界面通过这个变量停止流量监控
	 */
	public static volatile boolean isStop = false;
	private Monitor monitor = new Monitor();

	//流量信息 key:设备IP地址 value:设备各端口总流出、入流量
	public static Map<String,List<FlowInfo>> flowMap = new HashMap<String, List<FlowInfo>>();
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
		if(!flowMap.isEmpty()){
			List<FlowInfo> infos = flowMap.get(ip);
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
							long start = System.currentTimeMillis();
							handler();
							flowMap.clear();
							System.out.println("用时" + (System.currentTimeMillis() - start));
							System.out.println("");
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
		thread.start();
	}
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
							 * 找到边的左边接口索引对应的接口对象生成流量对象
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
								infos.add(info);
							}
						}
						flowMap.put(entry.getKey(), infos);
						System.out.print("\t" + entry.getKey());
						System.out.println("\t" + infos.size());
					}
				});
			}
		}
	}
}
