package core.businessobject.pv.search;

import java.util.Collection;
import java.util.Iterator;

import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.utility.pv.simu.DynamicValue;
import org.dom4j.Element;
import org.eclipse.swt.widgets.Display;

import Siteview.Convert;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewException;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.thread.IPrincipal;

public class GetApi extends DynamicValue{
	String names;
	Display dis;
	ISiteviewApi siteviewapi;
	IPrincipal iprincipal;
	public GetApi(String name) {
		super(name);
		names=name;
	}

	public GetApi(String name, Display dis, ISiteviewApi siteviewapi,
			IPrincipal iprincipal) {
		super(name);
		names=name;
		this.dis=dis;
		this.siteviewapi=siteviewapi;
		this.iprincipal=iprincipal;
	}

	public  String getMonitorLog(String s) throws SiteviewException{
		String table=s.substring(s.indexOf("//")+1,s.indexOf("?"));
		String key=s.substring(s.indexOf("?")+1,s.indexOf("="));
		String value=s.substring(s.indexOf("=")+1,s.indexOf("@"));
		String colum=s.substring(s.indexOf("@")+1);
		BusinessObject monitorlog=CreateBo(key, value, table);
		String returnvalue=monitorlog.GetField(colum).get_NativeValue().toString();
		return returnvalue;
	}

	@Override
	protected void update() {
		try {
			String value=getMonitorLog(names);
			String value0="0";
			if(value!=null){
				String[] s=value.split("\\*");
				if(s[0].contains("=")){
					value0=s[0].substring(s[0].indexOf("=")+1);
				}
			}
//			setValue(10);
			final ISeverity severity = ValueFactory.createOKSeverity();
			INumericMetaData in=null;
			setValue(ValueFactory.createDoubleValue(
					TimestampFactory.now(), severity, severity.toString(),
					null, Quality.Original, new double[]{Convert.ToDouble(value0)}));
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}
	
	public   BusinessObject CreateBo(String key,String s,String s1) throws SiteviewException {
		Siteview.thread.Thread.set_CurrentPrincipal(iprincipal);
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(s1, QueryInfoToGet.All);
		Element xml ;
		xml=query.get_CriteriaBuilder().FieldAndValueExpression(key,
				Operators.Equals, s);
		query.set_BusObSearchCriteria(xml);
		Collection iCollenction = siteviewapi.get_BusObService()
				.get_SimpleQueryResolver().ResolveQueryToBusObList(query);
		BusinessObject bo=null;
		Iterator interfaceTableIEnum = iCollenction.iterator();
		if(interfaceTableIEnum.hasNext()){
			bo = (BusinessObject) interfaceTableIEnum
					.next();
		}
		return bo;
	}
}
