package core.businessobject.pv.search;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.widgets.JSExecutor;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;


public class ServerConfig implements IEntryPoint {

	public ServerConfig() {
	}

	@Override
	public int createUI() {
		Display display = PlatformUI.createDisplay();
//		Branding.init();
		String url = RWT.getRequest().getRequestURL().toString();
		if (!(url.startsWith("http://localhost")||url.startsWith("http://127.0.0.1"))){
			JSExecutor.executeJS("alert('此页面不能从远程访问！');");
			return 0;
		}
		ConnectionManager cm = new ConnectionManager(display.getActiveShell());
		cm.open();
		
		//System.out.println(url);
		JSExecutor.executeJS("self.location='" + url + "';");
		return 0;
	}

}
