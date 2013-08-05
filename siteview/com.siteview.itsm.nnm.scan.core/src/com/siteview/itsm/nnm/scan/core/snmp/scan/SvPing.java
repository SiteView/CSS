package com.siteview.itsm.nnm.scan.core.snmp.scan;

import java.net.Socket;

import com.siteview.itsm.nnm.scan.core.snmp.model.SockaddrIn;

public class SvPing {
//	public static final int AF_INET	= 2;
//}
//	public SvPing()
//	{
////	    WSAData wsaData;
////	    if (WSAStartup(MAKEWORD(2, 1), &wsaData) != 0)
////	    {
////	        //winsocket�汾����
////	        qDebug() << "ping failed for version";
////	        //return SYSERROR;
////	    }
//	}
//
////	SvPing::~SvPing()
////	{
//////	    WSACleanup();
////	}
//
//	public short ip_checksum(short buffer, int size)
//	{
//	     long cksum = 0;
//
//	    while (size > 1) 
//		{
//	        cksum += buffer++;
//	        size -= sizeof(USHORT);
//	    }
//
//	    if (size) 
//		{
//			cksum += (short)buffer;
//	    }
//
//	    cksum = (cksum >> 16) + (cksum & 0xffff);
//	    cksum += (cksum >> 16);
//
//	    // ����У���
//	    return (short)(~cksum);
//	}
//
//	public int setup_for_ping(char host, int ttl, Socket sd, SockaddrIn dest, int iTimeOut)
//	{
//	    // ����socket
//	    sd = WSASocket(AF_INET, SOCK_RAW, IPPROTO_ICMP, 0, 0, WSA_FLAG_OVERLAPPED);
//
//	    if (sd == INVALID_SOCKET)
//	    {
//	        int i = WSAGetLastError();
//	        qDebug() << "create socked failed : " << i;
//	        if (WSA_NOT_ENOUGH_MEMORY == i)
//	        {
//
//	        }
//
//	        return SYSERROR;
//	    }
//
//	    if (setsockopt( sd,IPPROTO_IP,IP_TTL, (const char*)&ttl, sizeof(ttl)) == SOCKET_ERROR)
//	    {
//	        qDebug() << "set sock opt failed";
//	        return SYSERROR;
//	    }
//
//	    //���ó�ʱ����
//	    int rtn = setsockopt(sd, SOL_SOCKET, SO_RCVTIMEO, (char*)&iTimeOut, sizeof(iTimeOut));
//	    if( rtn != 0)
//	    {
//	        qDebug() << "set sock opt timeout failed : " << rtn;
//	        return SYSERROR;
//	    }
//	    // ��ʼ��������ַ
//	    memset(&dest, 0, sizeof(dest));
//
//	    // ת��IP��ַ
//	    unsigned int addr = inet_addr(host);
//	    if (addr != INADDR_NONE)
//	    {
//	        // ����������ַ
//	        dest.sin_addr.s_addr = addr;
//	        dest.sin_family = AF_INET;
//	    }
//	    else
//	    {
//	        // ��ȡ������Ϣ
//	        hostent* hp = gethostbyname(host);
//	        if (hp != 0)
//	        {
//	            // ���������ַ
//	            memcpy(&(dest.sin_addr), hp->h_addr, hp->h_length);
//	            dest.sin_family = hp->h_addrtype;
//	        }
//	        else
//	        {
//	            // ����
//	            qDebug() << "get host by name failed";
//	            return SYSERROR;
//	        }
//	    }
//	    return 0;
//	}
//
//	void init_ping_packet(ICMPHeader icmp_hdr, int packet_size, int seq_no)
//	{
//	    // ����ICMP��ͷ
//	    icmp_hdr->type = ICMP_ECHO_REQUEST;
//	    icmp_hdr->code = 0;
//	    icmp_hdr->checksum = 0;
//	    icmp_hdr->id = (USHORT)GetCurrentProcessId();
//	    icmp_hdr->seq = seq_no;
//	    icmp_hdr->timestamp = GetTickCount();
//
//	    //���DATA���ݲ�
//	    const unsigned long int deadmeat = 0xDEADBEEF;
//	    char* datapart = (char*)icmp_hdr + sizeof(ICMPHeader);
//	    int bytes_left = packet_size - sizeof(ICMPHeader);
//	    while (bytes_left > 0)
//	    {
//	        memcpy(datapart, &deadmeat, min(int(sizeof(deadmeat)),bytes_left));
//	        bytes_left -= sizeof(deadmeat);
//	        datapart += sizeof(deadmeat);
//	    }
//
//	    // ����У���
//	    icmp_hdr->checksum = ip_checksum((USHORT*)icmp_hdr, packet_size);
//	}
//
//	//��������
//	int SvPing::send_ping(SOCKET sd, const sockaddr_in& dest, ICMPHeader* send_buf,int packet_size)
//	{
//	    if(sendto(sd, (char*)send_buf, packet_size, 0, (sockaddr*)&dest, sizeof(dest)) == SOCKET_ERROR)
//	    {
//	        return SENDERR;
//	    }
//	    return 0;
//	}
//
//	int SvPing::recv_ping(SOCKET sd, sockaddr_in& source, IPHeader* recv_buf,int packet_size)
//	{
//	    // ����icmp��
//	    int fromlen = sizeof(source);
//	    int bread = recvfrom(sd, (char*)recv_buf, packet_size + sizeof(IPHeader), 0, (sockaddr*)&source, &fromlen);
//	    //���ճ�����
//	    if (bread == SOCKET_ERROR)
//	    {
//	        int  iErr = WSAGetLastError();
//	        if (iErr == WSAEMSGSIZE)
//	        {
//	            return SYSERROR;
//	        }
//	        else  if( iErr == 10060 )
//	        {
//	            return OVERTIME;
//	        }
//	        return RECVERR;
//	    }
//		return bread;
//	}
//
//	int SvPing::decode_reply(IPHeader* reply, int bytes, sockaddr_in* from)
//	{
//	    // ����IP��ͷ, �ҵ�ICMP�İ�ͷ
//	    unsigned short header_len = reply->h_len * 4;
//	    ICMPHeader* icmphdr = (ICMPHeader*)((char*)reply + header_len);
//
//	    // ���ĳ��ȺϷ�, header_len + ICMP_MINΪ��СICMP���ĳ���
//	    if (bytes < header_len + ICMP_MIN)
//	    {//���յ������ݰ����ȹ�С
//	        return RECVERR;
//	    }
//	    else if (icmphdr->type != ICMP_ECHO_REPLY)
//	    {
//	        if (icmphdr->type != ICMP_TTL_EXPIRE )
//	        {//ttl��Ϊ�� ��ʾ �������ɴ��Ƿ���ICMP������
//	            return NETERROR;
//	        }
//	    }	
//		else if (from->sin_addr.S_un.S_addr != destMachine.sin_addr.S_un.S_addr) 
//		{
//			return -10;
//		}
//		return 1;
//	}
//
//	int SvPing::allocate_buffers(ICMPHeader*& send_buf, IPHeader*& recv_buf,int packet_size)
//	{
//	    // ���ͻ����� ��ʼ��
//	    send_buf = (ICMPHeader*)new char[packet_size];
//	    if (send_buf == 0)
//	    {
//	        return SYSERROR;
//	    }
//	    // ���ջ����� ��ʼ��
//	    recv_buf = (IPHeader*)new char[MAX_PING_PACKET_SIZE];
//	    if (recv_buf == 0)
//	    {
//	        return SYSERROR;
//	    }
//	    return 0;
//	}
//
//	int SvPing::Ping(const char * cHost ,unsigned int iTimeOut,  int iReTry)
//	{
//	    //���ڷ��ͺͽ��ܵ�ICMP��ͷ��
//	    int seq_no = 0;
//	    ICMPHeader* send_buf = 0;
//	    IPHeader* recv_buf = 0;
//	    int rtn;
//	    int iCount = 0;
//	    int packet_size = DEFAULT_PACKET_SIZE;
//	    int ttl = DEFAULT_TTL;
//
//	    packet_size = max(sizeof(ICMPHeader), min((unsigned int)MAX_PING_DATA_SIZE, (unsigned int)packet_size));
//	    qDebug() << "ping start";
//	    // ���� Winsocket
////	    WSAData wsaData;
////	    if (WSAStartup(MAKEWORD(2, 1), &wsaData) != 0)
////	    {
////	        //winsocket�汾����
////	        qDebug() << "ping failed for version";
////	        return SYSERROR;
////	    }
//
//	    SOCKET sd; // RAW Socket���
//	    sockaddr_in dest, source;
//
//	    // ��������(����sd, ����ttl, ����dest��ֵ)
//	    rtn = setup_for_ping( cHost, ttl, sd, dest, iTimeOut);
//	    if ( rtn < 0)
//	    {
//	        //WSACleanup();		// ����winsock
//
//	        return rtn;
////	        qDebug() << "ping set failed";
////	        goto cleanup; //�ͷ���Դ���˳�
//	    }
//
//		destMachine = dest;
//	    // Ϊsend_buf��recv_buf�����ڴ�
////	    rtn = allocate_buffers(send_buf, recv_buf, packet_size);
////	    if ( rtn < 0)
////	    {
////	        qDebug() << "relocate failed";
////	        goto cleanup;
////	    }
//
//	        send_buf = (ICMPHeader*)new char[packet_size];
//	        if (send_buf == 0)
//	        {
//	            return SYSERROR;
//	        }
//	        // ���ջ����� ��ʼ��
//	        recv_buf = (IPHeader*)new char[MAX_PING_PACKET_SIZE];
//	        if (recv_buf == 0)
//	        {
//	            return SYSERROR;
//	        }
//
//	    // ��ʼ��IMCP���ݰ�(type=8,code=0)
//	    init_ping_packet(send_buf, packet_size, seq_no);
//
//	    // ����ICMP���ݰ�
//	    rtn = send_ping(sd, dest, send_buf, packet_size);
//	    if ( rtn >= 0)
//	    {//��ѭ������֤�ӵ��������ֳɵ�icmp��ʱ�ܺ��Բ���������icmp����
//	        while(1)
//	        {
//	            // ���ܻ�Ӧ��
//				Sleep(10);
//	            rtn = recv_ping(sd, source, recv_buf, MAX_PING_PACKET_SIZE);
//	            if ( rtn <= 0)
//	            {
//	                if( iCount > iReTry) goto cleanup;
//	                iCount++;
//	                continue;
//	            }
//	            //���ճɹ������غ�ʱ�����ߴ������
//	            rtn = decode_reply(recv_buf, packet_size, &source);
//				if( rtn == -10 ) // ���Ǳ����̷�����ping��
//					continue;
//	            if( rtn < 0 )
//				{
//					if( iCount > iReTry) // ������ݳ���
//						goto cleanup;
//					else
//						iCount++;
//				}			
//	            if( GetTickCount() - send_buf->timestamp >= iTimeOut )
//				{
//					rtn = OVERTIME;
//					goto cleanup;
//				}
//				goto cleanup;
//	        }
//	   }
//
//	cleanup:
//	    delete[]send_buf;	//�ͷŷ�����ڴ�
//	    delete[]recv_buf;
//	    //if (0 != WSACleanup())		// ����winsock
//	    //{
//	    //    qDebug() << "cleanup failed";
//	    //}
//
//	    return rtn;
//	}
//
//
//	char *SvPing::GetError( int iErr)
//	{
//	    switch( iErr )
//	    {
//	        case    SYSERROR:
//	                            return "SYSERROR";
//	                            break;
//	        case    NETERROR:
//	                            return  "NETERROR";
//	                            break;
//	        case    OVERTIME:
//	                            return  "OVERTIME";
//	                            break;
//	        case    SENDERR:
//	                            return  "Send ICMP Packet Error";
//	                            break;
//	        case    RECVERR:
//	                            return  "receive ICMP packet error";
//	                            break;
//	        default:
//	                            return  "OTHERERR";
//	                            break;
//	    }
}
