package vending;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class VendingMachine
{
	public static final int COIN_VALUE = 25;

	protected static final int MAX_VALUE = 1000;
	
	private int totalValue;
	
	private int currentValue;
	
	private Dispenser dispenser;

	public VendingMachine()
	{
		dispenser = new Dispenser();
	}

	public int getCurrentValue()
	{
		return currentValue;
	}
	
	protected int getTotalValue()
	{
		return totalValue;
	}
	
	
	public boolean insertCoin()
	{
		if (currentValue < MAX_VALUE) {
			currentValue += COIN_VALUE;
			return true;
		} else {
			return false;
		}
	}

	public int returnCoins()
	{
		if (currentValue == 0) {
			return 0;
		} else {
			int returnedCoins = 0;
			returnedCoins = currentValue;
			currentValue = 0;
			return returnedCoins;
		}
	}
	
	public String listItems()
	{
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

	public boolean vendItem(int selection)
	{
		int expense = dispenser.dispense(currentValue, selection);
		if (expense == 0) {
			return false;
		} else {
			totalValue += expense;
			currentValue -= expense;
			return true;
		}
	}
	
	public static void main(String[] args)
	{
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
					if (methodName.equals("insert")) {
						machine.insertCoin();
					} else if (methodName.equals("return")) {
						machine.returnCoins();
					} else if (methodName.equals("show")) {
						System.out.println(machine.listItems());
					} else if (methodName.equals("select")) {
						try {
							String value = tokens.nextToken();
							machine.vendItem(Integer.parseInt(value));
						} catch (IllegalArgumentException e) {
							System.out.println(e.getMessage());
						} catch (Exception e) {
							System.out.println("You must enter a number to select a product.");
						}
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading user commands. Bailing out...");
		}
		System.out.println("VendingMachine has been turned off");
	}

	
}
