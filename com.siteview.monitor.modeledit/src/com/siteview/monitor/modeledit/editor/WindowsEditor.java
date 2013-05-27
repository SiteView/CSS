package com.siteview.monitor.modeledit.editor;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.OPIRunner;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.rulers.RulerComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.properties.PropertySheetPage;

public class WindowsEditor extends GraphicalEditorWithFlyoutPalette {

	public static final String OPI_FILE_EXTENSION = "opi"; //$NON-NLS-1$
	public static final String ID = "com.siteview.monitor.modeledit.editor.windows"; //$NON-NLS-1$

	private PaletteRoot paletteRoot;

	/** the undoable <code>IPropertySheetPage</code> */
	private PropertySheetPage undoablePropertySheetPage;

	private DisplayModel displayModel;

	private RulerComposite rulerComposite;

	private KeyHandler sharedKeyHandler;

	private Clipboard clipboard;

	private SelectionSynchronizer synchronizer;
	
	
	public WindowsEditor() {
		if(getPalettePreferences().getPaletteState() <= 0)
			getPalettePreferences().setPaletteState(FlyoutPaletteComposite.STATE_PINNED_OPEN);
		setEditDomain(new DefaultEditDomain(this));
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if(PreferencesHelper.isNoEdit()){
			IDE.openEditor(site.getPage(), input, OPIRunner.ID);
			setSite(site);
			setInput(input);

			Display.getDefault().asyncExec(new Runnable(){
				public void run() {

					getSite().getPage().closeEditor(WindowsEditor.this, false);

				}
			});

		}
		else
			super.init(site, input instanceof NoResourceEditorInput ? input : new NoResourceEditorInput(input)); 
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		// TODO Auto-generated method stub
		return null;
	}

}
