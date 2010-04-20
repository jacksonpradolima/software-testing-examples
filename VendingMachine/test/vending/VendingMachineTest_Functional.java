package vending;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class VendingMachineTest_Functional
{
	private VendingMachine mac;

	@Before
	public void setUp() {
		mac = new VendingMachine();
	}

	/**
	 * Test Case 1: check the behaviour of the return coin operation
	 */
	@Test
	public void testReturnCoin() {
		// When no coin inserted, result should be 0
		int result = mac.returnCoins();
		assertTrue(result == 0);

		for (int i = 1; i <= 10; i++) {
			for (int j = VendingMachine.COIN_VALUE; j <= VendingMachine.COIN_VALUE * i; j += VendingMachine.COIN_VALUE) {
				mac.insertCoin();
			}
			// return all coins
			result = mac.returnCoins();
			assertEquals(VendingMachine.COIN_VALUE * i, result);

			// no coins should be returned
			result = mac.returnCoins();
			assertTrue(result == 0);
		}
	}

	/**
	 * Test Case 2: check the behaviour of the insert coin operation
	 */
	@Test
	public void testInsertCoin() {
		for (int i = 1; i <= 30; i++) {
			int result = mac.insertCoin();
			assertEquals(VendingMachine.COIN_VALUE * i, result);
		}
	}

	/**
	 * Test Case 2: check the behaviour of the insert coin operation
	 */
	@Test(expected=MachineOutOfService.class)
	public void testInsertTooMuchCoin() {
		for (int i = 1; i <= 1000; i++) {
			int result = mac.insertCoin();
			assertEquals(VendingMachine.COIN_VALUE * i, result);
		}
	}

	
	/**
	 * Test Case 3: check the behaviour of the vend item operation
	 */
	@Test
	public void testVendItem() {
		// Check is all valid and available item can be bought by 50
		mac.insertCoin();
		mac.insertCoin();

		int result = mac.vendItem(1);
		assertEquals(0, result);
	}

	/**
	 * Test Case 4: check the behaviour of the vend item operation when no coin
	 * had been inserted.
	 */
	@Test(expected=NoCoinsException.class)
	public void testVendItemNoCoin() {
		// Check is all valid and available item can be bought by 50
		mac.vendItem(1);
	}

	/**
	 * Test Case 5: check the behaviour of the vend item operation when an
	 * invalid item is being sold.
	 */
	@Test(expected=InvalidItemException.class)
	public void testVendItemInvalidException() {
		// All itens below 1 and above 20 should be invalid
		mac.insertCoin();
		mac.insertCoin();
		for (int i = 0; i >= -1000; i -= 5) {
			mac.vendItem(i);
		}
		for (int i = 21; i <= 1000; i += 5) {
			mac.vendItem(i);
		}
	}

	/**
	 * Test Case 6: check the behaviour of the vent item operation when a valid
	 * but unavailable item is being sold.
	 */
	@Test(expected=UnavailableItemException.class)
	public void testUnavailableItems() {
		mac.insertCoin();
		mac.insertCoin();
		mac.vendItem(5);
		mac.vendItem(18);
		mac.vendItem(20);
	}

	/**
	 * Test Case 7: checks the behaviour of the vend item when a valid and
	 * available item is being sold bu the credit is not enought.
	 */
	@Test(expected=InsufficientCreditException.class)
	public void testNotEnoughtCredit() {
		// Only one coin inserted
		mac.insertCoin();
		mac.vendItem(10); // valid and available item
	}

	/**
	 * Test Case 8: checks the behaviour of the vend item when a valid and
	 * available item is being sold and there is enougth credit.
	 */
	@Test
	public void testRemainCredit() {
		for (int i = 1; i <= 20; i++) {
			mac.returnCoins(); // return all coins
			for (int j = 1; j <= i; j++) { // inserting 50 i times
				mac.insertCoin();
				mac.insertCoin();
			}
			int value = mac.vendItem(10); // valid and available item
			assertEquals((i * 2 * VendingMachine.COIN_VALUE) - Dispenser.VALUE, value); // checking the charge
		}
	}
}