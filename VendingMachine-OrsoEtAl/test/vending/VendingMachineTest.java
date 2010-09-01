package vending;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A sample test set for <code>vending.VendingMachine</code>.
 *
 * This test set was developed by Orso et al., in the paper "Using Component
 * Metacontent to Support the Regression Testing of Component-Based
 * Software", published at at International Conference on Software
 * Maintenance 2001.
 */
public class VendingMachineTest {

	private VendingMachine mac;

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	private void resetStreams() {
		outContent.reset();
		errContent.reset();
	}

	@Before
	public void setUp() {
		mac = new VendingMachine();
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@After
	public void tearDown() {
		resetStreams();
		System.setOut(null);
		System.setErr(null);
	}

	@Test
	public void testCase1() {
		mac.returnCoin();
		assertEquals("No coins to return", errContent.toString().trim());
	}

	@Test
	public void testCase2() {
		mac.vendItem(3);
		assertEquals("No coins inserted", errContent.toString().trim());
		assertEquals("Current value = 0", outContent.toString().trim());

	}

	@Test
	public void testCase3() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		mac.returnCoin();
		assertEquals("", errContent.toString().trim());
	}

	@Test
	public void testCase4() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("Enter 25 coins", errContent.toString().trim());
		assertEquals("Current value = 25", outContent.toString().trim());
	}

	@Test
	public void testCase5() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.returnCoin();
		assertEquals("Take your coins", outContent.toString().trim());
	}

	@Test
	public void testCase6() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("Take selection", errContent.toString().trim());
		assertEquals("Current value = 0", outContent.toString().trim());
	}

	@Test
	public void testCase7() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 75", outContent.toString().trim());
		resetStreams();
		mac.returnCoin();
		assertEquals("Take your coins", outContent.toString().trim());
	}

	@Test
	public void testCase8() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 75", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("Take selection", errContent.toString().trim());
		assertEquals("Current value = 25", outContent.toString().trim());
	}

	@Test
	public void testCase9() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 75", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 100", outContent.toString().trim());
		resetStreams();
		mac.returnCoin();
		assertEquals("Take your coins", outContent.toString().trim());
	}

	@Test
	public void testCase10() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 75", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 100", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("Take selection", errContent.toString().trim());
		assertEquals("Current value = 50", outContent.toString().trim());

	}

	@Test
	public void testCase11() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.returnCoin();
		assertEquals("Take your coins", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("No coins inserted", errContent.toString().trim());
		assertEquals("Current value = 0", outContent.toString().trim());
	}

	@Test
	public void testCase12() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("Take selection", errContent.toString().trim());
		assertEquals("Current value = 0", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("No coins inserted", errContent.toString().trim());
		assertEquals("Current value = 0", outContent.toString().trim());
	}

	@Test
	public void testCase13() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 75", outContent.toString().trim());
		resetStreams();
		mac.returnCoin();
		assertEquals("Take your coins", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("No coins inserted", errContent.toString().trim());
		assertEquals("Current value = 0", outContent.toString().trim());
	}

	@Test
	public void testCase14() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 75", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("Take selection", errContent.toString().trim());
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("Enter 25 coins", errContent.toString().trim());
		assertEquals("Current value = 25", outContent.toString().trim());
	}

	@Test
	public void testCase15() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 75", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 100", outContent.toString().trim());
		resetStreams();
		mac.returnCoin();
		assertEquals("Take your coins", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("No coins inserted", errContent.toString().trim());
		assertEquals("Current value = 0", outContent.toString().trim());
	}

	@Test
	public void testCase16() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 75", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 100", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("Take selection", errContent.toString().trim());
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.vendItem(3);
		assertEquals("Take selection", errContent.toString().trim());
		assertEquals("Current value = 0", outContent.toString().trim());
	}

	/**
	 * Test set of available selection, but unavailable item
	 */
	@Test
	public void testCase17() {
		mac.vendItem(5);
		assertEquals("No coins inserted", errContent.toString().trim());
		assertEquals("Current value = 0", outContent.toString().trim());
	}

	@Test
	public void testCase18() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.vendItem(5);
		assertEquals("Selection 5 unavailable", errContent.toString().trim());
		assertEquals("Current value = 25", outContent.toString().trim());
	}

	@Test
	public void testCase19() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.returnCoin();
		assertEquals("Take your coins", outContent.toString().trim());
		resetStreams();
		mac.vendItem(5);
		assertEquals("No coins inserted", errContent.toString().trim());
		assertEquals("Current value = 0", outContent.toString().trim());
	}

	@Test
	public void testCase20() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.vendItem(5);
		assertEquals("Selection 5 unavailable", errContent.toString().trim());
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.vendItem(5);
		assertEquals("Selection 5 unavailable", errContent.toString().trim());
		assertEquals("Current value = 25", outContent.toString().trim());
	}


	/**
	 * Test set of invalid product selection.
	 */
	@Test
	public void testCase21() {
		mac.vendItem(35);
		assertEquals("No coins inserted", errContent.toString().trim());
		assertEquals("Current value = 0", outContent.toString().trim());
	}

	@Test
	public void testCase22() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.vendItem(35);
		assertEquals("Wrong selection 35", errContent.toString().trim());
		assertEquals("Current value = 25", outContent.toString().trim());
	}

	@Test
	public void testCase23() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.vendItem(35);
		assertEquals("Wrong selection 35", errContent.toString().trim());
		assertEquals("Current value = 50", outContent.toString().trim());
	}

	@Test
	public void testCase24() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 75", outContent.toString().trim());
		resetStreams();
		mac.vendItem(35);
		assertEquals("Wrong selection 35", errContent.toString().trim());
		assertEquals("Current value = 75", outContent.toString().trim());
	}

	@Test
	public void testCase25() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 75", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 100", outContent.toString().trim());
		resetStreams();
		mac.vendItem(35);
		assertEquals("Wrong selection 35", errContent.toString().trim());
		assertEquals("Current value = 100", outContent.toString().trim());
	}

	@Test
	public void testCase27() {
		mac.insertCoin();
		assertEquals("Current value = 25", outContent.toString().trim());
		resetStreams();
		mac.insertCoin();
		assertEquals("Current value = 50", outContent.toString().trim());
		resetStreams();
		mac.vendItem(5);
		assertEquals("Selection 5 unavailable", errContent.toString().trim());
		assertEquals("Current value = 50", outContent.toString().trim());
	}
}