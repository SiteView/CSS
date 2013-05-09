/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.diag.rmtcontrol.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.diag.rmtcontrol.Activator;
import org.csstudio.diag.rmtcontrol.Messages;
import org.csstudio.diag.rmtcontrol.Preference.SampleService;
import org.csstudio.utility.ioc_socket_communication.IOCAnswer;
import org.csstudio.utility.ioc_socket_communication.RMTControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class View extends ViewPart implements Observer {

	private Text ioc1;
	private Text ioc2;
	StyledText request;
	StyledText answer;
	StringBuffer requestMassage;
	IOCAnswer iocAnswer;
	Display disp;
	Element root;

	private Group befehl;
	private static HashMap<String, Integer> varMap;

	public View() {
		iocAnswer = new IOCAnswer();
		iocAnswer.addObserver(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		disp = parent.getDisplay();
		/**********************************************************************
		 * Layout
		 **********************************************************************/
		// Parent
		parent.setLayout(new GridLayout(1,false));
		// -Menu
		Composite menu = new Composite(parent,SWT.NONE);
		menu.setLayout(new GridLayout(3, false));
		menu.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		// --IOC1
		final Button iocButton1 = new Button(menu, SWT.RADIO);
		iocButton1.setText(Messages.getString("View.0"));
		iocButton1.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false,1,1));
		iocButton1.setSelection(true);
		ioc1 = new Text(menu, SWT.BORDER);
		ioc1.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,1,1));
		ioc1.setText(Activator.getDefault().getPluginPreferences().getString(SampleService.IOC_ADDRESS1)); //$NON-NLS-1$
		// - Start
		Button start = new Button(menu,SWT.PUSH);
		start.setText(Messages.getString("View.3")); //$NON-NLS-1$
		start.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false,1,2));
		// --IOC2
		Button iocButton2 = new Button(menu, SWT.RADIO);
		iocButton2.setText(Messages.getString("View.1"));
		iocButton2.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false,1,1));
		ioc2 = new Text(menu, SWT.BORDER);
		ioc2.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,1,1));
		ioc2.setText(Activator.getDefault().getPluginPreferences().getString(SampleService.IOC_ADDRESS2)); //$NON-NLS-1$
		// -- empty
//		new Label(menu, SWT.NONE);
		// BefehlsGroup
		befehl = new Group(menu,SWT.NONE);
		makeOrderGroup(menu);

		Group labelRequest = new Group(menu,SWT.NONE);
		labelRequest.setText(Messages.getString("View.4")); //$NON-NLS-1$
		GridData gridData = new GridData(SWT.FILL,SWT.TOP,true,false,3,1);
		gridData.heightHint = 100;
		gridData.minimumWidth=250;
	    labelRequest.setLayoutData(gridData);
	    labelRequest.setLayout(new GridLayout(1,false));
		request = new StyledText(labelRequest,SWT.MULTI | SWT.V_SCROLL);
		request.setWordWrap(true);
		gridData = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
	    gridData.heightHint = 80;
		gridData.minimumWidth=200;
	    request.setLayoutData(gridData);

		Group labelAnswer = new Group(menu,SWT.NONE);
	    labelAnswer.setText(Messages.getString("View.5")); //$NON-NLS-1$
	    gridData = new GridData(SWT.FILL,SWT.FILL,true,true,3,1);
	    gridData.minimumHeight = 100;
		gridData.minimumWidth = 250;
	    labelAnswer.setLayoutData(gridData);
	    labelAnswer.setLayout(new GridLayout(1,false));
		answer = new StyledText(labelAnswer,SWT.MULTI | SWT.V_SCROLL);
		answer.setWordWrap(true);
		gridData = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
	    gridData.minimumHeight = 80;
		gridData.minimumWidth = 200;
	    answer.setLayoutData(gridData);
		start.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				answer.setText(""); //$NON-NLS-1$
				RMTControl iocContr = RMTControl.getInstance();
				if(iocButton1.getSelection())
					iocContr.send(ioc1.getText(), request.getText(), iocAnswer);
				else
					iocContr.send(ioc2.getText(), request.getText(), iocAnswer);
			}

		});

		answer.addLineStyleListener(new LineStyleListener(){

			public void lineGetStyle(LineStyleEvent event) {
				// TODO Auto-generated method stub
//				System.out.println("----------------------------------------------------");
//				System.out.println("Test LineStyleListener");
//				System.out.println("Text: "+event.lineText);
//				System.out.println("alig: "+event.alignment);
//				System.out.println("bullet: "+event.bulletIndex);
//				System.out.println("indent: "+event.indent);
//				System.out.println("lineOffset: "+event.lineOffset);
//				System.out.println("time: "+event.time);
//				System.out.println("range: "+event.ranges);
//				System.out.println("styles: "+event.styles);
//				StyleRange sr = new StyleRange(event.lineOffset,event.lineText.length(),new Color(answer.getDisplay(),100,200,100),new Color(answer.getDisplay(),200,100,200));
//				answer.setLineBackground(event.lineOffset-1,0,new Color(answer.getDisplay(),100,200,100));
//				answer.setStyleRange(sr);
//				event.
			}

		});

	}

	void makeOrderGroup(Composite menu) {
		final HashMap<String, Element> befehlsliste = new HashMap<String, Element>();
		String[] befehlsreihenfolge = null;
		/***********************************/
		File inputFile = new File(Activator.getDefault().getPluginPreferences().getString(SampleService.RMT_XML_FILE_PATH));
		if(!inputFile.isFile()){
//			WriteDefaultXML.writeDefault(inputFile);
			System.out.println("hIER._"+Messages.getString("View.WrongFile")+inputFile); //$NON-NLS-1$
		}
		SAXBuilder saxb = new SAXBuilder(false);
		Document befehlsDoc;// = new Document();
		try {
			befehlsDoc = saxb.build(inputFile);
			root = befehlsDoc.getRootElement();
//			if(checkVerion(root.getAttribute("version")){//TODO: Versionkontrolle einf�hren}
			// Variablen einlesen
			List varListe = root.getChildren("var");
			Iterator varIte = varListe.iterator();
			varMap = new HashMap<String, Integer>();
//			befehlsreihenfolge = new String[varListe.size()];
			while(varIte.hasNext()){
				Element elm = (Element) varIte.next();
				Integer i1;
				try{
					i1 = new Integer(elm.getTextNormalize());
				}catch(NumberFormatException nfe){
					Activator.logInfo("Das File "+inputFile+" enth�lt ein ung�ltigen Variablen eintrag.\r\n"+
									  "Im Element "+elm.getName()+"->"+elm.getAttributeValue("name")+" Wert '"+elm.getText()+"'\r\nDer wert wurde auf 1 gesetzt.");
					i1=new Integer(1);

				}

				varMap.put(elm.getAttribute("name").getValue(),i1); //$NON-NLS-1$
			}

			// Befehle einlesen
			List befehlsListe = root.getChildren("Befehl");
			Iterator befehleIte = befehlsListe.iterator();
			befehlsreihenfolge = new String[befehlsListe.size()];
			int i=0;
			while(befehleIte.hasNext()){
				Element elm = (Element) befehleIte.next();
				befehlsreihenfolge[i++]=elm.getAttribute("name").getValue(); //$NON-NLS-1$
				befehlsliste.put(elm.getAttribute("name").getValue(),elm.getChild("Root")); //$NON-NLS-1$ //$NON-NLS-2$
			}

		} catch (JDOMException e1) {
			Activator.logException(Messages.getString("View.ExceptionJDOM"), e1); //$NON-NLS-1$
		} catch (IOException e1) {
			Activator.logException(Messages.getString("View.ExceptionIO"), e1); //$NON-NLS-1$
		}

		//-----------------------------------------------------------
		befehl.setText(Messages.getString("View.list")); //$NON-NLS-1$
		Control[] child = befehl.getChildren();
		for (Control control : child) {
			control.dispose();
		}
		RowLayout fl = new RowLayout(SWT.HORIZONTAL);
		fl.wrap=true;
		fl.fill=false;
		fl.justify=false;
		fl.pack=false;
		fl.marginRight=0;
		befehl.setLayout(fl);
		befehl.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,3,1));
		for (String name : befehlsreihenfolge) {
			final Button button = new Button (befehl, SWT.RADIO);
			button.setText (name);
			button.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {}

				public void widgetSelected(SelectionEvent e) {
					if(!button.getSelection())
						return;

					requestMassage = new StringBuffer();
					String text = button.getText();
					Element ele = befehlsliste.get(text);
					XMLOutputter outp = new XMLOutputter();
					String xml = outp.outputString(ele);
					requestMassage.append(xml);
//					String[] tmp = requestMassage.split("$");
//					boolean varExist = false;
					int pos=0;
//					String send ="";
					while((pos= requestMassage.indexOf("$("))>-1){
						int pos2=requestMassage.indexOf(")",pos);
						String key = requestMassage.substring(pos,pos2+1);
						Integer value = varMap.get(key);
						requestMassage = requestMassage.replace(pos, pos2+1, value.toString());
						varMap.put(key, value+1);
					}
					request.setText(requestMassage.toString());
				}

			});
		}
		final Button button = new Button (befehl, SWT.RADIO);
		button.setText (Messages.getString("View.vari")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				request.setText(""); //$NON-NLS-1$
			}

		});
		button.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				request.setEditable(true);
			}

		});

	}

	@Override
	public void setFocus() {	}

	public void update(Observable arg0, Object arg1) {
		disp.syncExec(new Runnable() {
			public void run() {
				String text = iocAnswer.getAnswer();
				answer.setText(text);
				answer.getParent().layout();
			}
		});
	}

}
