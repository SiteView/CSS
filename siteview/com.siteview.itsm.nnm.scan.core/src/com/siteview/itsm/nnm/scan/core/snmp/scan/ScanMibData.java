package com.siteview.itsm.nnm.scan.core.snmp.scan;

import java.util.ArrayList;
import java.util.List;

import org.snmp4j.mp.SnmpConstants;

import com.siteview.itsm.nnm.scan.core.snmp.base.BaseRequest;
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;

public class ScanMibData extends BaseRequest {

	public static void main(String[] args) {
		new ScanMibData().scanMasks("192.168.0.248", 161, "siteview", 1000, 2,
				SnmpConstants.version2c);
	}
	
	public List<Pair<String, String>> scanMasks(String ip, int port, String community,
			int timeout, int retry, int version) {
//		Target target = buildGetPduCommunityTarget();
		List<Pair<String, String>> resultList = new ArrayList<Pair<String,String>>();
//		IpAddressTableScan ipaddscan = new IpAddressTableScan();
//		ipaddscan.setIp(ip);
//		Map<String, Object> ipmask_list = ipaddscan.getTablePojos();
//		if (!ipmask_list.isEmpty()) {
//			Set<String> keys = ipmask_list.keySet();
//			for (String key : keys) {
//					if (!"".equals(key) 
//						&& !"0.0.0.0".equals(key) // �ų�����ƥ���ַ
//						&& !key.substring(0, 3).equals("127") // �ų����ص�ַ
//						&& !key.startsWith("0.255")) {
//					IpAddressTable ipAddressTable = (IpAddressTable) ipmask_list
//							.get(key);
//					Pair<String, String> pair = new Pair<String,String>();
//					pair.setFirst(key);
//					pair.setSecond(ipAddressTable.getIpAdEntNetMask());
//					resultList.add(pair);
//				}
//			}
//		}

		return resultList;
	}
}
