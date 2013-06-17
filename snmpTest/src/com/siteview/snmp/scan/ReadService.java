package com.siteview.snmp.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;

import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.constants.CommonDef;
import com.siteview.snmp.constants.OIDConstants;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Bgp;
import com.siteview.snmp.pojo.DevicePro;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.pojo.IfRec;
import com.siteview.snmp.pojo.RouteItem;
import com.siteview.snmp.pojo.SystemGroup;
import com.siteview.snmp.util.ScanUtils;

public class ReadService {

	//设备基本信息列表{devIP,(TYPE,SNMP,[IP],[MAC],[MASK],SYSOID,sysname)}
	Map<String, IDBody> devid_list;
	//设备接口属性列表 {devIP,(ifAmount,[(ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)])}
	Map<String, Pair<String, List<IfRec>>> ifprop_list;
	//设备AFT数据列表 {sourceIP,{port,[MAC]}}
	Map<String, Map<String, List<String>>> aft_list;
	//设备ARP数据列表 {sourceIP,{infInx,[(MAC,destIP)]}}
	Map<String, Map<String, List<Pair<String, String>>>> arp_list;
	//设备OSPF邻居列表 {sourceIP,{infInx,[destIP]}}
	Map<String, Map<String, List<String>>> ospfnbr_list;
	List<Bgp> bgp_list = new ArrayList<Bgp>();
	public Map<String,String> map_devType = new HashMap<String, String>();
	//设备路由表 {sourceIP,{infInx,[nextIP]}}
	Map<String, Map<String, List<RouteItem>>> ROUTE_LIST;
	List<Pair<SnmpPara,Pair<String,String>>> sproid_list = new ArrayList<Pair<SnmpPara,Pair<String,String>>>();
	private Map<String, IDBody> devid_list_visited = new HashMap<String, IDBody>();
	Map<String, DevicePro> dev_type_list = new HashMap<String,DevicePro>();
	public List<String> ip_visited_list = new ArrayList<String>();
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
	Map<String, IDBody> devid_list_valid = new HashMap<String,IDBody>();
	//获取所有设备的普通数据
	public boolean getDeviceData(Vector<SnmpPara> spr_list)
	{
	        if (!getSysInfos(spr_list))
	        {
	            return false;
	        }
		
//		aft_list.clear();
//		arp_list.clear();
//		ifprop_list.clear();
//		ospfnbr_list.clear();
//		//route_list.clear(); //changed by zhang 2009-03-26 去掉路由表的取数
//		bgp_list.clear();
//
//		if(sproid_list.size() > 0)
//		{
////	                qDebug() << "sproid size : " << sproid_list.size();
//			if(sproid_list.size() == 1)
//			{
//				getOneDeviceData(sproid_list.begin()->first, sproid_list.begin()->second.first, sproid_list.begin()->second.second);
//			}
//			else
//			{
//				//pool tp(scanPara.thrdamount);//(min(thrdAmount,ip_communitys.size()));
//	                        pool tp(min(scan.thrdamount, sproid_list.size()));//by zhangyan 2008-12-29
//	                        for (list<pair<SnmpPara, pair<String,String> > >::const_iterator i = sproid_list.begin(); i != sproid_list.end(); ++i)
//				{
//	                                if (isStop)
//	                                {
//	                                    return false;
//	                                }
//	                                else
//	                                {
//	                                    tp.schedule((boost::bind(&ReadService::getOneDeviceData,this, (*i).first, (*i).second.first, (*i).second.second)));
//	                                }
//				}
//	                        tp.wait();
//			}
//		}
	        return true;
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
		if(devid.getSysOid().isEmpty())
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
//		Map<String, IDBody> devid_cur = new Pair<String,IDBody>(spr.getIp(), devid);
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
		if(ip_visited_list.contains(spr.getIp()))
			return false;
		return true;
	}

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
					getOneSysInfo(para.getIp());
				}
			}
			else
			{
				//pool tp(scanPara.thrdamount);
	                        pool tp(min(scanPara.thrdamount, spr_list.size()));//by zhangyan 2008-12-29
				for (vector<SnmpPara>::const_iterator i = spr_list.begin(); i != spr_list.end(); ++i) 
				{
					if(isNewIp(i->ip))
					{
	                                        if (isStop)
	                                        {
	                                            return false;
	                                        }
	                                        else
	                                        {
	                                            tp.schedule((boost::bind(&ReadService::getOneSysInfo, this, *i)));
	                                        }

					}
				}
	                        tp.wait();
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
					 String mac_tmp = imac.getSecond().replaceAll(" ","").substring(0,12);
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
				sysOid = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.2");
				if(!sysOid.isEmpty())
				{
					sysSvcs = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.7");
					sysName = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.5");
				}
				else
				{
					sysOid = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.2");
					if(!sysOid.isEmpty())
					{
						sysSvcs = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.7");
						sysName = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.5");
						spr.setSnmpver("1");
					}
				}
			}
			else if (spr.getSnmpver().equals("1"))
			{
				sysOid = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.2");
				if(!sysOid.isEmpty())
				{
					sysSvcs = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.7");
					sysName = snmp.getMibObject(SnmpConstants.version1,spr,"1.3.6.1.2.1.1.5");
				}
				else
				{
					sysOid = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.2");
					if(!sysOid.isEmpty())
					{
						sysSvcs = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.7");
						sysName = snmp.getMibObject(SnmpConstants.version2c,spr,"1.3.6.1.2.1.1.5");
						spr.setSnmpver("2");
					}
				}
			}
			else
			{
				sysOid = snmp.getMibObject(spr,"1.3.6.1.2.1.1.2");
				if(!sysOid.isEmpty())
				{
					sysSvcs = snmp.getMibObject(spr,"1.3.6.1.2.1.1.7");
					sysName = snmp.getMibObject(spr,"1.3.6.1.2.1.1.5");
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

		if(sysOid == "1.3.6.1.4.1.13742.1.1.1")
		{//KVM 流量分频器  1.3.6.1.4.1.13742.1.1.1
			devid.setSysOid("1.3.6.1.4.1.13742.1.1.1");
//			devid.ips.push_back(spr.ip);
			devid.setDevType("6");//Other device
			devid.setDevTypeName("KVM");
		}
		else
		{		
			if("".equals(sysOid)&&"".equals(sysSvcs)&&"".equals(sysName)) return devid;
			if (sysOid == "")
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
				String mac_tmp = snmp.getMibObject(spr, "1.3.6.1.2.1.17.1.1");			
				if (!mac_tmp.isEmpty())
				{
					String baseMac = mac_tmp.replaceAll(" ", "").substring(0,12);
					
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
		if (devid.getIps().isEmpty())//.ips.empty())
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
		if(dev_type_list.containsKey(sysOid))
		{
			devtype_res = CommonDef.DEVICE_TYPE_MAP.get(sysOid).getDevType();//dev_type_list[sysOid].devType;
			factory_res =  CommonDef.DEVICE_TYPE_MAP.get(sysOid).getDevFac();//dev_type_list[sysOid].devFac;
			model_res   =  CommonDef.DEVICE_TYPE_MAP.get(sysOid).getDevModel();//dev_type_list[sysOid].devModel;
			typeName_res =  CommonDef.DEVICE_TYPE_MAP.get(sysOid).getDevTypeName();//dev_type_list[sysOid].devTypeName;
		}
		else
		{
			if (sysOid.substring(0,16) =="1.3.6.1.4.1.311.")//enterprises节点:1.3.6.1.4.1
			{
				devtype_res = "5";//host
			}
			else if(sysOid.substring(0,17) == "1.3.6.1.4.1.8072.")
			{
				devtype_res = "4";// "SERVER"
			}
			//else if(sysOid.substr(0,17) == "1.3.6.1.4.1.9952.") //TOPSEC  added by zhangyan 2008-11-04
			//{
			//	devtype_res = "3";// "FIREWALL"
			//}
			else if(sysOid.substring(0,21) == "1.3.6.1.4.1.6486.800.")
			{
				devtype_res = "0";//ROUTE-SWITCH
			}
			else if(sysOid == "1.3.6.1.4.1.13742.1.1.1")
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
