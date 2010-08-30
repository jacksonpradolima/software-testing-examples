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

	private String errorMessage;

	public static final String ERR_NO_COINS = "No coins inserted";

	public static final String ERR_INVALID_SELECTION = "Invalid selection";

	public static final String ERR_UNAVAILABLE_ITEM = "Item selected is unavailable";

	public static final String ERR_INSUFFICIENT_CREDIT = "Insufficient credit";


	public Dispenser() {
		errorMessage = null;
	}

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
     */
	public int dispense(int credit, int selection)
	{
		if (credit <= 0) {
			errorMessage = ERR_NO_COINS;
		} else if (selection < INITIAL_VALID_ITEM || selection > FINAL_VALID_ITEM) {
			errorMessage = ERR_INVALID_SELECTION;
		} else if (! isAvailable(selection)) {
			errorMessage = ERR_UNAVAILABLE_ITEM;
		} else if (credit < VALUE) {
			errorMessage = ERR_INSUFFICIENT_CREDIT;
		} else {
			errorMessage = null;
			return VALUE;
		}

		return 0;
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

	public int[] getValidSelection()
	{
		return AVAILABLE_ITEMS;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
