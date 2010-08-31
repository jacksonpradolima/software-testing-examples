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
	 * @throws Exception
	 */
	@Test
	public void testDispenseCredit_1() throws Exception {
		int expense = d.dispense(50, 2);
		assertEquals(50, expense);

		expense = d.dispense(51, 2);
		assertEquals(50, expense);

		expense = d.dispense(100, 13);
		assertTrue(expense == 50);
	}

	/**
	 * First test case: testing different credit parameter.
	 *
	 * Increase the value necessary to by a valid and available item.
	 */
	@Test(expected = InsufficientCreditException.class)
	public void testDispenseCredit_2() throws Exception {
		int expense = d.dispense(50, 2);
		assertEquals(50, expense);

		expense = d.dispense(51, 2);
		assertEquals(50, expense);

		expense = d.dispense(100, 13);
		assertTrue(expense == 50);

		expense = d.dispense(49, 2);
	}

	/**
	 * Second test case: testing different credit parameter.
	 *
	 * Increase the value necessary to by a valid and available item.
	 */
	@Test
	public void testCharge() throws Exception {
		for (int i = 0; i <= 500; i += 25) {
			int expense = d.dispense(50 + i, 2);
			assertEquals(50, expense);
		}
	}

	/**
	 * Third test case: checks no coin exception.
	 */
	@Test(expected=NoCoinsException.class)
	public void testNoCoin() throws Exception {
		d.dispense(0, 10);
	}

	/**
	 * Fourth test case: checks the invalid range for items.
	 */
	@Test(expected=InvalidItemException.class)
	public void testInvalidItems() throws Exception {
		for (int i = 0; i >= -10; i--) {
			d.dispense(50, i);
		}
	}

	/**
	 * Fifth test case: checks the unavailable items.
	 */
	@Test(expected=UnavailableItemException.class)
	public void testUnavailableItems() throws Exception {
		d.dispense(50, 5);
		d.dispense(50, 18);
		d.dispense(50, 20);
	}

	/**
	 * Sixth test case: checks the not enough credit exception.
	 */
	@Test(expected=InsufficientCreditException.class)
	public void testNotEnoughtCredit() throws Exception {
		for (int i = 49; i >= -1000; i--) {
			d.dispense(i, 13);
		}
	}

	@Test(expected=NoCoinsException.class)
	public void testNotEnoughtCredit_Negative() throws Exception {
		d.dispense(-1, 13);
	}
}