package com.siteview.itsm.nnm.scan.core.snmp.devicehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.itsm.nnm.scan.core.snmp.common.AuxParam;
import com.siteview.itsm.nnm.scan.core.snmp.common.ScanParam;
import com.siteview.itsm.nnm.scan.core.snmp.common.SnmpPara;
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Bgp;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.DeviceCpuInfo;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.DeviceMemInfo;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Directitem;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IfRec;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.RouteItem;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.RouterStandbyItem;
import com.siteview.itsm.nnm.scan.core.snmp.scan.MibScan;
import com.siteview.itsm.nnm.scan.core.snmp.util.ScanUtils;
import com.siteview.itsm.nnm.scan.core.snmp.util.Utils;

public class CiscoDeviceHandler extends UnivDeviceHandler implements IDeviceHandler {

	List<String> List_cmty = new ArrayList<String>();
	public CiscoDeviceHandler(){
		
	}
	public CiscoDeviceHandler(ScanParam scanParm,AuxParam auxParam){
		
	}
	@Override
	public Map<String, List<Directitem>> getDirectData(MibScan snmp,
			SnmpPara spr) {
		//cdp
		directData_list.clear();
		//PeerIP 1.3.6.1.4.1.9.9.23.1.2.1.1.4(localportindex,peerip)
	    List<Pair<String,String> > peerIPs = snmp.getMibTable(spr,"1.3.6.1.4.1.9.9.23.1.2.1.1.4");
		if(peerIPs != null && !peerIPs.isEmpty())
		{
			//PeerPort 1.3.6.1.4.1.9.9.23.1.2.1.1.7(localportindex,peerport)
	       List<Pair<String,String> > peerPorts = snmp.getMibTable(spr, "1.3.6.1.4.1.9.9.23.1.2.1.1.7");
			if(peerPorts != null &&!peerPorts.isEmpty())
			{
				//[localportindex,peerid,peerip,peerportdesc]
				List<Directitem> item_list = new ArrayList<Directitem>();
				for(Pair<String,String> iip : peerIPs)
				{
					String[] ip_indexs = iip.getFirst().split("\\.");
					if(ip_indexs.length < 14)
					{
						continue;
					}
					for(Pair<String,String> iport : peerPorts)
					{
						String[] pt_indexs = iport.getFirst().split("\\.");
						if(pt_indexs.length < 14)
						{
							continue;
						}
						if(ip_indexs[14].equals(pt_indexs[14]) && ip_indexs[15].equals(pt_indexs[15]))
						{
							String ip_str = iip.getSecond().replaceAll(":", "");
							if(ip_str.length() >= 8)
							{
								String ip_tmp = Integer.parseInt(ip_str.substring(0,2), 16)  
										  + "." 
										  + Integer.parseInt(ip_str.substring(2,4), 16) 
										  + "."
										  + Integer.parseInt(ip_str.substring(4,6), 16)  
										  + "."
										  + Integer.parseInt(ip_str.substring(6,8), 16) ;
								boolean bNew = true;
								for(Directitem item : item_list)
								{
									if(item.getLocalPortInx().equals(ip_indexs[14])
											&& item.getPeerIp().equals(ip_tmp)
											&& item.getPeerPortDsc().equals(iport.getSecond()))
									{
										bNew = false;
										break;
									}
								}
								if(bNew)
								{
									Directitem item_tmp = new Directitem();
									item_tmp.setLocalPortInx(ip_indexs[14]);
									item_tmp.setLocalPortDsc("");
									item_tmp.setPeerId("");
									item_tmp.setPeerIp(ip_tmp);
									item_tmp.setPeerPortDsc(iport.getSecond());
									item_tmp.setPeerPortInx("");
									item_list.add(item_tmp);
								}
							}
						}
					}
				}
				if(!item_list.isEmpty())
				{
					directData_list.put(spr.getIp(), item_list);
				}
			}
		}
		return directData_list;
	}
	/**
	 * 获取特定设备的AFT数据{devip,{port,[mac]}}
	 */
	@Override
	public Map<String, Map<String, List<String>>> getAftData(MibScan snmp,
			SnmpPara spr, Map<String, String> oidIndexList) {
		aft_list.clear();
		port_macs.clear();
		getAftByDtp(snmp, spr, oidIndexList);//1.3.6.1.2.1.17.4.3.1.2
		getAftByLogicEntity(snmp, spr, oidIndexList);	

		if(!port_macs.isEmpty())
		{
			aft_list.put(spr.getIp(), port_macs);//put(spr.getIp(), port_macs);//.insert(make_pair(spr.ip, port_macs));
		}
		return aft_list;
	}
	@Override
	public Map<String, Map<String, List<Pair<String, String>>>> getArpData(
			MibScan snmp, SnmpPara spr, Map<String, String> oidIndexList) {
		return super.getArpData(snmp, spr, oidIndexList);
	}
	@Override
	public Map<String, Map<String, List<String>>> getOspfNbrData(MibScan snmp,
			SnmpPara spr) {
		return super.getOspfNbrData(snmp, spr);
	}
	@Override
	public Map<String, Map<String, List<RouteItem>>> getRouteData(MibScan snmp,
			SnmpPara spr, Map<String, String> oidIndexList) {
		return super.getRouteData(snmp, spr, oidIndexList);
	}
	@Override
	public List<Bgp> getBgpData(MibScan snmp, SnmpPara spr) {
		return super.getBgpData(snmp, spr);
	}
	/**
	 * 获取特定设备的接口信息
	 * snmp snmp查询对象
	 * spr snmp查询参数
	 * oidIndexList  oid索引列表
	 * bRouter 是否是路由器
	 */
	@Override
	public Map<String, Pair<String, List<IfRec>>> getInfProp(MibScan snmp,
			SnmpPara spr, Map<String, String> oidIndexList, boolean bRouter) {
		ifprop_List.clear();
		Map<String, Pair<String, List<IfRec>>> result = new HashMap<String, Pair<String, List<IfRec>>>();
		//接口数量
		String infAmount = snmp.getMibObject(spr, "1.3.6.1.2.1.2.1.0");
		if (!Utils.isEmptyOrBlank(infAmount)) {
			// 接口类型 1.3.6.1.2.1.2.2.1.3
			List<Pair<String, String>> infTypes = snmp.getMibTable(spr,
					"1.3.6.1.2.1.2.2.1.3");
			// 接口描述 1.3.6.1.2.1.2.2.1.2
			List<Pair<String, String>> infDescs = snmp.getMibTable(spr,
					"1.3.6.1.2.1.2.2.1.2");
			// 端口接口对应关系1.3.6.1.2.1.17.1.4.1.2
			List<Pair<String, String>> infPorts = new ArrayList<Pair<String,String>>();
			List<Pair<String, String>> infPortsEx = new ArrayList<Pair<String,String>>();
			if (!bRouter) {
				getLogicEntity(new MibScan(), spr);
				// 用接口描述补充逻辑共同体列表
				List_cmty.add(new String(spr.getCommunity()) + "@0");
				for (Pair<String, String> idesc : infDescs)
				{
					String prefix_vlan = idesc.getSecond().substring(0, 4)
							.toUpperCase();
					if (prefix_vlan.equals("VLAN")) {
						String vlanID = idesc.getSecond().substring(4);
						if (vlanID.equals("1") || vlanID.equals("1002")
								|| vlanID.equals("1003") || vlanID.equals("1004")
								|| vlanID.equals("1005")) {
							continue;
						}
						String community_tmp = new String(spr.getCommunity())
								+ "@" + vlanID;
						if (!List_cmty.contains(community_tmp))
						{
							List_cmty.add(community_tmp);
						}
						break;
					}
				}
				// 1.用原有的共同体读Port与Ifindex的对应关系
				infPorts = snmp.getMibTable(spr, "1.3.6.1.2.1.17.1.4.1.2");
				List<String> ifindexList = new ArrayList<String>();
				for (Pair<String, String> iinfPort : infPorts)
				{
					ifindexList.add(iinfPort.getSecond());
				}
				// 2.用逻辑共同体补充Port与Ifindex的对应关系
				for (String i : List_cmty)
				{
					List<Pair<String, String>> infPorts_tmp = snmp.getMibTable(
							new SnmpPara(spr.getIp(), i, spr.getTimeout(), spr
									.getRetry()), "1.3.6.1.2.1.17.1.4.1.2");
					for (Pair<String, String> iinfPort : infPorts_tmp)
					{
						if (!ifindexList.contains(iinfPort.getSecond()))
						{
							infPorts.add(iinfPort);
							ifindexList.add(iinfPort.getSecond());
						}
					}
				}
				// cisco私有oid Ifindex-Port
				infPortsEx = snmp.getMibTable(spr,
						"1.3.6.1.4.1.9.9.276.1.5.1.1.1");
			}
			// 接口MAC地址 1.3.6.1.2.1.2.2.1.6
			List<Pair<String, String>> infMacs = snmp.getMibTable(spr,
					"1.3.6.1.2.1.2.2.1.6");
			// 接口速度 1.3.6.1.2.1.2.2.1.5
			List<Pair<String, String>> infSpeeds = snmp.getMibTable(spr,
					"1.3.6.1.2.1.2.2.1.5");

			List<IfRec> infprops = new ArrayList<IfRec>();// (ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)
			for (Pair<String, String> itype : infTypes)
			{
				IfRec inf_tmp = new IfRec();
				inf_tmp.setIfIndex(itype.getFirst().substring(20));
				inf_tmp.setIfType(itype.getSecond());
				for (Pair<String, String> idesc : infDescs)
				{
					if (idesc.getFirst().substring(20)
							.equals(inf_tmp.getIfIndex()))
					{
						inf_tmp.setIfDesc(idesc.getSecond());
						break;
					}
				}
				inf_tmp.setIfPort(inf_tmp.getIfIndex());
				boolean reConvert = true;

				for (Pair<String, String> iport : infPorts) {
					if (iport.getSecond().equals(inf_tmp.getIfIndex()))
					{
						inf_tmp.setIfPort(iport.getFirst().substring(23));
						reConvert = false;
						break;
					}
				}
				if (reConvert) {
					for (Pair<String, String> iport : infPortsEx) {
						if (iport.getFirst().substring(30)
								.equals(inf_tmp.getIfIndex()))
						{
							inf_tmp.setIfPort(iport.getSecond());
							break;
						}
					}
				}
				for (Pair<String, String> imac : infMacs) {
					if (imac.getFirst().substring(20)
							.equals(inf_tmp.getIfIndex()))
					{
						String temp_imac = imac.getSecond().replaceAll(":", "")
								.toUpperCase();
						if(temp_imac.length() >12){
							temp_imac = temp_imac.substring(0,12);
						}
						inf_tmp.setIfMac(temp_imac);
						break;
					}
				}
				for (Pair<String, String> ispeed : infSpeeds) {
					if (ispeed.getFirst().substring(20)
							.equals(inf_tmp.getIfIndex()))
					{
						inf_tmp.setIfSpeed(ispeed.getSecond());
						break;
					}
				}
				infprops.add(inf_tmp);
			}
			if (!infprops.isEmpty()) {
				Pair<String, List<IfRec>> infpropMap = new Pair<String, List<IfRec>>();
				infpropMap.setFirst(infAmount);
				infpropMap.setSecond(infprops);
				result.put(spr.getIp(), infpropMap);
			}
		}
		return result;
	}
	@Override
	public void getInfFlow(MibScan snmp, SnmpPara spr) {
		
	}
	@Override
	public Map<String, RouterStandbyItem> getVrrpData(MibScan snmp, SnmpPara spr) {
		return super.getVrrpData(snmp, spr);
	}
	@Override
	public Map<String, List<String>> getStpData(MibScan snmp, SnmpPara spr) {
		Map<String,List<String>> stp_list = new HashMap<String,List<String>>();
		for(String i : List_cmty){
			List<Pair<String,String>> stpport = snmp.getMibTable(spr, "1.3.6.1.2.1.17.2.15.1.1");
			if(stpport == null || stpport.isEmpty()) return null;
			for(Pair<String, String> item : stpport){
				List<String> p = stp_list.get(spr.getIp());
				if(p != null){
					if(!p.contains(item.getSecond())){
						p.add(item.getSecond());
					}
				}else{
					List<String> portList = new ArrayList<String>();
					portList.add(item.getSecond());
					stp_list.put(spr.getIp(),portList);
				}
			}
			
		}
		return stp_list;
	}
	/**
	 * 获取逻辑共同体
	 * @param snmp
	 * @param spr
	 */
	private void getLogicEntity(MibScan snmp, SnmpPara spr)
	{
		//逻辑共同体1.3.6.1.2.1.47.1.2.1.1.4 (ciscio4006不支持这个OID)
	    List<Pair<String,String>> entity_List = snmp.getMibTable(spr,"1.3.6.1.2.1.47.1.2.1.1.4");
		if(entity_List==null || entity_List.size() == 0)
		{
			entity_List = snmp.getMibTable(spr,"1.3.6.1.4.1.9.9.68.1.2.2.1.2");//取得Vlan号
			if(entity_List==null || entity_List.size() == 0)
			{
				return;
			}
	        for(Pair<String,String> i :entity_List)
			{
	                	i.setSecond(new String(spr.getCommunity() + "@" + i.getSecond()));
			}
		}
		for(Pair<String, String> i : entity_List)
		{
			String vlanID = i.getSecond().substring(i.getSecond().indexOf("@") + 1);
			if(vlanID.equals("1") || vlanID.equals("1002") || vlanID.equals("1003") || vlanID.equals("1004") || vlanID.equals("1005")){
				continue;
			}
			if(!List_cmty.contains(i.getSecond()))
			{
				List_cmty.add(i.getSecond());
				//日志 qDebug() << "comm : " << (i->second).c_str();
			}
		}
	}

	// 根据逻辑共同体获取转发表数据
	private void getAftByLogicEntity(MibScan snmp, SnmpPara spr,
			Map<String, String> oidIndexList) {
		for (String i : List_cmty) {
			if (getAftByDtp(snmp, new SnmpPara(spr.getIp(), i,
					spr.getTimeout(), spr.getRetry()), oidIndexList)) {
				// qDebug() << "true";
			} else {
				// qDebug() << "false";
			}
		}
	}
	public Map<String, RouterStandbyItem> getHsrpData(MibScan snmp, SnmpPara spr){
		//1.3.6.1.4.1.9.9.106.1.2.1.1.11 :cHsrpGrpVirtualIpAddr	
	    List<Pair<String,String> > hsrpvirip = snmp.getMibTable(spr, "1.3.6.1.4.1.9.9.106.1.2.1.1.1");
		if(!hsrpvirip.isEmpty())
		{
			//1.3.6.1.4.1.9.9.106.1.2.1.1.16 :cHsrpGrpVirtualMacAddr
	        List<Pair<String,String> > hsrpvirmacs = snmp.getMibTable(spr, "1.3.6.1.4.1.9.9.106.1.2.1.1.16");
			RouterStandbyItem vrrpItem = new RouterStandbyItem();
			//填充虚拟MAC
			for(Pair<String, String> i :hsrpvirmacs)
			{			
				String mac = i.getSecond().replaceAll(":", "").substring(0, 12);
				vrrpItem.getVirtualMacs().add(mac);
			}
			//填充虚拟IP
			for(Pair<String, String> i : hsrpvirip)
			{
				vrrpItem.getVirtualIps().add(i.getSecond());				
			}

			routeStandby_list.put(spr.getIp(), vrrpItem);
		}
		return routeStandby_list;
	}
	@Override
	public DeviceCpuInfo getDeviceCpuInfo(MibScan snmp, SnmpPara spr) {
		DeviceCpuInfo info = new DeviceCpuInfo();
		String cpuUsed5M = snmp.getMibObject(spr, ".1.3.6.1.4.1.9.2.1.58.0");//获取过去5分钟的CPU load (cpu繁忙的百分比)
		String cpuUsed1M = snmp.getMibObject(spr, ".1.3.6.1.4.1.9.2.1.57.0");//获取过去1分钟的CPU load (cpu繁忙的百分比)
		String cpuUsed5S = snmp.getMibObject(spr, ".1.3.6.1.4.1.9.2.1.56.0");//获取过去5秒的CPU load (cpu繁忙的百分比)
		
		info.setUsedCpu1M(Integer.parseInt(cpuUsed1M));
		info.setUsedCpu5M(Integer.parseInt(cpuUsed5M));
		info.setUsedCpu5S(Integer.parseInt(cpuUsed5S));
		info.setIp(spr.getIp());
		
		return info;
	}
	@Override
	public DeviceMemInfo getdeviceMemInfo(MibScan snmp, SnmpPara spr) {
		DeviceMemInfo result = new DeviceMemInfo();
		String freeMem = snmp.getMibObject(spr, "1.3.6.1.4.1.9.2.1.8.0");
		result.setMemFree(Integer.parseInt(freeMem));
		return result;
	}
	public static void main(String[] args) {
		MibScan scan = new MibScan();
		//1.3.6.1.4.1.9.9.23.1.2.1.1.4
//		List<Pair<String,String>> result = scan.getMibTable(new SnmpPara("192.168.9.1", "public", 200, 2, "2"), "1.3.6.1.2.1.17.4.3.1.2");
		List<Pair<String,String>> result = scan.getMibTable(new SnmpPara("192.168.9.1", "public", 200, 2, "2"), "1.3.6.1.4.1.9.9.23.1.2.1.1.7");
		System.out.println(result.size());
	}
}
