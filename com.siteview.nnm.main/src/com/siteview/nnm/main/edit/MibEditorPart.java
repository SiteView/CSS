package com.siteview.nnm.main.edit;



import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.siteview.itsm.nnm.scan.core.snmp.common.ScanParam;
import com.siteview.itsm.nnm.scan.core.snmp.common.SnmpPara;
import com.siteview.itsm.nnm.scan.core.snmp.data.GlobalData;
import com.siteview.itsm.nnm.scan.core.snmp.scan.MibScan;
import com.siteview.itsm.nnm.scan.core.snmp.util.Utils;
import com.siteview.nnm.main.utils.BaseUtils;

public class MibEditorPart extends EditorPart {

	public static final String ID = "com.siteview.nnm.main.mibbrowser";
	
	private Label oidLabel;
	
	public static  Text oidText;
	
	Group group ;
	private Label ipLable;
	private  Text ipText;
	
	private Text content ;
	
	MibScan mibScan = new MibScan();
	public Label getOidLabel() {
		return oidLabel;
	}

	public void setOidLabel(Label oidLabel) {
		this.oidLabel = oidLabel;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

	}
	private Composite comp;
	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setInput(input);
		setSite(site);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		this.comp = parent;
		
		group = new Group(parent, SWT.NONE);
		group.setSize(500,500);
		RowLayout rawLayout = new RowLayout(2);
		group.setLayout(rawLayout);
		oidLabel = new Label(group, SWT.NONE);
		oidLabel.setText("oid");
		
		oidText = new Text(group, SWT.BORDER);
		
		ipLable = new Label(group, SWT.NONE);
		ipLable.setText("ip地址");
		
		ipText = new Text(group, SWT.BORDER);
		
		Button getBtn = new Button(group, SWT.NONE);
		getBtn.setText("GET");
		
		content = new Text(group, SWT.WRAP | SWT.SCROLL_LINE | SWT.READ_ONLY);
		content.setSize(1000, 1000);
		getBtn.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				if(oidText.getText().endsWith("*")){
					BaseUtils.showError(comp,"获取snmp数据出错！", "oid 格式不正确!");
					return;
				}
				String ip = ipText.getText();
				if(!Utils.isIp(ip)){
					BaseUtils.showError(comp,"获取snmp数据出错！", "IP 地址 为空或者 格式不正确!");
					return;
				}
				ScanParam sp = GlobalData.scanParam;
				SnmpPara spa = new SnmpPara();
				spa.setCommunity(sp.getCommunity_get_dft());
				spa.setRetry(sp.getRetrytimes());
				spa.setTimeout(sp.getTimeout());
				
				spa.setIp(ip);
				
				if(oidText.getText().endsWith("0")){
					String value = mibScan.getMibObject(spa, oidText.getText());
					
					String contenta = "REQUEST GET OID = " + oidText.getText() + "\tValue = " + value + "\r\n";
					content.setText(content.getText() + contenta);
					group.redraw();
				}else{
					String oid = oidText.getText().substring(0,oidText.getText().indexOf("("));
					mibScan.getFullTableMib(spa, oid);
//					for(Pair<String,String> p : list){
//						System.out.println(p.getFirst() + "*******"+p.getSecond());
//					}
				}
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
		});
	}

	@Override
	public void setFocus() {

	}

}
