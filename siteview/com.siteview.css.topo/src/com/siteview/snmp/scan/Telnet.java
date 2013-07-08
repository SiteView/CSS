package com.siteview.snmp.scan;

public class Telnet extends TelnetReceivedHandler {

	@Override
	public void callback(byte[] buff, int length) {
		System.out.println(new String(buff,0,length));
	}

	public static void main(String[] args)  
    {  
        try  
        {  
            TelnetSenderHandler telnetSendHandler = new TelnetSenderHandler("test", "192.168.0.251", 23);  
            telnetSendHandler.initiation();  
            telnetSendHandler.getS12TelnetClient().setHandler(new Telnet());  
            if(telnetSendHandler.getS12TelnetClient().start())  
            {  
                telnetSendHandler.send("rootroot\r\n".getBytes());  
                Thread.sleep(1000);  
                telnetSendHandler.send("en\r\n".getBytes());  
                Thread.sleep(1000);  
                telnetSendHandler.send("rootroot\r\n".getBytes());
                telnetSendHandler.send("show arp\r\n".getBytes());
            }  
        }  
        catch(Exception e)  
        {  
            e.printStackTrace();  
        }  
    }  
}
