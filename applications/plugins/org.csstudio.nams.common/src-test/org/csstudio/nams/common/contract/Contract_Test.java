package org.csstudio.nams.common.contract;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.junit.Test;

/**
 * Dies ist kein {@link AbstractTestObject}, da Exemplare nicht möglich
 * sind!
 */
public class Contract_Test extends TestCase {

	@Test
	public void testCreationImpossible() {
		try {
			new Contract();
			Assert.fail("failed expected assertion error");
		} catch (final AssertionError ae) {
			Assert.assertEquals(
					"Creation of instances of this class is undesired!", ae
							.getMessage());
		}
	}

	@Test
	public void testEnsure() {
		try {
			Contract.ensure(false, "false");
			Assert.fail("failed expected assertion error");
		} catch (final AssertionError ae) {
			Assert.assertEquals("Precondition unsatisfied: false", ae
					.getMessage());
		}
	}

	@Test
	public void testEnsureResultNotNull() {
		try {
			Contract.ensureResultNotNull(null);
			Assert.fail("failed expected assertion error");
		} catch (final AssertionError ae) {
			Assert.assertEquals("Postcondition unsatisfied: §result != null",
					ae.getMessage());
		}
	}

	@Test
	public void testEnsureResultNotNullSuccess() {
		Contract.ensureResultNotNull(new Object());
	}

	@Test
	public void testEnsureSuccess() {
		Contract.ensure(true, "true");
	}

	@Test
	public void testRequireFail() {
		try {
			Contract.require(false, "false");
			Assert.fail("failed expected assertion error");
		} catch (final AssertionError ae) {
			Assert.assertEquals("Precondition unsatisfied: false", ae
					.getMessage());
		}
	}

	@Test
	public void testRequireFailWithoutConditionDescriptio() {
		try {
			Contract.require(false, null);
			Assert.fail("failed expected assertion error");
		} catch (final AssertionError ae) {
			Assert.assertEquals(
					"Precondition unsatisfied: description != null", ae
							.getMessage());
		}
	}

	@Test
	public void testRequireNotNull() {
		try {
			Contract.requireNotNull("null", null);
			Assert.fail("failed expected assertion error");
		} catch (final AssertionError ae) {
			Assert.assertEquals("Precondition unsatisfied: null != null", ae
					.getMessage());
		}
	}

	@Test
	public void testRequireNotNullSuccess() {
		Contract.requireNotNull("new Object()", new Object());
	}

	@Test
	public void testRequireNotNullWithoutAnything() {
		try {
			Contract.requireNotNull(null, null);
			Assert.fail("failed expected assertion error");
		} catch (final AssertionError ae) {
			Assert.assertEquals("Precondition unsatisfied: valueName != null",
					ae.getMessage());
		}
	}

	@Test
	public void testRequireNotNullWithoutValueName() {
		try {
			Contract.requireNotNull(null, new Object());
			Assert.fail("failed expected assertion error");
		} catch (final AssertionError ae) {
			Assert.assertEquals("Precondition unsatisfied: valueName != null",
					ae.getMessage());
		}
	}

	@Test
	public void testRequireSuccess() {
		Contract.require(true, "true");
	}

	@Test
	public void testRequireSuccessWithoutConditionDescriptio() {
		try {
			Contract.require(false, null);
			Assert.fail("failed expected assertion error");
		} catch (final AssertionError ae) {
			Assert.assertEquals(
					"Precondition unsatisfied: description != null", ae
							.getMessage());
		}
	}

}
