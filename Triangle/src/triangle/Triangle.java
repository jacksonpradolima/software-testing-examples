package triangle;

import java.util.Scanner;

public class Triangle
{
	private int side1;

	private int side2;

	private int side3;

	public static final String ISOSCELES = "Isosceles";

	public static final String EQUILATERAL = "Equilateral";

	public static final String SCALENE = "Scalene";

	public Triangle(int side1, int side2, int side3)
	{
		if (side1 <= 0 || side2 <= 0 || side3 <= 0) {
			throw new IllegalArgumentException("Invalid side values");
		}

		if (side1 + side2 < side3 || side1 + side3 < side2 || side2 + side3 < side1) {
			throw new IllegalArgumentException("Invalid side values");
		}

		this.side1 = side1;
		this.side2 = side2;
		this.side3 = side3;
	}

	public String getType()
	{
		if (side1 == side2 && side1 == side3) {
			return "Equilateral";
		}

		if (side1 == side2 || side1 == side3 || side2 == side3) {
			return "Isosceles";
		}

		return "Scalene";
	}


	/**
	 * The program should read three integer values that represents
	 * the triangle sides. The program should tell if the triangle
	 * is equilateral, isosceles, or scalene.
	 *
	 * Condition: the sum of two sides must be bigger than the third
	 * side.
	 */
	public static void main(String[] args)
	{
		int side1, side2, side3;
		Scanner in = new Scanner(System.in);
		side1 = in.nextInt();
		side2 = in.nextInt();
		side3 = in.nextInt();
		in.close();

		Triangle t = new Triangle(side1, side2, side3);
		System.out.println(t.getType());
	}
}