package vending;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class VendingMachine {
	public static final int COIN_VALUE = 25;

	protected static final int MAX_VALUE = 1000;

	private int totalValue;

	private int currentValue;

	private Dispenser dispenser;

	public VendingMachine() {
		dispenser = new Dispenser();
	}

	public int getCurrentValue() {
		return currentValue;
	}

	protected int getTotalValue() {
		return totalValue;
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
		if ((currentValue + COIN_VALUE) > MAX_VALUE) {
			throw new MachineOutOfService();
		}

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
		if (currentValue != 0) {
			currentValue = 0;
		}
		return value;
	}

	public String listItems() {
		int[] items = dispenser.getValidSelection();
		int i = 0;
		StringBuilder sb = new StringBuilder();

		for (i = 0; i < (items.length - 1); i++) {
			sb.append(items[i]);
			sb.append(", ");
		}
		sb.append(items[i]);

		return sb.toString();
	}

	/**
	 * This method simulates the request for a given item.
	 * 
	 * @param number
	 *            of a given item. Valid items are numbered from 1 to 20.
	 * 
	 * @return the current value remaining in cents after on success.
	 * 
	 * @throws NoCoinsException
	 *             when no coin has been inserted.
	 * @throws InvalidItemException
	 *             when the selected item is invalid.
	 * @throws UnavailableItemException
	 *             when the selected item is valid but is currently unavailable.
	 * @throws NotEnoughtCreditException
	 *             when the selected item is invalid but the credit is not
	 *             enought to buy it.
	 */
	public int vendItem(int selection) {
		int expense = dispenser.dispense(currentValue, selection);
		totalValue += expense;
		currentValue -= expense;
		return currentValue;
	}

	/**
	 * These method simulates the behaviour of the vending machine. Actions like
	 * insert a coin, ask to return the coins and ask to buy a item are
	 * performed by calling the operations: <BR>
	 * <UL>
	 * <LI>insertCoin;</LI>
	 * <LI>returnCoin; and</LI>
	 * <LI>vendItem <item_number>.</LI>
	 * <\UL>
	 * <P>
	 * The operations can be entered by keyboard or by text file in any order.
	 * 
	 * After executing each one of these operations, a message is displayed to
	 * the user, indicanting whether it was executed successfully or not.
	 */
	public static void main(String[] args) {
		VendingMachine machine = new VendingMachine();
		BufferedReader inputReader = null;
		String command = null;

		if (args.length < 1) {
			inputReader = new BufferedReader(new InputStreamReader(System.in));
		} else {
			try {
				inputReader = new BufferedReader(new FileReader(args[0]));
			} catch (FileNotFoundException e) {
				System.err.println("Cannot read file, falling back to read from the stdin");
				inputReader = new BufferedReader(new InputStreamReader(System.in));
			}
		}

		System.out.println("VendingMachine is operational!");
		try {
			while ((command = inputReader.readLine()) != null) {
				StringTokenizer tokens = new StringTokenizer(command);
				if (tokens.hasMoreTokens()) {
					String methodName = tokens.nextToken();
					int value = 0;

					if (methodName.equals("insertCoin")) {
						value = machine.insertCoin();
						System.out.println("Current value = " + value);
					} else if (methodName.equals("returnCoin")) {
						value = machine.returnCoins();
						if (value == 0) {
							System.out.println("No coins to return");
						} else {
							System.out.println("Take your coins");
						}
					} else if (methodName.equals("show")) {
						System.out.println(machine.listItems());
					} else if (methodName.equals("vendItem")) {
						String argument = tokens.nextToken();
						Integer selection = Integer.parseInt(argument);
						try {
							value = machine.vendItem(selection.intValue());

							System.out.println("Take your item!!!");
							System.out.println("Current value = " + value);
						} catch (NoCoinsException nce) {
							System.out.println("No coins inserted!!!");
						} catch (InvalidItemException ite) {
							System.out.println("The requested item (" + selection
									+ ") is invalid!!!");
						} catch (UnavailableItemException uie) {
							System.out.println("The requested item (" + selection
									+ ") is unavailable!!!");
						} catch (InsufficientCreditException ice) {
							System.out.println("Current value not enought to buy item " + selection
									+ ".");
						}
					}
				} else {
					System.out.println("Invalid operation.");
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading user commands. Bailing out...");
		}
		System.out.println("VendingMachine has been turned off");
	}
}
