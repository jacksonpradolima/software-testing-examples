package vending;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DispenserTest_Functional {

	protected Dispenser d;

	@Before
	public void setUp() {
		d = new Dispenser();
	}

	/**
	 * First test case: testing different credit parameter.
	 *
	 * Increase the value necessary to by a valid and available item.
	 */
	@Test
	public void testDispenseCredit_1() {
		int expense = d.dispense(50, 1);
		assertEquals(50, expense);

		expense = d.dispense(51, 1);
		assertEquals(50, expense);

		expense = d.dispense(100, 19);
		assertTrue(expense == 50);
	}

	/**
	 * First test case: testing different credit parameter.
	 *
	 * Increase the value necessary to by a valid and available item.
	 */
	@Test(expected = InsufficientCreditException.class)
	public void testDispenseCredit_2() {
		int expense = d.dispense(50, 1);
		assertEquals(50, expense);

		expense = d.dispense(51, 1);
		assertEquals(50, expense);

		expense = d.dispense(100, 19);
		assertTrue(expense == 50);

		expense = d.dispense(49, 1);
	}

	/**
	 * Second test case: testing different credit parameter.
	 *
	 * Increase the value necessary to by a valid and available item.
	 */
	@Test
	public void testCharge() {
		for (int i = 0; i <= 500; i += 25) {
			int expense = d.dispense(50 + i, 1);
			assertEquals(50, expense);
		}
	}

	/**
	 * Third test case: checks no coin exception.
	 */
	@Test(expected=NoCoinsException.class)
	public void testNoCoin() {
		d.dispense(0, 10);
	}

	/**
	 * Fourth test case: checks the invalid range for items.
	 */
	@Test(expected=InvalidItemException.class)
	public void testInvalidItems() {
		for (int i = 0; i >= -10; i--) {
			d.dispense(50, i);
		}
	}

	/**
	 * Fifth test case: checks the unavailable items.
	 */
	@Test(expected=UnavailableItemException.class)
	public void testUnavailableItems() {
		d.dispense(50, 5);
		d.dispense(50, 18);
		d.dispense(50, 20);
	}

	/**
	 * Sixth test case: checks the not enough credit exception.
	 */
	@Test(expected=InsufficientCreditException.class)
	public void testNotEnoughtCredit() {
		for (int i = 49; i >= -1000; i--) {
			d.dispense(i, 10);
		}
	}
}