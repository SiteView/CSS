package com.siteview.itsm.nnm.scan.core.snmp.model;

public class SockaddrIn {

	private short	sin_family;
	private short	sin_port;
	private In_Addr sin_addr;
	private char	sin_zero;
	public short getSin_family() {
		return sin_family;
	}
	public void setSin_family(short sin_family) {
		this.sin_family = sin_family;
	}
	public short getSin_port() {
		return sin_port;
	}
	public void setSin_port(short sin_port) {
		this.sin_port = sin_port;
	}
	public In_Addr getSin_addr() {
		return sin_addr;
	}
	public void setSin_addr(In_Addr sin_addr) {
		this.sin_addr = sin_addr;
	}
	public char getSin_zero() {
		return sin_zero;
	}
	public void setSin_zero(char sin_zero) {
		this.sin_zero = sin_zero;
	} 	
	
}
