package com.siteview.snmp.scan;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.constants.OIDConstants;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.IpAddressTable;
import com.siteview.snmp.util.ScanUtils;
import com.siteview.snmp.util.Utils;

public class IpAddressTableScan {

	public static final OID defaultEndOID = new OID("6");
	public static OID _OID = OIDConstants.ipAddressTable;
	public Map<String, IpAddressTable> resolute(List<TableEvent> tEvent) {
		Map<String,IpAddressTable> result = new HashMap<String, IpAddressTable>();
		if(tEvent.isEmpty()) return result;
		
		for(TableEvent t : tEvent){
			if(t.getStatus() < 0) continue;
			String varible = t.getColumns()[0].toString();
			String[] varibleSplit = varible.split("=");
			//前20位表示OID，之后的数据是由对应商品号+IP地生成的INDEX
			String oidStr = varibleSplit[0].substring(0,21);
			String dateIndex = varibleSplit[0].substring(21);
			IpAddressTable ipadd = null;
			if(result.containsKey(dateIndex)){
				ipadd = (IpAddressTable)result.get(dateIndex);
			}else{
				ipadd = new IpAddressTable();
				result.put(dateIndex, ipadd);
			}
			String value = Utils.isEmptyOrBlank(varibleSplit[1]) ? null
					: varibleSplit[1].trim();
			if(oidStr.equals("1.3.6.1.2.1.4.20.1.1.")){
				ipadd.setIpAdEntAddr(value);
			}else if(oidStr.equals("1.3.6.1.2.1.4.20.1.2.")){
				ipadd.setIpAdEntIfIndex(Utils.getIntValueFromAllowNullString(value));
			}else if(oidStr.equals("1.3.6.1.2.1.4.20.1.3.")){
				ipadd.setIpAdEntNetMask(value);
			}else if(oidStr.equals("1.3.6.1.2.1.4.20.1.4.")){
				ipadd.setIpAdEntBcastAddr(Utils.getIntValueFromAllowNullString(value));
			}else if(oidStr.equals("1.3.6.1.2.1.4.20.1.5.")){
				ipadd.setIpAdEntReasmMaxSize(Utils.getIntValueFromAllowNullString(value));
			}
		}
		return result;
	}
	/**
	 * 获取ipaddress表 掩码列数据
	 * @param spr
	 * @param ipcm_result
	 */
	public void getIpMaskList(SnmpPara spr, List<Pair<String, String>> ipcm_result){
		Map<String, IpAddressTable> tables = getIpAddressTables(spr);
		Set<String> keys = tables.keySet();
		for(String key : keys){
				if (!"".equals(key) 
					&& !"0.0.0.0".equals(key) // �ų�����ƥ���ַ
					&& !key.substring(0, 3).equals("127") // �ų����ص�ַ
					&& !key.startsWith("0.255")) {
					ipcm_result.add(new Pair<String, String>(key, tables.get(key).getIpAdEntNetMask()));
				}
		}
	}
	
	/**
	 * 获取ipaddress表数据
	 * @param spr
	 * @return
	 */
	public Map<String,IpAddressTable> getIpAddressTables(SnmpPara spr){
		Map<String,IpAddressTable> result = new HashMap<String, IpAddressTable>();
		CommunityTarget target = ScanUtils.buildGetPduCommunityTarget(spr.getIp(), 161, spr.getCommunity(), spr.getTimeout(), spr.getRetry(), Integer.parseInt(spr.getSnmpver()));
		Snmp snmp = new Snmp();
		TransportMapping transport1 = null;
		try {
			transport1 = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport1);
			snmp.listen();
			TableUtils utils = new TableUtils(snmp,new DefaultPDUFactory());
			utils.setMaxNumRowsPerPDU(5);
			OID[] columnOIDs = new OID[]{
					_OID
			};
			result =  resolute(utils.getTable(target, columnOIDs, new OID("1"), defaultEndOID)); 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(snmp != null){
				try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		IpAddressTableScan scan = new IpAddressTableScan();
		SnmpPara sp = new SnmpPara();
		sp.setCommunity("public");
		sp.setIp("192.168.0.248");
		sp.setRetry(2);
		sp.setTimeout(3000);
		Map<String, IpAddressTable> result = scan.getIpAddressTables(sp);
		Set<String> keys = result.keySet();
		for(String key : keys){
			IpAddressTable media = result.get(key);
			System.out.println("key = " + key + " { setIpAdEntAddr = "  +media.getIpAdEntAddr()  
					+" setIpAdEntIfIndex = "  +media.getIpAdEntIfIndex()
					+" setIpAdEntNetMask = "  +media.getIpAdEntNetMask()
					+" setIpAdEntBcastAddr = "  +media.getIpAdEntBcastAddr()
					+" setIpAdEntReasmMaxSize = "  +media.getIpAdEntReasmMaxSize()+ " }");
		}
		
	}
}
