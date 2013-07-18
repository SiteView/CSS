package com.siteview.snmp.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 对多线程任务执行的简单封装
 * @author haiming.wang
 *
 */
public class ThreadTaskPool {
	/**
	 * 线程池对象
	 */
	static ExecutorService pool = null;
	/**
	 * 单例的实例对象
	 */
	private static ThreadTaskPool instance= null;
	/**
	 * 默认的线程数
	 */
	public static final int defaultCount = 50;
	/**
	 * 构造函数
	 */
	private ThreadTaskPool(){}
	/**
	 * 构造函数
	 * @param threadCount 数据数目
	 */
	private ThreadTaskPool(int threadCount){
		pool = Executors.newFixedThreadPool(threadCount);
	}
	public static synchronized ThreadTaskPool getInstance(int threadCount){
		if(instance == null){
			instance = new ThreadTaskPool(threadCount);
		}
		return instance;
	}
	/**
	 * 返回默认的线程数 的线程池
	 * @return
	 */
	public static ThreadTaskPool getInstance(){
		return getInstance(defaultCount);
	}
	/**
	 * 执行任务
	 * @param task
	 */
	public void excute(Runnable task){
		pool.execute(task);
	}
	public void wai() {
		try {
			pool.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
