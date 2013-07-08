package org.csstudio.ui.menu.app;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.part.CoolItemGroupMarker;

/** {@link ActionBarAdvisor} that can be called by CSS
 *  application startup code to create the menu and tool bar.
 *
 *  <p>The menu bar is mostly empty, only providing the "additions"
 *  section that is used by the contributions in plugin.xml.
 *
 *  <p>The toolbar also mostly defines sections used by contributions
 *  from plugin.xml.
 *
 *  <p>Some actions are created for Eclipse command names
 *  in the help menu that have no default implementation.
 *
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{
	/** Toolbar ID of switch user and logout toolbar */
    private static final String TOOLBAR_USER = "user"; //$NON-NLS-1$

    final private IWorkbenchWindow window;

	private IWorkbenchAction lock_toolbar, edit_actionsets, save;

	private OpenAction navigatorAction;
	
	private IWorkbenchAction preferenceAction;
	public final String ID = "org.csstudio.ui.menu.app.PreferencePage";
	/** Initialize */
    public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer)
    {
        super(configurer);
        window = configurer.getWindowConfigurer().getWindow();
        
    }

    /** Create actions.
     *
     *  <p>Some of these actions are created to programmatically
     *  add them to the toolbar.
     *  Other actions like save_as are not used at all in here,
     *  but they need to be created because the plugin.xml
     *  registers their command ID in the menu bar,
     *  and the action actually implements the handler.
     *  The actions also provide the dynamic enablement.
     *
     *  {@inheritDoc}
     */
	@Override
    protected void makeActions(final IWorkbenchWindow window)
    {
        lock_toolbar = ActionFactory.LOCK_TOOL_BAR.create(window);
        register(lock_toolbar);

        edit_actionsets = ActionFactory.EDIT_ACTION_SETS.create(window);
        register(edit_actionsets);

        save = ActionFactory.SAVE.create(window);
        register(save);

        register(ActionFactory.SAVE_AS.create(window));

        if (window.getWorkbench().getIntroManager().hasIntro())
            register(ActionFactory.INTRO.create(window));

        register(ActionFactory.HELP_CONTENTS.create(window));
        
        //�����������
        navigatorAction = new OpenAction(window);
        
		// ����̨�����������󣬶����"��ѡ��"�Ի������
		preferenceAction = ActionFactory.PREFERENCES.create(window);
    }

    /** {@inheritDoc} */
    @Override
    protected void fillMenuBar(final IMenuManager menubar)
    {
/*        // Placeholder for possible additions, rest filled from plugin.xml
        menubar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        //����MenuManager���󣬼�����һ����Window���˵�
        MenuManager windowMenu = new MenuManager("&����",IWorkbenchActionConstants.M_WINDOW);
        //�ڡ�Window���˵�����Ӳ˵�������
        windowMenu.add((IAction) navigatorAction);
        //�ڲ˵�����Ӳ˵�
        menubar.add(windowMenu);*/
		// ����Window�˵�
		MenuManager windowMenu = new MenuManager("&Window",
				"IWorkbenchActionConstants.M_WINDOW");
		// ΪWindow���"����ѡ��"�����˵���
		windowMenu.add(preferenceAction);
		// ��Window�˵���ӵ��˵���
		menubar.add(windowMenu);
        
    }

    /** {@inheritDoc} */
    @Override
    protected void fillCoolBar(final ICoolBarManager coolbar)
    {
        // Set up the context Menu
        final MenuManager coolbarPopupMenuManager = new MenuManager();
        coolbarPopupMenuManager.add(new ActionContributionItem(lock_toolbar));
        coolbarPopupMenuManager.add(new ActionContributionItem(edit_actionsets));
        coolbar.setContextMenuManager(coolbarPopupMenuManager);
        final IMenuService menuService = (IMenuService) window.getService(IMenuService.class);
        menuService.populateContributionManager(coolbarPopupMenuManager, "popup:windowCoolbarContextMenu"); //$NON-NLS-1$

        // 'File' and 'User' sections of the cool bar
        final IToolBarManager file_bar = new ToolBarManager();
        final IToolBarManager user_bar = new ToolBarManager();
        coolbar.add(new ToolBarContributionItem(file_bar, IWorkbenchActionConstants.M_FILE));
        coolbar.add(new ToolBarContributionItem(user_bar, TOOLBAR_USER));

        // File 'new' and 'save' actions
        file_bar.add(ActionFactory.NEW.create(window));
        file_bar.add(save);
        file_bar.add(new CoolItemGroupMarker(IWorkbenchActionConstants.FILE_END));
    }
	   //�ж��û����������Ƿ��Ѿ����ڡ�
		public boolean preOpen() {
			// ���õ�¼�Ի���
			LoginDialog ld = new LoginDialog(null);
			//����Dcctor����
//	    	Doctor doctor = new Doctor();
//			ld.setDoctor(doctor);
			//�򿪵�¼�Ի���
			if (ld.open() == IDialogConstants.OK_ID) {
				//�����ݿ���ȥ��֤��Ϣ
				//doctor = DataBaseOperate.getLoginInfor(doctor);
//				if (doctor == null) {
//					MessageDialog.openInformation(null, null, "����ȷ��д��Ϣ��������");
//					return false;
//				}
//				if (doctor != null) {
//					return true;
//				}
				return true;
			}
			return false;
		}
}
