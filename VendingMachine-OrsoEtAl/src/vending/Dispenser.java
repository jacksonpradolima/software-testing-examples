package vending;

public class Dispenser
{
	final private int MAXSEL = 20;
	final private int VAL = 50;
	private int[] availSelectionVals = { 2, 3, 13 };

	public int dispense(int credit, int sel) {
        int val = 0;

        if (credit == 0) {
            System.err.println("No coins inserted");
        } else if (sel > MAXSEL) {
            System.err.println("Wrong selection " + sel);
        } else if (! available(sel)) {
            System.err.println("Selection " + sel + " unavailable");
        } else {
            val = VAL;
            if (credit < val) {
                System.err.println("Enter " + (val - credit) + " coins");
                // val = 0; /* Error that is detected by test cases 4 and 14 */
            } else {
                System.err.println("Take selection");
            }
        }
        return val;
    }

	private boolean available(int sel) {
		for (int i = 0; i < availSelectionVals.length; i++) {
			if (availSelectionVals[i] == sel) {
				return true;
			}
		}
		return false;
	}
}