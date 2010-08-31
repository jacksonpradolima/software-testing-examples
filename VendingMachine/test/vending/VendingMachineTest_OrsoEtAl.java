package vending;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A sample test set for <code>vending.VendingMachine</code>.
 *
 * This test set was developed by Orso et al., in the paper "Using Component
 * Metacontent to Support the Regression Testing of Component-Based
 * Software", published at at International Conference on Software
 * Maintenance 2001.
 */
public class VendingMachineTest_OrsoEtAl {

	private VendingMachine mac;

	@Before
	public void setUp() {
		mac = new VendingMachine();
	}

	@Test
	public void testCase1() throws Exception {
		assertEquals(0, mac.returnCoins());
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase2() throws Exception {
		try {
			assertEquals(0, mac.vendItem(3));
		} catch (NoCoinsException e) {}
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());

	}

	@Test
	public void testCase3() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.returnCoins());
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase4() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		try {
			assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(3));
		} catch (InsufficientCreditException e) {}
		assertEquals(VendingMachine.COIN_VALUE, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase5() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.returnCoins());
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase6() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(0, mac.vendItem(3));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.getTotalValue());
	}

	@Test
	public void testCase7() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.returnCoins());
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase8() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(3));
		assertEquals(VendingMachine.COIN_VALUE, mac.getCurrentValue());
		assertEquals(Dispenser.VALUE, mac.getTotalValue());
	}

	@Test
	public void testCase9() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.returnCoins());
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase10() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		assertEquals((VendingMachine.COIN_VALUE * 4) - Dispenser.VALUE, mac.vendItem(3));
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.getCurrentValue());
		assertEquals(Dispenser.VALUE, mac.getTotalValue());
	}

	@Test
	public void testCase11() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.returnCoins());
		try {
			assertEquals(0, mac.vendItem(3));
		} catch (NoCoinsException e) {}
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase12() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(0, mac.vendItem(3));
		try {
			assertEquals(0, mac.vendItem(3));
		} catch (NoCoinsException e) {}
		assertEquals(0, mac.getCurrentValue());
		assertEquals(Dispenser.VALUE, mac.getTotalValue());
	}

	@Test
	public void testCase13() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.returnCoins());
		try {
			assertEquals(0, mac.vendItem(3));
		} catch (NoCoinsException e) {}
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase14() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals((VendingMachine.COIN_VALUE * 3) - Dispenser.VALUE, mac.vendItem(3));
		try {
			assertEquals((VendingMachine.COIN_VALUE * 3) - Dispenser.VALUE, mac.vendItem(3));
		} catch (InsufficientCreditException e) {}
		assertEquals((VendingMachine.COIN_VALUE * 3) - Dispenser.VALUE, mac.getCurrentValue());
		assertEquals(Dispenser.VALUE, mac.getTotalValue());
	}

	@Test
	public void testCase15() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.returnCoins());
		try {
			assertEquals(0, mac.vendItem(3));
		} catch (NoCoinsException e) {}
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase16() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		assertEquals((VendingMachine.COIN_VALUE * 4) - Dispenser.VALUE, mac.vendItem(3));
		assertEquals(0, mac.vendItem(3));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.getTotalValue());
	}

	/**
	 * Test set of available selection, but unavailable item
	 */
	@Test
	public void testCase17() throws Exception {
		try {
			assertEquals(0, mac.vendItem(5));
		} catch (NoCoinsException e) {}
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase18() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		try {
			assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(5));
		} catch (UnavailableItemException e) {}
		assertEquals(VendingMachine.COIN_VALUE, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase19() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.returnCoins());
		try {
			assertEquals(0, mac.vendItem(5));
		} catch (NoCoinsException e) {}
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase20() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		try {
			assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(5));
		} catch (UnavailableItemException e) {}
		try {
			assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(5));
		} catch (UnavailableItemException e) {}
		assertEquals(VendingMachine.COIN_VALUE, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}


	/**
	 * Test set of invalid product selection.
	 */
	@Test
	public void testCase21() throws Exception {
		try {
			assertEquals(0, mac.vendItem(35));
		} catch (NoCoinsException e) {}
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase22() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		try {
			assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(35));
		} catch (InvalidItemException e) {}
		assertEquals(VendingMachine.COIN_VALUE, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase23() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		try {
			assertEquals(VendingMachine.COIN_VALUE * 2, mac.vendItem(35));
		} catch (InvalidItemException e) {}
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase24() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		try {
			assertEquals(VendingMachine.COIN_VALUE * 3, mac.vendItem(35));
		} catch (InvalidItemException e) {}
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase25() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		try {
			assertEquals(VendingMachine.COIN_VALUE * 4, mac.vendItem(35));
		} catch (InvalidItemException e) {}
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

	@Test
	public void testCase27() throws Exception {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		try {
			assertEquals(VendingMachine.COIN_VALUE * 2, mac.vendItem(5));
		} catch (UnavailableItemException e) {}
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
	}

}