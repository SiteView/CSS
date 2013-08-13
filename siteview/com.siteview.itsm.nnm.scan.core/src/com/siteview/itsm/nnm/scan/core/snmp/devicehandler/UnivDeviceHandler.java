package com.siteview.itsm.nnm.scan.core.snmp.devicehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


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
import com.siteview.itsm.nnm.scan.core.snmp.util.Utils;

public class UnivDeviceHandler implements IDeviceHandler {
	protected Map<String, List<Pair<String, String>>> inf_macs = new ConcurrentHashMap<String, List<Pair<String, String>>>();
	protected Map<String, List<String>> port_macs = new ConcurrentHashMap<String, List<String>>(); // {port,[mac]}
	protected Map<String, Map<String, List<String>>> aft_list = new ConcurrentHashMap<String, Map<String, List<String>>>();
	protected Map<String, Map<String, List<RouteItem>>> route_list = new ConcurrentHashMap<String, Map<String, List<RouteItem>>>();
	protected Map<String, Map<String, List<Pair<String, String>>>> arp_list = new ConcurrentHashMap<String, Map<String, List<Pair<String, String>>>>();
	protected Map<String, RouterStandbyItem> routeStandby_list = new ConcurrentHashMap<String, RouterStandbyItem>();
	protected Map<String,List<String>> stp_list = new ConcurrentHashMap<String,List<String>>();
	Map<String, List<Directitem>> directData_list = new HashMap<String, List<Directitem>>();
	
	public boolean getAftByDtp(MibScan snmp, SnmpPara spr,
			Map<String, String> oidIndexList) {
		// //�˿�״̬ 1.3.6.1.2.1.17.4.3.1.3
		// PORT-MAC 1.3.6.1.2.1.17.4.3.1.2
		List<Pair<String, String>> portMacs = new ArrayList<Pair<String, String>>();
		portMacs = snmp.getMibTable(spr, "1.3.6.1.2.1.17.4.3.1.2");

		if (portMacs.isEmpty() && (oidIndexList.containsKey(AFT_MAC_DTP_MACRO))
				&& (oidIndexList.containsKey(AFT_MAC_DTP_PORT))) {
			List<Pair<String, String>> ports = snmp.getMibTable(spr,
					oidIndexList.get(AFT_MAC_DTP_PORT));
			List<Pair<String, String>> macs = snmp.getMibTable(spr,
					oidIndexList.get(AFT_MAC_DTP_MACRO));
			int oidLength = oidIndexList.get(AFT_MAC_DTP_PORT).length();
			List<String> i_pm = new ArrayList<String>();

			if (ports.isEmpty() || macs.isEmpty()) {
				return false;
			}

			for (Pair<String, String> i : ports) {
				for (Pair<String, String> j : macs) {
					if (i.getFirst().substring(oidLength)
							.equals(j.getFirst().substring(oidLength)))
					{
						String aftport = i.getSecond();
						String aftmac = j.getSecond().replaceAll(":", "");
						if (!aftport.startsWith("eth"))
						{
							aftport = i.getSecond().substring(3);
						}
						i_pm = port_macs.get(aftport);
						if (port_macs.containsKey(aftport)) {
							if (!i_pm.contains(aftmac))
							{
								port_macs.get(aftport).add(aftmac);
							}
						} else {
							List<String> maclist = new ArrayList<String>();
							maclist.add(aftmac);
							port_macs.put(aftport, maclist);
						}
						break;
					}
				}
			}
		} else if (portMacs.isEmpty()
				&& oidIndexList.containsKey(AFT_MAC_DTP_PORT))
		{
			List<String> i_pm = new ArrayList<String>();
			List<Pair<String, String>> ports = snmp.getMibTable(spr,
					oidIndexList.get(AFT_MAC_DTP_PORT));
			if (ports.isEmpty())
				return false;
			for (Pair<String, String> i : ports) {
				int oidLength = oidIndexList.get(AFT_MAC_DTP_PORT).length();
				String[] v1 = i.getFirst().substring(oidLength).split("\\.");
				String mac_str = "";
				// ��ֹ���
				if (v1.length < 6) {
					continue;
				}
				mac_str = Utils.parseToHexString(v1[0])
						+ Utils.parseToHexString(v1[1])
						+ Utils.parseToHexString(v1[2])
						+ Utils.parseToHexString(v1[3])
						+ Utils.parseToHexString(v1[4])
						+ Utils.parseToHexString(v1[5]);
				i_pm = port_macs.get(i.getSecond());// ->second);
				if (i_pm != null) {// ���ڸö˿�
					if (!i_pm.contains(mac_str)) {// �����ڸ�mac
						i_pm.add(mac_str);
					}
				} else {// �����ڸö˿�
					List<String> maclist = new ArrayList<String>();
					maclist.add(mac_str);
					port_macs.put(i.getSecond(), maclist);
				}
			}
		} else {
			if (portMacs.isEmpty()) {
				return false;
			}
			List<String> i_pm = new ArrayList<String>();

			// ��Щ�豸����ֱ��ȡ�˿ڣ����ܿ���ȡoid
			Pair<String, String> oneItem = portMacs.get(0);
			String[] v0 = oneItem.getFirst().split("\\.");
			if (v0.length > 18) {
				List<Pair<String, String>> Macs = snmp.getMibTable(spr,
						"1.3.6.1.2.1.17.4.3.1.1");
				for (Pair<String, String> i : portMacs)
				{
					for (Pair<String, String> j : Macs)
					{
						if (j.getSecond().length() < 11)
							continue;
						if (i.getFirst().substring(22)
								.equals(j.getFirst().substring(22))) {
							i_pm = port_macs.get(i.getSecond());// .find(i->second);
							if (i_pm != null)// port_macs.end())
							{
								if (!i_pm.contains(j.getSecond())) {
									port_macs.get(i.getSecond()).add(
											j.getSecond());
								}
							} else {
								List<String> maclist = new ArrayList<String>();
								maclist.add(j.getSecond());
								port_macs.put(i.getSecond(), maclist);
							}
							break;
						}
					}
				}
			} else {
				for (Pair<String, String> imac : portMacs) {
					if (imac.getFirst().length() <= 22)
						continue;
					String[] v1 = imac.getFirst().substring(23).split("\\.");
					String mac_str = "";

					// ��ֹ���
					if (v1.length < 6) {
						continue;
					}
					mac_str = Utils.parseToHexString(v1[0])
							+ Utils.parseToHexString(v1[1])
							+ Utils.parseToHexString(v1[2])
							+ Utils.parseToHexString(v1[3])
							+ Utils.parseToHexString(v1[4])
							+ Utils.parseToHexString(v1[5]);
					i_pm = port_macs.get(imac.getSecond());
					if (i_pm != null) {// ���ڸö˿�
						if (!(i_pm.contains(mac_str))) {// �����ڸ�mac
							port_macs.get(imac.getSecond()).add(mac_str);
						}
					} else {// �����ڸö˿�
						List<String> maclist = new ArrayList<String>();
						maclist.add(mac_str);
						port_macs.put(imac.getSecond(), maclist);
					}
				}
			}
		}
		return true;
	}

	@Override
	public Map<String, List<Directitem>> getDirectData(MibScan snmp,
			SnmpPara spr) {
		return directData_list;
	}
	public void getAftByQtp(MibScan snmp , SnmpPara spr, Map<String,String> oidIndexList){
		//�˿�״̬ 1.3.6.1.2.1.17.7.1.2.2.1.3(mac,sta)
		//�˿�MAC 1.3.6.1.2.1.17.7.1.2.2.1.2(mac,port)
		List<Pair<String,String>> portMacs = snmp.getMibTable(spr, "1.3.6.1.2.1.17.7.1.2.2.1.2");
		if(portMacs.isEmpty() && (oidIndexList.containsKey(AFT_MAC_QTP_MACRO))){
			portMacs = snmp.getMibTable(spr, oidIndexList.get(AFT_MAC_DTP_MACRO));
		}
		if(!portMacs.isEmpty()){
			List<String> i_pm = new ArrayList<String>();
			for(Pair<String,String> imac : portMacs){
				String mac_str = "";
				if(imac.getFirst().length() <= 28){
					continue;
				}
				String[] v1 = imac.getFirst().split("\\.");
				if(v1.length < 6){
					continue;
				}
				mac_str = Utils.parseToHexString(v1[0])
						+ Utils.parseToHexString(v1[1])
						+ Utils.parseToHexString(v1[2])
						+ Utils.parseToHexString(v1[3])
						+ Utils.parseToHexString(v1[4])
						+ Utils.parseToHexString(v1[5]);
				String mac = mac_str;
				i_pm = port_macs.get(imac.getSecond());
				if(i_pm!=null && !i_pm.isEmpty()){
					if(!i_pm.contains(mac)){
						i_pm.add(mac);
					}
				}else{
					List<String> maclist = new ArrayList<String>();
					maclist.add(mac);
					port_macs.put(imac.getSecond(), maclist);
				}
			}
		}
	}
	@Override
	public Map<String, Map<String, List<String>>> getAftData(MibScan snmp,
			SnmpPara spr, Map<String, String> oidIndexList) {
		aft_list.clear();
		port_macs.clear();
		//1.3.6.1.2.1.17.4.3.1.2
		getAftByDtp(snmp,spr,oidIndexList);
		//1.3.6.1.2.1.17.7.1.2.2.1.2
		getAftByQtp(snmp, spr, oidIndexList);
		if(!port_macs.isEmpty()){
			aft_list.put(spr.getIp(), port_macs);
			for(Entry<String, List<String>> iter : port_macs.entrySet()){
				System.out.println("portmacs key  is -------" + iter.getKey());
			}
		}
		return aft_list;
	}

	@Override
	public Map<String, Map<String, List<Pair<String, String>>>> getArpData(
			MibScan snmp, SnmpPara spr, Map<String, String> oidIndexList) {
		// ��־
		arp_list.clear();
		inf_macs.clear();
		// IP-�˿�����1.3.6.1.2.1.4.22.1.1

		boolean isSpecial = false;
		List<Pair<String, String>> i_pm = new ArrayList<Pair<String, String>>();
		// IP-MAC��ַ1.3.6.1.2.1.4.22.1.2
		List<Pair<String, String>> ipMacs = new ArrayList<Pair<String, String>>();
		if (isSpecial && oidIndexList.containsKey(ARP_MAC_MACRO))
		{
			ipMacs = snmp.getMibTable(spr, oidIndexList.get(ARP_MAC_MACRO));
		} else {
			ipMacs = snmp.getMibTable(spr, "1.3.6.1.2.1.4.22.1.2");
		}
		for (Pair<String, String> imac : ipMacs) {
			if (imac.getFirst().length() <= 20)
			{
				continue;
			}
			String v1[] = imac.getFirst().substring(21).split("\\.");
			int len = v1.length;
			if (len >= 4) {
				String ip_tmp = v1[len - 4] + "." + v1[len - 3] + "."
						+ v1[len - 2] + "." + v1[len - 1];
				String mac_tmp = imac.getSecond().replaceAll(":", "");
						
				if(mac_tmp.length()>12){
					mac_tmp = mac_tmp.substring(0, 12);
				}
				if (v1[len - 1].equals("0") || v1[len - 1].equals("255")) {
					continue;
				}
				String inf_tem = v1[len - 5];
				i_pm = inf_macs.get(inf_tem);
				if (i_pm != null) {// ���ڸýӿ�
					boolean bExisted = false;
					for (Pair<String, String> j : i_pm) {
						if (j.getSecond().equals(mac_tmp))
						{
							bExisted = true;
							break;
						}
					}
					if (!bExisted) {// �ýӿڵ�ARP�в����ڸ�MAC
						i_pm.add(new Pair<String,String>(ip_tmp,mac_tmp));
					}
				} else {// �µĽӿ�
					List<Pair<String, String>> ipmac_list = new ArrayList<Pair<String, String>>();
					ipmac_list.add(new Pair<String, String>(ip_tmp, mac_tmp));
					inf_macs.put(inf_tem, ipmac_list);
				}
			}
		}
		if (!inf_macs.isEmpty())
		{
			arp_list.put(spr.getIp(), inf_macs);
		}
		return arp_list;
	}

	

	@Override
	public Map<String, Map<String, List<RouteItem>>> getRouteData(MibScan snmp,
			SnmpPara spr, Map<String, String> oidIndexList) {
		route_list.clear();
		//Ŀ������Ľӿ�����(oid,infinx) 1.3.6.1.2.1.4.21.1.2
	        List<Pair<String,String> > dstNetInfInx = snmp.getMibTable(spr, "1.3.6.1.2.1.4.21.1.2");
	        List<Pair<String,String> > dstNetNextHopIp = new ArrayList<Pair<String,String>>();
	        List<Pair<String,String> > dstStates = new ArrayList<Pair<String,String>>();
	        List<Pair<String,String> > dstMasks = new ArrayList<Pair<String,String>>();

		boolean isSpecial = false;
		if(dstNetInfInx.isEmpty() && (oidIndexList.containsKey(ROUTE_INFINDEX_MACRO)))
		{
			dstNetInfInx = snmp.getMibTable(spr, oidIndexList.get(ROUTE_INFINDEX_MACRO)); 
			isSpecial = true;
		}

		if(!dstNetInfInx.isEmpty())
		{
			//Ŀ���������һ��IP(oid,nbrip) 1.3.6.1.2.1.4.21.1.7
			if(isSpecial && (oidIndexList.containsKey(ROUTE_NEXTHOPIP_MACRO)))
				dstNetNextHopIp = snmp.getMibTable(spr, oidIndexList.get(ROUTE_NEXTHOPIP_MACRO)); 
			else
				dstNetNextHopIp = snmp.getMibTable(spr,"1.3.6.1.2.1.4.21.1.7");
			if(!dstNetNextHopIp.isEmpty())
			{
				//����һ������(oid,state) 1.3.6.1.2.1.4.21.1.8
				if(isSpecial && (oidIndexList.containsKey(ROUTE_ROUTETYPE_MACRO)))
					dstNetNextHopIp = snmp.getMibTable(spr, oidIndexList.get(ROUTE_ROUTETYPE_MACRO)); 
				else
					dstStates = snmp.getMibTable(spr,"1.3.6.1.2.1.4.21.1.8");
				if(!dstStates.isEmpty())
				{
					//Ŀ������mask(oid,mask) 1.3.6.1.2.1.4.21.1.11
					if(isSpecial && (oidIndexList.containsKey(ROUTE_ROUTEMASK_MACRO)))
						dstNetNextHopIp = snmp.getMibTable(spr, oidIndexList.get(ROUTE_ROUTEMASK_MACRO)); 
					else
						dstMasks  = snmp.getMibTable(spr, "1.3.6.1.2.1.4.21.1.11");
					if(!dstMasks.isEmpty())
					{
	                      List<Pair<String, RouteItem>> ipinx_list = new ArrayList<Pair<String,RouteItem>>();
//							++i)
	                    for(Pair<String, String> i : dstNetInfInx)
						{
							if (i.getFirst().length()<=21)
							{
								continue;
							}
							String[] v1 = i.getFirst().substring(21).split("\\.");
							int len = v1.length;
							if(len >= 4)
							{
								String ip_dest_i = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
								String next_hop = "";
								String dest_msk = "";
								for(Pair<String, String> j :dstNetNextHopIp)
								{
									v1 = j.getFirst().substring(21).split("\\.");
									len = v1.length;
									if(len >= 4)
									{
										String ip_dest_j = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
										if(!(ip_dest_j.equals(spr.getIp())) && ip_dest_j.equals(ip_dest_i))
										{
											for(Pair<String, String> k : dstStates)
											{
												v1 = k.getFirst().substring(21).split("\\.");
												len = v1.length;
												if(len >= 4)
												{
													String ip_dest_k = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
													if(ip_dest_k.equals(ip_dest_i) && ((k.getSecond().equals("4"))||(k.getSecond().equals("1"))))	
													{
														next_hop = j.getSecond();
													}
												}
											}
										}
									}
								}
								for(Pair<String, String> j :dstMasks)
								{
									v1 = j.getFirst().substring(21).split("\\.");
									len = v1.length;
									if(len >= 4)
									{
										String ip_dest_j = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
										if(ip_dest_j.equals(spr.getIp()) && ip_dest_j.equals(ip_dest_i))
										{
											for(Pair<String, String> k : dstStates)
											{
												v1 = k.getFirst().substring(21).split("\\.");
												len = v1.length;//v1.size();
												if(len >= 4)
												{
													String ip_dest_k = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
													if(ip_dest_k.equals(ip_dest_j) && ((k.getSecond().equals("4"))||(k.getSecond().equals("1"))))
													{
														dest_msk = j.getSecond();
													}
												}
											}
										}
									}
								}
								if(!next_hop.isEmpty() && !dest_msk.isEmpty())
								{
									RouteItem rtitem = new RouteItem();
									rtitem.setDest_net(ip_dest_i);
									rtitem.setNext_hop(next_hop);
									rtitem.setDest_msk(dest_msk);
									ipinx_list.add(new Pair<String,RouteItem>(i.getSecond(),rtitem));//.push_back(make_pair(i->second, rtitem));
								}					
							}
						}

						if(!ipinx_list.isEmpty())
						{
							Map<String,List<RouteItem>> infinx_nbrips = new HashMap<String,List<RouteItem>>();//{infindex,[ROUTEITEM]}
							for(Pair<String,RouteItem> i : ipinx_list){
								String inx_tmp = i.getFirst();
								String dest_net = i.getSecond().getDest_net();
								String dest_msk = i.getSecond().getDest_msk();
								String next_hop = i.getSecond().getNext_hop();
								
								List<RouteItem> i_pm = new ArrayList<RouteItem>();
								i_pm = infinx_nbrips.get(inx_tmp);
								if(i_pm != null)
								{//���ڸýӿ�
									boolean bNewRec = true;
									for(RouteItem j : i_pm)
									{
										if(j.getDest_net().equals(dest_net) || j.getDest_msk().equals(dest_msk) || j.getNext_hop().equals(next_hop))
										{
											bNewRec = false;
											break;
										}
									}
									if(bNewRec)
									{
										i_pm.add(i.getSecond());
									}
								}
								else
								{//�µĽӿ�
									List<RouteItem> iplist_tmp = new ArrayList<RouteItem>();
									iplist_tmp.add(i.getSecond());
									infinx_nbrips.put(inx_tmp, iplist_tmp);
								}
							}
							if(!infinx_nbrips.isEmpty())
							{
//								route_list.insert(make_pair(spr.ip,infinx_nbrips));
								route_list.put(spr.getIp(), infinx_nbrips);
							}
						}
					}
				}
			}
		}
		return route_list;
	}

	@Override
	public List<Bgp> getBgpData(MibScan snmp, SnmpPara spr) {
	    //SvLog::writeLog("Start read " + spr.ip + " bgp by " + spr.community);
		List<Bgp> bgp_list = new ArrayList<Bgp>();
		//1.3.6.1.2.1.15.3.1.5 :bgpPeerLocalAddr
		List<Pair<String, String>> localips = snmp.getMibTable(spr, "1.3.6.1.2.1.15.3.1.5");
		if(!localips.isEmpty())
		{
			//1.3.6.1.2.1.15.3.1.6 :bgpPeerLocalPort
	        List<Pair<String,String> > localports = snmp.getMibTable(spr, "1.3.6.1.2.1.15.3.1.6");
			//1.3.6.1.2.1.15.3.1.8 :bgpPeerRemotePort
	        List<Pair<String,String> > peerports  = snmp.getMibTable(spr, "1.3.6.1.2.1.15.3.1.8");
	         for(Pair<String, String> i : localips)
			{
	        	String v1[] = i.getFirst().split("\\.");
				int len = v1.length;
				if(len > 4)
				{
					String ip_dest_i = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
					if(ip_dest_i == "0.0.0.0") continue;
					String local_ip = i.getSecond();
					String local_port = "";
					String peer_port = "";
					for(Pair<String,String> j : localports)
					{
						v1 = j.getFirst().split("\\.");
						len = v1.length;
						if(len > 4)
						{
							String ip_dest_j = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
							if(!(ip_dest_j.equals(spr.getIp())) || (ip_dest_i.equals(ip_dest_j)))
							{
								for(Pair<String,String> k : peerports)
								{
									v1 = k.getFirst().split("\\.");
									len = v1.length;//.size();
									if(len > 4)
									{
										String ip_dest_k = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
										if(ip_dest_k == ip_dest_i)
										{
											local_port = j.getSecond();
											peer_port = k.getSecond();

											boolean bNew = true;
											for(Bgp m : bgp_list)
											{
												if(	    (
														(m.getLocal_ip().equals(spr.getIp())) 
														&& (m.getLocal_port().equals(local_port)) 
														&& (m.getPeer_ip().equals(ip_dest_i))
														&& (m.getPeer_port().equals(peer_port))
														)
													||
														(
														   (m.getPeer_ip().equals(spr.getIp()))
														&& (m.getPeer_port().equals(local_port))
														&& (m.getLocal_ip().equals(ip_dest_i))
														&& (m.getLocal_port().equals(peer_port))
														))
												{
													bNew = false;
													break;
												}
											}									
											if (bNew)
											{
												Bgp bgpitem = new Bgp();
												bgpitem.setPeer_ip(ip_dest_i);
												bgpitem.setPeer_port(peer_port);
												bgpitem.setLocal_ip(local_ip);
												bgpitem.setLocal_port(local_port);
												bgp_list.add(bgpitem);										
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return bgp_list;
	}

	@Override
	public Map<String, Pair<String, List<IfRec>>> getInfProp(MibScan snmp,
			SnmpPara spr, Map<String, String> oidIndexList, boolean bRouter) {
		ifprop_List.clear();
		// �ӿ�����
		String infAmount = snmp.getMibObject(spr, "1.3.6.1.2.1.2.1.0");
		// �ӿ����� 1.3.6.1.2.1.2.2.1.3
		List<Pair<String, String>> infTypes = snmp.getMibTable(spr,
				"1.3.6.1.2.1.2.2.1.3");
		int length = 23;
		// �ӿ����� 1.3.6.1.2.1.2.2.1.2
		List<Pair<String, String>> infDescs = snmp.getMibTable(spr,
				"1.3.6.1.2.1.2.2.1.2");
		// �˿ڽӿڶ�Ӧ��ϵ1.3.6.1.2.1.17.1.4.1.2(����Ϊ��)
		List<Pair<String, String>> infPorts = new ArrayList<Pair<String, String>>();
		if (!bRouter) {
			if(oidIndexList.containsKey(INF_INDEXPORT_MACRO)){
				infPorts = snmp.getMibTable(spr, oidIndexList.get(INF_INDEXPORT_MACRO));
				length = oidIndexList.get(INF_INDEXPORT_MACRO).length() + 1;
			}else{
				infPorts= snmp.getMibTable(spr,"1.3.6.1.2.1.17.1.4.1.2");
			}
		}
		//�ӿ�MAC��ַ 1.3.6.1.2.1.2.2.1.6 (ֻҪMac��Ϊ��)
		List<Pair<String,String>> infMacs = snmp.getMibTable(spr,"1.3.6.1.2.1.2.2.1.6");
		//�ӿ��ٶ� 1.3.6.1.2.1.2.2.1.5
		List<Pair<String,String>> infSpeeds = snmp.getMibTable(spr,"1.3.6.1.2.1.2.2.1.5");
		List<IfRec> infprops = new ArrayList<IfRec>();//(ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)
		//���Ӷ�infAmountΪ�գ����ӿ�Mac��ַ���յĴ���
		if(infTypes.isEmpty()){
			for(Pair<String,String> imac : infMacs){
				IfRec inf_tmp = new IfRec();
				if(imac.getFirst().length() < 21){
					continue;
				}
				inf_tmp.setIfIndex(imac.getFirst().substring(20));
				inf_tmp.setIfType("");
				inf_tmp.setIfMac(imac.getSecond().replaceAll(":", "").substring(0,12).toUpperCase());
				for(Pair<String,String> idesc : infDescs){
					if(idesc.getFirst().length() > 20 && idesc.getFirst().substring(20).equals(inf_tmp.getIfIndex())){
						inf_tmp.setIfDesc(idesc.getSecond());
						break;
					}
				}
				inf_tmp.setIfPort(inf_tmp.getIfIndex());
				for(Pair<String,String> iport : infPorts){
					if(iport.getSecond().equals(inf_tmp.getIfIndex())){
						inf_tmp.setIfPort(iport.getFirst().substring(23));
						break;
					}
				}
				for(Pair<String,String> ispeed : infSpeeds){
					if(ispeed.getFirst().length() > 20 && (ispeed.getFirst().substring(20).equals(inf_tmp.getIfIndex()))){
						inf_tmp.setIfSpeed(ispeed.getSecond());
						break;
					}
				}
				infprops.add(inf_tmp);
			}
		}else{
			for(Pair<String,String> itype : infTypes){
				IfRec inf_tmp = new IfRec();
				if(itype.getFirst().length() < 21) {
					continue;
				}
				inf_tmp.setIfIndex(itype.getFirst().substring(20));
				inf_tmp.setIfType(itype.getSecond());
				for(Pair<String,String> idesc : infDescs){
					if(idesc.getFirst().length() > 20 && idesc.getFirst().substring(20).equals(inf_tmp.getIfIndex())){
						inf_tmp.setIfDesc(idesc.getSecond());
						break;
					}
				}
			    inf_tmp.setIfPort(inf_tmp.getIfIndex());
			    for(Pair<String,String> iport : infPorts){
			    	if(iport.getFirst().length() > length && iport.getSecond().equals(inf_tmp.getIfIndex())){
			    		inf_tmp.setIfPort(iport.getFirst().substring(length));
			    		break;
			    	}
			    }
			    for(Pair<String,String> imac : infMacs){
			    	if(imac.getFirst().length() > 20 && imac.getFirst().substring(20).equals(inf_tmp.getIfIndex())){
			    		inf_tmp.setIfMac(imac.getSecond().replaceAll(":", "").substring(0,12));
			    		break;
			    	}
			    }
			    for(Pair<String,String> ispeed : infSpeeds){
			    	if(ispeed.getFirst().length() > 20 && ispeed.getFirst().substring(20).equals(inf_tmp.getIfIndex())){
			    		inf_tmp.setIfSpeed(ispeed.getSecond());
			    		break;
			    	}
			    }
			    infprops.add(inf_tmp);
			}
		}
		if(!infprops.isEmpty()){
			ifprop_List.put(spr.getIp(), new Pair<String, List<IfRec>>(infAmount, infprops));
		}
		return ifprop_List;
	}

	@Override
	public void getInfFlow(MibScan snmp, SnmpPara spr) {
	}

	@Override
	public Map<String, RouterStandbyItem> getVrrpData(MibScan snmp, SnmpPara spr) {
		// SvLog::writeLog("Start read " + spr.ip + " vrrp by " +
		// spr.community);
		routeStandby_list.clear();
		// 1.3.6.1.2.1.68.1.3.1.2 :vrrpOperVirtualMacAddr
		List<Pair<String, String>> vrmacs = snmp.getMibTable(spr,
				"1.3.6.1.2.1.68.1.3.1.2");
		if (!vrmacs.isEmpty()) {
			// 1.3.6.1.2.1.68.1.4.1.2 :vrrpAssoIpAddrRowStatus
			List<Pair<String, String>> vrassoipstatus = snmp.getMibTable(spr,
					"1.3.6.1.2.1.68.1.4.1.2");
			RouterStandbyItem vrrpItem = new RouterStandbyItem();
			// �������MAC
			for (Pair<String, String> i : vrmacs) {
				String mac = i.getSecond().replaceAll(" ", "").substring(0, 12);
				vrrpItem.getVirtualMacs().add(mac);
			}
			// �������IP
			for (Pair<String, String> i : vrassoipstatus) {
				if (i.getFirst().length() <= 23) {
					continue;
				}
				String v1[] = i.getFirst().substring(23).split("\\.");
				int len = v1.length;
				if (len > 4) {
					String ip = v1[len - 4] + "." + v1[len - 3] + "."
							+ v1[len - 2] + "." + v1[len - 1];
					vrrpItem.getVirtualIps().add(ip);
				}

			}

			routeStandby_list.put(spr.getIp(), vrrpItem);
		} else {
			getHsrpData(snmp, spr);
		}

		return routeStandby_list;
	}
	public Map<String, RouterStandbyItem> getHsrpData(MibScan snmp, SnmpPara spr){
		return routeStandby_list;
	}

	@Override
	public Map<String, List<String>> getStpData(MibScan snmp, SnmpPara spr) {
		return stp_list;
	}
	// ��ȡ�ض��豸��OSPF�ھӱ������
	public Map<String, Map<String, List<String>>> getOspfNbrData(MibScan snmp, SnmpPara spr){
		// SvLog::writeLog("Start read " + spr.ip + " nbr by " + spr.community);
		Map<String, Map<String, List<String>>> ospfnbr_list = new ConcurrentHashMap<String, Map<String, List<String>>>();
		// �ھ�IP���ڽӿ�����(oid,infinx) 1.3.6.1.2.1.14.10.1.2
		List<Pair<String, String>> ipInfInx = snmp.getMibTable(spr,
				"1.3.6.1.2.1.14.10.1.2");
		if (!ipInfInx.isEmpty()) {
			Map<String, List<String>> infinx_nbrips = new HashMap<String, List<String>>();// {infindex,[nbrip]}
			for (Pair<String, String> iInf : ipInfInx) {
				if (iInf.getFirst().length() <= 22) {
					continue;
				}
				String v1[] = iInf.getFirst().substring(22).split(".");
				int len = v1.length;
				if (len >= 5) {
					String ip_tmp = v1[len - 5] + "." + v1[len - 4] + "."
							+ v1[len - 3] + "." + v1[len - 2];
					List<String> i_pm;
					i_pm = infinx_nbrips.get(iInf.getSecond());
					if (i_pm != null) {// ���ڸýӿ�
						if (!i_pm.contains(ip_tmp)) {// �ýӿڵ�ARP�в����ڸ�MAC
							i_pm.add(ip_tmp);
						}
					} else { // �µĽӿ�
						List<String> iplist_tmp = new ArrayList<String>();
						iplist_tmp.add(ip_tmp);
						infinx_nbrips.put(iInf.getSecond(), iplist_tmp);
					}
				}
			}
			if (!infinx_nbrips.isEmpty()) {
				ospfnbr_list.put(spr.getIp(), infinx_nbrips);
			}
		}
		return ospfnbr_list;
	}
	public static void main(String[] args) {
		String oid = "1.3.6.1.2.1.17.4.3.1.2.0.19.32.125.202.170";
		System.out.println(oid.substring(22).split("\\.").length);
	}

	/**
	 * 获取设备cpu使用信息
	 */
	@Override
	public DeviceCpuInfo getDeviceCpuInfo(MibScan snmp, SnmpPara spr) {
		return null;
	}

	@Override
	public DeviceMemInfo getdeviceMemInfo(MibScan snmp, SnmpPara spr) {
		return null;
	}

}
