package com.siteview.css.topo.editparts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadAndCreate {

	private Map<String, String> map; // 坐标
	private List list;
	String filename = "C:\\拓扑图\\css3.gml";

	// 生成OPI
	private StringBuffer strContext = new StringBuffer("");
	private StringBuffer strGml = new StringBuffer("");

	/**
	 * 读取节点
	 */
	public Map<String, String> readNode() {
		map = new HashMap();
		Reader reader = null;
		try {
			// 一次读一个字符
			reader = new InputStreamReader(new FileInputStream(filename));
			int tempchar;
			StringBuffer d = new StringBuffer("");
			while ((tempchar = reader.read()) != -1) {
				if (((char) tempchar) != '\r' && ((char) tempchar) != '\t'
						&& ((char) tempchar) != '\n') {
					d.append((char) tempchar);
				}
			}
			reader.close();

			Pattern pattern = Pattern.compile("node\\[.*\\]");
			Matcher matcher = pattern.matcher(d.toString().replaceAll(" ", ""));
			if (matcher.find()) {
				// 要获得1.起点2.终点3.哪个label4.以及备注信息
				String[] spitNode = matcher.group(0).split("node");
				if (spitNode != null) {
					for (String str : spitNode) {
						if (str.indexOf("id") == -1)
							continue;
						// System.out.println(str);
						String key = str.substring(str.indexOf("id") + 2,
								str.indexOf("label"));
						String x1 = str.substring(
								(str.indexOf("graphics") + 9),
								str.indexOf("type"));
						int x = (int) Float.parseFloat(x1.substring(
								x1.indexOf("x") + 1, x1.indexOf("y")));
						int y = (int) Float.parseFloat(x1.substring(
								x1.indexOf("y") + 1, x1.indexOf("w")));
						// System.out.println(x1);
						map.put(key, x + "-" + y);
					}
				}
			}
			// Iterator iterator = map.keySet().iterator();
			// while (iterator.hasNext()) {
			// String key = (String) iterator.next();
			// String value=map.get(key);
			// System.out.println(key+value);
			// }
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 读取边
	 * 
	 * @return
	 */
	public List readEdge() {
		list = new ArrayList();
		Reader reader = null;
		try {
			// 一次读一个字符
			reader = new InputStreamReader(new FileInputStream(filename));
			int tempchar;
			StringBuffer d = new StringBuffer("");
			while ((tempchar = reader.read()) != -1) {
				if (((char) tempchar) != '\r' && ((char) tempchar) != '\t'
						&& ((char) tempchar) != '\n') {
					d.append((char) tempchar);
				}
			}
			reader.close();

			Pattern pattern = Pattern.compile("node\\[.*\\]");
			Matcher matcher = pattern.matcher(d.toString().replaceAll(" ", ""));
			if (matcher.find()) {
				// 要获得1.起点2.终点3.哪个label4.以及备注信息
				String[] spitEdge = matcher.group(0).split("edge");
				if (spitEdge != null) {
					for (String strE : spitEdge) {
						if (strE.indexOf("source") == -1)
							continue;
						// System.out.println(strE);
						String source = strE.substring(
								strE.indexOf("source") + 6,
								strE.indexOf("target"));
						String target = strE.substring(
								strE.indexOf("target") + 6,
								strE.indexOf("label"));
						// System.out.println(source + "-" + target);
						list.add(source + "-" + target);
					}
				}
			}
			// Iterator iterator = list.iterator();
			// while (iterator.hasNext()) {
			// System.out.println(iterator.next());
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}

	
	public List allInfo(){
		List li = new ArrayList();
		Map map = readNode();
		List list = readEdge();
		Iterator xys = map.keySet().iterator();
//		while (xys.hasNext()) {
//			String key = (String) xys.next();
//			String value = (String) map.get(key);
//			String[] values = value.split("-");
//			System.out.println("获得模型坐标");
//			System.out.println("模型id=" + key + "\n" + "模型X值=" + values[0]
//					+ "    模型Y值=" + values[1]);
//		}
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			String key = (String) xys.next();
			String value = (String) map.get(key);
			String[] values = value.split("-");
//			System.out.println("获得模型坐标");
//			System.out.println("模型id=" + key + "\n" + "模型X值=" + values[0]
//					+ "    模型Y值=" + values[1]);
			
			String s = (String) iterator.next();
			String str[]= s.split("-");
			li.add(str[0]+":"+map.get(str[0])+","+str[1]+":"+map.get(str[1]));
		}
		Iterator iterator2 = li.iterator();
		while (iterator2.hasNext()) {
			System.out.println(iterator2.next());
		}
		return li;
	}
	/**
	 * 生成OPI头部
	 */
	public void CreateHead() {
		strContext
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><display typeId=\"org.csstudio.opibuilder.Display\" version=\"1.0.0\">");
		strContext
				.append("\n<auto_zoom_to_fit_all>false</auto_zoom_to_fit_all>");
		strContext.append("\n<macros>");
		strContext
				.append("\n<include_parent_macros>true</include_parent_macros>");
		strContext.append("\n</macros>");
		strContext.append("\n<wuid>21c56654:13fc1641c9e:-7fff</wuid>");
		strContext.append("\n<boy_version>3.2.0.qualifier</boy_version>");
		strContext.append("\n<scripts />");
		strContext.append("\n<show_ruler>true</show_ruler>");
		strContext.append("\n<height>600</height>");
		strContext.append("\n<name></name>");
		strContext.append("\n<snap_to_geometry>true</snap_to_geometry>");
		strContext.append("\n<show_grid>true</show_grid>");
		strContext.append("\n<background_color>");
		strContext.append("\n<color red=\"240\" green=\"240\" blue=\"240\" />");
		strContext.append("\n</background_color>");
		strContext.append("\n<foreground_color>");
		strContext.append("\n<color red=\"192\" green=\"192\" blue=\"192\" />");
		strContext.append("\n</foreground_color>");
		strContext.append("\n<widget_type>Display</widget_type>");
		strContext.append("\n<show_close_button>true</show_close_button>");
		strContext.append("\n<width>800</width>");
		strContext.append("\n<rules />");
		strContext.append("\n<show_edit_range>true</show_edit_range>");
		strContext.append("\n<grid_space>6</grid_space>");
		strContext.append("\n<auto_scale_widgets>");
		strContext.append("\n<auto_scale_widgets>false</auto_scale_widgets>");
		strContext.append("\n<min_width>-1</min_width>");
		strContext.append("\n<min_height>-1</min_height>");
		strContext.append("\n</auto_scale_widgets>");
		strContext.append("\n<actions hook=\"false\" hook_all=\"false\" />");
		strContext.append("\n<y>-1</y>");
		strContext.append("\n<x>-1</x>");
	}

	/**
	 * 创建OPI节点
	 * 
	 * @param wuid
	 * @param x
	 * @param y
	 */
	public void CreateNode(String wuid, int x, int y) {
		strContext
				.append("\n<widget typeId=\"org.csstudio.opibuilder.widgets.Image\" version=\"1.0.0\">");
		strContext.append("\n<crop_right>0</crop_right>");
		strContext.append("\n<crop_left>0</crop_left>");
		strContext.append("\n<visible>true</visible>");
		strContext.append("\n<wuid>" + wuid + "</wuid>");
		strContext.append("\n<auto_size>true</auto_size>");
		strContext.append("\n<scripts />");
		strContext.append("\n<height>33</height>");
		strContext.append("\n<name>Image</name>");
		strContext.append("\n<stretch_to_fit>false</stretch_to_fit>");
		strContext.append("\n<scale_options>");
		strContext.append("\n<width_scalable>true</width_scalable>");
		strContext.append("\n<height_scalable>true</height_scalable>");
		strContext.append("\n<keep_wh_ratio>false</keep_wh_ratio>");
		strContext.append("\n</scale_options>");
		strContext.append("\n<background_color>");
		strContext.append("\n<color red=\"240\" green=\"240\" blue=\"240\" />");
		strContext.append("\n</background_color>");
		strContext.append("\n<foreground_color>");
		strContext.append("\n<color red=\"192\" green=\"192\" blue=\"192\" />");
		strContext.append("\n</foreground_color>");
		strContext.append("\n<widget_type>Image</widget_type>");
		strContext.append("\n<enabled>true</enabled>");
		strContext.append("\n<font>");
		strContext
				.append("\n<opifont.name fontName=\"Microsoft YaHei UI\" height=\"9\" style=\"0\">Default</opifont.name>");
		strContext.append("\n</font>");
		strContext.append("\n<width>35</width>");
		strContext.append("\n<no_animation>false</no_animation>");
		strContext.append("\n<flip_horizontal>false</flip_horizontal>");
		strContext.append("\n<permutation_matrix>");
		strContext.append("\n<row>");
		strContext.append("\n<col>1.0</col>");
		strContext.append("\n<col>0.0</col>");
		strContext.append("\n</row>");
		strContext.append("\n<row>");
		strContext.append("\n<col>0.0</col>");
		strContext.append("\n<col>1.0</col>");
		strContext.append("\n</row>");
		strContext.append("\n</permutation_matrix>");
		strContext.append("\n<crop_top>0</crop_top>");
		strContext.append("\n<border_style>0</border_style>");
		strContext.append("\n<rules />");
		strContext.append("\n<flip_vertical>false</flip_vertical>");
		strContext.append("\n<crop_bottom>0</crop_bottom>");
		strContext.append("\n<degree>0</degree>");
		strContext.append("\n<border_width>1</border_width>");
		strContext.append("\n<image_file>bmp_PC_Gray.bmp</image_file>");
		strContext.append("\n<border_color>");
		strContext.append("\n<color red=\"0\" green=\"128\" blue=\"255\" />");
		strContext.append("\n</border_color>");
		strContext.append("\n<actions hook=\"false\" hook_all=\"false\" />");
		strContext.append("\n<y>" + y + "</y>");
		strContext.append("\n<tooltip></tooltip>");
		strContext.append("\n<x>" + x + "</x>");
		strContext.append("\n</widget>");
	}

	/**
	 * 创建OPI连接边
	 * @param src
	 * @param tgt
	 */
	public void CreateEdge(String src, String tgt) {
		strContext
				.append("\n<connection typeId=\"org.csstudio.opibuilder.connection\" version=\"1.0.0\">");
		strContext.append("\n<fill_arrow>true</fill_arrow>");
		strContext.append("\n<widget_type>connection</widget_type>");
		strContext.append("\n<src_term>BOTTOM</src_term>");
		strContext.append("\n<arrow_length>15</arrow_length>");
		strContext.append("\n<line_style>0</line_style>");
		strContext.append("\n<line_color>");
		strContext.append("\n<color red=\"0\" green=\"0\" blue=\"0\" />");
		strContext.append("\n</line_color>");
		strContext.append("\n<wuid>6e843118:13fbda91f83:-7fe7</wuid>");
		strContext.append("\n<tgt_term>TOP</tgt_term>");
		strContext.append("\n<arrows>0</arrows>");
		strContext.append("\n<anti_alias>true</anti_alias>");
		strContext.append("\n<router>1</router>");
		strContext.append("\n<name>connection</name>");
		strContext.append("\n<line_width>1</line_width>");
		strContext.append("\n<src_wuid>" + src + "</src_wuid>");
		strContext.append("\n<points />");
		strContext.append("\n<tgt_wuid>" + tgt + "</tgt_wuid>");
		strContext.append("\n</connection>");
	}

	/**
	 * 生成OPI结束
	 */
	private void CreateOver() {
		strContext.append("\n</display>");
		SaveFile("css.opi");
	}

	public void load() {
		// TODO Auto-generated method stub
		CreateHead();
		CreateNodeAndEdge();
		CreateOver();
	}

	/**
	 * 创建OPI节点和边
	 */
	public void CreateNodeAndEdge() {
		Map map = readNode();
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {// 创建边
			String key = (String) iterator.next();
			String value = (String) map.get(key);
			String spit[] = value.split("-");
			// System.out.println(key + value);
			CreateNode(key, Integer.parseInt(spit[0]),
					Integer.parseInt(spit[1]));
		}
		Iterator iterator2 = list.iterator();
		while (iterator2.hasNext()) {
			String str = (String) iterator2.next();
			String value[] = str.split("-");
			CreateEdge(value[0], value[1]);
			// System.out.println(value[0]+value[1]);
		}
	}
	
	
	/**
	 * 生成gml头部
	 */
	public void DrawHead() {
		strGml.append("Creator	\"yFiles\" ");
		strGml.append("\nVersion	\"2.8\" ");
		strGml.append("\ngraph ");
		strGml.append("\n[\n\thierarchic	1");
		strGml.append("\n\tlabel	\"\"");
		strGml.append("\n\tdirected	1 \n");
	}
	
	/**
	 * Task:生成gml节点
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param id
	 * @param name
	 * @param type
	 * @param color
	 * @param outline
	 */
	public void Drawnode(int x, int y, int w, int h, int id, int name,
			String type, String color, String outline) {
		strGml.append("\t node\n \t[\n");
		strGml.append("\t\t id \"" + id + "\"\n");
		strGml.append("\t\t label \"" + name + "\"\n");
		strGml.append("\t\t graphics\n \t\t[ \n");
		strGml.append("\t\t\t x " + x + "\n");
		strGml.append("\t\t\t y " + y + "\n");
		strGml.append("\t\t\t w " + w + "\n");
		strGml.append("\t\t\t h " + h + "\n");
		strGml.append("\t\t\t type \"" + type + "\"\n");
		strGml.append("\t\t\t fill \"" + color + "\"\n");
		strGml.append("\t\t\t outline \"" + outline + "\"\n");
		strGml.append("\t\t ] \n");
		strGml.append("\t\t LabelGraphics\n \t\t [ \n");
		strGml.append("\t\t\t text \"" + name + "\"\n");//标识路由ip
		strGml.append("\t\t\t fontSize 12\n");
		strGml.append("\t\t\t fontName \"Dialog\"\n");
		strGml.append("\t\t\t anchor \"c\"\n");//mac地址
		strGml.append("\t\t ] \n");
		strGml.append("\t ] \n");
	}
	
	/**
	 * Task:生成gml边
	 * 
	 * @param source
	 * @param target
	 * @param name
	 * @param color
	 * @param targetArrow
	 */
	public void Drawedge(String source, String target, String name,
			String color, String targetArrow) {
		// name="";
		strGml.append("\t edge\n \t[\n");
		strGml.append("\t\t source \"" + source + "\"\n");
		strGml.append("\t\t target \"" + target + "\"\n");
		strGml.append("\t\t label \"" + name + "\"\n");
		strGml.append("\t\t graphics\n \t\t[ \n");
		strGml.append("\t\t\t fill \"" + color + "\"\n");
		strGml.append("\t\t\t targetArrow \"" + targetArrow + "\"\n");
		strGml.append("\t\t ] \n");
		strGml.append("\t\t LabelGraphics\n \t\t [ \n");
		strGml.append("\t\t\t text \"" + name + "\"\n");
		strGml.append("\t\t\t fontSize 12\n");
		strGml.append("\t\t\t fontName \"Dialog\"\n");
		strGml.append("\t\t\t model \"six_pos\"\n");
		strGml.append("\t\t\t position \"tail\"\n");
		strGml.append("\t\t ] \n");
		strGml.append("\t ] \n");
	}
	
	/**
	 * 生成gml结束
	 */
	public void DrawOver() {
		strGml.append("] \n");
		SaveFile("css.gml");
	}
	
	/**
	 * 保存文件
	 */
	public void SaveFile(String file){
		// 设置一个默认文件夹路径
		String path = "C:\\拓扑图\\";
		File fileUpdate = new File(path);
		if (fileUpdate.exists() == false) {
			fileUpdate.mkdirs();
		} else {
			System.out.println("文件路径存在:" + path);
		}
		// 创建gml文件
		File f = new File(path + file);
		OutputStream out = null;
		try {
			out = new FileOutputStream(f);
			byte b[] = strGml.toString().getBytes();
			for (int i = 0; i < b.length; i++) {
				out.write(b[i]);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
