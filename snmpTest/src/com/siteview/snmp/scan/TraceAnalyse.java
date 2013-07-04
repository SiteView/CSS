package com.siteview.snmp.scan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Edge;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.util.IoUtils;
import com.siteview.snmp.util.Utils;

public class TraceAnalyse {

	private ScanParam scanParam;
	private Map<String, IDBody> device_list;
	private List<List<String>> rtpath_list;
	private List<Pair<Pair<String, String>, String>> routeIPPairList;
	private List<String> unManagedIPList;

	public TraceAnalyse() {

	}

	public TraceAnalyse(Map<String, IDBody> devlist,
			List<List<String>> routepath_list,
			List<Pair<Pair<String, String>, String>> routeDestipPairList,
			List<String> unManagedDevices, ScanParam param) {
		this.scanParam.setTimeout(scanParam.getTimeout());
		this.scanParam.setRetrytimes(scanParam.getRetrytimes());

		this.device_list = devlist;
		this.rtpath_list = routepath_list;
		this.routeIPPairList = routeDestipPairList;
		this.unManagedIPList = unManagedDevices;
	}

	public TraceAnalyse(Map<String, IDBody> devlist,
			List<List<String>> routepath_list,
			List<Pair<Pair<String, String>, String>> routeDestipPairList,
			List<String> unManagedDevices) {
		this.device_list = devlist;
		this.rtpath_list = routepath_list;
		this.routeIPPairList = routeDestipPairList;
		this.unManagedIPList = unManagedDevices;
	}

	public static List<List<String>> set_conn = new ArrayList<List<String>>();

	public static int getConnection(List<Edge> topo_edge_list) {
		set_conn.clear();
		// 填充连通集set_conn
		// begin
		for (Edge i : topo_edge_list) {
			int iConnected = 0;
			List<String> i_left = new ArrayList<String>(), i_right = new ArrayList<String>();
			for (List<String> j : set_conn) {
				if (j.contains(i.getIp_left())) {
					i_left = j;
					iConnected++;
				}
			}
			for (List<String> j : set_conn) {
				if (j.contains(i.getIp_right())) {
					i_right = j;
					iConnected += 2;
					break;
				}
			}
			// 新连通集
			if (iConnected == 0) {
				List<String> list_new = new ArrayList<String>();
				list_new.add(i.getIp_left());
				list_new.add(i.getIp_right());
				for (Edge j : topo_edge_list) {
					if (i.getIp_left().equals(j.getIp_left())
							|| i.getIp_right().equals(j.getIp_right())) {
						if (!list_new.contains(j.getIp_right())) {
							list_new.add(j.getIp_right());
						}
						if (i.getIp_left().equals(j.getIp_right())
								|| i.getIp_right().equals(j.getIp_right())) {
							if (!list_new.contains(j.getIp_left())) {
								list_new.add(j.getIp_left());
							}
						}
					}
					set_conn.add(list_new);
				}
			} else if (iConnected == 1) {
				// 边属于左连通集
				i_left.add(i.getIp_right());
			} else if (iConnected == 2) {
				// 边属于右连通集
				i_right.add(i.getIp_left());
			} else if (iConnected == 3) {
				if (i_left.equals(i_right)) {
					// 边属于同一连通集，存在环路
				} else {
					// 合并连通集
					for (String j : i_right) {
						if (i_left.contains(j)) {
							i_left.add(j);
						}
					}
					set_conn.remove(i_right);
				}
			}
		}
		return set_conn.size();
	}

	public boolean analyseRRByRtPath_Direct(List<Edge> topo_edge_list) {
		// 1.存在路径
		// 2.路径中有一段走了不可网管设备(如电信设备)
		List<Edge> edge_list_rr = new ArrayList<Edge>();
		for (List<String> rp_iter : rtpath_list) {
			int num = 0;
			String ipleft_iter = "", ipright_iter = "";
			for (String ip_iter : rp_iter) {
				if (unManagedIPList.contains(ip_iter)) {
					if (num == 0) {
						ipleft_iter = ip_iter;
					} else {
						ipright_iter = ip_iter;
					}
					num++;
				}
			}
			if ((num < 2) || (ipleft_iter.equals(ipright_iter))) {
				// 忽略不能正常分析的trace路径
				continue;
			}

			// 规范化*ipleft_iter前的ips
			List<String> ipleft_list = new ArrayList<String>();
			for (String iter : rp_iter) {
				for (Entry<String, IDBody> j : device_list.entrySet()) {
					if (j.getValue().getIps().contains(iter)) {
						ipleft_list.add(j.getKey());
						break;
					}
				}
			}
			int numL = 0, numR = 0;
			String ip_left = "", port_left = "", ip_right = "", port_right = "";
			Pair<Pair<String, String>, String> l_iter = new Pair<Pair<String, String>, String>(), r_iter = new Pair<Pair<String, String>, String>();
			for (Pair<Pair<String, String>, String> iter : routeIPPairList) {
				if (iter.getSecond().equals(ipleft_iter)) {
					numL++;
					l_iter = iter;
					if (ipleft_list.contains(iter.getFirst().getFirst())) {
						ip_left = iter.getFirst().getFirst();
						port_left = iter.getFirst().getSecond();
					}

				} else if (iter.getSecond().equals(ipright_iter)) {
					numR++;
					r_iter = iter;
					if (rp_iter.contains(iter.getFirst().getFirst())) {
						ip_right = iter.getFirst().getFirst(); // ip_right不需规范化
						port_right = iter.getFirst().getSecond();
					}
				}
			}
			if (ip_left.isEmpty() && (numL == 1)) {
				ip_left = l_iter.getFirst().getFirst();
				port_left = l_iter.getFirst().getSecond();
			}
			if (ip_right.isEmpty() && (numR == 1)) {
				ip_right = r_iter.getFirst().getFirst();
				port_right = r_iter.getFirst().getSecond();
			}
			if (!ip_left.isEmpty() && !ip_right.isEmpty()) {
				Edge edge_tmp = new Edge();
				edge_tmp.setIp_left(ip_left);
				edge_tmp.setPt_left(port_left);
				edge_tmp.setInf_left(port_left);
				edge_tmp.setIp_right(ip_right);
				edge_tmp.setPt_right(port_right);
				edge_tmp.setInf_right(port_right);
				edge_list_rr.add(edge_tmp);
			}
		}
		for (Edge i : edge_list_rr) {
			int iConnected = 0;
			List<String> i_left = new ArrayList<String>(), i_right = new ArrayList<String>();
			for (List<String> j : set_conn) {
				if (j.contains(i.getIp_left())) {
					i_left = j;
					iConnected += 1;
					break;
				}
			}
			for (List<String> j : set_conn) {
				if (j.contains(i.getIp_right())) {
					i_right = j;
					iConnected += 2;
					break;
				}
			}
			if (iConnected == 3) {
				if (!Utils.compare(i_left, i_right)) {
					// 不属于同一连通集时
					topo_edge_list.add(i);
					// 合并连通集
					for (String j : i_right) {
						if (!i_left.contains(j)) {
							i_left.add(j);
						}
					}
					set_conn.remove(i_right);
				}
			}
		}
		return true;
	}

	public boolean analyseRRByRtPath(List<Edge> topo_edge_list) {
		// 1.存在路径
		// 2.路径中有一段走了不可网管设备(如电信设备)
		List<Edge> edge_list_rr = new ArrayList<Edge>();
		List<String> retracertIp_list = new ArrayList<String>();
		for (List<String> rp_iter : rtpath_list) {
			int num = 0;
			String ipleft_iter = "", ipright_iter = "";
			for (String ip_iter : rp_iter) {
				if (unManagedIPList.contains(ip_iter)) {
					if (num == 0) {
						ipleft_iter = ip_iter;
					} else {
						ipright_iter = ip_iter;
					}
					num++;
				}
			}
			if ((num < 2) || (ipleft_iter.equals(ipright_iter))) {
				// 忽略不能正常分析的trace路径
				String iplast_iter = rp_iter.get(rp_iter.size() - 1);
				if (rp_iter.contains("*") && !iplast_iter.equals("*")) {
					retracertIp_list.add(iplast_iter);
				}
				continue;
			}

			// 规范化*ipleft_iter前的ips
			List<String> ipleft_list = new ArrayList<String>();
			for (String iter : rp_iter) {
				for (Entry<String, IDBody> j : device_list.entrySet()) {
					if (j.getValue().getIps().contains(iter)) {
						ipleft_list.add(j.getKey());
						break;
					}
				}
			}
			int numL = 0, numR = 0;
			String ip_left = "", port_left = "", ip_right = "", port_right = "";
			Pair<Pair<String, String>, String> l_iter = new Pair<Pair<String, String>, String>(), r_iter = new Pair<Pair<String, String>, String>();
			for (Pair<Pair<String, String>, String> iter : routeIPPairList) {
				if (iter.getSecond().equals(ipleft_iter)) {
					numL++;
					l_iter = iter;
					if (ipleft_list.contains(iter.getFirst().getFirst())) {
						ip_left = iter.getFirst().getFirst();
						port_left = iter.getFirst().getSecond();
					}

				} else if (iter.getSecond().equals(ipright_iter)) {
					numR++;
					r_iter = iter;
					if (rp_iter.contains(iter.getFirst().getFirst())) {
						ip_right = iter.getFirst().getFirst(); // ip_right不需规范化
						port_right = iter.getFirst().getSecond();
					}
				}
			}
			if (ip_left.isEmpty() && (numL == 1)) {
				ip_left = l_iter.getFirst().getFirst();
				port_left = l_iter.getFirst().getSecond();
			}
			if (ip_right.isEmpty() && (numR == 1)) {
				ip_right = r_iter.getFirst().getFirst();
				port_right = r_iter.getFirst().getSecond();
			}
			if (!ip_left.isEmpty() && !ip_right.isEmpty()) {
				Edge edge_tmp = new Edge();
				edge_tmp.setIp_left(ip_left);
				edge_tmp.setPt_left(port_left);
				edge_tmp.setInf_left(port_left);
				edge_tmp.setIp_right(ip_right);
				edge_tmp.setPt_right(port_right);
				edge_tmp.setInf_right(port_right);
				edge_list_rr.add(edge_tmp);
			} else {
				String iplast_iter = rp_iter.get(rp_iter.size() - 1);
				if (!iplast_iter.equals("*")) {
					retracertIp_list.add(iplast_iter);
				}
			}
		}
		for (Edge i : edge_list_rr) {
			int iConnected = 0;
			List<String> i_left = new ArrayList<String>(), i_right = new ArrayList<String>();
			for (List<String> j : set_conn) {
				if (j.contains(i.getIp_left())) {
					i_left = j;
					iConnected += 1;
					break;
				}
			}
			for (List<String> j : set_conn) {
				if (j.contains(i.getIp_right())) {
					i_right = j;
					iConnected += 2;
					break;
				}
			}
			if (iConnected == 3) {
				if (!Utils.compare(i_left, i_right)) {
					// 不属于同一连通集时
					topo_edge_list.add(i);
					// 合并连通集
					for (String j : i_right) {
						if (!i_left.contains(j)) {
							i_left.add(j);
						}
					}
					set_conn.remove(i_right);
				}
			}
		}
		if (set_conn.size() > 1 && !retracertIp_list.isEmpty()) {
			// 重新进行路由跟踪
			TraceReader myTR = new TraceReader(scanParam.getRetrytimes(),
					scanParam.getTimeout(), 30);// hops暂时先固定为30
			for (String iter : retracertIp_list) {
				Pair<String, String> iii = new Pair<String, String>(iter, "");
				Pair<Pair<String, String>, String> iiii = new Pair<Pair<String, String>, String>();
				iiii.setFirst(iii);
				iiii.setSecond("");
				myTR.routeDESTIPPairList.add(iiii);
			}
			rtpath_list = myTR.getTraceRouteByIPs();// 得到新的路由跟踪路径表
			if (!rtpath_list.isEmpty()) {
				// 保存得到的路径表
				IoUtils.saveRenewTracertList(rtpath_list);
				// 对新得到的路径表进行分析
				analyseRRByRtPath_Direct(topo_edge_list);
			}
		}
		return true;
	}
}
