package org.csstudio.nams.common.material.regelwerk;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractTestObject;

public class WeiteresVersandVorgehen_Test extends
		AbstractTestObject<WeiteresVersandVorgehen> /*
 * TODO TestCase
 * fuer Enums
 * erstellen!
 */{

	public void testAnzahlDerElemente() {
		Assert.assertEquals(4, WeiteresVersandVorgehen.values().length);
	}

	@Override
	protected WeiteresVersandVorgehen getNewInstanceOfClassUnderTest() {
		return WeiteresVersandVorgehen.ERNEUT_PRUEFEN;
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected WeiteresVersandVorgehen[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new WeiteresVersandVorgehen[] {
				WeiteresVersandVorgehen.VERSENDEN,
				WeiteresVersandVorgehen.NICHT_VERSENDEN,
				WeiteresVersandVorgehen.NOCH_NICHT_GEPRUEFT };
	}

}
