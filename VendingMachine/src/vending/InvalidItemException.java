package vending;

public class InvalidItemException extends Exception
{
	private static final String ERR_INVALID_SELECTION = "Invalid selection";

	public InvalidItemException() {
		super(ERR_INVALID_SELECTION);
	}
}
