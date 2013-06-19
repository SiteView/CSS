package com.siteview.snmp.scan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;

import com.siteview.snmp.common.AuxParam;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.constants.CommonDef;
import com.siteview.snmp.constants.OIDConstants;
import com.siteview.snmp.devicehandler.IDeviceHandler;
import com.siteview.snmp.model.Device;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Bgp;
import com.siteview.snmp.pojo.DevicePro;
import com.siteview.snmp.pojo.Directitem;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.pojo.IfRec;
import com.siteview.snmp.pojo.RouteItem;
import com.siteview.snmp.pojo.RouterStandbyItem;
import com.siteview.snmp.pojo.SystemGroup;
import com.siteview.snmp.util.OIDTypeUtils;
import com.siteview.snmp.util.ScanUtils;
import com.siteview.snmp.util.ThreadTaskPool;
import com.siteview.snmp.util.Utils;

public class ReadService {

	//设备基本信息列表{devIP,(TYPE,SNMP,[IP],[MAC],[MASK],SYSOID,sysname)}
	Map<String, IDBody> devid_list = new HashMap<String,IDBody>();
	//设备接口属性列表 {devIP,(ifAmount,[(ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)])}
	Map<String, Pair<String, List<IfRec>>> ifprop_list = new HashMap<String,Pair<String,List<IfRec>>>();
	//设备AFT数据列表 {sourceIP,{port,[MAC]}}
	Map<String, Map<String, List<String>>> aft_list = new HashMap<String, Map<String,List<String>>>();
	//设备ARP数据列表 {sourceIP,{infInx,[(MAC,destIP)]}}
	Map<String, Map<String, List<Pair<String, String>>>> arp_list = new HashMap<String, Map<String,List<Pair<String,String>>>>();
	//设备OSPF邻居列表 {sourceIP,{infInx,[destIP]}}
	Map<String, Map<String, List<String>>> ospfnbr_list = new HashMap<String, Map<String,List<String>>>();
	List<Bgp> bgp_list = new ArrayList<Bgp>();
	public Map<String,String> map_devType = new HashMap<String, String>();
	//设备路由表 {sourceIP,{infInx,[nextIP]}}
	Map<String, Map<String, List<RouteItem>>> route_list = new HashMap<String, Map<String, List<RouteItem>>>();
	List<Pair<SnmpPara,Pair<String,String>>> sproid_list = new ArrayList<Pair<SnmpPara,Pair<String,String>>>();
	private Map<String, IDBody> devid_list_visited = new HashMap<String, IDBody>();
	Map<String, DevicePro> dev_type_list = new HashMap<String,DevicePro>();
	public List<String> ip_visited_list = new ArrayList<String>();
	private ThreadTaskPool pool ;
	private ScanParam scanParam;
	private AuxParam auxParam;
	boolean isStop = false;
	byte[] lock = new byte[0];
	Map<String, RouterStandbyItem> routeStandby_list = new HashMap<String, RouterStandbyItem>();
	Map<String, Map<String, String> > special_oid_list = new HashMap<String, Map<String,String>>();
	Map<String, List<Directitem>> directdata_list = new HashMap<String, List<Directitem>>();
	Map<String,List<String>> stp_list = new HashMap<String,List<String>>();
	public ScanParam getScanParam() {
		return scanParam;
	}

	public void setScanParam(ScanParam scanParam) {
		this.scanParam = scanParam;
	}

	public AuxParam getAuxParam() {
		return auxParam;
	}

	public void setAuxParam(AuxParam auxParam) {
		this.auxParam = auxParam;
	}

	public List<String> getIp_visited_list() {
		return ip_visited_list;
	}

	public void setIp_visited_list(List<String> ip_visited_list) {
		this.ip_visited_list = ip_visited_list;
	}

	public Map<String, IDBody> getDevid_list_visited() {
		return devid_list_visited;
	}

	public void setDevid_list_visited(Map<String, IDBody> devid_list_visited) {
		this.devid_list_visited = devid_list_visited;
	}
	private Map<String, IDBody> devid_list_valid = new HashMap<String,IDBody>();
	
	public Map<String, IDBody> getDevid_list() {
		return devid_list;
	}

	public void setDevid_list(Map<String, IDBody> devid_list) {
		this.devid_list = devid_list;
	}

	public Map<String, Pair<String, List<IfRec>>> getIfprop_list() {
		return ifprop_list;
	}

	public void setIfprop_list(Map<String, Pair<String, List<IfRec>>> ifprop_list) {
		this.ifprop_list = ifprop_list;
	}

	public Map<String, Map<String, List<String>>> getAft_list() {
		return aft_list;
	}

	public void setAft_list(Map<String, Map<String, List<String>>> aft_list) {
		this.aft_list = aft_list;
	}

	public Map<String, Map<String, List<Pair<String, String>>>> getArp_list() {
		return arp_list;
	}

	public void setArp_list(
			Map<String, Map<String, List<Pair<String, String>>>> arp_list) {
		this.arp_list = arp_list;
	}

	public Map<String, Map<String, List<String>>> getOspfnbr_list() {
		return ospfnbr_list;
	}

	public void setOspfnbr_list(Map<String, Map<String, List<String>>> ospfnbr_list) {
		this.ospfnbr_list = ospfnbr_list;
	}

	public List<Bgp> getBgp_list() {
		return bgp_list;
	}

	public void setBgp_list(List<Bgp> bgp_list) {
		this.bgp_list = bgp_list;
	}

	public Map<String, String> getMap_devType() {
		return map_devType;
	}

	public void setMap_devType(Map<String, String> map_devType) {
		this.map_devType = map_devType;
	}

	public Map<String, Map<String, List<RouteItem>>> getRoute_list() {
		return route_list;
	}

	public void setRoute_list(Map<String, Map<String, List<RouteItem>>> route_list) {
		this.route_list = route_list;
	}

	public List<Pair<SnmpPara, Pair<String, String>>> getSproid_list() {
		return sproid_list;
	}

	public void setSproid_list(
			List<Pair<SnmpPara, Pair<String, String>>> sproid_list) {
		this.sproid_list = sproid_list;
	}

	public Map<String, DevicePro> getDev_type_list() {
		return dev_type_list;
	}

	public void setDev_type_list(Map<String, DevicePro> dev_type_list) {
		this.dev_type_list = dev_type_list;
	}

	public Map<String, RouterStandbyItem> getRouteStandby_list() {
		return routeStandby_list;
	}

	public void setRouteStandby_list(
			Map<String, RouterStandbyItem> routeStandby_list) {
		this.routeStandby_list = routeStandby_list;
	}

	public Map<String, Map<String, String>> getSpecial_oid_list() {
		return special_oid_list;
	}

	public void setSpecial_oid_list(
			Map<String, Map<String, String>> special_oid_list) {
		this.special_oid_list = special_oid_list;
	}

	public Map<String, List<Directitem>> getDirectdata_list() {
		return directdata_list;
	}

	public void setDirectdata_list(Map<String, List<Directitem>> directdata_list) {
		this.directdata_list = directdata_list;
	}

	public Map<String, List<String>> getStp_list() {
		return stp_list;
	}

	public void setStp_list(Map<String, List<String>> stp_list) {
		this.stp_list = stp_list;
	}

	public Map<String, IDBody> getDevid_list_valid() {
		return devid_list_valid;
	}

	public void setDevid_list_valid(Map<String, IDBody> devid_list_valid) {
		this.devid_list_valid = devid_list_valid;
	}
	CountDownLatch latch ;
	//获取所有设备的普通数据
	public boolean getDeviceData(Vector<SnmpPara> spr_list)
	{
	        if (!getSysInfos(spr_list))
	        {
	            return false;
	        }
		
		aft_list.clear();
		arp_list.clear();
		ifprop_list.clear();
		ospfnbr_list.clear();
		route_list.clear(); //changed by zhang 2009-03-26 去掉路由表的取数
		bgp_list.clear();
		
		if(sproid_list.size() > 0)
		{
////	                qDebug() << "sproid size : " << sproid_list.size();
			if(sproid_list.size() == 1)
			{
				getOneDeviceData(sproid_list.get(0).getFirst(),sproid_list.get(0).getSecond().getFirst(),sproid_list.get(0).getSecond().getSecond());//->second.second);
			}
			else
			{
				latch = new CountDownLatch(spr_list.size());
				//pool tp(scanPara.thrdamount);//(min(thrdAmount,ip_communitys.size()));
//	                        pool tp(min(scan.thrdamount, sproid_list.size()));//by zhangyan 2008-12-29
				
//	                        for (list<pair<SnmpPara, pair<String,String> > >::const_iterator i = sproid_list.begin(); i != sproid_list.end(); ++i)
				for(final Pair<SnmpPara, Pair<String,String>> i : sproid_list)
				{
	                                if (isStop)
	                                {
	                                    return false;
	                                }
	                                else
	                                {
//	                                    tp.schedule((boost::bind(&ReadService::getOneDeviceData,this, (*i).first, (*i).second.first, (*i).second.second)));
	                                	ThreadTaskPool.getInstance().excute(new Runnable() {
											
											@Override
											public void run() {
												getOneDeviceData(i.getFirst(), i.getSecond().getFirst(), i.getSecond().getSecond());
												latch.countDown();
											}
										});
	                                }
				}
//	            ThreadTaskPool.getInstance().wai();
				try {
					latch.await();//等待所有线程完成
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	        return true;
	}

	// 获取一台设备的普通数据
	public void getOneDeviceData(SnmpPara spr, String devType, String sysOid)
	{
//	        SvLog::writeLog("Start read the data of " + spr.ip);
		IDeviceHandler device = DeviceFactory.getInstance().createDevice(sysOid);//DeviceFactory::Instance()->CreateDevice(getFacOid(sysOid));
		//device->Init(scanPara, auxPara); //by zhangyan 2008-10-28

		Map<String, Map<String, List<String>>> aftlist_cur = new HashMap<String, Map<String, List<String>>>();
		Map<String,Map<String, List<Pair<String, String>>>> arplist_cur = new HashMap<String, Map<String,List<Pair<String,String>>>>();
		Map<String,Pair<String,List<IfRec>>> inflist_cur = new HashMap<String, Pair<String,List<IfRec>>>();;
		Map<String, Map<String, List<String>>> nbrlist_cur = new HashMap<String, Map<String, List<String>>>();
		Map<String,Map<String,List<RouteItem>>> rttbl_cur = new HashMap<String, Map<String,List<RouteItem>>>();			//changed by zhang 2009-03-26 去掉路由表数据
		//changed again by wings 2009-11-13 恢复路由表
		List<Bgp> bgplist_cur = new ArrayList<Bgp>();
		/*VRRP_LIST vrrplist_cur;*/
		Map<String, RouterStandbyItem> vrrplist_cur = new HashMap<String, RouterStandbyItem>();
		Map<String,List<Directitem>> drctdata_cur = new HashMap<String, List<Directitem>>();
		Map<String,List<String>> stplist_cur = new HashMap<String, List<String>>();

		boolean bAft = false;
		boolean bArp = false;
		boolean bInf = true;
		boolean bNbr = false;
		boolean bRoute = false;
		boolean bBgp = false;
		boolean bVrrp = false; 
		boolean bDirect = true;

		MibScan snmp = new MibScan();
		//获取oid索引 add by jiangshanwen
		Map<String, String> oidIndexList = new HashMap<String,String>();
		getOidIndex(oidIndexList, sysOid);

		inflist_cur = device.getInfProp(snmp, spr, oidIndexList, devType.equals(CommonDef.ROUTER));
		drctdata_cur = device.getDirectData(snmp, spr);//->getDirectData(snmp, spr);
		

		if(devType.equals(CommonDef.ROUTE_SWITCH) || devType.equals(CommonDef.FIREWALL))// == ROUTE_SWITCH  || devType == FIREWALL)
		{//r-s
			bAft = true;
			bArp = true;				
			bRoute = true;	
			aftlist_cur = device.getAftData(snmp, spr, oidIndexList);
			arplist_cur = device.getArpData(snmp, spr, oidIndexList);
			rttbl_cur = device.getRouteData(snmp, spr, oidIndexList);  // 去掉路由表的取数
															// 恢复路由表
			if(auxParam.getNbr_read_type().equals("1"))
			{
				bNbr = true;
				nbrlist_cur = device.getOspfNbrData(snmp, spr);
			}
			if(auxParam.getBgp_read_type().equals("1"))
			{
				bBgp = true;
				bgplist_cur = device.getBgpData(snmp, spr);
			}
			if(auxParam.getVrrp_read_type().equals("1"))
			{
				bVrrp = true;
				vrrplist_cur = device.getVrrpData(snmp, spr);
			}
			//remarked by zhangyan 2008-11-28
			//if(aftlist_cur.empty())  
			//{
	                //	map<string, list<string> > portmac_tmp = telnetReader->getAftData(getDeviceIps(spr.ip));
			//	if(!portmac_tmp.empty())
			//	{
			//		aftlist_cur.insert(make_pair(spr.ip, portmac_tmp));
			//	}
			//}		
			//再用telnet读数
			//added by zhangyan 2009-01-13
//			Vector<String> ips_tmp = devid_list_valid[spr.ip].ips;
			Vector<String> ips_tmp = devid_list_valid.get(spr.getIp()).getIps();
			String ip_tmp = "";
//			for (vector<string>::iterator iter = ips_tmp.begin(); iter != ips_tmp.end(); ++iter)
			for(String iter :ips_tmp)
			{//
//				if (find(telnetReader->telnetIPList_Aft.begin(), telnetReader->telnetIPList_Aft.end(), *iter) != telnetReader->telnetIPList_Aft.end())
//				{
//					ip_tmp = *iter;
//					break;
//				}
			}
			if (ip_tmp != "") //该IP配置了telnet读aft表
//			{
//				//用telnet读该IP的aft表
//	                        map<string, list<string> > portmac_tmp = telnetReader->getAftData(ip_tmp);
//				if(!portmac_tmp.empty())
//				{
//					if (aftlist_cur.empty())
//					{
//						aftlist_cur.insert(make_pair(spr.ip, portmac_tmp));
//					}
//					else
//					{
//						//合并端口集
//	                                        for (map<string, list<string> >::iterator port_mac = portmac_tmp.begin(); port_mac != portmac_tmp.end(); ++port_mac)
//						{//port-macs
//							if (aftlist_cur[spr.ip].find(port_mac->first) != aftlist_cur[spr.ip].end())
//							{//存在该端口
//								//bexits = true;
//								for (list<string>::iterator idestmac = port_mac->second.begin(); idestmac != port_mac->second.end(); ++idestmac)
//								{
//									if (find(aftlist_cur[spr.ip][port_mac->first].begin(), aftlist_cur[spr.ip][port_mac->first].end(), *idestmac) == aftlist_cur[spr.ip][port_mac->first].end())
//									{//不存在该mac
//										aftlist_cur[spr.ip][port_mac->first].push_back(*idestmac);
//									}
//								}
//							}
//							else
//							{
//								aftlist_cur[spr.ip].insert(make_pair(port_mac->first, port_mac->second));
//							}
//						}
//					}
//				}
//			}
			
			/*if(arplist_cur.empty())
			{*/
				//added by zhangyan 2009-01-13
				ip_tmp = "";
//				for (vector<string>::iterator iter = ips_tmp.begin(); iter != ips_tmp.end(); ++iter)
//				{
//					if (find(telnetReader->telnetIPList_Arp.begin(), telnetReader->telnetIPList_Arp.end(), *iter) != telnetReader->telnetIPList_Arp.end())
//					{
//						ip_tmp = *iter;
//						break;
//					}
//				}
//				if (ip_tmp != "") //该IP配置了telnet读arp表
//				{
//					//用telnet读该IP的arp表
//	                                map<string, list<pair<string,string> > > portipmac_tmp = telnetReader->getArpData(ip_tmp);
//					//update by jiangshanwen 20100925
//					if(!portipmac_tmp.empty())
//					{
//						if (arplist_cur.empty())
//						{
//							arplist_cur.insert(make_pair(spr.ip, portipmac_tmp));
//						}
//						else
//						{
//	                                                for (map<string,list<pair<string,string> > >::iterator iter = portipmac_tmp.begin();iter != portipmac_tmp.end();++iter)
//							{
//								if (arplist_cur[spr.ip].find(iter->first) != arplist_cur[spr.ip].end())
//								{
//	                                                                for (list<pair<string,string> >::iterator item = iter->second.begin();item != iter->second.end();++item)
//									{
//	                                                                        for (list<pair<string,string> >::iterator mac_ip = arplist_cur[spr.ip][iter->first].begin();mac_ip != arplist_cur[spr.ip][iter->first].end();++mac_ip)
//										{
//											if (item->first == mac_ip->first)
//											{
//												continue;
//											}
//											else
//											{
//												arplist_cur[spr.ip][iter->first].push_back(*item);
//											}
//										}
//									}
//								}
//								else
//								{
//									arplist_cur[spr.ip].insert(make_pair(iter->first,iter->second));
//								}
//							}
//						}
//					}
//				}
			/*}*/
		}
		else if(devType.equals(CommonDef.SWITCH))
		{//s
			bAft = true;
			aftlist_cur = device.getAftData(snmp, spr, oidIndexList);
			//remarked by zhangyan 2008-11-28
			//if(aftlist_cur.empty())  
			//{
	                //	map<string, list<string> > portmac_tmp = telnetReader->getAftData(getDeviceIps(spr.ip));
			//	if(!portmac_tmp.empty())
			//	{
			//		aftlist_cur.insert(make_pair(spr.ip, portmac_tmp));
			//	}
			//}		
			//再用telnet读数
//			vector<string> ips_tmp = devid_list_valid[spr.ip].ips;
			Vector<String> ips_tmp = devid_list_valid.get(spr.getIp()).getIps();
			String ip_tmp = "";
//			for (vector<string>::iterator iter = ips_tmp.begin(); iter != ips_tmp.end(); ++iter)
			for(String iter : ips_tmp)
			{
//				if (find(telnetReader->telnetIPList_Aft.begin(), telnetReader->telnetIPList_Aft.end(), *iter) != telnetReader->telnetIPList_Aft.end())
//				{
//					ip_tmp = *iter;
//					break;
//				}
			}
			if (ip_tmp != "") //该IP配置了telnet读aft表
			{
				//用telnet读该IP的aft表
//	                        map<string, list<string> > portmac_tmp = telnetReader->getAftData(ip_tmp);
//				if(!portmac_tmp.empty())
//				{
//					if (aftlist_cur.empty())
//					{
//						aftlist_cur.insert(make_pair(spr.ip, portmac_tmp));
//					}
//					else
//					{
//						//合并端口集
//	                                        for (map<string, list<string> >::iterator port_mac = portmac_tmp.begin(); port_mac != portmac_tmp.end(); ++port_mac)
//						{//port-macs
//							if (aftlist_cur[spr.ip].find(port_mac->first) != aftlist_cur[spr.ip].end())
//							{//存在该端口
//								//bexits = true;
//								for (list<string>::iterator idestmac = port_mac->second.begin(); idestmac != port_mac->second.end(); ++idestmac)
//								{
//									if (find(aftlist_cur[spr.ip][port_mac->first].begin(), aftlist_cur[spr.ip][port_mac->first].end(), *idestmac) == aftlist_cur[spr.ip][port_mac->first].end())
//									{//不存在该mac
//										aftlist_cur[spr.ip][port_mac->first].push_back(*idestmac);
//									}
//								}
//							}
//							else
//							{
//								aftlist_cur[spr.ip].insert(make_pair(port_mac->first, port_mac->second));
//							}
//						}
//					}
//				}
			}
			//remarked by zhangyan 2009-01-16
			//if(auxPara.arp_read_type == "1")
			//{
			//	bArp = true;
			//	arplist_cur = device->getArpData(snmp, spr);
			//	if(arplist_cur.empty())
			//	{
			//		//added by zhangyan 2009-01-13
			//		ip_tmp = "";
			//		for (vector<string>::iterator iter = ips_tmp.begin(); iter != ips_tmp.end(); ++iter)
			//		{
			//			if (find(telnetReader->telnetIPList_Arp.begin(), telnetReader->telnetIPList_Arp.end(), *iter) != telnetReader->telnetIPList_Arp.end())
			//			{
			//				ip_tmp = *iter;
			//				break;
			//			}
			//		}
			//		if (ip_tmp != "") //该IP配置了telnet读arp表
			//		{
			//			//用telnet读该IP的arp表
	                //			map<string, list<pair<string,string> > > portipmac_tmp = telnetReader->getArpData(ip_tmp);
			//			if(!portipmac_tmp.empty())
			//			{
			//				arplist_cur.insert(make_pair(spr.ip, portipmac_tmp));
			//			}
			//		}
			//	}
			//}

			//add by jiangshanwen 20100825
			if(auxParam.getSeed_type().equals("1"))//.seed_type == "1")
			{
				bArp = true;
				arplist_cur = device.getArpData(snmp, spr, oidIndexList);
				if(arplist_cur.isEmpty())
				{
					ip_tmp = "";
//					for (vector<string>::iterator iter = ips_tmp.begin(); iter != ips_tmp.end(); ++iter)
					for(String iter : ips_tmp)
					{
//						if (find(telnetReader->telnetIPList_Arp.begin(), telnetReader->telnetIPList_Arp.end(), *iter) != telnetReader->telnetIPList_Arp.end())
//						{
//							ip_tmp = *iter;
//							break;
//						}
					}
					if (ip_tmp != "") //该IP配置了telnet读arp表
					{
						//用telnet读该IP的arp表
//	                                        map<string, list<pair<string,string> > > portipmac_tmp = telnetReader->getArpData(ip_tmp);
//						if(!portipmac_tmp.empty())
//						{
//							arplist_cur.insert(make_pair(spr.ip, portipmac_tmp));
//						}
					}
				}
			}
		}
		else if(devType.equals(CommonDef.ROUTER))// == ROUTER)
		{//r
			bArp = true;
			bRoute = true;		
			arplist_cur = device.getArpData(snmp, spr, oidIndexList);
			rttbl_cur = device.getRouteData(snmp, spr, oidIndexList);		//changed again by wings 2009-11-13 恢复路由表
			if(auxParam.getNbr_read_type().equals("1"))//.nbr_read_type == "1")
			{
				bNbr = true;
				nbrlist_cur = device.getOspfNbrData(snmp, spr);
			}
			if(auxParam.getBgp_read_type().equals("1"))//.bgp_read_type == "1")
			{
				bBgp = true;
				bgplist_cur = device.getBgpData(snmp, spr);
			}
			if(auxParam.getVrrp_read_type().equals("1"))//.vrrp_read_type == "1")
			{
				bVrrp = true;
				vrrplist_cur = device.getVrrpData(snmp, spr);
			}
			if(arplist_cur.isEmpty())
			{
				Vector<String> ips_tmp = devid_list_valid.get(spr.getIp()).getIps();//[spr.ip].ips;
				String ip_tmp = "";
//				for (vector<string>::iterator iter = ips_tmp.begin(); iter != ips_tmp.end(); ++iter)
				for(String iter : ips_tmp)
				{
//					if (find(telnetReader->telnetIPList_Arp.begin(), telnetReader->telnetIPList_Arp.end(), *iter) != telnetReader->telnetIPList_Arp.end())
//					{
//						ip_tmp = *iter;
//						break;
//					}
				}
				if (ip_tmp != "") //该IP配置了telnet读arp表
				{
					//用telnet读该IP的arp表
//	                                map<string, list<pair<string,string> > > portipmac_tmp = telnetReader->getArpData(ip_tmp);
//					if(!portipmac_tmp.empty())
//					{
//						arplist_cur.insert(make_pair(spr.ip, portipmac_tmp));
//					}
				}
			}
		}
		else if (devType.equals(CommonDef.SERVER)&& auxParam.getSeed_type().equals("1"))//.seed_type == "1")
		{
			bArp = true;
			bRoute = true;		
			arplist_cur = device.getArpData(snmp, spr, oidIndexList);
			rttbl_cur = device.getRouteData(snmp, spr, oidIndexList);		//changed again by wings 2009-11-13 恢复路由表
			if(arplist_cur.isEmpty())
			{
				Vector<String> ips_tmp = devid_list_valid.get(spr.getIp()).getIps();//[spr.ip].ips;
				String ip_tmp = "";
//				for (vector<string>::iterator iter = ips_tmp.begin(); iter != ips_tmp.end(); ++iter)
				for(String iter : ips_tmp)
				{
//					if (find(telnetReader->telnetIPList_Arp.begin(), telnetReader->telnetIPList_Arp.end(), *iter) != telnetReader->telnetIPList_Arp.end())
//					{
//						ip_tmp = *iter;
//						break;
//					}
				}
				if (ip_tmp != "") //该IP配置了telnet读arp表
				{
					//用telnet读该IP的arp表
//	                                map<string, list<pair<string,string> > > portipmac_tmp = telnetReader->getArpData(ip_tmp);
//					if(!portipmac_tmp.empty())
//					{
//						arplist_cur.insert(make_pair(spr.ip, portipmac_tmp));
//					}
				}
			}
		}

		if(aftlist_cur.isEmpty())
		{
			if(bAft)
			{
//	                        SvLog::writeLog("Can not get aft of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_AFT_LOG);
			}
		}
		else
		{
//	                mutex::scoped_lock lock(m_data_mutex);
			synchronized (lock) {
				aftlist_cur.get(null);
				Utils.mapAddAll(aft_list, aftlist_cur);
//				aft_list.insert(aftlist_cur.begin(), aftlist_cur.end());
			}
			
		}
		if(arplist_cur.isEmpty())
		{
			if(bArp)
			{
//	                        SvLog::writeLog("Can not get arp of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_ARP_LOG);
			}
		}
		else
		{
//	                mutex::scoped_lock lock(m_data_mutex);
			synchronized (lock) {
				Utils.mapAddAll(arp_list, arplist_cur);
			}
//			arp_list.insert(arplist_cur.begin(), arplist_cur.end());
		}

		if(inflist_cur.isEmpty())
		{
			if(bInf)
			{
//	                        SvLog::writeLog("Can not get inf of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_INF_LOG);
			}
		}
		else
		{
//	                mutex::scoped_lock lock(m_data_mutex);
			synchronized (lock) {
				Utils.mapAddAll(ifprop_list, inflist_cur);
			}
//			ifprop_list.insert(inflist_cur.begin(), inflist_cur.end());
		}

		if(nbrlist_cur.isEmpty())
		{
			if(bNbr)
			{
//	                        SvLog::writeLog("Can not get BNR of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_NBR_LOG);
			}
		}
		else
		{
//	                mutex::scoped_lock lock(m_data_mutex);
			synchronized (lock) {
				Utils.mapAddAll(ospfnbr_list, nbrlist_cur);
			}
//			ospfnbr_list.insert(nbrlist_cur.begin(), nbrlist_cur.end());
		}

		if(rttbl_cur.isEmpty())
		{
			if(bRoute)
			{
//	                        SvLog::writeLog("Can not get Route of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_RT_LOG);
			}
		}
		else
		{
//	                mutex::scoped_lock lock(m_data_mutex);
//			route_list.insert(rttbl_cur.begin(), rttbl_cur.end());
			synchronized (lock) {
				Utils.mapAddAll(route_list, rttbl_cur);
			}
		}

		if(bgplist_cur.isEmpty())
		{
			if(bBgp)
			{
//	                        SvLog::writeLog("Can not get BGP of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_BGP_LOG);
			}
		}
		else
		{
//	                mutex::scoped_lock lock(m_data_mutex);
//			bgp_list.insert(bgp_list.end(), bgplist_cur.begin(), bgplist_cur.end());
			synchronized (lock) {
				
			}
		}

		if(vrrplist_cur.isEmpty())
		{
			if(bVrrp)
			{
//	                        SvLog::writeLog("Can not get VRRP of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_VRRP_LOG);
			}
		}
		else
		{
//	                mutex::scoped_lock lock(m_data_mutex);
//			routeStandby_list.insert(vrrplist_cur.begin(), vrrplist_cur.end());
			synchronized (lock) {
				Utils.mapAddAll(routeStandby_list, vrrplist_cur);
			}
		}

		if(drctdata_cur.isEmpty())
		{
			if(bDirect)
			{
//	                        SvLog::writeLog("Can not get DIRECT of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_DRC_LOG);
			}
		}
		else
		{
//	                mutex::scoped_lock lock(m_data_mutex);
//			directdata_list.insert(drctdata_cur.begin(), drctdata_cur.end());
			synchronized (lock) {
				Utils.mapAddAll(directdata_list, drctdata_cur);
			}
			
		}
		if (stplist_cur.isEmpty())
		{
		}
		else
		{
//	                mutex::scoped_lock lock(m_data_mutex);
//			stp_list.insert(stplist_cur.begin(),stplist_cur.end());
			synchronized (lock) {
				Utils.mapAddAll(stp_list, stplist_cur);
			}
		}

//		Sleep(500);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//	        SvLog::writeLog("End read the data of " + spr.ip);
	}
	public void getIpMaskList(SnmpPara spr, List<Pair<String, String>> ipcm_result)
	{
//		ipcm_result.clear();
		MibScan snmp = new MibScan();
		//IP-MASK地址1.3.6.1.2.1.4.20.1.3
	    List<Pair<String,String> > ipmsks = snmp.getMibTable(spr,"1.3.6.1.2.1.4.20.1.3");
	    
		if(!ipmsks.isEmpty())
		{
//	                for(list<pair<string,string> >::iterator i = ipmsks.begin();	i != ipmsks.end();	++i)
			for(Pair<String,String> i : ipmsks)
			{
				String ip_cur = i.getFirst().substring(21);//->first.substr(21);
				if(ip_cur != "" && ip_cur != "0.0.0.0" //排除任意匹配地址
					&& !ip_cur.substring(0,3).equals("127")//.compare(0,3,"127") != 0  //排除环回地址
					//add by wings 2009-11-13
					&& !ip_cur.substring(0,5).equals("0.255")//.compare(0,5,"0.255") != 0
//					&& (ip_cur.compare(0,3,"224") < 0 || ip_cur.compare(0,3,"239") > 0) //排除组播地址
					)
				{
					//added by zhangyan 2009-01-15
					if(i.getSecond().isEmpty())//->second.empty())
					{
						continue;
					}
//					pair<string,string> ipm_tmp = make_pair(ip_cur, i->second);
					Pair<String,String> ipm_tmp = new Pair<String,String>(ip_cur , i.getSecond());
//					if(find(ipcm_result.begin(), ipcm_result.end(), ipm_tmp) == ipcm_result.end())
					if(!ipcm_result.contains(ipm_tmp))
					{
						ipcm_result.add(ipm_tmp);//.push_back(ipm_tmp);
					}
				}
			}
		}
	}
	public void getOidIndex(Map<String, String> oidIndexList, String sysOid)
	{
		if(sysOid.split("\\.").length < 7) return;//ScanUtils.tokenize(sysOid, ".", true,"").size() < 7) return;
		if(special_oid_list.containsKey(sysOid))
		{
			oidIndexList = special_oid_list.get(sysOid);
		}
		else
		{
			getOidIndex(oidIndexList, sysOid.substring(0, sysOid.lastIndexOf(".")));//.find_last_of(".")));
		}
	}
	public void getOneSysInfo(SnmpPara spr)
	{
		if(testIP(spr))
		{
			IDBody devid = getOneSysInfo_NJ(spr);
			if(devid.getSnmpflag().equals("1"))//add by wings 09-11-05
				addDevID(spr, devid);
		}
	}
	public void addDevID(SnmpPara spr,IDBody devid)
	{
		if(Utils.isEmptyOrBlank(devid.getSysOid()))
		{
			return;
		}
		//begin added by tgf 2008-09-23
		if(!devid.getIps().contains(spr.getIp()))//find(devid.ips.begin(), devid.ips.end(), spr.ip) == devid.ips.end())
		{//忽略vrrp的虚拟设备
			return;
		}
		//end added by tgf 2008-09-23

//	        mutex::scoped_lock lock(m_data_mutex);
		Set<String> set = CommonDef.DEVID_LIST.keySet();
		for(String key : set)//DEVID_LIST::iterator i = devid_list_visited.begin(); i != devid_list_visited.end();	++i)
		{
			IDBody body = CommonDef.DEVID_LIST.get(key);
			if(devid.getIps().containsAll(body.getIps()) && body.getIps().containsAll(devid.getIps()))
			{
				return;
			}
		}
		Pair<String, IDBody> devid_cur = new Pair<String,IDBody>(spr.getIp(), devid);
		devid_list_valid.put(spr.getIp(),devid);
		devid_list_visited.put(spr.getIp(),devid);
		ip_visited_list.add(devid.getIps().get(0));
		//remarked by zhangyan 2008-12-29
		//修改目的：将非网络设备不添加到sproid_list列表中
		//sproid_list.push_back(make_pair(spr, make_pair(devid.devType, devid.sysOid)));
		//修将非网络设备不添加到sproid_list列表中
		if (devid.getDevType().equals("0") || devid.getDevType().equals("1") || devid.getDevType().equals("2") || devid.getDevType().equals("3"))
		{
//	            qDebug() << " devType : " << devid.devType.c_str();
			sproid_list.add(new Pair<SnmpPara,Pair<String,String>>(spr, new Pair<String,String>(devid.getDevType(), devid.getSysOid())));
		}
		//记录日志
//		String msg = spr.getIp() + ", " + map_devType[devid.devType]+ ", " + devid.devFactory;
	        //SvLog::writeLog(msg, FOUND_DEVICE_MSG, m_callback);
//	        SvLog::writeLog(msg);
	}
	boolean testIP(SnmpPara spr)
	{
		if(!ip_visited_list.contains(spr.getIp()))
			return false;
		return true;
	}
    CountDownLatch latchgetSysinfo = null;
	public boolean getSysInfos(Vector<SnmpPara> spr_list)
	{
		devid_list_valid.clear();
		sproid_list.clear();
		if(spr_list.size() > 0)
		{
			if(spr_list.size() == 1)
			{
				SnmpPara para = spr_list.get(0);
				if(isNewIp(para.getIp()))
				{
					getOneSysInfo(spr_list.get(0));
				}
			}
			else
			{
				latchgetSysinfo = new CountDownLatch(spr_list.size());
				//pool tp(scanPara.thrdamount);
	            // pool tp(min(scanPara.thrdamount, spr_list.size()));//by zhangyan 2008-12-29
				for(final SnmpPara sp : spr_list){
					if(isNewIp(sp.getIp())){
						if(isStop){
							return false;
						}else{
							ThreadTaskPool.getInstance().excute(new Runnable() {
								
								@Override
								public void run() {
									getOneSysInfo(sp);
									latchgetSysinfo.countDown();
								}
							});
						}
					}
				}
				try {
					latchgetSysinfo.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	        return true;
	}

	public boolean isNewIp(String ip) {
		synchronized (this) {
			Set<String> keys = devid_list_visited.keySet();
			for(String key : keys){
				IDBody body = devid_list_visited.get(key);
				if(body.getIps().contains(ip)){
					return false;
				}
			}
			return true;
		}

	}
	/**完成*/
	public void getOneArpData(SnmpPara spr, Map<String, List<Pair<String,String>>> inf_ipmacs){
		IPNetToMediaTableScan arpTabScan = new IPNetToMediaTableScan();
		MibScan snmp = new MibScan();
		List<Pair<String,String> > ipInfInx = snmp.getMibTable(spr, "1.3.6.1.2.1.4.22.1.1");
		if(!ipInfInx.isEmpty()){
			 List<Pair<String,String>> i_pm = new ArrayList<Pair<String,String>>();
			 //IP-MAC地址1.3.6.1.2.1.4.22.1.2
			 List<Pair<String, String>> ipMac = snmp.getMibTable(spr, "1.3.6.1.2.1.4.22.1.2");
			 if(!ipMac.isEmpty()){
				 for(Pair<String, String> imac : ipMac){
					 Vector<String> v1 = ScanUtils.tokenize(imac.getFirst().substring(21), ".", true,"");
					 int len = v1.size();
					 if(len<4){
						 continue;
					 }
					 String ip_tmp  = v1.get(len-4) + "." + v1.get(len-3) + "." + v1.get(len-2) + "." + v1.get(len-1);
					 String mac_tmp = imac.getSecond().replaceAll(":","").substring(0,12);
					 for(Pair<String, String> iInf: ipInfInx){
						 int iPlace = iInf.getFirst().indexOf(ip_tmp);
						 if(iPlace > -1){
							 
							 if(inf_ipmacs.containsKey(iInf.getSecond())){
								 i_pm = inf_ipmacs.get(iInf.getSecond());
								 boolean bExisted = false;
								 for(Pair<String, String> j : i_pm){
									 if(j.getSecond().equals(mac_tmp)){
										 bExisted = true;
										 break;
									 }
								 }
								 if(!bExisted){
									 i_pm.add(new Pair<String, String>(ip_tmp, mac_tmp));
								 }
							 }
						 }else{//新的接口
							 List<Pair<String, String>> ipmac_list = new ArrayList<Pair<String, String>>();
							 ipmac_list.add(new Pair<String, String>(ip_tmp, mac_tmp));
							 inf_ipmacs.put(iInf.getSecond(), ipmac_list);
						 }
						 break;
					 }
				 }
			 }
		}
	}
	public IDBody getOneSysInfo_NJ(SnmpPara spr)
	{
//	        SvLog::writeLog("Start get ID of " + spr.ip + " " + spr.community);
		MibScan snmp = new MibScan();
		IDBody devid = new IDBody();
	        //list<pair<string,string> > sysInfos = snmp.GetMibTable(spr, "1.3.6.1.2.1.1");
	        List<Pair<String,String>> sysInfos = new ArrayList<Pair<String,String>>();
//	        qDebug() << "snmpversion : " << spr.snmpver.c_str();
		if (spr.getSnmpver().equals("2"))
		{
			sysInfos = snmp.getMibTable(SnmpConstants.version2c, spr, "1.3.6.1.2.1.1");

			if(sysInfos.isEmpty())
			{
				sysInfos = snmp.getMibTable(SnmpConstants.version1, spr, "1.3.6.1.2.1.1");
				if (!sysInfos.isEmpty())
				{
					spr.setSnmpver("1");
				}
			}
		}
		else if (spr.getSnmpver().equals("1"))
		{
			sysInfos = snmp.getMibTable(SnmpConstants.version1, spr, "1.3.6.1.2.1.1");

			if(sysInfos.isEmpty())
			{
				sysInfos = snmp.getMibTable(SnmpConstants.version2c, spr, "1.3.6.1.2.1.1");
				if (!sysInfos.isEmpty())
				{
					spr.setSnmpver("2");
				}
			}
		}
		else// if (spr.snmpver == "0")
		{
			sysInfos = snmp.getMibTable(spr, "1.3.6.1.2.1.1");
		}
		String sysOid = "";//snmp.GetMibObject("1.3.6.1.2.1.1.2");
		String sysSvcs = "";//snmp.GetMibObject("1.3.6.1.2.1.1.7");
		String sysName = "";//snmp.GetMibObject("1.3.6.1.2.1.1.5");
		if(sysInfos.isEmpty())
		{
			//修改oid重取一次 
			if (spr.getSnmpver().equals("2"))
			{
				sysOid = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.2.0");
				if(!sysOid.isEmpty())
				{
					sysSvcs = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.7.0");
					sysName = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.5");
				}
				else
				{
					sysOid = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.2.0");
					if(!sysOid.isEmpty())
					{
						sysSvcs = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.7.0");
						sysName = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.5.0");
						spr.setSnmpver("1");
					}
				}
			}
			else if (spr.getSnmpver().equals("1"))
			{
				sysOid = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.2.0");
				if(!sysOid.isEmpty())
				{
					sysSvcs = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.7.0");
					sysName = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.5.0");
				}
				else
				{
					sysOid = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.2.0");
					if(!sysOid.isEmpty())
					{
						sysSvcs = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.7.0");
						sysName = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.5.0");
						spr.setSnmpver("2");
					}
				}
			}
			else
			{
				sysOid = snmp.getMibObject(spr,"1.3.6.1.2.1.1.2.0");
				if(!sysOid.isEmpty())
				{
					sysSvcs = snmp.getMibObject(spr,"1.3.6.1.2.1.1.7.0");
					sysName = snmp.getMibObject(spr,"1.3.6.1.2.1.1.5.0");
				}
			}

			if(sysOid.isEmpty() && sysSvcs.isEmpty() && sysName.isEmpty())
			{
//	                        SvLog::writeLog("Can not get the ID of " + spr.ip);
				return devid;
			}
//	                SvLog::writeLog("newMethod**ip:"+spr.ip+" sysOid:"+sysOid+" sysSvcs:"+sysSvcs+" sysName:"+sysName);
		}
		else
		{
//	                for(List<pair<string,string> >::iterator i = sysInfos.begin();
//				i != sysInfos.end();
//				++i)
			for(Pair<String, String> i : sysInfos)
			{
//	                        SvLog::writeLog("sysInfo**ip:"+spr.ip+" sysInfo->first:"+i->first);
//				cout<<"i->first : "<<i->first.c_str()<<endl;
				if(i.getFirst().equals("1.3.6.1.2.1.1.2.0")
					//add by wings 2009-11-13
					||i.getFirst().equals("1.3.6.1.2.1.1.2"))
	                        {
					sysOid = i.getSecond();
	                                //qDebug() << sysOid.c_str();
				}
				else if(i.getFirst().equals("1.3.6.1.2.1.1.5.0")
					//add by wings 2009-11-13
					||i.getFirst().equals("1.3.6.1.2.1.1.5"))
				{ 
					sysName = i.getSecond();
	                                //qDebug() << sysName.c_str();
				}
				else if(i.getFirst().equals("1.3.6.1.2.1.1.7.0")
					//add by wings 2009-11-13
					||i.getFirst().equals("1.3.6.1.2.1.1.7"))
				{
					sysSvcs = i.getSecond();
				}
			}
//	                SvLog::writeLog("OldMethod**ip:"+spr.ip+" sysOid:"+sysOid+" sysSvcs:"+sysSvcs+" sysName:"+sysName);
		}
//	        SvLog::writeLog("get sysOid of " + spr.ip + ": " + sysOid);
//		cout<<"sysOid : "<<sysOid.c_str()<<endl;

		if(sysOid.equals("1.3.6.1.4.1.13742.1.1.1"))
		{//KVM 流量分频器  1.3.6.1.4.1.13742.1.1.1
			devid.setSysOid("1.3.6.1.4.1.13742.1.1.1");
//			devid.ips.push_back(spr.ip);
			devid.setDevType("6");//Other device
			devid.setDevTypeName("KVM");
		}
		else
		{		
			if(Utils.isEmptyOrBlank(sysOid)&&Utils.isEmptyOrBlank(sysSvcs)&&Utils.isEmptyOrBlank(sysName)) return devid;
			if (Utils.isEmptyOrBlank(sysOid))
			{
//				cout<<"sysOid is empty! "<<endl;
				sysOid = "00";
			}
			//IP-MASK地址1.3.6.1.2.1.4.20.1.3
	        List<Pair<String,String> > ipmsks = snmp.getMibTable(spr, "1.3.6.1.2.1.4.20.1.3");//[ipDes, mask]
			//IP-InfInx 1.3.6.1.2.1.4.20.1.2
	        List<Pair<String,String> > infinxs = snmp.getMibTable(spr, "1.3.6.1.2.1.4.20.1.2");//[ipDes, index]

			Map<String,String> map_type = new HashMap<String,String>();
			getDevTypeByOid(sysOid, sysSvcs, spr.getIp(), map_type);//填充map_type
			
//	                SvLog::writeLog("ip:"+spr.ip+"**map_type['devtype']:"+map_type["devtype"]); //add by jiang 20100602

			devid.setSysOid(sysOid);// = sysOid;
			devid.setSnmpflag("1"); //snmp is enabled
			devid.setCommunity_get(spr.getCommunity());
			devid.setDevType(map_type.get("devtype"));
			devid.setDevModel(map_type.get("model"));
			devid.setDevFactory(map_type.get("factory"));
			devid.setDevTypeName(map_type.get("typename"));
			//	devid.baseMac = baseMac;
			devid.setSysSvcs(sysSvcs);
	                //devid.sysName = Utf8ToString(sysName);	//update by jiang 20100602 编码转换
	       devid.setSysName(sysName);	

			/*update by jiang 20100602 如果没有读取到ip表，设备会被过滤掉
			//add by wings 09-11-05
			if(ipmsks.size() ==0)
			{
				return devid;
			}
			if((ipmsks.size() ==1)&&(ipmsks.begin()->second.compare(0,1,"0") == 0))
			{
				return devid;
			}
			*/
			//devid.snmpflag = "1"; update by jiang 20100602 前面已经赋值，重复，注释掉
	       if(!(ipmsks.size() == 0) && !(ipmsks.size() == 1) && (ipmsks.get(0).getSecond().substring(0,1).equals("0")) ){
	    	   
//	       }
//			if(!(ipmsks.size() == 0) && !((ipmsks.size() ==1)&&(ipmsks.begin()->second.compare(0,1,"0") == 0))) //update by jiang 20100602
//			{
//	            for(list<pair<string,string> >::iterator i = ipmsks.begin(); i != ipmsks.end(); ++i)
//				{
	    	   for(Pair<String, String> ipmask : ipmsks) {
//					cout<<"i->first : "<<i->first.c_str()<<" devid.sysOid:"<<devid.sysOid<<" devid.sysName:"<<devid.sysName<<" spr.ip:"<<spr.ip<<endl;
					//if (i->first.length() < 22)
	    		   if(ipmask.getFirst().length()<22)
					{
						continue;
					}
					//std::string ip_tmp = i->first.substr(21);
	    		   String ip_tmp = ipmask.getFirst().substring(21);
//					cout<<"ip_tmp:"<<ip_tmp<<endl;
					//			if(ip_tmp != "" && ip_tmp != "0.0.0.0"    //remarked by zhangyan 2008-10-15
	    		   if(!(ip_tmp.substring(0,6).equals("0.0.0."))
	    				   && !(ip_tmp.substring(0,3).equals("127"))
	    				   && !(ip_tmp.substring(0,5).equals("0.255")))
//					if(ip_tmp.compare(0,6,"0.0.0.") != 0
//						&& ip_tmp.compare(0,3,"127") != 0 //排除环回地址
//						//add by wings 2009-11-13
//						&& ip_tmp.compare(0,5,"0.255") != 0
//						//				&& (ip_tmp.compare(0,3,"224") < 0 || ip_tmp.compare(0,3,"239") > 0) //排除组播地址  //remarked by zhangyan 2008-10-15
//						)
					{
	    			   if(devid.getIps().contains(ip_tmp)){
	    				   for(Pair<String, String> p : infinxs){
	    					   if(p.getFirst().length() >21 && (p.getFirst().substring(21).equals(ip_tmp))){
	    						   devid.getInfinxs().add(p.getSecond());
	    						   devid.getIps().add(ip_tmp);
	    						   devid.getMsks().add(ipmask.getSecond());
	    					   }
	    				   }
	    			   }
//						if(find(devid.ips.begin(), devid.ips.end(), ip_tmp) == devid.ips.end())
//						{
//	                                                for(list<pair<string,string> >::iterator j = infinxs.begin();
//								j != infinxs.end();
//								++j)
//							{
//								if(j->first.length() > 21 && j->first.substr(21) == ip_tmp)
//								{
//									cout<<"ip_tmp : "<<ip_tmp.c_str()<<endl;
//									devid.infinxs.push_back(j->second);
//									devid.ips.push_back(ip_tmp);
//									devid.msks.push_back(i->second);
//									break;
//								}
//							}
//						}
					}
				}
			}
			
			// remarked by zhangyan 2008-10-23
			////IP-MAC地址1.3.6.1.2.1.4.22.1.2
	                //list<pair<string,string> > IpMacs = snmp.GetMibTable(spr, "1.3.6.1.2.1.4.22.1.2");
			//if(!IpMacs.empty())
			//{
	                //	for(list<pair<string,string> >::iterator imac = IpMacs.begin(); imac != IpMacs.end(); ++imac)
			//	{
			//		vector<string> v1 = tokenize(imac->first.substr(21), ".", true);
			//		size_t len = v1.size();
			//		if(len < 4)	{	continue;	}
			//		string ip_tmp  = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
			//		string mac_tmp = replaceAll(imac->second, " ","").substr(0,12);
			//		if(find(devid.ips.begin(), devid.ips.end(), ip_tmp) != devid.ips.end())
			//		{//(ip,mac) 中的 ip 地址 属于ip地址表
			//			std::transform(mac_tmp.begin(), mac_tmp.end(), mac_tmp.begin(), toupper);
			//			if(mac_tmp != "" && mac_tmp != "000000000000" && mac_tmp != "FFFFFFFFFFFF")
			//			{
			//				if(find(devid.macs.begin(), devid.macs.end(), mac_tmp) == devid.macs.end())
			//				{
			//					devid.macs.push_back(mac_tmp);
			//					devid.baseMac = mac_tmp;//added by tgf 2008-09-22
			//				}
			//			}
			//		}
			//	}
			//}

			// added by zhangyan 2008-10-23		
			// get base mac (ps:只对交换机有效)
			if (devid.getDevType().equals("0") || devid.getDevType().equals("1"))
			{
				String mac_tmp = snmp.getMibObject(spr, "1.3.6.1.2.1.17.1.1.0");		
				//noSuchObject, noSuchInstance, and endOfMibView
				if (!mac_tmp.isEmpty() && !mac_tmp.equals("Null") && !mac_tmp.equals("endOfMibView") && !mac_tmp.equals("noSuchObject") && !mac_tmp.equals("noSuchInstance"))
				{
					String baseMac = mac_tmp.replaceAll(":", "").substring(0,12).toUpperCase();
//							replaceAll(mac_tmp, " ","").substr(0,12);
//	                                std::transform(baseMac.begin(), baseMac.end(), baseMac.begin(), (int(*)(int))toupper);
					
					if (baseMac != "" && baseMac != "000000000000" && baseMac != "FFFFFFFFFFFF")
					{
						devid.getMacs().add(baseMac);//.macs.push_back(baseMac);
						devid.setBaseMac(baseMac);;//.baseMac = baseMac;
					}
				}
			}
		}
		// added by zhangyan 2008-11-04
		if (devid.getIps()==null || devid.getIps().size() == 0)//.isEmpty())
		{
			devid.getInfinxs().add("0");//.infinxs.push_back("0");
			devid.getIps().add(spr.getIp());//.ips.push_back(spr.ip);
			devid.getMsks().add("");//.msks.push_back("");
		}
//	        SvLog::writeLog("Success read the ID of " + spr.ip);
		return devid;
	}

	// 获取设备类型
	public void getDevTypeByOid(String sysOid,String sysSvcs,String ip,  Map<String,String> map_res)
	{
		String devtype_res = "5";//default to host
		String factory_res = "";
		String model_res = "";
		String typeName_res = "";

		//if(dev_type_list.find(sysOid) != dev_type_list.end())//DEVICE_TYPE_MAP {sysoid,<devType,devTypeName,devFac,devModel>}
		if(OIDTypeUtils.getInstance().containsKey(sysOid))//dev_type_list.containsKey(sysOid))
		{
			devtype_res  =  OIDTypeUtils.getInstance().getDevicePro(sysOid).getDevType();//dev_type_list[sysOid].devType;
			factory_res  =  OIDTypeUtils.getInstance().getDevicePro(sysOid).getDevFac();//dev_type_list[sysOid].devFac;
			model_res    =  OIDTypeUtils.getInstance().getDevicePro(sysOid).getDevModel();//dev_type_list[sysOid].devModel;
			typeName_res =  OIDTypeUtils.getInstance().getDevicePro(sysOid).getDevTypeName();//dev_type_list[sysOid].devTypeName;
		}
		else
		{
			if (sysOid.substring(0,16).equals("1.3.6.1.4.1.311."))//enterprises节点:1.3.6.1.4.1
			{
				devtype_res = "5";//host
			}
			else if(sysOid.substring(0,17).equals("1.3.6.1.4.1.8072."))
			{
				devtype_res = "4";// "SERVER"
			}
			//else if(sysOid.substr(0,17) == "1.3.6.1.4.1.9952.") //TOPSEC  added by zhangyan 2008-11-04
			//{
			//	devtype_res = "3";// "FIREWALL"
			//}
			else if(sysOid.substring(0,21).equals("1.3.6.1.4.1.6486.800."))
			{
				devtype_res = "0";//ROUTE-SWITCH
			}
			else if(sysOid.equals("1.3.6.1.4.1.13742.1.1.1"))
			{//KVM 流量分频器  1.3.6.1.4.1.13742.1.1.1
				devtype_res = "6"; //Other device
			}
			else
			{//
//	                        SvLog::writeErrorLog(string(ip) + ":" + sysOid, ERR_OID_LOG);
				int isvc = Integer.parseInt(sysSvcs);//str2int(sysSvcs);
				if (isvc == 0)
				{
					devtype_res = "5";//host
				}
				else if((isvc & 6) == 6)
				{
					devtype_res = "0"; //"ROUTE-SWITCH"
				}
				else if((isvc & 4) == 4)
				{
					devtype_res = "2"; //ROUTER
				}
				else if((isvc & 2) == 2)
				{
					devtype_res = "1"; //"SWITCH"
				}
				else
				{
					devtype_res = "5"; //host
//	                                SvLog::writeLog("Can't identify oid=" + sysOid + ", services=" + sysSvcs + " (" + ip + ")");
				} 
			}
		}

		if (devtype_res == "5")
		{
			devtype_res = "4"; //将开了SNMP的PC（或其它未被识别的设备）作为SERVER
		}
		map_res.put("devtype", devtype_res);//["devtype"] = devtype_res;
		map_res.put("factory", factory_res);//["factory"] = factory_res;
		map_res.put("model", model_res);//["model"] = model_res;
		map_res.put("typename", typeName_res);//["typename"] = typeName_res;
	}
	
}
