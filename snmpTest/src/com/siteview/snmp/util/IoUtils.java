package com.siteview.snmp.util;

import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import com.siteview.snmp.pojo.IDBody;

public class IoUtils {

	public static final String SPLIT_TOP    =  "[:::]";
	public static final String SPLIT_MAIN	=  "[::]";
	public static final String SPLIT_SUB	=  "[:]";
	public static final String Path = System.getProperty("user.dir");
	public static boolean saveFrmDevIDList(Map<String, IDBody> devid_list){
		for(Entry<String, IDBody> i : devid_list.entrySet()){
			StringBuffer line = new StringBuffer();
			line.append(i.getKey() + SPLIT_MAIN);
			IDBody id = i.getValue();
			line.append(id.getSnmpflag()).append(SPLIT_MAIN);
			line.append(id.getCommunity_get()).append(SPLIT_MAIN);
			line.append(id.getCommunity_set()).append(SPLIT_MAIN);
			line.append(id.getSysOid()).append(SPLIT_MAIN);
			line.append(id.getDevType()).append(SPLIT_MAIN);
			line.append(id.getDevFactory()).append(SPLIT_MAIN);
			line.append(id.getDevModel()).append(SPLIT_MAIN);
			line.append(id.getDevTypeName()).append(SPLIT_MAIN);
			line.append(id.getBaseMac()).append(SPLIT_MAIN);
			line.append(id.getSysName()).append(SPLIT_MAIN);
			line.append(id.getSysSvcs()).append(SPLIT_MAIN);
			if(id.getIps()!=null && !id.getIps().isEmpty()){
				Vector<String> ip_j_end = id.getIps();
				for(int ip_j = 0,msk_j = 0,inf_j = 0;ip_j<id.getIps().size();ip_j++){
					
				}
			}
			
		}
		return true;
	}
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
	}
}
