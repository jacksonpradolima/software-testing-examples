package factorial;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class FactorialTest_Functional
{
	private Factorial factorial;

	@Before
	public void setUp() {
		factorial = new Factorial();
	}

	@Test
	public void testCompute() {
		assertEquals(2, factorial.compute(2));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNegativeNumber() {
		factorial.compute(-34);
	}

	@Test
	public void testValidNumber() {
		long f = factorial.compute(10);
		assertEquals(3628800, f);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testOutOfBoundsNumber() {
		factorial.compute(212);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testOutOfBoundsLimitNumber() {
		factorial.compute(21);
	}

	@Test
	public void testValidLimitNumber() {
		long f = factorial.compute(20);
		String s = "2432902008176640000";
		long l = Long.valueOf(s).longValue();
		assertEquals(l, f);
	}

	@Test
	public void testValidLimitNumber2() {
		long f = factorial.compute(0);
		assertEquals(1, f);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNegativeLimitNumber() {
		factorial.compute(-1);
	}

}
