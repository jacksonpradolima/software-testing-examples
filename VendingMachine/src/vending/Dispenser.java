package vending;

import java.util.Arrays;

public class Dispenser
{
	public static final int VALUE = 50;

	private static final int[] availSelectionVals = { 1, 2, 3, 4, 6, 7, 8, 9, 10, 11, 12, 13, 14,
					15, 16, 17, 19 };

	public int dispense(int credit, int selection)
	{
		int i = Arrays.binarySearch(availSelectionVals, selection);

		if (i < 0) {
			throw new IllegalArgumentException("The selected product (" + selection
							+ ") does not exist");
		}

		if (! available(selection)) {
			throw new IllegalArgumentException("The selected product (" + selection
							+ ") is unavailable");
		}

		if (credit < VALUE) {
			return 0;
		} else {
			return VALUE;
		}
	}

	public boolean available(int sel)
	{
		return true;
	}

	public int[] getValidSelection()
	{
		return availSelectionVals;
	}
}
