package com.siteview.snmp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;

import com.siteview.snmp.model.Pair;

public class ScanUtils {

	public static final int npos = -1;

	private static Map<String, Integer> tbl = new HashMap<String, Integer>();
	
	/**
	 * ������������ ��ȡIP��ַ��Χ
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
	 * ����snmpPDU
	 * @param ip 		IP��ַ
	 * @param port 		�˿ں�
	 * @param community ��ͬ��
	 * @param timeout 	��ʱʱ��
	 * @param retry   	����ʱ��
	 * @param version 	snmp�汾
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
	 * IP��ַת��������long
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
	 * long ת���ɵ��ʮ����ip��ַ
	 * @param value
	 * @return
	 */
	public static String longToIp(long value) {
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf(value >>> 24));// ֱ������24λ
		sb.append(".");
		sb.append(String.valueOf((value & 0x00ffffff) >>> 16));// ����8λ��0��Ȼ������16λ
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
		// long i = ipToLong("192.168.0.248");
		// System.out.println(i);
		// System.out.println(longToIp(i));
		// Vector<String> v1 = ScanUtils.tokenize(src, tok, trim,
		// null_subst)ze("1.3.6.1.2.1.4.22.1.2.1.192.168.0.118".substring(21),
		// ".", true,"asdfasdf");
	}

	/**
	 * �ж� scaleB �Ƿ��ڡ�scaleA���ķ�Χ��
	 * @param scaleA
	 * @param scaleB
	 * @return��yes: true no:false;
	 */
	public static boolean isScaleBInA(Pair<String, String> scaleA,
			Pair<String, String> scaleB) {
		long numMin0 = ipToLong(scaleA.getFirst());
		long numMax0 = ipToLong(scaleA.getSecond());
		long numMin1 = ipToLong(scaleB.getFirst());
		long numMax1 = ipToLong(scaleB.getSecond());
		return (numMin0 <= numMin1 && numMax1 <= numMax0);
	}

	// trimָʾ�Ƿ����մ���Ĭ��Ϊ������
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

	public static String getSubnetByIPMask(String ip, String mask) {
		long masknum = ScanUtils.ipToLong(mask);// ntohl(inet_addr(mask));
		long subnet = ScanUtils.ipToLong(ip) & masknum;
		int iLen = getMaskBitLen(mask);
		// struct in_addr ipsubnet;
		// ipsubnet.S_un.S_addr = htonl(subnet);
		return ScanUtils.longToIp(subnet) + "/" + iLen;
	}

	// ��ȡmask�ķ�0λ��
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
			// throw std::runtime_error("Bad Mask");
		}
		return tbl.get(dest[0]) + tbl.get(dest[1]) + tbl.get(dest[2])
				+ tbl.get(dest[3]);
	}
	
	

}