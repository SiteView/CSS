package com.siteview.css.topo.wizard.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.SchemaService;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.filesystem.FileSystemConfiguration;
import org.eclipse.ui.internal.ide.filesystem.FileSystemSupportRegistry;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;

import com.siteview.itsm.nnm.scan.core.snmp.common.ScanParam;
import com.siteview.itsm.nnm.scan.core.snmp.data.GlobalData;
import com.siteview.itsm.nnm.scan.core.snmp.flowmonitor.MonitorControler;
import com.siteview.itsm.nnm.scan.core.snmp.scan.NetScan;
import com.siteview.itsm.nnm.scan.core.snmp.util.IoUtils;
/**
 * ��ʼ����ɨ���action
 * @author haiming.wang
 *
 */
@SuppressWarnings({ "unused", "restriction" })
public class StartScanAction implements IWorkbenchWindowActionDelegate {
	//ɨ�����
	private ScanParam scanParam;
	
	private IWorkbench workbench ;
	private boolean scaned = false;
	private CountDownLatch scanCound;
	//����ɨ�蹤��
	private NetScan scan;
	public static final String TOPO_OPI_FILENAME = "topo.opi";
	public static final String TOPO_PROJECTNAME = "topologyPro";
	private void showError(String title,String conent){
		MessageBox dialog = new MessageBox(window.getShell(), SWT.ICON_ERROR);
		dialog.setMessage(conent);
		dialog.setText(title);
		dialog.open();
	}
	@Override
	public void run(IAction action) {
		if(!projectExists(TOPO_PROJECTNAME)){
			try {
				createProject();
			} catch (CoreException e) {
				e.printStackTrace();
				showError("����project ʧ��", e.getMessage());
				return;
			}
		}
		workbench = PlatformUI.getWorkbench();
		//����û��ӽ���������ɨ������������õĲ�����Ϣɨ��
		if(GlobalData.isConfiged){
			scanParam = GlobalData.scanParam;
		}else{
			//������ϴα����������Ϣɨ��
			scanParam = IoUtils.readScanParam();
		}
		if(scanParam == null || (scanParam.getScan_scales().isEmpty() && scanParam.getScan_seeds().isEmpty())){
			showError("ɨ�������ʼ��ʧ��", "��������ɨ�����ӻ���ɨ��IP��Χ��");
			return;
		}
		//ɨ�������
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(workbench.getActiveWorkbenchWindow().getShell());
		scanCound = new CountDownLatch(1);
		try {
			pmd.run(true, false, new WithProgress());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			scanCound.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			if(scaned){
				drawTopo(scan);
				MonitorControler monitor = new MonitorControler(scan.getDevid_list(),scan.getTopo_edge_list());
				monitor.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			showError("չʾ����ͼʧ��", e.getMessage());
		}
		
	}
	
	class WithProgress implements IRunnableWithProgress{
		@Override
		public void run(IProgressMonitor monitor){
			monitor.beginTask("����ɨ������ṹ..." + "", IProgressMonitor.UNKNOWN);  
			/**
			 * ִ�е�����
			 */
			Map<String, Map<String, String>> special_oid_list = new ConcurrentHashMap<String, Map<String, String>>();
			scan = new NetScan(null, special_oid_list, scanParam);
			try{
				scan.scan();
				monitor.worked(50);
				//���������
				GlobalData.isInit = true;
				GlobalData.edgeList = scan.getTopo_edge_list();
				GlobalData.deviceList = scan.getDevid_list();
				monitor.worked(100);
				scaned = true;
			}catch (Exception e) {
				GlobalData.isInit = false;
				scaned = false;
				showError("ɨ��ʧ��", e.getMessage());
			}finally{
				monitor.done();
				scanCound.countDown();
			}
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
	 * ��������ͼ
	 * @param scan
	 * @throws Exception 
	 */
	private void drawTopo(NetScan scan) throws Exception{
		URI uri = new URI("/"+TOPO_PROJECTNAME+"/" + TOPO_OPI_FILENAME);
		IPath path = FileUtil.toPath(uri);
		IFile file = createFileHandle(path);
		FileEditorInput input = new FileEditorInput(file);
		IEditorPart topoEdit  = window.getActivePage().findEditor(input);
		if(topoEdit != null){
			window.getActivePage().closeEditor(topoEdit, false);
		}
		createFile(file, getInitialContents());
		window.getActivePage()
				.openEditor(input ,
						"com.siteview.css.topo.editparts.TOPOEdit");
	}
	public void createTopologyPro() throws CoreException {
		createProject();
	}
	private IProjectDescription description ;
	//������Ŀ
	public void createProject()
			throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(TOPO_PROJECTNAME);
		URI uri = getProjectLocationURI();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		description = workspace.newProjectDescription(project.getName());
		description.setLocationURI(uri);
		IProject[] refProjects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		if (refProjects.length > 0) {
			description.setReferencedProjects(refProjects);
		}
		project.create(null);
		project.open(null);
	}
	//��ȡ�ļ�����
	public IFile getFile(){
		IProject p = getProjectHandle();
		return p.getFile(TOPO_OPI_FILENAME);
	}
	//��ȡuri
	public URI getProjectLocationURI() {
		return getSelectedConfiguration().getContributor().getURI(
			      TOPO_PROJECTNAME);
	}
	//��ȡ�ļ�ϵͳ����
	private FileSystemConfiguration getSelectedConfiguration() {
			return FileSystemSupportRegistry.getInstance()
					.getDefaultConfiguration();
	}
	/**
	 * �ж�topo.opi�ļ��Ƿ����
	 * @param file
	 * @return
	 */
	public boolean fileExists(){
		return (getFile()!=null);
	}
	/**
	 * ɾ��topology opi�ļ�
	 * @throws CoreException
	 */
	public void deleteFile() throws CoreException{
		getFile().delete(true, null);
	}
	/**
	 * ��ȡ��Ŀ����
	 * @return
	 */
	public IProject getProjectHandle(){
		return ResourcesPlugin.getWorkspace().getRoot().getProject(
			      TOPO_PROJECTNAME);
	}
	/**
	 * �ж���Ŀ�Ƿ����
	 * @param projectName
	 * @return
	 */
	public boolean projectExists(String projectName){
		return (getProjectByName(projectName) != null);
	}
	/**
	 * ������Ŀ���� ��ȡ��Ŀ����
	 * @param projectName
	 * @return
	 */
	public IProject getProjectByName(String projectName){
		IProject ps[] = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(int i=0;i<ps.length;i++){
			IProject p = ps[i];
			if(projectName.equals(p.getProject().getName())){
				return p;
			}
		}
		return null;
	}
	/**
	 * �����ļ�����
	 * @param projectName
	 * @return
	 */
	public IFile createFileHandle(IPath filePath){
		return ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
	}
	/**
	 * Creates a file resource given the file handle and contents.
	 * @param fileHandle the file handle to create a file resource with
	 * @param contents the initial contents of the new file resource, or
	 *            <code>null</code> if none (equivalent to an empty stream)
	 * @exception CoreException if the operation fails
	 */
	private final void createFile(final IFile fileHandle,
			final InputStream contents) throws CoreException {
		InputStream inputStream = contents;
		//����Ŀ
		if(!getProjectHandle().isOpen()){
			getProjectHandle().open(null);
		}
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
