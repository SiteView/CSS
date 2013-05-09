/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.csstudio.opibuilder.converter.model.EdmException;
import junit.framework.TestCase;

public class OpiAttributeTest extends TestCase {

	public void testOpiAttribute() throws EdmException {
		
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		// OpiAttribute data
		String name = "x";
		
		// instantiating OpiAttribute
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		Context context = new Context(doc, parent, 0, 0);
		new OpiAttribute(context, name);
		
		// testing
		assertEquals(1, doc.getElementsByTagName(name).getLength());
		
		//XMLFileHandler.writeXML(doc);
		
	}
}
