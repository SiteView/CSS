package test;

import java.io.IOException;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

public class TableUtilsTest {
	private Snmp snmp = null;
	public void init() throws IOException{
		snmp = new Snmp(new DefaultUdpTransportMapping());
		snmp.listen();
	}
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		TableUtilsTest test = new TableUtilsTest();
		CommunityTarget target = new CommunityTarget();
		target.setAddress(new UdpAddress("192.168.0.248/161"));
		target.setCommunity(new OctetString("siteview"));
		target.setVersion(SnmpConstants.version2c);
		target.setTimeout(3000);
		target.setRetries(1);
		test.init();
		TableUtils utils = new TableUtils(test.snmp,new DefaultPDUFactory());
		utils.setMaxNumRowsPerPDU(5);
		OID[] columnOIDs = new OID[]{
				new OID("1.3.6.1.2.1.1")
//				,//sysORID
//				new OID("1.3.6.1.2.1.2.2.1"),//sysORDescr
//				new OID("1.3.6.1.2.1.4.1")//wrong OID, expect an null in in VariableBinding array
				};
		List<TableEvent> l = utils.getTable(target, columnOIDs, new OID("3"), new OID("10"));
		for(TableEvent t:l){
//			if(t.STATUS_OK == TableEvent.STATUS_OK){
			VariableBinding[] vbs = t.getColumns();
			
				System.out.println(t.getColumns()[0].toString());
//			}
		}
	}
	
	class TablePduFaction implements PDUFactory{
		@Override
		public PDU createPDU(Target target) {
			return null;
		}
		
	}
}
