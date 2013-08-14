package com.siteview.nnm.main.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.filesystem.FileSystemConfiguration;
import org.eclipse.ui.internal.ide.filesystem.FileSystemSupportRegistry;
import org.eclipse.ui.part.FileEditorInput;
/**
 * 启动扫描的工具类
 * @author haiming.wang
 *
 */
@SuppressWarnings("restriction")
public class DrawTopo {

	public static final String TOPO_OPI_FILENAME = "topo.opi";
	public static final String TOPO_PROJECTNAME = "topologyPro";
	private Composite parent;
	private static DrawTopo instance;
	private IWorkbenchWindow window;
	private DrawTopo(Composite parent){
		this.parent = parent;
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}
	public static synchronized DrawTopo getInstance(Composite parent){
		if(instance == null){
			instance = new DrawTopo(parent);
		}
		return instance;
	}
	/**
	 * 处理拓扑图显示
	 */
	public void showTopo(){
		if(!projectExists(TOPO_PROJECTNAME)){
			try {
				createProject();
			} catch (CoreException e) {
				e.printStackTrace();
				BaseUtils.showError(parent,"创建project 失败", e.getMessage());
				return;
			}
		}
		URI uri = null;
		try {
			uri = new URI("/"+TOPO_PROJECTNAME+"/" + TOPO_OPI_FILENAME);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		IPath path = FileUtil.toPath(uri);
		IFile file = createFileHandle(path);
		FileEditorInput input = new FileEditorInput(file);
		IEditorPart topoEdit  =window.getActivePage().findEditor(input);
		if(topoEdit != null){
			window.getActivePage().closeEditor(topoEdit, false);
		}
		try {
			createFile(file, getInitialContents());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		try {
			window.getActivePage()
					.openEditor(input ,
							"com.siteview.css.topo.editparts.TOPOEdit");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取项目对象
	 * @return
	 */
	private IProject getProjectHandle(){
		return ResourcesPlugin.getWorkspace().getRoot().getProject(
			      TOPO_PROJECTNAME);
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
		//打开项目
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
			//如果文件己存在先删除该文件
			if(fileHandle.exists()){
				fileHandle.delete(true, null);
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
	/**
	 * 创建文件对象
	 * @param projectName
	 * @return
	 */
	private IFile createFileHandle(IPath filePath){
		return ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
	}
	private InputStream getInitialContents() {
		DisplayModel displayModel = new DisplayModel();
		SchemaService.getInstance().applySchema(displayModel);
		String s = XMLUtil.widgetToXMLString(displayModel, true);
		InputStream result = new ByteArrayInputStream(s.getBytes());
		return result;
	}
	/**
	 * 判断项目是否存在
	 * @param projectName
	 * @return
	 */
	private boolean projectExists(String projectName){
		return (getProjectByName(projectName) != null);
	}
	/**
	 * 根据项目名称 获取项目对象
	 * @param projectName
	 * @return
	 */
	private IProject getProjectByName(String projectName){
		IProject ps[] = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(int i=0;i<ps.length;i++){
			IProject p = ps[i];
			if(projectName.equals(p.getProject().getName())){
				return p;
			}
		}
		return null;
	}

	// 获取文件系统配置
	private FileSystemConfiguration getSelectedConfiguration() {
		return FileSystemSupportRegistry.getInstance()
				.getDefaultConfiguration();
	}

	// 获取uri
	private URI getProjectLocationURI() {
		return getSelectedConfiguration().getContributor().getURI(
				TOPO_PROJECTNAME);
	}

	private IProjectDescription description;

	// 创建项目
	private void createProject() throws CoreException {
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
}
