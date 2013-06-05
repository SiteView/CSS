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

public class DevSnmpTest {
	private Snmp snmp = null;
    private CommunityTarget myTarget = null;
    private PDU request = new PDU();
    List<String> oidList=new ArrayList<String>();
    List<OID> oids = new ArrayList<OID>();
    private static final String COMMUNITY248 = "siteview";
    private static String USE_COMMUNITY;
    //Initialize
    private void init() throws IOException {
    	oidList.add("1.3.6.1.2.1.1.1.0");//sysDescr系统描述  
    	oidList.add("1.3.6.1.2.1.1.2.0");//产商oid 
    	oidList.add("1.3.6.1.2.1.1.3.0");//开启时间  
    	oidList.add("1.3.6.1.2.1.1.4.0");//  
        oidList.add("1.3.6.1.2.1.1.5.0");//sysName计算机名
        oidList.add("1.3.6.1.2.1.1.6.0");
        oidList.add("1.3.6.1.2.1.1.7.0");
//        oidList.add("1.3.6.1.2.1.1.9.1.2.0");
        //interface组
        oidList.add("1.3.6.1.2.1.2.1.0");//本地系统中包含的网络接口总数
        oidList.add("1.3.6.1.2.1.2.1.1.0");//接口表项的一行。
        //oidList.add("1.3.6.1.2.1.2.3.0");//不可访问  一个指定的接口表项，包含似有该对象下定义的对象
        
        
        //
        oidList.add("1.3.6.1.2.1.4.21.1");//iproutedest的对象标识符
//        oidList.add("1.3.6.1.2.1.1.1.1.0");//
        oids.add(SnmpConstants.coldStart);
        oids.add(SnmpConstants.authenticationFailure);
        oids.add(SnmpConstants.linkDown);
        oids.add(SnmpConstants.linkUp);
        oids.add(SnmpConstants.snmpEngineID);
        oids.add(SnmpConstants.snmpInPkts);
        oids.add(SnmpConstants.snmpInASNParseErrs);
        oids.add(SnmpConstants.snmpInBadCommunityNames);
        oids.add(SnmpConstants.snmpInBadCommunityUses);
        oids.add(SnmpConstants.snmpInBadVersions);
        oids.add(SnmpConstants.snmpInPkts);
        oids.add(SnmpConstants.snmpInvalidMsgs);
        //设置CommunityTarget
        myTarget = new CommunityTarget();
        myTarget.setRetries(2);
        myTarget.setTimeout(200);   
        myTarget.setVersion(SnmpConstants.version2c);
        // 设定采取的协议--SNMP
        TransportMapping transport1  = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport1);
        snmp.listen();
    }
    private static final String URL9_188 = "192.168.9.188";
    
    private static final String URL9_212 = "192.168.9.212";
    
    private static final String URL0_248 = "192.168.0.248";
    
    private static final String URL0_251 = "192.168.0.251";
    
    private static final String URL9_36 = "192.168.9.36";
    
    private static final String URL9_211 = "192.168.9.211";
    
    private static String USE_URL = "";
    public ResponseEvent send(PDU pdu,Target target) throws IOException{
    	if(snmp == null) init();
    	return snmp.send(pdu, target);
    }
    //Judge the device support Snmp or not
    public boolean isDevSupportSNMP(String ip,String getCommunity) {
       
        boolean isSupport = false;
        try{
            //未被初始化，则初始化
            if(snmp ==  null){ 
                init(); 
            }
            //设置CommunityTarget,IP及Community
            Address deviceAddr = GenericAddress.parse("udp:"+USE_URL+"/161");
            myTarget.setAddress(deviceAddr);
            myTarget.setCommunity(new OctetString(getCommunity));
            // 尝试获取MIB设备中的sysName
//            String oidstr ="1.3.6.1.4.1.6247.24.1.2.2.1.0";
            VariableBinding var = new VariableBinding(SnmpConstants.snmpEngineID);
            request.clear();
            for(String oid:oidList){
            	request.add(new VariableBinding(new OID(oid)));
            }
            for(OID o:oids){
            	request.add(new VariableBinding(o));
            }
            request.setType(PDU.GET);
            ResponseEvent responseEvent = send(request, myTarget);
            PDU response=responseEvent.getResponse();
            // 若返回结果不为空，则设备支持SNMP协议
            if(response != null){
                isSupport = true;
//                System.out.println(response.getVariableBindings().get(0).getVariable().getSyntaxString());
                Vector<? extends VariableBinding> a = response.getVariableBindings();
                for(VariableBinding vb : a){
                    System.out.println(vb.getOid()+" = "+vb.getVariable());   
                }
                
            }else{  // 返回结果为空，则设备不支持SNMP协议
                isSupport = false;
                System.out.println("返回结果为null!");
            }
            //关闭SNMP连接
        }catch(Exception e){
            isSupport = false;
        }
        return isSupport;
    }
    public void sendSet() throws IOException{
    	CommunityTarget target = new CommunityTarget();
		target.setAddress(GenericAddress.parse("udp:" + USE_URL + "/161"));
    	target.setTimeout(2000);
    	target.setRetries(2);
    	target.setCommunity(new OctetString("public"));
    	target.setVersion(SnmpConstants.version2c);
    	
    	ResponseEvent event = send(setPDU("1.3.6.1.2.1.1.5.0"),target);
    	
    	PDU response = event.getResponse();
    	
    	if(response != null){
//            System.out.println(response.getVariableBindings().get(0).getVariable().getSyntaxString());
            Vector<? extends VariableBinding> a = response.getVariableBindings();
            for(VariableBinding vb : a){
                System.out.println(vb.getOid()+" = "+vb.getVariable());   
            }
            response.getVariable(new OID());
            
        }else{  // 返回结果为空，则设备不支持SNMP协议
            System.out.println("返回结果为null!");
        }
    }
    public PDU setPDU(String oidString) throws IOException {
        // set PDU
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oidString), new OctetString("whm_asdf")));
        pdu.setType(PDU.SET);
        return pdu;
    }
    //关闭监听
    public void close(){
        try{
            if(snmp !=  null){
                snmp.close();
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    //简单测试
    public static void main(String[] args) throws IOException{
       
        DevSnmpTest dst = new DevSnmpTest();
        USE_URL = URL0_248;
        USE_COMMUNITY = COMMUNITY248;
        java.util.Date  begin0 = new java.util.Date();
        dst.isDevSupportSNMP(USE_URL,USE_COMMUNITY); 
//        dst.sendSet();
        java.util.Date  begin = new java.util.Date();
        System.out.println("Used: "+ (begin.getTime()-begin0.getTime()));
        dst.close();
    } 
}