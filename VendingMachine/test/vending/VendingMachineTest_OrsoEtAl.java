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
	public void testCase1() {
		assertEquals(0, mac.returnCoins());
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(VendingMachine.DEFAULT_MSG, mac.getMessage());
	}

	@Test
	public void testCase2() {
		assertEquals(0, mac.vendItem(3));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_NO_COINS, mac.getMessage());
	}

	@Test
	public void testCase3() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.returnCoins());
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(VendingMachine.DEFAULT_MSG, mac.getMessage());
	}

	@Test
	public void testCase4() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(3));
		assertEquals(VendingMachine.COIN_VALUE, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_INSUFFICIENT_CREDIT, mac.getMessage());
	}

	@Test
	public void testCase5() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.returnCoins());
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(VendingMachine.DEFAULT_MSG, mac.getMessage());
	}

	@Test
	public void testCase6() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(0, mac.vendItem(3));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.getTotalValue());
		assertEquals(VendingMachine.DEFAULT_MSG, mac.getMessage());
	}

	@Test
	public void testCase7() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.returnCoins());
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(VendingMachine.DEFAULT_MSG, mac.getMessage());
	}

	@Test
	public void testCase8() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(3));
		assertEquals(VendingMachine.COIN_VALUE, mac.getCurrentValue());
		assertEquals(Dispenser.VALUE, mac.getTotalValue());
		assertEquals(VendingMachine.DEFAULT_MSG, mac.getMessage());
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
		assertEquals(VendingMachine.DEFAULT_MSG, mac.getMessage());
	}

	@Test
	public void testCase10() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		assertEquals((VendingMachine.COIN_VALUE * 4) - Dispenser.VALUE, mac.vendItem(3));
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.getCurrentValue());
		assertEquals(Dispenser.VALUE, mac.getTotalValue());
		assertEquals(VendingMachine.DEFAULT_MSG, mac.getMessage());
	}

	@Test
	public void testCase11() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.returnCoins());
		assertEquals(0, mac.vendItem(3));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_NO_COINS, mac.getMessage());
	}

	@Test
	public void testCase12() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(0, mac.vendItem(3));
		assertEquals(0, mac.vendItem(3));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(Dispenser.VALUE, mac.getTotalValue());
		assertEquals(Dispenser.ERR_NO_COINS, mac.getMessage());
	}

	@Test
	public void testCase13() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.returnCoins());
		assertEquals(0, mac.vendItem(3));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_NO_COINS, mac.getMessage());
	}

	@Test
	public void testCase14() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals((VendingMachine.COIN_VALUE * 3) - Dispenser.VALUE, mac.vendItem(3));
		assertEquals((VendingMachine.COIN_VALUE * 3) - Dispenser.VALUE, mac.vendItem(3));
		assertEquals((VendingMachine.COIN_VALUE * 3) - Dispenser.VALUE, mac.getCurrentValue());
		assertEquals(Dispenser.VALUE, mac.getTotalValue());
		assertEquals(Dispenser.ERR_INSUFFICIENT_CREDIT, mac.getMessage());
	}

	@Test
	public void testCase15() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.returnCoins());
		assertEquals(0, mac.vendItem(3));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_NO_COINS, mac.getMessage());
	}

	@Test
	public void testCase16() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		assertEquals((VendingMachine.COIN_VALUE * 4) - Dispenser.VALUE, mac.vendItem(3));
		assertEquals(0, mac.vendItem(3));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.getTotalValue());
		assertEquals(VendingMachine.DEFAULT_MSG, mac.getMessage());
	}

	/**
	 * Test set of available selection, but unavailable item
	 */
	@Test
	public void testCase17() {
		assertEquals(0, mac.vendItem(5));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_NO_COINS, mac.getMessage());
	}

	@Test
	public void testCase18() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(5));
		assertEquals(VendingMachine.COIN_VALUE, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_UNAVAILABLE_ITEM, mac.getMessage());
	}

	@Test
	public void testCase19() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.returnCoins());
		assertEquals(0, mac.vendItem(5));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_NO_COINS, mac.getMessage());
	}

	@Test
	public void testCase20() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(5));
		assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(5));
		assertEquals(VendingMachine.COIN_VALUE, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_UNAVAILABLE_ITEM, mac.getMessage());
	}

	@Test
	public void testCase27() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.vendItem(5));
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_UNAVAILABLE_ITEM, mac.getMessage());
	}

	/**
	 * Test set of invalid product selection.
	 */
	@Test
	public void testCase21() {
		assertEquals(0, mac.vendItem(35));
		assertEquals(0, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_NO_COINS, mac.getMessage());
	}

	@Test
	public void testCase22() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.vendItem(35));
		assertEquals(VendingMachine.COIN_VALUE, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_INVALID_SELECTION, mac.getMessage());
	}

	@Test
	public void testCase23() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.vendItem(35));
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_INVALID_SELECTION, mac.getMessage());
	}

	@Test
	public void testCase24() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.vendItem(35));
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_INVALID_SELECTION, mac.getMessage());
	}

	@Test
	public void testCase25() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.vendItem(35));
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.getCurrentValue());
		assertEquals(0, mac.getTotalValue());
		assertEquals(Dispenser.ERR_INVALID_SELECTION, mac.getMessage());
	}
}