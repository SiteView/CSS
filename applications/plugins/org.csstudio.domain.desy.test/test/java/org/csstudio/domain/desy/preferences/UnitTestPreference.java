/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.preferences;

import javax.annotation.Nonnull;

/**
 * Test Helper class.
 *
 * @author bknerr
 * @since 20.04.2011
 * @param <T> the preference type
 */
final class UnitTestPreference<T> extends AbstractPreference<T> {

    public static final UnitTestPreference<String> STRING_PREF =
        new UnitTestPreference<String>("Unit_String_Pref", "Some string");

    public static final UnitTestPreference<Integer> INT_PREF =
        new UnitTestPreference<Integer>("Unit_Int_Pref", 1234);

    public static final UnitTestPreference<Long> LONG_PREF =
        new UnitTestPreference<Long>("Unit_Long_Pref", 1234L);

    public static final UnitTestPreference<Float> FLOAT_PREF =
        new UnitTestPreference<Float>("Unit_Float_Pref", 12.34f);

    public static final UnitTestPreference<Double> DOUBLE_PREF =
        new UnitTestPreference<Double>("Unit_Double_Pref", 12.34);

    public static final UnitTestPreference<Double> DOUBLE_PREF_WITH_VAL =
        (UnitTestPreference<Double>) new UnitTestPreference<Double>("Unit_Double_Pref",
                               12.34).addValidator(new MinMaxPreferenceValidator<Double>(0.0, 100.0));

    public static final UnitTestPreference<Boolean> BOOLEAN_PREF =
        new UnitTestPreference<Boolean>("Unit_Boolean_Pref", true);

    /**
     * For test purposes
     */
    public static final Integer STATIC_NOT_TESTPREFERENCE = Integer.valueOf(0);


    /**
     * The following two lines of a non static instance field of type <itself> enable an
     * infinite recursion while constructing the object => stack overflow.
     *
     * public final TestPreference<Boolean> NOT_STATIC =
     *     new TestPreference<Boolean>("NOT_STATIC", true);
     */
    UnitTestPreference(@Nonnull final String keyAsString,
                           @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    @Override
    @Nonnull
    public String getPluginID() {
        return "QualifierForTest";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) UnitTestPreference.class;
    }

}
