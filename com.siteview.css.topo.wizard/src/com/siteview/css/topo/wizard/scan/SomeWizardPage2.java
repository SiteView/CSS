package com.siteview.css.topo.wizard.scan;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.*;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
/**
 * 界面2
 * @author zhangxinnan
 *
 */
public class SomeWizardPage2 extends WizardPage {
    /** 红色*/
    private Color COLOR_SYSTEM_RED = null;
	private Display display = null;
	private Label parallelThreads;
	private Label retryCount;
	private Label timeOut;
	protected SomeWizardPage2() {
		super("Some wizard Page");
		setTitle("WizardPage Title");
		setMessage("WizardPage Message");
	}
	
public void createControl(Composite parent) {
    //设置颜色
    display = parent.getDisplay();
    COLOR_SYSTEM_RED=display.getSystemColor(SWT.COLOR_RED);
	
	Group group = new Group(parent, SWT.NONE);
	group.setText("\t\t扫描范围");
	group.setLayout(new GridLayout(2, true));
	GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
	layoutData.verticalIndent = 15;
	group.setLayoutData(layoutData);
	
	final Spinner spinner = new Spinner (group, SWT.BORDER);
	spinner.setValues(0, 0, 100, 0, 1, 10);
	spinner.setLayoutData(new GridData(200, SWT.DEFAULT));
	final ToolTip toolTip = new ToolTip(new Shell(), SWT.BALLOON | SWT.ICON_WARNING);
	spinner.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			String string = spinner.getText();
			String message = null;
			try {
				int value = Integer.parseInt(string);
				int maximum = spinner.getMaximum();
				int minimum = spinner.getMinimum();
				if (value > maximum) {
					message = "Current input is greater than the maximum limit ("+maximum+")";
				} else if (value < minimum) {
					message = "Current input is less than the minimum limit ("+minimum+")";
				}
			} catch (Exception ex) {
				message = "Current input is not numeric";
			}
			if (message != null) {
				spinner.setForeground(display.getSystemColor(SWT.COLOR_RED));
				Rectangle rect = spinner.getBounds();
				GC gc = new GC(spinner);
				Point pt = gc.textExtent(string);
				gc.dispose();
				toolTip.setLocation(display.map(new Shell(), null, rect.x + pt.x, rect.y + rect.height));
				toolTip.setMessage(message);
				toolTip.setVisible(true);
			} else {
				toolTip.setVisible(false);
				spinner.setForeground(null);
			}
		}
	});
	
	setControl(group);
	}
}