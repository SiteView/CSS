package com.siteview.css.topo.factory;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class ImageFactory {
	public static final Bundle bundle = Platform.getBundle("com.siteview.css.topo");
	public static Image pcImage = null;
	public static Image routerImage = null;
	public static Image switchImg = null;
	public static Image switchRouterImg = null;
	public static Image bumbImg = null;
	public static synchronized Image getPcBlueImage(){
		if(pcImage == null){
		    URL url = bundle.getEntry("icons/png_PC_Blue.png");
		    pcImage = ImageDescriptor.createFromURL(url).createImage();
		}
		return pcImage;
	}
	public static synchronized Image getRouterBlueImage(){
		if(routerImage == null){
			 URL url = bundle.getEntry("icons/png_Router_Blue.png");
			 routerImage = ImageDescriptor.createFromURL(url).createImage();
		}
		return routerImage;
	}
	public static synchronized Image getSwitchBlueImage(){
		if(switchImg == null){
			URL url = bundle.getEntry("icons/Switch_Blue.png");
			switchImg = ImageDescriptor.createFromURL(url).createImage(); 
		}
		return switchImg;
	}
	public static synchronized Image getSwitchRouterBlueImage(){
		if(switchRouterImg == null){
			URL url = bundle.getEntry("icons/png_SwitchRouter_Blue.png");
			switchRouterImg = ImageDescriptor.createFromURL(url).createImage();
		}
		return switchRouterImg;
	}
	public static synchronized Image getDumbImage(){
		if(bumbImg == null){
			URL url = bundle.getEntry("icons/png_Hub.png");
			bumbImg = ImageDescriptor.createFromURL(url).createImage();
		}
		return bumbImg;
	}

}
