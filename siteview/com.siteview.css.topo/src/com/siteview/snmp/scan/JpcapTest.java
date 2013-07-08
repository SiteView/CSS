package com.siteview.snmp.scan;



public class JpcapTest  {

//	static int j = 0;
//	static long p = 0, q = 0;
//	IPPacket ip;
//	String s, s1;
//	Long s2;
//	static long time11, time12;
//	String time;
//	static long num = 0; // 加一个静态产量，表示抓的包的序号，不然连自己抓的顺序都不知道
////	public static Connection conn;
//
//	@Override
//	public void handlePacket(Packet packet) {
//		if (packet instanceof IPPacket)// 判断是不是是ip包
//		{
//			System.out.println(j + "ok");
//			ip = (IPPacket) packet;
//			if (j < 10000) {
//				/*
//				 * try { Class.forName("sun.jdbc.odbc.JdbcOdbcDriver"); conn =
//				 * DriverManager.getConnection("jdbc:odbc:Jpcap");
//				 * //JOptionPane.showMessageDialog(null, "连接数据库!");
//				 * 
//				 * } catch (Exception e) { e.printStackTrace(); //
//				 * JOptionPane.showMessageDialog(null, "连接数据库错误!");
//				 * JOptionPane.showConfirmDialog(null, "连接数据库错误",
//				 * "抓包",JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
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
//							"rw");// 动态文件
//					rf.seek(rf.length());// 文件尾
//					rf.writeBytes(num + "\t" + ip.src_ip + "\t" + ip.dst_ip
//							+ "\t" + ip.protocol + "\t" + ip.length + "\t"
//							+ ip.version + "\t" + ip.ident + "\t" + ip.rsv_frag
//							+ "\t" + ip.offset + "\t" + ip.hop_limit + "\t"
//							+ ip.rsv_tos + "\r\n");// 写入
//					rf.close();// 关闭文件
//					num++;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				j++;
//			} else {
//				time12 = System.currentTimeMillis();// 当前系统时间
//				System.out.println("" + time12 + "-" + time11 + "="
//						+ (time12 - time11) + "\ncapture count:" + j);
//				System.exit(0);
//			}
//		}
//	}

}
