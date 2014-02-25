package core.businessobject.pv.search;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;


public class ServerConfig implements EntryPoint {

	public ServerConfig() {
	}

	@Override
	public int createUI() {
		Display display = PlatformUI.createDisplay();
		JavaScriptExecutor executor = RWT.getClient().getService( JavaScriptExecutor.class );
//		Branding.init();
		String url = RWT.getRequest().getRequestURL().toString();
		if (!(url.startsWith("http://localhost")||url.startsWith("http://127.0.0.1"))){
			if(executor!=null){
				executor.execute("alert('此页面不能从远程访问！');");
			}
			return 0;
		}
		ConnectionManager cm = new ConnectionManager(display.getActiveShell());
		cm.open();
		
		//System.out.println(url);
		if(executor!=null){
			executor.execute("self.location='" + url + "';");
		}
		return 0;
	}

}
