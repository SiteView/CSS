package com.siteview.snmp.pojo;

/***********************************************************************
 * Module:  InterfaceTable.java
 * Author:  haiming.wang
 * Purpose: Defines the Class InterfaceTable
 ***********************************************************************/

import java.util.*;

/** @pdOid c00671ed-4ee2-4b13-b8ae-874deda6b66a */
public class InterfaceTable {
	/** @pdOid 7cbc5aa4-6bad-4de1-9823-2970f2479ad4 */
	private int ifIndex;
	/** @pdOid 66cc9e6e-9acb-433e-ac17-4efbadeaca8b */
	private String ifDescr;
	/** @pdOid 15cdf2e4-d28c-4436-9881-ff154659fb88 */
	private String ifType;
	/** @pdOid 3b502707-b6cf-4297-9ea6-cc2a2f908f2a */
	private long ifMtu;
	/** @pdOid b2caea25-f596-4014-b4e6-786e9f1cc80e */
	private long ifSpeed;
	/** @pdOid 515c5fce-9fdc-41ca-9a13-f48872954052 */
	private String ifPhysAddress;
	/** @pdOid adf676ef-5d6b-4c6b-8458-89276b3b932e */
	private int ifAdminStatus;
	/** @pdOid b3836306-de0f-4553-9b1a-cf9ba4c88cc2 */
	private String ifLastChange;
	/** @pdOid 80109bcd-a31b-4205-94ee-f78cfde31796 */
	private int ifOperStatus;
	/** @pdOid 149018c3-f616-48bf-a4e6-7b83450a6a36 */
	private String inoctetsOid;
	/**
	 * 商品接收的字节总数
	 * 
	 * @pdOid 240619de-f9cf-42d6-b105-a2fea1b7828e
	 */
	private long ifInoctets;
	/**
	 * 输入广播包数
	 * 
	 * @pdOid 554602b0-763b-43c6-b9c7-80391c5547ac
	 */
	private long ifInNUcastPkts;
	/** @pdOid 8c7cd3b5-7667-4f5f-84f0-6afeff392318 */
	private long ifinUcastPkts;
	/**
	 * 输入包丢弃数
	 * 
	 * @pdOid 24744e00-0616-44db-8d4a-a6e93ea30ca6
	 */
	private long ifInDiscards;
	/**
	 * 输入包错误数
	 * 
	 * @pdOid 790785d0-9f41-4e2f-9b95-fb57a6a5a1c1
	 */
	private long ifInErrors;
	/** @pdOid 19ffce69-20ba-4ed2-b279-eb830b75d63e */
	private long ifOutOctets;
	/** @pdOid 80985886-b907-4dc7-be92-c3b61f5294e8 */
	private String outoctetsoid;
	/** @pdOid dc2b29e7-cda5-450d-949e-937059a87206 */
	private long ifOutUcastPkts;
	/** @pdOid 7b800192-e233-46b8-bc8b-8c232183f21b */
	private long ifOutNUcastPkts;
	/** @pdOid b7ef28cd-7515-42e4-bb13-a486a50d6512 */
	private long ifOutDiscards;
	/** @pdOid 4d10e1b3-aaf3-4525-be85-9f37fca8ddfe */
	private long ifOutErrors;
	/** @pdOid c04a4bd5-110e-4d2f-9557-cf8d052f074e */
	private long ifOutQLen;
	/** @pdOid 595b00cb-91fd-4522-9c3c-e349d44baf7d */
	private String ifspecific;
	/** @pdOid d2518412-8f1c-423a-8272-bb472115b54c */
	private String ifalias;
	/** @pdOid 3b62ee5b-c754-484e-b92b-4e6a42dd74f6 */
	private Date createddatetime;
	/** @pdOid 6bd910b3-b89a-44b0-8e43-7bce0ed48c06 */
	private String ip;
	/** @pdOid 716ac60f-cecc-4287-bfe7-cc694dd3d582 */
	private int ifInUnknownProtos;

	/**
	 * 判断接口是否正常使用中
	 * 
	 * @return
	 */
	public boolean isUsed() {
		return (this.getIfOperStatus() == 1 && this.getIfAdminStatus() == 1);
	}

	/**
	 * 接口连接失败
	 * 
	 * @return
	 */
	public boolean isError() {
		return (this.getIfOperStatus() == 2 && this.getIfAdminStatus() == 1);
	}

	/**
	 * 接口是否己停用
	 * 
	 * @return
	 */
	public boolean isDown() {
		return (this.getIfOperStatus() == 2 && this.getIfAdminStatus() == 2);
	}

	/**
	 * 接口是否正在测试中
	 * 
	 * @return
	 */
	public boolean isTesting() {
		return (this.getIfOperStatus() == 3 && this.getIfAdminStatus() == 3);
	}

	/** @pdOid 4bd6b0a6-c8c3-412d-a7f4-7eafd42f4cf6 */
	public int getIfIndex() {
		return ifIndex;
	}

	/**
	 * @param newIfIndex
	 * @pdOid 8e82bcb8-9414-44aa-a777-231e5d04fcdb
	 */
	public void setIfIndex(int newIfIndex) {
		ifIndex = newIfIndex;
	}

	/** @pdOid 45a70baf-fd05-471c-8119-c49ed8477595 */
	public String getIfDescr() {
		return ifDescr;
	}

	/**
	 * @param newIfDescr
	 * @pdOid 3ef9830d-c394-4196-b0e0-95df3e53df9a
	 */
	public void setIfDescr(String newIfDescr) {
		ifDescr = newIfDescr;
	}

	/** @pdOid 0dd726ed-f754-4497-93e3-4a2a3c88e55c */
	public String getIfType() {
		return ifType;
	}

	/**
	 * @param newIfType
	 * @pdOid f187e4de-aa2c-414f-8798-d09eeefdc225
	 */
	public void setIfType(String newIfType) {
		ifType = newIfType;
	}

	/** @pdOid e7343fe1-d92c-4ef1-b983-2534841aaf6c */
	public long getIfMtu() {
		return ifMtu;
	}

	/**
	 * @param newIfMtu
	 * @pdOid 344e3def-9a6a-4806-845a-8a0bf729bdb9
	 */
	public void setIfMtu(long newIfMtu) {
		ifMtu = newIfMtu;
	}

	/** @pdOid a33118b6-3526-433a-b88c-4c91396ddf22 */
	public long getIfSpeed() {
		return ifSpeed;
	}

	/**
	 * @param newIfSpeed
	 * @pdOid 2cbb7638-8442-4f6a-9750-15087eef70d0
	 */
	public void setIfSpeed(long newIfSpeed) {
		ifSpeed = newIfSpeed;
	}

	/** @pdOid c1b1cbdb-1b2e-45ac-825c-b1f94683919c */
	public String getIfPhysAddress() {
		return ifPhysAddress;
	}

	/**
	 * @param newIfPhysAddress
	 * @pdOid f89d119f-5b26-4eb5-8670-83423ad12621
	 */
	public void setIfPhysAddress(String newIfPhysAddress) {
		ifPhysAddress = newIfPhysAddress;
	}

	/** @pdOid 8432a03c-7e94-46c1-9eba-b1e307a7b277 */
	public int getIfAdminStatus() {
		return ifAdminStatus;
	}

	/**
	 * @param newIfAdminStatus
	 * @pdOid 1de77687-e787-4470-820f-5b985a3750a0
	 */
	public void setIfAdminStatus(int newIfAdminStatus) {
		ifAdminStatus = newIfAdminStatus;
	}

	/** @pdOid 98e76d5d-b741-4fc0-aeb5-edb952e4f8fc */
	public String getIfLastChange() {
		return ifLastChange;
	}

	/**
	 * @param newIfLastChange
	 * @pdOid 93db93b5-3e2d-444a-aef8-fc4fdd99ce80
	 */
	public void setIfLastChange(String newIfLastChange) {
		ifLastChange = newIfLastChange;
	}

	/** @pdOid 13bc3cdd-5091-43de-a4cb-5b77dc4786e4 */
	public String getInoctetsOid() {
		return inoctetsOid;
	}

	/**
	 * @param newInoctetsOid
	 * @pdOid 28100266-bbed-4020-99cb-3a887147f2d3
	 */
	public void setInoctetsOid(String newInoctetsOid) {
		inoctetsOid = newInoctetsOid;
	}

	/** @pdOid cdc90b7d-2f82-46fc-9ce4-32c95272da43 */
	public long getIfInoctets() {
		return ifInoctets;
	}

	/**
	 * @param newIfInoctets
	 * @pdOid 9b2a0ea4-51d2-4332-8eee-9109ef388d81
	 */
	public void setIfInoctets(long newIfInoctets) {
		ifInoctets = newIfInoctets;
	}

	/** @pdOid 7b6320c9-be97-499f-973c-eb63999155f7 */
	public long getIfInNUcastPkts() {
		return ifInNUcastPkts;
	}

	/**
	 * @param newIfInNUcastPkts
	 * @pdOid b5fc4b77-9881-4331-ac39-211561e567f6
	 */
	public void setIfInNUcastPkts(long newIfInNUcastPkts) {
		ifInNUcastPkts = newIfInNUcastPkts;
	}

	/** @pdOid 00c25a4c-8da1-434f-bb94-9a1ed501b6b6 */
	public long getIfInDiscards() {
		return ifInDiscards;
	}

	/**
	 * @param newIfInDiscards
	 * @pdOid 02d4cb11-a837-4e3c-b71f-5a4b1bb79b83
	 */
	public void setIfInDiscards(long newIfInDiscards) {
		ifInDiscards = newIfInDiscards;
	}

	/** @pdOid 033e9d23-8dbb-497d-94cc-1dae08fef9c0 */
	public long getIfInErrors() {
		return ifInErrors;
	}

	/**
	 * @param newIfInErrors
	 * @pdOid d9d2542c-9c47-4333-bacb-cbcfbf4d0166
	 */
	public void setIfInErrors(long newIfInErrors) {
		ifInErrors = newIfInErrors;
	}

	/** @pdOid ca37930b-2f40-4926-8bb4-b61c6c79dccf */
	public long getIfOutOctets() {
		return ifOutOctets;
	}

	/**
	 * @param newIfOutOctets
	 * @pdOid 91f77f46-48f4-486a-b70b-9848b0b477c6
	 */
	public void setIfOutOctets(long newIfOutOctets) {
		ifOutOctets = newIfOutOctets;
	}

	/** @pdOid c23623e9-90b7-4e3a-930c-8bd0cd759a6a */
	public String getOutoctetsoid() {
		return outoctetsoid;
	}

	/**
	 * @param newOutoctetsoid
	 * @pdOid 3b7368e0-4e77-457e-b9d5-26b48e7114a3
	 */
	public void setOutoctetsoid(String newOutoctetsoid) {
		outoctetsoid = newOutoctetsoid;
	}

	/** @pdOid 2dc4620f-097e-4a15-b795-744a8b57f5e0 */
	public long getIfOutUcastPkts() {
		return ifOutUcastPkts;
	}

	/**
	 * @param newIfOutUcastPkts
	 * @pdOid 772a299c-e012-4f03-a2c5-a877b6e2ec83
	 */
	public void setIfOutUcastPkts(long newIfOutUcastPkts) {
		ifOutUcastPkts = newIfOutUcastPkts;
	}

	/** @pdOid 473c56d0-fae1-40d3-987f-a0b788fc8845 */
	public long getIfOutNUcastPkts() {
		return ifOutNUcastPkts;
	}

	/**
	 * @param newIfOutNUcastPkts
	 * @pdOid 4a666710-238b-41f8-8d4e-841530599159
	 */
	public void setIfOutNUcastPkts(long newIfOutNUcastPkts) {
		ifOutNUcastPkts = newIfOutNUcastPkts;
	}

	/** @pdOid c6b4b2ac-3722-4f25-9b45-756b98d9222b */
	public long getIfOutDiscards() {
		return ifOutDiscards;
	}

	/**
	 * @param newIfOutDiscards
	 * @pdOid 49086591-2a4c-4d46-a471-efef4386fe7e
	 */
	public void setIfOutDiscards(long newIfOutDiscards) {
		ifOutDiscards = newIfOutDiscards;
	}

	/** @pdOid 1c753006-59bf-48ee-9fd2-e1ea12bde081 */
	public long getIfOutErrors() {
		return ifOutErrors;
	}

	/**
	 * @param newIfOutErrors
	 * @pdOid fd3aef06-2fe5-46e7-bec5-5bbf712865c8
	 */
	public void setIfOutErrors(long newIfOutErrors) {
		ifOutErrors = newIfOutErrors;
	}

	/** @pdOid f69a1328-0b3d-4d3e-be3b-9d8f68cba750 */
	public long getIfOutQLen() {
		return ifOutQLen;
	}

	/**
	 * @param newIfOutQLen
	 * @pdOid f65b5fb6-6ffb-4cc4-beea-d58a3cc25a20
	 */
	public void setIfOutQLen(long newIfOutQLen) {
		ifOutQLen = newIfOutQLen;
	}

	/** @pdOid 04387422-21a3-4736-914b-86b23d504b1b */
	public String getIfspecific() {
		return ifspecific;
	}

	/**
	 * @param newIfspecific
	 * @pdOid 49e0573f-e985-4a82-a3f4-6017133e71e8
	 */
	public void setIfspecific(String newIfspecific) {
		ifspecific = newIfspecific;
	}

	/** @pdOid 1b7ac6cc-f372-4d1c-b859-0fae872ef954 */
	public String getIfalias() {
		return ifalias;
	}

	/**
	 * @param newIfalias
	 * @pdOid e52a6600-3526-4bdb-9c48-00b5da5d76c1
	 */
	public void setIfalias(String newIfalias) {
		ifalias = newIfalias;
	}

	/** @pdOid bb40d3a7-2cd2-49e6-98b5-a946e54df936 */
	public Date getCreateddatetime() {
		return createddatetime;
	}

	/**
	 * @param newCreateddatetime
	 * @pdOid 34741526-303c-426d-be56-fbf89c7ca4f2
	 */
	public void setCreateddatetime(Date newCreateddatetime) {
		createddatetime = newCreateddatetime;
	}

	/** @pdOid 7e3d3e52-4581-4a1b-b6e0-d30146176c07 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param newIp
	 * @pdOid 745e54f7-c610-4f0e-ad87-f5e96347d31e
	 */
	public void setIp(String newIp) {
		ip = newIp;
	}

	/** @pdOid 11aea556-17af-4ff6-ac4c-bc95a90cf28d */
	public int getIfInUnknownProtos() {
		return ifInUnknownProtos;
	}

	/**
	 * @param newIfInUnknownProtos
	 * @pdOid 8be474d8-0ab1-4cc8-82bd-ff1e68d81e5e
	 */
	public void setIfInUnknownProtos(int newIfInUnknownProtos) {
		ifInUnknownProtos = newIfInUnknownProtos;
	}

	/** @pdOid 624e9bd5-6bb6-43bf-996a-502631282fe4 */
	public int getIfOperStatus() {
		return ifOperStatus;
	}

	/**
	 * @param newIfOperStatus
	 * @pdOid 70d99826-c095-4bb0-9d9b-0e2b8cdd4858
	 */
	public void setIfOperStatus(int newIfOperStatus) {
		ifOperStatus = newIfOperStatus;
	}

	/** @pdOid a37eb551-fab5-4d64-b042-9c48da12fcb6 */
	public long getIfinUcastPkts() {
		return ifinUcastPkts;
	}

	/**
	 * @param newIfinUcastPkts
	 * @pdOid 06ac94b0-db75-4cc3-a238-4af34e29b3b2
	 */
	public void setIfinUcastPkts(long newIfinUcastPkts) {
		ifinUcastPkts = newIfinUcastPkts;
	}
}