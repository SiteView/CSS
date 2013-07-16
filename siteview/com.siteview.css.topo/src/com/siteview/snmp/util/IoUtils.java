package com.siteview.snmp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;


import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Bgp;
import com.siteview.snmp.pojo.Directitem;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.pojo.IfRec;
import com.siteview.snmp.pojo.RouteItem;
import com.siteview.snmp.pojo.RouterStandbyItem;

/**
 * 保存文件的工具类
 * @author haiming.wang
 */
public class IoUtils {

	public static final String SPLIT_TOP    =  "[:::]";
	public static final String SPLIT_MAIN	=  "[::]";
	public static final String SPLIT_SUB	=  "[:]";
	/**
	 * 保存格式化后的arp表数据
	 * @param frm_aftarp_list
	 * @return
	 */
	public static boolean saveFrmArpList(Map<String, Map<String, List<String>>> frm_aftarp_list){
		StringBuffer line = new StringBuffer("");
		for(Entry<String, Map<String,List<String>>> i : frm_aftarp_list.entrySet()){
			if(i.getValue() == null || i.getValue().isEmpty()){
				continue;
			}
			line.append(i.getKey()).append("::");
			int indexj = 0;
			for(Entry<String, List<String>> j : i.getValue().entrySet()){
				indexj++;
				line.append(j.getKey()).append(":");
				int indexk = 0;
				for(String k : j.getValue()){
					line.append(k);
					indexk ++ ;
					if(indexk != j.getValue().size()){
						line.append(",");
					}
				}
				if(indexj != i.getValue().size() -1){
					line.append(";");
				}
			}
		}
		return writeData("Arp_FRM.txt", line.toString());
	}
	/**
	 * 保存格式化后的aft信息
	 * @param aft_list_frm aft数据集合
	 * @return
	 */
	public static boolean saveFrmAftList(Map<String, Map<String, List<String>>> aft_list_frm){
		StringBuffer line = new StringBuffer("");
		for(Entry<String, Map<String,List<String>>> i : aft_list_frm.entrySet()){
			if(i.getValue() == null || i.getValue().isEmpty()){
				continue;
			}
			line.append(i.getKey()).append("::");
			int indexj = 0;
			for(Entry<String,List<String>> j:i.getValue().entrySet()){
				indexj++;
				line.append(j.getKey()).append(":");
				int indexk = 0;
				for(String k :j.getValue()){
					indexk++;
					line.append(k);
					if(indexk != j.getValue().size()){
						line.append(",");
					}
				}
				if(indexj != i.getValue().size() - 1){
					line.append(";");
				}
			}
		}
		return writeData("Aft_FRM.txt", line.toString());
	}
	/**
	 * 保存文件
	 * @param fileName 文件名，目录为当前项目根目录
	 * @param data 文件内容
	 * @return
	 */
	public static boolean writeData(String fileName,String data){
		String path = getProductPath() + fileName;
		File file = new File(path);
		if(file.exists()){
			file.delete();
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(data);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if(fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
					fw = null;
					return true;
				}
			}
			fw = null;
		}
		return true;
	}
	// 用文件保存设备标识体数据列表
	public static boolean saveIDBodyData(Map<String, IDBody> devid_list)
	{
		String fileName = "DeviceInfos.txt";
		return writeData(fileName, buildDeviceLine(devid_list));
	}
	public static boolean saveArpList(Map<String, Map<String, List<Pair<String, String>>>> arp_list){
		StringBuffer line = new StringBuffer("");
		Iterator<Entry<String, Map<String,List<Pair<String,String>>>>> iter = arp_list.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Map<String,List<Pair<String,String>>>> i = iter.next();
			if(i.getValue() == null || i.getValue().isEmpty()){
				continue;
			}
			line.append(i.getKey()).append("::");
			Iterator<Entry<String,List<Pair<String,String>>>> jiter = i.getValue().entrySet().iterator();
			while(jiter.hasNext()){
				Entry<String,List<Pair<String,String>>> j = jiter.next();
				line.append(j.getKey()).append(":");
				Iterator<Pair<String,String>> kiter = j.getValue().iterator();
				while(kiter.hasNext()){
					Pair<String,String> k = kiter.next();
					line.append(k.getFirst()).append("-").append(k.getSecond());
					if(kiter.hasNext()){
						line.append(",");
					}
				}
				if(jiter.hasNext()){
					line.append(";");
				}
			}
			line.append("\r\n");
		}
		return writeData("Arp_ORG.txt", line.toString()); 
	}
	/**
	 * 保存设备的ip集合
	 * @param devid_list
	 * @return
	 */
	public static boolean savaDevidIps(Map<String, IDBody> devid_list){
		StringBuffer sb = new StringBuffer("");
		for(Entry<String, IDBody> i : devid_list.entrySet()){
			sb.append(i.getKey()).append("\r\n");
		}
		return writeData("deviceIps.txt", sb.toString());
	}
	/**
	 * 构造设备集合的文件内容
	 * @param devid_list
	 * @return
	 */
	private static String buildDeviceLine(Map<String,IDBody> devid_list){
		StringBuffer line = new StringBuffer("");
		Set<String> keys = devid_list.keySet();
		for(String key : keys){
			line.append(key + SPLIT_MAIN);//ip
			IDBody id = devid_list.get(key);
			line.append(id.getSnmpflag()).append(SPLIT_MAIN);
			line.append(id.getCommunity_get()).append(SPLIT_MAIN);
			line.append(id.getCommunity_set()).append(SPLIT_MAIN);
			line.append(id.getSysOid()).append(SPLIT_MAIN);
			line.append(id.getDevType()).append(SPLIT_MAIN);//设备类型
			line.append(id.getDevFactory()).append(SPLIT_MAIN);//设备厂家
			line.append(id.getDevModel()).append(SPLIT_MAIN);//设备型号
			line.append(id.getDevTypeName()).append(SPLIT_MAIN);//设备类型名称
			line.append(id.getBaseMac()).append(SPLIT_MAIN);//基本Mac地址
			line.append(id.getSysName()).append(SPLIT_MAIN);
			line.append(id.getSysSvcs()).append(SPLIT_MAIN);
			if(id.getIps()!=null && !id.getIps().isEmpty()){
				Vector<String> ip_j_end = id.getIps();
				for (int ip_j = 0, msk_j = 0, inf_j = 0; ip_j < id.getIps()
						.size()
						&& msk_j < id.getMsks().size()
						&& inf_j < id.getInfinxs().size(); ip_j++,msk_j++,inf_j++) {
					line.append(id.getIps().get(ip_j)).append("/").append(id.getMsks().get(msk_j)).append("/").append(id.getInfinxs().get(inf_j));
					if(ip_j != (id.getIps().size() - 1))
						line.append(SPLIT_SUB);
				}
			}
			line.append(SPLIT_MAIN);
			if(id.getMacs()!=null && !id.getMacs().isEmpty()){
				for(int mac_j =0 ; mac_j<id.getMacs().size();mac_j++){
					line.append(id.getMacs().get(mac_j));
					if(mac_j != id.getMacs().size()-1){
						line.append(SPLIT_SUB);
					}
				}
			}
			line.append("\r\n");
		}
		return line.toString();
	}
	// 用文件保存设备标识数据列表
	public static boolean saveFrmDevIDList(Map<String, IDBody> devid_list){
		return writeData("DeviceInfos_Frm.txt", buildDeviceLine(devid_list));
	}

	// 读取设备ip列表
	public static boolean readDeviceIpList(Vector<String> devip_list)
	{
		String path = System.getProperty("user.dir") + File.separator + "DeviceIps.txt";
		File file = new File(path);
		devip_list.clear();
		if(!file.exists()){
			return true;
		}
		InputStreamReader stream = null;
		try {
			stream = new InputStreamReader(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(stream);
			String lineTxt = "";
			while((lineTxt = reader.readLine()) != null){
			    devip_list.add(lineTxt);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	    return true;
	}
	public static boolean saveFrmSSAftList(Map<String,Map<String,List<String>>> aft_list_frm,int test)
	{
		StringBuffer line = new StringBuffer("");
		for(Entry<String, Map<String,List<String>>> i : aft_list_frm.entrySet())
		{
			if(i.getValue().isEmpty())
			{
				continue;
			}
			line.append(i.getKey()).append("::"); //管理IP
			int indexj = 0;
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//port 循环
				indexj++;
				if(j.getValue().isEmpty())
					continue;
				line.append(j.getKey()).append(":");
				int indexk = 0;
				for(String k : j.getValue())
				{//ip循环
					indexk ++ ;
					line.append(k);
					if(indexk != (j.getValue().size() - 1))
					{
						line.append(",");
					}
				}
				if(indexj != i.getValue().size() - 1)
				{
					line.append(";");
				}
			}
			writeData("Aft_FRM_SS_"+test+".txt", line.toString());
		}
		return true;
	}
	public static boolean readSwIpList(Vector<String> swipList){
		return true;
		
	}
	public static boolean saveTracertList(List<List<String>> tracert_list){
		StringBuffer line = new StringBuffer("");
		for(List<String> iter : tracert_list){
			for(String i : iter){
				line.append(i).append(",");
			}
			line.append("\r\n");
		}
		return writeData("Tracert.txt", line.toString());
	}
	public static boolean saveRenewTracertList(List<List<String>> tracert_list){
		StringBuffer line = new StringBuffer("");
		for(List<String> iter : tracert_list){
			for(String i : iter){
				line.append(i).append(",");
			}
			line.append("\r\n");
		}
		return writeData("Tracert_renew.txt", line.toString());
	}
	public static boolean saveAftList(Map<String, Map<String, List<String>>> aft_list){
		StringBuffer line = new StringBuffer("");
		for(Entry<String, Map<String,List<String>>> i : aft_list.entrySet()){
			if(i.getValue().isEmpty()){
				continue;
			}
			line.append(i.getKey()).append("::");//ip
			int indexj = 0;
			for(Entry<String,List<String>> j : i.getValue().entrySet()){
				indexj ++ ;
				line.append(j.getKey()).append(":");
				int indexk = 0;
				for(String k : j.getValue()){
					indexk++;
					line.append(k);
					if(indexk != j.getValue().size()){
						line.append(",");
					}
				}
				if(indexj != i.getValue().size()){
					line.append(";");
				}
				
			}
			line.append("\r\n");
		}
		return writeData("Aft_ORG.txt", line.toString());
	}
	/**
	 * save directData list data to Direct_Data.txt
	 * @param directdata_list
	 * @return
	 */
	public static boolean saveDirectData(Map<String, List<Directitem>> directdata_list){
		StringBuffer line = new StringBuffer();
		for(Entry<String, List<Directitem>> i : directdata_list.entrySet()){
			if(i.getValue() == null || i.getValue().isEmpty()){
				continue;
			}
			line.append(i.getKey()).append(SPLIT_TOP);//IP
			int indexj = 0;
			for(Directitem j : i.getValue()){
				line.append(j.getLocalPortInx()).append(SPLIT_SUB);
				line.append(j.getLocalPortDsc()).append(SPLIT_SUB);
				line.append(j.getPeerId()).append(SPLIT_SUB);
				line.append(j.getPeerIp()).append(SPLIT_SUB);
				line.append(j.getPeerPortInx()).append(SPLIT_SUB);
				line.append(j.getPeerPortDsc()).append(SPLIT_SUB);
				if(++indexj != i.getValue().size()){
					line.append(SPLIT_MAIN);
				}
				
			}
			line.append("\r\n");
				
		}
		return writeData("Direct_Data.txt", line.toString());
	}
	/**
	 * 保存配置信息 to Scan_Para.txt
	 * @param scanParam
	 * @return
	 */
	public static boolean saveConfigData(ScanParam scanParam){
		StringBuffer line = new StringBuffer("");
		if(scanParam.getScan_scales() != null && scanParam.getScan_scales().size() > 0){
			for(Pair<String,String> iter : scanParam.getScan_scales()){
				line.append("SCAN:").append(iter.getFirst()).append(":").append(iter.getSecond()).append("\r\n");
			}
		}
		if(scanParam.getFilter_scales() != null && scanParam.getFilter_scales().size() > 0){
			for(Pair<String,String> iter : scanParam.getFilter_scales()){
				line.append("SCAN:").append(iter.getFirst()).append(":").append(iter.getSecond()).append("\r\n");
			}
		}
		return writeData("Scan_Para.txt", line.toString());
	}
	/**
	 * 保存接口数据列表 to InfProps.txt
	 * @param ifprops
	 * @param cdpPrex
	 * @return
	 */
	public static boolean saveInfPropList(Map<String, Pair<String, List<IfRec>>> ifprops,String... cdpPrex){
		StringBuffer line = new StringBuffer("");
		Iterator<Entry<String, Pair<String,List<IfRec>>>> iiter = ifprops.entrySet().iterator();
		for(;iiter.hasNext();){
			Entry<String, Pair<String,List<IfRec>>> i = iiter.next();
			if(i.getValue().getSecond().isEmpty()){
				continue;
			}
			line.append(i.getKey()).append(SPLIT_TOP);//IP
			line.append(i.getValue().getFirst()).append(SPLIT_TOP);//閹恒儱褰涢弫鐗堝祦
			Iterator<IfRec> jiter = i.getValue().getSecond().iterator();
			while(jiter.hasNext()){
				IfRec j = jiter.next();
				line.append(j.getIfIndex()).append(SPLIT_SUB);//閹恒儱褰涚槐銏犵穿
				line.append(j.getIfType()).append(SPLIT_SUB);//閹恒儱褰涚猾璇茬�
				line.append(j.getIfMac()).append(SPLIT_SUB);//閹恒儱褰沵ac閸︽澘娼�
				line.append(j.getIfPort()).append(SPLIT_SUB);//閹恒儱褰涚粩顖氬經閸欙拷
				line.append(j.getIfDesc()).append(SPLIT_SUB);//閹恒儱褰涢幓蹇氬牚
				line.append(j.getIfSpeed());
				if(jiter.hasNext()){
					line.append(SPLIT_MAIN);
				}
			}
			line.append("\r\n");
		}
		return writeData("InfProps.txt", line.toString());
	}
	/**
	 * 保存Ospf邻居数据列表
	 * @return
	 */
	public static boolean saveOspfNbrList(Map<String, Map<String, List<String>>> ospfnbr_list){
		StringBuffer line = new StringBuffer("");
		Iterator<Entry<String, Map<String,List<String>>>> iiter = ospfnbr_list.entrySet().iterator();
		while(iiter.hasNext()){
			Entry<String, Map<String,List<String>>> i = iiter.next();
			if(i.getValue().isEmpty()){
				continue;
			}
			line.append(i.getKey()).append("::");//IP 
			Iterator<Entry<String,List<String>>> jiter = i.getValue().entrySet().iterator();
			while(jiter.hasNext()){
				Entry<String,List<String>> j = jiter.next();
				line.append(j.getKey()).append(":");
				Iterator<String> kiter = j.getValue().iterator();
				while(kiter.hasNext()){
					line.append(kiter.next());
					if(kiter.hasNext()){
						line.append(",");
					}
				}
				if(jiter.hasNext()){
					line.append(";");
				}
			}
			line.append("\r\n");
		}
		return writeData("OspfNbr_ORG.txt", line.toString());
	}
	/**
	 * 保存Route数据列表 Route_ORG.txt
	 * @return
	 */
	public static boolean saveRouteList(Map<String, Map<String, List<RouteItem>>> route_list){
		StringBuffer line = new StringBuffer("");
		Iterator<Entry<String, Map<String, List<RouteItem>>>> iiter = route_list.entrySet().iterator();
		while(iiter.hasNext()){
			Entry<String, Map<String, List<RouteItem>>> i = iiter.next();
			if(i.getValue().isEmpty()){
				continue;
			}
			line.append(i.getKey()).append("::");//IP 
			Iterator<Entry<String,List<RouteItem>>> jiter = i.getValue().entrySet().iterator();
			while(jiter.hasNext()){
				Entry<String,List<RouteItem>> j = jiter.next();
				line.append(j.getKey()).append(":");
				Iterator<RouteItem> kiter = j.getValue().iterator();
				while(kiter.hasNext()){
					RouteItem k = kiter.next();
					line.append(k.getNext_hop()).append("/").append(k.getDest_net()).append(k.getDest_msk());
					if(kiter.hasNext()){
						line.append(",");
					}
				}
				if(jiter.hasNext()){
					line.append(";");
				}
			}
			line.append("\r\n");
		}
		return writeData("Route_ORG.txt", line.toString());
	}
	/**
	 * 保存bgp数据列表 to Bgp_ORG.txt
	 * @return
	 */
	public static boolean saveBgpList(List<Bgp> bgp_list){
		StringBuffer line = new StringBuffer("");
		if(bgp_list == null || bgp_list.size() == 0){
			return true;
		}
		for(Bgp i : bgp_list){
			line.append(i.getLocal_ip()).append("::").append(i.getLocal_port()).append("::").append(i.getPeer_ip()).append("::").append(i.getPeer_port()).append("\r\n");
		}
		return writeData("Bgp_ORG.txt", line.toString());
	}
	/**
	 * 保存vrrp数据表 to  Vrrp_ORG.txt
	 * @return
	 */
	public static boolean saveVrrpList(Map<String, RouterStandbyItem> vrrp_list){
		StringBuffer line = new StringBuffer("");
		if(vrrp_list == null || vrrp_list.isEmpty()) return true;
		for(Entry<String, RouterStandbyItem> i : vrrp_list.entrySet()){
			line.append(i.getKey()).append(SPLIT_TOP);
			for(int j=0;j<i.getValue().getVirtualIps().size();j++){
				line.append(i.getValue().getVirtualIps().get(j));
				if (j != i.getValue().getVirtualIps().size() - 1) {
					line.append(SPLIT_SUB);
				} else {
					line.append(SPLIT_MAIN);
				}
				
			}
			for(int j=0;j<i.getValue().getVirtualMacs().size()-2;j++){
				line.append(i.getValue().getVirtualMacs().get(j)).append(SPLIT_SUB);
			}
			line.append(i.getValue().getVirtualMacs().get(i.getValue().getVirtualMacs().size()-1)).append("\r\n");
		}
		return writeData("Vrrp_ORG.txt", line.toString());
	}
	/**
	 * 读取DeviceInfos.txt 己经保存的设备信息
	 * @param devid_list
	 * @param cdpPrex cdp文件名前缀，默认为空字串
	 * @return
	 */
	public static boolean readIdBodyData(Map<String, IDBody> devid_list,
			String... cdpPrex) {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + cdpPrex + "DeviceInfos.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split(SPLIT_MAIN);
				if (vstr.length == 14) {
					String ip = vstr[0];
					IDBody id = new IDBody();
					id.setSnmpflag(vstr[1]);
					id.setCommunity_get(vstr[2]);
					id.setCommunity_set(vstr[3]);
					id.setSysOid(vstr[4]);
					id.setDevType(vstr[5]);
					id.setDevFactory(vstr[6]);
					id.setDevModel(vstr[7]);
					id.setDevTypeName(vstr[8]);
					id.setBaseMac(vstr[9]);
					id.setSysName(vstr[10]);
					id.setSysSvcs(vstr[11]);
					if (!Utils.isEmptyOrBlank(vstr[12])) {
						String[] vipmsks = vstr[12].split(SPLIT_SUB);
						for (int i = 0; i < vipmsks.length; i++) {
							String[] ipmskinx_tmp = vipmsks[i].split("/");
							id.getIps().add(ipmskinx_tmp[0]);
							id.getMsks().add(ipmskinx_tmp[1]);
							if (ipmskinx_tmp.length == 3) {
								id.getInfinxs().add(ipmskinx_tmp[2]);
							} else {
								id.getInfinxs().add("0");
							}

						}
					}
					if (!Utils.isEmptyOrBlank(vstr[13])) {
						String[] vmacs = vstr[13].split(SPLIT_SUB);
						for (int i = 0; i < vmacs.length; i++) {
							id.getMacs().add(vmacs[i]);
						}
					}
					devid_list.put(ip, id);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return true;
	}
	/**
	 * 读取Aft_ORG.txt aft原始数据
	 * @param aft_list
	 * @return
	 */
	public static boolean readAftList(Map<String, Map<String, List<String>>> aft_list){
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + "Aft_ORG.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split("::");
				if(vstr.length == 2){
					String ip = vstr[0];
					if(!vstr[1].isEmpty()){
						//存在端口集合
						Map<String,List<String>> map_ps = new HashMap<String, List<String>>();
						String[] vpms = vstr[1].split(";");
						for(int i=0;i<vpms.length;i++){
							if(!Utils.isEmptyOrBlank(vpms[i])){
								String[] vps = vpms[i].split(":");
								String port = vps[0];
								if(!vps[2].isEmpty()){
									//macs
									String macs[] = vps[1].split(",");
									List<String> mac_list = new ArrayList<String>();
									for(int j=0;j<macs.length;j++){
										mac_list.add(macs[j]);
									}
									map_ps.put(port, mac_list);
								}
							}
						}
						aft_list.put(ip, map_ps);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return true;
	}
	/**
	 * 读取Arp_ORG.txt arp原始数据表
	 * @param arpList
	 * @return
	 */
	public static boolean readArpList(Map<String, Map<String, List<Pair<String, String>>>> arpList){
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + "Arp_ORG.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split("::");
				if(vstr.length == 2){
					String ip = vstr[0];
					if(!Utils.isEmptyOrBlank(vstr[1])){
						//鐎涙ê婀粩顖氬經
						Map<String,List<Pair<String,String>>> map_ps = new HashMap<String,List<Pair<String,String>>>();
						String[] vpms = vstr[1].split(";");
						for(int i=0;i<vpms.length;i++){
							if(!Utils.isEmptyOrBlank(vpms[i])){
								String[] vps = vpms[i].split(":");
								String port = vps[0];
								if(!Utils.isEmptyOrBlank(vps[1])){
									String[] macs = vps[1].split(",");
									List<Pair<String,String>> ipmac_list = new ArrayList<Pair<String,String>>();
									for(int j=0;j<macs.length;j++){
										if(!Utils.isEmptyOrBlank(macs[j])){
											String[] ipmac_tmp = macs[j].split("-");
											ipmac_list.add(new Pair<String, String>(ipmac_tmp[0], ipmac_tmp[1]));
										}
									}
									map_ps.put(port, ipmac_list);
								}
							}
						}
						arpList.put(ip, map_ps);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return true;
	}
	/**
	 * 读取接口表原始数据  from InfProps.txt
	 * @param ifpropList
	 * @return
	 */
	public static boolean readInfPropList(Map<String, Pair<String, List<IfRec>>> ifpropList){
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + "InfProps.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split(SPLIT_TOP);
				if(vstr.length == 3){
					String ip = vstr[0];//IP
					String amount = vstr[1];//閹恒儱褰涢弫浼村櫤
					if(!Utils.isEmptyOrBlank(vstr[2])){
						List<IfRec> inf_list = new ArrayList<IfRec>();
						String[] vinfs = vstr[2].split(SPLIT_MAIN);
						for(int i=0;i<vinfs.length;i++){
							if(!Utils.isEmptyOrBlank(vinfs[i])){
								String props[] = vinfs[i].split(SPLIT_SUB);
								if(props.length == 6){
									IfRec ifrec = new IfRec();
									ifrec.setIfIndex(props[0]);
									ifrec.setIfType(props[1]);
									ifrec.setIfMac(props[2]);
									ifrec.setIfPort(props[3]);
									ifrec.setIfDesc(props[4]);
									ifrec.setIfSpeed(props[5]);
									inf_list.add(ifrec);
								}
							}
						}
						if(!inf_list.isEmpty()){
							ifpropList.put(ip, new Pair<String, List<IfRec>>(amount, inf_list));
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return true;
	}
	/**
	 * 读取邻居表数据 from OspfNbr_ORG.txt
	 * @param ospfnbrList
	 * @return
	 */
	public static boolean readOspfNbrList(Map<String, Map<String, List<String>>> ospfnbrList){
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + "OspfNbr_ORG.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split("::");
				if(vstr.length == 2){
					String ip = vstr[0];//IP
					if(!Utils.isEmptyOrBlank(vstr[1])){
						Map<String,List<String>> map_ps = new HashMap<String,List<String>>();
						String[] vpms = vstr[1].split(";");
						for(int i=0;i<vpms.length;i++){
							if(!Utils.isEmptyOrBlank(vpms[i])){
								String vps[] = vpms[i].split(":");
								String port = vps[0];
								if(!Utils.isEmptyOrBlank(vps[1])){
									String[] ips = vps[1].split(",");
									List<String> ip_list = new ArrayList<String>();
									for(int j = 0;j<ips.length ;j++){
										if(ip_list.contains(ips[j])){
											ip_list.add(ips[j]);
										}
									}
									if(!ip_list.isEmpty()){
										map_ps.put(port, ip_list);
									}
								}
							}
						}
						ospfnbrList.put(ip, map_ps);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return true;
	}
	/**
	 * 读取路由表数据 from Route_ORG.txt
	 * @param rttblList
	 * @return
	 */
	public static boolean readRouteList(Map<String, Map<String, List<RouteItem>>> rttblList){
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + "Route_ORG.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split("::");
				if(vstr.length == 2){
					String ip = vstr[0];//IP
					if(!Utils.isEmptyOrBlank(vstr[1])){//存在端口集合
						Map<String,List<RouteItem>> map_ps = new HashMap<String,List<RouteItem>>();
						String[] vpms = vstr[1].split(";");
						for(int i=0;i<vpms.length;i++){
							if(!Utils.isEmptyOrBlank(vpms[i])){
								String vps[] = vpms[i].split(":");
								String port = vps[0];
								if(!Utils.isEmptyOrBlank(vps[1])){
									String[] ips = vps[1].split(",");
									List<RouteItem> ip_list = new ArrayList<RouteItem>();
									for(int j = 0;j<ips.length ;j++){
										String[] dests = ips[j].split("/");
										RouteItem rtitem = new RouteItem();
										rtitem.setNext_hop(dests[0]);
										if(dests.length == 3){
											rtitem.setDest_net(dests[1]);
											rtitem.setDest_msk(dests[2]);
										}else{
											rtitem.setDest_net("");
											rtitem.setDest_msk("");
										}
										ip_list.add(rtitem);
									}
									if(!ip_list.isEmpty()){
										map_ps.put(port, ip_list);
									}
								}
							}
						}
						rttblList.put(ip, map_ps);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return true;
	}
	/**
	 * 读取邻居数据表 from Bgp_ORG.txt
	 * @param bgpList
	 * @return
	 */
	public static boolean readBgpList(List<Bgp> bgpList){
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + "Bgp_ORG.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split("::");
				if(vstr.length == 4){
					Bgp bgpItem = new Bgp();
					bgpItem.setLocal_ip(vstr[0]);
					bgpItem.setLocal_port(vstr[1]);
					bgpItem.setPeer_ip(vstr[2]);
					bgpItem.setPeer_port(vstr[3]);
					bgpList.add(bgpItem);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return true;
	}
	/**
	 *  读取vrrp数据 from Vrrp_ORG.txt
	 * @param routeStandbyList
	 * @return
	 */
	public static boolean readVrrpList(Map<String, RouterStandbyItem> vrrp_list){
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + "Vrrp_ORG.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split(SPLIT_TOP);
				if(vstr.length == 2){
					RouterStandbyItem vrrpItem = new RouterStandbyItem();
					String[] vipmacs = vstr[1].split(SPLIT_MAIN);
					if(vipmacs.length == 2){
						String[] vips = vipmacs[0].split(SPLIT_SUB);
						String[] vmacs = vipmacs[1].split(SPLIT_SUB);
						Collections.addAll(vrrpItem.getVirtualIps(), vips);
						Collections.addAll(vrrpItem.getVirtualMacs(), vmacs);
					}
					vrrp_list.put(vstr[0],vrrpItem);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return true;
	}
	/**
	 * 读取Trace route数据列表 from Tracert.txt
	 * @param tracert_list
	 * @return
	 */
	public static boolean readTracertList(List<List<String>> tracert_list){
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + "Tracert.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split(",");
				List<String> lstr = new ArrayList<String>();
				Collections.addAll(lstr, vstr);
				tracert_list.add(lstr);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return true;
	}
	/**
	 * 读取direct 数据表 from Direct_Data.txt
	 * @param directdata_list
	 * @return
	 */
	public static boolean readDirectData(Map<String, List<Directitem>> directdata_list){
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + "Direct_Data.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split(SPLIT_TOP);
				if(vstr.length == 2){
					String ip = vstr[0];//IP
					if(!Utils.isEmptyOrBlank(vstr[1])){//存在端口集合
						List<Directitem> item_list = new ArrayList<Directitem>();
						String[] vcdps = vstr[1].split(SPLIT_MAIN);
						for(int i=0;i<vcdps.length;i++){
							if(!Utils.isEmptyOrBlank(vcdps[i])){
								String items[] = vcdps[i].split(SPLIT_SUB);
								Directitem item_tmp = new Directitem();
								if(items.length == 6){
									item_tmp.setLocalPortInx(items[0]);
									item_tmp.setLocalPortDsc(items[1]);
									item_tmp.setPeerId(items[2]);
									item_tmp.setPeerIp(items[3]);
									item_tmp.setPeerPortInx(items[4]);
									item_tmp.setPeerPortDsc(items[5]);
									item_list.add(item_tmp);
								}else if(items.length == 4){
									item_tmp.setLocalPortInx(items[0]);
									item_tmp.setLocalPortDsc(items[0]);
									item_tmp.setPeerId(items[1]);
									item_tmp.setPeerIp(items[2]);
									item_tmp.setPeerPortInx(items[3]);
									item_tmp.setPeerPortDsc(items[3]);
									item_list.add(item_tmp);
								}
								
							}
						}
						if(!item_list.isEmpty()){
							directdata_list.put(ip, item_list);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return true;
	}
	/**
	 * 读取配置信息 from Scan_Para.txt
	 * @param scanPara
	 * @return
	 */
	public static boolean readConfigDate(ScanParam scanPara){
		scanPara.getScan_scales().clear();
		scanPara.getScan_scales_num().clear();
		scanPara.getFilter_scales().clear();
		scanPara.getFilter_scales_num().clear();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(getProductPath() + "Scan_Para.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split(":");
				if(vstr.length == 3){
					long startnum = ScanUtils.ipToLong(vstr[1]);
					long endnum = ScanUtils.ipToLong(vstr[2]);
					if("SCAN".equals(vstr[0])){
						scanPara.getScan_scales().add(new Pair<String, String>(vstr[1], vstr[2]));
						scanPara.getScan_scales_num().add(new Pair<Long,Long>(startnum,endnum));
					}else{
						scanPara.getFilter_scales().add(new Pair<String, String>(vstr[1], vstr[2]));
						scanPara.getFilter_scales_num().add(new Pair<Long,Long>(startnum,endnum));
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return true;
	}
	/**
	 * 获取插件的路径
	 * @return
	 */
	public static String getPlatformPath(){
		String path = Platform.getBundle("com.siteview.css.topo").getLocation().replaceAll("reference:file:", ""); 
		path = path.substring(1);
		return path;
	}
	public static String getProductPath(){
		Location location = Platform.getConfigurationLocation();
		URL url = location.getURL();
		return url.getPath();
	}
	/**
	 * 保存界面配置信息 to scanParam.txt
	 * @param sp
	 * @return
	 */
	public static boolean saveScanParam(ScanParam sp){
		StringBuffer line = new StringBuffer("");
		line.append("depth=").append(sp.getDepth()).append("\r\n");
		line.append("community_get_dft=").append(sp.getCommunity_get_dft()).append("\r\n");
		line.append("community_set_dft=").append(sp.getCommunity_set_dft()).append("\r\n");
		line.append("retrytimes=").append(sp.getRetrytimes()).append("\r\n");
		line.append("threadCount=").append(sp.getThreadCount()).append("\r\n");
		line.append("scanScales=");
		//淇濆瓨鎵弿鑼冨洿
		for(int i=0;i<sp.getScan_scales().size();i++){
			Pair<String,String> tmp = sp.getScan_scales().get(i);
			line.append(tmp.getFirst()).append("-").append(tmp.getSecond());
			if(i!=sp.getScan_scales().size()-1){
				line.append(":");
			}else{
				line.append("\r\n");
			}
		}
		line.append("\r\n");
		//淇濆瓨鎵弿杩囨护鑼冨洿
		line.append("filterScales=");
		for(int i=0;i<sp.getFilter_scales().size();i++){
			Pair<String, String> tmp = sp.getFilter_scales().get(i);
			line.append(tmp.getFirst()).append("-").append(tmp.getSecond());
			if(i!=sp.getFilter_scales().size()-1){
				line.append(":");
			}
		}
		line.append("\r\n");
		//淇濆瓨鎵弿绉嶅瓙
		line.append("scanSeeds=");
		for(int i=0;i<sp.getScan_seeds().size();i++){
			String tmp = sp.getScan_seeds().get(i);
			line.append(tmp);
			if(i!=(sp.getScan_seeds().size()-1)){
				line.append("-");
			}
		}
		return writeData("scanParam.txt", line.toString());
	}
	/**
	 * 读取界面配置信息
	 * @return
	 */
	public static ScanParam readScanParam(){
		ScanParam sp = new ScanParam();
		FileReader fr = null;
		File scanParmFile = new File(getProductPath() + "scanParam.txt");
		if(!scanParmFile.exists()){
			return null;
		}
		BufferedReader br = null;
		try {
			fr = new FileReader(scanParmFile);
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lineArr = line.split("=");
				if(lineArr.length != 2){
					continue;
				}
				if(lineArr[0].equals("depth")){
					if(!Utils.isEmptyOrBlank(lineArr[1].trim())){
						sp.setDepth(Integer.parseInt(lineArr[1].trim().replaceAll("\r\n", "")));
					}else{
						sp.setDepth(5);
					}
						
				}
				if(lineArr[0].equals("community_get_dft")){
					if(!Utils.isEmptyOrBlank(lineArr[1].trim())){
						sp.setCommunity_get_dft(lineArr[1].trim().replaceAll("\r\n", ""));
					}else{
						sp.setCommunity_get_dft("public");
					}
				}
				if(lineArr[0].equals("community_set_dft")){
					if(!Utils.isEmptyOrBlank(lineArr[1].trim())){
						sp.setCommunity_set_dft(lineArr[1].trim().replaceAll("\r\n", ""));
					}else{
						sp.setCommunity_set_dft("public");
					}
				}
				if(lineArr[0].equals("retrytimes")){
					if(!Utils.isEmptyOrBlank(lineArr[1].trim())){
						sp.setRetrytimes(Integer.parseInt(lineArr[1].trim().replaceAll("\r\n", "")));
					}else{
						sp.setRetrytimes(2);
					}
				}
				if(lineArr[0].equals("threadCount")){
					if(!Utils.isEmptyOrBlank(lineArr[1].trim())){
						sp.setThreadCount(Integer.parseInt(lineArr[1].trim().replaceAll("\r\n", "")));
					}else{
						sp.setThreadCount(200);
					}
				}
				if (lineArr[0].equals("scanScales")) {
					String[] values = lineArr[1].trim().replaceAll("\r\n", "")
							.split(":");
					for (int i = 0; i < values.length; i++) {
						String[] vs = values[i].split("-");
						if (vs.length != 2) {
							continue;
						}
						if (!Utils.isIp(vs[0])
								|| !Utils.isIp(vs[1])
								|| (ScanUtils.ipToLong(vs[0]) > ScanUtils
										.ipToLong(vs[1]))) {
							continue;
						}
						Pair<String, String> v = new Pair<String, String>(
								vs[0], vs[1]);
						sp.getScan_scales().add(v);
					}
				}
				if (lineArr[0].equals("filterScales")) {
					String[] values = lineArr[1].trim().replaceAll("\r\n", "")
							.split(":");
					for (int i = 0; i < values.length; i++) {
						String[] vs = values[i].split("-");
						if (vs.length != 2) {
							continue;
						}
						if (!Utils.isIp(vs[0])
								|| !Utils.isIp(vs[1])
								|| (ScanUtils.ipToLong(vs[0]) > ScanUtils
										.ipToLong(vs[1]))) {
							continue;
						}
						Pair<String, String> v = new Pair<String, String>(
								vs[0], vs[1]);
						sp.getFilter_scales().add(v);
					}
				}
				if (lineArr[0].equals("scanSeeds")) {
					String[] values = lineArr[1].trim().replaceAll("\r\n", "")
							.split("-");
					for (int i = 0; i < values.length; i++) {
						if (!Utils.isIp(values[i].trim())) {
							continue;
						}
						sp.getScan_seeds().add(values[i].trim());
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fr = null;
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return sp;
	}
	public static void main(String[] args) {
		List<String> l = new ArrayList<String>();
		String[] s = new String[]{"s"};
		Collections.addAll(l, s);
		System.out.println(l.size());
	}
}
