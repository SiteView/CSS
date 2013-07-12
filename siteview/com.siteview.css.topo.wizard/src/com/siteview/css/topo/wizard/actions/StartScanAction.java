package com.siteview.css.topo.wizard.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.SchemaService;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.csstudio.ui.util.composites.ResourceAndContainerGroup;
import org.csstudio.ui.util.internal.localization.Messages;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;

import com.siteview.css.topo.common.TopoData;
import com.siteview.css.topo.wizard.NewTopoFilePage;
import com.siteview.css.topo.wizard.common.GlobalData;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.scan.NetScan;
import com.siteview.snmp.util.IoUtils;
/**
 * 开始拓扑扫描的action
 * @author haiming.wang
 *
 */
public class StartScanAction implements IWorkbenchWindowActionDelegate {

	private ScanParam scanParam;
	private NewTopoFilePage topoFilePage;
	private IWorkbench workbench ;
	private NetScan scan;
	@Override
	public void run(IAction action) {
		workbench = PlatformUI.getWorkbench();
		//如果用户从界面配置了扫描参数，以配置的参数信息扫描
		if(GlobalData.isConfiged){
			scanParam = GlobalData.scanParam;
		}else{
			//否则从上次保存的配置信息扫描
			scanParam = IoUtils.readScanParam();
			if(scanParam == null || (scanParam.getScan_scales().isEmpty() && scanParam.getScan_seeds().isEmpty())){
				Shell shell = workbench.getDisplay().getActiveShell();
				MessageBox dialog = new MessageBox(shell, SWT.ICON_ERROR);
				dialog.setMessage("必须配置扫描种子或者扫描IP范围！");
				dialog.setText("扫描参数初始化失败");
				dialog.open();
				return;
			}
		}
		//扫描进度条
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(workbench.getActiveWorkbenchWindow().getShell());
		try {
			pmd.run(false, false, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					 monitor.beginTask("正在扫描网络结构..." + "", IProgressMonitor.UNKNOWN);  
					/**
					 * 执行的任务
					 */
					Map<String, Map<String, String>> special_oid_list = new ConcurrentHashMap<String, Map<String, String>>();
					scan = new NetScan(null, special_oid_list, scanParam);
					try{
						scan.scan();
						TopoData.isInit = true;
						TopoData.edgeList = scan.getTopo_edge_list();
						drawTopo(scan);
					}catch (Exception e) {
						TopoData.isInit = false;
						MessageDialog.openError(workbench.getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
					}
					monitor.done();
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			MessageDialog.openError(workbench.getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			MessageDialog.openError(workbench.getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
		}
		
	}
	private InputStream getInitialContents() {
		DisplayModel displayModel = new DisplayModel();
		SchemaService.getInstance().applySchema(displayModel);
		String s = XMLUtil.widgetToXMLString(displayModel, true);
		InputStream result = new ByteArrayInputStream(s.getBytes());
		return result;
	}
	/**
	 * 绘制拓扑图
	 * @param scan
	 * @throws Exception 
	 */
	private void drawTopo(NetScan scan) throws Exception{
		URI uri = new URI("/jajaj/topo.opi");
		IPath path = FileUtil.toPath(uri);
		IFile file = createFileHandle(path);
		createFile(file, getInitialContents());
		workbench
				.getActiveWorkbenchWindow()
				.getActivePage()
				.openEditor(new FileEditorInput(file),
						"com.siteview.css.topo.editparts.TOPOEdit");
	}
	public IFile createFileHandle(IPath filePath){
		return ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
	}
	public IFile createFile(IPath filePath){
		return null;
	}
	/**
	 * Creates a file resource given the file handle and contents.
	 * 
	 * @param fileHandle
	 *            the file handle to create a file resource with
	 * @param contents
	 *            the initial contents of the new file resource, or
	 *            <code>null</code> if none (equivalent to an empty stream)
	 * @exception CoreException
	 *                if the operation fails
	 */
	private final void createFile(final IFile fileHandle,
			final InputStream contents) throws CoreException {
		InputStream inputStream = contents;

		if (inputStream == null) {
			inputStream = new ByteArrayInputStream(new byte[0]);
		}

		try {
			IPath path = fileHandle.getFullPath();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			int numSegments = path.segmentCount();
			if (numSegments > 2
					&& !root.getFolder(path.removeLastSegments(1)).exists()) {
				// If the direct parent of the path doesn't exist, try to
				// create the
				// necessary directories.
				for (int i = numSegments - 2; i > 0; i--) {
					IFolder folder = root.getFolder(path.removeLastSegments(i));
					if (!folder.exists()) {
						folder.create(false, true, null);
					}
				}
			}
			fileHandle.create(inputStream, false, null);
		} catch (CoreException e) {
			// If the file already existed locally, just refresh to get contents
			if (e.getStatus().getCode() == IResourceStatus.PATH_OCCUPIED) {
				fileHandle.refreshLocal(IResource.DEPTH_ZERO, null);
			} else {
				throw e;
			}
		}
	}
	 class PackageExplorerLabelProvider extends LabelProvider{  
	        @Override  
	        public Image getImage(Object element) {  
	            // TODO Auto-generated method stub  
	            Image image = null;  
	            String text = element.toString();  
	            System.out.println(text);  
	            final Bundle bundle = Platform.getBundle("com.siteview.css.topo.wizard");  
	            final URL url = bundle.getEntry("icons/css16.png");  
	            final URL wfUrl = bundle.getEntry("icons/OPIBuilder.png");  
	              
//	          image = ImageDescriptor.createFromURL(url).createImage();  
	            if(text.startsWith("P/",0)){  
	                image = ImageDescriptor.createFromURL(url).createImage();  
	            }  
	            if(text.startsWith("L/",0)){  
	                image = ImageDescriptor.createFromURL(wfUrl).createImage();  
	            }  
	            return image;  
	        }  
	        @Override  
	        public String getText(Object element) {  
	            // TODO Auto-generated method stub  
	            String text = element.toString();  
	            text = text.substring(2,text.length());  
	            int pos = text.indexOf("/");  
	            if(pos == -1){  
	                return text;  
	            }else{  
	                if(text.endsWith(".project")){  
	                    return "Your name";  
	                }else{  
	                    text = text.substring(pos+1,text.length());  
	                }  
	            }  
	            return text;  
	        }  
	    }  
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	private IWorkbenchWindow window;

}
