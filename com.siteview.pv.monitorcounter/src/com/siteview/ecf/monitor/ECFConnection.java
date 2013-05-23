package com.siteview.ecf.monitor;

import org.osgi.framework.BundleContext;

import com.siteview.pv.monitor.api.CssMonitorInterface;

public class ECFConnection {
	public static ECfClient ecfclient=new ECfClient();
	public static CssMonitorInterface ecfapi=null;
	
	public static void getApi(BundleContext context){
		String proxyip="";
		String proxyport="";
		try {
			ecfapi=ecfclient.getAPIInterfaces("r-osgi://"+proxyip+":"+proxyport+"/mygroup",context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
