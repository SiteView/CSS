package com.siteview.itsm.nnm.scan.core.snmp.scan;

public abstract class TelnetReceivedHandler {

	public abstract void callback(byte[] buff,int length);
}
