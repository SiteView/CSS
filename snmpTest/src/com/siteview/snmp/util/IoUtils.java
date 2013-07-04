package com.siteview.snmp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JInternalFrame;

import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Directitem;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.pojo.IfRec;

/**
 * 保存文件的工具类
 * @author haiming.wang
 */
public class IoUtils {

	public static final String SPLIT_TOP    =  "[:::]";
	public static final String SPLIT_MAIN	=  "[::]";
	public static final String SPLIT_SUB	=  "[:]";
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
	public static boolean writeData(String fileName,String data){
		String path = System.getProperty("user.dir") + File.separator + fileName;
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
	public static boolean saveIDBodyData(Map<String, IDBody> devid_list, String... cdpPrex)
	{
		String fileName = cdpPrex+"DeviceInfos.txt";
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
	public static boolean savaDevidIps(Map<String, IDBody> devid_list){
		StringBuffer sb = new StringBuffer("");
		for(Entry<String, IDBody> i : devid_list.entrySet()){
			sb.append(i.getKey()).append("\r\n");
		}
		return writeData("deviceIps.txt", sb.toString());
	}
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
	// 用文件保存设备标识体数据列表
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
	 * 保存接口数据列表
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
			line.append(i.getValue().getFirst()).append(SPLIT_TOP);//接口数据
		}
		return writeData("InfProps.txt", line.toString());
	}
}
