package com.siteview.snmp.scan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.text.View;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;

import com.siteview.css.topo.editparts.TOPOEdit;
import com.siteview.snmp.common.AuxParam;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.constants.CommonDef;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Bgp;
import com.siteview.snmp.pojo.DevicePro;
import com.siteview.snmp.pojo.Directitem;
import com.siteview.snmp.pojo.Edge;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.pojo.IfRec;
import com.siteview.snmp.pojo.RouteItem;
import com.siteview.snmp.pojo.RouterStandbyItem;
import com.siteview.snmp.util.IoUtils;
import com.siteview.snmp.util.PropertiesUtils;
import com.siteview.snmp.util.ScanUtils;
import com.siteview.snmp.util.Utils;

/**
 * ����ɨ��
 * @author haiming.wang
 *
 */
public class NetScan implements Runnable {
	private Map<String, Map<String, List<String>>> aft_list = new ConcurrentHashMap<String, Map<String, List<String>>>();
	// �豸ARP�����б� {sourceIP,{infInx,[(MAC,destIP)]}}
	private Map<String, Map<String, List<Pair<String, String>>>> arp_list = new ConcurrentHashMap<String, Map<String, List<Pair<String, String>>>>();
	// �豸�ӿ������б�
	// {devIP,(ifAmount,[(ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)])}
	private Map<String, Pair<String, List<IfRec>>> ifprop_list = new ConcurrentHashMap<String, Pair<String, List<IfRec>>>();
	// �豸OSPF�ھ��б� {sourceIP,{infInx,[destIP]}}
	private Map<String, Map<String, List<String>>> ospfnbr_list = new ConcurrentHashMap<String, Map<String, List<String>>>();
	private List<Bgp> bgp_list = new ArrayList<Bgp>();
	// �豸·�ɱ� {sourceIP,{infInx,[nextIP]}}
	private Map<String, Map<String, List<RouteItem>>> route_list = new ConcurrentHashMap<String, Map<String, List<RouteItem>>>();
	private Map<String, Map<String, List<RouteItem>>> rttbl_list = new ConcurrentHashMap<String, Map<String, List<RouteItem>>>();
	private List<Pair<SnmpPara, Pair<String, String>>> sproid_list = new ArrayList<Pair<SnmpPara, Pair<String, String>>>();
	private Map<String, RouterStandbyItem> routeStandby_list = new ConcurrentHashMap<String, RouterStandbyItem>();
	private Map<String, Map<String, String>> special_oid_list = new ConcurrentHashMap<String, Map<String, String>>();
	private Map<String, List<Directitem>> directdata_list = new ConcurrentHashMap<String, List<Directitem>>();
	private Map<String, List<String>> stp_list = new ConcurrentHashMap<String, List<String>>();
	private List<Edge> topo_edge_list = new ArrayList<Edge>();
	// �淶������豸AFT��ARP���� {sourceIP,{infInx,[destIP]}}
	private Map<String, Map<String, List<String>>> frm_aftarp_list = new ConcurrentHashMap<String, Map<String, List<String>>>();
	private Map<String, Map<String, List<String>>> aft_list_frm = new ConcurrentHashMap<String, Map<String, List<String>>>();
	private List<List<String>> rtpath_list = new ArrayList<List<String>>();
	private Map<String, IDBody> topo_entity_list = new HashMap<String, IDBody>();
	private IWorkbench workbench;
	
	public List<Edge> getTopo_edge_list() {
		return topo_edge_list;
	}
	public void setTopo_edge_list(List<Edge> topo_edge_list) {
		this.topo_edge_list = topo_edge_list;
	}
	public Map<String, IDBody> getTopo_entity_list() {
		return topo_entity_list;
	}
	public void setTopo_entity_list(Map<String, IDBody> topo_entity_list) {
		this.topo_entity_list = topo_entity_list;
	}
	public void stop(){
		siReader.stop();
	}
	// �淶������豸AFT��ARP���� {sourceIP,{infInx,[destIP]}}
	public void init(ScanParam sp, AuxParam ap) {
		this.scanParam = sp;
		this.myParam = ap;
		siReader.init(ap);
	}

	public NetScan() {

	}
	public NetScan(Map<String, DevicePro> devtypemap,
			Map<String, Map<String, String>> specialoidlist, ScanParam param) {
		this.workbench = workbench;
		if(param == null){
			if(!IoUtils.readConfigDate(scanParam)){
				//��ʼ��ɨ�����ʧ��
			}
		}else{
			scanParam = param;
		}
		getLocalhostIPs();
		// ReadConfig(param);
		readMyScanConfigFile();
		siReader = new ReadService(devtypemap, scanParam, myParam, specialoidlist);
	}

	public void readMyScanConfigFile() {
		myParam = new AuxParam();
		// myParam.scan_type = "1"; //ɨ������
		myParam.setScan_type("0"); // ɨ������
		myParam.setSeed_type("0");// ���ӷ�ʽ 
		myParam.setPing_type("1");// ִ��ping
		myParam.setComp_type("1");// ��������
		// myParam.dumb_type = "0"; //����dumb
		myParam.setDumb_type("1"); // ����dumb
		myParam.setArp_read_type("0");// ����ȡ2�㽻������arp����
		myParam.setNbr_read_type("0");// ����ȡ�ھӱ�
		myParam.setRt_read_type("0");// ����ȡ·�ɱ� �ָ�·�ɱ� 
		myParam.setVrrp_read_type("0");// ����ȡVRRP,HSRP 
		myParam.setBgp_read_type("0");
		myParam.setSnmp_version("0");// ����ӦSNMP�汾
		myParam.setTracert_type("0");// ��ִ��trace route
		myParam.setFilter_type("0"); // �����ɨ�跶Χ���ip 
		myParam.setCommit_pc("1"); // �ύPC��SVDB 

		PropertiesUtils.load(IoUtils.getPlatformPath() + "scanconfig.properties");
		myParam.setScan_type(PropertiesUtils.getValue("SCAN_TYPE"));
		myParam.setSeed_type(PropertiesUtils.getValue("SEED_TYPE"));
		myParam.setPing_type(PropertiesUtils.getValue("PING_TYPE"));
		myParam.setArp_read_type(PropertiesUtils.getValue("ARP_READ_TYPE"));
		myParam.setComp_type(PropertiesUtils.getValue("COMP_TYPE"));
		myParam.setDumb_type(PropertiesUtils.getValue("DUMB_TYPE"));
		myParam.setNbr_read_type(PropertiesUtils.getValue("NBR_READ_TYPE"));
		myParam.setRt_read_type(PropertiesUtils.getValue("RT_READ_TYPE"));
		myParam.setVrrp_read_type(PropertiesUtils.getValue("VRRP_READ_TYPE"));
		myParam.setBgp_read_type(PropertiesUtils.getValue("BGP_READ_TYPE"));
		myParam.setTracert_type(PropertiesUtils.getValue("TRACERT_TYPE"));
		myParam.setFilter_type(PropertiesUtils.getValue("FILTER_TYPE"));
		myParam.setCommit_pc(PropertiesUtils.getValue("COMMIT_PC"));
		myParam.setSnmp_version(PropertiesUtils.getValue("SNMP_VERSION"));
		PropertiesUtils.clear();
		myParam.getSNMPV_list().clear();
		if ("1".equals(myParam.getSnmp_version())) {
			// ��SNMPV2_List.txt��ȡ����Ҫ��SNMPV2̽��SNMP��Ϣ���ض��豸
		} else if ("2".equals(myParam.getSnmp_version())) {
			// ��SNMPV1_List.txt��ȡ����Ҫ��SNMPV1̽��SNMP��Ϣ���ض��豸
		}
	}

	@Override
	public void run() {
		scan();
	}

	public static void maian(String[] args) {
		String tmp = "1123";
		String tmp1 = "000010101010";
		tmp1 = tmp1.replaceAll("^(0+)", "");
		System.out.println("tmp1 = " + tmp1);
	}

	public static void main(String[] args) {
		AuxParam auxParam = new AuxParam();
		auxParam.setScan_type("0");
		auxParam.setPing_type("0");
		auxParam.setSeed_type("0");
		auxParam.setSnmp_version("2");
		auxParam.setNbr_read_type("1");
		auxParam.setBgp_read_type("1");
		auxParam.setVrrp_read_type("1");

		ScanParam s = new ScanParam();
		s.setCommunity_get_dft("public");
		s.setDepth(5);
		s.getScan_seeds().add("192.168.0.248");
		s.getScan_seeds().add("192.168.0.251");
			
		Map<String, Map<String, String>> special_oid_list = new ConcurrentHashMap<String, Map<String, String>>();
		NetScan scan = new NetScan(null, special_oid_list, s);
		// scan.init(s, auxParam);
		Thread thread = new Thread(scan);
		thread.setDaemon(true);
		thread.start();
		try {
			thread.sleep(1200000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// ����ip��ַ��ɨ��
	public void scanByIplist() {
		Vector<String> aliveIp_list = new Vector<String>();
		if ((!IoUtils.readDeviceIpList(aliveIp_list)) || aliveIp_list.isEmpty()) {
			System.out.println("There none device ip: File DeviceIps.txt is not existed or No ips.");
			return;
		}
		scanByIps(aliveIp_list, false);
		devid_list = siReader.getDevid_list_valid();
	}

	public void scan() {
//		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
//		try {
//			workbench.getActiveWorkbenchWindow().getActivePage().showView(TOPOEdit.ID);
//		} catch (PartInitException e) {
//			e.printStackTrace();
//		}
//		for(IViewDescriptor id : views){
//			System.out.println(id.getId());
//			if(id.getId().equals(TOPOEdit.ID)){
//				System.out.println(TOPOEdit.ID);
//			}
//		}
		long start = System.currentTimeMillis();
		System.out.println("asdf");
		System.out.println("scan start");
		String msg = "";
		devid_list.clear();
		ifprop_list.clear();
		aft_list_frm.clear();
		frm_aftarp_list.clear();
		ospfnbr_list.clear();
		rttbl_list.clear();
		directdata_list.clear();
		m_ip_list_visited.clear();
		if ("1".equals(myParam.getScan_type())
				|| "2".equals(myParam.getScan_type())) {// ��ȡԴ���ݣ��ӱ��������ͼ
			readOriginData();
		}
		if ("0".equals(myParam.getScan_type())
				|| "2".equals(myParam.getScan_type())) {// ����ȫ��ɨ��
			if ("2".equals(myParam.getPing_type())) {
				// scan by ips;
				scanByIplist();
			} else {
				if (!scanParam.getScan_scales().isEmpty()) {// ����Χɨ��

				} else {// ������ɨ��
					List<String> seeds_cur = new ArrayList<String>();
					seeds_cur = scanParam.getScan_seeds();
					if (seeds_cur.isEmpty()) {
						// log
						return;
					}
					if ("0".equals(myParam.getSeed_type())) {// ����������ɨ��
						scanBySeeds(scanParam.getScan_seeds());
					} else {// ��arp����ɨ��

					}
				}
			}

			// ���ӿڱ��е�MAC��ӵ��豸��mac��ַ����
			Set<String> keys = devid_list.keySet();
			for (String key : keys) {
				System.out.print("\t key is " + key);
				Pair<String, List<IfRec>> iinf = ifprop_list.get(key);// .find(i->first);
				IDBody i = devid_list.get(key);
				if (iinf != null)
				{
					List<IfRec> ifrec = iinf.getSecond();
					for (IfRec j : ifrec) {
						if (!Utils.isEmptyOrBlank(j.getIfMac())
								&& !j.getIfMac().equals("000000000000")
								&& !j.getIfMac().equals("FFFFFFFFFFFF")) {
							if (!i.getMacs().contains(j.getIfMac())) {
								i.getMacs().add(j.getIfMac());
							}
						}
					}
				}
			}
			saveOriginData();
		}
		System.out.println("scan end");
		long end = System.currentTimeMillis();
		System.out.println("��ʱ" + (end - start));
		formatData();
		saveFormatData();


		if (devid_list.isEmpty()) {
			topo_edge_list.clear();
		} else if (existNetDevice(devid_list)) {
			TopoAnalyse analyse = new TopoAnalyse(devid_list, ifprop_list,
					aft_list_frm, frm_aftarp_list, ospfnbr_list, rttbl_list,
					bgp_list, directdata_list, myParam);
			if (analyse.edgeAnalyse()) {
				topo_edge_list = analyse.getTopo_edge_list();
				if ((myParam.getTracert_type().equals("1"))
						&& (TraceAnalyse.getConnection(topo_edge_list) > 1)) {
					TraceReader traceR = new TraceReader(devid_list, bgp_list,
							scanParam.getRetrytimes(), scanParam.getTimeout(),
							30);
					traceR.tracePrepare();
					if ("0".equals(myParam.getScan_type())) {
						rtpath_list = traceR.getTraceRouteByIPs();
						// ��trace path ���浽�ļ�
						IoUtils.saveTracertList(rtpath_list);
						TraceAnalyse traceA = new TraceAnalyse(devid_list,
								rtpath_list, traceR.routeDESTIPPairList,
								traceR.unManagedDevices, scanParam);
						traceA.analyseRRByRtPath(topo_edge_list);
					} else {
						// ���ļ�ɨ��ʱ
						TraceAnalyse traceA = new TraceAnalyse(devid_list,
								rtpath_list, traceR.routeDESTIPPairList,
								traceR.unManagedDevices);
						traceA.analyseRRByRtPath_Direct(topo_edge_list);
					}
				}
				// ����ߵĸ�����Ϣ
				fillEdge(topo_edge_list);
				// �������豸
				generateDumbDevice(topo_edge_list, devid_list);
				topo_entity_list = devid_list;// ����������豸
				if("0".equals(myParam.getCommit_pc())){
					for(Entry<String, IDBody> j : devid_list.entrySet()){
						if("5".equals(j.getValue().getDevType())){
							topo_entity_list.put(j.getKey(), j.getValue());
						}
					}
				}else{
					topo_entity_list = devid_list;
				}
			} else {
				// ����ʧ����־
			}
		}
		long theend = System.currentTimeMillis();
		System.out.println("is end by " + (theend - start));
//		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
//		try {
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TOPOEdit.ID);
//		} catch (PartInitException e) {
//			MessageDialog.openError(shell, "����", "������ͼʧ��");
//			e.printStackTrace();
//		}//.openEditor(input, "");
	}

	// �������豸
	public int generateDumbDevice(List<Edge> edge_list,
			Map<String, IDBody> device_list) {
		if ("0".equals(myParam.getDumb_type()) || edge_list.isEmpty()) {
			// ���������豸
			return 0;
		}
		int amountAdded = 0;
		List<Edge> edge_list_dumb = new ArrayList<Edge>();
		Map<String, IDBody> device_list_dumb = new HashMap<String, IDBody>();
		Edge i_end = edge_list.get(edge_list.size() - 1);
		Edge j_start = new Edge();
		for (Edge i : edge_list) {
			if (i.getIp_left().substring(0, 4).equals("DUMB")) {
				continue;
			}
			IDBody iii = device_list.get(i.getIp_right());
			if (iii != null && !iii.getDevType().equals("5")
					&& !iii.getDevType().equals("4")) {
				// ֻ�������ͷ��������dumb
				continue;
			}
			j_start = i;
			String dumbIp = "";
			IDBody dumbBody = new IDBody();
			for (Edge j : edge_list) {
				IDBody iiii = device_list.get(j.getIp_right());
				if (iiii != null && !iiii.getDevType().equals("5")
						&& !iiii.getDevType().equals("4")) {
					continue;
				}
				if (j.getIp_left().equals(i.getIp_left())
						&& j.getInf_left().equals(i.getInf_left())) {
					// �����豸��ͬ
					if ("".equals(dumbIp)) {
						// ������豸
						dumbIp = "DUMB" + amountAdded;
						dumbBody.setBaseMac("");
						dumbBody.setDevType("6");// �����豸
						dumbBody.setSysOid("HUB");
						dumbBody.setSysName("dumb device");
						device_list_dumb.put(dumbIp, dumbBody);
						amountAdded++;
					}
					// �ı�ߵĹ�����ϵ
					j.setIp_left(dumbIp);
					j.setDsc_left("");
					j.setInf_left("0");
					j.setPt_left("0");
				}
			}
			if (!dumbIp.equals("")) {
				// ���һ����
				Edge edge_tmp = new Edge();
				edge_tmp.setIp_left(i.getIp_left());
				edge_tmp.setInf_left(i.getInf_left());
				edge_tmp.setPt_left(i.getPt_left());
				edge_tmp.setDsc_left(i.getDsc_left());
				edge_tmp.setIp_right(dumbIp);
				edge_tmp.setInf_right("0");
				edge_tmp.setPt_right("0");
				edge_tmp.setDsc_right("");
				edge_list_dumb.add(edge_tmp);
				// ���ĵ�һ���ߵ������Ϣ
				i.setIp_left(dumbIp);
				i.setDsc_left("");
				i.setInf_left("0");
				i.setPt_left("0");
			}
		}
		for (Edge i : edge_list_dumb) {
			edge_list.add(i);
		}
		for (Entry<String, IDBody> i : device_list_dumb.entrySet()) {
			device_list.put(i.getKey(), i.getValue());
		}
		return amountAdded;
	}

	public void fillEdge(List<Edge> edge_list) {
		if (edge_list.isEmpty()) {
			return;
		}
		for (Edge i : edge_list) {
			// ������߶˿�
			if (i.getInf_left().equals("PX")) {
				i.setInf_left("0");
			} else {
				Pair<String, List<IfRec>> iinf = ifprop_list
						.get(i.getIp_left());
				if (iinf != null) {
					for (IfRec jj : iinf.getSecond()) {
						// ͨ���˿�Ѱ�Ҷ�Ӧ�Ľӿ�����
						if (jj.getIfIndex().equals(i.getInf_left())) {
							// ��Ҫ�޸Ķ˿�
							i.setPt_left(jj.getIfPort());
							i.setDsc_left(jj.getIfDesc());
							break;
						}
					}
				}
			}
			// �����ұ߶˿�
			if (!i.getPt_right().equals("PX")) {
				i.setInf_right("0");
			} else {
				Pair<String, List<IfRec>> iinf = ifprop_list.get(i
						.getIp_right());
				if (iinf != null) {
					for (IfRec jj : iinf.getSecond()) {
						// ͨ���˿�Ѱ�Ҷ�Ӧ�Ľӿ�����
						if (jj.getIfIndex().equals(i.getInf_right())) {
							// ��Ҫ�޸Ķ˿�
							i.setPt_right(jj.getIfPort());
							i.setDsc_right(jj.getIfDesc());
							break;
						}
					}
				}
			}
		}
	}

	public boolean existNetDevice(Map<String, IDBody> dev_list) {
		Set<String> keys = dev_list.keySet();
		for (String key : keys) {
			IDBody i = dev_list.get(key);
			if (i.getDevType().equals("0") || i.getDevType().equals("1")
					|| i.getDevType().equals("2") || i.getDevType().equals("3")
					|| i.getDevType().equals("4")) {
				return true;
			}
		}
		return false;
	}

	public void saveFormatData() {
		IoUtils.saveFrmDevIDList(devid_list);
		IoUtils.saveFrmAftList(aft_list_frm);
		IoUtils.saveFrmArpList(frm_aftarp_list);
	}

	// ����ɨ����ԭʼ����
	public boolean saveOriginData() // ���ٱ���ɨ����ԭʼ�ļ�
	{
		if(!myParam.getPing_type().equals("2"))
			IoUtils.savaDevidIps(devid_list);
		IoUtils.saveIDBodyData(devid_list);
		IoUtils.saveAftList(aft_list);
		IoUtils.saveArpList(arp_list);
		IoUtils.saveInfPropList(ifprop_list,"");
		IoUtils.saveOspfNbrList(ospfnbr_list);
		IoUtils.saveRouteList(rttbl_list);
		IoUtils.saveBgpList(bgp_list);
		IoUtils.saveVrrpList(routeStandby_list);
		IoUtils.saveDirectData(directdata_list);
		IoUtils.saveConfigData(scanParam);
		return true;
	}

	public boolean scanBySeeds(List<String> seedList) {
		if (scaned != null)
			scaned.clear();
		if (toscan != null)
			toscan.clear();
		// �����ӷ�������
		for (String seedIp : seedList) {
			List<Pair<String, String>> maskList = new ArrayList<Pair<String, String>>();
			/* ��ѯ���� */
			new IpAddressTableScan()
					.getIpMaskList(new SnmpPara(seedIp,
							getCommunity_Get(seedIp), scanParam.getTimeout(),
							scanParam.getRetrytimes()), maskList);
			for (int i = 0; i < maskList.size(); i++) {
				// ��������
				Pair<String, String> scale_cur = ScanUtils
						.getScaleByIPMask(maskList.get(i));
				boolean bExist = false;
				for (int j = 0; j < toscan.size(); j++) {
					String first = toscan.get(j).getFirst();
					String second = toscan.get(j).getSecond();
					if (first.equals(scale_cur.getFirst())
							&& second.equals(scale_cur.getSecond())) {
						bExist = true;
						break;
					}
				}
				if (!bExist) {
					toscan.add(scale_cur);
				}

			}
		}
		for (int depth = 0; depth < scanParam.getDepth(); depth++) {
			List<Pair<String, String>> scale_list_cur = new ArrayList<Pair<String, String>>();
			Utils.collectionCopyAll(scale_list_cur, toscan);
			// toscan.size());
			while (!scale_list_cur.isEmpty()) {
				Pair<String, String> scale_cur = scale_list_cur.remove(0);
				toscan.remove(scale_cur);
				scaned.add(scale_cur);
				if (scanOneScale(scale_cur, true)) {

				} else {
					return false;
				}
			}
		}

		devid_list = siReader.getDevid_list_visited();
		return true;
	}

	// ɨ��һ����Χ
	public boolean scanOneScale(Pair<String, String> scale, boolean bChng) {
		String msg = "start scan scale : " + scale.getFirst() + "-"
				+ scale.getSecond();
		System.out.println(msg);
		long ipnumMin = ScanUtils.ipToLong(scale.getFirst());
		long ipnumMax = ScanUtils.ipToLong(scale.getSecond()) + 1;
		Vector<String> ip_list_all = new Vector<String>();
		for (long i = ipnumMin; i < ipnumMax; ++i) {
			String ipStr = ScanUtils.longToIp(i);
			if (ipStr.length() < 7
					|| ipStr.substring(ipStr.length() - 4).equals(".255")
					|| ipStr.substring(ipStr.length() - 2) == ".0") {
				// �ų��㲥��ȱʡ��ַ
				continue;
			}
			boolean bExcluded = false;
			List<Pair<Long, Long>> scalesNum = scanParam.getFilter_scales_num();
			for (int j = 0; j < scalesNum.size(); ++j) {
				Pair<Long, Long> filterScaleNum = scalesNum.get(j);
				if (i <= filterScaleNum.getSecond()
						&& i >= filterScaleNum.getFirst()) {
					bExcluded = true;
					break;
				}
			}
			if ((!bExcluded) && !m_ip_list_visited.contains(ipStr)) {
				ip_list_all.add(ipStr);
				break;
			}
		}
		if (!ip_list_all.isEmpty()) {
			Vector<String> aliveIp_list = new Vector<String>();
			if (myParam.getPing_type().equals("1")) {
				boolean bGetLocalArp = false;
				for (String iter : localip_list) {
					String local_ip = iter;
					long ipnumLocal = ScanUtils.ipToLong(local_ip);
					if (ipnumLocal < ipnumMax && ipnumLocal >= ipnumMin) {
						// is local net;
						bGetLocalArp = true;
						break;
					}
				}
				if (!icmpPing(ip_list_all, bGetLocalArp, msg, aliveIp_list)) {
					return false;
				}

				if (!scanByIps(aliveIp_list, bChng)) {
					return false;
				}

			} else {
				if (!scanByIps(ip_list_all, bChng)) {
					return false;
				}
			}
		}
		msg = "end scan scale:" + scale.getFirst() + "-" + scale.getSecond();
		System.out.println(msg);
		return true;
	}

	public String getCommunity_Get(String ip) {
		String community_ret = scanParam.getCommunity_get_dft();
		long ipnum = ScanUtils.ipToLong(ip);
		for (Pair<Pair<Long, Long>, Pair<String, String>> i : scanParam
				.getCommunitys_num()) {
			if (ipnum >= i.getFirst().getFirst()
					&& ipnum <= i.getFirst().getSecond()) {
				return i.getSecond().getFirst();
			}
		}
		return community_ret;
	}

	// ��ȡIP��Ӧ��SNMP Version
	public String getSNMPVersion(String ip) {
		String sVersion = myParam.getSnmp_version();
		if (myParam.getSNMPV_list().isEmpty()) {
			return sVersion;
		}
		for (Pair<String, String> p : myParam.getSNMPV_list()) {
			if (ip.equals(p.getFirst())) {
				return p.getSecond();
			}
		}
		return sVersion;
	}

	public boolean scanByIps(Vector<String> aliveIp_list, boolean bChange) {
		Vector<SnmpPara> spr_list = new Vector<SnmpPara>();
		for (String aliveIp : aliveIp_list) {
			{// ���ӵ��ѷ����б�
				m_ip_list_visited.add(aliveIp);
				String ipCmt = getCommunity_Get(aliveIp);
				String snmpVer = getSNMPVersion(aliveIp);// "2" or "1" or "0"
				spr_list.add(new SnmpPara(aliveIp, ipCmt, scanParam
						.getTimeout(), scanParam.getRetrytimes(), snmpVer));
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++"  + aliveIp);
			}
		}
		siReader.setIp_visited_list(m_ip_list_visited);
		if (!siReader.getDeviceData(spr_list)) {
			return false;
		}
		m_ip_list_visited = siReader.getIp_visited_list();// ���º���ѷ���ip��ַ��
		System.out
				.println("m_ip_list_visited size() is +++++++++++++++++++++++++++++++++++++++++++++++++++++"
						+ m_ip_list_visited.size());
		for (String x : m_ip_list_visited)
			System.out
					.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
							+ x);
		Map<String, IDBody> devlist_cur = siReader.getDevid_list_valid();// �ڵ�ǰ��Χ�з��ֵ����豸

		Map<String, Map<String, List<String>>> aftlist_cur = siReader
				.getAft_list();
		Map<String, Map<String, List<Pair<String, String>>>> arplist_cur = siReader
				.getArp_list();
		Map<String, Pair<String, List<IfRec>>> inflist_cur = siReader
				.getIfprop_list();
		Map<String, Map<String, List<String>>> nbrlist_cur = siReader
				.getOspfnbr_list();
		Map<String, Map<String, List<RouteItem>>> rttbl_cur = siReader
				.getRoute_list();

		List<Bgp> bgplist_cur = siReader.getBgp_list();
		Map<String, RouterStandbyItem> vrrplist_cur = siReader
				.getRouteStandby_list();

		Map<String, List<Directitem>> directlist_cur = siReader
				.getDirectdata_list();

		if (!aftlist_cur.isEmpty()) {
			Utils.mapAddAll(aft_list, aftlist_cur);
		}
		if (!arplist_cur.isEmpty()) {
			Utils.mapAddAll(arp_list, arplist_cur);
		}
		if (!inflist_cur.isEmpty()) {
			Utils.mapAddAll(ifprop_list, inflist_cur);
		}
		if (!nbrlist_cur.isEmpty()) {
			Utils.mapAddAll(ospfnbr_list, nbrlist_cur);
		}
		if (!rttbl_cur.isEmpty()) {
			Utils.mapAddAll(rttbl_list, rttbl_cur);
		}
		if (!bgplist_cur.isEmpty()) {
		}
		if (!vrrplist_cur.isEmpty()) {
			Utils.mapAddAll(routeStandby_list, vrrplist_cur);
		}
		if (!directlist_cur.isEmpty()) {
			Utils.mapAddAll(directdata_list, directlist_cur);
		}

		if (bChange) {// �����·�Χ
			addScaleFromDevID(devlist_cur);
		}

		return true;
	}

	// ���豸��Ϣ����µ�ɨ�跶Χ
	public void addScaleFromDevID(Map<String, IDBody> devlist) {
		Set<String> keys = devlist.keySet();
		for (String key : keys) {
			IDBody i = devlist.get(key);
			if (i.getDevType().equals(CommonDef.ROUTE_SWITCH)
					|| i.getDevType().equals(CommonDef.ROUTER)) {// r-s,r,//s
				Vector<String> jp_j = i.getIps();
				Vector<String> msk_j = i.getMsks();
				for (int jp_j_i = 0, msk_j_i = 0; jp_j_i < jp_j.size()
						&& msk_j_i < msk_j.size(); ++jp_j_i, ++msk_j_i)

				{
					if (msk_j.get(msk_j_i).isEmpty()) {
						continue;
					}
					boolean bNew = true;

					Pair<String, String> scale_j = ScanUtils
							.getScaleByIPMask(new Pair<String, String>(jp_j
									.get(jp_j_i), msk_j.get(msk_j_i)));
					for (Pair<String, String> k : toscan) {
						if (ScanUtils.isScaleBInA(k, scale_j)) {
							bNew = false;
							break;
						}
					}
					if (bNew) {
						for (Pair<String, String> k : scaned) {
							if (ScanUtils.isScaleBInA(k, scale_j)) {
								bNew = false;
								break;
							}
						}
					}
					if (bNew) {// ����������
						toscan.add(scale_j);
					}
				}
			}
		}
	}

	/** ��� */
	public boolean icmpPing(Vector<String> iplist, boolean bGetla, String msg,
			Vector<String> iplist_alive) {
		iplist_alive.clear();
		Vector<String> iplist_to_ping = new Vector<String>();
		PingHelper myPing = new PingHelper();
		int iTotal = iplist.size();
		int istart = 0, iend = 0;
		int iBatchs = (iTotal + 99) / 100;
		for (int btchs = 0; btchs < iBatchs; ++btchs) {
			iend += 100;
			if (iend > iTotal) {
				iend = iTotal;
			}
			Utils.collectionCopyAll(iplist_to_ping, iplist);
			istart = iend;
			if (!myPing.multPing(iplist_to_ping, 2, scanParam.getTimeout()))// Ping���Լ���ʱ�̶�Ϊ2��1000ms
			{
				return false;
			}
			List<String> list_tmp = myPing.getAliveIpList();
			for (int i = 0; i < list_tmp.size(); ++i) {
				iplist_alive.add(list_tmp.get(i));
			}
			if (bGetla) {// ��ȡ����arp��
				String communityStr = getCommunity_Get(localip);

				siReader.getOneArpData(new SnmpPara(localip, communityStr,
						scanParam.getTimeout(), scanParam.getRetrytimes()),
						localport_macs);
			}
		}
		return true;
	}

	// �淶�������ļ�
	public boolean formatData() {
		// ����vrrp������:ɾ��vrrp��ip-mac����
		List<String> iplist_virtual = new ArrayList<String>();
		List<String> maclist_virtual = new ArrayList<String>();
		for (Entry<String, RouterStandbyItem> iter : routeStandby_list
				.entrySet()) {
			for (String ii : iter.getValue().getVirtualIps()) {
				if (!iplist_virtual.contains(ii)) {
					iplist_virtual.add(ii);
				}
			}
			for (String ii : iter.getValue().getVirtualMacs()) {
				if (!maclist_virtual.contains(ii)) {
					maclist_virtual.add(ii);
				}
			}
		}
		Map<String, List<Directitem>> drc_list_tmp = new HashMap<String, List<Directitem>>();
		for (Entry<String, List<Directitem>> i : directdata_list.entrySet()) {
			String left_ip = i.getKey();
			for (Entry<String, IDBody> s : devid_list.entrySet()) {
				if (s.getValue().getIps().contains(left_ip)) {
					left_ip = s.getKey();
					break;
				}
			}
			// format direcdata
			for (Iterator<Directitem> it = i.getValue().iterator(); it
					.hasNext();) {
				Directitem j = it.next();
				String right_ip = j.getPeerIp();
				if (right_ip.equals("0.0.0.0")) {
					it.remove();
					continue;
				}
				if (right_ip.indexOf("\\.") == -1) {
					boolean bExist = false;
					for (Entry<String, IDBody> iter : devid_list.entrySet()) {
						if (iter.getValue().getMacs().contains(right_ip)) {
							j.setPeerIp(iter.getKey());
							bExist = true;
							break;
						}
					}
					if (!bExist) {
						it.remove();
					}
				} else {
					for (Entry<String, IDBody> s : devid_list.entrySet()) {
						if (s.getValue().getMacs().contains(right_ip)) {
							j.setPeerIp(s.getKey());
							break;
						}
					}
				}
			}
			drc_list_tmp.put(left_ip, i.getValue());
		}
		directdata_list = drc_list_tmp;

		if (("1".equals(myParam.getFilter_type()))
				&& (scanParam.getScan_scales_num() != null && !scanParam
						.getScan_scales_num().isEmpty())) {
			for (Entry<String, Map<String, List<Pair<String, String>>>> m_srcip : arp_list
					.entrySet()) {// ��source ip ѭ��
				for (Entry<String, List<Pair<String, String>>> m_srcport : m_srcip
						.getValue().entrySet()) {// ��source port ѭ��
					for (Iterator<Pair<String, String>> destip_mac_iter = m_srcport
							.getValue().iterator(); destip_mac_iter.hasNext();) {// ��destip-macѭ��
						Pair<String, String> destip_mac = destip_mac_iter
								.next();
						boolean bAllowed = false;
						long ipnum = ScanUtils.ipToLong(destip_mac.getFirst());
						for (Pair<Long, Long> j : scanParam
								.getScan_scales_num()) {
							// ����ķ�Χ
							if (ipnum <= j.getSecond() && ipnum >= j.getFirst()) {
								bAllowed = true;
								for (Pair<Long, Long> k : scanParam
										.getFilter_scales_num()) {
									// �ų��ķ�Χip
									if (ipnum <= k.getSecond()
											&& ipnum >= k.getFirst()) {
										bAllowed = false;
										break;
									}
								}
								break;
							}
						}
						if (!bAllowed) {
							destip_mac_iter.remove();
						}
					}
				}
			}
		}
		// �淶���ӿ�,
		for (Entry<String, Map<String, List<Pair<String, String>>>> i : arp_list
				.entrySet()) {// ѭ��source ip
			List<Pair<String, List<Pair<String, String>>>> infindex_list = new ArrayList<Pair<String, List<Pair<String, String>>>>();
			// ѭ��source ip

			for (Entry<String, List<Pair<String, String>>> m_it : i.getValue()
					.entrySet()) {// ��source port ѭ��
				String port = m_it.getKey();
				if (port.length() == 1
						|| (!port.startsWith("G") && !port.startsWith("E"))) {
					continue;
				}
				if (i.getValue() != null && i.getValue().size() > 0) {
					boolean bValidPort = false;
					for (Iterator<Pair<String, String>> pi = m_it.getValue()
							.iterator(); pi.hasNext();) {
						Pair<String, String> pi_second = pi.next();
						if (pi_second.getSecond().length() != 12) {
							pi.remove();
						} else {
							bValidPort = true;
						}
					}
					if (bValidPort) {
						port = port.substring(1);
						while (port.substring(0, 1).equals("0")
								&& !port.substring(0, 2).equals("0/")) {
							port = port.substring(1);
						}
						infindex_list
								.add(new Pair<String, List<Pair<String, String>>>(
										port, m_it.getValue()));// delete G / E
					}
				}
			}
			if (infindex_list != null && !infindex_list.isEmpty()) {
				i.getValue().clear();
				for (Pair<String, List<Pair<String, String>>> k : infindex_list) {
					i.getValue().put(k.getFirst(), k.getSecond());
				}
			}
		}
		for (Entry<String, Map<String, List<String>>> i : aft_list.entrySet()) {// ��source ipѭ��
			List<Pair<String, List<String>>> infindex_list = new ArrayList<Pair<String, List<String>>>();// [<oldport,[mac]>]
			List<Pair<String, List<String>>> validinfindex_list = new ArrayList<Pair<String, List<String>>>();// [<validport,[mac]>]

			for (Entry<String, List<String>> m_it : i.getValue().entrySet()) {
				String port = m_it.getKey();
				if (port.length() == 1
						|| (!port.startsWith("G") && !port.startsWith("E"))) {
					validinfindex_list.add(new Pair<String, List<String>>(port,
							m_it.getValue()));
					continue;
				}
				if (i.getValue() != null && i.getValue().size() > 0) {
					boolean bValidPort = false;
					for (Iterator<String> pi = m_it.getValue().iterator(); pi
							.hasNext();) {
						String pi_next = pi.next();
						if (pi_next.length() != 12) {
							pi.remove();
						} else {
							bValidPort = true;
						}
					}
					if (bValidPort) {
						infindex_list.add(new Pair<String, List<String>>(port,
								m_it.getValue()));
					}
				}
			}
			if (infindex_list != null && !infindex_list.isEmpty()) {
				Pair<String, List<IfRec>> iif = ifprop_list.get(i.getKey());
				if (iif != null) {
					String myPrex = "";
					for (Pair<String, List<String>> k : infindex_list) {
						List<String> str_list = new ArrayList<String>();// port
						str_list.add(k.getFirst());
						myPrex = getInfDescPrex(str_list, iif.getSecond());
						String port_inf = k.getFirst();
						port_inf = port_inf.substring(1);
						while (port_inf.substring(0, 1).equals("0")
								&& !port_inf.substring(0, 2).equals("0/")) {
							port_inf = port_inf.substring(1);
						}
						String myport = myPrex + port_inf;
						k.setFirst(findInfPortFromDescr(iif.getSecond(), myport));
					}
					i.getValue().clear();
					for (Pair<String, List<String>> k : infindex_list) {
						i.getValue().put(k.getFirst(), k.getSecond());
					}
					// �ϲ��˿ڼ�
					for (Pair<String, List<String>> port_mac : validinfindex_list) {
						// port-macs
						if (i.getValue().containsKey(port_mac.getFirst())) {// ���ڸö˿�
							for (String idestmac : port_mac.getSecond()) {
								if (!i.getValue().get(port_mac.getFirst())
										.contains(idestmac)) {// �����ڸ�mac
									i.getValue().get(port_mac.getFirst())
											.add(idestmac);
								}
							}
						} else {
							i.getValue().put(port_mac.getFirst(),
									port_mac.getSecond());
						}
					}
				}
			}
		}
		// ��arp�г��ֵ��µ�ip-mac��Ϊhost���뵽�豸�б�
		List<Pair<String, String>> ipmac_list = new ArrayList<Pair<String, String>>();
		List<String> deleteIPS = new ArrayList<String>();
		for (Entry<String, Map<String, List<Pair<String, String>>>> i : arp_list
				.entrySet()) {// ��source ip ѭ��
			for (Entry<String, List<Pair<String, String>>> m_it : i.getValue()
					.entrySet()) {// ��source port ѭ��
				for (Pair<String, String> ip_mac_new : m_it.getValue()) {
					if (maclist_virtual.contains(ip_mac_new.getSecond())) {
						continue;// ����vrrp ����ip-mac
					}
					if (ip_mac_new.getFirst().substring(0, 3).equals("127")
							|| ip_mac_new.getFirst().substring(0, 5)
									.equals("0.255")
							|| deleteIPS.contains(ip_mac_new.getFirst())) {
						continue;
					}
					boolean bNew = true;
					for (Iterator<Pair<String, String>> ip_mac_Iter = ipmac_list
							.iterator(); ip_mac_Iter.hasNext();) {
						Pair<String, String> ip_mac = ip_mac_Iter.next();
						if (ip_mac.getFirst().equals(ip_mac_new.getFirst())
								&& ip_mac.getSecond().equals(
										ip_mac_new.getSecond())) {
							bNew = false;
							break;
						}
						if (ip_mac_new.getFirst().equals(ip_mac.getFirst())
								&& !(ip_mac_new.getSecond().equals(ip_mac
										.getSecond()))) {
							bNew = false;
							deleteIPS.add(ip_mac.getFirst());
							ip_mac_Iter.remove();
							break;
						}
					}
					if (bNew) {
						ipmac_list.add(ip_mac_new);
					}
				}
			}
		}
		for (Pair<String, String> i : ipmac_list) {
			boolean bExist = false;
			IDBody iid = new IDBody();
			for (Iterator<IDBody> iter = devid_list.values().iterator(); iter
					.hasNext();) {
				iid = iter.next();
				if (iid.getIps().contains(i.getFirst())) {
					iid.setBaseMac(i.getSecond());
					bExist = true;
					break;
				}
			}
			if (!bExist) {// ��Ϊhost����
				IDBody id_tmp = new IDBody();
				id_tmp.setSnmpflag("0");
				id_tmp.setBaseMac(i.getSecond());
				id_tmp.setDevType("5");// host
				id_tmp.setDevModel("");
				id_tmp.setDevFactory("");
				id_tmp.getIps().add(i.getFirst());
				id_tmp.getMsks().add("");
				id_tmp.getInfinxs().add("0");
				id_tmp.getMacs().add(i.getSecond());
				devid_list.put(i.getFirst(), id_tmp);
			} else if (iid.getMacs() == null || iid.getMacs().isEmpty()) {// ��MAC��ַ�����豸��
				iid.getMacs().add(i.getSecond());
				iid.setBaseMac(i.getSecond());
			}
		}
		// �淶��arp���ݱ�
		frm_aftarp_list.clear();
		for (Entry<String, Map<String, List<Pair<String, String>>>> i : arp_list
				.entrySet()) {
			String src_ip = i.getKey();
			boolean bDevice = false;
			for (Entry<String, IDBody> j : devid_list.entrySet()) {
				if (j.getValue().getIps().contains(src_ip)) {
					src_ip = j.getKey();
					bDevice = true;
					break;
				}
			}
			if (!bDevice) {
				continue;
			}
			if (!frm_aftarp_list.containsKey(src_ip)) {// �����Ѿ����ڵ�src_ip
				String myPrex = "";
				List<String> infindex_list = new ArrayList<String>();
				if (i.getValue() != null && !(i.getValue().isEmpty())) {
					String temp = i.getValue().entrySet().iterator().next()
							.getKey();// ��ȡ��һ��Ԫ�صļ�
					if (temp.startsWith("G") || temp.startsWith("E")) {
						for (Entry<String, List<Pair<String, String>>> j : i
								.getValue().entrySet()) {
							String str_tmp = j.getKey().substring(1);
							if (str_tmp.length() > 1
									&& !str_tmp.substring(0, 2).equals("0/")) {
								if (str_tmp.indexOf("0") > 0) {
									str_tmp = str_tmp.replaceAll("^(0+)", "");// ȥ���ַ���ǰ���0
								}
								infindex_list.add(str_tmp);
							}
						}
					}
				}
				Pair<String, List<IfRec>> iinf = ifprop_list.get(src_ip);
				if (infindex_list != null && !infindex_list.isEmpty()
						&& iinf != null) {
					myPrex = getInfDescPrex(infindex_list, iinf.getSecond());
				}
				Map<String, List<String>> pset_tmp = new HashMap<String, List<String>>();
				for (Entry<String, List<Pair<String, String>>> j : i.getValue()
						.entrySet()) {
					String myport = j.getKey();// ȱʡ�ӿ�
					List<String> destip_list = new ArrayList<String>();
					// dest_ip->dev_ip
					for (Pair<String, String> k : j.getValue()) {
						if (iplist_virtual.contains(k.getFirst())) {
							// ����vrrp ����ip-mac
							continue;
						}
						if (deleteIPS.contains(k.getFirst())) {
							continue;
						}
						for (Entry<String, IDBody> m : devid_list.entrySet()) {
							if (m.getValue().getIps().contains(k.getFirst())) {
								// ���Բ����豸�б��е���Ŀ
								if (m.getKey().equals(src_ip)) {
									// //����ת�����������Ŀ
									break;
								}
								if (!destip_list.contains(m.getKey())) {
									destip_list.add(m.getKey());
									break;
								}
							}
						}
					}
					if (!destip_list.isEmpty() && !pset_tmp.containsKey(myport)) {
						pset_tmp.put(myport, destip_list);
					}
				}
				if (!pset_tmp.isEmpty()) {
					frm_aftarp_list.put(src_ip, pset_tmp);
				}
			}
		}
		// �淶��aft���ݱ�
		aft_list_frm.clear();
		for (Entry<String, Map<String, List<String>>> i : aft_list.entrySet()) {
			String src_ip = i.getKey();
			boolean bDevice = false;
			for (Entry<String, IDBody> j : devid_list.entrySet()) {
				if (j.getValue().getIps().contains(src_ip)) {
					src_ip = j.getKey();
					bDevice = true;
					break;
				}
			}
			if (!bDevice) {
				continue;
			}
			if (!aft_list_frm.containsKey(src_ip)) {// �����Ѿ����ڵ�src_ip
				String myPrex = "";
				List<String> infindex_list = new ArrayList<String>();
				if (i.getValue() != null && !i.getValue().isEmpty()) {
					String tmp_Myprex = i.getValue().entrySet().iterator()
							.next().getKey();
					if (tmp_Myprex.startsWith("G")
							|| tmp_Myprex.startsWith("E")) {
						for (Entry<String, List<String>> j : i.getValue()
								.entrySet()) {
							String str_tmp = j.getKey().substring(1);
							infindex_list.add(str_tmp);
						}
					}
				}
				Pair<String, List<IfRec>> iinf = ifprop_list.get(src_ip);
				if (!(ifprop_list.isEmpty()) && iinf != null) {
					myPrex = getInfDescPrex(infindex_list, iinf.getSecond());
				}
				Map<String, List<String>> pset_tmp = new HashMap<String, List<String>>();
				for (Entry<String, List<String>> j : i.getValue().entrySet()) {
					String myport = j.getKey();// ȱʡ�ӿں�
					if (iinf != null && !iinf.isEmpty()) {
						for (IfRec k : iinf.getSecond()) {
							// ͨ���˿�Ѱ�Ҷ�Ӧ�Ľӿ�����
							if (k.getIfPort().equals(myport)
									&& !k.getIfIndex().equals(myport)) {
								// ��Ҫ�޸Ķ˿�
								myport = k.getIfIndex();
								break;
							}
						}
					}
					List<String> destip_list = new ArrayList<String>();
					for (String k : j.getValue()) {
						if (maclist_virtual.contains(k)) {
							// ����vrrp ����ip-mac
							continue;
						}
						String temp = k.toUpperCase();
						for (Entry<String, IDBody> m : devid_list.entrySet()) {
							if (m.getValue().getMacs().contains(k)) {
								// ���Բ����豸�б��е���Ŀ
								if (!m.getKey().equals(src_ip)
										&& !destip_list.contains(m.getKey())) {
									// ����ת�����������Ŀ
									destip_list.add(m.getKey());
								}
								break;
							}
						}
					}
					if (!destip_list.isEmpty() && !pset_tmp.containsKey(myport)) {
						pset_tmp.put(myport, destip_list);
					}
				}
				if (!pset_tmp.isEmpty()) {
					aft_list_frm.put(src_ip, pset_tmp);
				}
			}
		}
		return true;
	}

	public String findInfPortFromDescr(List<IfRec> inf_list, String port) {
		for (IfRec m : inf_list) {// ͨ���ӿ�������ϢѰ�Ҷ�Ӧ�Ľӿ�����
			int iPlace = m.getIfDesc().indexOf(port);
			if (iPlace != -1) {// ��Ҫ�޸Ķ˿�
				return m.getIfPort();
			} else {
				String toport = port;
				iPlace = m.getIfDesc().indexOf(toport.replaceAll("/", ":"));
				if (iPlace != -1) {
					return m.getIfPort();
				}
			}
		}
		return port;
	}

	// ���ݽӿ������б���ӿڱ�,��ȡ��ͬǰ׺
	public String getInfDescPrex(List<String> infIndex_list,
			List<IfRec> inf_list) {
		String prex = "";
		List<String> prex_list = new ArrayList<String>();
		for (String i : infIndex_list) {
			String port = i;
			String d = port.substring(0, 1);
			port = port.substring(1);
			while (port.substring(0, 1) == "0" && port.substring(0, 2) != "0/") {
				port = port.substring(1);
			}

			String prex_tmp = "";
			for (IfRec j : inf_list) {
				String vlan = j.getIfDesc().toUpperCase();

				if (vlan.indexOf("VLAN") > 0
						|| !vlan.startsWith(d)
						|| (j.getIfDesc().indexOf("/") >= 0 && port
								.indexOf("/") < 0)) {
					continue;
				}
				int iPlace = j.getIfDesc().indexOf(port);
				if (iPlace != -1) {
					prex_tmp = j.getIfDesc().substring(0, iPlace);
					break;
				} else {
					iPlace = j.getIfDesc().indexOf(port.replaceAll(":", "/"));
					if (iPlace != -1) {
						prex_tmp = j.getIfDesc().substring(0, iPlace);
						break;
					}
				}
			}
			if (!Utils.isEmptyOrBlank(prex_tmp)) {
				prex_list.add(prex_tmp);
			}
		}
		int iMinLen = 100000;
		for (String i : prex_list) {
			if (i.length() < iMinLen) {
				iMinLen = i.length();
				prex = i;
			}
		}
		return prex;
	}

	public boolean getLocalhostIPs() {
		localip_list.clear();
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
			localip_list.add(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (!localip_list.isEmpty()) {
			localip = localip_list.get(0);
		} else {
			localip = "";
		}
		localport_macs.clear();
		return true;
	}
	/**
	 * ��ȡ��ʽ��֮ǰ��ԭʼ����
	 * @return
	 */
	public boolean readOriginData(){
		if(!devid_list.isEmpty()){
			devid_list.clear();
		}
		if(!aft_list.isEmpty()){
			aft_list.clear();
		}
		if(!arp_list.isEmpty()){
			arp_list.clear();
		}
		if(!ifprop_list.isEmpty()){
			ifprop_list.clear();
		}
		if(!ospfnbr_list.isEmpty()){
			ospfnbr_list.clear();
		}
		if(!rttbl_list.isEmpty()){
			rttbl_list.clear();
		}
		if(!bgp_list.isEmpty()){
			bgp_list.clear();
		}
		if(!routeStandby_list.isEmpty()){
			routeStandby_list.clear();
		}
		if(!rtpath_list.isEmpty()){
			rtpath_list.clear();
		}
		if(!directdata_list.isEmpty()){
			directdata_list.clear();
		}
		IoUtils.readIdBodyData(devid_list,"");
		if(devid_list.isEmpty()){
			for(Entry<String,IDBody> i : devid_list.entrySet()){
				IDBody devid = i.getValue();
				Utils.collectionCopyAll(m_ip_list_visited, devid.getIps());
			}
			if(myParam.getScan_type().equals("2")){
				siReader.setDevid_list_visited(devid_list);
				siReader.setIp_visited_list(m_ip_list_visited);
			}
		}
		IoUtils.readAftList(aft_list);
		IoUtils.readArpList(arp_list);
		IoUtils.readInfPropList(ifprop_list);
		IoUtils.readOspfNbrList(ospfnbr_list);
		IoUtils.readRouteList(rttbl_list);
		IoUtils.readBgpList(bgp_list);
		IoUtils.readVrrpList(routeStandby_list);
		IoUtils.readTracertList(rtpath_list);
		IoUtils.readDirectData(directdata_list);
		if(myParam.getScan_type().equals("1")){
			IoUtils.readConfigDate(scanParam);
		}
		return true;
	}

	private Map<String, List<Pair<String, String>>> localport_macs = new HashMap<String, List<Pair<String, String>>>();

	private ScanParam scanParam = new ScanParam();

	// ɨ�貹�����
	AuxParam myParam = new AuxParam();
	// ��ɨ��������
	private List<Pair<String, String>> scaned = new ArrayList<Pair<String, String>>();
	// ��ɨ������ 
	private List<Pair<String, String>> toscan = new ArrayList<Pair<String, String>>();
	// �ѷ���ip
	private List<String> m_ip_list_visited = new ArrayList<String>();
	
	private ReadService siReader = new ReadService();
	// ��������ip��ַ�б�
	private List<String> localip_list = new ArrayList<String>();
	private String localip;
	// �豸�б�
	private Map<String, IDBody> devid_list = new HashMap<String, IDBody>();

	public Map<String, List<Pair<String, String>>> getLocalport_macs() {
		return localport_macs;
	}

	public void setLocalport_macs(
			Map<String, List<Pair<String, String>>> localport_macs) {
		this.localport_macs = localport_macs;
	}

	public ReadService getSiReader() {
		return siReader;
	}

	public void setSiReader(ReadService siReader) {
		this.siReader = siReader;
	}

	public Map<String, IDBody> getDevid_list() {
		return devid_list;
	}

	public void setDevid_list(Map<String, IDBody> devid_list) {
		this.devid_list = devid_list;
	}

	public List<String> getLocalip_list() {
		return localip_list;
	}

	public void setLocalip_list(List<String> localip_list) {
		this.localip_list = localip_list;
	}

	public AuxParam getMyParam() {
		return myParam;
	}

	public void setMyParam(AuxParam myParam) {
		this.myParam = myParam;
	}

	public ScanParam getScanParam() {
		return scanParam;
	}

	public List<String> getM_ip_list_visited() {
		return m_ip_list_visited;
	}

	public void setM_ip_list_visited(List<String> visited) {
		this.m_ip_list_visited = visited;
	}

	public void setScanParam(ScanParam scanParam) {
		this.scanParam = scanParam;
	}

	public List<Pair<String, String>> getScaned() {
		return scaned;
	}

	public void setScaned(List<Pair<String, String>> scaned) {
		this.scaned = scaned;
	}

	public List<Pair<String, String>> getToscan() {
		return toscan;
	}

	public void setToscan(List<Pair<String, String>> toscan) {
		this.toscan = toscan;
	}

	public String getLocalip() {
		return localip;
	}

	public void setLocalip(String localip) {
		this.localip = localip;
	}

}
