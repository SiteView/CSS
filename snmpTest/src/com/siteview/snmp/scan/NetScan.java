package com.siteview.snmp.scan;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.snmp4j.mp.SnmpConstants;

import jpcap.IPPacket;

import com.siteview.snmp.common.AuxParam;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.constants.CommonDef;
import com.siteview.snmp.model.In_Addr;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Bgp;
import com.siteview.snmp.pojo.DevicePro;
import com.siteview.snmp.pojo.Directitem;
import com.siteview.snmp.pojo.Edge;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.pojo.IfRec;
import com.siteview.snmp.pojo.RouteItem;
import com.siteview.snmp.pojo.RouterStandbyItem;
import com.siteview.snmp.util.ScanUtils;
import com.siteview.snmp.util.Utils;


public class NetScan implements Runnable{
	private Map<String, Map<String, List<String>>> aft_list = new ConcurrentHashMap<String, Map<String, List<String>>>();
	// 设备ARP数据列表 {sourceIP,{infInx,[(MAC,destIP)]}}
	private Map<String, Map<String, List<Pair<String, String>>>> arp_list = new ConcurrentHashMap<String, Map<String, List<Pair<String, String>>>>();
	// 设备接口属性列表
	// {devIP,(ifAmount,[(ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)])}
	private Map<String, Pair<String, List<IfRec>>> ifprop_list = new ConcurrentHashMap<String, Pair<String, List<IfRec>>>();
	// 设备OSPF邻居列表 {sourceIP,{infInx,[destIP]}}
	private Map<String, Map<String, List<String>>> ospfnbr_list = new ConcurrentHashMap<String, Map<String, List<String>>>();
	private List<Bgp> bgp_list = new ArrayList<Bgp>();
	// 设备路由表 {sourceIP,{infInx,[nextIP]}}
	private Map<String, Map<String, List<RouteItem>>> route_list = new ConcurrentHashMap<String, Map<String, List<RouteItem>>>();
	private Map<String, Map<String, List<RouteItem>>> rttbl_list = new ConcurrentHashMap<String, Map<String, List<RouteItem>>>();
	private List<Pair<SnmpPara, Pair<String, String>>> sproid_list = new ArrayList<Pair<SnmpPara, Pair<String, String>>>();
	private Map<String, RouterStandbyItem> routeStandby_list = new ConcurrentHashMap<String, RouterStandbyItem>();
	private Map<String, Map<String, String>> special_oid_list = new ConcurrentHashMap<String, Map<String, String>>();
	private Map<String, List<Directitem>> directdata_list = new ConcurrentHashMap<String, List<Directitem>>();
	private Map<String, List<String>> stp_list = new ConcurrentHashMap<String, List<String>>();
	private List<Edge> topo_edge_list = new ArrayList<Edge>();
	//规范化后的设备AFT或ARP数据 {sourceIP,{infInx,[destIP]}}
	private Map<String, Map<String, List<String>>> frm_aftarp_list = new ConcurrentHashMap<String, Map<String, List<String>>>();
	private Map<String, Map<String, List<String>>> aft_list_frm = new ConcurrentHashMap<String, Map<String, List<String>>>();
	public void init(ScanParam sp,AuxParam ap){
		this.scanParam = sp;
		this.myParam = ap;
		siReader.init(ap);
	}
	@Override
	public void run() {
		
		scan();
	}
	public static void main(String[] args) {
		String tmp = "1123";
		String tmp1 = "000010101010";
		tmp1.replaceAll("^(0+)", "");
		System.out.println("tmp1 = " + tmp1);
		System.out.println(tmp.indexOf("0"));
		System.out.println(tmp.substring(tmp.indexOf("0")));
	}
	public static void maian(String[] args) {
		AuxParam auxParam = new AuxParam();
		auxParam.setScan_type("0");
		auxParam.setPing_type("2");
		auxParam.setSeed_type("0");
		auxParam.setSnmp_version("1");
		auxParam.setNbr_read_type("1");
		auxParam.setBgp_read_type("1");
		auxParam.setVrrp_read_type("1");
		
		ScanParam s = new ScanParam();
		s.setCommunity_get_dft("public");
		s.setDepth(1);
		s.getScan_seeds().add("192.168.0.248");
		s.getScan_seeds().add("192.168.0.251");
		NetScan scan = new NetScan();
		scan.init(s, auxParam);
		Thread thread = new Thread(scan);
		thread.setDaemon(true);
		thread.start();
		try {
			thread.sleep(12000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void scan(){
		long start = System.currentTimeMillis();
		System.out.println("scan start");
		String msg = "";
		if("1".equals(myParam.getScan_type()) || "2".equals(myParam.getScan_type()))
		{//读取源数据，从保存的拓扑图
			
		}
		if("0".equals(myParam.getScan_type()) || "2".equals(myParam.getScan_type()))
		{//进行全新扫描
			if("0".equals(myParam.getPing_type()))
			{
				//scan by ips;
			}else{
				if(!scanParam.getScan_scales().isEmpty())
				{//按范围扫描
					
				}else
				{//按种子扫描
					List<String> seeds_cur = new ArrayList<String>();
					seeds_cur = scanParam.getScan_seeds();
					if(seeds_cur.isEmpty()){
						//log
						return ;
					}
					if("0".equals(myParam.getSeed_type()))
					{//按子网向下扫描
						scanBySeeds(scanParam.getScan_seeds());
					}else
					{//按arp向下扫描
						
					}
				}
			}
//			SvLog::writeLog("End Scan.");

			//将接口表中的MAC添加到设备的mac地址表中
			Set<String> keys = devid_list.keySet();
			for(String key : keys)
			{
				Pair<String, List<IfRec>> iinf =  ifprop_list.get(key);//.find(i->first);
				IDBody i = devid_list.get(key);
				if(iinf != null)//ifprop_list.end())
				{
					List<IfRec> ifrec = iinf.getSecond();
//					for(list<IFREC> j = iinf->second.second.begin();
//						j != iinf->second.second.end();
//						++j)
					for(IfRec j : ifrec)
					{
						if(!Utils.isEmptyOrBlank(j.getIfMac()) && !j.getIfMac().equals("000000000000") && !j.getIfMac().equals("FFFFFFFFFFFF"))//->ifMac != "" && j->ifMac != "000000000000" && j->ifMac != "FFFFFFFFFFFF")
						{
							if(!i.getMacs().contains(j.getIfMac()))//find(i->second.macs.begin(), i->second.macs.end(), j->ifMac) == i->second.macs.end())
							{
								i.getMacs().add(j.getIfMac());//i->second.macs.push_back(j->ifMac);
							}
						}
					}
				}
			}
			

//	                SvLog::writeLog("To Save.");
			saveOriginData();//待实现
//	                SvLog::writeLog("End Save.");
		}
		System.out.println("scan end");
		long end = System.currentTimeMillis();
		System.out.println("用时"+(end - start));
		//delete by wings 2009-11-13
		/*else
		{//从文件获取数据
	                //SvLog::writeLog("analyse through old data...", COMMON_MSG, m_callback);
			readOriginData();
		}*/

//	        SvLog::writeLog("To Format data.");
		formatData();//待实现
//	        SvLog::writeLog("To save Format data.");
		saveFormatData();//己实现 、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、继续

	        //SvLog::writeLog("Analyse data...", COMMON_MSG, m_callback);
//			SvLog::writeLog("Analyse data...");

		if(devid_list.isEmpty())
		{
			topo_edge_list.clear();		
		}
		else if(existNetDevice(devid_list))
		{
//			topoAnalyse TA(devid_list, ifprop_list, aft_list_frm, arp_list_frm, ospfnbr_list, rttbl_list, bgp_list, directdata_list, myParam);
//			if(TA.EdgeAnalyse())
//			{
//				topo_edge_list = TA.getEdgeList();
//				// add by zhangyan 2008-09-24
//				if ((myParam.tracert_type == "1") && (TraceAnalyse::getConnection(topo_edge_list) > 1))
//				{
//					TraceReader traceR(devid_list, bgp_list, scanParam.retrytimes, scanParam.timeout, 30);				
//					traceR.TracePrepare();
//					if (myParam.scan_type == "0")
//					{
//						rtpath_list = traceR.getTraceRouteByIPs();
//						//将trace path保存到文件
//						StreamData myfile;
//						myfile.saveTracertList(rtpath_list);
//
//						TraceAnalyse traceA(devid_list, rtpath_list, traceR.RouteDESTIPPairList, traceR.unManagedDevices, scanParam);
//						traceA.AnalyseRRByRtPath(topo_edge_list);
//					}
//					else
//					{//从文件扫描时
//						TraceAnalyse traceA(devid_list, rtpath_list, traceR.RouteDESTIPPairList, traceR.unManagedDevices);
//						traceA.AnalyseRRByRtPath_Direct(topo_edge_list);
//					}				
//					
//				}		
//
//				//补充边的附加信息
//				FillEdge(topo_edge_list);
//				//创建哑设备
//				GenerateDumbDevice(topo_edge_list,devid_list);
//				if(myParam.commit_pc == "0")//not commit pc to svdb
//				{
//	                                SvLog::writeLog("delete pc from entities.");
//					for(DEVID_LIST::iterator j = devid_list.begin();j != devid_list.end();++j)
//					{
//						/*if(j->second.devType != "5" && j->second.devType != "4")*/
//						if(j->second.devType != "5")
//						{
//							topo_entity_list.insert(*j);						
//						}
//					}			
//				}
//				else
//				{
//					topo_entity_list = devid_list;
//				}
//	                        SvLog::writeLog("success to analyse.");
//	                        emit FinishAnalyse();
//			}
//			else
//			{
//	                        SvLog::writeLog("fail to analyse.");
//			}
		}
	}
	public boolean existNetDevice(Map<String, IDBody> dev_list)
	{
//		for(DEVID_LIST::const_iterator i = dev_list.begin();
//			i != dev_list.end();
//			++i)
		Set<String> keys = dev_list.keySet();
		for(String key : keys)
		{
			IDBody i = dev_list.get(key);
//			if(i->second.devType == "0" || i->second.devType == "1" || i->second.devType == "2" || i->second.devType == "3" || i->second.devType == "4")
			if(i.getDevType().equals("0")
					|| i.getDevType().equals("1")
					|| i.getDevType().equals("2")
					|| i.getDevType().equals("3")
					|| i.getDevType().equals("4"))
			{
				return true;
			}
		}
		return false;
	}
	public void saveFormatData(){/////////////////////////////////////////////???????????????????????????????????待实现
		
	}
	// 保存扫描后的原始数据
	public boolean saveOriginData() ////////////////////////////////???????????????????????????????????????待实现
	{	
//		StreamData myfile;//扫描数据文件处理器
//		if(myParam.ping_type != "2")
//			myfile.savaDevidIps(devid_list);
//		myfile.saveIDBodyData(devid_list);
//		myfile.saveAftList(aft_list);
//		myfile.saveArpList(arp_list);
//		myfile.saveInfPropList(ifprop_list);
//		myfile.saveOspfNbrList(ospfnbr_list);
//		myfile.saveRouteList(rttbl_list);
//		myfile.saveBgpList(bgp_list);
//		myfile.saveVrrpList(routeStandby_list);
//	    myfile.saveDirectData(directdata_list);
//		myfile.saveConfigData(scanParam);// added by zhangyan 2008-10-30

		return true;
	}
	public boolean scanBySeeds(List<String> seedList) {
		if (scaned != null)
			scaned.clear();
		if (toscan != null)
			toscan.clear();
		// 从种子发现子网
		for (String seedIp : seedList) {
			List<Pair<String, String>> maskList = new ArrayList<Pair<String, String>>(); 
//			maskList = new ScanMibData().scanMasks(
//					seedIp, 161, scanParam.getCommunity_get_dft(), scanParam.getTimeout(), scanParam.getRetrytimes(), SnmpConstants.version2c);
//			siReader.getIpMaskList(new SnmpPara(seedIp, getCommunity_Get(seedIp), scanParam.getTimeout(), scanParam.getRetrytimes()), maskList);
			new IpAddressTableScan().getIpMaskList(new SnmpPara(seedIp, getCommunity_Get(seedIp), scanParam.getTimeout(), scanParam.getRetrytimes()), maskList);
			for (int i = 0; i < maskList.size(); i++) {
				Pair<String, String> scale_cur = ScanUtils
						.getScaleByIPMask(maskList.get(i));
				boolean bExist = false;
				for (int j = 0; j < toscan.size(); j++) {
					String first = toscan.get(j).getFirst();
					String secend = toscan.get(j).getSecond();
					if (first.equals(scale_cur.getFirst())
							&& secend.equals(scale_cur.getSecond())) {
						bExist = true;
						break;
					}
				}
				if (!bExist) {
					toscan.add(scale_cur);
					// SvLog::writeLog(scale_cur.first + "-" + scale_cur.second,
					// FIND_SUBNET_MSG, m_callback);
					// 记录日志
					// SvLog::writeLog(scale_cur.first + "-" +
					// scale_cur.second);
				}

			}
		}
		for (int depth = 0; depth < scanParam.getDepth(); depth++) {
			// SvLog::writeLog(string("start depth ") + int2str(depth) +
			// " scan.");
			List<Pair<String, String>> scale_list_cur = new ArrayList<Pair<String, String>>();//new ArrayList<Pair<String, String>>(
			Utils.collectionCopyAll(scale_list_cur, toscan);
//					toscan.size());
			while (!scale_list_cur.isEmpty()) {
				Pair<String, String> scale_cur = scale_list_cur.remove(0);
				scaned.add(scale_cur);
				if (scanOneScale(scale_cur, true)) {

				} else {
					return false;
				}
			}
		}

		devid_list = siReader.getDevid_list_visited();
		return true;
	}
	public boolean scanOneScale(Pair<String, String> scale, boolean bChng)
	{
	        String msg = "start scan scale : " + scale.getFirst() + "-" + scale.getSecond();
//	        emit SendScanMessage(s2q(msg));
//	        SvLog::writeLog(msg);
	    System.out.println(msg);
		long ipnumMin = ScanUtils.ipToLong(scale.getFirst());
		long ipnumMax = ScanUtils.ipToLong(scale.getSecond()) + 1;
		In_Addr addr = new In_Addr();
		Vector<String> ip_list_all = new Vector<String>();
		for(long i = ipnumMin; i < ipnumMax; ++i)
		{
			addr.setS_addr(i);
			String ipStr = ScanUtils.longToIp(i);
			if(ipStr.length() < 7 || ipStr.substring(ipStr.length()-4).equals(".255") || ipStr.substring(ipStr.length()-2) == ".0")
			{//排除广播和缺省地址
				continue;
			}
			boolean bExcluded = false;
			List<Pair<Long, Long>> scalesNum = scanParam.getFilter_scales_num();
			for(int j=0;j<scanParam.getFilter_scales_num().size();++j){
				Pair<Long, Long> filterScaleNum = scalesNum.get(j);
				if(i <= filterScaleNum.getSecond() && i >= filterScaleNum.getFirst())
				{
					bExcluded = true;
					break;
				}
			}
			if((!bExcluded) && !m_ip_list_visited.contains(ipStr))
			{
	          ip_list_all.add(ipStr);
	         // break;//////////////////////////////////////////////////????????????????????????????调试用，，只扫描一个ip
			}
		}
		if(!ip_list_all.isEmpty())
	        {
//	                qDebug() << "test123";
			Vector<String> aliveIp_list = new Vector<String>();
			if(myParam.getPing_type().equals("1"))
			{
				boolean bGetLocalArp = false;
				for(String iter : localip_list)
				{
					String local_ip = iter;
					long ipnumLocal = ScanUtils.ipToLong(local_ip);
					if(ipnumLocal < ipnumMax  && ipnumLocal >= ipnumMin)
					{
						//is local net;
						bGetLocalArp = true;
						break;
					}
				}
				if (!icmpPing(ip_list_all, bGetLocalArp, msg, aliveIp_list)) {
					return false;
				}

				if (!scanByIps(aliveIp_list, bChng)) {
					return false;
				}

			} else {
				if (!scanByIps(ip_list_all, bChng)) {
					// qDebug() << "return false";
					return false;
				}
			}
		}
		msg = "end scan scale:" + scale.getFirst() + "-" + scale.getSecond();
	        //SvLog::writeLog(msg, COMMON_MSG, m_callback);
//	        SvLog::writeLog(msg);
		System.out.println(msg);
	    return true;
	}
	public String getCommunity_Get(String ip){
		String community_ret = scanParam.getCommunity_get_dft();
		List<Pair<Pair<Long, Long>, Pair<String, String>>> list = new ArrayList<Pair<Pair<Long, Long>, Pair<String, String>>>();
		long ipnum = ScanUtils.ipToLong(ip);
		for(Pair<Pair<Long, Long>, Pair<String, String>> i : scanParam.getCommunitys_num()){
			if(ipnum >= i.getFirst().getFirst() && ipnum <= i.getFirst().getSecond()){
				return i.getSecond().getFirst();
			}
		}
		return community_ret;
	}
	// 获取IP对应的SNMP Version
	public String getSNMPVersion(String ip)
	{
		String sVersion = myParam.getSnmp_version();
		if (myParam.getSNMPV_list().isEmpty())
		{
			return sVersion;
		}
	    for (Pair<String, String> p : myParam.getSNMPV_list())
		{
			if (ip.equals(p.getFirst()))
			{
				return p.getSecond();
			}		
		}
		return sVersion;
	}
	public boolean scanByIps(Vector<String> aliveIp_list, boolean bChange)
	{
		Vector<SnmpPara> spr_list = new Vector<SnmpPara>();
		for(String aliveIp : aliveIp_list)
		{
			//remarked by zhangyan2008-10-20
			//if(find(m_ip_list_visited.begin(), m_ip_list_visited.end(), *i) == m_ip_list_visited.end())
			{//增加到已访问列表
				m_ip_list_visited.add(aliveIp);
				String ipCmt = getCommunity_Get(aliveIp);
				String snmpVer = getSNMPVersion(aliveIp);//"2" or "1" or "0"	 added by zhangyan 2009-01-06
				spr_list.add(new SnmpPara(aliveIp, ipCmt, scanParam.getTimeout(), scanParam.getRetrytimes(), snmpVer));
			}
		}
		//vector<string>::const_iterator i;
		//i = "172.25.1.1";
		//spr_list.push_back(SnmpPara(*i, "public", scanParam.timeout, scanParam.retrytimes, "2"));

		siReader.setIp_visited_list(m_ip_list_visited);
		//siReader->devid_list_visited = devid_list;//by zhangyan 2008-10-20
	        if (!siReader.getDeviceData(spr_list))
	        {
	            return false;
	        }
		//devid_list = siReader->devid_list_visited;//by zhangyan 2008-10-20
		m_ip_list_visited = siReader.getIp_visited_list();//更新后的已访问ip地址表
		Map<String, IDBody> devlist_cur = siReader.getDevid_list_valid();//在当前范围中发现的新设备

		Map<String, Map<String, List<String>>> aftlist_cur = siReader.getAft_list();
		Map<String, Map<String, List<Pair<String, String>>>> arplist_cur = siReader.getArp_list();
		Map<String, Pair<String, List<IfRec>>> inflist_cur = siReader.getIfprop_list();//->getInfProps();
		Map<String, Map<String, List<String>>> nbrlist_cur = siReader.getOspfnbr_list();//->getOspfNbrData();
		Map<String, Map<String, List<RouteItem>>> rttbl_cur = siReader.getRoute_list();//->getRouteData();  //changed by zhang 2009-03-26 去掉路由表的取数
														 //changed again by wings 2009-11-13 恢复路由表
		List<Bgp> bgplist_cur = siReader.getBgp_list();////getBgpData();
		Map<String, RouterStandbyItem> vrrplist_cur = siReader.getRouteStandby_list();//->getVrrpData();

		Map<String, List<Directitem>> directlist_cur = siReader.getDirectdata_list();//->getDirectData();

		if(!aftlist_cur.isEmpty())
		{
//			aft_list.insert(aftlist_cur.begin(), aftlist_cur.end());
			Utils.mapAddAll(aft_list, aftlist_cur);
		}
		if(!arplist_cur.isEmpty())
		{
//			arp_list.insert(arplist_cur.begin(), arplist_cur.end());
			Utils.mapAddAll(arp_list, arplist_cur);
		}
		if(!inflist_cur.isEmpty())
		{
//			ifprop_list.insert(inflist_cur.begin(), inflist_cur.end());
			Utils.mapAddAll(ifprop_list, inflist_cur);
		}
		if(!nbrlist_cur.isEmpty())
		{
			Utils.mapAddAll(ospfnbr_list, nbrlist_cur);
//			ospfnbr_list.insert(nbrlist_cur.begin(), nbrlist_cur.end());
		}
		if(!rttbl_cur.isEmpty())		//changed by zhang 2009-03-26 去掉路由表的取数
			//changed again by wings 2009-11-13 恢复路由表
		{
			Utils.mapAddAll(rttbl_list, rttbl_cur);
//			rttbl_list.insert(rttbl_cur.begin(), rttbl_cur.end());
		}
		if(!bgplist_cur.isEmpty())
		{
			//bgp_list.insert(bgp_list.end(), bgplist_cur.begin(), bgplist_cur.end());
		}	
		if(!vrrplist_cur.isEmpty())
		{
			Utils.mapAddAll(routeStandby_list, vrrplist_cur);
//			routeStandby_list.insert(vrrplist_cur.begin(), vrrplist_cur.end());
		}
		if(!directlist_cur.isEmpty())
		{
			Utils.mapAddAll(directdata_list, directlist_cur);
//			directdata_list.insert(directlist_cur.begin(), directlist_cur.end());
		}

		if(bChange)
		{//增加新范围
			addScaleFromDevID(devlist_cur);
//			addScaleFromRouteItems(rttbl_cur);			//by zhangyan 2008-12-18
//			addScaleFromDirectData(directlist_cur);		//by zhangyan 2008-10-15
		}

	        return true;
	}
	//从设备信息添加新的扫描范围
	public void addScaleFromDevID(Map<String, IDBody> devlist)
	{
//		for(DEVID_LIST::const_iterator i = devlist.begin(); i != devlist.end(); ++i)
		Set<String> keys = devlist.keySet();
		for(String key : keys)
		{
			IDBody i = devlist.get(key);
			if(i.getDevType().equals(CommonDef.ROUTE_SWITCH) || i.getDevType().equals(CommonDef.ROUTER))//devType == ROUTE_SWITCH || i->second.devType == ROUTER)// || i->second.devType == SWITCH)
			{//r-s,r,//s
//				vector<string>::const_iterator ip_j = i->second.ips.begin();
//				vector<string>::const_iterator msk_j = i->second.msks.begin();
				Vector<String> jp_j = i.getIps();
				Vector<String> msk_j = i.getMsks();
				for(int jp_j_i = 0, msk_j_i = 0; jp_j_i <jp_j.size() && msk_j_i < msk_j.size(); ++jp_j_i,++msk_j_i)              //ip_j != i->second.ips.end() && msk_j != i->second.msks.end();	++ip_j, ++msk_j)
				{
					//if((*ip_j).find("0.0.0.")==0) continue; //by zhangyan 2008-10-21
					if (msk_j.get(msk_j_i).isEmpty())
					{
						continue;
					}
					boolean bNew = true;
					
					Pair<String,String> scale_j = ScanUtils.getScaleByIPMask(new Pair<String,String>(jp_j.get(jp_j_i), msk_j.get(msk_j_i)));
//					
//					for(SCALE_LIST::iterator k = m_scale_list_toscan.begin();
//						k != m_scale_list_toscan.end();	
//						++k)
					for(Pair<String,String> k:toscan)
					{
						if(ScanUtils.isScaleBInA(k, scale_j))
						{
							bNew = false;
							break;
						}
					}
					if(bNew)
					{
//						for(SCALE_LIST::iterator k = m_scale_list_scaned.begin(); 
//							k != m_scale_list_scaned.end();	
//							++k)
						for(Pair<String,String> k : scaned)
						{
							if(ScanUtils.isScaleBInA(k, scale_j))
							{
								bNew = false;
								break;
							}
						}
					}
					if(bNew)
					{//发现新子网
						toscan.add(scale_j);
//						m_scale_list_toscan.push_back(scale_j);
//	                                        //SvLog::writeLog(scale_j.first + "-" + scale_j.second, FIND_SUBNET_MSG, m_callback);
//	                                        SvLog::writeLog(scale_j.first + "-" + scale_j.second);
					}
				}
			}
		}
	}
	

	/**完成*/
	public boolean icmpPing(Vector<String> iplist, boolean bGetla, String msg,
			Vector<String> iplist_alive) {
		iplist_alive.clear();

		Vector<String> iplist_to_ping = new Vector<String>();
		// SvLog::writeLog(string("start ping scale: ") + msg);

		PingHelper myPing = new PingHelper();

		int iTotal = iplist.size();
		int istart = 0, iend = 0;
		int iBatchs = (iTotal + 99)/100;
		for(int btchs = 0;btchs < iBatchs; ++btchs){
			//ping 100 devices at a time ;
			 iend += 100;
             if(iend > iTotal)
             {
                     iend = iTotal;
             }
             Utils.collectionCopyAll(iplist_to_ping,iplist);
			istart = iend;
			// myPing.multiPing(iplist_to_ping, scanParam.retrytimes,
			// scanParam.timeout);
			if (!myPing.multPing(iplist_to_ping, 2, scanParam.getTimeout()))// Ping重试及超时固定为2，1000ms
			{
				return false;
			}
			List<String> list_tmp = myPing.getAliveIpList();
			for(int i = 0;i<list_tmp.size();++i){
				iplist_alive.add(list_tmp.get(i));
			}
			if(bGetla){//获取本机arp表
				String communityStr = getCommunity_Get(localip);
				
				siReader.getOneArpData(new SnmpPara(localip, communityStr, scanParam.getTimeout(), scanParam.getRetrytimes()), localport_macs);
			}
		}
		
		return true;
	}
	// 规范化数据文件
	public boolean formatData()
	{
		//处理vrrp的数据:删除vrrp的ip-mac数据
		List<String> iplist_virtual  = new ArrayList<String>();
		List<String> maclist_virtual = new ArrayList<String>();
		Set<Entry<String, RouterStandbyItem>> entrys = routeStandby_list.entrySet();
		for(Entry<String, RouterStandbyItem> iter:entrys){
			for(String ii : iter.getValue().getVirtualIps()){
				if(!iplist_virtual.contains(ii)){
					iplist_virtual.add(ii);
				}
			}
			for(String ii : iter.getValue().getVirtualMacs()){
				if(!maclist_virtual.contains(ii)){
					maclist_virtual.add(ii);
				}
			}
		}
		Map<String, List<Directitem>> drc_list_tmp = new HashMap<String, List<Directitem>>();
		Set<Entry<String, List<Directitem>>> direct_entrys = directdata_list.entrySet();
		for(Entry<String, List<Directitem>> i : direct_entrys){
			String left_ip = i.getKey();
			for(Entry<String, IDBody> s : devid_list.entrySet()){
				if(s.getValue().getIps().contains(left_ip)){
					left_ip = s.getKey();
					break;
				}
			}
			//format direcdata
			for(Iterator<Directitem> it = i.getValue().iterator();it.hasNext();){
				Directitem j = it.next();
				String right_ip = j.getPeerIp();
				if(right_ip.equals("0.0.0.0")){
					it.remove();
				}
				if(right_ip.indexOf("\\.") == -1){
					boolean bExist = false;
					for(Entry<String, IDBody> iter :devid_list.entrySet()){
						if(iter.getValue().getMacs().contains(right_ip)){
							j.setPeerIp(iter.getKey());
							bExist =true;
							break;
						}
					}
					if(!bExist){
						it.remove();
					}
				}else{
					for(Entry<String, IDBody> s : devid_list.entrySet()){
						if(s.getValue().getMacs().contains(right_ip)){
							j.setPeerIp(s.getKey());
						}
					}
				}
			}
			drc_list_tmp.put(left_ip, i.getValue());
		}
		directdata_list=drc_list_tmp;
		
		if(("1".equals(myParam.getFilter_type())) && (scanParam.getScan_scales_num()!=null && !scanParam.getScan_scales_num().isEmpty())){
			for(Entry<String, Map<String, List<Pair<String, String>>>> m_srcip : arp_list.entrySet()){
				for(Entry<String, List<Pair<String, String>>> m_srcport:m_srcip.getValue().entrySet()){
					m_srcport.getValue().iterator();
					for(Iterator<Pair<String,String>> destip_mac_iter=m_srcport.getValue().iterator();destip_mac_iter.hasNext();){
						Pair<String, String> destip_mac = destip_mac_iter.next();
						boolean bAllowed = false;
						long ipnum = ScanUtils.ipToLong(destip_mac.getFirst());
						for(Pair<Long,Long> j : scanParam.getScan_scales_num()){
							//允许的范围
							if (ipnum <= j.getSecond() && ipnum >= j.getFirst()){
								for(Pair<Long, Long> k :scanParam.getFilter_scales_num()){
									//排除的范围ip
									if (ipnum <= k.getSecond() && ipnum >= k.getFirst())
									{					
										bAllowed = false;
										break;
									}
								}
								break;
							}
						}
						if(!bAllowed){
							destip_mac_iter.remove();
						}
					}
				}
			}
		}
		//规范化接口,
		for(Entry<String, Map<String, List<Pair<String, String>>>> i :arp_list.entrySet()){
			List<Pair<String, List<Pair<String, String>>>> infindex_list = new ArrayList<Pair<String, List<Pair<String, String>>>>();
			//循环source ip
			
				for(Entry<String,List<Pair<String,String>>> m_it : i.getValue().entrySet()){
					String port = m_it.getKey();
					if(port.length() == 1 || (!port.startsWith("G") && !port.startsWith("E"))){
						continue;
					}
					if(i.getValue() !=null && i.getValue().size()>0){
						boolean bValidPort = false;
						for(Iterator<Pair<String, String>> pi = m_it.getValue().iterator();pi.hasNext();){
							Pair<String, String> pi_second = pi.next();
							if(pi_second.getSecond().length() != 12){
								pi.remove();
							}else{
								bValidPort = true;
							}
						}
						if(bValidPort){
							port = port.substring(1);
							while(port.substring(0,1).equals("0") && !port.substring(0,2).equals("0/")){
								port = port.substring(1);
							}
							infindex_list.add(new Pair<String, List<Pair<String, String>>>(port, m_it.getValue()));//delete G / E
						}
					}
				}
			if(infindex_list != null && !infindex_list.isEmpty()){
				i.getValue().clear();
				for(Pair<String, List<Pair<String,String>>> k:infindex_list){
					i.getValue().put(k.getFirst(), k.getSecond());
				}
			}
		}
		for(Entry<String, Map<String, List<String>>> i :aft_list.entrySet()){
			List<Pair<String,List<String>>> infindex_list = new ArrayList<Pair<String,List<String>>>();
			List<Pair<String,List<String>>> validinfindex_list = new ArrayList<Pair<String,List<String>>>();
			
				for(Entry<String, List<String>> m_it : i.getValue().entrySet()){
					String port = m_it.getKey();
					if(port.length() == 1 || (!port.startsWith("G") && !port.startsWith("E"))){
						validinfindex_list.add(new Pair<String, List<String>>(port, m_it.getValue()));
						continue;
					}
					if(i.getValue()!=null&&i.getValue().size()>0){
						boolean bValidPort = false;
						for(Iterator<String> pi = m_it.getValue().iterator();pi.hasNext();){
							String pi_next = pi.next();
							if(pi_next.length()!=12){
								pi.remove();
							}else{
								bValidPort = true;
							}
						}
						if(bValidPort){
							infindex_list.add(new Pair<String, List<String>>(port, m_it.getValue()));
						}
					}
				}
				if(!infindex_list.isEmpty()){
					Pair<String, List<IfRec>> iif = ifprop_list.get(i.getKey());
					if(ifprop_list.containsKey(i.getKey())){
						String myPrex = "";
						for(Pair<String, List<String>> k :infindex_list){
							List<String> str_list = new ArrayList<String>();
							str_list.add(k.getFirst());
							myPrex = getInfDescPrex(str_list,iif.getSecond());
							String port_inf = k.getFirst();
							port_inf = port_inf.substring(1);
							while(port_inf.substring(0,1).equals("0") && !port_inf.substring(0,2).equals("0/")){
								port_inf = port_inf.substring(1);
							}
							String myport = myPrex + port_inf;
							k.setFirst(findInfPortFromDescr(iif.getSecond(),myport));
						}
						i.getValue().clear();
						for(Pair<String,List<String>> k : infindex_list){
							i.getValue().put(k.getFirst(), k.getSecond());
						}
						//合并端口集
						for(Pair<String,List<String>> port_mac : validinfindex_list){
							//port-macs
							if(i.getValue().containsKey(port_mac.getFirst())){//存在该端口
								for(String idestmac : port_mac.getSecond()){
									if(!i.getValue().get(port_mac.getFirst()).contains(port_mac.getFirst())){//不存在该mac
										i.getValue().get(port_mac.getFirst()).add(idestmac);
									}
								}
							}else{
								i.getValue().put(port_mac.getFirst(), port_mac.getSecond());
							}
						}
					}
				}
		}
		//在arp中出现的新的ip-mac作为host加入到设备列表
		List<Pair<String,String>> ipmac_list = new ArrayList<Pair<String,String>>();
		List<String> deleteIPS = new ArrayList<String>();
		for(Entry<String, Map<String, List<Pair<String, String>>>> i :arp_list.entrySet()){
			for(Entry<String, List<Pair<String, String>>> m_it : i.getValue().entrySet()){
				//对source port 循环
				for(Pair<String,String> ip_mac_new : m_it.getValue()){
					if(maclist_virtual.contains(ip_mac_new.getSecond())){
						continue;//忽略vrrp 虚拟ip-mac
					}
					if(ip_mac_new.getFirst().substring(0, 3).equals("127")
							|| ip_mac_new.getFirst().substring(0, 5).equals("0.255")
							|| deleteIPS.contains(ip_mac_new.getFirst())){
						continue;
					}
					boolean bNew = true;
					for(Iterator<Pair<String, String>> ip_mac_Iter = ipmac_list.iterator();ip_mac_Iter.hasNext();){
						Pair<String,String> ip_mac = ip_mac_Iter.next();
						if(ip_mac.getFirst().equals(ip_mac_new.getFirst())
								&& ip_mac.getSecond().equals(ip_mac_new.getSecond())){
							bNew = false;
							break;
						}
						if(ip_mac_new.getFirst().equals(ip_mac.getFirst())
								&& !(ip_mac_new.getSecond().equals(ip_mac.getSecond()))){
							bNew = false;
							deleteIPS.add(ip_mac.getFirst());
							ip_mac_Iter.remove();
							break;
						}
					}
					if(bNew){
						ipmac_list.add(ip_mac_new);
					}
				}
			}
		}
		for(Pair<String, String> i : ipmac_list){
			boolean bExist = false;
			IDBody iid = new IDBody();
			for(Iterator<IDBody> iter = devid_list.values().iterator();iter.hasNext();){
				iid = iter.next();
				if(iid.getIps().contains(i.getFirst())){
					iid.setBaseMac(i.getSecond());
					bExist = true;
					break;
				}
			}
			if(!bExist){
				IDBody id_tmp = new IDBody();
				id_tmp.setSnmpflag("0");
				id_tmp.setBaseMac(i.getSecond());
				id_tmp.setDevType("5");
				id_tmp.setDevModel("");
				id_tmp.setDevFactory("");
				id_tmp.getIps().add(i.getFirst());
				id_tmp.getMsks().add("");
				id_tmp.getInfinxs().add("0");
				id_tmp.getMacs().add(i.getSecond());
				devid_list.put(i.getFirst(), id_tmp);
			}else if(iid.getMacs() == null || iid.getMacs().isEmpty()){
				iid.getMacs().add(i.getSecond());
				iid.setBaseMac(i.getSecond());
			}
		}
		//规范化arp数据表
		frm_aftarp_list.clear();	
		for(Entry<String, Map<String, List<Pair<String, String>>>> i :arp_list.entrySet()){
			String src_ip = i.getKey();
			boolean bDevice = false;
			for(Entry<String,IDBody> j : devid_list.entrySet()){
				if(j.getValue().getIps().contains(src_ip)){
					src_ip = j.getKey();
					bDevice = true;
					break;
				}
			}
			if(!bDevice){
				continue;
			}
			if(!frm_aftarp_list.containsKey(src_ip)){//忽略已经存在的src_ip
				String myPrex = "";
				List<String> infindex_list = new ArrayList<String>();
				if(i.getValue()!=null){
					String temp = i.getValue().entrySet().iterator().next().getKey();
					if(temp.startsWith("G")
							|| temp.startsWith("E")){
						for(Entry<String, List<Pair<String, String>>> j:i.getValue().entrySet()){
							String str_tmp = j.getKey().substring(1);
							if(str_tmp.length() > 1 && !str_tmp.substring(0,2).equals("0/")){
								if(str_tmp.indexOf("0") >0){
									str_tmp = str_tmp.replaceAll("^(0+)", "");
								}
								infindex_list.add(str_tmp);
							}
							
						}
							
					}
				}
				Pair<String, List<IfRec>> iinf = ifprop_list.get(src_ip);
				if(infindex_list!=null && iinf !=null){
					myPrex = getInfDescPrex(infindex_list, iinf.getSecond());
				}
				Map<String, List<String>> pset_tmp = new HashMap<String, List<String>>();
				for(Entry<String, List<Pair<String,String>>> j : i.getValue().entrySet()){
					String myport = j.getKey();
					List<String> destip_list = new ArrayList<String>();
					for(Pair<String, String> k :j.getValue()){
						if(iplist_virtual.contains(k.getFirst())){
							//忽略vrrp 虚拟ip-mac
							continue;
						}
						if(deleteIPS.contains(k.getFirst())){
							continue;
						}
						for(Entry<String, IDBody> m : devid_list.entrySet()){
							if(m.getValue().getIps().contains(k.getFirst())){
								//忽略不在设备列表中的条目
								if(m.getKey().equals(src_ip)){
									////忽略转发到自身的条目
								}
								if(!destip_list.contains(m.getKey())){
									destip_list.add(m.getKey());
									break;
								}
							}
						}
					}
					if(!destip_list.isEmpty() && !pset_tmp.containsKey(myport)){
						pset_tmp.put(myport, destip_list);
					}
				}
				if(!pset_tmp.isEmpty()){
					frm_aftarp_list.put(src_ip, pset_tmp);
				}
			}
		}
		//规范化aft数据表
		aft_list_frm.clear();
		for(Entry<String,Map<String,List<String>>> i : aft_list.entrySet()){
			String src_ip = i.getKey();
			boolean bDevice = false;
			for(Entry<String,IDBody> j : devid_list.entrySet()){
				if(j.getValue().getIps().contains(src_ip)){
					src_ip = j.getKey();
					bDevice = true;
					break;
				}
			}
			if(!bDevice){
				continue;
			}
			if(!aft_list_frm.containsKey(src_ip)){
				//忽略已经存在的src_ip
				String myPrex = "";
				List<String> infindex_list = new ArrayList<String>();
				if(i.getValue()!=null && !i.getValue().isEmpty()){
					String tmp_Myprex = i.getValue().entrySet().iterator().next().getKey();
					if(tmp_Myprex.startsWith("G") || tmp_Myprex.startsWith("E")){
						for(Entry<String, List<String>> j : i.getValue().entrySet()){
							String str_tmp = j.getKey().substring(1);
							infindex_list.add(str_tmp);
						}
					}
				}
				Pair<String,List<IfRec>> iinf = ifprop_list.get(src_ip);
				if(!(ifprop_list.isEmpty()) && iinf!=null){
					myPrex =getInfDescPrex(infindex_list, iinf.getSecond());
				}
				Map<String,List<String>> pset_tmp = new HashMap<String,List<String>>();
				for(Entry<String, List<String>> j :i.getValue().entrySet()){
					String myport = j.getKey();
					if(iinf != null){
						for(IfRec k : iinf.getSecond()){
							//通过端口寻找对应的接口索引
							if(k.getIfPort().equals(myport) && !k.getIfIndex().equals(myport)){
								//需要修改端口
								myport = k.getIfIndex();
								break;
							}
						}
					}
					List<String> destip_list = new ArrayList<String>();
					for(String k : j.getValue()){
						if(maclist_virtual.contains(k)){
							//忽略vrrp 虚拟ip-mac
							continue;
						}
						String temp = k.toUpperCase();
						for(Entry<String, IDBody> m: devid_list.entrySet()){
							if(m.getValue().getMacs().contains(k)){
								//忽略不在设备列表中的条目
								if(m.getKey().equals(src_ip) && !destip_list.contains(m.getKey())){
									//忽略转发到自身的条目
									destip_list.add(m.getKey());
								}
								break;
							}
						}
					}
					if(!destip_list.isEmpty() && !pset_tmp.containsKey(myport)){
						pset_tmp.put(myport, destip_list);
					}
				}
				if(!pset_tmp.isEmpty()){
					aft_list_frm.put(src_ip, pset_tmp);
				}
			}
		}
		return true;
	}
	public String findInfPortFromDescr(List<IfRec> inf_list, String port)
	{
		for(IfRec m : inf_list)
		{//通过接口描述信息寻找对应的接口索引
			int iPlace = m.getIfDesc().indexOf(port);//->ifDesc.find(port);
			//remarked by zhangyan 2008-10-14
			//if(iPlace > 0)
			if(iPlace != -1)
			{//需要修改端口
				return m.getIfPort();//->ifPort;
			}
			else
			{
				String toport = port;
				iPlace = m.getIfDesc().indexOf(toport.replaceAll("/", ":"));//m->ifDesc.find(replaceAll(toport,"/", ":"));
				//remarked by zhangyan 2008-10-14
				//if(iPlace > 0)
				if(iPlace != -1)
				{
					return m.getIfPort();//->ifPort;
				}
			}
		}
		return port;
	}
	//根据接口索引列表与接口表,获取共同前缀
	public String getInfDescPrex(List<String> infIndex_list, List<IfRec> inf_list)
	{
		String prex = "";
		List<String> prex_list = new ArrayList<String>();
		for(String i :infIndex_list)
		{
			String port = i;
			String d=port.substring(0,1);
			port = port.substring(1);
			while(port.substring(0,1) == "0" && port.substring(0,2) != "0/")
			{
				port = port.substring(1);
			}
			

			String prex_tmp = "";
			for(IfRec j : inf_list)
			{
				String vlan = j.getIfDesc().toUpperCase();//->ifDesc;
//	                        transform(vlan.begin(),vlan.end(),vlan.begin(),(int(*)(int))toupper);
				
				if (vlan.indexOf("VLAN")>0 || !vlan.startsWith(d) || (j.getIfDesc().indexOf("/")>=0 && port.indexOf("/")<0))//vlan.find("VLAN") != string::npos || vlan.compare(0,1,d) != 0 || (j->ifDesc.find("/")!=string::npos && port.find("/")==string::npos))
				{
					continue;
				}
				int iPlace = j.getIfDesc().indexOf(port);//->ifDesc.find(port);
				//remarked by zhangyan 2008-10-14
				//if(iPlace > 0)
				if(iPlace != -1)
				{
					prex_tmp = j.getIfDesc().substring(0, iPlace);//j->ifDesc.substr(0, iPlace);
					break;
				}
				else
				{
					iPlace = j.getIfDesc().indexOf(port.replaceAll(":", "/"));//j->ifDesc.find(replaceAll(port, ":", "/"));
					//remarked by zhangyan 2008-10-14
					//if(iPlace > 0)
					if(iPlace != -1)
					{
						prex_tmp = j.getIfDesc().substring(0,iPlace);//->ifDesc.substr(0, iPlace);
						break;
					}
				}
			}
			if(!Utils.isEmptyOrBlank(prex_tmp))//prex_tmp.empty())
			{
				prex_list.add(prex_tmp);//.push_back(prex_tmp);
			}
		}
		int iMinLen = 100000;
//		for(list<string>::iterator i = prex_list.begin(); i != prex_list.end(); ++i)
		for(String i :prex_list)
		{
//			if((int)((*i).size()) < iMinLen)
			if(i.length() < iMinLen)
			{
				iMinLen = i.length();//(int)((*i).size());
				prex = i;
			}
		}
		return prex;
	}
	private Map<String, List<Pair<String,String>>> localport_macs = new HashMap<String, List<Pair<String,String>>>();

	private ScanParam scanParam;
	
	//扫描补充参数
	AuxParam myParam;
	//己扫描子网；
	private List<Pair<String, String>> scaned = new ArrayList<Pair<String,String>>();
	
	private List<Pair<String, String>> toscan = new ArrayList<Pair<String, String>>();//待扫描子网;
	//已访问ip
	private List<String> m_ip_list_visited  = new ArrayList<String>();

	private ReadService siReader = new ReadService();
	// 本地主机ip地址列表
	private List<String> localip_list = new ArrayList<String>();
	private String localip;
	//设备列表
	private Map<String, IDBody> devid_list = new HashMap<String, IDBody>(); 
	
	public Map<String, List<Pair<String, String>>> getLocalport_macs() {
		return localport_macs;
	}
	public void setLocalport_macs(
			Map<String, List<Pair<String, String>>> localport_macs) {
		this.localport_macs = localport_macs;
	}
	public ReadService getSiReader() {
		return siReader;
	}
	public void setSiReader(ReadService siReader) {
		this.siReader = siReader;
	}
	public Map<String, IDBody> getDevid_list() {
		return devid_list;
	}
	public void setDevid_list(Map<String, IDBody> devid_list) {
		this.devid_list = devid_list;
	}
	public List<String> getLocalip_list() {
		return localip_list;
	}
	public void setLocalip_list(List<String> localip_list) {
		this.localip_list = localip_list;
	}
	public AuxParam getMyParam() {
		return myParam;
	}
	public void setMyParam(AuxParam myParam) {
		this.myParam = myParam;
	}
	public ScanParam getScanParam() {
		return scanParam;
	}
	public List<String> getM_ip_list_visited() {
		return m_ip_list_visited;
	}
	public void setM_ip_list_visited(List<String> visited) {
		this.m_ip_list_visited = visited;
	}
	public void setScanParam(ScanParam scanParam) {
		this.scanParam = scanParam;
	}
	public List<Pair<String, String>> getScaned() {
		return scaned;
	}
	public void setScaned(List<Pair<String, String>> scaned) {
		this.scaned = scaned;
	}
	public List<Pair<String, String>> getToscan() {
		return toscan;
	}
	public void setToscan(List<Pair<String, String>> toscan) {
		this.toscan = toscan;
	}
	public String getLocalip() {
		return localip;
	}
	public void setLocalip(String localip) {
		this.localip = localip;
	}
	

}
