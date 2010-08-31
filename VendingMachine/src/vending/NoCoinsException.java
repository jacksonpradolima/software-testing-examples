package vending;

public class NoCoinsException extends Exception
{
	private static final String ERR_NO_COINS = "No coins inserted";

	public NoCoinsException() {
		super(ERR_NO_COINS);
	}
}
