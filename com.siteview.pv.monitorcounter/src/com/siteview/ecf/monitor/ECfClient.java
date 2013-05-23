package com.siteview.ecf.monitor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteCallListener;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteCallEvent;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.siteview.pv.monitor.api.CssMonitorInterface;

public class ECfClient {
	private IContainer container;
	private ServiceTracker containerManagerServiceTracker;
	public  static BundleContext context;
	public static CssMonitorInterface ecfapi;
	public static String address="";
	public static String username="";
	public static String password="";
	public static ECfClient ecf;
	public static boolean log=false;
	public static String proxyip;
	public static String proxyport;
	public CssMonitorInterface getAPIInterfaces(String s,BundleContext s1) throws Exception {
		// 1. Create R-OSGi Container
		IContainerManager containerManager = getContainerManagerService(s1);
		container = containerManager.getContainerFactory().createContainer("ecf.r_osgi.peer");
		// 2. Get remote service container adapter
		IRemoteServiceContainerAdapter containerAdapter = (IRemoteServiceContainerAdapter) container.getAdapter(IRemoteServiceContainerAdapter.class);
		// 3. Lookup IRemoteServiceReference
		IRemoteServiceReference[] helloReferences = containerAdapter.getRemoteServiceReferences
				(IDFactory.getDefault().createID(container.getConnectNamespace(), s),CssMonitorInterface.class.getName(), null);
		Assert.isNotNull(helloReferences);
		Assert.isTrue(helloReferences.length > 0);
		// 4. Get remote service for reference
		IRemoteService remoteService = containerAdapter.getRemoteService(helloReferences[0]);
		// 5. Get the proxy
		CssMonitorInterface proxy = (CssMonitorInterface) remoteService.getProxy();
		// 6. Finally...call the proxy
//		log=proxy.getLog(address, username, password);
//		if(!log){
//			System.out.println("用户名或者密码错误~~~~~~~~~~~~~~~~~~~");
//			return null;
//		}
		return proxy;
	}

	
//	void callViaListener(IRemoteService remoteService) {
//		remoteService.callAsync(createRemoteCall(), createRemoteCallListener());
//	}

	IRemoteCall createRemoteCall() {
		return new IRemoteCall() {
			public String getMethod() {
				return "helloWorld";
			}
			public Object[] getParameters() {
				return new Object[] { "Asynch RemoteService Consumer"};
			}
			public long getTimeout() {
				return 0;
			}
		};
	}

	IRemoteCallListener createRemoteCallListener() {
		return new IRemoteCallListener() {
			public void handleEvent(IRemoteCallEvent event) {
				if (event instanceof IRemoteCallCompleteEvent) {
					IRemoteCallCompleteEvent cce = (IRemoteCallCompleteEvent) event;
					if (!cce.hadException())
						System.out.println("Remote call completed successfully!");
					else
						System.out.println("Remote call completed with exception: "+ cce.getException());
				}
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop() throws Exception {
		if (container != null) {
			container.disconnect();
			container = null;
		}
		if (containerManagerServiceTracker != null) {
			containerManagerServiceTracker.close();
			containerManagerServiceTracker = null;
		}
	}

	private IContainerManager getContainerManagerService(BundleContext context) {
		if (containerManagerServiceTracker == null) {
			containerManagerServiceTracker = new ServiceTracker(context,IContainerManager.class.getName(), null);
			containerManagerServiceTracker.open();
		}
		return (IContainerManager) containerManagerServiceTracker.getService();
	}
	
	public static void getConn(){
		try {
			ecfapi=ecf.getAPIInterfaces("r-osgi://"+ECfClient.proxyip+":"+ECfClient.proxyport+"/mygroup",ECfClient.context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
