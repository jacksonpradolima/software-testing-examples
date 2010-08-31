package vending;

public class InsufficientCreditException extends Exception
{
	private static final String ERR_INSUFFICIENT_CREDIT = "Insufficient credit";

	public InsufficientCreditException() {
		super(ERR_INSUFFICIENT_CREDIT);
	}
}
