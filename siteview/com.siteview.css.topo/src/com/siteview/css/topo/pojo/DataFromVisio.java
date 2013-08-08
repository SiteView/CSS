package com.siteview.css.topo.pojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DataFromVisio {

	public List<Port> ports = new ArrayList<Port>();
	public List<Lamp> lamps = new ArrayList<Lamp>();

	private static List list;

	RouterClientData rcd = null;

	Element shapeElement = null;
	Port port = null;
	Point point = null;
	Lamp lamp = null;

	long lBasedX = 0L;
	long lBasedY = 0L;
	static long constant = 2438L;
	
	long lGroupX = 0L;
	long lGroupY = 0L;
	
	long lGroupPinX = 0L;
	long lGroupPinY = 0L;

	long num1 = 0L;
	long num2 = 0L;
	long num3 = 0L;
	long num4 = 0L;
	long num5 = 0L;
	long num6 = 0L;

	// String filename = "D://1.3.6.1.4.1.2011.2.62.2.2_copy.xml";
	/**
	 * 通过从指定visio文件中获取端口号和端口灯的坐标及尺寸。
	 * 
	 * @param filename
	 */
	public void getCoordinatesForXML(String filename) {

		SAXReader saxReader = new SAXReader();

		try {
			Document document = saxReader.read(new File(filename));
			Element rootElement = document.getRootElement();

			// 获取page框构造的参数PageWidth，PageHeight，PageScale（page缩放）
			Element PagePropsElement = rootElement.element("Pages")
					.element("Page").element("PageSheet").element("PageProps");

			String PageWidth = PagePropsElement.elementText("PageWidth");
			String PageHeight = PagePropsElement.elementText("PageHeight");
			String PageScale = PagePropsElement.elementText("PageScale");
			System.out.println(PageScale + " , " + PageWidth + " , "
					+ PageHeight);

			/**
			 * C#代码 获取值为 96 [DllImport("User32.DLL",
			 * CallingConvention=CallingConvention.StdCall, SetLastError=true,
			 * ExactSpelling=true)] private static extern IntPtr GetDC(IntPtr
			 * hWnd); [DllImport("gdi32.dll",
			 * CallingConvention=CallingConvention.StdCall, SetLastError=true,
			 * ExactSpelling=true)] private static extern int
			 * GetDeviceCaps(IntPtr hdc, int nIndex); 96是原来C#中获取的句柄值 --->
			 */

	//		constant = (long) (96 / Double.parseDouble(PageScale));

			lBasedX = (long) (Double.parseDouble(PageWidth) / 24.4 * constant);
			lBasedY = (long) (Double.parseDouble(PageHeight) / 24.4 * constant);

			System.out.println(constant + " , " + lBasedX + " , " + lBasedY);

			/*
			 * 获取模型的宽和高
			 */
			Element shapeElement = rootElement.element("Pages").element("Page")
					.element("Shapes").element("Shape");

			rcd = new RouterClientData();

			rcd.setWidth(Unit2Pix(Double.parseDouble(shapeElement.element(
					"XForm").elementText("Width"))));
			rcd.setHeigth(Unit2Pix(Double.parseDouble(shapeElement.element(
					"XForm").elementText("Height"))));

			System.out.println("面板宽：" + rcd.getWidth() + "," + "面板高："
					+ rcd.getHeigth());
			// end 获取模型的宽和高

			Element shapesElement = rootElement.element("Pages")
					.element("Page").element("Shapes");

			List shapes = shapesElement.elements();
			System.out.println(shapes.size());

	//		int h = 1, k = 0;
			
			for (int i = 0; i < shapes.size(); i++) {
				shapeElement = (Element) shapes.get(i);
				List props = new ArrayList();
//				List propsG = new ArrayList();
				List shapesGroup = new ArrayList();
				
				String str = shapeElement.attributeValue("Type");
				
				if (str.equals("Group")) {
					//获取Group节点下的 x，y  locPinX，locPinY
					lGroupX = (long) (Double.parseDouble(shapeElement.element("XForm").elementText("PinX")) / 24.4 * constant);
					lGroupY = (long) (Double.parseDouble(shapeElement.element("XForm").elementText("PinY")) / 24.4 * constant);
					System.out.println("lGroupX = "+lGroupX+","+"lGroupY = "+lGroupY);
					lGroupPinX = (long) (Double.parseDouble(shapeElement.element("XForm").elementText("LocPinX")) / 24.4 * constant);
					lGroupPinY = (long) (Double.parseDouble(shapeElement.element("XForm").elementText("LocPinY")) / 24.4 * constant);
					System.out.println("lGroupPinX = "+lGroupPinX+","+"lGroupPinY = "+lGroupPinY);
					
					shapesGroup = shapeElement.element("Shapes").elements("Shape");
					for (int j = 0; j < shapesGroup.size(); j++) {
						shapeElement = (Element) shapesGroup.get(j);
						props = shapeElement.elements("Prop");
						
						if (0 == props.size()) {
							continue;
						} else {
							System.out
							.println("类型："
									+ shapeElement.element("Prop").elementText(
											"Label")
									+ " , "
									+ "第几个:"
									+ shapeElement.element("Prop").elementText(
											"Value"));
							if (shapeElement.element("Prop").elementText("Label")
									.equals("端口号")) {
								port = new Port();
								point = new Point();
								/**
								 * x,y 
								 * x = (this.lOffsetX + num2) - num7;
		                    	 * y = (this.lOffsetY + this.lBasedY) - (num3 + num6);
								 * 
								 * width,height
								 * 
								 * num2 = (num / 24.4) * 2438;
								 * 
								 */
//								Rectangle r = new Rectangle();
//								r.setX(getX(Double.parseDouble(shapeElement.element("XForm").elementText("PinX"))));
//								r.setY(y);
//								r.setWidth(width);
//								r.setHeight(height);
//								port.setRec(r);
								int valueID = Integer.parseInt(shapeElement.element("Prop").elementText("Value"));
								port.setId(valueID);
								
								//打印索引号  或者 ID
								System.out.println(port.getId());
								
		//						port.setId(h);
								port.setName("port" + valueID);
								port.setPortState(false);
								
								
								num1 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("PinX")));
								num2 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("PinY")));
								
								num5 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("LocPinX")));
								num6 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("LocPinY")));
								
								num3 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("Width")));
								num4 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("Height")));
							
								point.setX((lGroupX-lGroupPinX) + num1 - num5);
								point.setY(lBasedY - (lGroupY + lGroupPinY) + lGroupPinY * 2L - num2 - num6);
								
								port.setP(point);
								
								port.setWidth(num3);
								port.setHeigth(num4);
								
								
								ports.add(port);
								
								//h++;
								
								System.out.println("端口坐标：(" + port.getP().getX()
										+ " , " + port.getP().getY() + ")");
								System.out.println("端口宽：" + num3 + " , "
										+ "端口高：" + num4);
								
							} else if (shapeElement.element("Prop")
									.elementText("Label").equals("端口灯号")) {
								lamp = new Lamp();
								point = new Point();
								
								//如果端口灯有全部为0的 不标准的visio文件时  需要特需处理
								int valueID = Integer.parseInt(shapeElement.element("Prop").elementText("Value"));
								lamp.setId(valueID);
								
								//打印索引号  或者 ID
								System.out.println(lamp.getId());
								
	//							lamp.setId(k);
								lamp.setName("lamp" + valueID);
								lamp.setLampState(false);
								
								num1 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("PinX")));
								num2 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("PinY")));
								
								num5 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("LocPinX")));
								num6 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("LocPinY")));
								
								num3 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("Width")));
								num4 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("Height")));

								point.setX(num1-num5);
								point.setY(lBasedY - (num2 + num6));
								
								lamp.setP(point);
								
								lamp.setWidth(num3);
								lamp.setHeigth(num4);
								
								
								lamps.add(lamp);
								
								//k++;
								
								System.out.println("端口灯坐标：(" + lamp.getP().getX()
										+ " , " + lamp.getP().getY() + ")");
								System.out.println("端口灯宽：" + num3 + " , "
										+ "端口灯高：" + num4);
							}
						}
					}
					
				}else if (str.equals("Shape")) {
					props = shapeElement.elements("Prop");
					
					
					if (0 == props.size()) {
						continue;
					} else {
						System.out
						.println("类型："
								+ shapeElement.element("Prop").elementText(
										"Label")
								+ " , "
								+ "第几个:"
								+ shapeElement.element("Prop").elementText(
										"Value"));
						
						if (shapeElement.element("Prop").elementText("Label")
								.equals("端口号")) {
							port = new Port();
							point = new Point();
							/**
							 * x,y 
							 * x = (this.lOffsetX + num2) - num7;
	                    	 * y = (this.lOffsetY + this.lBasedY) - (num3 + num6);
							 * 
							 * width,height
							 * 
							 * num2 = (num / 24.4) * 2438;
							 * 
							 */
//							Rectangle r = new Rectangle();
//							r.setX(getX(Double.parseDouble(shapeElement.element("XForm").elementText("PinX"))));
//							r.setY(y);
//							r.setWidth(width);
//							r.setHeight(height);
//							port.setRec(r);
							int valueID = Integer.parseInt(shapeElement.element("Prop").elementText("Value"));
							port.setId(valueID);
							
							//打印索引号  或者 ID
							System.out.println(port.getId());
							
							port.setName("port" + valueID);
							port.setPortState(false);
							
							
							num1 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("PinX")));
							num2 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("PinY")));
							
							num5 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("LocPinX")));
							num6 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("LocPinY")));
							
							num3 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("Width")));
							num4 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("Height")));

							point.setX(num1-num5);
							point.setY(lBasedY - (num2 + num6));
							
							port.setP(point);
							
							port.setWidth(num3);
							port.setHeigth(num4);
							
							
							ports.add(port);
							
							//h++;
							
							System.out.println("端口坐标：(" + port.getP().getX()
									+ " , " + port.getP().getY() + ")");
							System.out.println("端口宽：" + num3 + " , "
									+ "端口高：" + num4);
							
						} else if (shapeElement.element("Prop")
								.elementText("Label").equals("端口灯号")) {
							lamp = new Lamp();
							point = new Point();
							
							int valueID = Integer.parseInt(shapeElement.element("Prop").elementText("Value"));
							lamp.setId(valueID);
							
							//打印索引号  或者 ID
							System.out.println(lamp.getId());
							
							lamp.setName("lamp" + valueID);
							lamp.setLampState(false);
							
							num1 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("PinX")));
							num2 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("PinY")));
							
							num5 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("LocPinX")));
							num6 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("LocPinY")));
							
							num3 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("Width")));
							num4 = Unit2Pix(Double.parseDouble(shapeElement.element("XForm").elementText("Height")));

							point.setX(num1-num5);
							point.setY(lBasedY - (num2 + num6));
							
							lamp.setP(point);
							
							lamp.setWidth(num3);
							lamp.setHeigth(num4);
							
							
							lamps.add(lamp);
							
							//k++;
							
							System.out.println("端口灯坐标：(" + lamp.getP().getX()
									+ " , " + lamp.getP().getY() + ")");
							System.out.println("端口灯宽：" + num3 + " , "
									+ "端口灯高：" + num4);
						}
					}
				}

				

			}

			System.out.println("端口数：  " + ports.size());
			System.out.println("端口灯数：  " + lamps.size());

		} catch (DocumentException e) {

			e.printStackTrace();
		}
	}

	public static long Unit2Pix(double d) {
		long num = (long) (d / 24.4 * constant);

		return num;
	}

	/*
	 * 获取模型的宽和高
	 */
	public RouterClientData getModelSize(String filename) {
		SAXReader saxReader = new SAXReader();

		try {
			Document document = saxReader.read(new File(filename));
			Element rootElement = document.getRootElement();
			Element shapeElement = rootElement.element("Pages").element("Page")
					.element("Shapes").element("Shape");

			rcd = new RouterClientData();

			rcd.setWidth(Unit2Pix(Double.parseDouble(shapeElement.element(
					"XForm").elementText("Width"))));
			System.out.println(shapeElement.element("XForm").elementText(
					"Width"));
			rcd.setHeigth(Unit2Pix(Double.parseDouble(shapeElement.element(
					"XForm").elementText("Height"))));

			System.out.println("面板宽：" + rcd.getWidth() + "," + "面板高："
					+ rcd.getHeigth());

		} catch (DocumentException e) {

			e.printStackTrace();
		}

		return rcd;
	}

	public static void main(String[] args) {
		long startDate = System.currentTimeMillis();

		DataFromVisio dfv = new DataFromVisio();

		String filename = "D://1.3.6.1.4.1.2011.10.1.80.vdx";

		dfv.getCoordinatesForXML(filename);

		long endDate = System.currentTimeMillis();

		System.out.println("程序耗时： " + (endDate - startDate));
	}
}
