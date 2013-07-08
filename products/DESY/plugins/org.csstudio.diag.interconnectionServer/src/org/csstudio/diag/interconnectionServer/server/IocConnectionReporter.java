/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.diag.interconnectionServer.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.csstudio.diag.icsiocmonitor.service.IIocConnectionReporter;
import org.csstudio.diag.icsiocmonitor.service.IocConnectionReport;
import org.csstudio.diag.icsiocmonitor.service.IocConnectionReportItem;
import org.csstudio.diag.icsiocmonitor.service.IocConnectionState;
import org.csstudio.servicelocator.ServiceLocator;

/**
 * Implements the <code>IIocConnectionReporter</code> service for remote
 * monitoring of IOCs connected to this interconnection server.
 *
 * @author Joerg Rathlev
 */
public class IocConnectionReporter implements IIocConnectionReporter {

	/**
	 * {@inheritDoc}
	 */
	public IocConnectionReport getReport() {
		final String serverName = ServiceLocator.getService(IInterconnectionServer.class).getLocalHostName();
		final List<IocConnectionReportItem> items = new ArrayList<IocConnectionReportItem>();
		final Collection<IocConnection> iocConnections = ServiceLocator.getService(IIocConnectionManager.class).getIocConnections();
		for (final IocConnection ioc : iocConnections) {
			final IocConnectionState state = ioc.getIocConnectionState();
			final IocConnectionReportItem item = new IocConnectionReportItem(
					ioc.getNames().getHostName(), ioc.getNames().getLogicalIocName(), state);
			items.add(item);
		}
		return new IocConnectionReport(serverName, items);
	}

}
