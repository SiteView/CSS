package com.siteview.snmp.scan;

public abstract class TelnetReceivedHandler {

	public abstract void callback(byte[] buff,int length);
}
