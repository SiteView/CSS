package com.siteview.css.topo.editparts;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import com.siteview.css.topo.common.TopoData;
import com.siteview.css.topo.models.TopologyModel;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.pojo.DevicePro;
import com.siteview.snmp.pojo.Edge;

public class TOPOEdit extends OPIEditor {
	/** 坐标定位 */
	final String TOP_LEFT = "TOP_LEFT";
	final String TOP = "TOP";
	final String TOP_RIGHT = "TOP_RIGHT";
	final String LEFT = "LEFT";
	final String RIGHT = "RIGHT";
	final String BOTTOM_LEFT = "BOTTOM_LEFT";
	final String BOTTOM = "BOTTOM";
	final String BOTTOM_RIGHT = "BOTTOM_RIGHT";

	public static final String DUMB0 = "192.168.8.80";
	public static final String DUMB1 = "192.168.8.81";
	public static final String DUMB2 = "192.168.8.82";
	public static final String DUMB3 = "192.168.8.83";
	public static final String DUMB4 = "192.168.8.84";
	ReadAndCreate red = new ReadAndCreate();
	public static final String PROP_IMAGE_FILE = "image_file";
	private static final String[] FILE_EXTENSIONS = new String[] { "jpg",
			"jpeg", "gif", "bmp", "png" };
	int nnodes;
	// List list = new A
	private StringBuffer strGml = new StringBuffer("");
	public static final String ID = "com.siteview.css.topo.editparts.TOPOEdit";
	static Random rand = new Random(90);

	public TOPOEdit() {
		super();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
	}

	public void NetScan(Map<String, DevicePro> devtypemap,
			Map<String, Map<String, String>> specialoidlist, ScanParam param) {
	}

	protected void createGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
		DisplayModel displayModel = getDisplayModel();
		String leftIp;
		String rightIp;
		List ipList = new ArrayList();
		if (TopoData.isInit) {
			List list = TopoData.edgeList;
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				Edge edge = (Edge) iterator.next();
				leftIp = edge.getIp_left();
				rightIp = edge.getIp_right();
				ipList.add(leftIp + "-" + rightIp);
			}
		}

		/** 生成gml文件 */
		// 画头部
		red.DrawHead();
		// 画部件
		Map deviceList = TopoData.deviceList;// 获取扫描数据
		Iterator ii = deviceList.keySet().iterator();
		while (ii.hasNext()) {
			String key = (String) ii.next();
			red.Drawnode(482, 84, 122, 122, key, key, "rectangle", "#FFCC00",
					"#000000");
		}
		// 画边
		Iterator iterator = ipList.iterator();
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			String[] intIp = string.split("-");
			red.Drawedge(intIp[0], intIp[1], "备注信息", "#000000", "standard");
		}
		// 生成结束
		red.DrawOver();

		//TreeLayout treeLayout = new TreeLayout();
		/** 界面展示 */
//		// 测试数据
//		List testList = new ArrayList();
//		// 模型节点
//		ImageModel[] model = new ImageModel[15];
//
//		// 连接模型
//		ConnectionModel[] cModels = new ConnectionModel[model.length];
//
//		for (int i = 0; i < model.length; i++) {
//			// 创建 i个模型
//			model[i] = new ImageModel();
//			// 创建i个连接线模型
//			cModels[i] = new ConnectionModel(displayModel);
//			// 设置模型坐标
//			getDisplayModel().addChild(model[i]);
//		}
//		// 如果添加了模型。需要先在这添加 allIp
//		String t = "0-1,0-2,0-3,1-4,1-5,1-6,2-7,2-8,2-9,3-10,3-11,3-12,3-13,3-14";
//		String test[] = t.split(",");
//		for (int i = 0; i < test.length; i++) {
//			testList.add(test[i]);
//		}
//		// 设置了连接的边 没有确定具体位置
//		Iterator it = testList.iterator();// 迭代模拟数据
//		String mStr = "";
//		String spitStr[];
//		for (int i = 0; i < test.length; i++) {
//			mStr = (String) it.next();
//			spitStr = mStr.split("-");
//			cModels[i].connect(model[Integer.parseInt(spitStr[0])], BOTTOM,
//					model[Integer.parseInt(spitStr[1])], TOP);
//		}
//		// 解析gml3获取模型坐标,设置连接坐标
//		Map<String, String> map = red.readNode();// 在界面展示
//		Iterator its = map.keySet().iterator();
//		while (its.hasNext()) {
//			String key = (String) its.next();
//			String value = map.get(key);
//			String[] values = value.split("-");
//			// 设置坐标
//			model[Integer.parseInt(key)].setX(Integer.parseInt(values[0]));
//			model[Integer.parseInt(key)].setY(Integer.parseInt(values[1]));
//		}

		// 生成opi
		/**
		 * 生成正确的opi 1.解析gml3文件 2.获得坐标及其连接方位
		 */
		red.load();// 生成opi
	}

	/**
	 * @return the origin editor input before wrapped by
	 *         {@link NoResourceEditorInput}.
	 */
	public IEditorInput getOriginEditorInput() {
		IEditorInput editorInput = super.getEditorInput();
		if (editorInput instanceof NoResourceEditorInput) {
			return ((NoResourceEditorInput) editorInput).getOriginEditorInput();
		}
		return editorInput;
	}

	private InputStream getInputStream() {
		InputStream result = null;

		IEditorInput editorInput = getOriginEditorInput();

		if (editorInput instanceof FileEditorInput) {

			// try {
			// result = ((FileEditorInput) editorInput).getFile()
			// .getContents();
			// } catch (CoreException e) {
			// e.printStackTrace();
			// }
		} else if (editorInput instanceof FileStoreEditorInput) {
			IPath path = URIUtil.toPath(((FileStoreEditorInput) editorInput)
					.getURI());
			try {
				result = new FileInputStream(path.toFile());
			} catch (FileNotFoundException e) {
				result = null;
			}
		}
		return result;
	}

	public void connetDirection() {

	}

	// ========================
	private void buildLevelTree(Node v, int x) {
		if (v.getOffsetIncoming() != 0) {

		}
	}
}