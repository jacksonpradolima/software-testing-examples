package identifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class IdentifierTest_Functional {
	protected Identifier id;

	@Before
	public void setUp() {
		id = new Identifier();
	}

	@Test
	public void validateInitialSymbol1() {
		boolean result = id.validateIdentifier("Abcd5");
		Assert.assertTrue(result);
	}

	@Test
	public void validateInitialSymbol2() {
		boolean result = id.validateIdentifier("abcd5");
		Assert.assertTrue(result);
	}

	@Test
	public void validateInitialSymbol3() {
		boolean result = id.validateIdentifier("&123");
		Assert.assertFalse(result);
	}

	@Test
	public void validateInitialSymbol4() {
		boolean result = id.validateIdentifier(" 123");
		Assert.assertFalse(result);
	}

	@Test
	public void validateInitialSymbol5() {
		boolean result = id.validateIdentifier("1123");
		Assert.assertFalse(result);
	}

	@Test
	public void validateMinLength1() {
		boolean result = id.validateIdentifier("");
		Assert.assertFalse(result);
	}

	@Test
	public void validateMinLength2() {
		boolean result = id.validateIdentifier("a");
		Assert.assertTrue(result);
	}

	@Test
	public void validateMaxLength1() {
		boolean result = id.validateIdentifier("a23456");
		Assert.assertTrue(result);
	}

	@Test
	public void validateMaxLength2() {
		boolean result = id.validateIdentifier("a234567");
		Assert.assertFalse(result);
	}

	@Test
	public void validateSymbol() {
		boolean result = id.validateIdentifier("Inv@lido");
		Assert.assertFalse(result);
	}
}