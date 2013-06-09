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
import com.siteview.snmp.pojo.IpNetToMedia;
import com.siteview.snmp.util.Utils;

public class IPNetToMediaTableScan extends BaseTableRequest {

	public static final OID defaultEndOID = new OID("5");
	@Override
	public Map<String, Object> resolute(List<TableEvent> tEvent) {
		Map<String,Object> result = new HashMap<String, Object>();
		for (TableEvent t : tEvent) {
			String varible = t.getColumns()[0].toString();
			String[] varibleSplit = varible.split("=");
			//前20位表示OID，之后的数据是由对应商品号+IP地生成的INDEX
			String oidStr = varibleSplit[0].substring(0,21);
			String dateIndex = varibleSplit[0].substring(21);
			IpNetToMedia media = null;
			if(result.containsKey(dateIndex)){
				media = (IpNetToMedia)result.get(dateIndex);
			}else{
				media = new IpNetToMedia();
				result.put(dateIndex, media);
			}
			String value = Utils.isEmptyOrBlank(varibleSplit[1]) ? null
					: varibleSplit[1].trim();
			if(oidStr.equals("1.3.6.1.2.1.4.22.1.1.")){
				media.setIpNetToMediaIfIndex(Utils.getIntValueFromNotNullString(value));
			}else if(oidStr.equals("1.3.6.1.2.1.4.22.1.2.")){
				media.setIpNetToMediaPhysAddress(value);
			}else if(oidStr.equals("1.3.6.1.2.1.4.22.1.3.")){
				media.setIpNetToMediaNetAddress(value);
			}else if(oidStr.equals("1.3.6.1.2.1.4.22.1.4.")){
				media.setIpNetToMediaType(Utils.getIntValueFromNotNullString(value));
			}
		}
		return result;
	}

	public static void main(String[] args) {
		
		IPNetToMediaTableScan scan = new IPNetToMediaTableScan();
		scan.setEnd(defaultEndOID);
		Map<String, Object> result = scan.getTablePojos(OIDConstants.ipNetToMediaTable);
		Set<String> keys = result.keySet();
		for(String key : keys){
			IpNetToMedia media = (IpNetToMedia)result.get(key);
			System.out.println("key = " + key + " { ipNetToMediaIfIndex = "  +media.getIpNetToMediaIfIndex()  
					+" ipNetToMediaPhysAddress = "  +media.getIpNetToMediaPhysAddress()
					+" ipNetToMediaNetAddress = "  +media.getIpNetToMediaNetAddress()
					+" ipNetToMediaType = "  +media.getIpNetToMediaType() + " }");
		}
		
	}

	
}
