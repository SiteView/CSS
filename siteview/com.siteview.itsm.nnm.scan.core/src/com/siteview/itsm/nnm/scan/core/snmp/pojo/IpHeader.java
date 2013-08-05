package com.siteview.itsm.nnm.scan.core.snmp.pojo;

public class IpHeader {

	/**
	 * IP ͷ����
	 */
	private char h_len = 4;
	/**
	 * ip�汾
	 */
	private char version = 4;
	/**
	 * ��������
	 */
	private char tos;
	/**
	 * ���ݱ�����
	 */
	private short total_len;
	/**
	 * id
	 */
	private short ident;
	/**
	 * 
	 */
	private short flags;
	/**
	 * ����ʱ��
	 */
	private char ttl;
	/**
	 * Э��(TCP, UDP etc)
	 */
	
	private char proto;
	/**
	 * IP У���
	 */
	private short checksum;
	private long source_ip;
	private long dest_ip;
	public char getH_len() {
		return h_len;
	}
	public void setH_len(char h_len) {
		this.h_len = h_len;
	}
	public char getVersion() {
		return version;
	}
	public void setVersion(char version) {
		this.version = version;
	}
	public char getTos() {
		return tos;
	}
	public void setTos(char tos) {
		this.tos = tos;
	}
	public short getTotal_len() {
		return total_len;
	}
	public void setTotal_len(short total_len) {
		this.total_len = total_len;
	}
	public short getIdent() {
		return ident;
	}
	public void setIdent(short ident) {
		this.ident = ident;
	}
	public short getFlags() {
		return flags;
	}
	public void setFlags(short flags) {
		this.flags = flags;
	}
	public char getTtl() {
		return ttl;
	}
	public void setTtl(char ttl) {
		this.ttl = ttl;
	}
	public char getProto() {
		return proto;
	}
	public void setProto(char proto) {
		this.proto = proto;
	}
	public short getChecksum() {
		return checksum;
	}
	public void setChecksum(short checksum) {
		this.checksum = checksum;
	}
	public long getSource_ip() {
		return source_ip;
	}
	public void setSource_ip(long source_ip) {
		this.source_ip = source_ip;
	}
	public long getDest_ip() {
		return dest_ip;
	}
	public void setDest_ip(long dest_ip) {
		this.dest_ip = dest_ip;
	}
	
	
}
