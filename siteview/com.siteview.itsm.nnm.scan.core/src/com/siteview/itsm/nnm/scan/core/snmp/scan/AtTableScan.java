package com.siteview.itsm.nnm.scan.core.snmp.scan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.siteview.itsm.nnm.scan.core.snmp.base.BaseTableRequest;
import com.siteview.itsm.nnm.scan.core.snmp.constants.OIDConstants;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.AtTable;

public class AtTableScan extends BaseTableRequest {
	
	public static final OID defaultEndOID = new OID("4");
	
	@Override
	public Map<String, Object> resolute(List<TableEvent> tEvent) {
		Map<String, Object> result = new HashMap<String, Object>();
		for(TableEvent t :tEvent){
			VariableBinding vbing = t.getColumns()[0];
			Variable vb = vbing.getVariable();
			String oidstr = vbing.getOid().toString().substring(0,19);
			String index = vbing.getOid().toString().substring(20); 
			AtTable at = null;
			if(result.containsKey(index)){
				at = (AtTable)result.get(index);
			}else{
				at = new AtTable();
				result.put(index, at);
			}
			if(oidstr.equals("1.3.6.1.2.1.3.1.1.1")){
				at.setAtIfIndex(vb.toInt());
			}else if(oidstr.equals("1.3.6.1.2.1.3.1.1.2")){
				at.setAtPhysAddress(vb.toString());
			}else if(oidstr.equals("1.3.6.1.2.1.3.1.1.3")){
				at.setAtNetAddress(vb.toString());
			}
//			System.out.println(vbing.getOid() + " = " + vb);
		}
		return result;
	}
	public static void main(String[] args) {
		AtTableScan scan = new AtTableScan();
		scan.setEnd(defaultEndOID);
		Map<String, Object> result = scan.getTablePojos(OIDConstants.atTable);
		Set<String> keys = result.keySet();
		for(String key : keys){
			AtTable media = (AtTable)result.get(key);
			System.out.println("key = " + key + " { atIfIndex = "  +media.getAtIfIndex()  
					+" atPhysAddress = "  +media.getAtPhysAddress()
					+" atNetAddress = "  +media.getAtNetAddress() + " }");
		}
		
	}

}
