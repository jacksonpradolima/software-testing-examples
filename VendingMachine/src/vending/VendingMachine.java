package vending;

/**
 * This VendingMachine was originally developed by Alessandro Orso et al., in
 * the paper "Using Component Metacontent to Support the Regression Testing
 * of Component-Based Software", published at at International Conference
 * on Software Maintenance 2001.
 *
 * Their implementation was modified by Marco Aurélio Graciotto Silva,
 * preserving the functions, but including new features for assessing the
 * object's states. This was required to implement the test cases using
 * JUnit.
 */
public class VendingMachine
{
	public static final int COIN_VALUE = 25;

	private int totalValue;

	private int currentValue;

	private Dispenser dispenser;

	public VendingMachine() {
		totalValue = 0;
		currentValue = 0;
		dispenser = new Dispenser();
	}

	/**
	 * This method simulates that a new coin was inserted. It returns the
	 * current value in cents in the machine.
	 *
	 * @param - no parameter required.
	 * @return - the current value in cents.
	 *
	 * @throws MachineOutOfService
	 *             if it has so much money that cannot get any more coins.
	 */
	public int insertCoin() {
		currentValue += COIN_VALUE;
		return currentValue;
	}

	/**
	 * This method simulates the return of the coins.
	 *
	 * @param - no parameter required.
	 * @return - the current value in cents, or zero when no coin was inserted.
	 */
	public int returnCoins() {
		int value = currentValue;
		currentValue = 0;
		return value;
	}

	/**
	 * This method simulates the request for a given item.
	 *
	 * @param number
	 *            of a given item. Valid items are numbered from 1 to 20.
	 *
	 * @return the current value remaining in cents after on success.
	 */
	public int vendItem(int selection) throws Exception {
		int expense = 0;

		try {
			expense = dispenser.dispense(currentValue, selection);
		} catch (Exception e) {
			throw e;
		}
		totalValue += expense;
		currentValue -= expense;
		return currentValue;
	}

	public int getCurrentValue() {
		return currentValue;
	}

	protected int getTotalValue() {
		return totalValue;
	}
}
