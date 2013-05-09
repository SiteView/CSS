/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Timer;

import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.utility.pv.PVFactory;
import org.junit.Test;

/** [Headless] JUnit Plug-In test of the PVItem
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVItemTest
{
    /** Time in seconds for each test */
    private static final double RUNTIME_SECS = 10.0;

    @Test
    public void checkPV() throws Exception
    {
        try
        {
            PVFactory.getSupportedPrefixes();
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
            fail("Must run as JUnit *Plug-In* test to use PVFactory");
        }
    }

    /** Check if PVItem scans its PV */
    @Test
    public void testScannedPVItem() throws Exception
    {
        System.out.println("Scanned samples: (" + RUNTIME_SECS + " secs)");
        final Timer scan_timer = new Timer();
        final PVItem pv = new PVItem("sim://sine(0,10,10,1)", 1.0);
        pv.start(scan_timer);
        Thread.sleep((long) (RUNTIME_SECS * 1000));
        pv.stop();
        // Should have about 1 sample per second
        final IDataProvider samples = pv.getSamples();
        System.out.println(samples);
        assertEquals(RUNTIME_SECS, samples.getSize(), 2.0);
        checkMinMax(samples);
    }

    /** Check if PVItem logs received PV monitors (value updates) */
    @Test
    public void testMonitoredPVItem() throws Exception
    {
        System.out.println("Monitored samples: (" + RUNTIME_SECS + " secs)");
        final Timer unused_timer = null;
        final PVItem pv = new PVItem("sim://sine(0,10,10,1)", 0.0);
        pv.start(unused_timer);
        Thread.sleep((long) (RUNTIME_SECS * 1000));
        pv.stop();
        // Should have about 1 sample per second
        final IDataProvider samples = pv.getSamples();
        System.out.println(samples);
        assertEquals(RUNTIME_SECS, samples.getSize(), 2.0);
        checkMinMax(samples);
    }

    /** Check if scan period can be changed while running */
    @Test
    public void testScanPeriodChange() throws Exception
    {
        System.out.println("Scan time change: (" + 2 * RUNTIME_SECS + " secs)");
        final Timer scan_timer = new Timer();
        final PVItem pv = new PVItem("sim://sine(0,10,10,1)", 1.0);
        pv.start(scan_timer);
        Thread.sleep((long) (RUNTIME_SECS * 1000));

        // Leave PV running. Should have about 1 sample per second
        System.out.println("Samples while scanned at 1 second");
        IDataProvider samples = pv.getSamples();
        System.out.println(samples);
        assertEquals(RUNTIME_SECS, samples.getSize(), 2.0);

        // Change to 2 second scan
        System.out.println("Changing scan to 2 seconds...");
        pv.setScanPeriod(2.0);
        Thread.sleep((long) (RUNTIME_SECS * 1000));

        // Should have about 1 sample per second + 0.5 per second
        samples = pv.getSamples();
        System.out.println(samples);
        assertEquals(RUNTIME_SECS + RUNTIME_SECS/2, samples.getSize(), 4.0);
        pv.stop();
    }

    /** Check if value min..max is correct */
    private void checkMinMax(final IDataProvider samples)
    {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i=0; i<samples.getSize(); ++i)
        {
            final double value = samples.getSample(i).getYValue();
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }
        assertEquals(new Range(min, max), samples.getYDataMinMax());
    }
}
