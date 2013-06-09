package com.siteview.snmp.common;

public class SnmpPara {
		String ip;
		String community;
		int timeout=300;
		int retry=3;
		String snmpver="0";
		
		public SnmpPara(){
			
		}
		public SnmpPara(String ip, String community, int timeout,int retry, String snmpver){
			this.ip = ip;
			this.community = community;
			this.timeout = timeout;
			this.retry = retry;
			this.snmpver = snmpver;
		}
}
