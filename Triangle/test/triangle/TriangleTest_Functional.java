package triangle;

import org.junit.Test;

import static org.junit.Assert.*;

public class TriangleTest_Functional {
	@Test
	public void testEquilateral() {
		Triangle t = new Triangle(3, 3, 3);
		assertEquals(t.getType(), Triangle.EQUILATERAL);
	}

	@Test
	public void testIsosceles1() {
		Triangle t = new Triangle(3, 5, 5);
		assertEquals(t.getType(), Triangle.ISOSCELES);
	}

	@Test
	public void testIsosceles2() {
		Triangle t = new Triangle(5, 3, 5);
		assertEquals(t.getType(), Triangle.ISOSCELES);
	}

	@Test
	public void testIsosceles3() {
		Triangle t = new Triangle(5, 5, 3);
		assertEquals(t.getType(), Triangle.ISOSCELES);
	}

	@Test
	public void testScalene() {
		Triangle t = new Triangle(3, 4, 5);
		assertEquals(t.getType(), Triangle.SCALENE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testZero() {
		new Triangle(0, 5, 5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAllZero() {
		new Triangle(0, 0, 0);
	}

	@Test
	public void testTwoSidesSum1() {
		new Triangle(3, 3, 6);
	}

	@Test
	public void testTwoSidesSum2() {
		new Triangle(3, 6, 3);
	}

	@Test
	public void testTwoSidesSum3() {
		new Triangle(6, 3, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotTriangle1() {
		new Triangle(3, 3, 9);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotTriangle2() {
		new Triangle(3, 9, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotTriangle3() {
		new Triangle(9, 3, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegative() {
		new Triangle(-3, -5, -5);
	}
}