package com.siteview.pv.monitor.bean;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Security.Principal.IPrincipal;
import system.Xml.XmlElement;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;

/**
 * 
 * 
 * @author Administrator
 * 
 */
public class FileTools {
	public static IPrincipal mainIPrincipal = null;

	public FileTools() {
	}

	public static ISiteviewApi getApi() {
		system.Threading.Thread.set_CurrentPrincipal(mainIPrincipal);
		return ConnectionBroker.get_SiteviewApi();
	}

	public static ICollection getBussCollection(String s) {
		system.Threading.Thread.set_CurrentPrincipal(mainIPrincipal);
		System.out.println();
		System.out
				.println("======================zlh================================="
						+ ConnectionBroker.get_SiteviewApi().get_LoggedIn());
		System.out
				.println("======================zlh================================="
						+ ConnectionBroker.get_SiteviewApi().get_BusObService());
		System.out
				.println("======================zlh================================="
						+ ConnectionBroker.get_SiteviewApi());
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(s, QueryInfoToGet.All);
		XmlElement xml = null;
		xml = query.get_CriteriaBuilder().FieldAndValueExpression("RecId",
				Operators.NotNull);
		query.set_BusObSearchCriteria(xml);
		ICollection iCollenction = ConnectionBroker.get_SiteviewApi()
				.get_BusObService().get_SimpleQueryResolver()
				.ResolveQueryToBusObList(query);
		return iCollenction;
	}

	public static ICollection getBussCollection(String key, String value,
			String s) {
		system.Threading.Thread.set_CurrentPrincipal(mainIPrincipal);
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(s, QueryInfoToGet.All);
		XmlElement xml = null;
		xml = query.get_CriteriaBuilder().FieldAndValueExpression(key,
				Operators.Equals, value);
		query.set_BusObSearchCriteria(xml);
		ICollection iCollenction = ConnectionBroker.get_SiteviewApi()
				.get_BusObService().get_SimpleQueryResolver()
				.ResolveQueryToBusObList(query);
		return iCollenction;
	}

	// ip
	public static String getHostName(String ip) {
		InetAddress a;
		String s = "";
		try {

			if (ip.startsWith("\\")) {
				a = InetAddress.getByName(ip.substring(ip.indexOf("\\") + 2));
				s = a.getHostAddress();
			} else {
				a = InetAddress.getByName(ip);
				s = a.getHostName();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static ICollection getBussCollection(Map<String, String> map,
			String s) {
		system.Threading.Thread.set_CurrentPrincipal(mainIPrincipal);
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(s, QueryInfoToGet.All);
		XmlElement[] xmls = new XmlElement[map.size()];
		XmlElement xml;
		Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			key = key.substring(0, key.indexOf("="));
			xml = query.get_CriteriaBuilder().FieldAndValueExpression(key,
					Operators.Equals, map.get(key).toString());
			xmls[i++] = xml;
		}
		query.set_BusObSearchCriteria(query.get_CriteriaBuilder()
				.AndExpressions(xmls));
		ICollection iCollenction = ConnectionBroker.get_SiteviewApi()
				.get_BusObService().get_SimpleQueryResolver()
				.ResolveQueryToBusObList(query);
		return iCollenction;
	}

	public static BusinessObject CreateBo(String key, String s, String s1) {
		system.Threading.Thread.set_CurrentPrincipal(mainIPrincipal);
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(s1, QueryInfoToGet.All);
		XmlElement xml;
		xml = query.get_CriteriaBuilder().FieldAndValueExpression(key,
				Operators.Equals, s);
		query.set_BusObSearchCriteria(xml);
		ICollection iCollenction = ConnectionBroker.get_SiteviewApi()
				.get_BusObService().get_SimpleQueryResolver()
				.ResolveQueryToBusObList(query);
		BusinessObject bo = null;
		IEnumerator interfaceTableIEnum = iCollenction.GetEnumerator();
		if (interfaceTableIEnum.MoveNext()) {
			bo = (BusinessObject) interfaceTableIEnum.get_Current();
		}
		return bo;
	}

	public static ICollection getLog(Map<String, Object> map, String s) {
		system.Threading.Thread.set_CurrentPrincipal(mainIPrincipal);
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(s, QueryInfoToGet.All);
		XmlElement[] xmls = new XmlElement[map.size() - 1];
		XmlElement xml;
		int i = 0;
		if (map.get("startTime") != null) {
			xml = query.get_CriteriaBuilder().QueryListExpression(
					"CreatedDateTime", Operators.Between.name());
			query.get_CriteriaBuilder().AddLiteralValue(xml,
					map.get("startTime").toString());
			query.get_CriteriaBuilder().AddLiteralValue(xml,
					map.get("endTime").toString());
			xmls[i++] = xml;
			map.remove("startTime");
			map.remove("endTime");
		}
		Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			key = key.substring(0, key.indexOf("="));
			xml = query.get_CriteriaBuilder().FieldAndValueExpression(key,
					Operators.Equals, map.get(key).toString());
			xmls[i++] = xml;
		}
		query.AddOrderByDesc("CreatedDateTime");
		query.set_BusObSearchCriteria(query.get_CriteriaBuilder()
				.AndExpressions(xmls));
		ICollection iCollenction = ConnectionBroker.get_SiteviewApi()
				.get_BusObService().get_SimpleQueryResolver()
				.ResolveQueryToBusObList(query);
		return iCollenction;
	}

	public static ICollection getLog2(Map<String, Object> map, String s) {
		system.Threading.Thread.set_CurrentPrincipal(mainIPrincipal);
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(s, QueryInfoToGet.All);
		XmlElement[] xmls = new XmlElement[map.size() - 1];
		XmlElement xml;
		int i = 0;
		if (map.get("startTime") != null) {
			xml = query.get_CriteriaBuilder().QueryListExpression(
					"CreatedDateTime", Operators.Between.name());
			query.get_CriteriaBuilder().AddLiteralValue(xml,
					map.get("startTime").toString());
			query.get_CriteriaBuilder().AddLiteralValue(xml,
					map.get("endTime").toString());
			xmls[i++] = xml;
			map.remove("startTime");
			map.remove("endTime");
		}
		Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			key = key.substring(0, key.indexOf("="));
			xml = query.get_CriteriaBuilder().FieldAndValueExpression(key,
					Operators.Null, (String) map.get(key));
			xmls[i++] = xml;
		}
		query.AddOrderByDesc("CreatedDateTime");
		query.set_BusObSearchCriteria(query.get_CriteriaBuilder()
				.AndExpressions(xmls));
		ICollection iCollenction = ConnectionBroker.get_SiteviewApi()
				.get_BusObService().get_SimpleQueryResolver()
				.ResolveQueryToBusObList(query);
		return iCollenction;
	}

	public static IPrincipal getIPrincipal() {
		return mainIPrincipal;
	}

//	public static String getPath() {
//		String path = null;
//		try {
//			path = FileLocator.toFileURL(
//					Platform.getBundle(Activator.PLUGIN_ID).getEntry("")).getPath();
//			path = path.substring(path.indexOf("/") + 1, path.length());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return path;
//	}
}
