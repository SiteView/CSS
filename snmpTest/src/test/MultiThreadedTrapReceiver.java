package test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Vector;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
/**
 * 本类用于监听代理进程的Trap信息 
 * @author haiming.wang
 *
 */
public class MultiThreadedTrapReceiver implements CommandResponder {

	
	 private MultiThreadedMessageDispatcher dispatcher;  
	 private Snmp snmp = null;  
	 private Address listenAddress;  
	 private ThreadPool threadPool; 
	 private void init() throws UnknownHostException, IOException {  
	        threadPool = ThreadPool.create("Trap", 2);  
	        dispatcher = new MultiThreadedMessageDispatcher(threadPool,  
	                new MessageDispatcherImpl());  
	        listenAddress = GenericAddress.parse(System.getProperty(  
	                "snmp4j.listenAddress", "udp:192.168.0.248/162")); // 本地IP与监听端口  
	        @SuppressWarnings("rawtypes")
			TransportMapping transport;  
	        // 对TCP与UDP协议进行处理  
	        if (listenAddress instanceof UdpAddress) {  
	            transport = new DefaultUdpTransportMapping(  
	                    (UdpAddress) listenAddress);  
	        } else {  
	            transport = new DefaultTcpTransportMapping(
	                    (TcpAddress) listenAddress);  
	        }  
	        snmp = new Snmp(dispatcher, transport);  
	        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());  
	        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());  
	        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());  
	        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3  
	                .createLocalEngineID()), 0);  
	        SecurityModels.getInstance().addSecurityModel(usm);  
	        snmp.listen();  
	    }  
	  
	      
	    public void run() {  
	        try {  
	            init();  
	            snmp.addCommandResponder(this);  
	            System.out.println("开始监听Trap信息!");  
	        } catch (Exception ex) {  
	            ex.printStackTrace();  
	        }  
	    }  
	  
	    /** 
	     * 实现CommandResponder的processPdu方法, 用于处理传入的请求、PDU等信息 
	     * 当接收到trap时，会自动进入这个方法 
	     *  
	     * @param respEvnt 
	     */  
	    public void processPdu(CommandResponderEvent respEvnt) {  
	    	System.out.println(respEvnt.getPDU());
	        // 解析Response  
	        if (respEvnt != null && respEvnt.getPDU() != null) {  
	            @SuppressWarnings("unchecked")
				Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getPDU().getVariableBindings();  
	            for (int i = 0; i < recVBs.size(); i++) {  
	                VariableBinding recVB = recVBs.elementAt(i);  
	                System.out.println(recVB.getOid() + " : " + recVB.getVariable());  
	            }  
	        }  
	    }  
	  
	    public static void main(String[] args) throws UnknownHostException, IOException {  
	        MultiThreadedTrapReceiver multithreadedtrapreceiver = new MultiThreadedTrapReceiver();  
//	        multithreadedtrapreceiver.run();
	        multithreadedtrapreceiver.init();
	        multithreadedtrapreceiver.getRequest();
	        
	    }  
	    public void getRequest() throws IOException{
	    	CommunityTarget cTarget = new CommunityTarget();
	    	Address address = GenericAddress.parse(System.getProperty(  
	                "snmp4j.listenAddress", "udp:192.168.0.248/161"));
	    	cTarget.setAddress(address);
	    	cTarget.setVersion(SnmpConstants.version1);
	    	cTarget.setCommunity(new OctetString("siteview"));
	    	cTarget.setRetries(5);
	    	cTarget.setTimeout(1000);
	    	PDU pdu = new PDU();
	    	pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.0")));
	    	pdu.setType(1);
	    	ResponseEvent event = snmp.send(pdu, cTarget);
	    	PDU response = event.getResponse();
	    	if(response!=null && response.getBERLength()> 0 ){
	    		System.out.println(response.getRequestID());
	    	}
	    		
	    }

}
