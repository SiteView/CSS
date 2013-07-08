package com.siteview.snmp.scan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.text.StyleContext.SmallAttributeSet;

import com.siteview.snmp.common.AuxParam;
import com.siteview.snmp.constants.CommonDef;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Bgp;
import com.siteview.snmp.pojo.Directitem;
import com.siteview.snmp.pojo.Edge;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.pojo.IfRec;
import com.siteview.snmp.pojo.RouteItem;
import com.siteview.snmp.util.IoUtils;
import com.siteview.snmp.util.ScanUtils;
import com.siteview.snmp.util.Utils;

public class TopoAnalyse {

	private List<String> rt_list = new ArrayList<String>();// 路由器ip地址表
	private List<String> sw_list = new ArrayList<String>();// 交换机ip地址表
	private List<String> rs_list = new ArrayList<String>();// 交换路由,路由器ip地址表
	private List<String> sw2_list = new ArrayList<String>();// 记录2层交换机ip
	private List<String> sw3_list = new ArrayList<String>();// 记录3层交换机ip
	
	private Map<String, List<Pair<String, String>>> IPADDR_map = new HashMap<String, List<Pair<String, String>>>(); // {ip,[<subnet,ifindex>]}
																													// 2009-01-15
	private AuxParam m_param = new AuxParam();
	private List<Edge> edge_list_rs_byaft = new ArrayList<Edge>();
	// 边连接关系
	private List<Edge> topo_edge_list = new ArrayList<Edge>();
	private List<Edge> edge_list_cur = new ArrayList<Edge>();

	private Map<String, IDBody> devid_list = new HashMap<String, IDBody>();
	private Map<String, Map<String, List<String>>> aft_list = new HashMap<String, Map<String, List<String>>>();
	private Map<String, Map<String, List<String>>> arp_list = new HashMap<String, Map<String, List<String>>>();
	// 设备OSPF邻居列表 {sourceIP,{infInx,[destIP]}}
	private Map<String, Map<String, List<String>>> nbr_list = new HashMap<String, Map<String, List<String>>>();

	private Map<String, Map<String, List<RouteItem>>> rttbl_list = new HashMap<String, Map<String, List<RouteItem>>>();

	private List<Bgp> bgp_list = new ArrayList<Bgp>();
	private Map<String, List<Directitem>> direct_list = new HashMap<String, List<Directitem>>();
	// 设备接口属性列表
	// {devIP,(ifAmount,[(ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)])}
	private Map<String, Pair<String, List<IfRec>>> ifprop_list = new HashMap<String, Pair<String, List<IfRec>>>();
	
	public TopoAnalyse(
			Map<String, IDBody> devidList,
			Map<String, Pair<String, List<IfRec>>> ifpropList,
			Map<String, Map<String, List<String>>> aftList,
			Map<String, Map<String, List<String>>> arpList,
			Map<String, Map<String, List<String>>> nbrList,
			Map<String, Map<String, List<RouteItem>>> routeList,
			List<Bgp> bgpList,
			Map<String, List<Directitem>> directList,
			AuxParam param
			){
		m_param.setComp_type(param.getComp_type());
		devid_list = devidList;
		aft_list = aftList;
		arp_list = arpList;
		nbr_list = nbrList;
		rttbl_list = routeList;
		bgp_list = bgpList;
		direct_list = directList;
		ifprop_list = ifpropList;
		
		rt_list.clear();
		rt_list.clear();
		sw_list.clear();
		sw2_list.clear();
		sw3_list.clear();
		rs_list.clear();
		
		if(devid_list !=null && !devid_list.isEmpty()){
			for(Entry<String, IDBody> i : devid_list.entrySet()){
				if(i.getValue().getDevType().equals(CommonDef.SWITCH)){
					if(!sw2_list.contains(i.getKey())){
						sw2_list.add(i.getKey());
					}
				}
				if(i.getValue().getDevFactory().equals(CommonDef.ROUTE_SWITCH) || i.getValue().getDevType().equals(CommonDef.FIREWALL)){
					if(!sw3_list.contains(i.getKey())){
						sw3_list.add(i.getKey());
					}
				}
				if(i.getValue().getDevType().equals(CommonDef.ROUTER)){
					if(!rt_list.contains(i.getKey())){
						rt_list.add(i.getKey());
						List<Pair<String,String>> subnet_ifindex = new ArrayList<Pair<String,String>>();
						
						for(int ip_i = 0,msk_i=0,if_i=0;
								ip_i<i.getValue().getIps().size()
								&& msk_i<i.getValue().getMsks().size()
								&& if_i<i.getValue().getInfinxs().size();
								ip_i++,msk_i++,if_i++){
							Vector<String> ip_iter   = new Vector<String>(), 
										  msk_iter 	 = new Vector<String>(), 
									  ifindex_iter 	 = new Vector<String>();
							msk_iter = i.getValue().getMsks();
							ip_iter = i.getValue().getIps();
							ifindex_iter = i.getValue().getInfinxs();
							if(msk_iter == null || msk_iter.isEmpty()){
								continue;
							}
							//当掩码不为空时
							String subnet = ScanUtils.getSubnetByIPMask(i.getValue().getIps().get(ip_i),i.getValue().getMacs().get(msk_i));
							subnet_ifindex.add(new Pair<String,String>(subnet,i.getValue().getInfinxs().get(if_i)));
						}
						IPADDR_map.put(i.getKey(), subnet_ifindex);
					}
				}
				if(i.getValue().getDevType().equals(CommonDef.ROUTE_SWITCH)
						|| i.getValue().getDevType().equals(CommonDef.ROUTER)
						|| i.getValue().getDevType().equals(CommonDef.FIREWALL)){
					if(!rs_list.contains(i.getKey())){
						rs_list.add(i.getKey());
					}
				}
			}
			for(String i : sw_list){
				if(!aft_list.containsKey(i)){
					List<String> list_tmp = new ArrayList<String>();
					Map<String,List<String>> mm = new HashMap<String, List<String>>();
					mm.put("0", list_tmp);
					aft_list.put(i, mm);
				}
			}
		}
	}
	
	public boolean analyseRRBySubnet(){
		TraceAnalyse.getConnection(topo_edge_list);
		List<Edge> edge_list_rr = new ArrayList<Edge>();
		Object[] iterIter = IPADDR_map.entrySet().toArray();
		for(int i=0;i<iterIter.length;i++){
			Entry<String, List<Pair<String,String>>> iter = (Entry<String, List<Pair<String,String>>>)iterIter[i];
			String routeip = iter.getKey();
			boolean bConn_i = false;
			List<String> i_left  = new ArrayList<String>(),
							   i_right = new ArrayList<String>();
			for(List<String> conn_iter : TraceAnalyse.set_conn){
				if(conn_iter.contains(routeip)){
					i_left = conn_iter;
					bConn_i = true;
					break;
				}
			}
			for(int j = i+1;j<iterIter.length;j++){
				Entry<String, List<Pair<String,String>>> ii = (Entry<String, List<Pair<String,String>>>)iterIter[j];
				boolean bConn_j = false;
				boolean bSame = false;
				String routeip_tmp = ii.getKey();
				for(List<String> conn_iter : TraceAnalyse.set_conn){
					if(conn_iter.contains(routeip_tmp)){
						i_right = conn_iter;
						bConn_j = true;
						break;
					}
				}
				if (bConn_i && bConn_j) {
					// 不属于同一连通集
					if (!Utils.compare(i_left, i_right)) {
						// 判断是否有相同的subnet
						for (Pair<String, String> subnet_iterI : iter
								.getValue()) {
							String subnet = subnet_iterI.getFirst();
							for (Pair<String, String> subnet_iterJ : ii
									.getValue()) {
								if (subnet.equals(subnet_iterJ.getFirst())) {
									// 找到相同的subnet,添加一条边到edge_list_rr
									bSame = true;
									Edge edge_tmp = new Edge();
									edge_tmp.setIp_left(routeip);
									edge_tmp.setPt_left(subnet_iterI
											.getSecond());
									edge_tmp.setInf_left(subnet_iterI
											.getSecond());
									edge_tmp.setIp_right(routeip_tmp);
									edge_tmp.setPt_right(subnet_iterJ
											.getSecond());
									edge_tmp.setInf_right(subnet_iterJ
											.getSecond());
									edge_list_rr.add(edge_tmp);
									// 合并子连通集
									for (String k : i_right) {
										i_left.add(k);
									}
									TraceAnalyse.set_conn.remove(i_right);
									break;
								}
							}
							if (bSame) {
								break;
							}
						}// end for 判断是否有相同的subnet

					}
				}else if(bConn_i && !bConn_j){
					//判断是否有相同的subnet
					for (Pair<String, String> subnet_iterI : iter
							.getValue()) {
						String subnet = subnet_iterI.getFirst();
						for (Pair<String, String> subnet_iterJ : ii
								.getValue()) {
							if (subnet.equals(subnet_iterJ.getFirst())) {
								// 找到相同的subnet,添加一条边到edge_list_rr
								bSame = true;
								Edge edge_tmp = new Edge();
								edge_tmp.setIp_left(routeip);
								edge_tmp.setPt_left(subnet_iterI.getSecond());
								edge_tmp.setInf_left(subnet_iterI.getSecond());
								edge_tmp.setIp_right(routeip_tmp);
								edge_tmp.setPt_right(subnet_iterJ.getSecond());
								edge_tmp.setInf_right(subnet_iterJ.getSecond());
								edge_list_rr.add(edge_tmp);
								//将该孤立路由器添加到子连通集
								i_left.add(routeip_tmp);
								break;
							}
						}
						if (bSame) {
							break;
						}
					}// end for 判断是否有相同的subnet
				}else if(!bConn_i && bConn_j){
					//判断是否有相同的subnet
					for (Pair<String, String> subnet_iterI : iter.getValue()) {
						String subnet = subnet_iterI.getFirst();
						for (Pair<String, String> subnet_iterJ : ii.getValue()) {
							if (subnet.equals(subnet_iterJ.getFirst())) {
								// 找到相同的subnet,添加一条边到edge_list_rr
								bSame = true;
								Edge edge_tmp = new Edge();
								edge_tmp.setIp_left(routeip);
								edge_tmp.setPt_left(subnet_iterI.getSecond());
								edge_tmp.setInf_left(subnet_iterI.getSecond());
								edge_tmp.setIp_right(routeip_tmp);
								edge_tmp.setPt_right(subnet_iterJ.getSecond());
								edge_tmp.setInf_right(subnet_iterJ.getSecond());
								edge_list_rr.add(edge_tmp);
								//将该孤立路由器添加到子连通集
								i_left.add(routeip);
								break;
							}
						}
						if (bSame) {
							break;
						}
					}// end for 判断是否有相同的subnet
				}else{
					//判断是否有相同的subnet
					for (Pair<String, String> subnet_iterI : iter
							.getValue()) {
						String subnet = subnet_iterI.getFirst();
						for (Pair<String, String> subnet_iterJ : ii
								.getValue()) {
							if (subnet.equals(subnet_iterJ.getFirst())) {
								// 找到相同的subnet,添加一条边到edge_list_rr
								bSame = true;
								Edge edge_tmp = new Edge();
								edge_tmp.setIp_left(routeip);
								edge_tmp.setPt_left(subnet_iterI.getSecond());
								edge_tmp.setInf_left(subnet_iterI.getSecond());
								edge_tmp.setIp_right(routeip_tmp);
								edge_tmp.setPt_right(subnet_iterJ.getSecond());
								edge_tmp.setInf_right(subnet_iterJ.getSecond());
								edge_list_rr.add(edge_tmp);
								//将该孤立路由器添加到子连通集
								List<String> subconn = new ArrayList<String>();
								subconn.add(routeip);
								subconn.add(routeip_tmp);
								TraceAnalyse.set_conn.add(subconn);
								break;
							}
						}
						if (bSame) {
							break;
						}
					}// end for 判断是否有相同的subnet
				}
			} //end for IPADDR_map	2
		}//end for IPADDR_map	1
		//舍弃掉一个路由器端口被多个路由器占用的边
		List<Edge> edge_list_rr_tmp = new ArrayList<Edge>();
		List<Edge> edge_list_rr_del = new ArrayList<Edge>();
		for(Edge i : edge_list_rr){
			boolean bNew = true;
			for(Edge j : edge_list_rr_tmp){
				if((j.getIp_left().equals(i.getIp_left()) && j.getPt_left().equals(i.getPt_left())) 
						|| (j.getIp_right().equals(i.getIp_right()) && j.getPt_right().equals(i.getPt_right())) 
						|| (j.getIp_left().equals(i.getIp_right()) && j.getPt_left().equals(i.getPt_right()))
						|| (j.getIp_right().equals(i.getIp_left()) && j.getPt_right().equals(i.getPt_left()))){
					bNew = false;
					edge_list_rr_del.add(j);
					break;
				}
			}
			if(bNew){
				edge_list_rr_tmp.add(i);
			}
		}
		for(Edge i : edge_list_rr_del){
			Iterator<Edge> jiter = edge_list_rr_tmp.iterator();
			while(jiter.hasNext()){
				Edge j = jiter.next();
				if(j.getIp_left().equals(i.getIp_left()) && j.getPt_left().equals(i.getPt_left())){
					jiter.remove();
					break;
				}
			}
		}
		for(Edge i : edge_list_rr_tmp){
			boolean bNew = true;
			for(Edge j : topo_edge_list){
				if(j.getIp_left().equals(i.getIp_left()) && j.getIp_right().equals(i.getIp_right()) 
					|| (j.getIp_left().equals(i.getIp_right()) && j.getIp_right().equals(i.getIp_left())) 
					|| (j.getIp_left().equals(i.getIp_left()) && j.getPt_left().equals(i.getPt_left()))
					|| (j.getIp_right().equals(i.getIp_right()) && j.getPt_right().equals(i.getPt_right()))
					|| (j.getIp_left().equals(i.getIp_right()) && j.getPt_left().equals(i.getPt_right()))){
					bNew = false;
					break;
				}
			}
			if(bNew){
				topo_edge_list.add(i);
			}
		}
		return true;
	}
	/**
	 *  分析路由器之间的连接关系
	 * @return
	 */
	public boolean analyseRRByRt(){
		//规范route table
		Map<String,Map<String, List<String>>> rttbl_list_frm = new HashMap<String,Map<String, List<String>>>();
		for(Entry<String,Map<String,List<RouteItem>>> i : rttbl_list.entrySet()){
			String ip_src = i.getKey();
			for(Entry<String, IDBody> j : devid_list.entrySet()){
				if(j.getValue().getIps().contains(ip_src)){
					if(j.getValue().getDevType().equals("0") || j.getValue().getDevType().equals("2")){
						ip_src = j.getKey();
						if(!rttbl_list_frm.containsKey(ip_src)){
							Map<String, List<String>> ml = new HashMap<String,List<String>>();
							for(Entry<String, List<RouteItem>> k : i.getValue().entrySet()){
								List<String> ipdst_list = new ArrayList<String>();
								
								for(RouteItem dst : k.getValue()){
									for(Entry<String,IDBody> id_dst : devid_list.entrySet()){
										if(id_dst.getValue().getIps().contains(dst.getNext_hop())){
											if(id_dst.getValue().getDevType().equals("0") || id_dst.getValue().getDevType().equals("2")){
												if(!ipdst_list.contains(id_dst.getKey())){
													ipdst_list.add(id_dst.getKey());
												}
											}
										}
									}
								}
								if(!ipdst_list.isEmpty()){
									ml.put(k.getKey(), ipdst_list);
								}
							}
							if(!ml.isEmpty()){
								rttbl_list_frm.put(ip_src, ml);
							}
						}
					}
					break;
				}
			}
		}
		List<Edge> edge_list_rr = new ArrayList<Edge>();
		for(Entry<String, Map<String,List<String>>> i: rttbl_list_frm.entrySet()){
			//对source ip循环
			String ip_src = i.getKey();
			for(Entry<String, List<String>> j :i.getValue().entrySet()){
				//循环source port
				String pt_src = j.getKey();
				for(String k :j.getValue()){
					//循环dest ip
					String ip_dst = k;
					String pt_dst = "0";
					//dest ip存在于rttbl_list数据列表中
					if(rttbl_list_frm.containsKey(ip_dst)){
						//寻找对端设备中包含source ip的端口
						for(Entry<String, List<String>> j_dst : rttbl_list_frm.get(ip_dst).entrySet()){
							//发现对端router
							if(j_dst.getValue().contains(ip_src)){
								pt_dst = j_dst.getKey();
								Edge edge_tmp = new Edge();
								edge_tmp.setIp_left(ip_src);
								edge_tmp.setPt_left(pt_src);
								edge_tmp.setInf_left(pt_src);
								edge_tmp.setIp_right(ip_dst);
								edge_tmp.setPt_right(pt_dst);
								edge_tmp.setInf_right(pt_dst);
								edge_list_rr.add(edge_tmp);
								break;
							}
						}
					}
				}
			}
		}
		for(Edge i : edge_list_rr){
			if(i.getIp_left().equals(i.getIp_right()) || (i.getInf_left().equals("0") && i.getInf_right().equals("0"))){
				continue;
			}
			if(i.getInf_left().equals("0") || i.getInf_right().equals("0")){
				//用连通集来决定是否加入此连接关系
				boolean bNew = true;
				TraceAnalyse.getConnection(topo_edge_list);
				for(List<String> j : TraceAnalyse.set_conn){
					//属于同一连通集
					if(j.contains(i.getIp_left()) && j.contains(i.getIp_right())){
						bNew = false;
						break;
					}
				}
				if(bNew){
					topo_edge_list.add(i);
				}
			}else{
				boolean bNew = true;
				for(Edge j : topo_edge_list){
					if( (j.getIp_left().equals(i.getIp_left()) && j.getIp_right().equals(i.getIp_right())) 
						|| (j.getIp_left().equals(i.getIp_right()) && j.getIp_right().equals(i.getIp_left()))
						|| (j.getIp_left().equals(i.getIp_left()) && j.getPt_left().equals(i.getPt_left()))
						|| (j.getIp_right().equals(i.getIp_right()) && j.getPt_right().equals(i.getPt_right()))
						|| (j.getIp_left().equals(i.getIp_right()) && j.getPt_left().equals(i.getPt_right()))
						|| (j.getIp_right().equals(i.getIp_left()) && j.getPt_right().equals(i.getPt_left()))){
						bNew = false;
						break;
					}
				}
				if(bNew){
					topo_edge_list.add(i);
				}
			}
		}
		return true;
	}
	public boolean edgeAnalyse(){
		if(!topo_edge_list.isEmpty()){
			topo_edge_list.clear();
		}
		analyseRS();
		analyseSH();
		analyseSS();
		//对纯路由的处理（优先于route表等等）
		analyseRRBySubnet();
		//去掉路由表的取数
		analyseRRByRt();
		//分析路由器之间的连接关系
		analyseRRByNbr();
		
		analyseRRByBgp();
		// 分析路由器之间的连接关系
		analyseRRByArp();
		
		List<Edge> edge_list_tmp = getDirectEdge();
		for(Edge i :edge_list_tmp){
			boolean bNew =true;
			for(Edge j :topo_edge_list){
				if((j.getIp_left().equals(i.getIp_left()) && j.getPt_left().equals(i.getPt_left()))
						||(j.getIp_right().equals(i.getIp_right()) && j.getPt_right().equals(i.getPt_right()))
						||(j.getIp_left().equals(i.getIp_right()) && j.getPt_right().equals(i.getPt_right()))
						||(j.getIp_right().equals(i.getIp_left()) && j.getPt_right().equals(i.getPt_left()))){
					bNew = false;
					break;
				}
			}
			if(bNew){
				topo_edge_list.add(i);
			}
		}
		if(m_param.getComp_type().equals("00")){
			compAftWithArp(m_param.getComp_type());
			analyseSH_COMP();
			analyseSS_COMP();
		}
		return true;
	}
	/**
	 *  分析交换机之间的连接关系
	 */
	public boolean analyseSS_COMP(){
		if(sw_list.size() < 2){
			return true;
		}
		for(Entry<String, Map<String,List<String>>> i : aft_list.entrySet()){
			boolean bEmpty =true;
			for(Entry<String,List<String>> j : i.getValue().entrySet()){
				Iterator<String> kiter = j.getValue().iterator();
				while(kiter.hasNext()){
					String k = kiter.next();
					if(aft_list.containsKey(k) && sw_list.contains(k)){
						kiter.remove();
					}
				}
			}
		}
		List<Edge> edge_list_ss = new ArrayList<Edge>();
		boolean bGoOn = true;
		while(bGoOn){
			bGoOn = false;
			//先处理正常边
			boolean bNormal = true;
			while(bNormal){
				//处理过的交换ip
				List<String> iplist_del = new ArrayList<String>();
				bNormal = false;
				//source ip 循环
				for(Entry<String, Map<String,List<String>>> i : aft_list.entrySet()){
					String ip_src = i.getKey();
//					source port 循环
					for(Entry<String,List<String>> j : i.getValue().entrySet()){
						if(j.getValue() == null || j.getValue().isEmpty()){
							continue;
						}
						String pt_src = j.getKey();
						//是交换叶子端口
						if(isSWLeafPort(j.getValue(), sw_list)){
							String ip_dest = j.getValue().get(0);
							if(aft_list.containsKey(ip_dest)){
								//对端是交换叶子,添加一条交换-交换边
								if(isSWLeafDevice(ip_src, aft_list.get(ip_dest))){
									bNormal = true;
									//目的设备的端口缺省为PX
									String pt_dest = "PX";
									//寻找对端设备中包含source ip的端口
									for(Entry<String, List<String>> j_dest : aft_list.get(ip_dest).entrySet()){
										if(j_dest.getValue().contains(ip_src)){
											pt_dest = j_dest.getKey();
											//删除对端中的source ip
											j_dest.getValue().remove(ip_src);
											break;
										}
									}
									Edge edge_tmp = new Edge();
									edge_tmp.setIp_left(ip_src);
									edge_tmp.setPt_left(pt_src);
									edge_tmp.setInf_left(pt_src);
									edge_tmp.setIp_right(ip_dest);
									edge_tmp.setPt_right(pt_dest);
									edge_tmp.setInf_right(pt_dest);
									edge_list_ss.add(edge_tmp);
									if(!iplist_del.contains(ip_dest)){
										iplist_del.add(ip_dest);
									}
									j.getValue().clear();
								}
							}
						}
					}
				}
				//删除处理后的dest交换IP
				for(String ip_del : iplist_del){
					//对source ip 循环
					for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet()){
						String ip_src = i.getKey();
						for(Entry<String, List<String>> j : i.getValue().entrySet()){
							j.getValue().remove(ip_del);
						}
					}
				}
			}
			/*
			 * 再处理环路
			 * 只要存在一台设备,其不同端口能够到达同一设备,则一定存在环路
			 * 若存在环路,则在处理完正常边后,需要继续处理正常边
			 */
			boolean bCircle = true;
			while(bCircle){
				bCircle = false;
				//对source ip 循环
				for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet()){
					//是否存在环路
					if(i.getValue().size() > 1){
						String ip_src = i.getKey();
						Map<String,List<String>> k_start = new HashMap<String,List<String>>();
						List<Pair<String,String>> sametip_list = new ArrayList<Pair<String,String>>();
						Object isetarr[] = i.getValue().entrySet().toArray();
						//对source port 循环
						for(int ji=0 ;ji<isetarr.length-1;ji++){
							Entry<String, List<String>> j = (Entry<String,List<String>>)isetarr[ji];
							//比较 j,k 端口集合的元素
							for(int ki = ji+1;ki < isetarr.length;ki++){
								Entry<String, List<String>> k = (Entry<String,List<String>>)isetarr[ki];
								for(String ip_j : j.getValue()){
									for(String ip_k : k.getValue()){
										if(ip_k.equals(ip_j)){
											boolean bk = true,bj = true;
											for(Pair<String,String> ptip : sametip_list){
												if(ptip.getFirst().equals(j.getKey()) && ptip.getSecond().equals(ip_j)){
													//不同端口的相同可达设备
													bj = false;
												}
												if(ptip.getFirst().equals(k.getKey()) && ptip.getSecond().equals(ip_k)){
													//不同端口的相同可达 设备
													bk = false;
												}
											}
											if(bk)
													sametip_list.add(new Pair<String, String>(k.getKey(), ip_k));
											if(bj)	
													sametip_list.add(new Pair<String, String>(j.getKey(), ip_j));
										}
										
									}
								}
							}
 							
						}
						//存在环路
						if(!sametip_list.isEmpty()){
							bGoOn = true;//必须继续进行正常分析
							//将环路添加到边集合
							for(Pair<String,String> ptip_dest : sametip_list){
								//忽略对端没有source ip 的数据FRM_AFTARP_LIST
								String pt_src = ptip_dest.getFirst();
								String ip_dest = ptip_dest.getSecond();
								Map<String,List<String>> ps_peer = aft_list.get(ip_dest);
								//存在对端交换设备的转发表数据
								if(ps_peer != null){
									//对端端口集合
									for(Entry<String, List<String>> j_peer : ps_peer.entrySet()){
										Iterator<String> ipiter = j_peer.getValue().iterator();
										while(ipiter.hasNext()){
											String ip_peer = ipiter.next();
											if(ip_peer.equals(ip_src)){
												//加入一条环路边
												Edge edge_tmp = new Edge();
												edge_tmp.setIp_left(ip_src);
												edge_tmp.setPt_left(pt_src);
												edge_tmp.setInf_left(pt_src);
												edge_tmp.setPt_right(j_peer.getKey());
												edge_tmp.setIp_right(ip_dest);
												edge_tmp.setInf_right(j_peer.getKey());
												ipiter.remove();
											}
										}
									}
								}
							}
							//在source 端删除环路设备
							for(Entry<String, List<String>> j : i.getValue().entrySet()){
								//对source port 循环
								Iterator<String> kiter = j.getValue().iterator();
								for(;kiter.hasNext();){
									String k = kiter.next();
									//对dest  ip循环
									boolean bDelete = false;
									for(Pair<String,String> ptip_dest : sametip_list){
										if(ptip_dest.getSecond().equals(k)){
											bDelete = true;
											break;
										}
									}
									if(bDelete){
										kiter.remove();
									}
								}
							}
						}
					}
				}
			}
		}
		//剩余的交换数据，逐条连接即可
		for(Entry<String,Map<String,List<String>>> i : aft_list.entrySet()){
			//对source ip 循环
			String ip_src = i.getKey();
			for(Entry<String,List<String>> j : i.getValue().entrySet()){
				//对source port 循环
				String pt_src = j.getKey();
				Iterator<String> kiter = j.getValue().iterator();
				while(kiter.hasNext()){
					String k = kiter.next();
					Map<String,List<String>> res_dest = aft_list.get(k);
					if(res_dest != null){
						//对端存在于aft中
						for(Entry<String,List<String>> m : res_dest.entrySet()){
							if(m.getValue().contains(ip_src)){
								//添加一条ss边
								Edge edge_tmp = new Edge();
								edge_tmp.setIp_left(ip_src);
								edge_tmp.setPt_left(pt_src);
								edge_tmp.setInf_left(pt_src);
								edge_tmp.setPt_right(m.getKey());
								edge_tmp.setIp_right(k);
								edge_tmp.setInf_right(m.getKey());
								//从对端端口 集合中删除源IP
								m.getValue().remove(ip_src);
							}
						}
					}
				}
			}
		}
		List<List<String>> set_conn = new ArrayList<List<String>>();
		for(Edge i : topo_edge_list){
			int iConnected = 0;
			List<String> i_left  = new ArrayList<String>(),
							   i_right = new ArrayList<String>();
			for(List<String> j : set_conn){
				if(j.contains(i.getIp_left())){
					i_left = j;
					iConnected ++ ;
					break;
				}
			}
			for(List<String> j : set_conn){
				if(j.contains(i.getIp_right())){
					i_right = j;
					iConnected += 2 ;
					break;
				}
			}
			if(iConnected == 0){
				//新连通集
				List<String> list_new = new ArrayList<String>();
				list_new.add(i.getIp_left());
				list_new.add(i.getIp_right());
				for(Edge j:topo_edge_list){
					if(i.getIp_left().equals(j.getIp_left()) || i.getIp_right().equals(j.getIp_left())){
						if(!list_new.contains(j.getIp_right())){
							list_new.add(j.getIp_right());
						}
					}
					if(i.getIp_right().equals(j.getIp_right()) || i.getIp_right().equals(j.getIp_right())){
						if(!list_new.contains(j.getIp_left())){
							list_new.add(j.getIp_left());
						}
					}
				}
				set_conn.add(list_new);
			}else if(iConnected == 1){
				//边属于左连通集
				i_left.add(i.getIp_right());
			}else if(iConnected == 2){
				//边属于右连通集
				i_right.add(i.getIp_left());
			}else if(iConnected == 3){
				if(i_left.equals(i_right)){
					//边属于同一连通集，存在环路
				}else{
					//合并连通集
					for(String j : i_right){
						if(!i_left.contains(j)){
							i_left.add(j);
						}
					}
					set_conn.remove(i_right);
				}
			}
		}
		for(Edge i :edge_list_ss){
			int iConnected = 0;
			List<String> i_left  = new ArrayList<String>(),
						 i_right = new ArrayList<String>();
			for(List<String> j : set_conn){
				if(j.contains(i.getIp_left())){
					i_left = j;
					iConnected ++ ;
					break;
				}
			}
			for(List<String> j : set_conn){
				if(j.contains(i.getIp_right())){
					i_right = j;
					iConnected += 2;
					break;
				}
			}
			boolean bNew = true;
			if(iConnected == 0){
				//新连通集
				List<String> list_new = new ArrayList<String>();
				list_new.add(i.getIp_left());
				list_new.add(i.getIp_right());
				set_conn.add(list_new);
			}else if(iConnected == 1){
				//边属于左连通集
				i_left.add(i.getIp_right());
			}else if(iConnected == 2){
				//边属于右连通集
				i_right.add(i.getIp_left());
			}else if(iConnected == 3){
				if(!i_left.equals(i_right)){
					//合并连通集
					for(String j : i_right){
						if(!i_left.contains(j)){
							i_left.add(j);
						}
					}
					set_conn.remove(i_right);
				}else{
					//属于同一连通集合
					bNew = false;
				}
			}
			if(bNew){
				topo_edge_list.add(i);
			}
		}
		return true;
	}
	/**
	 * 分析交换机与主机之间的连接关系
	 */
	public void analyseSH_COMP(){
		List<Edge> edge_list_sh = new ArrayList<Edge>();
		boolean bGoOn = true;
		while(bGoOn){
			bGoOn = false;
			//处理过的ip
			List<String> iplist_del = new ArrayList<String>();
			//循环sourceIp
			for(Entry<String, Map<String,List<String>>> i : aft_list.entrySet()){
				String ip_src = i.getKey();
				//循环source port
				for(Entry<String,List<String>> j : i.getValue().entrySet()){
					//是叶子节点
					if(isLeafPort(j.getValue(), sw_list)){
						String pt_src = j.getKey();
						//对dest ip 循环,为每个目的ip添加一条边
						for(String k : j.getValue()){
							if(sw_list.contains(k)){
								continue;
							}
							if(rs_list.contains(k)){
								continue;
							}
							bGoOn = true;
							String pt_dest = "PX";
							//是路由器且存在对应的arp条目,寻找对应的端口(接口)
							if(arp_list.containsKey(k)){
								for(Entry<String, List<String>> i_arp : arp_list.get(k).entrySet()){
									if(i_arp.getValue().contains(ip_src)){
										pt_dest = i_arp.getKey();
										break;
									}
								}
							}
							Edge edge_tmp = new Edge();
							edge_tmp.setIp_left(ip_src);
							edge_tmp.setPt_left(pt_src);
							edge_tmp.setInf_left(pt_src);
							edge_tmp.setIp_right(k);
							edge_tmp.setPt_right(pt_dest);
							edge_tmp.setInf_right(pt_dest);
							//将该dest ip 添加到已经处理的ip地址表中
							if(!iplist_del.contains(k)){
								iplist_del.add(k);
							}
						}//对dest ip 循环 end
					}//是叶子节点 end
				}//循环source port end
			}
			//删除处理后的dest ip
			for(String ip_del : iplist_del){
				//循环source ip
				for(Entry<String, Map<String,List<String>>> i:aft_list.entrySet()){
					String ip_src = i.getKey();
					
					for(Entry<String,List<String>> j :i.getValue().entrySet()){
						j.getValue().remove(ip_del);
					}
				}
			}
		}//while end
		//将不能分析的主机ip全部抛弃
		for(Entry<String, Map<String,List<String>>> i : aft_list.entrySet()){
			String ip_src = i.getKey();
			for(Entry<String, List<String>> j:i.getValue().entrySet()){
				Iterator<String> kiter = j.getValue().iterator();
				while(kiter.hasNext()){
					String k = kiter.next();
					if(!sw_list.contains(k)){
						kiter.remove();
					}
				}
			}
		}
	}
	public void compAftWithArp(String stype){
		aft_list.clear();
		Utils.addAllMap(arp_list, aft_list);
	}
	public List<Edge> getDirectEdge(){
		List<Edge> edge_list_cur = new ArrayList<Edge>();
		for(Entry<String, List<Directitem>> i : direct_list.entrySet()){
			String left_ip = i.getKey();
			for(Directitem j : i.getValue()){
				String right_ip = j.getPeerId();
				if(left_ip.equals(right_ip)){
					continue;
				}
				Edge edge = new Edge();
				edge.setIp_left(left_ip);
				edge.setInf_left(j.getLocalPortInx());
				edge.setPt_left(j.getLocalPortInx());
				edge.setDsc_left("");
				edge.setIp_right(right_ip);
				edge.setInf_right("0");
				edge.setPt_right("0");
				edge.setDsc_right(j.getPeerPortDsc());
				Pair<String,List<IfRec>> ifprop_left = ifprop_list.get(left_ip);
				if(ifprop_left != null){
					//左边接口描述
					for(IfRec k : ifprop_left.getSecond()){
						if(k.getIfIndex().equals(j.getLocalPortInx())){
							edge.setDsc_left(k.getIfDesc());
							break;
						}
					}
				}
				Pair<String,List<IfRec>> ifprop_right = ifprop_list.get(right_ip);
				if(ifprop_right != null){
					//右边接口描述
					String desc_tmp = j.getPeerPortDsc();
					for(IfRec k : ifprop_right.getSecond()){
						if(k.getIfDesc().equals(desc_tmp)){
							edge.setPt_right(k.getIfIndex());
							edge.setInf_right(k.getIfIndex());
							break;
						}
					}
				}
				boolean bNew = true;
				for(Edge e : edge_list_cur){
					if(e.getIp_left().equals(e.getIp_right())){
						continue;
					}
					if((edge.getIp_left().equals(e.getIp_left()) && edge.getInf_left().equals(e.getInf_left()))
							|| (edge.getIp_right().equals(e.getIp_right()) && edge.getInf_right().equals(e.getInf_right()))
							|| (edge.getIp_left().equals(e.getIp_right()) && edge.getInf_left().equals(e.getInf_right()))
							|| (edge.getIp_right().equals(e.getIp_left()) && edge.getInf_right().equals(e.getInf_left()))){
						bNew = false;
						break;
					}
				}
				if(bNew){
					edge_list_cur.add(edge);
				}
			}
		}
		return edge_list_cur;
	}
	/**
	 * 分析路由器之间的连接关系
	 * @return
	 */
	public boolean analyseRRByArp(){
		if(rs_list.isEmpty() || arp_list.isEmpty()){
			return true;
		}
		List<Edge> edge_list_rr = new ArrayList<Edge>();
		boolean bGoOn = true;
		while(bGoOn){
			//处理过的路由器IP
			List<String> iplist_del = new ArrayList<String>();
			bGoOn = false;
			//循环sourceIP
			for(Entry<String, Map<String, List<String>>> i : arp_list.entrySet()){
				//source ip 不为router
				if(rs_list.contains(i.getKey())){
					continue;
				}
				String ip_src = i.getKey();
				//循环source port
				for(Entry<String,List<String>> j :i.getValue().entrySet()){
					//忽略没有router的端口集
					boolean bRPort = false;
					for(String k : rs_list){
						if(j.getValue().contains(k)){
							bRPort = true;
							break;
						}
					}
					if(!bRPort){
						continue;
					}else{
						//忽略包含交换设备的端口
						bRPort = false;
						for(String k : sw_list){
							if(j.getValue().contains(k)){
								bRPort = true;
								break;
							}
						}
						if(bRPort) continue;
					}
					String pt_src = j.getKey();
					Iterator<String> kiter = j.getValue().iterator();
					//循环dest ip
					while(kiter.hasNext()){//String k :j.getValue()){
						String k = kiter.next();
						//忽略非router的dest ip
						if(rs_list.contains(k)){
							continue;
						}
						//目的ip为router
						String ip_dest = k;
						bGoOn = true;
						//目的设备的端口缺省为PX
						String pt_dest = "PX";
						//dest ip存在于arp数据列表中
						if(arp_list.containsKey(ip_dest)){
							//寻找对端设备中包含source ip的端口
							for(Entry<String, List<String>> j_dest : arp_list.get(ip_dest).entrySet()){
								if(j_dest.getValue().contains(ip_src)){
									pt_dest = j_dest.getKey();
									//从对端删除source ip
									j_dest.getValue().remove(ip_src);
									break;
								}
							}
						}
						Edge edge_tmp = new Edge();
						edge_tmp.setIp_left(ip_src);
						edge_tmp.setPt_left(pt_src);
						edge_tmp.setInf_left(pt_src);
						edge_tmp.setIp_right(ip_dest);
						edge_tmp.setPt_right(pt_dest);
						edge_tmp.setInf_right(pt_dest);
						edge_list_rr.add(edge_tmp);
						kiter.remove();
					}
					
				}
			}
		}
		for(Edge i : edge_list_rr){
			if(i.getIp_left().equals(i.getIp_right())){
				continue;
			}
			if(i.getInf_left().equals("PX") || i.getInf_right().equals("PX")){
				//用连通集来决定是否加入此连接关系
				boolean bNew = true;
				TraceAnalyse.getConnection(topo_edge_list);
				for(List<String> j :TraceAnalyse.set_conn){
					if(j.contains(i.getIp_left())){
						bNew = false;
						break;
					}
				}
				if(bNew){
					topo_edge_list.add(i);
				}
			}else{
				boolean bNew = true;
				if(bNew){
					for(Edge j : topo_edge_list){
						if((j.getIp_left().equals(i.getIp_left()) && j.getPt_left().equals(i.getPt_left()))
								||(j.getIp_right().equals(i.getIp_right()) && j.getPt_right().equals(i.getPt_right()))
								||(j.getIp_left().equals(i.getIp_right()) && j.getPt_left().equals(i.getPt_right()))
								||(j.getIp_right().equals(i.getIp_left()) && j.getPt_right().equals(i.getPt_left()))){
								bNew = false;
								break;
						}
					}
					//过滤掉Vlan端口
					if(IPADDR_map.containsKey(i.getIp_left()) && IPADDR_map.containsKey(i.getIp_right())){
						bNew = false;
					}
				}
				if(bNew){
					topo_edge_list.add(i);
				}
			}
		}
		return true;
	}
	
	public boolean analyseRRByBgp(){
		List<Edge> edge_list_rr = new ArrayList<Edge>();
		for(Bgp i : bgp_list){
			String ip_src = i.getLocal_ip();
			String ip_dst = i.getPeer_ip();
			for(Entry<String, IDBody> j : devid_list.entrySet()){
				if(j.getValue().getIps().contains(ip_src)){
					ip_src = j.getKey();
					break;
				}
			}
			for(Entry<String, IDBody> j : devid_list.entrySet()){
				if(j.getValue().getIps().contains(ip_dst)){
					ip_dst = j.getKey();
					break;
				}
			}
			Edge edge_tmp = new Edge();
			edge_tmp.setIp_left(ip_src);
			edge_tmp.setPt_left(i.getLocal_port());
			edge_tmp.setInf_left(i.getLocal_port());
			edge_tmp.setIp_right(ip_dst);
			edge_tmp.setPt_right(i.getPeer_port());
			edge_tmp.setInf_right(i.getPeer_port());
			edge_list_rr.add(edge_tmp);
		}
		for(Edge i : edge_list_rr){
			if(i.getIp_left().equals(i.getIp_right()) || (i.getInf_left().equals("0") && i.getInf_right().equals("0"))){
				continue;
			}
			boolean bNew = true;
			for(Edge j : topo_edge_list){
				if((j.getIp_left().equals(i.getIp_left()) && j.getIp_right().equals(i.getIp_right()))
						|| (j.getIp_left().equals(i.getIp_right()) && j.getIp_right().equals(j.getIp_left()))
						|| (j.getIp_left().equals(i.getIp_left())&&j.getPt_left().equals(i.getPt_left()))
						|| (j.getIp_right().equals(i.getIp_right())&& j.getPt_right().equals(i.getPt_right()))
						|| (j.getIp_left().equals(i.getIp_right())&&j.getPt_left().equals(i.getPt_right()))
						|| (j.getIp_right().equals(i.getIp_left()) && j.getPt_right().equals(i.getPt_left()))){
					bNew = false;
					break;
				}
			}
			if(bNew){
				topo_edge_list.add(i);
			}
			
		}
		return true;
	}
	public boolean analyseRRByNbr(){
		List<Edge> edge_list_rr = new ArrayList<Edge>();
		//循环source ip
		for(Entry<String, Map<String, List<String>>> i : nbr_list.entrySet()){
			String ip_src = i.getKey();
			String ip_src_frm = ip_src;
			Vector<String> ip_src_list = new Vector<String>();
			//循环设备列表
			for(Entry<String, IDBody> j : devid_list.entrySet()){
				if(j.getValue().getIps().contains(ip_src)){
					ip_src_list = j.getValue().getIps();
					ip_src_frm = j.getKey();
					break;
				}
			}
			if(ip_src_list.isEmpty()){
				continue;
			}
			//循环source ip
			for(Entry<String, List<String>> j : i.getValue().entrySet()){
				String pt_src = j.getKey();
				//循环dest ip
				for(String k : j.getValue()){
					//目的IP为router
					String ip_dst = k;
					String ip_dst_frm = ip_dst;
					String ptsrc_to_dst = "0";
					String ptdst_to_src = "0";
					Vector<String> ip_dst_list = new Vector<String>();
					for(Entry<String, IDBody> m : devid_list.entrySet()){
						if(m.getValue().getIps().contains(ip_dst)){
							ip_dst_list = m.getValue().getIps();
							ip_dst_frm = m.getKey();
							break;
						}
					}
					if(ip_dst_list.isEmpty()){
						continue;
					}
					if(!devid_list.get(ip_dst_frm).getDevType().equals("0") && !devid_list.get(ip_dst_frm).getDevType().equals("2")){
						continue;
					}
					String ip_dst_valid = "";//有效目的端口
					for(String vi : ip_dst_list){
						if(nbr_list.containsKey(vi)){
							ip_dst_valid = vi;
							break;
						}
					}
					if(Utils.isEmptyOrBlank(ip_dst_valid)){
						continue;
					}
					//dest ip存在于nbr_list数据列表中
					boolean bOk = false;
					for(Entry<String, List<String>> j_dst : nbr_list.get(ip_dst_valid).entrySet()){
						//寻找对端设备中包含source ip的端口
						for(String m : j_dst.getValue()){
							String n1 = "",n2 = "";
							for(int n1i = 0,n2i = 0; n1i<=ip_src_list.size();n1i++,n2i++){
								n1=ip_src_list.get(n1i);
								if(n2i < devid_list.get(ip_src_frm).getInfinxs().size()){
									n2 = devid_list.get(ip_src_frm).getInfinxs().get(n2i);
									if(n1.equals(m)){
										ptsrc_to_dst = n2;
										bOk = true;
										break;
									}
								}
								
							}
							if(bOk){
								break;
							}
						}
						if(bOk){
							break;
						}
					}
					bOk = false;
					for(Entry<String, List<String>> j_src : nbr_list.get(ip_src).entrySet()){
						//寻找对端设备中包含source ip的端口
						for(String m : j_src.getValue()){
							String n1="",n2="";
							for(int n1i = 0,n2i = 0;n1i<ip_dst_list.size() && n2i<devid_list.get(ip_dst_frm).getInfinxs().size();n1i++,n2i++){
								n1 = ip_dst_list.get(n1i);
								n2 = devid_list.get(ip_dst_frm).getInfinxs().get(n2i);
								if(n1.equals(m)){
									ptdst_to_src = n2;
									bOk = true;
									break;
								}
							}
							if(bOk) break;
						}
						if(bOk) break;
					}
					Edge edge_tmp = new Edge();
					edge_tmp.setIp_left(ip_src_frm);
					edge_tmp.setPt_left(ptsrc_to_dst);
					edge_tmp.setInf_left(ptsrc_to_dst);
					edge_tmp.setIp_right(ip_dst_frm);
					edge_tmp.setPt_right(ptdst_to_src);
					edge_tmp.setInf_right(ptdst_to_src);
					edge_list_rr.add(edge_tmp);
				}
			}
		}
		for(Edge i : edge_list_rr){
			if(i.getIp_left().equals(i.getIp_right()) || i.getInf_left().equals("0") || i.getInf_right().equals("0")){
				continue;
			}
			boolean bNew = true;
			for(Edge j : topo_edge_list){
				if((j.getIp_left().equals(i.getIp_left()) && j.getIp_right().equals(i.getIp_right()))
						|| (j.getIp_left().equals(i.getIp_right()) && j.getIp_right().equals(j.getIp_left()))
						|| (j.getIp_left().equals(i.getIp_left())&&j.getPt_left().equals(i.getPt_left()))
						|| (j.getIp_right().equals(i.getIp_right())&& j.getPt_right().equals(i.getPt_right()))
						|| (j.getIp_left().equals(i.getIp_right())&&j.getPt_left().equals(i.getPt_right()))
						|| (j.getIp_right().equals(i.getIp_left()) && j.getPt_right().equals(i.getPt_left()))){
					bNew = false;
					break;
				}
			}
			if(bNew){
				topo_edge_list.add(i);
			}
		}
		return true;
	}
	public boolean isInclude(Map<String,List<String>> port_set_a,Map<String,List<String>> port_set_b,String excludeIp){
		List<String> iplist_in_b = new ArrayList<String>();
		for(Entry<String, List<String>> i : port_set_b.entrySet()){
			for(String j :i.getValue()){
				iplist_in_b.add(j);
			}
			
		}
		iplist_in_b.add(excludeIp);
		int ii = 0;
		for(Entry<String, List<String>> i : port_set_a.entrySet()){
			if(i.getValue().isEmpty()){
				ii++;
			}
			for(String j : i.getValue()){
				if(iplist_in_b.contains(j)){
					return false;
				}
			}
		}
		if(ii>1){
			return false;
		}else{
			return true;
		}
		
	}
	// 判定一个交换设备是否为一个叶子交换设备
	// remoteip -- 对端的ip
	// port_set -- 本端的端口集合
	public boolean isSWLeafDevice(String remoteip,Map<String,List<String>> port_set){
		if(port_set.isEmpty()){
			return true;
		}
		int mycount = 0;
		boolean bFindPeer = false;
		for(Entry<String, List<String>> i : port_set.entrySet()){
			if(i.getValue().size() >= 1){
				mycount++;
				if(i.getValue().contains(remoteip)){
					bFindPeer = true;
				}
			}
		}
		if(mycount == 0 || (mycount == 1 && bFindPeer)){
			return true;
		}
		return false;
	}
	// 分析交换机之间的连接关系
	public boolean analyseSS(){
		if(sw_list.size() < 2)
		{
			return true;
		}
		int test = 0;
		IoUtils.saveFrmSSAftList(aft_list,test++);
		for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
		{
			boolean bEmpty = true;
//	                for(std::map<string,list<string> >::iterator j = i->second.begin();
//				j != i->second.end();
//				++j)
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{
//				for(std::list<string>::iterator k = j->second.begin();
//					k != j->second.end();
//					)
				for(Iterator<String> iter = j.getValue().iterator();iter.hasNext();)
				{
					String k = iter.next();
//					if(aft_list.find(*k) == aft_list.end() && find(sw_list.begin(), sw_list.end(), *k) == sw_list.end())
					if(!aft_list.containsKey(k))
					{
						iter.remove();
					}
				}
			}
		}

		List<Edge>  edge_list_ss = new ArrayList<Edge>();
		boolean bGoOn = true;
		while(bGoOn)
		{
			bGoOn = false;
			//先处理正常边
			boolean bNormal = true;
			while(bNormal)
			{
				List<String> iplist_del = new ArrayList<String>();//处理过的交换ip
				bNormal = false;
//				for(FRM_AFTARP_LIST::iterator i = aft_list.begin();	i != aft_list.end(); ++i)
				for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
				{//对source ip 循环
					String ip_src = i.getKey();
//	                                for(std::map<string,list<string> >::iterator j = i->second.begin();
//						j != i->second.end();
//						++j)
					for(Entry<String, List<String>> j : i.getValue().entrySet())
					{//对source port 循环
						if(j.getValue().isEmpty())
						{
							continue;
						}
						String pt_src = j.getKey();

						if(isSWLeafPort(j.getValue(), sw_list))
						{//是交换叶子端口
							String ip_dest = (j.getValue().get(0));//->second.begin());

							if(aft_list.containsKey(ip_dest))
							{
								if(isSWLeafDevice(ip_src, aft_list.get(ip_dest)))
								{//对端是交换叶子,添加一条交换-交换边
									bNormal = true;
									String pt_dest = "PX";//目的设备的端口缺省为PX
									for(Entry<String, List<String>> j_dest : aft_list.get(ip_dest).entrySet())
									{//寻找对端设备中包含source ip的端口
										if(j_dest.getValue().contains(ip_src))
										{
											pt_dest = j_dest.getKey();
											j_dest.getValue().clear();//清空对端中包含source ip的端口
											break;
										}
									}
									Edge edge_tmp = new Edge();
									edge_tmp.setIp_left(ip_src);
									edge_tmp.setPt_left(pt_src);
									edge_tmp.setInf_left(pt_src);
									edge_tmp.setIp_right(ip_dest);
									edge_tmp.setPt_right(pt_dest);
									edge_tmp.setInf_right(pt_dest);
									edge_list_ss.add(edge_tmp);
									if(iplist_del.contains(ip_dest))
									{                                                                  
										iplist_del.add(ip_dest);
									}

									j.getValue().clear();//清空source port集合
								}
								else if(isInclude(aft_list.get(ip_dest), aft_list.get(ip_src), ip_src))
								{
									bNormal = true;
									String pt_dest = "PX";//目的设备的端口缺省为PX
//	                                                                for(std::map<string,list<string> >::iterator j_dest = aft_list[ip_dest].begin();
//										j_dest != aft_list[ip_dest].end();
//										++j_dest)
									for(Entry<String, List<String>> j_dest : aft_list.get(ip_dest).entrySet())
									{//寻找对端设备中非空端口
										if(!(j_dest.getValue().isEmpty()))
										{
											pt_dest = j_dest.getKey();
											j_dest.getValue().clear();////清空对端中包含source ip的端口
											break;
											
										}
									}
									Edge edge_tmp = new Edge();
									edge_tmp.setIp_left(ip_src);
									edge_tmp.setPt_left(pt_src);
									edge_tmp.setInf_left(pt_src);
									edge_tmp.setIp_right(ip_dest);
									edge_tmp.setPt_right(pt_dest);
									edge_tmp.setInf_right(pt_dest);
									edge_list_ss.add(edge_tmp);

									if(iplist_del.contains(ip_dest))
									{
										iplist_del.add(ip_dest);
									}
									j.getValue().clear();//清空source port集合
								}
							}
						}
					}
				}
			
				//删除处理后的dest交换ip
				for(String ip_del : iplist_del)
				{
					for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
					{//对source ip 循环
						String ip_src = i.getKey();
						for(Entry<String, List<String>> j : i.getValue().entrySet())
						{//对source port 循环
							j.getValue().remove(ip_del);
						}
					}
				}
			}

			//再处理环路
			//只要存在一台设备,其不同端口能够到达同一设备,则一定存在环路
			//若存在环路,则在处理完正常边后,需要继续处理正常边
			boolean bCircle = true;
			//myfile.saveFrmSSAftList(aft_list,test++);
			//已经可以计算环路
			while(bCircle)
			{
				bCircle = false;
				for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
				{//对source ip 循环
					if(i.getValue().size() > 1)
					{//可能存在环路(存在多个端口）
						String ip_src = i.getKey();//source ip
	                    Map<String,List<String>> k_start = new HashMap<String,List<String>>();
	                    List<Pair<String,String>> sameptip_list = new ArrayList<Pair<String,String>>(); //相同的 <port, dest ip>这个
																	//是source ip下具有相同的目标ip的端口和目标IP的对列表
						List<String> src_pt_list = new ArrayList<String>();//环路中i->first的端口
						List<String> dest_pt_list =  new ArrayList<String>();//环路中k->first的端口
						String dest_ip = "";
						String src_ip = "";
						boolean cycleflag = false;
						int cycle = 0;
						int cycle2 = 0;
						for(Entry<String, List<String>> j : i.getValue().entrySet())
						{//对source port 循环
							if(cycleflag)//一次只找两个ip的环路
								break;
//							k_start = j;
//							k_start++; // = j+1
							for(String ip_j : j.getValue())
							{
								if(cycleflag)//一次只找两个ip的环路
									break;
								for(Entry<String, List<String>> k : i.getValue().entrySet())
								{//比较 j,k 端口集合的元素
									for(String ip_k : k.getValue())
									{//k端口下的destip循环（ip_k）
										if(ip_k.equals(ip_j))
										{//k,j端口下存在相同的destip
											if(!cycleflag)
											{
												dest_ip = ip_j;
												src_ip  = i.getKey();
												cycleflag = true;
											}
											boolean bk = true, bj = true;
											for(String pt1 : src_pt_list)
											{
												if(pt1.equals(j.getKey()))
												{
													bj = false;
													break;
												}
											}
											for(String pt2 : src_pt_list)
											{
												if(pt2.equals(k.getKey()))
												{
													bk = false;
													break;
												}
											}
											if(bk)
											{
												src_pt_list.add(k.getKey());
												cycle++;
											}
											if(bj)
											{
												src_pt_list.add(j.getKey());
												cycle++;
											}
											break;
										}
									}
								}
							}
						}
						if(cycleflag)
						{//存在环路
							bGoOn = true; //必须继续进行正常分析
							//将环路添加到边集合
							Map<String,List<String>> ps_peer = aft_list.get(dest_ip);
							if(ps_peer != null)
							{
								for(Entry<String,List<String>> j_peer : ps_peer.entrySet())
								{//对端端口集合
									for(Iterator<String> iter = j_peer.getValue().iterator();iter.hasNext();)
									{//对端端口集合中的元素
										String ip_peer = iter.next();
										if(ip_peer.equals(ip_src))
										{//得到一个对边端口
											dest_pt_list.add(j_peer.getKey());
											iter.remove();
											break;
										}
									}
								}
								if(cycle2>0)
								{
									for(int index= 0;index < src_pt_list.size() && index < dest_pt_list.size();index ++)
									{
										String spt = src_pt_list.get(index);
										String dpt = dest_pt_list.get(index);
										bCircle = true;
										Edge edge_tmp = new Edge();
										edge_tmp.setIp_left(src_ip);
										edge_tmp.setPt_left(spt);
										edge_tmp.setInf_left(spt);
										edge_tmp.setIp_right(dest_ip);
										edge_tmp.setPt_right(dpt);
										edge_tmp.setInf_right(dpt);
										edge_list_ss.add(edge_tmp);
									}
								}
							}
						
							
							//在source 端删除环路设备
							for(Entry<String, List<String>> j : i.getValue().entrySet())
							{//对source port 循环
								for(Iterator<String> iter = j.getValue().iterator();iter.hasNext();)
								{//对dest ip
									String k = iter.next();
									if(k.equals(dest_ip))
									{
										iter.remove();
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		//todo:剩余的交换数据,逐条连接即可
		for(Entry<String,Map<String,List<String>>> i : aft_list.entrySet())
		{//对source ip 循环
			String ip_src = i.getKey();
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//对source port 循环
				String pt_src = j.getKey();
				for(String k : j.getValue())
				{//对dest ip 循环
					Map<String,List<String>> res_dest = aft_list.get(k);
					if(res_dest != null)
					{//对端存在于aft中
						for(Entry<String,List<String>> m : res_dest.entrySet())
						{
							if(m.getValue().contains(ip_src))//find(m->second.begin(), m->second.end(), ip_src) != m->second.end())
							{
								//添加一条ss边
								Edge edge_tmp = new Edge();
								edge_tmp.setIp_left(ip_src);
								edge_tmp.setPt_left(pt_src);
								edge_tmp.setInf_left(pt_src);
								edge_tmp.setIp_right(k);
								edge_tmp.setPt_right(m.getKey());
								edge_tmp.setInf_right(m.getKey());
								edge_list_ss.add(edge_tmp);
								//从对端端口集合中删除源ip
								m.getValue().remove(ip_src);
							}
						}
					}
				}
			}
		}
		StringBuffer line = new StringBuffer("");
		for(Edge i : edge_list_ss)
		{
			line.append(i.getIp_left() + ":" +i.getInf_left() + "--" +i.getIp_right() + ":" + i.getInf_right() + ";");//ini->ip_left+":"+i->inf_left+"--"+i->ip_right+":"+i->inf_right+";";
			
		}
		FileWriter output = null;
		
		try {
			output= new FileWriter(new File("edge_ss.txt"));
			output.write(line.toString());
			output.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for(Edge i : edge_list_ss)
		{
			if(i.getInf_left().equals("PX") || i.getInf_right().equals("PX"))
			{
				//用连通集来决定是否加入此连接关系
				boolean bNew = true;
				TraceAnalyse.getConnection(topo_edge_list);
				for(List<String> j : TraceAnalyse.set_conn)
				{
					if(j.contains(i.getIp_left()) && j.contains(i.getIp_right()))
					{
						//属于同一连通集
						bNew = false;
						break;
					}
				}
				if(bNew)
				{
					topo_edge_list.add(i);
				}
			}
			else
			{
				boolean bNew = true;
				for(Edge j : topo_edge_list)
				{
					if( (i.getIp_left().equals(j.getIp_left()) && i.getInf_left().equals(j.getInf_left())) || 
						(i.getIp_right().equals(j.getIp_right()) && i.getInf_right().equals(j.getInf_right())) ||
						(i.getIp_left().equals(j.getIp_right()) && i.getInf_left().equals(j.getInf_right())) ||
						(i.getIp_right().equals(j.getIp_left()) && i.getInf_right().equals(j.getInf_left())))
					{
						bNew = false;
						break;
					}
				}
				if(bNew)
				{
					topo_edge_list.add(i);
				}
			}
		}

		StringBuffer line1 = new StringBuffer("");
		for(Edge i : topo_edge_list)
		{
			line1.append(i.getIp_left()+":"+i.getInf_left()+"--"+i.getIp_right()+":"+i.getInf_right()+";");
		}
		FileWriter output1 = null;
		try {
			output1 = new FileWriter(new File("edge_ssss.txt"));
			output1.write(line1.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(output1 != null){
				try {
					output1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}
	public boolean analyseSH(){
		List<Edge> edge_list_sh = new ArrayList<Edge>();
		//add by wings 2009-11-26  
		//优先确定连有一个pc的交换机端口
		for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
		{//对source ip 循环
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//对source port 循环
				if(j.getValue().size() == 1)
				{
					String ip_dest = j.getValue().get(0);
					if(!sw_list.contains(ip_dest))
					{
						String ip_src = i.getKey();
						String pt_src = j.getKey();
						String pt_dest = "PX";
						if(arp_list.containsKey(ip_dest))//.find(*ip_dest) != arp_list.end())
						{//寻找对应的端口(接口)
							for(Entry<String, List<String>> i_arp : arp_list.get(ip_dest).entrySet())
							{
								if(i_arp.getValue().contains(ip_src))
								{
									pt_dest = i_arp.getKey();
									break;
								}
							}
						}
							//保证每个Host只出现一次
							boolean bNew = true;
							for(Edge ie : edge_list_sh)
							{
								if(ie.getIp_right().equals(ip_dest) && pt_dest.equals(ie.getPt_right()))
								{
									bNew = false;
									break;
								}
							}
							if(bNew)
							{
								Edge edge_tmp = new Edge();
								edge_tmp.setIp_left(ip_src);//.ip_left  = ip_src;
								edge_tmp.setPt_left(pt_src);
								edge_tmp.setInf_left(pt_src);
								edge_tmp.setIp_right(ip_dest);
								edge_tmp.setPt_right(pt_dest);
								edge_tmp.setInf_right(pt_dest);
								edge_list_sh.add(edge_tmp);
							}	
						

					}
				}
			}
		}
		//第二确定二层交换和pc边
		for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
		{//对source ip 循环
			String ip_src = i.getKey();
			if(sw3_list.contains(ip_src))
			{
				continue;
			}
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//对source port 循环
				if(isLeafPort(j.getValue(), sw_list))
				{//是叶子端口
					String pt_src = j.getKey();//->first; 
					for(String k : j.getValue())
					{//对dest ip 循环,为每个目的ip添加一条边
						String pt_dest = "PX";
						if(arp_list.containsKey(k))
						{//寻找对应的端口(接口)
							for(Entry<String, List<String>> i_arp : arp_list.get(k).entrySet())
							{
								if(i_arp.getValue().contains(ip_src))
								{
									pt_dest = i_arp.getKey();
									break;
								}
							}
						}
						//保证每个Host只出现一次
						boolean bNew = true;
						for(Edge ie : edge_list_sh)
						{
							if(k.equals(ie.getIp_right()) && pt_dest.equals(ie.getPt_right()))
							{
								bNew = false;
								break;
							}
						}
						if(bNew)
						{
							Edge edge_tmp = new Edge();
							edge_tmp.setIp_left(ip_src);//.ip_left  = ip_src;
							edge_tmp.setPt_left(pt_src);
							edge_tmp.setInf_left(pt_src);
							edge_tmp.setIp_right(k);
							edge_tmp.setPt_right(pt_dest);
							edge_tmp.setInf_right(pt_dest);
							edge_list_sh.add(edge_tmp);
							
						}					
					}//对dest ip 循环
				}//是叶子端口
			}
		}
		//再确定三层交换机和pc边
		for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
		{//对source ip 循环
			String ip_src = i.getKey();
			if(sw2_list.contains(ip_src))
			{
				continue;
			}
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//对source port 循环
				if(isLeafPort(j.getValue(), sw_list))
				{//是叶子端口
					String pt_src = j.getKey();
					for(String k : j.getValue())
					{//对dest ip 循环,为每个目的ip添加一条边
						String pt_dest = "PX";
						if(arp_list.containsKey(k))
						{//寻找对应的端口(接口)
							for(Entry<String, List<String>> i_arp : arp_list.get(k).entrySet())
							{
								if(i_arp.getValue().contains(ip_src))
								{
									pt_dest = i_arp.getKey();
									break;
								}
							}
						}
						//保证每个Host只出现一次
						boolean bNew = true;
						for(Edge ie : edge_list_sh)
						{
							if(ie.getIp_right().equals(k) && ie.getPt_right().equals(pt_dest))
							{
								bNew = false;
								break;
							}
						}
						if(bNew)
						{
							Edge edge_tmp = new Edge();
							edge_tmp.setIp_left(ip_src);
							edge_tmp.setPt_left(pt_src);
							edge_tmp.setInf_left(pt_src);
							edge_tmp.setIp_right(k);
							edge_tmp.setPt_right(pt_dest);
							edge_tmp.setInf_right(pt_dest);
							edge_list_sh.add(edge_tmp);
						}					
					}//对dest ip 循环
				}//是叶子端口
			}
		}
		// 让交换机与路由器的连接关系优先于交换机与主机的连接关系
		for(Edge i : edge_list_sh)
		{
			boolean bNew = true;
			for(Edge j : edge_list_rs_byaft)
			{
				if((j.getIp_left().equals(i.getIp_left()) && j.getPt_left().equals(i.getPt_left()))
						|| (j.getIp_right().equals(i.getIp_right()) && j.getPt_right().equals(i.getPt_right()))
						|| (j.getIp_left().equals(i.getIp_right()) && j.getPt_left().equals(i.getPt_right()))
						|| (j.getIp_right().equals(i.getIp_left()) && j.getPt_right().equals(i.getPt_left()))
						)
				{
					bNew = false;
					break;
				}
			}
			if(bNew)
			{
				topo_edge_list.add(i);
			}
		}

		//将主机ip全部抛弃
		for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
		{//对source ip 循环
			String ip_src = i.getKey();
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//对source port 循环
				for(Iterator<String> iter = j.getValue().iterator();iter.hasNext();)
				{//对dest ip 循环
					String k = iter.next();
					if(!sw_list.contains(k))
					{
						iter.remove();
					}
				}
			}
		}
		return true;
	}
	// 判定一个端口集是否叶子端口
	public boolean isLeafPort(List<String> iplist, List<String> dv_list)
	{
		if(iplist.isEmpty())
		{//没有元素的端口,定义为非叶子端口
			return false;
		}
		for(String i : dv_list)
		{
			if(iplist.contains(i))
			{//dest ip中存在交换设备
				return false;
			}
		}
		return true;
	}
	public boolean analyseRS()
	{
		edge_list_rs_byaft.clear();		
		for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
		{//对source ip 循环
			String ip_src = i.getKey();
	        for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//对source port 循环
				boolean bSkip = false;
				for(String isw : sw_list)
				{
					if(j.getValue().contains(isw))
					{
						bSkip = true;
						break;
					}
				}
				if(bSkip)
				{//跳过存在交换设备的接口集合
					continue;
				}

				String pt_src = j.getKey();
				for(String k : j.getValue())
				{//对dest ip 循环
					String ip_dest = k;
					if(rt_list.contains(k))
	                                {//目的ip为router
						String pt_dest = "PX";//目的设备的端口缺省为PX
						if(arp_list.containsKey(ip_dest))
						{//dest ip存在于arp数据列表中
							for(Entry<String, List<String>> j_dest : arp_list.get(ip_dest).entrySet())
							{//寻找对端设备中包含source ip的端口
								if(j_dest.getValue().contains(ip_src))
								{
									pt_dest = j_dest.getKey();
									break;
								}
							}
						}
						if(pt_dest != "PX")
						{//忽略不能找到目的端口的边
							Edge edge_tmp = new Edge();
							edge_tmp.setIp_left(ip_src);
							edge_tmp.setPt_left(pt_src);
							edge_tmp.setInf_left(pt_src);
							edge_tmp.setIp_right(ip_dest);
							edge_tmp.setPt_right(pt_dest);
							edge_tmp.setInf_right(pt_dest);
							edge_list_rs_byaft.add(edge_tmp);
							break;//在交换机的当前端口下找到了一台可连接的路由器，则退出当前dest ip循环  added by zhangyan 2008-10-28
						}					
					}
				}
			}
		}
		
		//删除aft中所有的router ip
		for(String rt : rt_list)
		{
			Set<String> keys = aft_list.keySet();
			for(String key : keys)
			{//对source ip 循环
				Map<String,List<String>> map_tmp = aft_list.get(key);
				Set<String> ks = map_tmp.keySet();
				for(String ss : ks)
				{//对source port 循环
					List<String> tmp = map_tmp.get(ss);
					tmp.remove(rt);
				}
			}
		}
		
		// 优先插入交换机与路由器的连接关系
		topo_edge_list.addAll(edge_list_rs_byaft);
		return true;
	}
	// 判定一个端口是否仅连接了一台交换设备
	public boolean isSWLeafPort(List<String> iplist,List<String> sw_list){
		if(iplist.size() == 1){
			if(sw_list.contains(iplist.get(0))){
				return true;
			}
		}
		return false;
	}
	public List<Edge> getTopo_edge_list() {
		return topo_edge_list;
	}

	public void setTopo_edge_list(List<Edge> topo_edge_list) {
		this.topo_edge_list = topo_edge_list;
	}
	
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String,String>();
		map.put("a", "a");
		map.put("b", "b");
		map.put("c", "c");
		map.put("d", "d");
		Object[] tmps = map.keySet().toArray();
		
		Object iteriter[] = map.entrySet().toArray();
		for(int i=0;i<iteriter.length;i++){
			Entry<String, String> iter = (Entry<String, String>)iteriter[i];
			System.out.println("key:" + iter.getKey() + " value:" + iter.getValue() );
			
			for(int j=i+1;j<iteriter.length;j++){
				Entry<String, String> tmp = (Entry<String, String>)iteriter[j];
				System.out.println("key:" + tmp.getKey() + " value:" + tmp.getValue() );
			}
		}
	}
}
