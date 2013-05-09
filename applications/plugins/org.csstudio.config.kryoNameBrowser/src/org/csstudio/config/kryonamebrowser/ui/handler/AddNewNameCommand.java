package org.csstudio.config.kryonamebrowser.ui.handler;

import org.csstudio.config.kryonamebrowser.ui.MainView;
import org.csstudio.config.kryonamebrowser.ui.dialog.AddNewNameDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddNewNameCommand extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		final MainView view = (MainView) page.findView(MainView.ID);
		AddNewNameDialog dialog = new AddNewNameDialog(HandlerUtil
				.getActiveShell(event));

		dialog.setLogic(view.getLogic());
		dialog.open();

		if (dialog.isCallUpdate()) {
			view.getFilter().updateTable(HandlerUtil.getActiveShell(event));
		}

		return null;
	}

}
