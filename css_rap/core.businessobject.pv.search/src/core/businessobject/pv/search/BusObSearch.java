package core.businessobject.pv.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Element;

import Siteview.DataTable;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SearchCriteria;
import Siteview.SiteviewException;
import Siteview.SiteviewQuery;

public class BusObSearch {
	
	public Map<Class<?>,Object> searchBusOb(String busobname,String findfieldname,Map<String,String> expression) throws SiteviewException{
		SiteviewQuery siteviewQuery = new SiteviewQuery();
		siteviewQuery.AddBusObQuery(busobname,QueryInfoToGet.RequestedFields,findfieldname);
		if(expression.size()>0){
			SearchCriteria searchCriteria = siteviewQuery.get_CriteriaBuilder();
			Element[] expressElement = new Element[expression.size()];
			int index = 0;
			for(Iterator<String> iter = expression.keySet().iterator();iter.hasNext();){
				String fieldname = iter.next();
				String value = expression.get(fieldname);
				expressElement[index] = searchCriteria.FieldAndValueExpression(fieldname,Operators.Equals, value);
				index++;
			}
			if(expression.size()>1){
				Element occelement = searchCriteria.AndExpressions(expressElement);
				siteviewQuery.set_BusObSearchCriteria(occelement);
			}
			else{
				siteviewQuery.set_BusObSearchCriteria(expressElement[0]);
			}
			
		}
		
		DataTable dt = BusObConnection.getBusObApi().get_BusObService().get_SimpleQueryResolver().ResolveQueryToBusObDataTable(siteviewQuery);
		
		if(dt == null){
			return null;
		}
		else{
			if(dt.get_Rows().size()==0){
				return null;
			}
			else if(dt.get_Rows().size()>1){
				throw new SiteviewException("查询的结果不是唯一的.");
			}
			else{
				Map<Class<?>,Object> valueMap = new HashMap<Class<?>, Object>();
				valueMap.put(String.class,dt.get_Rows().get(0).get(findfieldname));
				return valueMap;
			}
		}
	}
	
	public static void main(String[] args) throws SiteviewException {
		BusObSearch search = new BusObSearch();
		Map<String,String> map = new HashMap<String,String>();
		map.put("monitorid","BEC27AFD44C44456B2C392E7DF516063");
		System.out.println(search.searchBusOb("EccDyn", "monitordesc", map).get(String.class));
	}
}
