package com.siteview.snmp.scan;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Bgp;
import com.siteview.snmp.pojo.IDBody;

public class TraceReader {

	private Map<String, IDBody> devidList = new HashMap<String, IDBody>();
	private List<Bgp> bgpList = new ArrayList<Bgp>();
	private int retrys;
	private int timeout;
	private int hops;
	public List<Pair<Pair<String, String>, String>> routeDESTIPPairList = new ArrayList<Pair<Pair<String, String>, String>>();//[<<localip,localport>, peerip>]
	public List<String> unManagedDevices = new ArrayList<String>();//不可网管设备IP列表
	private List<List<String>> traceRoute_list = new ArrayList<List<String>>();
	private List<String> oneRoutePath = new ArrayList<String>();
 	public TraceReader(int retry,int timeout,int hops){
		this.retrys = retry;
		this.timeout = timeout;
		this.hops = hops;
	}
	public TraceReader(Map<String,IDBody> devidList,List<Bgp> bgpList,int retry,int timeout,int hops){
		this(retry,timeout,hops);
		this.devidList = devidList;
		this.bgpList = bgpList;
		
	}
	public void tracePrepare()
	{	
		routeDESTIPPairList.clear();
		unManagedDevices.clear();
		// 获得trace目的设备	
		for (Bgp i : bgpList)
		{		
			boolean bOK = true;
			for (Entry<String, IDBody> j : devidList.entrySet())
			{			
//				if (find(j->second.ips.begin(), j->second.ips.end(), i->peer_ip) != j->second.ips.end() && (j->second.devType != "5"))
				if(j.getValue().getIps().contains(i.getPeer_ip()) && !j.getValue().getDevType().equals("5"))
				{					
					bOK = false;
					break;
				}			
			}
			if (bOK)
			{//对端为不可管理设备时
				String local_ip = i.getLocal_ip();
				if (devidList.get(local_ip)!=null)//.find(local_ip) == devid_list.end())
				{
//					for (DEVID_LIST::iterator j = devid_list.begin(); j != devid_list.end(); ++j)
					for(Entry<String,IDBody> j : devidList.entrySet())
					{			
//						if (find(j->second.ips.begin(), j->second.ips.end(), local_ip) != j->second.ips.end())
						if(j.getValue().getIps().contains(local_ip))
						{					
							local_ip = j.getKey();
							break;
						}
					}
				}
				routeDESTIPPairList.add(new Pair<Pair<String,String>,String>(new Pair<String,String>(local_ip, i.getLocal_port()), i.getPeer_ip()));			
//				if (find(unManagedDevices.begin(), unManagedDevices.end(), i->peer_ip) == unManagedDevices.end())
				if(!unManagedDevices.contains(i.getPeer_ip()))
				{
					unManagedDevices.add(i.getPeer_ip());				
				}	
			}	
		}
	}
	public List<List<String>> getTraceRouteByIPs(){
		traceRoute_list.clear();
		for(Pair<Pair<String,String>,String> i : routeDESTIPPairList){
			getOneRoutePath(i.getFirst().getFirst());
		}
		return traceRoute_list;
	}
	/**
	 *  获取设备的trace route信息
	 * @param id
	 */
	public void getOneTraceRouteData(String ip){
		//读取一条traceroute路径
		List<String> onePath = getOneRoutePath(ip);
		if(onePath.size()>0){
			traceRoute_list.add(onePath);
		}
	}
	public List<String> getOneRoutePath(String ip){
		oneRoutePath.clear();
		for(int i=0;i<=hops;i++){
//			int rtn = ????????????????????????????????????????????????????????????????
		}
		return oneRoutePath;
	}
	//发送ICMP包进行路由跟踪
	public int traceRouteICMP(String cHost,int ttl){
		int seq_no = 0;
		int rtn = 0;
		 try {
			InetAddress.getByName(cHost).isReachable(ttl);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rtn;
	}
}
