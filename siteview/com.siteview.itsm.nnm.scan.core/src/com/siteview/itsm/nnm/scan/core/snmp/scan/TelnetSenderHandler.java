package com.siteview.itsm.nnm.scan.core.snmp.scan;

public class TelnetSenderHandler {

	private String serverName = "";
	private String serverIpAddress  = "";
	private int serverPort  = 23;
	private TelnetReader telnetclient;
	public TelnetSenderHandler(String serverName, String serverIPAdress, int serverPort)  
    {  
        this.serverName     = serverName;  
        this.serverIpAddress = serverIPAdress;  
        this.serverPort     = serverPort;  
    }  
      
    public void initiation()  
    {  
        telnetclient    = new TelnetReader(this.serverName, this.serverIpAddress,"rootroot", this.serverPort);  
    }  
      
    public TelnetReader getS12TelnetClient()  
    {  
         return this.telnetclient;  
    }  
      
    public boolean send(byte[] byteArray)  
    {  
        return this.telnetclient.send(byteArray);  
    }  
}
