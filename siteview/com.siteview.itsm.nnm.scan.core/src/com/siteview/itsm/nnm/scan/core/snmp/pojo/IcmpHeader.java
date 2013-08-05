package com.siteview.itsm.nnm.scan.core.snmp.pojo;

public class IcmpHeader {

	private char type ;//icmp����
	private char code ;//����
	private short checksum;//У���
	private short id;//id
	private short seq; //���
	private long timestamp;//��¼ʱ��
	public char getType() {
		return type;
	}
	public void setType(char type) {
		this.type = type;
	}
	public char getCode() {
		return code;
	}
	public void setCode(char code) {
		this.code = code;
	}
	public short getChecksum() {
		return checksum;
	}
	public void setChecksum(short checksum) {
		this.checksum = checksum;
	}
	public short getId() {
		return id;
	}
	public void setId(short id) {
		this.id = id;
	}
	public short getSeq() {
		return seq;
	}
	public void setSeq(short seq) {
		this.seq = seq;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
