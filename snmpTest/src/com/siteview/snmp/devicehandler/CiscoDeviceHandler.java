package com.siteview.snmp.devicehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.snmp.common.AuxParam;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Bgp;
import com.siteview.snmp.pojo.Directitem;
import com.siteview.snmp.pojo.IfRec;
import com.siteview.snmp.pojo.RouteItem;
import com.siteview.snmp.pojo.RouterStandbyItem;
import com.siteview.snmp.scan.MibScan;
import com.siteview.snmp.util.ScanUtils;
import com.siteview.snmp.util.Utils;

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
		Map<String, List<Directitem>> result= new HashMap<String,List<Directitem>>();
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
					String[] ip_indexs = iip.getFirst().split("\\.");//ScanUtils.tokenize(iip.getFirst(), ".");
					if(ip_indexs.length < 14)
					{
						continue;
					}
//	                for(List<Pair<string,string> >::iterator iport = peerPorts.begin(); iport != peerPorts.end(); ++iport)
					for(Pair<String,String> iport : peerPorts)
					{
						String[] pt_indexs = iport.getFirst().split("\\.");//tokenize(iport->first, ".");
						if(pt_indexs.length < 14)
						{
							continue;
						}
						if(ip_indexs[14].equals(pt_indexs[14]) && ip_indexs[15].equals(pt_indexs[15]))
						{
							String ip_str = iip.getSecond().replaceAll(":", "");
							if(ip_str.length() > 10)
							{
								String ip_tmp = ip_str.substring(0,2) + "." 
										  + ip_str.substring(2,4) + "."
										  + ip_str.substring(4,6) + "."
										  + ip_str.substring(6,2);
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
					result.put(spr.getIp(), item_list);
				}
			}
		}
		return result;
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
//		getAftByQtp(snmp, spr);//1.3.6.1.2.1.17.7.1.2.2.1.2

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
	// 获取特定设备的接口信息
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
				List_cmty.add(new String(spr.getCommunity()) + "@0");// 增加vlan0的逻辑共同体?????????????????????????不明白为什么要加上这个共同体
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
							//日志 qDebug() << "commuinity_tmp : " <<
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public Map<String, RouterStandbyItem> getVrrpData(MibScan snmp, SnmpPara spr) {
		return super.getVrrpData(snmp, spr);
	}
	@Override
	public Map<String, List<String>> getStpData(MibScan snmp, SnmpPara spr) {
		// TODO Auto-generated method stub
		return null;
	}
	//获得逻辑共同体
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
			String vlanID = i.getSecond().substring(i.getSecond().indexOf("@") + 1);//i->second.substr(i->second.find("@") + 1);
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
		// for(list<string>::iterator i = list_cmty.begin(); i !=
		// list_cmty.end(); ++i)
		for (String i : List_cmty) {
			// qDebug() << "test cisco123";
			// if(getAftByDtp(snmp, SnmpPara(spr.ip, *i, spr.timeout,spr.retry),
			// oidIndexList))
			if (getAftByDtp(snmp, new SnmpPara(spr.getIp(), i,
					spr.getTimeout(), spr.getRetry()), oidIndexList)) {
				// qDebug() << "true";
			} else {
				// qDebug() << "false";
			}
		}
	}
	public Map<String, RouterStandbyItem> getHsrpData(MibScan snmp, SnmpPara spr){
		//routeStandby_list;.clear();
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
				String mac = i.getSecond().replaceAll(":", "").substring(0, 12);//replaceAll(i->second, " ", "").substr(0, 12);
				vrrpItem.getVirtualMacs().add(mac);//.virtualMacs.push_back(mac);
			}
			//填充虚拟IP
//	                for(list<pair<string,string> >::iterator i = hsrpvirip.begin(); i != hsrpvirip.end(); ++i)
			for(Pair<String, String> i : hsrpvirip)
			{
				vrrpItem.getVirtualIps().add(i.getSecond());//.virtualIps.push_back(i->second);						
			}

			routeStandby_list.put(spr.getIp(), vrrpItem);//.insert(make_pair(spr.ip, vrrpItem));
		}
		return routeStandby_list;
	}
}
