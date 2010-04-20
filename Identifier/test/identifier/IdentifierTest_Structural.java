package identifier;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IdentifierTest_Structural {

	private boolean b;
	
	protected Identifier id;

	@Before
	public void setUp() {
		id = new Identifier();
	}

	@Test
	public void test1stValidIdFalse() {
		b = id.validateIdentifier("_");
		assertEquals(false, b);
	}

	@Test
	public void testsValidIdFalseCaps() {
		b = id.validateIdentifier("B");
		assertEquals(true, b);
	}

	@Test
	public void testsValidIdFalseGTNCaps() {
		b = id.validateIdentifier("{");
		assertEquals(false, b);
	}

	@Test
	public void testfValidIdFalseCaps() {
		b = id.validateIdentifier("aB");
		assertEquals(true, b);
	}

	@Test
	public void testfValidIdFalseGTNCaps() {
		b = id.validateIdentifier("a{");
		assertEquals(false, b);
	}

	@Test
	public void testfValidIdFalseLTCaps() {
		b = id.validateIdentifier("a_");
		assertEquals(false, b);
	}

	@Test
	public void testfValidIdFalseGTNumberCaps() {
		b = id.validateIdentifier("a#");
		assertEquals(false, b);
	}

	@Test
	public void testDefUseValidID() {
		b = id.validateIdentifier("a#asd");
		assertEquals(false, b);
	}
}
