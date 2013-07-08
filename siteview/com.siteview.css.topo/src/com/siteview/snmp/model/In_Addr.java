package com.siteview.snmp.model;

public class In_Addr {

	long S_addr;
	
	private S_un_b s_un_b;
	
	private S_un_w s_un_w;
	
	public long getS_addr() {
		return S_addr;
	}
	public void setS_addr(long s_addr) {
		S_addr = s_addr;
	}
	public S_un_b getS_un_b() {
		return s_un_b;
	}
	public void setS_un_b(S_un_b s_un_b) {
		this.s_un_b = s_un_b;
	}
	public S_un_w getS_un_w() {
		return s_un_w;
	}
	public void setS_un_w(S_un_w s_un_w) {
		this.s_un_w = s_un_w;
	}
	class S_un_b{
		private char s_b1,s_b2,s_b3,s_b4;

		public char getS_b1() {
			return s_b1;
		}

		public void setS_b1(char s_b1) {
			this.s_b1 = s_b1;
		}

		public char getS_b2() {
			return s_b2;
		}

		public void setS_b2(char s_b2) {
			this.s_b2 = s_b2;
		}

		public char getS_b3() {
			return s_b3;
		}

		public void setS_b3(char s_b3) {
			this.s_b3 = s_b3;
		}

		public char getS_b4() {
			return s_b4;
		}

		public void setS_b4(char s_b4) {
			this.s_b4 = s_b4;
		}
		
	}
	class S_un_w{
		private short s_w1,s_w2;

		public short getS_w1() {
			return s_w1;
		}

		public void setS_w1(short s_w1) {
			this.s_w1 = s_w1;
		}

		public short getS_w2() {
			return s_w2;
		}

		public void setS_w2(short s_w2) {
			this.s_w2 = s_w2;
		}
		
	}
}
