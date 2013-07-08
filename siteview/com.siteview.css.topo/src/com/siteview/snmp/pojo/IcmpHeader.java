package com.siteview.snmp.pojo;

public class IcmpHeader {

	private char type ;//icmp类型
	private char code ;//代码
	private short checksum;//校验和
	private short id;//id
	private short seq; //序号
	private long timestamp;//记录时间
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
