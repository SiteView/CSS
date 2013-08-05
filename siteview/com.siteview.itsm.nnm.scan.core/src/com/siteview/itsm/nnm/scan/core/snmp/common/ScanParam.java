package com.siteview.itsm.nnm.scan.core.snmp.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;

public class ScanParam {
	// ȱʡ����ͬ����
	private String community_get_dft = "public";
	// ȱʡд��ͬ����
	private String community_set_dft = "public";
	// ��ʱʱ��
	private int timeout = 200;
	// �߳�����
	private int threadCount = 50;
	// ɨ�����
	private int depth = 5;
	// ���Դ���
	private int retrytimes = 2;
	// <����IP>
	private List<String> scan_seeds = new ArrayList<String>();
	// <��ʼIP,����IP>
	private List<Pair<String, String>> scan_scales = new ArrayList<Pair<String, String>>();
	private List<Pair<Long, Long>> scan_scales_num = new ArrayList<Pair<Long,Long>>();
	// <<��Χ>,<��д��ͬ����> >
	private List<Pair<Pair<String, String>, Pair<String, String>>> communitys = new ArrayList<Pair<Pair<String,String>,Pair<String,String>>>();

	private List<Pair<Pair<Long, Long>, Pair<String, String>>> communitys_num = new ArrayList<Pair<Pair<Long, Long>, Pair<String, String>>>();
	// �ų��ķ�Χ
	private List<Pair<String, String>> filter_scales = new ArrayList<Pair<String, String>>();
	private List<Pair<Long, Long>> filter_scales_num = new ArrayList<Pair<Long, Long>>();

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

	public List<Pair<String, String>> getScan_scales() {
		return scan_scales;
	}

	public void setScan_scales(List<Pair<String, String>> scan_scales) {
		this.scan_scales = scan_scales;
	}

	public List<Pair<Long, Long>> getScan_scales_num() {
		return scan_scales_num;
	}

	public void setScan_scales_num(List<Pair<Long, Long>> scan_scales_num) {
		this.scan_scales_num = scan_scales_num;
	}

	public List<Pair<Pair<String, String>, Pair<String, String>>> getCommunitys() {
		return communitys;
	}

	public void setCommunitys(
			List<Pair<Pair<String, String>, Pair<String, String>>> communitys) {
		this.communitys = communitys;
	}

	public List<Pair<Pair<Long, Long>, Pair<String, String>>> getCommunitys_num() {
		return communitys_num;
	}

	public void setCommunitys_num(
			List<Pair<Pair<Long, Long>, Pair<String, String>>> communitys_num) {
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
