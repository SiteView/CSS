package com.siteview.pv.monitor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.csstudio.utility.pv.simu.DynamicValue;

import com.siteview.pv.monitor.data.JDBCForSQL;


public class MonitorCounterValue extends DynamicValue{
	public MonitorCounterValue(String name) {
		super(name);
		recId=name.substring(0,name.indexOf("="));
		counter=name.substring(name.indexOf("=")+1);
	}
	public String recId="";
	public String counter="";
		
	@Override//B1E587E8AE67448BAAAB2EFE462CD48C=size
	protected void update() {
		String sql="select * from MonitorLog where monitorId='"+recId+"' order by CreatedDateTime desc;";
		ResultSet result=JDBCForSQL.sql_ConnectExecute_Select(sql); 
		try {
			if(result.next()){
				String value=result.getString("MonitorMassage");
				value=value.replaceAll("\t", ";");
				String[] values=value.split(";");
				System.out.println(value);
				for(String s:values){
					if(s.contains("=")){
						String s1=s.substring(0,s.indexOf("="));
						if(s1.equalsIgnoreCase(counter)){
							String s0=s.substring(s.indexOf("=")+1);
							if(s0.contains("ok")||s0.contains("good")){
								s0="200";
							}
							try{
								final double count=Double.parseDouble(s0);
								setValue((double )count);
							}catch (Exception e) {
								setValue((double )0);
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			setValue(0);
		}
	}

}
