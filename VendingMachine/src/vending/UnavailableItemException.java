package vending;

public class UnavailableItemException extends RuntimeException {

	public UnavailableItemException(String string) {
		super(string);
	}

}
