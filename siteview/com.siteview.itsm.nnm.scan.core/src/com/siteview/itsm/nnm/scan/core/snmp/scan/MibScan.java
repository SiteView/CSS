package com.siteview.itsm.nnm.scan.core.snmp.scan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import com.siteview.itsm.nnm.scan.core.snmp.common.SnmpPara;
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Directitem;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.InterfaceTable;
import com.siteview.itsm.nnm.scan.core.snmp.util.ScanUtils;

public class MibScan {

	public static final int defaultVersion = SnmpConstants.version1;
	public void scan(){
		
	}
	public void scan(String ip,int port){
		
	}
	public int getVersion(String snmpVer){
		if(snmpVer.equals(String.valueOf(SnmpConstants.version1))){
			return SnmpConstants.version1;
		}
		if(snmpVer.equals(String.valueOf(SnmpConstants.version2c))){
			return SnmpConstants.version2c;
		}
		if(snmpVer.equals(String.valueOf(SnmpConstants.version3))){
			return SnmpConstants.version3;
		}
		return defaultVersion;
	}
	public List<Pair<String,String>> getMibTable(SnmpPara par,String oidStr){
		return getMibTable(getVersion(par.getSnmpver()), par, oidStr);
	}
	public void getFullTableMib(SnmpPara par,String oidStr){
		getFullTableMib(getVersion(par.getSnmpver()),par, oidStr);
	}
	public String getMibObject(SnmpPara par,String oidStr){
		return getMibObject(getVersion(par.getSnmpver()),par, oidStr);
	}
	public String getMibObject(int version,SnmpPara par,String oidStr){
		String result = "";
		String ip = par.getIp().indexOf("/") < 0 ?par.getIp() + "/161" : par.getIp();
		UdpAddress address = new UdpAddress(ip);
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
			VariableBinding vb = response.get(0);
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
		String ip = par.getIp().indexOf("/") < 0 ?par.getIp() + "/161" : par.getIp();
		UdpAddress address = new UdpAddress(ip);
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
					break;
				}
				PDU response = event.getResponse();
				if(response == null){
					break;
				}
				Vector<? extends VariableBinding> vbs = response.getVariableBindings();
				for(VariableBinding vb :vbs){
					if(!vb.getOid().toString().startsWith(oid.toString())){
						isOut = true;
						break;
					}
					itemOid = vb.getOid();
					Pair<String, String> temp = new Pair<String,String>();
					temp.setFirst(vb.getOid().toString());
					temp.setSecond(vb.getVariable().toString());
					result.add(temp);
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
	public static void mbain(String[] args) {
		new MibScan().getMibTable(
				new SnmpPara("192.168.9.1", "public@0", 300, 2), "1.3.6.1.2.1.17.1.4.1.2");
//				[public@9, public@4, public@0]
	}
	public static void main(String[] args) {
		MibScan scan = new MibScan();
		SnmpPara sp = new SnmpPara();
		sp.setCommunity("public");
		sp.setIp("192.168.9.1");
		sp.setRetry(2);
		sp.setTimeout(300);
		
		String result = scan.getMibObject(sp, "1.3.6.1.2.1.2.1.0");//"1.3.6.1.2.1.2.1.0");
		System.out.println(result);
	}
	public static void maain(String[] args) {
		Map<String, List<Directitem>> map = new HashMap<String, List<Directitem>>();
		List<Directitem> l1 = new ArrayList<Directitem>();
		l1.add(buildd("l1_1"));
		l1.add(buildd("l1_2"));
		l1.add(buildd("l1_3"));
		l1.add(buildd("l1_4"));
		l1.add(buildd("l1_5"));
		
		List<Directitem> l2 = new ArrayList<Directitem>();
		l2.add(buildd("l2_1"));
		l2.add(buildd("l2_2"));
		l2.add(buildd("l2_3"));
		l2.add(buildd("l2_4"));
		l2.add(buildd("21_5"));
		map.put("li", l1);
		map.put("l2", l2);
		
		for(Entry<String, List<Directitem>> entry : map.entrySet()){
//			Iterator<Directitem> it = entry.getValue().iterator();
			for(Iterator<Directitem> it = entry.getValue().iterator();it.hasNext();){
				Directitem iter = it.next();
				if(iter.getLocalPortDsc().equals("l2_2_localportdsc")){
					it.remove();
				}
			}
		}
//		Set<String> keys = map.keySet();
//		for(String key : keys){
//			List<Directitem> iters = map.get(key);
//			for(Directitem iter : iters){
//				if(iter.getLocalPortDsc().equals("l2_2_localportdsc")){
//					iters.remove(iter);
//				}
//			}
//		}
		for(Entry<String, List<Directitem>> entry : map.entrySet()){
			for(Directitem iter : entry.getValue()){
				System.out.println(iter.getLocalPortDsc());
			}
		}
		
 	}
	public static Directitem buildd(String prefix){
		Directitem iter = new Directitem();
		iter.setLocalPortDsc(prefix +"_localportdsc");
		iter.setLocalPortInx(prefix + "_localportinx");
		iter.setPeerId(prefix +"_peerid");
		iter.setPeerPortInx(prefix+"_peerportinx");
		return iter;
	}
	/**
	 * 获取表数据
	 * @param spr
	 * @return
	 */
	public static void getFullTableMib(int version,SnmpPara spr,String oid){
		
			Map<String,InterfaceTable> result = new HashMap<String, InterfaceTable>();
			CommunityTarget target = ScanUtils.buildGetPduCommunityTarget(spr.getIp(), 161, spr.getCommunity(), spr.getTimeout(), spr.getRetry(), Integer.parseInt(spr.getSnmpver()));
			Snmp snmp = new Snmp();
			TransportMapping transport1 = null;
			try {
				transport1 = new DefaultUdpTransportMapping();
				snmp = new Snmp(transport1);
				snmp.listen();
				TableUtils utils = new TableUtils(snmp,new DefaultPDUFactory());
				utils.setMaxNumRowsPerPDU(5);
				OID[] columnOIDs = new OID[]{
						new OID(oid)
				};
				List<TableEvent> te = utils.getTable(target, columnOIDs, new OID("1"), new OID("50"));
				for (TableEvent t : te) {
					System.out.println(t);
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
	}
}
