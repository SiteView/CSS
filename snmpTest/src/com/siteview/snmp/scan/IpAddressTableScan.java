package com.siteview.snmp.scan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.siteview.snmp.base.BaseTableRequest;
import com.siteview.snmp.constants.OIDConstants;
import com.siteview.snmp.pojo.IpAddressTable;
import com.siteview.snmp.pojo.IpNetToMedia;
import com.siteview.snmp.util.Utils;

public class IpAddressTableScan extends BaseTableRequest {

	public static final OID defaultEndOID = new OID("6");
	public static OID _OID = OIDConstants.ipAddressTable;
	@Override
	public Map<String, Object> resolute(List<TableEvent> tEvent) {
		Map<String,Object> result = new HashMap<String, Object>();
		for(TableEvent t : tEvent){
			VariableBinding[] vbs = t.getColumns();
			
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
	public static void main(String[] args) {
		IpAddressTableScan scan = new IpAddressTableScan();
		scan.setEnd(defaultEndOID);
		
		Map<String, Object> result = scan.getTablePojos(_OID);
		Set<String> keys = result.keySet();
		for(String key : keys){
			IpAddressTable media = (IpAddressTable)result.get(key);
			System.out.println("key = " + key + " { setIpAdEntAddr = "  +media.getIpAdEntAddr()  
					+" setIpAdEntIfIndex = "  +media.getIpAdEntIfIndex()
					+" setIpAdEntNetMask = "  +media.getIpAdEntNetMask()
					+" setIpAdEntBcastAddr = "  +media.getIpAdEntBcastAddr()
					+" setIpAdEntReasmMaxSize = "  +media.getIpAdEntReasmMaxSize()+ " }");
		}
		
	}
}
