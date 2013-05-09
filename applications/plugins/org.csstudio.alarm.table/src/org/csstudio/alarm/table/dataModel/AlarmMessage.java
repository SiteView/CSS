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
package org.csstudio.alarm.table.dataModel;

import java.util.Map;

import javax.annotation.CheckReturnValue;

/**
 * Message received from the JMS server. The properties of messages are not
 * restricted but the table will only display properties for which a column with
 * the same name is defined.
 *
 * @author jhatje
 *
 */
public class AlarmMessage extends BasicMessage {

    /**
     * for alarm table: false->no other message with the same pv name and an
     * other severity is in the table. true->another NEWER message with same pv
     * an other severity is in the table and the label provider change the color
     * to gray.
     */
    private boolean _outdated = false;

    /**
     * is this message already acknowledged?
     */
    private boolean _acknowledged = false;

    public AlarmMessage() {
        super();
    }

    public AlarmMessage(final String[] propertyNames) {
        super(propertyNames);
    }

    public AlarmMessage(final Map<String, String> hashMap) {
        super(hashMap);
    }

    public boolean isOutdated() {
        return _outdated;
    }

    public void setOutdated(final boolean outdated) {
        _outdated = outdated;
    }

    public boolean isAcknowledged() {
        return _acknowledged;
    }

    public void setAcknowledged(final boolean ack) {
        _acknowledged = ack;
    }

    /**
     * @return deep copy of the JMSMessage.
     */
    @CheckReturnValue
    public AlarmMessage copy() {
        final AlarmMessage copy = new AlarmMessage(copyProperties());
        copy._acknowledged = _acknowledged;
        copy._outdated = _outdated;
        return copy;
    }
}
