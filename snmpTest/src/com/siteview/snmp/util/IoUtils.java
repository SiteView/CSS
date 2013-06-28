package com.siteview.snmp.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import com.siteview.snmp.pojo.IDBody;

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
					if(indexk != j.getValue().size() -1){
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
					return false;
				}
			}
		}
		return true;
	}
	public static boolean saveFrmDevIDList(Map<String, IDBody> devid_list){
		StringBuffer line = new StringBuffer("");
		for(Entry<String, IDBody> i : devid_list.entrySet()){
			line.append(i.getKey() + SPLIT_MAIN);
			IDBody id = i.getValue();
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
					line.append(id.getIps().get(ip_j)).append("/").append(id.getMsks().get(ip_j)).append("/").append(id.getInfinxs().get(ip_j));
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
		}
		return writeData("DeviceInfos.txt", line.toString());
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
//		char mac_str[30] = "";
//	        sprintf(mac_str,"test/Aft_FRM_SS_%d.txt",test);
	        //SvLog::writeLog(mac_str);
//		ofstream output(mac_str,ios::out);
		StringBuffer line = new StringBuffer("");
//		for(FRM_AFTARP_LIST::const_iterator i = aft_list_frm.begin(); i != aft_list_frm.end(); i++)
		for(Entry<String, Map<String,List<String>>> i : aft_list_frm.entrySet())
		{
			if(i.getValue().isEmpty())
			{
				continue;
			}
			line.append(i.getKey()).append("::"); //管理IP
//			std::map<std::string, std::list<std::string> >::const_iterator j_end = i->second.end();
//			j_end--;
//			for(std::map<std::string, std::list<std::string> >::const_iterator j = i->second.begin();
//				j != i->second.end();
//				j++)
			int indexj = 0;
			for(Entry<String, List<String>> j : i.getValue().entrySet())
			{//port 循环
				indexj++;
				if(j.getValue().isEmpty())//->second.empty())
					continue;
//				line += j->first + ":";
				line.append(j.getKey()).append(":");
//				std::list<std::string>::const_iterator k_end =  j->second.end();
//				k_end--;
//				for(std::list<std::string>::const_iterator k = j->second.begin(); 
//					k != j->second.end(); 
//					k++)
				int indexk = 0;
				for(String k : j.getValue())
				{//ip循环
					indexk ++ ;
//					line += *k;
					line.append(k);
					if(indexk != (j.getValue().size() - 1))
					{
						line.append(",");//line += ",";
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
		writeData("Tracert_renew.txt", line.toString());
		return true;
	}
}
