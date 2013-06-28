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
	

	public boolean edgeAnalyse(){
		if(!topo_edge_list.isEmpty()){
			topo_edge_list.clear();
		}
		analyseRS();
		analyseSH();
		analyseSS();
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

//							if(aft_list.find(ip_dest) != aft_list.end())
							if(aft_list.containsKey(ip_dest))
							{
								if(isSWLeafDevice(ip_src, aft_list.get(ip_dest)))
								{//对端是交换叶子,添加一条交换-交换边
									bNormal = true;
									String pt_dest = "PX";//目的设备的端口缺省为PX
//	                                                                for(std::map<string,list<string> >::iterator j_dest = aft_list[ip_dest].begin();
//										j_dest != aft_list[ip_dest].end();
//										j_dest++)
									for(Entry<String, List<String>> j_dest : aft_list.get(ip_dest).entrySet())
									{//寻找对端设备中包含source ip的端口
//										if(find(j_dest->second.begin(), j_dest->second.end(), ip_src) != j_dest->second.end())
										if(j_dest.getValue().contains(ip_src))
										{
											pt_dest = j_dest.getKey();
											j_dest.getValue().clear();//->second.clear();//清空对端中包含source ip的端口
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
									edge_list_ss.add(edge_tmp);//.push_back(edge_tmp);

//	                                                                qDebug() << "single switch, source ip : " << ip_src.c_str() << " dest ip : " << ip_dest.c_str();

//									if(find(iplist_del.begin(), iplist_del.end(), ip_dest) == iplist_del.end())
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
											pt_dest = j_dest.getKey();//->first;
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

//									if(find(iplist_del.begin(), iplist_del.end(), ip_dest) == iplist_del.end())
									if(iplist_del.contains(ip_dest))
									{
//	                                                                        qDebug() << "include source ip : " << ip_src.c_str() << " dest ip : " << ip_dest.c_str();
										iplist_del.add(ip_dest);
									}
									j.getValue().clear();//清空source port集合
								}
							}
						}
					}
				}
			
				//删除处理后的dest交换ip
//				for(list<string>::iterator ip_del = iplist_del.begin();
//					ip_del != iplist_del.end();
//					++ip_del)
				for(String ip_del : iplist_del)
				{
//					for(FRM_AFTARP_LIST::iterator i = aft_list.begin();
//						i != aft_list.end();
//						++i)
					for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
					{//对source ip 循环
						String ip_src = i.getKey();//->first;
//	                                        for(std::map<string,list<string> >::iterator j = i->second.begin();
//							j != i->second.end();
//							++j)
						
						for(Entry<String, List<String>> j : i.getValue().entrySet())
						{//对source port 循环
							j.getValue().remove(ip_del);
//							j->second.remove(*ip_del);
						}
					}
				}
			}

			//再处理环路
			//只要存在一台设备,其不同端口能够到达同一设备,则一定存在环路
			//若存在环路,则在处理完正常边后,需要继续处理正常边
			boolean bCircle = true;
			//myfile.saveFrmSSAftList(aft_list,test++);
			//update by wings 2009-11-13  
			//已经可以计算环路
			while(bCircle)
			{
				bCircle = false;
//				for(FRM_AFTARP_LIST::iterator i = aft_list.begin();	i != aft_list.end(); ++i)
				for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
				{//对source ip 循环
					if(i.getValue().size() > 1)
					{//可能存在环路(存在多个端口）
						String ip_src = i.getKey();//source ip
	                    Map<String,List<String>> k_start = new HashMap<String,List<String>>();
//	                                        std::map<string,list<string> >::iterator j_end = i->second.end();
//						j_end--;
	                    List<Pair<String,String>> sameptip_list = new ArrayList<Pair<String,String>>(); //相同的 <port, dest ip>这个
																	//是source ip下具有相同的目标ip的端口和目标IP的对列表
						List<String> src_pt_list = new ArrayList<String>();//环路中i->first的端口
						List<String> dest_pt_list =  new ArrayList<String>();//环路中k->first的端口
						String dest_ip = "";
						String src_ip = "";
						boolean cycleflag = false;
						int cycle = 0;
						int cycle2 = 0;
//	                                        for(std::map<string,list<string> >::iterator j = i->second.begin();
//							j != j_end; 
//							++j)
						for(Entry<String, List<String>> j : i.getValue().entrySet())
						{//对source port 循环
							if(cycleflag)//一次只找两个ip的环路
								break;
//							k_start = j;
//							k_start++; // = j+1
//							for(std::list<string>::iterator ip_j = j->second.begin();
//								ip_j != j->second.end();
//								++ip_j)//ip_j是destip
							for(String ip_j : j.getValue())
							{
								if(cycleflag)//一次只找两个ip的环路
									break;
//	                                                        for(std::map<string,list<string> >::iterator k = k_start;//j+1开始
//									k != i->second.end();
//									++k)
								for(Entry<String, List<String>> k : i.getValue().entrySet())
								{//比较 j,k 端口集合的元素
//									for(list<string>::iterator ip_k = k->second.begin();
//										ip_k != k->second.end();
//										++ip_k)
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
//											for(List<String> pt1 = src_pt_list.begin();
//												pt1 != src_pt_list.end();
//												++pt1)
											for(String pt1 : src_pt_list)
											{
												if(pt1.equals(j.getKey()))
												{
													bj = false;
													break;
												}
											}
//											for(list<string>::iterator pt2 = src_pt_list.begin();
//												pt2 != src_pt_list.end();
//												++pt2)
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
//	                                                        for(map<string,list<string> >::iterator j_peer = ps_peer->second.begin();
//									j_peer != ps_peer->second.end();
//									++j_peer)
								for(Entry<String,List<String>> j_peer : ps_peer.entrySet())
								{//对端端口集合
//									for(list<string>::iterator ip_peer = j_peer->second.begin();
//										ip_peer != j_peer->second.end();
//										)
//									for(String ip_peer : j_peer.getValue())
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
//									String spt = src_pt_list.get(0);
//									String dpt = dest_pt_list.get(0);
//									for(;spt != src_pt_list.end()&&dpt != dest_pt_list.end();
//										++spt,++dpt)
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
	                                                                        ////SvLog::writeLog("one cycle edge"+edge_tmp.ip_left+":"+edge_tmp.inf_left+
										//"--"+edge_tmp.ip_right+":"+edge_tmp.inf_right);	
										edge_list_ss.add(edge_tmp);
									}
								}
							}
						
							
							//在source 端删除环路设备
//	                                                for(std::map<string,list<string> >::iterator j = i->second.begin();
//								j != i->second.end();
//								++j)
							for(Entry<String, List<String>> j : i.getValue().entrySet())
							{//对source port 循环
//								for(list<string>::iterator k = j->second.begin();
//									k != j->second.end();
//									)
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
			//myfile.saveFrmSSAftList(aft_list,test++);
		}
		//todo:剩余的交换数据,逐条连接即可
//		for(FRM_AFTARP_LIST::iterator i = aft_list.begin();
//			i != aft_list.end();
//			++i)
		for(Entry<String,Map<String,List<String>>> i : aft_list.entrySet())
		{//对source ip 循环
			String ip_src = i.getKey();
//	                for(std::map<string,list<string> >::iterator j = i->second.begin();
//				j != i->second.end();
//				++j)
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//对source port 循环
				String pt_src = j.getKey();
//				for(list<string>::iterator k = j->second.begin();
//					k != j->second.end();
//					++k)
				for(String k : j.getValue())
				{//对dest ip 循环
					Map<String,List<String>> res_dest = aft_list.get(k);
					if(res_dest != null)
					{//对端存在于aft中
//	                                        for(std::map<string,list<string> >::iterator m = res_dest->second.begin();
//							m != res_dest->second.end();
//							++m)
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
//	                                                        qDebug() << "last edge source ip : " << ip_src.c_str() << " dest ip : " << (*k).c_str();

								m.getValue().remove(ip_src);
							}
							//else
							//{
							//	EDGE edge_tmp;
							//	edge_tmp.ip_left  = ip_src;
							//	edge_tmp.pt_left  = pt_src;
							//	edge_tmp.inf_left = pt_src;
							//	edge_tmp.ip_right  = *k;
							//	edge_tmp.pt_right  ="PX";
							//	edge_tmp.inf_right ="PX";
							//	edge_list_ss.push_back(edge_tmp);
							//	//从对端端口集合中删除源ip
							//	m->second.remove(ip_src); //update by jiangshanwen 20100607
							//}
						}
					}
				}
			}
		}
		//add by wings 2009-11-13
//		ofstream output("test/edge_ss.txt",ios::out);
		
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
		
//		for(EDGE_LIST::iterator i = edge_list_ss.begin(); 
//			i != edge_list_ss.end(); 
//			++i)
		for(Edge i : edge_list_ss)
		{
			// added by zhangyan 2008-12-04
//			if (i->inf_left == "PX" || i->inf_right == "PX")
			if(i.getInf_left().equals("PX") || i.getInf_right().equals("PX"))
			{
				//用连通集来决定是否加入此连接关系
				boolean bNew = true;
				TraceAnalyse.getConnection(topo_edge_list);
//	            for(std::list<std::list<std::string> >::iterator j = TraceAnalyse::set_conn.begin();	j != TraceAnalyse::set_conn.end(); ++j)
				for(List<String> j : TraceAnalyse.set_conn)
				{
//					if ((find(j->begin(), j->end(), i->ip_left) != j->end()) && (find(j->begin(), j->end(), i->ip_right) != j->end()))
					if(j.contains(i.getIp_left()) && j.contains(i.getIp_right()))
					{
						//属于同一连通集
						bNew = false;
						break;
					}
				}
				if(bNew)
				{
//					cout<<"AnalyseSS:"<<i->ip_left<<" "<<i->pt_left<<" "<<i->ip_right<<" "<<i->pt_right<<endl;
					topo_edge_list.add(i);
				}
			}
			else
			{
				boolean bNew = true;
//				for(Edge j = topo_edge_list.begin();
//					j != topo_edge_list.end();
//					++j)
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
//					cout<<"AnalyseSS:"<<i->ip_left<<" "<<i->pt_left<<" "<<i->ip_right<<" "<<i->pt_right<<endl;
					topo_edge_list.add(i);
				}
			}
		}

		//add by wings 2009-11-13
//		ofstream output1("test/edge_ssss.txt",ios::out);
		
//		for(EDGE_LIST::const_iterator i = topo_edge_list.begin(); i != topo_edge_list.end(); i++)
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
//	                for(std::map<string,list<string> >::iterator j = i->second.begin(); j != i->second.end(); ++j)
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//对source port 循环
				if(j.getValue().size() == 1)//->second.size() == 1)
				{
//					list<string>::iterator ip_dest = j->second.begin();
					String ip_dest = j.getValue().get(0);
//					if(find(sw_list.begin(),sw_list.end(),*ip_dest) == sw_list.end())
					if(!sw_list.contains(ip_dest))
					{
						String ip_src = i.getKey();
						String pt_src = j.getKey();
						String pt_dest = "PX";
						if(arp_list.containsKey(ip_dest))//.find(*ip_dest) != arp_list.end())
						{//寻找对应的端口(接口)
//	                                                qDebug() << "single pc : " << (*ip_dest).c_str();
//	                                                for(std::map<string,list<string> >::iterator i_arp = arp_list[*ip_dest].begin();
//								i_arp != arp_list[*ip_dest].end();
//								++i_arp
//								)
							for(Entry<String, List<String>> i_arp : arp_list.get(ip_dest).entrySet())
							{
								if(i_arp.getValue().contains(ip_src))//find(i_arp->second.begin(), i_arp->second.end(), ip_src) != i_arp->second.end())
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
//								if(*ip_dest == ie->ip_right && pt_dest == ie->pt_right)
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
								edge_list_sh.add(edge_tmp);//.push_back(edge_tmp);
//	                                                        qDebug() << "single pc: " << ip_src.c_str() << ",dest ip: " << (*ip_dest).c_str();
	                                                        ////SvLog::writeLog("edge:"+edge_tmp.ip_left
								//	+edge_tmp.pt_left+edge_tmp.ip_right
								//	+edge_tmp.pt_right+"**************");
							}	
						

					}
				}
			}
		}
		//add by wings 2009-11-26 
		//第二确定二层交换和pc边
		for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
		{//对source ip 循环
			String ip_src = i.getKey();//->first;
//			if(find(sw3_list.begin(),sw3_list.end(),ip_src)!=sw3_list.end())
			if(sw3_list.contains(ip_src))
			{
				continue;
			}
//	                for(std::map<string,list<string> >::iterator j = i->second.begin(); j != i->second.end(); ++j)
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//对source port 循环
//				if(IsLeafPort(j->second, sw_list))
				if(isLeafPort(j.getValue(), sw_list))
				{//是叶子端口
//	                                SvLog::writeLog("see the LeafPort"+j->first+"  "+int2str(j->second.size()));
					String pt_src = j.getKey();//->first; 
//					for(list<string>::iterator k = j->second.begin();
//						k != j->second.end();
//						++k)
					for(String k : j.getValue())
					{//对dest ip 循环,为每个目的ip添加一条边
						String pt_dest = "PX";
//						if(arp_list.find(*k) != arp_list.end())
						if(arp_list.containsKey(k))
						{//寻找对应的端口(接口)
//	                                                for(std::map<string,list<string> >::iterator i_arp = arp_list[*k].begin();
//								i_arp != arp_list[*k].end();
//								++i_arp
//								)
							for(Entry<String, List<String>> i_arp : arp_list.get(k).entrySet())
							{
//								if(find(i_arp->second.begin(), i_arp->second.end(), ip_src) != i_arp->second.end())
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
//							if(*k == ie->ip_right && pt_dest == ie->pt_right)
							if(k.equals(ie.getIp_right()) && pt_dest.equals(ie.getPt_right()))
							{
//	                                                        qDebug() << "second switch the same ip : " << (*k).c_str();
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
							
//	                                                qDebug() << "second switch source ip:" << ip_src.c_str() <<  "dest ip:" << (*k).c_str();
							//cout<<"edge:"<<edge_tmp.ip_left<<" "<<edge_tmp.pt_left<<" "<<edge_tmp.ip_right<<" "<<edge_tmp.pt_right<<endl;
						}					
					}//对dest ip 循环
				}//是叶子端口
			}
		}
		//add by wings 2009-11-26
		//再确定三层交换机和pc边
		for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
		{//对source ip 循环
			String ip_src = i.getKey();
//			if(find(sw2_list.begin(),sw2_list.end(),ip_src)!=sw2_list.end())
			if(sw2_list.contains(ip_src))
			{
				continue;
			}
//	                for(std::map<string,list<string> >::iterator j = i->second.begin(); j != i->second.end(); ++j)
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//对source port 循环
				if(isLeafPort(j.getValue(), sw_list))
				{//是叶子端口
					String pt_src = j.getKey();
//					for(list<string>::iterator k = j->second.begin();
//						k != j->second.end();
//						++k)
					for(String k : j.getValue())
					{//对dest ip 循环,为每个目的ip添加一条边
						String pt_dest = "PX";
//						if(arp_list.find(*k) != arp_list.end())
						if(arp_list.containsKey(k))
						{//寻找对应的端口(接口)
//	                                                for(std::map<string,list<string> >::iterator i_arp = arp_list[*k].begin();
//								i_arp != arp_list[*k].end();
//								++i_arp
//								)
							for(Entry<String, List<String>> i_arp : arp_list.get(k).entrySet())
							{
//								if(find(i_arp->second.begin(), i_arp->second.end(), ip_src) != i_arp->second.end())
								if(i_arp.getValue().contains(ip_src))
								{
									pt_dest = i_arp.getKey();
									break;
								}
							}
						}
						//保证每个Host只出现一次
						boolean bNew = true;
//						for(EDGE_LIST::iterator ie = edge_list_sh.begin();
//							ie != edge_list_sh.end();
//							++ie)
						for(Edge ie : edge_list_sh)
						{
//							if(*k == ie->ip_right && pt_dest == ie->pt_right)
							if(ie.getIp_right().equals(k) && ie.getPt_right().equals(pt_dest))
							{
//	                                                        qDebug() << "third switch the same ip : " << pt_src.c_str() << ",dest ip" << (*k).c_str();
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
//	                                                qDebug() << "third switch source ip:" << ip_src.c_str() <<  "dest ip:" << (*k).c_str();
							//cout<<"edge:"<<edge_tmp.ip_left<<" "<<edge_tmp.pt_left<<" "<<edge_tmp.ip_right<<" "<<edge_tmp.pt_right<<endl;
						}					
					}//对dest ip 循环
				}//是叶子端口
			}
		}
		// remarked by zhangyan 2008-12-04
		// 让交换机与路由器的连接关系优先于交换机与主机的连接关系
		//topo_edge_list.insert(topo_edge_list.end(), edge_list_sh.begin(), edge_list_sh.end());
//		for(EDGE_LIST::iterator i = edge_list_sh.begin(); i != edge_list_sh.end(); ++i)
		for(Edge i : edge_list_sh)
		{
			boolean bNew = true;
//			for(EDGE_LIST::iterator j = edge_list_rs_byaft.begin();
//				j != edge_list_rs_byaft.end();
//				++j)
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
//				cout<<"AnalyseSH:"<<i->ip_left<<" "<<i->pt_left<<" "<<i->ip_right<<" "<<i->pt_right<<endl;
				topo_edge_list.add(i);//.push_back(*i);
			}
		}

		//将主机ip全部抛弃
//		for(FRM_AFTARP_LIST::iterator i = aft_list.begin();	i != aft_list.end(); ++i)
		for(Entry<String,Map<String,List<String>>> i :aft_list.entrySet())
		{//对source ip 循环
			String ip_src = i.getKey();
//	                for(std::map<string,list<string> >::iterator j = i->second.begin(); j != i->second.end(); ++j)
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//对source port 循环
//				for(list<string>::iterator k = j->second.begin();
//					k != j->second.end();
//					)
				for(Iterator<String> iter = j.getValue().iterator();iter.hasNext();)
				{//对dest ip 循环
//					if(find(sw_list.begin(), sw_list.end(), *k) == sw_list.end())
					String k = iter.next();
					if(!sw_list.contains(k))
					{
						iter.remove();
					}
//					else
//					{
//						++k;
//					}
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
			if(iplist.contains(i))//find(iplist.begin(), iplist.end(), *i) != iplist.end())
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
//				for(list<string>::iterator isw = sw_list.begin();
//					isw != sw_list.end();
//					++isw)
				for(String isw : sw_list)
				{
					if(j.getValue().contains(isw))//find(j->second.begin(), j->second.end(), *isw) != j->second.end())
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
//				for(list<string>::iterator k = j->second.begin();
//					k != j->second.end();
//					++k)
				for(String k : j.getValue())
				{//对dest ip 循环
					String ip_dest = k;
//					if(find(rt_list.begin(), rt_list.end(), ip_dest) != rt_list.end())
					if(rt_list.contains(k))
	                                {//目的ip为router
//	                                        qDebug() << "sourceip : " << pt_src.c_str() << "dest router : " << ip_dest.c_str();
						String pt_dest = "PX";//目的设备的端口缺省为PX
						if(arp_list.containsKey(ip_dest))//arp_list.find(ip_dest) != arp_list.end())
						{//dest ip存在于arp数据列表中
//	                                                for(std::map<string,list<string> >::iterator j_dest = arp_list[ip_dest].begin();
//								j_dest != arp_list[ip_dest].end();
//								++j_dest)
							for(Entry<String, List<String>> j_dest : arp_list.get(ip_dest).entrySet())
							{//寻找对端设备中包含source ip的端口
								if(j_dest.getValue().contains(ip_src))//find(j_dest->second.begin(), j_dest->second.end(), ip_src) != j_dest->second.end())
								{
									pt_dest = j_dest.getKey();//j_dest->first;
									break;
								}
							}
						}
						if(pt_dest != "PX")
						{//忽略不能找到目的端口的边
							Edge edge_tmp = new Edge();
							edge_tmp.setIp_left(ip_src);//.ip_left  = ip_src;
							edge_tmp.setPt_left(pt_src);//.pt_left  = pt_src;
							edge_tmp.setInf_left(pt_src);//.inf_left = pt_src;
							edge_tmp.setIp_right(ip_dest);//.ip_right  = ip_dest;
							edge_tmp.setPt_right(pt_dest);//.pt_right  = pt_dest;
							edge_tmp.setInf_right(pt_dest);//.inf_right = pt_dest;
							edge_list_rs_byaft.add(edge_tmp);//.push_back(edge_tmp);
//							cout<<"AnalyseRS:"<<edge_tmp.ip_left<<" "<<edge_tmp.pt_left<<" "<<edge_tmp.ip_right<<" "<<edge_tmp.pt_right<<endl;
							break;//在交换机的当前端口下找到了一台可连接的路由器，则退出当前dest ip循环  added by zhangyan 2008-10-28
						}					
					}
				}
			}
		}
		
		//删除aft中所有的router ip
		for(String rt : rt_list)
		{
//			for(FRM_AFTARP_LIST::iterator i = aft_list.begin();	i != aft_list.end(); ++i)
			Set<String> keys = aft_list.keySet();
			for(String key : keys)
			{//对source ip 循环
//	                        for(std::map<string,list<string> >::iterator j = i->second.begin(); j != i->second.end(); ++j)
				Map<String,List<String>> map_tmp = aft_list.get(key);
				Set<String> ks = map_tmp.keySet();
				for(String ss : ks)
				{//对source port 循环
					List<String> tmp = map_tmp.get(ss);
					tmp.remove(rt);
				}
			}
		}
		
		// added by zhangyan 2008-12-04 
		// 优先插入交换机与路由器的连接关系
		topo_edge_list.addAll(edge_list_rs_byaft);//.add(e).insert(topo_edge_list.end(), edge_list_rs_byaft.begin(), edge_list_rs_byaft.end());

		//printdata(aft_list);

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
	
	
}
