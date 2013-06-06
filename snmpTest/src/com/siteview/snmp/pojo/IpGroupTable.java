package com.siteview.snmp.pojo;
/***********************************************************************
 * Module:  ipGroupTable.java
 * Author:  haiming.wang
 * Purpose: Defines the Class ipGroupTable
 ***********************************************************************/

import java.util.*;

/** ip组表
 * 
 * @pdOid 5513ab41-2be7-4581-bc69-94b8e443189c */
public class IpGroupTable {
   /** 表明此实体是否是网关 转发1，不转发2
    * 
    * @pdOid 03fd1664-d9f2-44e6-9985-5276acfb27b0 */
   private int ipForwarding;
   /** 插入到 IP 首部的缺省时间长度 
    * 
    * @pdOid d884dfee-8291-475f-8d44-c949966d2671 */
   private int ipDefaultTTL;
   /** 接收的数据报数 
    * 
    * @pdOid 811357c3-7ad8-4e68-8f51-c2bf08c2acfb */
   private double ipInReceives;
   /** 因 IP 首部出错而放弃的输入数据报数 
    * 
    * @pdOid a1f2a6e6-4bbc-4605-a354-c514fa18f126 */
   private double ipInHdrErrors;
   /** 因目标 IP 地址出错而放弃的输入数据报数 
    * 
    * @pdOid 2a110ea5-986e-460b-a782-f94d53612016 */
   private double ipInAddrErrors;
   /** 转发的数据报数
    * 
    * @pdOid 7cf77c00-4621-4922-9db6-623cfcbeaa90 */
   private double ipForwDatagrams;
   /** 因使用不支持的协议而放弃的发往本地的数据报数 
    * 
    * @pdOid c22121aa-009b-4545-b2da-bbabb11f2b7d */
   private double ipInUnknownProtos;
   /** 放弃的输入数据报数 
    * 
    * @pdOid a6e60799-dfaa-4b4e-9995-4339e680aad2 */
   private double ipInDiscards;
   /** 成功传送的输入数据报数 
    * 
    * @pdOid af23de0a-7ace-4f0d-bddf-382c9db72063 */
   private double ipInDelivers;
   /** 提供给 IP 进行传送的数据报数 
    * 
    * @pdOid 427df5fc-eae6-43ad-a575-f2ecaa83c2c7 */
   private double ipOutRequests;
   /** 放弃的输出 IP 数据报数 
    * 
    * @pdOid 4860b393-56a5-423d-a4e7-663036c19ddc */
   private double ipOutDiscards;
   /** 因未找到路由目标而放弃的输出 IP 数据报数 
    * 
    * @pdOid 3ca963b7-bf52-49e0-83a6-63c893b50fbf */
   private double ipOutNoRoutes;
   /** 重装收到的段之前等待的最长时间（秒）
    * 
    * @pdOid 67eef1d3-3ff4-4647-ae33-d7cf22c8dfde */
   private int ipReasmTimeout;
   /** 接收到的要求重装的 IP 段数
    * 
    * @pdOid d42092fa-62c2-4181-8809-4119bbcba298 */
   private double ipReasmReqds;
   /** 成功重装的 IP 数据报数
    * 
    * @pdOid b1b194df-0327-46cd-b68f-1c0c74bdbacf */
   private double ipReasmOKs;
   /** 重装算法检测到的失败次数 
    * 
    * @pdOid d3d3489c-2dab-44bd-8b9d-2895e4ef807f */
   private double ipReasmFails;
   /** 成功分段的 IP 数据报数 
    * 
    * @pdOid 86688a40-5c91-4135-bad9-65730f273f8e */
   private double ipFragOKs;
   /** 分段失败的 IP 数据报数
    * 
    * @pdOid bf6e22ce-ea7b-4202-a4e9-ed0ebd9322bd */
   private double ipFragFails;
   /** 由分段生成的 IP 数据报段数
    * 
    * @pdOid 9c9e131d-b623-4a07-a0c2-59aa3bdcee81 */
   private double ipFragCreates;
   /** 被丢弃的路由条数，即使路由信息是有效的，有可能的原因是清理缓存
    * 
    * @pdOid 42a148cd-91ba-46a7-8ffd-af50d3e4f318 */
   private double ipRoutingDiscards;
   
   private IpNetToMedia ipNetToMedia;
   
   private IpRouteTable ipRouteTable;
   
   private IpAddressTable ipAddressTable;
   
   /** @pdOid 1c4d2c71-fdac-49a2-99bd-01b2e6eda431 */
   public int getIpForwarding() {
      return ipForwarding;
   }
   
   /** @param newIpForwarding
    * @pdOid 8de675a4-63b9-4170-a189-94594a81d138 */
   public void setIpForwarding(int newIpForwarding) {
      ipForwarding = newIpForwarding;
   }
   
   /** @pdOid 7af222ce-7e72-4e59-8136-4a7b636aa459 */
   public int getIpDefaultTTL() {
      return ipDefaultTTL;
   }
   
   /** @param newIpDefaultTTL
    * @pdOid 5fc4aa91-87f7-4d6b-b93c-de85a9ac4294 */
   public void setIpDefaultTTL(int newIpDefaultTTL) {
      ipDefaultTTL = newIpDefaultTTL;
   }
   
   /** @pdOid a4b122e3-ca38-4899-835e-f6850c9793ba */
   public double getIpInReceives() {
      return ipInReceives;
   }
   
   /** @param newIpInReceives
    * @pdOid ac1fae90-e55e-42e0-a37b-bc3267af8add */
   public void setIpInReceives(double newIpInReceives) {
      ipInReceives = newIpInReceives;
   }
   
   /** @pdOid a4d061c6-6188-4e16-9967-a6af15f3f764 */
   public double getIpInHdrErrors() {
      return ipInHdrErrors;
   }
   
   /** @param newIpInHdrErrors
    * @pdOid e93c0842-c35f-42e4-b1cb-7a3c7c47dc62 */
   public void setIpInHdrErrors(double newIpInHdrErrors) {
      ipInHdrErrors = newIpInHdrErrors;
   }
   
   /** @pdOid 1a3dd4c3-c8e2-4ad0-81eb-caa496c17dea */
   public double getIpInAddrErrors() {
      return ipInAddrErrors;
   }
   
   /** @param newIpInAddrErrors
    * @pdOid 546d063b-7414-4f51-b288-cf83544852a0 */
   public void setIpInAddrErrors(double newIpInAddrErrors) {
      ipInAddrErrors = newIpInAddrErrors;
   }
   
   /** @pdOid 0fe54acb-a782-474b-b1c6-e3468a30201d */
   public double getIpForwDatagrams() {
      return ipForwDatagrams;
   }
   
   /** @param newIpForwDatagrams
    * @pdOid 313887ae-0008-4d55-9a73-27620b8449f0 */
   public void setIpForwDatagrams(double newIpForwDatagrams) {
      ipForwDatagrams = newIpForwDatagrams;
   }
   
   /** @pdOid e914d32e-f155-4d35-afa5-7ec5a01ab449 */
   public double getIpInUnknownProtos() {
      return ipInUnknownProtos;
   }
   
   /** @param newIpInUnknownProtos
    * @pdOid 60895e4f-6c84-468f-833d-7f2a274fc09b */
   public void setIpInUnknownProtos(double newIpInUnknownProtos) {
      ipInUnknownProtos = newIpInUnknownProtos;
   }
   
   /** @pdOid 639d5e80-e23b-41b1-b0d7-5effea60e07b */
   public double getIpInDiscards() {
      return ipInDiscards;
   }
   
   /** @param newIpInDiscards
    * @pdOid 9f843cf2-d8c9-490d-9da3-3102d3ecac72 */
   public void setIpInDiscards(double newIpInDiscards) {
      ipInDiscards = newIpInDiscards;
   }
   
   /** @pdOid 95760204-3168-4bc4-aa13-662d8ec70e29 */
   public double getIpInDelivers() {
      return ipInDelivers;
   }
   
   /** @param newIpInDelivers
    * @pdOid 9a09a010-cd80-418a-858a-fabc8c199b62 */
   public void setIpInDelivers(double newIpInDelivers) {
      ipInDelivers = newIpInDelivers;
   }
   
   /** @pdOid 21a5d920-6a22-4d6f-a1c8-21bc1389481b */
   public double getIpOutRequests() {
      return ipOutRequests;
   }
   
   /** @param newIpOutRequests
    * @pdOid e463bdb4-16ca-42d4-8bf1-7d32249d40a5 */
   public void setIpOutRequests(double newIpOutRequests) {
      ipOutRequests = newIpOutRequests;
   }
   
   /** @pdOid 96e0f364-5244-4605-90f8-a99f4ca11c4d */
   public double getIpOutDiscards() {
      return ipOutDiscards;
   }
   
   /** @param newIpOutDiscards
    * @pdOid a5fcc329-6928-4b77-9369-f8c992a453aa */
   public void setIpOutDiscards(double newIpOutDiscards) {
      ipOutDiscards = newIpOutDiscards;
   }
   
   /** @pdOid 481e7a9a-b218-450a-891b-63647815f312 */
   public double getIpOutNoRoutes() {
      return ipOutNoRoutes;
   }
   
   /** @param newIpOutNoRoutes
    * @pdOid 3eb077af-e2ee-48f1-9b32-9226922c5c17 */
   public void setIpOutNoRoutes(double newIpOutNoRoutes) {
      ipOutNoRoutes = newIpOutNoRoutes;
   }
   
   /** @pdOid bd1b320d-8fed-4c88-987c-087529a34955 */
   public int getIpReasmTimeout() {
      return ipReasmTimeout;
   }
   
   /** @param newIpReasmTimeout
    * @pdOid 6bf26baa-7e58-45cd-8a76-03765cfc4811 */
   public void setIpReasmTimeout(int newIpReasmTimeout) {
      ipReasmTimeout = newIpReasmTimeout;
   }
   
   /** @pdOid 47abb782-cbf2-4c48-a7d1-55e4a9815e12 */
   public double getIpReasmReqds() {
      return ipReasmReqds;
   }
   
   /** @param newIpReasmReqds
    * @pdOid 1a88bf2e-941e-479f-8fc8-86c44be112d5 */
   public void setIpReasmReqds(double newIpReasmReqds) {
      ipReasmReqds = newIpReasmReqds;
   }
   
   /** @pdOid ac8e0ab4-fd6f-4d75-a863-65fa7d23e4fe */
   public double getIpReasmOKs() {
      return ipReasmOKs;
   }
   
   /** @param newIpReasmOKs
    * @pdOid 21e3d47d-574a-4e0b-8873-22285120e2fa */
   public void setIpReasmOKs(double newIpReasmOKs) {
      ipReasmOKs = newIpReasmOKs;
   }
   
   /** @pdOid c04b272a-fb8c-48c2-9281-24067801da51 */
   public double getIpReasmFails() {
      return ipReasmFails;
   }
   
   /** @param newIpReasmFails
    * @pdOid c4e81e6a-0f2e-4a77-951c-27bded481a20 */
   public void setIpReasmFails(double newIpReasmFails) {
      ipReasmFails = newIpReasmFails;
   }
   
   /** @pdOid 335e9eac-4c5f-403e-9c6e-36a44a3daf81 */
   public double getIpFragOKs() {
      return ipFragOKs;
   }
   
   /** @param newIpFragOKs
    * @pdOid cae47342-2d2c-493e-96f4-aabc2169ca8b */
   public void setIpFragOKs(double newIpFragOKs) {
      ipFragOKs = newIpFragOKs;
   }
   
   /** @pdOid 917b549a-fa27-4262-87f6-d61884f78e41 */
   public double getIpFragFails() {
      return ipFragFails;
   }
   
   /** @param newIpFragFails
    * @pdOid b0d41609-1386-4e0a-895c-627da9348203 */
   public void setIpFragFails(double newIpFragFails) {
      ipFragFails = newIpFragFails;
   }
   
   /** @pdOid 71478cc1-2ba9-47cd-baac-c72bf0d6bfd4 */
   public double getIpFragCreates() {
      return ipFragCreates;
   }
   
   /** @param newIpFragCreates
    * @pdOid 3381e0bd-12f3-48c6-8a95-75d3a69ec4f4 */
   public void setIpFragCreates(double newIpFragCreates) {
      ipFragCreates = newIpFragCreates;
   }
   
   /** @pdOid 42c7298e-4d61-4409-9740-f8b75ec93061 */
   public double getIpRoutingDiscards() {
      return ipRoutingDiscards;
   }
   
   /** @param newIpRoutingDiscards
    * @pdOid c459cff2-510a-40b5-a590-c38eb250514b */
   public void setIpRoutingDiscards(double newIpRoutingDiscards) {
      ipRoutingDiscards = newIpRoutingDiscards;
   }

}