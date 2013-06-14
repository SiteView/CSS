package com.siteview.snmp.scan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.model.Pair;

public class MibScan {

	public static final int defaultVersion = SnmpConstants.version1;
	public void scan(){
		
	}
	public void scan(String ip,int port){
		
	}
	public List<Pair<String,String>> getMibTable(SnmpPara par,String oidStr){
		return getMibTable(defaultVersion, par, oidStr);
	}
	public String getMibObject(SnmpPara par,String oidStr){
		return getMibObject(par, oidStr);
	}
	public String getMibObject(int version,SnmpPara par,String oidStr){
		String result = "";
		UdpAddress address = new UdpAddress(par.getIp());
		OID oid = new OID(oidStr);
		if(!oid.isValid()){
			return result;
		}	
		OID itemOid = oid;
		CommunityTarget target = new CommunityTarget();
		target.setVersion(version);
		target.setAddress(address);
		target.setCommunity(new OctetString(par.getCommunity()));
		target.setRetries(par.getRetry());
		target.setTimeout(par.getTimeout());
		Snmp snmp = null;
		try {
			snmp = new Snmp(new DefaultUdpTransportMapping());
			snmp.listen();
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(itemOid));
			pdu.setType(PDU.GET);
			ResponseEvent event = snmp.send(pdu, target);
			if(event == null){
				return null;
			}
			PDU response = event.getResponse();
			if(response == null){
				return null;
			}
			VariableBinding vb = event.getRequest().get(0);
			return vb.getVariable().toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(snmp != null){
				try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	public List<Pair<String,String>> getMibTable(int version,SnmpPara par,String oidStr){
		List<Pair<String,String>> result = new ArrayList<Pair<String,String>>();
		UdpAddress address = new UdpAddress(par.getIp());
		OID oid = new OID(oidStr);
		if(!oid.isValid()){
			return result;
		}	
		
		OID itemOid = oid;
		CommunityTarget target = new CommunityTarget();
		target.setVersion(version);
		target.setAddress(address);
		target.setCommunity(new OctetString(par.getCommunity()));
		target.setRetries(par.getRetry());
		target.setTimeout(par.getTimeout());
		Snmp snmp = null;
		try {
			snmp = new Snmp(new DefaultUdpTransportMapping());
			snmp.listen();
			boolean isOut = false;
			while(!isOut){
				PDU pdu = new PDU();
				pdu.add(new VariableBinding(itemOid));
				pdu.setType(PDU.GETNEXT);
				ResponseEvent event = snmp.getNext(pdu, target);
				if(event == null){
					continue;
				}
				PDU response = event.getResponse();
				if(response == null){
					continue;
				}
				Vector<? extends VariableBinding> vbs = response.getVariableBindings();
				for(VariableBinding vb :vbs){
					if(!vb.getOid().toString().startsWith(oidStr)){
						isOut = true;
						break;
					}
					itemOid = vb.getOid();
					Pair<String, String> temp = new Pair<String,String>();
					temp.setFirst(vb.getOid().toString());
					temp.setSecond(vb.getVariable().toString());
					result.add(temp);
					System.out.println("oid = " + vb.getOid().toString() + " : value = " + vb.getVariable().toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(snmp != null){
				try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	public static void main(String[] args) {
		MibScan scan = new MibScan();
		SnmpPara sp = new SnmpPara();
		sp.setCommunity("public");
		sp.setIp("192.168.0.248/161");
		sp.setRetry(2);
		sp.setTimeout(300);
		scan.getMibTable(sp, "1.3.6.1.2.1.4.22.1.2");
	}
}
