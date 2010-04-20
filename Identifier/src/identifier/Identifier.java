package identifier;

public class Identifier
{
	public boolean validateIdentifier(String s)
	{
		char achar;
		boolean valid_id = true;

		if (s == null) {
			return false;
		}
		
		// We could check for s.length() == 0, but we want to force an ED test requirement here
		try {
			achar = s.charAt(0);
		} catch (StringIndexOutOfBoundsException e) {
			return false;
		}
		
		valid_id = valid_s(achar);
		if (s.length() > 1) {
			for (int i = 1; i < s.length(); i++) {
				achar = s.charAt(i);
				if (! valid_f(achar)) {
					valid_id = false;
				}
			}
		}
		if (valid_id && (s.length() >= 1) && (s.length() <= 6))
			return true;
		else
			return false;
	}

	public boolean valid_s(char ch)
	{
		if (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')))
			return true;
		else
			return false;
	}

	public boolean valid_f(char ch)
	{
		if (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z'))
				|| ((ch >= '0') && (ch <= '9')))
			return true;
		else
			return false;
	}

	public static void main(String[] args)
	{
		if (args.length == 0) {
			System.out.println("Usage: identifier.Identifier [string]");
		} else {
			Identifier id = new Identifier();
			if (id.validateIdentifier(args[0])) {
				System.out.println("Valid");
			} else {
				System.out.println("Invalid");
			}
		}
	}
}