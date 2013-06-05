package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public abstract class BaseRequest {

	public Snmp snmp = null;
    private PDU request = new PDU();
    List<OID> oids = new ArrayList<OID>();
    private static final String COMMUNITY248 = "siteview";
    
    public void buildSystemOid(){
    	oids.add(SnmpConstants.sysContact);
        oids.add(SnmpConstants.sysDescr);
        oids.add(SnmpConstants.sysLocation);
        oids.add(SnmpConstants.sysName);
        oids.add(SnmpConstants.sysObjectID);
        oids.add(SnmpConstants.sysOREntry);
        oids.add(SnmpConstants.sysServices);
        oids.add(SnmpConstants.sysUpTime);
    }
    public void buildOid(){
    	buildSystemOid();
    }
    
    public CommunityTarget buildGetCommunityTarget(String ip,String type,String community){
    	CommunityTarget target = new CommunityTarget();
    	target.setAddress(buildAddress(ip, (type == null || ("".equals(type)))?"udp":type));
    	target.setCommunity(new OctetString(community));
		target.setRetries(2);
		target.setTimeout(200);
		target.setVersion(SnmpConstants.version2c);
		return target;
    }
    public Address buildAddress(String ip,String type){
    	return GenericAddress.parse(type+":" + ip + "/161");
    }
    public Address buildAddress(String ip,String type,String port){
    	return GenericAddress.parse(type+":" + ip + "/" + port);
    }
    public abstract PDU buildPDU();
    
    protected void init() throws IOException {
        // 设定采取的协议--SNMP
        TransportMapping transport1  = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport1);
        snmp.listen();
    }
    public ResponseEvent send(PDU pdu,Target target) throws IOException{
    	if(snmp == null) init();
    	return snmp.send( pdu, target);
    }
    public void sendSynchronismGet(String ip,String type,String community){
    	try{
	    	ResponseEvent responseEvent = send(buildPDU(), buildGetCommunityTarget(ip,type,community));
	    	// 若返回结果不为空，则设备支持SNMP协议
	    	PDU response=responseEvent.getResponse();
	    	
		        if(response != null){
		            Vector<? extends VariableBinding> a = response.getVariableBindings();
		            for(VariableBinding vb : a){
		                System.out.println(vb.getOid()+" = "+vb.getVariable());   
		            }
		        }else{ 
		            System.out.println("返回结果为null!");
		        }
    	}catch (Exception e) {
    		e.printStackTrace();
		}
        finally{
        	if(snmp!=null) {
        		try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
    }
}
