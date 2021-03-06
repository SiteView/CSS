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
/**
 *
 */
package org.csstudio.alarm.table.ui.messagetable;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Sorter for log table viewer. Insert newest message at the top.
 *
 * @author jhatje
 */
public class MessageTableMessageSorter extends ViewerComparator {

    private final TableViewer _tableViewer;

    public MessageTableMessageSorter(final TableViewer tableViewer) {
        super();
        _tableViewer = tableViewer;
    }

    @Override
    public int compare(final Viewer viewer, final Object o1, final Object o2) {

        final BasicMessage jmsm1 = (BasicMessage) o1;
        final BasicMessage jmsm2 = (BasicMessage) o2;
        // FIXME (jhatje) : transform string to Date and compare with Date.before or Date.after
        return (super.compare(_tableViewer,
                              jmsm2.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName()),
                              jmsm1.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName())));
    }
}
