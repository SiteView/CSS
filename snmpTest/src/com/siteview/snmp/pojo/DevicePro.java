package com.siteview.snmp.pojo;

/**
 * 设备基本属性数据
 * @author haiming.wang
 *
 */
public class DevicePro {

	private String devType;     //设备类型
    private String devTypeName; //设备类型名称
    private String devFac;      //设备厂家
    private String devModel;    //设备型号
    
	public String getDevType() {
		return devType;
	}
	public void setDevType(String devType) {
		this.devType = devType;
	}
	public String getDevTypeName() {
		return devTypeName;
	}
	public void setDevTypeName(String devTypeName) {
		this.devTypeName = devTypeName;
	}
	public String getDevFac() {
		return devFac;
	}
	public void setDevFac(String devFac) {
		this.devFac = devFac;
	}
	public String getDevModel() {
		return devModel;
	}
	public void setDevModel(String devModel) {
		this.devModel = devModel;
	}
    
	
}
