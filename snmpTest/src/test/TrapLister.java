package test;

import java.io.IOException;
import java.util.Vector;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class TrapLister implements CommandResponder{
	
	private Snmp snmp  = null;
	public void init() throws IOException{
		CommunityTarget target = new CommunityTarget();
		target.setAddress(GenericAddress.parse("192.168.0.248"));
		target.setCommunity(new OctetString("siteview"));
		TransportMapping transport =
		        new DefaultUdpTransportMapping(new UdpAddress("192.168.0.248/162"));
		snmp = new Snmp(transport);
		snmp.addCommandResponder(this);
		snmp.listen();
	}
	@Override
	public void processPdu(CommandResponderEvent event) {
		PDU response = event.getPDU();
		if(response!=null&&response.size()>0){
			Vector<? extends VariableBinding> results = response.getVariableBindings();
			for(VariableBinding vb : results){
				System.out.println(vb.getOid() + ":" + vb.getVariable());
			}
		}
		
	}
	public static void main(String[] args) {
		TrapLister lister = new TrapLister();
		try {
			lister.init();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				lister.snmp.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
