package rapps_weyuker;

public final class PowFunction
{
		public static int pow(int x, int y)	{
/* 1 */		int aux, result;
/* 1 */		if (y > 0) {
/* 2 */			aux = y;
/* 3 */		} else {
/* 3 */			aux = -y;
/* 3 */		}
/* 4 */		result = 1;
/* 5 */		while (aux != 0) {
/* 6 */			aux = aux - 1;
/* 6 */			result = result * x;
/* 6 */		}
/* 7 */		if (y < 0) {
/* 8 */			result = 1/result;
/* 8 */		}
/* 9 */		return result;
		}
}
