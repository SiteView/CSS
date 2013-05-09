/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.autocomplete.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * Enable auto complete PV content on the specified {@link Control}
 */
public class AutoCompleteWidget {

	private AutoCompleteProposalProvider provider = null;
	private ContentProposalAdapter adapter = null;
	private final Control control;
	private final String type;

	public AutoCompleteWidget(Control control, String type) {
		Assert.isNotNull(type);
		
		this.control = control;
		this.type = type;
		enableContentProposal();
	}

	public AutoCompleteWidget(CellEditor cellEditor, String type) {
		Assert.isNotNull(type);
		
		this.control = cellEditor.getControl();
		this.type = type;
		enableContentProposal();
	}

	/**
	 * Return a character array representing the keyboard input triggers used
	 * for firing the ContentProposalAdapter.
	 * 
	 * @return - character array of trigger chars
	 */
	protected static char[] getAutoactivationChars() {
		String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";
		String uppercaseLetters = lowercaseLetters.toUpperCase();
		String numbers = "0123456789";
		// String delete = new String(new char[] {SWT.DEL});
		// the event in {@link ContentProposalAdapter#addControlListener(Control control)}
		// holds onto a character and when the DEL key is pressed that char
		// value is 8 so the line below catches the DEL key press
		String delete = new String(new char[] { 8 });
		String allChars = lowercaseLetters + uppercaseLetters + numbers
				+ delete + "*?";
		return allChars.toCharArray();
	}

	/**
	 * Returns KeyStroke object which when pressed will fire the
	 * ContentProposalAdapter.
	 * 
	 * @return - the activation keystroke
	 */
	protected static KeyStroke getActivationKeystroke() {
		// keyStroke = KeyStroke.getInstance("Ctrl+Space");
		// Activate on <ctrl><space>
		return KeyStroke.getInstance(new Integer(SWT.CTRL).intValue(),
				new Integer(' ').intValue());
	}

	private void enableContentProposal() {
		if (control instanceof Combo) {

			Combo combo = (Combo) control;
			provider = new AutoCompleteProposalProvider(type);
			adapter = new ContentProposalAdapter(combo,
					new ComboContentAdapter(), provider,
					getActivationKeystroke(), getAutoactivationChars());
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		} else if (control instanceof Text) {

			Text text = (Text) control;
			provider = new AutoCompleteProposalProvider(type);
			adapter = new ContentProposalAdapter(text,
					new TextContentAdapter(), provider,
					getActivationKeystroke(), getAutoactivationChars());
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		}
	}

}
