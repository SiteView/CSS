package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class ScanRoutTable {
	private Snmp snmp = null;
	CommunityTarget target = null;
	protected void init() throws IOException {
        // 设定采取的协议--SNMP
        TransportMapping transport1  = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport1);
        snmp.listen();
        target = new CommunityTarget();
        target.setAddress(GenericAddress.parse("udp:192.168.0.248/161"));
        target.setRetries(2);
        target.setTimeout(2000);
        target.setCommunity(new OctetString("siteview"));
        target.setVersion(SnmpConstants.version2c);
    }

	public static void main(String[] args) throws IOException {
		int a = 0;
		PDU pdu = new PDU();
		ScanRoutTable tab = new ScanRoutTable();
		tab.init();
		String oidstring = "1.3.6.1.2.1.2.2.1.0";//对应OID请查询snmp的oid说明文件
//		OID oid = OIDConstants.INTERFACE_ifEntry_OperStatus;
//		OID adminStatus = OIDConstants.INTERFACE_ifEntry_AdminStatus;
		String resultOId = "";
		StringBuffer mibValue = new StringBuffer();
		for (;true;) {

			if(a==0){
				resultOId = oidstring;
				a = 1;
			}
			pdu.add(new VariableBinding(new OID(resultOId)));
			pdu.setType(PDU.GETNEXT);
			ResponseEvent response;
			try {
				response = tab.snmp.send(pdu, tab.target);
				if(response == null || response.getResponse() == null){continue;}
				Vector variableBindings = response.getResponse()
						.getVariableBindings();
				
				String variable = variableBindings.toString();
				int i = variable.indexOf("=");
				int j = variable.length();
				resultOId = variable.substring(1, i - 1);
				String routetype = variable.substring(i + 1, j - 1);
//				if(!(resultOId.substring(0, 20).equals(oidstring.substring(0, 20)))){
//					return;
//				}
				mibValue.append(variable + "\r\n");
				if(!(resultOId.startsWith("1.3.6.1.2.1.2"))) break;
				System.out.print(resultOId);
				System.out.println(routetype);
				
				pdu.remove(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Writer writer = new BufferedWriter(new FileWriter(new File("d:\\mib_ip.txt")));
		try {
			writer.write(mibValue.toString());
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			writer.close();
		}
	}

	

}
