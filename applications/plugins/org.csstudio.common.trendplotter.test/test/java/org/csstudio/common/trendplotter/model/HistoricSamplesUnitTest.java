/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.junit.Test;
import org.mockito.Mockito;

/** JUnit test of HistoricSamples
 *  @author Kay Kasemir
 *
 *  FIXME (kasemir) : remove sysos, use assertions
 */
@SuppressWarnings("nls")
public class HistoricSamplesUnitTest
{
    @Test
    public void addArchivedData() throws OsgiServiceUnavailableException, ArchiveServiceException
    {
//       TODO jhatje: update test
//        final ArchiveReader readerMock = Mockito.mock(ArchiveReader.class);
//        Mockito.when(readerMock.getServerName()).thenReturn("Testserver");
//
//        final RequestType reqType = RequestType.RAW;
//        final HistoricSamples history = new HistoricSamples(reqType);
//        final int N = 10;
//        // Initial data, time 10..19
//        final ArrayList<IValue> samples = new ArrayList<IValue>();
//        for (int i=0; i<N; ++i) {
//            samples.add(TestSampleBuilder.makeValue(N+i));
//        }
//
//        history.mergeArchivedData("TestChannel", readerMock, reqType, samples);
//        //System.out.println(history);
//
//        assertEquals(N, history.getSize());
//        assertEquals(samples.get(0), history.getSample(0).getValue());
//        assertEquals(samples.get(N-1), history.getSample(N-1).getValue());
//
//        Range range = history.getYDataMinMax();
//        //System.out.println(range);
//        assertEquals(new Range(10, 19), range);
//
//        // Check subset
//        history.setBorderTime(TimestampFactory.fromDouble(12));
//        assertEquals(2, history.getSize());
//        assertEquals(new Range(10, 11), history.getYDataMinMax());
//
//        history.setBorderTime(null);
//        assertEquals(N, history.getSize());
//        assertEquals(new Range(10, 19), history.getYDataMinMax());
//
//        // Pre-pend time 0..9
//        for (int i=0; i<N; ++i) {
//            samples.set(i, TestSampleBuilder.makeValue(i));
//        }
//        history.mergeArchivedData("TestChannel", readerMock, reqType, samples);
//        assertEquals(2*N, history.getSize());
//        //System.out.println(history);
//        range = history.getYDataMinMax();
//        //System.out.println(range);
//        assertEquals(new Range(0, 19), range);
//
//        history.setBorderTime(TimestampFactory.fromDouble(12));
//        assertEquals(12, history.getSize());
//
//        history.setBorderTime(TimestampFactory.fromDouble(100));
//        assertEquals(20, history.getSize());
//
//        history.setBorderTime(TimestampFactory.fromDouble(0));
//        assertEquals(0, history.getSize());
//
//        history.setBorderTime(null);
//        assertEquals(2*N, history.getSize());
    }
}
