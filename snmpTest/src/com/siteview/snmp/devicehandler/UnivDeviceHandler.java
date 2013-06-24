package com.siteview.snmp.devicehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Bgp;
import com.siteview.snmp.pojo.Directitem;
import com.siteview.snmp.pojo.IfRec;
import com.siteview.snmp.pojo.RouteItem;
import com.siteview.snmp.pojo.RouterStandbyItem;
import com.siteview.snmp.scan.MibScan;

public class UnivDeviceHandler implements IDeviceHandler {
	protected Map<String, List<Pair<String, String>>> inf_macs = new ConcurrentHashMap<String, List<Pair<String, String>>>();
	protected Map<String, List<String>> port_macs = new ConcurrentHashMap<String, List<String>>(); // {port,[mac]}
	protected Map<String, Map<String, List<String>>> aft_list = new ConcurrentHashMap<String, Map<String, List<String>>>();
	protected Map<String, Map<String, List<RouteItem>>> route_list = new ConcurrentHashMap<String, Map<String, List<RouteItem>>>();
	protected Map<String, Map<String, List<Pair<String, String>>>> arp_list = new ConcurrentHashMap<String, Map<String, List<Pair<String, String>>>>();
	protected Map<String, RouterStandbyItem> routeStandby_list = new ConcurrentHashMap<String, RouterStandbyItem>();
	public boolean getAftByDtp(MibScan snmp, SnmpPara spr,
			Map<String, String> oidIndexList) {
		// SvLog::writeLog("Start read " + spr.ip + " aft by " + spr.community);
		// //端口状态 1.3.6.1.2.1.17.4.3.1.3
		// PORT-MAC 1.3.6.1.2.1.17.4.3.1.2
		List<Pair<String, String>> portMacs = new ArrayList<Pair<String, String>>();
		// qDebug() << " test source ip: " << spr.ip.c_str() <<
		// " portMacs size : " << portMacs.size();
		// qDebug() << "community : " << spr.community.c_str() <<
		// " snmpversion " << spr.snmpver.c_str();
		portMacs = snmp.getMibTable(spr, "1.3.6.1.2.1.17.4.3.1.2");
		// qDebug() << "source ip: " << spr.ip.c_str() << " portMacs size : " <<
		// portMacs.size();

		if (portMacs.isEmpty() && (oidIndexList.containsKey(AFT_MAC_DTP_MACRO))
				&& (oidIndexList.containsKey(AFT_MAC_DTP_PORT))) {
			// qDebug() << "test12345";
			List<Pair<String, String>> ports = snmp.getMibTable(spr,
					oidIndexList.get(AFT_MAC_DTP_PORT));
			List<Pair<String, String>> macs = snmp.getMibTable(spr,
					oidIndexList.get(AFT_MAC_DTP_MACRO));
			int oidLength = oidIndexList.get(AFT_MAC_DTP_PORT).length();
			List<String> i_pm = new ArrayList<String>();
			// SvLog::writeLog("oidIndexList[AFT_MAC]:" +
			// oidIndexList[AFT_MAC_DTP_MACRO]);

			if (ports.isEmpty() || macs.isEmpty()) {
				return false;
			}

			for (Pair<String, String> i : ports) {
				for (Pair<String, String> j : macs) {
					// //SvLog::writeLog("i->first:"+i->first+"i->second"+i->second+"j->first"+j->first+"j->second"+j->second);
					if (i.getFirst().substring(oidLength)
							.equals(j.getFirst().substring(oidLength)))// i->first.substr(oidLength)
																		// ==
																		// j->first.substr(oidLength))
					{
						/*
						 * transform((j->second).begin(), (j->second).end(),
						 * (j->second).begin(), toupper); //SvLog
						 * ::writeLog("port:"+i->second+"mac:"+j->second);
						 */
						String aftport = i.getSecond();
						String aftmac = j.getSecond().replaceAll(":", "");// replaceAll(j->second,":","");
						if (!aftport.startsWith("eth"))// .compare(0,3,"eth") ==
														// 0)
						{
							aftport = i.getSecond().substring(3);
						}
						i_pm = port_macs.get(aftport);// .find(aftport);
						if (port_macs.containsKey(aftport)) {
							if (!i_pm.contains(aftmac))// find(i_pm->second.begin(),
														// i_pm->second.end(),
														// aftmac) ==
														// i_pm->second.end())
							{
								port_macs.get(aftport).add(aftmac);// .getSecond.push_back(aftmac);
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
				&& oidIndexList.containsKey(AFT_MAC_DTP_PORT))// .find(AFT_MAC_DTP_PORT)
																// !=
																// oidIndexList.end())
		{
			// qDebug() << "test123456";
			// cout<<"start read aft use private oid"<<endl;
			List<String> i_pm = new ArrayList<String>();
			List<Pair<String, String>> ports = snmp.getMibTable(spr,
					oidIndexList.get(AFT_MAC_DTP_PORT));
			if (ports.isEmpty())
				return false;
			for (Pair<String, String> i : ports) {
				int oidLength = oidIndexList.get(AFT_MAC_DTP_PORT).length();
				String[] v1 = i.getFirst().substring(oidLength).split("\\.");// tokenize(i->first.substr(oidLength),
																				// ".",
																				// true);

				String mac_str = "";

				// vector<string> v1 = tokenize(imac->first.substr(22), ".",
				// true);
				// add by wings 2009-11-13
				// 防止溢出
				if (v1.length < 6) {
					continue;
				}
				// sprintf(mac_str, "%02X%02X%02X%02X%02X%02X",
				// str2int(v1[0]), str2int(v1[1]), str2int(v1[2]),
				// str2int(v1[3]), str2int(v1[4]), str2int(v1[5]));
				mac_str = Integer.toHexString(Integer.parseInt(v1[0]))
						+ Integer.toHexString(Integer.parseInt(v1[1]))
						+ Integer.toHexString(Integer.parseInt(v1[2]))
						+ Integer.toHexString(Integer.parseInt(v1[3]))
						+ Integer.toHexString(Integer.parseInt(v1[4]))
						+ Integer.toHexString(Integer.parseInt(v1[5]));
				i_pm = port_macs.get(i.getSecond());// ->second);
				if (i_pm != null) {// 存在该端口
									// if(find(i_pm->second.begin(),
									// i_pm->second.end(), mac_str) ==
									// i_pm->second.end())
					if (!i_pm.contains(mac_str)) {// 不存在该mac
						i_pm.add(mac_str);// ->second.push_back(mac_str);
					}
				} else {// 不存在该端口
					List<String> maclist = new ArrayList<String>();
					maclist.add(mac_str);
					port_macs.put(i.getSecond(), maclist);
				}
			}
		} else {
			// qDebug() << "test1234567";
			if (portMacs.isEmpty()) {
				// qDebug() << "testxxx exit";
				return false;
			}
			List<String> i_pm = new ArrayList<String>();

			// 有些设备必需直接取端口，不能靠截取oid
			Pair<String, String> oneItem = portMacs.get(0);
			String[] v0 = oneItem.getFirst().split("\\.");// vector<string> v0 =
															// tokenize(oneItem->first,
															// ".", true);
			if (v0.length > 18) {
				List<Pair<String, String>> Macs = snmp.getMibTable(spr,
						"1.3.6.1.2.1.17.4.3.1.1");
				for (Pair<String, String> i : portMacs)// .begin(); i !=
														// portMacs.end(); ++i)
				{
					for (Pair<String, String> j : Macs)// .begin(); j !=
														// Macs.end(); ++j)
					{
						// //SvLog::writeLog("i->first:"+i->first+"i->second"+i->second+"j->first"+j->first+"j->second"+j->second);
						if (j.getSecond().length() < 11)
							continue;// j->second.length() < 11) continue;
						// if (i->first.substr(22) == j->first.substr(22))
						if (i.getFirst().substring(22)
								.equals(j.getFirst().substring(22))) {
							/*
							 * transform((j->second).begin(), (j->second).end(),
							 * (j->second).begin(), toupper); /
							 * /SvLog::writeLog(
							 * "port:"+i->second+"mac:"+j->second );
							 */
							i_pm = port_macs.get(i.getSecond());// .find(i->second);
							if (i_pm != null)// port_macs.end())
							{
								// if (find(i_pm->second.begin(),
								// i_pm->second.end(), j->second) ==
								// i_pm->second.end())
								if (!i_pm.contains(j.getSecond())) {
									// i_pm.put(key,
									// value)->second.push_back(j->second);
									port_macs.get(i.getSecond()).add(
											j.getSecond());
								}
							} else {
								List<String> maclist = new ArrayList<String>();
								maclist.add(j.getSecond());// .push_back(j->second);
								port_macs.put(i.getSecond(), maclist);// .insert(make_pair(i->second,
																		// maclist));
							}
							break;
						}
					}
				}
			} else {
				// for(List<Pair<String,String> imac =
				// portMacs.begin(); imac != portMacs.end(); ++imac)
				for (Pair<String, String> imac : portMacs) {
					// if(imac->first.length() <= 22) continue; //add by
					// jiangshanwen 2010-7-19
					if (imac.getFirst().length() <= 22)
						continue;
					// vector<string> v1 = tokenize(imac->first.substr(22), ".",
					// true);
					String[] v1 = imac.getFirst().substring(23).split("\\.");
					// qDebug() << "imac first: " << imac->first.c_str() <<
					// " second:" << imac->second.c_str();
					String mac_str = "";

					// vector<string> v1 = tokenize(imac->first.substr(22), ".",
					// true);
					// add by wings 2009-11-13
					// 防止溢出
					if (v1.length < 6) {
						continue;
					}
					// sprintf(mac_str, "%02X%02X%02X%02X%02X%02X",
					// str2int(v1[0]), str2int(v1[1]), str2int(v1[2]),
					// str2int(v1[3]), str2int(v1[4]), str2int(v1[5]));
					mac_str = Integer.toHexString(Integer.parseInt(v1[0]))
							+ Integer.toHexString(Integer.parseInt(v1[1]))
							+ Integer.toHexString(Integer.parseInt(v1[2]))
							+ Integer.toHexString(Integer.parseInt(v1[3]))
							+ Integer.toHexString(Integer.parseInt(v1[4]))
							+ Integer.toHexString(Integer.parseInt(v1[5]));
					// i_pm = port_macs.find(imac->second);
					i_pm = port_macs.get(imac.getSecond());
					// if(i_pm != port_macs.end())
					if (i_pm != null) {// 存在该端口
										// if(find(i_pm->second.begin(),
										// i_pm->second.end(), mac_str) ==
										// i_pm->second.end())
						if (!(i_pm.contains(mac_str))) {// 不存在该mac
														// i_pm->second.push_back(mac_str);
							port_macs.get(imac.getSecond()).add(mac_str);
						}
					} else {// 不存在该端口
							// list<string> maclist;
						// maclist.push_back(mac_str);
						// port_macs.insert(make_pair(imac->second, maclist));
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Map<String, List<String>>> getAftData(MibScan snmp,
			SnmpPara spr, Map<String, String> oidIndexList) {
		return null;
	}

	@Override
	public Map<String, Map<String, List<Pair<String, String>>>> getArpData(
			MibScan snmp, SnmpPara spr, Map<String, String> oidIndexList) {
		// SvLog::writeLog("Start read " + spr.ip + " arp by " + spr.community);
		arp_list.clear();
		inf_macs.clear();
		// IP-端口索引1.3.6.1.2.1.4.22.1.1
		// list<pair<String,String> > IpInfInx = snmp.getMibTable(spr,
		// "1.3.6.1.2.1.4.22.1.1");

		boolean isSpecial = false;
		/*
		 * if(IpInfInx.isEmpty() && (oidIndexList.find(ARP_INFINDEX_MACRO) !=
		 * oidIndexList.end())) { IpInfInx = snmp.getMibTable(spr,
		 * oidIndexList[ARP_INFINDEX_MACRO]); isSpecial = true; }
		 * 
		 * if(!IpInfInx.isEmpty()) {
		 */
		List<Pair<String, String>> i_pm = new ArrayList<Pair<String, String>>();
		// IP-MAC地址1.3.6.1.2.1.4.22.1.2
		List<Pair<String, String>> ipMacs = new ArrayList<Pair<String, String>>();
		if (isSpecial && oidIndexList.containsKey(ARP_MAC_MACRO))// (oidIndexList.find(ARP_MAC_MACRO)
																	// !=
																	// oidIndexList.end()))
		{
			ipMacs = snmp.getMibTable(spr, oidIndexList.get(ARP_MAC_MACRO));// [ARP_MAC_MACRO]);
		} else {
			ipMacs = snmp.getMibTable(spr, "1.3.6.1.2.1.4.22.1.2");
		}
		// list<pair<String,String> > IpMacs = snmp.getMibTable(spr,
		// "1.3.6.1.2.1.4.22.1.2");

		// for(list<pair<String,String> imac = IpMacs.begin(); imac !=
		// IpMacs.end(); ++imac)
		for (Pair<String, String> imac : ipMacs) {
			// cout<<"IpMacs:"<<IpMacs.size()<<endl;
			if (imac.getFirst().length() <= 20)// ->first.size()<=20)
			{
				continue;
			}
			String v1[] = imac.getFirst().substring(21).split("\\.");// tvector<string>
																		// v1 =
																		// tokenize(imac->first.substr(21),
																		// ".",
																		// true);
			int len = v1.length;// .size();
			if (len >= 4) {
				String ip_tmp = v1[len - 4] + "." + v1[len - 3] + "."
						+ v1[len - 2] + "." + v1[len - 1];
				String mac_tmp = imac.getSecond().replaceAll(":", "");
						
				if(mac_tmp.length()>12){
					mac_tmp = mac_tmp.substring(0, 12);// replaceAll(imac->second,
				}else{
					
				}
				
											// " ","").substr(0,12);
				if (v1[len - 1] == "0" || v1[len - 1] == "255") {
					continue;
				}
				String inf_tem = v1[len - 5];
				i_pm = inf_macs.get(inf_tem);// .find(inf_tem);
				if (i_pm != null) {// 存在该接口
					boolean bExisted = false;
					// for(list<pair<string,string> j = i_pm->second.begin();
					//
					// j != i_pm->second.end();
					// ++j)
					for (Pair<String, String> j : i_pm) {
						if (j.getSecond().equals(mac_tmp))// ->second ==
															// mac_tmp)
						{
							bExisted = true;
							break;
						}
					}
					if (!bExisted) {// 该接口的ARP中不存在该MAC
									// i_pm->second.push_back(make_pair(ip_tmp,
									// mac_tmp));
						inf_macs.get(inf_tem).add(
								new Pair<String, String>(ip_tmp, mac_tmp));
					}
				} else {// 新的接口
						// list<pair<string,string> > ipmac_list;
					List<Pair<String, String>> ipmac_list = new ArrayList<Pair<String, String>>();
					// ipmac_list.push_back(make_pair(ip_tmp, mac_tmp));
					ipmac_list.add(new Pair<String, String>(ip_tmp, mac_tmp));
					// inf_macs.insert(make_pair(iInf->second, ipmac_list));
					// inf_macs.insert(make_pair(inf_tem, ipmac_list));
					inf_macs.put(inf_tem, ipmac_list);
				}
				// break;
				// }
				// }
			}
		}
		if (!inf_macs.isEmpty())// .isEmpty())
		{
			// arp_list.insert(make_pair(spr.ip,inf_macs));
			arp_list.put(spr.getIp(), inf_macs);
		}
		// }
		// cout<<"end read "<<spr.ip<<" arp"<<endl;
		return arp_list;
	}

	

	@Override
	public Map<String, Map<String, List<RouteItem>>> getRouteData(MibScan snmp,
			SnmpPara spr, Map<String, String> oidIndexList) {
		//SvLog::writeLog("Start read " + spr.ip + " route by " + spr.community);
		route_list.clear();
		//目的网络的接口索引(oid,infinx) 1.3.6.1.2.1.4.21.1.2
	        List<Pair<String,String> > dstNetInfInx = snmp.getMibTable(spr, "1.3.6.1.2.1.4.21.1.2");
	        List<Pair<String,String> > dstNetNextHopIp = new ArrayList<Pair<String,String>>();
	        List<Pair<String,String> > dstStates = new ArrayList<Pair<String,String>>();
	        List<Pair<String,String> > dstMasks = new ArrayList<Pair<String,String>>();

		//add by jiangshanwen 2010-7-21
		boolean isSpecial = false;
		if(dstNetInfInx.isEmpty() && (oidIndexList.containsKey(ROUTE_INFINDEX_MACRO)))//oidIndexList.find(ROUTE_INFINDEX_MACRO) != oidIndexList.end()))
		{
			dstNetInfInx = snmp.getMibTable(spr, oidIndexList.get(ROUTE_INFINDEX_MACRO)); 
			isSpecial = true;
		}

		if(!dstNetInfInx.isEmpty())
		{
			//目的网络的下一跳IP(oid,nbrip) 1.3.6.1.2.1.4.21.1.7
			if(isSpecial && (oidIndexList.containsKey(ROUTE_NEXTHOPIP_MACRO)))//.find(ROUTE_NEXTHOPIP_MACRO) != oidIndexList.end()))
				dstNetNextHopIp = snmp.getMibTable(spr, oidIndexList.get(ROUTE_NEXTHOPIP_MACRO)); 
			else
				dstNetNextHopIp = snmp.getMibTable(spr,"1.3.6.1.2.1.4.21.1.7");
			if(!dstNetNextHopIp.isEmpty())
			{
				//到下一跳类型(oid,state) 1.3.6.1.2.1.4.21.1.8
				if(isSpecial && (oidIndexList.containsKey(ROUTE_ROUTETYPE_MACRO)))//.find(ROUTE_ROUTETYPE_MACRO) != oidIndexList.end()))
					dstNetNextHopIp = snmp.getMibTable(spr, oidIndexList.get(ROUTE_ROUTETYPE_MACRO)); 
				else
					dstStates = snmp.getMibTable(spr,"1.3.6.1.2.1.4.21.1.8");
				if(!dstStates.isEmpty())
				{
					//目的网络mask(oid,mask) 1.3.6.1.2.1.4.21.1.11
					if(isSpecial && (oidIndexList.containsKey(ROUTE_ROUTEMASK_MACRO)))//.find(ROUTE_ROUTEMASK_MACRO) != oidIndexList.end()))
						dstNetNextHopIp = snmp.getMibTable(spr, oidIndexList.get(ROUTE_ROUTEMASK_MACRO)); 
					else
						dstMasks  = snmp.getMibTable(spr, "1.3.6.1.2.1.4.21.1.11");
					if(!dstMasks.isEmpty())
					{
//	                                        List<Pair<string, ROUTEITEM> > ipinx_list;//[(index,<nextip,dest_net,dest_msk>)]
	                      List<Pair<String, RouteItem>> ipinx_list = new ArrayList<Pair<String,RouteItem>>();
//	                                        for(List<Pair<String,String> i = dstNetInfInx.begin();
//							i != dstNetInfInx.end(); 
//							++i)
	                    for(Pair<String, String> i : dstNetInfInx)
						{
							if (i.getFirst().length()<=21)//i->first.size()<=21)
							{
								continue;
							}
//							vector<string> v1 = tokenize(i->first.substr(21), ".", true);
							String[] v1 = i.getFirst().substring(21).split("\\.");
							int len = v1.length;//size_t len = v1.size();
							if(len >= 4)
							{
								String ip_dest_i = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
								String next_hop = "";
								String dest_msk = "";
//	                                                        for(List<Pair<String,String> j = dstNetNextHopIp.begin();
//									j != dstNetNextHopIp.end(); 
//									++j)
								for(Pair<String, String> j :dstNetNextHopIp)
								{
//									v1 = tokenize(j->first.substr(21), ".", true);
									v1 = j.getFirst().substring(21).split("\\.");
									len = v1.length;//v1.size();
									if(len >= 4)
									{
										String ip_dest_j = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
//										if(ip_dest_j != spr.ip && ip_dest_j == ip_dest_i)
										if(!(ip_dest_j.equals(spr.getIp())) && ip_dest_j.equals(ip_dest_i))
										{
//	                                                                                for(List<Pair<String,String> k = dstStates.begin();
//												k != dstStates.end(); 
//												++k)
											for(Pair<String, String> k : dstStates)
											{
//												v1 = tokenize(k->first.substr(21), ".", true);
												v1 = k.getFirst().substring(21).split("\\.");
												len = v1.length;//v1.size();
												if(len >= 4)
												{
													String ip_dest_k = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
//													if( ip_dest_k == ip_dest_i && ((k->second == "4") || (k->second == "1")))
													if(ip_dest_k.equals(ip_dest_i) && ((k.getSecond().equals("4"))||(k.getSecond().equals("1"))))	
													{
														next_hop = j.getSecond();//j->second;
													}
												}
											}
										}
									}
								}
//	                                                        for(List<Pair<String,String> j = dstMasks.begin();
//									j != dstMasks.end(); 
//									++j)
								for(Pair<String, String> j :dstMasks)
								{
//									v1 = tokenize(j->first.substr(21), ".", true);
									v1 = j.getFirst().substring(21).split("\\.");
									len = v1.length;//v1.size();
									if(len >= 4)
									{
										String ip_dest_j = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
//										if(ip_dest_j != spr.ip && ip_dest_j != "0.0.0.0" && ip_dest_j == ip_dest_i) //by tgf 2008-10-10
//										if(ip_dest_j != spr.ip && ip_dest_j == ip_dest_i)
										if(ip_dest_j.equals(spr.getIp()) && ip_dest_j.equals(ip_dest_i))
										{
//	                                                                                for(List<Pair<String,String> k = dstStates.begin();
//												k != dstStates.end(); 
//												++k)
											for(Pair<String, String> k : dstStates)
											{
//												v1 = tokenize(k->first.substr(21), ".", true);
												v1 = k.getFirst().substring(21).split("\\.");
												len = v1.length;//v1.size();
												if(len >= 4)
												{
													String ip_dest_k = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
//													if( ip_dest_k == ip_dest_i && ((k->second == "4") || (k->second == "1")))
													if(ip_dest_k.equals(ip_dest_j) && ((k.getSecond().equals("4"))||(k.getSecond().equals("1"))))
													{
														dest_msk = j.getSecond();//j->second;
													}
												}
											}
										}
									}
								}
								if(!next_hop.isEmpty() && !dest_msk.isEmpty())
								{
									RouteItem rtitem = new RouteItem();
									rtitem.setDest_net(ip_dest_i);//.dest_net = ip_dest_i;
									rtitem.setNext_hop(next_hop);//.next_hop = next_hop;
									rtitem.setDest_msk(dest_msk);//.dest_msk = dest_msk;
									ipinx_list.add(new Pair<String,RouteItem>(i.getSecond(),rtitem));//.push_back(make_pair(i->second, rtitem));
								}					
							}
						}

						if(!ipinx_list.isEmpty())
						{
							Map<String,List<RouteItem>> infinx_nbrips = new HashMap<String,List<RouteItem>>();//{infindex,[ROUTEITEM]}
							for(Pair<String,RouteItem> i : ipinx_list){
								String inx_tmp = i.getFirst();//i.getFirst();//->first;
								String dest_net = i.getSecond().getDest_net();//i->second.dest_net;
								String dest_msk = i.getSecond().getDest_msk();//i->second.dest_msk;
								String next_hop = i.getSecond().getNext_hop();//i->second.next_hop;
								
//	                                                        map<string, list<ROUTEITEM> i_pm;
								List<RouteItem> i_pm = new ArrayList<RouteItem>();
								i_pm = infinx_nbrips.get(inx_tmp);//.find(inx_tmp);
								if(i_pm != null)
								{//存在该接口
									boolean bNewRec = true;
//									for(list<ROUTEITEM>::iterator j = i_pm->second.begin();
//										j != i_pm->second.end();
//										++j)
									for(RouteItem j : i_pm)
									{
//										if(j->dest_net == dest_net || j->dest_msk == dest_msk || j->next_hop == next_hop)
										if(j.getDest_net().equals(dest_net) || j.getDest_msk().equals(dest_msk) || j.getNext_hop().equals(next_hop))
										{
											bNewRec = false;
											break;
										}
									}
									if(bNewRec)
									{
//										i_pm->second.push_back(i->second);
										i_pm.add(i.getSecond());
									}
								}
								else
								{//新的接口
									List<RouteItem> iplist_tmp = new ArrayList<RouteItem>();
									iplist_tmp.add(i.getSecond());//.push_back(i->second);
									infinx_nbrips.put(inx_tmp, iplist_tmp);//.insert(make_pair(inx_tmp, iplist_tmp));
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
//	        list<pair<string,string> > localips = snmp.GetMibTable(spr, "1.3.6.1.2.1.15.3.1.5");
		List<Pair<String, String>> localips = snmp.getMibTable(spr, "1.3.6.1.2.1.15.3.1.5");
		if(!localips.isEmpty())
		{
			//1.3.6.1.2.1.15.3.1.6 :bgpPeerLocalPort
	                List<Pair<String,String> > localports = snmp.getMibTable(spr, "1.3.6.1.2.1.15.3.1.6");
			//1.3.6.1.2.1.15.3.1.8 :bgpPeerRemotePort
	                List<Pair<String,String> > peerports  = snmp.getMibTable(spr, "1.3.6.1.2.1.15.3.1.8");
//	                for(list<pair<string,string> >::iterator i = localips.begin(); i != localips.end(); ++i)
	         for(Pair<String, String> i : localips)
			{
//				vector<string> v1 = tokenize(i->first, ".", true);
	        	 String v1[] = i.getFirst().split("\\.");
				int len = v1.length;//v1.size();
				if(len > 4)
				{
					String ip_dest_i = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
					if(ip_dest_i == "0.0.0.0") continue;
					String local_ip = i.getSecond();//->second; //add by zhangyan 2008-09-02
					String local_port = "";
					String peer_port = "";
//	                                for(list<pair<string,string> >::iterator j = localports.begin(); j != localports.end(); ++j)
					for(Pair<String,String> j : localports)
					{
//						v1 = tokenize(j->first, ".", true);
						v1 = j.getFirst().split("\\.");
						len = v1.length;//.size();
						if(len > 4)
						{
							String ip_dest_j = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
//							if(ip_dest_j != spr.ip && ip_dest_j == ip_dest_i)
							if(!(ip_dest_j.equals(spr.getIp())) || (ip_dest_i.equals(ip_dest_j)))
							{
//	                                                        for(list<pair<string,string> >::iterator k = peerports.begin();	k != peerports.end(); ++k)
								for(Pair<String,String> k : peerports)
								{
//									v1 = tokenize(k->first, ".", true);
									v1 = k.getFirst().split("\\.");
									len = v1.length;//.size();
									if(len > 4)
									{
										String ip_dest_k = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
										if(ip_dest_k == ip_dest_i)
										{
											local_port = j.getSecond();//j->second;
											peer_port = k.getSecond();//k->second;

											boolean bNew = true;
//											for(BGP_LIST::iterator m = bgp_list.begin(); m != bgp_list.end(); ++m)
											for(Bgp m : bgp_list)
											{
//												if((m->local_ip == spr.ip && m->local_port == local_port && m->peer_ip == ip_dest_i && m->peer_port == peer_port) 
//													|| (m->peer_ip == spr.ip && m->peer_port == local_port && m->local_ip == ip_dest_i && m->local_port == peer_port))
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
												bgpitem.setPeer_ip(ip_dest_i);//.peer_ip = ip_dest_i;
												bgpitem.setPeer_port(peer_port);//.peer_port = peer_port;
												//bgpitem.local_ip = ip; //remaked by zhangyan 2008-09-02
												bgpitem.setLocal_ip(local_ip);//.local_ip = local_ip;
												bgpitem.setLocal_port(local_port);//.local_port = local_port;
												bgp_list.add(bgpitem);//.push_back(bgpitem);										
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getInfFlow(MibScan snmp, SnmpPara spr) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, RouterStandbyItem> getVrrpData(MibScan snmp, SnmpPara spr) {
		//SvLog::writeLog("Start read " + spr.ip + " vrrp by " + spr.community);
		routeStandby_list.clear();
		//1.3.6.1.2.1.68.1.3.1.2 :vrrpOperVirtualMacAddr
	       List<Pair<String,String> > vrmacs = snmp.getMibTable(spr, "1.3.6.1.2.1.68.1.3.1.2");
		if(!vrmacs.isEmpty())
		{
			//1.3.6.1.2.1.68.1.4.1.2 :vrrpAssoIpAddrRowStatus
	        List<Pair<String,String> > vrassoipstatus = snmp.getMibTable(spr, "1.3.6.1.2.1.68.1.4.1.2");
			RouterStandbyItem vrrpItem = new RouterStandbyItem();
			//填充虚拟MAC
//	                for(List<Pair<String,String> >::iterator i = vrmacs.begin(); i != vrmacs.end(); ++i)
			for(Pair<String,String> i: vrmacs)
			{			
				String mac = i.getSecond().replaceAll(" ", "").substring(0, 12);//replaceAll(i->second, " ", "").substr(0, 12);
				vrrpItem.getVirtualMacs().add(mac);//.push_back(mac);
			}
			//填充虚拟IP
//	                for(list<pair<string,string> >::iterator i = vrassoipstatus.begin(); i != vrassoipstatus.end(); ++i)
			for(Pair<String,String> i : vrassoipstatus)
			{		
				if (i.getFirst().length() <= 23)//i->first.size() <= 23)
				{
					continue;
				}
//				vector<string> v1 = tokenize((i->first).substr(23), ".", true);
				String v1[] = i.getFirst().substring(23).split("\\.");
				int len = v1.length;//.size();
				if(len > 4)
				{
					String ip = v1[len-4] + "." + v1[len-3] + "." + v1[len-2] + "." + v1[len-1];
					vrrpItem.getVirtualIps().add(ip);//.virtualIps.push_back(ip);					
				}				
				
			}

			routeStandby_list.put(spr.getIp(), vrrpItem);//.insert(make_pair(spr.ip, vrrpItem));
		}
		else
		{
			getHsrpData(snmp, spr); //added by zhangyan 2008-12-04
		}
		
		return routeStandby_list;
	}
	public Map<String, RouterStandbyItem> getHsrpData(MibScan snmp, SnmpPara spr){
		return routeStandby_list;
	}

	@Override
	public Map<String, List<String>> getStpData(MibScan snmp, SnmpPara spr) {
		// TODO Auto-generated method stub
		return null;
	}
	// 获取特定设备的OSPF邻居表的数据
	public Map<String, Map<String, List<String>>> getOspfNbrData(MibScan snmp, SnmpPara spr)
	{
		  //SvLog::writeLog("Start read " + spr.ip + " nbr by " + spr.community);
		Map<String, Map<String, List<String>>> ospfnbr_list = new ConcurrentHashMap<String, Map<String, List<String>>>();
				//邻居IP所在接口索引(oid,infinx) 1.3.6.1.2.1.14.10.1.2
//			        list<pair<string,string> > IpInfInx = snmp.GetMibTable(spr, "1.3.6.1.2.1.14.10.1.2");
				List<Pair<String, String>> ipInfInx = snmp.getMibTable(spr, "1.3.6.1.2.1.14.10.1.2");
				if(!ipInfInx.isEmpty())
				{
//			                std::map<std::string, std::list<std::string> > infinx_nbrips; //{infindex,[nbrip]}
					Map<String,List<String>> infinx_nbrips = new HashMap<String,List<String>>();
//			                for(list<pair<string,string> >::iterator iInf = IpInfInx.begin(); iInf != IpInfInx.end(); ++iInf)
					for(Pair<String, String> iInf : ipInfInx)
					{
//						if (iInf->first.size() <= 22)
						if(iInf.getFirst().length() <= 22)
						{
							continue;
						}
//						vector<string> v1 = tokenize(iInf->first.substr(22), ".", true);
						String v1[] = iInf.getFirst().substring(22).split(".");
						int len = v1.length;//.size();
						if(len >= 5)
						{
							String ip_tmp = v1[len-5] + "." + v1[len-4] + "." + v1[len-3] + "." + v1[len-2];
							String inx_tmp = iInf.getSecond();//->second;
//			                                std::map<std::string, std::list<std::string> >::iterator i_pm;
							List<String> i_pm ;
							i_pm = infinx_nbrips.get(iInf.getSecond());//.find(iInf->second);
							if(i_pm != null)//infinx_nbrips.end())
							{//存在该接口
//								if(find(i_pm->second.begin(), i_pm->second.end(),ip_tmp) == i_pm->second.end())
								if(!i_pm.contains(ip_tmp))
								{//该接口的ARP中不存在该MAC
									i_pm.add(ip_tmp);//->second.push_back(ip_tmp);
								}
							}
							else
							{//新的接口
//								std::list<std::string> iplist_tmp;
								List<String> iplist_tmp = new ArrayList<String>();
								iplist_tmp.add(ip_tmp);//.push_back(ip_tmp);
//								infinx_nbrips.insert(make_pair(iInf->second, iplist_tmp));
								infinx_nbrips.put(iInf.getSecond(), iplist_tmp);
							}
						}
					}
					if(!infinx_nbrips.isEmpty())//.empty())
					{
//						ospfnbr_list.insert(make_pair(spr.ip,infinx_nbrips));
						ospfnbr_list.put(spr.getIp(), infinx_nbrips);
					}
				}
				return ospfnbr_list;
	}
	public static void main(String[] args) {
		String oid = "1.3.6.1.2.1.17.4.3.1.2.0.19.32.125.202.170";
		System.out.println(oid.substring(22).split("\\.").length);
	}
}
