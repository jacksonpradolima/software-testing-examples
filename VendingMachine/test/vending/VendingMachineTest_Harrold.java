package vending;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A sample test case, testing <code>vending.VendingMachine</code>.
 */
public class VendingMachineTest_Harrold {

	private VendingMachine mac;

	@Before
	public void setUp() {
		mac = new VendingMachine();
	}

	@Test
	public void testCase1() {
		int value = mac.returnCoins();
		assertTrue(value == 0);
	}

	@Test(expected = NoCoinsException.class)
	public void testCase2() {
		mac.vendItem(3);
	}

	@Test
	public void testCase3() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.returnCoins());
	}

	@Test(expected = InsufficientCreditException.class)
	public void testCase4() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		mac.vendItem(3);
	}

	@Test
	public void testCase5() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		int value = mac.insertCoin();
		assertTrue(value == mac.returnCoins());
	}

	@Test
	public void testCase6() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		int charge = mac.vendItem(3);
		assertTrue(charge == 0);
	}

	@Test
	public void testCase7() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		int value = mac.returnCoins();
		assertEquals(VendingMachine.COIN_VALUE * 3, value);
	}

	@Test
	public void testCase8() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		int change = mac.vendItem(3);
		assertEquals(VendingMachine.COIN_VALUE, change);
	}

	@Test
	public void testCase9() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		int value = mac.returnCoins();
		assertEquals(VendingMachine.COIN_VALUE * 4, value);
	}

	@Test
	public void testCase10() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		int change = mac.vendItem(3);
		assertEquals((VendingMachine.COIN_VALUE * 4) - Dispenser.VALUE, change);
	}

	@Test(expected=NoCoinsException.class)
	public void testCase11() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.returnCoins());
		mac.vendItem(3);
	}

	@Test(expected=NoCoinsException.class)
	public void testCase12() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		int charge = mac.vendItem(3);
		assertEquals(0, charge);
		mac.vendItem(3);
	}

	@Test(expected=NoCoinsException.class)
	public void testCase13() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertTrue(VendingMachine.COIN_VALUE * 3 == mac.returnCoins());
		mac.vendItem(3);
	}

	@Test(expected=InsufficientCreditException.class)
	public void testCase14() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());

		int charge = mac.vendItem(3);
		assertEquals(VendingMachine.COIN_VALUE, charge);
		mac.vendItem(3);
	}

	@Test(expected=NoCoinsException.class)
	public void testCase15() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.returnCoins());
		mac.vendItem(3);
	}

	@Test
	public void testCase16() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		assertEquals((VendingMachine.COIN_VALUE * 4) - Dispenser.VALUE, mac.vendItem(3));
		assertEquals(0, mac.vendItem(3));
	}

	@Test(expected=NoCoinsException.class)
	public void testCase17() {
		mac.vendItem(5);
	}

	@Test(expected=UnavailableItemException.class)
	public void testCase18() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		mac.vendItem(5);
	}

	@Test(expected=NoCoinsException.class)
	public void testCase19() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE, mac.returnCoins());
		mac.vendItem(5);
	}

	@Test(expected=UnavailableItemException.class)
	public void testCase20() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		mac.vendItem(5);
	}

	@Test(expected=NoCoinsException.class)
	public void testCase21() {
		mac.vendItem(35);
	}

	@Test(expected=InvalidItemException.class)
	public void testCase22() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		mac.vendItem(35);
	}

	@Test(expected=InvalidItemException.class)
	public void testCase23() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		mac.vendItem(35);
	}

	@Test(expected=InvalidItemException.class)
	public void testCase24() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		mac.vendItem(35);
	}

	@Test(expected=InvalidItemException.class)
	public void testCase25() {
		assertEquals(VendingMachine.COIN_VALUE, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 2, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 3, mac.insertCoin());
		assertEquals(VendingMachine.COIN_VALUE * 4, mac.insertCoin());
		mac.vendItem(35);
	}
}