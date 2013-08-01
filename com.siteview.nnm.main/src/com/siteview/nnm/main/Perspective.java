package com.siteview.nnm.main;

import org.eclipse.swt.SWT;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.siteview.nnm.main.viewer.MenuViewer;

public class Perspective implements IPerspectiveFactory {

	
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		layout.addView(MenuViewer.ID, SWT.LEFT , 0.2f, layout.getEditorArea());
	}

}
