package factorial;

public class Factorial {

	long compute(int x) {
		if (x < 0) {
			throw new IllegalArgumentException("Cannot calculate the factorial of a negative number");
		}
		
		long factor1 = 1;
		long factor2 = 1;
		boolean oob = false;
		while (x > 0 && ! oob) {
			factor2 = factor1;
			factor1 = factor1 * x;
			if (factor2 > factor1) {
				oob = true;
			}
			x--;
		}
		
		if (oob) {
			throw new UnsupportedOperationException("Number too big");
		}
		
		return factor1;
	}
}
