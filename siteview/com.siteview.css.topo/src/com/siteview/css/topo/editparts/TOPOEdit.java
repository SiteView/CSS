package com.siteview.css.topo.editparts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.siteview.css.topo.models.TopologyModel;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.pojo.DevicePro;

public class TOPOEdit extends OPIEditor {
	/** ���궨λ */
	final String TOP_LEFT = "TOP_LEFT";
	final String TOP = "TOP";
	final String TOP_RIGHT = "TOP_RIGHT";
	final String LEFT = "LEFT";
	final String RIGHT = "RIGHT";
	final String BOTTOM_LEFT = "BOTTOM_LEFT";
	final String BOTTOM = "BOTTOM";
	final String BOTTOM_RIGHT = "BOTTOM_RIGHT";

	ReadAndCreate red = new ReadAndCreate();
	/** ģ�ͽڵ� */
	TopologyModel[] model = new TopologyModel[13];
	/** ����ģ�� */
	ConnectionModel[] cModels = new ConnectionModel[model.length];
	/** �������� */
	List testList = new ArrayList();
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
	private String s2[];
	private String s4[];
	private int x;
	private int y;
	private int x1;
	private int y1;
	protected void createGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
		DisplayModel displayModel = getDisplayModel();

		for (int i = 0; i < model.length; i++) {
			// ���� i��ģ��
			model[i] = new TopologyModel();
			// ����i��������ģ��
			cModels[i] = new ConnectionModel(displayModel);
			// ����ģ������
			getDisplayModel().addChild(model[i]);
		}
		// String ip = "192.168.0.1-192.168.0.152,192.168.0.1-192.168.0.156";

		String t = "0-1,0-2,0-3,1-4,1-5,1-6,2-7,2-8,2-9,3-10,3-11,3-12";
		String test[] = t.split(",");
		for (int i = 0; i < test.length; i++) {
			testList.add(test[i]);
		}

		// ��ͷ��
		red.DrawHead();
		// ������
		for (int i = 0; i < model.length; i++) {// model[i].getWUID()
												// 6.model[i].getName()
												// ��getWUID��Ϊ�豸���
			red.Drawnode(482, 84, 122, 122, i, i, "rectangle", "#FFCC00",
					"#000000");
		}

		
		//int x=0, y=0, x1=0, y1=0;
		String xy = "";
		String x1y1 = "";
		
		List list = red.allInfo();
		Iterator info = list.iterator();

		for (int i = 0; i < list.size(); i++) {
			String s  = (String) info.next();
			String str[] = s.split(",");
			s2 = str[0].split(":");
			s4 = str[1].split(":");
			String s3[] = s2[1].split("-");
			String s5[] = s4[1].split("-");
			x = Integer.parseInt(s3[0]);
			y =Integer.parseInt(s3[1]);
			x1=Integer.parseInt(s5[0]);
			y1=Integer.parseInt(s5[1]);
			
			// �ж����·�
			if (x - x1 <= 2 * TopologyModel.WIDTH
					&& x - x1 >= -2 * TopologyModel.WIDTH) {
				if (y - y1 > 0) {
					xy = TOP;
					x1y1 = BOTTOM;
				}
				if (y - y1 < 0) {
					xy = BOTTOM;
					x1y1 = TOP;
				}
			}
			// �ж����ҷ�
			if (y - y1 <= 2 * TopologyModel.WIDTH
					&& y - y1 >= -2 * TopologyModel.WIDTH) {
				if (x - x1 > 0) {
					xy = LEFT;
					x1y1 = RIGHT;
				}
				if (x - x1 < 0) {
					xy = RIGHT;
					x1y1 = LEFT;
				}
			}
			if (x - x1 > 2 * TopologyModel.WIDTH
					&& y - y1 > 2 * TopologyModel.WIDTH) {
				xy = TOP_LEFT;
				x1y1 = BOTTOM_RIGHT;
			}
			if (x - x1 > 2 * TopologyModel.WIDTH
					&& y - y1 < -2 * TopologyModel.WIDTH) {
				xy = BOTTOM_LEFT;
				x1y1 = TOP_RIGHT;
			}
			if (x - x1 < -2 * TopologyModel.WIDTH
					&& y - y1 > 2 * TopologyModel.WIDTH) {
				xy = TOP_RIGHT;
				x1y1 = BOTTOM_LEFT;
			}
			if (x - x1 < -2 * TopologyModel.WIDTH
					&& y - y1 < -2 * TopologyModel.WIDTH) {
				xy = BOTTOM_RIGHT;
				x1y1 = TOP_LEFT;
			}
			// ��������ģ��
			cModels[i].connect(model[Integer.parseInt(s2[0])], xy,
					model[Integer.parseInt(s4[0])], x1y1);

			// ���� cModels[j].getSource().getWUID()
			red.Drawedge(s2[0], s4[0], "��ע��Ϣ", "#000000", "standard");
		}
		System.out.println(s2[0]+"-"+x+"-"+y);
		System.out.println(s4[0]+"-"+x1+"-"+y1);
		
		
		
		
		
//		// ���������ӵı� û��ȷ������λ��
//		Iterator it = testList.iterator();// ����ģ������
//		String mStr = "";
//		String spitStr[];
//		for (int i = 0; i < test.length; i++) {
//			mStr = (String) it.next();
//			spitStr = mStr.split("-");
//			// System.out.println(spitStr[0] + "-" + spitStr[1]);
//
//			/**
//			 * ����������Ҫ�������˺��gml,Ȼ���ȡ��Ӧģ�͵�xy���� �������ģ�͵����� ��ѯ��������
//			 */
//
//			// ��������ģ��
//			cModels[i].connect(model[Integer.parseInt(spitStr[0])], BOTTOM,
//					model[Integer.parseInt(spitStr[1])], TOP);
//
//			// ���� cModels[j].getSource().getWUID()
//			red.Drawedge(spitStr[0], spitStr[1], "��ע��Ϣ", "#000000", "standard");
//		}
		// ���ɽ���
		red.DrawOver();

		// ����gml��ȡģ������,������������

		Map<String, String> map = red.readNode();// �ڽ���չʾ
		Iterator its = map.keySet().iterator();
		while (its.hasNext()) {
			String key = (String) its.next();
			String value = map.get(key);
			String[] values = value.split("-");
			// System.out.println("ģ��id="+key+"\n"+"ģ��Xֵ="+values[0]+"    ģ��Yֵ="+values[1]);
			// ��������
			model[Integer.parseInt(key)].setX(Integer.parseInt(values[0]));
			model[Integer.parseInt(key)].setY(Integer.parseInt(values[1]));
			if (Integer.parseInt(key) == 0) {
				model[0].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Blue.bmp"), FILE_EXTENSIONS));
			} else if (Integer.parseInt(key) == 1 || Integer.parseInt(key) == 2
					|| Integer.parseInt(key) == 3 || Integer.parseInt(key) == 4) {
				model[1].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[2].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[3].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[4].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[5].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[6].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[7].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[8].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[9].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[10].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[11].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[12].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
			}
		}

		// ����opi
		red.load();// ����opi
	}

	/**
	 * �ж� ���� �ַ����ȴ�С
	 */
	public int DrawWidth(String name, int number) {
		int width = name.length() * number;
		if (name.charAt(0) > 0 && name.charAt(0) < 127) {
			width = name.length() * number / 2;
		}
		return width;
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

	/*
	 * {
	 * 
	 * Map<String, String> map1 = red.readNode();//�ڽ���չʾ Iterator its1 =
	 * map1.keySet().iterator(); int x,y,x1,y1; String xy = ""; String x1y1="";
	 * String key = (String) its1.next(); String value = map1.get(key); String[]
	 * values = value.split("-"); // String value1[] = values[0].split("[.]");
	 * // String value2[] = values[1].split("[.]");
	 * System.out.println("ģ��id="+key
	 * +"\n"+"ģ��Xֵ="+values[0]+"    ģ��Yֵ="+values[1]); if (key == spitStr[0]) {
	 * x=Integer.parseInt(values[0]); y=Integer.parseInt(values[1]); } if (key
	 * == spitStr[1]) { x1=Integer.parseInt(values[0]);
	 * y1=Integer.parseInt(values[1]); }
	 *//**
	 * �����������ģ�͵�����spitStr[0] spitStr[1] �����0-1 ���0������x=380 y=578 ���1������x=187
	 * y=913 ��� ��ô��������Ϊleft_buttom=====right_top
	 */
//
//		{
//			int x, y, x1, y1;
//			String xy = "";
//			String x1y1 = "";
//			/**
//			 * �����������ģ�͵�����spitStr[0] spitStr[1] �����0-1 ���0������x=380 y=578
//			 * ���1������x=187 y=913 ��� ��ô��������Ϊleft_buttom=====right_top
//			 */
//			x = 380;
//			y = 578;
//			x1 = 187;
//			y1 = 913;
//			// �ж����·�
//			if (x - x1 <= 2 * TopologyModel.WIDTH
//					&& x - x1 >= -2 * TopologyModel.WIDTH) {
//				if (y - y1 > 0) {
//					xy = TOP;
//					x1y1 = BOTTOM;
//				}
//				if (y - y1 < 0) {
//					xy = BOTTOM;
//					x1y1 = TOP;
//				}
//			}
//			// �ж����ҷ�
//			if (y - y1 <= 2 * TopologyModel.WIDTH
//					&& y - y1 >= -2 * TopologyModel.WIDTH) {
//				if (x - x1 > 0) {
//					xy = LEFT;
//					x1y1 = RIGHT;
//				}
//				if (x - x1 < 0) {
//					xy = RIGHT;
//					x1y1 = LEFT;
//				}
//			}
//			if (x - x1 > 2 * TopologyModel.WIDTH
//					&& y - y1 > 2 * TopologyModel.WIDTH) {
//				xy = TOP_LEFT;
//				x1y1 = BOTTOM_LEFT;
//			}
//			if (x - x1 > 2 * TopologyModel.WIDTH
//					&& y - y1 < -2 * TopologyModel.WIDTH) {
//				xy = LEFT;
//				x1y1 = TOP_RIGHT;
//			}
//			if (x - x1 < -2 * TopologyModel.WIDTH
//					&& y - y1 > 2 * TopologyModel.WIDTH) {
//				xy = TOP_RIGHT;
//				x1y1 = BOTTOM_LEFT;
//			}
//			if (x - x1 < -2 * TopologyModel.WIDTH
//					&& y - y1 < -2 * TopologyModel.WIDTH) {
//				xy = BOTTOM_RIGHT;
//				x1y1 = TOP_LEFT;
//			}
//		}
}