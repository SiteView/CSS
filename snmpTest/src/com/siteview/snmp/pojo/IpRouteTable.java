package com.siteview.snmp.pojo;
/***********************************************************************
 * Module:  IpRouteTable.java
 * Author:  haiming.wang
 * Purpose: Defines the Class IpRouteTable
 ***********************************************************************/

import java.util.*;

/** 路由表
 * 
 * @pdOid 6a341c4b-fec2-4084-8176-f685b3a07c3e */
public class IpRouteTable {
   /** @pdOid 9b2d5d00-4959-4fc9-b045-c4bc4693a097 */
   private String routeDest;
   /** 接口表中接口的索引，通过它到达此路由的下一个中继站 
    * 
    * @pdOid 49c8a167-b232-4447-8ec5-578791888933 */
   private int routeIfIndex;
   /** 下一中继站的ip地址
    * 
    * @pdOid bcd06df5-b2ca-4a2b-89ca-20bcbb387eb8 */
   private String routeNextHop;
   /** @pdOid 2715f121-4d31-4095-bf75-01870a63a52a */
   private String routeType;
   /** 该路由所依据的路由选择机制 
    * 
    * @pdOid 92219422-4b72-4636-b18c-d79b2fd0690f */
   private String ipRouteProto;
   /** 在与路由目标比较之前，逻辑的、带有目标地址的掩码 
    * 
    * @pdOid 2d72d680-721c-4352-b519-5ebf881c5d10 */
   private String ipRouteMask;
   /** 自该路由上次更新以来经过的时间（秒）
    * 
    * @pdOid 838cfb53-5d44-431d-a278-4f879e1c4e9b */
   private int ip路由时间;
   /** 专用于路由选择 1 协议的路由的主要路由选择衡量标准
    * 
    * @pdOid 45d4f7d9-de40-4892-8c19-81e79e72a790 */
   private int ipRouteMetric1;
   /** 专用于路由选择 2 协议的路由的主要路由选择衡量标准 
    * 
    * @pdOid 13276bb4-55e2-443a-ad59-82c0fc7a7469 */
   private int ipRouteMetric2;
   /** 专用于路由选择 3 协议的路由的主要路由选择衡量标准 
    * 
    * @pdOid c87d218d-c73f-4e8b-a23d-bbe19c3ea807 */
   private int ipRouteMetric3;
   /** 专用于路由选择 4 协议的路由的主要路由选择衡量标准 
    * 
    * @pdOid e41ecee0-94ad-457e-b72f-3a9704a61838 */
   private int ipRouteMetric4;
   /** 专用于路由选择 5 协议的路由的主要路由选择衡量标准
    * 
    * @pdOid 1a38b8d0-db5f-4c57-b33e-2b5c05b98ce8 */
   private int ipRouteMetric5;
   /** @pdOid cb091384-0266-4f59-825a-b61f4030698b */
   private String ipRouteInfo;
   
   /** @pdOid b52ade7c-f4b4-4068-b995-1e48a8822b36 */
   public String getRouteDest() {
      return routeDest;
   }
   
   /** @param newRouteDest
    * @pdOid ae389ab3-8ed2-4ccd-b70f-e6e5186b5840 */
   public void setRouteDest(String newRouteDest) {
      routeDest = newRouteDest;
   }
   
   /** @pdOid 3183d109-172b-4a9b-befa-c32841362b1e */
   public int getRouteIfIndex() {
      return routeIfIndex;
   }
   
   /** @param newRouteIfIndex
    * @pdOid 9ab96a45-a9f2-47bd-8c61-13e0833a05db */
   public void setRouteIfIndex(int newRouteIfIndex) {
      routeIfIndex = newRouteIfIndex;
   }
   
   /** @pdOid 12938a25-dc5a-4726-a9ee-af2a718d56f0 */
   public String getRouteNextHop() {
      return routeNextHop;
   }
   
   /** @param newRouteNextHop
    * @pdOid 452ec7b2-d69b-4710-805c-86745533914e */
   public void setRouteNextHop(String newRouteNextHop) {
      routeNextHop = newRouteNextHop;
   }
   
   /** @pdOid 7b7c651e-59a1-4249-a0a5-5ee5784656a7 */
   public String getRouteType() {
      return routeType;
   }
   
   /** @param newRouteType
    * @pdOid 41da3671-3c40-484c-81b9-b5252ab6e577 */
   public void setRouteType(String newRouteType) {
      routeType = newRouteType;
   }
   
   /** @pdOid 3f7d166a-e7bf-4c57-9d10-828fa15abf1e */
   public String getIpRouteProto() {
      return ipRouteProto;
   }
   
   /** @param newIpRouteProto
    * @pdOid bed2933c-dfb6-4099-b652-a1db3513b4d9 */
   public void setIpRouteProto(String newIpRouteProto) {
      ipRouteProto = newIpRouteProto;
   }
   
   /** @pdOid 7defe3f6-3c3d-493c-82be-2e241c55b12b */
   public String getIpRouteMask() {
      return ipRouteMask;
   }
   
   /** @param newIpRouteMask
    * @pdOid ea37b88a-09c2-4334-9bae-cdb24bbf56ae */
   public void setIpRouteMask(String newIpRouteMask) {
      ipRouteMask = newIpRouteMask;
   }
   
   /** @pdOid 4dd6f96b-7a67-4b31-beff-93027a242765 */
   public int getIp路由时间() {
      return ip路由时间;
   }
   
   /** @param newIp路由时间
    * @pdOid 2a5fbd71-5687-404e-bcb0-da1cf23db9f0 */
   public void setIp路由时间(int newIp路由时间) {
      ip路由时间 = newIp路由时间;
   }
   
   /** @pdOid 1a29cd6b-4842-4753-9fcc-2b93cb0dff50 */
   public int getIpRouteMetric1() {
      return ipRouteMetric1;
   }
   
   /** @param newIpRouteMetric1
    * @pdOid a6edb795-1310-4a10-aa48-5336413fd7e1 */
   public void setIpRouteMetric1(int newIpRouteMetric1) {
      ipRouteMetric1 = newIpRouteMetric1;
   }
   
   /** @pdOid b06ff4b3-ce3f-4b70-bddc-2000c887d530 */
   public int getIpRouteMetric2() {
      return ipRouteMetric2;
   }
   
   /** @param newIpRouteMetric2
    * @pdOid 5d8a3fbd-c228-4e02-8768-5dee5e19b807 */
   public void setIpRouteMetric2(int newIpRouteMetric2) {
      ipRouteMetric2 = newIpRouteMetric2;
   }
   
   /** @pdOid 29301152-fb36-4f79-94fc-75048ae59bed */
   public int getIpRouteMetric3() {
      return ipRouteMetric3;
   }
   
   /** @param newIpRouteMetric3
    * @pdOid b61258b9-07fe-42d0-81f6-e1f621dd9c0f */
   public void setIpRouteMetric3(int newIpRouteMetric3) {
      ipRouteMetric3 = newIpRouteMetric3;
   }
   
   /** @pdOid 52a683bc-ae77-44ed-99e0-e95b42bdfcc5 */
   public int getIpRouteMetric4() {
      return ipRouteMetric4;
   }
   
   /** @param newIpRouteMetric4
    * @pdOid a4e48765-d170-4739-bd7e-d36ded7e272f */
   public void setIpRouteMetric4(int newIpRouteMetric4) {
      ipRouteMetric4 = newIpRouteMetric4;
   }
   
   /** @pdOid 20f79e1a-fd3d-467e-8970-f43a96284648 */
   public int getIpRouteMetric5() {
      return ipRouteMetric5;
   }
   
   /** @param newIpRouteMetric5
    * @pdOid b4f79e11-9d0d-4aca-8280-45151dd1e312 */
   public void setIpRouteMetric5(int newIpRouteMetric5) {
      ipRouteMetric5 = newIpRouteMetric5;
   }
   
   /** @pdOid fe869e57-706c-4602-9064-73b6966ddb07 */
   public String getIpRouteInfo() {
      return ipRouteInfo;
   }
   
   /** @param newIpRouteInfo
    * @pdOid ea59252c-95dd-4d33-8720-7f9a1fabed39 */
   public void setIpRouteInfo(String newIpRouteInfo) {
      ipRouteInfo = newIpRouteInfo;
   }

}