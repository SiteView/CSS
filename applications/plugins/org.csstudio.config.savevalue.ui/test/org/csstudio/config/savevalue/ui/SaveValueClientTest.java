/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.config.savevalue.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * @author Joerg Rathlev
 */
public class SaveValueClientTest {

	/**
	 * Tests that double values are formatted correctly as required for the CA
	 * files.
	 */
	@Test
	public void testDecimalFormat() throws Exception {
		double d;
		
		d = 1.0;
		assertEquals("1", SaveValueClient.formatForCaFile(d, 0));
		assertEquals("1.0", SaveValueClient.formatForCaFile(d, 1));
		assertEquals("1.00", SaveValueClient.formatForCaFile(d, 2));
		
		d = 0;
		assertEquals("0", SaveValueClient.formatForCaFile(d, 0));
		assertEquals("0.0", SaveValueClient.formatForCaFile(d, 1));
		assertEquals("0.00", SaveValueClient.formatForCaFile(d, 2));
	
		d = 0.00005;
		assertEquals("0", SaveValueClient.formatForCaFile(d, 0));
		assertEquals("0.0", SaveValueClient.formatForCaFile(d, 1));
		assertEquals("0.00", SaveValueClient.formatForCaFile(d, 2));
		assertEquals("0.0000", SaveValueClient.formatForCaFile(d, 4));
		assertEquals("0.00005", SaveValueClient.formatForCaFile(d, 5));
		
		d = -1;
		assertEquals("-1", SaveValueClient.formatForCaFile(d, 0));
		assertEquals("-1.0", SaveValueClient.formatForCaFile(d, 1));
		assertEquals("-1.00", SaveValueClient.formatForCaFile(d, 2));
	}
}
