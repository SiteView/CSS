package com.siteview.snmp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;


import com.siteview.snmp.model.Pair;

public class ScanUtils {

	public static final int npos = -1;

	private static Map<String, Integer> tbl = new HashMap<String, Integer>();
	
	/**
	 * 构造snmpPDU
	 * @param ip 		IP地址
	 * @param port 		端口号
	 * @param community 共同体
	 * @param timeout 	超时时间
	 * @param retry   	重试时间
	 * @param version 	snmp版本
	 * @return
	 */
	public static CommunityTarget buildGetPduCommunityTarget(String ip,
			int port, String community, int timeout, int retry, int version) {
		UdpAddress add = new UdpAddress(ip + "/" + port);
		CommunityTarget target = new CommunityTarget();
		target.setAddress(add);
		target.setVersion(version);
		target.setRetries(retry);
		target.setTimeout(timeout);
		target.setCommunity(new OctetString(community));
		return target;
	}
	/**
	 * 根据子网掩码 获取IP地址范围
	 * @param ipMask
	 * @return
	 */
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
	}
	/**
	 * IP地址转换成数字long
	 * @param ip 
	 * @return
	 */
	public static long ipToLong(String ip) {
		String[] ips = ip.split("\\.");
		long[] ipLong = new long[4];
		ipLong[0] = Long.parseLong(ips[0].trim());
		ipLong[1] = Long.parseLong(ips[1].trim());
		ipLong[2] = Long.parseLong(ips[2].trim());
		ipLong[3] = Long.parseLong(ips[3].trim());
		return (ipLong[0] << 24) + (ipLong[1] << 16) + (ipLong[2] << 8)
				+ ipLong[3];
	}
	/**
	 * long 转换成点分十进制ip地址
	 * @param value
	 * @return
	 */
	public static String longToIp(long value) {
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf(value >>> 24));// 直接右移24位
		sb.append(".");
		sb.append(String.valueOf((value & 0x00ffffff) >>> 16)); // 将高8位置0，然后右移16位
		sb.append(".");
		sb.append(String.valueOf((value & 0x0000ffff) >>> 8));  // 将高16位置0，然后右移8位
		sb.append(".");
		sb.append(String.valueOf(value & 0x000000ff));		    // 将高24位置0
		return sb.toString();
	}
	/**
	 * 将long转换为bytes
	 * @param n
	 * @return
	 */
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
	/**
	 * 将bytes 转换为long
	 * @param array
	 * @param offset
	 * @return
	 */
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
		// long i = ipToLong("192.168.0.248");
		// System.out.println(i);
		// System.out.println(longToIp(i));
		// Vector<String> v1 = ScanUtils.tokenize(src, tok, trim,
		// null_subst)ze("1.3.6.1.2.1.4.22.1.2.1.192.168.0.118".substring(21),
		// ".", true,"asdfasdf");
	}

	/**
	 * 判断 scaleB 是否在　scaleA　的范围内
	 * @param scaleA
	 * @param scaleB
	 * @return　yes: true no:false;
	 */
	public static boolean isScaleBInA(Pair<String, String> scaleA,
			Pair<String, String> scaleB) {
		long numMin0 = ipToLong(scaleA.getFirst());
		long numMax0 = ipToLong(scaleA.getSecond());
		long numMin1 = ipToLong(scaleB.getFirst());
		long numMax1 = ipToLong(scaleB.getSecond());
		return (numMin0 <= numMin1 && numMax1 <= numMax0);
	}

	// trim指示是否保留空串，默认为保留。
	public static Vector<String> tokenize(String src, String tok, boolean trim,
			String null_subst) {
		Vector<String> v = new Vector<String>();
		if (src.isEmpty() || tok.isEmpty()) {
			return v;
		}
		int pre_index = 0, index = 0, len = 0;
		if ((index = src.indexOf(tok, pre_index)) != npos) {
			String[] ss = src.split(tok);
			for (int i = 0; i < ss.length; i++) {
				String temp = ss[i];
				if (temp.isEmpty())
					v.add(null_subst);
				else
					v.add(ss[i]);
			}
		}
		return v;
	}
	/**
	 * 获取子网
	 * @param ip 种子IP
	 * @param mask　子网掩码
	 * @return
	 */
	public static String getSubnetByIPMask(String ip, String mask) {
		long masknum = ScanUtils.ipToLong(mask);
		long subnet = ScanUtils.ipToLong(ip) & masknum;
		int iLen = getMaskBitLen(mask);
		return ScanUtils.longToIp(subnet) + "/" + iLen;
	}

	// 获取mask的非0位数
	public static int getMaskBitLen(String msk) {
		if (tbl.isEmpty()) {
			tbl.put("255", 8);
			tbl.put("254", 7);
			tbl.put("252", 6);
			tbl.put("248", 5);
			tbl.put("240", 4);
			tbl.put("224", 3);
			tbl.put("192", 2);
			tbl.put("128", 1);
			tbl.put("0", 0);
		}

		int len = 0;
		String[] dest = msk.split("\\.");
		if (dest.length != 4) {
			throw new RuntimeException("掩码地址格式不正确！");
		}
		return tbl.get(dest[0]) + tbl.get(dest[1]) + tbl.get(dest[2])
				+ tbl.get(dest[3]);
	}
	
	

}
