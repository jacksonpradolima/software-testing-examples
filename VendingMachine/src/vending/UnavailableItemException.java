package vending;

public class UnavailableItemException extends Exception
{
	private static final String ERR_UNAVAILABLE_ITEM = "Item selected is unavailable";

	public UnavailableItemException() {
		super(ERR_UNAVAILABLE_ITEM);
	}
}
