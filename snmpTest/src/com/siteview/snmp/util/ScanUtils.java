package com.siteview.snmp.util;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;

import com.siteview.snmp.model.Pair;

public class ScanUtils {


	public static Pair<String, String> getScaleByIPMask(
			Pair<String, String> ipMask) {
		long ipnum = ipToLong(ipMask.getFirst());
		long masknum = ipToLong(ipMask.getSecond());
		long allipnum = ipToLong("255.255.255.255");
		long subnet = ipnum & masknum;
		long ip_min = subnet + 1;
		long ip_max = ((allipnum - masknum) | subnet) - 1;
		if (ipMask.getSecond().equals("255.255.255.255")) {
			return new Pair<String, String>(ipMask.getFirst(),
					ipMask.getSecond());
		} else {
			String ipStrMin = longToIp(ip_min);
			String ipStrMax = longToIp(ip_max);
			return new Pair<String, String>(ipStrMin, ipStrMax);
		}
		// unsigned long ipnum = ntohl(inet_addr(ipmask.first.c_str()));
		// unsigned long masknum = ntohl(inet_addr(ipmask.second.c_str()));
		// unsigned long allipnum= ntohl(inet_addr("255.255.255.255"));
		// unsigned long subnet = ipnum & masknum;
		// unsigned long ip_min = subnet + 1;
		// unsigned long ip_max = ((allipnum - masknum) | subnet) - 1;
		// struct in_addr ipmin, ipmax;
		// if(ipmask.second == "255.255.255.255")
		// {
		// return(make_pair(ipmask.first, ipmask.first));
		// }
		// else
		// {
		// ipmin.S_un.S_addr = htonl(ip_min);
		// ipmax.S_un.S_addr = htonl(ip_max);
		// string ipStrMin = inet_ntoa(ipmin);
		// string ipStrMax = inet_ntoa(ipmax);
		// return(make_pair(ipStrMin, ipStrMax));
		// }
	}

	public static Target buildTarget(String ip, int port, String community,
			int timeout, int retry, int version) {
		UdpAddress add = new UdpAddress(ip + "/" + port);
		CommunityTarget target = new CommunityTarget();
		target.setAddress(add);
		target.setVersion(version);
		target.setRetries(retry);
		target.setTimeout(timeout);
		target.setCommunity(new OctetString(community));
		return target;
	}

	public static long ipToLong(String ip) {
		String[] ips = ip.split("\\.");
		long[] ipLong = new long[4];
		ipLong[0] = Long.parseLong(ips[0]);
		ipLong[1] = Long.parseLong(ips[1]);
		ipLong[2] = Long.parseLong(ips[2]);
		ipLong[3] = Long.parseLong(ips[3]);
		return (ipLong[0] << 24) + (ipLong[1] << 16) + (ipLong[2] << 8)
				+ ipLong[3];
	}

	public static String longToIp(long value) {
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf(value >>> 24));// 直接右移24位
		sb.append(".");
		sb.append(String.valueOf((value & 0x00ffffff) >>> 16));// 将高8位置0，然后右移16位
		sb.append(".");
		sb.append(String.valueOf((value & 0x0000ffff) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(value & 0x000000ff));
		return sb.toString();
	}

	public static byte[] longToBytes(long n) {
		byte[] b = new byte[8];
		b[7] = (byte) (n & 0xff);
		b[6] = (byte) (n >> 8 & 0xff);
		b[5] = (byte) (n >> 16 & 0xff);
		b[4] = (byte) (n >> 24 & 0xff);
		b[3] = (byte) (n >> 32 & 0xff);
		b[2] = (byte) (n >> 40 & 0xff);
		b[1] = (byte) (n >> 48 & 0xff);
		b[0] = (byte) (n >> 56 & 0xff);
		return b;
	}

	public static long bytesToLong(byte[] array, int offset) {
		return ((((long) array[offset + 0] & 0xff) << 56)
				| (((long) array[offset + 1] & 0xff) << 48)
				| (((long) array[offset + 2] & 0xff) << 40)
				| (((long) array[offset + 3] & 0xff) << 32)
				| (((long) array[offset + 4] & 0xff) << 24)
				| (((long) array[offset + 5] & 0xff) << 16)
				| (((long) array[offset + 6] & 0xff) << 8) | (((long) array[offset + 7] & 0xff) << 0));
	}

	public static void main(String[] args) {
		long i = ipToLong("192.168.0.248");
		System.out.println(i);
		System.out.println(longToIp(i));
	}
	
}
