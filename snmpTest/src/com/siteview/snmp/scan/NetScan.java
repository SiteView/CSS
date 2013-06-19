package com.siteview.snmp.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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
	private Map<String, Map<String, List<String>>> aft_list = new HashMap<String, Map<String, List<String>>>();
	// 设备ARP数据列表 {sourceIP,{infInx,[(MAC,destIP)]}}
	private Map<String, Map<String, List<Pair<String, String>>>> arp_list = new HashMap<String, Map<String, List<Pair<String, String>>>>();
	// 设备接口属性列表
	// {devIP,(ifAmount,[(ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)])}
	private Map<String, Pair<String, List<IfRec>>> ifprop_list = new HashMap<String, Pair<String, List<IfRec>>>();
	// 设备OSPF邻居列表 {sourceIP,{infInx,[destIP]}}
	private Map<String, Map<String, List<String>>> ospfnbr_list = new HashMap<String, Map<String, List<String>>>();
	private List<Bgp> bgp_list = new ArrayList<Bgp>();
	// 设备路由表 {sourceIP,{infInx,[nextIP]}}
	private Map<String, Map<String, List<RouteItem>>> route_list = new HashMap<String, Map<String, List<RouteItem>>>();
	private Map<String, Map<String, List<RouteItem>>> rttbl_list = new HashMap<String, Map<String, List<RouteItem>>>();
	private List<Pair<SnmpPara, Pair<String, String>>> sproid_list = new ArrayList<Pair<SnmpPara, Pair<String, String>>>();
	private Map<String, RouterStandbyItem> routeStandby_list = new HashMap<String, RouterStandbyItem>();
	private Map<String, Map<String, String>> special_oid_list = new HashMap<String, Map<String, String>>();
	private Map<String, List<Directitem>> directdata_list = new HashMap<String, List<Directitem>>();
	private Map<String, List<String>> stp_list = new HashMap<String, List<String>>();
	private List<Edge> topo_edge_list = new ArrayList<Edge>();
	public void init(ScanParam sp,AuxParam ap){
		this.scanParam = sp;
		this.myParam = ap;
	}
	@Override
	public void run() {
		
		scan();
	}
	public static void main(String[] args) {
		AuxParam auxParam = new AuxParam();
		auxParam.setScan_type("0");
		auxParam.setPing_type("2");
		auxParam.setSeed_type("0");
		auxParam.setSnmp_version("1");
		
		ScanParam s = new ScanParam();
		s.setCommunity_get_dft("public");
		s.setDepth(5);
		s.getScan_seeds().add("192.168.0.248");
		s.getScan_seeds().add("192.168.0.251");
		NetScan scan = new NetScan();
		scan.init(s, auxParam);
		Thread thread = new Thread(scan);
		thread.setDaemon(true);
		thread.start();
		try {
			thread.sleep(1000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void scan(){
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
		//delete by wings 2009-11-13
		/*else
		{//从文件获取数据
	                //SvLog::writeLog("analyse through old data...", COMMON_MSG, m_callback);
			readOriginData();
		}*/

//	        SvLog::writeLog("To Format data.");
		formatData();//待实现
//	        SvLog::writeLog("To save Format data.");
		saveFormatData();//待实现

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
	          break;//////////////////////////////////////////////////????????????????????????????调试用，，只扫描一个ip
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
				
				siReader.getOneArpData(new SnmpPara(localip, communityStr, scanParam.getTimeout(), scanParam.getRetrytimes()), localport_macs);//???????????localport_macs 是干什么的
			}
		}
		
		return true;
	}
	// 规范化数据文件
	public boolean formatData()
	{
		//begin added by tgf 2008-09-22
		//处理vrrp的数据:删除vrrp的ip-mac数据
//		list<string> iplist_virtual;
//		list<string> maclist_virtual;
//		//by zhangyan 2009-01-09
//		for(RouterStandby_LIST::iterator iter = routeStandby_list.begin(); iter != routeStandby_list.end(); ++iter)
//		{		
//			for(vector<string>::iterator ii = iter->second.virtualIps.begin(); ii != iter->second.virtualIps.end(); ++ii)
//			{
//				if(find(iplist_virtual.begin(), iplist_virtual.end(), *ii) == iplist_virtual.end())
//				{
//					iplist_virtual.push_back(*ii);
//				}
//			}
//			for(vector<string>::iterator ii = iter->second.virtualMacs.begin(); ii != iter->second.virtualMacs.end(); ++ii)
//			{
//				if(find(maclist_virtual.begin(), maclist_virtual.end(), *ii) == maclist_virtual.end())
//				{
//					maclist_virtual.push_back(*ii);
//				}
//			}		
//		}
//
//		//for(VRRP_LIST::iterator i = vrrp_list.begin(); i != vrrp_list.end(); ++i)
//		//{
//		//	for(vector<string>::iterator j = i->second.assoips.begin(); j !=  i->second.assoips.end(); ++j)
//		//	{
//		//		if(find(iplist_virtual.begin(), iplist_virtual.end(), *j) == iplist_virtual.end())
//		//		{
//		//			iplist_virtual.push_back(*j);
//		//		}
//		//	}
//		//	for(vector<VRID>::iterator j = i->second.vrids.begin(); j !=  i->second.vrids.end(); ++j)
//		//	{
//		//		if(find(maclist_virtual.begin(), maclist_virtual.end(), j->virtualMac) == maclist_virtual.end())
//		//		{
//		//			maclist_virtual.push_back(j->virtualMac);
//		//		}
//		//	}
//		//}
//
//		//format directdata	    
//		DIRECTDATA_LIST drc_list_tmp;
//		for(DIRECTDATA_LIST::iterator i = directdata_list.begin(); i != directdata_list.end(); ++i)
//		{
//			string left_ip = i->first;		
//			for(DEVID_LIST::iterator s = devid_list.begin(); s != devid_list.end();	++s)
//			{
//				if(find(s->second.ips.begin(), s->second.ips.end(), left_ip) != s->second.ips.end())
//				{
//					left_ip = s->first;
//					break;
//				}
//			}
//			for(list<DIRECTITEM>::iterator j = i->second.begin(); j != i->second.end();)// ++j)
//			{
//				//add by zhangyan 2008-10-6
//				string right_ip = j->PeerIP;			
//				if (right_ip == "0.0.0.0")
//				{
//					i->second.erase(j++);
//					continue;
//				}
//				if (right_ip.find(".") == string::npos)
//				{
//					bool bExist = false;
//					for(DEVID_LIST::iterator iter = devid_list.begin(); iter != devid_list.end(); ++iter)
//					{
//						if (find(iter->second.macs.begin(), iter->second.macs.end(), right_ip) != iter->second.macs.end())
//						{//mac-to-ip
//							j->PeerIP = iter->first;
//							bExist = true;
//							break;
//						}				
//					}
//					if(!bExist)
//					{
//						i->second.erase(j++);
//					}
//					else
//						++j;
//				}
//				else
//				{				
//					for(DEVID_LIST::iterator s = devid_list.begin(); s != devid_list.end();	++s)
//					{
//						if(find(s->second.ips.begin(), s->second.ips.end(), right_ip) != s->second.ips.end())
//						{						
//							j->PeerIP = s->first;	
//							break;
//						}
//					}
//					++j;
//				}			
//			}
//			drc_list_tmp[left_ip] = i->second;
//		}
//		directdata_list = drc_list_tmp;
//		//end added by tgf 2008-09-22
//
//		// added by zhangyan 2008-10-30	
//		if ((myParam.filter_type == "1") && (!scanParam.scan_scales_num.empty()))
//		{
//	                SvLog::writeLog("delete ips, while not in scan scales");
//			for(ARP_LIST::iterator m_srcip = arp_list.begin(); m_srcip != arp_list.end(); ++m_srcip)
//			{//对source ip 循环			
//				for(std::map<std::string, std::list<pair<string,string> > >::iterator m_srcport = m_srcip->second.begin(); 
//					m_srcport != m_srcip->second.end(); 
//					++m_srcport)
//				{//对source port 循环
//	                                for(list<pair<string,string> >::iterator destip_mac = m_srcport->second.begin();
//						destip_mac != m_srcport->second.end(); )					
//					{//对destip-mac	循环
//						bool bAllowed = false;					
//						unsigned long ipnum = ntohl(inet_addr(destip_mac->first.c_str()));					
//	                                        for (std::list<std::pair<unsigned long, unsigned long> >::iterator j = scanParam.scan_scales_num.begin();
//							j != scanParam.scan_scales_num.end(); 
//							++j)
//						{//允许的范围
//							if (ipnum <= j->second && ipnum >= j->first)
//							{					
//								bAllowed = true;
//	                                                        for (std::list<std::pair<unsigned long, unsigned long> >::iterator k = scanParam.filter_scales_num.begin();
//									k != scanParam.filter_scales_num.end(); 
//									++k)
//								{//排除的范围ip
//									if (ipnum <= k->second && ipnum >= k->first)
//									{					
//										bAllowed = false;
//										break;
//									}
//								}
//								break;
//							}
//						}
//						if (!bAllowed)
//						{						
//							//删除不在允许范围内的ip-mac
//							m_srcport->second.erase(destip_mac++);
//						}
//						else
//						{
//							destip_mac++;
//						}
//					}
//				}
//			}
//			
//		} // end by zhangyan 2008-10-30	
//
//		//规范化接口, begin added by tgf 2008-07-04
//		for(ARP_LIST::iterator i = arp_list.begin(); i != arp_list.end(); ++i)
//		{//对source ip 循环
//	                list<pair<string,list<pair<string,string> > > > infindex_list;//[<oldport,[ipmac]>]
//			for(std::map<std::string, std::list<pair<string,string> > >::iterator m_it = i->second.begin(); 
//				m_it != i->second.end(); 
//				++m_it)
//			{//对source port 循环
//				string port = m_it->first;
//				if(port.length() == 1 || (port.compare(0,1,"G") != 0 && port.compare(0,1,"E") != 0))
//				{
//					continue;
//				}
//				if(!(i->second.empty()))
//				{
//					bool bValidPort = false;
//					for(std::list<pair<string,string> >::iterator pi = m_it->second.begin();
//						pi != m_it->second.end();
//						)
//					{
//						if(pi->second.length() != 12)
//						{
//							m_it->second.erase(pi++);
//						}
//						else
//						{
//							++pi;
//							bValidPort = true;
//						}
//					}
//					if(bValidPort)
//					{
//						port = port.substr(1);
//						while(port.substr(0,1) == "0" && port.substr(0,2) != "0/")
//						{
//							port = port.substr(1);
//						}
//						infindex_list.push_back(make_pair(port, m_it->second));//delete G,E
//					}
//				}
//			}
//			if(!infindex_list.empty())
//			{
//				i->second.clear();
//	                        for(list<pair<string,list<pair<string,string> > > >::iterator k = infindex_list.begin();
//					k != infindex_list.end();
//					++k)
//				{
//					i->second.insert(*k);
//				}
//			}
//		}
//
//		for(AFT_LIST::iterator i = aft_list.begin(); i != aft_list.end(); ++i)
//		{//对source ip 循环
//	                list<pair<string,list<string> > > infindex_list;//[<oldport,[mac]>]
//	                list<pair<string,list<string> > > Validinfindex_list;//[<validport,[mac]>]  // added by zhangyan 2008-12-05
//			for(std::map<std::string, std::list<string> >::iterator m_it = i->second.begin(); 
//				m_it != i->second.end(); 
//				++m_it)
//			{//对source port 循环: telnetport -> infPort
//				string port = m_it->first;
//				if(port.length() == 1 || (port.compare(0,1,"G") != 0 && port.compare(0,1,"E") != 0))
//				{
//					Validinfindex_list.push_back(make_pair(port, m_it->second)); // added by zhangyan 2008-12-05
//					continue;
//				}
//				if(!(i->second.empty()))
//				{
//					bool bValidPort = false;
//					for(std::list<string>::iterator pi = m_it->second.begin();
//						pi != m_it->second.end();
//						)
//					{//mac
//						if((*pi).length() != 12)
//						{
//							m_it->second.erase(pi++);
//						}
//						else
//						{
//							++pi;
//							bValidPort = true;
//						}
//					}
//					if(bValidPort)
//					{
//						/*port = port.substr(1);
//						while(port.substr(0,1) == "0" && port.substr(0,2) != "0/")
//						{
//							port = port.substr(1);
//						}*/
//						infindex_list.push_back(make_pair(port, m_it->second));//delete G,E
//					}
//				}
//			}
//			if(!infindex_list.empty())
//			{
//				IFPROP_LIST::iterator iif = ifprop_list.find(i->first);
//				if(iif != ifprop_list.end())
//				{
//					string myPrex = "";
//					//list<string>str_list;//port
//	                                //for(list<pair<string,list<string> > >::iterator k = infindex_list.begin();
//					//	k != infindex_list.end();
//					//	++k)
//					//{
//					//	str_list.push_back(k->first);
//					//}
//					//myPrex = getInfDescPrex(str_list, iif->second.second);
//	                                for(list<pair<string,list<string> > >::iterator k = infindex_list.begin();
//						k != infindex_list.end();
//						++k)
//					{
//						list<string>str_list;//port
//						str_list.push_back(k->first);
//						myPrex = getInfDescPrex(str_list, iif->second.second);
//						string port_inf = k->first;
//						port_inf = port_inf.substr(1);
//						while(port_inf.substr(0,1) == "0" && port_inf.substr(0,2) != "0/")
//						{
//							port_inf = port_inf.substr(1);
//						}
//						string myport = myPrex + port_inf;
//						k->first = findInfPortFromDescr(iif->second.second, myport);
//					}
//					i->second.clear();
//	                                for(list<pair<string,list<string> > >::iterator k = infindex_list.begin();
//						k != infindex_list.end();
//						++k)
//					{
//						i->second.insert(*k);
//					}
//					// begin added by zhangyan 2008-12-05
//					//合并端口集
//	                                for (list<pair<string,list<string> > >::iterator port_mac = Validinfindex_list.begin();
//						port_mac != Validinfindex_list.end(); 
//						++port_mac)
//					{//port-macs
//						if (i->second.find(port_mac->first) != i->second.end())
//						{//存在该端口						
//							for (list<string>::iterator idestmac = port_mac->second.begin(); 
//								idestmac != port_mac->second.end(); 
//								++idestmac)
//							{
//								if (find(i->second[port_mac->first].begin(), i->second[port_mac->first].end(), *idestmac) == i->second[port_mac->first].end())
//								{//不存在该mac
//									i->second[port_mac->first].push_back(*idestmac);
//								}
//							}
//						}
//						else
//						{
//							i->second.insert(*port_mac);
//						}
//					}
//					// end added by zhangyan 2008-12-05
//				}
//			}
//		}
//		//规范化接口, end added by tgf 2008-07-04
//
//		//在arp中出现的新的ip-mac作为host加入到设备列表
//	        list<std::pair<string,string> > ipmac_list;
//		list<string> deleteIPS; // added by zhangyan 2008-12-04
//		for(ARP_LIST::iterator i = arp_list.begin(); i != arp_list.end(); ++i)
//		{//对source ip 循环
//			for(std::map<std::string, std::list<pair<string,string> > >::iterator m_it = i->second.begin(); 
//				m_it != i->second.end(); 
//				++m_it)
//			{//对source port 循环
//				for(std::list<pair<string,string> >::iterator ip_mac_new = m_it->second.begin();
//					ip_mac_new != m_it->second.end();
//					++ip_mac_new)
//				{//对dest ip 循环
//					//begin added by tgf 2008-09-23
//					if(find(maclist_virtual.begin(), maclist_virtual.end(), ip_mac_new->second) != maclist_virtual.end())
//					{//忽略vrrp 虚拟ip-mac
//						continue;
//					}				
//					//end added by tgf 2008-09-23
//					
//					// added by zhangyan 2008-12-04
//					if((ip_mac_new->first.compare(0, 3, "127") == 0) 
//						//add by wings 2009-11-13
//						||(ip_mac_new->first.compare(0, 5, "0.255") == 0) 
//						|| (find(deleteIPS.begin(), deleteIPS.end(), ip_mac_new->first) != deleteIPS.end()))
//					{
//						continue;
//					}
//
//					bool bNew = true;
//					for(std::list<pair<string,string> >::iterator ip_mac = ipmac_list.begin();
//						ip_mac != ipmac_list.end();
//						++ip_mac)
//					{
//						if(ip_mac_new->first == ip_mac->first && ip_mac_new->second == ip_mac->second)
//						{
//							bNew = false;
//							break;
//						}
//						// added by zhangyan 2008-10-23
//						if(ip_mac_new->first == ip_mac->first && ip_mac_new->second != ip_mac->second)
//						{//删除此IP-MAC
//							bNew = false;
//							deleteIPS.push_back(ip_mac->first);
//							//cout<<"deleteIPS;"<<ip_mac->first<<endl;
//							ipmac_list.remove(*ip_mac);
//							break;
//						}
//					}
//					if(bNew)
//					{
//						ipmac_list.push_back(*ip_mac_new);
//					}
//				}
//			}
//		}
//		for(std::list<pair<string,string> >::iterator i = ipmac_list.begin(); i != ipmac_list.end(); ++i)
//		{
//			bool bExist = false;
//			DEVID_LIST::iterator iid;
//			for(iid = devid_list.begin(); iid != devid_list.end(); ++iid)
//			{
//				if(find(iid->second.ips.begin(), iid->second.ips.end(), i->first) != iid->second.ips.end())
//				{
//					iid->second.baseMac = i->second;// added by zhangyan 2008-10-23
//					bExist = true;
//					break;
//				}
//			}
//			if(!bExist)
//			{//作为host加入
//				IDBody id_tmp;
//				id_tmp.snmpflag = "0";
//				id_tmp.baseMac = i->second;
//				id_tmp.devType = "5";//host
//				id_tmp.devModel = "";
//				id_tmp.devFactory = "";
//				id_tmp.devTypeName = "";
//				id_tmp.ips.push_back(i->first);
//				id_tmp.msks.push_back("");
//				id_tmp.infinxs.push_back("0");
//				id_tmp.macs.push_back(i->second);
//				devid_list.insert(make_pair(i->first, id_tmp));
//			}
//			else if(iid->second.macs.empty())
//			{//将MAC地址加入设备的
//				iid->second.macs.push_back(i->second);
//				iid->second.baseMac = i->second;
//			}
//		}
//		
//		//规范化arp数据表
//		arp_list_frm.clear();	
//		for(ARP_LIST::iterator i = arp_list.begin(); i != arp_list.end(); ++i)
//		{
//			string src_ip = i->first;
//			bool bDevice = false;
//			for(DEVID_LIST::iterator j = devid_list.begin(); j != devid_list.end(); ++j)
//			{
//				if(find(j->second.ips.begin(), j->second.ips.end(), src_ip) != j->second.ips.end())
//				{
//					src_ip = j->first;
//					bDevice = true;
//					break;
//				}
//			}
//			if(!bDevice)
//			{
//				continue;
//			}
//			if(arp_list_frm.find(src_ip) == arp_list_frm.end())
//			{//忽略已经存在的src_ip
//				string myPrex = "";
//				list<string> infindex_list;
//				if(!(i->second.empty()))
//				{
//					if( i->second.begin()->first.compare(0, 1, "G") == 0 ||
//						i->second.begin()->first.compare(0, 1, "E") == 0)
//					{
//	                                        for(std::map<string,list<pair<string,string> > >::iterator j = i->second.begin();
//							j != i->second.end();
//							++j)
//						{
//							string str_tmp = j->first.substr(1);
//							if(str_tmp.length() > 1 && str_tmp.substr(0,2) != "0/")
//							{
//								str_tmp = lTrim(lTrim(str_tmp),"0");
//							}
//							infindex_list.push_back(str_tmp);
//						}
//					}
//				}
//				IFPROP_LIST::iterator iinf = ifprop_list.find(src_ip);
//				if(!infindex_list.empty() && iinf != ifprop_list.end())
//				{
//					myPrex = getInfDescPrex(infindex_list, iinf->second.second);
//				}
//
//	                        std::map<string, list<string> > pset_tmp;
//	                        for(std::map<string,list<pair<string,string> > >::iterator j = i->second.begin();
//					j != i->second.end();
//					++j)
//				{				
//					string myport = j->first;//缺省接口				
//					list<string> destip_list;
//					//dest_ip->dev_ip
//	                                for(list<pair<string,string> >::iterator k = j->second.begin(); k != j->second.end(); ++k)
//					{					
//						//begin added by tgf 2008-09-23
//						if(find(iplist_virtual.begin(), iplist_virtual.end(), k->first) != iplist_virtual.end())
//						{//忽略vrrp 虚拟ip-mac
//							continue;
//						}
//						//end added by tgf 2008-09-23
//
//						// added by zhangyan 2008-12-04
//						if(find(deleteIPS.begin(), deleteIPS.end(), k->first) != deleteIPS.end())
//						{
//							continue;
//						}
//
//						for(DEVID_LIST::iterator m = devid_list.begin(); m != devid_list.end(); ++m)
//						{
//							if(find(m->second.ips.begin(), m->second.ips.end(), k->first) != m->second.ips.end())
//							{//忽略不在设备列表中的条目
//								if(m->first == src_ip)
//								{//忽略转发到自身的条目
//									break;
//								}
//								if(find(destip_list.begin(), destip_list.end(), m->first) == destip_list.end())
//								{
//									destip_list.push_back(m->first); 
//									break;
//								}
//							}
//						}
//					}
//					if(!destip_list.empty() && pset_tmp.find(myport) == pset_tmp.end())
//					{
//						pset_tmp.insert(make_pair(myport,destip_list));
//					}
//				}
//				if(!pset_tmp.empty())
//				{
//					arp_list_frm.insert(make_pair(src_ip, pset_tmp));
//				}
//			}
//		}
//
//		//规范化aft数据表
//		aft_list_frm.clear();
//		for(AFT_LIST::iterator i = aft_list.begin(); i != aft_list.end(); ++i)
//		{
//			string src_ip = i->first;
//			bool bDevice = false;
//			for(DEVID_LIST::iterator j = devid_list.begin(); j != devid_list.end(); ++j)
//			{//src_ip -> dev_ip
//				if(find(j->second.ips.begin(), j->second.ips.end(), src_ip) != j->second.ips.end())
//				{
//					src_ip = j->first;
//					bDevice = true;
//					break;
//				}
//			}
//			if(!bDevice)
//			{
//				continue;
//			}
//			if(aft_list_frm.find(src_ip) == aft_list_frm.end())
//			{//忽略已经存在的src_ip
//				string myPrex = "";
//				list<string> infindex_list;
//				if(!(i->second.empty()))
//				{
//	                                ////SvLog::writeLog(src_ip.c_str()+string("  ") + i->second.begin()->first.c_str());
//					if( i->second.begin()->first.compare(0, 1, "G") == 0 ||
//						i->second.begin()->first.compare(0, 1, "E") == 0)
//					{
//	                                        for(std::map<string,list<string> >::iterator j = i->second.begin();
//							j != i->second.end();
//							++j)
//						{
//							string str_tmp = j->first.substr(1);
//	                                                ////SvLog::writeLog(string("str_tmp:")+str_tmp.c_str());
//							/*if(str_tmp.length() > 1 && str_tmp.substr(1,2) != "/")
//							{
//								str_tmp = lTrim(lTrim(str_tmp),"0");
//							}*/
//							infindex_list.push_back(str_tmp);
//						}
//					}
//				}
//				IFPROP_LIST::iterator iinf = ifprop_list.find(src_ip);
//				if(!infindex_list.empty() && iinf != ifprop_list.end())
//				{
//					myPrex = getInfDescPrex(infindex_list, iinf->second.second);
//				}
//
//	                        std::map<string, list<string> > pset_tmp;
//	                        for(std::map<string,list<string> >::iterator j = i->second.begin();
//					j != i->second.end();
//					++j)
//				{//port -> infindex
//					string myport = j->first;//缺省接口号
//					
//					if(iinf != ifprop_list.end())
//					{
//						for(list<IFREC>::iterator k = iinf->second.second.begin();
//							k != iinf->second.second.end();
//							++k)
//						{//通过端口寻找对应的接口索引
//							if(k->ifPort == myport && k->ifIndex != myport)
//							{//需要修改端口
//								myport = k->ifIndex;
//								break;
//							}
//						}
//					}
//					
//					list<string> destip_list;
//					//mac->dev_ip
//					for(list<string>::iterator k = j->second.begin(); k != j->second.end(); ++k)
//					{
//						//begin added by tgf 2008-09-23
//						if(find(maclist_virtual.begin(), maclist_virtual.end(), *k) != maclist_virtual.end())
//						{//忽略vrrp 虚拟ip-mac
//							continue;
//						}
//						//end added by tgf 2008-09-23
//	                                        std::transform((*k).begin(), (*k).end(), (*k).begin(), (int(*)(int))toupper);
//						for(DEVID_LIST::iterator m = devid_list.begin(); m != devid_list.end(); ++m)
//						{
//							if(find(m->second.macs.begin(), m->second.macs.end(), *k) != m->second.macs.end())
//							{//忽略不在设备列表中的条目
//								if(m->first != src_ip && find(destip_list.begin(), destip_list.end(), m->first) == destip_list.end())
//								{//忽略转发到自身的条目
//									destip_list.push_back(m->first);
//								}
//								break;
//							}
//						}
//					}
//					
//					if(!destip_list.empty() && pset_tmp.find(myport) == pset_tmp.end())
//					{
//						pset_tmp.insert(make_pair(myport, destip_list));
//					}
//				}
//				if(!pset_tmp.empty())
//				{
//					aft_list_frm.insert(make_pair(src_ip, pset_tmp));
//				}
//			}
//		}
		return true;
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
