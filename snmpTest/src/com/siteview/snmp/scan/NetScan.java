package com.siteview.snmp.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.siteview.snmp.common.AuxParam;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.model.In_Addr;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.util.ScanUtils;


public class NetScan {
	


	public static void main(String[] args) {
		List<String> l = new ArrayList<String>();
		l.add("192.168.0.248");
		NetScan scan = new NetScan();
		scan.scanBySeeds(l);
		scan.myParam.setScan_type("2");
		scan.myParam.setPing_type("1");
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
					if(scanParam.getScan_seeds().isEmpty()){
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
//			for(String key : keys)
//			{
//				
//				IFPROP_LIST iinf =  ifprop_list.find(i->first);
//				if(iinf != ifprop_list.end())
//				{
//					for(list<IFREC> j = iinf->second.second.begin();
//						j != iinf->second.second.end();
//						++j)
//					{
//						if(j->ifMac != "" && j->ifMac != "000000000000" && j->ifMac != "FFFFFFFFFFFF")
//						{
//							if(find(i->second.macs.begin(), i->second.macs.end(), j->ifMac) == i->second.macs.end())
//							{
//								i->second.macs.push_back(j->ifMac);
//							}
//						}
//					}
//				}
//			}
			
		//delete by wings 2009-11-13
			/*if (!localport_macs.empty())
			{
				arp_list.insert(make_pair(localip, localport_macs));
			}
			else
			{
				//add by zhangyan 2008-10-09
				//todo: 读取本机arp(先ping本网段，再读)
				for (list<string>::iterator iter = localip_list.begin(); iter != localip_list.end(); ++iter)
				{
					SCALE_LIST localip_scales;	
					getLocalNet(localip_scales, *iter);
					if (!localip_scales.empty())
					{
						PingLocalNet(localip_scales, *iter);
						break;
					}				
				}
				
			}*/

//	                SvLog::writeLog("To Save.");
//			saveOriginData();
//	                SvLog::writeLog("End Save.");
		}
		//delete by wings 2009-11-13
		/*else
		{//从文件获取数据
	                //SvLog::writeLog("analyse through old data...", COMMON_MSG, m_callback);
			readOriginData();
		}*/

//	        SvLog::writeLog("To Format data.");
//		FormatData();
//	        SvLog::writeLog("To save Format data.");
//		saveFormatData();

	        //SvLog::writeLog("Analyse data...", COMMON_MSG, m_callback);
//			SvLog::writeLog("Analyse data...");

		if(devid_list.isEmpty())
		{
//			topo_edge_list.clear();		
		}
//		else if(ExistNetDevice(devid_list))
//		{
//			topoAnalyse TA(devid_list, ifprop_list, aft_list_frm, arp_list_frm, ospfnbr_list, rttbl_list, bgp_list, directdata_list, myParam);
//			if(TA.EdgeAnalyse())
//			{
//				topo_edge_list = TA.getEdgeList();
//				// add by zhangyan 2008-09-24
//				if ((myParam.tracert_type == "1") && (TraceAnalyse::getConnection(topo_edge_list) > 1))
//				{
//					//TODO(read,analyse)
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
//		}
	}

	public boolean scanBySeeds(List<String> seedList) {
		if (scaned != null)
			scaned.clear();
		if (toscan != null)
			toscan.clear();
		// 从种子发现子网
		for (String seedIp : seedList) {
			List<Pair<String, String>> maskList = new ScanMibData().scanMasks(
					seedIp, 0, "", 0, 0, 0);
			for (int i = 0; i < maskList.size(); i++) {
				Pair<String, String> scale_cur = ScanUtils
						.getScaleByIPMask(maskList.get(i));
				boolean bExist = false;
				for (int j = 0; j < toscan.size(); i++) {
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
			List<Pair<String, String>> scale_list_cur = new ArrayList<Pair<String, String>>(
					toscan.size());
			while (!scale_list_cur.isEmpty()) {
				Pair<String, String> scale_cur = scale_list_cur.remove(0);
				toscan.remove(0);
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
			}
		}
		if(!ip_list_all.isEmpty())
	        {
//	                qDebug() << "test123";
			Vector<String> aliveIp_list = null;
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
	        if (!siReader->getDeviceData(spr_list))
	        {
	            return false;
	        }
		//devid_list = siReader->devid_list_visited;//by zhangyan 2008-10-20
		m_ip_list_visited = siReader.getIp_visited_list();//更新后的已访问ip地址表
		DEVID_LIST devlist_cur = siReader->devid_list_valid;//在当前范围中发现的新设备

		AFT_LIST aftlist_cur = siReader->getAftData();
		ARP_LIST arplist_cur = siReader->getArpData();
		IFPROP_LIST inflist_cur = siReader->getInfProps();
		OSPFNBR_LIST nbrlist_cur = siReader->getOspfNbrData();
		ROUTE_LIST rttbl_cur = siReader->getRouteData();  //changed by zhang 2009-03-26 去掉路由表的取数
														 //changed again by wings 2009-11-13 恢复路由表
		BGP_LIST bgplist_cur = siReader->getBgpData();
		RouterStandby_LIST vrrplist_cur = siReader->getVrrpData();

		DIRECTDATA_LIST directlist_cur = siReader->getDirectData();

		if(!aftlist_cur.empty())
		{
			aft_list.insert(aftlist_cur.begin(), aftlist_cur.end());
		}
		if(!arplist_cur.empty())
		{
			arp_list.insert(arplist_cur.begin(), arplist_cur.end());
		}
		if(!inflist_cur.empty())
		{
			ifprop_list.insert(inflist_cur.begin(), inflist_cur.end());
		}
		if(!nbrlist_cur.empty())
		{
			ospfnbr_list.insert(nbrlist_cur.begin(), nbrlist_cur.end());
		}
		if(!rttbl_cur.empty())		//changed by zhang 2009-03-26 去掉路由表的取数
			//changed again by wings 2009-11-13 恢复路由表
		{
			rttbl_list.insert(rttbl_cur.begin(), rttbl_cur.end());
		}
		if(!bgplist_cur.empty())
		{
			bgp_list.insert(bgp_list.end(), bgplist_cur.begin(), bgplist_cur.end());
		}	
		if(!vrrplist_cur.empty())
		{
			routeStandby_list.insert(vrrplist_cur.begin(), vrrplist_cur.end());
		}
		if(!directlist_cur.empty())
		{
			directdata_list.insert(directlist_cur.begin(), directlist_cur.end());
		}

		if(bChange)
		{//增加新范围
			addScaleFromDevID(devlist_cur);
//			addScaleFromRouteItems(rttbl_cur);			//by zhangyan 2008-12-18
//			addScaleFromDirectData(directlist_cur);		//by zhangyan 2008-10-15
		}

	        return true;
	}
	/**完成*/
	public boolean icmpPing(Vector<String> iplist, boolean bGetla, String msg,
			Vector<String> iplist_alive) {
		iplist_alive.clear();

		Vector<String> iplist_to_ping;
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
             iplist_to_ping = new Vector<>(istart, iend);
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
