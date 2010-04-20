package vending;

public class InsufficientCreditException extends RuntimeException {

	public InsufficientCreditException(String string) {
		super(string);
	}

}
