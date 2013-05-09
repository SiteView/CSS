
/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 */

package org.csstudio.archive.sdds.server.command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.sdds.server.command.header.DataRequestHeader;
import org.csstudio.archive.sdds.server.conversion.SampleParameters;
import org.csstudio.archive.sdds.server.data.DataCollector;
import org.csstudio.archive.sdds.server.data.DataCollectorException;
import org.csstudio.archive.sdds.server.data.EpicsRecordData;
import org.csstudio.archive.sdds.server.data.RecordDataCollection;
import org.csstudio.archive.sdds.server.type.TypeFactory;
import org.csstudio.archive.sdds.server.type.TypeNotSupportedException;
import org.csstudio.archive.sdds.server.util.RawData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.desy.aapi.AapiServerError;

/**
 * @author Markus Moeller
 *
 */
public class DataRequestServerCommand extends AbstractServerCommand {

    private static final Logger LOG = LoggerFactory.getLogger(DataRequestServerCommand.class);

    /** The data reader */
    private DataCollector dataCollector;

    /**
     *
     * @throws ServerCommandException
     */
    public DataRequestServerCommand() throws ServerCommandException {

        super();

        try {
            dataCollector = new DataCollector();
        } catch(final DataCollectorException dce) {
            throw new ServerCommandException("Cannot create instance of DataCollector: " + dce.getMessage());
        }
    }

    /**
     *
     */
    @Override
    @CheckForNull
    public RawData execute(@Nonnull final RawData buffer)
                           throws ServerCommandException, CommandNotImplementedException {

        final DataRequestHeader header = new DataRequestHeader(buffer.getData());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);

        LOG.info(header.toString());

        if(!header.isTimeDiffValid()) {

            final AapiServerError error = AapiServerError.FROM_MORE_THEN_TO;
            LOG.error(error.toString());
            return new RawData(createErrorAnswer(error.getErrorNumber()),
                               error.getErrorNumber());
        }

        // TODO: Does it make sense to set a default number of samples instead of returning with an error?
        if(!header.hasValidNumberofSamples()) {

            LOG.warn(AapiServerError.BAD_MAX_NUM.toString());
            LOG.warn("Using default: 1000");

            header.setMaxNumOfSamples(1000);
//            receivedValue.setData(createErrorAnswer(AAPI.AAPI.BAD_MAX_NUM));
//            receivedValue.setErrorValue(AAPI.AAPI.BAD_MAX_NUM);
//            logger.debug("ERROR: " + AAPI.AAPI.aapiServerSideErrorString[AAPI.AAPI.BAD_MAX_NUM]);
//            return;
        }

        try {
            // Number of PV's
            dos.writeInt(header.getPvNameSize());

            RecordDataCollection data = null;
            for(final String name : header.getPvName()) {

                data = dataCollector.readData(name, header);
                LOG.info("Number of samples: " + data.getNumberOfData());

                if (data.containsError()) {
                    return new RawData(createErrorAnswer(AapiServerError.CAN_T_OPEN_FILE.getErrorNumber()),
                                       AapiServerError.CAN_T_OPEN_FILE.getErrorNumber());
                }

                // TODO: Nicht vorhandene Daten abfangen und saubere Fehlermeldung zurueck liefern
                // Error
                dos.writeInt(0);
                // Type (6 = double)
                dos.writeInt(6);
                // Number of samples
                dos.writeInt(data.getNumberOfData());

                for(final EpicsRecordData o : data.getData()) {
                    dos.writeInt((int) o.getTime());
                    dos.writeInt((int) o.getNanoSeconds());
                    dos.writeInt((int) o.getStatus());
                    // TODO: Handle ALL data types
                    switch(o.getSddsType()) {
                        case SDDS_DOUBLE:
                            try {
                                final double f = TypeFactory.toDouble(o.getValue());
                                dos.writeDouble(f);
                            } catch (final TypeNotSupportedException tnse) {
                                dos.writeDouble(Double.NaN);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

            if (data != null) {

                final SampleParameters sampleParameter = data.getSampleParameter();

                dos.writeInt(sampleParameter.getPrecision());
                dos.writeDouble(sampleParameter.getDisplayHigh());
                dos.writeDouble(sampleParameter.getDisplayLow());
                dos.writeDouble(sampleParameter.getHighAlarm());
                dos.writeDouble(sampleParameter.getHighWarning());
                dos.writeDouble(sampleParameter.getLowAlarm());
                dos.writeDouble(sampleParameter.getLowWarning());
                dos.writeInt(sampleParameter.getUnitsLength());
                dos.writeChars(sampleParameter.getUnits());
                dos.write('\0');
            }

            return new RawData(baos.toByteArray());

        } catch(final IOException ioe) {
            LOG.error("[*** IOException ***]: " + ioe.getMessage());
        } finally {
            try {
                dos.close();
            } catch (final Exception e) {
                LOG.warn("Closing of data output stream failed.", e);
            }
        }
        return null;
    }
}
