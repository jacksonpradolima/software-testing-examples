package vending;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class VendingTestDriver {

	/**
	 * These method simulates the behavior of the vending machine. Actions like
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
	 * the user, indicating whether it was executed successfully or not.
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

		System.out.println("VendingMachine is operational");
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
							System.out.println("Take your coins (" + value + ")");
						}
					} else if (methodName.equals("show")) {
						System.out.println(machine.listItems());
					} else if (methodName.equals("vendItem")) {
						String argument = tokens.nextToken();
						Integer selection = Integer.parseInt(argument);
						value = machine.vendItem(selection.intValue());
						if (VendingMachine.DEFAULT_MSG.equals(machine.getMessage())) {
							System.out.println("Take your item");
							System.out.println("Current value = " + value);
						} else {
							System.out.println(machine.getMessage());
						}
					}
				} else {
					System.err.println("Invalid operation.");
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading user commands. Bailing out...");
		}
		System.out.println("VendingMachine has been shut down");
	}

}
