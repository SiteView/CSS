package com.siteview.css.topo.editparts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.csstudio.opibuilder.widgets.model.PolyLineModel;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.siteview.css.topo.models.TopologyModel;

public class TOPOEdit extends OPIEditor {
	public static final String ID = "com.siteview.css.topo.editparts.TOPOEdit";
	public TOPOEdit(){
		super();
		
	}
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
	}
	@Override
	protected void createGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
		DisplayModel displayModel = getDisplayModel();
		TopologyModel model1 = new TopologyModel();
		TopologyModel model2 = new TopologyModel();
		model2.setX(model1.getX() + model1.getWidth() +20);
		PolyLineModel line = new PolyLineModel();
		PointList ps = new PointList(2);
		ps.addPoint(model1.getBounds().getCenter());
		ps.addPoint(model2.getBounds().getCenter());
		line.setPoints(ps, true);
		line.setBorderColor(CustomMediaFactory.getInstance().COLOR_RED);
		
		
		ConnectionModel cModel = new ConnectionModel(displayModel);
		cModel.connect(model1, "model1", model2, "model2");
//		WidgetDescriptor widgetDescriptor = WidgetsService.getInstance().getWidgetDescriptor(cModel.getTypeID());
		getDisplayModel().addChild(model1);
		getDisplayModel().addChild(model2);
//		getDispsayModel().addChild(cModel);
//		try {
//			XMLUtil.fillDisplayModelFromInputStream(getInputStream(), displayModel);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	

	/**
	 * @return the origin editor input before wrapped by {@link NoResourceEditorInput}.
	 */
	public IEditorInput getOriginEditorInput() {
		IEditorInput editorInput = super.getEditorInput();
		if(editorInput instanceof NoResourceEditorInput){
			return ((NoResourceEditorInput)editorInput).getOriginEditorInput();
		}
		return editorInput;
	}
	private InputStream getInputStream() {
		InputStream result = null;

		IEditorInput editorInput = getOriginEditorInput();

		if (editorInput instanceof FileEditorInput) {
			
//			try {
//				result = ((FileEditorInput) editorInput).getFile()
//						.getContents();
//			} catch (CoreException e) {
//				e.printStackTrace();
//			}
		} else if (editorInput instanceof FileStoreEditorInput) {
			IPath path = URIUtil.toPath(((FileStoreEditorInput) editorInput)
					.getURI());
			try {
				result = new FileInputStream(path.toFile());
			} catch (FileNotFoundException e) {
				result = null;
			}
		}

		return result;
	}


}