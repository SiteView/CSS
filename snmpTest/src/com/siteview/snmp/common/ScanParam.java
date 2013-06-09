package com.siteview.snmp.common;

import java.util.List;

import com.siteview.snmp.model.Pair;

public class ScanParam {
	// 缺省读共同体名
	private String community_get_dft;
	// 缺省写共同体名
	private String community_set_dft;
	// 超时时间
	private int timeout;
	// 线程数量
	private int threadCount;
	// 扫描深度
	private int depth;
	// 重试次数
	private int retrytimes;
	// <种子IP>
	private List<String> scan_seeds;
	// <起始IP,结束IP>
	private Pair<String, String> scan_scales;
	private Pair<Long, Long> scan_scales_num;
	// <<范围>,<读写共同体名> >
	private Pair<Pair<String, String>, Pair<String, String>> communitys;
	private Pair<Pair<Long, Long>, Pair<Long, Long>> communitys_num;
	// 排除的范围
	private List<Pair<String, String>> filter_scales;
	private List<Pair<Long, Long>> filter_scales_num;
//	std::list<std::pair<std::string,std::string> > filter_scales;
//    std::list<std::pair<unsigned long, unsigned long> > filter_scales_num;

	
	
	public String getCommunity_get_dft() {
		return community_get_dft;
	}

	public void setCommunity_get_dft(String community_get_dft) {
		this.community_get_dft = community_get_dft;
	}

	public String getCommunity_set_dft() {
		return community_set_dft;
	}

	public void setCommunity_set_dft(String community_set_dft) {
		this.community_set_dft = community_set_dft;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getRetrytimes() {
		return retrytimes;
	}

	public void setRetrytimes(int retrytimes) {
		this.retrytimes = retrytimes;
	}

	public List<String> getScan_seeds() {
		return scan_seeds;
	}

	public void setScan_seeds(List<String> scan_seeds) {
		this.scan_seeds = scan_seeds;
	}

	public Pair<String, String> getScan_scales() {
		return scan_scales;
	}

	public void setScan_scales(Pair<String, String> scan_scales) {
		this.scan_scales = scan_scales;
	}

	public Pair<Long, Long> getScan_scales_num() {
		return scan_scales_num;
	}

	public void setScan_scales_num(Pair<Long, Long> scan_scales_num) {
		this.scan_scales_num = scan_scales_num;
	}

	public Pair<Pair<String, String>, Pair<String, String>> getCommunitys() {
		return communitys;
	}

	public void setCommunitys(
			Pair<Pair<String, String>, Pair<String, String>> communitys) {
		this.communitys = communitys;
	}

	public Pair<Pair<Long, Long>, Pair<Long, Long>> getCommunitys_num() {
		return communitys_num;
	}

	public void setCommunitys_num(
			Pair<Pair<Long, Long>, Pair<Long, Long>> communitys_num) {
		this.communitys_num = communitys_num;
	}

	public List<Pair<String, String>> getFilter_scales() {
		return filter_scales;
	}

	public void setFilter_scales(List<Pair<String, String>> filter_scales) {
		this.filter_scales = filter_scales;
	}

	public List<Pair<Long, Long>> getFilter_scales_num() {
		return filter_scales_num;
	}

	public void setFilter_scales_num(List<Pair<Long, Long>> filter_scales_num) {
		this.filter_scales_num = filter_scales_num;
	}
}
