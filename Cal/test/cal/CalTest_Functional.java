package cal;
import static org.junit.Assert.*;

import org.junit.Test;

import cal.Cal;

public class CalTest_Functional
{
	@Test
	public void testFirstOfMonth1()
	{
		assertEquals(6, Cal.firstOfMonth(1, 2000));
	}

	@Test
	public void testFirstOfMonth2()
	{
		assertEquals(0, Cal.firstOfMonth(10, 1752));
	}

	@Test
	public void testFirstOfMonth3()
	{
		assertEquals(0, Cal.firstOfMonth(10, 1752));
	}

	@Test
	public void testNumberOfDays1()
	{
		assertEquals(29, Cal.numberOfDays(2, 2000));
	}

	@Test
	public void testNumberOfDays2()
	{
		assertEquals(19, Cal.numberOfDays(9, 1752));
	}

	@Test
	public void testIsLeap1()
	{
		assertTrue(Cal.isLeap(1752));
	}

	@Test
	public void testIsLeap2()
	{
		assertFalse(Cal.isLeap(1751));
	}

	@Test
	public void testIsLeap3()
	{
		assertTrue(Cal.isLeap(2000));
	}

	@Test
	public void testIsLeap4()
	{
		assertFalse(Cal.isLeap(1900));
	}

	@Test
	public void testIsLeap5()
	{
		assertTrue(Cal.isLeap(1992));
	}

	@Test
	public void testIsLeap6()
	{
		assertFalse(Cal.isLeap(2001));
	}

	@Test
	public void testJan1_1()
	{
		assertEquals(1, Cal.jan1(2001));
	}
}
