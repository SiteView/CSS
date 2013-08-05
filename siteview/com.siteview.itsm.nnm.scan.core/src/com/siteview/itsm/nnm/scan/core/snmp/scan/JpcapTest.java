package com.siteview.itsm.nnm.scan.core.snmp.scan;



public class JpcapTest  {

//	static int j = 0;
//	static long p = 0, q = 0;
//	IPPacket ip;
//	String s, s1;
//	Long s2;
//	static long time11, time12;
//	String time;
//	static long num = 0; // ��һ����̬��������ʾץ�İ�����ţ���Ȼ���Լ�ץ��˳�򶼲�֪��
////	public static Connection conn;
//
//	@Override
//	public void handlePacket(Packet packet) {
//		if (packet instanceof IPPacket)// �ж��ǲ�����ip��
//		{
//			System.out.println(j + "ok");
//			ip = (IPPacket) packet;
//			if (j < 10000) {
//				/*
//				 * try { Class.forName("sun.jdbc.odbc.JdbcOdbcDriver"); conn =
//				 * DriverManager.getConnection("jdbc:odbc:Jpcap");
//				 * //JOptionPane.showMessageDialog(null, "�������ݿ�!");
//				 * 
//				 * } catch (Exception e) { e.printStackTrace(); //
//				 * JOptionPane.showMessageDialog(null, "�������ݿ����!");
//				 * JOptionPane.showConfirmDialog(null, "�������ݿ����",
//				 * "ץ��",JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
//				 * } Date jhsjDate = new Date();
//				 * //In_Time.setText(jhsjDate.toLocaleString()); time =
//				 * jhsjDate.toLocaleString(); insert(
//				 * "INSERT [JpcapTip] ([src_ip] , [dst_ip] , [protocol] , [length] , [version] , [ident] , [rsv_frag] , [offset] , [hop_limit] , [rsv_tos] , [time] ) values('"
//				 * + ip.src_ip + "','" + ip.dst_ip + "','" + ip.protocol + "','"
//				 * + ip.length + "','" + ip.version + "','" + ip.ident + "','" +
//				 * ip.rsv_frag + "','" + ip.offset + "','" + ip.hop_limit +
//				 * "','" + ip.rsv_tos + "','" + time + "')");
//				 */
//
//				try {
//					RandomAccessFile rf = new RandomAccessFile("packet.txt",
//							"rw");// ��̬�ļ�
//					rf.seek(rf.length());// �ļ�β
//					rf.writeBytes(num + "\t" + ip.src_ip + "\t" + ip.dst_ip
//							+ "\t" + ip.protocol + "\t" + ip.length + "\t"
//							+ ip.version + "\t" + ip.ident + "\t" + ip.rsv_frag
//							+ "\t" + ip.offset + "\t" + ip.hop_limit + "\t"
//							+ ip.rsv_tos + "\r\n");// д��
//					rf.close();// �ر��ļ�
//					num++;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				j++;
//			} else {
//				time12 = System.currentTimeMillis();// ��ǰϵͳʱ��
//				System.out.println("" + time12 + "-" + time11 + "="
//						+ (time12 - time11) + "\ncapture count:" + j);
//				System.exit(0);
//			}
//		}
//	}

}
