package core.businessobject.pv.search;

import java.awt.Event;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import Siteview.ArgumentException;
import Siteview.ConnectionDef;
import Siteview.ConnectionDefGroup;
import Siteview.ConnectionDefType;
import Siteview.DataProviders;
import Siteview.DatabaseEngine;
import Siteview.DbConnectionDef;
import Siteview.DbLoginMethod;
import Siteview.FileBasedConnectionDefMgr;
import Siteview.IConnectionDefManager;
import Siteview.InternalDbConnectionDef;
import Siteview.ResourceUtils;
import Siteview.ServerConnectionDef;
import Siteview.SiteviewException;
import Siteview.TestConnection;
import Siteview.IO.IsolateStoreFile.IsolatedStorageFile;
import Siteview.IO.IsolateStoreFile.IsolatedStorageFileOutputStream;

public class ConnectionManager extends Dialog {
	private TabFolder tabFolder;
	private IConnectionDefManager cmgr;
	private Composite pnlConnection;
	private Text txtServerName;
	private Text txtURI;
	private Text txtProxyName;
	private Text txtProxyPasswd;
	private Text txtProxyDomain;
	private Text txtDescription;
	
	private Composite pnlServer;
	private Composite pnlSqlServer;
	private Composite pnlOracle;
	private Text txtSqlName;
	private Text txtSqlServerName;
	private Text txtSqlDbName;
	private Text txtSqlAdminId;
	private Text txtSqlAdminPwd;
	private Text txtSqlUserPwd;
	private Text txtSqlUserId;
	private Text txtSqlMinConn;
	private Text txtSqlHoldConn;
	private Text txtSqlMaxConn;
	private Text txtSqlConnTimeout;
	private Text txtSqlBreakConnTime;
	private Text txtSqlDescription;
	private Text txtOracleName;
	private Text txtOracleHost;
	private Text txtOracleAdminId;
	private Text txtOracleAdminPwd;
	private Text txtOracleUserPwd;
	private Text txtOracleUserId;
	private Text txtOracleMinConn;
	private Text txtOracleHoldConn;
	private Text txtOracleMaxConn;
	private Text txtOracleConnTimeout;
	private Text txtOracleBreakConnTime;
	private Text txtOracleDescription;
	private Combo cboZip;
	private Combo cboCache;
	private Button chkProxy;
	private Button btNew;
	
	private ConnectionDef m_curConnDef;
	private int	m_oldCommonSelectIndex = -1;
	private int	m_oldPersonalSelectIndex = -1;
	private Table m_oldList = null;
	
	private final String PersonalPrefix = "{Personal}";
	private final String CommonPrefix = "{Common}";

	private VerifyListener intVerifyListener;
	private Table lstCommon;
	private Table lstPriv;
	private Text txtOracleInstanceName;
	
	private Composite pnlMySql;
	private Text txtMySqlName;
	private Text txtMySqlServerName;
	private Text txtMySqlDbName;
	private Text txtMySqlAdminId;
	private Text txtMySqlAdminPwd;
	private Text txtMySqlUserId;
	private Text txtMySqlUserPwd;
	private Text txtMySqlMinConn;
	private Text txtMySqlHoldConn;
	private Text txtMySqlMaxConn;
	private Text txtMySqlConnTimeout;
	private Text txtMySqlBreakConnTime;
	private Text txtMySqlDescription;
	private Composite pnlH2;
	private Text txtH2Name;
	private Text txtH2DbName;
	private Text txtH2AdminId;
	private Text txtH2AdminPwd;
	private Text txtH2UserId;
	private Text txtH2UserPwd;
	private Text txtH2MinConn;
	private Text txtH2HoldConn;
	private Text txtH2MaxConn;
	private Text txtH2ConnTimeout;
	private Text txtH2BreakConnTime;
	private Text txtH2Description;
	private Text txt_mssqllogin;
	private Text txt_mssqlpwd;
	
	private String loginname ="";
	private String loginpassword="";
	private Text txt_oracleloginname;
	private Text txt_oracleloginpwd;
	private Text txt_mysqllogin;
	private Text txt_mysqlpwd;
	private Text txt_h2login;
	private Text txt_h2pwd;
	private SecurityPWD sp = new SecurityPWD();
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ConnectionManager(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());
		
		tabFolder = new TabFolder(container, SWT.FLAT);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.left = new FormAttachment(0, 10);
		fd_tabFolder.bottom = new FormAttachment(0, 369);
		fd_tabFolder.top = new FormAttachment(0, 10);
		tabFolder.setLayoutData(fd_tabFolder);
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("\u5E38\u89C4");
		
		lstCommon = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		lstCommon.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					onConnectionSelected();
				}catch(SiteviewException ee){
					MessageDialog.openError(getShell(), "提示信息", ee.getMessage());
				}
			}
		});
		tabItem.setControl(lstCommon);
		lstCommon.setHeaderVisible(false);

		TableColumn tblclmnName = new TableColumn(lstCommon, SWT.NONE);
		tblclmnName.setWidth(145);
		tblclmnName.setText("Name");
		
//		TabItem tabPriv = new TabItem(tabFolder, SWT.NONE);
//		tabPriv.setText("个人");
//		
//		lstPriv = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
//		lstPriv.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				onConnectionSelected();
//			}
//		});
//		lstPriv.setHeaderVisible(false);
//		tabPriv.setControl(lstPriv);
//		
//		TableColumn tableColumn = new TableColumn(lstPriv, SWT.NONE);
//		tableColumn.setWidth(145);
//		tableColumn.setText("Name");
		
		btNew = new Button(container, SWT.DOWN);
		btNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(lstCommon.getItemCount()>0){
					MessageDialog.openInformation(getShell(), "提示信息", "当前只能包含一个有效连接.");
					return;
				}
				
				
				Menu menu = new Menu(btNew.getShell(), SWT.POP_UP);
				
//				MenuItem mntmsiteview = new MenuItem(menu, SWT.NONE);
//				mntmsiteview.setText("\u8FDE\u63A5\u5230SiteView\u5E94\u7528\u7A0B\u5E8F");
//				mntmsiteview.addSelectionListener(new SelectionAdapter() {
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						ConnectionDefType type = ConnectionDefType.Server;
//						if (tabFolder.getSelectionIndex() == 0){
//							ServerConnectionDef newdef = (ServerConnectionDef) cmgr.CreateNewConnectionDef(type , ConnectionDefGroup.Common );
//							newdef.set_Name(CommonPrefix + getNewName(cmgr));
//							newdef.set_Uri("http://");
//		
//							cmgr.AddConnectionDef(newdef);
//							TableItem ti = new TableItem(lstCommon, SWT.NONE);
//							ti.setText(newdef.get_DisplayName());
//							ti.setImage(Activator.getImage("img/Images.Server16.png"));
//						}else{
//							ServerConnectionDef newdef = (ServerConnectionDef) cmgr.CreateNewConnectionDef(type , ConnectionDefGroup.Personal );
//							newdef.set_Name(PersonalPrefix + getNewName(cmgr));
//							newdef.set_Uri("http://");
//							//lstPriv.add(newdef.get_DisplayName());
//							TableItem ti = new TableItem(lstPriv, SWT.NONE);
//							ti.setText(newdef.get_DisplayName());
//							ti.setImage(Activator.getImage("img/Images.Server16.png"));
//							cmgr.AddConnectionDef(newdef);
//						}
//					}
//				});
				
				MenuItem mntmoracle = new MenuItem(menu, SWT.NONE);
				mntmoracle.setText("连接到Oracle数据库");
				mntmoracle.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try{
						ConnectionDefType type = ConnectionDefType.InternalDatabase;
						if (tabFolder.getSelectionIndex() == 0){
							InternalDbConnectionDef newdef = (InternalDbConnectionDef) cmgr.CreateNewConnectionDef(type , ConnectionDefGroup.Common );
							newdef.set_Name(CommonPrefix + getNewName(cmgr));
							newdef.set_DbEngine(DatabaseEngine.Oracle);
							newdef.SetLoginMethod(newdef.AdminConnection, DbLoginMethod.Connection);
							newdef.SetLoginMethod(newdef.UserConnection, DbLoginMethod.Connection);
							newdef.BuildConnectionString(newdef.AdminConnection);
							newdef.BuildConnectionString(newdef.UserConnection);
							newdef.set_Provider(DataProviders.OracleClient);
	
					
							TableItem ti = new TableItem(lstCommon, SWT.NONE);
							ti.setText(newdef.get_DisplayName());
//							ti.setImage(Activator.getImage("img/Images.Icons.DatabaseConnect.png"));
							cmgr.AddConnectionDef(newdef);
						}else{
							InternalDbConnectionDef newdef = (InternalDbConnectionDef) cmgr.CreateNewConnectionDef(type , ConnectionDefGroup.Personal );
							newdef.set_Name(PersonalPrefix + getNewName(cmgr));
							newdef.set_DbEngine(DatabaseEngine.Oracle);
							newdef.SetLoginMethod(newdef.AdminConnection, DbLoginMethod.Connection);
							newdef.SetLoginMethod(newdef.UserConnection, DbLoginMethod.Connection);
							newdef.BuildConnectionString(newdef.AdminConnection);
							newdef.BuildConnectionString(newdef.UserConnection);
							newdef.set_Provider(DataProviders.OracleClient);
							//lstPriv.add(newdef.get_DisplayName());
							TableItem ti = new TableItem(lstPriv, SWT.NONE);
							ti.setText(newdef.get_DisplayName());
//							ti.setImage(Activator.getImage("img/Images.Icons.DatabaseConnect.png"));
							cmgr.AddConnectionDef(newdef);
						}
						}catch(SiteviewException ee){
							MessageDialog.openError(getShell(), "提示信息",ee.getMessage());
						}
					}
					
				});
				
				
				MenuItem mntmmysql = new MenuItem(menu, SWT.NONE);
				mntmmysql.setText("连接到MySql数据库");
				mntmmysql.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try{
						ConnectionDefType type = ConnectionDefType.InternalDatabase;
						if (tabFolder.getSelectionIndex() == 0){
							InternalDbConnectionDef newdef = (InternalDbConnectionDef) cmgr.CreateNewConnectionDef(type , ConnectionDefGroup.Common );
							newdef.set_Name(CommonPrefix + getNewName(cmgr));
							newdef.set_DbEngine(DatabaseEngine.MySql);
							newdef.SetLoginMethod(newdef.AdminConnection, DbLoginMethod.Connection);
							newdef.SetLoginMethod(newdef.UserConnection, DbLoginMethod.Connection);
							newdef.BuildConnectionString(newdef.AdminConnection);
							newdef.BuildConnectionString(newdef.UserConnection);
							newdef.set_Provider(DataProviders.MySqlClient);
					
							TableItem ti = new TableItem(lstCommon, SWT.NONE);
							ti.setText(newdef.get_DisplayName());
//							ti.setImage(Activator.getImage("img/Images.Icons.DatabaseConnect.png"));
							cmgr.AddConnectionDef(newdef);
						}else{
							InternalDbConnectionDef newdef = (InternalDbConnectionDef) cmgr.CreateNewConnectionDef(type , ConnectionDefGroup.Personal );
							newdef.set_Name(PersonalPrefix + getNewName(cmgr));
							newdef.set_DbEngine(DatabaseEngine.MySql);
							newdef.SetLoginMethod(newdef.AdminConnection, DbLoginMethod.Connection);
							newdef.SetLoginMethod(newdef.UserConnection, DbLoginMethod.Connection);
							newdef.BuildConnectionString(newdef.AdminConnection);
							newdef.BuildConnectionString(newdef.UserConnection);
							newdef.set_Provider(DataProviders.MySqlClient);
							//lstPriv.add(newdef.get_DisplayName());
							TableItem ti = new TableItem(lstPriv, SWT.NONE);
							ti.setText(newdef.get_DisplayName());
//							ti.setImage(Activator.getImage( "img/Images.Icons.DatabaseConnect.png"));
							cmgr.AddConnectionDef(newdef);
						}
						}catch(SiteviewException ee){
							MessageDialog.openError(getShell(), "提示信息", ee.getMessage());
						}
					}
				});
				//
				MenuItem mntmh2 = new MenuItem(menu, SWT.NONE);
				mntmh2.setText("连接到H2数据库");
				mntmh2.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try{
						ConnectionDefType type = ConnectionDefType.InternalDatabase;
						if (tabFolder.getSelectionIndex() == 0){
							InternalDbConnectionDef newdef = (InternalDbConnectionDef) cmgr.CreateNewConnectionDef(type , ConnectionDefGroup.Common );
							newdef.set_Name(CommonPrefix + getNewName(cmgr));
							newdef.set_DbEngine(DatabaseEngine.H2);
							newdef.SetLoginMethod(newdef.AdminConnection, DbLoginMethod.Connection);
							newdef.SetLoginMethod(newdef.UserConnection, DbLoginMethod.Connection);
							newdef.BuildConnectionString(newdef.AdminConnection);
							newdef.BuildConnectionString(newdef.UserConnection);
							newdef.set_Provider(DataProviders.H2Client);
					
							TableItem ti = new TableItem(lstCommon, SWT.NONE);
							ti.setText(newdef.get_DisplayName());
//							ti.setImage(Activator.getImage( "img/Images.Icons.DatabaseConnect.png"));
							cmgr.AddConnectionDef(newdef);
						}else{
							InternalDbConnectionDef newdef = (InternalDbConnectionDef) cmgr.CreateNewConnectionDef(type , ConnectionDefGroup.Personal );
							newdef.set_Name(PersonalPrefix + getNewName(cmgr));
							newdef.set_DbEngine(DatabaseEngine.H2);
							newdef.SetLoginMethod(newdef.AdminConnection, DbLoginMethod.Connection);
							newdef.SetLoginMethod(newdef.UserConnection, DbLoginMethod.Connection);
							newdef.BuildConnectionString(newdef.AdminConnection);
							newdef.BuildConnectionString(newdef.UserConnection);
							newdef.set_Provider(DataProviders.H2Client);
							//lstPriv.add(newdef.get_DisplayName());
							TableItem ti = new TableItem(lstPriv, SWT.NONE);
							ti.setText(newdef.get_DisplayName());
//							ti.setImage(Activator.getImage( "img/Images.Icons.DatabaseConnect.png"));
							cmgr.AddConnectionDef(newdef);
						}
						}catch(SiteviewException ee){
							MessageDialog.openError(getShell(), "提示信息", ee.getMessage());
						}
					}
				});
				
				MenuItem mntmsqlserver = new MenuItem(menu, SWT.NONE);
				mntmsqlserver.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ConnectionDefType type = ConnectionDefType.InternalDatabase;
						try{
						if (tabFolder.getSelectionIndex() == 0){
							InternalDbConnectionDef newdef = (InternalDbConnectionDef) cmgr.CreateNewConnectionDef(type , ConnectionDefGroup.Common );
							newdef.set_Name(CommonPrefix + getNewName(cmgr));
							newdef.set_DbEngine(DatabaseEngine.SqlServer);
							newdef.SetLoginMethod(newdef.AdminConnection, DbLoginMethod.Connection);
							newdef.SetLoginMethod(newdef.UserConnection, DbLoginMethod.Connection);
							newdef.BuildConnectionString(newdef.AdminConnection);
							newdef.BuildConnectionString(newdef.UserConnection);
							newdef.set_Provider(DataProviders.SqlClient);
							//lstCommon.add(newdef.get_DisplayName());
							TableItem ti = new TableItem(lstCommon, SWT.NONE);
							ti.setText(newdef.get_DisplayName());
//							ti.setImage(Activator.getImage("img/Images.Icons.DatabaseConnect.png"));
							cmgr.AddConnectionDef(newdef);
						}else{
							InternalDbConnectionDef newdef = (InternalDbConnectionDef) cmgr.CreateNewConnectionDef(type , ConnectionDefGroup.Personal );
							newdef.set_Name(PersonalPrefix + getNewName(cmgr));
							newdef.set_DbEngine(DatabaseEngine.SqlServer);
							newdef.SetLoginMethod(newdef.AdminConnection, DbLoginMethod.Connection);
							newdef.SetLoginMethod(newdef.UserConnection, DbLoginMethod.Connection);
							newdef.BuildConnectionString(newdef.AdminConnection);
							newdef.BuildConnectionString(newdef.UserConnection);
							newdef.set_Provider(DataProviders.SqlClient);
							//lstPriv.add(newdef.get_DisplayName());
							TableItem ti = new TableItem(lstPriv, SWT.NONE);
							ti.setText(newdef.get_DisplayName());
//							ti.setImage(Activator.getImage("img/Images.Icons.DatabaseConnect.png"));
							cmgr.AddConnectionDef(newdef);
						}
						}catch(SiteviewException ee){
							MessageDialog.openError(getShell(), "提示信息", ee.getMessage());
						}
					}
				});
				mntmsqlserver.setText("\u8FDE\u63A5\u5230SqlServer\u6570\u636E\u5E93");
				menu.setLocation(btNew.getShell().toDisplay(btNew.getLocation().x, btNew.getLocation().y+btNew.getBounds().height));
				btNew.setMenu(menu);
				menu.setVisible(true);
			}
		});
		
		FormData fd_btNew = new FormData();
		fd_btNew.top = new FormAttachment(tabFolder, 6);
		fd_btNew.left = new FormAttachment(tabFolder, 0, SWT.LEFT);
		fd_btNew.right = new FormAttachment(0, 82);
		btNew.setLayoutData(fd_btNew);
		btNew.setText("\u65B0\u5EFA");
		
		Button btDelete = new Button(container, SWT.NONE);
		btDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
				onDeleteConnection();
				}catch(SiteviewException ee){
					MessageDialog.openError(getShell(), "提示信息", ee.getMessage());
				}
			}
		});
		FormData fd_btDelete = new FormData();
		fd_btDelete.bottom = new FormAttachment(btNew, 0, SWT.BOTTOM);
		fd_btDelete.left = new FormAttachment(tabFolder, 84, SWT.LEFT);
		fd_btDelete.top = new FormAttachment(tabFolder, 6);
		fd_btDelete.right = new FormAttachment(100, -513);
		btDelete.setLayoutData(fd_btDelete);
		btDelete.setText("\u5220\u9664");
		
		pnlConnection = new Composite(container, SWT.NONE);
		fd_tabFolder.right = new FormAttachment(pnlConnection, -7);
		FormData fd_pnlConnection = new FormData();
		fd_pnlConnection.bottom = new FormAttachment(100, -63);
		fd_pnlConnection.top = new FormAttachment(0, 10);
		fd_pnlConnection.right = new FormAttachment(0, 669);
		fd_pnlConnection.left = new FormAttachment(0, 173);
		pnlConnection.setLayoutData(fd_pnlConnection);
		
		pnlConnection.setLayout(new StackLayout());
		
		pnlServer = new Composite(pnlConnection, SWT.NONE);
		
		CLabel lblNewLabel = new CLabel(pnlServer, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 42, 12);
		lblNewLabel.setText("\u7C7B\u578B\uFF1A");
		
		Label lblNewLabel_1 = new Label(pnlServer, SWT.NONE);
		lblNewLabel_1.setBounds(58, 10, 162, 12);
		lblNewLabel_1.setText("\u8FDE\u63A5\u5230Siteview\u5E94\u7528\u7A0B\u5E8F");
		
		Label label = new Label(pnlServer, SWT.NONE);
		label.setBounds(10, 31, 36, 12);
		label.setText("\u540D\u79F0\uFF1A");
		
		txtServerName = new Text(pnlServer, SWT.BORDER);
		txtServerName.setBounds(70, 28, 178, 18);
		
		Label lblUri = new Label(pnlServer, SWT.NONE);
		lblUri.setBounds(22, 57, 30, 12);
		lblUri.setText("URI\uFF1A");
		
		txtURI = new Text(pnlServer, SWT.BORDER);
		txtURI.setBounds(70, 54, 178, 18);
		
		Label label_1 = new Label(pnlServer, SWT.NONE);
		label_1.setBounds(16, 81, 36, 12);
		label_1.setText("\u538B\u7F29\uFF1A");
		
		cboZip = new Combo(pnlServer, SWT.READ_ONLY);
		cboZip.setBounds(70, 78, 178, 20);
		
		Label label_2 = new Label(pnlServer, SWT.NONE);
		label_2.setBounds(10, 115, 54, 12);
		label_2.setText("\u7F13\u5B58\u6A21\u5F0F\uFF1A");
		
		cboCache = new Combo(pnlServer, SWT.NONE);
		cboCache.setBounds(70, 107, 178, 20);
		
		Label label_6 = new Label(pnlServer, SWT.NONE);
		label_6.setBounds(22, 142, 36, 12);
		label_6.setText("\u8BF4\u660E\uFF1A");
		
		txtDescription = new Text(pnlServer, SWT.BORDER);
		txtDescription.setBounds(70, 139, 318, 18);
		
		Group group = new Group(pnlServer, SWT.NONE);
		group.setText("\u4EE3\u7406\u670D\u52A1\u5668");
		group.setBounds(10, 188, 453, 160);
		
		chkProxy = new Button(group, SWT.CHECK);
		chkProxy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onCheckProxy();
			}
		});
		chkProxy.setBounds(10, 20, 142, 16);
		chkProxy.setText("\u6307\u5B9A\u4EE3\u7406\u670D\u52A1\u5668");
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setBounds(10, 56, 54, 12);
		label_3.setText("\u7528\u6237\u540D\uFF1A");
		
		txtProxyName = new Text(group, SWT.BORDER);
		txtProxyName.setBounds(70, 53, 180, 18);
		
		Label label_4 = new Label(group, SWT.NONE);
		label_4.setBounds(10, 92, 54, 12);
		label_4.setText("\u5BC6\u7801\uFF1A");
		
		txtProxyPasswd = new Text(group, SWT.BORDER | SWT.PASSWORD);
		txtProxyPasswd.setBounds(70, 87, 180, 18);
		
		Label label_5 = new Label(group, SWT.NONE);
		label_5.setBounds(10, 133, 54, 12);
		label_5.setText("\u57DF\uFF1A");
		
		txtProxyDomain = new Text(group, SWT.BORDER);
		txtProxyDomain.setBounds(70, 127, 180, 18);
		
		Button btTestServer = new Button(pnlServer, SWT.NONE);
		btTestServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onConnectionTest();
			}
		});
		btTestServer.setBounds(391, 374, 72, 22);
		btTestServer.setText("\u6D4B\u8BD5");
		
		pnlSqlServer = new Composite(pnlConnection, SWT.NONE);
		
		Label lblNewLabel_2 = new Label(pnlSqlServer, SWT.NONE);
		lblNewLabel_2.setBounds(18, 10, 51, 15);
		lblNewLabel_2.setText("\u7C7B\u578B\uFF1A");
		
		Label lblsqlServer = new Label(pnlSqlServer, SWT.NONE);
		lblsqlServer.setBounds(77, 8, 257, 15);
		lblsqlServer.setText("\u8FDE\u63A5\u5230SQL Server\u6570\u636E\u5E93");
		
		Label label_7 = new Label(pnlSqlServer, SWT.NONE);
		label_7.setBounds(20, 31, 43, 15);
		label_7.setText("\u540D\u79F0\uFF1A");
		
		txtSqlName = new Text(pnlSqlServer, SWT.BORDER);
		txtSqlName.setBounds(77, 28, 274, 18);
		
		Label label_8 = new Label(pnlSqlServer, SWT.NONE);
		label_8.setBounds(9, 52, 49, 16);
		label_8.setText("\u670D\u52A1\u5668\uFF1A");
		
		txtSqlServerName = new Text(pnlSqlServer, SWT.BORDER);
		txtSqlServerName.setBounds(77, 52, 274, 18);
		
		Label label_9 = new Label(pnlSqlServer, SWT.NONE);
		label_9.setBounds(10, 74, 49, 16);
		label_9.setText("\u6570\u636E\u5E93\uFF1A");
		
		txtSqlDbName = new Text(pnlSqlServer, SWT.BORDER);
		txtSqlDbName.setBounds(77, 75, 274, 18);
		
		Group group_1 = new Group(pnlSqlServer, SWT.NONE);
		group_1.setText("\u7BA1\u7406\u5458\u6807\u8BC6/\u5BC6\u7801");
		group_1.setBounds(10, 103, 201, 83);
		
		Label label_10 = new Label(group_1, SWT.NONE);
		label_10.setBounds(7, 19, 39, 19);
		label_10.setText("\u6807\u8BC6\uFF1A");
		
		txtSqlAdminId = new Text(group_1, SWT.BORDER);
		txtSqlAdminId.setBounds(60, 20, 120, 18);
		
		Label label_11 = new Label(group_1, SWT.NONE);
		label_11.setBounds(7, 54, 39, 17);
		label_11.setText("\u5BC6\u7801\uFF1A");
		
		txtSqlAdminPwd = new Text(group_1, SWT.BORDER | SWT.PASSWORD);
		txtSqlAdminPwd.setBounds(61, 53, 119, 18);
		
		Group group_2 = new Group(pnlSqlServer, SWT.NONE);
		group_2.setText("\u7528\u6237\u6807\u8BC6/\u5BC6\u7801");
		group_2.setBounds(217, 103, 227, 83);
		
		Label label_13 = new Label(group_2, SWT.NONE);
		label_13.setText("\u5BC6\u7801\uFF1A");
		label_13.setBounds(11, 52, 47, 19);
		
		txtSqlUserId = new Text(group_2, SWT.BORDER);
		txtSqlUserId.setBounds(68, 20, 134, 18);
		
		Label label_12 = new Label(group_2, SWT.NONE);
		label_12.setBounds(10, 20, 45, 18);
		label_12.setText("\u6807\u8BC6\uFF1A");
		
		txtSqlUserPwd = new Text(group_2, SWT.BORDER | SWT.PASSWORD);
		txtSqlUserPwd.setBounds(68, 53, 134, 18);
		
		Group group_3 = new Group(pnlSqlServer, SWT.NONE);
		group_3.setText("\u8FDE\u63A5\u9650\u5236");
		group_3.setBounds(10, 194, 434, 82);
		
		Label label_14 = new Label(group_3, SWT.NONE);
		label_14.setBounds(4, 22, 72, 16);
		label_14.setText("\u6700\u5C11\u8FDE\u63A5\u6570\uFF1A");
		
		txtSqlMinConn = new Text(group_3, SWT.BORDER);
		txtSqlMinConn.setBounds(81, 20, 45, 18);
		
		Label label_15 = new Label(group_3, SWT.NONE);
		label_15.setBounds(142, 21, 80, 17);
		label_15.setText("\u4FDD\u7559\u8FDE\u63A5\u6570\uFF1A");
		
		txtSqlHoldConn = new Text(group_3, SWT.BORDER);
		txtSqlHoldConn.setBounds(224, 20, 45, 18);
		
		Label label_16 = new Label(group_3, SWT.NONE);
		label_16.setBounds(282, 20, 83, 18);
		label_16.setText("\u6700\u591A\u8FDE\u63A5\u6570\uFF1A");
		
		txtSqlMaxConn = new Text(group_3, SWT.BORDER);
		txtSqlMaxConn.setBounds(365, 18, 45, 18);
		
		Label label_17 = new Label(group_3, SWT.NONE);
		label_17.setBounds(10, 49, 66, 19);
		label_17.setText("\u8FDE\u63A5\u8D85\u65F6\uFF1A");
		
		txtSqlConnTimeout = new Text(group_3, SWT.BORDER);
		txtSqlConnTimeout.setBounds(87, 50, 54, 18);
		
		Label label_18 = new Label(group_3, SWT.NONE);
		label_18.setBounds(146, 50, 24, 18);
		label_18.setText("\u79D2");
		
		Label label_19 = new Label(group_3, SWT.NONE);
		label_19.setBounds(201, 53, 97, 15);
		label_19.setText("\u4E2D\u65AD\u8FDE\u63A5\u65F6\u95F4\uFF1A");
		
		txtSqlBreakConnTime = new Text(group_3, SWT.BORDER);
		txtSqlBreakConnTime.setBounds(301, 50, 45, 18);
		
		Label label_20 = new Label(group_3, SWT.NONE);
		label_20.setBounds(352, 51, 26, 17);
		label_20.setText("\u79D2");
		
		Label label_21 = new Label(pnlSqlServer, SWT.NONE);
		label_21.setBounds(10, 291, 51, 15);
		label_21.setText("\u8BF4\u660E\uFF1A");
		
		txtSqlDescription = new Text(pnlSqlServer, SWT.BORDER);
		txtSqlDescription.setBounds(71, 288, 257, 20);
		
		Button btSqlTest = new Button(pnlSqlServer, SWT.NONE);
		btSqlTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onConnectionTest();
			}
		});
		btSqlTest.setBounds(386, 358, 72, 22);
		btSqlTest.setText("\u6D4B\u8BD5");
		
		Link link = new Link(pnlSqlServer, SWT.NONE);
		link.setBounds(10, 360, 96, 18);
		link.setText("<a>\u547D\u4EE4\u8D85\u65F6\u8BBE\u7F6E</a>");
		
		Label lbl_mssqllogin = new Label(pnlSqlServer, SWT.NONE);
		lbl_mssqllogin.setBounds(9, 323, 43, 17);
		lbl_mssqllogin.setText("\u767B\u5F55\u540D:");
		
		txt_mssqllogin = new Text(pnlSqlServer, SWT.BORDER);
		txt_mssqllogin.setBounds(58, 320, 104, 20);
		
		Label lbl_mssqlpwd = new Label(pnlSqlServer, SWT.NONE);
		lbl_mssqlpwd.setText("\u5BC6\u7801:");
		lbl_mssqlpwd.setBounds(199, 323, 34, 17);
		
		txt_mssqlpwd = new Text(pnlSqlServer, SWT.BORDER | SWT.PASSWORD);
		txt_mssqlpwd.setBounds(235, 320, 168, 20);
		
		pnlOracle = new Composite(pnlConnection, SWT.NONE);
		
		pnlOracle.setBounds(-222, -83, 398, 334);
		
		Label label_22 = new Label(pnlOracle, SWT.NONE);
		label_22.setText("\u7C7B\u578B\uFF1A");
		label_22.setBounds(20, 8, 48, 16);
		
		Label lbloracle = new Label(pnlOracle, SWT.NONE);
		lbloracle.setText("\u8FDE\u63A5\u5230Oracle\u6570\u636E\u5E93");
		lbloracle.setBounds(76, 8, 268, 19);
		
		Label label_24 = new Label(pnlOracle, SWT.NONE);
		label_24.setText("\u540D\u79F0\uFF1A");
		label_24.setBounds(20, 36, 43, 18);
		
		txtOracleName = new Text(pnlOracle, SWT.BORDER);
		txtOracleName.setBounds(75, 32, 271, 18);
		
		Label label_25 = new Label(pnlOracle, SWT.NONE);
		label_25.setText("\u670D\u52A1\u540D\uFF1A");
		label_25.setBounds(8, 60, 60, 16);
		
		txtOracleHost = new Text(pnlOracle, SWT.BORDER);
		txtOracleHost.setBounds(75, 58, 271, 18);
		
		Group group_4 = new Group(pnlOracle, SWT.NONE);
		group_4.setText("\u7BA1\u7406\u5458\u6807\u8BC6/\u5BC6\u7801");
		group_4.setBounds(20, 110, 208, 82);
		
		Label label_27 = new Label(group_4, SWT.NONE);
		label_27.setText("\u6807\u8BC6\uFF1A");
		label_27.setBounds(10, 23, 44, 17);
		
		txtOracleAdminId = new Text(group_4, SWT.BORDER);
		txtOracleAdminId.setBounds(70, 20, 116, 18);
		
		Label label_28 = new Label(group_4, SWT.NONE);
		label_28.setText("\u5BC6\u7801\uFF1A");
		label_28.setBounds(10, 47, 36, 18);
		
		txtOracleAdminPwd = new Text(group_4, SWT.BORDER | SWT.PASSWORD);
		txtOracleAdminPwd.setBounds(70, 47, 116, 18);
		
		Group group_5 = new Group(pnlOracle, SWT.NONE);
		group_5.setText("\u7528\u6237\u6807\u8BC6/\u5BC6\u7801");
		group_5.setBounds(239, 110, 204, 82);
		
		
		Label label_30 = new Label(group_5, SWT.NONE);
		label_30.setText("\u5BC6\u7801\uFF1A");
		label_30.setBounds(8, 53, 50, 15);
		
		txtOracleUserId = new Text(group_5, SWT.BORDER);
		txtOracleUserId.setBounds(59, 20, 119, 18);
		
		Label label_29 = new Label(group_5, SWT.NONE);
		label_29.setText("\u6807\u8BC6\uFF1A");
		label_29.setBounds(8, 23, 48, 15);
		
		txtOracleUserPwd = new Text(group_5, SWT.BORDER | SWT.PASSWORD);
		txtOracleUserPwd.setBounds(61, 50, 117, 18);
		
		Group group_6 = new Group(pnlOracle, SWT.NONE);
		group_6.setText("\u8FDE\u63A5\u9650\u5236");
		group_6.setBounds(20, 198, 423, 91);
		
		Label label_31 = new Label(group_6, SWT.NONE);
		label_31.setText("\u6700\u5C11\u8FDE\u63A5\u6570\uFF1A");
		label_31.setBounds(10, 22, 73, 16);
		
		txtOracleMinConn = new Text(group_6, SWT.BORDER);
		txtOracleMinConn.setBounds(87, 20, 45, 18);
		
		Label label_32 = new Label(group_6, SWT.NONE);
		label_32.setText("\u4FDD\u7559\u8FDE\u63A5\u6570\uFF1A");
		label_32.setBounds(146, 23, 78, 18);
		
		txtOracleHoldConn = new Text(group_6, SWT.BORDER);
		txtOracleHoldConn.setBounds(226, 20, 45, 18);
		
		Label label_33 = new Label(group_6, SWT.NONE);
		label_33.setText("\u6700\u591A\u8FDE\u63A5\u6570\uFF1A");
		label_33.setBounds(285, 23, 74, 19);
		
		txtOracleMaxConn = new Text(group_6, SWT.BORDER);
		txtOracleMaxConn.setBounds(363, 20, 45, 18);
		
		Label label_34 = new Label(group_6, SWT.NONE);
		label_34.setText("\u8FDE\u63A5\u8D85\u65F6\uFF1A");
		label_34.setBounds(10, 58, 63, 16);
		
		txtOracleConnTimeout = new Text(group_6, SWT.BORDER);
		txtOracleConnTimeout.setBounds(87, 56, 54, 18);
		
		Label label_35 = new Label(group_6, SWT.NONE);
		label_35.setText("\u79D2");
		label_35.setBounds(146, 57, 24, 17);
		
		Label label_36 = new Label(group_6, SWT.NONE);
		label_36.setText("\u4E2D\u65AD\u8FDE\u63A5\u65F6\u95F4\uFF1A");
		label_36.setBounds(181, 58, 92, 16);
		
		txtOracleBreakConnTime = new Text(group_6, SWT.BORDER);
		txtOracleBreakConnTime.setBounds(286, 56, 45, 18);
		
		Label label_37 = new Label(group_6, SWT.NONE);
		label_37.setText("\u79D2");
		label_37.setBounds(334, 57, 23, 17);
		
		Label label_38 = new Label(pnlOracle, SWT.NONE);
		label_38.setText("\u8BF4\u660E\uFF1A");
		label_38.setBounds(20, 309, 46, 16);
		
		txtOracleDescription = new Text(pnlOracle, SWT.BORDER);
		txtOracleDescription.setBounds(80, 309, 308, 18);
		
		Button btOracleTest = new Button(pnlOracle, SWT.NONE);
		btOracleTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onConnectionTest();
			}
		});
		btOracleTest.setText("\u6D4B\u8BD5");
		btOracleTest.setBounds(391, 340, 72, 19);
		
		Link link_1 = new Link(pnlOracle, 0);
		link_1.setText("<a>\u547D\u4EE4\u8D85\u65F6\u8BBE\u7F6E</a>");
		link_1.setBounds(10, 340, 97, 16);
		
		Label label_23 = new Label(pnlOracle, SWT.NONE);
		label_23.setText("\u5B9E\u4F8B\u540D\uFF1A");
		label_23.setBounds(6, 84, 60, 16);
		
		txtOracleInstanceName = new Text(pnlOracle, SWT.BORDER);
		txtOracleInstanceName.setBounds(73, 82, 271, 18);
		
		Label label_26 = new Label(pnlOracle, SWT.NONE);
		label_26.setText("\u767B\u5F55\u540D:");
		label_26.setBounds(20, 368, 43, 17);
		
		txt_oracleloginname = new Text(pnlOracle, SWT.BORDER);
		txt_oracleloginname.setBounds(69, 365, 104, 20);
		
		Label label_39 = new Label(pnlOracle, SWT.NONE);
		label_39.setText("\u5BC6\u7801:");
		label_39.setBounds(210, 368, 34, 17);
		
		txt_oracleloginpwd = new Text(pnlOracle, SWT.BORDER | SWT.PASSWORD);
		txt_oracleloginpwd.setBounds(246, 365, 168, 20);
		
		intVerifyListener = new VerifyListener(){

			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Pattern.matches("^[0-9]+$", e.text);//"0123456789".indexOf(e.text) >= 0 ;
				Text txt = (Text) e.widget;
				if (txt.getText().length() == 1 && e.keyCode == Event.BACK_SPACE )
					txt.setText("0");
			}
			
		};
		
		//mysql panel
		pnlMySql = new Composite(pnlConnection, SWT.NONE);
		
		Label lblName = new Label(pnlMySql, SWT.NONE);
		lblName.setBounds(20, 13, 36, 17);
		lblName.setText("\u7C7B\u578B\uFF1A");
		
		Label lblmysqlServer = new Label(pnlMySql, SWT.NONE);
		lblmysqlServer.setBounds(67, 10, 168, 17);
		lblmysqlServer.setText("\u8FDE\u63A5\u5230MySQL\u6570\u636E\u5E93");
		
		Label label_mysql_7 = new Label(pnlMySql, SWT.NONE);
		label_mysql_7.setBounds(20, 34, 36, 17);
		label_mysql_7.setText("\u540D\u79F0\uFF1A");
		
		txtMySqlName = new Text(pnlMySql, SWT.BORDER);
		txtMySqlName.setBounds(67, 28, 168, 18);
		
		Label label_mysql_8 = new Label(pnlMySql, SWT.NONE);
		label_mysql_8.setBounds(10, 52, 48, 17);
		label_mysql_8.setText("\u670D\u52A1\u5668\uFF1A");
		
		txtMySqlServerName = new Text(pnlMySql, SWT.BORDER);
		txtMySqlServerName.setBounds(67, 48, 168, 18);
		
		Label label_mysql_9 = new Label(pnlMySql, SWT.NONE);
		label_mysql_9.setBounds(10, 72, 48, 17);
		label_mysql_9.setText("\u6570\u636E\u5E93\uFF1A");
		
		txtMySqlDbName = new Text(pnlMySql, SWT.BORDER);
		txtMySqlDbName.setBounds(67, 69, 168, 18);
		
		Group group_mysql_1 = new Group(pnlMySql, SWT.NONE);
		group_mysql_1.setText("\u7BA1\u7406\u5458\u6807\u8BC6/\u5BC6\u7801");
		group_mysql_1.setBounds(20, 95, 189, 63);
		
		Label label_mysql_10 = new Label(group_mysql_1, SWT.NONE);
		label_mysql_10.setBounds(10, 21, 36, 17);
		label_mysql_10.setText("\u6807\u8BC6\uFF1A");
		
		txtMySqlAdminId = new Text(group_mysql_1, SWT.BORDER);
		txtMySqlAdminId.setBounds(91, 18, 88, 18);
		
		Label label_mysql_11 = new Label(group_mysql_1, SWT.NONE);
		label_mysql_11.setBounds(10, 39, 36, 17);
		label_mysql_11.setText("\u5BC6\u7801\uFF1A");
		
		txtMySqlAdminPwd = new Text(group_mysql_1, SWT.BORDER | SWT.PASSWORD);
		txtMySqlAdminPwd.setBounds(91, 36, 88, 18);
		
		Group group_mysql_2 = new Group(pnlMySql, SWT.NONE);
		group_mysql_2.setText("\u7528\u6237\u6807\u8BC6/\u5BC6\u7801");
		group_mysql_2.setBounds(226, 93, 178, 65);
		
		Label label_mysql_13 = new Label(group_mysql_2, SWT.NONE);
		label_mysql_13.setText("\u5BC6\u7801\uFF1A");
		label_mysql_13.setBounds(10, 38, 68, 17);
		
		txtMySqlUserId = new Text(group_mysql_2, SWT.BORDER);
		txtMySqlUserId.setBounds(84, 17, 84, 18);
		
		Label label_mysql_12 = new Label(group_mysql_2, SWT.NONE);
		label_mysql_12.setBounds(10, 20, 68, 17);
		label_mysql_12.setText("\u6807\u8BC6\uFF1A");
		
		txtMySqlUserPwd = new Text(group_mysql_2, SWT.BORDER | SWT.PASSWORD);
		txtMySqlUserPwd.setBounds(84, 38, 84, 18);
		
		Group group_mysql_3 = new Group(pnlMySql, SWT.NONE);
		group_mysql_3.setText("\u8FDE\u63A5\u9650\u5236");
		group_mysql_3.setBounds(10, 161, 394, 88);
		
		Label label_mysql_14 = new Label(group_mysql_3, SWT.NONE);
		label_mysql_14.setBounds(10, 30, 72, 17);
		label_mysql_14.setText("\u6700\u5C11\u8FDE\u63A5\u6570\uFF1A");
		
		txtMySqlMinConn = new Text(group_mysql_3, SWT.BORDER);
		txtMySqlMinConn.setBounds(82, 27, 45, 18);
		
		Label label_mysql_15 = new Label(group_mysql_3, SWT.NONE);
		label_mysql_15.setBounds(130, 30, 72, 17);
		label_mysql_15.setText("\u4FDD\u7559\u8FDE\u63A5\u6570\uFF1A");
		
		txtMySqlHoldConn = new Text(group_mysql_3, SWT.BORDER);
		txtMySqlHoldConn.setBounds(205, 27, 45, 18);
		
		Label label_mysql_16 = new Label(group_mysql_3, SWT.NONE);
		label_mysql_16.setBounds(261, 30, 72, 17);
		label_mysql_16.setText("\u6700\u591A\u8FDE\u63A5\u6570\uFF1A");
		
		txtMySqlMaxConn = new Text(group_mysql_3, SWT.BORDER);
		txtMySqlMaxConn.setBounds(339, 27, 45, 18);
		
		Label label_mysql_17 = new Label(group_mysql_3, SWT.NONE);
		label_mysql_17.setBounds(10, 68, 60, 17);
		label_mysql_17.setText("\u8FDE\u63A5\u8D85\u65F6\uFF1A");
		
		txtMySqlConnTimeout = new Text(group_mysql_3, SWT.BORDER);
		txtMySqlConnTimeout.setBounds(73, 65, 54, 18);
		
		Label label_mysql_18 = new Label(group_mysql_3, SWT.NONE);
		label_mysql_18.setBounds(133, 68, 12, 17);
		label_mysql_18.setText("\u79D2");
		
		Label label_mysql_19 = new Label(group_mysql_3, SWT.NONE);
		label_mysql_19.setBounds(151, 66, 84, 17);
		label_mysql_19.setText("\u4E2D\u65AD\u8FDE\u63A5\u65F6\u95F4\uFF1A");
		
		txtMySqlBreakConnTime = new Text(group_mysql_3, SWT.BORDER);
		txtMySqlBreakConnTime.setBounds(241, 63, 73, 18);
		
		Label label_mysql_20 = new Label(group_mysql_3, SWT.NONE);
		label_mysql_20.setBounds(329, 65, 12, 17);
		label_mysql_20.setText("\u79D2");
		
		Label label_mysql_21 = new Label(pnlMySql, SWT.NONE);
		label_mysql_21.setBounds(10, 255, 30, 18);
		label_mysql_21.setText("\u8BF4\u660E\uFF1A");
		
		txtMySqlDescription = new Text(pnlMySql, SWT.BORDER);
		txtMySqlDescription.setBounds(95, 252, 257, 18);
		
		Button btMySqlTest = new Button(pnlMySql, SWT.NONE);
		btMySqlTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onConnectionTest();
			}
		});
		btMySqlTest.setBounds(316, 302, 72, 22);
		btMySqlTest.setText("\u6D4B\u8BD5");
		
		Link mysqllink = new Link(pnlMySql, SWT.NONE);
		mysqllink.setBounds(10, 302, 96, 22);
		mysqllink.setText("<a>\u547D\u4EE4\u8D85\u65F6\u8BBE\u7F6E</a>");
		
		Label label_40 = new Label(pnlMySql, SWT.NONE);
		label_40.setText("\u767B\u5F55\u540D:");
		label_40.setBounds(10, 346, 43, 17);
		
		txt_mysqllogin = new Text(pnlMySql, SWT.BORDER);
		txt_mysqllogin.setBounds(59, 343, 104, 20);
		
		Label label_41 = new Label(pnlMySql, SWT.NONE);
		label_41.setText("\u5BC6\u7801:");
		label_41.setBounds(200, 346, 34, 17);
		
		txt_mysqlpwd = new Text(pnlMySql, SWT.BORDER | SWT.PASSWORD);
		txt_mysqlpwd.setBounds(236, 343, 168, 20);
		
		
		//h2 panel
		pnlH2 = new Composite(pnlConnection, SWT.NONE);
		
		Label lblH2Name = new Label(pnlH2, SWT.NONE);
		lblH2Name.setBounds(20, 13, 36, 17);
		lblH2Name.setText("\u7C7B\u578B\uFF1A");
		
		Label lblH2Server = new Label(pnlH2, SWT.NONE);
		lblH2Server.setBounds(67, 10, 268, 17);
		lblH2Server.setText("\u8FDE\u63A5\u5230H2\u6570\u636E\u5E93");
		
		Label label_h2_7 = new Label(pnlH2, SWT.NONE);
		label_h2_7.setBounds(20, 38, 36, 17);
		label_h2_7.setText("\u540D\u79F0\uFF1A");
		
		txtH2Name = new Text(pnlH2, SWT.BORDER);
		txtH2Name.setBounds(67, 38, 268, 18);
		
		Label label_h2_9 = new Label(pnlH2, SWT.NONE);
		label_h2_9.setBounds(10, 68, 48, 17);
		label_h2_9.setText("\u6570\u636E\u5E93\uFF1A");
		
		txtH2DbName = new Text(pnlH2, SWT.BORDER);
		txtH2DbName.setBounds(67, 66, 268, 18);
		
		Group group_h2_1 = new Group(pnlH2, SWT.NONE);
		group_h2_1.setText("\u7BA1\u7406\u5458\u6807\u8BC6/\u5BC6\u7801");
		group_h2_1.setBounds(20, 95, 189, 63);
		
		Label label_h2_10 = new Label(group_h2_1, SWT.NONE);
		label_h2_10.setBounds(10, 21, 36, 17);
		label_h2_10.setText("\u6807\u8BC6\uFF1A");
	
		txtH2AdminId = new Text(group_h2_1, SWT.BORDER);
		txtH2AdminId.setBounds(91, 18, 88, 18);
		
		Label label_h2_11 = new Label(group_h2_1, SWT.NONE);
		label_h2_11.setBounds(10, 39, 36, 17);
		label_h2_11.setText("\u5BC6\u7801\uFF1A");
		
		txtH2AdminPwd = new Text(group_h2_1, SWT.BORDER | SWT.PASSWORD);
		txtH2AdminPwd.setBounds(91, 36, 88, 18);
		
		Group group_h2_2 = new Group(pnlH2, SWT.NONE);
		group_h2_2.setText("\u7528\u6237\u6807\u8BC6/\u5BC6\u7801");
		group_h2_2.setBounds(226, 93, 178, 65);
		
		Label label_h2_13 = new Label(group_h2_2, SWT.NONE);
		label_h2_13.setText("\u5BC6\u7801\uFF1A");
		label_h2_13.setBounds(10, 38, 68, 17);
		
		txtH2UserId = new Text(group_h2_2, SWT.BORDER);
		txtH2UserId.setBounds(84, 17, 84, 18);
		
		Label label_h2_12 = new Label(group_h2_2, SWT.NONE);
		label_h2_12.setBounds(10, 20, 68, 17);
		label_h2_12.setText("\u6807\u8BC6\uFF1A");
		
		txtH2UserPwd = new Text(group_h2_2, SWT.BORDER | SWT.PASSWORD);
		txtH2UserPwd.setBounds(84, 38, 84, 18);
		
		Group group_h2_3 = new Group(pnlH2, SWT.NONE);
		group_h2_3.setText("\u8FDE\u63A5\u9650\u5236");
		group_h2_3.setBounds(10, 161, 394, 88);
		
		Label label_h2_14 = new Label(group_h2_3, SWT.NONE);
		label_h2_14.setBounds(10, 30, 72, 17);
		label_h2_14.setText("\u6700\u5C11\u8FDE\u63A5\u6570\uFF1A");
		
		txtH2MinConn = new Text(group_h2_3, SWT.BORDER);
		txtH2MinConn.setBounds(82, 27, 45, 18);
		
		Label label_h2_15 = new Label(group_h2_3, SWT.NONE);
		label_h2_15.setBounds(130, 30, 72, 17);
		label_h2_15.setText("\u4FDD\u7559\u8FDE\u63A5\u6570\uFF1A");
		
		txtH2HoldConn = new Text(group_h2_3, SWT.BORDER);
		txtH2HoldConn.setBounds(205, 27, 45, 18);
		
		Label label_h2_16 = new Label(group_h2_3, SWT.NONE);
		label_h2_16.setBounds(261, 30, 72, 17);
		label_h2_16.setText("\u6700\u591A\u8FDE\u63A5\u6570\uFF1A");
		
		txtH2MaxConn = new Text(group_h2_3, SWT.BORDER);
		txtH2MaxConn.setBounds(339, 27, 45, 18);
		
		Label label_h2_17 = new Label(group_h2_3, SWT.NONE);
		label_h2_17.setBounds(10, 68, 60, 17);
		label_h2_17.setText("\u8FDE\u63A5\u8D85\u65F6\uFF1A");
		
		txtH2ConnTimeout = new Text(group_h2_3, SWT.BORDER);
		txtH2ConnTimeout.setBounds(73, 65, 54, 18);
		
		Label label_h2_18 = new Label(group_h2_3, SWT.NONE);
		label_h2_18.setBounds(133, 68, 12, 17);
		label_h2_18.setText("\u79D2");
		
		Label label_h2_19 = new Label(group_h2_3, SWT.NONE);
		label_h2_19.setBounds(151, 66, 84, 17);
		label_h2_19.setText("\u4E2D\u65AD\u8FDE\u63A5\u65F6\u95F4\uFF1A");
		
		txtH2BreakConnTime = new Text(group_h2_3, SWT.BORDER);
		txtH2BreakConnTime.setBounds(241, 63, 73, 18);
		
		Label label_h2_20 = new Label(group_h2_3, SWT.NONE);
		label_h2_20.setBounds(329, 65, 12, 17);
		label_h2_20.setText("\u79D2");
		
		Label label_h2_21 = new Label(pnlH2, SWT.NONE);
		label_h2_21.setBounds(10, 255, 30, 18);
		label_h2_21.setText("\u8BF4\u660E\uFF1A");
		
		txtH2Description = new Text(pnlH2, SWT.BORDER);
		txtH2Description.setBounds(95, 252, 257, 18);
		
		Button btH2Test = new Button(pnlH2, SWT.NONE);
		btH2Test.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onConnectionTest();
			}
		});
		btH2Test.setBounds(332, 338, 72, 22);
		btH2Test.setText("\u6D4B\u8BD5");
		
		Label label_42 = new Label(pnlH2, SWT.NONE);
		label_42.setText("\u767B\u5F55\u540D:");
		label_42.setBounds(10, 302, 43, 17);
		
		txt_h2login = new Text(pnlH2, SWT.BORDER);
		txt_h2login.setBounds(59, 299, 104, 20);
		
		Label label_43 = new Label(pnlH2, SWT.NONE);
		label_43.setText("\u5BC6\u7801:");
		label_43.setBounds(200, 302, 34, 17);
		
		txt_h2pwd = new Text(pnlH2, SWT.BORDER | SWT.PASSWORD);
		txt_h2pwd.setBounds(236, 299, 168, 20);
		
		try{
			loadConnections();
		}catch(SiteviewException e){
			MessageDialog.openError(getShell(), "提示信息", e.getMessage());
		} catch (DocumentException e) {
			MessageDialog.openError(getShell(), "提示信息", e.getMessage());
		}
		return container;
	}
	
	private String getNewName(IConnectionDefManager mgr) throws SiteviewException{
		String sName = "新连接";
		int i = 0;
		Collection cmnList = mgr.ConnectionsForGroup(ConnectionDefGroup.Common);
		//ICollection prvList = mgr.ConnectionsForGroup(ConnectionDefGroup.Personal);
		START:
		while(true){
			++i;
			Iterator it = cmnList.iterator();
			while(it.hasNext()){
				ConnectionDef def = (ConnectionDef)it.next();
				if (def.get_DisplayName().equals(sName + i))
					continue START;
			}
//			it = prvList.GetEnumerator();
//			while(it.MoveNext()){
//				ConnectionDef def = (ConnectionDef)it.get_Current();
//				if (def.get_DisplayName().equals(sName + i))
//					continue START;
//			}
			break;
		}
		
		return sName + i;
	}

	protected void onCheckProxy() {
		if (m_curConnDef == null && m_curConnDef instanceof ServerConnectionDef) return;
		ServerConnectionDef sdf = (ServerConnectionDef)m_curConnDef;
		if (chkProxy.getSelection()){
			sdf.get_ProxyInfo().set_UseProxyServer(true);
			txtProxyName.setEnabled(true);
			txtProxyPasswd.setEnabled(true);
			txtProxyDomain.setEnabled(true);
		}else{
			sdf.get_ProxyInfo().set_UseProxyServer(false);
			txtProxyName.setEnabled(false);
			txtProxyPasswd.setEnabled(false);
			txtProxyDomain.setEnabled(false);
		}
		
	}

	protected void onConnectionTest() {
		boolean flag = false;
		String[] err = new String[1];
		String[] warn = new String[1];
		
		try{
			this.setCurConnectionValue();
			
			TestConnection testConn = new TestConnection();
			if (this.m_curConnDef.get_InternalDatabaseConnection()){
				InternalDbConnectionDef dbdf = (InternalDbConnectionDef)m_curConnDef;
				flag = testDatabaseConnection(testConn,dbdf,dbdf.UserConnection,err,warn);
				if (flag){
					flag = testDatabaseConnection(testConn,dbdf,dbdf.AdminConnection,err,warn);
				}
			}else if(m_curConnDef.get_ServerConnection()){
				ServerConnectionDef sdf = (ServerConnectionDef)m_curConnDef;
				flag = testConn.TestServerConnection(sdf.get_Uri(), err);
			}
			if (flag){
				MessageBox messageBox = new MessageBox(this.getShell(),SWT.ICON_INFORMATION);
				messageBox.setText("连接测试");
				messageBox.setMessage("连接成功！");
				messageBox.open();
				
			}else{
				MessageBox messageBox = new MessageBox(this.getShell(),SWT.ICON_ERROR);
				messageBox.setText("连接测试");
				String msg = "连接失败";
				if (err[0]!=null && err[0].length()>0)
					msg = msg + ",错误：" + err[0];
				if (warn[0]!=null && warn[0].length()>0)
					msg = msg + ",警告：" + warn[0];
				
				msg = msg + "！";
				
				messageBox.setMessage(msg);
				messageBox.open();
			}
		}catch(Exception e){
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(this.getShell(),SWT.ICON_ERROR);
			messageBox.setText("错误");
			messageBox.setMessage(e.getMessage());
			messageBox.open();
			
		}
		
	}
	
	private boolean testDatabaseConnection(TestConnection tc,DbConnectionDef def,int connectionType,String[] err,String[] warning) throws ArgumentException, SiteviewException{
		boolean flag = false;
		String connString = def.GetConnectionString(connectionType);
		if (connString == null || connString.length() == 0){
			connString = def.BuildConnectionString(connectionType);
		}
		if (connString.length()>0){
			flag = tc.TestDbConnection(def.get_Provider(), connString, err, warning);
		}
		return flag;
	}

	protected void onDeleteConnection() throws SiteviewException {
		if (this.tabFolder.getSelectionIndex()==0){
			if (lstCommon.getSelectionIndex()<0) return;
			
			String cName = lstCommon.getItem(lstCommon.getSelectionIndex()).getText();
			ConnectionDef def = cmgr.GetConnectionDef(CommonPrefix + cName);
			if (def!=null){
				cmgr.DeleteConnectionDef(def);
				lstCommon.remove(lstCommon.getSelectionIndex());
				m_curConnDef = null;
				((StackLayout)pnlConnection.getLayout()).topControl = null;
				pnlConnection.layout();
			}
		}else{
			if (lstPriv.getSelectionIndex() < 0 ) return;
			
			String cName = lstPriv.getItem(lstPriv.getSelectionIndex()).getText();
			ConnectionDef def = cmgr.GetConnectionDef(PersonalPrefix + cName);
			if (def!=null){
				cmgr.DeleteConnectionDef(def);
				lstPriv.remove(lstPriv.getSelectionIndex());
				m_curConnDef = null;
				((StackLayout)pnlConnection.getLayout()).topControl = null;
				pnlConnection.layout();
				
			}
		}
		
	}

	private void ShowConnectionDef(ConnectionDef cdf,Object lst,int oldIndex) throws ArgumentException {
		
		try {
			getlogininfo();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
		
		if (cdf.get_InternalDatabaseConnection()){
			InternalDbConnectionDef dbdf = (InternalDbConnectionDef) cdf;
			if (dbdf.get_DbEngine() == DatabaseEngine.SqlServer){
				((StackLayout)pnlConnection.getLayout()).topControl = pnlSqlServer;
				pnlConnection.layout();
				dbdf.LoadDataFromConnectionStrings();
				Text[] numFields = new Text[]{txtSqlMinConn,txtSqlHoldConn,txtSqlMaxConn,txtSqlConnTimeout,txtSqlBreakConnTime};
				for (Text txt: numFields){
					txt.removeVerifyListener(intVerifyListener);
				}
				txtSqlName.setText(dbdf.get_DisplayName());
				txtSqlServerName.setText(dbdf.get_ServerName());
				txtSqlDbName.setText(dbdf.get_DataSource());
				txtSqlAdminId.setText(dbdf.GetUserId(dbdf.AdminConnection));
				txtSqlAdminPwd.setText(dbdf.GetPassword(dbdf.AdminConnection));
				txtSqlUserId.setText(dbdf.GetUserId(dbdf.UserConnection));
				txtSqlUserPwd.setText(dbdf.GetPassword(dbdf.UserConnection));
				txtSqlMinConn.setText(dbdf.get_MinConnections().toString());
				txtSqlHoldConn.setText(dbdf.get_HoldConnections().toString());
				txtSqlMaxConn.setText(dbdf.get_MaxConnections().toString());
				txtSqlConnTimeout.setText(dbdf.get_ConnectionTimeout().toString());
				txtSqlBreakConnTime.setText(dbdf.get_FadeTimeout().toString());
				txtSqlDescription.setText(dbdf.get_Description());
				txt_mssqllogin.setText(loginname);
				txt_mssqlpwd.setText(loginpassword);
				for (Text txt: numFields){
					txt.addVerifyListener(intVerifyListener);
				}
			}else if(dbdf.get_DbEngine() == DatabaseEngine.Oracle){
				((StackLayout)pnlConnection.getLayout()).topControl = pnlOracle;
				pnlConnection.layout();
				dbdf.LoadDataFromConnectionStrings();
				
				Text[] numFields = new Text[]{txtOracleMinConn,txtOracleHoldConn,txtOracleMaxConn,txtOracleConnTimeout,txtOracleBreakConnTime};
				for (Text txt: numFields){
					txt.removeVerifyListener(intVerifyListener);
				}
				txtOracleName.setText(dbdf.get_DisplayName());
				txtOracleHost.setText(dbdf.get_ServerName());
				txtOracleInstanceName.setText(dbdf.get_DataSource());
				txtOracleAdminId.setText(dbdf.GetUserId(dbdf.AdminConnection));
				txtOracleAdminPwd.setText(dbdf.GetPassword(dbdf.AdminConnection));
				txtOracleUserId.setText(dbdf.GetUserId(dbdf.UserConnection));
				txtOracleUserPwd.setText(dbdf.GetPassword(dbdf.UserConnection));
				txtOracleMinConn.setText(dbdf.get_MinConnections().toString());
				txtOracleHoldConn.setText(dbdf.get_HoldConnections().toString());
				txtOracleMaxConn.setText(dbdf.get_MaxConnections().toString());
				txtOracleConnTimeout.setText(dbdf.get_ConnectionTimeout().toString());
				txtOracleBreakConnTime.setText(dbdf.get_FadeTimeout().toString());
				txtOracleDescription.setText(dbdf.get_Description());
				txt_oracleloginname.setText(loginname);
				txt_oracleloginpwd.setText(loginpassword);
				for (Text txt: numFields){
					txt.addVerifyListener(intVerifyListener);
				}
			}
			else if (dbdf.get_DbEngine() == DatabaseEngine.H2){
				((StackLayout)pnlConnection.getLayout()).topControl = this.pnlH2;
				pnlConnection.layout();
				dbdf.LoadDataFromConnectionStrings();
				Text[] numFields = new Text[]{txtH2MinConn,txtH2HoldConn,txtH2MaxConn,txtH2ConnTimeout,txtH2BreakConnTime};
				for (Text txt: numFields){
					txt.removeVerifyListener(intVerifyListener);
				}
				txtH2Name.setText(dbdf.get_DisplayName());
				txtH2DbName.setText(dbdf.get_DataSource());
				txtH2AdminId.setText(dbdf.GetUserId(dbdf.AdminConnection));
				txtH2AdminPwd.setText(dbdf.GetPassword(dbdf.AdminConnection));
				txtH2UserId.setText(dbdf.GetUserId(dbdf.UserConnection));
				txtH2UserPwd.setText(dbdf.GetPassword(dbdf.UserConnection));
				txtH2MinConn.setText(dbdf.get_MinConnections().toString());
				txtH2HoldConn.setText(dbdf.get_HoldConnections().toString());
				txtH2MaxConn.setText(dbdf.get_MaxConnections().toString());
				txtH2ConnTimeout.setText(dbdf.get_ConnectionTimeout().toString());
				txtH2BreakConnTime.setText(dbdf.get_FadeTimeout().toString());
				txtH2Description.setText(dbdf.get_Description());
				txt_h2login.setText(loginname);
				txt_h2pwd.setText(loginpassword);
				for (Text txt: numFields){
					txt.addVerifyListener(intVerifyListener);
				}
			}
			else if (dbdf.get_DbEngine() == DatabaseEngine.MySql){
				((StackLayout)pnlConnection.getLayout()).topControl = this.pnlMySql;
				pnlConnection.layout();
				dbdf.LoadDataFromConnectionStrings();
				Text[] numFields = new Text[]{txtMySqlMinConn,txtMySqlHoldConn,txtMySqlMaxConn,txtMySqlConnTimeout,txtMySqlBreakConnTime};
				for (Text txt: numFields){
					txt.removeVerifyListener(intVerifyListener);
				}
				txtMySqlName.setText(dbdf.get_DisplayName());
				txtMySqlServerName.setText(dbdf.get_ServerName());
				txtMySqlDbName.setText(dbdf.get_DataSource());
				txtMySqlAdminId.setText(dbdf.GetUserId(dbdf.AdminConnection));
				txtMySqlAdminPwd.setText(dbdf.GetPassword(dbdf.AdminConnection));
				txtMySqlUserId.setText(dbdf.GetUserId(dbdf.UserConnection));
				txtMySqlUserPwd.setText(dbdf.GetPassword(dbdf.UserConnection));
				txtMySqlMinConn.setText(dbdf.get_MinConnections().toString());
				txtMySqlHoldConn.setText(dbdf.get_HoldConnections().toString());
				txtMySqlMaxConn.setText(dbdf.get_MaxConnections().toString());
				txtMySqlConnTimeout.setText(dbdf.get_ConnectionTimeout().toString());
				txtMySqlBreakConnTime.setText(dbdf.get_FadeTimeout().toString());
				txtMySqlDescription.setText(dbdf.get_Description());
				txt_mysqllogin.setText(loginname);
				txt_mysqlpwd.setText(loginpassword);
				for (Text txt: numFields){
					txt.addVerifyListener(intVerifyListener);
				}
			}
		}else if(cdf.get_ServerConnection()){
			ServerConnectionDef sdf = (ServerConnectionDef)cdf;
			((StackLayout)pnlConnection.getLayout()).topControl = pnlServer;
			pnlConnection.layout();
			txtServerName.setText(sdf.get_DisplayName());
			txtURI.setText(sdf.get_Uri());
			if (sdf.get_ProxyInfo().get_UseProxyServer()){
				this.chkProxy.setSelection(true);
				this.txtProxyName.setEnabled(true);
				this.txtProxyName.setText(sdf.get_ProxyInfo().get_Username());
				this.txtProxyPasswd.setEnabled(true);
				this.txtProxyPasswd.setText(sdf.get_ProxyInfo().get_Password());
				this.txtProxyDomain.setEnabled(true);
				this.txtProxyDomain.setText(sdf.get_ProxyInfo().get_Domain());
			}else{
				this.chkProxy.setSelection(false);
				this.txtProxyName.setEnabled(false);
				this.txtProxyName.setText("");
				this.txtProxyPasswd.setEnabled(false);
				this.txtProxyPasswd.setText("");
				this.txtProxyDomain.setEnabled(false);
				this.txtProxyDomain.setText("");
			}
			
		}
		
	}
	
	private void onConnectionSelected() throws SiteviewException{
		setCurConnectionValue();
		
		if (tabFolder.getSelectionIndex() == 0){
			m_oldList = lstCommon;
			String cName = lstCommon.getItem(lstCommon.getSelectionIndex()).getText();
			ConnectionDef cdf = cmgr.GetConnectionDef(CommonPrefix + cName);
			m_curConnDef = cdf;
			if (cdf!=null){
				this.m_oldCommonSelectIndex = lstCommon.getSelectionIndex();
				ShowConnectionDef(cdf,lstCommon,m_oldCommonSelectIndex);
			}
		}else{
			m_oldList = lstPriv;
			String cName = lstPriv.getItem(lstPriv.getSelectionIndex()).getText();
			ConnectionDef cdf = cmgr.GetConnectionDef(PersonalPrefix + cName);
			m_curConnDef = cdf;
			if (cdf!=null){
				this.m_oldPersonalSelectIndex = lstPriv.getSelectionIndex();
				ShowConnectionDef(cdf,lstPriv,m_oldPersonalSelectIndex);
			}
		}
	}
	
	private void setCurConnectionValue() throws SiteviewException{
		if (m_curConnDef==null) return;
		
		String strPerfix = CommonPrefix;
		if (m_oldList == lstCommon){
			strPerfix = CommonPrefix;
		}else
			strPerfix = PersonalPrefix;
		
		
		if (m_curConnDef.get_InternalDatabaseConnection()){
			InternalDbConnectionDef dbdf = (InternalDbConnectionDef) m_curConnDef;
			if (dbdf.get_DbEngine() == DatabaseEngine.SqlServer){

				if (m_oldList == lstCommon){
					lstCommon.getItem(m_oldCommonSelectIndex).setText(txtSqlName.getText());
				}else{
					//lstPriv.getItem(m_oldPersonalSelectIndex).setText(txtSqlName.getText());
				}
				
				dbdf.set_Name(strPerfix + txtSqlName.getText());
				dbdf.set_Alias(strPerfix + txtSqlName.getText());
				
				dbdf.set_ServerName(txtSqlServerName.getText());
				dbdf.set_DataSource(txtSqlDbName.getText());
				
				dbdf.SetUserId(dbdf.AdminConnection, txtSqlAdminId.getText());
				dbdf.SetPassword(dbdf.AdminConnection, txtSqlAdminPwd.getText());
				dbdf.SetUserId(dbdf.UserConnection, txtSqlUserId.getText());
				dbdf.SetPassword(dbdf.UserConnection, txtSqlUserPwd.getText());
				dbdf.set_MinConnections(Integer.parseInt(txtSqlMinConn.getText()));
				dbdf.set_HoldConnections(Integer.parseInt(txtSqlHoldConn.getText()));
				dbdf.set_MaxConnections(Integer.parseInt(txtSqlMaxConn.getText()));
				dbdf.set_ConnectionTimeout(Integer.parseInt(txtSqlConnTimeout.getText()));
				dbdf.set_FadeTimeout(Integer.parseInt(txtSqlBreakConnTime.getText()));
				dbdf.set_Description(txtSqlDescription.getText());
				dbdf.BuildConnectionString(dbdf.AdminConnection);
				dbdf.BuildConnectionString(dbdf.UserConnection);
				cmgr.UpdateConnectionDef(dbdf);
				
				loginname = txt_mssqllogin.getText();
				loginpassword = txt_mssqlpwd.getText();
				
			}else if(dbdf.get_DbEngine() == DatabaseEngine.Oracle){
				if (m_oldList == lstCommon){
					lstCommon.getItem(m_oldCommonSelectIndex).setText(txtOracleName.getText());
				}else{
					lstPriv.getItem(m_oldPersonalSelectIndex).setText(txtOracleName.getText());
				}
				dbdf.set_Name(strPerfix + txtOracleName.getText());
				dbdf.set_Alias(strPerfix + txtOracleName.getText());
				dbdf.set_ServerName(txtOracleHost.getText());
				dbdf.set_DataSource(txtOracleInstanceName.getText());
				//dbdf.set_OwnerName(txtOracleInstanceName.getText());
				dbdf.SetUserId(dbdf.AdminConnection, txtOracleAdminId.getText());
				dbdf.SetPassword(dbdf.AdminConnection, txtOracleAdminPwd.getText());
				dbdf.SetUserId(dbdf.UserConnection, txtOracleUserId.getText());
				dbdf.SetPassword(dbdf.UserConnection, txtOracleUserPwd.getText());
				dbdf.set_MinConnections(Integer.parseInt(txtOracleMinConn.getText()));
				dbdf.set_HoldConnections(Integer.parseInt(txtOracleHoldConn.getText()));
				dbdf.set_MaxConnections(Integer.parseInt(txtOracleMaxConn.getText()));
				dbdf.set_ConnectionTimeout(Integer.parseInt(txtOracleConnTimeout.getText()));
				dbdf.set_FadeTimeout(Integer.parseInt(txtOracleBreakConnTime.getText()));
				dbdf.set_Description(txtOracleDescription.getText());
				dbdf.BuildConnectionString(dbdf.AdminConnection);
				dbdf.BuildConnectionString(dbdf.UserConnection);
				cmgr.UpdateConnectionDef(dbdf);
				
				loginname = txt_oracleloginname.getText();
				loginpassword = txt_oracleloginpwd.getText();
			}
			else if (dbdf.get_DbEngine() == DatabaseEngine.MySql){

				if (m_oldList == lstCommon){
					lstCommon.getItem(m_oldCommonSelectIndex).setText(txtMySqlName.getText());
				}else{
					lstPriv.getItem(m_oldPersonalSelectIndex).setText(txtMySqlName.getText());
				}
				
				dbdf.set_Name(strPerfix + txtMySqlName.getText());
				dbdf.set_Alias(strPerfix + txtMySqlName.getText());
				dbdf.set_ServerName(txtMySqlServerName.getText());
				dbdf.set_DataSource(txtMySqlDbName.getText());
				dbdf.SetUserId(dbdf.AdminConnection, txtMySqlAdminId.getText());
				dbdf.SetPassword(dbdf.AdminConnection, txtMySqlAdminPwd.getText());
				dbdf.SetUserId(dbdf.UserConnection, txtMySqlUserId.getText());
				dbdf.SetPassword(dbdf.UserConnection, txtMySqlUserPwd.getText());
				dbdf.set_MinConnections(Integer.parseInt(txtMySqlMinConn.getText()));
				dbdf.set_HoldConnections(Integer.parseInt(txtMySqlHoldConn.getText()));
				dbdf.set_MaxConnections(Integer.parseInt(txtMySqlMaxConn.getText()));
				dbdf.set_ConnectionTimeout(Integer.parseInt(txtMySqlConnTimeout.getText()));
				dbdf.set_FadeTimeout(Integer.parseInt(txtMySqlBreakConnTime.getText()));
				dbdf.set_Description(txtMySqlDescription.getText());
				dbdf.BuildConnectionString(dbdf.AdminConnection);
				dbdf.BuildConnectionString(dbdf.UserConnection);
				cmgr.UpdateConnectionDef(dbdf);
				
				loginname = txt_mysqllogin.getText();
				loginpassword = txt_mysqlpwd.getText();
			}
			else if (dbdf.get_DbEngine() == DatabaseEngine.H2){

				if (m_oldList == lstCommon){
					lstCommon.getItem(m_oldCommonSelectIndex).setText(txtH2Name.getText());
				}else{
					lstPriv.getItem(m_oldPersonalSelectIndex).setText(txtH2Name.getText());
				}
				
				dbdf.set_Name(strPerfix + txtH2Name.getText());
				dbdf.set_Alias(strPerfix + txtH2Name.getText());
				dbdf.set_ServerName("");
				dbdf.set_DataSource(txtH2DbName.getText());
				dbdf.SetUserId(dbdf.AdminConnection, txtH2AdminId.getText());
				dbdf.SetPassword(dbdf.AdminConnection, txtH2AdminPwd.getText());
				dbdf.SetUserId(dbdf.UserConnection, txtH2UserId.getText());
				dbdf.SetPassword(dbdf.UserConnection, txtH2UserPwd.getText());
				dbdf.set_MinConnections(Integer.parseInt(txtH2MinConn.getText()));
				dbdf.set_HoldConnections(Integer.parseInt(txtH2HoldConn.getText()));
				dbdf.set_MaxConnections(Integer.parseInt(txtH2MaxConn.getText()));
				dbdf.set_ConnectionTimeout(Integer.parseInt(txtH2ConnTimeout.getText()));
				dbdf.set_FadeTimeout(Integer.parseInt(txtH2BreakConnTime.getText()));
				dbdf.set_Description(txtH2Description.getText());
				dbdf.BuildConnectionString(dbdf.AdminConnection);
				dbdf.BuildConnectionString(dbdf.UserConnection);
				cmgr.UpdateConnectionDef(dbdf);
				
				loginname = txt_h2login.getText();
				loginpassword = txt_h2pwd.getText();
			}
		}else if (m_curConnDef.get_ServerConnection()){
			if (m_oldList == lstCommon){
				lstCommon.getItem(m_oldCommonSelectIndex).setText(txtServerName.getText());
			}else{
				lstPriv.getItem(m_oldPersonalSelectIndex).setText(txtServerName.getText());
			}
			
			ServerConnectionDef sdf = (ServerConnectionDef)m_curConnDef;
			sdf.set_Name(strPerfix + txtServerName.getText());
			sdf.set_Alias(strPerfix + txtServerName.getText());
			sdf.set_Uri(txtURI.getText());
			if (chkProxy.getSelection()){
				sdf.get_ProxyInfo().set_UseProxyServer(true);
				sdf.get_ProxyInfo().set_Username(txtProxyName.getText());
				sdf.get_ProxyInfo().set_Password(txtProxyPasswd.getText());
				sdf.get_ProxyInfo().set_Domain(txtProxyDomain.getText());
			}else{
				sdf.get_ProxyInfo().set_UseProxyServer(false);
				sdf.get_ProxyInfo().set_Username("");
				sdf.get_ProxyInfo().set_Password("");
				sdf.get_ProxyInfo().set_Domain("");
			}
			cmgr.UpdateConnectionDef(sdf);
		}
	}

	private void loadConnections() throws SiteviewException, DocumentException {
		Iterator it;
		
		cmgr = new FileBasedConnectionDefMgr();
        cmgr.LoadConnectionsForGroupIfNeeded(ConnectionDefGroup.Common);
        
        Collection ccs = cmgr.ConnectionsForGroup(ConnectionDefGroup.Common);
        it = ccs.iterator();
        while(it.hasNext()){
            ConnectionDef cdf = (ConnectionDef)it.next();
            
            //lstCommon.add(cdf.get_DisplayName());
            //lstCommon.setData(getDislayName(cdf.get_Alias()), cdf);
            
            TableItem ti = new TableItem(lstCommon, SWT.NONE);
			ti.setText(cdf.get_DisplayName());
//			if (cdf.get_ServerConnection())
//				ti.setImage(Activator.getImage("img/Images.Server16.png"));
//			else
//				ti.setImage(Activator.getImage("img/Images.Icons.DatabaseConnect.png"));
        }
        
	}

	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, "确定",
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				"取消", false);
	}

	protected void saveConnections() throws ArgumentException, SiteviewException, DocumentException {
		this.cmgr.SaveConnectionDefs();
		
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(685, 543);
	}
	
	

	@Override
	protected void okPressed() {
		try{
		setCurConnectionValue();
		saveConnections();
		savelogin();
		super.okPressed();
		}catch(Exception e){
			MessageDialog.openError(getShell(), "提示信息", e.getMessage());
		}
	}
	
	public void savelogin() throws DocumentException, IOException, SiteviewException{
		SAXReader saxreader = new SAXReader();
		String path = ResourceUtils.getLibDir() + java.io.File.separator +"Connections.ciq";
		Document doc = saxreader.read(new File(path));
		Element element = doc.getRootElement().element("DbConnectionDef");
		List<Element> logininfoList = element.elements("logininfo");
		
		if(logininfoList!=null&&logininfoList.size()>0){
			Element logininfo = logininfoList.get(0);
			logininfo.element("loginname").setText(loginname);
			logininfo.element("loginpassword").setText(sp.encrypt2String(loginpassword));
		}
		else{
			Element logininfo = element.addElement("logininfo");
			logininfo.addElement("loginname").setText(loginname);
			logininfo.addElement("loginpassword").setText(sp.encrypt2String(loginpassword));
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xw = new XMLWriter(new OutputStreamWriter(new FileOutputStream(path)),format);
		xw.write(doc);
		xw.close();
	}
	
	public void getlogininfo() throws DocumentException, IOException, SiteviewException{
		SAXReader saxreader = new SAXReader();
		String path = ResourceUtils.getLibDir() + java.io.File.separator +"Connections.ciq";
		Document doc = saxreader.read(new File(path));
		Element element = doc.getRootElement().element("DbConnectionDef");
		Element logininfo = element.element("logininfo");
		if(logininfo!=null){
			loginname = logininfo.element("loginname").getText();
			loginpassword = sp.decrypt2String(logininfo.element("loginpassword").getText());
		}
	}
}
