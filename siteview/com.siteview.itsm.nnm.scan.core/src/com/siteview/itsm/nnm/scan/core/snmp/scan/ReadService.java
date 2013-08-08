package com.siteview.itsm.nnm.scan.core.snmp.scan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;

import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;

import com.siteview.itsm.nnm.scan.core.snmp.common.AuxParam;
import com.siteview.itsm.nnm.scan.core.snmp.common.ScanParam;
import com.siteview.itsm.nnm.scan.core.snmp.common.SnmpPara;
import com.siteview.itsm.nnm.scan.core.snmp.constants.CommonDef;
import com.siteview.itsm.nnm.scan.core.snmp.constants.OIDConstants;
import com.siteview.itsm.nnm.scan.core.snmp.devicehandler.IDeviceHandler;
import com.siteview.itsm.nnm.scan.core.snmp.model.Device;
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Bgp;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.DevicePro;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Directitem;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IfRec;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.RouteItem;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.RouterStandbyItem;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.SystemGroup;
import com.siteview.itsm.nnm.scan.core.snmp.util.OIDTypeUtils;
import com.siteview.itsm.nnm.scan.core.snmp.util.ScanUtils;
import com.siteview.itsm.nnm.scan.core.snmp.util.ThreadTaskPool;
import com.siteview.itsm.nnm.scan.core.snmp.util.Utils;

public class ReadService {

	//�豸������Ϣ�б�{devIP,(TYPE,SNMP,[IP],[MAC],[MASK],SYSOID,sysname)}
	private Map<String, IDBody> devid_list = new ConcurrentHashMap<String,IDBody>();
	//�豸�ӿ������б� {devIP,(ifAmount,[(ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)])}
	private Map<String, Pair<String, List<IfRec>>> ifprop_list = new ConcurrentHashMap<String,Pair<String,List<IfRec>>>();
	//�豸AFT�����б� {sourceIP,{port,[MAC]}}
	private Map<String, Map<String, List<String>>> aft_list = new ConcurrentHashMap<String, Map<String,List<String>>>();
	//�豸ARP�����б� {sourceIP,{infInx,[(MAC,destIP)]}}
	private Map<String, Map<String, List<Pair<String, String>>>> arp_list = new ConcurrentHashMap<String, Map<String,List<Pair<String,String>>>>();
	//�豸OSPF�ھ��б� {sourceIP,{infInx,[destIP]}}
	private Map<String, Map<String, List<String>>> ospfnbr_list = new ConcurrentHashMap<String, Map<String,List<String>>>();
	private volatile List<Bgp> bgp_list = new ArrayList<Bgp>();
	public Map<String,String> map_devType = new ConcurrentHashMap<String, String>();
	//�豸·�ɱ� {sourceIP,{infInx,[nextIP]}}
	private Map<String, Map<String, List<RouteItem>>> route_list = new ConcurrentHashMap<String, Map<String, List<RouteItem>>>();
	private volatile List<Pair<SnmpPara,Pair<String,String>>> sproid_list = new ArrayList<Pair<SnmpPara,Pair<String,String>>>();
	private Map<String, IDBody> devid_list_visited = new ConcurrentHashMap<String, IDBody>();
	private Map<String, DevicePro> dev_type_list = new ConcurrentHashMap<String,DevicePro>();
	public volatile List<String> ip_visited_list = new ArrayList<String>();
	private ThreadTaskPool pool ;
	private ScanParam scanParam = new ScanParam();
	private AuxParam auxParam = new AuxParam();
	private volatile boolean  isStop = false;
	byte[] lock = new byte[0];
	private Map<String, RouterStandbyItem> routeStandby_list = new ConcurrentHashMap<String, RouterStandbyItem>();
	private Map<String, Map<String, String> > special_oid_list = new ConcurrentHashMap<String, Map<String,String>>();
	private Map<String, List<Directitem>> directdata_list = new ConcurrentHashMap<String, List<Directitem>>();
	private Map<String,List<String>> stp_list = new ConcurrentHashMap<String,List<String>>();
	public void stop(){
		isStop = true;
	}
	public void init(AuxParam auxParam){
		this.auxParam = auxParam;
	}
	public ReadService(){
		
	}
	public ReadService(Map<String,DevicePro> devtypemap,ScanParam scanpr,AuxParam auxpr,Map<String,Map<String,String>> specialoidlist){
		this.dev_type_list = devtypemap;
		this.special_oid_list = specialoidlist;
		scanParam.setCommunity_get_dft(scanpr.getCommunity_get_dft());
		scanParam.setThreadCount(scanpr.getThreadCount());
		scanParam.setRetrytimes(scanpr.getRetrytimes());
		scanParam.setTimeout(scanpr.getTimeout());
		
		auxParam.setSeed_type(auxpr.getSeed_type());
		auxParam.setArp_read_type(auxpr.getArp_read_type());
		auxParam.setNbr_read_type(auxpr.getArp_read_type());
		auxParam.setRt_read_type(auxpr.getRt_read_type());
		auxParam.setVrrp_read_type(auxpr.getVrrp_read_type());
		auxParam.setPing_type(auxpr.getPing_type());
		auxParam.setBgp_read_type(auxpr.getBgp_read_type());
		auxParam.setSnmp_version(auxpr.getSnmp_version());
		auxParam.setSNMPV_list(auxpr.getSNMPV_list());
		
		map_devType.put("0", "ROUTE-SWITCH");
		map_devType.put("1", "SWITCH");
		map_devType.put("2", "ROUTER");
		map_devType.put("3", "FIREWALL");
		map_devType.put("4", "SERVER");
		map_devType.put("5", "HOST");
		map_devType.put("6", "OTHER");
	}
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
	private Map<String, IDBody> devid_list_valid = new ConcurrentHashMap<String,IDBody>();
	
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
	//��ȡ�����豸����ͨ����
	public boolean getDeviceData(Vector<SnmpPara> spr_list)
	{
		if (!getSysInfos(spr_list)) {
			return false;
		}
		aft_list.clear();
		arp_list.clear();
		ifprop_list.clear();
		ospfnbr_list.clear();
		bgp_list.clear();
		if(sproid_list.size() > 0)
		{
			if(sproid_list.size() == 1)
			{
				getOneDeviceData(sproid_list.get(0).getFirst(),sproid_list.get(0).getSecond().getFirst(),sproid_list.get(0).getSecond().getSecond());
			}
			else
			{
				latch = new CountDownLatch(sproid_list.size());
				for(final Pair<SnmpPara, Pair<String,String>> i : sproid_list)
				{
	                                if (isStop)
	                                {
	                                    return false;
	                                }
	                                else
	                                {
	                                	ThreadTaskPool.getInstance().excute(new Runnable() {
											
											@Override
											public void run() {
												getOneDeviceData(i.getFirst(), i.getSecond().getFirst(), i.getSecond().getSecond());
												latch.countDown();
											}
										});
	                               }
				}
				try {
					latch.await();//�ȴ������߳����
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	    return true;
	}

	/**
	 * ��ȡһ̨�豸����ͨ���� <aft��,arp��,�ӿڱ�,
	 * @param spr
	 * @param devType
	 * @param sysOid
	 */
	public void getOneDeviceData(SnmpPara spr, String devType, String sysOid)
	{
		//	        ��־
		System.out.println(Thread.currentThread().getName() + "======================================================��ʼ   getOneDeviceData");
		IDeviceHandler device = DeviceFactory.getInstance().createDevice(sysOid);
		//�豸AFT�����б�{sourceIP,[port,[MAC]]}
		Map<String, Map<String, List<String>>> aftlist_cur = new HashMap<String, Map<String, List<String>>>();
		//�豸ARP�����б�{sourceIP,[infInx,[(MAC,destIP)]]}
		Map<String,Map<String, List<Pair<String, String>>>> arplist_cur = new HashMap<String, Map<String,List<Pair<String,String>>>>();
		//�豸�ӿ������б� {devIP,(ifAmount,[(ifindex,ifType,ifDescr,ifMac,portNum,ifSpeed)])}
		Map<String,Pair<String,List<IfRec>>> inflist_cur = new HashMap<String, Pair<String,List<IfRec>>>();;
		//�豸��OSPF�ھӱ�{sourceIP,{infInx,[destIP]}}
		Map<String, Map<String, List<String>>> nbrlist_cur = new HashMap<String, Map<String, List<String>>>();
		//�豸��·�ɱ�{sourceIP,{infInx,[nextIP]}}
		Map<String,Map<String,List<RouteItem>>> rttbl_cur = new HashMap<String, Map<String,List<RouteItem>>>();			
		//�ָ�·�ɱ�
		List<Bgp> bgplist_cur = new ArrayList<Bgp>();
		/*VRRP_LIST vrrplist_cur;*/
		Map<String, RouterStandbyItem> vrrplist_cur = new HashMap<String, RouterStandbyItem>();//vrrp,hsrp��
		//ֱ�ӱ������б�{ip,(index,peerid,peerip,peerportdesc)}
		Map<String,List<Directitem>> drctdata_cur = new HashMap<String, List<Directitem>>();
		//�豸stp�б�
		Map<String,List<String>> stplist_cur = new HashMap<String, List<String>>();

		boolean bAft   =  false;
		boolean bArp   =  false;
		boolean bInf   =  true;
		boolean bNbr   =  false;
		boolean bRoute =  false;
		boolean bBgp   =  false;
		boolean bVrrp  =  false; 
		boolean bDirect = true;

		MibScan snmp = new MibScan();
		//��ȡoid���� 
		Map<String, String> oidIndexList = new HashMap<String,String>();
		getOidIndex(oidIndexList, sysOid);
		
		//��ѯ�ӿڱ�
		inflist_cur = device.getInfProp(snmp, spr, oidIndexList, devType.equals(CommonDef.ROUTER));
		
		//��ѯֱ�ӱ����ݱ�
		drctdata_cur = device.getDirectData(snmp, spr);
		
		
		if(devType.equals(CommonDef.ROUTE_SWITCH) || devType.equals(CommonDef.FIREWALL))
		{//r-s
			bAft = true;
			bArp = true;				
			bRoute = true;	
			aftlist_cur = device.getAftData(snmp, spr, oidIndexList);
			arplist_cur = device.getArpData(snmp, spr, oidIndexList);
			rttbl_cur = device.getRouteData(snmp, spr, oidIndexList);  
			if("1".equals(auxParam.getNbr_read_type()))
			{
				bNbr = true;
				nbrlist_cur = device.getOspfNbrData(snmp, spr);
			}
			if("1".equals(auxParam.getBgp_read_type()))
			{
				bBgp = true;
				bgplist_cur = device.getBgpData(snmp, spr);
			}
			if("1".equals(auxParam.getVrrp_read_type()))
			{
				bVrrp = true;
				vrrplist_cur = device.getVrrpData(snmp, spr);
			}
			//����telnet����
			Vector<String> ips_tmp = devid_list_valid.get(spr.getIp()).getIps();
			String ip_tmp = "";
			for(String iter :ips_tmp)
			{//
			}
			if (!"".equals(ip_tmp)) //��IP������telnet��aft��
				ip_tmp = "";
		}
		else if(devType.equals(CommonDef.SWITCH))
		{//s
			bAft = true;
			aftlist_cur = device.getAftData(snmp, spr, oidIndexList);
			//����telnet����
			Vector<String> ips_tmp = devid_list_valid.get(spr.getIp()).getIps();
			String ip_tmp = "";
			for(String iter : ips_tmp)
			{
			}
			if (ip_tmp != "") //��IP������telnet��aft��
			{
			}

			if(auxParam.getSeed_type().equals("1"))
			{
				bArp = true;
				arplist_cur = device.getArpData(snmp, spr, oidIndexList);
				if(arplist_cur.isEmpty())
				{
					ip_tmp = "";
					for(String iter : ips_tmp)
					{
					}
					if (ip_tmp != "") //��IP������telnet��arp��
					{
					}
				}
			}
		}
		else if(devType.equals(CommonDef.ROUTER))
		{//r
			bArp = true;
			bRoute = true;		
			arplist_cur = device.getArpData(snmp, spr, oidIndexList);
			rttbl_cur = device.getRouteData(snmp, spr, oidIndexList);		
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
			if(arplist_cur.isEmpty())
			{
				Vector<String> ips_tmp = devid_list_valid.get(spr.getIp()).getIps();
				String ip_tmp = "";
				for(String iter : ips_tmp)
				{
				}
				if (ip_tmp != "") //��IP������telnet��arp��
				{
					
				}
			}
		}
		else if (devType.equals(CommonDef.SERVER)&& auxParam.getSeed_type().equals("1"))
		{
			bArp = true;
			bRoute = true;		
			arplist_cur = device.getArpData(snmp, spr, oidIndexList);
			rttbl_cur = device.getRouteData(snmp, spr, oidIndexList);		
			if(arplist_cur.isEmpty())
			{
				Vector<String> ips_tmp = devid_list_valid.get(spr.getIp()).getIps();
				String ip_tmp = "";
				for(String iter : ips_tmp)
				{
				}
				if (ip_tmp != "") //��IP������telnet��arp��
				{
					//��telnet����IP��arp��
//	                                map<string, list<pair<string,string> > > portipmac_tmp = telnetReader->getArpData(ip_tmp);
//					if(!portipmac_tmp.empty())
//					{
//						arplist_cur.insert(make_pair(spr.ip, portipmac_tmp));
//					}
				}
			}
		}

		if(aftlist_cur == null || aftlist_cur.isEmpty())
		{
			if(bAft)
			{
//	                        SvLog::writeLog("Can not get aft of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_AFT_LOG);
			}
		}
		else
		{
			synchronized (lock) {
				Utils.mapAddAll(aft_list, aftlist_cur);
			}
			
		}
		if(arplist_cur == null || arplist_cur.isEmpty())
		{
			if(bArp)
			{
//	                        SvLog::writeLog("Can not get arp of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_ARP_LOG);
			}
		}
		else
		{
			synchronized (lock) {
				Utils.mapAddAll(arp_list, arplist_cur);
			}
		}

		if(inflist_cur == null || inflist_cur.isEmpty())
		{
			if(bInf)
			{
//	                        SvLog::writeLog("Can not get inf of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_INF_LOG);
			}
		}
		else
		{
			synchronized (lock) {
				Utils.mapAddAll(ifprop_list, inflist_cur);
			}
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
			synchronized (lock) {
				Utils.mapAddAll(ospfnbr_list, nbrlist_cur);
			}
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
			synchronized (lock) {
				Utils.mapAddAll(route_list, rttbl_cur);
			}
		}

		if(bgplist_cur == null || bgplist_cur.isEmpty())
		{
			if(bBgp)
			{
//	                        SvLog::writeLog("Can not get BGP of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_BGP_LOG);
			}
		}
		else
		{
			synchronized (lock) {
				Utils.collectionCopyAll(bgp_list, bgplist_cur);
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
			synchronized (lock) {
				Utils.mapAddAll(routeStandby_list, vrrplist_cur);
			}
		}

		if(drctdata_cur==null || drctdata_cur.isEmpty())
		{
			if(bDirect)
			{
//	                        SvLog::writeLog("Can not get DIRECT of " + spr.ip);
//	                        SvLog::writeErrorLog(spr.ip, ERR_DRC_LOG);
			}
		}
		else
		{
			synchronized (lock) {
				Utils.mapAddAll(directdata_list, drctdata_cur);
			}
			
		}
		if (stplist_cur.isEmpty())
		{
		}
		else
		{
			synchronized (lock) {
				Utils.mapAddAll(stp_list, stplist_cur);
			}
		}

		System.out.print(spr.getIp() + "����     ");
		System.out.println(Thread.currentThread().getName() + "======================================================����   getOneDeviceData");
	}
	public void getIpMaskList(SnmpPara spr, List<Pair<String, String>> ipcm_result)
	{
//		ipcm_result.clear();
		MibScan snmp = new MibScan();
		//IP-MASK��ַ1.3.6.1.2.1.4.20.1.3
	    List<Pair<String,String> > ipmsks = snmp.getMibTable(spr,"1.3.6.1.2.1.4.20.1.3");
	    
		if (!ipmsks.isEmpty()) {
			for (Pair<String, String> i : ipmsks) {
				String ip_cur = i.getFirst().substring(21);
				if (ip_cur != "" && ip_cur != "0.0.0.0" // �ų�����ƥ���ַ
						&& !ip_cur.substring(0, 3).equals("127")// != 0 //�ų����ص�ַ
						&& !ip_cur.substring(0, 5).equals("0.255")// �ų��鲥��ַ
				) {
					if (i.getSecond().isEmpty()) {
						continue;
					}
					Pair<String, String> ipm_tmp = new Pair<String, String>(
							ip_cur, i.getSecond());
					if (!ipcm_result.contains(ipm_tmp)) {
						ipcm_result.add(ipm_tmp);
					}
				}
			}
		}
	}

	public void getOidIndex(Map<String, String> oidIndexList, String sysOid) {
		if (sysOid.split("\\.").length < 7)
			return;
		if (special_oid_list.containsKey(sysOid)) {
			oidIndexList = special_oid_list.get(sysOid);
		} else {
			getOidIndex(oidIndexList,
					sysOid.substring(0, sysOid.lastIndexOf(".")));
		}
	}

	public void getOneSysInfo(SnmpPara spr) {
		if (testIP(spr)) {
			IDBody devid = getOneSysInfo_NJ(spr);
			if ("1".equals(devid.getSnmpflag()))
				addDevID(spr, devid);
		}
	}

	public void addDevID(SnmpPara spr, IDBody devid) {
		if (Utils.isEmptyOrBlank(devid.getSysOid())) {
			return;
		}
		if (!devid.getIps().contains(spr.getIp())) {// ����vrrp�������豸
			return;
		}
		Set<String> set = CommonDef.DEVID_LIST.keySet();
		synchronized (lock) {
			for (String key : set) {
				IDBody body = CommonDef.DEVID_LIST.get(key);
				if (devid.getIps().containsAll(body.getIps())
						&& body.getIps().containsAll(devid.getIps())) {
					return;
				}
			}
			Pair<String, IDBody> devid_cur = new Pair<String, IDBody>(
					spr.getIp(), devid);
			devid_list_valid.put(spr.getIp(), devid);
			devid_list_visited.put(spr.getIp(), devid);
			ip_visited_list.add(spr.getIp());//devid.getIps().get(0));
			// ���������豸����ӵ�sproid_list�б���
			if (devid.getDevType().equals("0")
					|| devid.getDevType().equals("1")
					|| devid.getDevType().equals("2")
					|| devid.getDevType().equals("3")) {
				sproid_list.add(new Pair<SnmpPara, Pair<String, String>>(spr,
						new Pair<String, String>(devid.getDevType(), devid
								.getSysOid())));
			}
		}
		String msg = spr.getIp() + ", " + map_devType.get(devid.getDevType())
				+ ", " + devid.getDevFactory();
		System.out.println(msg);
	}

	boolean testIP(SnmpPara spr) {
		if (!ip_visited_list.contains(spr.getIp()))
			return false;
		return true;
	}
    CountDownLatch latchgetSysinfo = null;

	public boolean getSysInfos(Vector<SnmpPara> spr_list) {
		devid_list_valid.clear();
		sproid_list.clear();
		if (spr_list.size() > 0) {
			if (spr_list.size() == 1) {
				SnmpPara para = spr_list.get(0);
				if (isNewIp(para.getIp())) {
					getOneSysInfo(spr_list.get(0));
				}
			} else {
				latchgetSysinfo = new CountDownLatch(spr_list.size());

				for (final SnmpPara sp : spr_list) {
					if (isNewIp(sp.getIp())) {
						if (isStop) {
							return false;
						} else {
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
	/**���*/
	public void getOneArpData(SnmpPara spr, Map<String, List<Pair<String,String>>> inf_ipmacs){
		IPNetToMediaTableScan arpTabScan = new IPNetToMediaTableScan();
		MibScan snmp = new MibScan();
		List<Pair<String,String> > ipInfInx = snmp.getMibTable(spr, "1.3.6.1.2.1.4.22.1.1");
		if(!ipInfInx.isEmpty()){
			 List<Pair<String,String>> i_pm = new ArrayList<Pair<String,String>>();
			 //IP-MAC��ַ1.3.6.1.2.1.4.22.1.2
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
						 }else{//�µĽӿ�
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

	public IDBody getOneSysInfo_NJ(SnmpPara spr) {
		MibScan snmp = new MibScan();
		IDBody devid = new IDBody();
		List<Pair<String, String>> sysInfos = new ArrayList<Pair<String, String>>();
		if (spr.getSnmpver().equals("2")) {
			sysInfos = snmp.getMibTable(SnmpConstants.version2c, spr,
					"1.3.6.1.2.1.1");
			if (sysInfos == null || sysInfos.isEmpty()) {
				sysInfos = snmp.getMibTable(SnmpConstants.version1, spr,
						"1.3.6.1.2.1.1");
				if (sysInfos != null && !sysInfos.isEmpty()) {
					spr.setSnmpver("1");
				}
			}
		} else if (spr.getSnmpver().equals("1")) {
			sysInfos = snmp.getMibTable(SnmpConstants.version1, spr,
					"1.3.6.1.2.1.1");

			if (sysInfos == null || sysInfos.isEmpty()) {
				sysInfos = snmp.getMibTable(SnmpConstants.version2c, spr,
						"1.3.6.1.2.1.1");
				if (sysInfos != null && !sysInfos.isEmpty()) {
					spr.setSnmpver("2");
				}
			}
		} else {
			sysInfos = snmp.getMibTable(spr, "1.3.6.1.2.1.1");
		}
		String sysOid = "";
		String sysSvcs = "";
		String sysName = "";
		if (sysInfos == null || sysInfos.isEmpty()) {
			// �޸�oid��ȡһ��
			if (spr.getSnmpver().equals("2")) {
				sysOid = snmp.getMibObject(SnmpConstants.version2c, spr,
						"1.3.6.1.2.1.1.2.0");
				if (sysOid != null && !sysOid.isEmpty()) {
					sysSvcs = snmp.getMibObject(SnmpConstants.version2c, spr,
							"1.3.6.1.2.1.1.7.0");
					sysName = snmp.getMibObject(SnmpConstants.version2c, spr,
							"1.3.6.1.2.1.1.5.0");
				} else {
					sysOid = snmp.getMibObject(SnmpConstants.version1, spr,
							"1.3.6.1.2.1.1.2.0");
					if (sysOid != null && !sysOid.isEmpty()) {
						sysSvcs = snmp.getMibObject(SnmpConstants.version1,
								spr, "1.3.6.1.2.1.1.7.0");
						sysName = snmp.getMibObject(SnmpConstants.version1,
								spr, "1.3.6.1.2.1.1.5.0");
						spr.setSnmpver("1");
					}
				}
			} else if (spr.getSnmpver().equals("1")) {
				sysOid = snmp.getMibObject(SnmpConstants.version1, spr,
						"1.3.6.1.2.1.1.2.0");
				if (sysOid != null) {
					sysSvcs = snmp.getMibObject(SnmpConstants.version1, spr,
							"1.3.6.1.2.1.1.7.0");
					sysName = snmp.getMibObject(SnmpConstants.version1, spr,
							"1.3.6.1.2.1.1.5.0");
				} else {
					sysOid = snmp.getMibObject(SnmpConstants.version2c, spr,
							"1.3.6.1.2.1.1.2.0");
					if (sysOid != null && !sysOid.isEmpty())// .isEmpty())
					{
						sysSvcs = snmp.getMibObject(SnmpConstants.version2c,
								spr, "1.3.6.1.2.1.1.7.0");
						sysName = snmp.getMibObject(SnmpConstants.version2c,
								spr, "1.3.6.1.2.1.1.5.0");
						spr.setSnmpver("2");
					}
				}
			} else {
				sysOid = snmp.getMibObject(spr, "1.3.6.1.2.1.1.2.0");
				if (sysOid != null && !sysOid.isEmpty()) {
					sysSvcs = snmp.getMibObject(spr, "1.3.6.1.2.1.1.7.0");
					sysName = snmp.getMibObject(spr, "1.3.6.1.2.1.1.5.0");
				}
			}

			if (Utils.isEmptyOrBlank(sysOid) && Utils.isEmptyOrBlank(sysSvcs)
					&& Utils.isEmptyOrBlank(sysName)) {
				return devid;
			}
		} else {
			for (Pair<String, String> i : sysInfos) {
				if (i.getFirst().equals("1.3.6.1.2.1.1.2.0")
						|| i.getFirst().equals("1.3.6.1.2.1.1.2")) {
					sysOid = i.getSecond();
				} else if (i.getFirst().equals("1.3.6.1.2.1.1.5.0")
						|| i.getFirst().equals("1.3.6.1.2.1.1.5")) {
					sysName = i.getSecond();
				} else if (i.getFirst().equals("1.3.6.1.2.1.1.7.0")
						|| i.getFirst().equals("1.3.6.1.2.1.1.7")) {
					sysSvcs = i.getSecond();
				}
			}
		}

		if (sysOid.equals("1.3.6.1.4.1.13742.1.1.1")) {// KVM ������Ƶ��
														// 1.3.6.1.4.1.13742.1.1.1
			devid.setSysOid("1.3.6.1.4.1.13742.1.1.1");
			devid.setDevType("6");
			devid.setDevTypeName("KVM");
		} else {
			if (Utils.isEmptyOrBlank(sysOid) && Utils.isEmptyOrBlank(sysSvcs)
					&& Utils.isEmptyOrBlank(sysName))
				return devid;
			if (Utils.isEmptyOrBlank(sysOid)) {
				sysOid = "00";
			}
			// IP-MASK��ַ1.3.6.1.2.1.4.20.1.3
			List<Pair<String, String>> ipmsks = snmp.getMibTable(spr,
					"1.3.6.1.2.1.4.20.1.3");// [ipDes, mask]
			// IP-InfInx 1.3.6.1.2.1.4.20.1.2
			List<Pair<String, String>> infinxs = snmp.getMibTable(spr,
					"1.3.6.1.2.1.4.20.1.2");// [ipDes, index]

			Map<String, String> map_type = new HashMap<String, String>();
			getDevTypeByOid(sysOid, sysSvcs, spr.getIp(), map_type);// ���map_type

			devid.setSysOid(sysOid);
			devid.setSnmpflag("1");
			devid.setCommunity_get(spr.getCommunity());
			devid.setDevType(map_type.get("devtype"));
			devid.setDevModel(map_type.get("model"));
			devid.setDevFactory(map_type.get("factory"));
			devid.setDevTypeName(map_type.get("typename"));
			devid.setSysSvcs(sysSvcs);
			devid.setSysName(sysName);

			if (!(ipmsks.size() == 0)
					&& !((ipmsks.size() == 1) && (ipmsks.get(0).getSecond()
							.substring(0, 1).equals("0")))) {
				for (Pair<String, String> ipmask : ipmsks) {
					if (ipmask.getFirst().length() < 22) {
						continue;
					}
					String ip_tmp = ipmask.getFirst().substring(21);
					if (!(ip_tmp.substring(0, 6).equals("0.0.0."))
							&& !(ip_tmp.substring(0, 3).equals("127"))
							&& !(ip_tmp.substring(0, 5).equals("0.255"))) {
						if (!devid.getIps().contains(ip_tmp)) {
							for (Pair<String, String> p : infinxs) {
								if (p.getFirst().length() > 21
										&& (p.getFirst().substring(21)
												.equals(ip_tmp))) {
									devid.getInfinxs().add(p.getSecond());
									devid.getIps().add(ip_tmp);
									devid.getMsks().add(ipmask.getSecond());
									break;
								}
							}
						}
					}
				}
			}
			// get base mac (ps:ֻ�Խ�������Ч)
			if (devid.getDevType().equals("0")
					|| devid.getDevType().equals("1")) {
				String mac_tmp = snmp.getMibObject(spr, "1.3.6.1.2.1.17.1.1.0");
				// noSuchObject, noSuchInstance, and endOfMibView
				if (mac_tmp != null && !mac_tmp.isEmpty()
						&& !mac_tmp.equals("Null")
						&& !mac_tmp.equals("endOfMibView")
						&& !mac_tmp.equals("noSuchObject")
						&& !mac_tmp.equals("noSuchInstance")) {
					String baseMac = "";
					mac_tmp = mac_tmp.replaceAll(":", "");
					if(mac_tmp.length() >=12){
						mac_tmp = mac_tmp.substring(0, 12);
					}
					baseMac = mac_tmp.toUpperCase();
					if ((!"".equals(baseMac))
							&& (!"000000000000".equals(baseMac))
							&& (!"FFFFFFFFFFFF".equals(baseMac))) {
						devid.getMacs().add(baseMac);
						devid.setBaseMac(baseMac);
					}
				}
			}
		}
		if (devid.getIps() == null || devid.getIps().size() == 0) {
			devid.getInfinxs().add("0");
			devid.getIps().add(spr.getIp());
			devid.getMsks().add("");
		}
		return devid;
	}

	// ��ȡ�豸����
	public void getDevTypeByOid(String sysOid, String sysSvcs, String ip,
			Map<String, String> map_res) {
		String devtype_res = "5";// default to host
		String factory_res = "";
		String model_res = "";
		String typeName_res = "";

		// DEVICE_TYPE_MAP {sysoid,<devType,devTypeName,devFac,devModel>}
		if (OIDTypeUtils.getInstance().containsKey(sysOid)) {
			devtype_res = OIDTypeUtils.getInstance().getDevicePro(sysOid)
					.getDevType();
			factory_res = OIDTypeUtils.getInstance().getDevicePro(sysOid)
					.getDevFac();
			model_res = OIDTypeUtils.getInstance().getDevicePro(sysOid)
					.getDevModel();
			typeName_res = OIDTypeUtils.getInstance().getDevicePro(sysOid)
					.getDevTypeName();
		} else {
			System.out.println(sysOid);
			if (sysOid.substring(0, 16).equals("1.3.6.1.4.1.311."))// enterprises�ڵ�:1.3.6.1.4.1
			{
				devtype_res = "5";// host
			} else if (sysOid.substring(0, 17).equals("1.3.6.1.4.1.8072.")) {
				devtype_res = "4";// "SERVER"
			} else if (sysOid.substring(0, 21).equals("1.3.6.1.4.1.6486.800.")) {
				devtype_res = "0";// ROUTE-SWITCH
			} else if (sysOid.equals("1.3.6.1.4.1.13742.1.1.1")) {// KVM ������Ƶ��
																	// 1.3.6.1.4.1.13742.1.1.1
				devtype_res = "6"; // Other device
			} else {//
				int isvc = Integer.parseInt(sysSvcs);
				if (isvc == 0) {
					devtype_res = "5";// host
				} else if ((isvc & 6) == 6) {
					devtype_res = "0"; // "ROUTE-SWITCH"
				} else if ((isvc & 4) == 4) {
					devtype_res = "2"; // ROUTER
				} else if ((isvc & 2) == 2) {
					devtype_res = "1"; // "SWITCH"
				} else {
					devtype_res = "5"; // host
					// SvLog::writeLog("Can't identify oid=" + sysOid +
				}
			}
		}

		if (devtype_res == "5") {
			devtype_res = "4"; // ������SNMP��PC��������δ��ʶ����豸����ΪSERVER
		}
		map_res.put("devtype", devtype_res);// ["devtype"] = devtype_res;
		map_res.put("factory", factory_res);// ["factory"] = factory_res;
		map_res.put("model", model_res);// ["model"] = model_res;
		map_res.put("typename", typeName_res);// ["typename"] = typeName_res;
		System.out.print("ip = " + ip + " sysoid = " + sysOid + "\t");
		System.out.println("devtype = " + devtype_res + "factory = "
				+ factory_res + " model = " + model_res + " typename"
				+ typeName_res);
	}
	
}
