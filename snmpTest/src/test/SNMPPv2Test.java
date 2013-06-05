package test;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

public class SNMPPv2Test {

	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
		snmp.listen();
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("public"));
		target.setVersion(SnmpConstants.version1);
		target.setAddress(new UdpAddress("192.168.9.212/161"));
		target.setTimeout(3000);
		target.setRetries(1);
		
		sendRequest(snmp, createGetPdu(), target);
		sendRequest(snmp, CreateGetNextPdu(), target);
		sendRequest(snmp, createGetBulkPdu(), target);
		snmpWalk(snmp, target);
		
		target.setCommunity(new OctetString("private"));
		sendRequest(snmp, createSetPdu(), target);
		
		CommunityTarget boadcastTarget = new CommunityTarget();
		boadcastTarget.setCommunity(new OctetString("public"));
		boadcastTarget.setVersion(SnmpConstants.version2c);
		boadcastTarget.setAddress(new UdpAddress("192.168.0.248/161"));
		boadcastTarget.setTimeout(5000);
		
		sendAsyncRequest(snmp, CreateGetNextPdu(), boadcastTarget);
		Thread.sleep(6000);
		
	}
	
	public static PDU createGetPdu(){
		PDU pdu = new PDU();
		pdu.setType(PDU.GET);
		pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3.0")));
		pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0")));
		pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5")));
		return pdu;
	}
	
	private static PDU CreateGetNextPdu(){
		PDU pdu = new PDU();
		pdu.setType(PDU.GETNEXT);
		pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3")));
		pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5")));
		return pdu;
	}
	private static PDU createGetBulkPdu(){
		PDU pdu = new PDU();
		pdu.setType(PDU.GETBULK);
		pdu.setMaxRepetitions(10);
		pdu.setNonRepeaters(0);
		pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1")));
		return pdu;
	}
	private static PDU createSetPdu(){
		PDU pdu = new PDU();
		pdu.setType(PDU.SET);
		pdu.add(new VariableBinding(new OID(""),new OctetString("sysname")));
		
		return pdu;
	}
	private static void sendRequest(Snmp snmp,PDU pdu,CommunityTarget target) throws IOException{
		ResponseEvent event = snmp.send(pdu, target);
		PDU response = event.getRequest();
		if(response == null){
			System.out.println("TimeOut....");
		}else{
			if(response.getErrorStatus() == PDU.noError){
				Vector<? extends VariableBinding> vbs = response.getVariableBindings();
				for(VariableBinding vb :vbs){
					System.out.println(vb+"," + vb.getVariable().getSyntaxString());
				}
			}else{
				System.out.println("error:" + response.getErrorStatusText());
			}
			
		}
	}
	private static void sendAsyncRequest(Snmp snmp, PDU pdu, CommunityTarget target) throws IOException{
		snmp.send(pdu, target, null, new ResponseListener() {
			@Override
			public void onResponse(ResponseEvent event) {
				PDU response = event.getResponse();
				System.out.println("Got response from "+ event.getPeerAddress());
				if(response== null){
					System.out.println("Time Out");
				}else{
					if(response.getErrorStatus() == PDU.noError){
						Vector<? extends VariableBinding> vbs = response.getVariableBindings();
						for(VariableBinding vb :vbs){
							System.out.println(vb+"," + vb.getVariable().getSyntaxString());
						}
					}else{
						System.out.println("error:" + response.getErrorStatusText());
					}
				}
				
			}
		});
	}
	private static void snmpWalk(Snmp snmp,CommunityTarget target){
		TableUtils utils = new TableUtils(snmp,new DefaultPDUFactory(PDU.GETBULK));
		utils.setMaxNumRowsPerPDU(5);
		OID[] columnOids = new OID[] { 
					new OID("1.3.6.1.2.1.1.9.1.2"),
					new OID("1.3.6.1.2.1.1.9.1.3"), 
					new OID("1.3.6.1.2.1.1.9.1.5") 
				};// sysorid
		List<TableEvent> l = utils.getTable(target, columnOids, new OID("3"),
				new OID("10"));
		for (TableEvent e : l) {
			System.out.println(e);
		}
	}
}
