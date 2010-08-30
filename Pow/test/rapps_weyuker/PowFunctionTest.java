package rapps_weyuker;

import static org.junit.Assert.*;

import org.junit.Test;

public class PowFunctionTest
{
	@Test
	public void testXor1()
	{
		assertEquals(Math.pow(3, 9), PowFunction.pow(3, 9), 0);
	}


	@Test
	public void testXor2()
	{
		assertEquals(Math.pow(0, 9), PowFunction.pow(0, 9), 0);
	}


	@Test
	public void testXor3()
	{
		assertEquals(Math.pow(3, 0), PowFunction.pow(3, 0), 0);
	}


	@Test
	public void testXor4()
	{
		assertEquals(Math.pow(3, -9), PowFunction.pow(3, -9), 0.01);
	}

	@Test
	public void testXor5()
	{
		assertEquals(Math.pow(-3, 9), PowFunction.pow(-3, 9), 0);
	}
}
