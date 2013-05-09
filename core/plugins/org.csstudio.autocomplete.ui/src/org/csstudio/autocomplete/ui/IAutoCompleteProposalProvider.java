/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui;

public interface IAutoCompleteProposalProvider {

	public ContentProposalList getProposals(String contents, int position, int max);

	public boolean hasProviders();

	public void cancel();

	public String getType();

}
