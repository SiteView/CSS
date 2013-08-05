package com.siteview.itsm.nnm.scan.core.snmp.pojo;

/**
 * �豸������������
 * @author haiming.wang
 *
 */
public class DevicePro {

	private String devType;     //�豸����
    private String devTypeName; //�豸��������
    private String devFac;      //�豸����
    private String devModel;    //�豸�ͺ�
    
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
