package vending;

import java.util.Arrays;

public class Dispenser
{
	public static final int VALUE = 50;
	
	private static final int INITIAL_VALID_ITEM = 1;

	private static final int FINAL_VALID_ITEM = 20;

	private static final int[] AVAILABLE_ITEMS = { 1, 2, 3, 4, 6, 7, 8, 9, 10, 11, 12, 13, 14,
					15, 16, 17, 19 };
	   
    /**
     * Simulates the behaviour of the dispenser component of a vending
     * machine. A given item is dispensed when there is enought credit
     * to buy it and the item is valid and available.
     *
     * @param credit - the current value in cents in the coin compartment. A
     *                 value of 50 is necessary to buy any item.
     *
     * @param selection - the selected item. A valid item is a integer
     * number x, such that 1 <= x <= 20. 
     *
     * @return the value of the item (expense).
     *
     * @throws NoCoinsException when no coin has been inserted in the machine.
     * @throws InvalidItemException when the selected item is invalid.
     * @throws UnavailableItemException when the selected item is valid but is
     * currently unavailable.
     * @throws InsufficientCreditException when the selected item is valid and
     * available, but the credit is not enought to buy it.
     */
	public int dispense(int credit, int selection)
	{
		if (credit <= 0) {
			throw new NoCoinsException();
		}
		
		if (! isValid(selection)) {
			throw new InvalidItemException("The selected product (" + selection + ") is invalid (does not exist)");
		}

		if (! isAvailable(selection)) {
			throw new UnavailableItemException("The selected product (" + selection + ") is unavailable");
		}

		if (credit < VALUE) {
			int value = VALUE - credit;
			throw new InsufficientCreditException("Insufficient credit: more " + value + " is required");
		} else {
			return VALUE;
		}
	}

	public boolean isValid(int selection)
	{
		if (selection >= INITIAL_VALID_ITEM && selection <= FINAL_VALID_ITEM) {
			return true;
		}
		return false;
	}
	
	
	public boolean isAvailable(int selection)
	{
		int i = Arrays.binarySearch(AVAILABLE_ITEMS, selection);
		if (i < 0) {
			return false;
		} else {
			return true;
		}
	}

	public int[] getValidSelection()
	{
		return AVAILABLE_ITEMS;
	}
}
