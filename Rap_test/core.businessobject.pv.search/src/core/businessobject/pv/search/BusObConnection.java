package core.businessobject.pv.search;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import Siteview.IAuthenticationBundle;
import Siteview.ResourceUtils;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import Siteview.Api.SiteviewApi;
import Siteview.thread.IPrincipal;
import Siteview.thread.Thread;

public class BusObConnection {
	
	private static ISiteviewApi siteviewapi;
	private static IPrincipal pal;
	
	public static ISiteviewApi getBusObApi() throws SiteviewException{
		if(siteviewapi == null||!siteviewapi.get_Connected()){
			String[] logininfo=getlogininfo();
			ISiteviewApi api = SiteviewApi.get_CreateInstance();
			api.Connect(logininfo[0]);
			IAuthenticationBundle authentication = api.GetAuthenticationBundle();
			authentication.set_UserType("User");
			authentication.set_AuthenticationId(logininfo[1]);
			authentication.set_Password(logininfo[2]);
			if(api.Login(authentication)){
				siteviewapi = api;
				pal = Thread.get_CurrentPrincipal();
			}
			else{
				throw new SiteviewException("µÇÂ¼Ê§°Ü!");
			}
		}
		return siteviewapi;
	}
	
	public static IPrincipal getPrincipal() throws SiteviewException{
		if(pal==null){
			getBusObApi();
		}
		return pal;
	}
	
	public static String[] getlogininfo() throws SiteviewException{
		try {
			SecurityPWD sp = new SecurityPWD();
			String[] logininfo = new String[3];
			SAXReader saxreader = new SAXReader();
			String path = ResourceUtils.getLibDir() + java.io.File.separator +"Connections.ciq";
			Document doc = saxreader.read(new File(path));
			Element element = doc.getRootElement().element("DbConnectionDef");
			logininfo[0] = element.attributeValue("Name").substring(element.attributeValue("Name").indexOf("}")+1).trim();
			Element logininfoElement = element.element("logininfo");
			logininfo[1] = logininfoElement.element("loginname").getText().trim();
			logininfo[2] = sp.decrypt2String(logininfoElement.element("loginpassword").getText().trim()).trim();
			return logininfo;
		} catch (Exception e) {
			throw new SiteviewException(e.getMessage(),e);
		}
	}
}
