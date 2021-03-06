/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.sim;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.vtype.VTable;
import static org.epics.vtype.ValueFactory.*;

/**
 * Function to simulate a signal that generates VTables.
 *
 * @author carcassi
 */
public class Table extends SimFunction<VTable> {

    public Table() {
        this(0.1);
    }

    public Table(Double interval) {
        super(interval, VTable.class);
        if (interval <= 0.0) {
            throw new IllegalArgumentException("Interval must be greater than zero (was " + interval + ")");
        }
    }
    
    private final List<Class<?>> types = Arrays.asList((Class<?>) String.class, Double.TYPE, Integer.TYPE);
    

    @Override
    VTable nextValue() {
        return newVTable(types, Arrays.asList("text", "value", "index"),
                Arrays.asList((Object) generateStringColumn(10), generateDoubleColumn(10), generateIntegerColumn(10)));
    }
    
    private final Random rand = new Random();
    
    ListInt generateIntegerColumn(int size) {
        int[] column = new int[size];
        for (int i = 0; i < column.length; i++) {
            column[i] = i;
        }
        return new ArrayInt(column);
    }
    
    ListDouble generateDoubleColumn(int size) {
        double[] column = new double[size];
        for (int i = 0; i < column.length; i++) {
            column[i] = rand.nextGaussian();
        }
        return new ArrayDouble(column);
    }
    
    List<String> generateStringColumn(int size) {
        String[] column = new String[size];
        for (int i = 0; i < column.length; i++) {
            column[i] = generateString(i);
        }
        return Arrays.asList(column);
    }
    
    String generateString(int id) {
        if (id == 0) {
            return "A";
        }
        
        StringBuilder sb = new StringBuilder();
        while (id != 0) {
            char letter = (char) ('A' + (id %  26));
            sb.insert(0, letter);
            id = id / 26;
        }
        return sb.toString();
    }
}
