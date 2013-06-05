package test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

public class SnmpGetBaseInfo extends BaseRequest {
	public static final String sysPrefix = "1.3.6.1.2.1.1";
	/**
	 * 系统描述
	 */
	public static final OID sysDescr = new OID(sysPrefix+".1.0");
	
	/**
	 * 系统OID
	 */
	public static final OID sysObjectId = new OID(sysPrefix+".2.0");
	/**
	 * 系统开启时间
	 */
	public static final OID sysUptime = new OID(sysPrefix+".3.0");
	/**
	 * 系统厂商联系人
	 */
	public static final OID sysContact = new OID(sysPrefix+".4.0");
	/**
	 * 系统名称
	 */
	public static final OID sysName = new OID(sysPrefix+".5.0");
	/**
	 * 设备物理位置
	 */
	public static final OID sysLocation = new OID(sysPrefix+".6.0");
	

	public static final String SYSDESCR = "sysDescr";
	public static final String SYSOBJECTID = "sysObjectID";
	public static final String SYSUPTIME	= "sysUptime";
	public static final String SYSCONTACT = "sysContact";
	public static final String SYSNAME = "sysName";
	public static final String SYSLOCATION = "sysLocation";
	private Snmp snmp;
	
	private CommunityTarget target;
	
	private PDU request;
	
	public void init(){
		
	}

	@Override
	public PDU buildPDU() {
		request = new PDU();
		request.setType(PDU.GET);
		request.add(new VariableBinding(sysDescr));
		request.add(new VariableBinding(sysObjectId));
		request.add(new VariableBinding(sysUptime));
		request.add(new VariableBinding(sysContact));
		request.add(new VariableBinding(sysName));
		request.add(new VariableBinding(sysLocation));
		return request;
	}
	public Map<String, Variable> getBaseInfo(){
		Map<String, Variable> result = new HashMap<String ,Variable>();
		target = new CommunityTarget(buildAddress("192.168.0.248", "udp","161"), new OctetString("siteview"));
		try {
			super.init();
			ResponseEvent responseEvent = super.snmp.send(buildPDU(), target);
			PDU response = responseEvent.getResponse();
			Vector<? extends VariableBinding> vbs = response.getVariableBindings();
			for(VariableBinding vb:vbs){
				String key = getKeyFromOID(vb.getOid());
				if(key == null){
					continue;
				}
				result.put(key, vb.getVariable());
//				System.out.println(vb.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	public String getKeyFromOID(OID oid){
		if(oid == null) return null;
		if(oid.toString().equals(sysDescr.toString())){
			return SYSDESCR;
		}else if(oid.toString().equals(sysObjectId.toString())){
			return SYSOBJECTID;
		}else if(oid.toString().equals(sysUptime.toString())){
			return SYSUPTIME;
		}else if(oid.toString().equals(sysContact.toString())){
			return SYSCONTACT;
		}else if(oid.toString().equals(sysName.toString())){
			return SYSNAME;
		}else if(oid.toString().equals(sysLocation.toString())){
			return SYSLOCATION;
		}
		return null;
	}
	public void getDevicePort(){
		
	}
	public static void main(String[] args) {
		SnmpGetBaseInfo info = new SnmpGetBaseInfo();
		Map<String, Variable> result = info.getBaseInfo();
		Set<String> keys = result.keySet();
		Iterator<String> i = keys.iterator();
		while(i.hasNext()){
			String key = i.next();
			System.out.println(key + " = " + result.get(key));
		}
	}
	
	
}
