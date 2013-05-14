package com.siteview.pv.monitor.data;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import com.siteview.pv.monitorcounter.Activator;


/**
 * <p>
 * 读取配置文件properties文件
 * </p>
 */
public class Config {
	public static Properties prop = new Properties();  
	// 获取文件路径
			public static String getRealPath(String fileName) {
				String str = getRoot()+fileName;
				  return str;
			}
			
			public static String getRoot() {
				String path = null;
				try {
					path = FileLocator.toFileURL(
							Platform.getBundle(Activator.PLUGIN_ID).getEntry("")).getPath();
					path = path.substring(path.indexOf("/") + 1, path.length());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return path;
			}

			public static String getReturnStr(String filePath, String parm) {
				try {
					FileInputStream fis = new FileInputStream(new File(filePath));
					prop = new Properties();
					prop.load(fis);
				}catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return prop.getProperty(parm);
			}
}