package com.siteview.snmp.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTaskPool {

	static ExecutorService pool = null;
	
	private static ThreadTaskPool instance= null;
	
	public static final int defaultCount = 50;
	
	private ThreadTaskPool(){}
	
	private ThreadTaskPool(int threadCount){
		pool = Executors.newFixedThreadPool(threadCount);
	}
	public static synchronized ThreadTaskPool getInstance(int threadCount){
		if(instance == null){
			instance = new ThreadTaskPool(threadCount);
		}
		return instance;
	}
	public static ThreadTaskPool getInstance(){
		return getInstance(defaultCount);
	}
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
