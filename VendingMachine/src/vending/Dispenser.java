package vending;

/**
 * This Dispenser was originally developed by Alessandro Orso et al., in
 * the paper "Using Component Metacontent to Support the Regression Testing
 * of Component-Based Software", published at at International Conference
 * on Software Maintenance 2001.
 *
 * Their implementation was modified by Marco Aurélio Graciotto Silva,
 * preserving the functions, but including new features for assessing the
 * object's states. This was required to implement the test cases using
 * JUnit.
 */
public class Dispenser
{
	public static final int VALUE = 50;

	private static final int INITIAL_VALID_ITEM = 1;

	private static final int FINAL_VALID_ITEM = 20;

	private static final int[] AVAILABLE_ITEMS = { 2, 3, 13 };

    /**
     * Simulates the behavior of the dispenser component of a vending
     * machine. A given item is dispensed when there is enough credit
     * to buy it and the item is valid and available.
     *
     * @param credit - the current value in cents in the coin compartment. A
     *                 value of 50 is necessary to buy any item.
     *
     * @param selection - the selected item. A valid item is a integer
     * number x, such that 1 <= x <= 20.
     *
     * @return the value of the item if available and the credit is sufficient,
     * or the credit if the item is invalid, unavailable or the credit is
     * insufficient.
     * @throws NoCoinsException
     * @throws InvalidItemException
     * @throws UnavailableItemException
     * @throws InsufficientCreditException
     */
	public int dispense(int credit, int selection) throws Exception
	{
		if (credit == 0) {
			throw new NoCoinsException();
		} else if (selection < INITIAL_VALID_ITEM || selection > FINAL_VALID_ITEM) {
			throw new InvalidItemException();
		} else if (! isAvailable(selection)) {
			throw new UnavailableItemException();
		} else if (credit < VALUE) {
			throw new InsufficientCreditException();
		} else {
			return VALUE;
		}
	}

	/**
	 * Check if a given product is available in the machine.
	 *
	 * @param selection Product.
	 * @return True if available, false otherwise.
	 */
	public boolean isAvailable(int selection)
	{
		for (int i = 0; i < AVAILABLE_ITEMS.length; i++) {
			if (AVAILABLE_ITEMS[i] == selection) {
				return true;
			}
		}
		return false;
	}
}
