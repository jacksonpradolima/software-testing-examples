import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class VetTest
{
	private Vet vet;

	@Before
	public void setUp() throws Exception
	{
		vet = new Vet();
	}

	@Test
	public void testAverage_EmptyVector()
	{
		int[] in = {};
		assertEquals(Double.NaN, vet.average(in), 0);
	}

	@Test
	public void testAverage_NullVector()
	{
		int[] in = null;
		assertEquals(0, vet.average(in), 0);
	}

	@Test
	public void testAverage_ConstantValue()
	{
		int[] in = {1, 1, 1, 1};
		assertEquals(1, vet.average(in), 0);
	}


	@Test
	public void testAverage_CommonValues()
	{
		int[] in = {1, 2, 3, 4};
		assertEquals(2.5, vet.average(in), 0);
	}

}
